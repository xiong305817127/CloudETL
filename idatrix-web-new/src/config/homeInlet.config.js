/**
 * 首页入口相关配置
 * @props [string] paths  该子系统备选路由，默认选择第一条。当第一条无授权时自动依次选择。
 * @type {Object}
 */

import sysIds from './systemIdsMap.config';
import baseInfo from 'config/baseInfo.config';

export default {
  [sysIds['routes/gather']]: {
    index: 0,
    title: '数据采集&集成',
    icon: require('../assets/images/数据采集.png'),
    icon2: require('../assets/images/gov/数据采集&集成.png'),
    desc: '数据汇聚（ETL）、清洗、融合',
    img: require('../assets/images/2.jpg'),
    paths: [
      '#/gather/taskcenter/transcenter',
			'#/gather/taskcenter/jobscenter',
			'#/gather/designplatform',
			'#/gather/qualityAnalysis/taskList',
			'#/gather/qualityAnalysis/designSpace',
			'#/gather/dataDictionary',
			'#/gather/resourcelist/DataSystem',
			'#/gather/resourcelist/Server',
			'#/gather/resourcelist/Cluster',
			'#/gather/resourcelist/HadoopCluster',
			'#/gather/resourcelist/SparkEngine',
			'#/gather/resourcelist/ExecutionEngine',
			'#/gather/resourcelist/FileSystem'
    ],
  },
  [sysIds['routes/resources']]: {
    index: 1,
    title: ' 数据共享交换平台',
    icon: require('../assets/images/数据资源展现.png'),
    icon2: require('../assets/images/gov/数据资源目录.png'),
    desc: '数据字典与黄页，分类展现与检索',
    img: require('../assets/images/3.png'),
    paths: [
      '#/resources/sourceview',
			'#/resources/exchange/serverData/index',
			'#/resources/exchange/report/index',
			'#/resources/exchange/report/exchangeData',
			'#/resources/exchange/front',
			'#/resources/database/classify',
			'#/resources/database/systemparm',
			'#/resources/database/maintenance',
			'#/resources/database/subscription',
			'#/resources/database/service/sourceservice',
			'#/resources/database/service/shareservice',
			'#/resources/management/mysource',
			'#/resources/management/reporting',
			'#/resources/register/approval',
			'#/resources/register/approved',
			'#/resources/release/approval',
			'#/resources/release/approved',
			'#/resources/subscription/mysubscriptions',
			'#/resources/subscription/Relationship',
			'#/resources/subscription/approval',
			'#/resources/subscription/approved'
    ],
  },
  [sysIds['routes/metadata']]: {
    index: 2,
    title: '元数据管理',
    icon: require('../assets/images/元数据管理.png'),
    icon2: require('../assets/images/gov/元数据管理.png'),
    desc: '系统与数据全品类元信息定义、展现、管理',
    img: require('../assets/images/1.jpg'),
    paths: [
      /*'#/DataAcquisition',*/
      '#/FrontendResManage',
      '#/DataSystemSegistration',
      '#/MetadataDefine',
      '#/DataRelationshipManagement',
			'#/DataStandardView',
			'#/resources/directory',
			'#/resources/myResource',
			'#/resources/myApplication',
			'#/resources/unapproved',
			'#/resources/approved'
    ],
  },
  [sysIds['routes/security']]: {
    index: 3,
    title: '安全管理',
    icon: require('../assets/images/安全管理.png'),
    icon2: require('../assets/images/gov/安全管理.png'),
    desc: '全方位&多层次系统与数据安全管控中心',
    paths: [
      '#/OrganizationManagementTable',
      '#/RoleManagementTable',
			'#/UserManagementTable',
			'#/LogoManagement'
    ],
  },
  [sysIds['routes/serviceOpen']]: {
    index: 4,
    title: '服务开放&治理',
    icon: require('../assets/images/服务开放.png'),
    icon2: require('../assets/images/gov/服务开放.png'),
    desc: '数据服务与应用服务管理中心',
    paths: [
      '#/MyAppTable',
      '#/service/ServiceTableVisitApi',
    ]
  },
  [sysIds['routes/analysis']]: {
    index: 5,
    title: '数据分析&探索',
    icon: require('../assets/images/数据分析.png'),
    icon2: require('../assets/images/gov/数据分析.png'),
    desc: '提供IDE界面进行数据查询与分析；提供数据分析与任务调度',
    paths: [
      '#/DataQueryTable',
			'#/ListManagementTable',
			'#/analysis/FullTextSearch',
			'#/analysis/TaskManage',
			'#/analysis/DataModel',
			'#/analysis/ReportForms/New',
			'#/analysis/ReportForms/Open',
    ]
  },

  [sysIds['routes/operation']]: {
    index: 7,
    title: baseInfo.premit && baseInfo.premit.includes("shen") ? "大数据应用开发环境" : '运维管理',
    icon: require('../assets/images/运维管理.png'),
    icon2: require('../assets/images/gov/运维管理.png'),
    desc: baseInfo.premit && baseInfo.premit.includes("shen") ?"全流程可视化建模应用开发环境":'平台系统运维中心',
    paths: [
     // 'https://senses.jusfoun.com/home/recommend/guide',
     // '#/',
      '/uniom/#/main/dashboard/metrics',
    ]
  },
  [sysIds['routes/monitor']]: {
    index: 6,
    title: '运维监控',
    icon: require('../assets/images/监控管理.png'),
    icon2: require('../assets/images/gov/监控管理.png'),
    desc: '平台监控中心',
    paths: [
      '/uniom/#/main/alerts',
    ]
  },
};

