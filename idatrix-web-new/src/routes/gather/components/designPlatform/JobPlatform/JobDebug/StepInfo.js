/**
 * Created by Administrator on 2017/4/26.
 */
import React from 'react';
import { Table } from 'antd'
import { connect } from 'dva'


let i = 0;
class StepInfo extends React.Component{

  constructor(props){
    super(props);
    this.state = {
      stepMeasure:[]
    }
  }

  columns = [
    {
    title: '任务/任务条目',
    dataIndex: 'entryName'
    },
    {
      title: '注释',
      dataIndex: 'comment'
    },
    {
      title: '原因',
      dataIndex: 'reason'
    },
    {
      title:"输入/输出/读/写",
      dataIndex:"action",
      render:(text,record)=>{
        return `${record.linesInput}/${record.linesOutput}/${record.linesRead}/${record.linesWritten}`
      }
    },
    {
      title:"成功/失败(次数)",
      dataIndex:"num",
      render:(text,record)=>{
         return `${record.successTimes}/${record.failTimes}`
      }
    },
    {
      title: '结果',
      dataIndex: 'result'
    }
  ];

 getChildren(data){
    data.map(index=>{
        index.key = i++;
        if(index.childEntryMeasure){
            index.children = index.childEntryMeasure;
            return this.getChildren(index.childEntryMeasure)
        }
        return index;
    });

    return data;
 }



  render(){
    const { stepMeasure } = this.props.infostep;
    let data =  this.getChildren(stepMeasure);

    return(
      <div className={this.props.styleClass}>
          <Table columns={this.columns} size={"small"} dataSource={data} bordered pagination={false} />
      </div>
    )
  }
}

export default connect(({ infostep }) => ({
  infostep
}))(StepInfo)
