/**
 * Created by Steven Leo on 2018/10/10.
 */
import { get_TransRecords,getAnalysisReports , getAnalysisReportsByNode  } from 'services/quality';
import Immutable from "immutable"

/**
 * 此model为配置分析报告
 * 1. 页面加载时，注册报告列表：getRecordList
 * 2. 以上第一步也可以通过：getRecordInfo 获取，参数不同，但是数据格式相同
 * 3. 点击流程详细清单时：getResultInfo
 * 以上三种报告可以通过 "List", "Record", "Result"三种配置来调用
 * 如果没有设置则会获取数据异常
 */

// 页面初始化tag
// 防止重新刷新
let tag = 0;

// 初始化数据
const initialState = Immutable.fromJS({

  // selectedId用于获取当前指定的执行历史记录的id下的节点报告
  // 此id可以通过history来捕获
  execId: "",
  nodeList: [],

  // 默认开启第一个node
  // 用户展开第二个node开始，重新发起请求，但第一次数据不变
  selectedNodes: [],

  // 节点详细数据，使用对象存储
  nodeFullData: {},

  //@edited by pwj
  // 分析列表
  analysisList:[],

  //分析名称
  name:""

  // PS：以上数据离开页面时，清空
});

export default {
  namespace: "analysisReports",
  state: initialState,

  // 注册页面路由
  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query }) => {
        if (pathname === "/gather/qualityAnalysis/report") {
          // 获取当前页面的queryid
          dispatch({
            type: "getAnalysisList",
            payload: { ...query,name: decodeURIComponent(query.name) }
          });
        };
        if (pathname === "/gather/qualityAnalysis/report/reportChart") {
          // 获取当前页面的queryid
          tag = 1
          dispatch({
            type: "getData",
            option: {
              execId: query.execId
            }
          });
        }else if(tag === 1){

          console.log("清空报告！！！");
          // 清除标记
          tag = 0
          dispatch({
            type: "clear"
          });
        }
      });
    }
  },

  effects: {
    /**
     * 获取可用于生成分析报告的分析列表
     * @edited by pwj 
     */
    *getAnalysisList({ payload },{ call,put }){
      const { data } = yield call(get_TransRecords,{...payload});
      const { code } = data;
      if(code === "200"){
        yield put({ 
          type:"saveMerge", 
          newState:{
            //只有Stopped Finished状态才可以产生分析报告
            analysisList: data.data.records.filter(index=>index.status === "Stopped" || index.status === "Finished"),
            ...payload
          }
        })
      }
    },
    /**
     * 获取页面全部node列表
     * 并自动触发第一个nodelist详细信息
     */
    *getData({ option }, { call, put }) {
      const data = yield call(
        getAnalysisReports, 
        {
          execId: option.execId
        },
        "List");
      
      if(data){

        const result = typeof data.data.data !== "undefined" ? data.data.data : [];
        
        // 检测data中是否有节点列表，如果节点列表不为空，则获取第一个节点
        if(result.length > 0 && typeof result[0]["nodId"] !== "undefined"){
          yield put({
            type: "getNodeDetails",
            option: {
              execId: option.execId,
              nodId: result[0]["nodId"],
              nodeList: result
            }
          })
        }

      }
    },

    /**
     * 获取单个节点node的报告信息
     */
    *getNodeDetails({option},{call, put, select}){
      const data = yield call(
        getAnalysisReportsByNode,
        {
          execId: option.execId, // 执行id
          nodId: option.nodId,
          ifList: true, // 此处默认false，采用对象方式遍历,
          referenceValue: "" // 暂定此值为空
        });
      
      if(data && typeof data.data.data !== "undefined"){

        // 获取节点信息
        const selectedNodes = yield select(state=>state.analysisReports.get("selectedNodes"))
        const nodeFullData = yield select(state=>state.analysisReports.get("nodeFullData"))

        if(option.nodeList){
          yield put({
            type: "saveMerge",
            newState: {
              execId: option.execId,
              nodeList: option.nodeList,
              nodeFullData: {[option.nodId]:data.data.data},
              selectedNodes: [option.nodId]
            }
          })
        }else{
          const newSelected = Array.from(new Set(selectedNodes.concat([option.nodId])));

          yield put({
            type: "saveDeep",
  
            // 如果存在nodeList，说明此次dispatch来自初始化数据，协助保存至state；
            // 如果不存在则标志此次数据来自列展开时的请求，无需保存nodeList
            newState: {
              // nodeFullData: {...nodeFullData,[option.nodId]:data.data.data},
              nodeFullData: nodeFullData.set(option.nodId,data.data.data),
              selectedNodes: newSelected
            }
          })
        }
      }
    },

    *changeExpandedRows({newState},{put,select}){

      // 页面卸载时，防止触发
      if(tag === 0) return;

      const newSelected = newState.selectedNodes
      const analysisReports = yield select(state=>state.analysisReports)
      const selectedNodes = analysisReports.get("selectedNodes");

      // 检测newSelected
      if(newSelected.length < selectedNodes.size){

        // 检测到节点为空
        yield put({type:"saveMerge",
          newState:{
            selectedNodes: newSelected
          }
        })

      }else{
        const nodeFullData = yield select((state)=>state.analysisReports.get("nodeFullData"));

        // 检测到节点是否存在，存在则直接调用save,
        // 如果不存在则获取该节点
        let ifNoData = 0;

        for(let nodId of newSelected){
          if(!nodeFullData.has(nodId)){
            const execId = yield select(state=>state.analysisReports.get("execId"))
            ifNoData = 1;
            yield put({
              type: "getNodeDetails", 
              option:{
                execId: execId,
                nodId: nodId
              }
            })
          }
        }


        if(ifNoData == 0 ){
          // 节点全部都有数据，则直接渲染即可
          yield put({type:"saveMerge",
          newState:{
            selectedNodes: newSelected
          }
        })
        }
      }
    }
  },
  reducers: {

    // 合并newState
    saveDeep: (state, action)=>{

      // 因为newstate中的数据存在嵌套，所以需要做mergeDeep
      // 此处不要用于更新selectedNodes，会导致组件不更新
      return state.mergeDeep(action.newState);
    },
    saveMerge: (state, action)=>{
      
      // 此处的newState仅仅为更新数据，只需要merge即可
      return state.merge(action.newState);
    },

    clear: ()=>{
      return initialState;
    }
  }
};