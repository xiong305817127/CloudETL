/**
 * Created by Administrator on 2017/10/11.
 */
import React from "react";
import { connect } from "dva";
import ReactEcharts from "echarts-for-react";
import {
  getOpen_trans,
  getTrans_status,
  getTrans_exec_step_status,
  getTrans_exec_id
} from "../../../../../services/gather";
import {
  getOpen_job,
  getJob_exec_step_status,
  getJob_exec_id,
  getJob_status
} from "../../../../../services/gather1";
import style from "./EchartsView.css";
import Tools from "../../config/Tools";
import { stopStatus, errorStatus } from "../../../constant";

class EchartsView extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      option: {
        animationDurationUpdate: 1500,
        hoverAnimation: false,
        animationEasingUpdate: "quinticInOut",
        series: [
          {
            type: "graph",
            layout: "none",
            symbolSize: 36,
            roam: true,
            edgeSymbol: ["circle", "arrow"],
            edgeSymbolSize: [4, 6],
            focusNodeAdjacency: true,
            edgeLabel: {
              normal: {
                show: false
              },
              emphasis: {
                show: false
              }
            },
            label: {
              normal: {
                show: false
              },
              emphasis: {
                show: true,
                color: "#666",
                position: "bottom"
              }
            },
            data: [],
            // links: [],
            links: [],
            lineStyle: {
              normal: {
                opacity: 0.9,
                color: "#5c96bc",
                curveness: 0.1,
                width: 1
              }
            }
          }
        ]
      },
      hasItems: false
    };
  }

  componentDidMount() {
    if (this.props.type === "trans") {
      this.reloadTrans();
    } else {
      this.reloadJob();
    }
  }

  reloadTrans() {
    const { name, owner } = this.props;
    console.log(this.props);

    getOpen_trans({ name, owner }).then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        console.log(data);

        let itemsData = [];
        let lines = [];
        const { hopList, stepList } = data;

        stepList.forEach(index => {
          let type = index.type;

          if (!Tools[index.type]) {
            type = "UNKNOWN";
          }

          itemsData.push({
            name: index.name,
            x: index.gui.xloc,
            y: index.gui.yloc,
            symbol: "image://" + Tools[type].imgData
          });
        });
        hopList.forEach(index => {
          lines.push({
            source: index.from,
            target: index.to
          });
        });

        const series = this.state.option.series;
        series[0] = { ...series[0], data: itemsData, links: lines };
        const option = { ...this.state.option, series };
        this.setState({
          ...this.state,
          option: option,
          hasItems: itemsData.length ? true : false
        });
      }
    });
    this.getTransErrorInfo(name);
  }

  getTransErrorInfo(name) {
    getTrans_status({name}).then(res => {
      const { code, data } = res.data;
      const { status } = data;
      if (code === "200") {
        if (stopStatus.has(status) || errorStatus.has(status)) {
          getTrans_exec_id({name}).then(res => {
            if (code === "200") {
              const { code, data } = res.data;
              if (code === "200" && data.executionId) {
                getTrans_exec_step_status({executionId: data.executionId}).then(res => {
                  const { code, data } = res.data;
                  if (code === "200") {
                    console.log(data, "执行状态终止");
                    let args = [];
                    for (let index of data) {
                      if (index.errCount !== 0) {
                        args.push(index.stepName);
                      }
                    }
                    const series = this.state.option.series;
                    const itemData = this.state.option.series[0].data;

                    itemData.map(index => {
                      if (args.includes(index.name)) {
                        index.itemStyle = {
                          normal: {
                            shadowColor: "red",
                            shadowBlur: 10
                          }
                        };
                      }
                      return index;
                    });
                    series[0] = { ...series[0], data: itemData };
                    const option = { ...this.state.option, series };
                    this.setState({
                      ...this.state,
                      option: option
                    });
                  }
                });
              }
            }
          });
        }
      }
    });
  }

  reloadJob() {
    const { name, owner } = this.props;
    getOpen_job({ name, owner }).then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        let itemsData = [];
        let lines = [];
        const { hopList, entryList } = data;

        entryList.forEach(index => {
          let type = index.type;

          if (!Tools[index.type]) {
            type = "UNKNOWN";
          }

          itemsData.push({
            name: index.name,
            x: index.gui.xloc,
            y: index.gui.yloc,
            symbol: "image://" + Tools[type].imgData
          });
        });
        hopList.forEach(index => {
          lines.push({
            source: index.from,
            target: index.to
          });
        });
        console.log(itemsData, "名字");
        const series = this.state.option.series;
        series[0] = { ...series[0], data: itemsData, links: lines };
        const option = { ...this.state.option, series };
        this.setState({
          ...this.state,
          option: option,
          hasItems: itemsData.length ? true : false
        });
      }
    });
    this.getJobErrorInfo(name);
  }

  getJobErrorInfo(name) {
    getJob_status(name).then(res => {
      const { code, data } = res.data;
      const { status } = data;
      if (code === "200") {
        if (stopStatus.has(status) || errorStatus.has(status)) {
          getJob_exec_id(name).then(res => {
            if (code === "200") {
              const { code, data } = res.data;
              if (code === "200" && data.executionId) {
                getJob_exec_step_status(data.executionId).then(res => {
                  const { code, data } = res.data;
                  if (code === "200") {
                    let args = [];
                    for (let index of data) {
                      if (index.errCount !== 0) {
                        args.push(index.entryName);
                      }
                    }
                    const series = this.state.option.series;
                    const itemData = this.state.option.series[0].data;

                    itemData.map(index => {
                      if (args.includes(index.name)) {
                        index.itemStyle = {
                          normal: {
                            shadowColor: "red",
                            shadowBlur: 10
                          }
                        };
                      }
                      return index;
                    });
                    series[0] = { ...series[0], data: itemData };
                    const option = { ...this.state.option, series };
                    this.setState({
                      ...this.state,
                      option: option
                    });
                  }
                });
              }
            }
          });
        }
      }
    });
  }

  render() {
    const { option, hasItems } = this.state;

    return (
      <div className={style.EchartsView}>
        {hasItems ? (
          <ReactEcharts
            option={option}
            style={{ height: "175px", width: "100%" }}
            notMerge={true}
            lazyUpdate={true}
            theme={"theme_name"}
            onChartReady={this.onChartReadyCallback}
            onEvents={this.EventsDict}
          />
        ) : (
          <div className={style.blank}>暂无节点</div>
        )}
      </div>
    );
  }
}

export default connect(({ echartsview,transheader }) => ({
  echartsview, transheader
}))(EchartsView);
