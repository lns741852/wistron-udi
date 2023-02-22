import router from './index';
import store from '@/store/index';
import JWT from '@/api/cookies';

// import { errorPage } from './index.js';

const whiteList = ['/login'];

router.beforeEach(async (to, from, next) => {
  // login or not
  const hasToken = JWT.getToken();
  if (hasToken) {
    if (to.path === '/login') {
      // if is logged in, depends on request we want: ex: keep or redirect to home
      next();
    } else {
      const hasRoles = store.getters.roles && store.getters.roles.length > 0;
      if (hasRoles) {
        next();
      } else {
        try {
          const menus = await store.dispatch('user/fetchLocalUserInfo');
          const removeRoutes = await store.dispatch('route/removeRoutes', menus);
          removeRoutes.forEach((v) => {
            router.removeRoute(v);
          });

          // URL Path is defined in router.js, and user has no right
          const routeExist = router.hasRoute(to.name);
          if (routeExist) {
            next({ ...to, replace: true });
          } else {
            next({ name: 'NotFound' });
          }
        } catch (error) {
          next('/login');
        }
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next();
      return;
    }
    next('/login');
  }
});
