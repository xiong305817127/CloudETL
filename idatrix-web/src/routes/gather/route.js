
//数据
const cloudetlCommon = resolve => require(['./models/rbacData/cloudetlCommon'], resolve);

// 数据集成首页
const App = resolve => require(['./index'], resolve);
const appheader = resolve => require(['./models/appheader'], resolve);

/*任务中心*/
const TaskCenter = resolve => require(['./components/TaskCenter'], resolve);
const JobsCenter = resolve => require(['./components/taskCenter/JobsCenter'], resolve);
const TransCenter = resolve => require(['./components/taskCenter/TransCenter'], resolve);

/*任务中心  model*/
const taskcontent = resolve => require(['./models/taskcenter/taskcontent'], resolve);
const taskdetails = resolve => require(['./models/taskcenter/taskdetails'], resolve);
const controljobplatform = resolve => require(['./models/taskcenter/controljobplatform'], resolve);
const controltransplatform = resolve => require(['./models/taskcenter/controltransplatform'], resolve);

/*设计平台*/
const DesignPlatform = resolve => require(['./components/DesignPlatform'], resolve);
const designplatform = resolve => require(['./models/designplatform/designplatform'], resolve);
const newtrans = resolve => require(['./models/designplatform/newtrans'], resolve);
const foldertree = resolve => require(['./models/designplatform/foldertree'], resolve);
const domconfig = resolve => require(['./models/domconfig'], resolve);

const uploadfile = resolve => require(['./models/designplatform/uploadfile'], resolve);

const worktools = resolve => require(['./models/designplatform/worktools'], resolve);

/*job平台*/
const jobheader = resolve => require(['./models/designplatform/jobplatform/jobheader'], resolve);
const jobspace = resolve => require(['./models/designplatform/jobplatform/jobspace'], resolve);
const runjob = resolve => require(['./models/designplatform/jobplatform/runjob'], resolve);
const jobdebug = resolve => require(['./models/designplatform/jobplatform/jobdebug'], resolve);

/*转换平台*/
const transheader = resolve => require(['./models/designplatform/transplatform/transheader'], resolve);
const transspace = resolve => require(['./models/designplatform/transplatform/transspace'], resolve);
const runtrans = resolve => require(['./models/designplatform/transplatform/runtrans'], resolve);
const transdebug = resolve => require(['./models/designplatform/transplatform/transdebug'], resolve);
const rundebugger = resolve => require(['./models/designplatform/transplatform/rundebugger'], resolve);

const filemodel = resolve => require(['./models/designplatform/filemodel'], resolve);

//文件树组件
const treeview = resolve => require(['./models/designplatform/treeview'], resolve);
const ResourceList = resolve => require(['./components/ResourceList'], resolve);


const DataSystem = resolve => require(['./components/resourceList/DataSystem/DataSystem'], resolve);
const Server = resolve => require(['./components/resourceList/Server/Server'], resolve);
const Cluster = resolve => require(['./components/resourceList/Cluster/Cluster'], resolve);
const HadoopCluster = resolve => require(['./components/resourceList/HadoopCluster/HadoopCluster'], resolve);
const SparkEngine = resolve => require(['./components/resourceList/SparkEngine/SparkEngine'], resolve);
const ExecutionEngine = resolve => require(['./components/resourceList/ExecutionEngine/ExecutionEngine'], resolve);
const FileSystem = resolve => require(['./components/resourceList/FileSystem/FileSystem'], resolve);

// 授权管理
const EtlAuth = resolve=> require(["./components/resourceList/Auth"], resolve);

const resourcecontent = resolve => require(['./models/resourcelist/resourcecontent'], resolve);


const items = resolve => require(['./models/items'], resolve);
const dbtable = resolve => require(['./models/dbtable'], resolve);
const tip = resolve => require(['./models/tip'], resolve);
const infolog = resolve => require(['./models/infolog'], resolve);
const infostep = resolve => require(['./models/infostep'], resolve);
const debugdetail = resolve => require(['./models/debugdetail'], resolve);

const excelinputmodel = resolve => require(['./models/domitems/excelinputmodel'], resolve);
const hadoopoutputmodel = resolve => require(['./models/domitems/hadoopoutputmodel'], resolve);


//质量分析菜单  独立
//@edit by pwj 2018/09/27
const QualityAnalysis = resolve => require(['./qualityAnalysis/index'], resolve);
const qualityAnalysisModel = resolve => require(['./qualityAnalysis/model'], resolve);
//运行转换model初始化

const runAnysisModel = resolve => require(['./qualityAnalysis/components/Modals/RunAnalysis/model'], resolve);
const newAnalysisModel = resolve => require(['./qualityAnalysis/components/Modals/NewAnalysis/model'], resolve);
const domItemsModel = resolve => require(['./qualityAnalysis/components/Modals/DomItems/model'], resolve);
const analysisConfigModel = resolve => require(['./qualityAnalysis/components/Modals/AnalysisConfig/model'], resolve);
const analysisLogModel = resolve => require(['./qualityAnalysis/components/Modals/RunLog/model'], resolve);
const analysisDbtableModel = resolve => require(['./qualityAnalysis/components/Modals/DbTable/model'], resolve);


//分析任务界面
const DesignSpace = resolve => require(['./qualityAnalysis/designSpace/'], resolve);
const DesignSpaceModel = resolve => require(['./qualityAnalysis/designSpace/model'], resolve);
const analysisInfoModel = resolve => require(['./qualityAnalysis/designSpace/components/workfooter/model'], resolve);

// 分析报告
const QualityAnalysisReport = resolve => require(["./qualityAnalysis/report"], resolve);
const QualityAnalysisReportChart = resolve => require(["./qualityAnalysis/report/reportChart"], resolve);
const QualityAnalysisReportModel = resolve => require(["./qualityAnalysis/report/reportChart/model"], resolve);

//数据字典  独立
const DataDictionary = resolve => require(['./dataDictionary/index'], resolve);
const dataDictionModel = resolve => require(['./dataDictionary/model'], resolve);
//数据字典编辑
const DataDictionaryEdit = resolve => require(['./dataDictionary/editList/edit'], resolve);
const DataDictionaryEditModel = resolve => require(['./dataDictionary/editList/model'], resolve);

export default [
  {
    path: '/gather',
    name: 'gather',

    component: App,
    models: [
      cloudetlCommon, items, transdebug, jobdebug, jobspace, transspace, domconfig,
      newtrans, runtrans, dbtable, tip, infolog, uploadfile, treeview, rundebugger,
      infostep, debugdetail, excelinputmodel, runjob, taskdetails, foldertree, worktools, controljobplatform, controltransplatform,
      hadoopoutputmodel, resourcecontent, appheader, designplatform, jobheader, transheader, filemodel
    ],
    routes: [
      {
        path: '/gather/taskcenter',
        name: 'taskcenter',
        component: TaskCenter,
        model: taskcontent,

        routes: [
          {
            path: '/gather/taskcenter/transcenter/all',
            name: 'transcenter',
            component: TransCenter,
            breadcrumbName: "全部转换列表",
            empowerApi: '/cloud/getTransList/all.do'
          },
          {
            path: '/gather/taskcenter/jobscenter/all',
            name: 'jobscenter',
            component: JobsCenter,
            breadcrumbName: "全部调度任务",
            empowerApi: '/cloud/getJobList/all.do'
          },
          {
            path: '/gather/taskcenter/transcenter',
            name: 'transcenter',
            component: TransCenter,
            breadcrumbName: "我的转换任务",
            empowerApi: '/cloud/getTransList.do'
          },
          {
            path: '/gather/taskcenter/jobscenter',
            name: 'jobscenter',
            component: JobsCenter,
            breadcrumbName: "我的调度任务",
            empowerApi: '/cloud/getJobList.do'
          }
        ]
      },
      {
        breadcrumbName: "设计平台",
        path: '/gather/designplatform',
        name: 'designplatform',
        component: DesignPlatform
      },
      {
        path: '/gather/resourcelist',
        name: 'resourcelist',
        component: ResourceList,
        routes: [
          {
            path: '/gather/resourcelist/DataSystem',
            name: 'datasystem',
            component: DataSystem,
            breadcrumbName: "数据系统",
            empowerApi: '/db/getDbList.do'
          },
          {
            path: '/gather/resourcelist/Server',
            name: 'designplaform',
            component: Server,
            breadcrumbName: "服务器",
            empowerApi: '/cloud/getServerList.do'
          },
          {
            path: '/gather/resourcelist/Cluster',
            name: 'cluster',
            component: Cluster,
            breadcrumbName: "服务器集群",
            empowerApi: '/cloud/getClusterList.do'

          },
          {
            path: '/gather/resourcelist/HadoopCluster',
            name: 'hadoopcluster',
            breadcrumbName: "Hadoop集群",
            component: HadoopCluster,
            empowerApi: '/cloud/getHadoopList.do'
          },
          {
            path: '/gather/resourcelist/SparkEngine',
            name: 'sparkengine',
            component: SparkEngine,
            breadcrumbName: "Spark引擎",
            empowerApi: '/cloud/getSparkEngineList.do'
          },
          {
            path: '/gather/resourcelist/ExecutionEngine',
            name: 'executionengine',
            component: ExecutionEngine,
            breadcrumbName: "执行引擎",
            empowerApi: '/cloud/getDefaultEngineList.do'
          },
          {
            path: '/gather/resourcelist/FileSystem',
            name: 'filesystem',
            component: FileSystem,
            breadcrumbName: "文件管理",
            empowerApi: '/cloud/getFileList.do'
          },{
            path: '/gather/resourcelist/auth',
            name: "eltauth",
            component: EtlAuth,
            breadcrumbName:"etl授权管理",
            empowerApi: "/cloud/etlauth.do"
          }
        ]
      },
      {
        breadcrumbName: "质量分析",
        path: '/gather/qualityAnalysis/taskList',
				name: 'qualityAnalysis',
				empowerApi: '/gather/qualityAnalysis/taskList',
        component: QualityAnalysis,
        models: [qualityAnalysisModel, runAnysisModel, newAnalysisModel, DesignSpaceModel, analysisConfigModel,
          domItemsModel, analysisInfoModel, analysisLogModel,analysisDbtableModel],
        routes: [
          {
            path: '/gather/qualityAnalysis/designSpace',
            name: 'StorageTable',
            breadcrumbName: '分析任务',
						component: DesignSpace,
						empowerApi: '/gather/qualityAnalysis/designSpace',
          },
          {
            path: "/gather/qualityAnalysis/report",
            name: "analysisReports",
						breadcrumbName: "分析列表",
						empowerApi: '/gather/qualityAnalysis/report',
            component: QualityAnalysisReport,
            models: [QualityAnalysisReportModel],
            routes: [
              {
                path: "/gather/qualityAnalysis/report/reportChart",
                name: "analysisReportCharts",
                breadcrumbName: "分析报告",
                component: QualityAnalysisReportChart
                // models: [QualityAnalysisReportModel],
              }
            ]
          },
          {
            breadcrumbName: '数据字典',
            name: "dataDictionary",
						path: '/gather/dataDictionary',
						empowerApi: '/gather/dataDictionary',
            icon: 'designplatform',
            component: DataDictionary,
            models: [dataDictionModel],
            routes: [
              {
                path: '/gather/dataDictionary/edit/:params',
                name: 'DataDictionaryEdit',
                component: DataDictionaryEdit,
                breadcrumbName: "数据字典内容",
                models: [DataDictionaryEditModel],
              }
            ]
          }
        ]
      },
    ]
  }
];
