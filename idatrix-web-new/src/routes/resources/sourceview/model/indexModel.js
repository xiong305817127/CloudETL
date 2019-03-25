import { MLZYgetPublishedAll,MLZYgetOverview,MLZYgetOverall,MLZYgetLatest,getDeptServer } from  'services/DirectoryOverview';
import { getSubtreeAndDepth } from 'services/catalog';

export default {
  namespace:"indexModel",

  state:{
		//资源分类
		resourcesList:[],

    total:0,
    datasource:[],
    selectedRowKeys:[],
    loading:false,
    selectedRows:[],
    BaseText:[],
    subCount:0,
    pubCount:0,
    regCount:0,
    size: "default",
    echartsData:[],
    deptId:"",
    deptCode:"",
    deptName:"",
    queryList:"",
    option :{
      tooltip: {
          trigger: 'axis'
      },
      xAxis: {
          type: 'category',
          data: []
      },
      yAxis: {
          name : '单位（个）',
          type: 'value',
            splitLine: {
              show: false
            }
      },
      grid: {
        // left: '20px',
        // top: '50px',
        // width: '630px',
        // height: '180px',
        
        left: "2%",
        top: "10%",
        bottom: "2%",
        right: "3%",
        show: true,
        containLabel: true,
        borderWidth: 0,
        borderColor: "#85e0a4"
      },
      series: [{
        data: [],
        type: 'line',
        smooth: true,
          // markPoint: {
          //   data: [
          //     {type: 'max', name: '最大值'},
          //     {type: 'min', name: '最小值'},
          //     {type: 'average', name: '平均值'},
          //   ]
          // },
        itemStyle : {
              normal : {
                  color:'#85e0a4'
              },
              
             },
        }]
    }
  },
  subscriptions:{
    setup({history,dispatch}){
      return history.listen(({ pathname,query })=>{
        if(pathname === "/resources/sourceview/viwe/More"){
            dispatch({
              type:"getList",payload:{
                ...query,
                page:query.page?query.page:1,
                pageSize:query.pageSize?query.pageSize:10
              }
						});
						//新增异步加载资源分类
						dispatch({ type:"getResourcesFolder" })
        }

        if(pathname === "/resources/sourceview"){
          dispatch({
            type: "initSourceview"
          })
        }
      })
    },
  },
  effects:{
		/**
     *  逐级查询资源目录
     *  根据传入的id,逐级加载数据
     */
    *getResourcesFolder({ payload,resolve,targetOption,resourcesList, }, { put,call,select }) {
			const { data } = yield call(getSubtreeAndDepth,{...payload});
			const { code } = data;
			if(code === "200"){
				if(resourcesList && resourcesList.length > 0){
					targetOption.loading = false;
					targetOption.children = data.data.map(index=> {
						index.value = index.id;
						index.code = index.resourceEncode;
						index.label = index.resourceName;
						index.isLeaf = !index.hasChildFlag;
						index.children = null;
						return index;
					});
					resolve();
					yield put({ type: 'save', payload: { resourcesList } });
				}else{
					const indexModel = yield select(state=>state.indexModel);
					for(let index of data.data){
						index.value = index.id;
						index.code = index.resourceEncode;
						index.label = index.resourceName;
						index.children = null;
						index.isLeaf = !index.hasChildFlag;
						indexModel.resourcesList.push(index);
					}
					yield put({ type: 'save', payload: { resourcesList:indexModel.resourcesList } });
				}				
			}
		},
    *initSourceview({},{put,call,select}){
      const latest = yield call(MLZYgetLatest,{num: 4});
      const all = yield call(MLZYgetOverall);
      const overView = yield call(MLZYgetOverview,{catalogName: "base"});

      // handle datas from three request

      const data1 = latest.data;
      const data2 = all.data;
      const data3 = overView.data;

      if(data1.code === "200" && data2.code === "200" && data3.code === "200" ){
        
        // 一次性提交修改
        const payload = {
          BaseText:data1.data,
          subCount:data2.data.subCount,
          pubCount:data2.data.pubCount,
          regCount:data2.data.regCount,
          echartsData: data3.data
        };

        // 更改option和统计数据
        yield put({type: "save",payload});

        // 渲染图表
        yield put({type:"echartsData"});

        // 加载底部各个部分统计
        yield put({type: "loadViewData"})
      }
    },

    *loadViewData({},{call}){
     // const { data } = yield call(getResource);
     // console.log("测试data结构",data);
    },
    *echartsData({payload},{put,select}){
      let seriesq=[];
      let xAxisq=[];

      const echartsData = yield select(state=>state.indexModel.echartsData);
      
      console.log(echartsData,"获取到echartsData")
      for(let key of echartsData){
          seriesq.push(key.monthName);

          // 默认显示发布量
          // 也可根据传入的参数调整
          xAxisq.push((payload && payload.select) ? key[payload.select]: key["regCount"] );
      }
      let series = {data:[...xAxisq],type:'line'};
      let xAxis = {data:[...seriesq]};
      const options = yield select((state)=>state.indexModel.option);
      const option = {...options,series,xAxis};

      const payData = {
        option: option,
        selected: (payload && payload.select) ? payload.select: "regCount"
      };
      yield put({type: "save",payload:payData});
    },
    *getList({payload},{put,call}){
      yield put({type:"save",payload:{loading:true}})
      const { data } = yield call(MLZYgetPublishedAll,{...payload});
      const { code } = data;
      if(code === "200"){
            yield put({
                type:'save',
                payload:{
                  datasource:data.data?data.data.results:[],
                  total:data.data?data.data.total:0,
                  loading:false
                }
            })
        }
    },

    *getDeptList({payload},{put,call}){
      yield put({type:"save",payload:{loading:true}})
      const { data } = yield call(getDeptServer,{...payload});
      const { code } = data;
      if(code === "200"){
            yield put({
                type:'save',
                payload:{
                    deptId: label[label.length - 1].value,
                    deptCode: label[label.length - 1].code,
                    deptName: label[label.length - 1].label,
                }
            })
        }
    },
   
  },
  reducers:{
    save(state,action){
      return {...state,...action.payload}
    }
  }
}