import { message } from 'antd';
import {
    getScheduling,
    getTaskExecLinesByMonth,
    getRenterSuccessTasks,
    getSchedulingQuality,
    getNodeTotal,
    getQuailyRenterSuccessTasks,
    getTaskExecTimesByMonth,
    getTaskByNodeType,
    getCount,
    getresources,
    getCountByNumberOfCalls,
    getServerByTheAmountOfData,
    getReptByNumberOfTasks,
    getCountByTheAmountOfData,
    getCountByNumberOfTasks,
    getNumberOfTasks,
    getServicesList,
    getreportList,
    getExchangeList,
    getCountByNumberOfTimes,
    getStatistics,
    getStatisticsInfo,
    getStatisticsUser,
    getDetails,
    getSchemaclick,
    getDetail
} from "services/ReportInterface";

export default {
    namespace: 'DataOverviewModel',
    state: {
        argelist:"ali",
        dataTime:"",
        visible:false,
        hover:false,
        optionTske:{},  //调度任务计算单数
        RenterSuccessTasks:[],  //调度列表
        optionTskelist:[],  //调度任务图表显示
        optionTskeData:{},   //处理数据 图表信息
        NodeTotal:[],   //质量分析任务图表 合格不合格  
        NodeTotalList:{},  //质量分析单数据  合格不合格  
        QuailyRenterSuccessTasks:[],//质量分析返回成功数据列表 合格不合格  

        QuailyRenterSuccessTasksSuccess:[],//质量分析返回成功数据列表 成功不成功
        NodeTotalSuccess:[],   //质量分析任务图表   成功不成功
        NodeTotalListSuccess:{},  //质量分析单数据 成功不成功
        resourcesData:[],   //根据数据量查询
        countList:{},
        SchedulingQualityList:[],
        SchedulingQuality:{},

        ServerNumberOfCallsData:[],   //接口调用次数列表
        ServerNumberOfCalls:{},       //接口调用次数总条数
        ServicesList:[],        //查看服务调用列表 详情列表 调用次数
        ServicesListData:[],    //查看服务调用列表 详情列表  调用量
        serverInfo:"",
        getDetailsLIst:[],    //服务详情列表
        ServerByTheAmountOfData:[],   //数据调用量列表
        ServerByTheAmountOf:{},       //数据调用量总条数

        ReptByNumberOfTasksData:[],   //上报任务数量列表
        ReptByNumberOfTasks:{},       //上报任务数量总条数
        ReportListData:[],

        CountByTheAmountOfData:[],   //上报数据量列表
        CountByTheAmountOf:{},       //上报数据量总条数

        CountByNumberOfTasksData:[],   //作业交换数据量列表
        CountByNumberOfTasks:{},       //作业交换数据量总条数
        ExchangeListData:[],

        NumberOfTasksData:[],   //作业交换作业量列表
        NumberOfTasks:{},       //作业交换作业量总条数

       CountByNumberOfTimes:[],   //服务调用总次数、成功次数、失败次数
       CountByNumberOfOption:{},   //服务调用总次数、成功次数、失败次数 的总量
       StatisticsDataOptian:{},   //服务调用数据量总数、调用数据量最多的前N个接口
       serverDataOption:{},
       StatisticsUserOptian:{},  //活跃信息的查询
       StatisticsOption:{},    //本月登陆用户排行Top10
       SchemaInfo:{},     //BI新增模型数据
       pagination: {
        //分页
        page: 1,
        pageSize: 10
      },
      total:0,

       SchemaclickInfo:{},  //BI报表分析
       SchemaclickData:[],   //BI报表分析

       startTime:"",      //开始时间查询
       endTime:"",       //结束时间查询
    },
    subscriptions:{
       
		setup({ history,dispatch }){
			return history.listen(({ pathname,query })=>{
				if(pathname === "/gather/qualityAnalysis/indexViewOne"){
					dispatch({ type: "DataOverviewModel/getScheduling"});
                    dispatch({ type: "DataOverviewModel/getTaskExecLinesByMonth"});
                    dispatch({ type: "DataOverviewModel/getRenterSuccessTasks"});
                    dispatch({ type: "DataOverviewModel/getNodeTotal"});
                    dispatch({ type: "DataOverviewModel/getQuailyRenterSuccessTasks"});
                    dispatch({ type: "DataOverviewModel/getTaskExecTimesByMonth"});
                    dispatch({ type: "DataOverviewModel/getTaskByNodeType"});
                    dispatch({ type: "DataOverviewModel/SchedulingQuality"});
				}else if(pathname === "/gather/qualityAnalysis/indexViewTwo"){
                    dispatch({ type: "DataOverviewModel/getCount"});
                    dispatch({ type: "DataOverviewModel/getCountByNumberOfCalls"});
                    dispatch({ type: "DataOverviewModel/getServerByTheAmountOfData"});
                    dispatch({ type: "DataOverviewModel/getReptByNumberOfTasks"});
                    dispatch({ type: "DataOverviewModel/getCountByTheAmountOfData"});
                    dispatch({ type: "DataOverviewModel/getCountByNumberOfTasks"});
				}else if(pathname === "/gather/qualityAnalysis/indexViewThere"){
					dispatch({ type: "DataOverviewModel/getSchemaclick",payload:{date:"2019/03/01"}});
				}else if(pathname === "/gather/qualityAnalysis/indexViewFour"){
                    dispatch({ type: "DataOverviewModel/getCountByNumberOfTimes"});
                    dispatch({ type: "DataOverviewModel/getCountByServer"});
                    dispatch({ type: "DataOverviewModel/getStatistics",payload:{year:"2019"}});
                    dispatch({ type: "DataOverviewModel/getStatisticsInfo"});
				}else if(pathname === "/gather/qualityAnalysis/indexViewFives"){
					// dispatch({ type: "DataOverviewModel/getStatisticsUser"});
                    dispatch({ type: "DataOverviewModel/getDetail"});
				}
			})
		}
	},
    reducers: {
        save(state, action) {
            return { ...state, ...action.payload }
        }
    },
    effects: {
        //表一的饼图，返回成功或是失败
        *getScheduling({ payload },{ select,call,put }){
            const { data } = yield call(getScheduling, {...payload });
            const datalist=[];
            if(data.code==="200"){
                data.data.length===0?datalist.push({ value:"0", name:"成功"},{ value:"0", name:"失败" }):datalist.push({ value:data.data.successTask, name:"成功"},{ value:data.data.failTask, name:"失败" })
                yield put({
                    type: "save",
                    payload: { optionTskelist:datalist,optionTske:data.data }
                  });
                
            }
        },
          //表一的折线图，返回处理数据的总数
        *getTaskExecLinesByMonth({ payload },{ select,call,put }){
            const { data } = yield call(getTaskExecLinesByMonth, {...payload });
            console.log(data,"data====getTaskExecLinesByMonth");
            const dataSouerMonth=[];
            const dataSouerTotal=[];
            let sum = 0;
            if(data.code==="200"){
               data.data.map((key)=>{
                   sum += key.total;  
                   dataSouerMonth.push(key.month);
                   dataSouerTotal.push(key.total);
                })
                 yield put({
                    type: "save",
                     payload: { optionTskeData:{
                        sum,
                        dataSouerTotal,
                        dataSouerMonth
                     } }
                   });
            }
        },
        //返回饼图旁边的列表名称和日期
        *getRenterSuccessTasks({ payload },{ select,call,put }){
            const { data } = yield call(getRenterSuccessTasks, {...payload });
            if(data.code==="200"){
                yield put({
                    type: "save",
                    payload: {RenterSuccessTasks:data.data.rows }
                  });
                
            }
        },
        //数据质量分析合格不合格
        *getNodeTotal({ payload },{ select,call,put }){
        const { data } = yield call(getNodeTotal, {...payload });
        console.log(data,"data========")
        const datalist=[];
        if(data.code==="200"){
            data.data.length===0?datalist.push({ value:"0", name:"成功"},{ value:"0", name:"失败" }):datalist.push({ value:data.data.successTotal, name:"成功"},{ value:data.data.failTotal, name:"失败" });
            yield put({
                type: "save",
                payload: { NodeTotal:datalist,NodeTotalList:data.data }
                });
          }
        },

        *SchedulingQuality({ payload },{ select,call,put }){
            const { data } = yield call(getSchedulingQuality, {...payload });
            console.log(data,"data========")
            const datalist=[];
            if(data.code==="200"){
                data.data.length===0?datalist.push({ value:"0", name:"成功"},{ value:"0", name:"失败" }):datalist.push({ value:data.data.successTask, name:"成功"},{ value:data.data.failTask, name:"失败" });
                yield put({
                    type: "save",
                    payload: { SchedulingQualityList:datalist,SchedulingQuality:data.data }
                    });
              }
            },
        
        


        //数据质量分析成功不成功
        *getQuailyRenterSuccessTasks({ payload },{ select,call,put }){
            const { data } = yield call(getQuailyRenterSuccessTasks, {...payload });
            if(data.code==="200"){
                yield put({
                    type: "save",
                    payload: {QuailyRenterSuccessTasksSuccess:data.data.rows }
                  });
                
            }
        },
           //数据质量分析成功不成功图表
           *getTaskExecTimesByMonth({ payload },{ select,call,put }){
            const { data } = yield call(getTaskExecTimesByMonth, {...payload });
            const datalist=[];
            if(data.code==="200"){
                data.data.length===0?datalist.push({value:"0", name:"成功"},{ value:"0", name:"失败"}):data.data.map((key)=>{ datalist.push({ value:key.successTotal, name:"成功"},{ value:key.failTotal, name:"失败" }) });
                yield put({
                    type: "save",
                    payload: { NodeTotalSuccess:datalist,NodeTotalListSuccess:data.data }
                  });
            }
        },
           //数据质量分析合格不合格
           *getTaskByNodeType({ payload },{ select,call,put }){
            const { data } = yield call(getTaskByNodeType, {...payload });
            if(data.code==="200"){
                yield put({
                    type: "save",
                    payload: {QuailyRenterSuccessTasks:data.data }
                  });
                
            }
        },
        //统计资源单条数据
        *getCount({ payload },{ select,call,put }){
            const { data } = yield call(getCount, {...payload });
            if(data.code==="200"){
                yield put({
                    type: "save",
                    payload: {countList:data.data }
                  });
            }
        },

         //根据数据量类型查询
         *getresources({ payload },{ select,call,put }){
            const { data } = yield call(getresources, {...payload });
            if(data.code==="200"){
                yield put({
                    type: "save",
                    payload: {resourcesData:data.data }
                  });
            }
        },
        //接口调用次数
        *getCountByNumberOfCalls({ payload },{ select,call,put }){
            const { data } = yield call(getCountByNumberOfCalls, {...payload });
            if(data.code==="200"){
                yield put({ type: "save", payload: {ServerNumberOfCallsData:data.data.topKList,ServerNumberOfCalls:data.data }});
            }
        },
        //数据调用量
        *getServerByTheAmountOfData({ payload },{ select,call,put }){
            const { data } = yield call(getServerByTheAmountOfData, {...payload });
            if(data.code==="200"){
                yield put({type: "save", payload: {ServerByTheAmountOfData:data.data.topKList,ServerByTheAmountOf:data.data } });
            }
        },

        //上报任务数量
        *getReptByNumberOfTasks({ payload },{ select,call,put }){
            const { data } = yield call(getReptByNumberOfTasks, {...payload });
            if(data.code==="200"){
                yield put({ type: "save", payload: {ReptByNumberOfTasksData:data.data.topKList,ReptByNumberOfTasks:data.data } });
            }
        },

        //上报数据量
        *getCountByTheAmountOfData({ payload },{ select,call,put }){
            const { data } = yield call(getCountByTheAmountOfData, {...payload });
            if(data.code==="200"){
                yield put({ type: "save", payload: {CountByTheAmountOfData:data.data.topKList,CountByTheAmountOf:data.data }  });
            }
        },

         //作业交换数据量
         *getCountByNumberOfTasks({ payload },{ select,call,put }){
            const { data } = yield call(getCountByNumberOfTasks, {...payload });
            if(data.code==="200"){
                yield put({ type: "save", payload: {CountByNumberOfTasksData:data.data.topKList,CountByNumberOfTasks:data.data }});
            }
        },

         //作业交换作业量
         *getNumberOfTasks({ payload },{ select,call,put }){
            const { data } = yield call(getNumberOfTasks, {...payload });
            if(data.code==="200"){
                yield put({ type: "save", payload: {NumberOfTasksData:data.data.topKList,NumberOfTasks:data.data }});
            }
        },
           //查看服务调用列表 详情列表 调用次数
          *getServicesList({ payload },{ select,call,put }){
            const { data } = yield call(getServicesList, {...payload });
            if(data.code==="200"){
                yield put({ type: "save", payload: {ServicesList:data.data }});
            }
        },

         //查看服务调用列表 详情列表  调用量
         *getServicesListData({ payload },{ select,call,put }){
            const { data } = yield call(getServicesList, {...payload });
            if(data.code==="200"){
                yield put({ type: "save", payload: {ServicesListData:data.data }});
            }
        },
         //查看服务调用列表 详情列表  调用量
         *getreportList({ payload },{ select,call,put }){
            const { data } = yield call(getreportList, {...payload });
            if(data.code==="200"){
                yield put({ type: "save", payload: {ReportListData:data.data }});
            }
        },
        //查看服务调用列表 详情列表  调用量
        *getExchangeList({ payload },{ select,call,put }){
            const { data } = yield call(getExchangeList, {...payload });
            if(data.code==="200"){
                yield put({ type: "save", payload: {ExchangeListData:data.data }});
            }
        },
         //表一的饼图，返回成功或是失败
         *getCountByNumberOfTimes({ payload },{ select,call,put }){
            const { data } = yield call(getCountByNumberOfTimes, {...payload });
            const datalist=[];
            if(data.code==="200"){
                data.data.length===0?datalist.push({value:"0", name:"成功"},{ value:"0", name:"失败"}):datalist.push({ value:data.data.success,name:"成功"+":"+data.data.success},{ value:data.data.failure, name:"失败"+":"+data.data.failure })
                yield put({
                    type: "save",
                    payload: { CountByNumberOfTimes:datalist,CountByNumberOfOption:data.data}
                  });
                
            }
        },

          //服务调用数据量总数、调用数据量最多的前N个接口
          *getCountByServer({ payload },{ select,call,put }){
            const { data } = yield call(getServerByTheAmountOfData, {...payload });
            const serverDataNme=[];
            const serverDataSuoer=[];
            if(data.code==="200"){
                 data.data.topKList.map(index =>{
                    serverDataNme.push(index.serviceName);
                    serverDataSuoer.push(index.count)
                 })
                 console.log(serverDataNme,"serverDataNme")
                yield put({
                    type: "save",
                    payload: { serverDataOption:{serverDataNme,serverDataSuoer}}
                  });
                
            }
        },

        //登陆用户月度统计
        *getStatistics({ payload },{ select,call,put }){
            const { data } = yield call(getStatistics, {...payload });
            const StatisticsData=[];
            const StatisticsName=[];
            const StatisticsNameDeptID=[];
            if(data.code==="200"){
               data.data.map((res)=>{
                StatisticsData.push(res.userLoginCount);
                StatisticsName.push(res.month);
                StatisticsNameDeptID.push(res.deptId);
               })
                 yield put({
                     type: "save",
                     payload: { StatisticsDataOptian:{StatisticsData,StatisticsName,StatisticsNameDeptID}}
                   });
            }
        },

        //登陆用户统计
        *getStatisticsInfo({ payload },{ select,call,put }){
            const { data } = yield call(getStatisticsInfo, {...payload });
            const StatisticsData=[];
            const StatisticsName=[];
            const StatisticsID=[];
            const StatisticsDataInfo=[];
            const StatisticsNameInfo=[];
            const StatisticsinfoID=[];
            if(data.code==="200"){
                   data.data.monthLoginUserRankingList.map((res)=>{StatisticsData.push(res.userLoginCount); StatisticsName.push(res.deptName,); StatisticsID.push(res.deptId);}) // 本月登陆用户排行Top10
                   data.data.sumLoginUserCountRankingList.map((res)=>{StatisticsDataInfo.push(res.userLoginCount); StatisticsNameInfo.push(res.deptName);StatisticsinfoID.push(res.deptId);})  //本月登陆用户排行Top10

                 yield put({
                     type: "save",
                     payload: { StatisticsOption:{StatisticsData,StatisticsName,StatisticsID,StatisticsDataInfo,StatisticsNameInfo,StatisticsinfoID}}
                   });
                
            }
        },

        //神算子记录统计
        *getDetail({ payload },{ select,call,put }){
            const { data } = yield call(getDetail, {...payload });
            if(data.code===0){
                    yield put({
                        type: "save",
                        payload: { StatisticsUserOptian:data.data}
                    });
            }
        },
        //展示用户列表
        // *getSchemaInfo({ payload },{ select,call,put }){
        //     const { data } = yield call(getSchemaInfo, {...payload });
        //     if(data.code==="200"){
        //             yield put({
        //                 type: "save",
        //                 payload: { SchemaInfo:data.data}
        //             });
        //     }
        // },
        
        //展示用户列表
        *getSchemaclick({ payload },{ select,call,put }){
            const { data } = yield call(getSchemaclick, {...payload });
            if(data.code==="200"){
                    yield put({
                        type: "save",
                        payload: { SchemaclickInfo:data.data,SchemaclickData:data.data.schemaClickDetails}
                    });
            }
        },
    
        //服务详情列表
        *getDetails({ payload },{ select,call,put }){
            const { data } = yield call(getDetails, {...payload });
            console.log(data,"data==getDetails");
            if(data.code==="200"){
                    yield put({
                        type: "save",
                        payload: { 
                            getDetailsLIst:data.data.rows,
                            pagination:{
                                page:1,
                                size:10
                            },
                            total:data.data.total,
                        }
                    });
            }
        },
        
        
    }
};

