/**
 * 组织用户管理
 */

import { Transfer, Row, Col, Tree, message } from 'antd';
import { connect } from 'dva';
import { convertArrayToTree } from '../../../utils/utils';
import { addUserToOrg } from '../../../services/usersManage';
import Modal from 'components/Modal';

const TreeNode = Tree.TreeNode;

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
    treeList: [], // 组织树
    allUsers: [], // 用户列表
    organizationId: '', // 选中的组织
    targetKeys: [], // 已添加列表
    filtered: false, // 是否有用户被过滤
  }

  componentWillMount() {
    const { dispatch, account, selectedIds } = this.props;
    dispatch({ type: 'usersManage/getAllUserList', });
  }

  componentWillReceiveProps(nextProps) {
    const { allOrganizations, allUserList } = nextProps.usersManage;
    const treeList = convertArrayToTree(allOrganizations, '');
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
    this.setState({ treeList, allUsers, targetKeys, filtered: ids.length !== targetKeys.length });
  }

  filterOption = (inputValue, option) => {
    return option.title.indexOf(inputValue) > -1;
  }

  handleChange = (targetKeys) => {
    this.setState({ targetKeys });
  }

  // 选择组织
  onSelectOrganization = (selectedKeys) => {
    const organizationId = parseInt(selectedKeys[0]);
    this.setState({ organizationId });
  }

  async handleOk() {
    const { organizationId, targetKeys } = this.state;
    const { data } = await addUserToOrg({
      orgId: organizationId,
      uIds: targetKeys.join(','),
    });
    if (data.code === "200") {
      message.success('分配组织成功');
      if (typeof this.props.onOk === 'function') {
        this.props.onOk();
      }
    } else {
     // message.error(data.message || data.resultMsg);
    }
  }

  handleCancel() {
    if (typeof this.props.onCancel === 'function') {
      this.props.onCancel();
    }
  }

  render() {
    const { treeList, allUsers, targetKeys } = this.state;
    return <Modal
      title="分配组织"
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
          <div style={{height: 360, overflow: 'auto'}}>
            <Tree
              onSelect={this.onSelectOrganization}
            >
              {createTree(treeList)}
            </Tree>
          </div>
        </Col>
      </Row>
      <div
        style={{textAlign:'center', color: '#f00', display: this.state.filtered ? 'block': 'none'}}
      >
        已过滤已分配过组织的用户
      </div>
    </Modal>;
  }
}

export default connect(({ account, usersManage }) => ({
  account,
  usersManage,
}))(Empower);
