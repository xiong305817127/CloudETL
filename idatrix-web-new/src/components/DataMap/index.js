/**
 * 基于D3.js的数据地图图表组件
 */
import React from 'react';
import PropTypes from 'prop-types';
import { Icon, Slider, Checkbox, Select } from 'antd';
import * as d3 from 'd3';
import { fullScreen, exitFullscreen } from 'utils/utils';

import createDBNode from './DBNode';
import createTableNode from './TableNode';
import createFieldNode from './FieldNode';
import curveLink from './curveLink';
import config from './config';
import Style from './Style.less';

// 导入icon
import typeIcon1 from 'assets/images/datamap/alg1.png';
import typeIcon2 from 'assets/images/datamap/alg2.png';
import typeIcon3 from 'assets/images/datamap/alg3.png';

const Option = Select.Option;

// 关系数量图标
const createLinkNum = (svg, d) => {
  svg.append('circle')
    .attr('r', 8)
    .attr('cx', 9)
    .attr('cy', 9)
    .attr('class', Style.linkNumWrap);
  svg.append('text')
    .attr('class', Style.linkNum)
    .attr('dx', 9)
    .attr('dy', 10)
    .text(d.count || 0);
};

// 关系类型图标
const createLinkTypeIcon = (svg, d) => {
  let icon = typeIcon3;
  switch (d.fieldType) {
    case '1': icon = typeIcon1; break;
    case '2': icon = typeIcon2; break;
    case '3': icon = typeIcon3; break;
  }
  svg.append('image')
    .attr('href', icon)
    .attr('width', 18)
    .attr('height', 18)
    .attr('x', 0)
    .attr('y', 0);
};

// 获取tooltips
const getTooltipsLayer = () => {
  const { offsetX, offsetY } = d3.event;
  const tooltips = d3.select('#tooltips');
  tooltips.attr('x', offsetX + 5).attr('y', offsetY + 5)
    .style('display', '').selectAll('*')
    .remove();
  return tooltips;
};

class AppView extends React.Component {

  static propTypes = {
    type: PropTypes.string, // 地图类型  db - 数据库、table - 表、field - 字段
    nodesData: PropTypes.array.isRequired, // 节点数据
    linksData: PropTypes.array.isRequired, // 关联关系数据
    width: PropTypes.oneOfType([PropTypes.string, PropTypes.number]), // 画布宽度
    height: PropTypes.oneOfType([PropTypes.string, PropTypes.number]), // 画布长度
    defaultSelection: PropTypes.object, // { type, nodeId, sourceId, targetId } 其中，type取值 'node'、'link'
    selection: PropTypes.object, // { type, nodeId, sourceId, targetId } 其中，type取值 'node'、'link'
    onClickNode: PropTypes.func, // 单击节点
    onDblClickNode: PropTypes.func, // 双击节点
    onClickLink: PropTypes.func, // 单击连线
    onDblClickLink: PropTypes.func, // 双击连线
    onOffside: PropTypes.func, // 拖动时超出边界
    onChangeBlood: PropTypes.func, // 血缘分析选择
    onChangeEffect: PropTypes.func, // 影响分析选择
    onChangeLevel: PropTypes.func, // 层级选择

    analysisView:PropTypes.bool //是否显示  血缘分析，影响分析，层级选择
  };

  state = {
    full: false,
    zoomValue: 1,
    nodesData: [],
    linksData: [],
    zoomX: 0,
    zoomY: 0,
    setDefaultSelected: false,
    bloodChecked: false,
    effectChecked: false,
  }

  componentWillMount() {
    const { nodesData, linksData } = this.props;
    this.setState({ nodesData, linksData });
  }

  componentDidMount() {
    this.initGraphView();
    this.wrapper.addEventListener('fullscreenchange', this.exitFullscreenByESC);
    this.wrapper.addEventListener('webkitfullscreenchange', this.exitFullscreenByESC);
    this.wrapper.addEventListener('mozfullscreenchange', this.exitFullscreenByESC);
    this.wrapper.addEventListener('MSFullscreenChange', this.exitFullscreenByESC);
  }

  componentWillReceiveProps(nextProps) {
    const { defaultSelection, selection } = nextProps;
    const { setDefaultSelected } = this.state;
    this.updateForceData(nextProps);
    if (!setDefaultSelected && defaultSelection &&
      (defaultSelection.nodeId || defaultSelection.sourceId)
    ) {
      this.customSelect(defaultSelection);
      this.setState({ setDefaultSelected: true });
    } else if (selection) {
      this.customSelect(selection);
    }
  }

  initGraphView = () => {
    const width = this.wrapper.clientWidth;
    const height = this.wrapper.clientHeight;
    if (!this.graphView) {
      this.zoom = d3.zoom()
        .scaleExtent([config.zoomMin, config.zoomMax])
        .on('zoom', this.zoomed)
        .on('end', this.handleZoomEnd);

      this.graphView = d3.select(this.graphSvg)
        .call(this.zoom)
        .attr('width', '100%')
        .attr('height', '100%');

      // 定义力导图
      this.force = d3.forceSimulation()
        .force('charge', d3.forceManyBody().strength(-200))
        .force('link', d3.forceLink().id(d => String(d.id)).distance(200))
        .force('center', d3.forceCenter(width / 2, height / 2));

      // 节点
      this.nodeGroup = this.graphView.append('g').attr('class', Style.nodeGroup);

      // 关系
      this.linkGroup = this.graphView.append('g');

      // 关系数量
      this.linkNumGroup = this.graphView.append('g').attr('class', Style.linkNumGroup);

      // 工具提示
      this.tooltipsGroup = this.graphView.append('svg').attr('id', 'tooltips').attr('class', Style.tooltipsGroup);

      this.startForce();
    }
  }

  // 更新力导图数据
  updateForceData = (nextProps) => {
    const { nodesData, linksData } = this.state;
    const newNodesData = [];
    const newLinksData = [];
    let changed = false;
    nextProps.nodesData.forEach((node) => {
      const find = nodesData.find(it => it.id === node.id);
      if (!nodesData.some(it => it.id === node.id)) {
        newNodesData.push(node);
        changed = true;
      } else {
        newNodesData.push(find);
      }
    });
    nextProps.linksData.forEach((link) => {
      const sid = link.source.id || link.source;
      const tid = link.target.id || link.target;
      const find = linksData.find(it => it.source.id == sid && it.target.id == tid);
      if (!linksData.some(it => it.source.id == sid && it.target.id == tid)) {
        newLinksData.push(link);
        changed = true;
      } else {
        newLinksData.push(find);
      }
    });
    if (changed) {
      this.setState({ nodesData: newNodesData, linksData: newLinksData }, () => {
        this.startForce();
      });
    }
  }

  // 按ESC键退出全屏处理
  exitFullscreenByESC = () => {
    this.setState({ full: !this.state.full });
  }

  // 全屏操作
  handleFullscreen = () => {
    if (!this.state.full) {
      fullScreen(this.wrapper);
    } else {
      exitFullscreen();
    }
  }

  // 单击节点
  handleClickNode = (d) => {
    this.selectNode(d);
    if (this.props.onClickNode) {
      this.props.onClickNode(d);
    }
  }

  // 双击节点
  handleDblClickNode = (d) => {
    if (this.props.onDblClickNode) {
      this.props.onDblClickNode(d);
    }
  }

  // 单击链接
  handleClickLink = (d) => {
    this.selectLink(d);
    if (this.props.onClickLink) {
      this.props.onClickLink(d);
    }
  }

  // 双击链接
  handleDblClickLink = (d) => {
    if (this.props.onDblClickLink) {
      this.props.onDblClickLink(d);
    }
  }

  // 拖放停止
  handleZoomEnd = () => {
    const { k, x, y } = d3.event.transform;
    if (k === this.state.zoomValue) {
      this.handleOffside(x, y);
    }
    this.setState({ zoomValue: k });
  }

  // 越线控制
  handleOffside = (x, y) => {
    const { zoomX, zoomY } = this.state;
    const nodes = [];
    let dir = 'none';
    let sideNode = null; // 边界node
    this.nodeGroup.selectAll('svg').each(d => nodes.push(d));
    if (Math.abs(x - zoomX) > Math.abs(y - zoomY)) { // 横向拖动
      const xe = d3.extent(nodes, d => d.x);
      if (x > zoomX) { // 向右拖动
        if (x + xe[0] > 0) {
          sideNode = nodes.find(d => d.x === xe[0]);
          dir = 'right';
        }
      } else { // 向右拖动
        if (x + (xe[1] - this.wrapper.clientWidth) < 0) {
          sideNode = nodes.find(d => d.x === xe[1]);
          dir = 'left';
        }
      }
    } else { // 纵向拖动
      const ye = d3.extent(nodes, d => d.y);
      if (y > zoomY) { // 向下拖动
        if (y + ye[0] > 0) {
          sideNode = nodes.find(d => d.y === ye[0]);
          dir = 'bottom';
        }
      } else { // 向上拖动
        if (y + (ye[1] - this.wrapper.clientHeight) < 0) {
          sideNode = nodes.find(d => d.y === ye[1]);
          dir = 'top';
        }
      }
    }
    this.setState({
      zoomX: x,
      zoomY: y,
    });
    if (sideNode && this.props.onOffside) {
      this.props.onOffside(dir, sideNode);
    }
  }

  // 点击血缘分析
  handleChangeBlood = (e) => {
    const { checked } = e.target;
    if (checked) {
      this.setState({
        bloodChecked: true,
        effectChecked: false,
      });
    } else {
      this.setState({
        bloodChecked: false,
      });
    }
    if (typeof this.props.onChangeBlood === 'function') {
      this.props.onChangeBlood(checked);
    }
  }

  // 点击影响分析
  handleChangeEffect = (e) => {
    const { checked } = e.target;
    if (checked) {
      this.setState({
        effectChecked: true,
        bloodChecked: false,
      });
    } else {
      this.setState({
        effectChecked: false,
      });
    }
    if (typeof this.props.onChangeEffect === 'function') {
      this.props.onChangeEffect(checked);
    }
  }

  // 启动（更新）力导图
  startForce = () => {


    const { type: type } = this.props;
    const { nodesData, linksData } = this.state;
    const linkName = type ==="table"?"字段":"表";

    this.linkGroup
      .attr('class', Style.linkWrap)
      .selectAll('path').remove();
    this.linkNumGroup
      .selectAll('svg').remove();
    this.nodeGroup
      .selectAll('svg').remove();

    const link = this.linkGroup
      .attr('class', Style.linkWrap)
      .selectAll('path')
      .data(linksData)
      .enter()
      .append('path')
      .attr('marker-start', 'url(#dot)')
      .attr('marker-end', 'url(#arrow)');

    link.exit().remove();

    const linkNum = this.linkNumGroup
      .selectAll('svg')
      .data(linksData)
      .enter()
      .append('svg')
      .on('mouseover', function(d) {
        if(type === "table" || type === "db"){
          const tooltips = getTooltipsLayer();
          tooltips.append('rect').attr('class', Style.tooltipsBg);
          tooltips.append('text').attr('y', 10).attr('x', 50).attr('class', Style.tooltipsMsg)
            .text(`${d.count || 0}个关联${linkName}`);
          d3.select(this).classed(Style.hover, true);
        }
      })
      .on('mouseout', function(d) {
        d3.select('#tooltips').style('display', 'none');
        d3.select(this).classed(Style.hover, false);
      })
      .on('dblclick', this.handleDblClickLink)
      .on('click', this.handleClickLink)
      .each(function(d) {
        if (type === 'field') {
          createLinkTypeIcon(d3.select(this), d);
        } else {
          createLinkNum(d3.select(this), d);
        }
      });

    linkNum.exit().remove();

    const node = this.nodeGroup
      .selectAll('svg')
      .data(nodesData)
      .enter()
      .append('svg')
      .on('mouseover', function(d) {
        d3.select(this).classed(Style.hover, true);
      })
      .on('mouseout', function(d) {
        d3.select(this).classed(Style.hover, false);
      })
      .on('dblclick', this.handleDblClickNode)
      .on('click', this.handleClickNode)
      .each(function(d) {
        if (type === 'db') {
          createDBNode(d3.select(this), d);
        } else if (type === 'table') {
          createTableNode(d3.select(this), d);
        } else if (type === 'field') {
          createFieldNode(d3.select(this), d);
        }
      })
      .call(d3.drag()
        .on('start', this.dragstart)
        .on('drag', this.dragging)
        .on('end', this.dragend));

    node.exit().remove();

    // 固定原始节点位置
    nodesData.forEach((d) => {
      if (typeof d.x !== 'undefined') {
        d.fx = d.x;
        d.fy = d.y;
      }
    });

    this.force.nodes(nodesData)
      .on('tick', this.ticked)
      .force('link')
      .links(linksData);

    this.force.alphaTarget(0.3).restart();

    clearTimeout(this.unfixTimer);
    this.unfixTimer = setTimeout(() => {
      this.force.alphaTarget(0);
      // 解除位置固定
      nodesData.forEach((d) => {
        d.fx = null;
        d.fy = null;
      });
    }, 1500);
  }

  ticked = () => {
    const { type } = this.props;
    const { nodeWidth, nodeHeight } = config;

    this.nodeGroup.selectAll('svg')
      .attr('x', function(d) {
        return d.x - (this.getBBox().width / 2);
      })
      .attr('y', function(d) {
        return d.y - (this.getBBox().height / 2);
      });

    this.linkGroup.selectAll('path')
      .attr('d', d => curveLink(d.source, d.target, nodeWidth[type] / 2, nodeHeight[type] / 2, 0, 5));

    this.linkNumGroup.selectAll('svg')
      .attr('x', d => d.source.x + (d.target.x - d.source.x) / 2 - 8)
      .attr('y', d => {
        let y = d.source.y + (d.target.y - d.source.y) / 2 - 8;
        if (d.source.id === d.target.id) { // 自己联接自己
          y -= 112;
        }
        return y;
      });
  }

  dragstart = (d) => {
    if (!d3.event.active) {
      this.force.alphaTarget(0.3).restart();
    }
    d.fx = d.x;
    d.fy = d.y;
  }

  dragging = (d) => {
    d.fx = d3.event.x;
    d.fy = d3.event.y;
  }

  dragend = (d) => {
    if (!d3.event.active) {
      this.force.alphaTarget(0);
    }
    d.fx = null;
    d.fy = null;
  }

  // 定义缩放
  zoomed = () => {
    this.linkGroup.attr('transform', d3.event.transform);
    this.linkNumGroup.attr('transform', d3.event.transform);
    this.nodeGroup.attr('transform', d3.event.transform);
  }

  // 调整放大倍数
  adjustZoom = (value) => {
    const { zoomMin, zoomMax } = config;
    const { zoomValue } = this.state;
    const newValue = zoomValue + value;
    const fixValue = newValue < zoomMin ? zoomMin : newValue > zoomMax ? zoomMax : newValue;
    this.setState({
      zoomValue: fixValue,
    });
    this.zoom.scaleTo(this.graphView, fixValue);
  }


  // 用户自定义选择
  customSelect = (selection) => {
    const { type, nodeId, sourceId, targetId } = (selection || {});
    setTimeout(() => {
      if (type === 'node') {
        this.selectNode({ id: nodeId });
      } else if (sourceId && targetId) {
        this.selectLink({
          source: { id: sourceId },
          target: { id: targetId },
        });
      }
    }, 300);
  }

  // 选择节点
  selectNode = (d) => {
    this.linkNumGroup.selectAll('svg').classed(Style.active, false);
    this.nodeGroup.selectAll('svg').each(function(node) {
      if (d.id === node.id) {
        d3.select(this).classed(Style.active, true);
      } else {
        d3.select(this).classed(Style.active, false);
      }
    });
  }

  // 选择链接
  selectLink = (d) => {
    this.nodeGroup.selectAll('svg').classed(Style.active, false);
    this.linkNumGroup.selectAll('svg').each(function(link) {
      const sid1 = d.source.id;
      const sid2 = link.source.id;
      const tid1 = d.target.id;
      const tid2 = link.target.id;
      if ((sid1 === sid2 && tid1 === tid2) || (sid1 === tid2 && tid1 === sid2)) {
        d3.select(this).classed(Style.active, true);
      } else {
        d3.select(this).classed(Style.active, false);
      }
    });
  }

  render() {
    const { width, height, style, type,analysisView } = this.props;
    const { zoomValue, full, bloodChecked, effectChecked } = this.state;
    return (<div
      className={Style.wrapper}
      ref={ref => this.wrapper = ref}
      style={{ width: full ? '100%' : width, height: full ? '100%' : height, ...style }}
    >
      <section className={Style.legend}>
        {type === 'table' && !analysisView ? (<Checkbox onChange={this.handleChangeBlood} checked={bloodChecked}>血缘分析</Checkbox>) : null}
        {type === 'table' && !analysisView ? (<Checkbox onChange={this.handleChangeEffect} checked={effectChecked}>影响分析</Checkbox>) : null}
        {(type === 'table' || type === 'field') && !analysisView ? (<span>
          展开层数：
          <Select defaultValue="2" onChange={this.props.onChangeLevel}>
            <Option key="2">二层</Option>
            <Option key="3">三层</Option>
            <Option key="4">四层</Option>
            <Option key="5">五层</Option>
            <Option key="6">六层</Option>
            <Option key="7">七层</Option>
            <Option key="8">八层</Option>
            <Option key="9">九层</Option>
            <Option key="10">十层</Option>
          </Select>
        </span>) : null}
      </section>
      <svg ref={ref => this.graphSvg = ref}>
        <defs>
          <marker
            id="arrow"
            markerUnits="strokeWidth"
            markerWidth="12"
            markerHeight="12"
            viewBox="0 0 12 12"
            refX="6"
            refY="6"
            orient="auto"
          >
            <path d="M2,2 L10,6 L2,10 L6,6 L2,2" className={Style.arrow} />
          </marker>
          <marker
            id="dot"
            markerUnits="strokeWidth"
            markerWidth="6"
            markerHeight="6"
            viewBox="0 0 6 6"
            refX="3"
            refY="3"
            orient="auto"
          >
            <circle cx="3" cy="3" r="3" className={Style.dot} />
          </marker>
        </defs>
      </svg>
      <section className={Style.panel}>
        <div className={Style.fullBtn} onClick={this.handleFullscreen}>
          <Icon type={full ? 'shrink' : 'arrows-alt'} />
        </div>
        <Icon type="plus-circle-o" onClick={() => this.adjustZoom(0.5)} />
        <Slider
          className={Style.slider}
          vertical
          value={zoomValue * 10}
          min={config.zoomMin * 10}
          max={config.zoomMax * 10}
          onChange={value => this.adjustZoom((value / 10) - zoomValue)}
        />
        <Icon type="minus-circle-o" onClick={() => this.adjustZoom(-0.5)} />
      </section>
    </div>);
  }
}

export default AppView;
