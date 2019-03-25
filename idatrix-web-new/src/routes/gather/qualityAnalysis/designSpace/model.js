/**
 * Created by Administrator on 2017/8/29.
 */
import { message } from 'antd';
import { getDelete_trans, delete_hop, delete_step, addLine, getTrans_list, getOpen_trans, add_step, move_step } from 'services/quality';
import Tools from '../common/tools';

/**
 * 格式化节点，并返回数组
 * @param doms 节点数组
 * @returns {Array}
 */
const fromatItems = (doms) => {
    let args = [];
    for (let index of doms) {
        if (index) {
            let type = index.type;
            if (!Tools[type] || Tools[type].typeFeil != "trans") {
                type = "UNKNOWN";
            }
            args.push({
                id: jsPlumbUtil.uuid(),
                panel: type,
                text: index.name,
                imgUrl: Tools[type].imgUrl,
                config: Tools[type].config,
                dragClass: "dragNormal",
                x: parseInt(index.gui.xloc),
                y: parseInt(index.gui.yloc),
                distributes: index.distributes,
                supportsErrorHandling: index.supportsErrorHandling
            });
        }

    }
    return args;
};
/**
 * 格式化线条
 * @param items
 * @param lines
 * @returns {Array}
 */
const fromatLines = (items, lines) => {
    let args = [];
    for (var index of lines) {
        let obj = {};
        obj.sourceId = getItem(items, index.from).id;
        obj.targetId = getItem(items, index.to).id;
        obj.enabled = index.enabled;
        obj.evaluation = index.evaluation;
        obj.unconditional = index.unconditional;
        args.push(obj);
    }
    return args;
};
/**
* 根据名字返回节点数组中的对象
* @param items  节点数组
* @param name   名字
* @returns {*}
*/
const getItem = (items, name) => {
    for (var index of items) {
        if (index.text === name) {
            return index;
        }
    }
};
/**
 * 根据已存在名字，生成新名字
 * @param text
 * @returns {*}
 */
const getNewName = (text, elems) => {
    let nameArgs = [];
    let newName = text;
    let i = 1;
    for (let index of elems) {
        nameArgs.push(index.text)
    }

    while (nameArgs.includes(newName)) {
        newName = text + " " + i++;
    }
    return newName;
};
/**
 * 根据id，返回节点的名字
 * @param items  节点数组
 * @param id      节点ID
 */
const getItemName = (items, id) => {
    for (let index of items) {
        if (index.id === id) {
            return index.text
        }
    }
};




const initState = {
    //视图状态  false欢迎页  true 设计页
    view: false,
    //已经打开的列表
    activeArgs: [],
    //分析任务列表
    taskList: [],
    //当前打开的名字
    name: "",
    //要删除的名字
    removeKey: "",
    //弹窗展示
    modelVisible: false,
    //页面的组件集合
    items: [],
    //页面的线条集合
    lines: [],
    //元素id的集合防止重复渲染
    itemsId: [],
    //按钮初始化风格
    status: "",
    //是否需要更新
    shouldUpdate: false,
    //切换主题风格 false  默认背景  true 网格背景
    netStyle: false,
};

export default {
    namespace: 'designSpace',
    state: { ...initState },
    reducers: {
        clear(state){
            state.activeArgs.splice(0);
            state.taskList.splice(0);
            state.items.splice(0);
            state.lines.splice(0);
            state.itemsId.splice(0);
            return {...state,...initState}
        },
        save(state, action) {
            return { ...state, ...action.payload }
        },
        //更新转换的名字
        updateName(state, action) {
            const { name, newName } = action.payload;
            return {
                ...state, name: newName,
                taskList: state.taskList.map(index => index === name ? newName : index),
                activeArgs: state.activeArgs.map(index => index === name ? newName : index)
            }
        },
        //改变节点名字
        changeItemName(state, action) {
            const { key, newName } = action.payload;
            return {
                ...state,
                shouldUpdate: true,
                items: state.items.map((index) => {
                    if (index.id === key) {
                        index.text = newName
                    }
                    return index;
                })
            };
        },
        //执行结束时，渲染结果
        updateStepStatus(state, action) {
            const { style, StepStatus, status } = action.payload;
            const { items } = state;
            let newItems = items.map(index => {
                index.dragClass = style;
                return index;
            })
            for (let index of StepStatus) {
                if (index && index.errCount > 0) {
                    let item = getItem(newItems, index.stepName)
                    if (item) {
                        item.dragClass = "errorStyle";
                    }
                }
            }
            return { ...state, status, items: newItems }
        }
    },
    subscriptions: {
        setup({ history, dispatch }) {
            history.listen(({ pathname }) => {
                if (pathname === "/gather/qualityAnalysis/designSpace") {
                    dispatch({ type: "queryTransList" });
                }
            })
        }
    },
    effects: {
        //查询任务列表
        *queryTransList({ }, { select, put }) {
            const { name } = yield select(state=>state.designSpace);
            const { data } = yield getTrans_list({ isOnlyName: true });
            const { code } = data;
            if (code === "200") {
                yield put({
                    type: 'save',
                    payload: {
                        taskList: data.data
                    }
                });
            }
            if(name && name.trim()){
                yield put({type:"openAnalysis",payload:{ name }})
            }
        },
        //打开分析任务
        *openAnalysis({ payload }, { select, call, put }) {
            const { data } = yield call(getOpen_trans, { ...payload });
            const { activeArgs } = yield select(state => state.designSpace);
            const { code } = data;
            if (code === "200") {
                const { stepList, hopList } = data.data;
                let items = stepList ? fromatItems(stepList) : [];
                let lines = hopList ? fromatLines(items, hopList) : [];
                if (!activeArgs.includes(payload.name)) {
                    activeArgs.push(payload.name);
                }
                yield put({
                    type: "save",
                    payload: {
                        activeArgs, items, lines, view: true, shouldUpdate: true, itemsId: [],
                        ...payload
                    }
                });
                yield put({ type:"analysisInfo/getStatus",payload })
            }
        },
        //添加节点
        *addNewItem({ payload }, { select, call, put }) {
            const { items, name } = yield select(state => state.designSpace);
            payload.text = getNewName(payload.text, items);

            let obj1 = {};
            obj1.transName = name;
            obj1.stepName = payload.text;
            obj1.stepType = payload.panel;
            const data1 = yield call(add_step, obj1);
            if (data1.data.code === "200") {
                const { distributes, supportsErrorHandling } = data1.data.data;
                items.push({
                    ...payload,
                    distributes,
                    supportsErrorHandling,
                    dragClass: "dragNormal",
                    id: jsPlumbUtil.uuid()
                });
                yield put({ type: "save", payload: { shouldUpdate: true, items } });
                obj1.xloc = payload.x;
                obj1.yloc = payload.y;
                yield call(move_step, obj1);
            }
        },
        //移动步骤
        *moveStep({ payload }, { select, call, put }) {
            const { name, items } = yield select(state => state.designSpace);
            const { data } = yield call(move_step, { xloc: payload.x, yloc: payload.y, transName: name, stepName: getItemName(items, payload.id) });
            const { code } = data;
            items.map(index => {
                if (index.id === payload.id) {
                    index.x = payload.x;
                    index.y = payload.y
                }
            });
            if (code === "200") {
                yield put({ type: "save", payload: { items } })
            }
        },
        //添加连线
        *addLine({ payload }, { select, call, put }) {
            const { items, lines, name } = yield select(state => state.designSpace);
            lines.push(payload);
            let obj = {
                name,
                from: getItemName(items, payload.sourceId),
                to: getItemName(items, payload.targetId),
                enabled: true, evaluation: true,
                unconditional: true, isTrans: true
            };
            const { data } = yield call(addLine, obj);
            const { code } = data;
            if (code === "200") {
                yield put({
                    type: "save",
                    payload: { lines }
                });
            }
        },
        //更新连线属性
        *saveLine({ payload }, { select, call, put }) {
            const { items, lines, name } = yield select(state => state.designSpace);
            const {owner} = yield select(state=>state.transheader);
            const { sendType, errorInfo } = payload;

            if (sendType === "error") {
                let obj = {
                    name,
                    from: getItemName(items, errorInfo.sourceId),
                    to: getItemName(items, errorInfo.targetId),
                    isTrans: true,
                    enabled: true,
                    evaluation: true,
                    unconditional: false
                };
                const { data } = yield call(addLine, obj);
                const { code } = data;
                if (code === "200") {
                    for (let index of lines) {
                        if (index.sourceId === errorInfo.sourceId && index.targetId === errorInfo.targetId) {
                            index.unconditional = false
                        } else {
                            index.unconditional = true
                        }
                    }
                }
            } else {
                let obj = {
                    "transName": name,
                    "stepName": getItemName(items, errorInfo.sourceId),
                    "configs": {
                        "clusterSchema": "##ignore##",// 集群配置
                        "distribute": sendType != "copy"
                    }
                };
                const { data } = yield call(save_stepConfigs, { ...obj, owner });
                const { code } = data;

                if (code === "200") {
                    for (let index of items) {
                        if (index.id === errorInfo.sourceId) {
                            index.distributes = sendType !== "copy";
                        }
                    }
                }
            }
            yield put({
                type: "save",
                payload: {
                    lines, items, shouldUpdate: true
                }
            })
        },
        //删除连线
        *deleteLine({ payload }, { select, call, put }) {
            const { name, items, lines } = yield select(state => state.designSpace);
            let obj = {
                name,
                from: getItemName(items, payload.sourceId),
                to: getItemName(items, payload.targetId),
                isTrans: true
            };
            const { data } = yield call(delete_hop, obj);
            const { code } = data;

            if (code === "200") {
                yield put({
                    type: "save",
                    payload: {
                        lines: lines.filter(index => {
                            return !(index.targetId === payload.targetId && index.sourceId === payload.sourceId)
                        }),
                        shouldUpdate: true
                    }
                })
            }
        },
        //删除步骤
        *deleteStep({ payload }, { select, call, put }) {
            const { name, items, lines } = yield select(state => state.designSpace);
            const { data } = yield call(delete_step, { "transName": name, "stepName": payload.text });
            const { code } = data;

            if (code === "200") {
                const newItems = items.filter(index => index.id !== payload.id);
                const newLines = lines.filter(index => index.sourceId !== payload.id && index.targetId !== payload.id);
                yield put({
                    type: "save",
                    payload: {
                        shouldUpdate: true, items: newItems, lines: newLines
                    }
                });
                message.success("删除成功");
            } else {
                message.success("删除失败，请重试");
            }
        },
        //删除Tabs
        *deleteTabs({ payload }, { select, call, put }) {
            const { removeKey } = yield select(state => state.designSpace);
            const { data, taskList } = yield call(getDelete_trans, { name: removeKey });
            const { code } = data;
            if (code === "200") {
                yield put({ type: "closeTabs" });
                yield put({ type: "save", payload: { taskList: taskList.filter(index => index !== payload.name) } })
                message.success("删除成功");
            }
        },
        //关闭tabs
        *closeTabs({ payload }, { select, call, put }) {
            const { removeKey, name, activeArgs } = yield select(state => state.designSpace);
            //新activeKey名字
            let newName = name;
            let newArgs = activeArgs.filter(index => index !== removeKey);
            let newView = true;

            if (removeKey === name && newArgs.length > 0) {
                newName = newArgs[newArgs.length - 1];
                yield put({ type: "save", payload: { activeArgs: newArgs, modelVisible: false, name: newName } })
                yield put({ type: "openAnalysis", payload: { name: newName } })
            } else {
                if (removeKey === name) {
                    newName = "";
                    newView = false;
                }
                yield put({
                    type: "save",
                    payload: { name: newName, activeArgs: newArgs, view: newView, modelVisible: false }
                })
            }
        }
    }
};
