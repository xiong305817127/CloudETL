/**
 * 生成数据库级别的节点
 */

// 导入icon
import icon1 from 'assets/images/datamap/db1.png';
import icon2 from 'assets/images/datamap/db2.png';
import icon3 from 'assets/images/datamap/db3.png';
import icon4 from 'assets/images/datamap/db4.png';
import icon5 from 'assets/images/datamap/db5.png';
import icon6 from 'assets/images/datamap/file.png';

import config from './config';
import Style from './Style.less';

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

const nodeWidth = config.nodeWidth.db;
const nodeHeight = config.nodeHeight.db;

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
const createDBNode = (svg, d) => {
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
    .attr('y', 55)
    .attr('rx', 8)
    .attr('ry', 8)
    .attr('class', Style.nodeTitleBg);
  svg.append('text')
    .attr('x', 52)
    .attr('y', 65)
    .attr('class', Style.nodeTitle)
    .text(d.dbName);
  svg.append('image')
    .attr('href', getDbIcon)
    .attr('x', 36)
    .attr('y', 8);
};

export default createDBNode;

/*
  svg.append('rect')
    .attr('width', 20)
    .attr('height', 14)
    .attr('x', 56)
    .attr('y', 36)
    .attr('rx', 2)
    .attr('ry', 2)
    .attr('class', Style.nodeNumBox);
  svg.append('text')
    .attr('x', 66)
    .attr('y', 44)
    .attr('class', Style.nodeNum)
    .text(d.count || 0);

 */