import react from "react";
import Modal from 'components/Modal';
import { connect } from 'dva';
import { Form, Icon, Input, Tree, Button, Cascader, message } from 'antd';
import styles from '../index.less';
import { routerRedux } from 'dva/router';
import qs from 'qs';

const DirectoryTree = Tree.DirectoryTree;
const TreeNode = Tree.TreeNode;
const FormItem = Form.Item;

let foldId = "";
let Timer = null;

const index = ({ dispatch, form, biDatamodel }) => {

	const { newModal, folderTreeList, options } = biDatamodel;
	const { getFieldDecorator } = form;

	//提交
	const handleSubmit = (e) => {
		e.preventDefault();
		form.validateFields((err, values) => {
			console.log(values);
			if (!err) {
				if (foldId) {

					let params = values.dataSource[1].split("/");
					let dsId = params[0];
					let databaseName = params[1];
					handleCancel();
					const querystring = qs.stringify({
						dsId, databaseName, categoryId: foldId, name: values.name, action: "new"
					});
					dispatch(routerRedux.push(`/analysis/DataModel/Config?${querystring}`));
				} else {
					message.warn("请先选择文件夹！");
				}
			}
		});
	}

	//选择文件夹
	const onSelect = (e) => {
		foldId = e[0];
	};

	//关闭模态框
	const handleCancel = () => {
		form.resetFields();
		dispatch({
			type: "biDatamodel/hide",
			payload: {
				newModal: false
			}
		})
	}

	//文件夹扩展
	const renderTreeNodes = (data) => {
		if(data){
			return data.map((item) => {
				return <TreeNode {...item} dataRef={item} title={item.name} key={item.id} />;
			});
		}
		return null;
	};

	//新建文件夹
	const handeNewFolder = () => {
		dispatch({
			type: "biDatamodel/save",
			payload: {
				newFolder: true
			}
		})
	}

	//加载数据
	const loadData = (selectedOptions) => {

		const targetOption = selectedOptions[selectedOptions.length - 1];
		targetOption.loading = true;
		const type = selectedOptions[0].value;
		const length = selectedOptions.length;
		let dsId = "";
		if(length >= 2){
			dsId = targetOption.value.split("/")[0];
		}
		// load options lazily
		return new Promise((resolve, reject) => {
			dispatch({
				type: "biDatamodel/getDataSource",
				payload: {
					targetOption,
					dsType: type,
					length,dsId
				},
				resolve, reject
			})
		})
	}

	//检测schame名字
	const handleCheckName = (rule, value, callback) => {
		if (Timer) {
			clearTimeout(Timer);
			Timer = null;
		}
		if (value) {
			Timer = setTimeout(() => {
				dispatch({
					type: "biDatamodel/isExistFolder",
					payload: { name: value },
					action: "name",
					callback
				});
			}, 1000)
		}
	}



	return (
		<Modal
			visible={newModal}
			title="新建模型"
			onCancel={handleCancel}
			footer={[
				<Button key="new" onClick={handeNewFolder} style={{ float: "left" }} >新建文件夹</Button>,
				<Button type="primary" key="sure" onClick={handleSubmit}>确定</Button>,
				<Button key="cancel" onClick={handleCancel}>取消</Button>
			]}
		>
			<Form onSubmit={handleSubmit} className="login-form">
				<FormItem
					label="数据模型名称"
					labelCol={{ span: 6 }}
					wrapperCol={{ span: 16 }}
				>
					{getFieldDecorator('name', {
						rules: [
							{ required: true, message: '数据模型名称不能为空！' },
							{ validator: handleCheckName }
						],
					})(
						<Input placeholder="请输入数据模型名称" />
					)}
				</FormItem>
				<FormItem
					label="数据源"
					labelCol={{ span: 6 }}
					wrapperCol={{ span: 16 }}
				>
					{getFieldDecorator('dataSource', {
						rules: [{ required: true, message: '数据源不能为空！' }],
					})(
						<Cascader
							placeholder="请选择数据源"
							options={options}
							loadData={loadData}
							changeOnSelect
						/>
					)}
				</FormItem>
			</Form>
			<div className={styles.treeList}>
				<div className={styles.header}>&nbsp;</div>
				<div className={styles.tree}>
					<DirectoryTree
						multiple
						defaultExpandAll
						onSelect={onSelect}
					>
						<TreeNode disabled title="全部文件夹" key="0-0">
							{renderTreeNodes(folderTreeList)}
						</TreeNode>
					</DirectoryTree>
				</div>
			</div>
		</Modal>
	)
}

export default connect(({
	biDatamodel
}) => ({ biDatamodel }))(Form.create()(index));