/**
 * 主题风格
 * default     默认主题
 * government  政府风格主题
 * finance     金融风格主题
 */
var SITE_THEME = "government";

// 集群名称
var CLUSTER_NAME = "GDBDCluster";

// 支持的数据采集类型
var ACQUISITION_DB_TYPE = ["MySQL", "Oracle"];

/**
 * 平台名称（可自定义系统名称、版权，无则取默认值）
 * lengin    灵机大数据
 * chongqing 重庆农村云大数据中心
 * cangzhou  沧州大数据
 * noLogo		 无logo的状态
 */
var SITE_NAME = "chongqing";

//自定义参数
// SITE_NAME:"灵机大数据"
// COPY_RIGHT: "Copyright ©灵机大数据技术有限公司版权所有"
// premit:["quality","bi"]
// 1、quality(质量权限) 2、bi(BI权限) 3、shen(跳神算子)
// 4、dataMap(home页面大屏) 5、bbsPage(bbs论坛) BBS_URL 6、dataView(可视化) DATAVIEW_URL
// 7、portalPage(门户网站) PORTAL_URL 8、kibanaPage KIBANA_URL 9、logAnalysis(日志分析) LOG_URL
// 8、DIVINE_URL 神算子链接
var CUSTOM_PARAMS = {
  SITE_NAME: "灵机大数据111111",
  premit: ["dataMap", "bbsPage", "dataView", "bi", "quality", "portalPage", "kibanaPage", "logAnalysis"],
  BBS_URL: "http://10.0.0.85:60130/",
	DATAVIEW_URL: "http://113.207.110.159:8099/dashboard/list/",
	PORTAL_URL:"http://113.207.110.164:60170",
	KIBANA_URL:"http://113.207.110.164:5601",
	LOG_URL:"http://113.207.110.164:5601/app/kibana#/dashboard/c72e9b70-f6a4-11e8-a1e0-536204da1605?embed=true&_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now-2y,mode:quick,to:now))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((gridData:(h:3,i:'1',w:6,x:0,y:0),id:'833179c0-f6a3-11e8-a1e0-536204da1605',panelIndex:'1',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'2',w:6,x:6,y:0),id:a3228ca0-f6a4-11e8-a1e0-536204da1605,panelIndex:'2',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'3',w:6,x:0,y:3),id:b2d5ee50-f3cd-11e8-8eb2-7ddd94344fc2,panelIndex:'3',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'4',w:6,x:6,y:3),id:'56df5a00-f3d2-11e8-8eb2-7ddd94344fc2',panelIndex:'4',type:visualization,version:'6.2.4'),(embeddableConfig:(vis:(colors:('200':%239AC48A))),gridData:(h:3,i:'5',w:6,x:0,y:6),id:'4aae0c70-f6a4-11e8-a1e0-536204da1605',panelIndex:'5',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'6',w:6,x:6,y:6),id:'43283540-f6a7-11e8-a1e0-536204da1605',panelIndex:'6',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'7',w:6,x:0,y:9),id:'6b2faa20-3f20-11e9-a382-77c1ef6d3198',panelIndex:'7',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'8',w:6,x:6,y:9),id:'2bc47e90-3f1d-11e9-a382-77c1ef6d3198',panelIndex:'8',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'9',w:6,x:0,y:12),id:'7fb62900-3f0d-11e9-a382-77c1ef6d3198',panelIndex:'9',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'10',w:6,x:6,y:12),id:a6c13840-4184-11e9-a382-77c1ef6d3198,panelIndex:'10',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'11',w:6,x:0,y:15),id:'56df5a00-f3d2-11e8-8eb2-7ddd94344fc2',panelIndex:'11',type:visualization,version:'6.2.4')),query:(language:lucene,query:''),timeRestore:!f,title:%E5%AE%89%E5%85%A8%E6%97%A5%E5%BF%97%E5%88%86%E6%9E%90,viewMode:view)",
	DIVINE_URL:""
};
// var _Error_Report_URL = "http://localhost:8000/error_reporter";

/**
 * 配置iframe地址
 */
var IFRAME_URL = "http://10.0.0.84/bi";
var SHOW_EXTRA = true;
