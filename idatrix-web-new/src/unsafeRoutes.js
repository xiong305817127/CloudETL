/**
 * 不需要登录权限的路由均须在此配置
 * 如：登录页、忘记密码页等
 */

// 数据展示
const dataExhibitionModel = resolve => require(["./routes/hotMap/dataExhibition/model.js"],resovle)
const dataExhibition = resolve => require(["./routes/hotMap/dataExhibition/index.js"],resovle)

import { SITE_CUSTOM_THEME } from 'constants';

let loginPage;

switch (SITE_CUSTOM_THEME) {
  default:
    loginPage = resolve => require(['./routes/login/LoginPage'], resolve);
    break;
  case 'government':
    loginPage = resolve => require(['./routes/login/LoginPage-gov'], resolve);
    break;
}

export default [
  { // 登录页
    path: '/login',
    name: 'loginPage',
    model: resolve => require(['./models/login'], resolve),
    component: loginPage,
  },
  { // 忘记密码页
    path: '/resetpwd',
    name: 'resetpwdPage',
    model: resolve => require(['./routes/login/resetpwdModel'], resolve),
    component: resolve => require(['./routes/login/resetpwdPage'], resolve),
  },
  {
    path: "/hotMap",
    name: "hotMap",
    component: resolve => require(["./routes/hotMap/hotMapApp"],resolve),
    model: resolve => require(['./routes/hotMap/models/mainPageModel'], resolve)
	},
	{
    path: "/dataView",
    name: "dataView",
    component: resolve => require(["./routes/framePage/dataView/"],resolve),
	},
	{
    path: "/bbsPage",
    name: "bbsPage",
    component: resolve => require(["./routes/framePage/bbsPage/"],resolve),
	},
	{
    path: "/portalPage",
    name: "portalPage",
    component: resolve => require(["./routes/framePage/portalPage/"],resolve),
	},
	{
    path: "/kibanaPage",
    name: "kibanaPage",
    component: resolve => require(["./routes/framePage/kibanaPage/"],resolve),
	},
	{
    path: "/logAnalysis",
    name: "logAnalysis",
    component: resolve => require(["./routes/framePage/logAnalysis/"],resolve),
  }
];
