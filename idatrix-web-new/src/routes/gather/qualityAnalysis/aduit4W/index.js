import { Row, Col, Table } from "antd";
import styles from "./index.less";
import { connect } from "dva";
import moment from "moment";
import { withRouter, routerRedux } from "dva/router";

const index = ({ aduit4WModel, dispatch, location }) => {

	const { detailInfo, aduitDataSource, total } = aduit4WModel;
	const { query, pathname } = location;

	// column 
	const columns = [{
		dataIndex: 'name',
		key: 'name',
		title: '报告名称',
		className: "tableCenter"
	}, {
		title: '执行交换时间',
		dataIndex: 'updateDate',
		key: 'updateDate',
		className: "tableCenter",
		render: (text, record) => {
			return moment(text).format("YYYY-MM-DD HH:mm:ss");
		}
	},
	{
		title: '执行类操作',
		dataIndex: 'view',
		key: 'view',
		className: "tableCenter",
		render: (text, record) => {
			return (
				<div className={styles.pathStyle} >
					<span onClick={() => { handleClick(record) }}  >查看稽核</span>&nbsp;&nbsp;&nbsp;&nbsp;
					<span onClick={() => { handleOpen(record.name, record.owner, record.type) }} >查看转换</span>
				</div>
			)
		}
	}];

	const handleClick = (record) => {
		dispatch({
			type: "aduit4WModel/save",
			payload: {
				aduitDataSource: aduitDataSource.map(index => {
					if (record.execId === index.execId) {
						index.rowClassName = true
					} else {
						index.rowClassName = false
					}
					return index;
				}),
				detailInfo: record
			}
		})
	}


	const handleOpen = (name, owner, type) => {

		if (type === "job") {
			dispatch({
				type: "jobheader/openFile",
				payload: {
					activeKey: name,
					owner
				}
			});
			dispatch({
				type: "designplatform/changeStatus",
				payload: {
					status: "job"
				}
			});
			window.href = "/gather/designplatform";
		} else {
			dispatch({
				type: "transheader/openFile",
				payload: {
					activeKey: name,
					owner
				}
			});
			dispatch({
				type: "designplatform/changeStatus",
				payload: {
					status: "trans"
				}
			});
			window.href = "/gather/designplatform";
		}
	};




	return (
		<Row className={styles.aduit4W} >
			<Col span={12} className={styles.leftItem} >
				<Table bordered size={"big"}
					rowClassName={(record) => {
						return record.rowClassName ? "selectedRow" : ""
					}}
					pagination={{
						total: total,
						current: query.page ? query.page : 1,
						pageSize: 12,
						onChange: (page) => {
							dispatch(routerRedux.push(pathname, { query: { page } }))
						}
					}} rowKey="execId" columns={columns} dataSource={aduitDataSource} />
			</Col>
			<Col span={12} className={styles.rightItem}>
				<div span={12} className={styles.upPart}>
					<div className={styles.header} >
						<span className={styles.banner} ></span>
						<span>{`${detailInfo.name?detailInfo.name:""}异常数据分析报告`}</span>
					</div>
					<div className={styles.content}>
						<div>
							<span>异常数据执行ID:</span>
							<span>{detailInfo.execId}</span>
						</div>
						<div>
							<span>错误发生控件:</span>
							<span>{detailInfo.position}</span>
						</div>
						<div>
							<span>异常数据输入:</span>
							<span>{detailInfo.inputSource?decodeURIComponent(detailInfo.inputSource):""}</span>
						</div>
						<div>
							<span>异常数据输出:</span>
							<span>{detailInfo.outputSource?decodeURIComponent(detailInfo.outputSource):""}</span>
						</div>
					</div>
				</div>
				<div className={styles.downPart}>
					<pre className={styles.prePart} >
					{detailInfo.exceptionDetail?	decodeURIComponent(detailInfo.exceptionDetail):""}
					</pre>
				</div>

				{/* 三角形图标 */}
				<div className={styles.squareImg}></div>
				<div className={styles.squareImg1}></div>
			</Col>
		</Row>
	)
}

export default connect(({ aduit4WModel }) => ({ aduit4WModel }))(withRouter(index));