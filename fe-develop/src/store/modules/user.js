import JWT from '@/api/cookies';
import LocalStorage from '@/common/localstorage';
import { apiSignIn } from '@/api';

const states = {
  token: JWT.getToken(),
  roles: [],
  accountId: null,
  name: '',
  localKey: 'sims-usrinfo',
};

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token;
  },
  SET_ROLES: (state, roles) => {
    state.roles = roles;
  },
  SET_ACCOUNT_ID: (state, accountId) => {
    state.accountId = accountId;
  },
  SET_NAME: (state, name) => {
    state.name = name;
  },
};

const actions = {
  async login({ commit, state }, { account, password }) {
    try {
      const params = `account=${account}&password=${password}`;
      const res = await apiSignIn(params);
      const { authToken, ...rest } = res.data;

      const limitTime = 2 * 60 * 60 * 1000;
      const expireTime = new Date().getTime() + limitTime;
      JWT.saveToken(authToken, expireTime);
      LocalStorage.saveItem(state.localKey, rest);
      commit('SET_TOKEN', authToken);

      return;
    } catch (error) {
      throw new Error('error occur!');
    }
  },
  fetchLocalUserInfo({ state, dispatch }) {
    return new Promise((resolve) => {
      const userInfo = LocalStorage.getItem(state.localKey);
      dispatch('generateUserInfo', userInfo);
      resolve(userInfo.permissions);
    });
  },
  generateUserInfo({ commit }, userInfo) {
    const { permissions, name, accountId } = userInfo;

    commit('SET_ROLES', permissions);
    commit('SET_ACCOUNT_ID', accountId);
    commit('SET_NAME', name);
  },
  async logout({ commit, state }) {
    try {
      commit('SET_TOKEN', '');
      commit('SET_ROLES', []);
      commit('SET_ACCOUNT_ID', null);
      commit('SET_NAME', '');
      JWT.removeToken();
      LocalStorage.removeItem(state.localKey);
    } catch (error) {
      throw new Error(error);
    }
  },
};

export default {
  namespaced: true,
  state: states,
  mutations,
  actions,
};
