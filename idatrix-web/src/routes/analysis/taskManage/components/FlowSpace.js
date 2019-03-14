import React from 'react';
import { connect } from "dva";
import { Icon, Button, notification, Dropdown, Menu, Row, Col, Checkbox, message, Tooltip } from 'antd';
import { initItems, updateItems, getNewName, getItem, deleteItemLines, getItems, getLines, getItemName } from "./flowdata";
import { modifyTask, startTask, pauseTask, resumeTask, cancelTask } from '../../../../services/analysisTask';
import Schedule from './Schedule';
import Empower from '../../../../components/Empower';
import Modal from 'components/Modal';

import Tools from './tools.config';
import Style from './Flow.css';

const ButtonGroup = Button.Group;

/**
 * 检查是否包含孤立节点
 * @param  {object}  obj 任务配置
 * @return {boolean}     包含则返回true
 */
const hasUnsocial = (obj) => {
  const { hopList, stepList } = obj;
  let has = false;
  if (stepList.length < 2) return false; // 单个节点允许孤立
  stepList.some(step => {
    has = !hopList.some(hop => {
      return step.name === hop.from || step.name === hop.to;
    });
    return has;
  });
  return has;
};

class FlowSpace extends React.Component {

  constructor(props) {
    super(props);
    const { items, info_name, viewId } = props.flowspace;
    this.state = {
      dispatch: props.dispatch,
      info_name: info_name,
      items: items,
      viewId: viewId,
      style: "workspace_contain1",
      /**
       * 运行状态
       * edit   - 编辑
       * run    - 运行中
       * pause  - 暂停中
       * stop   - 停止中
       */
      runStatus: '',
      scheduleVisible: false,
    }
  }

  setModal1Visible(value, text, id) {
    this.state.dispatch({
      type: 'deletemodel/deleteItem',
      visible: value,
      id: id,
      text: text
    })
  }
  setModal2Visible(value, id, text, config, type, e) {
    e.preventDefault();
    // console.log(value);
    // console.log(id);
    // console.log(text);
    // console.log(config);
    // console.log(type);

    this.state.dispatch({
      type: 'items/show',
      visible: value,
      text: text,
      id: id,
      panel: type,
      config: config,
      readonly: this.state.runStatus !== 'edit' && this.state.runStatus !== 'unsave',
    });
  }

  /*添加节点*/
  drop(e) {
    this.state.dispatch({
      type: 'flowspace/addItem',
      panel: e.dataTransfer.getData("type"),
      text: getNewName(Tools[e.dataTransfer.getData("type")].text),
      x: e.clientX - 330,
      y: e.clientY - 290,
      imgUrl: Tools[e.dataTransfer.getData("type")].imgUrl
    })
  }

  handleDragClick(id) {
    this.state.dispatch({
      type: 'flowspace/dragClick',
      id: id
    })
  }

  componentDidMount() {
    const { lines, model, items, info_name } = this.props.flowspace;
    if (model === "newState") {
      initItems(lines, items, this.refs.mainContent, info_name);
    }
    this.props.router.setRouteLeaveHook(
      this.props.route,
      this.routerWillLeave.bind(this),
    );
  }

  routerWillLeave(nextLocation) {
    if (this.state.runStatus === 'unsave' && this.state.items.length > 0) {
      Modal.confirm({
        content: '尚有内容未保存，确定要离开当前页面吗？',
        onOk: () => {
          this.setState({
            runStatus: '',
          }, () => {
            this.props.router.push(nextLocation);
          });
        }
      });
      return false;
    }
  }

  componentWillReceiveProps(nextProps) {
    const { model } = nextProps.flowspace;
    const { items, info_name, viewId, status, nodes } = nextProps.flowspace;
    let runStatus = this.state.runStatus;
    switch (status) {
      case 'SAVED':
      case 'UNEXECUTED': runStatus = 'edit'; break; // 可编辑可运行
      case 'UNSAVED': runStatus = 'unsave'; break; // 未保存（此时也可编辑，但不可运行）
      case 'RUNNING': runStatus = 'run'; break; // 运行中
      case 'PAUSED': runStatus = 'pause'; break; // 暂停中
      case 'SUCCEEDED':
      case 'CANCELLED':
      case 'FAILED': runStatus = 'stop'; break; // 已停止
      default: runStatus = 'stop'; break; // 未适配的类型都默认已停止
    }
    // console.log(status, runStatus);

    // 处理节点状态
    /*items.forEach(item => {
      const found = nodes.find(node => node.name === item.text);
      switch (found.status) {
        case 'PAUSE':
          item.dragClass = 'pauseStyle';
        break;
        case 'RUNNING':
          item.dragClass = 'runningStyle';
        break;
        case 'SUCCEEDED':
          item.dragClass = 'successStyle';
        break;
        case 'CANCELLED':
        case 'FAILED':
          item.dragClass = 'errStyle';
        break;
        case 'PREPARING':
          item.dragClass = 'pauseStyle';
        break;
      }
    });*/
    this.setState({
      items: items,
      info_name: info_name,
      viewId: viewId,
      runStatus
    });
  }

  componentDidUpdate() {
    const { lines, model, items, info_name } = this.props.flowspace;
    if (model === "updateState" || model === "newState") {
      initItems(lines, items, this.refs.mainContent, info_name);
    } else if (model === "addItem" || model === "saveItem" || model === "updateStep" || model === "updateStatus") {
      updateItems(this.state.items, info_name);
    } else if (model === "deleteItem") {
      const { deleteId } = this.props.flowspace;
      updateItems(this.state.items, info_name);
      deleteItemLines(deleteId);
    }
  }

  updateStatus = (payload = {}) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'flowspace/updateStatus',
      payload,
    });
  }

  handleChangeStyle(e) {
    const { dispatch } = this.props;
    // console.log(e);

    dispatch({
      type: "flowspace/changeStyle",
      spaceStyle: e.target.checked ? "workspace_contain1" : "workspace_contain2"
    })
  }

  // 启动任务
  handleStartTask = async () => {
    const { id } = this.props;
    const isPause = this.state.runStatus === 'pause';
    const runTask = isPause ? resumeTask : startTask;
    const { data } = await runTask(id);
    if (data.code === "200") {
      this.updateStatus({
        status: 'RUNNING',
      });
      this.setState({ runStatus: 'run' });
      message.success(isPause ? '已恢复' : '启动成功');
    } 
  }

  // 暂停任务
  handlePauseTask = async () => {
    const { id } = this.props;
    const { data } = await pauseTask(id);
    if (data.code === "200") {
      // const runStatus = 'pause';
      // this.updateStatus();
      // this.setState({ runStatus });
      this.updateStatus({
        status: 'PAUSED',
      });
      message.success('已暂停');
    }
  }

  // 取消任务
  handleCancelTask = () => {
    Modal.confirm({
      content: '确定要停止任务吗？',
      onOk: async () => {
        const { id } = this.props;
        const { data } = await cancelTask(id);
        if (data.code === "200") {
          // const runStatus = 'stop';
          // this.updateStatus();
          // this.setState({ runStatus });
          this.updateStatus({
            status: 'CANCELLED',
          });
          message.success('已停止');
        }
      }
    });
  }

  // 创建调度成功
  handleScheduleUpdated = (needReload) => {
    const { dispatch, id } = this.props;
    this.setState({ scheduleVisible: false });
    if (needReload) {
      dispatch({
        type: 'flowspace/getTaskView',
        payload: id,
      });
    }
  }

  // 创建调度
  createSchedule = () => {
    this.setState({ scheduleVisible: true });
    this.updateStatus();
  }

  // 保存任务
  async saveView() {
    // console.log(this.state.items);
    // console.log(this.props.flowspace);
    // console.log(getItems());
    const { viewId, description, name, lines } = this.props.flowspace;

    let obj = {};
    obj.id = viewId;
    obj.name = name;
    obj.description = description;
    obj.stepList = [];
    for (let index of this.state.items) {
      const item = {
        scripts: [],
        jarContent: {},
      };
      if (index.dataId) {
        item.id = index.dataId;
      } else {
        item.id = 0;
      }
      item.name = index.text;
      item.description = index.description;
      item.type = index.panel;
      item.location = {
        "xloc": index.x,  //X轴坐标
        "yloc": index.y
      };
      if (index.config) {
        if (index.config instanceof Array) {
          item.scripts = index.config
        } else {
          item.jarContent = index.config
        }
      }
      obj.stepList.push(item)
    };
    obj.hopList = [];
    for (let index1 of getLines()) {
      const fromName = getItemName(index1.sourceId);
      const toName = getItemName(index1.targetId);
      if (fromName && toName) {
        // 去重
        if (obj.hopList.some(it => it.from === fromName && it.to === toName)) continue;
        obj.hopList.push({
          from: fromName,
          to: toName,
        });
      }
    }
    if (obj.stepList.length === 0) {
      message.error('任务必须至少包括一个可执行节点');
      return;
    }

    if (hasUnsocial(obj)) {
      message.error('存在孤立节点，请检查');
      return;
    }

    const { data } = await modifyTask(obj);
    if (data.code === "200") {
      message.success('保存成功');
      this.updateStatus({
        status: 'SAVED',
      });
    }
  }

  render() {
    const { runStatus } = this.state;
    const { status } = this.props.flowspace;
    const draggable = runStatus === 'edit' || runStatus === 'unsave';

    return (
      <div className={Style.mainContent}>
        <div ref="mainContent"
          className={Style.divContent + " workspace_contain2"}
          onDrop={!draggable ? null : e => { this.drop(e) }}
          onDragOver={!draggable ? null : e => { e.preventDefault() }}
          id="workspace_container"
        >
          {
            this.state.items.map(item => {
              return (
                <div draggable={draggable} className={Style.drop + " " + item.dragClass}
                  onDoubleClick={(e) => { this.setModal2Visible(true, item.id, item.text, item.config, item.panel, e) }}
                  onClick={() => { this.handleDragClick(item.id) }} id={item.id} key={item.id}
                  style={{ left: item.x, top: item.y, cursor: draggable ? 'move' : 'default' }}>
                  {draggable ? <div className={Style.canDrag}></div> : null}
                  <img className={Style.img} src={item.imgUrl} />
                  <div className={Style.span}>{item.text}</div>
                  {draggable ? <Icon type="check-circle" className="checkCircle" /> : null}
                  {draggable ? <Icon onClick={() => { this.setModal1Visible(true, item.text, item.id) }} className={Style.close}
                    type="close" /> : null}
                  {!draggable ? <div style={{ position: 'absolute', top: 0, left: 0, right: 0, bottom: 0 }}></div> : null}
                </div>
              )
            })
          }
          <div className={Style.saveButton}>
            <ButtonGroup>
              <Empower api="/executor/startTask">
                <Button
                  title={runStatus !== 'pause' ? '执行任务' : '恢复任务'}
                  disabled={runStatus === 'unsave' || runStatus === 'run' || this.state.items.length < 1}
                  type={runStatus === 'run' ? 'primary' : 'default'}
                  className={Style.resetPadding}
                  onClick={this.handleStartTask.bind(this)} icon="caret-right"></Button>
              </Empower>
              <Empower api="/executor/pauseTask">
                <Button
                  title="暂停执行"
                  disabled={runStatus !== 'run'}
                  type={runStatus === 'pause' ? 'primary' : 'default'}
                  className={Style.resetPadding}
                  onClick={this.handlePauseTask.bind(this)} icon="pause"></Button>
              </Empower>
              <Empower api="/executor/cancelTask">
                <Button
                  title="停止执行"
                  disabled={runStatus !== 'run' && runStatus !== 'pause'}
                  type={runStatus === 'stop' ? 'primary' : 'default'}
                  className={Style.resetPadding}
                  onClick={this.handleCancelTask.bind(this)} icon="poweroff"></Button>
              </Empower>
              <Empower api="/executor/createTaskSchedule">
                <Button
                  title="创建调度"
                  disabled={runStatus === 'unsave' || this.state.items.length < 1}
                  className={Style.resetPadding}
                  onClick={this.createSchedule.bind(this)} icon="clock-circle-o"></Button>
              </Empower>
              <Empower api="/task/definition/updateTaskAndFlow">
                <Button
                  title="保存任务"
                  disabled={runStatus !== 'unsave'}
                  className={Style.resetPadding}
                  type={runStatus === 'unsave' ? 'primary' : 'default'}
                  onClick={this.saveView.bind(this)} icon="save"></Button>
              </Empower>
            </ButtonGroup>
          </div>
        </div>

        <Schedule
          visible={this.state.scheduleVisible}
          data={this.props.flowspace}
          onOk={this.handleScheduleUpdated}
          onCancel={() => this.setState({ scheduleVisible: false })}
        />

      </div >
    )
  }
}

export default connect(({ flowspace }) => ({
  flowspace
}))(FlowSpace)
