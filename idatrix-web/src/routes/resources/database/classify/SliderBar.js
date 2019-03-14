import React from 'react';
import { Icon, Input, Button, Tree, Tooltip, message, Modal } from 'antd';
import { convertArrayToTree, deepCopy, downloadFile } from 'utils/utils';
import { connect } from 'dva';
import { deleteResource } from 'services/securityResources';
import Empower from 'components/Empower'; // 导入授权组件
import Style from './index.less';
import FileUpload from 'components/FileUpload/FileUpload.js';
import { API_BASE_CATALOG } from '../../../../constants';
const TreeNode = Tree.TreeNode;

// // 创建树形菜单
// function createTree(list) {
// 	return list.map((item) => {
// 		if (item.children) {
// 			return (
// 				<TreeNode parentId={item.parentId} dataRef={item} isLeaf={!item.hasChildFlag} resourceEncode={item.resourceEncode} key={item.id} title={item.resourceName}>
// 					{createTree(item.children)}
// 				</TreeNode>
// 			)
// 		}
// 		return <TreeNode parentId={item.parentId} dataRef={item} isLeaf={!item.hasChildFlag} resourceEncode={item.resourceEncode} key={item.id} title={item.resourceName} />
// 	});
// }

// class SliderBar extends React.Component {

// 	state = {
// 		list: [],
// 		expandedKeys: [],
// 		selectedKeys: [],
// 		selectedNodes: [],
// 		// creatingTreeNode: false
// 	}

// 	componentDidMount() {
// 		const list = deepCopy(this.props.data);
// 		const expandedKeys = list[0] ? [String(list[0].id)] : [];
// 		const selectedKeys = list[0] ? [String(list[0].id)] : [];
// 		this.setState({ list, expandedKeys, selectedKeys }, () => {
// 			this.selectNode(selectedKeys);
// 		});
// 	}

// 	componentWillReceiveProps(nextProps) {
// 		const { expandedKeys, selectedKeys } = this.state;

// 		console.log(nextProps, "更新参数");

// 		const list = deepCopy(nextProps.data);
// 		const newState = { list, creatingTreeNode: false };
// 		// 新增节点检测
// 		if (nextProps.data.length > 1 && nextProps.data.length - this.props.data.length === 1) {
// 			const index = nextProps.data.length - 1;
// 			const id = String(nextProps.data[index].id);
// 			newState.selectedKeys = [id];
// 			if (!expandedKeys.indexOf(id) > -1) {
// 				expandedKeys.push(id);
// 				newState.expandedKeys = expandedKeys;
// 			}
// 		}
// 		// 默认展示节点设置
// 		if (expandedKeys.length === 0 && list[0]) {
// 			expandedKeys[0] = String(list[0].id);
// 			newState.expandedKeys = expandedKeys;
// 		}
// 		// 默认选中节点设置
// 		if (selectedKeys.length === 0 && list[0]) {
// 			selectedKeys[0] = String(list[0].id);
// 			newState.selectedKeys = selectedKeys;
// 			setTimeout(() => {
// 				this.selectNode(selectedKeys);
// 			}, 300);
// 		}
// 		this.setState(newState);
// 	}

// 	// 快速定位
// 	handleSearch(val) {
// 		const result = this.props.data.find(item => item.name.indexOf(val) > -1);
// 		if (result) {
// 			const keys = [String(result.id)];
// 			this.setState({
// 				expandedKeys: keys,
// 				selectedKeys: keys,
// 			});
// 			this.selectNode(keys);
// 		}
// 	}

// 	// // 刷新
// 	// handleReload = () => {
// 	//   const { dispatch } = this.props;
// 	//   this.setState({
// 	//     data: this.props.data,
// 	//     creatingTreeNode: false,
// 	//   });
// 	//   dispatch({
// 	//     type: 'resourcesManage/getResourcesList',
// 	//     payload: {},
// 	//   });
// 	// }

// 	// 删除节点
// 	handleDelete = () => {
// 		const { dispatch } = this.props;
// 		const id = this.state.selectedKeys[0];
// 		Modal.confirm({
// 			content: '确定要删除该节点吗？',
// 			onOk: () => {
// 				dispatch({
// 					type: "databaseModel/deleteNodeInfo",
// 					payload: { id }
// 				})
// 			}
// 		});
// 	}

// 	onExpand = (expandedKeys) => {
// 		this.setState({
// 			expandedKeys,
// 		});
// 	}

// 	onSelect = (selectedKeys, e) => {
// 		if (selectedKeys.length > 0) {
// 			this.setState({
// 				selectedKeys,
// 				selectedNodes: e.selectedNodes,
// 			});
// 			this.selectNode(selectedKeys);
// 		}
// 	}

// 	// 选中节点后显示详情
// 	selectNode = (selectedKeys) => {
// 		const { dispatch } = this.props;
// 		const id = selectedKeys[0];
// 		if (id) {
// 			dispatch({ type: "databaseModel/getNode", payload: { id } })
// 		}
// 	}

// 	// 新增子节点
// 	addTreeNode = () => {
// 		const { dispatch } = this.props;
// 		dispatch({
// 			type: "databaseModel/save",
// 			payload: { actionType: "new", visible: true }
// 		})
// 	}

// 	//编辑节点
// 	editTreeNode = () => {
// 		const { dispatch } = this.props;
// 		dispatch({
// 			type: "databaseModel/save",
// 			payload: { visible: true, actionType: "edit" }
// 		})
// 	};

// 	//上传文件
// 	uploadTreeNode = () => {

// 	}
// 	//下载文件
// 	downloadExcel = () => {
// 		downloadFile('files/excel-template/资源分类管理模板.xlsx');
// 	};

// 	//加载子节点
// 	onLoadData = treeNode => new Promise((resolve) => {
// 		const { dispatch } = this.props;
// 		console.log(treeNode.props.dataRef);

// 		if (treeNode.props.children) {
// 			resolve();
// 			return;
// 		} else {
// 			dispatch({
// 				type: "resourcesCommon/getResourcesFolder",
// 				resolve,
// 				treeNode,
// 				dataList: this.props.data,
// 				payload: {
// 					id: treeNode.props.eventKey
// 				}
// 			})
// 		}
// 		// setTimeout(() => {
// 		//   treeNode.props.dataRef.children = [
// 		//     { title: 'Child Node', key: `${treeNode.props.eventKey}-0` },
// 		//     { title: 'Child Node', key: `${treeNode.props.eventKey}-1` },
// 		//   ];
// 		//   this.setState({
// 		//     treeData: [...this.state.treeData],
// 		//   });
// 		//   resolve();
// 		// }, 1000);
// 	})

// 	render() {
// 		const { list } = this.state;

// 		console.log(list, "参数");

// 		const { parentId } = this.props;

// 		const fileUploadProps = {
// 			fileName: "file",
// 			uploadUrl: `${API_BASE_CATALOG}/classify/batchImport`,
// 			multiple: false,
// 			uploadBtn: (fuc) => {
// 				return (
// 					<Tooltip title="文件上传">
// 						<Button className={Style['tree-btn']} type="primary" size="small" icon="upload"
// 							onClick={fuc} />
// 					</Tooltip>
// 				)
// 			},
// 			handleCallback: (fileList) => {
// 				const { dispatch } = this.props;
// 				const { status, response } = fileList[0];
// 				if (status === "done" && response.code === "200") {
// 					dispatch({
// 						type: "databaseModel/fileImport",
// 						payload: {
// 							fileName: response.data.fileName
// 						}
// 					})
// 				}
// 			}
// 		}

// 		return (
// 			<div style={{ padding: 10, position: 'absolute', top: 0, bottom: 0, left: 0, right: 0 }}>
// 				<header>
// 					<div className={Style['tree-btns-wrap']}>
// 						{/*<Tooltip title="刷新">
//               <Button className={Style['tree-btn']} type="primary" size="small" icon="reload"
//                       onClick={this.handleReload} />
//             </Tooltip>*/}

// 						<Tooltip title="新增子节点">
// 							<Button className={Style['tree-btn']} type="primary" size="small" icon="plus-square-o"
// 								onClick={this.addTreeNode} />
// 						</Tooltip>
// 						<Tooltip title="编辑">
// 							<Button className={Style['tree-btn']} type="primary" size="small" icon="edit"
// 								disabled={parentId === 0}
// 								onClick={this.editTreeNode} />
// 						</Tooltip>
// 						<FileUpload {...fileUploadProps} />
// 						<Tooltip title="删除">
// 							<Button className={Style['tree-btn']} type="primary" size="small" icon="delete"
// 								disabled={parentId === 0}
// 								onClick={this.handleDelete} />
// 						</Tooltip>
// 						<Tooltip title="下载模板">
// 							<Button className={Style['tree-btn']} type="primary" size="small" icon="download"
// 								onClick={this.downloadExcel} />
// 						</Tooltip>
// 					</div>
// 					{/*<Input.Search placeholder="输入资源关键字定位" onSearch={this.handleSearch.bind(this)} />*/}
// 				</header>
// 				<section className={Style['tree-list']}>
// 					<Tree
// 						showLine
// 						loadData={this.onLoadData}
// 						expandedKeys={this.state.expandedKeys}
// 						selectedKeys={this.state.selectedKeys}
// 						onExpand={this.onExpand}
// 						onSelect={this.onSelect}
// 					>
// 						{createTree(list)}
// 					</Tree>
// 				</section>
// 			</div>
// 		);
// 	}
// }

// export default connect(({ resourcesCommon }) => ({ resourcesCommon }))(SliderBar);

const SliderBar = ({  databaseModel, dispatch }) => {

	const { resourcesList,config } = databaseModel;
	const { parentId,id } = config;

	// 新增子节点
	const addTreeNode = () => {
		if(id){
			dispatch({
				type: "databaseModel/save",
				payload: { actionType: "new", visible: true }
			})
		}else{
			message.info("请先选择父节点！");
		}
	}

	//编辑节点
	const editTreeNode = () => {
		if(id){
			dispatch({
				type: "databaseModel/save",
				payload: { visible: true, actionType: "edit" }
			})
		}else{
			message.info("请先选择节点！");
		}
	};

	// 删除节点
	const handleDelete = () => {
		if(id){
			Modal.confirm({
				content: '确定要删除该节点吗？',
				onOk: () => {
					dispatch({
						type: "databaseModel/deleteNodeInfo",
						payload: { id }
					})
				}
			});
		}else{
			message.info("请先选择要删除的节点！");
		}
	}

	//下载文件
	const downloadExcel = () => {
		downloadFile('files/excel-template/资源分类管理模板.xlsx');
	};

	//选择节点
	const onSelect = (node) => {
		dispatch({ type: "databaseModel/getNode", payload: { id: node[0] ? node[0] : "" } })
	}

	//加载子节点
	const onLoadData = treeNode => new Promise((resolve) => {
		if (treeNode.props.children) {
			resolve();
			return;
		} else {
			dispatch({
				type: "databaseModel/getResourcesFolder",
				resolve,
				treeNode,
				resourcesList,
				payload: {
					id: treeNode.props.eventKey
				}
			})
		}
	});

	//加载treeNode节点
	const renderTreeNodes = data => data.map((item) => {
		if (item.children) {
			return (
				<TreeNode isLeaf={!item.hasChildFlag} title={item.resourceName} key={item.id} dataRef={item}>
					{renderTreeNodes(item.children)}
				</TreeNode>
			);
		}
		return <TreeNode isLeaf={!item.hasChildFlag} title={item.resourceName} key={item.id} {...item} dataRef={item} />;
	});

	const fileUploadProps = {
		fileName: "file",
		uploadUrl: `${API_BASE_CATALOG}/classify/batchImport`,
		multiple: false,
		uploadBtn: (fuc) => {
			return (
				<Tooltip title="文件上传">
					<Button className={Style['tree-btn']} type="primary" size="small" icon="upload"
						onClick={fuc} />
				</Tooltip>
			)
		},
		handleCallback: (fileList) => {
			const { dispatch } = this.props;
			const { status, response } = fileList[0];
			if (status === "done" && response.code === "200") {
				dispatch({
					type: "databaseModel/fileImport",
					payload: {
						fileName: response.data.fileName
					}
				})
			}
		}
	}


	return (
		<div style={{ padding: 10, position: 'absolute', top: 0, bottom: 0, left: 0, right: 0 }}>
			<header>
				<div className={Style['tree-btns-wrap']}>
					{/*<Tooltip title="刷新">
              <Button className={Style['tree-btn']} type="primary" size="small" icon="reload"
                      onClick={this.handleReload} />
            </Tooltip>*/}

					<Tooltip title="新增子节点">
						<Button className={Style['tree-btn']} type="primary" size="small" icon="plus-square-o"
							onClick={addTreeNode} />
					</Tooltip>
					<Tooltip title="编辑">
						<Button className={Style['tree-btn']} type="primary" size="small" icon="edit"
							disabled={parentId === 0}
							onClick={editTreeNode} />
					</Tooltip>
					<FileUpload {...fileUploadProps} />
					<Tooltip title="删除">
						<Button className={Style['tree-btn']} type="primary" size="small" icon="delete"
							disabled={parentId === 0}
							onClick={handleDelete} />
					</Tooltip>
					<Tooltip title="下载模板">
						<Button className={Style['tree-btn']} type="primary" size="small" icon="download"
							onClick={downloadExcel} />
					</Tooltip>
				</div>
				{/*<Input.Search placeholder="输入资源关键字定位" onSearch={this.handleSearch.bind(this)} />*/}
			</header>
			<section className={Style['tree-list']}>
				<Tree
					showLine
					loadData={onLoadData}
					// expandedKeys={this.state.expandedKeys}
					// selectedKeys={this.state.selectedKeys}
					// onExpand={this.onExpand}
					onSelect={onSelect}
				>
					{renderTreeNodes(resourcesList)}
				</Tree>
			</section>
		</div>
	)

}

export default connect(({ databaseModel }) => ({ databaseModel }))(SliderBar);
