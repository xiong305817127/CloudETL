/**
 * 公共搜索组件
 * 用法同antd的Input.Search组件
 * 详见 https://ant.design/components/input-cn/#Input.Search
 *
 * @prop  {function}  onSearch      当点击搜索按钮或按下回车键时触发
 * @prop  {string}    placeholder   占位符
 * @prop  {string}    defaultValue  缺省值
 */
import React from 'react';
import { Input, Button } from 'antd';
import PropTypes from 'prop-types';

import Style from './Search.css';

const InputGroup = Input.Group;

class Search extends React.Component {
  static propTypes = {
    // 点击搜索按钮时触发
    onSearch: PropTypes.func.isRequired,
    defaultValue: PropTypes.string,
    // 占位符
    placeholder: PropTypes.string,
    // 宽度
    width: PropTypes.string,
  }

  state = {
    defaultValue: '',
    value: '',
  }

  componentWillMount() {
    this.mergeStatus(this.props);
  }

  componentWillReceiveProps(nextProps) {
    this.mergeStatus(nextProps);
  }

  // 合并状态
  mergeStatus(props) {
    this.setState(props);
  }

  // 点击搜索
  handleSearch() {
    const { value } = this.state;
    const { onSearch } = this.props;
    if (typeof onSearch === 'function') {
      onSearch(value);
    }
  }

  handleChange(e) {
    const { onChange } = this.props;
    this.setState({ value: e.target.value });
    if (typeof onChange === 'function') {
      onChange(e);
    }
  }

  render() {
    const { defaultValue } = this.state;
    const { placeholder, width } = this.props;
    const style = { width,display: "inline-block" };
    return (<InputGroup compact className={Style['search-group']} style={style}>
      <Input
        placeholder={placeholder || '输入关键字查询'}
        defaultValue={decodeURIComponent(defaultValue || '')}
        style={{ width: 'calc(100% - 50px)', verticalAlign: "middle"}}
        className={Style['search-input']}
        onPressEnter={this.handleSearch.bind(this)}
        onChange={this.handleChange.bind(this)}
        spellCheck={false}
      />
      <Button onClick={this.handleSearch.bind(this)} icon="search" />
    </InputGroup>);
  }
}

export default Search;
