/**
 * Created by Administrator on 2018/4/4.
 */


export default{
    namespace:"rowid",
    state:{
        collapsed:false,  //控制侧边栏伸缩
        view:true,      //视图控制   true 表格  false  d3视图                
        
        
        connections:[],
        selected:""
    },
    reducers:{
        save(state,action){
            return{...state,...action.payload}
        }
    },
    subscriptions: {
        setup({dispatch,history}) {

        }
    },
    effects:{
        *open({payload},{put,call,select}){
           console.log("打开转换");
        },
        *excute({payload},{put,call,select}){
            console.log("执行。。。。");
            console.log(payload);
        }
    }
}