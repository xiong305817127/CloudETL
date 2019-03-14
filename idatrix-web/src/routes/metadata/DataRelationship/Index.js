/**
 * Created by Administrator on 2017/8/26.
 */
import TableList from "../../../components/TableList";
import React from "react";
import {
  Icon,
  TreeSelect,
  Input,
  Button,
  Table,
  Tooltip,
  Popconfirm,
  message
} from "antd";
import Style from "./style.css";
import {
  getDepartmentTree,
  SJGXGLsearch,
  SJGXGLGLZDshanchu,
  SJGXGdeleteById
} from "../../../services/metadata";
import { connect } from "dva";
import { withRouter } from "react-router";
import { getLabelByTreeValue } from "utils/metadataTools";
import Search from "../../../components/Search";
import { convertArrayToTree } from "../../../utils/utils";
import Empower from "../../../components/Empower"; // 导入授权组件
/*import newModel from'./components/Model/newModelIndex.js';*/

class DataRelationShip extends React.Component {
  state = {
    pagination: {
      current: 1,
      pageSize: 10
    },
    data: [],
    loading: false,
    //选择部门
    options: [],
    text: "",
    textBuMenAnNiu: "",
    routerListened: false, // 是否已监听路由
    isMounted: false // 组件是否已挂载
  };
  //选择部门级联
  handleChangeDept = value => {
    this.setState(
      {
        text: value,
        textBuMenAnNiu: "取消选择"
      },
      () => {
        this.Request();
      }
    );
  };

  //更新表格
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
            if (
              location.pathname === "/DataStandardView" &&
              this.state.isMounted
            ) {
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

  componentWillUnmount() {
    const { dispatch } = this.props;
    this.setState({
      options: [],
      text: "",
      textBuMenAnNiu: "",
      isMounted: false
    });
    const location = this.props.location;
    delete location.query.keyword;
    this.Request();
  }

  SJGXGLtankuang() {
    console.log(this.props, "this.props");
    const { dispatch } = this.props;
    const { id, renterId } = this.props.account;
    dispatch({
      type: "drmselecttable/showSelectTable",
      payload: {
        obj: {
          metaType: "",
          dept: "",
          metaNameCn: "",
          renterId: renterId,
          sourceId: 2,
          id: id,
          status: 1
        },
        paper: {
          pageSize: 10,
          current: 1
        }
      }
    });

    dispatch({
      type: "drmnewfilemodel/changeModel",
      payload: {
        visible: true,
        actionType: "new",
        updataTable: this.Request.bind(this)
      }
    });
  }

  componentWillReceiveProps() {
    defaultChecked: this.props.selectedRowKeysLeft &&
      this.props.selectedRowKeysRight === null;
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: "metadataCommon/getDepartments" });
    dispatch({ type: "metadataCommon/getAllResource" });
    this.Request();
  }

  componentWillReceiveProps(nextProps) {
    this.Request();
  }

  Request() {
    this.setState({
      loading: true
    });
    const { query } = this.props.location;
    const pager = this.state.pagination;
    const { id, renterId } = this.props.account;

    let obj = {
      dept: this.state.text,
      renterId: renterId,
      keyword: query.keyword ? decodeURIComponent(query.keyword) : ""
    };

    SJGXGLsearch(obj, {
      current: query.page || 1,
      pageSize: query.pageSize || pager.pageSize
    }).then(res => {
      if (res.data && res.data.data) {
        const { total, rows } = res.data.data;
        pager.total = total;
        rows.map((row, index) => {
          row.key = row.id;
          row.index = pager.pageSize * (pager.current - 1) + index + 1;
          return row;
        });

        console.log(pager);
        this.setState({
          data: rows,
          pagination: pager,
          loading: false
        });
      }
    });
  }

  Search(e) {
    console.log(e);
    const location = this.props.location;
    if (e && e.trim()) {
      location.query.keyword = encodeURIComponent(e);
    } else {
      delete location.query.keyword;
    }
    this.props.router.push(location);
  }

  handleEdit(record) {
    const { dispatch } = this.props;
    console.log(record.id, "record");
    dispatch({
      type: "drmnewfilemodel/queryTableAndFiledById",
      payload: {
        visible: true,
        //id:record ? record.id : record,
        id: record.id,
        actionType: "edit"
        //  updataTable:this.Request.bind(this)
      }
    });
  }

  columns = [
    {
      title: "关联性质",
      dataIndex: "rsType",
      key: "rsType",
      render: (text, record) => {
        return <span>{record.rsType === 1 ? "引用关系" : "生成关系"}</span>;
      }
    },
    {
      title: "数据表中文名称",
      dataIndex: "tableName",
      key: "tableName"
    },
    {
      title: "数据表英文名称",
      dataIndex: "tableNameEn",
      key: "tableNameEn"
    },
    {
      title: "所属组织",
      dataIndex: "dept",
      key: "dept",
      render: text => {
        const { departmentsOptions } = this.props.metadataCommon;
        return getLabelByTreeValue(text, departmentsOptions);
      }
    },
    {
      title: "拥有者",
      dataIndex: "owner",
      key: "owner"
    },
    {
      title: "关联表中文名称",
      dataIndex: "childTable",
      key: "childTable",
      width: "10%"
    },
    {
      title: "关联表英文名称",
      dataIndex: "childMetaCode",
      key: "childMetaCode",
      width: "10%"
    },
    { title: "拥有者", dataIndex: "childOwner", key: "childOwner" },
    { title: "关联关系建立人", dataIndex: "creator", key: "creator" },
    { title: "建立关联时间", dataIndex: "createTime", key: "createTime" },
    {
      title: "操作",
      key: "x12",
      render: (text, record) => {
        return (
          <div>
            <Empower api="/MetaRelationshipController/tableAndFiledRelation">
              <a
                onClick={() => {
                  this.handleEdit(record);
                }}
              >
                <Tooltip title="编辑">
                  <Icon type="edit" className="op-icon" />
                  &nbsp;&nbsp;&nbsp;&nbsp;
                </Tooltip>
              </a>
            </Empower>
            {/*   <a>
              <Tooltip title="关系图" >
                <Icon type="qrcode" className="op-icon"/>
              </Tooltip>
            </a>*/}
            &nbsp;&nbsp;&nbsp;
            <Empower
              api="/MetaRelationshipController/deleteById"
              disabled={record.canEdited === false}
            >
              <Popconfirm
                placement="topLeft"
                title="确认要删除该行吗？"
                onConfirm={() => {
                  this.handleDelete(record);
                }}
              >
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

  handleDelete(record) {
    let ids = {};
    ids = record.id;
    console.log(record, "record111222333", ids);
    SJGXGdeleteById(ids).then(res => {
      console.log(res, "res");
      const { code } = res.data;
      if (code === "200") {
        this.Request();
        message.success("删除成功");
        this.Request();
      }
    });
  }

  render() {
    const { query } = this.props.location;
    const pagination = this.state.pagination;
    const { departmentsTree } = this.props.metadataCommon;
    const { drmnewfilemodel, drmselecttable } = this.props;
    return (
      <div className="padding_20">
        {/*搜索*/}

        <div>
          <Search
            placeholder="可以按关联表中文名、英文名进行模糊搜索"
            onSearch={e => {
              this.Search(e);
            }}
            defaultValue={
              query.keyword ? decodeURIComponent(query.keyword) : ""
            }
          />
          <span className={Style.xuanzebumen}>
            <TreeSelect
              placeholder="请选择组织"
              treeData={departmentsTree}
              onChange={value => {
                this.handleChangeDept(value);
              }}
              treeDefaultExpandAll
              style={{ width: 200 }}
              allowClear
            />
          </span>
        </div>
        {/*表格和按钮*/}
        <div>
          <Empower api="/MetaRelationshipController/createRelation">
            <Button
              type="primary"
              onClick={this.SJGXGLtankuang.bind(this)}
              className="margin_20_0"
              style={{marginBottom: 0}}
            >
              新建
            </Button>
          </Empower>

          <Empower api="/MetaRelationshipController/search">
            <TableList
              showIndex
              onRowClick={() => {
                return false;
              }}
              style={{ marginTop: 20 }}
              columns={this.columns}
              pagination={pagination}
              loading={this.state.loading}
              dataSource={this.state.data}
              className="th-nowrap "
            />
          </Empower>
        </div>
      </div>
    );
  }
}

export default withRouter(
  connect(({ account, metadataCommon }) => ({
    account,
    metadataCommon
  }))(DataRelationShip)
);
