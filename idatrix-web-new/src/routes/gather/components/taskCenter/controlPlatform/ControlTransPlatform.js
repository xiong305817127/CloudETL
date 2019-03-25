/**
 * Created by Administrator on 2018/1/27.
 */
/**
 * Created by Administrator on 2017/12/22.
 */
import React from "react";
import { Button, Modal } from "antd";
import { runStatus, pauseStatus } from "../../../constant";
import { stopStatus, errorStatus } from "../../../constant";
import { connect } from "dva";
import ReactEcharts from "echarts-for-react";
import Tools from "../../config/Tools";
import Style from "./ControlPlatform.css";
import * as gatherFuncs from "../../../../../services/gather";

const ButtonGroup = Button.Group;
let planePath =
  "path://M1705.06,1318.313v-89.254l-319.9-221.799l0.073-208.063c0.521-84.662-26.629-121.796-63.961-121.491c-37.332-0.305-64.482,36.829-63.961,121.491l0.073,208.063l-319.9,221.799v89.254l330.343-157.288l12.238,241.308l-134.449,92.931l0.531,42.034l175.125-42.917l175.125,42.917l0.531-42.034l-134.449-92.931l12.238-241.308L1705.06,1318.313z";

let Timer2 = null;

/*请求间隔时间*/
let spaceTime = 2000;

let timeTicket = null;

class ControlTransPlatform extends React.Component {
  constructor(props) {
    super(props);

    var grids = [];
    var xAxes = [];
    var yAxes = [];
    var series = [];

    grids.push({
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      show: true,
      borderWidth: 0,
      shadowColor: "rgba(0, 0, 0, 0.3)",
      shadowBlur: 2
    });
    xAxes.push({
      type: "value",
      show: false,
      min: 0,
      max: 1,
      gridIndex: 0
    });
    yAxes.push({
      type: "value",
      show: false,
      min: 0,
      max: 1,
      gridIndex: 0
    });
    series.push(
      {
        type: "lines",
        coordinateSystem: "cartesian2d",
        xAxisIndex: 0,
        zlevel: 1,
        yAxisIndex: 0,
        data: [],
        effect: {
          show: false, //开启动画
          period: 3,
          trailLength: 0.1,
          color: "#1780de",
          symbolSize: 8
        },
        lineStyle: {
          normal: {
            color: "#1780de",
            width: 0,
            curveness: 0.2
          }
        },
        polyline: false,
        showSymbol: false,
        animationEasing: "eChart线条",
        animationDuration: 1000
      },
      {
        type: "lines",
        coordinateSystem: "cartesian2d",
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
            color: "#1780de",
            width: 1,
            opacity: 0.4,
            curveness: 0.2
          }
        },
        polyline: false,
        showSymbol: false,
        animationEasing: "eChart线条",
        animationDuration: 1000
      },
      {
        type: "effectScatter",
        coordinateSystem: "cartesian2d",
        data: [],
        zlevel: 2,
        rippleEffect: {
          period: 1, //开启动画6  2.5
          scale: 1,
          brushType: "fill"
        },
        label: {
          normal: {
            show: true,
            position: "bottom",
            formatter: "{b}",
            distance: 15
          }
        },
        symbolSize: 40,
        itemStyle: {
          normal: {
            color: "#fff"
          }
        }
      }
    );

    this.state = {
      option: {
        tooltip: {
          trigger: "item",
          formatter: function(o) {
            const { seriesType } = o;
            if (seriesType === "lines") {
              return "线名称：" + o.name;
            } else {
              return (
                "名称：" +
                o.name +
                "<br />执行状态：" +
                o.data.statusDescription +
                "<br />执行时间：" +
                o.data.seconds +
                "<br />执行速度：" +
                o.data.speed +
                "<br />错误数量：" +
                o.data.errCount +
                "<br />错误原因：<div style=" +
                "max-width:600px;overflow:" +
                "hidden" +
                ";text-overflow:" +
                "ellipsis" +
                "" +
                ">" +
                o.data.logText +
                "</div>"
              );
            }
          }
        },
        grid: grids,
        xAxis: xAxes,
        yAxis: yAxes,
        series: series
      },
      startBtn: false,
      endBtn: true
    };
  }

  handleCancel() {
    const { dispatch } = this.props;
    dispatch({
      type: "controltransplatform/hideModel",
      visible: false
    });
  }

  /*静态渲染*/
  staticRender(hopData, stepData, xyMax, bool) {
    if (timeTicket) {
      clearInterval(timeTicket);
      timeTicket = null;
    }

    const xAxis = this.state.option.xAxis;
    xAxis[0] = { ...xAxis[0], max: xyMax[0] };

    const yAxis = this.state.option.yAxis;
    yAxis[0] = { ...yAxis[0], max: xyMax[1] };

    const series = this.state.option.series;

    let itemData = [];

    for (let index of stepData) {
      itemData.push({
        name: index.name,
        value: index.value,
        symbol: "image://" + Tools[index.type].imgData,
        statusDescription: "等待执行",
        seconds: 0,
        speed: 0,
        logText: "无",
        errCount: 0,
        itemStyle: {
          normal: {
            shadowColor: "white",
            shadowBlur: 20
          }
        }
      });
    }

    const effect1 = { ...series[0].effect, show: bool };
    const effect2 = { ...series[1].effect, show: bool };
    const rippleEffect = {
      ...series[2].rippleEffect,
      period: bool ? 6 : 1,
      scale: bool ? 2.5 : 1
    };

    series[0] = { ...series[0], data: hopData, effect: effect1 };
    series[1] = { ...series[1], data: hopData, effect: effect2 };
    series[2] = { ...series[2], data: itemData, rippleEffect: rippleEffect };

    const option = { ...this.state.option, xAxis, yAxis, series };

    this.setState({
      option: option,
      startBtn: bool,
      endBtn: !bool
    });
  }

  /*动态渲染*/
  dynamicRender(obj) {
    if (timeTicket) {
      clearInterval(timeTicket);
      timeTicket = null;
    }

    const { bool, errorData, status } = obj;

    let selectColor = "white";

    if (status === "error") {
      selectColor = "red";
    } else if (status === "warn") {
      selectColor = "yellow";
    }

    const series = this.state.option.series;

    const effect1 = { ...series[0].effect, show: bool };
    const effect2 = { ...series[1].effect, show: bool };
    const rippleEffect = {
      ...series[2].rippleEffect,
      period: bool ? 6 : 1,
      scale: bool ? 2.5 : 1
    };
    const data = series[2].data;

    series[0] = { ...series[0], effect: effect1 };
    series[1] = { ...series[1], effect: effect2 };

    if (errorData) {
      for (let index of errorData) {
        const { logText, errCount, stepName } = index;
        for (let item of data) {
          if (stepName === item.name) {
            if (errCount != 0) {
              item.itemStyle = {
                normal: {
                  shadowColor: selectColor,
                  shadowBlur: 20
                }
              };
            }

            item.logText = logText ? logText : "无";
            item.errCount = errCount;
          }
        }
      }
    }

    series[2] = { ...series[2], rippleEffect: rippleEffect, data: data };
    const option = { ...this.state.option, series };

    this.setState({
      option: option
    });
  }

  /*动态渲染消息*/
  dynamicInfo(statusData, bool) {
    if (!timeTicket) {
      let currentIndex = -1;
      let echarts_instance = this.echarts_react.getEchartsInstance();

      timeTicket = setInterval(function() {
        var dataLen = series[2].data.length;

        currentIndex = (currentIndex + 1) % dataLen;

        // 显示 tooltip
        echarts_instance.dispatchAction({
          type: "showTip",
          seriesIndex: 2,
          dataIndex: currentIndex
        });
      }, 1000);
    }

    if (bool) {
      if (timeTicket) {
        clearInterval(timeTicket);
        timeTicket = null;
      }
    }

    const series = this.state.option.series;
    const data = series[2].data;
    for (let index of statusData) {
      const { statusDescription, seconds, speed, stepName } = index;
      for (let item of data) {
        if (stepName === item.name) {
          item.statusDescription = statusDescription;
          item.seconds = seconds;
          item.speed = speed;
        }
      }
    }

    series[2] = { ...series[2], data: data };
    const option = { ...this.state.option, series };

    this.setState({
      option: option
    });
  }

  /*动态显示消息*/

  /*执行中的状态更新*/
  sendTransEvent({ executionId }) {
    const { getTrans_exec_step_measure } = this.props.transheader.methods;
    getTrans_exec_step_measure({ executionId }).then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        this.dynamicInfo(data);
      } else {
        if (Timer2) {
          clearTimeout(Timer2);
        }
      }
    });
  }

  /*开动态执行*/
  autoRunView(transName) {
    this.setState({
      startBtn: true,
      endBtn: false
    });

    const { getTrans_exec_id } = this.props.transheader.methods;
    getTrans_exec_id({ name: transName }).then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        const { executionId } = data;

        if (executionId) {
          this.timeTest(this.sendTransEvent.bind(this), spaceTime, {
            name: transName,
            executionId: executionId
          });
        }
      }
    });
  }

  componentWillReceiveProps(nextProps) {
    const {
      hopData,
      stepData,
      xyMax,
      actionType,
      transName,
      owner 
    } = nextProps.controltransplatform;

		const { getTrans_status} =  owner == "" ? this.props.transheader.methods : gatherFuncs;
		
		if(!getTrans_status) return;
    if (actionType === "openView") {
			
      getTrans_status({ name: transName, owner: owner  }).then(res => {
        const { code, data } = res.data;
        const { status } = data;
        if (code === "200" && runStatus.has(status)) {
          this.staticRender(hopData, stepData, xyMax, true);
          this.autoRunView(transName);
        } else {
          this.staticRender(hopData, stepData, xyMax, false);
        }
      });
    } else if (actionType === "runView") {
      getTrans_status({ name: transName }).then(res => {
        const { code, data } = res.data;
        const { status } = data;
        this.dynamicRender({ bool: true });
        if (code === "200" && runStatus.has(status)) {
          this.autoRunView(transName);
        } else {
          this.autoRunView(transName);
        }
      });
    }
  }

  timeTest(func, w, obj) {
    const {
      getTrans_status,
      getTrans_exec_step_status,
      getTrans_exec_step_measure
    } = this.props.transheader.methods;

    var interv = () => {
      getTrans_status({ name: obj.name }).then(res => {
        const { code, data } = res.data;
        const { status } = data;
        if (code === "200" && runStatus.has(status)) {
          Timer2 = setTimeout(interv, w);
          try {
            func({ ...obj, status });
          } catch (e) {
            throw e.toString();
          }
        } else {
          this.setState({
            startBtn: false,
            endBtn: true
          });

          getTrans_exec_step_status({ executionId: obj.executionId }).then(
            res => {
              const { code, data } = res.data;

              if (code === "200") {
                if (data) {
                  getTrans_exec_step_measure({executionId: obj.executionId}).then(res => {
                    const { code, data } = res.data;
                    if (code === "200") {
                      this.dynamicInfo(data, true);
                    } else {
                      if (Timer2) {
                        clearTimeout(Timer2);
                      }
                    }
                  });

                  if (stopStatus.has(status)) {
                    this.dynamicRender({
                      bool: false,
                      errorData: data,
                      status: "warn"
                    });
                  } else if (errorStatus.has(status)) {
                    this.dynamicRender({
                      bool: false,
                      errorData: data,
                      status: "error"
                    });
                  } else if (pauseStatus.has(status)) {
                    return;
                  } else {
                    this.dynamicRender({
                      bool: false,
                      errorData: data,
                      status: "normal"
                    });
                  }
                }
              }
            }
          );
        }
      });
    };
    setTimeout(interv, w);
  }

  /*手动运行转换*/

  handleRunTrans() {
    const { transName } = this.props.controltransplatform;
    const { dispatch } = this.props;
    console.log(
      this.props.controltransplatform,
      "this.props.controltransplatform"
    );
    dispatch({
      type: "runtrans/queryExecuteList",
      payload: {
        visible: true,
        actionName: transName,
        runModel: "viewRun"
      }
    });
  }

  handleEndTrans() {
    const { dispatch } = this.props;
    const { transName } = this.props.controltransplatform;

    dispatch({
      type: "controltransplatform/stopTrans",
      payload: {
        actionName: transName
      }
    });
  }

  render() {
    console.log(this.props, "this.props.controltransplatform");
    const { visible, transName } = this.props.controltransplatform;
    const { option, startBtn, endBtn } = this.state;

    return (
      <Modal
        visible={visible}
        title={transName}
        wrapClassName="vertical-center-modal gather_control_view"
        maskClosable={false}
        width={1200}
        onClose={this.handleCancel.bind(this)}
        footer={[
          <Button
            key="back"
            size="large"
            onClick={() => {
              this.handleCancel();
            }}
          >
            关闭
          </Button>
        ]}
        onCancel={() => {
          this.handleCancel();
        }}
      >
        <div className={Style.viewBtn}>
          <ButtonGroup>
            <Button
              disabled={startBtn}
              onClick={this.handleRunTrans.bind(this)}
            >
              开始执行
            </Button>
            <Button disabled={endBtn} onClick={this.handleEndTrans.bind(this)}>
              终止执行
            </Button>
          </ButtonGroup>
        </div>
        <div className={Style.viewContent}>
          <div className={Style.viewCanvas}>
            <ReactEcharts
              option={option}
              style={{ height: "640px", width: "100%" }}
              notMerge={true}
              lazyUpdate={true}
              ref={e => {
                this.echarts_react = e;
              }}
              theme="my_theme"
              onChartReady={this.onChartReadyCallback}
              onEvents={this.EventsDict}
            />
          </div>
        </div>
      </Modal>
    );
  }
}

export default connect(({ controltransplatform, transheader }) => ({
  controltransplatform,
  transheader
}))(ControlTransPlatform);
