const Layout = resolve => require(['./Layout'], resolve);
const CommonModel = resolve => require(['./model'], resolve);

const deleteTip = resolve => require(['../../models/deletetip'],resolve);
// const DataStandardView = resolve => require(['./standardManage/DataStandardView'], resolve);

// 前置机资源管理
const FrontendResManage = resolve => require(['./FrontendResManage/Index'], resolve);
const FrontendModel = resolve => require(['./FrontendResManage/models/frontendfesmanage'], resolve);
const MFServermodel = resolve => require(['./FrontendResManage/models/mfservermodel'], resolve);
const ZCRegisteredModel = resolve => require(['./FrontendResManage/ZCRegisteredModel'], resolve);
// 数据系统注册
const DataSystemSegistration = resolve => require(['./DataSystem/Index'], resolve);

const StorageTable = resolve => require(['./DataSystem/components/StorageTable'], resolve);
const StorageTableModel = resolve => require(['./DataSystem/models/storagetable'], resolve);
const NewStorageTableModel= resolve => require(['./DataSystem/models/newstoragetable'], resolve);
const DataSystemModel = resolve => require(['./DataSystem/model'], resolve);
const FTPModel = resolve => require(['./DataSystem/models/ftpmodel'], resolve);
const DSRegisterModel = resolve => require(['./DataSystem/models/dsregistermodel'], resolve);
const DSSegistrationModel = resolve => require(['./DataSystem/models/datasystemsegistration'], resolve);
const DataBaseModel = resolve => require(['./DataSystem/models/databasemodel'], resolve);
const DSRegisterplatformModel = resolve => require(['./DataSystem/models/dsregisterplatform'], resolve);
const Tab1Model = resolve => require(['./DataSystem/models/tab1model'], resolve);
const AddSubdirectoriesModel = resolve => require(['./DataSystem/models/addsubdirectories'], resolve);
const resitionemodel = resolve => require(['./DataSystem/models/resitionemodel'], resolve);

    
// 元数据定义
const MetadataDefine = resolve => require(['./MetadataDefine/Index'], resolve);
const MetaDataDefineModel = resolve => require(['./MetadataDefine/models/metaData.model'], resolve);
const MetaFileDefineModel = resolve => require(['./MetadataDefine/models/metaFile.model'], resolve);
const MetaESModel = resolve => require(['./MetadataDefine/models/es.model'], resolve);
const MetadataRecycle = resolve => require(['./MetadataDefine/MetaDataRecycle'], resolve);
const MetadataRecycleModel = resolve => require(['./MetadataDefine/models/metaDataRecycle.model'], resolve);
const MetadataDrafts = resolve => require(['./MetadataDefine/MetaDataDrafts'], resolve);
const MetadataDraftsModel = resolve => require(['./MetadataDefine/models/metaDataDrafts.model'], resolve);
const MetadataHistory = resolve => require(['./MetadataDefine/MetaDataHistory'], resolve);
const MetadataHistoryModel = resolve => require(['./MetadataDefine/models/metaDataHistory.model'], resolve);
const MetaFileRecycle = resolve => require(['./MetadataDefine/MetaFileRecycle'], resolve);
const MetaFileRecycleModel = resolve => require(['./MetadataDefine/models/metaFileRecycle.model'], resolve);
const MetadataESHistory = resolve => require(['./MetadataDefine/ESHistory'], resolve);
const MetadataESHistoryModel = resolve => require(['./MetadataDefine/models/ESHistory.model'], resolve);
const DatabaseAcquisitionModel = resolve => require(['./MetadataDefine/models/acquisition'], resolve);
// 数据采集
// const DataAcquisition = resolve => require(['./DataAcquisition/index'], resolve);
// const DatabaseNameModel = resolve => require(['./DataAcquisition/viwe/DatabaseNameModel'], resolve);
// const AcquisitionModel = resolve => require(['./DataAcquisition/viwe/AcquisitionModel'], resolve);

// 数据关系管理
const DataRelationshipManagement = resolve => require(['./DataRelationship/Index'], resolve);
const DRMnewfilemodel = resolve => require(['./DataRelationship/models/drmnewfilemodel'], resolve);
const DRMselectTable = resolve => require(['./DataRelationship/models/drmselecttable'], resolve);
/*const Tree = resolve => require(['./DataRelationship/models/Tree'], resolve);*/

// 数据标准查询
const DataStandardView = resolve => require(['./DataStandardView/Index'], resolve);

const DataStandardModel = resolve => require(['./DataStandardView/models/frontendfesmanage'], resolve);
const DataItemModel = resolve => require(['./DataStandardView/models/mfservermodel'], resolve);

/*const DataModeldView = resolve => require(['./modeManage/modeles'], resolve);*/
const Acquisition = resolve => require(['./DataAcquisition/model/acquisition'], resolve);

// 数据地图
const DataMap = resolve => require(['./DataMap/Index'], resolve);
const DataMapModel = resolve => require(['./DataMap/model'], resolve);
const DataMapTable = resolve => require(['./DataMap/TableMap'], resolve);
const DataMapFields = resolve => require(['./DataMap/FieldsMap'], resolve);

//数据资源目录合并
const ResourceCommonModel = resolve => require(['../resource-bak/model'], resolve);

// 数据资源目录
const Directory = resolve => require(['../resource-bak/directory/Index'], resolve);
const DirectoryResult = resolve => require(['../resource-bak/directory/Result'], resolve);
const DirectoryModel = resolve => require(['../resource-bak/directory/model'], resolve);

// 我的资源
const MyResource = resolve => require(['../resource-bak/myResource/Index'], resolve);
const MyResourceModel = resolve => require(['../resource-bak/myResource/model'], resolve);

// 我的申请
const MyApplication = resolve => require(['../resource-bak/myApplication/Index'], resolve);
const MyApplicationModel = resolve => require(['../resource-bak/myApplication/model'], resolve);

// 待审批
const Unapproved = resolve => require(['../resource-bak/unapproved/Index'], resolve);
const UnapprovedModel = resolve => require(['../resource-bak/unapproved/model'], resolve);

// 已审批
const Approved = resolve => require(['../resource-bak/approved/Index'], resolve);
const ApprovedModel = resolve => require(['../resource-bak/approved/model'], resolve);



export default [
  {
    path: '/metadata',
    name: 'metadata',
    breadcrumbName: '元数据管理',
    model: CommonModel,
    models: [
      // NewfileModel,
      // StructuredFieldIntroducer,
      // TableStructureDefinition,
      // TableIndexDefine,
      // ShangCuanYangLi,
      // ShouQuan,
      // ShouQuan1,
      // NewFileFirectory,
      // SelectDepartment,
	    deleteTip,
      ResourceCommonModel,
      DRMnewfilemodel, // 1
      DRMselectTable,
      AddSubdirectoriesModel, // 2
      NewStorageTableModel // 3
    ],
    component: Layout,
    routes: [
      {
        path: '/FrontendResManage',
        name: 'FrontendResManage',
        breadcrumbName: '资源管理',
        models: [
          FrontendModel,
          MFServermodel,
          ZCRegisteredModel,
        ],
        component: FrontendResManage,
        empowerApi: '/frontEndServerController/get',
      },
      {
        path: '/DataSystemSegistration',
        name: 'DataSystemSegistration',
        breadcrumbName: '数据系统注册',
        model: DataSystemModel,
        models: [
          FTPModel,
          DSRegisterModel,
          DSSegistrationModel,
          DataBaseModel,
          DSRegisterplatformModel,
          Tab1Model,
          resitionemodel,
        ],
        component: DataSystemSegistration,
        empowerApi: '/DataSystem',
        routes:[
          {
            path: '/DataSystemSegistration/StorageTable/:dsId/:serverName/:dbDatabasename',
            name: 'StorageTable',
            breadcrumbName: '表结构注册',
            models: [
              StorageTableModel
            ],
            component: StorageTable,
            empowerApi: '/frontMetadataInfoController/search',
          }
        ]
      },
      {
        path: '/MetadataDefine',
        name: 'MetadataDefine',
        breadcrumbName: '元数据定义',
        models: [
          MetaDataDefineModel,
          MetaFileDefineModel,
          MetaESModel,
          DatabaseAcquisitionModel,
        ],
        component: MetadataDefine,
        empowerApi: '/frontMetadataInfoController',
        routes:[
          {
            path: '/MetadataDefine/tableRecycle',
            name: 'tableRecycle',
            breadcrumbName: '数据表回收站',
            component: MetadataRecycle,
            model: MetadataRecycleModel,
          },
          {
            path: '/MetadataDefine/drafts',
            name: 'Drafts',
            breadcrumbName: '草稿箱',
            component: MetadataDrafts,
            model: MetadataDraftsModel,
          },
          {
            path: '/MetadataDefine/hisrory',
            name: 'History',
            breadcrumbName: '历史版本',
            component: MetadataHistory,
            model: MetadataHistoryModel,
          },
          {
            path: '/MetadataDefine/fileRecycle',
            name: 'fileRecycle',
            breadcrumbName: '文件类回收站',
            component: MetaFileRecycle,
            model: MetaFileRecycleModel,
          },
          {
            path: '/MetadataDefine/es-hisrory',
            name: 'ESHistory',
            breadcrumbName: '历史版本',
            component: MetadataESHistory,
            model: MetadataESHistoryModel,
          },
        ]
      },
      // {
      //   path: '/DataAcquisition',
      //   name: 'DataAcquisition',
      //   breadcrumbName: '外部数据源数据定义采集',
      //    routes:[
      //     {
      //       path: '/DataAcquisition/DatabaseNameModel/:dsId/:dbDatabasename',
      //       name: 'DatabaseNameModel',
      //       breadcrumbName: '外部数据源数据采集',
      //       component: DatabaseNameModel,
      //       empowerApi: '/frontMetadataInfoController/search',
      //     },{
      //       path: '/DataAcquisition/AcquisitionModel',
      //       name: 'AcquisitionModel',
      //       breadcrumbName: '外部数据源数据采集',
      //       component: AcquisitionModel,
      //     }
      //   ],
      //   models: [
      //     Acquisition,
      //   ],
      //   component: DataAcquisition,
      //   empowerApi: '/directDataCollect/getdbinfo',
      // },
      {
        path: '/DataRelationshipManagement',
        name: 'DataRelationshipManagement',
        breadcrumbName: '数据关系管理',
        models: [
          DRMnewfilemodel, // 1
          DRMselectTable,
         
        ],
        component: DataRelationshipManagement,
        empowerApi: '/MetaRelationshipController/search',
      },
      {
        path: '/DataStandardView',
        name: 'DataStandardView',
        breadcrumbName: '数据标准查询',
        models: [
          DataStandardModel,
          DataItemModel,
        ],
        component: DataStandardView,
        empowerApi: '/DataStandardController/search',
      },
      {
        path: '/DataMap',
        name: 'DataMap',
        breadcrumbName: '数据地图',
        component: DataMap,
        model: DataMapModel,
        empowerApi: '/graph/queryRelationship',
        routes: [
          {
            path: 'table/:sourceId',
            name: 'DataMapTable',
            breadcrumbName: '数据地图',
            component: DataMapTable,
          },
          {
            path: 'table/:sourceId/:targetId',
            name: 'DataMapTable',
            breadcrumbName: '数据地图',
            component: DataMapTable,
          },
          {
            path: 'fields/:sourceId/:targetId',
            name: 'DataMapTable',
            breadcrumbName: '数据地图',
            component: DataMapFields,
          },
        ],
      },
       {
        path: '/resources/directory',
        name: 'directory',
        breadcrumbName: '数据资源目录',
        model: DirectoryModel,
        component: Directory,
        empowerApi: '/resources/directory',
      },
      {
        path: '/resources/directory/result',
        name: 'directory-result',
        breadcrumbName: '数据资源目录搜索结果',
        model: DirectoryModel,
        component: DirectoryResult,
        empowerApi: '/frontMetadataInfoController/searchResource',
      },
      {
        path: '/resources/myResource',
        name: 'myResource',
        breadcrumbName: '我的资源',
        models: [
          MyResourceModel,
          DirectoryModel,
        ],
        component: MyResource,
        empowerApi: '/myResourceController/search',
      },
      {
        path: '/resources/myApplication',
        name: 'myApplication',
        breadcrumbName: '我的申请',
        model: MyApplicationModel,
        component: MyApplication,
        empowerApi: '/myResourceController/getMyApprove',
      },
      {
        path: '/resources/unapproved',
        name: 'unapproved',
        breadcrumbName: '待审批',
        model: UnapprovedModel,
        component: Unapproved,
        empowerApi: '/resources/unapproved',
      },
      {
        path: '/resources/approved',
        name: 'approved',
        breadcrumbName: '已审批',
        model: ApprovedModel,
        component: Approved,
        empowerApi: '/resources/approved',
      },
    ],
  },
];
