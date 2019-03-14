
export default{
	namespace:"fuctest",
	state:{
		count:1
	},

	reducers:{
		save(state,action){
			return {...state,...action.payload}
		}
	},

	effects:{

	},

	subscriptions:{
		
	}
}