/**
 * 管理组织机构用户 组件
 */
import { Transfer } from 'antd';
import { connect } from 'dva';
import Modal from 'components/Modal';

class OrgUsers extends React.Component {

  state = {
    allUsers: [], // 用户列表
    targetKeys: [], // 已添加列表
  };

  componentWillMount() {
    const { dispatch, account } = this.props;
    dispatch({ type: 'usersManage/getAllUserList' });
  }

  componentWillReceiveProps(nextProps) {
    // 由隐藏切换为显示时，清理缓存数据
    if (nextProps.visible && !this.props.visible) {
      const { dispatch, data: { id: orgId } } = nextProps;
      this.setState({
        targetKeys: [],
      });
      dispatch({ type: 'organizationManage/getUsersByOrgId', payload: orgId});
    }
    const { allUserList } = nextProps.usersManage;
    const allUsers = allUserList.map(item => ({
      key: item.id,
      title: `${item.realName}(${item.username})`,
    }));
    const { userList } = nextProps.organizationManage;
    this.setState({ allUsers, targetKeys: userList.map(u => u.id) });
  }

  handleChange = (targetKeys) => {
    this.setState({ targetKeys });
  };

  handleOk() {
    const { targetKeys } = this.state;
    const { data: { id: orgId }, onOk } = this.props;
    onOk(orgId, targetKeys);
  }

  handleCancel() {
    if (typeof this.props.onCancel === 'function') {
      this.props.onCancel();
    }
  }

  filterOption = (inputValue, option) => {
    return option.title.indexOf(inputValue) > -1;
  };

  render() {
    const { allUsers, targetKeys } = this.state;
    return <Modal
      title={this.props.title}
      visible={this.props.visible}
      closable={false}
      onOk={this.handleOk.bind(this)}
      onCancel={this.handleCancel.bind(this)}
      maskClosable={false}
      width={720}
    >
      <Transfer
        titles={['可添加用户', '组织已有用户列表']}
        dataSource={allUsers}
        showSearch
        searchPlaceholder="按用户名称或账号搜索"
        filterOption={this.filterOption}
        targetKeys={targetKeys}
        onChange={this.handleChange}
        render={item => item.title}
        listStyle={{
          height: 360,
          width: 300,
        }}
      />
    </Modal>
  }
}

export default connect(({ organizationManage, usersManage }) => ({
  organizationManage,
  usersManage,
}))(OrgUsers);
