import { STANDALONE_ETL, SITE_CUSTOM_THEME } from 'constants';

const HomePage = resolve => require(['./HomePage'], resolve); // 默认风格
const Government = resolve => require(['./Government'], resolve); // 政府风格

let Comp;
switch (SITE_CUSTOM_THEME) {
  default:
    Comp = HomePage;
    break;
  case 'government':
    Comp = Government;
    break;
}

// 首页跳转处理
const redirect = (nextState, replace) => {
  if (STANDALONE_ETL) {
    replace('/gather/taskcenter/transcenter');
  }
};

export default [
  {
    path: '/',
    name: 'HomePage',
    // model: Model,
    component: Comp,
    indexRoute: { onEnter: redirect },
  },
  {
    path: '/home',
    name: 'HomePage',
    component: Comp,
    indexRoute: { onEnter: redirect },
  },
];
