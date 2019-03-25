import React from 'react';
import { Router, Route } from 'dva/router';
import { message } from 'antd';
import idsMap from 'config/systemIdsMap.config';
import { fetchPermission } from 'services/login';
import { getUserSession } from 'utils/session';
import { goLogin,getOtherUser } from 'utils/goLogin';
import { DEFAULT_EXCEPTION_MESSAGE, STANDALONE_ETL } from 'constants';
import unsafeRoutes from './unsafeRoutes';

const asyncQueue = []; // 待加载模块队列
// const loadedModels = []; // 已加载过的model列表
const allowPaths = []; // 存放鉴权通过的路由
const idsPatt = /^\.\/(routes\/\w+)\/.*$/;
const pathPatt = /^.\/|route\.js$/g;

const messageValveDuration = 5e3; // 错误提示信息弹出后多少秒内不再弹出，默认5秒
let messageValveOn = false; // 错误提示信息开关

let asyncTimer = 0;

/**
 * 路由鉴权
 * 该方法将向服务器确认用户是否能访问该路径
 * @param  {string} path       需要鉴权的地址
 * @param  {string} empowerApi 授权api路径，用于与授权接口对比
 * @param  {string} pagePath   路由存放路径
 * @param  {object} app        app实例
 * @return {promise}           鉴权结果 true / false
 */
function validateRoute(path, empowerApi, pagePath, app) {
  return new Promise(async (resolve) => {
    const systemId = idsMap[pagePath.replace(idsPatt, '$1')];
    
    const { system: { currentSystemId } } = app._store.getState();
    // 设置当前系统id
    if(currentSystemId !== systemId){
      app._store.dispatch({
        type: 'system/setcurrentSystemId',
        payload: systemId,
      });
    }

    if (allowPaths.indexOf(path) > -1) { // 之前有鉴权成功则直接通过
    // if (true) { // 之前有鉴权成功则直接通过
      resolve(true);
    } else {
      const userVT = getUserSession();

      if (!userVT) {
        //第三方用户
        if(path === "/home" && getOtherUser()){
            allowPaths.push(path);
            resetOtherUser(false);
            resolve(true);
            return;
        }else{
            // 无用户信息则应先登录
            goLogin();
            return;
        }

      } else if (path === '/home' || path === '/') { // 首页无须二次验证
        allowPaths.push(path);
        resolve(true);
        return;
      } 
      const { system: { permits } } = app._store.getState();



      if (!permits[systemId]) { // 没有加载过该子系统授权，则主动加载
        const userVT = getUserSession();
        const { data } = await fetchPermission({
          cid: systemId,
          __vt_param__: userVT,
        });

				if (data.status !== 600 && (!data || !data.data || !data.data.permits)) { // 登录超时无法取得授权，需要重新登录
          goLogin();
          return;
        } else {
          if(data.code === "200"){
            if(data.data.permits && data.data.permits.length>0){
              app._store.dispatch({
                type: 'system/injectPermits',
                payload: {
                  id: systemId,
                  permits: data.data.permits,
                },
              });
            }else{
              if (!messageValveOn) {
                messageValveOn = true;
                message.warn('无权访问该页面');
                setTimeout(() => (messageValveOn = false), messageValveDuration);
              }
              resolve(false);
            }
          }
        }
      }
      // 如果该路由需要授权（配置了empowerApi），并且在授权表里找不到相应权限，则拒绝访问
      if (empowerApi && permits[systemId] && !permits[systemId].some(it => it.url === empowerApi)) {
        if (!messageValveOn) {
         
          messageValveOn = true;
          message.warn('无权访问该页面');
          setTimeout(() => (messageValveOn = false), messageValveDuration);
        }
        resolve(false);
        return;
      } else {
        allowPaths.push(path);
        resolve(true);
      }
    }
  });
}

function registerAsync(app) {
  clearTimeout(asyncTimer);
  asyncQueue.some((item) => {
    // 遍历到尚未获得实体的节点，则终止
    if (!item.instance) return true;
    if (!item.registered) {
      item.registered = true;
      if (item.type === 'model' && !asyncQueue.some(it => it.registered && it.namespace === item.instance.namespace)) {
        try {
          item.namespace = item.instance.namespace;
          app.model(item.instance);
        } catch (err) {}
      } else if (item.type === 'component') {
        try {
          item.cb(null, item.instance);
          // 所有模块加载完成
          if (!asyncQueue.some(it => !it.registered)) {
            app._store.dispatch({
              type: 'system/updatePageLoading',
              payload: false,
            });
          }
        } catch (err) {
          if (!messageValveOn) {
            messageValveOn = true;
            message.warn(DEFAULT_EXCEPTION_MESSAGE);
            setTimeout(() => (messageValveOn = false), messageValveDuration);
          }
          console.log('加载页面出现异常:', err);
        }
      }
    }
  });
  // 递归，直到所有异步模块/组件加载完毕
  if (asyncQueue.some(it => !it.registered)) {
    asyncTimer = setTimeout(() => registerAsync(app), 10);
  }
}

/**
 * 将异步处理模块/组件加入待处理队列
 * @param  {object}   app      app对象
 * @param  {object}   asyncObj 异步模块/组件
 * @param  {string}   type     类型  model / component
 * @param  {function} cb       component回调函数
 */
function pushAsyncQueue(app, asyncObj, type, cb) {
  const item = {
    registered: false,
    instance: null,
    type,
    cb,
  };
  asyncQueue.push(item);

  if(asyncObj instanceof Function){
      asyncObj((inst) => {
        item.instance = inst;
        registerAsync(app);
      });
  }else{
    console.log(asyncObj);
  }
}

/**
 * 解析路由配置
 * @param  {array}  routeConfigs 路由配置数组
 * @param  {string} pagePath     路由页面地址
 * @param  {array}  routes       旧路由地址
 * @param  {object} app          app实例
 * @return {array}               路由数组
 */
function parseRoutes(routeConfigs, pagePath, routes, app) {

  routeConfigs.forEach((route) => {
    const routeItem = Object.assign({}, route);
    delete routeItem.component;
    delete routeItem.routes;

    // 加载组件
    if (route.component) {
      routeItem.getComponent = async (nextState, cb) => {
        const allow = STANDALONE_ETL || await validateRoute(route.path, route.empowerApi, pagePath, app);
        if (allow) { // 允许访问
          if (route.model) { // 加载model
            pushAsyncQueue(app, route.model, 'model');
          }
          if (Array.isArray(route.models)) { // 加载model组
            route.models.forEach(async (model) => {
              pushAsyncQueue(app, model, 'model');
            });
          }
          if (route.component) { // 加载ui页面组件
            pushAsyncQueue(app, route.component, 'component', cb);
          }
        }
      };
    }
    routeItem.onEnter = (...args) => {
      app._store.dispatch({
        type: 'system/updatePageLoading',
        payload: true,
      });
      if (typeof route.onEnter === 'function') {
        route.onEnter(...args);
      }
    };
    // 子路由处理
    if (route.routes) {
      const tmpRoutes = [];
      routeItem.routes = parseRoutes(route.routes, pagePath, tmpRoutes, app);
    }
    routes.push(routeItem);
  });
  return routes;
}

/**
 * 获取完整路径的路由列表
 * @param  {array}  routes   原始路由列表（树型）
 * @param  {string} rootPath 上级绝对路径
 * @return {array}           扁平结构路由列表，path已改为绝对路径
 */
function getFullPathRoutes(routes, rootPath) {
  const result = [];
  routes.forEach((r) => {
    const newRoute = {};
    if (r.name) newRoute.name = r.name;
    if (r.breadcrumbName) newRoute.breadcrumbName = r.breadcrumbName;
    if (r.empowerApi) newRoute.empowerApi = r.empowerApi;
    if (r.path) {
      if (r.path.indexOf('/') === 0) {
        newRoute.path = r.path;
      } else {
        newRoute.path = `${rootPath}/${r.path}`;
      }
    }
    result.push(newRoute);
    if (Array.isArray(r.routes)) {
      getFullPathRoutes(r.routes, r.path || rootPath).forEach(item => result.push(item));
    }
  });
  return result;
}

const RouterConfig = ({ history, app })=> {


  let routeConfigs;
  if ('[STANDALONE_ETL]') { // 该处'[STANDALONE_ETL]'由webpack控制，请勿修改
    routeConfigs = require.context('./routes', true, /(?:home|gather).*route\.js$/); // 仅导入ETL相关路由页面
  } else {
    routeConfigs = require.context('./routes', true, /route\.js$/); // 导入所有路由页面
  }



  // 加载不需要拦截处理的路由
  const routes = unsafeRoutes.map((r) => {
    return {
      path: r.path,
      name: r.name,
      getComponent: async (nextState, cb) => {
				if(r.model){
					r.model((m) => {
						try { app.model(m); } catch (err) {}
						r.component((c) => {
							cb(null, c);
						});
					});
				}else{
					r.component((c) => {
						cb(null, c);
					});
				}
      },
    };
  });


  // 创建路由
  const createRoute = (route, index) => {

    return (
      <Route key={index} {...route}>
        {route.routes ? route.routes.map((c, i) => createRoute(c, i)) : null}
      </Route>
    )
  };

  // 动态载入路由
  routeConfigs.keys().forEach((filepath) => {
    const pagePath = `./routes/${filepath.replace(pathPatt, '')}`;
    parseRoutes(routeConfigs(filepath), pagePath, routes, app);
  });

  // 将路由配置保存到store中，以便核验权限
  app._store.dispatch({
    type: 'system/saveFullPathRoute',
    payload: getFullPathRoutes(routes, '/'),
  });

  return (<Router history={history}>
    {routes.map((route, i) => createRoute(route, i))}
  </Router>);
}

export default RouterConfig;
