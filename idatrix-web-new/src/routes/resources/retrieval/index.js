import { Input, Row, Col, List } from 'antd';
import { connect } from "dva";
import styles from "./index.less";
import ListView from "../common/ListView";
import CheckView from "../common/CheckView";
import { withRouter } from 'dva/router';
import qs from "querystring";

const Search = Input.Search;

const index = ({ retrievalModel, location,router,dispatch }) => {

	const { query,pathname } = location;
	const { total, dataSource } = retrievalModel;

	console.log(retrievalModel,"model里面的数据");

	//类型类数据
	const list = [
		{ value: "all", label: "全部" },
		{ value: "表", label: "库表" },
		{ value: "文件", label: "文件" }
	]

	//得到默认值
	const getDefaultValue = () => {
		switch(query.formatTypeV){
			case "表":
				return "表";
			case "文件":
				return "文件";
			default:
				return "all"
		}
	}

	const handleListChange = (item) => {
		query.page = 1;
		if(item.value === "all" || !item.value){
			delete query.formatTypeV
		}else{
			query.formatTypeV = item.value;
		}
		router.push(`${pathname}?${qs.stringify(query)}`)
	}

	const handleSearch = (value)=>{
		query.page = 1;
		if(value){
			query.keyword = value;
		}else{
			delete query.keyword
		}
		router.push(`${pathname}?${qs.stringify(query)}`)
	}

	const handleClick = (id)=>{
		dispatch({ 
			type:"checkview/getEditResource",
			payload:{ id }
		})
	}

	return (
		<div className={styles.retrievalModel}>
			<div className="btn_std_group">
				<div className={styles.searchBtn} >
					<Search
						placeholder="请输入检索关键字"
						onSearch={value => handleSearch(value)}
						style={{ width: 400 }}
						enterButton
						defaultValue={query.keyword?query.keyword:""}
					/>
				</div>
				<Row className={styles.rowType}>
					<Col span={12} >
						<ListView list={list} defaultValue={getDefaultValue()} onChange={handleListChange} />
					</Col>
					<Col span={12} className={styles.colRight} >
						搜索到 <span style={{ color:"#2592D0" }}>{total}</span> 条记录
					</Col>
				</Row>
			</div>
			<div>
				<List
					itemLayout="horizontal"
					dataSource={dataSource}
					pagination={{
						onChange: (page) => {
							query.page = page;
							router.push(`${pathname}?${qs.stringify(query)}`)
						},
						pageSize: 10,
						total:total,
						current:query.page?parseInt(query.page):1
					}}
					renderItem={item => (
						<List.Item className={styles.item}>
							<div  className={styles.upPart} >
								<div className={styles.title} onClick={()=>{handleClick(item.resourceId)}} dangerouslySetInnerHTML={{__html:item.resourceName}} />
								<Row className={styles.info}>
									<Col span={3} >提供方：<span dangerouslySetInnerHTML={{__html:item.provideDeptName}} /></Col>
									<Col span={3} >资源格式：{item.resourceFormat}</Col>
									<Col span={5} >资源编码：{item.resourceCode}</Col>
									<Col span={13} >{item.resourceFormatType}：<span dangerouslySetInnerHTML={{__html:item.resourceTarget}} /></Col>
								</Row>
								<div className={styles.downPart}>
								资源摘要：<span dangerouslySetInnerHTML={{__html:item.dataDigest}} />
								</div>
							</div>
						</List.Item>
					)}
				/>
			</div>
			<CheckView />
		</div>
	)
}

export default connect(({ retrievalModel }) => ({ retrievalModel }))(withRouter(index));