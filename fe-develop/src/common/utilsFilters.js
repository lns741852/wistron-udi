export function imagePath(url) {
  if (process.env.NODE_ENV === 'development') {
    return `${process.env.VUE_APP_API}${url}`;
  }
  return url;
}

export function currency(num) {
  return num && `NT$ ${num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')}`;
}

export function localeDateStr(time, timezone = 'zh-TW') {
  const localDate = new Date(time);
  return localDate.toLocaleDateString(timezone);
}

export function localeTimeStr(time, timezone = 'zh-TW') {
  const localDate = new Date(time);
  return localDate.toLocaleTimeString(timezone);
}

export function dateStr(time = '') {
  let modifyTime = '';
  if (typeof time === 'string') {
    modifyTime = time
      .replace(/-/g, '/')
      .replace(/T/g, ' ')
      .replace('.000+0800', '');
  } else {
    modifyTime = time;
  }
  const date = time ? new Date(modifyTime) : new Date();
  const mm = date.getMonth() + 1 >= 10 ? date.getMonth() + 1 : `0${date.getMonth() + 1}`;
  const dd = date.getDate() >= 10 ? date.getDate() : `0${date.getDate()}`;
  const hour = date.getHours() >= 10 ? date.getHours() : `0${date.getHours()}`;
  const minute = date.getMinutes() >= 10 ? date.getMinutes() : `0${date.getMinutes()}`;
  return `${date.getFullYear()}-${mm}-${dd} ${hour}:${minute}`;
}

export function generateParams(arr = []) {
  return arr.reduce((acc, { key, value }) => {
    if (value || value === 0) {
      const paramSet = `${key}=${value}`;
      return !acc ? `${paramSet}` : `${acc}&${paramSet}`;
    }
    return acc;
  }, '');
}

export function objectEquals(a, b) {
  if (a === b) return true;

  if (a instanceof Date && b instanceof Date) return a.getTime() === b.getTime();

  if (!a || !b || (typeof a !== 'object' && typeof b !== 'object')) return a === b;

  if (a.prototype !== b.prototype) return false;

  const keys = Object.keys(a);
  if (keys.length !== Object.keys(b).length) return false;
  return keys.every((k) => objectEquals(a[k], b[k]));
}
