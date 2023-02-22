// 取得包盤狀態名稱
//
export function inventoryPackage(status) {
  const list = [
    { status: -1, name: '申請中', value: 'APPLY' },
    { status: 0, name: '配盤完成', value: 'PACK_DONE' },
    { status: 1, name: '交付滅菌', value: 'PACK_TO_STER' },
    { status: 2, name: '滅菌領取', value: 'STER_RECV' },
    { status: 3, name: '開始滅菌', value: 'STER_PROCESS' },
    { status: 4, name: '滅菌完成', value: 'STER_DONE' },
    { status: 5, name: '交付庫存', value: 'STER_TO_STOCK' },
    { status: 6, name: '入庫', value: 'IN_STOCK' },
    { status: 7, name: '發放', value: 'DISTRIBUTE' },
    { status: 8, name: '開始使用', value: 'IN_USE' },
    { status: 9, name: '使用完成', value: 'USE_DONE' },
    { status: 10, name: '回收清點', value: 'CIRCULATION' },
    { status: 11, name: '開始清洗', value: 'WASH_PROCESS' },
    { status: 12, name: '清洗完成', value: 'WASH_FINISH' },
    { status: 13, name: '重新滅菌', value: 'RE_STERILIZE' },
    { status: 14, name: '滅菌失敗', value: 'STER_FAIL' },
    { status: 15, name: '清洗失敗', value: 'WASH_FAIL' },
    { status: 16, name: '滅菌過期領取', value: 'RECV_FROM_EXPIRED_STER' },
    { status: 17, name: '包內指示劑失敗', value: 'PACK_INDICATOR_FAIL' },
    { status: 99, name: '拆盤', value: 'UNPACK' },
  ];
  if (status === 'all') return list;
  return list.find((item) => item.status === Number(status));
}

// 器械狀態
//
export function surgeryDevice(query) {
  const list = [
    { status: 0, value: 'NORMAL', name: '全新', color: 'secondary' },
    { status: 1, value: 'IN_PACKAGE', name: '使用中', color: 'secondary' }, // 在包盤中
    { status: 2, value: 'REPLACED', name: '待使用', color: 'grey-500' },
    { status: 3, value: 'RECEIVE_SCAN_DONE', name: '已回收清點', color: 'grey-500' },
    { status: 4, value: 'REPAIR', name: '維修', color: 'warning' },
    { status: 5, value: 'REPAIR_DONE', name: '維修完成', color: 'secondary' },
    { status: 6, value: 'UNSCANABLE', name: '無法掃描', color: 'accent' },
    { status: 7, value: 'MISSING', name: '遺失', color: 'accent' },
    { status: 9, value: 'SCRAPPED', name: '報廢', color: 'accent' },
    // { status: 99, name: '刪除', color: 'accent' },
  ];
  if (query === 'all') return list;
  return list.find((item) => item.status === Number(query) || item.value === query);
}

// 包盤申請
//
export function packageApply(status) {
  const list = [
    {
      status: 0,
      name: '已取消',
      color: 'grey-500',
    },
    {
      status: 1,
      name: '申請中',
      color: 'accent',
    },
    {
      status: 2,
      name: '處理中',
      color: 'secondary',
    },
    {
      status: 3,
      name: '已完成',
      color: 'primary',
    },
  ];
  if (status === 'all') return list;
  return list.find((item) => item.status === Number(status));
}

// 權限
//
export function authRight(status) {
  const list = [
    {
      status: 0,
      name: '正常',
    },
    {
      status: 1,
      name: '停權',
    },
    {
      status: 2,
      name: '刪除',
    },
  ];
  return list.find((item) => item.status === Number(status));
}

// 滅菌狀態
export function sterilized(status) {
  const list = [
    {
      status: 0,
      name: '未滅菌',
      color: 'grey-500',
    },
    {
      status: 1,
      name: '滅菌中',
      color: 'accent',
    },
    {
      status: 2,
      name: '滅菌完成',
      color: 'primary',
    },
  ];
  if (status === 'all') return list;
  return list.find((item) => item.status === Number(status));
}
// 滅菌狀態
export function washing(status) {
  const list = [
    {
      status: 0,
      name: '未清洗',
      color: 'grey-500',
    },
    {
      status: 1,
      name: '清洗中',
      color: 'accent',
    },
    {
      status: 2,
      name: '清洗完成',
      color: 'primary',
    },
  ];
  if (status === 'all') return list;
  return list.find((item) => item.status === Number(status));
}

// Package_Config.isActive // Station.isActive
// 0:關
// 1:開

export function surgicalApplyOrder(status) {
  const list = [
    {
      status: 0,
      name: '未領用',
    },
    {
      status: 1,
      name: '已領用',
    },
    {
      status: 2,
      name: '使用中',
    },
    {
      status: 3,
      name: '使用完成',
    },
    {
      status: 4,
      name: '回收清點完成',
    },
  ];
  if (status === 'all') return list;
  return list.find((item) => item.status === Number(status));
}

export function surgical(status) {
  const list = [
    {
      status: 0,
      name: '申請中',
    },
    {
      status: 1,
      name: '已審核',
    },
    {
      status: 2,
      name: '已發放',
    },
    {
      status: 3,
      name: '使用中',
    },
    {
      status: 4,
      name: '使用完成',
    },
    {
      status: 5,
      name: '回收清點完成',
    },
  ];
  if (status === 'all') return list;
  return list.find((item) => item.status === Number(status));
}

export function sterilizedTests() {
  return [
    { type: 0, label: 'BD TEST' },
    { type: 1, label: '滅菌時間' },
    { type: 2, label: '滅菌溫度' },
    { type: 3, label: '包外指示劑' },
    { type: 4, label: '第五級整合型CI' },
    { type: 5, label: '生物培養' },
  ];
}

export function divisionStatus({ value }) {
  const list = [
    { status: 0, label: '正常', value: 'enable' },
    { status: 1, label: '停用', value: 'disabled' },
    { status: 2, label: '刪除', value: 'delete' },
  ];
  if (!value) return null;
  return list.find((item) => item.value === value);
}
