import React from 'react';
import { Icon, Input, Button, Tree, Tooltip, message } from 'antd';
import { convertArrayToTree, deepCopy } from '../../../utils/utils';
import { connect } from 'dva';
import { deleteResource } from '../../../services/securityResources';
import Empower from '../../../components/Empower'; // 导入授权组件
import Style from './style.css';
import Modal from 'components/Modal';

const TreeNode = Tree.TreeNode;

// 创建树形菜单
function createTree(list) {
  if (!list || list.length === 0) return null;
  return list.map((item) => (
    <TreeNode title={item.name} key={item.id}>
      {createTree(item.childList)}
    </TreeNode>
  ));
}

class SliderBar extends React.Component {

  state = {
    data: [], // 初始数据
    list: [],
    expandedKeys: [],
    selectedKeys: [],
    selectedNodes: [],
    creatingTreeNode: false,
    autoExpandParent: true,
  }

  componentDidMount() {
    const data = deepCopy(this.props.data);
    const list = convertArrayToTree(data || [], null);
    const expandedKeys = list[0] ? [String(list[0].id)] : [];
    const selectedKeys = list[0] ? [String(list[0].id)] : [];
    this.setState({ data, list, expandedKeys, selectedKeys }, () => {
      this.selectNode(selectedKeys);
    });
  }

  componentWillReceiveProps(nextProps) {
    const { expandedKeys, selectedKeys } = this.state;
    const data = deepCopy(nextProps.data);
    const list = convertArrayToTree(data || [], null);
    const newState = { data, list, creatingTreeNode: false };
    // 新增节点检测
    if (nextProps.data.length > 1 && nextProps.data.length - this.props.data.length === 1) {
      const index = nextProps.data.length - 1;
      const id = String(nextProps.data[index].id);
      newState.selectedKeys = [id];
      if (!expandedKeys.indexOf(id) > -1) {
        expandedKeys.push(id);
        newState.expandedKeys = expandedKeys;
      }
    }
    // 默认展示节点设置
    if (expandedKeys.length === 0 && list[0]) {
      expandedKeys[0] = String(list[0].id);
      newState.expandedKeys = expandedKeys;
    }
    // 默认选中节点设置
    if (selectedKeys.length === 0 && list[0]) {
      selectedKeys[0] = String(list[0].id);
      newState.selectedKeys = selectedKeys;
      setTimeout(() => {
        this.selectNode(selectedKeys);
      }, 300);
    }
    this.setState(newState);
  }

  // 快速定位
  handleSearch(val) {
    const result = this.props.data.find(item=>item.name.indexOf(val) > -1);
    if (result) {
      const keys = [String(result.id)];
      this.setState({
        expandedKeys: keys,
        selectedKeys: keys,
      });
      this.selectNode(keys);
    }
  }

  // 刷新
  handleReload = () => {
    const { dispatch } = this.props;
    this.setState({
      data: this.props.data,
      creatingTreeNode: false,
    });
    dispatch({
      type: 'resourcesManage/getResourcesList',
      payload: {},
    });
  }

  // 删除节点
  handleDelete = () => {
    const { dispatch } = this.props;
    const id = this.state.selectedKeys[0];
    const result = this.props.data.find(item=>item.id == id);
    if (result) {
      if (!result.parentId) {
        message.warn('该节点不允许删除');
        return;
      }
      const delModal = Modal.confirm({
        content: '确定要删除该节点吗？',
        onOk: () => {
          (async () => {
            const data = this.state.data.filter(item => item.id != id);
            const res = await deleteResource({ids: id});
            if (res.data.code === "200") {
              this.setState({ data });
              message.success('删除成功');
              this.handleReload();
              dispatch({
                type: 'resourcesManage/showResource',
                payload: {},
              });
            } else {
              //message.error(res.data.message || res.data.resultMsg);
            }
          })()
          // delModal.destroy();
        }
      });
    }
  }

  onExpand = (expandedKeys) => {
    this.setState({
      autoExpandParent: false,
      expandedKeys,
    });
  }

  onSelect = (selectedKeys, e) => {
    if (selectedKeys.length > 0) {
      this.setState({
        selectedKeys,
        selectedNodes: e.selectedNodes,
      });
      this.selectNode(selectedKeys);
    }
  }

  // 选中节点后显示详情
  selectNode = (selectedKeys) => {
    const { dispatch } = this.props;
    const id = selectedKeys[0];
    const result = this.state.data.find(item=>item.id == id);
    if (result) {
      dispatch({
        type: 'resourcesManage/showResource',
        payload: result,
      });
    }
  }

  // 新增节点
  addTreeNode = (type) => {
    const id = this.state.selectedKeys[0];
    const result = this.state.data.find(item=>item.id == id);
    if (result) {
      const type = !result.clientSystemId || !result.parentId ? '系统' : '菜单';
      if (this.state.creatingTreeNode) {
        message.warn('当前有新节点未保存');
        return;
      }
      const { data } = this.state;
      const id = `_${new Date().getTime()}`; // 临时id
      const expandedKeys = this.state.expandedKeys;
      expandedKeys.push(id);
      data.push({
        id,
        name: '新资源',
        isShow: true,
        url: '',
        urlDesc: '',
        clientSystemId: result.clientSystemId,
        parentId: result.id,
        type,
        childList: [],
      });
      const list = convertArrayToTree(data || [], null);
      this.setState({
        list,
        data,
        creatingTreeNode: true,
        expandedKeys,
        selectedKeys: [id],
        autoExpandParent: true,
      });
      this.selectNode([id]);
    }
  }

  render() {
    const { list, selectedNodes } = this.state;
    const selectedId = this.state.selectedKeys[0];
    const selectedResult = this.state.data.find(item=>item.id == selectedId);
    const type = selectedResult ? selectedResult.type : '';
    const hasChildren = selectedNodes[0] && selectedNodes[0].props.children ? selectedNodes[0].props.children.length > 0 : false;

    return (
      <div style={{padding:10, position: 'absolute', top: 0, bottom: 0, left: 0, right: 0}}>
        <header>
          <div className={Style['tree-btns-wrap']}>
            {/*<Tooltip title="刷新">
              <Button className={Style['tree-btn']} type="primary" size="small" icon="reload"
                      onClick={this.handleReload} />
            </Tooltip>*/}
            <Empower api="/permission/add.shtml">
              <Tooltip title="新增节点">
                <Button className={Style['tree-btn']} type="primary" size="small" icon="plus-square-o"
                        disabled={this.state.selectedKeys.length===0 || !type || type === '按钮'}
                        onClick={this.addTreeNode} />
              </Tooltip>
            </Empower>
            <Empower api="/permission/deletePermissionById.shtml">
              <Tooltip title="删除">
                <Button className={Style['tree-btn']} type="primary" size="small" icon="delete"
                        disabled={this.state.selectedKeys.length===0 || !type || type === '系统' || hasChildren}
                        onClick={this.handleDelete}/>
              </Tooltip>
            </Empower>
          </div>
          {/*<Input.Search placeholder="输入资源关键字定位" onSearch={this.handleSearch.bind(this)} />*/}
        </header>
        <section className={Style['tree-list']}>
          <Tree
            expandedKeys={this.state.expandedKeys}
            selectedKeys={this.state.selectedKeys}
            autoExpandParent={this.state.autoExpandParent}
            onExpand={this.onExpand}
            onSelect={this.onSelect}
          >
            {createTree(list)}
          </Tree>
        </section>
      </div>
    );
  }
}

export default connect()(SliderBar);
