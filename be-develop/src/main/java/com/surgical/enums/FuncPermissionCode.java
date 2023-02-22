package com.surgical.enums;

public enum FuncPermissionCode{

    Device(1),
    DeviceBox(2),
    Repair(3),
    ManagementOverview(4),
    CreatePackageConfig(5),
    PackageApplication(6),
    PackageUsingRecord(7),
    SterilizationBatchRecord(8),
    WashingBatchRecord(9),
    StockReceivePackage(10),
    StockSterilizeFailed(11),
    StockPackageExpired(12),
    PackReceivePackage(13),
    PackCreatePackage(14),
    PackRePackaging(15),
    PackDeliverToSterilization(16),
    CirculationDeviceCheck(17),
    CirculationWashingBatchCreate(18),
    CirculationWashingBatchList(19),
    SterilizationReceivePackage(20),
    SterilizationBatchCreate(21),
    SterilizationBatchList(22),
    SterilizationDeliverToStock(23),
    SurgeryDeviceApplication(24),
    SurgeryDeviceApplicationAgain(25),
    SurgeryDeviceRelease(26),
    SurgeryDeviceCheckBefore(27),
    SurgeryDeviceCheckAfter(28),
    StatisticCreatePackage(29),
    StatisticSterilizer(30),
    StatisticWasher(31),
    UserManagementUserList(32),
    UserManagementRoleList(33),
    Division(34)
    ;

    private Integer value;

    private FuncPermissionCode(Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }
    
    public static FuncPermissionCode valueOf(Integer value) {
        switch(value){
            case 1:
                return Device;
            case 2:
                return DeviceBox;
            case 3:
                return Repair;
            case 4:
                return ManagementOverview;
            case 5:
                return CreatePackageConfig;
            case 6:
                return PackageApplication;
            case 7:
                return PackageUsingRecord;
            case 8:
                return SterilizationBatchRecord;
            case 9:
                return WashingBatchRecord;
            case 10:
                return StockReceivePackage;
            case 11:
                return StockSterilizeFailed;
            case 12:
                return StockPackageExpired;
            case 13:
                return PackReceivePackage;
            case 14:
                return PackCreatePackage;
            case 15:
                return PackRePackaging;
            case 16:
                return PackDeliverToSterilization;
            case 17:
                return CirculationDeviceCheck;
            case 18:
                return CirculationWashingBatchCreate;
            case 19:
                return CirculationWashingBatchList;
            case 20:
                return SterilizationReceivePackage;
            case 21:
                return SterilizationBatchCreate;
            case 22:
                return SterilizationBatchList;
            case 23:
                return SterilizationDeliverToStock;
            case 24:
                return SurgeryDeviceApplication;
            case 25:
                return SurgeryDeviceApplicationAgain;
            case 26:
                return SurgeryDeviceRelease;
            case 27:
                return SurgeryDeviceCheckBefore;
            case 28:
                return SurgeryDeviceCheckAfter;
            case 29:
                return StatisticCreatePackage;
            case 30:
                return StatisticSterilizer;
            case 31:
                return StatisticWasher;
            case 32:
                return UserManagementUserList;
            case 33:
                return UserManagementRoleList;
            case 34:
                return Division;
            default:
                return null;
        }
    }
}
