/**
 * 数据稽核总览图表
 */
import React from "react";
import ReactEcharts from "echarts-for-react";
import { Row, Col } from "antd";
import styles from "../index.less";

const lineColor = "#C2C2C2";
const barColor = "#448AD0";

const index = ({ text, typeList }) => {


	const options = {
		grid: {
			left: 100,
			top: 40,
			bottom: 30,
		},
		title: {
			text: text + "数量统计",
			left: "center"
		},
		tooltip: {
			trigger: 'axis',
			axisPointer: {            // 坐标轴指示器，坐标轴触发有效
				type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
			}
		},
		xAxis: {
			axisTick: false,
			type: 'category',
			data: typeList.map(index => index.referenceValue),
			axisLine: {
				lineStyle: {
					color: lineColor
				}
			},
			axisLabel: { color: "#666", fontSize: 14 },
		},
		yAxis: [
			{
				type: 'value',
				axisLine: {
					show: false
				},
				axisLabel: { margin: 20 },
				axisTick: false
			}
		],
		series: [
			{
				data: typeList.map(index => index.dataTotal),
				type: 'line',
				itemStyle: {
					normal: {
						color: barColor
					}
				}
			}
		]
	};

	const options1 = {
		grid: {
			left: 100,
			top: 40,
			bottom: 40,
		},
		tooltip: {
			trigger: 'axis',
			axisPointer: {            // 坐标轴指示器，坐标轴触发有效
				type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
			}
		},
		title: {
			text: text + "任务量统计",
			left: "center"
		},
		xAxis: {
			axisTick: false,
			type: 'category',
			data: typeList.map(index => index.referenceValue),
			axisLine: {
				lineStyle: {
					color: lineColor
				}
			},
			axisLabel: { color: "#666", fontSize: 14 },
		},
		yAxis: [
			{
				type: 'value',
				axisLine: {
					show: false
				},
				axisLabel: { margin: 20 },
				axisTick: false
			}
		],
		series: [{
			data: typeList.map(index => index.taskTotal),
			type: 'line',
			itemStyle: {
				normal: {
					color: barColor
				}
			}
		}]
	};

	return (
		<Row gutter={10} >
			<Col span={12} >
				<ReactEcharts
					option={options}
					className={styles.chartItem}
					style={{ height: "350px" }} />
			</Col>
			<Col span={12}>
				<ReactEcharts
					option={options1}
					className={styles.chartItem}
					style={{ height: "350px" }} />
			</Col>
		</Row>
	)
}

export default index;