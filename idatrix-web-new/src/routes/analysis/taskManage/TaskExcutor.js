/**
 * 任务执行情况
 */
import React from 'react';
import { Form, Input, Table, Popover, Button, Row, Col, Tabs} from 'antd';
import { connect } from 'dva';
import { Link } from 'react-router';
import NodeFlow from './components/NodeFlow';
import { dateFormat } from '../../../utils/utils';
import Style from './TaskExcutor.css';
import Modal from 'components/Modal';

const TabPane = Tabs.TabPane;

class EditTaskManage extends React.Component{

  reloadTimer = null;

  columns = [
    {
      title: '名称',
      dataIndex: 'name',
    },
    {
      title: '类型',
      dataIndex: 'type',
    },
    {
      title: '开始时间',
      dataIndex: 'startTime',
      render: (text) => text < 0 ? '' : dateFormat(text),
    },
    {
      title: '结束时间',
      dataIndex: 'endTime',
      render: (text) => text < 0 ? '' : dateFormat(text),
    },
    {
      title: '执行时长',
      render: (text, record) => record.endTime > 0 ? (record.endTime - record.startTime) / 1000 : '',
    },
    {
      title: '执行状态',
      dataIndex: 'status',
    },
    {
      title: '详情',
      render: (text, record) => {
        const { id, execId } = this.props.params;
        const path = `/analysis/TaskManage/EditTaskManage/${id}/TaskExcutor/${execId}/node/${record.name}`;
        return <Link to={path}>查看</Link>
      }
    }
  ];

  componentWillMount() {
    this.reLoad();
  }

  componentWillReceiveProps(nextProps) {
    const { view, nodeInfo } = this.props.taskManage;
    if (!this.reloadTimer && nodeInfo.status === 'RUNNING') {
      this.reloadTimer = setInterval(() => {
        this.reLoad();
      }, 5e3);
    } else if (this.reloadTimer && nodeInfo.status !== 'RUNNING') {
      clearInterval(this.reloadTimer);
    }
  }

  componentWillUnmount() {
    if (this.reloadTimer) {
      clearInterval(this.reloadTimer);
    }
  }

  reLoad() {
    const { dispatch } = this.props;
    const { id, execId } = this.props.params;
    dispatch({
      type: 'taskManage/getTaskView',
      payload: id,
    });
    dispatch({
      type: 'taskManage/getTaskNodeInfo',
      payload: {
        execid: execId,
      },
    });
    dispatch({
      type: 'taskManage/getTaskExecLog',
      payload: {
        execid: execId,
      },
    });
  }

  render(){
    const { id, execId } = this.props.params;
    const { nodeInfo, taskExecLog, view } = this.props.taskManage;

    return(
      <div style={{ padding: 20, backgroundColor:'#fff',width:"100%", alignItems:"stretch"}}>
        <div className={Style.statusInfo}>
          <span className={Style.tit}>开始时间：</span>{dateFormat(nodeInfo.startTime)}
          <span className={Style.tit}>执行时长：</span>{nodeInfo.endTime > 0 ? (nodeInfo.endTime - nodeInfo.startTime) / 1000 + ' (s)' : ''}
          <span className={Style.tit}>结束时间：</span>{nodeInfo.endTime > 0 ? dateFormat(nodeInfo.endTime) : ''}
        </div>
        <div style={{marginTop: 20}}>
          <Button type="primary" onClick={this.reLoad.bind(this)}>刷新</Button>
        </div>
        <Tabs defaultActiveKey="1" style={{ height:"100%"}}>
          <TabPane tab="流程图" key="1" style={{ height:"100%"}}>
            <NodeFlow data={view.data} nodes={nodeInfo.nodes} />
          </TabPane>

          <TabPane tab="节点列表" key="2" style={{ height:"100%"}}>
            <Table className="stripe-table" rowKey="name" columns={this.columns} dataSource={nodeInfo.nodes} pagination={false} />
          </TabPane>

          <TabPane tab="日志" key="3" style={{ height:"100%"}}>
            <pre className={Style.log} dangerouslySetInnerHTML={{__html: taskExecLog}}></pre>
          </TabPane>
        </Tabs>
      </div>
    )
  }
}

export default connect(({ taskManage })=>({
  taskManage,
}))(EditTaskManage);
