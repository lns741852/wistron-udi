import { apiQueryDivision } from '@/api';

const states = {
  divisions: [],
};

const mutations = {
  SET_DIVISIONS(state, divisions) {
    state.divisions = divisions;
  },
};

const actions = {
  async queryDivisions({ commit }) {
    try {
      const res = await apiQueryDivision();
      commit('SET_DIVISIONS', res.data);
    } catch (error) {
      // continus regardless
    }
  },
};

export default {
  namespaced: true,
  state: states,
  mutations,
  actions,
};
