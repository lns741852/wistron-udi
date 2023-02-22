import { createRouter, createWebHistory } from 'vue-router';
import Main from '@/views/Main.vue';
/**
 * name: router名稱及mapping登入者可用功能頁面
 * constantStatus: true 不可以被移除
 * meta: {
 *   title: 功能名稱 (navbar 顯示)
 *   scan: 頁面左上角的輸入框是掃瞄“器械”或“包盤”條碼,
 *   route: {
 *      mainGroup 管理類型區域
 *      icon
 *   }
 * }
 */
const deviceRouter = [
  {
    path: '/devices',
    name: 'device',
    meta: {
      title: '器械',
      context: '器械',
      scan: 'device',
      route: {
        mainGroup: 'dm',
        icon: 'img:/icons/device.png',
      },
    },
    component: () => import('../views/device/DeviceTypes.vue'),
  },
  {
    path: '/deviceDetail/:typeId',
    name: 'deviceDetail',
    constantStatus: true,
    meta: {
      scan: 'device',
      title: '器械類型明細',
    },
    component: () => import('../views/device/DeviceTypesDetail.vue'),
  },
  {
    path: '/deviceBoxes',
    name: 'deviceBox',
    meta: {
      scan: 'box',
      title: '器械盒',
      route: {
        mainGroup: 'dm',
        icon: 'img:/icons/devicebox.png',
      },
    },
    component: () => import('../views/device/DeviceBoxes.vue'),
  },
  {
    path: '/repairDevice',
    name: 'repair',
    meta: {
      title: '維修',
      route: {
        mainGroup: 'dm',
        icon: 'img:/icons/deviceRepair.png',
      },
    },
    component: () => import('../views/device/RepairDevice.vue'),
  },
];

const nurseManagementRouter = [
  {
    path: '/dashboard',
    name: 'managementOverview',
    meta: {
      title: '總覽',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_m' },
    },
    component: () => import('../views/report/DashBoardManage.vue'),
  },
  {
    path: '/packagesTransaction',
    name: 'createPackageConfig',
    meta: {
      title: '建立包盤類型',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_m' },
    },
    component: () => import('../views/headNurse/PackagesConfigTransaction.vue'),
  },
  {
    path: '/packagesApplies',
    name: 'packageApplication',
    meta: {
      title: '申請包盤製作',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_m' },
    },
    component: () => import('../views/headNurse/PackagesApplyList.vue'),
  },
  {
    path: '/packageUsageRecord',
    name: 'packageUsingRecord',
    meta: {
      scan: 'package',
      title: '包盤使用紀錄',
      route: { mainGroup: 'pm', subGroup: 'pm_m' },
    },
    component: () => import('../views/report/PackageUsageRecord.vue'),
  },
  {
    path: '/packageTrackingDetail/:trackingId',
    name: 'packageTrackingDetail',
    constantStatus: true,
    props: true,
    meta: {
      scan: 'package',
      title: '包盤使用明細',
      route: { mainGroup: 'pm', subGroup: 'pm_m' },
    },
    component: () => import('../views/report/PackageTrackingDetail.vue'),
  },
  {
    path: '/sterilizedList',
    name: 'sterilizationBatchRecord',
    meta: {
      title: '滅菌鍋次紀錄',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_m' },
    },
    component: () => import('../views/sterilization/SterilizedList.vue'),
  },
  {
    path: '/washingList',
    name: 'washingBatchRecord',
    meta: {
      title: '清洗批次紀錄',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_m' },
    },
    component: () => import('../views/circulation/WashingList.vue'),
  },
];

const inventoryRouter = [
  {
    path: '/inventoryReceive',
    name: 'stockReceivePackage',
    meta: {
      title: '領取包盤',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_i' },
    },
    component: () => import('../views/InventoryReceive.vue'),
  },
  {
    path: '/reSterilized',
    name: 'stockSterilizeFailed',
    meta: {
      title: '滅菌失效',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_i' },
    },
    component: () => import('../views/ReSterilizedDeliver.vue'),
  },
  {
    path: '/packageExpiry',
    name: 'stockPackageExpired',
    meta: {
      title: '包盤過期',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_i' },
    },
    component: () => import('../views/PackageExpireList.vue'),
  },
];

const packageRouter = [
  {
    path: '/packageReceive',
    name: 'packReceivePackage',
    meta: {
      title: '領取包盤',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_p' },
    },
    component: () => import('../views/wrapPackage/PackageReceive.vue'),
  },
  {
    path: '/wrapPackages',
    name: 'packCreatePackage',
    meta: {
      title: '包盤製作',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_p' },
    },
    component: () => import('../views/wrapPackage/WrapPackages.vue'),
  },
  {
    path: '/repack',
    name: 'packRePackaging',
    meta: {
      title: '重新打包',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_p' },
    },
    component: () => import('../views/wrapPackage/RewrapPackages.vue'),
  },
  {
    path: '/wrapPackagesDeliver',
    name: 'packDeliverToSterilization',
    meta: {
      title: '交付滅菌',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_p' },
    },
    component: () => import('../views/wrapPackage/WrapPackagesDeliver.vue'),
  },
];

const circulationRouter = [
  {
    path: '/circulationReceive',
    name: 'circulationDeviceCheck',
    meta: {
      title: '回收器械清點',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_c' },
    },
    component: () => import('../views/circulation/CirculationReceives.vue'),
  },
  {
    path: '/washingTransaction',
    name: 'circulationWashingBatchCreate',
    meta: {
      title: '建立清洗批次',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_c' },
    },
    component: () => import('../views/circulation/WashingTransaction.vue'),
  },
  {
    path: '/washingFinish',
    name: 'circulationWashingBatchList',
    meta: {
      title: '清洗批次',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_c' },
    },
    component: () => import('../views/circulation/WashingFinish.vue'),
  },
];

const sterlizeRouter = [
  {
    path: '/sterilizedReceive',
    name: 'sterilizationReceivePackage',
    meta: {
      title: '領取包盤',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_s' },
    },
    component: () => import('../views/sterilization/SterilizedReceive.vue'),
  },
  {
    path: '/sterilizedTransaction',
    name: 'sterilizationBatchCreate',
    meta: {
      title: '建立滅菌鍋次',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_s' },
    },
    component: () => import('../views/sterilization/SterilizedTransaction.vue'),
  },
  {
    path: '/sterilizedFinish',
    name: 'sterilizationBatchList',
    meta: {
      title: '滅菌鍋次',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_s' },
    },
    component: () => import('../views/sterilization/SterilizedFinish.vue'),
  },
  {
    path: '/sterilizedDeliver',
    name: 'sterilizationDeliverToStock',
    meta: {
      title: '交付庫存',
      scan: 'package',
      route: { mainGroup: 'pm', subGroup: 'pm_s' },
    },
    component: () => import('../views/sterilization/SterilizedDeliver.vue'),
  },
];

const surgeryRouter = [
  {
    path: '/surgicalApplyCreate',
    name: 'surgeryDeviceApplication',
    meta: {
      title: '手術器械申請',
      scan: 'package',
      route: { mainGroup: 'surgerym', icon: 'img:/icons/dispatch.png' },
    },
    component: () => import('../views/surgical/SurgicalApplyCreate.vue'),
  },
  {
    path: '/surgeryDeviceApplicationAgain',
    name: 'surgeryDeviceApplicationAgain',
    meta: {
      title: '手術器械補申請',
      scan: 'package',
      route: { mainGroup: 'surgerym', icon: 'img:/icons/dispatch.png' },
    },
    component: () => import('../views/surgical/SurgeryApplyAgainCreate.vue'),
  },
  {
    path: '/sugicalOrderDispatch',
    name: 'surgeryDeviceRelease',
    meta: {
      title: '手術器械發放',
      scan: 'package',
      route: { mainGroup: 'surgerym', icon: 'img:/icons/dispatch.png' },
    },
    component: () => import('../views/surgical/SugicalOrderDispatch.vue'),
  },
  {
    path: '/sugicalReceive',
    name: 'surgeryDeviceCheckBefore',
    meta: {
      title: '器械使用前清點',
      scan: 'package',
      route: { mainGroup: 'surgerym', icon: 'img:/icons/tempApply.png' },
    },
    component: () => import('../views/surgical/SurgicalReceive.vue'),
  },
  {
    path: '/sugicalFinished',
    name: 'surgeryDeviceCheckAfter',
    meta: {
      title: '器械使用完成清點',
      scan: 'package',
      route: { mainGroup: 'surgerym', icon: 'img:/icons/tempApply.png' },
    },
    component: () => import('../views/surgical/SurgicalFinished.vue'),
  },
];

const reportRouter = [
  {
    path: '/report',
    name: 'report',
    component: () => import('../views/report/Report.vue'),
    children: [
      {
        path: '',
        meta: {
          title: '製作包盤分析',
          route: { mainGroup: 'statisticm', subGroup: 'statisticm_p' },
        },
        name: 'statisticCreatePackage',
        component: () => import('../views/report/WrapPackagesAnalysis.vue'),
      },
      {
        path: 'sterilized',
        meta: {
          title: '滅菌鍋分析',
          route: { mainGroup: 'statisticm', subGroup: 'statisticm_s' },
        },
        name: 'statisticSterilizer',
        component: () => import('../views/report/SterilizedAnalysis.vue'),
      },
      {
        path: 'washing',
        meta: {
          title: '清洗機分析',
          route: { mainGroup: 'statisticm', subGroup: 'statisticm_r' },
        },
        name: 'statisticWasher',
        component: () => import('../views/report/ＷashingAnalysis.vue'),
      },
    ],
  },
];

const userRouter = [
  {
    path: '/userList',
    name: 'userManagementUserList',
    meta: {
      title: '使用者列表',
      route: { mainGroup: 'systemm', subGroup: 'systemm_m' },
    },
    component: () => import('../views/user/UserAdminList.vue'),
  },
  {
    path: '/roleList',
    name: 'userManagementRoleList',
    meta: {
      title: '角色列表',
      route: { mainGroup: 'systemm', subGroup: 'systemm_m' },
    },
    component: () => import('../views/user/UserRoleList.vue'),
  },
];

const divisionRouter = [
  {
    path: '/divisionList',
    name: 'division',
    meta: {
      title: '科別',
      route: { mainGroup: 'systemm', icon: 'img:/icons/division.png' },
    },
    component: () => import('../views/division/DivisionList.vue'),
  },
];

// main Routes
const routes = [
  {
    path: '/',
    name: 'index',
    component: Main,
    children: [
      {
        path: '',
        name: 'home',
        constantStatus: true,
        meta: { scan: 'package' },
        component: () => import('../views/report/DashBoardManage.vue'),
      },
      ...deviceRouter,
      ...nurseManagementRouter,
      ...inventoryRouter,
      ...packageRouter,
      ...circulationRouter,
      ...sterlizeRouter,
      ...surgeryRouter,
      ...reportRouter,
      ...userRouter,
      ...divisionRouter,
    ],
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue'),
  },
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('../views/NotFound.vue') },
];

export const modifyRoutes = routes[0].children.filter((r) => !r.constantStatus);

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
