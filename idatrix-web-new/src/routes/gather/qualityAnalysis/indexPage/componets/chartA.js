/**
 * 数据稽核总览图表
 */
import React from "react";
import ReactEcharts from "echarts-for-react";
import { Row, Col, Table, Select } from "antd";
import styles from "../index.less";

const lineColor = "#C2C2C2";
const barColor = "#448AD0";

const index = ({ aduitsList, handleChange, nodeDataSource, nodeType,flag }) => {

	const options = {
		grid: {
			left: 60,
			bottom: 30,
			top: 20
		},
		tooltip: {
			trigger: 'axis',
			axisPointer: {            // 坐标轴指示器，坐标轴触发有效
				type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
			},
		},
		xAxis: {
			axisTick: false,
			type: 'category',
			data: ['标准值', '电话匹配', '证件匹配', '日期匹配', '冗余数据'],
			axisLine: {
				lineStyle: {
					color: lineColor
				}
			},
			axisLabel: { color: "#666", fontSize: 14 },
		},
		yAxis: {
			type: 'value',
			axisLine: {
				show: false
			},
			splitLine: {
				lineStyle: {
					color: lineColor
				}
			},
			axisLabel: { margin: 20, color: "#666" },
			axisTick: false
		},
		series: [{
			name: '成功',
			data: [],
			type: 'bar',
			stack: '总量',
			barMaxWidth: 40,
			itemStyle: {
				normal: {
					color: barColor
				},
				emphasis: {
					barBorderWidth: 1,
					shadowBlur: 10,
					shadowOffsetX: 0,
					shadowOffsetY: 0,
					shadowColor: 'rgba(0,0,0,0.5)'
				}
			}
		}, {
			name: '失败',
			data: [],
			type: 'bar',
			stack: '总量',
			barMaxWidth: 40,
			itemStyle: {
				normal: {
					color: "#C23531"
				},
				emphasis: {
					barBorderWidth: 1,
					shadowBlur: 10,
					shadowOffsetX: 0,
					shadowOffsetY: 0,
					shadowColor: 'rgba(0,0,0,0.5)'
				}
			}
		}]
	};

	for (let index of aduitsList) {
		switch (index.nodType) {
			case "CHARACTER":
				options.series[0].data[0] = index.succTotal;
				options.series[1].data[0] = index.errTotal;
				break;
			case "NUMBER":
				options.series[0].data[1] = index.succTotal;
				options.series[1].data[1] = index.errTotal;
				break;
			case "CERTIFICATES":
				options.series[0].data[2] = index.succTotal;
				options.series[1].data[2] = index.errTotal;
				break;
			case "DATE":
				options.series[0].data[3] = index.succTotal;
				options.series[1].data[3] = index.errTotal;
				break;
			case "REDUNDANCE":
				options.series[0].data[4] = index.succTotal;
				options.series[1].data[4] = index.errTotal;
				break;
			default:
				break;
		}
	}


	// column 
	const columns = [{
		dataIndex: 'taskName',
		key: 'taskName',
		title: '执行任务名称',
	}, {
		title: '执行次数',
		dataIndex: 'beginStr',
		key: 'beginStr',
		render: (text, record) => {
			return record.succTotal + record.errTotal;
		}
	},
	{
		title: '执行类型',
		dataIndex: 'view',
		key: 'view',
		render: () => {
			switch (nodeType) {
				case "CHARACTER":
					return "标准值"
				case "NUMBER":
					return "电话匹配"
				case "CERTIFICATES":
					return "证件匹配"
				case "DATE":
					return "日期匹配"
				case "REDUNDANCE":
					return "冗余数据"
				default:
					return "标准值"
			}
		}
	}];

	const onEvents = {
		"click": (node) => {
			switch (node.name) {
				case "标准值":
					handleChange({ nodeType: "CHARACTER" });
					break;
				case "电话匹配":
					handleChange({ nodeType: "NUMBER" });
					break;
				case "证件匹配":
					handleChange({ nodeType: "CERTIFICATES" });
					break;
				case "日期匹配":
					handleChange({ nodeType: "DATE" });
					break;
				case "冗余数据":
					handleChange({ nodeType: "REDUNDANCE" });
					break
				default:
					break;
			}
		}
	}

	return (
		<Row className={styles.upContent} gutter={5} >
			<Col span={14} style={{ height: "100%" }}  >
				<div className={styles.selectView}  >
					<Select style={{ width: 120, marginLeft: 20 }} value={flag} onChange={(flag) => { handleChange({ flag }) }} >
						<Select.Option key="year" value="year" >当年</Select.Option>
						<Select.Option key="month" value="month" >当月</Select.Option>
						<Select.Option key="day" value="day" >当天</Select.Option>
					</Select>
				</div>
				<div className={styles.chartView}>
					<ReactEcharts
						onEvents={onEvents}
						resize={true}
						option={options}
						style={{ height: "100%" }} />
				</div>
			</Col>
			<Col span={10} >
				<Table rowKey={"taskName"} size={"small"} bordered columns={columns} pagination={false} dataSource={nodeDataSource} />
			</Col>
		</Row>
	)
}

export default index;