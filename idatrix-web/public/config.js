/**
 * 主题风格
 * default     默认主题
 * government  政府风格主题
 * finance     金融风格主题
 */
var SITE_THEME = 'government';

// 集群名称
var CLUSTER_NAME = 'GDBDCluster';

// 支持的数据采集类型
var ACQUISITION_DB_TYPE = ['MySQL','Oracle'];

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
// premit:["quality","bi"]    1、quality(质量权限) 2、bi(BI权限) 3、shen(跳神算子) 4、dataMap(home页面大屏)
var CUSTOM_PARAMS = {}
// var _Error_Report_URL = "http://localhost:8000/error_reporter";

/**
 * 配置iframe地址
 */
var IFRAME_URL = "http://10.0.0.84/bi";
var SHOW_EXTRA = true;
