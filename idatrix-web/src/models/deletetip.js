
export default {
  namespace: 'deletetip',
  state: {
    visible:false,
    model:"",
    info:{}
  },
  reducers: {
    "delete"(state,action){
      let arg1 = [];
      let arg2 = [];
      if(action.currentSelected.length>0){
         for(let index1 of action.currentSelected){
              arg1.push({
                 dsId:index1.key,
                 id:index1.key,
                  status:1
              })
         }
      }
      if(action.allSelected.length>0){
        for(let index2 of action.allSelected){
          arg2.push({
            dsId:index2,
            id:index2,
            status:1
          })
        }
      }

      return {
        ...state,
        visible:action.visible,
        allSelected:arg2,
        currentSelected:arg1,
        model:action.model,
        tip:"É¾³ý"
      };
    },
    "abandon"(state,action){
      let arg1 = [];
      let arg2 = [];
      if(action.currentSelected.length>0){
        for(let index1 of action.currentSelected){
          arg1.push({
            dsId:index1.key,
            id:index1.key,
            status:2
          })
        }
      }
      if(action.allSelected.length>0){
        for(let index2 of action.allSelected){
          arg2.push({
            dsId:index2,
            id:index2,
            status:2
          })
        }
      }

      return {
        ...state,
        visible:action.visible,
        allSelected:arg2,
        currentSelected:arg1,
        model:action.model,
        tip:"·ÏÆú"
      };
    },
    "export"(state,action){
      let arg1 = [];
      let arg2 = [];
      if(action.currentSelected.length>0){
        for(let index1 of action.currentSelected){
          arg1.push({
            metaid:index1.key
          })
        }
      }
      if(action.allSelected.length>0){
        for(let index2 of action.allSelected){
          arg2.push({
            metaid:index2
          })
        }
      }

      return {
        ...state,
        visible:action.visible,
        allSelected:arg2,
        currentSelected:arg1,
        model:action.model,
        tip:action.tip
      };
    },
    "hide"(state,action){
      return {
        ...state,
        visible:action.visible
      };
    }
  },
};
