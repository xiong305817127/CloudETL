/**
 * Tree组件二次封装
 * 添加控制默认显示层数的能力
 * defaultExpandedKeys优先级大于defaultExpandDeep
 *
 * @prop  {number} defaultExpandDeep   设置展开深度
 */
import React from 'react';
import { Tree } from 'antd';
import PropTypes from 'prop-types';

const DEFAULT_EXPAND_DEEP = 1; // 默认展开深度

const defaultExpandedKeysArray = [];

/**
 * 根据深度值获取keys
 * @param  {array}  list 节点列表
 * @param  {number} deep 当前深度
 * @return {array}       key值数组
 */
const getKeysByDeep = (list, deep) => {
  if (deep <= 0) return [];
  const keys = [];
  deep --;
  list.forEach(item => {
    keys.push(item.key);
    if (Array.isArray(item.props.children)) {
      getKeysByDeep(item.props.children, deep).forEach(key => keys.push(key));
    }
  });
  return keys;
};

// 更新默认展开keys
const updateDefaultExpandedKeys = (keys) => {
  defaultExpandedKeysArray.splice(0);
  keys.forEach(key => {
    defaultExpandedKeysArray.push(key);
  });
};

class NewTree extends React.Component {
  static TreeNode = Tree.TreeNode;

  static propTypes = {
    defaultExpandDeep: PropTypes.number,
  };

  constructor(props) {
    super(props);
  }

  state = {
    defaultExpandedKeys: [],
  }

  componentWillMount() {
    this.updateStateByProps(this.props);
  }

  componentWillReceiveProps(nextProps) {
    this.updateStateByProps(nextProps);
  }

  // 根据属性更新状态
  updateStateByProps(props) {
    const { defaultExpandDeep, defaultExpandedKeys } = props;
    if (Array.isArray(defaultExpandedKeys) && defaultExpandedKeys.length > 0) {
      updateDefaultExpandedKeys(defaultExpandedKeys);
    } else if (props.children) {
      const keys = getKeysByDeep(props.children, defaultExpandDeep || DEFAULT_EXPAND_DEEP);
      updateDefaultExpandedKeys(keys);
    }
  }

  render() {
    return <Tree {...this.props} defaultExpandedKeys={defaultExpandedKeysArray} />
  }
}

export default NewTree;
