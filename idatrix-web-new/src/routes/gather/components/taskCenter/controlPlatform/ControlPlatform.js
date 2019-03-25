/**
 * Created by Administrator on 2017/12/22.
 */
import React from 'react';
import { Button, Modal,Alert,Row,Col,message} from 'antd';
import {  getTrans_status,getTrans_exec_step_status,getTrans_exec_id,getTrans_exec_step_measure } from  '../../../../../services/gather';
import { getJob_status,getJob_exec_step_status,getJob_exec_step_measure,getJob_exec_id } from  '../../../../../services/gather1';
import { runStatus,delayTime,pauseStatus } from '../../../constant';
import Empower from '../../../../../components/Empower';
import { stopStatus,errorStatus} from '../../../constant';
import { connect } from 'dva';
import ReactEcharts from 'echarts-for-react';
import Tools from '../../config/Tools';
import Style from './ControlPlatform.css';
const ButtonGroup = Button.Group;

let planePath = 'path://M1705.06,1318.313v-89.254l-319.9-221.799l0.073-208.063c0.521-84.662-26.629-121.796-63.961-121.491c-37.332-0.305-64.482,36.829-63.961,121.491l0.073,208.063l-319.9,221.799v89.254l330.343-157.288l12.238,241.308l-134.449,92.931l0.531,42.034l175.125-42.917l175.125,42.917l0.531-42.034l-134.449-92.931l12.238-241.308L1705.06,1318.313z';

let Timer2 = null;

/*请求间隔时间*/
let spaceTime = 2000;

let timeTicket = null;

class ControPlatform extends React.Component{

  constructor(props){
    super(props);

    var grids = [];
    var xAxes = [];
    var yAxes = [];
    var series = [];

    grids.push({
      top:0,
      left:0,
      right:0,
      bottom:0,
      show: true,
      borderWidth: 0,
      shadowColor: 'rgba(0, 0, 0, 0.3)',
      shadowBlur: 2
    });
    xAxes.push({
      type: 'value',
      show: false,
      min: 0,
      max: 1,
      gridIndex: 0
    });
    yAxes.push({
      type: 'value',
      show: false,
      min: 0,
      max: 1,
      gridIndex: 0
    });
    series.push(
      {
        type: 'lines',
        coordinateSystem:'cartesian2d',
        xAxisIndex: 0,
        zlevel: 1,
        yAxisIndex: 0,
        data: [],
        effect: {
          show: false,  //开启动画
          period: 3,
          trailLength: 0.1,
          color: '#1780de',
          symbolSize: 8
        },
        lineStyle: {
          normal: {
            color: '#1780de',
            width: 0,
            curveness: 0.2
          }
        },
        polyline:false,
        showSymbol: false,
        animationEasing: "eChart线条",
        animationDuration: 1000
      },
      {
        type: 'lines',
        coordinateSystem:'cartesian2d',
        xAxisIndex: 0,
        yAxisIndex: 0,
        zlevel: 2,
        data: [],
        effect: {
          show: false, //开启动画
          period: 3,
          trailLength: 0,
          //symbol: 'image://',
          symbol: planePath,
          symbolSize: 15
        },
        lineStyle: {
          normal: {
            color: '#1780de',
            width: 1,
            opacity: 0.4,
            curveness: 0.2
          }
        },
        polyline:false,
        showSymbol: false,
        animationEasing: "eChart线条",
        animationDuration: 1000
      },
      {
        type: 'effectScatter',
        coordinateSystem: 'cartesian2d',
        data: [],
        zlevel: 2,
        rippleEffect: {
          period: 1,   //开启动画6  2.5
          scale: 1,
          brushType: 'fill'
        },
        label: {
          normal: {
            show: true,
            position: 'bottom',
            formatter: '{b}',
            distance:15
          }
        },
        symbolSize: 40,
        itemStyle: {
          normal: {
            color: '#fff',

          }
        }
      }
    );

    this.state = {
      option :{
        tooltip: {
          trigger: 'item',
          formatter: function(o) {
            const { seriesType } = o;
            if(seriesType === "lines"){
                return "线名称："+o.name
            }else{
              return "名称："+o.name
                +"<br />执行状态：" +o.data.statusDescription
                +"<br />执行时间：" +o.data.seconds
                +"<br />执行速度：" +o.data.speed
                +"<br />错误数量：" +o.data.errCount
                +"<br />错误原因：<div style="+"max-width:600px;overflow:"+"hidden"+";text-overflow:"+"ellipsis"+""+">"+o.data.logText+"</div>";
            }
          }
        },
        grid: grids,
        xAxis: xAxes,
        yAxis: yAxes,
        series: series
      },
      startBtn:false,
      endBtn:true,
      viewType:"trans"
    }
  }

  setModelHide(){
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    })
  };

  handleCancel(){
    const { dispatch } = this.props;
    dispatch({
      type:'controlplatform/hideModel',
      visible:false
    })
  };

  /*静态渲染*/
  staticRender(hopData,stepData,xyMax,bool){

    if(timeTicket){
      clearInterval(timeTicket);
      timeTicket = null;
    }

    const xAxis = this.state.option.xAxis;
    xAxis[0] = {...xAxis[0],max:xyMax[0]};

    const yAxis = this.state.option.yAxis;
    yAxis[0] = {...yAxis[0],max:xyMax[1]};

    const series = this.state.option.series;

    let itemData = [];

    for(let index of stepData){

      itemData.push({
        name:index.name,
        value:index.value,
        symbol:"image://"+Tools[index.type].imgData,
        statusDescription:"等待执行",
        seconds:0,
        speed:0,
        logText:"无",
        errCount:0,
        itemStyle:{
          normal:{
            shadowColor: "white",
            shadowBlur: 20
          }
        }
      })
    }

    const effect1 = {...series[0].effect,show:bool};
    const effect2 = {...series[1].effect,show:bool};
    const rippleEffect = {...series[2].rippleEffect,period: bool?6:1, scale:bool?2.5:1};

    series[0] = {...series[0],data:hopData,effect:effect1};
    series[1] = {...series[1],data:hopData,effect:effect2};
    series[2] = {...series[2],data:itemData,rippleEffect:rippleEffect};

    const option = {...this.state.option,xAxis,yAxis,series};

    this.setState({
      option:option,
      startBtn:bool,
      endBtn:!bool
    })
  };

  /*动态渲染*/
  dynamicRender(obj){

    if(timeTicket){
      clearInterval(timeTicket);
      timeTicket = null;
    }


    const { bool,errorData,status} = obj;

    let selectColor = "white";

    if(status === "error"){
      selectColor = "red";
    }else if(status === "warn"){
      selectColor = "yellow";
    }

    const series = this.state.option.series;

    const effect1 = {...series[0].effect,show:bool};
    const effect2 = {...series[1].effect,show:bool};
    const rippleEffect = {...series[2].rippleEffect,period: bool?6:1, scale:bool?2.5:1};
    const data = series[2].data;

    series[0] = {...series[0],effect:effect1};
    series[1] = {...series[1],effect:effect2};

    if(errorData){
      for(let index of errorData){
        const {logText, errCount,stepName} = index;
        for(let item of data){
          if(stepName === item.name){

            if(errCount != 0){
              item.itemStyle = {
                normal:{
                  shadowColor: selectColor,
                  shadowBlur: 20
                }
              }
            }

            item.logText = logText?logText:"无";
            item.errCount = errCount;
          }
        }
      }
    }

    series[2] = {...series[2],rippleEffect:rippleEffect,data:data};
    const option = {...this.state.option,series};

    this.setState({
      option:option
    });

  };

  /*动态渲染消息*/
  dynamicInfo(statusData,bool){

    if(!timeTicket){
      let currentIndex = -1;
      let echarts_instance = this.echarts_react.getEchartsInstance();

      timeTicket = setInterval(function() {
        var dataLen = series[2].data.length;

        currentIndex = (currentIndex + 1) % dataLen;

        // 显示 tooltip
        echarts_instance.dispatchAction({
          type: 'showTip',
          seriesIndex: 2,
          dataIndex: currentIndex
        });
      }, 1000);
    }

    if(bool){
      if(timeTicket){
        clearInterval(timeTicket);
        timeTicket = null;
      }
    }

      const series = this.state.option.series;
      const data = series[2].data;
      for(let index of statusData){

        const {statusDescription, seconds, speed,stepName} = index;
          for(let item of data){
              if(stepName === item.name){
                  item.statusDescription = statusDescription;
                  item.seconds = seconds;
                  item.speed = speed;
              }
          }
      }

    series[2] = {...series[2],data:data};
    const option = {...this.state.option,series};

     this.setState({
        option:option
     })
  }

  /*动态显示消息*/


  /*执行中的状态更新*/
  sendTransEvent({executionId}){
    const { viewType } = this.state;

    const get_exec_step_measure = viewType === "trans"?getTrans_exec_step_measure:getJob_exec_step_measure;

    get_exec_step_measure(executionId).then((res)=>{
      const {code,data } = res.data;
      if(code === "200"){
          this.dynamicInfo(data);
      }else{
        if(Timer2){
          clearTimeout(Timer2);
        }
      }
    });
  };

  /*开动态执行*/
  autoRunView(transName){
      this.setState({
          startBtn:true,
          endBtn:false
      });

    const { viewType } = this.state;

    const get_exec_id = viewType==="trans"?getTrans_exec_id:getJob_exec_id;

      get_exec_id(transName).then((res)=>{
       const { code,data } = res.data;
       if(code === "200" ){
         const {executionId} = data;

         if(executionId){
           this.timeTest(this.sendTransEvent.bind(this),spaceTime,{name:transName,executionId:executionId});
         }
       }
     });


  }


  componentWillReceiveProps(nextProps){
      const { hopData,stepData,xyMax,actionType,transName,viewType } = nextProps.controlplatform;

      const get_status = viewType === "trans"?getTrans_status:getJob_status;

      this.setState({
        viewType:viewType
      });

      if(actionType === "openView" ){

          get_status(transName).then((res)=>{

              const {code, data} = res.data;
              const {status} = data;
              if(code === "200" && runStatus.has(status)){
                this.staticRender(hopData,stepData,xyMax,true);
                this.autoRunView(transName);
              }else{
                this.staticRender(hopData,stepData,xyMax,false);
              }
          })
      }else if(actionType === "runView"){
        get_status(transName).then((res)=>{
          const {code, data} = res.data;
          const {status} = data;
          this.dynamicRender({bool:true});
          if(code === "200" && runStatus.has(status)){
            this.autoRunView(transName);
          }else{
            this.autoRunView(transName);
          }
        })
      }
  }

  timeTest(func, w,obj){
    const { viewType } = this.state;

    const get_status = viewType === "trans"?getTrans_status:getJob_status;
    const get_exec_step_status = viewType === "trans"?getTrans_exec_step_status:getJob_exec_step_status;
    const get_exec_step_measure = viewType === "trans"?getTrans_exec_step_measure:getJob_status;

    var interv = ()=> {
      get_status(obj.name).then((res)=> {
        const {code, data} = res.data;
        const {status} = data;
        if (code === "200" && runStatus.has(status)) {
          Timer2 = setTimeout(interv, w);
          try {
            func({...obj, status});
          }
          catch (e) {
            throw e.toString();
          }
        } else {
          this.setState({
            startBtn:false,
            endBtn:true
          });

          get_exec_step_status({executionId: obj.executionId}).then((res)=> {
            const {code, data} = res.data;

            if (code === "200") {
              if (data) {
                get_exec_step_measure(obj.executionId).then((res)=>{
                  const {code,data } = res.data;
                  if(code === "200"){
                    this.dynamicInfo(data,true);
                  }else{
                    if(Timer2){
                      clearTimeout(Timer2);
                    }
                  }
                });

                if (stopStatus.has(status)) {
                  this.dynamicRender({bool:false,errorData:data,status:"warn"});
                } else if (errorStatus.has(status)) {
                  this.dynamicRender({bool:false,errorData:data,status:"error"});
                } else if (pauseStatus.has(status)) {
                  return;
                } else {
                  this.dynamicRender({bool:false,errorData:data,status:"normal"});
                }
              }
            }
          });
        }
      })
    };
    setTimeout(interv, w);
  };

  /*手动运行转换*/

  handleRunTrans(){
    const { transName } = this.props.controlplatform;
    const { dispatch } = this.props;
    const { viewType } = this.state;

    if(viewType === "trans"){
      dispatch({
        type: 'runtrans/queryExecuteList',
        payload:{
          visible: true,
          actionName: transName,
          runModel:"viewRun"
        }
      });
    }else{
      dispatch({
        type: 'runjob/queryServerList',
        payload:{
          visible: true,
          actionName: transName,
          runModel:"viewRun"
        }
      });
    }
  };

  handleEndTrans(){
    const { viewType } = this.state;
    const { dispatch } = this.props;
    const { transName } = this.props.controlplatform;

    if(viewType === "trans"){
      dispatch({
        type: 'controlplatform/stopTrans',
        payload:{
          visible: true,
          actionName: transName,
          runModel:"viewRun"
        }
      })
    }else{
      dispatch({
        type: 'controlplatform/stopTrans',
        payload:{
          visible: true,
          actionName: transName,
          runModel:"viewRun"
        }
      })
    }
  }


  render(){

    const { visible,transName } = this.props.controlplatform;

    const { option,startBtn,endBtn } = this.state;

    return(
      <Modal
        visible={visible}
        title={transName}
        wrapClassName="vertical-center-modal gather_control_view"
        maskClosable={false}
        width={1200}
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={()=>{this.setModelHide()}}>
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={()=>{this.handleCancel();}}>取消</Button>,
                ]}
      >
        <div className={Style.viewBtn}>
          <ButtonGroup >
            <Button disabled={startBtn} onClick={this.handleRunTrans.bind(this)}>开始执行</Button>
            <Button disabled={endBtn} onClick={this.handleEndTrans.bind(this)}>终止执行</Button>
          </ButtonGroup>
        </div>
        <div className={Style.viewContent}>
          <div   className={Style.viewCanvas} >
            <ReactEcharts
              option={option}
              style={{height: '640px',width:'100%'}}


              notMerge={true}
              lazyUpdate={true}
              ref={(e) => { this.echarts_react = e;  }}
              theme="my_theme"
              onChartReady={this.onChartReadyCallback}
              onEvents={this.EventsDict}
            />
          </div>
        </div>
      </Modal>
    )
  }
}

export default connect(({controlplatform})=>({
  controlplatform
}))(ControPlatform);
