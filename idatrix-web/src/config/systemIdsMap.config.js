/**
 * 子系统id映射表，根据路由信息做映射
 * key值对应路由目录，不可修改
 * value值为相对应子系统context
 */
export default {
  'routes/analysis': 'datalab',  // 数据分析子系统
  'routes/gather': 'cloudetl',   // 数据采集子系统
  'routes/metadata': 'idatrix-metacube-web',  // 元数据子系统
  'routes/monitor': 'monitor',   // 监控子系统
  'routes/operation': 'ITIL', // 运维子系统
  'routes/security': 'security',   // 安全子系统
  'routes/serviceOpen': 'servicebase',   // 服务开放子系统
  'routes/resources': 'dataResDir',  // 数据资源目录子系统
};
