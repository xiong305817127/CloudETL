import React from "react";
import { Form, Cascader, Input, Select, Button, Row, Col, Table } from "antd";
import Modal from "components/Modal";
import { connect } from "dva";
import { colTypeArgs } from "../../constants";
import { API_BASE_CATALOG } from "constants";
import TableList from "components/TableList";
import { downloadFile } from "utils/utils";
const FormItem = Form.Item;
const Option = Select.Option;
const { TextArea } = Input;
const Search = Input.Search;

const getItemById = (id, args) => {
  if (args && args.length > 0) {
    for (let index of args) {
      if (id === index.id) {
        return index;
      }
    }
  }
  return "";
};

//获得共享类型
const getShareType = str => {
  switch (str) {
    case 1:
      return "无条件共享";
    case 2:
      return "有条件共享";
    case 3:
      return "不予共享";
    default:
      return "无条件共享";
  }
};

//获得共享类型
const getShareMethod = str => {
  switch (str) {
    case 1:
      return "数据库";
    case 2:
      return "文件下载";
    case 3:
      return "服务方式";
    default:
      return "数据库";
  }
};

//获得分享时间
const getShareTime = str => {
  switch (str) {
    case 1:
      return "实时";
    case 2:
      return "每日";
    case 3:
      return "每周";
    case 4:
      return "每月";
    case 5:
      return "每季度";
    case 6:
      return "每半年";
    case 7:
      return "每年";
    default:
      return "每月";
  }
};

class index extends React.Component {

	constructor() {
		super();
	}

	componentWillMount() {
		const { dispatch } = this.props;
		dispatch({ type: "resourcesCommon/getResourcesList" });
		dispatch({ type: "resourcesCommon/getDepartments" });
		dispatch({ type: "resourcesCommon/getAllDepartments" });
		dispatch({ type: "resourcesCommon/getServicesList" });
		dispatch({ type: "checkview/getResourceTypeDict" });
	}

	columns = [
		{
			title: "信息项名称",
			dataIndex: 'colName',
			key: 'colName',
			width: "18%",
		}, {
			title: '数据类型',
			dataIndex: 'colType',
			key: 'colType',
			width: "12%",
			render: (text) => {
				return (<span>{colTypeArgs[text].title}</span>);
			}
		}, {
			title: '物理表列名',
			dataIndex: 'tableColCode',
			key: 'tableColCode',
			width: "18%",
		}, {
			title: '列类型',
			dataIndex: 'tableColType',
			key: 'tableColType',
			width: "10%"
		}, {
			title: "日期格式",
			dataIndex: 'dateFormat',
			key: 'dateFormat',
			width: "20%"
		}, {
			title: '唯一标识',
			dataIndex: 'uniqueFlag',
			key: 'uniqueFlag',
			width: "10%",
			render: (bool) => {
				return bool ? "是" : "否"
			}
		}, {
			title: '订阅必选',
			dataIndex: 'requiredFlag',
			key: 'requiredFlag',
			render: (bool) => {
				return bool ? "是" : "否"
			}
		}
	];

	columns1 = [
		{
			title: "信息项名称",
			dataIndex: 'colName',
			key: 'colName',
			width: "65%",
			editable: true,
		}, {
			title: '数据类型',
			dataIndex: 'colType',
			key: 'colType',
			render: (text) => {
				return (<span>{colTypeArgs[text].title}</span>);
			}
		}
	];


	fileColumns = [
		{
			title: '文件名称',
			dataIndex: 'pubFileName',
			key: 'pubFileName',
			width: "30%",
			//屏蔽文件下载功能
			//@edit by pwj 2018/11/3
			render: (text, record) => {
				if ( record.downFlag ===true) {
					return (
						<a onClick={(e) => { this.headClickDolwe(e, record) }}>{text}</a>
					)
				}
				return text;
			}
		}, {
			title: '文件大小',
			dataIndex: 'fileSize',
			key: 'fileSize',
			width: "10%"
		}, {
			title: '数据批次',
			dataIndex: 'dataBatch',
			key: 'dataBatch',
			width: "10%"
		}, {
			title: "文件描述",
			dataIndex: 'fileDescription',
			key: 'fileDescription',
			width: "25%"
		}, {
			title: "更新时间",
			dataIndex: 'updateTime',
			key: 'updateTime',
			width: "25%"
		}
	];

	//点击下载
	headClickDolwe = (e, record) => {
		downloadFile(`${API_BASE_CATALOG}/dataUpload/download?` + "fileId=" + record.id);
	}



	//点击取消
	handleCancel() {
		const { dispatch } = this.props;
		dispatch({ type: "checkview/clear" })
	}

	//不同意确认框
	handleDisAgree() {
		const { dispatch } = this.props;
		dispatch({ type: "checkview/save", payload: { rejectVisible: true } })
	}

	//同意或不同意
	handleAgree(args) {
		const { dispatch } = this.props;
		const { config } = this.props.checkview;
		dispatch({ type: "checkview/getProcess", payload: { id: config.id, ...args } })
	}

	//文件列表
	handleFileList() {
		const { config } = this.props.checkview;
		const { dispatch } = this.props;

		dispatch({
			type: "checkview/getResourceFile",
			action: "init",
			payload: {
				id: config.id,
			}
		})
	}

	//文件关闭
	onFileCancel() {
		const { dispatch } = this.props;
		dispatch({
			type: "checkview/save",
			payload: { fileVisible: false }
		})
	}

	//搜索
	handleSearch(value) {
		const { config } = this.props.checkview;
		const { dispatch } = this.props;
		dispatch({
			type: "checkview/getResourceFile",
			action: "search",
			payload: {
				name: value.trim(),
				id: config.id
			}
		})
	}

	handleFileListChange(index) {
		const { dispatch } = this.props;
		const { current, pageSize } = index;
		dispatch({
			type: "checkview/getResourceFile",
			payload: {
				current, pageSize
			}
		})
	}

	/**
	 * 判断提交审批拒绝原因
	 */
	handleOkReject(e) {
		e.preventDefault();
		const { validateFields } = this.props.form;
		validateFields(["suggestion"], (errors, values) => {
			if (errors) return;
			this.handleAgree({ action: "reject", ...values });
		})
	}

	/**
	 * 关闭审批拒绝框
	 */
	handleCancelReject() {
		const { form, dispatch } = this.props;
		const { setFieldsValue } = form;
		setFieldsValue({ suggestion: "" });
		dispatch({ type: "checkview/save", payload: { rejectVisible: false } })
	}


	render() {

		const { getFieldDecorator } = this.props.form;
		const { allDepartments, servicesList } = this.props.resourcesCommon;

		const { config, controlVisible, shareType, bindTables, dataSource, visible, actionType, rejectVisible,
			fileVisible, fileSource, total, loading, current, pageSize,ResourceFormat } = this.props.checkview;

		const viewType = config.formatType && config.formatType[0] && config.formatType[0] !== 3 && config.formatType[0] !== 7
			? (<Button style={{ float: "left" }} onClick={this.handleFileList.bind(this)} key="fileList" type="primary">文件列表</Button>)
			: null;

		const formItemLayout = {
			labelCol: {
				xs: { span: 24 },
				sm: { span: 6 },
			},
			wrapperCol: {
				xs: { span: 24 },
				sm: { span: 16 },
			},
		};

		const children = [];
		for (let index of allDepartments) {
			children.push(<Option key={index.id}>{index.deptName}</Option>);
		}

		return (
			<div>
				<Modal
					visible={visible}
					title="资源查看"
					width={800}
					zIndex={800}
					onCancel={this.handleCancel.bind(this)}
					footer={actionType === "check" ?
						[viewType,
							<Button onClick={() => { this.handleAgree({ action: "agree" }) }} key="checkviewAgree" type="primary">同意</Button>,
							<Button onClick={() => { this.handleDisAgree() }} key="checkviewNoAgree" >不同意</Button>,
							<Button onClick={this.handleCancel.bind(this)} key="checkview"  >关闭</Button>] :
						[viewType,
							<Button onClick={this.handleCancel.bind(this)} key="checkview" type="primary">关闭</Button>]
					}
				>
					<Form style={{ maxWidth: "600px" }}>
						<FormItem label="资源分类" {...formItemLayout}>
							<span className="ant-form-text">{config.catalogName}</span>
						</FormItem>
						<FormItem label="资源名称" {...formItemLayout} style={{ marginTop: '-3%' }}>
							<span className="ant-form-text">{config.name}</span>
						</FormItem>
						<FormItem label="资源代码" {...formItemLayout} style={{ marginTop: '-3%' }}>
							<span className="ant-form-text">{config.code}</span>
						</FormItem>
						<FormItem label="资源提供方" {...formItemLayout} style={{ marginTop: '-3%' }}>
							<span className="ant-form-text">{config.deptName}</span>
						</FormItem>

						<FormItem label="资源创建者" {...formItemLayout} style={{ marginTop: '-3%' }}>
							<span className="ant-form-text">{config.creator}</span>
						</FormItem>

						<FormItem label="资源摘要" {...formItemLayout} style={{ marginTop: '-3%' }}>
							<span className="ant-form-text">{config.remark}</span>
						</FormItem>

						<FormItem label="资源格式" {...formItemLayout} style={{ marginTop: '-3%' }}>
							{getFieldDecorator("formatType", {
								initialValue: config.formatType ? [config.formatType[0]+"",config.formatType[1]] : []
							})(
								<Cascader
									disabled={true}
									fieldNames={{ label: 'name',value:'code',children:'childrenList'}}
                	options={ResourceFormat}
								/>
							)}
						</FormItem>
						{
							controlVisible === "info" ? (<FormItem label="自定义格式描述" {...formItemLayout}>
								<span className="ant-form-text">{config.formatInfoExtend}</span>
							</FormItem>) : null
						}
						{
							controlVisible === "database" ? (<FormItem
								{...formItemLayout}
								label="物理表名"
								hasFeedback
							>
								{getFieldDecorator('bindTableId', {
									initialValue: config.bindTableId ? config.bindTableId : ""
								})
									(
									<Cascader
										disabled={true}
										options={bindTables}
									/>
									)}
							</FormItem>) : null
						}
						{
							controlVisible === "service" ? (<FormItem
								{...formItemLayout}
								label="服务名"
								hasFeedback
							>
								{getFieldDecorator('bindServiceId', {
									initialValue: config.bindServiceId ? config.bindServiceId + "" : ""
								})(
									<Select disabled={true} >
										{
											servicesList.map(index => {
												return (<Option key={index.id} value={index.id + ""}>{index.serviceName}</Option>)
											})
										}
									</Select>
								)}
							</FormItem>) : null
						}
						<FormItem {...formItemLayout} label="共享类型" style={{ marginTop: '-3%' }}>
							<span className="ant-form-text">{getShareType(config.shareType)}</span>

						</FormItem>
						{
							shareType !== "3" ?
								(<div>
									{
										shareType === "2" ? (
											<FormItem label="共享条件" {...formItemLayout}>
												<span className="ant-form-text">{config.shareCondition}</span>
											</FormItem>

										) : null
									}
									<FormItem label="共享部门" {...formItemLayout} style={{ marginTop: '-3%' }}>
										{getFieldDecorator("shareDeptArray", {
											initialValue: config.shareDeptArray ? config.shareDeptArray : []
										})(
											<Select
												mode="tags"
												disabled={true}
												size={"default"}
												style={{ width: '100%' }}
											>
												{children}
											</Select>
										)}
									</FormItem>
									<FormItem {...formItemLayout} label="共享方式" style={{ marginTop: '-3%' }}>
										<span className="ant-form-text">{getShareMethod(config.shareMethod)}</span>

									</FormItem>
								</div>) : null}
						<FormItem	 {...formItemLayout} label="更新周期" style={{ marginTop: '-3%' }}>
							<span className="ant-form-text">{getShareTime(config.refreshCycle)}</span>
						</FormItem>
						<FormItem	 {...formItemLayout} label="是否向社会开放" style={{ marginTop: '-3%' }}>
							<span className="ant-form-text">{config.openType === 0 ? "否" : "是"}</span>
						</FormItem>
						{
							config.openType === 1 ? (
								<FormItem label="开放条件" {...formItemLayout}>
									<span className="ant-form-text">{config.openCondition}</span>
								</FormItem>
							) : null
						}
						{
							config.suggestion ? (
								<FormItem label="审批意见" {...formItemLayout}>
									<span className="ant-form-text">{config.suggestion}</span>
								</FormItem>
							) : null
						}
					</Form>
					{
						controlVisible === "database" ? (
							<div style={{ margin: "0px 5%" }}>
								<Table size={"small"} rowKey="id" pagination={false} columns={this.columns} dataSource={dataSource} scroll={{ y: 140 }} />
							</div>
						) : null
					}
					{
						controlVisible !== "database" ? (
							<div style={{ margin: "0px 5%" }}>
								<Table size={"small"} rowKey="id" pagination={false} columns={this.columns1} dataSource={dataSource} scroll={{ y: 140 }} />
							</div>
						) : null
					}
				</Modal>
				<Modal
					visible={fileVisible}
					width={800}
					zIndex={1030}
					title="文件列表"
					footer={[<Button key="filelist" onClick={this.onFileCancel.bind(this)}>关闭</Button>]}
					onCancel={this.onFileCancel.bind(this)}
				>
					<Row style={{ marginBottom: "20px" }}>
						<Col span={6}>&nbsp;</Col>
						<Col span={12}>
							<Search
								placeholder="按文件名模糊搜索"
								size="large"
								onSearch={this.handleSearch.bind(this)}
							/>
						</Col>
						<Col span={6}>&nbsp;</Col>
					</Row>
					<TableList
						showIndex
						loading={loading}
						expandedRowRender={record => <p style={{ margin: 0 }}>{record.fileDescription}</p>}
						rowKey='__index'
						columns={this.fileColumns}
						dataSource={fileSource}
						onChange={this.handleFileListChange.bind(this)}
						pagination={{ current, pageSize, total }}
						useRouter={false}
					/>
				</Modal>
				<Modal
					title="审批意见"
					visible={rejectVisible}
					zIndex={1030}
					onOk={this.handleOkReject.bind(this)}
					onCancel={this.handleCancelReject.bind(this)}
				>
					<Form>
						<FormItem label="拒绝事由" >
							{getFieldDecorator("suggestion", {
								initialValue: "",
								rules: [{ required: true, message: '拒绝事由不能为空！', }],
							})(
								<TextArea />
							)}
						</FormItem>
					</Form>
				</Modal>
			</div>
		)
	}
}

export default connect(({ resourcesCommon, checkview }) => ({
  resourcesCommon,
  checkview
}))(Form.create()(index));
