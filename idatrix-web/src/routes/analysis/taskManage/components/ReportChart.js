/**
 * 任务管理曲线图
 */

import React from 'react';

// 引入echarts-for-react内核
import ReactEchartsCore from 'echarts-for-react/lib/core';
// 引入echarts内核
import echarts from 'echarts/lib/echarts';
// 仅引入line图表
import 'echarts/lib/chart/line';
import 'echarts/lib/component/tooltip';

import { dateFormat } from '../../../../utils/utils';

class Report extends React.Component{

  state = {
    list: [],
    option: {
      calculable: true,
      tooltip: {
        trigger: 'axis',
        formatter: (params) => {
          const sec = params[0].value;
          const { status } = this.state.list[params[0].dataIndex];
          // const statusMsg = status === 'SUCCEEDED' ? '成功' : '<span style="color:#da5858">失败</span>';
          return `持续时长：${sec}秒<br />　　状态：${status}`;
        }
      },
      grid: {
        top: 10,
        left: 10,
        right: 10,
        bottom: 10,
        containLabel: true
      },
      xAxis : [
        {
          type : 'category',
          boundaryGap : false,
          axisLine: {show : false},
          data : []
        }
      ],
      yAxis : [
        {
          type : 'value',
          axisLine: {show : false},
        }
      ],
      series : [
        {
          name:'持续时长',
          type:'line',
          stack: '总量',
          smooth: true,
          itemStyle: {
            normal: {color: '#43d8a2'},
          },
          areaStyle: {normal: {
            color: new echarts.graphic.LinearGradient(
              0, 0, 0, 1,
              [
                {offset: 0, color: '#43d8a2'},
                {offset: 1, color: '#ffffff'}
              ]
            )
          }},
          data:[]
        }
      ]
    },
  }

  componentDidMount() {
    this.updateData(this.props.data);
  }

  componentWillReceiveProps(nextProps) {
    this.updateData(nextProps.data);
  }

  updateData(list) {
    if (!Array.isArray(list)) return;
    const { option } = this.state;
    const xData = [];
    const sData = [];
    list.forEach(item => {
      xData.push(dateFormat(item.startTime));
      sData.push(item.endTime > 0 ? (item.endTime - item.startTime) / 1000 : 0);
    });
    option.xAxis[0].data = xData;
    option.series[0].data = sData;
    this.setState({ option, list });
  }

  render() {
    return (
      <section>
        <ReactEchartsCore
          echarts={echarts}
          option={this.state.option}
          notMerge={true}
          lazyUpdate={true}
          theme={"mint"}
          style={{height: '300px', width: '100%', marginRight: -100}}
        />
      </section>
    )
  }
}

export default Report;
