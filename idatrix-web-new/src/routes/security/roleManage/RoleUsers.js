/**
 * 管理组织机构用户 组件
 */
import { Transfer, TreeSelect } from 'antd';
import { connect } from 'dva';
import { convertArrayToTree } from '../../../utils/utils';
import Modal from 'components/Modal';

function mapTreeList(list) {
  if (!list) return null;
  return list.map(item => {
    const id = String(item.id);
    item.label = item.deptName;
    item.value = id;
    item.key = id;
    item.children = mapTreeList(item.childList);
    return item;
  })
}

class RoleUsers extends React.Component {

  state = {
    allUsers: [], // 用户列表
    targetKeys: [], // 已添加列表
    orgId: '', // 组织id
  }

  componentWillMount() {
    const { dispatch, account } = this.props;
    dispatch({ type: 'securityCommon/getOrgList' });
    dispatch({ type: 'usersManage/getAllUserList' });
  }

  componentWillReceiveProps(nextProps) {
    const { targetKeys } = this.state;
    const { allUserList } = nextProps.usersManage;
    // 组织用户列表
    const { userList: orgUsers } = nextProps.organizationManage;
    // 角色用户列表
    const { userList: roleUsers } = nextProps.roleManage;
    // 由隐藏切换为显示时，清理缓存数据
    if (nextProps.visible && !this.props.visible) {
      const { dispatch, id } = nextProps;
      this.setState({
        targetKeys: [],
      });
      targetKeys.splice(0);
      roleUsers.splice(0);
      orgUsers.splice(0);
      dispatch({ type: 'roleManage/getUsersByRoleId', payload: id});
    }
    // 仅初始化一次，防止多次加载刷掉数据
    const newTargetKeys = targetKeys.length ? targetKeys : roleUsers.map(u => u.id);
    const allUsers = allUserList.map(item => ({
      key: item.id,
      title: `${item.realName}(${item.username})`,
    })).filter(item => {
      if (!orgUsers.length && !this.state.orgId) return true;
      return orgUsers.some(user => user.id == item.key) || newTargetKeys.indexOf(item.key) > -1;
    });
    this.setState({ allUsers, targetKeys: newTargetKeys });
  }

  // 穿梭框选择用户
  handleChange = (targetKeys) => {
    this.setState({ targetKeys });
  }

  // 组织机构切换
  handleOrgChange = (value) => {
    const { dispatch } = this.props;
    this.setState({
      orgId: value,
    });
    dispatch({ type: 'organizationManage/getUsersByOrgId', payload: value });
  }

  handleOk() {
    const { targetKeys } = this.state;
    const { id, onOk } = this.props;
    onOk(id, targetKeys);
  }

  handleCancel() {
    if (typeof this.props.onCancel === 'function') {
      this.props.onCancel();
    }
  }

  filterOption = (inputValue, option) => {
    return option.title.indexOf(inputValue) > -1;
  }

  render() {
    const { allUsers, targetKeys } = this.state;
    const { organizationTree } = this.props.securityCommon;

    return <Modal
      title={this.props.title}
      visible={this.props.visible}
      closable={false}
      onOk={this.handleOk.bind(this)}
      onCancel={this.handleCancel.bind(this)}
      maskClosable={false}
      width={700}
    >
      <TreeSelect
        placeholder="选择组织机构"
        treeData={organizationTree}
        onChange={this.handleOrgChange}
        style={{width: 200, marginBottom: 10}}
        allowClear
      />
      <Transfer
        titles={['可把下列用户添加到角色', '角色中已有的用户']}
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

export default connect(({ roleManage, organizationManage, usersManage, account, securityCommon }) => ({
  account,
  roleManage,
  organizationManage,
  usersManage,
  securityCommon,
}))(RoleUsers);
