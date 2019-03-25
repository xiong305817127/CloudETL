/**
 * Created by steven leo on 2018/10/10
 */

import React from "react";
import { connect } from "dva";
import { Table, Button, Icon } from "antd";
import { hashHistory } from "dva/router";

// 引入immutable component，用于隔离mapstatetoprops的toJS转化
// 防止toJS引入新的对象导致重复渲染
import ImmComponent from "components/utils/toJS";
import ChartA from "./charts/chartA";
import ChartB from "./charts/chartB";
// import ChartC from "./charts/chartC";
import ChartD from "./charts/chartD";

const goback = () => {
	hashHistory.goBack();
};
// column
const column = [
	{
		title: "节点名称",
		dataIndex: "nodId",
		key: "nodId"
	},
	{
		title: "匹配数量",
		dataIndex: "succNum",
		key: "succNum"
	},
	{
		title: "错误/未匹配数量",
		dataIndex: "errNum",
		key: "errNum"
	},
	{
		title: "总计匹配次数",
		dataIndex: "totalNum",
		key: "totalNum"
	}
];

/**
 * 从ImmComponent中获取
 * nodelist：全部节点列表
 * selectedNodes：已选中的节点列表
 * nodeFullData：全部节点的数据，用于显示饼图
 * dispatch： 用于分发action
 * @param {*} param0
 */
const ReportComponent = ({
	nodeList,
	selectedNodes,
	nodeFullData,
	dispatch,
	name
}) => {
	// 获取选中的key列表，
	// 注意selectedNodesselectedNodes为list类型
	const nodeKeysSelected = Array.from(selectedNodes ? selectedNodes : []);

  /**
   * 切换row的显示
   * 返回至model中
   */
	const onExpandedRowsChange = expandedRows => {
		console.log(expandedRows);
		dispatch({
			type: "analysisReports/changeExpandedRows",
			newState: {
				selectedNodes: expandedRows
			}
		});
	};

  /**
   * 渲染单个row的render方法
   * @param {Object} record
   * @param {Number} index
   */
	const rowRender = record => {
		const FullData =
			typeof nodeFullData[record.nodId] !== "undefined"
				? nodeFullData[record.nodId]
				: "";

		let total = 0;

		// 从FullData中整理新的格式
		const FullDataList = Object.entries(FullData)

			.map(val => {
				// 给每条数据新增一个newNumber，备用
				total += FullData[val[0]].number;
				return { ...FullData[val[0]], newNumber: FullData[val[0]].number };
			})

			.map(val => {
				// 给每条数据新增一个total，用于计算百分比
				return { ...val, total: total };
			});

		console.log(nodeList, "新的格式");


		return (
			<div>
				{/*处理顶部标题 */}
				{/*新增对冗余率进行特殊处理 */}
				{
					record.nodType === "REDUNDANCE" ? <ChartD FullData={FullDataList} /> : (
						<div>
							<label className="f_bold" style={{ lineHeight: "44px" }}>
									总共
							{record.totNodNum}
									个参考值对
							{FullDataList.length}
									个匹配项进行匹配,其中正确匹配
							{record.curNodNum}
									条,错误匹配
							{record.errNodNum}
									条.
						</label>
							{/* 处理表格，日期组件用柱形图表示 */}
							{record.nodType !== "DATE" ? (
								<ChartA FullData={FullDataList} />
							) : (
									<ChartB FullData={FullDataList} />
								)}
						</div>
					)
				}
			</div>
		);
	};

	console.log(nodeFullData, "列表界面");
	console.log(nodeList, "节点列表");

	return (
		<div className="padding_16">
			<p>
				<Button type="default" onClick={goback} size="small">
					<Icon type="left" />
					返回上一页
        </Button>
			</p>
			<h2 style={{ textAlign: "center" }}>数据质量分析报告</h2>
			<p style={{ textAlign: "center", marginBottom: "64px" }}>
				报告来源：
        {name}
				（任务名），共计分析节点：
        {nodeList.length}条
      </p>
			{// 检测类型
				Object.prototype.toString.call(nodeList) === "[object Array]" && (
					<Table
						columns={column}
						dataSource={nodeList}
						// 使用nodid作为唯一标识
						rowKey={"nodId"}
						expandedRowKeys={nodeKeysSelected}
						onExpandedRowsChange={onExpandedRowsChange}
						expandedRowRender={rowRender}
					/>
				)}
		</div>
	);
};

export default connect(({ analysisReports }) => {
	// 获取store中的信息
	return {
		nodeList: analysisReports.get("nodeList"),
		selectedNodes: analysisReports.get("selectedNodes"),
		execId: analysisReports.get("execId"),
		nodeFullData: analysisReports.get("nodeFullData"),
		name: analysisReports.get("name")
	};
})(ImmComponent(ReportComponent));
