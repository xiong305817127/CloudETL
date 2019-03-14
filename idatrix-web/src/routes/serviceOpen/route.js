const Layout = resolve => require(['./Layout'], resolve);
const MyAppTable = resolve => require(['./MyApp/Index'], resolve);
const ServiceTableVisitApi = resolve => require(['./VisitApi/Index'], resolve);
const ApiTableModel = resolve => require(['./VisitApi/ApiTableModel'], resolve);
const ServiceTableAuthor = resolve => require(['./ServiceAuthor/Index'], resolve);
// const Model = resolve => require(['./model'], resolve);

export default [
  {
    path: '/service',
    name: 'Service',
    breadcrumbName: '服务开放&治理',
    // model: Model,
    component: Layout,
    routes: [
      {
        path: '/MyAppTable',
        name: 'MyAppTable',
        breadcrumbName: '我的应用',
        component: MyAppTable,
        empowerApi: '/app/list',
      },
      {
        path: 'ServiceTableVisitApi',
        name: 'ServiceTableVisitApi',
        breadcrumbName: '数据访问API',
        component: ServiceTableVisitApi,
        empowerApi: '/service/list',
        routes: [
          {
            path: 'api/:id',
            name: 'ApiTableModel',
            breadcrumbName: 'API详情',
            component: ApiTableModel,
            empowerApi: '/service/detail',
          },
        ],
      },
      {
        path: '/ServiceTableAuthor',
        name: 'ServiceTableAuthor',
        breadcrumbName: '服务授权（2.0）',
        component: ServiceTableAuthor,
      },
    ],
  },
];
