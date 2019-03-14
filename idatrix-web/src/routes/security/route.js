const Layout = resolve => require(['./Layout'], resolve);
const commonModel = resolve => require(['./model'], resolve);

const ResourcesManagingTable = resolve => require(['./resourcesManage/Index'], resolve);
const ResourcesModel = resolve => require(['./resourcesManage/model'], resolve);

// 租户管理
const TenantManagementTable = resolve => require(['./tenantManage/Index'], resolve);
const NewTableFlow = resolve => require(['./components/NewTableFlow'], resolve);
const ModifyTableFlow = resolve => require(['./components/ModifyTableFlow'], resolve);
const TenantModel = resolve => require(['./tenantManage/model'], resolve);

// 组织机构
const OrganizationManagementTable = resolve => require(['./organizationManage/Index'], resolve);
const OrganizationModel = resolve => require(['./organizationManage/model'], resolve);

// 用户管理
const UserManagementTable = resolve => require(['./usersManage/Index'], resolve);
const UserModel = resolve => require(['./usersManage/model'], resolve);

// 角色管理
const RoleManagementTable = resolve => require(['./roleManage/Index'], resolve);
const RoleModel = resolve => require(['./roleManage/model'], resolve);

// 菜单操作权限
// const MenuManagementTable = resolve => require(['./menuManage/Index'], resolve);
// const MenuModel = resolve => require(['./menuManage/model'], resolve);

// 脱敏规则管理
const DesensitizationRuleTable = resolve => require(['./desensitizationRuleManage/Index'], resolve);
const DesensitizationRuleModel = resolve => require(['./desensitizationRuleManage/model'], resolve);

// 脱敏规则管理
const LogoManagement = resolve => require(['./LogoManagement/index'], resolve);
const LogoManagementModel = resolve => require(['./LogoManagement/model'], resolve);

export default [
  {
    path: '/security',
    name: 'security',
    breadcrumbName: '安全管理',
    model: commonModel,
    component: Layout,
    routes: [
      {
        path: '/ResourcesManagingTable',
        name: 'ResourcesManagingTable',
        breadcrumbName: '资源管理',
        model: ResourcesModel,
        component: ResourcesManagingTable,
        empowerApi: '/permission/list.shtml',
      },
      {
        path: 'TenantManagementTable',
        name: 'TenantManagementTable',
        breadcrumbName: '租户管理',
        model: TenantModel,
        component: TenantManagementTable,
        empowerApi: '/renter/list.shtml',
        routes: [
          {
            path: 'NewTableFlow',
            name: 'NewTableFlow',
            breadcrumbName: '新增租户',
            component: NewTableFlow,
            empowerApi: '/renter/add.shtml',
          },
          {
            path: 'ModifyTableFlow/:id',
            name: 'ModifyTableFlow',
            breadcrumbName: '编辑租户',
            component: ModifyTableFlow,
            empowerApi: '/renter/update.shtml',
          },
        ],
      },
      {
        path: '/OrganizationManagementTable',
        name: 'OrganizationManagementTable',
        breadcrumbName: '组织机构管理',
        models: [OrganizationModel, UserModel],
        component: OrganizationManagementTable,
        empowerApi: '/organization/list.shtml',
      },
      {
        path: '/UserManagementTable',
        name: 'UserManagementTable',
        breadcrumbName: '用户管理',
        model: UserModel,
        component: UserManagementTable,
        empowerApi: '/member/users.shtml',
      },
      {
        path: '/RoleManagementTable',
        name: 'RoleManagementTable',
        breadcrumbName: '角色管理',
        models: [OrganizationModel, UserModel, RoleModel],
        component: RoleManagementTable,
        empowerApi: '/role/list.shtml',
      },
      {
        path: '/DesensitizationRuleTable',
        name: 'DesensitizationRuleTable',
        breadcrumbName: '脱敏规则管理',
        models: DesensitizationRuleModel,
        component: DesensitizationRuleTable,
        empowerApi: '/security/sensitiveInfo/index',
      },
      {
        path: '/LogoManagement',
        name: 'LogoManagement',
        breadcrumbName: '日志管理',
        model: LogoManagementModel,
        component: LogoManagement,
        empowerApi:'/auditLog/list.shtml',
      },
    ],
  },
];
