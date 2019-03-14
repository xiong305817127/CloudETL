/**
 * 角色授权组件
 */

import { Checkbox, Button, Icon } from "antd";
import { connect } from "dva";
import { convertArrayToTree } from "../../../utils/utils";
import Style from "./style.css";
import Modal from "components/Modal";

const CheckboxGroup = Checkbox.Group;

const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 }
};

// 将树形摊平
function plattenTree(tree, deep = 0) {
  const result = [tree];
  if (!tree) return [];
  tree.deep = deep;
  deep++;
  if (tree.childList) {
    tree.childList.forEach(child => {
      plattenTree(child, deep).forEach(t => {
        result.push(t);
      });
    });
  }
  return result;
}

class Empower extends React.Component {
  state = {
    menuList: [], // 菜单列表
    btnList: [], // 操作列表
    checkTree: {}, // 复选框选择树
    expanded: []
  };

  componentWillMount() {
    const { dispatch } = this.props;
    dispatch({
      type: "roleManage/getResourcesList"
    });
  }

  componentWillReceiveProps(nextProps) {
    const { dispatch } = nextProps;
    const { permissionIds } = nextProps.roleManage;
    // 由隐藏切换为显示时执行
    if (nextProps.visible && !this.props.visible) {
      const { resourcesList } = nextProps.roleManage;
      const menuTree = convertArrayToTree(
        resourcesList.filter(
          item => item.type !== "按钮" && item.clientSystemId !== "security"
        )
      );
      const btnList = resourcesList.filter(item => item.type === "按钮");
      const menuList = plattenTree(menuTree[0]);
      this.setState({ menuList, btnList, checkTree: {} });
      dispatch({
        type: "roleManage/getPermissionById",
        payload: nextProps.id
      });
    }
    // 角色选中选项处理
    if (permissionIds.length > 0) {
      const { menuList, btnList, checkTree } = this.state;
      permissionIds.forEach(id => {
        if (menuList.some(item => item.id === id)) {
          checkTree[id] = this.makeOptions(id)
            .filter(op => {
              return permissionIds.some(permitId => op.value === permitId);
            })
            .map(child => child.value);
        }
      });
      this.setState({ checkTree });
    } else {
      this.setState({ checkTree: {} });
    }
    // 由显示切换为隐藏时，清空数据
    if (!nextProps.visible && this.props.visible) {
      this.setState({
        menuList: [],
        btnList: [],
        checkTree: {}
      });
    }
  }

  // 确定提交授权请求
  handleOk() {
    const { checkTree } = this.state;
    const ids = [];
    Object.keys(checkTree).forEach(pid => {
      ids.push(pid); // 并入父节点id
      checkTree[pid].forEach(id => ids.push(id)); // 并入子节点id
    });
    if (typeof this.props.onOk === "function") {
      this.props.onOk(ids);
    }
  }
  handleCancel() {
    if (typeof this.props.onCancel === "function") {
      this.props.onCancel();
    }
  }

  // 左侧父节点选取
  onParentChange = (id, checked) => {
    const { checkTree, menuList } = this.state;
    if (checked) {
      if (!checkTree[id]) checkTree[id] = [];
      this.traceTree(menuList.find(it => it.id === id).parentId);
    } else {
      delete checkTree[id];
    }
    this.ergodicTree(id, checked);
    this.setState({ checkTree });
  };

  // 右侧子节点选取
  onChildrenChange = (pid, values) => {
    const { checkTree, menuList } = this.state;
    checkTree[pid] = values;
    this.traceTree(menuList.find(it => it.id === pid).parentId);
    this.setState({ checkTree });
  };

  // 向下遍历子节点
  ergodicTree = (pid, checked) => {
    const { checkTree, menuList } = this.state;
    const children = menuList.filter(it => it.parentId === pid);
    if (checked)
      checkTree[pid] = this.makeOptions(pid).map(child => child.value);
    children.forEach(child => {
      if (!checked && checkTree[child.id]) {
        delete checkTree[child.id];
      }
      this.ergodicTree(child.id, checked);
    });
    this.setState({ checkTree });
  };

  // 向上追溯父节点
  traceTree = pid => {
    const { checkTree, menuList } = this.state;
    if (pid && !checkTree[pid]) {
      const parent = menuList.find(it => it.id === pid);
      checkTree[pid] = [];
      this.setState({ checkTree });
      if (parent) {
        this.traceTree(parent.parentId);
      }
    }
  };

  // 构造操作选项
  makeOptions(parentId) {
    return this.state.btnList
      .filter(item => item.parentId == parentId)
      .map(item => ({
        label: item.name,
        value: item.id
      }));
  }

  changeShow = id => {
    return () => {
      const expandedSet = new Set(this.state.expanded);
      if (expandedSet.has(id)) {
        expandedSet.delete(id);
      } else {
        expandedSet.add(id);
      }
      this.setState({
        expanded: Array.from(expandedSet)
      });
    };
  };

  render() {
    return (
      <Modal
        title={this.props.title}
        visible={this.props.visible}
        closable={false}
        onOk={this.handleOk.bind(this)}
        onCancel={this.handleCancel.bind(this)}
        width={800}
        maskClosable={false}
        footer={[
          <Button key="back" onClick={this.handleCancel.bind(this)}>
            取消
          </Button>,
          <Button
            key="submit"
            type="primary"
            loading={this.props.submitLoading}
            onClick={this.handleOk.bind(this)}
          >
            确定
          </Button>
        ]}
      >
        <div style={{ height: 360, overflow: "auto" }}>
          <table className={Style["empower-table"] + " stripe-table"}>
            <thead>
              <tr>
                <th style={{ width: "30%" }}>菜单</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {this.state.menuList.map((row, index) => {
                const arrowShow = row.deep == 1 ;
                const ifExpanded = this.state.expanded.includes(row.clientSystemId);
                const show = row.deep == 1 || row.deep == 0 || (row.deep > 1 && ifExpanded);

                return (
                  <tr key={"juese"+index} style={{ display: show ? "table-row" : "none" }}>
                    <td style={{ paddingLeft: row.deep + 1 + "em" }}>
                      {arrowShow && (
                        <Icon
                          type={ ifExpanded ? "caret-down" : "caret-right" }
                          onClick={this.changeShow(row.clientSystemId)}
                          style={{cursor:"pointer", color:"#0faedb",transform:"translateX(-100%)"}}
                        />
                      )}

                      <Checkbox
                        checked={!!this.state.checkTree[row.id]}
                        onChange={e =>
                          this.onParentChange(row.id, e.target.checked)
                        }
                      >
                        {row.name}
                      </Checkbox>
                    </td>
                    <td>
                      <CheckboxGroup
                        options={this.makeOptions(row.id)}
                        value={this.state.checkTree[row.id]}
                        onChange={values =>
                          this.onChildrenChange(row.id, values)
                        }
                      />
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </Modal>
    );
  }
}

export default connect(({ roleManage }) => ({
  roleManage
}))(Empower);
