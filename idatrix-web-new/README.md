# WEB前端

## 开发框架
react + dva + antd

## 项目结构
```
+ mock              # 模拟api文件
+ public            # 公共文件，不会被构建
- src               # 源码目录
  + assets          # 静态资源
  + components      # 公共组件库
  + models          # 公共model
  - routes          # 路由
    + [子系统]/[页面]
  + services        # 接口调用文件
  + utils           # 工具类
    constants.js    # 全局使用的常量
    unsafeRoutes.js # 不需要登录权限的路由
.justreq            # justreq配置文件
.roadhogrc          # roadhog配置文件
README.md
```

## 版本控制
1. 统一使用git进行版本管理。
2. 每天开始开发前，先从远端V01R01分支pull最新代码。
3. 每天结束开发时，从远端V01R01分支fetch最新代码，merge并解决冲突后，再push回去

[查看操作指引](./git-doc.md)

## 安装配置
### Node环境安装
首先下载安装[Node.js](https://nodejs.org/en/)，运行以下命令安装cnpm
```shell
npm install -g cnpm --registry=https://registry.npm.taobao.org
```

### 脚手架安装
运行以下命令安装脚手架及项目依赖
```shell
cnpm install
```

### justreq安装
运行以下命令安装justreq命令行
```shell
cnpm install -g justreq-cli
```

## 启动脚手架
运行以下命令启动roadhog
```shell
npm start
```
新开一个命令窗口，运行以下命令启动justreq
```shell
justreq start
```

## 开发指引
本框架以路由为单位切分子系统及页面，页面路径`/routes/[子系统]/[页面]`

故，做以下约定：

1. 页面私有组件存放在`/routes/[子系统]/[页面]/components/`即可，不要放入公共组件库
2. 页面私有model也存放在页面路径下，如`/routes/[子系统]/[页面]/model.js`
3. 页面路由统一命名为`route.js`，存入于页面路径`/routes/[子系统]/[页面]/route.js`

### 路由配置
为实现按需加载及路由拦截，路由须统一配置为异步加载。以下是`/route/home/route.js`示例：
```javascript
const HomePage = resolve => require(['./HomePage'], resolve);
// const Model = resolve => require(['./model'], resolve);

export default [
  {
    path: '/',
    name: 'HomePage',
    // model: Model,
    component: HomePage,
  },
  {
    path: '/home',
    name: 'HomePage',
    // model: Model,
    component: HomePage,
  },
];
```
如需配置子路由，添加routes节点即可：
```javascript

export default [
  {
    path: '/home',
    name: 'HomePage',
    // model: Model,
    component: HomePage,
  },
  routes: [
    {
      path: 'child',
      name: 'Child',
      // model: [ChildModel],
      component: [ChildComponent],
    },
  ]
];
```

### 接口代理
为实现多接口机联调、接口掉线自动使用缓存、接口模拟等功能，采用justreq进行接口代理。其配置如下：
```json
{
  "host": "10.0.0.83", // 测试服
  "port": 8090,
  "cacheTime": "20m",
  "cachePath": ".jr/cache/",
  "substitutePath": "mock/",
  "jrPort": 8060,
  "proxyTimeout": "3s",
  "rules": [
    {
      "href": ".+",
      "keepFresh": true
    },
    {
      "href": "/security",
      "host": "10.0.0.119", // 接口机A
      "port": 8080
    },
    {
      "href": "/u/submitLogin.shtml", // 模拟登录接口
      "subs": "submitLogin.jrs"
    },
    {
      "href": "/cloudetl",
      "host": "10.0.0.83", // ETL子系统测试机
      "port": 8181
    }
  ]
}
```

RULES说明

1. href为需要代理的接口路径，支持正则表达式
2. host为接口机地址
3. port为接口机端口
4. subs为本地模拟文件，存放于`/mock`路径

***修改完配置文件后，须按CTRL+C结束justreq进程，并重新运行`justreq start`启动进程***

***如有新接口未能代理到，可于`.roadhogrc`文件proxy配置项添加接口***

更多用法可[点这里查看](https://github.com/vilien/justreq/blob/master/README-cn.md)

*******************************
*(END)*
