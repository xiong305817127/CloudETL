import { getDefaultLength } from 'utils/metadataTools';
import { deepCopy, dateFormat, createGUID } from 'utils/utils';
const immutableState = {
  source: { // 基本列表
    list: [],
    total: 0,
  },
  view: { // 表详情
    optimize: {},
    sourceTable: [],
  },
  viewMode: 'read', // 查看详情模式  read, new, edit
  viewFields: [], // 表字段

};

export default {
  namespace: 'datasystemsegistration',
  state: {
    model:"1",
    actionKey:""
  },
  reducers: {

    changeView(state,action){
      return{
        ...state,
        ...action.payload
      }
    },

    // 修改字段
    modifyField(state, action) {
       console.log(state,"state");
      const { viewFields } = state;
      viewFields.forEach(row => {
        if (row.key === action.payload.key) {
          const newField = action.payload;
          if (newField.dataType !== row.dataType) { // 设置默认长度
            newField.length = getDefaultLength(newField.dataType);
          }
          Object.assign(row, newField);
        }
      });
      return { ...state, viewFields };
    },
  },
  subscriptions: {
    setup({ dispatch, history }) {
      history.listen(location=>{
        if(location.pathname === "/DataSystemSegistration"){
              dispatch({
                 type:"changeView",
                  payload:{
                    model:location.query.model?location.query.model:"1"
                  }
              })
        }
      });
    }



  }
};
