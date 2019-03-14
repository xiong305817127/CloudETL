const Layout = resolve => require(['./Layout'], resolve);
const CommonModel = resolve => require(['./model'], resolve);

// 数据资源目录
const Directory = resolve => require(['./directory/Index'], resolve);
const DirectoryResult = resolve => require(['./directory/Result'], resolve);
const DirectoryModel = resolve => require(['./directory/model'], resolve);

// 我的资源
const MyResource = resolve => require(['./myResource/Index'], resolve);
const MyResourceModel = resolve => require(['./myResource/model'], resolve);

// 我的申请
const MyApplication = resolve => require(['./myApplication/Index'], resolve);
const MyApplicationModel = resolve => require(['./myApplication/model'], resolve);

// 待审批
const Unapproved = resolve => require(['./unapproved/Index'], resolve);
const UnapprovedModel = resolve => require(['./unapproved/model'], resolve);

// 已审批
const Approved = resolve => require(['./approved/Index'], resolve);
const ApprovedModel = resolve => require(['./approved/model'], resolve);

export default [
  {
    path: '/resources',
    name: 'resources',
    breadcrumbName: '数据资源展现',
    model: CommonModel,
    component: Layout,
    routes: [
      {
        path: 'directory',
        name: 'directory',
        breadcrumbName: '数据资源目录',
        model: DirectoryModel,
        component: Directory,
        // empowerApi: '/DataResourceController/getAllResource',
      },
      {
        path: 'directory/result',
        name: 'directory-result',
        breadcrumbName: '数据资源目录搜索结果',
        model: DirectoryModel,
        component: DirectoryResult,
        // empowerApi: '/frontMetadataInfoController/searchResource',
      },
      {
        path: 'myResource',
        name: 'myResource',
        breadcrumbName: '我的资源',
        models: [
          MyResourceModel,
          DirectoryModel,
        ],
        component: MyResource,
        // empowerApi: '/myResourceController/search',
      },
      {
        path: 'myApplication',
        name: 'myApplication',
        breadcrumbName: '我的申请',
        model: MyApplicationModel,
        component: MyApplication,
        // empowerApi: '/myResourceController/getMyApprove',
      },
      {
        path: 'unapproved',
        name: 'unapproved',
        breadcrumbName: '待审批',
        model: UnapprovedModel,
        component: Unapproved,
        // empowerApi: '/myResourceController/getMyWillApprove',
      },
      {
        path: 'approved',
        name: 'approved',
        breadcrumbName: '已审批',
        model: ApprovedModel,
        component: Approved,
        // empowerApi: '/myResourceController/getMyApproved',
      },
    ],
  },
];
