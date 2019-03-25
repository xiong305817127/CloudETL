import React from 'react'
import { Table, Form, Button } from 'antd';
import { connect } from 'dva';
import styles from "./style.less";
import ReactEcharts from 'echarts-for-react';
import _ from "lodash";
class index extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            optionBor:[]
        };
    }


    //调度任务 饼图表数据
    optionBor=()=>{
        const {optionTskelist,optionTske,NodeTotalList} =this.props.DataOverviewModel;
        let Propor = optionTske.successTask + optionTske.runningTask +optionTske.failTask;
        return {
            option :{
                tooltip: { trigger: 'item', formatter: "{a} <br/>{b}: {c} ({d}%)"},
                color:['#009943','#ed7d31'],
                legend: { x : 'center', y : 'bottom', data:['成功','失败'] },
                series: [
                    { name:'访问来源', type:'pie', radius: ['50%', '70%'],avoidLabelOverlap: false,
                        label: {
                            normal: {
                                        show: true,
                                        position: 'center',
                                        formatter:function (argument) {
                                            var html;
                                            html='ETL任务总次数\r\n\r\n'+Propor;
                                            return html;
                                        },
                                        textStyle:{
                                           fontSize: 16,
                                            color:'#000'
                                        }
                                    },
                        },
                        labelLine: {  normal: { show: false }
                        },
                        data:optionTskelist
                    }
                ]
            },
        }
    }

     //处理数据 折线表数据
     optionBoDatar=()=>{
        const {optionTskeData} =this.props.DataOverviewModel;
        return {
            option :{
                tooltip : { trigger: 'axis', axisPointer : {type : 'shadow' }},
                grid: { left: '5%', right: '2%',bottom: '3%',containLabel: true},
                color:['#448acf'],
                xAxis : [
                    {
                        type : 'category',
                        data :optionTskeData.dataSouerMonth,
                        axisTick: { alignWithLabel: true }
                    }
                ],
                yAxis : [ { type : 'value' }],
                series : [{ name:'直接访问', type:'bar', barWidth: '60%', data:optionTskeData.dataSouerTotal}]
            },
        }
    };
        //调度任务 饼图表数据
        optionBorNode=()=>{
            const {SchedulingQualityList} =this.props.DataOverviewModel;
            return {
                option:{
                    tooltip : {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c} ({d}%)"
                    },
                    color:['#009943','#ed7d31'],
                    legend: {
                        bottom: 10,
                        left: 'center',
                        data: ['成功', '失败']
                    },
                    series : [
                        {
                            type: 'pie',
                            radius : '65%',
                            center: ['50%', '50%'],
                            selectedMode: 'single',
                            data:SchedulingQualityList,
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }
                    ]
                },
            }
        }
        
        
     //调度任务 饼图表数据
     optionBorQuily=()=>{
        const {NodeTotal,NodeTotalList} =this.props.DataOverviewModel;
        return {
            option :{
                tooltip: { trigger: 'item', formatter: "{a} <br/>{b}: {c} ({d}%)"},
                color:['#009943','#ed7d31'],
                legend: { x : 'center', y : 'bottom', data:['成功','失败'] },
                series: [
                    { name:'访问来源', type:'pie', radius: ['50%', '70%'],avoidLabelOverlap: false,
                        label: {
                            normal: {
                                show: true,
                                position: 'center',
                                formatter:function (argument) {
                                    var html;
                                    html='质量分析数据总量\r\n\r\n'+NodeTotalList.total;
                                    return html;
                                },
                                textStyle:{
                                   fontSize: 17,
                                    color:'#000'
                                }
                            },
                         
                        },
                        labelLine: {  normal: { show: false }
                        },
                        data:NodeTotal
                    }
                ]
            },
        }
    }


    columnsSuccess = [{
        dataIndex: 'name',
        key: 'name',
        title: '任务名称'
      }, {
        title: '执行时间',
        dataIndex: 'beginStr',
        key: 'beginStr'
      }];

      columnsQuily = [{
        dataIndex: 'name',
        key: 'name',
        title: '任务名称'
      }, {
        title: '执行时间',
        dataIndex: 'beginStr',
        key: 'beginStr'
      }];

      columns = [{
        dataIndex: 'taskName',
        key: 'taskName',
        title: '任务名称'
      }, {
        title: '合格数量',
        dataIndex: 'succTotal',
        key: 'succTotal'
      }, {
        title: '不合格数量',
        dataIndex: 'errTotal',
        key: 'errTotal'
      }];

      componentDidMount(){
          const {dispatch}=this.props;
          dispatch({ type: "DataOverviewModel/getScheduling"});
          dispatch({ type: "DataOverviewModel/getTaskExecLinesByMonth"});
          dispatch({ type: "DataOverviewModel/getRenterSuccessTasks"});
          dispatch({ type: "DataOverviewModel/getNodeTotal"});
          dispatch({ type: "DataOverviewModel/getQuailyRenterSuccessTasks"});
          dispatch({ type: "DataOverviewModel/getTaskExecTimesByMonth"});
          dispatch({ type: "DataOverviewModel/getTaskByNodeType"});
          dispatch({ type: "DataOverviewModel/SchedulingQuality"});
      }



    render(){
          const {RenterSuccessTasks,NodeTotalList,QuailyRenterSuccessTasksSuccess,QuailyRenterSuccessTasks,optionTske,optionTskeData,SchedulingQuality}=this.props.DataOverviewModel;
          const optionBer = _.cloneDeep(this.optionBor().option);
          const optionData = _.cloneDeep(this.optionBoDatar().option);
          const optionBorNode = _.cloneDeep(this.optionBorNode().option);
          const optionBorQuily = _.cloneDeep(this.optionBorQuily().option);
          let Proportion = optionTske.successTask + optionTske.runningTask+optionTske.failTask;
          let Propors = optionTske.successTask / Proportion;
          let Propor = SchedulingQuality.successTask + SchedulingQuality.runningTask +SchedulingQuality.failTask;
          let ProportionNode = SchedulingQuality.successTask / Propor;
    
          let ption =  Math.round(Propors*10000)/100.00;
          let ptionNodes =  Math.round(ProportionNode*10000)/100.00;
        return (
            <div> 
                 <div className={styles.left}>
                    <div className={styles.title}>调度任务</div>
                    <div className={styles.leftBott}>
                    <h4 className={styles.leftTitle}>
                        任务个数：<label className={styles.leftColor}>{optionTske.taskTotal?optionTske.taskTotal:"0"}</label> &nbsp;&nbsp;
                        成功次数：<label className={styles.leftColor}>{optionTske.successTask?optionTske.successTask:"0"}</label> &nbsp;&nbsp;
                        占比：<label className={styles.leftColor}>{ption?ption:"0"}%</label>
                    </h4>
                      <ReactEcharts
                          option={optionBer}
                          style={{height: '300px', width: '88%'}}
                          // notMerge={false}
                          // lazyUpdate={true}
                          theme={"theme_name"}
                      />
                    </div>
                     <div className={styles.leftBott}>
                        <h3 className={styles.leftTitleSccess}>成功任务</h3>
                        <Table bordered className={styles.bordered} columns={this.columnsSuccess} dataSource={RenterSuccessTasks} pagination={false}/>
                     </div>
                 </div>
                 <div className={styles.left}>
                  <div className={styles.title}>处理数据</div>
                      <div className={styles.leftBottMax}>
                      <h3 className={styles.leftTitleLeft}>处理数据总量：<label className={styles.leftColor}>{optionTskeData.sum?optionTskeData.sum:"0"}</label></h3>
                          <ReactEcharts
                              option={optionData}
                              style={{height: '300px', width: '88%'}}
                              // notMerge={false}
                              // lazyUpdate={true}
                              theme={"theme_name"}
                          />
                      </div>
                 </div>
                 <div className={styles.left}>
                 <div className={styles.title}>质量分析任务</div>
                      <div className={styles.leftBott}>
                         <h4 className={styles.leftTitle}>
                              任务个数：<label className={styles.leftColor}>{SchedulingQuality.taskTotal?SchedulingQuality.taskTotal:"0"}</label> &nbsp;&nbsp;
                              成功次数：<label className={styles.leftColor}>{SchedulingQuality.successTask?SchedulingQuality.successTask:"0"}</label>&nbsp;&nbsp; 
                              占比：<label className={styles.leftColor}>{ptionNodes?ptionNodes:"0"}%</label>
                          </h4>
                              <ReactEcharts
                                  option={optionBorNode}
                                  style={{height: '300px', width: '88%'}}
                                  // notMerge={false}
                                  // lazyUpdate={true}
                                  theme={"theme_name"}
                              />
                          </div>
                          <div className={styles.leftBott}>
                              <h3 className={styles.leftTitleSccess}>成功任务</h3>
                              <Table bordered className={styles.bordered} columns={this.columnsQuily} dataSource={QuailyRenterSuccessTasksSuccess} pagination={false}/>
                          </div>
                 </div>
                 <div className={styles.left}>  
                    <div className={styles.title}>质量分析数据</div>
                      <div className={styles.leftBott}>
                      <h3 className={styles.leftTitle}>成功次数：<label className={styles.leftColor}>{NodeTotalList.successTotal?NodeTotalList.successTotal:"0"}</label></h3>
                              <ReactEcharts
                                  option={optionBorQuily}
                                  style={{height: '300px', width: '88%'}}
                                  // notMerge={false}
                                  // lazyUpdate={true}
                                  theme={"theme_name"}
                              />
                      </div>
                      <div className={styles.leftBott}>
                          <h3 className={styles.leftTitleSccess}>合格数据</h3>
                          <Table bordered className={styles.bordered} columns={this.columns} dataSource={QuailyRenterSuccessTasks} pagination={false}/>
                      </div>
                 </div>
                 {/*  */}
            </div>
     )
    }
}



export default connect(({ DataOverviewModel }) => ({
    DataOverviewModel
}))(index)
