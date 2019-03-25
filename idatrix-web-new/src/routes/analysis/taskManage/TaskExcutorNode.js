/**
 * 任务执行情况
 */
import React from 'react';
import { Button, Spin } from 'antd';
import { connect } from 'dva';
import { dateFormat } from '../../../utils/utils';
import Style from './TaskExcutor.css';

const logLength = 50000; // 每次加载多少字节的日志

class EditTaskManage extends React.Component {

  state = {
    logLoading: false,
    logOffset: 0,
  }

  componentWillMount() {
    this.reLoad();
  }

  componentWillReceiveProps(nextProps) {
    const { nodeExecLog: oldLog } = this.props.taskManage;
    const { nodeExecLog: newLog } = nextProps.taskManage;
    if (oldLog && newLog !== oldLog) {
      this.setState({
        logLoading: false,
        logOffset: this.state.logOffset + logLength,
      });
    }
  }

  handleScrollLog = (e) => {
    const { scrollTop, clientHeight, scrollHeight } = e.target;
    const { nodeExecLogLen } = this.props.taskManage;
    if (nodeExecLogLen >= logLength && scrollTop + clientHeight >= scrollHeight) {
      const { dispatch } = this.props;
      const { execId, stepname } = this.props.params;
      const logOffset = this.state.logOffset + logLength;
      this.setState({ logLoading: true });
      dispatch({
        type: 'taskManage/getNodeExecLog',
        payload: {
          execid: execId,
          stepname,
          len: logLength,
          offset: logOffset,
        },
      });
    }
  }

  reLoad() {
    const { dispatch } = this.props;
    const { execId, stepname } = this.props.params;
    this.setState({ logOffset: 0 });
    dispatch({
      type: 'taskManage/getTaskNodeInfo',
      payload: {
        execid: execId,
      },
    });
    dispatch({
      type: 'taskManage/getNodeExecLog',
      payload: {
        execid: execId,
        stepname,
        len: logLength,
        offset: 0,
      },
    });
  }

  render(){
    const { id, execId, stepname } = this.props.params;
    const { nodeInfo, nodeExecLog } = this.props.taskManage;
    const result = (nodeInfo.nodes || []).find(item => item.name === stepname) || {};

    return (
      <div style={{ padding: 20, backgroundColor:'#fff',width:"100%", alignItems:"stretch"}}>
        <div className={Style.statusInfo}>
          <span className={Style.tit}>开始时间：</span>{dateFormat(result.startTime)}
          <span className={Style.tit}>执行时长：</span>{(result.endTime - result.startTime) / 1000} (s)
          <span className={Style.tit}>结束时间：</span>{dateFormat(result.endTime)}
        </div>
        <div style={{marginTop: 20}}>
          <Button type="primary" onClick={this.reLoad.bind(this)}>刷新</Button>
        </div>
        <div style={{marginTop: 20, width: '100%', textAlign: 'center'}}>
          <pre onScroll={this.handleScrollLog} className={Style.log} dangerouslySetInnerHTML={{__html: nodeExecLog}}></pre>
          <Spin tip="Loading..." spinning={this.state.logLoading} style={{marginTop: -100}} />
        </div>
      </div>
    )
  }
}

export default connect(({ taskManage })=>({
  taskManage,
}))(EditTaskManage);
