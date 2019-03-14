/**
 * 角色用户管理
 */

import { Transfer, Row, Col, Card, message } from 'antd';
import { connect } from 'dva';
import { convertArrayToTree } from '../../../utils/utils';
import { addUsersToRoles } from '../../../services/usersManage';
import Style from './style.css';
import Modal from 'components/Modal';

// 创建树形菜单
function createTree(list) {
  if (!list || list.length === 0) return null;
  return list.map((item) => (
    <TreeNode title={item.deptName} key={item.id}>
      {createTree(item.childList)}
    </TreeNode>
  ));
}

class Empower extends React.Component {

  state = {
    allRoleList: [], // 组织树
    allUsers: [], // 用户列表
    roleIds: [], // 选中的组织
    targetKeys: [], // 已添加列表
    filtered: false, // 是否有用户被过滤
  }

  componentWillMount() {
    const { dispatch, account } = this.props;
    dispatch({ type: 'usersManage/getAllUserList', });
    dispatch({
      type: 'usersManage/getAllRoleList',
      payload: {
        renterId: account.renterId,
      },
    });
  }

  componentWillReceiveProps(nextProps) {
    const { allRoleList, allUserList } = nextProps.usersManage;
    const allUsers = allUserList.map(item => ({
      key: item.id,
      title: `${item.realName}(${item.username})`,
      disabled: !!item.deptId,
    }));
    const { selectedIds } = nextProps;
    const ids = (typeof selectedIds === 'string' ? selectedIds.split(',') : selectedIds).map(id=>parseInt(id));
    // 过滤出有效选项
    const targetKeys = ids.filter(id=>{
      const found = allUsers.find(user=>user.key == id);
      return ids.length === 1 || !found.disabled;
    });
    this.setState({ allRoleList, allUsers, targetKeys, filtered: ids.length !== targetKeys.length });
  }

  filterOption = (inputValue, option) => {
    return option.title.indexOf(inputValue) > -1;
  }

  handleChange = (targetKeys) => {
    this.setState({ targetKeys });
  }

  // 选择角色
  handleSelectRole = (e) => {
    const options = Array.from(e.target.options).filter((item) => {
      return item.selected;
    });
    const roleIds = options.map(item => parseInt(item.value));
    this.setState({ roleIds });
  }

  handleSelectRole2 = (id) => {
    const { allRoleList } = this.state;
    allRoleList.forEach(item => {
      if (item.id == id) item.selected = true;
      else item.selected = false;
    });
    this.setState({ allRoleList });
  }

  async handleOk() {
    const { roleIds, targetKeys } = this.state;
    const { data } = await addUsersToRoles({
      rIds: roleIds.join(','),
      uIds: targetKeys.join(','),
    });
    console.log(data);
    if (data.code === "200") {
      message.success('分配角色成功');
      if (typeof this.props.onOk === 'function') {
        this.props.onOk();
      }
    } else {
      //message.error(data.message || data.resultMsg);
    }
  }

  handleCancel() {
    if (typeof this.props.onCancel === 'function') {
      this.props.onCancel();
    }
  }

  render() {
    const { allRoleList, allUsers, targetKeys } = this.state;
    return <Modal
      title="分配角色"
      visible={this.props.visible}
      closable={false}
      onOk={this.handleOk.bind(this)}
      onCancel={this.handleCancel.bind(this)}
      width={700}
      maskClosable={false}
    >
      <Row>
        <Col span={16}>
          <Transfer
            titles={['可添加用户', '已选用户']}
            dataSource={allUsers}
            showSearch
            filterOption={this.filterOption}
            targetKeys={targetKeys}
            onChange={this.handleChange}
            render={item => item.title}
            listStyle={{
              height: 360,
            }}
          />
        </Col>
        <Col span={8}>
          <Card title="角色列表"
            className={Style['role-picker']}
            bodyStyle={{ padding: 0 }}
            style={{ marginRight: 10, height: 360 }}
          >
            <select onChange={this.handleSelectRole} multiple="multiple" size="10">
              {allRoleList.map(item=>(
                <option key={item.id} value={item.id}>{item.name}({item.code})</option>
              ))}
            </select>
            {/*<ul>
              {allRoleList.map(item=>(
                <li key={item.id}
                  className={item.selected ? Style.active : ''}
                  onClick={() => this.handleSelectRole(item.id)}
                >
                  {item.name}({item.code})
                </li>
              ))}
            </ul>*/}
          </Card>
        </Col>
      </Row>
      <div
        style={{textAlign:'center', color: '#f00', display: this.state.filtered ? 'block': 'none'}}
      >
        已过滤已分配过角色的用户
      </div>
    </Modal>;
  }
}

export default connect(({ account, usersManage }) => ({
  account,
  usersManage,
}))(Empower);
