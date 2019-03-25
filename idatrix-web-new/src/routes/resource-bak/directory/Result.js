import React from "react";
import { connect } from "dva";
import {
  Input,
  Button,
  Row,
  Col,
  Tag,
  Radio,
  message,
  Tooltip,
  Icon,
  TreeSelect
} from "antd";
import { withRouter, Link } from "react-router";
import { applyPermits } from "services/dataResource";
import Empower from "components/Empower";
import Search from "components/Search";
import { getLabelByTreeValue } from "utils/metadataTools";
import TableList from "../../../components/TableList";
import ViewTable from "../components/ViewTable";
import DataMap from "../components/NewDataMap";
import Application from "./Application";
import Style from "./style.less";

class Result extends React.Component {
  state = {
    result: {},
    resultType: "table",
    viewTable: [],
    viewTableVisible: false,
    viewDataMapVisible: false,
    viewDataMapId: -1,
    selectedType: "table",
    applicationVisible: false,
    selectedRowKeys: [],
    selectedRows: [],
    permitsList: [],
    current1: 0,
    current2: 0,
    tableColumns: [
      {
        title: "数据表中文名称",
        dataIndex: "metaNameCn",
        width: 160,
        render: (text, record) => (
          <a href="#" onClick={e => this.handleView(record, e)}>
            {text}
          </a>
        )
      },
      {
        title: "数据表英文名称",
        dataIndex: "metaNameEn",
        width: 160
      },
      {
        title: "所属组织",
        dataIndex: "deptName",
        dataIndex: "dept",
        render: text => {
          const { allDepartmentsOptions } = this.props.resourcesCommonChange;
          return getLabelByTreeValue(text, allDepartmentsOptions) || "";
        }
      },
      {
        title: "行业",
        dataIndex: "industry",
        render: text => {
          const { industryOptions } = this.props.resourcesCommonChange;
          return getLabelByTreeValue(text, industryOptions) || text;
        }
        // }, {
        //   title: '主题',
        //   dataIndex: 'theme',
        //   render: (text) => {
        //     const { themeOptions } = this.props.resourcesCommonChange;
        //     return getLabelByTreeValue(text, themeOptions) || text;
        //   },
      },
      {
        title: "标签",
        dataIndex: "tag",
        render: text => {
          const { tagsOptions } = this.props.resourcesCommonChange;
          return getLabelByTreeValue(text, tagsOptions) || text;
        }
      },
      {
        title: "备注",
        dataIndex: "remark",
        render: text =>
          text && text !== "null" ? (
            <div className="word25" title={text}>
              {text}
            </div>
          ) : null
      },
      {
        title: "操作",
        width: 10,
        className: "td-center",
        render: (text, record) => (
          <Tooltip title="数据地图">
            <a
              style={{ fontSize: 16 }}
              onClick={e => this.handleViewDataMap(record, e)}
            >
              <Icon type="global" />
            </a>
          </Tooltip>
        )
      }
    ],
    fileColumns: [
      {
        title: "文件目录名称",
        dataIndex: "dirName",
        width: "200px"
      },
      {
        title: "文件存在目录",
        dataIndex: "storDir"
        /*render: (text) => {
          const { hdfsPlanList } = this.props.resourcesCommonChange;
          let result = [];
          try {
            const arr = typeof text === 'string' ? JSON.parse(text) : text;
            arr.forEach(id => {
              const found = hdfsPlanList.find(it => it.value == id);
              if (found) result.push(found.label);
            });
          } catch (err) {}
          return result.join('/');
        },*/
      },
      {
        title: "所属组织",
        dataIndex: "dept",
        render: text => {
          const { allDepartmentsOptions } = this.props.resourcesCommonChange;
          return getLabelByTreeValue(text, allDepartmentsOptions) || "";
        }
      },
      {
        title: "行业",
        dataIndex: "industry",
        render: text => {
          const { industryOptions } = this.props.resourcesCommonChange;
          return getLabelByTreeValue(text, industryOptions) || text;
        }
        // }, {
        //   title: '主题',
        //   dataIndex: 'theme',
        //   render: (text) => {
        //     const { themeOptions } = this.props.resourcesCommonChange;
        //     return getLabelByTreeValue(text, themeOptions) || text;
        //   },
      },
      {
        title: "标签",
        dataIndex: "tag",
        render: text => {
          const { tagsOptions } = this.props.resourcesCommonChange;
          return getLabelByTreeValue(text, tagsOptions) || text;
        }
      },
      {
        title: "备注",
        dataIndex: "remark",
        render: text =>
          text && text !== "null" ? (
            <div className="word25" title={text}>
              {text}
            </div>
          ) : null
      }
    ]
  };

  componentDidMount() {
    const { dispatch } = this.props;
    this.mergeResult(this.props);
    dispatch({ type: "resourcesCommonChange/getDepartments" });
    dispatch({ type: "resourcesCommonChange/findOrgnazation" });
    dispatch({ type: "resourcesCommonChange/getAllResource" });
    dispatch({ type: "resourcesCommonChange/getHdfsTree" });
  }

  componentWillReceiveProps(nextProps) {
    this.mergeResult(nextProps);
  }

  // 合并状态
  mergeResult(props) {
    const {
      dataResource: {
        result,
        resultType,
        viewTable,
        permitsList,
        permitsResults
      }
    } = props;
    this.setState({
      result,
      resultType,
      viewTable,
      permitsList,
      permitsResults
    });
  }

  // 全选操作
  onChangeAllSelect(selectedRowKeys, selectedRows) {
    this.setState({ selectedRowKeys, selectedRows });
  }

  // 查看详情
  handleView(record, e) {
    const { dispatch } = this.props;
    if (e) e.preventDefault();
    this.setState({
      viewTableVisible: true,
      viewTableName: record.metaName
    });
    dispatch({
      type: "dataResource/getMeta",
      payload: { metaid: record.metaid }
    });
  }

  // 切换类型
  handleChangeListType(e) {
    const { location, router } = this.props;
    const type = e.target.value;
    const query = { ...location.query };
    router.push({
      ...location,
      query: { ...query, page: 1, type }
    });
    this.setState({
      selectedRowKeys: [],
      selectedRows: [],
      selectedType: type
    });
  }

  // 查看数据地图
  handleViewDataMap = (record, e) => {
    e.preventDefault();
    this.setState(
      {
        viewDataMapId: record.metaid
      },
      () => {
        this.setState({
          viewDataMapVisible: true
        });
      }
    );
  };

  // 打开申请权限窗口
  handleApplicationShow() {
    const { dispatch } = this.props;
    this.setState({ applicationVisible: true });
  }

  // 提交权限申请
  async handleSubmitApplication(permits) {
    const formData = [];
    this.state.selectedRows.forEach(row => {
      // (permits || []).forEach(item => {
      formData.push({
        userId: this.props.account.id,
        auditusr: row.owner,
        resourceId: row.metaid || row.fileid,
        // type: this.state.resultType === 'table' ? 1 : 2,
        resourceType: this.state.resultType === "table" ? 1 : 2,
        resourcename:
          this.state.resultType === "table" ? row.metaNameCn : row.dirName,
        resourcedept: row.dept,
        creator: this.props.account.username,
        authValue: permits,
        reason: ""
      });
      // });
    });
    const {
      data: { code, msg }
    } = await applyPermits(formData);
    this.setState({
      applicationVisible: false,
      selectedRowKeys: [],
      selectedRows: []
    });
    if (code == "200") {
      message.success("申请权限成功");
      this.handleClickSearch();
    }
  }

  // 判断申请权限按钮是否禁用
  isDisabledOfApplyButton() {
    const { selectedRows, selectedRowKeys } = this.state;
    if (selectedRowKeys.length === 0) return true;
    return selectedRows.some(
      item => item.publicStats === "0" || item.isremove === 4
    );
  }

  // 点击搜索时
  handleClickSearch(keyword, dept) {
    if (dept === "") {
      const { router, location } = this.props;
      const query = {
        ...location.query,
        keyword,
        dept: ""
      };
      router.push({ ...location, query });
    } else {
      const { router, location } = this.props;
      const query = {
        ...location.query,
        keyword,
        dept: dept || location.query.dept
      };
      router.push({ ...location, query });
    }
  }

  // 选择组织
  handleChangeDept(dept) {
    if (dept === undefined) {
      dept = "";
      const {
        query: { keyword }
      } = this.props.location;
      this.handleClickSearch(keyword, dept);
    } else {
      const {
        query: { keyword }
      } = this.props.location;
      this.handleClickSearch(keyword, dept);
    }
  }

  render() {
    const {
      result,
      resultType,
      tableColumns,
      fileColumns,
      viewTable,
      selectedRowKeys,
      permitsResults
    } = this.state;
    const {
      router,
      resourcesCommonChange: { allDepartmentsTree }
    } = this.props;

    return (
      <div style={{ backgroundColor: "#fff", padding: 10 }}>
        <header className={Style["head-wrap"]}>
          <Search
            onSearch={this.handleClickSearch.bind(this)}
            placeholder="可以按数据所属组织、行业、标签进行模糊搜索"
          />
          <TreeSelect
            placeholder="请选择组织"
            treeData={allDepartmentsTree}
            onChange={value => {
              this.handleChangeDept(value);
            }}
            allowClear
            treeDefaultExpandAll
            style={{ width: 200 }}
            allowClear
          />
        </header>

        <section className={Style["tags-wrap"]}>
          <Radio.Group
            style={{ marginLeft: "20px", marginTop: "20px" }}
            onChange={this.handleChangeListType.bind(this)}
            value={this.state.resultType}
          >
            <Radio value="table">数据表类</Radio>
            <Radio value="file">文件目录类</Radio>
          </Radio.Group>

          <div style={{ margin: 20 }}>
            <Button
              onClick={() => router.push({ pathname: "/resources/directory" })}
            >
              返回
            </Button>
            <Empower api="/myResourceController/batchInsert">
              <Button
                disabled={this.isDisabledOfApplyButton()}
                onClick={this.handleApplicationShow.bind(this)}
                style={{ marginLeft: 10 }}
              >
                申请权限
              </Button>
            </Empower>
          </div>

          {/* table组件的一个漏洞，即：不可以外界传入current进行控制，导致页码混乱 */}
          {/* 故此处使用两个table分别记录pagnition，需后续优化*/}
          {this.state.selectedType === "table" && (
            <TableList
              showIndex
              rowKey={resultType === "table" ? "metaid" : "fileid"}
              columns={resultType === "table" ? tableColumns : fileColumns}
              dataSource={result.rows}
              rowSelection={{
                onChange: this.onChangeAllSelect.bind(this),
                selectedRowKeys,
                getCheckboxProps: record => {
                  const { account } = this.props;
                  return {
                    disabled: record.owner === account.username
                  };
                }
              }}
              pagination={{
                total: result.total
              }}
              style={{ margin: "20px" }}
            />
          )}
          {this.state.selectedType === "file" && (
            <TableList
              showIndex
              rowKey={resultType === "table" ? "metaid" : "fileid"}
              columns={resultType === "table" ? tableColumns : fileColumns}
              dataSource={result.rows}
              rowSelection={{
                onChange: this.onChangeAllSelect.bind(this),
                selectedRowKeys,
                getCheckboxProps: record => {
                  const { account } = this.props;
                  return {
                    disabled: record.owner === account.username
                  };
                }
              }}
              pagination={{
                total: result.total
              }}
              style={{ margin: "20px" }}
            />
          )}
        </section>

        <ViewTable
          visible={this.state.viewTableVisible}
          tableName={this.state.viewTableName}
          data={viewTable}
          onClose={() => this.setState({ viewTableVisible: false })}
        />
        <DataMap
          visible={this.state.viewDataMapVisible}
          id={this.state.viewDataMapId}
          onClose={() => this.setState({ viewDataMapVisible: false })}
        />

        <Application
          data={{ permitsResults }}
          visible={this.state.applicationVisible}
          type={this.state.resultType}
          onSubmit={this.handleSubmitApplication.bind(this)}
          onCancel={() => this.setState({ applicationVisible: false })}
        />
      </div>
    );
  }
}

export default connect(
  ({ system, account, dataResource, resourcesCommonChange }) => ({
    system,
    account,
    dataResource,
    resourcesCommonChange
  })
)(withRouter(Result));
