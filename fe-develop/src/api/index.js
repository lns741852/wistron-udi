import APIService from './https';

// Common 共用
//
export const apiUpload = (file) => APIService.form('fileupload/0', file);
export const apiFileDelete = (data) => APIService.create('fileDelete', data);
export const apiQueryAttr = (attr) => APIService.get('query/list', attr);
export const apiQueryDivision = () => APIService.query('division/list');
export const apiGetBoxByCode = (params) => APIService.query('deviceBox/info', params);
export const apiGetDeviceByCode = (params) => APIService.query('device/check/getInfo', params);
export const apiGetPackageByCode = (params) => APIService.query('package/getInfo', params);
export const apiQueryTransferPackages = (params) => APIService.query('batch/transfer/list', params);

// 系統
//
export const apiDivisionModify = (data) => APIService.create('division/status/update', data);
export const apiDivisionCreate = (data) => APIService.create('division/create', data);

// 登入及驗證
//
export const apiSignIn = (params) => APIService.createParams('login', params);
export const apiLogout = () => APIService.create('signOut');

// 器械 (種類&單隻器械)
//
export const apiQueryDeviceTypes = (params) => APIService.query('deviceType/list', params);
export const apiAddDeviceType = (data) => APIService.create('deviceType/add', data);
export const apiEditDeviceType = (data) => APIService.create('deviceType/update', data);
export const apiGetDeviceType = (id) => APIService.query('deviceType/detail', { typeId: id });

export const apiQueryDeviceBrands = (id, p) => APIService.query(`deviceType/model/list/${id}`, p);

export const apiQueryDeviceItem = (params) => APIService.query('device/list', params);
export const apiAddDeviceItem = (data) => APIService.create('device/add', data);
export const apiGetDeviceItem = (params) => APIService.query('device/detail', params);
export const apiDeleteDeviceItem = (data) => APIService.delete('device/delete', data);

// 器械盒
//
export const apiQueryDeviceBox = (params) => APIService.query('deviceBox/list', params);
export const apiAddDeviceBox = (data) => APIService.create('deviceBox/add', data);
export const apiGetDeviceBox = (qrcode) => APIService.query('deviceBox/details', { qrcode });

// 維修報廢 - 器械 / 器械盒
export const apiDeviceRepair = (data) => APIService.create('device/repair', data);
export const apiDeviceScrapped = (data) => APIService.create('device/scrapped', data);
export const apiDeviceRepairReturn = (data) => APIService.create('device/repair/return', data);
export const apiDeviceReturn = (data) => APIService.create('device/return', data);

export const apiDeviceBoxRepair = (data) => APIService.create('deviceBox/repair', data);
export const apiDeviceBoxScrapped = (data) => APIService.create('deviceBox/scrapped', data);
export const apiDeviceBoxReturn = (data) => APIService.create('deviceBox/repair/return', data);

// 包盤
//

// 包盤種類
export const apiQueryPackageConf = (params) => APIService.query('package/config/list', params);
export const apiGetPkConfig = (params) => APIService.query('package/config/detail', params);
export const apiCreatePackageConf = (data) => APIService.create('package/config/create', data);

// 包盤製作
export const apiQueryPackageApplies = ({ page, data }) => {
  const params = `page=${page}`;
  return APIService.create(`package/apply/list?${params}`, data);
};

export const apiAddPkApplyQty = (data) => APIService.create('package/apply/addAmount', data);
export const apiUpdatePkApplyQty = (data) => APIService.create('package/apply/updateAmount', data);
export const apiGetPkApplyInfo = (params) => APIService.query('package/apply/detail', params);
export const apiDelPkApply = (data) => APIService.create('package/apply/cancelApplication', data);

// 庫存
//

export const apiPackageExpiry = (params) => APIService.query('package/expired/list', params);

// 盤包
//
// 打包
export const apiWrapPackage = (data) => APIService.create('package/process', data);
export const apiReWrapPackage = (pkId, data) => APIService.create(`package/repack/${pkId}`, data);

// 滅菌
//
// 鍋次建立
export const apiQuerySterilized = (params) => APIService.query('sterilizedBatch/list', params);
export const apiCreateSterilizedDone = (data) => APIService.create('sterilizedBatch/finish', data);
export const apiCreateSterilizedBatch = (data) => APIService.create('sterilizedBatch/create', data);
export const apiGetSterilizedDetail = (p) => APIService.query('sterilizedBatch/detail', p);

// 交付領取
export const apiQueryBatch = (param) => APIService.query('deliverBatch/list', param);
export const apiQueryPackageBatch = (param) => APIService.query('deliverBatch/package/list', param);
export const apiDeliverBatch = (data) => APIService.create('deliverBatch/delivery', data);
export const apiReceiveBatch = (data) => APIService.create('deliverBatch/receive', data);

// 手術
//
export const apiSurgicalApply = (params) => APIService.query('surgicalApply/list', params);
export const apiSurgicalCreate = (data) => APIService.create('surgicalApply/create', data);
export const apiApplyAgainCreate = (data) => APIService.create('surgicalApply/order/create', data);
export const apiApplyOrders = (params) => APIService.query('surgicalApply/order/list', params);
export const apiGetOrder = (params) => APIService.query('surgicalApply/detail', params); // count = true
export const apiDispatchOrder = (data) => APIService.create('surgicalApply/grant/process', data);
export const apiGetPackageTrack = (tkId) => APIService.get('packageDeviceCheck/detail', tkId);
export const apiGetPkTrackProcess = (tkId, data) => {
  const trackingId = tkId;
  return APIService.create(`packageDeviceCheck/process/${trackingId}`, data);
};
export const apiCancelOrder = (data) => APIService.create('surgicalApply/order/cancel', data);

// 回收清洗
//
export const apiQueryWashing = (params) => APIService.query('washingBatch/list', params);
export const apiCreateWashingBatch = (data) => APIService.create('washingBatch/create', data);
export const apiCreateWashingDone = (p) => APIService.createParams('washingBatch/finish', p);
export const apiCreateDeviceScanCheck = (tkId, data) => {
  const trackingId = tkId;
  return APIService.create(`packageDeviceCheck/scan/process/${trackingId}`, data);
};

// 報表
//
export const apiQueryPackagesResult = () => APIService.query('package/dashboard');
export const apiQueryWrapPkAnalysis = (p) => APIService.query('report/package/packing', p);
export const apiQuerySterAnalysis = (p) => APIService.query('report/sterilization/sterilizer', p);
export const apiQueryWashAnalysis = (p) => APIService.query('report/circulation/washing', p);
export const apiQueryPackHistory = (p) => APIService.query('package/history/list', p);
export const apiGetTracking = (tId) => APIService.get('package/tracking', tId);
export const apiGetTrackingDevice = (tId) => APIService.get('package/tracking/device/list', tId);

// 權限
export const apiQueryRoleList = (params) => APIService.query('role/list', params);
export const apiQueryPermissionList = () => APIService.query('permission/list');
export const apiQueryUserList = (params) => APIService.query('user/list', params);
export const apiCreateUser = (data) => APIService.create('user/create', data);
export const apiCreateRole = (data) => APIService.create('role/create', data);
