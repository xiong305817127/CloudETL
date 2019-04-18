import React from 'react'
import { Table, DatePicker, Button,Select, Row, Col,Modal } from 'antd';
import { connect } from 'dva';
import styles from "./style.less";
import ReactEcharts from 'echarts-for-react';
import moment from 'moment';
const { MonthPicker, RangePicker } = DatePicker;
const dateFormat = 'YYYY';
import _ from "lodash";
const Option = Select.Option;
class index extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            visible:false,
            show:false
        };
    }

    

    
    //接口服务 调用量 饼图表数据
    optionServer=()=>{
        const {CountByNumberOfTimes,CountByNumberOfOption} =this.props.DataOverviewModel;
        return {
            option :{
                tooltip: { trigger: 'item', formatter: "{a} <br/>{b}: {c} ({d}%)"},
                color:['#009943','#ed7d31'],
                legend: { x : 'center', y : 'top', data:CountByNumberOfTimes },
                series: [
                    { name:'访问来源', type:'pie', radius: ['50%', '70%'],avoidLabelOverlap: false,
                        label: {
                            normal: {
                                        show: true,
                                        position: 'center',
                                        formatter:function (argument) {
                                            var html;
                                            html='接口调用总数\r\n\r\n'+CountByNumberOfOption.count;
                                            return html;
                                        },
                                        textStyle:{
                                           fontSize: 20,
                                            color:'#000'
                                        }
                                    },
                        },
                        labelLine: {  normal: { show: false }
                        },
                        data:CountByNumberOfTimes
                    }
                ]
            },
        }
    }
  //登陆用户月度统计
    optionServerData=()=>{
        const {serverDataOption} =this.props.DataOverviewModel;
        return{
            option:{
                color:['#448acf'],
                tooltip: {  trigger: 'axis',  axisPointer: { type: 'shadow' } },
                xAxis: {
                    type: 'value',
                    name: 'Days',
                    axisLabel: {
                        formatter: '{value}'
                    }
                },
                grid: [{ top: 50, width: '80%', bottom: '15%',left: 10,  containLabel: true }, { top: '15%', width: '30%', bottom: 0, left: 10, containLabel: true }],
                yAxis: {
                    type: 'category',
                    inverse: true,
                    data: serverDataOption.serverDataNme
                },
                series : [
                    {
                        type:'bar',
                         data:serverDataOption.serverDataSuoer,
                        //顶部数字展示pzr
                        itemStyle: {
                            //柱形图圆角，鼠标移上去效果，如果只是一个数字则说明四个参数全部设置为那么多
                            emphasis: {  barBorderRadius: 10  },
                            normal: {
                                //柱形图圆角，初始化效果
                                barBorderRadius:[0, 50, 50, 0],
                                label: {
                                    show: true,//是否展示
                                    textStyle: {  fontWeight:'bolder', fontSize : '12',  fontFamily : '微软雅黑',  }
                                }
                            }
                        },
                    }
                ]
            }
        }
    }
  //本月登陆用户排行Top10
    optionServerDataMonth=()=>{
        const {StatisticsOption,visible,dispatch} =this.props.DataOverviewModel;
        console.log(StatisticsOption,"StatisticsOption");
        return{
            option:{
                color:['#448acf'], 
                tooltip: {  trigger: 'axis',  axisPointer: { type: 'shadow' },
                triggerOn: 'click',
               
                 //formatter: "<table><thead class='ant-table-thead'><tr class='ant-table-tbody'><td>登录时间&nbsp;&nbsp;&nbsp;</td><td >客户端IP&nbsp;&nbsp;</td><td >用户名</td></tr> <tr class='ant-table-tbody'><td>{a}&nbsp;&nbsp;&nbsp;</td><td >{b}</td></tr> </thead></table>" 
                //  formatter:function(params){
                //     var html = '';
                //     var htmls = '';
                //     if(params.length>0){
                //         html+="登录时间"+"&nbsp;&nbsp;"+"客户端IP&nbsp;&nbsp;"+'用户名'+'</br>';
                //         for(let val of StatisticsOption.StatisticsName){
                //                 console.log(val,"val================")
                //                 htmls+=val+"&nbsp;&nbsp;"+val+"&nbsp;&nbsp;"+val+'</br>';
                //             }
                //     }
                //     return html+htmls;
                //     }
                },
                xAxis: {
                    type: "value",
                    name: 'Days',
                    axisLabel: {
                        formatter: '{value}'
                    }
                },
                grid: [{ top: 50, width: '80%', bottom: '15%',left: 10,  containLabel: true }, { top: '15%', width: '30%', bottom: 0, left: 10, containLabel: true }],
                yAxis: {
                    type: 'category',
                    inverse: true,
                    data: StatisticsOption.StatisticsName
                },
                series : [
                    {
                        name:StatisticsOption.StatisticsID,
                        type:'bar',
                         data:StatisticsOption.StatisticsData,
                        //顶部数字展示pzr
                        itemStyle: {
                            //柱形图圆角，鼠标移上去效果，如果只是一个数字则说明四个参数全部设置为那么多
                            emphasis: {  barBorderRadius: 10  },
                            normal: {
                                //柱形图圆角，初始化效果
                                barBorderRadius:[0, 50, 50, 0],
                                label: {
                                    show: true,//是否展示
                                    textStyle: {  fontWeight:'bolder', fontSize : '12',  fontFamily : '微软雅黑',  }
                                }
                            }
                        },
                    }
                ]
            }
        }
    }

      //登陆用户数总排行Top10
      optionServerDataUser=()=>{
        const {StatisticsOption} =this.props.DataOverviewModel;
        return{
            option:{
                color:['#448acf'],
                tooltip: {  trigger: 'axis',  axisPointer: { type: 'shadow' },  triggerOn: 'click', },
                xAxis: {
                    type: 'value',
                    name: 'Days',
                    axisLabel: {
                        formatter: '{value}'
                    }
                },
                grid: [{ top: 50, width: '80%', bottom: '15%',left: 10,  containLabel: true }, { top: '15%', width: '30%', bottom: 0, left: 10, containLabel: true }],
                yAxis: {
                    type: 'category',
                    inverse: true,
                    data: StatisticsOption.StatisticsNameInfo
                },
                series : [
                    {
                        name:'降水量',
                        type:'bar',
                         data:StatisticsOption.StatisticsDataInfo,
                        //顶部数字展示pzr
                        itemStyle: {
                            //柱形图圆角，鼠标移上去效果，如果只是一个数字则说明四个参数全部设置为那么多
                            emphasis: {  barBorderRadius: 10  },
                            normal: {
                                //柱形图圆角，初始化效果
                                barBorderRadius:[0, 50, 50, 0],
                                label: {
                                    show: true,//是否展示
                                    textStyle: {  fontWeight:'bolder', fontSize : '12',  fontFamily : '微软雅黑',  }
                                }
                            }
                        },
                    }
                ]
            }
        }
    }

       //处理数据 折线表数据
  optionStatistics=()=>{
    const {StatisticsDataOptian} =this.props.DataOverviewModel;
        return {
            option :{
                tooltip : { trigger: 'axis', axisPointer : {type : 'shadow' }},
                grid: { left: '5%', right: '2%',bottom: '3%',containLabel: true},
                color:['#448acf'],
                xAxis : [
                    {
                        type : 'category',
                        data :StatisticsDataOptian.StatisticsName,
                        axisTick: { alignWithLabel: true }
                    }
                ],
                yAxis : [ { type : 'value' }],
                series : [{ name:'直接访问', type:'bar', barWidth: '60%', data:StatisticsDataOptian.StatisticsData}]
            },
        }
    };
    onChange=(date)=>{
        // let data =dateString.split("-")[0];
        // let time =dateString.split("-")[1];
         const {dispatch}=this.props;
         const list = [];
         // list.push({data:data,time:time});
          dispatch({ type: "DataOverviewModel/save",payload:{argelist:"alisa",dataTime:date}});
         dispatch({ type: "DataOverviewModel/getStatistics",payload:{year:date}});
         dispatch({ type: "DataOverviewModel/getCountByNumberOfTimes",payload:{year:date}});
         dispatch({ type: "DataOverviewModel/getCountByServer"});
         dispatch({ type: "DataOverviewModel/getStatisticsInfo"});
    }

    componentDidMount(){
        const {dispatch}=this.props;
        dispatch({ type: "DataOverviewModel/getCountByNumberOfTimes",payload:{year:"2019"}});
        dispatch({ type: "DataOverviewModel/getCountByServer"});
        dispatch({ type: "DataOverviewModel/getStatistics",payload:{year:"2019"}});
        dispatch({ type: "DataOverviewModel/getStatisticsInfo"});
      }
      //本月登陆用户排行Top10
      clickEchartsPie=(e)=>{
        const {dispatch}=this.props;
        const {StatisticsOption} =this.props.DataOverviewModel;
        var myDate = new Date();
        if(e.dataIndex === 0){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsID[0],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 1){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsID[1],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 2){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsID[2],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 3){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsID[3],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 4){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsID[4],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 5){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsID[5],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 6){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsID[6],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 7){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsID[7],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 8){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsID[8],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 9){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsID[9],month:myDate.getMonth()+1}});
        }
        dispatch({ type: "DataOverviewModel/save",payload:{visible:true}});
      }
      

      //登陆用户月度统计
      clickEchartsPieLogin=(e)=>{
        const {dispatch}=this.props;
        const {argelist,dataTime,StatisticsDataOptian} =this.props.DataOverviewModel;
        console.log(argelist,dataTime,"argelist,dataTime======",StatisticsDataOptian.StatisticsName[5]);
        var myDate = new Date();
        if(argelist === "alisa"){
            if(e.dataIndex === 0){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:dataTime.data,month:StatisticsDataOptian.StatisticsName[0]}});
            }else if(e.dataIndex === 1){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:dataTime.data,month:StatisticsDataOptian.StatisticsName[1]}});
            }else if(e.dataIndex === 2){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:dataTime.data,month:StatisticsDataOptian.StatisticsName[2]}});
            }else if(e.dataIndex === 3){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:dataTime.data,month:StatisticsDataOptian.StatisticsName[3]}});
            }else if(e.dataIndex === 4){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:dataTime.data,month:StatisticsDataOptian.StatisticsName[4]}});
            }else if(e.dataIndex === 5){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:dataTime.data,month:StatisticsDataOptian.StatisticsName[5]}});
            }else if(e.dataIndex === 6){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:dataTime.data,month:StatisticsDataOptian.StatisticsName[6]}});
            }else if(e.dataIndex === 7){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:dataTime.data,month:StatisticsDataOptian.StatisticsName[7]}});
            }else if(e.dataIndex === 8){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:dataTime.data,month:StatisticsDataOptian.StatisticsName[8]}});
            }else if(e.dataIndex === 9){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:dataTime.data,month:StatisticsDataOptian.StatisticsName[9]}});
            }else if(e.dataIndex === 10){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:dataTime.data,month:StatisticsDataOptian.StatisticsName[10]}});
            }else if(e.dataIndex === 11){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:dataTime.data,month:StatisticsDataOptian.StatisticsName[11]}});
            }else if(e.dataIndex === 12){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:dataTime.data,month:StatisticsDataOptian.StatisticsName[12]}});
            }
        }
        else{
            if(e.dataIndex === 0){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:myDate.getFullYear(),month:StatisticsDataOptian.StatisticsName[0]}});
            }else if(e.dataIndex === 1){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:myDate.getFullYear(),month:StatisticsDataOptian.StatisticsName[1]}});
            }else if(e.dataIndex === 2){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:myDate.getFullYear(),month:StatisticsDataOptian.StatisticsName[2]}});
            }else if(e.dataIndex === 3){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:myDate.getFullYear(),month:StatisticsDataOptian.StatisticsName[3]}});
            }else if(e.dataIndex === 4){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:myDate.getFullYear(),month:StatisticsDataOptian.StatisticsName[4]}});
            }else if(e.dataIndex === 5){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:myDate.getFullYear(),month:StatisticsDataOptian.StatisticsName[5]}});
            }else if(e.dataIndex === 6){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:myDate.getFullYear(),month:StatisticsDataOptian.StatisticsName[6]}});
            }else if(e.dataIndex === 7){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:myDate.getFullYear(),month:StatisticsDataOptian.StatisticsName[7]}});
            }else if(e.dataIndex === 8){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:myDate.getFullYear(),month:StatisticsDataOptian.StatisticsName[8]}});
            }else if(e.dataIndex === 9){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:myDate.getFullYear(),month:StatisticsDataOptian.StatisticsName[9]}});
            }else if(e.dataIndex === 10){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:myDate.getFullYear(),month:StatisticsDataOptian.StatisticsName[10]}});
            }else if(e.dataIndex === 11){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:myDate.getFullYear(),month:StatisticsDataOptian.StatisticsName[11]}});
            }else if(e.dataIndex === 12){
                dispatch({ type: "DataOverviewModel/getDetails",payload:{year:myDate.getFullYear(),month:StatisticsDataOptian.StatisticsName[12]}});
            }
        }
       
        dispatch({ type: "DataOverviewModel/save",payload:{visible:true}});
      }

      //登陆用户数总排行Top10
      clickEchartsPieQuery=(e)=>{
        const {dispatch}=this.props;
        const {StatisticsOption} =this.props.DataOverviewModel;
        var myDate = new Date();
        if(e.dataIndex === 0){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsinfoID[0],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 1){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsinfoID[1],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 2){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsinfoID[2],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 3){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsinfoID[3],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 4){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsinfoID[4],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 5){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsinfoID[5],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 6){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsinfoID[6],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 7){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsinfoID[7],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 8){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsinfoID[8],month:myDate.getMonth()+1}});
        }else if(e.dataIndex === 9){
            dispatch({ type: "DataOverviewModel/getDetails",payload:{deptId:StatisticsOption.StatisticsinfoID[9],month:myDate.getMonth()+1}});
        }
        dispatch({ type: "DataOverviewModel/save",payload:{visible:true}});
      }

      handleCancel=()=>{
        const {dispatch}=this.props;
        dispatch({ type: "DataOverviewModel/save",payload:{visible:false}});
      }
      columns=[{
        title: '登录时间',
        dataIndex: 'loginTime',
        key: 'loginTime',
      },{
        title: '客户端IP',
        dataIndex: 'ip',
        key: 'ip',
      },{
        title: '用户名',
        dataIndex: 'username',
        key: 'username',
      }]

      onChangeTotal(rest) {
        const { dispatch } = this.props;
        dispatch({ type: "DataOverviewModel/getDetails",payload:{ page: rest[0],size: rest[1]}});
      }

    render(){
        const {pagination,total,serverDataOption,visible,getDetailsLIst} = this.props.DataOverviewModel;
        console.log( this.props.DataOverviewModel,"visible====",getDetailsLIst);
        const optionServer = _.cloneDeep(this.optionServer().option);
        const optionServerData = _.cloneDeep(this.optionServerData().option);
        const optionStatistics = _.cloneDeep(this.optionStatistics().option);
        const optionServerDataMonth = _.cloneDeep(this.optionServerDataMonth().option);
        const optionServerDataUser = _.cloneDeep(this.optionServerDataUser().option);
        //本月登陆用户排行Top10
        const onEvents = {
            'click': this.clickEchartsPie.bind(this)
        }
        //登陆用户数总排行Top10
        const onEventsQuery = {
            'click': this.clickEchartsPieQuery.bind(this)
        }
        //登陆用户月度统计
        const onEventsLogin = {
            'click': this.clickEchartsPieLogin.bind(this)
        }
        
        return (
            <div> 
                 <div className={styles.leftFour}>
                     <div>
                          <div className={styles.title}>用户登陆日记</div>
                           <Row>
                               <Col style={{margin:'20px' ,fontSize:"18"}} span={16}>登陆用户月度统计</Col>
                               <Col span={6} style={{margin:'20px'}} >
                                 {/* <MonthPicker  format={dateFormat} defaultValue={moment()} onChange={this.onChange.bind(this)}/> */}
                                 <Select  style={{ width: 200 }}  onChange={this.onChange.bind(this)}  defaultValue="2019" >
                                    <Option value="2019">2019</Option>
                                    <Option value="2018">2018</Option>
                                    <Option value="2017">2017</Option>
                                    <Option value="2016">2016</Option>
                                    <Option value="2015">2015</Option>
                                    <Option value="2014">2014</Option>
                                    <Option value="2013">2013</Option>
                                </Select>
                                 
                              </Col>
                            </Row>
                              <div className={styles.leftBottMax}>
                                  <ReactEcharts
                                      option={optionStatistics}
                                      style={{height: '300px', width: '88%'}}
                                      onEvents={onEventsLogin}
                                      // notMerge={false}
                                      // lazyUpdate={true}
                                      theme={"theme_name"}
                                  />
                              </div>
                      </div>
                      <div className={styles.leftFourTetel}>
                              <div className={styles.leftTetel}>本月登陆用户排行Top10</div>
                                  
                                  <div className={styles.leftBottMax}>
                                      <ReactEcharts
                                          option={optionServerDataMonth}
                                          style={{height: '450px', width: '88%'}}
                                          onEvents={onEvents}
                                          // notMerge={false}
                                          // lazyUpdate={true}
                                          theme={"theme_name"}
                                      />
                                 </div>
                      </div>
                      <div className={styles.leftFourTetel}>
                              <div className={styles.leftTetel}>登陆用户数总排行Top10</div>
                                  <div className={styles.leftBottMax}>
                                      <ReactEcharts
                                          option={optionServerDataUser}
                                          onEvents={onEventsQuery}
                                          style={{height: '450px', width: '88%'}}
                                          // notMerge={false}
                                          // lazyUpdate={true}
                                          theme={"theme_name"}
                                      />
                                 </div>
                      </div>
               </div>
  
                 <div className={styles.leftFourRight}>
                      <div className={styles.FourTitleText}>
                          <div className={styles.title}></div>
                          <div className={styles.leftTetel} style={{marginLeft: "3%"}}>接口调用月度统计</div>
                          <div className={styles.leftBottMax}>
                              <ReactEcharts
                                  option={optionServer}
                                  style={{height: '300px', width: '88%'}}
                                  // notMerge={false}
                                  // lazyUpdate={true}
                                  theme={"theme_name"}
                              />
                          </div>
                      </div>
                      <div className={styles.FourTitleText}>
                          <div className={styles.leftTetel} style={{marginLeft: "3%"}}>接口调用 Top10</div>
                          <div className={styles.leftBottMax}>
                              <ReactEcharts
                                      option={optionServerData}
                                      style={{height: '450px', width: '88%'}}
                                      // notMerge={false}
                                      // lazyUpdate={true}
                                      theme={"theme_name"}
                                  />
                          </div>
                      </div>
                 </div>
                 <Modal
                    title="详情列表"
                    visible={visible}
                    width={600}
                    footer={null}
                    onCancel={this.handleCancel.bind(this)} 
                    destroyOnClose>
                  
                      <Table dataSource={getDetailsLIst} columns={this.columns} 
                            pagination={{
                                total:total,
                                onChange: (...rest) => this.onChangeTotal(rest)
                                }}
                      />
                   
                   
                </Modal>
            </div>
     )
    }
}


export default connect(({ DataOverviewModel }) => ({
    DataOverviewModel
}))(index)
