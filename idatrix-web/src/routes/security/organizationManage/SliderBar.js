import React from 'react';
import { Tree } from 'antd';
import { convertArrayToTree, deepCopy } from '../../../utils/utils';
import { connect } from 'dva';
import { deleteResource } from '../../../services/securityResources';
import Style from './style.css';

const TreeNode = Tree.TreeNode;

// 创建树形菜单
function createTree(list) {
  if (!list || list.length === 0) return null;
  return list.map((item) => (
    <TreeNode title={item.label} key={item.value}>
      {createTree(item.children)}
    </TreeNode>
  ));
}

class SliderBar extends React.Component {

  state = {
    data: [], // 初始数据
    list: [],
    expandedKeys: [],
    selectedKeys: [],
  };

  componentDidMount() {
    const { dispatch } = this.props;
    // dispatch({ type: 'securityCommon/getOrgList' });
  }

  componentWillReceiveProps(nextProps) {
    const { expandedKeys, selectedKeys } = this.state;
    const { organizationTree, organizationOptions } = nextProps.securityCommon;
    const newState = {};
    // 新增节点检测
    if (organizationOptions.length > 1 && organizationOptions.length - this.props.securityCommon.organizationOptions.length === 1) {
      const newIndex = organizationOptions.length - 1;
      const id = String(organizationOptions[newIndex].parentId);
      newState.selectedKeys = [id];
      if (!expandedKeys.indexOf(id) > -1) {
        expandedKeys.push(id);
        newState.expandedKeys = expandedKeys;
      }
    }
    // 默认展示节点设置
    if (expandedKeys.length === 0 && organizationTree[0]) {
      expandedKeys[0] = String(organizationTree[0].value);
      newState.expandedKeys = expandedKeys;
    }
    // 默认选中节点设置
    if (selectedKeys.length === 0 && organizationTree[0]) {
      selectedKeys[0] = String(organizationTree[0].value);
      newState.selectedKeys = selectedKeys;
      setTimeout(() => {
        this.selectNode(selectedKeys);
      }, 300);
    }
    this.setState(newState);
  }

  onExpand = (expandedKeys) => {
    this.setState({
      expandedKeys,
    });
  };

  onSelect = (selectedKeys) => {
    if (selectedKeys.length > 0) {
      this.setState({
        selectedKeys,
      });
      this.selectNode(selectedKeys);
    }
  };

  // 选中节点后显示详情
  selectNode = (selectedKeys) => {
    const { dispatch } = this.props;
    const id = selectedKeys[0];
    const result = this.state.data.find(item=>item.id == id);
    dispatch({
      type: 'organizationManage/getList',
      payload: {
        parentId: id,
      },
    });
  };

  render() {
    const { list } = this.state;
    const selectedId = this.state.selectedKeys[0];
    const selectedResult = this.state.data.find(item=>item.id == selectedId);
    const type = selectedResult ? selectedResult.type : '';
    const { organizationTree } = this.props.securityCommon;

    return (
      <div style={{padding:10, height: '100%'}}>
        <section className={Style['tree-list']}>
          <Tree
            expandedKeys={this.state.expandedKeys}
            selectedKeys={this.state.selectedKeys}
            autoExpandParent={false}
            onExpand={this.onExpand}
            onSelect={this.onSelect}
          >
            {createTree(organizationTree)}
          </Tree>
        </section>
      </div>
    );
  }
}

export default connect(({ securityCommon }) => ({
  securityCommon,
}))(SliderBar);
