import React from 'react'
import { connect } from 'dva';
import { Link } from 'react-router';
import { Button, Icon, Popconfirm, message, Tooltip } from 'antd';
import TableList from '../../../components/TableList';
import AddTask from './components/AddTask';
import { deleteTask, deleteScheduleTask } from '../../../services/analysisTask';
import Empower from '../../../components/Empower';

class TaskManage extends React.Component{

  state = {
    addModalVisable: false,
  }

  columns = [
    {
      title: '名称',
      dataIndex: 'name',
      render: (text, record) => <Link to={`/analysis/TaskManage/EditTaskManage/${record.id}`}>{text}</Link>,
    }, {
      title: '描述',
      dataIndex: 'description',
      render:(text) => (<div className="word25" title={text}>{text}</div>)
    }, {
      title: '最后修改时间',
      dataIndex: 'modifyTime',
    }, {
      title: '上次执行时间',
      dataIndex: 'lastExecTime',
    }, {
      title: '下次执行时间',
      dataIndex: 'nextExecTime',
    }, {
      title: '执行周期',
      dataIndex: 'scheduleInfo',
    }, {
      title: '状态',
      dataIndex: 'status',
    }, {
      title: '操作',
      className: 'td-nowrap',
      render: (text, record) => (
        <span>
          <Empower api="/task/definition/updateTaskAndFlow">
            <Tooltip title="编辑任务">
              <Link to={`/analysis/TaskManage/EditTaskManage/${record.id}`}
                style={{marginRight: 10}}
              >
                <Icon type="edit" className="op-icon"/>
              </Link>
            </Tooltip>
          </Empower>
          <Empower api="/task/definition/deleteTask">
            <Popconfirm title="确认要删除该任务吗？" onConfirm={() => this.handleDeleteTask(record)}>
              <Tooltip title="删除任务">
                <a style={{marginRight: 10}}><Icon type="delete" className="op-icon" /></a>
              </Tooltip>
            </Popconfirm>
          </Empower>
          {record.scheduleInfo ? (
          <Empower api="/task/definition/deleteTask">
            <Popconfirm title="确认要删除该调度吗？" onConfirm={() => this.handleDeleteSchedule(record)}>
              <Tooltip title="删除调度">
                <a><Icon type="clock-circle" className="op-icon" /></a>
              </Tooltip>
            </Popconfirm>
          </Empower>
          ) : null}
        </span>
      ),
    }
  ]

  // 删除任务
  handleDeleteTask = async (record) => {
		const { data } = await deleteTask({ taskid: record.id });
		const { code } = data;
    if (code === "200") {
      message.success('删除任务成功');
      this.reloadList();
    }
  }

  // 删除调度
  handleDeleteSchedule = async (record) => {
    const { data } = await deleteScheduleTask({ taskid: record.id });
		const { code } = data;
    if (code === "200") {
      message.success('删除调度成功');
      this.reloadList();
    }
  };

  // 新增任务
  handleAddTaskOk = () => {
    this.setState({ addModalVisable: false });
    this.reloadList();
  };

  // 重新加载列表
  reloadList = () => {
    const { dispatch, location: { query } } = this.props;
    console.log(query,"location",location,"location---qurey");
    dispatch({
      type: 'taskManage/getTaskList',
      payload: {
        pageNum: query.page,
        pageSize: query.pageSize,
      },
    });
  }

  //3.输出组件页面：
  render(){
    const { taskManage: { taskList } } = this.props;
    return(
      <div style={{ backgroundColor: '#fff',width:"100%" }}>
        <Empower api="/task/definition/createTask">
          <Button type="primary" className="margin_20"
            onClick={() => this.setState({ addModalVisable: true })}>新建</Button>
        </Empower>
        <TableList className="margin_20"
          rowKey="id"
          showIndex
          columns={this.columns}
          dataSource={taskList.results}
          pagination={{total: taskList.total}}
        />

        <AddTask
          title="新建任务"
          visible={this.state.addModalVisable}
          closable={false}
          onOk={this.handleAddTaskOk}
          onCancel={() => this.setState({ addModalVisable: false })}
        />
      </div>
    )
  }
}

export default connect(({ taskManage }) => ({
  taskManage,
}))(TaskManage);
