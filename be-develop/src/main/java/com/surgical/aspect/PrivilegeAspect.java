package com.surgical.aspect;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.surgical.entities.Admin;
import com.surgical.exception.AppException;
import com.surgical.security.JwtTokenProvider;
import com.surgical.services.AdminService;

@Aspect
@Order(1)
@Component
public class PrivilegeAspect{

    private static final Logger logger = LoggerFactory.getLogger(PrivilegeAspect.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AdminService adminService;

    @Pointcut("execution(* com.surgical.controllers.DeviceTypeController..*(..)) "
        + "|| execution(* com.surgical.controllers.DeviceBoxController..*(..)) "
        + "|| execution(* com.surgical.controllers.DeviceController..*(..)) "
        + "|| execution(* com.surgical.controllers.DivisionController..*(..)) "
        + "|| execution(* com.surgical.controllers.PackageController..*(..)) "
        + "|| execution(* com.surgical.controllers.DeliverBatchController..*(..)) "
        + "|| execution(* com.surgical.controllers.StationController..*(..)) "
        + "|| execution(* com.surgical.controllers.BatchTransferController..*(..)) "
        + "|| execution(* com.surgical.controllers.SurgicalApplyController..*(..)) "
        + "|| execution(* com.surgical.controllers.SterilizedBatchController..*(..)) "
        + "|| execution(* com.surgical.controllers.PackageDeviceCheckController..*(..)) "
        + "|| execution(* com.surgical.controllers.PackageOrderController..*(..)) "
        + "|| execution(* com.surgical.controllers.WashingBatchController..*(..)) "
        + "|| execution(* com.surgical.controllers.ReportController..*(..)) "
        + "|| execution(* com.surgical.controllers.UserController..*(..)) "
        + "|| execution(* com.surgical.controllers.CommonController..*(..)) "
        + "|| execution(* com.surgical.controllers.PermissionController..*(..)) "
        + "|| execution(* com.surgical.controllers.RoleController..*(..)) "
        + "|| execution(* com.surgical.controllers.PrivilegeController..*(..)) "
        + "|| execution(* com.surgical.controllers.SystemPropertiesController..*(..)) ")
    
    public void pointcut(){
    }

    @Around("pointcut()")
    public Object verifyPrivilege(ProceedingJoinPoint pjp) throws Throwable{
        //Object[] signatureArgs = pjp.getArgs();
        //String sessionId = (String) signatureArgs[0];
        try{
            HttpServletRequest request = ( (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes() ).getRequest();
            verifyPrivilegeViaAccount(request);
            return pjp.proceed();
        }catch(Exception e){
            logger.error("verify authentication error: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized account!");
        }
    }

    private void verifyPrivilegeViaAccount(HttpServletRequest request){
        String authToken = request.getHeader("authToken");
        if (!tokenProvider.validateToken(authToken)){
            logger.error("invalid authToken!");
            throw new AppException("invalid authToken!");
        }
        String source = tokenProvider.getUserAccountInfoFromJWT(authToken);
        String[] accountInfo = source.split(";");
        String accountId = accountInfo[0];
        String account = accountInfo[1];
        Optional<Admin> admin = adminService.findByAccountAndId(account, Long.valueOf(accountId));
        if (!admin.isPresent()){
            throw new AppException("unauthorized account!");
        }else{
            HttpSession session = request.getSession();
            session.setAttribute("privilegeIds", admin.get().getPrivilege());
            session.setAttribute("account", account);
            session.setAttribute("accountId", Long.valueOf(accountId));
        }
    }
}