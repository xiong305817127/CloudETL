const Layout = resolve => require(['./Layout'], resolve);

// 数据查询
const DataQueryTable = resolve => require(['./dataQuery/Index.js'], resolve);

// 多维分析
const StatisticalAnalysisTable = resolve => require(['./statisticalAnalysis/Index.js'], resolve);
const DataManagement = resolve => require(['./statisticalAnalysis/dataManagement/'], resolve);
const AnalysisManagement = resolve => require(['./statisticalAnalysis/analysisManagement/index.js'], resolve);
const SaikuId = resolve => require(['./statisticalAnalysis/analysisManagement/SaikuId/'], resolve);
const saikuid = resolve => require(['./statisticalAnalysis/analysisManagement/SaikuId/models/rowid'], resolve);

// 目录管理
const ListManagementTable = resolve => require(['./fileSystem/Index.js'], resolve);
const MyDocumentsTable = resolve => require(['./fileSystem/MyDocumentsTable.js'], resolve);
const RecycleBinTable = resolve => require(['./fileSystem/RecycleBinTable.js'], resolve);

// 全文检索
const FullTextSearch = resolve => require(['./fullTextSearch/Index.js'], resolve);
const FullTextSearchCustom = resolve => require(['./fullTextSearch/Custom.js'], resolve);
const FullTextSearchModel = resolve => require(['./fullTextSearch/model.js'], resolve);

// 任务调度
const TaskManageTable = resolve => require(['./taskManage/Index.js'], resolve);
const EditTaskManage = resolve => require(['./taskManage/EditTaskManage.js'], resolve);
const TaskManageModel = resolve => require(['./taskManage/model.js'], resolve);
const flowspace = resolve => require(['./taskManage/flowspace.js'], resolve);
const deletemodel = resolve => require(['./taskManage/deletemodel.js'], resolve);
const items = resolve => require(['./taskManage/items.js'], resolve);
const TaskExcutor = resolve => require(['./taskManage/TaskExcutor.js'], resolve);
const TaskExcutorNode = resolve => require(['./taskManage/TaskExcutorNode.js'], resolve);
const TaskHelp = resolve => require(['./taskManage/TaskHelp.js'], resolve);

//数据模型
const DataModel = resolve => require(['./dataModel/index.js'], resolve);
const DataModelModel = resolve => require(['./dataModel/model.js'], resolve);
const ModelId = resolve => require(['./dataModel/components/ModelId/'], resolve);
const ModelIdModel = resolve => require(['./dataModel/components/ModelId/model.js'], resolve);

//Bi管理
const BiManage = resolve => require(['./biManage/'], resolve);
const BiManageModel = resolve => require(['./biManage/model.js'], resolve);


export default [
  {
    path: '/analysis',
    name: 'Data',
    breadcrumbName: '数据分析&探索',
    //  model: Model,
    component: Layout,
    routes: [
      {
        path: '/DataQueryTable',
        name: 'DataQueryTable',
        breadcrumbName: '数据查询',
        component: DataQueryTable,
        empowerApi: '/db/storage/list',
      },
      {
        path: '/ListManagementTable',
        name: 'ListManagementTable',
        breadcrumbName: '目录管理',
        component: ListManagementTable,
        empowerApi: '/hdfs/metadata/list',
      },
      {
        path: '/ListManagementTable/MyDocumentsTable/:path',
        name: 'MyDocumentsTable',
        breadcrumbName: '目录管理',
        component: MyDocumentsTable,
        empowerApi: '/hdfs/file/list',
      },
      /*{
        path: '/RecycleBinTable',
        name: 'RecycleBinTable',
        breadcrumbName: '目录管理 / 回收站',
        component: RecycleBinTable,
      },*/
      {
        path: 'FullTextSearch',
        name: 'FullTextSearch',
        breadcrumbName: '全文检索',
        model: FullTextSearchModel,
        component: FullTextSearch,
        empowerApi: '/es/index/list',
        routes: [
          {
            path: 'custom',
            name: 'Custom',
            breadcrumbName: '自定义搜索',
            component: FullTextSearchCustom,
            empowerApi: '/es/search/custom',
          },
        ],
      },
      {
        path: 'TaskManage',
        name: 'TaskManage',
        breadcrumbName: '任务管理',
        model: TaskManageModel,
        component: TaskManageTable,
        empowerApi: '/task/definition/getTasks',
        routes: [
          {
            path: 'help',
            name: 'help',
            breadcrumbName: '任务帮助',
            component: TaskHelp,
          },
          {
            path: 'EditTaskManage/:id',
            name: 'EditTaskManage',
            breadcrumbName: '任务详情',
            component: EditTaskManage,
            models: [deletemodel, flowspace, items],
            routes: [
              {
                path: 'TaskExcutor/:execId',
                name: 'TaskExcutor',
                breadcrumbName: '任务执行情况',
                component: TaskExcutor,
                routes: [
                  {
                    path: 'node/:stepname',
                    name: 'TaskExcutorNode',
                    breadcrumbName: '节点',
                    component: TaskExcutorNode,
                  },
                ]
              },
            ]
          },
        ]
      },
      {
        path: 'ReportForms',
        name: 'ReportForms',
				breadcrumbName: 'BI分析',
				empowerApi: '/analysis/ReportForms',
        routes: [
          {
            path: '/analysis/DataModel',
            name: 'DataModel',
            breadcrumbName: '数据模型',
            models: [DataModelModel, ModelIdModel],
						component: DataModel,
						empowerApi: '/analysis/DataModel',
            routes: [
              {
                path: '/analysis/DataModel/Config',
                name: 'ModelId',
                breadcrumbName: '数据模型配置',
                component: ModelId
              },
            ]
          },
          {
            path: '/analysis/ReportForms/New',
            name: 'New',
						breadcrumbName: '新建报表分析',
						empowerApi: '/analysis/ReportForms/New',
          },
          {
            path: '/analysis/ReportForms/Open',
            name: 'Open',
						breadcrumbName: '打开报表分析',
						empowerApi: '/analysis/ReportForms/Open',
					},
					{
            path: '/analysis/ReportForms/Dashboards',
            name: 'Dashboards',
						breadcrumbName: '打开仪表盘',
						empowerApi: '/analysis/ReportForms/Dashboards',
          },
          // {
          //   breadcrumbName: 'BI管理',
          //   path: '/analysis/ReportForms/manage',
          //   model: BiManageModel,
          //   component:BiManage 
          // }
        ]
      }
    ],
  },
];


// {
//   path: '/analysis/ReportForms/Dashboards',
//   name: 'Dashboards',
//   breadcrumbName: '仪表盘'
// }{
// 	path: '/StatisticalAnalysisTable',
// 	name: 'StatisticalAnalysisTable',
// 	breadcrumbName: '多维分析',
// 	component: StatisticalAnalysisTable,
// 	empowerApi: '/analysis/StatisticalAnalysisTable',
// 	routes: [
// 		{
// 			path: '/analysis/StatisticalAnalysisTable/DataManagement',
// 			name: 'DataManagement',
// 			breadcrumbName: '数据集管理',
// 			component: DataManagement,
// 			//  empowerApi: '/analysis/StatisticalAnalysisTable/DataManagement',
// 		},
// 		{
// 			path: '/analysis/StatisticalAnalysisTable/AnalysisManagement',
// 			name: 'AnalysisManagement',
// 			breadcrumbName: '自助分析管理',
// 			component: AnalysisManagement,
// 			//empowerApi: '/analysis/StatisticalAnalysisTable/AnalysisManagement',
// 			routes: [
// 				{
// 					path: '/analysis/StatisticalAnalysisTable/AnalysisManagement/:rowid',
// 					name: 'AnalysisExcutor',
// 					breadcrumbName: '分析透视图',
// 					model: saikuid,
// 					component: SaikuId,
// 				},
// 			]
// 		}
// 	]
// },