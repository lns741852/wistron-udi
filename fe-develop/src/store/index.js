import { createStore } from 'vuex';
import user from './modules/user';
import route from './modules/route';
import sysSetting from './modules/sysSetting';
import { divisionStatus } from '@/common/statusFilters';

const getters = {
  name: () => user.state.name,
  roles: () => user.state.roles,
  menus: () => route.state.menus,

  divisionById: () => (id) => {
    const division = sysSetting.state.divisions.find((d) => d.id === id);
    if (!division) return null;
    return division;
  },
  divisionViewAll: () => {
    const divisionDelete = divisionStatus({ value: 'delete' }).status;
    return sysSetting.state.divisions.filter((d) => d.status !== divisionDelete);
  },
  divisionByStatus: () => (status) => {
    if (Number.isNaN(status)) return getters.divisions();
    return sysSetting.state.divisions.filter((d) => d.status === status);
  },
  divisions: () => sysSetting.state.divisions,
  divisionsWithAll: () => [{ id: 'all', name: '全部科別' }, ...sysSetting.state.divisions],
};

export default createStore({
  modules: {
    user,
    route,
    sysSetting,
  },
  getters,
});
