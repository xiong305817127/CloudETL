/**
 * 数据地图 - 图表
 */
import React from 'react';
import PropTypes from 'prop-types';
import lodash from 'lodash';

// 引入echarts-for-react内核
import ReactEchartsCore from 'echarts-for-react/lib/core';
// 引入echarts内核
import echarts from 'echarts/lib/echarts';
// 仅引入graph图表
import 'echarts/lib/chart/graph';
import 'echarts/lib/component/tooltip';

import { deepCopy } from '../../../utils/utils';

// 正常情况的颜色
const normalColor = 'rgb(90, 197, 236)';
// 激活时的颜色
const activeColor = 'rgb(165,100,236)';

let shouldUpdate = false;

// nodes节点比较，相同时返回true
function compareData(data1, data2) {
  if (data1.length !== data2.length) return false;
  const different = data1.some(d1 => !data2.some(d2 => d1.value === d2.value));
  return !different;
}

// links比较，相同时返回true
function compareLink(links1, links2) {
  if (links1.length !== links2.length) return false;
  const different = links1.some(l1 => !links2.some(l2 => l1.source === l2.source
    && l1.target === l2.target));
  return !different;
}

class DataMapChart extends React.Component {
  static propTypes = {
    data: PropTypes.array.isRequired,
    links: PropTypes.array.isRequired,
    onClick: PropTypes.func.isRequired,
  }

  state = {
    selectedIds: [],
    // 数据地图配置
    mapOption: {
      tooltip: {},
      series : [
        {
          type: 'graph',
          layout: 'force',
          symbol: 'roundRect',
          symbolSize: [60, 30],
          roam: true,
          force: {
            repulsion: 200,
            edgeLength: 100,
          },
          label: {
            normal: {
              show: true,
              textStyle: {
                color: normalColor,
              }
            },
            emphasis: {
              textStyle: {
                color: activeColor,
              }
            },
          },
          edgeSymbol: ['circle', 'arrow'],
          edgeSymbolSize: [4, 10],
          edgeLabel: {
            normal: {
              textStyle: {
                fontSize: 20
              }
            },
          },
          itemStyle: {
            normal: {
              color: '#fff',
              borderWidth: 4,
              borderColor: normalColor,
            },
            emphasis: {
              borderColor: activeColor,
            },
          },
          lineStyle: {
            normal: {
              color: 'rgb(250,189,139)',
              opacity: 0.9,
              width: 2,
            },
            emphasis: {
              color: activeColor,
            },
          },

          data: [],
          links: [],
        }
      ]
    },
  }

  componentWillReceiveProps(nextProps) {
    const { mapOption, selectedIds } = this.state;
    const { data, links } = nextProps;
    if (!lodash.isEqual(mapOption.series[0].data, data)
      || !lodash.isEqual(mapOption.series[0].links, links)
      || !lodash.isEqual(nextProps.selectedIds, selectedIds))
    {
      mapOption.series[0].data = deepCopy(data);
      mapOption.series[0].links = deepCopy(links);
      this.setState({
        mapOption,
        selectedIds,
      });
      this.paintNode(nextProps.selectedIds);
      shouldUpdate = true;
    } else {
      shouldUpdate = false;
    }
  }

  shouldComponentUpdate(nextProps, nextState) {
    return shouldUpdate;
  }

  // 地图事件处理
  handleMapClick = (e) => {
    console.log(e.data,"事件处理");

    this.props.onClick(e.data);
  }

  // 给选中的节点着色
  paintNode(ids) {
    const { mapOption } = this.state;
    mapOption.series[0].data.forEach(node => {
      const found = ids.indexOf(node.value) > -1;
      if (found) {
        node.itemStyle = {
          normal: {
            borderColor: activeColor,
          },
        }
        node.label = {
          normal: {
            textStyle: {
              color: activeColor
            },
          },
        }
      } else {
        delete node.itemStyle;
        delete node.label;
      }
    });
    // 选中边
    if (ids.length === 2) {
      let sourceIndex = -1, targetIndex = -1;
      mapOption.series[0].data.forEach((it, index) => {
        if (it.value === ids[0]) {
          sourceIndex = index;
        }
        if (it.value === ids[1]) {
          targetIndex = index;
        }
      });
      mapOption.series[0].links.forEach(link => {
        const found = link.source === sourceIndex && link.target === targetIndex;
        if (found) {
          link.lineStyle = {
            normal: {
              color: activeColor,
            },
          }
        } else {
          delete link.lineStyle;
        }
      });
    }
  }

  render() {
    shouldUpdate = false;
    return <ReactEchartsCore
      echarts={echarts}
      option={this.state.mapOption}
      notMerge={false}
      lazyUpdate={true}
      theme={"mint"}
      style={{height: '100%', zIndex: 1}}
      onEvents={{
        click: this.handleMapClick,
      }}
    />
  }
}

export default DataMapChart;
