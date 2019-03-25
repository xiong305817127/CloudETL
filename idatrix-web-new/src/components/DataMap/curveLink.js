/**
 * 联接曲线路径生成器
 * @param  {Object} source     源点坐标系
 * @param  {Object} target     终点坐标系
 * @param  {Number} dx         x轴偏移量
 * @param  {Number} dy         y轴偏移量
 * @param  {Number} startSpace 起始位留空
 * @param  {Number} endSpace   结束位留空
 * @param  {Number} factor     曲率系数，取值：0 < factor < 1
 * @return {String}            曲线路径
 */
export default (source, target, dx = 0, dy = 0, startSpace = 0, endSpace = 0, factor = 0.5) => {
  const { x: sx, y: sy } = source;
  const { x: tx, y: ty } = target;
  const way = Math.abs(sx - tx) - dx > Math.abs(sy - ty) - dy ? 'v' : 'h'; // v横向 h纵向
  let { x1, y1 } = { x1: sx, y1: sy }; // 起点坐标
  let { x2, y2 } = { x2: tx, y2: ty }; // 终点坐标
  let { cx1, cy1 } = { cx1: x1, cy1: y1 }; // 插值坐标1
  let { cx2, cy2 } = { cx2: x2, cy2: y2 }; // 插值坐标2
  if (way === 'v') { // 横向联接修正
    x1 = sx < tx ? sx + dx : sx - dx;
    x2 = sx < tx ? tx - dx : tx + dx;
    cx1 = x1 + ((x2 - x1) * factor);
    cx2 = x2 - ((x2 - x1) * factor);
    x1 = sx < tx ? x1 + startSpace : x1 - startSpace;
    x2 = sx < tx ? x2 - endSpace : x2 + endSpace;
  } else { // 纵向联接修正
    y1 = sy < ty ? sy + dy : sy - dy;
    y2 = sy < ty ? ty - dy : ty + dy;
    cy1 = y1 + ((y2 - y1) * factor);
    cy2 = y2 - ((y2 - y1) * factor);
    y1 = sy < ty ? y1 + startSpace : y1 - startSpace;
    y2 = sy < ty ? y2 - endSpace : y2 + endSpace;
  }
  if (source.id === target.id) { // 自已联接自己
    x2 = x1;
    y2 = y1;
    cx1 = x1 - 100;
    cx2 = x2 + 100;
    cy1 = y1 - 100;
    cy2 = y2 - 100;
  }
  return `M${x1} ${y1} C${cx1} ${cy1} ${cx2} ${cy2} ${x2} ${y2}`;
};
