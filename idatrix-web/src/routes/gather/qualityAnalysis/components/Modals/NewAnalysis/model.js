import { getTransList, editTransAttributes, saveTransAttributes,newTrans } from "services/quality";
import { message } from "antd";
import { routerRedux } from "dva/router";
 
const initState = {
    //是否显示
    visible: false,
    //初始动作 new新建  edit编辑
    actionType: "new",
    //已存在的分析名
    nameArgs: [],
    //初始名字
    name: "",
    //配置参数
    params: {},
    //描述
    description: ""
}


export default {
    namespace: 'newAnalysis',
    state: { ...initState },
    reducers: {
        save(state, action) {
            return { ...state, ...action.payload }
        },
        reset() {
            return { ...initState }
        }
    },
    effects: {
        //新建分析
        *getNewModel({ }, { call, put }) {
            const { data } = yield call(getTransList, { isOnlyName: true });
            const { code } = data;
            if (code === "200") {
                yield put({
                    type: 'save',
                    payload: {
                        actionType: "new",
                        visible: true,
                        nameArgs: data.data
                    }
                });
            }
        },
        //编辑分析
        *showTransModel({ payload }, { call, put }) {
            const { data } = yield call(editTransAttributes, { ...payload });
            const { code } = data;
            if (code === "200") {
                const { description, params } = data.data;
                yield put({
                    type: 'save',
                    payload: {
                        ...payload,
                        description: description ? description : "",
                        params: params,
                        visible: true,
                        actionType: "edit"
                    }
                });
            }
        },
        //新建分析任务
        *newAnalysis({ payload }, { select, call, put }) {
            const { data } = yield call(newTrans,{...payload, group: "default"});
            const { activeArgs } = yield select(state => state.designSpace);
            activeArgs.push(payload.name);
            const { code } = data;
            if (code === "200") {
                message.success("新建分析任务成功");
                yield put({ type:"reset" });
                yield put({ type: "designSpace/save", payload: { name: payload.name } });
                yield put(routerRedux.push("/gather/qualityAnalysis/designSpace"));
            }
        },
        //保存分析
        *saveTransAttributes({ payload }, { select, call, put }) {
            const { name } = yield select(state => state.newAnalysis);
            let newName = "";
            if (name !== payload.name) {
                newName = payload.name;
            }
            const { data } = yield call(saveTransAttributes, { ...payload, name, newName });
            const { code } = data;
            if (code === "200") {
                message.success("保存成功！");
                if (newName) {
                    yield put({
                        type: "designSpace/updateName",
                        payload: { name, newName }
                    })
                }
                yield put({ type: "reset" });
            }
        }
    }
};

