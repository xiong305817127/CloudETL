//数据质量报告首页
import { Row, Col, Tabs } from "antd";
import { connect } from "dva";
import { routerRedux, withRouter } from "dva/router";
import styles from "./index.less";

import ChartA from "./componets/chartA";
import ChartB from "./componets/chartB";

// import CHARACTER from "./componets/CHARACTER";
import NUMBER from "./componets/NUMBER";
// import CERTIFICATES from "./componets/CERTIFICATES";
// import DATE from "./componets/DATE";
// import REDUNDANCE from "./componets/REDUNDANCE";


const TabPane = Tabs.TabPane;

const getChartName = (tabType) => {
	switch (tabType) {
		case "CHARACTER":
			return "标准值匹配"
		case "NUMBER":
			return "电话匹配"
		case "CERTIFICATES":
			return "证件匹配"
		case "DATE":
			return "日期匹配"
		// case "REDUNDANCE": <DATE text={"日期匹配"} typeList={typeList} />
		// 	return <REDUNDANCE text={"冗余数据"} typeList={typeList} />
		default:
			return "标准值匹配"
	}
}

const index = ({ qualityIndexModel, dispatch, location }) => {

	const { aduitsList, aduitsTotal, tabType, nodeDataSource, loading, nodeType, flag, typeList } = qualityIndexModel;
	console.log("tabType", tabType);

	// column 
	// const columns = [{
	// 	dataIndex: 'execId',
	// 	key: 'execId',
	// 	title: '报告名称',
	// 	width: "300px"
	// }, {
	// 	title: '执行交换时间',
	// 	dataIndex: 'beginStr',
	// 	key: 'beginStr',
	// 	width: "25%"
	// },
	// {
	// 	title: '操作',
	// 	dataIndex: 'view',
	// 	key: 'view',
	// 	render: (text, record) => {
	// 		return (
	// 			<div>
	// 				<span>查看稽核</span>&nbsp;&nbsp;
	// 				<span>查看转换</span>
	// 			</div>
	// 		);
	// 	}
	// }];

	const handleChange = (params) => {
		const { pathname, query } = location;
		dispatch(routerRedux.push({ pathname, query: { ...query, ...params } }));
	}

	const handleTabsChange = (tabType) => {
		const { pathname, query } = location;
		dispatch(routerRedux.push({ pathname, query: { ...query, tabType } }));
	}

	return (
		<div className={styles.indexPage} >
			<Row gutter={10} className={styles.upPart} >
				<Col span={14}  >
					<div className={styles.pageHeader}>执行算子单项数据稽核总览</div>
					<div className={styles.part} >
						<ChartA aduitsList={aduitsList} handleChange={handleChange} flag={flag} nodeType={nodeType} nodeDataSource={nodeDataSource} />
					</div>
				</Col>
				<Col span={10} >
					<div className={styles.pageHeader}></div>
					<div className={styles.part} >
						<ChartB aduitsTotal={aduitsTotal} />
					</div>
				</Col>
			</Row>
			<div className={styles.downPart} >
				<Tabs activeKey={tabType} onChange={handleTabsChange} >
					<TabPane tab="标准值匹配" key="CHARACTER"></TabPane>
					<TabPane tab="电话匹配" key="NUMBER"></TabPane>
					<TabPane tab="证件匹配" key="CERTIFICATES"></TabPane>
					<TabPane tab="日期匹配" key="DATE"></TabPane>
					{/* <TabPane tab="冗余数据" key="REDUNDANCE"></TabPane> */}
				</Tabs>
				{
					loading ? <NUMBER text={getChartName(tabType)} typeList={typeList} /> : <div style={{ height: "350px", fontSize: "30px", lineHeight: "350px", textAlign: "center" }} >图形加载中...</div>
				}
			</div>
		</div>
	)
}

export default connect(({ qualityIndexModel }) => ({ qualityIndexModel }))(withRouter(index));


// (()=>{
// 	switch (tabType) {
// 		case "CHARACTER":
// 			return <CHARACTER text={"标准值匹配"} typeList={typeList} />
// 		case "NUMBER":
// 			return <NUMBER text={"电话匹配"} typeList={typeList} />
// 		case "CERTIFICATES":
// 			return <CERTIFICATES text={"证件匹配"} typeList={typeList} />
// 		case "DATE":
// 			return <NUMBER text={"日期匹配"} typeList={typeList} /> 
// 		// case "REDUNDANCE": <DATE text={"日期匹配"} typeList={typeList} />
// 		// 	return <REDUNDANCE text={"冗余数据"} typeList={typeList} />
// 		default:
// 			return <div>DATE</div>
// 	}
// })()