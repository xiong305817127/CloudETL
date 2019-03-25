/**
 * 新增质量分析模块model,主要控制整个子模块的store
 * @author pwj  2018/09/27
 */
import { DEFAULT_PAGESIZE, DEFAULT_PAGE, ERROR_POINT } from "./constant";
import { getTransList } from "services/quality";
import { message } from "antd";
 
const initState = {
    //分析列表
    analysisList: [],
    //加载状态
    loading: false,
    //分析列表总条数
    total: 0,
    //选择的分析
    selectedRows: [],
    //执行方式 default 默认执行  batch批量执行
    runType: "default"
}

export default {
    namespace: "qualityAnalysis",

    state: { ...initState },

    subscriptions: {
        steup({ history, dispatch }) {
            return history.listen(({ pathname, query }) => {
                if (pathname === "/gather/qualityAnalysis/taskList") {
                    dispatch({
                        type: "getTransList",
                        payload: {
                            ...query,
                            search: query.search ? decodeURIComponent(query.search) : "",
                            page: query.page || DEFAULT_PAGE,
                            pageSize: query.pageSize || DEFAULT_PAGESIZE
                        }
                    })
                }
            })
        }
    },
    effects: {
        *getTransList({ payload }, { put, call }) {
            yield put({ type: "save", payload: { loading: true } })
            const { data } = yield call(getTransList, { ...payload });
            const { code } = data;
            if (code === "200") {
                const { total, rows } = data.data;
                yield put({
                    type: "save",
                    payload: { analysisList: rows, total, loading: false }
                })
            }
        },
        *queryBatchStop({ }, { select, call, put }) {
            const { selectedRows } = yield select(state=>state.qualityAnalysis);
            let args = [];
            for (let index of selectedRows) {
                args.push(`${index.group}/${index.key}`)
            }
            const { data } = yield call(batchTransStop, args);
            
            const { code } = data;
            if (code === "200") {
                message.success("批量停止成功，请稍后刷新页面！");
            }
        },
    },
    reducers: {
        save(state, action) {
            return { ...state, ...action.payload }
        }
    }
}