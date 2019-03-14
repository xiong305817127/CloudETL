import React from 'react'
import { Modal, Table } from 'antd';
import { connect } from 'dva';
import LogDetails from './LogDetails';
import LogSearchValue from "./LogSearchValue";
import RunExcute from './RunExcute';
import moment from "moment";

//计算天数
const GetDateDiff = (startDate, endDate) => {
  let startTime = moment(startDate, "YYYY/MM/DD HH:mm:ss").unix();
  let endTime = endDate ? moment(endDate, "YYYY/MM/DD HH:mm:ss").unix() : moment().unix();
  let dates = Math.abs((startTime - endTime)) / (60 * 60 * 24);
  return dates;
}

const index = ({ analysisDetails, dispatch }) => {
  const { visible, records, visible1, logs, name } = analysisDetails;

  const logModel = { visible1, logs };

  const columns = [{
    dataIndex: 'execId',
    key: 'execId',
    title: '执行ID',
    width: "280px"
  }, {
    title: '执行配置',
    dataIndex: 'configuration',
    key: 'configuration',
    width: "10%",
    render: (text, record) => {
      return (
        <a onClick={() => showExcute(record.configuration)}>查看执行配置</a>
      );
    }
  }, {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: "12%"
  }, {
    title: '开始时间',
    dataIndex: 'beginStr',
    key: 'beginStr',
    width: "15%"
  }, {
    title: '结束时间',
    dataIndex: 'endStr',
    key: 'endStr',
    width: "15%"
  }, {
    title: '输出行数',
    dataIndex: 'outputLines',
    key: 'outputLines',
		width: "18%",
		render:(text,record)=>{
			return `${record.inputLines}/${record.outputLines}/${record.readLines}/${record.writeLines}`;
		}	
  },
  {
    title: '操作',
    dataIndex: 'view',
    key: 'view',
    render: (text, record) => {
      return (
        <span>
          <a title={record.logPath} onClick={() => onSelect(record)}>查看执行日志</a>
        </span>
      );
    }
  }];

  const getArgs = () => {
    let args = [];
    let count = 0;
    if (records.length > 0) {
      for (let index of records) {
        args.push({
          key: index.id,
          ...index
        });
        count++;
      }
    }
    return args;
  };

  //查看执行配置
  const showExcute = (props) => {
    dispatch({
      type: "analysisDetails/save",
      payload: {
        visible2: true,
        excute: props
      }
    })
  };

  //查看执行日志
  const onSelect = (record) => {
    //beginStr endStr execId
    let end = record.endStr;
    let begin = record.beginStr;
    let id = record.execId;

    if (GetDateDiff(begin, end) <= 2) {
      dispatch({
        type: "analysisDetails/queryTransLog",
        payload: {
          name: record.name,
          group: "default",
          date: begin,
          endDate: record.endStr ? record.endStr : "",
          id
        }
      })
    } else {
      dispatch({
        type: "analysisDetails/save",
        payload: {
          visible3: true,
          record
        }
      })
    }
  };

  const handleCancel = () => {
    dispatch({
      type: "analysisDetails/save",
      payload:{ visible:false }
    })
  };

  return (
    <Modal
      title={`执行记录(${name})`}
      visible={visible}
      wrapClassName="vertical-center-modal"
      onOk={handleCancel}
      width={"90%"}
      onCancel={handleCancel} >
      <Table columns={columns} rowKey="execId" dataSource={getArgs()} pagination={false} scroll={{ y: 600 }} />
      <LogDetails logModel={logModel} />
      <RunExcute />
      <LogSearchValue />
    </Modal>
  )
};


export default connect(({ analysisDetails }) => ({
  analysisDetails
}))(index)
