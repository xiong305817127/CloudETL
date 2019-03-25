
const delay = timeout => new Promise(resolve=>setTimeout(resolve,timeout));

export default{
	namespace:"dvatestModel",
	state:0,
	reducers:{
		save(state,{payload}){ 
			console.log(payload,"更新的action");
			return state + payload || 1; 
		}
	},
	effects:{
		*setDelay({payload},{call,put}){
			console.log("开始");
			yield call(delay,payload.delay || 100);
			yield put({	type:"save",payload: payload.amount })
			console.log("延时一秒执行");
		},
		*process({payload},{put,select,take}){
			yield put({ type:"setDelay",payload:{ amount:2}});		
	        yield take("setDelay/@@end");    
			const  count  = yield select(state => state.dvatestModel); 

			console.log(count,"执行后的值");

			yield put({ type: 'setDelay', payload: { amount: count, delay: 0 } });
		}
	},
	subscriptions:{}
}

// dispatch({ type:"dvatestModel/process",payload:{count:1}});
// 1、count值为 1;
// 2、count值为 0;