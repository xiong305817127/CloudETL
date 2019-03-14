/**
 * Created by Administrator on 2018/2/5.
 */
import React from 'react';
import { connect } from "dva";
import { Icon, Button, Checkbox, message, Modal, Radio } from 'antd';
import { runStatus, pauseStatus, getScreenSize } from '../../../../../constant';
import { defaultTransSettings, sourceTransConfig, targetTransConfig } from '../../../../../common/workspace.config';
import Tools from '../../../../../common/tools';
import styles from './index.less';
import { get_exec_pause, getTrans_exec_id, get_exec_stop, get_exec_resume } from 'services/quality';

import classnames from "classnames";
import DomItems from '../../../../../components/Modals/DomItems';
import AnalysisConfig from '../../../../../components/Modals/AnalysisConfig';

const ButtonGroup = Button.Group;
const confirm = Modal.confirm;

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
        }
    }

    componentDidMount() {
        const mainContent = this.refs.mainContent;
        const { dispatch } = this.props;
        const _this = this;

        const Instance = jsPlumb.getInstance({
            ...defaultTransSettings,
            Container: mainContent
        });

        Instance.registerConnectionType("basic", { anchor: "Continuous", connector: "StateMachine" });
        Instance.bind("connection", function (info) {
            const { shouldUpdate, lines, items } = _this.props.designSpace;

            if (!shouldUpdate) {
                dispatch({
                    type: "designSpace/addLine",
                    payload: {
                        sourceId: info.sourceId,
                        targetId: info.targetId,
                        enabled: true,
                        evaluation: true,
                        unconditional: true
                    }
                });
            }

            let args = info.connection.getOverlay("label").canvas.className.split(" ");
            info.connection.removeClass("errTransClass");

            for (let index of items) {
                if (index.id === info.sourceId && index.distributes) {
                    info.connection.getOverlay("label").canvas.className = args[0] + " " + "aLabelDistribute";
                }
            }
            for (let index of lines) {
                if (index.sourceId === info.sourceId && index.targetId === info.targetId && !index.unconditional) {
                    info.connection.getOverlay("label").canvas.className = args[0] + " " + "aLabelError";
                    info.connection.addClass("errTransClass");
                }
            }

            info.connection.getOverlay("label").canvas.onclick = function () {
                timer && clearTimeout(timer);
                timer = setTimeout(function () {
                    const { items, lines } = _this.props.designSpace;
                    let { supportsErrorHandling, distributes } = getItem(items, info.sourceId);
                    for (let index of lines) {
                        if (index.sourceId === info.sourceId && index.targetId === info.targetId && !index.unconditional) {
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

            info.connection.getOverlay("label").canvas.ondblclick = function () {
                timer && clearTimeout(timer);
                dispatch({
                    type: "designSpace/deleteLine",
                    payload: {
                        sourceId: info.sourceId,
                        targetId: info.targetId
                    }
                });
            }
        });
        Instance.bind("beforeDrop", function (_ref) {
            const { lines } = _this.props.designSpace;
            if (_ref.sourceId === _ref.targetId) {
                return false;
            }
            for (let index of lines) {
                if ((index.targetId === _ref.targetId && index.sourceId === _ref.sourceId) || (index.targetId === _ref.sourceId && index.sourceId === _ref.targetId)) {
                    return false
                }
            }
            return true;
        });

        this.setState({
            Instance
        })
    }

    componentDidUpdate() {
        const { shouldUpdate } = this.props.designSpace;
        if (shouldUpdate) {
            this.initItemsView(this.props.designSpace);
        }
    }

    //重新渲染界面
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
                    return
                }
                let el = this.refs[index.id];
                Instance.draggable(el, {
                    start: function (event) {
                    },
                    drag: function (event, ui) {
                        Instance.repaintEverything();
                    },
                    stop: function (event) {
                        dispatch({
                            type: "designSpace/moveStep",
                            payload: {
                                x: event.finalPos[0],
                                y: event.finalPos[1],
                                id: event.el.id
                            }
                        });
                        Instance.repaintEverything();
                    }
                });
                Instance.makeSource(el, { //设置连接的源实体，就是这一头
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
            type: "designSpace/save",
            payload: {
                shouldUpdate: false,
                itemsId: newItemsId
            }
        })
    }


    showConfirm(error, distributes, info, onlyCopy) {
        const { dispatch } = this.props;
        const that = this;

        confirm({
            title: '数据发送方式?',
            content: (
                <Radio.Group defaultValue={distributes ? "distribute" : "copy"} onChange={(e) => { that.setState({ linesState: e.target.value }) }}>
                    <Radio.Button value="copy">复制</Radio.Button>
                    {onlyCopy ? "" : <Radio.Button value="distribute">分发</Radio.Button>}
                    {error ? <Radio.Button value="error">错误处理步骤</Radio.Button> : ""}
                </Radio.Group>
            )
            ,
            okText: '确定',
            okType: 'default',
            cancelText: '关闭',
            onOk(close) {
                const { linesState } = that.state;
                let type = distributes ? "distribute" : "copy";
                if (type !== linesState) {
                    dispatch({
                        type: "designSpace/saveLine",
                        payload: {
                            id: info.sourceId,
                            sendType: linesState,
                            errorInfo: { sourceId: info.sourceId, targetId: info.targetId }
                        }
                    })
                }
                close();
            },
            onCancel() { }
        });
    }

    //运行转换
    runTrans() {
        const { name , items } = this.props.designSpace;
        const { dispatch } = this.props;

        if(items.some(val=>val.panel === "AnalysisReport")){
            dispatch({
                type: 'runAnalysis/openRunAnalysis',
                payload: {
                    visible: true,
                    actionName: name,
                    runType:"default"
                }
            });
        }else{
            message.info("请至少添加一个[报表生成]节点");
        }

    }

    //重启转换
    restartTrans(viewId) {
        const { name } = this.props.designSpace;
        const { dispatch } = this.props;
        message.success("转换正在重启，请耐心等候！", 5);
        getTrans_exec_id({ name }).then((res) => {
            const { code, data } = res.data;
            if (code === "200") {
                const { executionId } = data;
                if (executionId) {
                    get_exec_stop({ executionId }).then((res) => {
                        const { code } = res.data;
                        if (code === "200") {
                            dispatch({
                                type: 'transdebug/pauseDebug',
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
        })
    }

    //运行转换
    runDebugger(viewId) {
        const { name } = this.props.designSpace;
        const { dispatch } = this.props;

        dispatch({
            type: 'rundebugger/queryExecuteList',
            payload: {
                visible: true,
                actionName: name,
                viewId: viewId
            }
        });
    }


    //暂停转换
    pauseTrans() {
        const { name,status } = this.props.designSpace;
        const { dispatch } = this.props;

        getTrans_exec_id({ name }).then((res) => {
            const { code, data } = res.data;
            if (code === "200") {
                const { executionId } = data;
                if (status === "Paused") {
                    get_exec_resume({ executionId }).then((res) => {
                        const { code } = res.data;
                        if (code === "200") {
                            dispatch({ type:"analysisInfo/getStatus", payload:{ name } });
                            message.success("继续执行成功");
                        }
                    })
                } else {
                    get_exec_pause({ executionId }).then((res) => {
                        const { code } = res.data;
                        if (code === "200") {
                            message.success("执行已暂停");
                        }
                    });
                }
            }
        })
    }
    //终止转换
    stopTrans() {
        const { name } = this.props.designSpace;
        const { dispatch } = this.props;
        getTrans_exec_id({ name }).then((res) => {
            const { code, data } = res.data;
            if (code === "200") {
                const { executionId } = data;
                if (executionId) {
                    get_exec_stop({ executionId }).then((res) => {
                        const { code } = res.data;
                        if (code === "200") {
                            message.success("执行已终止");
                        }
                    });
                }
            }
        })
    }

    //完善后的功能

    //编辑节点集群配置
    editItemClusterSetting(stepName) {
        const { name } = this.props.designSpace;
        const { dispatch } = this.props;
        dispatch({
            type: 'analysisConfig/editConfig',
            payload: {
                transName: name,
                stepName
            }
        })
    }


    //编辑节点内容
    editItemContent(e, item) {
        e.preventDefault();
        const { name } = this.props.designSpace;
        const { dispatch } = this.props;
        if (item.panel === "UNKNOWN") {
            dispatch({
                type: 'domItems/save',
                payload: {
                    visible: true,
                    text: item.text,
                    panel: item.panel,
                    key: item.id
                }
            });
        } else {
            dispatch({
                type: 'domItems/editStep',
                payload: {
                    transname: name,
                    stepname: item.text
                },
                key: item.id
            });
        }
    }

    //编辑trans属性
    editTransConfig(e, name) {
        const { dispatch } = this.props;
        if (e.target === this.refs.mainContent) {
            dispatch({
                type: 'newAnalysis/showTransModel',
                payload: { name }
            })
        }
    }

    //删除节点
    deleteItem(item) {
        const { dispatch } = this.props;
        confirm({
            title: "确定删除此分析节点吗？",
            onOk() {
                dispatch({
                    type: "designSpace/deleteStep",
                    payload: { ...item }
                })
            }
        })
    }

    //切换背景
    handleChangeStyle(e) {
        const { dispatch } = this.props;
        dispatch({
            type: "designSpace/save",
            payload: {
                netStyle: e.target.checked
            }
        })
    }

    //添加节点
    addItem(e) {
        const { dispatch } = this.props;
        const type = e.dataTransfer.getData("type");
        if (!type) { return false; }
        let obj = getScreenSize();

        dispatch({
            type: 'designSpace/addNewItem',
            payload: {
                panel: type,
                text: Tools[type].text,
                x: e.clientX - 250 - obj.moveX,
                y: e.clientY - 162 - obj.moveY
            }
        })
    }

    render() {
        const { status, netStyle, viewId, items, name } = this.props.designSpace;

        console.log(items);

        return (
            <div className={classnames(styles.mainContent, {
                [styles.netStyle]: netStyle
            })}
                ref="mainContent"
                onDrop={e => { this.addItem(e) }} onDragOver={e => { e.preventDefault() }}
                onDoubleClick={(e) => { this.editTransConfig(e, name) }}
            >
                {
                    items.map(item => {
                        return (
                            <div className={styles.drop + " " + item.dragClass} ref={item.id}
                                onDoubleClick={(e) => { this.editItemContent(e, item) }}
                                id={item.id} key={item.id}
                                style={{ left: item.x, top: item.y }}  >
                                <div className={styles.canDrag}></div>
                                <div className={styles.domSet} onClick={() => { this.editItemClusterSetting(item.text) }} ></div>
                                <img className={styles.img} src={Tools[item.panel].imgData} draggable="false" />
                                <div className={styles.span}>{item.text}</div>
                                <Icon type="check-circle" className="checkCircle" />
                                <Icon onClick={() => { this.deleteItem(item) }} className={styles.close}
                                    type="close" />
                            </div>
                        )
                    })
                }
                <div className={styles.fucBtn} >
                    <div className={styles.fucBtn1}>
                        <Checkbox defaultChecked={netStyle} onChange={this.handleChangeStyle.bind(this)}>开启网格</Checkbox>
                    </div>
                    <div className={styles.fucBtn2}>
                        <ButtonGroup >
                            {/*<Button title="dubugger" icon="disconnect"  disabled ={ (runStatus.has(status) || pauseStatus.has(status))? true:false}  onClick={()=>{this.runDebugger(viewId)}} />
                                <Button title="预览这个转换" icon="eye"  disabled ={ (runStatus.has(status) || pauseStatus.has(status))? true:false}  onClick={()=>{this.runTrans(viewId)}} />*/}
                            {
                                /*(runStatus.has(status) || pauseStatus.has(status))?(
                                  <Button title="重启" icon="reload"   onClick={()=>{this.restartTrans(this.state.viewId)}} />
                                ):(
                                  <Button title="执行" icon="caret-right"   onClick={()=>{this.runTrans(viewId)}} />
                                )*/
                            }
                            <Button title="执行" icon="caret-right" disabled={(runStatus.has(status) || pauseStatus.has(status)) ? true : false} onClick={() => { this.runTrans(viewId) }} />
                            <Button title="暂停" icon={pauseStatus.has(status) ? "play-circle-o" : "pause-circle-o"} disabled={(pauseStatus.has(status) || runStatus.has(status)) ? false : true} onClick={() => { this.pauseTrans(status) }} />
                            <Button title="终止" icon="poweroff" disabled={(runStatus.has(status) || pauseStatus.has(status)) ? false : true} onClick={() => { this.stopTrans() }} />
                        </ButtonGroup>
                    </div>
                </div>
                {/* 子节点modal */}
                <DomItems />
                {/* 分析节点配置 */}
                <AnalysisConfig />
                {/* <TransDebug />
                <DbTable />
                <DomConfig /> */}
            </div >
        )
    }
}

export default connect(({ designSpace }) => ({
    designSpace
}))(Workspace)
