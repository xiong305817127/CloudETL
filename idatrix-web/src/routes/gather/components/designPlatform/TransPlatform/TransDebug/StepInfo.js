/**
 * Created by Administrator on 2017/4/26.
 */
import React from 'react';
import { Table } from 'antd'
import { connect } from 'dva'

class StepInfo extends React.Component{

  constructor(props){
    super(props);
    this.state = {
      stepMeasure:[]
    }
  }

  columns = [
    {
      title: '#',
      dataIndex: 'key',
    },
    {
    title: '步骤名称',
    dataIndex: 'stepName',
    },
    {
      title: '复制的记录行数',
      dataIndex: 'copy',
    },
    {
      title: '读',
      dataIndex: 'linesRead',
    },
    {
      title: '写',
      dataIndex: 'linesWritten',
    },
    {
      title: '输入',
      dataIndex: 'linesInput',
    },
    {
      title: '输出',
      dataIndex: 'linesOutput',
    },
    {
      title: '更新',
      dataIndex: 'linesUpdated',
    },
    {
      title: '拒绝',
      dataIndex: 'linesRejected',
    },
    {
      title: '错误',
      dataIndex: 'errors',
    },
    {
      title: '激活',
      dataIndex: 'statusDescription',
    },{
      title: '时间',
      dataIndex: 'seconds',
    },{
      title: '速度(条记录/秒)',
      dataIndex: 'speed',
    },{
      title: 'Pri/in/out',
      dataIndex: 'priority',
    }
  ];



  render(){
    const { stepMeasure } = this.props.infostep;

    let i = 1;
    for(let index of stepMeasure){
      index.key = i++;
    }

    return(
      <div className={this.props.styleClass}>
          <Table columns={this.columns} size={"small"} dataSource={stepMeasure} bordered pagination={false} />
      </div>
    )
  }
}

export default connect(({ infostep }) => ({
  infostep
}))(StepInfo)
