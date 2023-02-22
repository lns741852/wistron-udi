const ID_TOKEN_KEY = 'authToken';

const getToken = function token() {
  return document.cookie.replace(/(?:(?:^|.*;\s*)authToken\s*=\s*([^;]*).*$)|^.*$/, '$1');
};

const saveToken = (token, expired) => {
  document.cookie = `${ID_TOKEN_KEY}=${token}; expires=${new Date(expired).toUTCString()}`;
};

const removeToken = () => {
  document.cookie = `${ID_TOKEN_KEY}=; expires=Thu, 01 Jan 1970 00:00:00 UTC;`;
};

export default { getToken, saveToken, removeToken };
