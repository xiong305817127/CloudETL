/**
 * Created by Administrator on 2018/3/5.
 */

import React from "react";
import { connect } from "dva";
import {
  Icon,
  Button,
  notification,
  Dropdown,
  Menu,
  Row,
  Col,
  Checkbox,
  message,
  Badge,
  Popover,
  Modal,
  Radio
} from "antd";
import { runStatus, pauseStatus, getScreenSize } from "../../../constant";
import {
  defaultJobsSettings,
  sourceJobsConfig,
  targetJobsConfig
} from "../../config/workspace.config";
const confirm = Modal.confirm;
const ButtonGroup = Button.Group;
import Tools from "../../config/Tools";
import JobDebug from "./JobDebug/JobDebug";
import Style from "./Workspace.css";

let timer = null;

class Workspace extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      Instance: null,
      linesState: "copy"
    };
  }

  componentDidMount() {
    const mainContent = this.refs.mainContent;
    const { dispatch } = this.props;
    const _this = this;

    const Instance = jsPlumb.getInstance({
      ...defaultJobsSettings,
      Container: mainContent
    });

    Instance.registerConnectionType("basic", {
      anchor: "Continuous",
      connector: "StateMachine"
    });
    Instance.bind("connection", function(info) {
      const { shouldUpdate, lines, items } = _this.props.jobspace;

      if (!shouldUpdate) {
        dispatch({
          type: "jobspace/addHop",
          payload: {
            start: info.sourceId,
            target: info.targetId,
            isJob: true
          }
        });
      }

      let line = {};
      for (let index of lines) {
        if (
          index.sourceId === info.sourceId &&
          index.targetId === info.targetId
        ) {
          line = index;
        }
      }

      info.connection.removeClass("errJobClass");
      info.connection.removeClass("sucJobClass");
      let args = info.connection
        .getOverlay("label")
        .canvas.className.split(" ");
      if (line.unconditional) {
        info.connection.getOverlay("label").canvas.className =
          args[0] + " " + "aLabel";
      } else {
        if (line.evaluation) {
          info.connection.addClass("sucJobClass");
          info.connection.getOverlay("label").canvas.className =
            args[0] + " " + "aLabelSuccess";
        } else {
          info.connection.addClass("errJobClass");
          info.connection.getOverlay("label").canvas.className =
            args[0] + " " + "aLabelError";
        }
      }

      info.connection.getOverlay("label").canvas.onclick = function() {
        timer && clearTimeout(timer);
        timer = setTimeout(function() {
          const { lines } = _this.props.jobspace;

          let obj = {};

          for (let index of lines) {
            if (
              index.sourceId === info.sourceId &&
              index.targetId === info.targetId
            ) {
              obj = index;
            }
          }

          if (obj.unconditional) {
            obj.unconditional = false;
            obj.evaluation = true;
          } else {
            if (obj.evaluation) {
              obj.unconditional = false;
              obj.evaluation = false;
            } else {
              obj.unconditional = true;
              obj.evaluation = true;
            }
          }
          dispatch({
            type: "jobspace/saveLine",
            payload: {
              from: info.sourceId,
              to: info.targetId,
              unconditional: obj.unconditional,
              evaluation: obj.evaluation
            }
          });
        }, 300);
      };

      info.connection.getOverlay("label").canvas.ondblclick = function() {
        timer && clearTimeout(timer);
        dispatch({
          type: "jobspace/deleteHop",
          payload: {
            sourceId: info.sourceId,
            targetId: info.targetId
          }
        });
      };
    });
    Instance.bind("beforeDrop", function(_ref) {
      const { lines } = _this.props.jobspace;
      if (_ref.sourceId === _ref.targetId) {
        return false;
      }
      for (let index of lines) {
        if (
          (index.targetId === _ref.targetId &&
            index.sourceId === _ref.sourceId) ||
          (index.targetId === _ref.sourceId && index.sourceId === _ref.targetId)
        ) {
          return false;
        }
      }
      return true;
    });

    this.setState({
      Instance
    });
  }

  componentDidUpdate() {
    const { shouldUpdate } = this.props.jobspace;

    if (shouldUpdate) {
      this.initItemsView(this.props.jobspace);
    }
  }

  initItemsView(props) {
    const { Instance } = this.state;
    const { dispatch } = this.props;

    const { items, lines, itemsId } = props;
    console.count("执行次数");
    console.log(items);
    console.log(lines);
    console.log(Instance);

    let newItemsId = [];

    if (Instance) {
      Instance.deleteEveryConnection();

      (items || []).map(index => {
        newItemsId.push(index.id);

        if (itemsId.includes(index.id)) {
          return;
        }
        let el = this.refs[index.id];
        Instance.draggable(el, {
          start: function(event) {},
          drag: function(event, ui) {
            Instance.repaintEverything();
          },
          stop: function(event) {
            dispatch({
              type: "jobspace/moveStep",
              payload: {
                x: event.finalPos[0],
                y: event.finalPos[1],
                id: event.el.id
              }
            });
            Instance.repaintEverything();
          }
        });
        Instance.makeSource(el, {
          //设置连接的源实体，就是这一头
          ...sourceJobsConfig,
          filter: "." + el.firstChild.className,
          Container: Instance.getContainer()
        });
        Instance.makeTarget(el, {
          ...targetJobsConfig,
          Container: Instance.getContainer()
        });
        Instance.fire("jsPlumbDemoNodeAdded", el);
      });
      (lines || []).map(index => {
        Instance.connect({ source: index.sourceId, target: index.targetId });
      });
    }

    dispatch({
      type: "jobspace/updateData",
      payload: {
        shouldUpdate: false,
        itemsId: newItemsId
      }
    });
  }

  //编辑节点内容 ok
  editItemContent(e, item) {
    e.preventDefault();

    const { name } = this.props.jobspace;
    const { dispatch } = this.props;
    const { activeArgs } = this.props.jobheader;

    if (item.panel === "UNKNOWN") {
      dispatch({
        type: "items/show",
        visible: true,
        text: item.text,
        panel: item.panel,
        key: item.id
      });
    } else {
      this.props.jobheader.methods.edit_entry({
        jobName: name,
        entryName: item.text,
        owner: activeArgs.get(name).owner
      }).then(res => {
        const { code, data } = res.data;
        if (code === "200") {
          const {
            type,
            description,
            entryParams,
            nextEntryNames,
            prevEntryNames,
            parallel
          } = data;
          dispatch({
            type: "items/show",
            visible: true,
            transname: name,
            text: item.text,
            panel: type,
            parallel: parallel,
            description: description,
            config: entryParams,
            nextStepNames: nextEntryNames,
            prevStepNames: prevEntryNames,
            key: item.id
          });
        }
      });
    }
  }
  //复制节点 ok
  handleCopyItem(item) {
    const { name } = this.props.jobspace;
    const { dispatch } = this.props;

    item.viewName = name;
    dispatch({
      type: "designplatform/copyCache",
      payload: {
        copyJobItme: item
      }
    });
  }
  //删除节点 ok
  deleteItem(item) {
    const { name } = this.props.jobspace;
    const { dispatch } = this.props;
    dispatch({
      type: "tip/deleteItem",
      visible: true,
      id: item.id,
      text: item.text,
      transname: name,
      status: "job"
    });
  }
  //添加节点 ok
  addItem(e, type) {
    const { items } = this.props.jobspace;

    let panel = e.dataTransfer.getData("type");

    if (!panel) {
      return false;
    }

    if (panel === "SPECIAL") {
      for (let index of items) {
        if (index.panel === "SPECIAL") {
          message.error("每个调度中只允许有一个START!");
          return false;
        }
      }
    }

    let obj = getScreenSize();

    this.props.dispatch({
      type: "jobspace/addNewItem",
      payload: {
        panel: panel,
        text: Tools[e.dataTransfer.getData("type")].text,
        x: e.clientX - 255 - obj.moveX,
        y: e.clientY - 155 - obj.moveY,
        imgUrl: Tools[e.dataTransfer.getData("type")].imgUrl
      }
    });
  }
  //编辑job属性 ok
  editJobConfig(e, name, viewId) {
    const { dispatch, viewStatus } = this.props;
    console.log(viewStatus, "状态");

    if (e.target === this.refs.mainContent) {
      dispatch({
        type: "newtrans/showJobModel",
        payload: {
          info_name: name,
          viewId: viewId,
          status: viewStatus
        }
      });
    }
  }
  //切换背景 ok
  handleChangeStyle(e) {
    const { dispatch } = this.props;
    dispatch({
      type: "jobspace/updateData",
      payload: {
        spaceStyle: e.target.checked
          ? "workspace_contain1"
          : "workspace_contain2"
      }
    });
  }

  //执行调度
  runJob(viewId) {
    const { name } = this.props.jobspace;
    const { dispatch } = this.props;
    dispatch({
      type: "runjob/queryServerList",
      payload: {
        visible: true,
        actionName: name,
        viewId: viewId
      }
    });
  }

  //重启调度
  restartJob(viewId) {
    const { name } = this.props.jobspace;
    this.props.jobheader.methods.getJob_exec_id({name}).then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        const { executionId } = data;
        if (executionId) {
          this.props.jobheader.methods.getJob_exec_stop({executionId}).then(res => {
            const { code } = res.data;
            if (code === "200") {
              message.success("调度正在重启，请耐心等待！", 5);
              this.runJob(viewId);
            }
          });
        }
      }
    });
  }

  //终止调度
  stopJob() {
    const { name } = this.props.jobspace;
    this.props.jobheader.methods.getJob_exec_id({name}).then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        const { executionId } = data;
        if (executionId) {
          this.props.jobheader.methods.getJob_exec_stop({executionId}).then(res => {
            const { code } = res.data;
            if (code === "200") {
              message.success("执行正在终止中，如时间过长，请耐心等待！", 5);
            }
          });
        }
      }
    });
  }

  render() {
    const { btnStatus, spaceStyle, viewId, items, name } = this.props.jobspace;

    console.log(items);
    console.log(btnStatus);
    console.log(runStatus);
    console.log(pauseStatus);

    return (
      <div className={Style.mainContent}>
        <div
          ref="mainContent"
          className={Style.divContent + " " + spaceStyle}
          onDrop={e => {
            this.addItem(e);
          }}
          onDragOver={e => {
            e.preventDefault();
          }}
          onDoubleClick={e => {
            this.editJobConfig(e, name, viewId);
          }}
          id="workspace_container"
        >
          {items.map(item => {
            return (
              <div
                className={Style.drop + " " + item.dragClass}
                ref={item.id}
                onDoubleClick={e => {
                  this.editItemContent(e, item);
                }}
                id={item.id}
                key={item.id}
                style={{ left: item.x, top: item.y }}
              >
                <div className={Style.canDrag} />
                <Popover
                  content={
                    <Button
                      onClick={() => {
                        this.handleCopyItem(item);
                      }}
                    >
                      复制
                    </Button>
                  }
                  key={item.id}
                >
                  <div className={Style.domSet} />
                </Popover>
                <img className={Style.img} src={require(item.imgUrl + "")} />
                <div className={Style.span}>{item.text}</div>
                <Icon type="check-circle" className="checkCircle" />
                <Icon
                  onClick={() => {
                    this.deleteItem(item);
                  }}
                  className={Style.close}
                  type="close"
                />
              </div>
            );
          })}
        </div>
        <div id="space_button">
          <ButtonGroup size={"large"}>
            {/* (runStatus.has(btnStatus) || pauseStatus.has(btnStatus))?(
                      <Button title="重启" icon="reload"   onClick={()=>{this.restartJob(this.state.viewId)}} />
                  ):(
                      <Button title="执行" icon="caret-right"   onClick={()=>{this.runJob(this.state.viewId)}} />
                  )*/}
            <Button
              title="执行"
              icon="caret-right"
              disabled={
                runStatus.has(btnStatus) || pauseStatus.has(btnStatus)
                  ? true
                  : false
              }
              onClick={() => {
                this.runJob(this.state.viewId);
              }}
            />
            <Button
              title="终止"
              icon="poweroff"
              disabled={runStatus.has(btnStatus) ? false : true}
              onClick={() => {
                this.stopJob(this.state.viewId);
              }}
            />
          </ButtonGroup>
        </div>
        <div id="space_switch">
          <Checkbox
            defaultChecked={spaceStyle === "workspace_contain1"}
            onChange={this.handleChangeStyle.bind(this)}
          >
            开启网格
          </Checkbox>
        </div>
        <JobDebug />
      </div>
    );
  }
}

export default connect(({ jobspace, jobheader }) => ({
  jobspace,
  jobheader
}))(Workspace);
