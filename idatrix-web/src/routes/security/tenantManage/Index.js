//1.
import React from "react";
import {
  Table,
  Button,
  Icon,
  Form,
  Input,
  Popconfirm,
  message,
  Tooltip
} from "antd";
import { connect } from "dva";
import TableList from "../../../components/TableList";
import Empower from "../../../components/Empower";
import { deleteTenant } from "../../../services/securityTenant";
import { hashHistory } from "dva/router";

const { Column, ColumnGroup } = Table;

//2.
class TenantManagementTable extends React.Component {
  //初始化状态：
  state = {
    list: [],
    loading: false
  };

  // 禁用租户，也可以用此方法开启租户
  disableTenant(id, status) {
    return () => {
      const { dispatch } = this.props;
      dispatch({
        type: "tenantManage/disable",
        payload: { renterIds: id, status: status }
      });
    };
  }

  resetPassword(id) {
    return () => {
      const { dispatch } = this.props;

      dispatch({
        type: "tenantManage/resetPassword",
        payload: { renterId: id }
      });
    };
  }
  //加载页面前渲染数据:数据通过connect连接
  componentWillMount() {
    /**
     * 从tenantManage接收数据
     * tenantManage在model.js中定义
     */
    const { tenantManage } = this.props;
    this.setState(tenantManage);
  }
  //再次加载数据:数据依旧通过connect连接
  componentWillReceiveProps(nextProps) {
    /**
     * 从tenantManage接收数据
     * tenantManage在model.js中定义
     */
    const { tenantManage } = nextProps;
    this.setState(tenantManage);
  }

  //点击删除
  async handleDelete(record) {
    const {
      dispatch,
      location: { query }
    } = this.props;
    const { data } = await deleteTenant({ ids: record.id });
    if (data.code === "200") {
      message.success("删除成功.");
      dispatch({
        type: "tenantManage/getList",
        payload: {
          pageNo: query.page,
          pageSize: query.pageSize
        }
      });
    } else {
      //message.error(data.message || data.resultMsg);
    }
  }

  render() {
    const data = this.state; //表格数据的指代data
    return (
      <div style={{ padding: "20px 10px", backgroundColor: "#fff" }}>
        {/*新建步骤条*/}
        <Empower api="/renter/add.shtml">
          <Button
            type="primary"
            style={{ marginBottom: 10 }}
            onClick={() => {
              hashHistory.push("/security/TenantManagementTable/NewTableFlow");
            }}
          >
            新增
          </Button>
        </Empower>
        {/*公共组件TableList的表格:封装了序号的输出showIndex*/}
        <TableList
          rowKey="id"
          dataSource={data.list}
          loading={data.loading}
          pagination={{ total: data.totalCount }}
          showIndex
          className="th-nowrap"
        >
          <Column key="1" title="序号" width={80} dataIndex="__index" />
          <Column
            key="2"
            title="租户名称"
            className="td-nowrap"
            dataIndex="renterName"
          />
          <Column
            key="3"
            title="管理员账号"
            className="td-nowrap"
            dataIndex="adminAccount"
          />
          <Column
            key="4"
            title="姓名"
            className="td-nowrap"
            dataIndex="adminName"
          />
          <Column
            key="5"
            title="手机"
            className="td-nowrap"
            dataIndex="adminPhone"
          />
          <Column
            key="6"
            title="邮箱"
            className="td-nowrap"
            dataIndex="adminEmail"
          />
          <Column
            key="7"
            title="开通服务"
            dataIndex="openedService"
            render={(text, record) => (
              <div className="word25" title={text}>
                {text.split(",").join(", ")}
              </div>
            )}
          />
          <Column
            key="8"
            title="开通资源"
            dataIndex="openedResource"
            render={(text, record) => {
              const words = text
                .split(",")
                .map(id => {
                  const result = data.resourcesList.find(
                    res => res.clientSystemId == id
                  );
                  return result ? result.name : id;
                })
                .join(", ");
              return (
                <div className="word25" title={words}>
                  {words}
                </div>
              );
            }}
          />
          {/*<Column key="8" title="状态" className="td-nowrap td-center" width={60} render={(text, record) => record.renterStatus ? '有效' : '无效'} />*/}
          <Column
            key="9"
            title="操作"
            className="td-nowrap td-center"
            width={90}
            render={(text, record) => {
              if (!record.renterStatus) {
                return null;
              }
              const action = record.renterStatus == 1 ? "禁用" : "开启";
              return (
                <div>
                  <Empower api="/renter/update.shtml">
                    <a
                      href={`#/security/TenantManagementTable/ModifyTableFlow/${
                        record.id
                      }`}
                      style={{ marginRight: 10 }}
                    >
                      <Tooltip title="编辑">
                        <Icon type="edit" className="op-icon" />
                      </Tooltip>
                    </a>
                  </Empower>
                  <span>
                    <Tooltip title={`点击${action}`} placement="bottom">
                      <Popconfirm
                        title={`是否要${action}该账户`}
                        onConfirm={this.disableTenant(
                          record.id,
                          record.renterStatus == 1 ? 2 : 1
                        )}
                        okText={action}
                        cancelText="取消"
                      >
                        {record.renterStatus == 1 ? (
                          <Icon
                            type="unlock"
                            style={{
                              color: "blue",
                              cursor: "pointer",
                              fontSize: "18px"
                            }}
                          />
                        ) : (
                          <Icon
                            type="lock"
                            style={{
                              color: "red",
                              cursor: "pointer",
                              fontSize: "18px"
                            }}
                          />
                        )}
                      </Popconfirm>
                    </Tooltip>
                  </span>
                  <span>
                    <Popconfirm
                      title="是否要重置此租户的密码？"
                      onConfirm={this.resetPassword(record.id)}
                    >
                      <Tooltip title="重置密码" placement="bottom">
                        <Icon
                          type="reload"
                          style={{
                            color: "#CC00FF",
                            cursor: "pointer",
                            marginLeft: "8px",
                            fontSize: "16px"
                          }}
                        />
                      </Tooltip>
                    </Popconfirm>
                  </span>
                  {/*<Empower api="/renter/delete.shtml">
                <a>
                  <Popconfirm title="确认要删除该租户吗？" onConfirm={() => this.handleDelete(record)}>
                    <Tooltip title="删除"><Icon type="delete" className="op-icon" /></Tooltip>
                  </Popconfirm>
                </a>
              </Empower>*/}
                </div>
              );
            }}
          />
        </TableList>
      </div>
    );
  }
}
//3.connect的引入，类似Form的引入，目的：传参。
export default connect(({ tenantManage }) => ({
  tenantManage
}))(TenantManagementTable);
