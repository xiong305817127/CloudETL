import { message } from 'antd';
import { get_TransRecords, get_TransLog } from 'services/quality';

export default {
    namespace: 'analysisDetails',
    state: {

        //展示日志列表
        visible: false,

        //日志展示
        visible1: false,

        //查看执行配置
        visible2: false,

        //选择时间查看日志
        visible3: false,
        record: {},

        excute: {},
        records: [],
        logs: "",
        name: ""
    },
    reducers: {
        save(state, action) {
            return { ...state, ...action.payload }
        }
    },
    effects: {
        *queryTransHistory({ payload }, { call, put }) {
            const { data } = yield call(get_TransRecords, { ...payload });
            const { code } = data;
            if (code === "200") {
                yield put({
                    type: 'save',
                    payload: {
                        records: data.data.records,
                        name: payload.name,
                        visible: true
                    }
                });
            }
        },
        *queryTransLog({ payload }, { call, put }) {
            const { data } = yield call(get_TransLog, { ...payload });
            console.log(data);
            const { code } = data;
            if (code === "200") {
                yield put({
                    type: 'save',
                    payload: {
                        logs: decodeURIComponent(data.data.logs),
                        visible1: true
                    }
                });
            }
        }
    }

};

