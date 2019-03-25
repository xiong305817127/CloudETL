/**
 * api前置地址，方便统一调整
 * 从package.json文件_env_节点读取配置
 */
export const API_BASE = '[package._env_.api_base]';
// 安全子系统:/security
export const API_BASE_SECURITY = '/security';
// 数据资源目录子系统
export const API_BASE_RESOURCE = `${API_BASE}metacube`;
// 下载文件
export const API_BASE_DOWNLOAD_FILE = `${API_BASE}metacube`;
// 元数据子系统
export const API_BASE_METADATA = `${API_BASE}metacube`;

// 新metadata和数据共享接口
export const API_BASE_METADATATOSWAP = `${API_BASE}/metadata/metadataToSwap`;
// [新增！！] 
// 新的元数据子系统
export const API_BASE_METADATA_NEW = `${API_BASE}metadata`;
// 服务开放子系统
export const API_BASE_SERVICE = `${API_BASE}servicebase`;
// 数据分析子系统
export const API_BASE_ANALYSIS = `${API_BASE}datalab`;
// 数据采集子系统
export const API_BASE_GATHER = `${API_BASE}cloudetl`;
// 监控管理子系统
export const API_BASE_MONITOR = `/uniom/api/v1`;
// 运维管理子系统
export const API_BASE_OPERATION = '/uniom/api/v1';
// 数据地图
export const API_BASE_GRAPH = `${API_BASE}graph`;

// 多维分析
export const API_BASE_OLAP = `${API_BASE}olap`;

//沧州项目数据资源
export const API_BASE_CATALOG = `${API_BASE}catalog`;

//数据分析bi项目
export const API_BASE_BI = `${API_BASE}saiku`;

//es索引搜索
export const API_BASE_ES = `${API_BASE}es`;

//神算子记录统计
export const API_BASE_DMP =  `${API_BASE}dmp`;


/**
 * 质量分析
 * @author pwj 2018/09/27
 */
export const API_BASE_QUALITY = `${API_BASE}quality`;

// 缺省分页大小
export const DEFAULT_PAGE_SIZE = 10;

// 登录状态号
export const LOGIN_SUCCESS = 'LOGIN_SUCCESS';
export const LOGIN_FAILED = 'LOGIN_FAILED';
export const LOGIN_SUSPEND = 'LOGIN_SUSPEND';

// root账户用户名，仅用于核对当前用户是否root用户
export const ROOT_USER_NAME = 'root';

// 缺省未授权按钮/菜单禁用方式(disable/hide)
export const DEFAULT_EMPOWER_DISABLE_TYPE = 'disable';

// 缺省异常提示信息
export const DEFAULT_EXCEPTION_MESSAGE = '请求异常，请稍后再试！';

// 缺省再次重试提交时间间隔(loading超时时间)
export const DEFAULT_SUBMIT_DURATION = 10e3;  // 10秒

// ETL是否独立，缺省为不独立
export const STANDALONE_ETL = false;

// 站点主题（`SITE_THEME`取自/public/config.js文件）
export const SITE_CUSTOM_THEME = typeof SITE_THEME !== 'undefined' ? SITE_THEME : 'default';

export const ACQUISITION_DB_TYPE_LIST = typeof ACQUISITION_DB_TYPE !== 'undefined' ? ACQUISITION_DB_TYPE : [];
