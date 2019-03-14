/**
 * Cascader级联选择二次封装
 * 添加显示title的效果
 */
import React from 'react';
import { Cascader } from 'antd';

/**
 * 根据传入的value数组递归查找title
 * @param  {array}  options 选项数组
 * @param  {array}  values  value数组
 * @return {string}         返回查找到的title
 */
const findTitleByValues = (options, values) => {
  const value = values.shift();
  const found = options.find(it => it.value == value);
  let title = '';
  if (found) {
    title = found.label;
    if (Array.isArray(found.children)) {
      title += '\/' + findTitleByValues(found.children, values);
    }
  }
  return title;
}

class NewCascader extends React.Component {
  constructor(props) {
    super(props);
  }

  componentWillMount() {
    this.updateStateByProps(this.props);
  }

  componentWillReceiveProps(nextProps) {
    this.updateStateByProps(nextProps);
  }

  updateStateByProps(props) {
    const value = props.value ? [...props.value] : [];
    const title = findTitleByValues(props.options, value);
    this.setState({ title });
  }

  render() {
    return <Cascader title={this.state.title} {...this.props} />
  }
}

export default NewCascader;
