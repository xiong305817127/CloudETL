import { connect } from "dva";
import styles from '../index.less';
import { Collapse, Icon, Dropdown, Menu, Form, Button, Input, Select, message,Row,Col } from 'antd';
import Modal from "components/Modal";
import { params } from "config/jsplumb.config.js";
import EditCell from "components/common/EditCell"

const Panel = Collapse.Panel;
const FormItem = Form.Item;
const Option = Select.Option;

let current = "";
let currentName = "";
let currentId = "";
let action = "";
let fieldId = "";
let fields = [];

const index = ({ biModelId, form, dispatch, canEdit }) => {

	const { dimension, dimVisible, measure, dimensionView } = biModelId;
	const { getFieldDecorator } = form;

	//右键菜单事件
	const menu = (
		<Menu onClick={(item) => { handleMenuClick(item) }}>
			<Menu.Item key="edit">重命名</Menu.Item>
			<Menu.Item key="new">新建层次</Menu.Item>
			<Menu.Item key="add">加入层次</Menu.Item>
			<Menu.Item key="delete">删除层次</Menu.Item>
		</Menu>
	)

	const handleMenuClick = (item) => {	
		if(!current){
			message.info("未选中维度,请重新选择！");
			return false;
		}
		action = item.key;
		let args = current.split("/");
		currentId = args[0];
		currentName = args[1];
		fieldId = args[2];

		if (item.key === "new") {
			currentName = "";
			dispatch({
				type: "biModelId/save",
				payload: { dimVisible: true }
			})
		} else if (item.key === "edit") {
			let item = dimension.filter(index => index.name === currentName)[0];
			if (item.name === item.tableName) {
				message.warn("主层次无法重命名！")
				return false
			} else {
				dispatch({
					type: "biModelId/save",
					payload: { dimVisible: true }
				})
			}
		} else if (item.key === "delete") {
			let item = dimension.filter(index => index.name === currentName)[0];
			if (item.name === item.tableName) {
				message.warn("主层次无法删除！")
				return false;
			} else {
				const { Level } = item;
				let newDimension = dimension.filter(index => index.name !== currentName);
				dispatch({
					type: "biModelId/save",
					payload: {
						dimension: newDimension.map(index => {
							if(!index.visible){
								Level.map(obj=>{ 
									obj.visible = false;
									return obj;
								})
							}
							if (index.id === currentId && index.name === index.tableName) {
								index.Level = [...index.Level, ...Level];
							};
							return index;
						})
					}
				})
			}
		} else {
			if (fieldId) {
				fields = dimension.filter(index => index.id === currentId && index.name !== currentName);
				if (fields.length === 0) {
					message.warn("暂无可加入的层次！");
					return false;
				}
				dispatch({
					type: "biModelId/save",
					payload: { dimVisible: true }
				})
			}
		}
	}

	//校验事件
	const handleValidator = (rule, value, callback) => {
		if (value) {
			const args = dimension.filter(index => index.name === value);
			if (action === "new") {
				args.length > 0 ? callback(true) : callback();
			} else {
				value !== currentName && args.length > 0 ? callback(true) : callback();
			}
		}
		callback(false);
	}

	const handleMouseClick = (e) => {
		const logo = e.target.getAttribute("data-logo") || e.target.parentNode.getAttribute("data-logo");
		if (logo === "panel") {
			current = e.target.getAttribute("data-id") || e.target.parentNode.getAttribute("data-id");;
		}
	}

	const handelSure = (e) => {
		e.preventDefault();
		form.validateFields((err, values) => {
			if (!err) {
				let newArgs = dimension;

				if (action === "new") {
					const item = dimension.filter(index => index.id === currentId)[0];
					newArgs.push({ ...item, Level: [], name: values.name });
					dispatch({ type: "biModelId/save", payload: { dimVisible: false, dimension: newArgs } });
				} else if (action === "edit") {
					if (values !== currentName) {
						dispatch({
							type: "biModelId/save", payload: {
								dimVisible: false,
								dimension: newArgs.map(index => {
									if (index.name === currentName) {
										index.name = values.name;
									}
									return index;
								})
							}
						});
					}
				} else {
					let item = null;
					for (let index of newArgs) {
						if (index.name === currentName) {
							for (let obj of index.Level) {
								if (parseInt(obj.id) === parseInt(fieldId)) {
									item = obj;
								}
							}
						}
					};
					newArgs = newArgs.map(index => {
						if (index.name === values.name) {
							if(!index.visible){
								item.visible = false;
							}
							index.Level.push(item)
						}
						if (index.name === currentName) {
							index.Level = index.Level.filter(obj => {
								return parseInt(obj.id) !== parseInt(item.id)
							})
						}
						return index;
					});

					dispatch({
						type: "biModelId/save", payload: { dimVisible: false, dimension: newArgs }
					})
				}
				form.resetFields();
			}
		});
	}

	const handelCancel = () => {
		dispatch({
			type: "biModelId/save",
			payload: { dimVisible: false }
		})
		form.resetFields();
	}

	//拖拽事件
	const onDragStart = (ev, item, name, id) => {
		if (!canEdit) {
			return false;
		}
		ev.dataTransfer.setData("otherName", name);
		ev.dataTransfer.setData("otherId", id);
		for (let index of params) {
			ev.dataTransfer.setData(index, item[index]);
		}
	}
	const onDragOver = (ev) => {
		ev.preventDefault();
	}
	const onDrop = (ev) => {

		if (!canEdit) {
			return false;
		}
		let id = ev.dataTransfer.getData("otherId");
		let name = ev.dataTransfer.getData("otherName");
		let item = {};
		for (let index of params) {
			item[index] = ev.dataTransfer.getData(index);
		}

		const newId = ev.target.getAttribute("data-id") || ev.target.parentNode.getAttribute("data-id");

		if (newId && id) {
			let args = newId.split("/");
			let dropId = args[0];
			let dropName = args[1];

			if (dropId === id && dropName === name || dropId !== id) {
				return
			} else {
				let newDimension = dimension;
				let newMeasure = measure;

				if (!name) {
					newMeasure = newMeasure.filter(index => parseInt(index.id) !== parseInt(item.id));
				}
				for (let index of newDimension) {
					if (index.id === id && index.name === name) {
						index.Level = index.Level.filter(obj => {
							return parseInt(obj.id) !== parseInt(item.id);
						});
					}
					if (index.id === dropId && index.name === dropName) {
						if(!index.visible){
							item.visible = false;
						}
						index.Level.push(item);
					}
				}

				dispatch({ type: "biModelId/save", payload: { dimension: newDimension, measure: newMeasure } })
			}
		}
	}

	//修改值
	const modifyField = (column, value, record, name) => {
		if (!canEdit) {
			return;
		}

		dispatch({
			type: "biModelId/save",
			payload: {
				dimension: dimension.map(index => {
					if (index.name === name) {
						index.Level = index.Level.map(item => {
							if (item.id === record.id) {
								item[column] = value;
							}
							return item;
						})
					}
					index.visible = index.Level.some(index=>index.visible);
					return index;
				})
			}
		})
	}

	//修改维度的可见性
	const updateVisible = (name,e)=>{
		e.preventDefault();
		if (!canEdit) {
			return;
		}
		dispatch({
			type: "biModelId/save",
			payload: {
				dimension: dimension.map(index => {
					if (index.name === name) {
						index.visible = !index.visible;
						index.Level = index.Level.map(item => {
							item.visible = index.visible;
							return item;
						})
					}
					return index;
				})
			}
		})
	}

	const getPanel = dimension.map(index => {
		let newArgs = index.Level;
		if (dimensionView) {
			newArgs = newArgs.filter(index => index.visible === true)
		}
		return (
			<Panel data-logo="panel" data-id={`${index.id}/${index.name}`} header={(<div data-logo="panel" >
				<span data-logo="panel" data-id={`${index.id}/${index.name}`} className={styles.headerText}>{`${index.name}(${index.tableName})`}</span>
				<span data-logo="panel" data-id={`${index.id}/${index.name}`} className={styles.headerSpan} ><Icon style={{
					fontSize: "20px",
					color: index.visible ? "#6A9EBB" : "#ccc",
					cursor: "pointer",
				}} 
				onClick={(e) => { updateVisible(index.name,e) }}
				type="eye-o" />
				</span>
			</div>)} id={`${index.id}/${index.name}`} key={index.name}  >
				{
					newArgs.map(item => (
						<div data-logo="panel" data-id={`${index.id}/${index.name}/${item.id}`} key={item.column} draggable={canEdit ? true : false} onDragStart={(ev) => { onDragStart(ev, item, index.name, index.id) }} className={styles.item}>
							<span data-logo="panel" data-id={`${index.id}/${index.name}/${item.id}`} className={styles.itemSpan}>{item.column}</span>
							<span data-logo="panel" data-id={`${index.id}/${index.name}/${item.id}`} className={styles.itemSpan}>
								{
									canEdit ? <EditCell text={item.name} onChange={(e) => { modifyField("name", e, item, index.name) }} /> : item.name
								}
							</span>
							<span data-logo="panel" data-id={`${index.id}/${index.name}/${item.id}`} className={styles.itemSpan}>
								<Icon style={{
									fontSize: "20px",
									color: item.visible ? "#6A9EBB" : "#ccc", cursor: "pointer"
								}} onClick={() => { modifyField("visible", !item.visible, item, index.name) }} type="eye-o" />
							</span>
						</div>
					))
				}
			</Panel>
		)
	});

	const getModalTitle = (action) => {
		switch (action) {
			case "new":
				return "新建";
			case "edit":
				return "重命名";
			case "add":
				return "加入层次";
			default:
				return "新建"
		}
	}


	return (
		<div className={styles.leftTable}>
			<div className={styles.leftHeader}>
				<span>名称</span>
				<span>别名</span>
				<span>可见性</span>
			</div>
			{
				canEdit ? (
					<Dropdown overlay={menu} trigger={['contextMenu']}>
						<div className={styles.leftContent} onDragOver={onDragOver} onDrop={onDrop} onMouseDown={handleMouseClick} >
							<Collapse defaultActiveKey={['1']} >
								{getPanel}
							</Collapse>
						</div>
					</Dropdown>
				) : (
						<div className={styles.leftContent} onDragOver={onDragOver} onDrop={onDrop} onMouseDown={handleMouseClick} >
							<Collapse defaultActiveKey={['1']} >
								{getPanel}
							</Collapse>
						</div>
					)
			}
			<Modal
				visible={dimVisible}
				title={getModalTitle(action)}
				onCancel={handelCancel}
				footer={[
					<Button type="primary" key="sure" onClick={handelSure} >确定</Button>,
					<Button key="cancel" onClick={handelCancel} >取消</Button>
				]}
				style={{ paddingBottom: "0px" }}
			>
				<Form>
					{
						action === "add" ? (
							<FormItem
								label="层次名称"
								labelCol={{ span: 5 }}
								wrapperCol={{ span: 16 }}
							>
								{getFieldDecorator('name', {
									initialValue: "",
									rules: [
										{ required: true, message: '请选择层次名！' },
									],
								})(
									<Select>
										{fields.map(index => <Option key={index.name} value={index.name}>{index.name}</Option>)}
									</Select>
								)}
							</FormItem>
						) : (
								<FormItem
									label="层次名称"
									labelCol={{ span: 5 }}
									wrapperCol={{ span: 16 }}
								>
									{getFieldDecorator('name', {
										initialValue: currentName,
										rules: [
											{ required: true, message: '请输入层次名！' },
											{ validator: handleValidator, message: "层次名不能重复！" }
										],
									})(
										<Input />
									)}
								</FormItem>
							)
					}
				</Form>
			</Modal>
		</div>
	)
}

export default connect(({
	biModelId
}) => ({ biModelId }))(Form.create()(index));