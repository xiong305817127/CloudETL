
export default{
    namespace:"biManageModel",
    state:{

    },
    subscriptions:{
        setup({ history }){
            history.listen(({ pathname })=>{

            })
        }
    },
    reducers:{
        save(state,action){
            return {...state,...action.payload}
        }
    },
    effects:{

    }
}