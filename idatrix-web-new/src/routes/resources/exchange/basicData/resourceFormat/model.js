import { addType, getDict, deletelist, forceDelete } from 'services/DirectoryOverview';
import { message } from 'antd';
import { deepCopy, dateFormat, createGUID } from 'utils/utils';
// 字段模板
const immutableField = {
	"id":0,
	"code": "",
	"name": "",
	"useFlag": "",
};

const modifyStatus = {
	'new': 1,  //新增
	'del': 2,  //删除
	'edit': 3,  //编辑
	'acquis': 4   //查看
};
export default{
	namespace:"resourceFormatModel",
	state:{
		viewFields: [], // 表字段
		total: 0,
		show:true,
		loading: false,
		dataObj:[],
		datalist: [],
		sobmitlist: [],
		Globalvariable:[],
		dataId:[],  //用于保存已有数据的id，用于做隐藏
		updataTime:"",  //获取最新更新时间
		status: 1,
		pagination: 10,
	},

	subscriptions: {
		setup({ history, dispatch }) {
			return history.listen(({ pathname, query }) => {
				if (pathname === "/resources/exchange/basicData/resourceFormat") {
					dispatch({	type: "getList", payload: {	type: "type"}})
				}
			})
		},
	},
	effects: {
	//查询字段
	*getList({ payload }, { call, select, put }) {
		const { data } = yield call(getDict, { type: "type" });
		const { code } = data;
		const datalist = data.data;
		if (code === "200") {
			let solist = datalist.map((val,i)=>{return val.typeParentId;})
			let solist1 = datalist.map((val,i)=>{ return val.id;})
			let set = new Set(solist);
			let list=[];
			let list1=[];
			let list2 = [];
			let list3 = [];
			let list4 = [];
			let list5 = [];
			console.log(solist,"solist",solist1);
			for(let key of solist){
				let asd1 = solist.find(item=>item === key);
				list3.push(asd1);
				for(let obj of datalist){
					if(asd1 === obj.id){
						list4.push(obj);
						for(let index of list4){
							if(index.typeParentId === obj.typeParentId){
								list5.push(index);
							}
						
						}
					}
				}
			}

			for(let key of set){
			let asd = solist1.find(item=>item===key);	
			list.push(asd);
				for(let obj of datalist){
					if(asd === obj.id){
						list1.push(obj);
						for(let index of list1){
							if(index.typeParentId === obj.typeParentId){
								list2.push(index);
							}
						
						}
					}
				}
			}

			let dataId=[];
			for(let index of data.data){
				 dataId.push(index.id);
			} 



			yield put({
				type: 'save',
				payload: {
					dataObj:datalist,
					datalist:list2,
					dataId:dataId?dataId:[],
					status:dataId.length>0?4:1,
					show:true,
					updataTime:data.data?data.data[0].updateTime:"",
					loading: false
				}
			})

		}

	},
	//提交表单
	*getSubmit({ payload }, { call, select, put }) {
		const { data } = yield call(addType, { ...payload });
		const { code } = data;
		if (code === "200") {
			message.info("保存成功");
			yield put({ type: "getList", payload:{ type: "type" } })
		}
	},

	//删除表单
	*getDeletelist({ payload }, { call, select, put }) {
		const { data } = yield call(deletelist, { ...payload });
		const { code } = data;
		if (code === "200") {
			message.info("删除成功");
			yield put({ type: "delField"})
			yield put({ type: "getList", payload: { type: "type" } })
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
		const { viewFields, datalist } = state;
		console.log(viewFields,"viewFields, datalist", datalist);
		if (state.datalist.length > 0) {
			const newField = datalist.concat([deepCopy(immutableField)]);
			return { ...state, viewFields: newField, datalist: newField };

		} else {
			const newField = datalist.concat([deepCopy(immutableField)]);
			return { ...state, viewFields: newField, datalist: newField };
		}
	},

	// 删除字段
	delField(state, action) {
		const { viewFields, sobmitlist } = state;
		const newViewFields = [];
		viewFields.forEach((row, index) => {
			let needDiscard = false;
			if (action.selectedRows.indexOf(row.id) > -1) {
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
		return { ...state, viewFields: newViewFields };
	},

	}
}