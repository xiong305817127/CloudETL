import { queryMetaDataByDsId } from 'services/catalog';
import { getFieldsById } from 'services/metadataDefine';
import { insertSchema,openSchema,editSchema } from 'services/bi';
import { initPos } from "config/jsplumb.config.js";
import { message } from "antd";
import { routerRedux } from "dva/router";
/**
 * 根据起点Id,自动获取x,y位置
 * @param  {Array} items  所有组件的数组
 * @param  {String} id    起点id
 * @return {[type]} {x,y} 自动生成的位置信息
 */
const getPosition = (items,id)=>{
	let posYArgs = [];
	let posX = initPos.startX;
	let posY = initPos.startY;
	items.map(index=>{
		if(index.preId === id){
			posYArgs.push(parseInt(index.y))
		}
		if(index.id === id){
			posX = parseInt(index.x)+initPos.width+initPos.splitX;
		}
	});
	while(posYArgs.includes(posY)){
		posY += initPos.height+initPos.splitY;
	}

	return {x:posX+"px",y:posY+"px"}
}

/**
 * 根据数组格式化成生成xml所需数据
 * @param  {Array} args 需格式化的数组
 * @return {Array}      格式化后的数组
 */
const formatDimension = (args)=>{
	let newArgs = [];
	for(let index of args){
		let obj = {};
		obj.id = index.id;
		obj.name = index.name;
		obj.foreignKey = index.foreignKey;
		obj.Hierarchy = {};
		obj.Hierarchy.Table = {};
		obj.Hierarchy.Table.name = index.tableName;
		obj.Hierarchy.hasAll = true;
		obj.Hierarchy.visible = index.visible;
		obj.Hierarchy.Level = index.Level.map(item=>{
			if(typeof(item.visible) !== "boolean"){
				if(item.visible === "true"){
					item.visible = true;
				}else{
					item.visible = false;
				}
			};
			delete item.aggregator;
			delete item.formatString;
			return item;
		});
		obj.Hierarchy.primaryKey = index.primaryKey;
		newArgs.push(obj);
	}
	return newArgs;
}

/**
 * 解析精度
 * @param  {Array} args 精度数组
 * @return {Array}      解析完成返回的数组
 */
const parseDimension = (args)=>{
	let newArgs = [];
	for(let index of args){
		let obj = {};
		obj.id = index.id;
		obj.name = index.name;
		obj.foreignKey = index.foreignKey;
		obj.tableName = index.hierarchy.table.name;
		obj.primaryKey =  index.hierarchy.primaryKey;
		obj.Level = index.hierarchy.level;
		obj.visible = index.hierarchy.visible;
		newArgs.push(obj);
	}
	return newArgs;
}

/**
 * 通过维度，获得表字段
 * @param  {string} id  	  主表Id
 * @param  {Array} dimension  维度数组
 * @return {Array}            字段数组
 */
const getFields = (id,dimension)=>{
	let args = [];
	for(let index of dimension){
		if(id === index.id){
			args=[...args,...index.Level];
		}
	};
	return args;
}

/**
 * 通过维度度量,获得主表字段
 * @param  {string} id   			主表Id
 * @param  {Array} measure   		度量数组
 * @param  {Array} dimension 		维度数组
 * @return {Array}           		主表数组
 */
const getMainFields = (id,measure,dimension)=>{
	let args = getFields(id,dimension);
	return [...measure,...args];
}

/**
 * 通过线条定位置
 * @param  {String} id    端点id
 * @param  {Array} lines  线条数组
 * @return {x,y}          端点位置
 */
const getPosByLines = (id,lines)=>{
	let pos = {};
	for(let index of lines){
		if(index.targetId === id){
			pos = index.targetPos;
			pos.preId = index.sourceId;
		}
	}
	return pos;
}

/**
 * 通过主表id,筛选出其余表数组
 * @param  {String} id        主表Id
 * @param  {Array} lines  線條数组
 * @param  {Array} dimension 维度数组
 * @return {Array}           其他组件数组
 */
const getOtherItem = (id,lines,dimension)=>{
	let existArgs = [id];

	console.log(id);
	console.log(existArgs);
	let otherItems = [];
	for(let index of dimension){
		if(!existArgs.includes(index.id)){
			const {x,y,preId} = getPosByLines(index.id,lines);
			otherItems.push({
				id:index.id,
				tableName:index.tableName,
				fields:getFields(index.id,dimension),
				x,y,preId
			});
			existArgs.push(index.id);
		}
	}
	return otherItems;
}



/**
 * 保存元素最终的位置
 * @param  {Array} lines   线条数组
 * @param  {[type]} items  节点数组
 * @return {[type]}       格式化后带位置的线条
 */
const formatLines = (lines,items)=>{
	return lines.map(index=>{
		let source = items.filter(item=>index.sourceId === item.id)[0];
		let target = items.filter(item=>index.targetId === item.id)[0];
		index.sourcePos = {x:source.x,y:source.y};
		index.targetPos = {x:target.x,y:target.y};
		return index;
	})
}


const initState = {
   //修改id
	id:"",
	//库表名
	tables:[],
	//模型名称
	name:"",
	//文件夹ID
	categoryId:"",
	//库id 
	dsId:"",
	//selectId:6088,
	//主表ID
	mainTableId:"",
	//度量字段 主表
	mainDataSource:[],
	//保存主item
	mainItem:null,
	//主表主键
	visibleMain:false,
	//组件数组
	items:[],
	//通过组件Id,避免重绘
	itemsId:[],
	//连接数组
	lines:[],
	//编辑连线
	line:null,
	//关联字段Modal
	visible:false,
	//当前选中的数组
	targetItem:null,
	//资源id
	sourceItem:null,
	//是否需要重绘
	shouldUpdate:false,
	//度量表
	measure:[],
	//维度表
	dimension:[],
	//新建维度Modal
	dimVisible:false,
	//操作
	action:"new",
	//维度可见
	dimensionView:false,
	//度量可见
	measureView:false,
};


export default{
	namespace:"biModelId",
	state:{...initState},
	subscriptions:{
		setup({history,dispatch}){
			history.listen(({ pathname,query })=>{
				console.log(pathname);
				console.log(query);
				if(pathname === "/analysis/DataModel/Config"){
					console.log(query,"调用其他");
					if(query.action === "new"){
						dispatch({ type:"getTableList",payload:{...query},init:true});
					}else if(query.action === "edit"){
						dispatch({ type:"openView",payload:{...query}});
					}
				}
			})
		}
	},

	effects:{
		*openView({ payload },{ put,select,call }){
			const { id } = yield select(state=>state.biModelId);
			if(id && id === payload.id){
				return false;
			}
			const { data } = yield call(openSchema,{id:payload.id});
			if(data.code === "200"){
				const { categoryId,dsId,dsName,tableRelation,dbSchema,id } = data.data;

				yield put({ type:"getTableList",payload:{ dsId }});

				const lines = JSON.parse(tableRelation).table;
				const schema = JSON.parse(dbSchema);
				const measure = schema.cube.measure;
				const dimension = parseDimension(schema.cube.dimension);
				const mainTableId = schema.cube.table.id;
				let mainItem = {};
				mainItem.id = mainTableId;
				mainItem.tableName = schema.cube.table.name;
				mainItem.x=initPos.startX+"px";
				mainItem.y=initPos.startY+"px";
				let mainDataSource = getMainFields(mainTableId,measure,dimension);
				mainItem.fields=mainDataSource;
				let otherItem = getOtherItem(mainTableId,lines,dimension);

				yield put({
					type:"save",
					payload:{
						categoryId,dsId,name:dsName,id,lines,measure,dimension,mainDataSource,
						items:[mainItem,...otherItem],action:payload.action,mainItem,mainTableId,shouldUpdate:true
					}
				})
			}
		},
		*getTableList({payload,init},{put,call}){
			const { data } = yield call(queryMetaDataByDsId,{dsId:payload.dsId});
			const { code } = data;

			if(code === "200"){
				if(init && payload.action === "new"){
					payload = {...initState,...payload,shouldUpdate:true}
				}
				yield put({ type:"save",payload:{...payload,tables:data.data}})
			}
		},
		*addMainTable({payload},{put,call}){
			const { data } = yield call(getFieldsById,{metaid:payload.id});
			const { code } = data;
			if(code === "200"){
				const fields = data.data.map(index=>{
					let obj = {};
					obj.name = index.colName;
					obj.visible = true;
					obj.column = index.colName;
					obj.id = index.id;
					obj.fieldType = index.dataType;
					obj.aggregator = "sum";
					obj.formatString = "#,###.00";
					return obj;
				});
				payload.x=initPos.startX+"px";
				payload.y=initPos.startY+"px";
				payload.fields = fields;
				yield put({type:"save",payload:{
					mainTableId:payload.id,
					mainDataSource:fields,
					mainItem:payload,visibleMain:true
				}})
			}
		},
		*addOtherTable({payload,sourceId},{put,select,call}){
			const { biModelId } = yield select(state => state);
			const { items } = biModelId;
			const { data } = yield call(getFieldsById,{metaid:payload.id});
			const { code } = data;
			if(code === "200"){
				const fields = data.data.map(index=>{
					let obj = {};
					obj.name = index.colName;
					obj.visible = true;
					obj.column = index.colName;
					obj.id = index.id;
					obj.fieldType = index.dataType;
					return obj;
				});
				const sourceItem = items.filter(index=>index.id === sourceId)[0];
				yield put({type:"save",payload:{
					sourceItem,targetItem:{...payload,fields},visible:true
				}})
			}
		},
		*submitData({},{put,select,call}){
			const biModelId = yield select(state=>state.biModelId);
			const { name,dsId,categoryId,lines,mainItem,dimension,measure,items,action,id } = biModelId;
			if(measure.length === 0 || dimension.some(index=>index.Level.length === 0)){
				message.error("保存失败,所有维度与度量必须至少有一条数据!");
				return false;
			}
			
			console.log(dimension,"度量");
			console.log(measure,"维度");

			if(!measure.some(index=>index.visible) || !dimension.some(index=>index.visible)){
				message.error("保存失败,所有维度与度量必须至少有一条可见数据!");
				return false;
			}

			let obj = {};
			obj.dsId = parseInt(dsId);	
			obj.categoryId = parseInt(categoryId);	
			obj.tableRelation = {};
			obj.tableRelation.table = formatLines(lines,items);
			obj.schema = {};
			obj.schema.Cube = {};
			obj.schema.name = name;
			obj.schema.Cube.Table = {
				id:mainItem.id,
				name:mainItem.tableName
			};
			obj.schema.Cube.name = mainItem.tableName;
			obj.schema.Cube.Dimension = formatDimension(dimension);
			obj.schema.Cube.Measure = measure;

			if(action === "edit" && id){
				obj.id = id;
				const { data } = yield call(editSchema,obj);
				if(data.code === "200"){
					message.success("更新模型成功！");
					yield put(routerRedux.push("/analysis/DataModel"));
				}
			}else{
				const { data } = yield call(insertSchema,obj);
				if(data.code === "200"){
					message.success("保存模型成功！");
					yield put(routerRedux.push("/analysis/DataModel"));
				}
			}

		}
	},

	reducers:{
		save(state,action){
			return {...state,...action.payload}
		},
		//重置数据
		clearAll(state,action){
			state.mainDataSource.splice(0);
			state.items.splice(0);
			state.itemsId.splice(0);
			state.lines.splice(0);
			state.measure.splice(0);
			state.dimension.splice(0);
			return {...initState}
		},
		//添加线条
		addLine(state,action){
			const { targetItem,lines,items,dimension } = state;
			let newItems = items;
			if(items.every(index=>index.id !== targetItem.id)){
				const {x,y} = getPosition(items,action.payload.sourceId);
				targetItem.x = x;
				targetItem.y = y;
				targetItem.preId = action.payload.sourceId;
				newItems.push(targetItem);
			}else{
				newItems = items.map(index=>{
					if(index.id === targetItem.id){
						index.preId = action.payload.sourceId;
					}
					return index;
				})
			};
			dimension.push({
				id:targetItem.id,
				tableName:targetItem.tableName,
				Level:targetItem.fields, 
				primaryKey:action.payload.keyColumn,
				foreignKey:action.payload.foreignKey,
				name:targetItem.tableName,
				visible:true
			});

			return {...state,
				items:newItems,dimension,
				lines:[...lines,action.payload],
				visible:false,shouldUpdate:true,line:null
			};
		},
		//更新线条参数
		updateLine(state,action){
			const { targetId,sourceId } = action.payload;
			return {
				...state,line:null,visible:false,
				lines:state.lines.map(index=>{
					if(index.targetId === targetId && index.sourceId === sourceId){
						return {...index,...action.payload};
					}
				})
			}
		},
		//删除节点
		deleteItem(state,action){
			const {lines,items,mainTableId,mainDataSource,dimension,measure,mainItem} = state;
			const { id } = action.payload;
			let newMainTableId = mainTableId;
			let newMainDataSource = mainDataSource;
			let newDimension = dimension.filter(index=>index.id !== id);
			let newMeasure = measure;
			let newMainItem = mainItem;
			if(id === mainTableId){
				newMainTableId = "";
				newMainDataSource = [];
				newDimension = [];
				newMeasure = [];
				newMainItem = null;
			}

			return {
				...state,
				items:items.filter(index=>index.id !== id),
				lines:lines.filter(index=>index.targetId !== id && index.sourceId !== id),
				dimension:newDimension,measure:newMeasure,mainItem:newMainItem,
				mainTableId:newMainTableId,mainDataSource:newMainDataSource,
				shouldUpdate:true
			}
		},
		//添加主键
		addMainKey(state,action){
			const { items,mainItem,dimension } = state;

			return {
				...state,
				items:[...items,mainItem],
				visibleMain:false,
				shouldUpdate:true,
				dimension:[...dimension,action.payload],
				measure:action.measure
			}
		}
	},


}