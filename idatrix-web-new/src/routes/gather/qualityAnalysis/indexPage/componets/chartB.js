/**
 * 数据稽核总览图表
 */
import React from "react";
import ReactEcharts from "echarts-for-react";
import { Row, Col, Table, Select } from "antd";
import styles from "../index.less";

const lineColor = "#C2C2C2";
const barColor = "#448AD0";

const index = ({ aduitsTotal }) => {

	const options = {
		grid:{
			left:100,
			bottom:40,
			top:20,
			right:30
		},
		tooltip : {
			trigger: 'axis',
			axisPointer : {            // 坐标轴指示器，坐标轴触发有效
					type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
			}
		},
    xAxis: {
			type: 'value',
			axisLine:{
				show:false
			},
			axisLabel:{ margin:20 },
			axisTick:false
    },
    yAxis: {
			axisTick:false,
			type: 'category',
			data: ['总稽核规则量', '稽核正常数量', '异常数量'],
			axisLine:{
				lineStyle:{
					color:lineColor
				}
			},
			axisLabel:{ color:"#666",fontWeight:"bold",fontSize:14 },
    },
    series: [{
			data: [],
			type: 'bar',
			barMaxWidth:26,
			itemStyle:{
				normal:{
					color:barColor
				}
			}
    }]
	};

	if(Object.keys(aduitsTotal).length>0){
		options.series[0].data[0] = aduitsTotal.total;
		options.series[0].data[1] = aduitsTotal.successTotal;
		options.series[0].data[2] = aduitsTotal.failTotal;
	}

	console.log(aduitsTotal,"数据总览");

	return (
		<div className={styles.upRightContent} >
				<Row className={styles.top}  >
					<Col span={8} >
						<span>总稽核规则量</span>
						<span>{aduitsTotal.total}</span>
					</Col>
					<Col span={8} >
						<span>稽核正常数量</span>
						<span>{aduitsTotal.successTotal}</span>
					</Col>
					<Col span={8} >
						<span>异常数量</span>
						<span>{aduitsTotal.failTotal}</span>
					</Col>
				</Row>
				<div className={styles.bottom}>
					<ReactEcharts 
						option={options} 
						className={styles.chartView}
						style={{ height:"200px" }} />
				</div>
		</div>
	)
}

export default index;