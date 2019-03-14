/**
 * Created by Steven Leo on 2018/10/10.
 */
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
    data: []
});

export default {
  namespace: "dataExhibition",
  state: initialState,

  // 注册页面路由
  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query }) => {
        if(path === "/hotMap/dataExhibition"){
            
        }
      });
    }
  },

  effects: {
   
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