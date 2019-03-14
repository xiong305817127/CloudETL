/**
 * Created by Administrator on 2018/2/5.
 */
import React from "react";
import { connect } from "dva";
import { Icon, Button, Checkbox, message, Popover, Modal, Radio } from "antd";
import { runStatus, pauseStatus, getScreenSize } from "../../../constant";
import {
  defaultTransSettings,
  sourceTransConfig,
  targetTransConfig
} from "../../config/workspace.config";
const confirm = Modal.confirm;
const ButtonGroup = Button.Group;
import Tools from "../../config/Tools";
import TransDebug from "./TransDebug/TransDebug";
import DbTable from "../Newtrans/DbTable";
import DomConfig from "../Newtrans/DomConfig";
import Style from "./Workspace.css";

const getItem = (items, id) => {
  for (let index of items) {
    if (index.id === id) {
      return index;
    }
  }
};

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
      ...defaultTransSettings,
      Container: mainContent
    });

    Instance.registerConnectionType("basic", {
      anchor: "Continuous",
      connector: "StateMachine"
    });
    Instance.bind("connection", function(info) {
      const { name, shouldUpdate, lines, items } = _this.props.transspace;

      if (!shouldUpdate) {
        dispatch({
          type: "transspace/addHop",
          payload: {
            transname: name,
            start: info.sourceId,
            target: info.targetId,
            isTrans: true,
            enabled: true,
            evaluation: true,
            unconditional: true
          }
        });
      }

      let args = info.connection
        .getOverlay("label")
        .canvas.className.split(" ");
      info.connection.removeClass("errTransClass");

      for (let index of items) {
        if (index.id === info.sourceId && index.distributes) {
          info.connection.getOverlay("label").canvas.className =
            args[0] + " " + "aLabelDistribute";
        }
      }
      for (let index of lines) {
        if (
          index.sourceId === info.sourceId &&
          index.targetId === info.targetId &&
          !index.unconditional
        ) {
          info.connection.getOverlay("label").canvas.className =
            args[0] + " " + "aLabelError";
          info.connection.addClass("errTransClass");
        }
      }

      info.connection.getOverlay("label").canvas.onclick = function() {
        timer && clearTimeout(timer);
        timer = setTimeout(function() {
          const { items, lines } = _this.props.transspace;
          let { supportsErrorHandling, distributes } = getItem(
            items,
            info.sourceId
          );
          for (let index of lines) {
            if (
              index.sourceId === info.sourceId &&
              index.targetId === info.targetId &&
              !index.unconditional
            ) {
              return;
            }
          }

          let i = 0;
          for (let index of lines) {
            if (index.sourceId === info.sourceId) {
              i++;
            }
          }
          let onlyCopy = i <= 1;
          _this.showConfirm(supportsErrorHandling, distributes, info, onlyCopy);
        }, 300);
      };

      info.connection.getOverlay("label").canvas.ondblclick = function() {
        timer && clearTimeout(timer);
        dispatch({
          type: "transspace/deleteHop",
          payload: {
            sourceId: info.sourceId,
            targetId: info.targetId
          }
        });
      };
    });
    Instance.bind("beforeDrop", function(_ref) {
      const { lines } = _this.props.transspace;
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
    const { shouldUpdate } = this.props.transspace;

    if (shouldUpdate) {
      this.initItemsView(this.props.transspace);
    }
  }

  componentWillReceiveProps(nextProps) {
    console.log(nextProps.transspace, "更新name");
  }

  showConfirm(error, distributes, info, onlyCopy) {
    const { dispatch } = this.props;
    const that = this;

    confirm({
      title: "数据发送方式?",
      content: (
        <Radio.Group
          defaultValue={distributes ? "distribute" : "copy"}
          onChange={e => {
            that.setState({ linesState: e.target.value });
          }}
        >
          <Radio.Button value="copy">复制</Radio.Button>
          {onlyCopy ? "" : <Radio.Button value="distribute">分发</Radio.Button>}
          {error ? <Radio.Button value="error">错误处理步骤</Radio.Button> : ""}
        </Radio.Group>
      ),
      okText: "确定",
      okType: "default",
      cancelText: "关闭",
      onOk(close) {
        const { linesState } = that.state;
        let type = distributes ? "distribute" : "copy";
        if (type !== linesState) {
          dispatch({
            type: "transspace/saveLine",
            payload: {
              id: info.sourceId,
              sendType: linesState,
              errorInfo: { sourceId: info.sourceId, targetId: info.targetId }
            }
          });
        }
        close();
      },
      onCancel() {}
    });
  }

  initItemsView(props) {
    const { Instance } = this.state;
    const { dispatch } = this.props;

    const { items, lines, itemsId } = props;
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
              type: "transspace/moveStep",
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
          ...sourceTransConfig,
          filter: "." + el.firstChild.className,
          Container: Instance.getContainer()
        });
        Instance.makeTarget(el, {
          ...targetTransConfig,
          Container: Instance.getContainer()
        });
        Instance.fire("jsPlumbDemoNodeAdded", el);
      });
      (lines || []).map(index => {
        Instance.connect({ source: index.sourceId, target: index.targetId });
      });
    }

    dispatch({
      type: "transspace/updateData",
      payload: {
        shouldUpdate: false,
        itemsId: newItemsId
      }
    });
  }

  //编辑节点内容
  editItemContent(e, item) {
    e.preventDefault();

    const { name } = this.props.transspace;
    const { dispatch } = this.props;

    if (item.panel === "UNKNOWN") {
      dispatch({
        type: "items/show",
        visible: true,
        text: item.text,
        panel: item.panel,
        key: item.id
      });
    } else {
      this.props.transheader.methods.edit_step({
        transname: name,
        stepname: item.text
      }).then(res => {
        const { code, data } = res.data;
        if (code === "200") {
          const {
            type,
            description,
            stepParams,
            nextStepNames,
            prevStepNames
          } = data;
          dispatch({
            type: "items/show",
            visible: true,
            transname: name,
            text: item.text,
            panel: type,
            description: description,
            config: stepParams,
            nextStepNames: nextStepNames,
            prevStepNames: prevStepNames,
            key: item.id
          });
        }
      });
    }
  }
  //复制节点
  handleCopyItem(item) {
    const { name } = this.props.transspace;
    const { dispatch } = this.props;

    item.viewName = name;
    dispatch({
      type: "designplatform/copyCache",
      payload: {
        copyTransItme: item
      }
    });
  }
  //编辑节点集群配置
  editItemClusterSetting(text) {
    const { name } = this.props.transspace;
    const { dispatch } = this.props;

    dispatch({
      type: "domconfig/query",
      obj: {
        transName: name,
        stepName: text
      }
    });
  }
  //删除节点
  deleteItem(item) {
    const { name } = this.props.transspace;
    const { dispatch } = this.props;
    dispatch({
      type: "tip/deleteItem",
      visible: true,
      id: item.id,
      text: item.text,
      transname: name,
      status: "trans"
    });
  }
  //编辑trans属性
  editTransConfig(e, name, viewId) {
    const { dispatch, viewStatus } = this.props;
    if (e.target === this.refs.mainContent) {
      dispatch({
        type: "newtrans/showTransModel",
        payload: {
          info_name: name,
          viewId: viewId,
          status: viewStatus
        }
      });
    }
  }
  //添加节点
  addItem(e, type) {
    if (!e.dataTransfer.getData("type")) {
      return false;
    }
    let obj = getScreenSize();

    this.props.dispatch({
      type: "transspace/addNewItem",
      payload: {
        panel: e.dataTransfer.getData("type"),
        text: Tools[e.dataTransfer.getData("type")].text,
        x: e.clientX - 255 - obj.moveX,
        y: e.clientY - 155 - obj.moveY,
        imgUrl: Tools[e.dataTransfer.getData("type")].imgUrl
      }
    });
  }
  //切换背景
  handleChangeStyle(e) {
    const { dispatch } = this.props;
    dispatch({
      type: "transspace/updateData",
      payload: {
        spaceStyle: e.target.checked
          ? "workspace_contain1"
          : "workspace_contain2"
      }
    });
  }

  //运行转换
  runTrans(viewId) {
    const { name } = this.props.transspace;
    const { dispatch } = this.props;
    dispatch({
      type: "runtrans/queryExecuteList",
      payload: {
        visible: true,
        actionName: name,
        viewId: viewId
      }
    });
  }

  //重启转换
  restartTrans(viewId) {
    const { name } = this.props.transspace;
    const { dispatch } = this.props;
    message.success("转换正在重启，请耐心等候！", 5);
    this.props.transheader.methods.getTrans_exec_id({name}).then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        const { executionId } = data;
        if (executionId) {
          this.props.transheader.methods.get_exec_stop({executionId}).then(res => {
            const { code } = res.data;
            if (code === "200") {
              dispatch({
                type: "transdebug/pauseDebug",
                payload: {
                  transName: name,
                  visible: "block"
                }
              });
              this.runTrans(viewId);
            }
          });
        }
      }
    });
  }

  //运行转换
  runDebugger(viewId) {
    const { name } = this.props.transspace;
    const { dispatch } = this.props;

    dispatch({
      type: "rundebugger/queryExecuteList",
      payload: {
        visible: true,
        actionName: name,
        viewId: viewId
      }
    });
  }

  //预览步骤
  preViewsTrans() {
    const { name } = this.props.transspace;
    const { dispatch } = this.props;

    this.props.transheader.methods.getDebugPreviewData({ transName: name }).then(res => {
      console.log(res.data, "得到的数据");
    });
  }

  //暂停转换
  pauseTrans(viewId, transStatus) {
    const { name } = this.props.transspace;
    const { dispatch } = this.props;

    this.props.transheader.methods.getTrans_exec_id({name}).then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        const { executionId } = data;
        if (transStatus === "Paused") {
          this.mothods.get_exec_resume(executionId).then(res => {
            const { code } = res.data;
            if (code === "200") {
              dispatch({
                type: "transdebug/pauseDebug",
                payload: {
                  transName: name,
                  viewId: viewId,
                  visible: "block"
                }
              });
              message.success("继续执行成功");
            }
          });
        } else {
          this.props.transheader.methods.get_exec_pause({executionId}).then(res => {
            const { code } = res.data;
            if (code === "200") {
              message.success("执行已暂停");
            }
          });
        }
      }
    });
  }
  //终止转换
  stopTrans() {
    const { name } = this.props.transspace;
    const { dispatch } = this.props;
    this.props.transheader.methods.getTrans_exec_id(name).then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        const { executionId } = data;
        if (executionId) {
          this.mothods.get_exec_stop(executionId).then(res => {
            const { code } = res.data;
            if (code === "200") {
              dispatch({
                type: "transdebug/pauseDebug",
                payload: {
                  transName: name,
                  visible: "block"
                }
              });
              message.success("执行已终止");
            }
          });
        }
      }
    });
  }

  render() {
    const {
      btnStatus,
      spaceStyle,
      viewId,
      items,
      name
    } = this.props.transspace;
    console.log(items);

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
            this.editTransConfig(e, name, viewId);
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
                  <div
                    className={Style.domSet}
                    onClick={() => {
                      this.editItemClusterSetting(item.text);
                    }}
                  />
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
            <ButtonGroup>
              {/*<Button title="dubugger" icon="disconnect"  disabled ={ (runStatus.has(btnStatus) || pauseStatus.has(btnStatus))? true:false}  onClick={()=>{this.runDebugger(viewId)}} />

              {               
                /*(runStatus.has(btnStatus) || pauseStatus.has(btnStatus))?(
                  <Button title="重启" icon="reload"   onClick={()=>{this.restartTrans(this.state.viewId)}} />
                ):(
                  <Button title="执行" icon="caret-right"   onClick={()=>{this.runTrans(viewId)}} />
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
                  this.runTrans(viewId);
                }}
              />
              <Button
                title="暂停"
                icon={
                  pauseStatus.has(btnStatus)
                    ? "play-circle-o"
                    : "pause-circle-o"
                }
                disabled={
                  pauseStatus.has(btnStatus) || runStatus.has(btnStatus)
                    ? false
                    : true
                }
                onClick={() => {
                  this.pauseTrans(viewId, btnStatus);
                }}
              />
              <Button
                title="终止"
                icon="poweroff"
                disabled={
                  runStatus.has(btnStatus) || pauseStatus.has(btnStatus)
                    ? false
                    : true
                }
                onClick={() => {
                  this.stopTrans();
                }}
              />
            </ButtonGroup>
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
        <TransDebug />
        <DbTable />
        <DomConfig />
      </div>
    );
  }
}

export default connect(({ transspace,transheader}) => ({
  transspace,transheader
}))(Workspace);
