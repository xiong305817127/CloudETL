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

	// data: typeList.map(index => index.referenceValue),
	// data: typeList.map(index => index.dataTotal),

	// typeList.map(index => {
	// 	return {
	// 		name: index.referenceValue,
	// 		value: index.taskTotal
	// 	}
	// }),



	const options = {
		title: {
			text: text + "数量统计",
			x: 'center'
		},
		tooltip: {
			trigger: 'item',
			formatter: "{a} <br/>{b} : {c} ({d}%)"
		},
		legend: {
			orient: 'vertical',
			left: 'left',
			data: typeList.map(index => index.referenceValue)
		},
		series: [
			{
				name: text + "数量统计",
				type: 'pie',
				radius: '55%',
				center: ['50%', '60%'],
				data: typeList.map(index => {
					return {
						name: index.referenceValue,
						value: index.dataTotal
					}
				}),
				itemStyle: {
					emphasis: {
						shadowBlur: 10,
						shadowOffsetX: 0,
						shadowColor: 'rgba(0, 0, 0, 0.5)'
					}
				}
			}
		]
	};


	const options1 = {
		title: {
			text: text + "数量统计",
			x: 'center'
		},
		tooltip: {
			trigger: 'item',
			formatter: "{a} <br/>{b} : {c} ({d}%)"
		},
		legend: {
			orient: 'vertical',
			left: 'left',
			data: typeList.map(index => index.referenceValue)
		},
		series: [
			{
				name: text + "数量统计",
				type: 'pie',
				radius: '55%',
				center: ['50%', '60%'],
				data: typeList.map(index => {
					return {
						name: index.referenceValue,
						value: index.taskTotal
					}
				}),
				itemStyle: {
					emphasis: {
						shadowBlur: 10,
						shadowOffsetX: 0,
						shadowColor: 'rgba(0, 0, 0, 0.5)'
					}
				}
			}
		]
	};

	return (
		<Row gutter={10}>
			<Col span={12} >
				<ReactEcharts
					option={options}
					className={styles.chartItem}
					style={{ height: "350px" }} />
			</Col>
			<Col span={12} >
				<ReactEcharts
					option={options1}
					className={styles.chartItem}
					style={{ height: "350px" }} />
			</Col>
		</Row>
	)
}

export default index;