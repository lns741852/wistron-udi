import axios from 'axios';
import JWT from './cookies';
import LocalStorage from '@/common/localstorage';

const AxiosInstance = axios.create({
  baseURL: process.env.VUE_APP_API,
});

const apiPath = process.env.VUE_APP_PATH;

// Error Handler
const errorHandler = (statusCode) => {
  if (statusCode === 401) {
    JWT.removeToken();
    LocalStorage.removeItem('sims-usrinfo');
    setTimeout(() => {
      window.location.reload();
    }, 500);
  }
};

AxiosInstance.interceptors.request.use(
  (config) => {
    const tokenParams = 'authToken';
    const reqConfig = config;
    reqConfig.headers[tokenParams] = JWT.getToken();
    return reqConfig;
  },
  (err) => {
    if (err && err.response) {
      return Promise.reject(err);
    }
    return Promise.reject(err.response);
  },
);

AxiosInstance.interceptors.response.use(
  (response) => response,
  (err) => {
    const { status = 400, data } = err.response;
    errorHandler(status, data);
    return Promise.reject(err.response);
  },
);

const APIService = {
  query(url, json) {
    return AxiosInstance.get(`${apiPath}/${url}`, { params: json });
  },
  get(url, slug) {
    return AxiosInstance.get(`${apiPath}/${url}/${slug}`);
  },
  create(url, data) {
    return AxiosInstance.post(`${apiPath}/${url}`, data);
  },
  createParams(url, params) {
    return AxiosInstance.post(`${apiPath}/${url}?${params}`);
  },
  update(url, slug, data) {
    return AxiosInstance.post(`${apiPath}/${url}/${slug}`, data);
  },
  delete(url, data) {
    return AxiosInstance.post(`${apiPath}/${url}`, data);
  },
  form(url, data) {
    return AxiosInstance.post(`${apiPath}/${url}`, data, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },
};

export default APIService;
