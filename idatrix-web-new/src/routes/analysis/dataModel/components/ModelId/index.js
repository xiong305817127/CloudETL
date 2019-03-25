import styles from "./index.less";
import { connect } from "dva";
import { Input, List, Button, Form, Col, Select, Dropdown, Menu, Switch, message } from "antd";
import { defaultSettings, sourceConfig, targetConfig } from "config/jsplumb.config.js";
import Modal from "components/Modal";
import RightTable from "./components/RightTable";
import LeftTable from "./components/LeftTable";
import NewMainTable from "./components/NewMainTable";
import { routerRedux, withRouter } from "dva/router";

const Search = Input.Search;
const FormItem = Form.Item;
const Option = Select.Option;
const OptGroup = Select.OptGroup;
const confirm = Modal.confirm;

const formItemLayout = {
	labelCol: { span: 4 },
	wrapperCol: { span: 18 }
};

class index extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			Instance: null,
			loading: false,
			canLeave:false
		}
		this.routerWillLeave = this.routerWillLeave.bind(this);
	}

	routerWillLeave() {
		const { canLeave } = this.state;
		const { pathname } = this.props.location;


		if (!canLeave && pathname === "/analysis/DataModel/Config") {
			// Modal.confirm({
			// 	title: "确定离开此页面，放弃保存页面更改内容吗？",
			// 	onOk() {
			// 		return true;
			// 	},
			// 	onCancel() {
			// 	  console.log('Cancel');
			// 	},
			// }) 

			return "确定离开此页面，放弃保存页面更改内容吗？"
		}else{
			this.setState({ canLeave:false });
		}
	}


	componentDidMount() {
		this.props.router.setRouteLeaveHook(this.props.route, this.routerWillLeave);
		const mainContent = this.refs.mainContent;
		const { dispatch } = this.props;
		const { shouldUpdate } = this.props.biModelId;
		const _this = this;
		const Instance = jsPlumb.getInstance({
			...defaultSettings,
			Container: mainContent
		});
		Instance.registerConnectionType("basic", { anchor: "Continuous", connector: "StateMachine" });
		Instance.bind("connection", function (info) {
			info.connection.getOverlay("label").canvas.ondblclick = function () {
				const { lines, items } = _this.props.biModelId;
				const line = lines.filter(index => index.sourceId === info.sourceId && index.targetId === info.targetId)[0];
				let sourceItem = items.filter(index => index.id === info.sourceId)[0];
				let targetItem = items.filter(index => index.id === info.targetId)[0];
				dispatch({ type: "biModelId/save", payload: { sourceItem, targetItem, visible: true, line } });
			}
		})

		Instance.bind("beforeDrop", function (_ref) {
			const { shouldUpdate, lines, items,mainTableId } = _this.props.biModelId;

			if (_ref.sourceId === _ref.targetId) {
				return false;
			}
			for (let index of lines) {
				if ((index.targetId === _ref.targetId && index.sourceId === _ref.sourceId) || (index.targetId === _ref.sourceId && index.sourceId === _ref.targetId)) {
					return false
				}
			}

			if(mainTableId !== _ref.sourceId){
				message.warn("主从表之间才能建立关联关系！");
				return false;
			}	

			if (!shouldUpdate) {
				let sourceItem = items.filter(index => index.id === _ref.sourceId)[0];
				let targetItem = items.filter(index => index.id === _ref.targetId)[0];
				dispatch({ type: "biModelId/save", payload: { sourceItem, targetItem, visible: true } });
				return false;
			}
			return true;
		});


		this.setState({ Instance }, () => {
			if (shouldUpdate) {
				this.initItemsView();
			}
		});
	}


	componentDidUpdate() {
		const { shouldUpdate } = this.props.biModelId;
		if (shouldUpdate) {
			this.initItemsView();
		}
	}

	initItemsView() {
		const { Instance } = this.state;
		const { lines, items, itemsId, mainTableId } = this.props.biModelId;
		const { dispatch } = this.props;
		let newItemsId = [];

		if (Instance) {
			Instance.deleteEveryConnection();
			Instance.deleteEveryEndpoint();

			(lines || []).map(index => {
				Instance.connect({
					source: index.sourceId,
					target: index.targetId,
					overlays: [
						["Label", { label: index.count + "", id: "label", location: -40, cssClass: "aLabel" }]
					]
				});
			});
			(items || []).map(index => {
				newItemsId.push(index.id);
				if (itemsId.includes(index.id)) {
					return
				}
				let el = this.refs[index.id];
				Instance.makeSource(el, { //设置连接的源实体，就是这一头
					...sourceConfig,
					filter: el.lastChild.firstChild.nodeName,
					Container: this.refs.mainContent
				});
				if (mainTableId !== index.id) {
					Instance.makeTarget(el, {
						...targetConfig,
						Container: this.refs.mainContent
					});
				}

				Instance.fire("jsPlumbDemoNodeAdded", el);
			});
		}
		dispatch({ type: "biModelId/save", payload: { shouldUpdate: false, itemsId: newItemsId } })
	}

	//开始拖拽，赋初始值
	drag = (e, item) => {
		e.dataTransfer.setData("id", item.id + "");
		e.dataTransfer.setData("tableName", item.name);
	}

	//添加主表
	addMainTable = (e) => {
		e.preventDefault();
		const { dispatch } = this.props;
		const { mainTableId, items } = this.props.biModelId;
		if (!mainTableId) {
			var id = e.dataTransfer.getData("id");
			var tableName = e.dataTransfer.getData("tableName");
			if (id && tableName && items.every(index => index.id !== id)) {
				dispatch({ type: "biModelId/addMainTable", payload: { id, tableName } })
			}
		}
		return false;
	}

	//添加其他的表
	addOtherTable = (e, item) => {
		const { itemsId,mainTableId } = this.props.biModelId;
		const sourceId = item.id;
		if(item.preId || sourceId !== mainTableId){
			message.warn("暂不支持多层级表关联！");
			return false;
		}

		e.preventDefault();
		if (document.body.classList) {
			this.refs[sourceId].classList.remove("enter");
		}
		var id = e.dataTransfer.getData("id");
		var tableName = e.dataTransfer.getData("tableName");
		if (!id || !tableName || sourceId === id || itemsId.includes(id)) {
			message.info("表关联已存在！");
			return
		} else {
			const { dispatch } = this.props;
			dispatch({ type: "biModelId/addOtherTable", sourceId, payload: { id, tableName } })
		}
	}

	//确定
	handleSubmit = (e) => {
		e.preventDefault();
		const { form, dispatch } = this.props;
		form.validateFields((err, values) => {
			if (!err) {
				const { targetItem, sourceItem, line } = this.props.biModelId;
				if (line) {
					dispatch({
						type: "biModelId/updateLine",
						payload: {
							keyColumn: values.targetId,
							foreignKey: values.sourceId,
							targetId: targetItem.id,
							sourceId: sourceItem.id,
						}
					})
				} else {
					dispatch({
						type: "biModelId/addLine",
						payload: {
							name: targetItem.tableName,
							keyColumn: values.targetId,
							foreignKey: values.sourceId,
							targetId: targetItem.id,
							sourceId: sourceItem.id,
							count: 1
						}
					})
				}
				form.resetFields();
			}
		});
	}

	//取消
	handleCancel = () => {
		const { dispatch } = this.props;
		dispatch({
			type: "biModelId/save",
			payload: { visible: false, targetItem: null, sourceItem: null }
		})
	}

	//点击菜单
	handleMenuClick = (e, id) => {
		const { key } = e;
		const { dispatch } = this.props;
		if (key === "delete") {
			dispatch({ type: "biModelId/deleteItem", payload: { id } });
		}
	}

	//添加样式
	handleClassChange = (id, className, action) => {
		if (document.body.classList) {
			if (action === "enter") {
				this.refs[id].classList.add(className);
			} else {
				this.refs[id].classList.remove(className);
			}
		}
	}

	//双击弹出
	handleDoubleClick = (id) => {
		const { mainItem } = this.props.biModelId;
		const { dispatch } = this.props;
		if (id === mainItem.id) {
			dispatch({
				type: "biModelId/save", payload: { visibleMain: true }
			})
		}
	}

	//保存数据
	handleSave = () => {
		const { dispatch } = this.props;
		this.setState({ canLeave:true },()=>{
			dispatch({
				type: "biModelId/submitData",
				callback: () => {
					this.setState({
						loading: false
					})
				}
			})
		});
	}

	//取消数据
	handleCancel = () => {
		const { dispatch } = this.props;
		const _this = this;
		confirm({
			title: '确定离开该页面吗?',
			content: '放弃保存页面改动内容。',
			okText: '确定',
			okType: 'danger',
			cancelText: '取消',
			onOk() {
				_this.setState({ canLeave:true },()=>{
					dispatch({ type:"biModelId/clearAll" });
					dispatch(routerRedux.push("/analysis/DataModel"));
				});
			},
		});
	}

	handleFieldsCancel = () => {
		const { dispatch } = this.props;
		dispatch({
			type: "biModelId/save",
			payload: {
				visible: false
			}
		})
	}

	handleCheckChange = (obj) => {
		const { dispatch } = this.props;
		dispatch({
			type: "biModelId/save",
			payload: {
				...obj
			}
		})
	}

	handleSerach = (value) => {
		const { biModelId, dispatch } = this.props;
		const { tables } = biModelId;
		dispatch({
			type: "biModelId/save",
			payload: {
				tables: tables.map(index => {
					if (index.name.indexOf(value) !== -1) {
						index.noVisible = false;
					} else {
						index.noVisible = true;
					}
					return index;
				})
			}
		})
	}

	render() {
		const { tables, items, visible, targetItem, sourceItem, line, dimensionView, measureView } = this.props.biModelId;
		const { getFieldDecorator } = this.props.form;

		const newTables = tables.filter(index => !index.noVisible);

		console.log(newTables, "新表");

		return (
			<div className={styles.modelConfig}>
				<div className={styles.silder}>
					<div className={styles.silderHeader}>
						<Search
							placeholder="输入关键词搜索"
							onSearch={value => this.handleSerach(value)}
						/>
					</div>
					<div className={styles.silderList}>
						<List
							itemLayout="horizontal"
							dataSource={newTables}
							renderItem={item => (
								<List.Item draggable="true" key={item.id} onDragStart={(e) => { this.drag(e, item) }} className="item" >
									<img src={require("assets/images/analysis/table.png")} />
									<span className="text">{item.name}</span>
									<img src={require("assets/images/analysis/scan.png")} />
								</List.Item>
							)
							}
						/>
					</div>
				</div>
				<div className={styles.content}>
					<div className={styles.modelContent}>
						<div className={styles.upPart}
							ref="mainContent"
							onDrop={e => { this.addMainTable(e) }}
							onDragOver={e => { e.preventDefault() }}
						>
							{
								items.map(index => {
									return (
										<Dropdown key={index.id}
											overlay={(
												<Menu onClick={(e) => { this.handleMenuClick(e, index.id) }}>
													<Menu.Item key="delete">删除库表</Menu.Item>
												</Menu>
											)} trigger={['contextMenu']}>
											<div
												id={index.id}
												ref={index.id}
												onDrop={e => { this.addOtherTable(e, index) }}
												onDoubleClick={e => this.handleDoubleClick(index.id)}
												key={index.id}
												style={{ top: index.y, left: index.x }}
												className={styles.item}>
												<span
													onDragEnter={() => { this.handleClassChange(index.id, "enter", "enter") }}
													onDragLeave={() => { this.handleClassChange(index.id, "enter") }}
													className={styles.text}
												>{index.tableName}</span>
												<span className={styles.img} ><img className={styles.imgTag} src={require("assets/images/analysis/line_icon.png")} /></span>
											</div>
										</Dropdown>
									)
								})
							}
						</div>
						<div className={styles.downPart}>
							<div className={styles.left}>
								<div className={styles.header}>
									<div><span className={styles.split}>|</span>&nbsp;&nbsp;维度</div>
									<div>
										<Switch size="small" checkedChildren="可见" checked={dimensionView} onChange={(e) => { this.handleCheckChange({ dimensionView: e }) }} unCheckedChildren="全部" />
									</div>
								</div>
								<div className={styles.tables}><LeftTable canEdit={true} /></div>
							</div>
							<div className={styles.right}>
								<div className={styles.header}>
									<div><span className={styles.split}>|</span>&nbsp;&nbsp;度量</div>
									<div className={styles.switch}>
										<Switch size="small" checkedChildren="可见" unCheckedChildren="全部" onChange={(e) => { this.handleCheckChange({ measureView: e }) }} checked={measureView} />
									</div>
								</div>
								<div className={styles.tables}><RightTable canEdit={true} /></div>
							</div>
						</div>
					</div>
					<div className={styles.footer}>
						<Button type="primary" onClick={this.handleSave.bind(this)} loading={this.state.loading}>保存</Button>
						<Button onClick={this.handleCancel.bind(this)}>退出编辑</Button>
					</div>
				</div>
				<NewMainTable />
				<Modal
					title="表关联字段"
					visible={visible}
					width={600}
					onCancel={this.handleFieldsCancel.bind(this)}
					footer={[
						<Button key="submit" type="primary" onClick={this.handleSubmit.bind(this)}>确定</Button>,
						<Button key="cancel" onClick={this.handleFieldsCancel.bind(this)}>取消</Button>
					]}
				>
					<Form>
						<FormItem
							label="关联字段"
							{...formItemLayout}
						>
							<Col span={11}>
								<FormItem>
									{getFieldDecorator('sourceId', {
										initialValue: line ? line.foreignKey : "",
										rules: [{ required: true, message: '请选择关联字段！' }],
									})(
										<Select>
											{
												sourceItem ? (
													<OptGroup label={sourceItem.tableName}  >
														{sourceItem.fields.map(index => <Option key={index.id} value={index.name}>{index.name}</Option>)}
													</OptGroup>
												) : null
											}
										</Select>
									)}
								</FormItem>
							</Col>
							<Col span={2}>
								<span style={{ display: 'inline-block', width: '100%', textAlign: 'center' }}>
									-
						        </span>
							</Col>
							<Col span={11}>
								<FormItem>
									{getFieldDecorator('targetId', {
										initialValue: line ? line.keyColumn : "",
										rules: [{ required: true, message: '请选择关联字段！' }],
									})(
										<Select>
											{
												targetItem ? (
													<OptGroup label={targetItem.tableName}  >
														{targetItem.fields.map(index => <Option key={index.id} value={index.name}>{index.name}</Option>)}
													</OptGroup>
												) : null
											}
										</Select>
									)}
								</FormItem>
							</Col>
						</FormItem>
					</Form>
				</Modal>
			</div>
		)
	}
}

export default withRouter(connect(({
	biModelId
}) => ({ biModelId }))(Form.create()(index)));