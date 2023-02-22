import { modifyRoutes as router } from '@/router';

const states = {
  menus: [
    {
      name: '器械管理',
      mainGroup: 'dm',
      hasItem: false,
    },
    {
      name: '包盤管理',
      mainGroup: 'pm',
      hasItem: false,
      groups: [
        {
          name: '管理區',
          icon: 'img:/icons/inventory.png',
          subGroup: 'pm_m',
        },
        {
          name: '庫存區',
          icon: 'img:/icons/inventory.png',
          subGroup: 'pm_i',
        },
        {
          name: '配盤區',
          icon: 'img:/icons/package.png',
          subGroup: 'pm_p',
        },
        {
          name: '回收清洗區',
          subGroup: 'pm_c',
          icon: 'img:/icons/recycle.png',
        },
        {
          name: '滅菌站',
          icon: 'img:/icons/disinfect.png',
          subGroup: 'pm_s',
        },
      ],
    },
    {
      name: '手術管理',
      mainGroup: 'surgerym',
      hasItem: false,
    },
    {
      name: '統計分析',
      mainGroup: 'statisticm',
      hasItem: false,
      groups: [
        {
          name: '包盤',
          subGroup: 'statisticm_p',
          icon: 'img:/icons/package.png',
        },
        {
          name: '滅菌',
          subGroup: 'statisticm_s',
          icon: 'img:/icons/disinfect.png',
        },
        {
          name: '清洗',
          subGroup: 'statisticm_r',
          icon: 'img:/icons/recycle.png',
        },
      ],
    },
    {
      name: '系統設定',
      mainGroup: 'systemm',
      hasItem: false,
      groups: [
        {
          name: '人員管理',
          subGroup: 'systemm_m',
          icon: 'img:/icons/member.png',
        },
      ],
    },
  ],
};

const mutations = {
  CLEAR_MENU(state) {
    state.menus = [];
  },
  SET_ROUTERS(state, routes) {
    const { name: routerName } = routes;
    const { title } = routes.meta;
    const { mainGroup, subGroup, icon } = routes.meta?.route || {};
    const mainIdx = state.menus.findIndex((m) => m.mainGroup === mainGroup);
    if (mainIdx !== -1) {
      state.menus[mainIdx].hasItem = true;
      if (subGroup) {
        const subIdx = state.menus[mainIdx].groups.findIndex((s) => s.subGroup === subGroup);
        if (subIdx !== -1) {
          const target = state.menus[mainIdx].groups[subIdx];
          const content = {
            name: title,
            routerName,
          };
          if (target.contents) {
            target.contents.push(content);
          } else {
            target.contents = [content];
          }
        }
        return;
      }
      const mainTarget = state.menus[mainIdx];
      const groupContent = {
        name: title,
        routerName,
        icon,
      };
      if (mainTarget.groups) {
        mainTarget.groups.push(groupContent);
      } else {
        state.menus[mainIdx].groups = [groupContent];
      }
    }
  },
};

const actions = {
  async removeRoutes({ commit }, menus) {
    if (menus.length === 0) {
      commit('CLEAR_MENU');
      return [];
    }

    const tempRouters = router.reduce((acc, current) => {
      const arr = current.children;
      if (typeof arr === 'object') {
        const childrenR = arr.map((cr) => cr);
        return [...acc, ...childrenR];
      }
      return [...acc, current];
    }, []);

    const removeArray = tempRouters.reduce((acc, current) => {
      const status = menus.includes(current.name);
      if (status) {
        commit('SET_ROUTERS', current);
      }
      return status ? acc : [...acc, current.name];
    }, []);

    return removeArray;
  },
};

export default {
  namespaced: true,
  state: states,
  mutations,
  actions,
};
