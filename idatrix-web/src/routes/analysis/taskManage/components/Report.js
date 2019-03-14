/**
 * 任务管理报表
 */

import React from 'react';
// import { } from  'antd';
import { connect } from 'dva';
import { Link } from 'react-router';
import { DEFAULT_PAGE_SIZE } from '../../../../constants';
import TableList from '../../../../components/TableList';
import ReportChart from './ReportChart';
import { dateFormat } from '../../../../utils/utils';

class Report extends React.Component{

  columns = [
    {
      title: 'ID',
      dataIndex: 'execid',
      render: (text) => {
        const { id } = this.props;
        const path = `/analysis/TaskManage/EditTaskManage/${id}/TaskExcutor/${text}`;
        return <Link to={path}>{text}</Link>
      }
    },
    {
      title: '用户',
      dataIndex: 'submitUser',
    },
    {
      title: '开始时间',
      dataIndex: 'startTime',
      className: 'td-center',
      render: (text) => text < 0 ? '' : dateFormat(text),
    },
    {
      title: '结束时间',
      dataIndex: 'endTime',
      className: 'td-center',
      render: (text) => text < 0 ? '' : dateFormat(text),
    },
    {
      title: '持续时长(s)',
      className: 'td-center',
      render: (text, record) => record.endTime > 0 ? (record.endTime - record.startTime) / 1000 : '',
    },
    {
      title: '状态',
      className: 'td-center',
      dataIndex: 'status',
    },
    {
      title: 'action',
      dataIndex: 'action',
    },
  ]

  /*componentWillMount() {
    const { dispatch, id } = this.props;
    dispatch({
      type: 'taskManage/getReport',
      payload: id,
    })
  }*/

  render() {
    const { taskManage: { taskReport } } = this.props;

    return (
      <div>
        {taskReport.executions && taskReport.executions.length > 0 ? (
          <ReportChart data={taskReport.executions} />
        ) : null}
        <TableList
          rowKey="execid"
          columns={this.columns}
          dataSource={taskReport.executions}
          pagination={false}
          style={{marginTop: 40}}
        />
      </div>
    )
  }
}

export default connect(({ taskManage }) => ({
  taskManage,
}))(Report);
