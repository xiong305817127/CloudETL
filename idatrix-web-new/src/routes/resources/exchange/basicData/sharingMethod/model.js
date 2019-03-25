import { addShare,getDict,deletelist } from  'services/DirectoryOverview';
import { message } from 'antd';
import { deepCopy, dateFormat, createGUID } from 'utils/utils';


// 字段模板
const immutableField = {
	"key": "",
	"id":0,
	"code": "",
	"name": "",
	"useFlag": "",
};

const modifyStatus = {
  'new': 1,  //新增
  'del': 2,  //删除
  'edit': 3,  //编辑
  'acquis':4   //查看
};


export default{
	namespace:"sharingMethodModel",
	state: {
		viewFields: [], // 表字段
		total: 0,
		show:true,
		loading: false,
		datalist: [],
		sobmitlist: [],
		dataId:[],  //用于保存已有数据的id，用于做隐藏
		updataTime:"",  //获取最新更新时间
		status: 1,
		pagination: 10,
	},

	subscriptions: {
		setup({ history, dispatch }) {
			return history.listen(({ pathname, query }) => {
				if (pathname === "/resources/exchange/basicData/sharingMethod") {
					dispatch({	type: "getList", payload: {	type: "share"	}})
				}
			})
		},
	},
	effects: {
		//查询字段
		*getList({ payload }, { call, select, put }) {
			const { data } = yield call(getDict, { type: "share" });
			const { code } = data;
			if (code === "200") {
				let dataId=[];
				for(let index of data.data){
					 dataId.push(index.id);
				} 
				yield put({
					type: 'save',
					payload: {
						datalist: data?data.data:[],
						dataId:dataId?dataId:[],
						status:dataId.length>0?4:1,
						updataTime:data.data?data.data[0].updateTime:"",
						loading: false,
						show:true,
					}
				})

			}

		},
		//提交表单
		*getSobmit({ payload }, { call, select, put }) {
			const { data } = yield call(addShare, { ...payload });
			const { code } = data;
			if (code === "200") {
				message.info("保存成功");
				yield put({ type: "getList", payload:{ type: "share" } })

			}else if(code === "6000000"){
				yield put({ type: "save", payload:{ state:4 } })
			}

		},

		//删除表单
		*getDeletelist({ payload }, { call, select, put }) {
			const { data } = yield call(deletelist, { ...payload });
			const { code } = data;
			if (code === "200") {
				message.info("删除成功");
				yield put({ type: "delField"})
				yield put({ type: "getList", payload: { type: "share" } })
			}
			//forceDelete
			//if(){}

		},


	},
	reducers: {
		save(state, action) {
			return { ...state, ...action.payload }
		},
	// 添加字段
	addField(state, action) {
		console.log(state, action,"state, action");
		const { viewFields, datalist } = state;
		if (state.datalist.length > 0) {
			const newField = datalist.concat([deepCopy(immutableField,immutableField.key=createGUID())]);
			return { ...state, viewFields: newField, datalist: newField };

		} else {
			const newField = deepCopy(immutableField);
			viewFields.push({ ...newField, key: createGUID(),status: modifyStatus.new});
			console.log(viewFields,"viewFields");
			return { ...state, viewFields };
		}
	},

	// 删除字段
	delField(state, action) {
		const { viewFields, datalist } = state;
		const newViewFields = [];
		
		viewFields.forEach((row, index) => {
			let needDiscard = false;
			if (action.keys.indexOf(row.key) > -1) {
				if (action.status === modifyStatus.new) {
					needDiscard = true;
				} else {
					action.status = modifyStatus.del;
				}
			}

			if (!needDiscard) {
				newViewFields.push(row);
			}
		});
		return { ...state, viewFields: newViewFields,datalist:newViewFields };
	},

}
}