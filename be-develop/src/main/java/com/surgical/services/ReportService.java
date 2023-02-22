package com.surgical.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.surgical.entities.Package;
import com.surgical.entities.PackageConfig;
import com.surgical.enums.PackageStatus;
import com.surgical.exception.AppException;
import com.surgical.repositories.PackageConfigRepository;
import com.surgical.repositories.SterilizedBatchRepository;
import com.surgical.repositories.TrackingRepository;
import com.surgical.repositories.WashingBatchRepository;
import com.surgical.vo.CirculationWashingReportVo;
import com.surgical.vo.PackagePackingReportPageResponse;
import com.surgical.vo.PackagePackingReportRequest;
import com.surgical.vo.PackagePackingReportResponse;
import com.surgical.vo.SterilizationReportVo;
import com.surgical.vo.SterilizerVo;
import com.surgical.vo.WashingMachineVo;

@Service
public class ReportService{
    private static final Integer MAXIMUM_STERILIZATION = 10;

    @Autowired
    private SterilizedBatchRepository sterilizedBatchRepository;

    @Autowired
    private TrackingRepository trackingRepository;

    @Autowired
    private PackageConfigRepository packageConfigRepository;

    @Autowired
    private WashingBatchRepository washingBatchRepository;

    public PackagePackingReportResponse packagePackingReport(PackagePackingReportRequest request) throws ParseException{
        if(request.getStart().after(request.getEnd())) {
            throw new AppException("時間區間 end 不能小於 start");
        }
        SimpleDateFormat sdfStart = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat sdfEnd = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeString = sdfStart.format(request.getStart());
        Date start = sdf.parse(timeString);
        timeString = sdfEnd.format(request.getEnd());
        Date end = sdf.parse(timeString);
        Pageable pageable = PageRequest.of(request.getPage(), 10);
        List<Map<String, Long>> listMap = trackingRepository.countForReportPackagePacking(start, end);
        Long totalPackingCount = listMap.stream().mapToLong(e -> e.get("packingCount")).sum();
        int startItem = (int) pageable.getOffset();
        int endItem = Math.min((startItem + pageable.getPageSize()),listMap.size());
        if(endItem < startItem) {
            throw new AppException("頁數超過可選範圍");
        }
        Page<Map<String, Long>> pageMap = new PageImpl<Map<String, Long>>(listMap.subList(startItem, endItem), pageable, listMap.size());
        Page<PackagePackingReportPageResponse> pageResponse = pageMap.map(new Function<Map<String, Long>, PackagePackingReportPageResponse>(){

            @Override
            public PackagePackingReportPageResponse apply(Map<String, Long> map){
                PackageConfig packageConfig = packageConfigRepository.findById(Long.valueOf(map.get("configId").toString())).orElseThrow(() -> new AppException(map.get("configId").toString() + "此 組合id 不存在"));
                long packageQty = 0L;
                for(Package pack : packageConfig.getPackages()){
                    if (pack.getStatus() != PackageStatus.APPLY.getValue() && pack.getStatus() != PackageStatus.UNPACK.getValue()){
                        packageQty += 1;
                    }
                }
                PackagePackingReportPageResponse response = PackagePackingReportPageResponse.builder()
                    .configCode(packageConfig.getConfigCode())
                    .packageName(packageConfig.getPackageName())
                    .divisionId(packageConfig.getDivisionId())
                    .packageQty(packageQty)
                    .packingCount(map.get("packingCount")).build();
                return response;
            }
        });
        PackagePackingReportResponse result = new PackagePackingReportResponse();
        result.setPackageConfigs(pageResponse);
        result.setTotalPackingCount(totalPackingCount);
        return result;
    }

    private Double calculateUsageRate(Long count, Long daysElapsed){
        Double usageRate = ( count * 100.0 ) / ( daysElapsed * MAXIMUM_STERILIZATION );
        return new BigDecimal(usageRate).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
    }

    public SterilizationReportVo sterilizationSterilizer(String start, String end) throws Exception{
        SterilizationReportVo sterilizationReport = new SterilizationReportVo();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = sdf.parse(start + " 00:00:00");
        Date endDate = sdf.parse(end + " 23:59:59");
        if (startDate.after(endDate)){
            throw new AppException("起始日期不得大於結束日期");
        }
        Long daysElapsed = TimeUnit.MILLISECONDS.toDays(( endDate.getTime() - startDate.getTime() )) + 1;
        List<SterilizerVo> sterilizers = sterilizedBatchRepository.getSterilizersStatistics(startDate, endDate);
        Long failedSterilized = sterilizedBatchRepository.getFailedSterilizedCount(startDate, endDate);
        Long totalSterilizedPackages = 0L;
        Long totalSterilizedCount = 0L;
        Double totalUsageRate = 0.0;
        for(SterilizerVo sterilizer : sterilizers){
            totalSterilizedPackages += sterilizer.getSterilizedPackages();
            totalSterilizedCount += sterilizer.getSterilizedCount();
            Double usageRate = calculateUsageRate(sterilizer.getSterilizedCount(), daysElapsed);
            sterilizer.setUsageRate(usageRate);
            totalUsageRate += usageRate;
        }
        sterilizationReport.setTotalSterilizedPackages(totalSterilizedPackages);
        sterilizationReport.setTotalSterilizedCount(totalSterilizedCount);
        sterilizationReport.setFailedSterilized(failedSterilized);
        totalUsageRate = ( sterilizers.isEmpty() ) ? 0 : ( new BigDecimal(totalUsageRate / sterilizers.size()).setScale(2, RoundingMode.HALF_DOWN).doubleValue() );
        sterilizationReport.setTotalUsageRate(totalUsageRate);
        sterilizationReport.setSterilizers(sterilizers);
        return sterilizationReport;
    }
    
    public CirculationWashingReportVo circulationWashing(String start, String end) throws Exception{
        CirculationWashingReportVo circulationWashingReport = new CirculationWashingReportVo();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = sdf.parse(start + " 00:00:00");
        Date endDate = sdf.parse(end + " 23:59:59");
        if (startDate.after(endDate)){
            throw new AppException("起始日期不得大於結束日期");
        }
        Long daysElapsed = TimeUnit.MILLISECONDS.toDays(( endDate.getTime() - startDate.getTime() )) + 1;
        List<WashingMachineVo> washingMachines = washingBatchRepository.getWashingMachinesStatistics(startDate, endDate);
        Long failedWashed = washingBatchRepository.getFailedWashedCount(startDate, endDate);
        Long totalWashedPackages = 0L;
        Long totalWashedCount = 0L;
        Double totalUsageRate = 0.0;
        for(WashingMachineVo washingMachine : washingMachines){
            totalWashedPackages += washingMachine.getWashedPackages();
            totalWashedCount += washingMachine.getWashedCount();
            Double usageRate = calculateUsageRate(washingMachine.getWashedCount(), daysElapsed);
            washingMachine.setUsageRate(usageRate);
            totalUsageRate += usageRate;
        }
        circulationWashingReport.setTotalWashedPackages(totalWashedPackages);
        circulationWashingReport.setTotalWashedCount(totalWashedCount);
        circulationWashingReport.setFailedWashed(failedWashed);
        totalUsageRate = ( washingMachines.isEmpty() ) ? 0 : ( new BigDecimal(totalUsageRate / washingMachines.size()).setScale(2, RoundingMode.HALF_DOWN).doubleValue() );
        circulationWashingReport.setTotalUsageRate(totalUsageRate);
        circulationWashingReport.setWashingMachines(washingMachines);
        return circulationWashingReport;
    }
}
