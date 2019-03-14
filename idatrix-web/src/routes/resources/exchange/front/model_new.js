/**
 * 重写frontModel
 * 
 * author pwj 2018/9/22
 */
import {
    deleteTerminalManageRecordById, getTerminalManageRecordsByCondition, saveOrUpdateTerminalManage, getTerminalManageRecordById,
    getDeptServer, getFSDatabase, getFSSftp, isExistedTerminalManageRecord
} from 'services/DirectoryOverview';
import { getDbSchemasByDsId } from 'services/metadataCommon';
import { databaseType } from "config/jsplumb.config"
import { message } from 'antd';

const initState = {
    //弹框状态 false 新增 true 编辑
    status:false,
    //表格加载状态
    loading: false,
    //表格数据
    dataSource: [],
    //总数据量
    total: 0,
    //弹框显示
    visible: false,
    //参数
    config: {},
    //前置机信息
    frontEndServer:{},
    //前置机列表
    frontList: [],
    //数据库列表
    databaseList: [],
    //schema列表
    schemaList: [],
    //用户列表
    userList: [],
    //部门名称
    deptName:"",
}

export default {
    namespace: 'frontModel',
    state: { ...initState },
    reducers: {
        save(state, action) {
            return { ...state, ...action.payload }
        },
        reset(state,action){
            return {...initState}
        },
        hide(state, action) {
            return { ...state, ...action.payload,visible:false }
        },
    },
    subscriptions: {
        setup({ history, dispatch }) {
            return history.listen(({ pathname, query }) => {
                if (pathname === "/resources/exchange/front") {
                    dispatch({
                        type: "getList",
                        payload: {
                            ...query,
                            pageNum: query.page ? query.page : 1,
                            pageSize: query.pageSize ? query.pageSize : 10
                        }
                    });
                    dispatch({ type: "resourcesCommon/getDepartments" })
                }
            })
        }
    },

    effects: {
        *getList({ payload }, { call, put }) {
            yield put({ type: "save", payload: { loading: true } });
            const { data } = yield call(getTerminalManageRecordsByCondition, { ...payload });
            const { code } = data;
            if (code === "200") {
                yield put({
                    type: "save",
                    payload: {
                        dataSource: data.data && data.data.results ? data.data.results : [],
                        total: data.data && data.data.total ? data.data.total : 0,
                        loading: false
                    }
                })
            }
        },
        /**
         *根据部门id, 获取服务器列表
         * @param {Number} payload(部门id)
         * author pwj
         */
        *getDeptServer({ payload }, { put, call,select }) {
            const { data } = yield call(getDeptServer, payload);
            const { config } = yield select(state=>state.frontModel);
            const { code } = data;
            if (code === "200") {
                delete config.tmDBPort;
                delete config.tmDBType;
                yield put({
                    type: "save",
                    payload: {
                        frontList: data && data.data ? data.data : [],
                        config:{...config}
                    }
                })
            }
        },
        /**
         * 根据服务器id,获取数据库列表
         * @param {Number} payload(服务器id) 
         * author pwj
         */
        *getFSDatabase({ payload }, { put, call }) {
            const { data } = yield call(getFSDatabase, payload);
            const { code } = data;
            if (code === "200") {
                yield put({
                    type: "save",
                    payload: {
                        databaseList: data && data.data ? data.data : []
                    }
                })
            }
        },
        /**
         * 根据服务器id,获取数据库用户信息
         * @param {Number} payload(服务器id) 
         * author pwj
         */
        *getFSSftp({ payload }, { put, call,select }) {
            const { config } = yield select(state=>state.frontModel);
            const { data } = yield call(getFSSftp, payload);
            const { code } = data;
            if (code === "200") {
                let info = {};
                if(data && data.data && data.data.length>0){
                    info = data.data[data.data.length-1].frontEndServer;
                }

                yield put({
                    type: "save",
                    payload: {
                        userList: data && data.data ? data.data : [],
                        config:{...config,tmDBPort:info.dbPort ,
                            tmDBType:databaseType.filter(index=>index.value === info.dsType)[0].name  
                        }
                    }
                })
            }
        },
        /**
         * 获取schema列表
         * @param {Number} payload(数据库ID)
         *  Author pwj 
         */
        *getDbSchemasByDsId({ payload }, { call, put }) {
            const { data } = yield call(getDbSchemasByDsId,payload);
            const { code,msg } = data;
            if (code === "200") {
                let arge=[];
                for (var i in data.data){
                     arge.push({
                        name:data.data[i]
                     })
                  }
                yield put({
                    type: "save",
                    payload: {
                        schemaList: arge
                    }
                })
            }
        },
        /**
         * 
         * @param {Number} payload() 
         * @param {*} param1 
         */
        *saveOrUpdateTerminalManage({ payload },{ call,put }){
            const { data } = yield call(saveOrUpdateTerminalManage,payload);
             const { code } = data;

              if(code === 200){
                    message.success("保存成功");
                    yield put({type:"save",payload:{dataSource:payload}});
              }
        }
    }
};
