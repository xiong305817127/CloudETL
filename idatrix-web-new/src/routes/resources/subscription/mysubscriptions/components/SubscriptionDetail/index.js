import React from 'react';
import { Table, Row, Col, Button, Form, Input, message, Select,InputNumber } from "antd";
import { connect } from 'dva';
import styles from './index.less';
import { hashHistory } from 'dva/router';
import { deepCopy } from 'utils/utils';


const FormItem = Form.Item;
const { TextArea } = Input;
const Option = Select.Option;
const formItemLayout = {
	labelCol: { span: 10 },
	wrapperCol: { span: 13 },
}
class Index extends React.Component {

	constructor(props) {
		super(props);
	}

	componentWillMount() {
		const { params, location, dispatch } = this.props;
		let status = "new";

		if (location.pathname.indexOf("/resources/subscription/DetailsList") != -1) {
			status = "new";
		} else if (location.pathname.indexOf("/resources/subscription/approval") != -1) {
			status = "approval";
		}

		dispatch({
			type: "subscriptionDetailModel/getSubDetail",
			payload: {
				id: params.params
			},
			status
		})

	}

	//订阅详情表
	detailColumns = [
		{
			title: "字段名称",
			key: "fieldname",
			dataIndex: "fieldname",
			width: "200px"
		},
		{
			title: "值",
			key: "value",
			dataIndex: "value",
		}
	]


	//查询列表
	searchColumns = [
		{
			title: "查询条件",
			rowClassName: "searchClass",
			children: [
				{
					title: "序号",
					key: "key",
					dataIndex: "key",
					width: "50px"
				},
				{
					title: "信息项名称",
					key: "colNamecolName",
					dataIndex: "colName"
				},
				{
					title: "信息编码",
					key: "tableColCode",
					dataIndex: "tableColCode"
				}
			]
		}
	]

	//订阅信息项
	infoColumns = [
		{
			title: "订阅信息项",
			rowClassName: "infoClass",
			children: [
				{
					title: "序号",
					key: "key",
					dataIndex: "key",
					width: "50px"
				},
				{
					title: "信息项名称",
					key: "colNamecolName",
					dataIndex: "colName"
				},
				{
					title: "唯一标识",
					key: "uniqueFlag",
					dataIndex: "uniqueFlag",
					render: (text) => {
						return (<span>{text ? "是" : "否"}</span>)
					}
				},
				{
					title: "信息编码",
					key: "tableColCode",
					dataIndex: "tableColCode",
				}
			]
		}
	]



	//订阅信息项加上唯一标识
	infoColumnsIdent = () => {
		const { getFieldDecorator } = this.props.form;
		return [
			{
				title: "订阅信息项",
				rowClassName: "infoClass",
				children: [
					{
						title: "序号",
						key: "key",
						dataIndex: "key",
						width: "5%"
					},
					{
						title: "信息项名称",
						key: "colNamecolName",
						dataIndex: "colName",
						width: "15%"
					},
					{
						title: "唯一标识",
						key: "uniqueFlag",
						dataIndex: "uniqueFlag",
						width: "10%",
						render: (text) => {
							return (<span>{text ? "是" : "否"}</span>)
						}
					},
					{
						title: "信息编码",
						key: "tableColCode",
						dataIndex: "tableColCode",
						width: "15%"
					}, {
						title: "脱敏规则",
						dataIndex: "dataMaskingType",
						key: "dataMaskingType",
						width: "10%",
						render: (text, record, index) => {
							return record.uniqueFlag ? <span>无</span> : (
								<Select value={record.dataMaskingType}  style={{ width: "100%" }} onChange={(value) => { this.modifyField(`dataMaskingType`,value,index) }} >
									<Option key="" value="">无</Option>
									<Option key="mask" value="mask">掩码</Option>
									<Option key="truncate" value="truncate">截断</Option>
								</Select>
							)
						}
					}, {
						title: "规则设定",
						dataIndex: "name",
						key: "name",
						children: [
							{
								title: "起始位置",
								key: "dataStartIndex",
								dataIndex: "dataStartIndex",
								render: (text, record, index) => record.uniqueFlag ? null : (
									<InputNumber min={0} style={{ width: "100%" }} value={record.dataStartIndex} maxLength="20" onChange={(value) => { this.modifyField(`dataStartIndex`, value, index) }} />
								)
							},
							{
								title: "字符长度",
								key: "dataLength",
								dataIndex: "dataLength",
								render: (text, record, index) => record.uniqueFlag ? null : (
									<InputNumber min={1} style={{ width: "100%" }} value={record.dataLength} maxLength="20" onChange={(value) => { this.modifyField(`dataLength`,value, index) }} />
								)
							}
						]
					}
				]
			}
		]
	}

	// 修改字段
	modifyField(field, value, index) {
		const { dispatch } = this.props;
		const { subSource } = this.props.subscriptionDetailModel;
		subSource[index][field] = value;
		dispatch({ type: "subscriptionDetailModel/save", payload: { subSource } });
	}

	//返回
	handleGoBack = () => {
		hashHistory.goBack();
	}

	//同意
	handleAgree = (str) => {
		const { dispatch, params } = this.props;
		const { getFieldValue } = this.props.form;
		const { subSource } = this.props.subscriptionDetailModel;
		const suggest = getFieldValue("suggestion");
		this.props.form.validateFields((err, value) => {
			if (!err) {
				if ((suggest && str === "reject") || str === "agree") {
					dispatch({
						type: "subscriptionDetailModel/getSubProcess",
						payload: {
							suggestion: suggest,
							id: params.params,
							action: str,
							inputDbioList: subSource
						}
					})
				} else {
					message.warning("请输入您的意见。")
				}
			}
		})
	}

	render() {
		const { dataSource, dbShareMethod, shareMethod, searchSource, subSource, status } = this.props.subscriptionDetailModel;
		const { getFieldDecorator } = this.props.form;

		const formItemLayout = {
			labelCol: {
				xs: { span: 24 },
				sm: { span: 4 },
			},
			wrapperCol: {
				xs: { span: 24 },
				sm: { span: 16 },
			},
		};


		return (
			<div className={styles.Content}>
				<Form>
					<div className={styles.tableContent}>
						<Table
							columns={this.detailColumns}
							dataSource={dataSource}
							pagination={false}
							bordered={true}
							showHeader={false}
						/>
						{
							shareMethod !== 0 && dbShareMethod === 2 ? (
								<div className={styles.infoContent}>
									<Table
										columns={this.searchColumns}
										dataSource={searchSource}
										pagination={false}
										bordered={true}
									/>
								</div>
							) : null
						}
						{shareMethod !== 0 && dbShareMethod === 2 ? (
							// infoColumns
							<div className={styles.infoContent}>
								<Table
									columns={this.infoColumns}
									dataSource={subSource}
									pagination={false}
									bordered={true}
								/>
							</div>
						) : null
						}
						{dbShareMethod === 1 ? (
							<div className={styles.infoContent}>
								<Table
									size={"small"}
									style={{ padding: "0px" }}
									columns={this.infoColumnsIdent()}
									dataSource={subSource}
									pagination={false}
									bordered={true}

								/>
							</div>
						) : null}
						{
							status === "approval" ? (
								<FormItem label="审批意见" {...formItemLayout} style={{ marginTop: "40px" }}>
									{getFieldDecorator("suggestion", {
										initialValue: ""
									})(
										<TextArea />
									)}
								</FormItem>
							) : null
						}
						<Row style={{ margin: "40px 0 20px 0" }} justify="center">
							<Col span={24} style={{ textAlign: "center" }}>
								<Button type="primary" onClick={this.handleGoBack.bind(this)} style={{ marginRight: "20px" }}>返回</Button>
								{
									status === "approval" ? (
										<span>
											<Button onClick={() => { this.handleAgree("reject") }} style={{ marginRight: "20px" }} >不同意</Button>
											<Button onClick={() => { this.handleAgree("agree") }} type="primary">同意</Button>
										</span>
									) : null
								}
							</Col>
						</Row>
					</div>
				</Form>
			</div>
		)
	}
}

export default connect(({ subscriptionDetailModel
}) => ({ subscriptionDetailModel }))(Form.create()(Index));