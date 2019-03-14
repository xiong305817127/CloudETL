/**
 * 组织用户管理
 */

import { Input, Checkbox, Row, Col, message } from 'antd';
import { connect } from 'dva';
import { convertArrayToTree } from '../../../utils/utils';
import { addOrgToUser, addUsersToRoles } from '../../../services/usersManage';
import Tree from '../../../components/Tree';
import Modal from 'components/Modal';

import ORStyle from './OrgAndRole.css';

const TreeNode = Tree.TreeNode;
const Search = Input.Search;

// 创建树形菜单
function createTree(list) {
  if (!list || list.length === 0) return null;
  return list.map((item) => (
    <TreeNode title={item.label} key={item.value}>
      {createTree(item.children)}
    </TreeNode>
  ));
}

class OrgAndRole extends React.Component {

  state = {
    roleList: [], // 角色列表
    organizationId: '', // 选中的组织
  }

  componentWillMount() {
    const { dispatch, account, selectedIds } = this.props;
    dispatch({ type: 'usersManage/getAllRoleList', });
  }

  componentWillReceiveProps(nextProps) {
    // 由隐藏切换为显示时执行
    if (nextProps.visible && !this.props.visible) {
      const { dispatch, id } = nextProps;
      dispatch({ type: 'usersManage/getRolesByUserId', payload: id});
    }
    const { allRoleList, belongRoles } = nextProps.usersManage;
    const roleList = allRoleList.map(item => ({
      id: item.id,
      label: item.name,
      checked: belongRoles.some(role => role.id === item.id),
    }));
    this.setState({ roleList, organizationId: nextProps.orgId });
  }

  filterOption = (inputValue, option) => {
    return option.title.indexOf(inputValue) > -1;
  }

  handleChange = (targetKeys) => {
    this.setState({ targetKeys });
  }

  // 选择组织
  onSelectOrganization = (selectedKeys) => {
    if (selectedKeys.length < 1) return;
    const organizationId = parseInt(selectedKeys[0]);
    this.setState({ organizationId });
  }

  // 搜索角色
  handleSearchRole = (e) => {
    const { value } = e.target;
    const { allRoleList, belongRoles } = this.props.usersManage;
    const roleList = allRoleList.filter(item => item.name.indexOf(value) > -1)
      .map(item => ({
        id: item.id,
        label: item.name,
        checked: belongRoles.some(id => id === item.id),
      }));
    this.setState({ roleList });
  }

  // 选中角色
  handleChangeRole = (role, e) => {
    const { checked } = e.target;
    const { roleList } = this.state;
    roleList.some(item => {
      if (item.id === role.id) {
        item.checked = checked;
        return true;
      }
    });
    this.setState({ roleList });
  }

  async handleOk() {
    const { organizationId, roleList } = this.state;
    const { id: userId } = this.props;
    const roles = roleList.filter(item => item.checked).map(item => item.id).join(',');
    const { data: orgRes } = await addOrgToUser({
      orgId: organizationId,
      uid: userId,
    });
    const { data: roleRes } = await addUsersToRoles({
      rIds: roles,
      uIds: userId,
    });
    if (orgRes.code === "200" && roleRes.code === "200") {
      // message.success('分配组织及角色成功');
      if (typeof this.props.onOk === 'function') {
        this.props.onOk();
      }
    } else {
      message.error(
        orgRes.code !== "200"
          ? (orgRes.msg || orgRes.resultMsg)
          : (roleRes.msg || roleRes.resultMsg)
      );
    }
  }

  handleCancel() {
    if (typeof this.props.onCancel === 'function') {
      this.props.onCancel();
    }
  }

  render() {
    const { roleList } = this.state;
    const { organizationTree } = this.props.securityCommon;
    return <Modal
      title={this.props.title}
      visible={this.props.visible}
      closable={false}
      onOk={this.handleOk.bind(this)}
      onCancel={this.handleCancel.bind(this)}
      width={700}
      maskClosable={false}
    >
      <Row>
        <Col span={11}>
          <header className={ORStyle.hd}>
            选择用户所属组织
          </header>
          <div className={ORStyle.treeWrap}>
            <Tree
              onSelect={this.onSelectOrganization}
              defaultExpandedDeep={1}
              selectedKeys={[String(this.state.organizationId)]}
            >
              {createTree(organizationTree)}
            </Tree>
          </div>
        </Col>
        <Col span={11} offset={2}>
          <header className={ORStyle.hd}>
            选择用户关联的角色
            <Search
              placeholder="按角色名称搜索"
              style={{ width: 160, marginLeft: 10 }}
              onChange={this.handleSearchRole}
            />
          </header>
          <div className={ORStyle.selectWrap}>
            <ul>
              {roleList.map(role => (
                <li key={role.id}><Checkbox checked={role.checked} onChange={(e) => this.handleChangeRole(role, e)}>{role.label}</Checkbox></li>
              ))}
            </ul>
          </div>
        </Col>
      </Row>
    </Modal>;
  }
}

export default connect(({ account, usersManage, securityCommon }) => ({
  account,
  usersManage,
  securityCommon,
}))(OrgAndRole);
