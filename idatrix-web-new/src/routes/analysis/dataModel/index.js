import { Icon, Dropdown, Menu, Tree, Select, Input, message } from 'antd';
import styles from "./index.less";
import Modal from "components/Modal";
import { connect } from "dva";
import NewFolder from "./components/NewFolder";
import NewModal from "./components/NewModal";
import ViewContent from "./components/ViewContent";
import { routerRedux } from 'dva/router';
import qs from 'qs';
import { isEmpowered } from "components/Empower";

const Search = Input.Search;
const DirectoryTree = Tree.DirectoryTree;
const TreeNode = Tree.TreeNode;
const InputGroup = Input.Group;
const Option = Select.Option;
const confirm = Modal.confirm;
//true 为文件夹，false为数据模型
let name = "";

const index = ({ biDatamodel, system, dispatch }) => {

	const { folderTreeList, view, selectType, menuId, expandedKeys } = biDatamodel;

	console.log(folderTreeList, "目录树");

	const handleMenuClick = ({ item, key }) => {
		if (key === "0") {
			//新建模型
			dispatch({ type: "biDatamodel/save", payload: { newModal: true } })
		} else if (key === "1") {
			//新建文件夹
			dispatch({ type: "biDatamodel/save", payload: { newFolder: true } })
		} else if (key === "2") {
			//删除文件夹
			confirm({
				title: '确定删除该文件夹吗？',
				content: `${name}`,
				okText: '确定',
				cancelText: '取消',
				onOk() {
					dispatch({ type: "biDatamodel/deleteFolder" })
				}
			});
		} else if (key === "3") {
			//编辑模型
			const querystring = qs.stringify({
				id: menuId, action: "edit"
			});
			dispatch(routerRedux.push(`/analysis/DataModel/Config?${querystring}`));
		} else if (key === "4") {
			//删除模型
			confirm({
				title: '确定删除该模型吗？',
				content: `${name}`,
				okText: '确定',
				cancelText: '取消',
				onOk() {
					dispatch({ type: "biDatamodel/deleteModel" })
				}
			});
		} else if (key === "5") {
			//删除模型
			confirm({
				title: '确定一键导入吗？',
				okText: '确定',
				cancelText: '取消',
				onOk() {
					dispatch({ type: "biDatamodel/importToSaiku" });
				}
			});
		}
	};


	// <Empower api="/saiku/rest/schema/insertSchema" disableType="hide" ><span></span></Empower>?
	// <Empower api="/saiku/rest/schema/batchDelete" disableType="hide" ><span></span></Empower>?
	// <Empower api="/saiku/rest/schema/importToSaiku" disableType="hide" ><span></span></Empower>?

	const menu =
		(
			<Menu onClick={handleMenuClick}>
				{
					isEmpowered("/saiku/rest/schema/insertSchema", system) ? (
						<Menu.Item key="0">
							新建模型
						</Menu.Item>
					) : null
				}
				{
					isEmpowered("/saiku/rest/schema/batchDelete", system) ? (
						<Menu.Item key="1">
							新建文件夹
						</Menu.Item>
					) : null
				}
				{
					isEmpowered("/saiku/rest/schema/importToSaiku", system) ? (
						<Menu.Item key="5">
							一键导入
						</Menu.Item>
					) : null
				}
			</Menu>
		)

	const treeMenu = (
		<Menu onClick={handleMenuClick}>
			<Menu.Item key="1">
				新建文件夹
		    </Menu.Item>
			<Menu.Item key="2" >
				删除文件夹
		    </Menu.Item>
		</Menu>
	)

	const ModelMenu = (
		<Menu onClick={handleMenuClick}>
			<Menu.Item key="3">
				编辑模型
		    </Menu.Item>
			<Menu.Item key="4" >
				删除模型
		    </Menu.Item>
		</Menu>
	)

	const renderTreeNodes = (data) => {
		return data.map((item, text) => {
			if (item.schemaVoList) {
				return (
					<TreeNode title={item.name} key={`folder_${item.id}`} dataRef={item} >
						{renderTreeNodes(item.schemaVoList)}
					</TreeNode>
				);
			}
			return item.noVisible ? null : <TreeNode {...item} dataRef={item} title={item.name} key={item.id} isLeaf={item.schemaVoList ? false : true} />
		});
	};

	const onSelect = (selectedKeys, { node }) => {
		const { props } = node;
		if (props.isLeaf) {
			dispatch({
				type: "biModelId/openView",
				payload: {
					id: parseInt(props.eventKey),
					action: "view"
				}
			})
			dispatch({ type: "biDatamodel/save", payload: { view: true } })
		}
	}

	const handleRightClick = ({ node }) => {
		const { isLeaf, dataRef } = node.props;
		let selectType = true;
		if (isLeaf) {
			selectType = false;
		}
		let menuId = "";
		if (dataRef.id) {
			menuId = dataRef.id;
			name = dataRef.name;
		}
		dispatch({ type: "biDatamodel/save", payload: { selectType, menuId } })
	}

	const handleSearch = (value) => {
		let expandedKeys = [];
		for (let index of folderTreeList) {
			index.schemaVoList = index.schemaVoList.map(item => {
				if (item.name.indexOf(value) !== -1) {
					item.noVisible = false;
				} else {
					item.noVisible = true;
				}
				return item
			});
			if (index.schemaVoList.length > 0) {
				expandedKeys.push(index.id + "");
			}
		}
		dispatch({ type: "biDatamodel/save", payload: { folderTreeList, expandedKeys } })
	}

	const onExpand = (expandedKeys) => {
		dispatch({ type: "biDatamodel/save", payload: { expandedKeys } })
	}

	return (
		<div className={styles.dataModel}>
			<div className={styles.sliderTree}>
				<div className={styles.treeHeader}>
					<div className={styles.name}>数据模型</div>
					<div className={styles.icon}>
						<Dropdown overlay={menu} trigger={['click']}>
							<Icon type="plus-circle-o" />
						</Dropdown>
					</div>
				</div>
				<div className={styles.treeSearch}>
					<InputGroup compact>
						<Select defaultValue="名称">
							<Option value="名称">名称</Option>
						</Select>
						<Search
							placeholder="关键字搜索"
							onSearch={value => handleSearch(value)}
							style={{ width: 140 }}
						/>
					</InputGroup>
				</div>
				<Dropdown overlay={selectType ? treeMenu : ModelMenu} trigger={['contextMenu']}>
					<div className={styles.treeContent}>
						<DirectoryTree
							onSelect={onSelect}
							onRightClick={handleRightClick}
							expandedKeys={expandedKeys}
							onExpand={onExpand}
						>
							{renderTreeNodes(folderTreeList)}
						</DirectoryTree>
					</div>
				</Dropdown>
			</div>
			<div className={styles.content}>
				{
					view ? <ViewContent /> : (
						<div className={styles.welcome}>
							<img src={require("assets/images/analysis/folder.png")} />
							<span className={styles.welcomeFont}>请在左侧新建或选择数据模型</span>
						</div>
					)
				}
			</div>
			<NewFolder />
			<NewModal />
		</div>
	)
}

export default connect(({
	biDatamodel, system
}) => ({ biDatamodel, system }))(index);