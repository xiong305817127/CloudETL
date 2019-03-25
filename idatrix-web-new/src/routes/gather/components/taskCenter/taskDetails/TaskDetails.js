import React, { PropTypes } from "react";
import { Modal, Button, Table, Popconfirm, Tooltip, Icon } from "antd";
import { connect } from "dva";
import { hashHistory } from "dva/router";
import LogDetails from "./LogDetails";
import LogSearchValue from "./LogSearchValue";
import RunExcute from "./RunExcute";
import Style from "./TaskDetailsStyle.css";
import moment from "moment";

//计算天数
const GetDateDiff = (startDate, endDate) => {
  let startTime = moment(startDate, "YYYY/MM/DD HH:mm:ss").unix();
  let endTime = endDate
    ? moment(endDate, "YYYY/MM/DD HH:mm:ss").unix()
    : moment().unix();
  let dates = Math.abs(startTime - endTime) / (60 * 60 * 24);
  return dates;
};

const TaskDetails = ({ taskdetails, dispatch }) => {
  const { visible, records, status, visible1, logs, name } = taskdetails;

  const logModel = { visible1, logs };

  const columns = [
    {
      dataIndex: "execId",
      key: "execId",
      title: "执行ID",
      width: "280px"
    },
    {
      title: "执行配置",
      dataIndex: "configuration",
      key: "configuration",
      width: "10%",
      render: (text, record) => {
        return (
          <a onClick={() => showExcute(record.configuration)}>查看执行配置</a>
        );
      }
    },
    {
      title: "状态",
      dataIndex: "status",
      key: "status",
      width: "12%"
    },
    {
      title: "开始时间",
      dataIndex: "beginStr",
      key: "beginStr",
      width: "15%"
    },
    {
      title: "结束时间",
      dataIndex: "endStr",
      key: "endStr",
      width: "15%"
    },
    {
      title: "输入/输出/读/写",
      dataIndex: "outputLines",
      key: "outputLines",
      width: "18%",
      render: (text, record) => {
        return `${record.inputLines}/${record.outputLines}/${
          record.readLines
        }/${record.writeLines}`;
      }
    },
    {
      title: "操作",
      dataIndex: "view",
      key: "view",
      render: (text, record) => {
        return (
          <span>
            <a title={record.logPath} onClick={() => onSelect(record)}>
              查看执行日志
            </a>
          </span>
        );
      }
    }
  ];

  const getArgs = ()=>{
      let args = [];
     if(records.length>0){
        for(let index of records){
           args.push({
            key:index.id,
            ...index
           });
        }
     }
     return args;
  };

  const showExcute = props => {
    dispatch({
      type: "taskdetails/showExcute",
      payload: {
        visible2: true,
        excute: props
      }
    });
  };

  const onSelect = record => {
    //beginStr endStr execId
    let end = record.endStr;
    let begin = record.beginStr;
    let id = record.execId;

    if (GetDateDiff(begin, end) <= 2) {
      if (status === "job") {
        dispatch({
          type: "taskdetails/queryJobLog",
          payload: {
            name: record.name,
            group: "default",
            date: begin,
            endDate: record.endStr ? record.endStr : "",
						id,owner:record.owner
          }
        });
      } else {
        dispatch({
          type: "taskdetails/queryTransLog",
          payload: {
            name: record.name,
            group: "default",
            date: begin,
            endDate: record.endStr ? record.endStr : "",
            id,owner:record.owner
          }
        });
      }
    } else {
      dispatch({
        type: "taskdetails/showExcute",
        payload: {
          visible3: true,
          record
        }
      });
    }
  };

  const handleCancel = () => {
    dispatch({
      type: "taskdetails/closeRecord"
    });
  };

  console.log(taskdetails, "数据");
  return (
    <Modal
      title={`执行记录(${name})`}
      visible={visible}
      wrapClassName="vertical-center-modal"
      className={Style.classStyle}
      onOk={handleCancel}
      width={"90%"}
      onCancel={handleCancel}
    >
      <Table
        columns={columns}
        rowKey="execId"
        dataSource={getArgs()}
        pagination={false}
        scroll={{ y: 600 }}
      />
      <LogDetails logModel={logModel} />
      <RunExcute />
      <LogSearchValue />
    </Modal>
  );
};

export default connect(({ taskdetails }) => ({
  taskdetails
}))(TaskDetails);
