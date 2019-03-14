/**
 * Created by Steven Leo on 2018.10/09.
 */
import React from "react";
import { connect } from 'dva';
import { Form, Select, Input, Checkbox, Row, Col } from 'antd';
import PluginComponent from "./HOC/PluginComponent";

const FormItem = Form.Item;
const CheckboxGroup = Checkbox.Group;

class Redundance extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			standardKey: -1,
			schemaList: [],
			tableList: [],
			fieldList: [],
			databaseList: [],
			//部分选择
			indeterminate: false,
			//全选
			selectAll: false,
		}
	}

	handleCreate = (cb) => {
		const form = this.props.form;
		const { panel, transname, description, text } = this.props.model;
		form.validateFields((err, values) => {
			if (err) {
				return;
			}
			let obj = {};
			let nodeName = values.text;
			delete values.text;
			obj.transname = transname;
			obj.newname = (text === values.text ? "" : values.text);
			obj.stepname = text;
			obj.type = panel;
			obj.description = description;
			obj.config = {
				...values,
				nodeName
			};
			cb(obj);
		});
	};

	getSelectParams() {
		const { selectOption, config, prevStepNames } = this.props.model;
		const { connection, schemaName, tableName, acceptingRows, acceptingStepName } = config;
		selectOption(data => this.setState({ databaseList: data }));
		if (connection) {
			this.getSchemaList(connection, "one");
		}
		if (connection && schemaName) {
			this.getTableList(schemaName, "one");
		}
		if (acceptingRows) {
			if (acceptingStepName && prevStepNames.includes(acceptingStepName)) {
				this.handleSelectName(acceptingStepName, "one");
			}
		} else {
			if (connection && schemaName && tableName) {
				this.handleGetFields(tableName, "one");
			};
		}
	};

	componentDidMount() {
		this.getSelectParams();
	}


	getSchemaList(name, type) {
		const { setFieldsValue } = this.props.form;
		const { getSchema } = this.props.model;
		if (type != "one") {
			setFieldsValue({ "schemaName": "", "tableName": "" });
		}
		getSchema({ name }, data => {
			this.setState({ schemaList: data })
		})
	}

	getTableList(schema, type) {
		const { setFieldsValue, getFieldValue } = this.props.form;
		const { getDbTable } = this.props.model;
		if (type != "one") {
			setFieldsValue({ "tableName": "" });
		}
		let connection = getFieldValue("connection");
		if (connection) {
			getDbTable({ connection, schema },
				data => this.setState({ tableList: data }))
		}
	};

	handleGetFields(table, type) {
		const { getFieldValue, setFieldsValue } = this.props.form;
		let connection = getFieldValue("connection");
		let schema = getFieldValue("schemaName");

		//重置选择的值
		if (type !== "one") {
			setFieldsValue({ fieldkeys: [] });
		}

		if (connection && schema && table) {
			const { getDbFields, config } = this.props.model;
			getDbFields({ connection, schema, table }, data => {

				let selectAll = this.state.selectAll;
				let indeterminate = this.state.indeterminate;
				if (type === "one") {
					if (config && config.fieldkeys.length > 0) {
						if (config.fieldkeys.length === data.length) {
							selectAll = true;
							indeterminate = false;
						} else {
							selectAll = false;
							indeterminate = true;
						}
					}
				}
				this.setState({ fieldList: data.map(index => index.name), selectAll, indeterminate });
			})
		}
	};

	handleSelectAll = () => {
		const { setFieldsValue } = this.props.form;

		setFieldsValue({
			fieldkeys: !this.state.selectAll ? this.state.fieldList : []
		});

		this.setState({
			selectAll: !this.state.selectAll,
			indeterminate: false
		});
	}

	checkSelected = (checkedList) => {
		const { fieldList } = this.state;
		this.setState({
			indeterminate: !!checkedList.length && (checkedList.length < fieldList.length),
		})
	}

	//选择步骤名  更新字段
	handleSelectName = (stepname, type) => {
		const { getOutFields, transname, config } = this.props.model;
		const { setFieldsValue } = this.props.form;

		//重置选择的值
		if (type !== "one") {
			setFieldsValue({ fieldkeys: [] });
		}

		getOutFields({ transname, stepname }, data => {
			let selectAll = this.state.selectAll;
			let indeterminate = this.state.indeterminate;
			if (type === "one") {
				if (config && config.fieldkeys.length > 0) {
					if (config.fieldkeys.length === data.length) {
						selectAll = true;
					} else {
						indeterminate = true;
					}
				}
			}
			this.setState({ fieldList: data.map(index => index.name), selectAll, indeterminate });
		})
	}

	//当获取方法发生改变时 重置选项
	handleGetMethodChange = (e) => {
		const { setFieldsValue } = this.props.form;
		if (e.target.checked) {
			setFieldsValue({ tableName: "", fieldkeys: [] });
		} else {
			setFieldsValue({ acceptingStepName: "", fieldkeys: [] })
		}
		this.setState({
			selectAll: false,
			fieldList: []
		});
	}

	render() {

		const { getFieldDecorator, getFieldValue } = this.props.form;
		const { text, config, handleCheckName, prevStepNames } = this.props.model;

		const formItemLayout1 = {
			labelCol: { span: 6 },
			wrapperCol: { span: 14 },
		};

		const setDisabled = () => {
			if (getFieldValue("acceptingRows") === undefined) {
				return config.acceptingRows;
			} else {
				if (getFieldValue("acceptingRows")) {
					return getFieldValue("acceptingRows");
				} else {
					return false;
				}
			}
		}

		return (
			<Form >
				<FormItem label="步骤自定义" style={{ marginBottom: "8px" }}  {...formItemLayout1}>
					{getFieldDecorator('text', {
						initialValue: text,
						rules: [{ whitespace: true, required: true, message: '请输入步骤名称' },
						{ validator: handleCheckName, message: '步骤名称已存在，请更改!' }
						]
					})(
						<Input />
					)}
				</FormItem>
				<FormItem
					{...formItemLayout1}
					label="数据库连接"
					hasFeedback
					style={{ marginBottom: "8px" }}
				>
					{getFieldDecorator('connection', {
						initialValue: config.connection ? config.connection : "",
						rules: [{ required: true, message: '请选择数据库链接' }]
					})(
						<Select disabled={setDisabled()} placeholder="请选择数据库链接" onChange={this.getSchemaList.bind(this)} >
							{
								this.state.databaseList.map((index) =>
									<Select.Option key={index.name} value={index.name}>{index.name}</Select.Option>
								)
							}
						</Select>
					)}
				</FormItem>
				<FormItem
					{...formItemLayout1}
					label="目标模式"
					style={{ marginBottom: "8px" }}
				>
					<div>
						{getFieldDecorator('schemaName', {
							initialValue: config.schemaName ? config.schemaName : ""
						})(
							<Select disabled={setDisabled()} onChange={this.getTableList.bind(this)} allowClear>
								{
									this.state.schemaList.map((index) =>
										<Select.Option key={index} value={index}>{index}</Select.Option>
									)
								}
							</Select>
						)}
					</div>
				</FormItem>
				<FormItem
					{...formItemLayout1}
					label="目标表"
					style={{ marginBottom: "8px" }}
				>
					<div>
						{getFieldDecorator('tableName', {
							initialValue: config.tableName === "lookup table" ? "" : config.tableName
						})(
							<Select disabled={setDisabled()} onChange={this.handleGetFields.bind(this)}  >
								{
									this.state.tableList.map((index) =>
										<Select.Option key={index.table} value={index.table}>{index.table}</Select.Option>
									)
								}
							</Select>
						)}
					</div>
				</FormItem>
				<FormItem
					{...formItemLayout1}
					label="提交记录数量"
					hasFeedback
					style={{ marginBottom: "8px" }}
				>
					{getFieldDecorator('detailNum', {
						initialValue: config.detailNum ? config.detailNum : 100,
						rules: [
							{ required: true, message: '提交记录数量不能为空' },
							{ pattern: /^(^[0-9]*$)$/, message: '请输入数字!' }
						],
					})(
						<Input disabled={setDisabled()} />
					)}
				</FormItem>
				<FormItem style={{ marginBottom: "0px", marginLeft: "188px" }} {...formItemLayout1}>
					{getFieldDecorator('acceptingRows', {
						valuePropName: 'checked',
						initialValue: config.acceptingRows
					})(
						<Checkbox onChange={this.handleGetMethodChange}  >从以前的步骤名获取</Checkbox>
					)}
				</FormItem>
				{
					prevStepNames.length > 0 ? (
						<FormItem label="获取的步骤名" style={{ marginBottom: "8px" }} {...formItemLayout1}>
							{getFieldDecorator('acceptingStepName', {
								initialValue: config.acceptingStepName
							})(
								<Select disabled={!setDisabled()} onChange={this.handleSelectName} >
									{
										prevStepNames.map((index) => (<Select.Option key={index}>{index}</Select.Option>))
									}
								</Select>
							)}
						</FormItem>
					) : null
				}
				<FormItem
					label="选择字段名"
					style={{ marginBottom: "8px" }}
					{...formItemLayout1}
				>
					{getFieldDecorator("fieldkeys", {
						initialValue: config.fieldkeys ? config.fieldkeys : []
					})(
						<CheckboxGroup
							style={{ border: "1px solid #f0f0f0", borderRadius: "8px", padding: "16px" }}
							onChange={this.checkSelected}
						>
							<Row>
								{this.state.fieldList.map((val, index) => (
									<Col span={8} key={index + val}>
										<Checkbox value={val}>
											{val}
										</Checkbox>
									</Col>
								))}
							</Row>
						</CheckboxGroup>
					)}
				</FormItem>
				<Row>
					<Col span={18} offset={6}>
						<Checkbox
							indeterminate={this.state.indeterminate}
							onChange={this.handleSelectAll}
							checked={this.state.selectAll}
						>
							全选
            </Checkbox>
					</Col>
				</Row>
			</Form>
		);
	}
}
const RedundanceForm = Form.create()(PluginComponent(Redundance, { width: 800, notNeedPreNodes: true }));

export default connect()(RedundanceForm);
