/**
 * Created by Administrator on 2017/8/22.
 */
import React from "react";
import { connect } from "dva";
import {
  Layout,
  Button,
  Table,
  Form,
  Input,
  Row,
  Col,
  Tabs,
  Icon,
  Popconfirm,
  message,
  Tooltip
} from "antd";
import { withRouter } from "react-router";
const TabPane = Tabs.TabPane;
import Tab1 from "./DSRegisterPlatform/Tab1";
import { get_platform_url } from "services/metadata";
import { queryPlatformList } from "services/metadata";
import {
  get_platform_HDFStree,
  delete_database_table_fields
} from "services/metadata";
import Tab1Model from "./DSRegisterPlatform/Tab1Model";
import TableList from "components/TableList";
import Tree from "components/Tree";
import Empower from "components/Empower";

const TreeNode = Tree.TreeNode;
const x = 3;
const y = 2;
const z = 1;
const gData = [];
const generateData = (_level, _preKey, _tns) => {
  const preKey = _preKey || "0";
  const tns = _tns || gData;
  const children = [];
  for (let i = 0; i < x; i++) {
    const key = `${preKey}-${i}`;
    tns.push({ title: key, key });
    if (i < y) {
      children.push(key);
    }
  }
  if (_level < 0) {
    return tns;
  }
  const level = _level - 1;
  children.forEach((key, index) => {
    tns[index].children = [];
    return generateData(level, key, tns[index].children);
  });
};
generateData(z);

class DataSystemPlatform extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      expandedKeys: ["0-0", "0-0-0", "0-0-0-0"],
      selectedKeys: [],
      data: [],
      pagination: {
        current: 1,
        pageSize: 10
      },
      loading: false,
      info: {},
      gData: [],
      RootDirectory: "",
      selectedTitle: "",
      routerListened: false, // 是否已监听路由
      isMounted: false // 组件是否已挂载
    };
  }

  componentDidMount() {
    console.log("执行");
    this.setState(
      {
        isMounted: true
      },
      () => {
        const { router } = this.props;
        if (!this.state.routerListened) {
          router.listen(location => {
            let model = location.query.model;
            console.log("执行");
            console.log(this.state.isMounted);
            console.log(model);
            if (
              location.pathname === "/DataSystemSegistration" &&
              model == 2 &&
              this.state.isMounted
            ) {
              console.log("执行请求");
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
    let activeKey = this.getActiveKey();

    const pager = this.state.pagination;
    const { query } = this.props.location;

    console.log(activeKey);

    if (
      activeKey === "3" ||
      activeKey === "4" ||
      activeKey === "5" ||
      activeKey === "7"
    ) {
      this.setState({
        loading: true
      });

      // 新增renter直接传入
      // edited by steven leo
      const { renterId } = this.props.account;
      queryPlatformList(
        {
          current: query.page || 1,
          pageSize: query.pageSize || pager.pageSize
        },
        {
          dsType: activeKey,
          sourceId: 2,
          renterId
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
            } else if (index.status === 2) {
              index.status = "已生效";
            }
          }
          rows.map((row, index) => {
            row.key = row.dsId;
            row.index = pager.pageSize * (pager.current - 1) + index + 1;
            return row;
          });

          this.setState({
            loading: false,
            data: rows,
            pagination: pager
          });
        }
      });
      let obj1 = {};
      if (activeKey === "3") {
        obj1.serverName = "MYSQL";
      } else if (activeKey === "4") {
        obj1.serverName = "HIVE";
      } else if (activeKey === "5") {
        obj1.serverName = "HBASE";
      }
      get_platform_url(obj1).then(res => {
        const info = res.data.data ? res.data.data.rows[0] : null;
        if (info) {
          this.setState({
            info: info
          });
        } else {
          this.setState({
            info: {}
          });
        }
      });
    } else if (activeKey === "6") {
      // get_platform_HDFStree(this.props.account.renterId).then((res)=>{
      //   const data = JSON.parse(res.data.data) || [];
      //   const expandedKeys = data && data[0] ? [data[0].value] : [];
      //   this.setState({
      //     gData:data,
      //     RootDirectory:data.length ? data[0].value : null,
      //     selectedKeys: [],
      //     // expandedKeys,
      //   });
      // })
    }
    setTimeout(() => {
      if (this.state.loading) {
        try {
          this.setState({ loading: false });
        } catch (err) {
          console.log(err);
        }
      }
    }, 4000);
  }

  componentWillUnmount() {
    this.state.isMounted = false;
  }

  //tab面板切换
  handleTabsChange(e) {
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

  //树型控件
  onDragEnter = info => {
    console.log(info);
  };

  onDrop = info => {
    console.log(info);
    const dropKey = info.node.props.eventKey;
    const dragKey = info.dragNode.props.eventKey;
    const dropPos = info.node.props.pos.split("-");
    const dropPosition =
      info.dropPosition - Number(dropPos[dropPos.length - 1]);
    // const dragNodesKeys = info.dragNodesKeys;
    const loop = (data, key, callback) => {
      data.forEach((item, index, arr) => {
        if (item.key === key) {
          return callback(item, index, arr);
        }
        if (item.children) {
          return loop(item.children, key, callback);
        }
      });
    };
    const data = [...this.state.gData];
    let dragObj;
    loop(data, dragKey, (item, index, arr) => {
      arr.splice(index, 1);
      dragObj = item;
    });
    if (info.dropToGap) {
      ``;
      let ar;
      let i;
      loop(data, dropKey, (item, index, arr) => {
        ar = arr;
        i = index;
      });
      if (dropPosition === -1) {
        if (ar) ar.splice(i, 0, dragObj);
      } else {
        if (ar) ar.splice(i - 1, 0, dragObj);
      }
    } else {
      loop(data, dropKey, item => {
        item.children = item.children || [];
        // where to insert 示例添加到尾部，可以是随意位置
        item.children.push(dragObj);
      });
    }
    this.setState({
      gData: data
    });
  };

  editTree(e) {
    e.preventDefault();
    console.log(this.state);
    if (this.state.selectedKeys.length > 0 || !this.state.RootDirectory) {
      if (
        e.currentTarget.value == "删除" &&
        this.state.selectedKeys[0] == String(this.state.RootDirectory)
      ) {
        message.warn("不能删除根节点");
      } else if (
        this.state.selectedTitle == "" &&
        (e.currentTarget.value === "重命名" || e.currentTarget.value === "删除")
      ) {
        message.warn("请选择一个节点");
      } else {
        const { dispatch } = this.props;
        dispatch({
          type: "addsubdirectories/show",
          visible: true,
          doWhat: e.currentTarget.value,
          selectedKeys: this.state.selectedKeys,
          selectedTitle: this.state.selectedTitle,
          treeData: this.state.gData
        });
        // this.state.selectedKeys.splice(0);
      }
    } else {
      message.info("请先选择节点");
    }
  }

  TreeonSelect(selectedKeys, selectedNodes) {
    this.setState({
      selectedKeys: selectedKeys,
      selectedTitle:
        selectedNodes.length > 0 ? selectedNodes[0].props.title : ""
    });
  }

  showModel() {
    const { dispatch } = this.props;
    dispatch({
      type: "tab1model/show",
      visible: true,
      model: "newmodel",
      dsType: this.getActiveKey(),
      info: {}
    });
  }

  columns = [
    {
      title: "数据库名称",
      dataIndex: "dbDatabasename",
      key: "dbDatabasename"
    } /*,{
    title: '所在资源',
    dataIndex: 'dbResource',
    key: 'dbResource'
  }*/,
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
      title: "状态",
      dataIndex: "status",
      key: "status"
    },
    /*{
   title: '用户名',
   dataIndex: 'dbUsername',
   key: 'dbUsername'
   },*/ {
      title: "备注",
      dataIndex: "remark",
      key: "remark",
      width: "35%",
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
        return (
          <div>
            <Empower
              api="/dataSourceInfoRegController/platform/update"
              disabled={!record.canEdited}
            >
              <a target="_blank">
                <Tooltip title="改">
                  <Icon
                    onClick={() => this.editModel(record)}
                    type="edit"
                    className="op-icon"
                  />
                </Tooltip>
              </a>
            </Empower>
            &nbsp;&nbsp;&nbsp;&nbsp;
            {/**
          <Empower api="/dataSourceInfoRegController/platform/updateStatus" disabled>
            <Popconfirm placement="topLeft" title="确认要删除该行吗？" onConfirm={()=>{this.confirm(record)}} >
              <a>
                <Tooltip title="删除" >
                  <Icon type="delete" className="op-icon"/>
                </Tooltip>
              </a>
            </Popconfirm>
          </Empower> */}
          </div>
        );
      }
    }
  ];

  cancel() {
    return false;
  }

  confirm(record) {
    record.status = 1;
    /*if(record==this.state.RootDirectory){
      message.warn('不能删除根目录');
    }*/
    delete_database_table_fields([record]).then(res => {
      if (res.data.msg == "Success") {
        message.success("删除成功");
      } else {
        message.success("删除失败");
      }
      this.Request();
    });
  }

  componentWillReceiveProps(nextProps) {
    const { clearSelect } = this.props.metadataCommon;
    if (clearSelect) {
      this.setState({
        selectedKeys: [],
        selectedTitle: ""
      });
    }
  }

  editModel(record) {
    for (let index of this.state.data) {
      if (index.dsId === record.dsId) {
        const { dispatch } = this.props;
        dispatch({
          type: "tab1model/show",
          visible: true,
          model: "editmodel",
          info: index,
          dsType: this.getActiveKey()
        });
      }
    }
  }

  getActiveKey() {
    const { query } = this.props.location;
    if (query.model == 2 && query.tabType) {
      return query.tabType + "";
    } else {
      return "3";
    }
  }

  showTable() {
    let activeKey = this.getActiveKey();

    if (activeKey === "3" || activeKey === "4" || activeKey === "5") {
      return (
        <div>
          <div className="margin_20_0" style={{marginTop: 0}}>
            <Empower api="/dataSourceInfoRegController/platform/insert">
              <Button
                type="primary"
                onClick={this.showModel.bind(this)}
              >
                新建
              </Button>
            </Empower>
          </div>
          <TableList
            showIndex
            onRowClick={record => {
              console.log(record);
            }}
            onChange={this.handleTableChange}
            pagination={this.state.pagination}
            loading={this.state.loading}
            columns={this.columns}
            dataSource={this.state.data}
          />
        </div>
      );
    }
  }

  onExpand = expandedKeys => {
    this.setState({
      expandedKeys
    });
  };

  render() {
    const { hdfsTree } = this.props.metadataCommon;
    const loop = data =>
      data.map(item => {
        if (item.children && item.children.length) {
          return (
            <TreeNode key={item.value} title={item.label}>
              {loop(item.children)}
            </TreeNode>
          );
        }
        return <TreeNode key={item.value} title={item.label} />;
      });

    return (
      <section style={{ backgroundColor: "#fff", padding: "0 20px"}}>
        <Tabs
          onChange={this.handleTabsChange.bind(this)}
          activeKey={this.getActiveKey()}
        >
          <TabPane tab="MYSQL" key="3">
            <Tab1 info={this.state.info} />
          </TabPane>
          <TabPane tab="HIVE" key="4">
            <Tab1 info={this.state.info} />
          </TabPane>
          <TabPane tab="HBASE" key="5">
            <Tab1 info={this.state.info} />
          </TabPane>
          <TabPane tab="HDFS" key="6">
            <div>

              {/* 
                修改逻辑为没有目录时可以创建，
                有目录时，需要选中才能创建
                edited by steven leo on 2018.11.12
              */}
              <Empower api="/hdfsPathController/insert">
                <Button
                  style={{ margin: "10px 0px" }}
                  disabled={ hdfsTree.length === 0 ? false : this.state.selectedTitle === "" }
                  type="primary"
                  value="新建子目录"
                  onClick={e => {
                    this.editTree(e);
                  }}
                >
                  新建子目录
                </Button>
              </Empower>

              {/**
                修改逻辑：判断是否选中否则不能新增
              */}
              <Empower api="/hdfsPathController/update">
                <Button
                  style={{ margin: "10px" }}
                  type="primary"
                  disabled={this.state.selectedTitle === ""}
                  value="重命名"
                  onClick={e => {
                    this.editTree(e);
                  }}
                >
                  重命名
                </Button>
              </Empower>

              {/**
                修改逻辑：判断是否选中否则不能新增
              */}
              <Empower api="/hdfsPathController/delete">
                <Button
                  style={{ margin: "10px" }}
                  type="primary"
                  disabled={this.state.selectedTitle === ""}
                  value="删除"
                  onClick={e => {
                    this.editTree(e);
                  }}
                >
                  删除
                </Button>
              </Empower>
            </div>
            {hdfsTree ? (
              <Tree
                draggable
                onDragEnter={this.onDragEnter}
                onDrop={this.onDrop}
                selectedKeys={this.state.selectedKeys}
                onSelect={(selectedKeys, e) => {
                  this.TreeonSelect(selectedKeys, e.selectedNodes);
                }}
              >
                {loop(hdfsTree)}
              </Tree>
            ) : (
              <Tree
                draggable
                onDragEnter={this.onDragEnter}
                onDrop={this.onDrop}
                selectedKeys={this.state.selectedKeys}
                onSelect={(selectedKeys, e) => {
                  this.TreeonSelect(selectedKeys, e.selectedNodes);
                }}
              >
                {loop(this.data.gData)}
              </Tree>
            )}
          </TabPane>
        </Tabs>
        {this.showTable()}
        <Tab1Model />
      </section>
    );
  }
}

export default withRouter(
  connect(({ account, metadataCommon }) => ({
    account,
    metadataCommon
  }))(DataSystemPlatform)
);
