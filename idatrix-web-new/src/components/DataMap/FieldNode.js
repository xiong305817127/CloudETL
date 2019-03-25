/**
 * 生成表级别的节点
 */

// 导入icon
import icon1 from 'assets/images/datamap/db1.png';
import icon2 from 'assets/images/datamap/db2.png';
import icon3 from 'assets/images/datamap/db3.png';
import icon4 from 'assets/images/datamap/db4.png';
import icon5 from 'assets/images/datamap/db5.png';
import icon6 from 'assets/images/datamap/file.png';
import iconField from 'assets/images/datamap/icon-field.png';

import config from './config';
import Style from './Style.less';

const nodeWidth = config.nodeWidth.field;
const nodeHeight = config.nodeHeight.field;

// 获取数据库icon
const getDbIcon = (d) => {
  let ret = icon1;
  switch (d.typeName) {
    default: ret = icon1; break;
    case 'mysql': ret = icon1; break;
    case 'hive': ret = icon2; break;
    case 'hbase': ret = icon3; break;
    case 'front': ret = icon4; break;
    case 'out': ret = icon5; break;
    case 'file': ret = icon6; break;
  }
  return ret;
};

const getTableName = (d) => {
  const guid = (d.guid || '...').split('.');
  return d.table_name_cn || guid[3];
};

const getFieldName = (d) => {
  return d.name || (d.guid || '.').replace(/^.*\.([^.]+)$/, '$1');
};

/**
 * [description]
 * @param  {[type]} svg 由d3创建的dom元素
 * @param  {[type]} d   节点数据
 * @return {string}
    <svg>
      <rect className={Style.nodeWrap} width="105" height="75" rx="8" ry="8" />
      <rect className={Style.nodeTitleBg} y="55" width="105" height="20" rx="8" ry="8" />
      <text className={Style.nodeTitle} x="52" y="65">Title</text>
      <image href={icon1} x="36" y="8" />
      <rect className={Style.nodeNumBox} x="56" y="36" width="20" height="14" rx="2" ry="2" />
      <text className={Style.nodeNum} x="66" y="44">0</text>
    </svg>
 */
const createTableNode = (svg, d) => {
  svg.append('rect')
    .attr('width', nodeWidth)
    .attr('height', nodeHeight)
    .attr('x', 1)
    .attr('y', 1)
    .attr('rx', 8)
    .attr('ry', 8)
    .attr('class', Style.nodeWrap);
  svg.append('rect')
    .attr('width', nodeWidth)
    .attr('height', 20)
    .attr('x', 1)
    .attr('y', 1)
    .attr('rx', 8)
    .attr('ry', 8)
    .attr('class', Style.nodeTitleBg);
  svg.append('text')
    .attr('x', 20)
    .attr('y', 16)
    .attr('class', Style.nodeTitle2)
    .text(getTableName(d));
  svg.append('image')
    .attr('href', iconField)
    .attr('width', 14)
    .attr('height', 14)
    .attr('x', 5)
    .attr('y', 4);
  svg.append('text')
    .attr('x', 52)
    .attr('y', 35)
    .attr('class', Style.nodeDesc)
    .text(getFieldName(d));
  svg.append('rect')
    .attr('width', nodeWidth)
    .attr('height', 20)
    .attr('x', 1)
    .attr('y', nodeHeight - 19)
    .attr('rx', 8)
    .attr('ry', 8)
    .attr('class', Style.nodeTitleBg2);
  svg.append('image')
    .attr('href', getDbIcon)
    .attr('width', 16)
    .attr('height', 16)
    .attr('x', 4)
    .attr('y', nodeHeight - 17);
  svg.append('text')
    .attr('x', 52)
    .attr('y', nodeHeight - 8)
    .attr('class', Style.nodeDesc)
    .text(d.dbName || '数据库名称');
};

export default createTableNode;
