/**
 * 申请权限组件
 */
import React from 'react';
import { Checkbox, message } from 'antd';
import { connect } from 'dva';
import PropTypes from 'prop-types';
import Style from './style.less';
import Modal from 'components/Modal';

const CheckboxGroup = Checkbox.Group;

// 选项
const options = [
  { label: '查看表结构', value: 1 },
  { label: '查看样例数据', value: 2 },
  { label: '查看数据地图', value: 3 },
  { label: '查看部分真实数据', value: 4 },
  { label: '在IDE界面查询真实数据', value: 5 },
  { label: '通过ETL或跑批在平台内跨租户交换数据', value: 6 },
  { label: '通过数据服务被平台外系统查询', value: 7 },
];

class Application extends React.Component {
  // 属性定义
  static propTypes = {
    visible: PropTypes.bool.isRequired,
    onSubmit: PropTypes.func.isRequired,
    onCancel: PropTypes.func.isRequired,
  }

  state = {
    selectedList: [],
  }

  componentWillMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'resourcesCommonChange/getPermitsOptions' });
  }

  componentWillReceiveProps(nextProps) {
    const { permitsResults } = nextProps.data;
    this.setState({
      selectedList: permitsResults.map(item => item.auth),
    });
  }

  handleChange(val) {
    this.setState({
      selectedList: val,
    });
  }

  handleSubmit() {
    const { selectedList } = this.state;
    if (selectedList.length === 0) {
      message.warn('请选择权限');
    } else {
      const bits = selectedList.reduce((sum, val) => sum | val);
      this.props.onSubmit(bits);
    }
  }

  render() {
    const { data, type, resourcesCommonChange: { permits } } = this.props;
    const options = type === 'table' ? permits.tableOptions : permits.fileOptions;
    const title = `${(type === 'table' ? '表' : '文件目录')}权限申请`;

    return <Modal
      title={title}
      visible={this.props.visible}
      onOk={this.handleSubmit.bind(this)}
      onCancel={this.props.onCancel}
    >
      <CheckboxGroup
        value={this.state.selectedList}
        className={Style['appl-checkbox']}
        options={options}
        onChange={this.handleChange.bind(this)} />
    </Modal>
  }
}

export default connect(({ resourcesCommonChange }) => ({
  resourcesCommonChange,
}))(Application);
