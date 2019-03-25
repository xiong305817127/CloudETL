import React from "react";
import ReactEcharts from "echarts-for-react";
import { Row, Col, Table, Progress } from "antd";
import configs from "./configs";

export default ({ FullData }) => {
	// 表格信息
	const columns = [
		{
			title: "数据总行数",
			dataIndex: "total",
			key: "total"
		},
		{
			title: "重复数据行数",
			dataIndex: "optional1",
			key: "optional1",
			render: (text, record) => {
				return record.total - text;
			}
		},
		{
			title: "冗余率",
			dataIndex: "newNumber",
			render: (text, record) => {
				return Math.floor(100 - (record.optional1 / record.total) * 100) + "%";
			},
		},
		{
			title: "冗余占比(重复数据行数/数据总行数)",
			dataIndex: "",
			render: (text, record) => {
				return (
					<Progress
						percent={(1 - record.optional1 / record.total) * 100}
						showInfo={false}
					/>
				);
			}
		}
	];

	const dataredundanceData = Object.keys(FullData[0].dataredundanceData).map(index => {
		return { name: index, value: parseInt(FullData[0]["dataredundanceData"][index], 10) }
	});

	// 获取option
	const options ={
		toolbox: {
			show: true,
			feature: {
				saveAsImage: { show: true }
			}
		},
		tooltip: {
			trigger: 'axis',
			axisPointer: {            // 坐标轴指示器，坐标轴触发有效
				type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
			},
		},
		grid: {
			left: '3%',
			right: '4%',
			bottom: '40',
			containLabel: true
		},
		dataZoom: [{}, {type: 'inside'}],
		yAxis: {
			type: 'value',
			boundaryGap: [0, 0.01]
		},
		xAxis: {
			type: 'category',
			data: dataredundanceData.map(index=>index.name)
		},
		series:[
			{ type:"bar",barMaxWidth:30,data:dataredundanceData.map(index=>index.value)} 
		]
	};

	return (
		<div>
			<Row gutter={32}>
				{/* 处理饼图 */}
				<Col span={12}>
					<ReactEcharts option={options} style={{ height: 300 }} />
				</Col>

				{/* 处理表格 */}
				<Col span={12} style={{ maxHeight: "600px", overflowY: "scroll" }}>
					{/* Tip：后续表格数据量很大时，如何优化，可以采用两种方式： */}
					{/* 1. 采用react-window，此组件可以对数据量较大的进行处理，处理速度极快 */}
					{/* 2. 采用分页。实际解决方法按照产品需求来定。*/}
					<Table
						columns={columns}
						dataSource={FullData}
						rowKey={"referenceValue"}
						// 控制分页
						pagination={false}
					/>
				</Col>
			</Row>
		</div>
	);
};
