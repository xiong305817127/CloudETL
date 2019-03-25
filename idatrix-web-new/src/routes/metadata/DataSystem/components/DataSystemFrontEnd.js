/**
 * Created by Administrator on 2017/8/22.
 */
import React from "react";
import {
  Layout,
  Button,
  Table,
  Tabs,
  message,
  Icon,
  Popconfirm,
  Tooltip
} from "antd";
const TabPane = Tabs.TabPane;
const ButtonGroup = Button.Group;
import { withRouter } from "react-router";
import { connect } from "dva";
import {
  delete_ftp_table_fields,
  get_database_table_fields,
  get_ftp_table_fields,
  queryPlatformList,
  search_ftp_table_fields,
  get_table_struct,
  delete_database_table_fields
} from "services/metadata";
import TableList from "components/TableList";
import Empower from "components/Empower";

class DataSystemFrontEnd extends React.Component {
  constructor() {
    super();

    this.state = {
      pagination: {
        current: 1,
        pageSize: 10
      },
      data1: [],
      data2: [],
      loading: false,

      routerListened: false, // 是否已监听路由
      isMounted: false // 组件是否已挂载
    };

    // 绑定方法
    this.handleDatabase = this.handleDatabase.bind(this);
    this.handleDatabaseRES = this.handleDatabaseRES.bind(this);
    this.addModel2 = this.addModel2.bind(this);
    this.handelTabsChange = this.handelTabsChange.bind(this);
  }

  // 获取部门名称
  getDeptNames = ids => {
    const { departmentsOptions } = this.props.metadataCommon;
    const result = [];
    try {
      const arr = typeof ids === "string" ? JSON.parse(ids) : ids;
      arr.forEach(id => {
        const found = departmentsOptions.find(dept => dept.value == id);
        if (found) result.push(found.label);
      });
    } catch (err) {}
    return result.join("、");
  };

  columns1 = [
    {
      title: "数据库名称",
      dataIndex: "dbDatabasename",
      key: "dbDatabasename"
    },
    {
      title: "创建者",
      dataIndex: "creator",
      key: "creator"
    },
    {
      title: "数据库中文名",
      dataIndex: "dsName"
    },
    {
      title: "所在的前置机名称",
      dataIndex: "serverName",
      key: "serverName"
    },
    {
      title: "IP地址",
      dataIndex: "serverIp",
      key: "serverIp"
    },
    {
      title: "所属组织",
      dataIndex: "organization",
      key: "organization",
      render: this.getDeptNames
    },
    {
      title: "数据库类型",
      dataIndex: "dsType",
      key: "dsType",
      render: text =>
        text === 2 ? "Oracle" : "" 
        || text === 3 ? "MySQL" : "" 
        || text === 14 ? "DM": ""
        || text === 8 ? "PostgreSql":""
    },
    {
      title: "状态",
      dataIndex: "status",
      key: "status"
    },
    {
      title: "新建/注册",
      dataIndex: "type",
      key: "type",
      render: text => (text === "register" ? "注册" : "新建")
    },
    {
      title: "备注",
      dataIndex: "remark",
      key: "remark",
      render: text => (
        <div className="word25" title={text}>
          {text}
        </div>
      )
    },
    {
      title: "操作",
      dataIndex: "x123",
      key: "x123",
      render: (text, record) => {
        /*{record.status !== '未生效'?(
              <a
                href={"#/DataSystemSegistration/StorageTable/" +record.dsId+"/"+record.serverName+"/"+record.dbDatabasename}
                style={{marginRight: 10}}
              >
                <Tooltip title="库表" >
                  <Icon type="layout" className="op-icon" onClick={()=>{this.handleDSRegister1(record)}}/>
                </Tooltip>
              </a>
          ) : null}*/
        return (
          <div>
            <Empower
              api="/dataSourceInfoRegController/update"
              disabled={!record.canEdited}
            >
              <a style={{ marginRight: 10 }}>
                <Tooltip title="编辑">
                  <Icon
                    onClick={() => {
                      this.editModel(record);
                    }}
                    type="edit"
                    className="op-icon"
                  />
                </Tooltip>
              </a>
            </Empower>
           {/**
           <Empower api="/dataSourceInfoRegController/updateStatus" disabled>
              <Popconfirm title="确认要删除该行吗?" onConfirm={() => { this.confirm(record); }} onCancel={() => {  this.cancel(); }}
                okText="是"  cancelText="否" >
                <a>
                  <Tooltip title="删除">
                    <Icon type="delete" className="op-icon" />
                  </Tooltip>
                </a>
              </Popconfirm>
            </Empower> */}
          </div>
        );
      }
    }
  ];
  //确定删除
  confirm(record) {
    record.status = 1;
    delete_database_table_fields([record]).then(res => {
      if (res.data.msg == "Success") {
        this.Request();
        message.success("删除成功");
      } else {
        message.success("删除失败");
      }
    });
  }
  //取消删除
  cancel() {
    return false;
  }
  //编辑
  editModel(record) {
    console.log(record.type, "record");
    for (let index of this.state.data1) {
      if (index.dsId === record.dsId) {
        const { dispatch } = this.props;
        dispatch({
          type: "databasemodel/show",
          visible: true,
          model: "editmodel",
          modelDle: record.type === "register" ? "host" : "last",
          info: index
        });
      }
    }
  }
  //更新
  componentWillReceiveProps(nextProps) {
    const { actionKey } = nextProps.datasystemsegistration;
    if (actionKey === "updatemodel" && this.state.isMounted === true) {
      this.Request();
    }
  }

  columns2 = [
    {
      title: "用户名",
      dataIndex: "ftpUser",
      key: "ftpUser",
      className: "td-nowrap"
    },
    {
      title: "创建者",
      dataIndex: "creator",
      key: "creator"
    },
    {
      title: "前置机名称",
      dataIndex: "serverName",
      key: "serverName"
    },
    {
      title: "对接的组织",
      dataIndex: "organization",
      key: "organization",
      render: this.getDeptNames
    },
    {
      title: "状态",
      dataIndex: "status",
      key: "status"
    },
    {
      title: "备注",
      dataIndex: "remark",
      key: "remark",
      render: text => (
        <div className="word25" title={text}>
          {text}
        </div>
      )
    },
    {
      title: "操作",
      dataIndex: "",
      key: "x",
      render: (text, record) => {
        //这里的text和record没区别
        return (
          <div>
            <Empower
              api="/ftpUserPathServiceController/update"
              disabled={!record.canEdited}
            >
              <a>
                <Icon
                  onClick={() => {
                    this.editModel2(record);
                  }}
                  type="edit"
                  className="op-icon"
                />
              </a>
            </Empower>
            &nbsp;&nbsp;&nbsp;&nbsp;
            <Empower api="/ftpUserPathServiceController/updateStatus"  disabled={!record.canEdited} >
              <Popconfirm placement="topLeft" title="确认要删除该行吗？" onConfirm={() => { this.confirm2(record); }} >
                <a>
                  <Tooltip title="删除">
                    <Icon type="delete" className="op-icon" />
                  </Tooltip>
                </a>
              </Popconfirm>
            </Empower>
          </div>
        );
      }
    }
  ];
  //确定删除2
  confirm2(record) {
    record.status = 1;
    delete_ftp_table_fields([record]).then(res => {
      if (res.data.code == "200") {
        this.Request();
        message.success("删除成功");
      } else {
        message.error("删除失败，"+ res.data.msg ? res.data.msg : "");
      }
    });
  }
  editModel2(record) {
    for (let index of this.state.data2) {
      if (index.id === record.id) {
        const { dispatch } = this.props;
        dispatch({
          type: "ftpmodel/show",
          visible: true,
          model: "editmodel",
          modelDle: "host",
          info: index
        });
      }
    }
  }

  addModel2() {
    const { dispatch } = this.props;
    dispatch({
      type: "ftpmodel/show",
      visible: true,
      model: "newmodel",
      info: {}
    });
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: "metadataCommon/getDepartments" });
    this.setState(
      {
        isMounted: true
      },
      () => {
        const { router } = this.props;
        if (!this.state.routerListened) {
          router.listen(location => {
            let model = location.query.model ? location.query.model : "1";
            if (
              location.pathname === "/DataSystemSegistration" &&
              model == 1 &&
              this.state.isMounted
            ) {
              console.log(123);
              this.Request();
            }
          });
          this.setState({
            routerListened: true
          });
        }
      }
    );
  }

  Request() {
    const { query } = this.props.system;
    const pager = { ...this.state.pagination };
    console.log(query, "query");
    this.setState({
      loading: true
    });

    let tabType = query.tabType ? query.tabType : "1";
    console.log(tabType, "tabType");
    if (tabType == 1) {
      // 新增renter直接传入
      // edited by steven leo
      const { renterId } = this.props.account;

      queryPlatformList(
        {
          current: query.page || 1,
          pageSize: query.pageSize || pager.pageSize
        },
        { sourceId: 1, renterId: renterId }
      ).then(res => {
        console.log(res, "前置机平台");
        if (res.data) {
          const { total, rows } = res.data.data;
          pager.total = total;
          for (let index of rows) {
            if (index.status === 0) {
              index.status = "未生效";
            } else if (index.status === 1) {
              index.status = "已删除";
            } else if (index.status === 2) {
              index.status = "已生效";
            }
          }
          rows.map((row, index) => {
            row.key = row.dsId;
            if (row.frontEndServer) {
              row.serverName = row.frontEndServer.serverName;
              row.serverIp = row.frontEndServer.serverIp;
              row.organization = row.frontEndServer.organization;
            }
            row.index = pager.pageSize * (pager.current - 1) + index + 1;
            return row;
          });

          this.setState({
            loading: false,
            data1: rows,
            pagination: pager
          });
        }
      });
    } else {
      console.log(this.props, "this.props123");
      get_ftp_table_fields(
        {
          page: query.page || 1,
          rows: query.pageSize || pager.pageSize
        },
        {
          renterId: this.props.account.renterId
        }
      ).then(res => {
        if (res.data) {
          const { total, rows } = res.data.data;
          pager.total = total;
          for (let index of rows) {
            if (index.status === 0) {
              index.status = "未生效";
            } else if (index.status === 1) {
              index.status = "已删除";
            } else {
              index.status = "已生效";
            }
          }
          rows.map((row, index) => {
            row.key = row.id;
            if (row.frontEndServer) {
              row.serverName = row.frontEndServer.serverName;
              row.serverIp = row.frontEndServer.serverIp;
              row.organization = row.frontEndServer.organization;
            }
            row.index = pager.pageSize * (pager.current - 1) + index + 1;
            return row;
          });
          this.setState({
            loading: false,
            data2: rows,
            pagination: pager
          });
        }
      });
    }

    setTimeout(() => {
      if (this.state.loading) {
        try {
          this.setState({ loading: false });
        } catch (err) {}
      }
    }, 4000);
  }

  handleDatabase() {
    console.log("新建visible", this.props);
    const { dispatch } = this.props;

    dispatch({
      type: "databasemodel/show",
      visible: true,
      model: "newmodel",
      info: {}
    });
    console.log(dispatch, "dispatch({type");
  }
  handleDatabaseRES() {
    console.log("注册visible", this.props);
    const { dispatch } = this.props;

    dispatch({
      type: "databasemodel/show",
      visible: true,
      model: "newmodelres",
      info: {}
    });
    console.log(dispatch, "dispatch({type2111");
  }

  handleDSRegister() {
    if (this.state.selectedRowKeys1.length == 0) {
      message.info("请选择数据库2222");
    } else if (this.state.selectedRowKeys1.length == 1) {
      const { dispatch } = this.props;
      dispatch({
        type: "dsregistermodel/show",
        visible: true,
        model: "newTableModel",
        info: {
          dsId: this.state.selectedRows1[0].dsId
        }
      });
    } else if (this.state.selectedRowKeys1.length > 1) {
      message.info("只能选择一个数据库");
    }
  }
  handleDSRegister1(record) {
    const { dispatch } = this.props;
    dispatch({
      type: "storagetable/show",
      model: "newTableModel",
      info: {
        dsId: record.dsId
      }
    });
  }

  //Tabs活动面板
  getActiveKey() {
    const { query } = this.props.location;
    if ((!query.model || query.model === "1") && query.tabType) {
      return query.tabType + "";
    } else {
      return "1";
    }
  }

  //通过路由定义面板
  handelTabsChange(e) {
    const location = this.props.location;
    if (e) {
      location.query.tabType = e;
      location.query.page = 1;
      location.query.pageSize = 10;
    } else {
      delete location.query.tabType;
    }
    this.props.router.push(location);
  }
  componentWillUnmount() {
    this.state.isMounted = false;
  }

  render() {
    const { dispatch } = this.props;
    return (
      <section style={{ backgroundColor: "#fff",}} className="padding_20" style={{paddingTop: 0}}>
        <Tabs
          onChange={this.handelTabsChange}
          activeKey={this.getActiveKey()}
        >
          <TabPane tab="数据库" key="1">
            <Layout style={{ height: "100%", background: "white" }}>
              <div>
                <div style={{ width: "100%" }}>
                  <ButtonGroup style={{ marginBottom: 20 }}>
                    {/*新建9.11*/}
                    <Empower api="/dataSourceInfoRegController/insert">
                      <Tooltip
                        placement="bottom"
                        title="新建：在前置机新建一个数据库"
                      >
                        <Button
                          type="primary"
                          onClick={this.handleDatabase}
                          style={{ marginRight: 10 }}
                        >
                          新建
                        </Button>
                      </Tooltip>
                    </Empower>
                    <Empower api="/dataSourceInfoRegController/insert">
                      <Tooltip
                        placement="bottom"
                        title="注册：前置机外部数据库登记"
                      >
                        <Button
                          type="primary"
                          onClick={this.handleDatabaseRES}
                          style={{ marginRight: 10 }}
                        >
                          注册
                        </Button>
                      </Tooltip>
                    </Empower>
                  </ButtonGroup>
                </div>
                <TableList
                  showIndex
                  columns={this.columns1}
                  pagination={this.state.pagination}
                  loading={this.state.loading}
                  dataSource={this.state.data1}
                />
              </div>
            </Layout>
          </TabPane>
          <TabPane tab="SFTP用户" key="2">
            <Layout style={{ height: "100%", background: "white" }}>
              <div style={{ width: "100%" }}>
                <ButtonGroup style={{marginBottom: 10 }}>
                  {/*新建2*/}
                  <Empower api="/ftpUserPathServiceController/insert">
                    <Button type="primary" onClick={this.addModel2}>
                      新建
                    </Button>
                  </Empower>
                </ButtonGroup>
              </div>
              <TableList
                showIndex
                pagination={this.state.pagination}
                loading={this.state.loading}
                columns={this.columns2}
                dataSource={this.state.data2}
                className="th-nowrap"
              />
            </Layout>
          </TabPane>
        </Tabs>
      </section>
    );
  }
}

export default withRouter(
  connect(({ metadataCommon, account, system }) => ({
    metadataCommon,

    account,
    system
  }))(DataSystemFrontEnd)
);
