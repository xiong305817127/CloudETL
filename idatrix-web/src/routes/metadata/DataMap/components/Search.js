import React from 'react';
import { connect } from 'dva';
import { Icon, Input, AutoComplete } from 'antd';

const Option = AutoComplete.Option;
const OptGroup = AutoComplete.OptGroup;

// 从guid取出id
const getIdByGuid = (guid) => {
  let id = `${guid.system}.${guid.database}`;
  id += guid.table ? `.${guid.schema}.${guid.table}` : '';
  id += guid.field ? `.${guid.field}` : '';
  return id;
};

class Search extends React.Component {

  state = {
    options: [],
  }

  componentWillReceiveProps = (props) => {
    const { searchList } = props.DataMap;
    this.updateOptions(searchList);
  }

  // 更新选项
  updateOptions = (data) => {
    const obj = {};
    data.forEach((d) => {
      if (!obj[d.levelType]) obj[d.levelType] = [];
      obj[d.levelType].push(d);
    });
    const options = Object.keys(obj).map((key) => {
      const group = obj[key].map(it => ({
        ...it.extra,
        id: getIdByGuid(it.guid),
      }));
      let groupLabel = '';
      switch (key) {
        case '20': groupLabel = '数据库'; break;
        case '40': groupLabel = '数据表'; break;
        case '80': groupLabel = '字段/文件'; break;
        default: groupLabel = key;
      }
      return (<OptGroup
        key={key}
        label={groupLabel}
      >
        {group.map((opt, index) => (
          <Option key={index} value={opt.id} title={opt.name}>
            {opt.name}
          </Option>
        ))}
      </OptGroup>);
    });
    this.setState({ options });
  }

  handleSearch = (value) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'DataMap/search',
      payload: {
        keyword: value,
      },
    });
  }

  handleSelect = (value) => {
    const guid = value.split('.');
    let path = '#DataMap/';
    let id = '';
    switch (guid.length) {
      case 5:
        id = guid.slice(0, 4).join('.');
        path += `fields/${id}/${id}`;
        break;
      case 4:
        id = guid.slice(0, 2).join('.');
        path += `table/${id}`;
        break;
    }
    if (typeof this.props.onSelect === 'function') {
      this.props.onSelect(value);
    }
    location.hash = path;
  }

  render() {
    return (<AutoComplete
      dropdownMatchSelectWidth={false}
      dropdownStyle={{ width: 300 }}
      size="large"
      style={{ width: 382,marginRight: 20}}
      dataSource={this.state.options}
      placeholder={this.props.placeholder}
      optionLabelProp="title"
      onSearch={this.handleSearch}
      onSelect={this.handleSelect}
    >
      <Input suffix={<Icon type="search" />} />
    </AutoComplete>);
  }
}

export default connect(({ DataMap }) => ({
  DataMap: DataMap.toJS(),
}))(Search);
