const Layout = resolve => require(['./Layout'], resolve);
const CommonModel = resolve => require(['./model'], resolve);

const CheckViewModel = resolve => require(['./common/CheckView/model'], resolve);
const SubscriptionModel = resolve => require(['./common/Subscription/model'], resolve);
//文件下载
const DownloadFileList = resolve => require(['./common/DownloadFileList/index'], resolve);
const DownloadFileListModel = resolve => require(['./common/DownloadFileList/model'], resolve);


//订阅数据描述
const ServerFileList = resolve => require(['./subscription/mysubscriptions/components/DataServer/index'], resolve);
const ServerFileListModel = resolve => require(['./subscription/mysubscriptions/components/DataServer/model'], resolve);


// 系统管理
// 资源分类管理
const Classify = resolve => require(['./database/classify/index'], resolve);
const classifyModel = resolve => require(['./database/classify/model'], resolve);
//系统参数
const Systemparm = resolve => require(['./database/systemparm/index'], resolve);
const systemparmModel = resolve => require(['./database/systemparm/model'], resolve);
//资源维护
const Maintenance = resolve => require(['./database/maintenance/index'], resolve);
const maintenanceModel = resolve => require(['./database/maintenance/model'], resolve);
//服务管理
//源服务
const server = resolve => require(['./database/service/sourceservice/index'], resolve);
const serverModel = resolve => require(['./database/service/sourceservice/model'], resolve);
//共享服务管理
const Shareservice = resolve => require(['./database/service/shareservice/index'], resolve);
const shareserviceModel = resolve => require(['./database/service/shareservice/model'], resolve);

//订阅关系管理
const subscription = resolve => require(['./database/subscription/index'], resolve);
const subscriptionModelS = resolve => require(['./database/subscription/model'], resolve);

// 资源概览
const Sourceview = resolve => require(['./sourceview/index'], resolve);
const SourceviewModel = resolve => require(['./sourceview/model/indexModel'], resolve);
//三大库
const indexType = resolve => require(['./sourceview/model/indexType'], resolve);
const More = resolve => require(['./sourceview/viwe/More'], resolve);
const TypeText = resolve => require(['./sourceview/viwe/TypeText'], resolve);

//资源管理
//我的资源
const MySource = resolve => require(['./management/mysource/index'], resolve);
const mySourceModel = resolve => require(['./management/mysource/model'], resolve);
//资源编辑
const SourceEditView = resolve => require(["./management/mysource/SourceEditView/index"],resolve);
const SourceEditViewModel = resolve => require(["./management/mysource/SourceEditView/model"],resolve);

//注册管理
//
//待审批
const Approval = resolve => require(['./register/approval/index'],resolve);
const ApprovalModel = resolve => require(['./register/approval/model'],resolve);
//我审批的
const Approved = resolve => require(['./register/approved/index'],resolve);
const ApprovedModel = resolve => require(['./register/approved/model'],resolve);

//发布管理
//待审批
const ReleaseApproval = resolve => require(['./release/approval/index'],resolve);
const ReleaseApprovalModel = resolve => require(['./release/approval/model'],resolve);
//我审批的
const ReleaseApproved = resolve => require(['./release/approved/index'],resolve);
const ReleaseApprovedModel = resolve => require(['./release/approved/model'],resolve);

//交换管理
//前置管理
const frontList = resolve => require(['./exchange/front/index'],resolve);
const frontModel = resolve => require(['./exchange/front/model'],resolve);
/*日志管理*/
const serverData = resolve => require(['./exchange/serverData/index'],resolve);
const serverDataModel = resolve => require(['./exchange/serverData/model'],resolve);
/*作业管理*/
const report = resolve => require(['./exchange/Report/index'],resolve);
const reportModel = resolve => require(['./exchange/Report/model'],resolve);
/*上报管理*/
const exchangeData = resolve => require(['./exchange/exchangeData/index'],resolve);
const exchangeDataModel = resolve => require(['./exchange/exchangeData/model'],resolve);

//数据上报
const Myreporting = resolve => require(['./management/reporting/index'], resolve);
const MyreportingModel = resolve => require(['./management/reporting/model'], resolve);


//基础数据
//资源格式分类  classification
const classification = resolve => require(['./exchange/basicData/classification/index'],resolve);
const classificationModel = resolve => require(['./exchange/basicData/classification/model'],resolve);
/*资源格式  resourceFormat*/
const resourceFormat = resolve => require(['./exchange/basicData/resourceFormat/index'],resolve);
const resourceFormatModel = resolve => require(['./exchange/basicData/resourceFormat/model'],resolve);
/*共享方式 sharingMethodclass*/
const sharingMethodclass = resolve => require(['./exchange/basicData/sharingMethod/index'],resolve);
const sharingMethodModel = resolve => require(['./exchange/basicData/sharingMethod/model'],resolve);
// 云阳数据上报，后期可以同于其他地区
const DataUploader = resolve=>require(['./exchange/dataUploader/index'], resolve);
const DataUploaderModel = resolve=>require(['./exchange/dataUploader/dataUploader.model'], resolve);

//订阅管理
//我的订阅
const MySubscriptions = resolve => require(['./subscription/mysubscriptions/index'],resolve);
const mysubscriptionsModel = resolve => require(['./subscription/mysubscriptions/model'],resolve);
//订阅详情
const SubscriptionDetail = resolve => require(['./subscription/mysubscriptions/components/SubscriptionDetail/index'],resolve)
const subscriptionDetailModel = resolve => require(['./subscription/mysubscriptions/components/SubscriptionDetail/model'],resolve)
//待我审批
const SubscriptionApproval = resolve => require(['./subscription/approval/index'],resolve);
const subscriptionApprovalModel = resolve => require(['./subscription/approval/model'],resolve);
//已审批
const SubscriptionApproved = resolve => require(['./subscription/approved/index'],resolve);
const subscriptionApprovedModel = resolve => require(['./subscription/approved/model'],resolve);
//订阅关系管理
const Relationship = resolve => require(['./subscription/Relationship/index'],resolve);
const RelationshipModel = resolve => require(['./subscription/Relationship/model'],resolve);
//资源检索 
const Retrieval = resolve => require(['./retrieval/index'],resolve);
const RetrievalModel = resolve => require(['./retrieval/model'],resolve);

//测试dva框架功能
//测试1
const DvaTest = resolve => require(['./test/dvatest/index'],resolve);
const DvaTestModel = resolve => require(['./test/dvatest/model'],resolve);
//测试2
const FucTest = resolve => require(['./test/fuctest/index'],resolve);
const FucTestModel = resolve => require(['./test/fuctest/model'],resolve);

/*监控*/
const control = resolve => require(['../gather/models/taskcenter/controljobplatform'], resolve);
const runtrans = resolve => require(['../gather/models/designplatform/jobplatform/runjob'], resolve);
const cloudetlCommon = resolve => require(['../gather/models/rbacData/cloudetlCommon'], resolve);
const transspace = resolve => require(['../gather/models/designplatform/jobplatform/jobspace'], resolve);

export default [
  {
    path: '/resources',
    name: 'resources',
    breadcrumbName: '数据共享交换平台',
    models: [
        CommonModel,
        CheckViewModel,
        SubscriptionModel,
        control,
        runtrans,
        cloudetlCommon,
        transspace
    ],
    component: Layout,
    routes: [
      {
        path: '/resources/exchange/datauploader',
        name: 'yunyanguploader',
        breadcrumbName: '云阳数据上报',
        empowerApi: '/resources/database',
        component: DataUploader,
        models: [DataUploaderModel]
      },
      {
        path: '/resources/database',
        name: 'database',
        breadcrumbName: '系统管理',
        empowerApi: '/resources/database',
        routes: [
          {
            path: '/resources/database/classify',
            name: 'classify',
            breadcrumbName: '资源分类管理',
            models: [classifyModel,CommonModel],
            component: Classify,
            empowerApi: '/resources/database/classify' 
					},
					//新增资源检索界面
					// pwj   2019/1/25
					{
						path: '/resources/retrieval',
            name: 'retrieval',
            breadcrumbName: '资源检索',
            models: [RetrievalModel],
            component: Retrieval,
            empowerApi: '/resources/retrieval' 
					},
          {
            path: '/resources/database/maintenance',
            name: 'maintenance',
            breadcrumbName: '资源维护',
            component: Maintenance,
            models:[maintenanceModel,CommonModel],
            empowerApi: '/resources/database/maintenance'
          },
          {
            path: '/resources/database/systemparm',
            name: 'systemparm',
            breadcrumbName: '系统参数',
            models:[systemparmModel,CommonModel],
            component: Systemparm, 
            empowerApi: '/resources/database/systemparm'
          },
           {
            path: '/resources/database/subscription',
            name: 'subscription',
            breadcrumbName: '订阅关系管理',
            models:[subscriptionModelS,CommonModel],
            component: subscription, 
            empowerApi: '/resources/database/subscription',
            routes: [
              {
                path: '/resources/database/DetailsList/:params',
                name: '订阅详情',
                breadcrumbName: '订阅详情',
                component: SubscriptionDetail,
                models:[subscriptionDetailModel,CommonModel]
              }
            ],
          },
          {
            name: 'service',
            path: '/resources/database/service',
            breadcrumbName: '服务管理',
            empowerApi: '/resources/database/service',
            routes: [
              {
                name: 'sourceservice',
                path: '/resources/database/service/sourceservice',
                breadcrumbName: '源服务管理',
                models:[serverModel,CommonModel],
                component: server,
                empowerApi: '/resources/database/service/sourceservice'
               /* routes: [
                  {
                    path: '/resources/management/mysource/:id',
                    name: '资源编辑',
                    breadcrumbName: '资源编辑',
                    component: server,
                    model:serverModel
                  },
                ],*/
                // empowerApi: '/myResourceController/getMyApprove',
              },
              {
                path: '/resources/database/service/shareservice',
                name: 'Shareservice',
                breadcrumbName: '共享服务管理',
                component: Shareservice,
                models:[shareserviceModel,CommonModel],
                empowerApi: '/resources/database/service/shareservice'
              },
              /* {
                path: '/resources/database/reporting',
                name: '数据上报',
                breadcrumbName: '数据上报',
                component: Myreporting,
                model:MyreportingModel
              },*/
            ]
            // empowerApi: '/myResourceController/getMyApprove',
          },
          {
            name: 'basicData',
            path: '/exchange/basicData/classification',
            breadcrumbName: '基础数据',
            empowerApi: '/exchange/basicData/classification',
            routes: [
              {
                name: 'classification',
                path: '/resources/exchange/basicData/classification',
                breadcrumbName: '资源格式分类',
                models:[classificationModel],
                component: classification,
                path: '/resources/exchange/basicData/classification',
              },
              {
                path: '/resources/exchange/basicData/resourceFormat',
                name: 'resourceFormat',
                breadcrumbName: '资源格式',
                component: resourceFormat,
                models:[resourceFormatModel],
                path: '/resources/exchange/basicData/resourceFormat',
              },{
                path: '/resources/exchange/basicData/sharingMethod',
                name: 'sharingMethodclass',
                breadcrumbName: '共享方式',
                component: sharingMethodclass,
                models:[sharingMethodModel],
                path: '/resources/exchange/basicData/sharingMethod',
              },
            ]
          },

        ]
      },
      {
        path: '/resources/sourceview',
        name: 'sourceview',
        breadcrumbName: '资源概览',
        model: SourceviewModel,
        component: Sourceview,
        empowerApi: '/resources/sourceview',
        routes:[
          {
            path: '/resources/sourceview/viwe/More',
            name: 'More',
            breadcrumbName: '更多资源',
            component: More,
             model: SourceviewModel,
          },{
            path: '/resources/sourceview/viwe/TypeText/base/:libName',
            name: 'TypeText',
            breadcrumbName: '基础库',
            component: TypeText,
            model: indexType,
          },{
            path: '/resources/sourceview/viwe/TypeText/department/:libName',
            name: 'department',
            breadcrumbName: '部门库',
            component: TypeText,
            model: indexType,
          },{
            path: '/resources/sourceview/viwe/TypeText/topic/:libName',
            name: 'topic',
            breadcrumbName: '主题库',
            component: TypeText,
            model: indexType,
          }
        ],
        // empowerApi: '/DataResourceController/getAllResource',
      },
      {
        name: 'management',
        path: '/resources/management',
        breadcrumbName: '资源管理',
        empowerApi: '/resources/management',
        routes: [
          {
            name: 'mysource',
            path: '/resources/management/mysource',
            breadcrumbName: '我的资源',
            models:[mySourceModel,CommonModel],
            component: MySource,
            empowerApi: '/resources/management/mysource',
            routes: [
              {
                path: '/resources/management/mysource/:id',
                name: '资源编辑',
                breadcrumbName: '资源编辑',
                component: SourceEditView ,
                models: [SourceEditViewModel,CommonModel]
              },
            ],
          },
          {
              path: '/resources/management/reporting',
              name: '数据上报',
              breadcrumbName: '数据上报',
              component: Myreporting,
              models: [MyreportingModel,CommonModel],
              empowerApi: '/resources/management/reporting'
            }
        ]
      },
      {
        name: 'register',
        path: '/resources/register',
        breadcrumbName: '注册管理',
        empowerApi: '/resources/register',
        routes: [
          {
            path: '/resources/register/approval',
            name: 'approval',
            breadcrumbName: '待我审批',
            component: Approval,
            model:ApprovalModel,
            empowerApi: '/resources/register/approval',
             routes: [
              {
                path: '/resources/register/approval/:id',
                name: '审批',
                breadcrumbName: '审批',
                component: SourceEditView,
                model:SourceEditViewModel
              },
            ],
          },
          {
            path: '/resources/register/approved',
            name: 'approved',
            breadcrumbName: '我审批的',
            component: Approved,
            model:ApprovedModel,
            empowerApi: '/resources/register/approved'
          },
        ],
        // empowerApi: '/myResourceController/getMyApprove',
      },
      {
        name: 'exchange',
        path: '/resources/exchange',
        breadcrumbName: '交换管理',
        empowerApi: '/resources/exchange',
        routes: [
          {
            path: '/resources/exchange/front',
            name: 'frontList',
            breadcrumbName: '前置管理',
            component: frontList,
            model:frontModel,
            empowerApi: '/resources/exchange/front',
          }, {
            path: '/resources/exchange/serverData',
            name: 'serverData',
            breadcrumbName: '日志管理',
            empowerApi: '/resources/exchange/serverData',
            routes: [
              {
                path: '/resources/exchange/serverData/index',
                name: '服务日志',
                breadcrumbName: '服务日志',
                component: serverData,
                model:serverDataModel,
                empowerApi: '/resources/exchange/serverData/index',
              }
            ],
          },{
            path: '/resources/exchange/report',
            name: 'report',
            breadcrumbName: '作业管理',
            empowerApi: '/resources/exchange/report',
            routes: [
              {
                path: '/resources/exchange/report/index',
                name: '交换作业',
                breadcrumbName: '交换作业',
                component: report,
                model:reportModel,
                empowerApi: '/resources/exchange/report/index',
              }, {
                path: '/resources/exchange/report/exchangeData',
                name: '上报作业',
                breadcrumbName: '上报作业',
                component: exchangeData,
                model:exchangeDataModel,
                empowerApi: '/resources/exchange/report/exchangeData',
              },
            ],
          },
        ],
      },
      {
        name: 'release',
        path: '/resources/release',
        breadcrumbName: '发布管理',
        empowerApi: '/resources/release',
        routes: [
          {
            path: '/resources/release/approval',
            name: 'approval',
            breadcrumbName: '待我审批',
            component: ReleaseApproval,
            model:ReleaseApprovalModel,
            empowerApi: '/resources/release/approval',
          },
           {
            path: '/resources/release/approved',
            name: 'approved',
            breadcrumbName: '我审批的',
            component: ReleaseApproved,
            model:ReleaseApprovedModel,
            empowerApi: '/resources/release/approved',
          },
        ]
        // empowerApi: '/myResourceController/getMyApprove',
      },
       {
        name: 'subscription',
        path: '/resources/subscription',
        breadcrumbName: '订阅管理',
        empowerApi: '/resources/subscription',
        routes: [
          {
            path: '/resources/subscription/mysubscriptions',
            name: 'mysubscriptions',
            breadcrumbName: '我订阅的',
            component: MySubscriptions,
            model:mysubscriptionsModel,
            empowerApi: '/resources/subscription/mysubscriptions',
             routes: [
              {
                path: '/resources/subscription/mysubscriptions/DetailsList/:params',
                name: '订阅详情',
                breadcrumbName: '订阅详情',
                component: SubscriptionDetail,
                model:subscriptionDetailModel
              },
              {
                path: '/resources/subscription/mysubscriptions/downloadfile/:fileId',
                name: '文件下载',
                breadcrumbName: '文件下载',
                component: DownloadFileList,
                model:DownloadFileListModel
              },
              {
                path: '/resources/subscription/mysubscriptions/dataserver/:fileId',
                name: '数据查询服务',
                breadcrumbName: '数据查询服务',
                component: ServerFileList,
                model:ServerFileListModel
              },
            ],
          },
          {
            path: '/resources/subscription/approval',
            name: 'approval',
            breadcrumbName: '待我审批',
            component: SubscriptionApproval,
            model:subscriptionApprovalModel,
            empowerApi: '/subscription/subscription/approval',
            routes: [
              {
                path: '/resources/subscription/approval/:params',
                name: '审批',
                breadcrumbName: '审批',
                component: SubscriptionDetail,
                model:subscriptionDetailModel
              },{
                path: '/resources/subscription/approvalInst/:params',
                name: '订阅详情',
                breadcrumbName: '订阅详情',
                component: SubscriptionDetail,
                model:subscriptionDetailModel
              },
            ],
          },
           {
            path: '/resources/subscription/approved',
            name: 'approved',
            breadcrumbName: '我审批的',
            component: SubscriptionApproved,
            model:subscriptionApprovedModel,
            empowerApi: '/resources/subscription/approved',
          },{
            path: '/resources/subscription/Relationship', //alisa 2019-9-26日填写
            name: 'Relationship',
            breadcrumbName: '订阅关系管理',
            component: Relationship,
            model:RelationshipModel,
            empowerApi: '/resources/subscription/Relationship',
            routes: [
              {
                path: '/resources/subscription/Relationship/datali/:params',
                name: '订阅详情',
                breadcrumbName: '订阅详情',
                component: SubscriptionDetail,
                model:subscriptionDetailModel
              }
            ],
          },
        ]

        // empowerApi: '/myResourceController/getMyApprove',
      },
      {
        name: 'test',
        path: '/resources/test',
        breadcrumbName: '功能测试',
        routes: [
          {
            path: '/resources/test/dvatest',
            name: 'dvatest',
            breadcrumbName: '框架测试',
            component: DvaTest,
            model:DvaTestModel
          },
           {
            path: '/resources/test/fuctest',
            name: 'fuctest',
            breadcrumbName: '函数测试',
            component: FucTest,
            model:FucTestModel
          },
        ]
        // empowerApi: '/myResourceController/getMyApprove',
      }
    ]
  }
];


/*


 {
        name: 'mysource',
        path: '/resources/mysource',
        breadcrumbName: '我的资源',
        icon: 'environment-o',
        models: [
          MySourceModel,
          RegisterModel
        ],
        component: MySource,
        routes: [
          {
            path: '/resources/mysource/:id/:serverModel',
            name: '资源编辑',
            breadcrumbName: '资源编辑',
            component: SourceEditView,
            model:SourceEditViewModel
          },
        ],
        // empowerApi: '/myResourceController/getMyApprove',
      },
        {
        path: '/resources/server',
        name: 'server',
        breadcrumbName: '数据服务资源',
        model: serverModel,
        component: server,
        icon: 'environment-o',
         routes:[
          {
            path: '/resources/server/:id',
            name: 'ServerModeIndel',
            breadcrumbName: '服务编辑',
            component: ServerModeIndel,
             model: serverModel,
          }
        ],
      },
        {
        path: '/resources/reporting',
        name: 'Myreporting',
        breadcrumbName: '数据上报',
        model: MyreportingModel,
        component: Myreporting,
        icon: 'environment-o',
      },
      {
        name: 'approval',
        path: '/resources/approval',
        breadcrumbName: '资源审批',
        icon: 'fork',
        // empowerApi: '/myResourceController/getMyApprove',
      },


 */