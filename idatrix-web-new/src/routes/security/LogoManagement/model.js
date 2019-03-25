import { getLogoList } from '../../../services/logoManage';
import { message } from "antd";

export default {

  namespace: 'logoManage',

  state: {
    dataSource: [],
    total:0,
    loading:false
  },

  effects: {
    *getList({ payload }, { put }) {
      yield put({ type:"save",payload:{loading:true} })
      const { data } = yield getLogoList(payload);
      if(data.msg === "success"){
      	yield put({ type: 'save', payload:{
      		dataSource:data.data.list,
      		total:data.data.total,
      		loading:false
      	}});
      }
    },
  },

  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query }) => {
        if (pathname === '/LogoManagement') {
          	dispatch({
	            type: 'getList',
	            payload: {
	              ...query,
	              size: query.pageSize,
	            },
	        });
        }
      });
    },
  },

  reducers: {
    save(state, action) {
      return { ...state, ...action.payload };
    },
  },

};
