/**组织机构管理*/
import React from "react";
import { connect } from "dva";
import {
  message,
  Table,
  Layout,
  Button,
  Popconfirm,
  Tooltip,
  Icon,
  Modal
} from "antd";
import Upload from "components/Upload";
import TableList from "../../../components/TableList";
import Search from "../../../components/Search";
import Empower from "../../../components/Empower";
import SliderBar from "./SliderBar";
import Ediror from "./Editor";
import OrgUsers from "./OrgUsers";
import { deepCopy, downloadFile } from "../../../utils/utils";
import {
  addOrganization,
  modifyOrganization,
  deleteOrganization,
  exportApi,
  importApi,
  addUserToOrg
} from "../../../services/securityOrganization";
import Style from "./style.css";
import { sureConfirm } from "utils/utils";

const { Column } = Table;
const { Sider, Content } = Layout;

class OrganizationManagementTable extends React.Component {
  state = {
    list: [],
    allList: [],
    loading: false,
    editorTitle: "新建组织机构",
    editorVisible: false,
    editorData: {},
    usersModalTitle: "",
    usersModalVisible: false,
    selectedRowKeys: []
  };

  componentDidMount() {
    const { dispatch, account } = this.props;
    this.reloadList();
  }

  componentWillReceiveProps(nextProps) {
    /**
     * 从organizationManage接收数据
     * organizationManage在model.js中定义
     */
    const { organizationManage } = nextProps;
    this.setState({ ...organizationManage, selectedRowKeys: [] });
  }

  handleSearch(keyword) {
    const { dispatch } = this.props;
    dispatch({
      type: "organizationManage/getList",
      payload: { keyword }
    });
  }

  // 新建
  handleNewContent() {
    this.setState({
      editorTitle: "新建组织机构",
      editorVisible: true,
      editorData: {}
    });
  }

  // 修改
  handleModify = (e, record) => {
    e.preventDefault();
    this.setState({
      editorTitle: "修改组织机构",
      editorVisible: true,
      editorData: record
    });
  };

  // 管理成员
  handleModifyMember = (e, record) => {
    e.preventDefault();
    this.setState({
      usersModalTitle: `组织机构【${record.deptName}】的用户`,
      usersModalVisible: true,
      editorData: record
    });
  };

  // 删除
  handleDelete = async record => {
    const hasChildren = this.state.allList.some(
      item => item.parentId === record.id
    );
    if (hasChildren) {
      message.warn("该组织机构含有子组织，无法被删除");
      return;
    }
    const formData = {
      // _method: 'put',
      ids: record.id
    };
    const { data } = await deleteOrganization(formData);
    if (data.code === "200") {
      message.success("删除成功");
      this.reloadList();
    }
  };

  // 选择行
  handleSelectChange = selectedRowKeys => {
    this.setState({ selectedRowKeys });
  };

  // 导出
  handleExport = () => {
    const ids = this.state.selectedRowKeys.join(",");
    downloadFile(exportApi, "GET", { ids });
  };

  // 提交表单
  submitEditor = async values => {
    const { account } = this.props;
    const { parentId } = this.props.organizationManage;
    const formData = {
      renterId: account.renterId,
      renterName: account.username,
      isActive: true,
      ...values
    };
    if (parentId && parentId !== "null") formData.parentId = parentId;
    if (!values.id) {
      // 新增
      const { data } = await addOrganization(formData);
      if (data.code === "200") {
        message.success("新增成功");
        this.reloadList();
        this.setState({ editorVisible: false });
      }
    } else {
      // 修改:没有修改内容点击确定应该作判断报错
      formData.id = values.id;
      /*formData['_method'] = 'put';*/
      if (values.parentId) formData.parentId = values.parentId;
      const { data } = await modifyOrganization(formData);
      if (data.code === "200") {
        message.success("修改成功");
        this.reloadList();
        this.setState({ editorVisible: false });
      }
    }
  };

  // 提交用户
  submitUsers = async (orgId, uIds) => {
    const { data } = await addUserToOrg({ orgId, uIds: uIds.join(",") });
    if (data.code === "200") {
      this.reloadList();
      this.setState({ usersModalVisible: false });
    }
  };

  // 刷新列表
  reloadList() {
    const {
      location: { query },
      dispatch,
      account
    } = this.props;
    dispatch({
      type: "organizationManage/getAllList",
      payload: {
        renterId: account.renterId,
        pageSize: 10000000
      }
    });
    // dispatch({
    //   type: 'securityCommon/getOrgList',
    //   force: true,
    // });
  }

  // 导入状态跟踪
  handleImportStatusChange(res) {
    const {
      file: { response },
      event
    } = res;
    // 上传进度100
    if (event && event.percent === 100) {
      // console.log('上传完成');
    }
    // 服务器返回信息
    if (response) {
      if (response.code === "200") {
        message.success("导入成功");
        this.reloadList();
      } else {
        Modal.warning({
          title: response.msg,
          content: response.data
            ? response.data.join(";")
            : "文件格式或内容不正确"
        });
      }
    }
  }

  // 下载模板
  downloadExcel() {
    downloadFile("files/excel-template/组织机构导入.xlsx");
  }

  //批量删除
  batchDelete() {
    const { dispatch } = this.props;
    const { selectedRowKeys } = this.state;
    if (selectedRowKeys.length === 0) return;

    sureConfirm(
      {
        title: "确定批量删除页面所有组织吗？"
      },
      bool => {
        if (bool) {
          dispatch({
            type: "organizationManage/batchDelete",
            payload: selectedRowKeys
          });
        }
      }
    );
  }

  render() {
    const data = this.state;
    const { account } = this.props;
    const allList = deepCopy(this.state.allList);
    return (
      <Layout>
        {/*侧边菜单：组织机构目录*/}
        <Sider className={Style["sider-wrap"]}>
          <SliderBar data={allList} />
        </Sider>
        {/*搜索*/}
        <Content
          style={{ marginLeft: 10, padding: 10, backgroundColor: "#fff" }}
        >
          <Search
            placeholder="可以按组织机构名称、组织机构代码进行模糊搜索"
            style={{ width: 400 }}
            onSearch={this.handleSearch.bind(this)}
          />
          {/*<h3 style={{margin:10}}>机构A</h3>*/}
          <div style={{ marginTop: 10 }}>
            {/*新建*/}
            <Empower api="/organization/add.shtml">
              <Button onClick={this.handleNewContent.bind(this)} type="primary">
                新建
              </Button>
            </Empower>

            {/*导出*/}
            <Empower api="/organization/export.shtml">
              <Button
                style={{ marginLeft: 10 }}
                type="primary"
                disabled={data.selectedRowKeys.length > 0 ? false : true}
                onClick={this.handleExport}
              >
                导出
              </Button>
            </Empower>

            {/*下载模板*/}
            <Empower api="/organization/import.shtml">
              <Button
                style={{ marginLeft: 10 }}
                onClick={this.downloadExcel.bind(this)}
                type="primary"
              >
                下载模板
              </Button>
            </Empower>

            {/*批量导入*/}
            <Empower api="/organization/import.shtml">
              <Upload
                action={importApi}
                showUploadList={false}
                data={{
                  renterId: account.renterId
                }}
                accept="application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                beforeUpload={() => message.loading("正在导入，请稍候...")}
                onChange={this.handleImportStatusChange.bind(this)}
              >
                <Button style={{ marginLeft: 10 }} type="primary">
                  批量导入
                </Button>
              </Upload>
            </Empower>

            {/*下载模板*/}
            <Empower api="/organization/import.shtml">
              <Button
                style={{ marginLeft: 10 }}
                disabled={!(data.selectedRowKeys.length > 0)}
                onClick={this.batchDelete.bind(this)}
                type="primary"
              >
                批量删除
              </Button>
            </Empower>
          </div>

          <TableList
            style={{ marginTop: 10 }}
            className="th-nowrap"
            rowKey="id"
            showIndex
            rowSelection={{
              selectedRowKeys: this.state.selectedRowKeys,
              onChange: this.handleSelectChange
            }}
            dataSource={data.list}
            loading={data.loading}
            pagination={false}
          >
            <Column title="序号" width={80} dataIndex="__index" />
            <Column
              title="组织机构名称"
              dataIndex="deptName"
              render={(text, record) => (
                <span title={record.remark}>{text}</span>
              )}
            />
            <Column title="组织机构代码" dataIndex="deptCode" />
            <Column title="统一社会信用代码" dataIndex="unifiedCreditCode" />
            {/*<Column title="租户名称" dataIndex="renterName" />*/}
            {/*<Column title="状态" render={(text, record) => record.isActive ? '有效' : '无效'} />*/}
            <Column
              title="操作"
              className="td-nowrap"
              render={(text, record) => {
                return record.isActive ? (
                  <div>
                    <Empower api="/organization/update.shtml">
                      <Tooltip title="用户">
                        <a
                          href="#"
                          style={{ marginRight: 10 }}
                          onClick={e => this.handleModifyMember(e, record)}
                        >
                          <Icon type="user" className="op-icon" />
                        </a>
                      </Tooltip>
                    </Empower>
                    <Empower api="/organization/update.shtml">
                      <Tooltip title="修改">
                        <a
                          href="#"
                          style={{ marginRight: 10 }}
                          onClick={e => this.handleModify(e, record)}
                        >
                          <Icon type="edit" className="op-icon" />
                        </a>
                      </Tooltip>
                    </Empower>
                    <Empower api="/organization/delete.shtml">
                      <Tooltip title="删除">
                        <Popconfirm
                          placement="topLeft"
                          title="确认要删除该行吗？"
                          onConfirm={() => this.handleDelete(record)}
                        >
                          <a>
                            <Icon type="delete" className="op-icon" />
                          </a>
                        </Popconfirm>
                      </Tooltip>
                    </Empower>
                  </div>
                ) : null;
              }}
            />
          </TableList>
        </Content>
        {/*新建与修改对话框：*/}
        <Ediror
          title={this.state.editorTitle}
          visible={this.state.editorVisible}
          onCancel={() => this.setState({ editorVisible: false })}
          onOk={this.submitEditor}
          selectData={allList}
          data={this.state.editorData}
        />
        {/*组织用户成员*/}
        <OrgUsers
          title={this.state.usersModalTitle}
          visible={this.state.usersModalVisible}
          onCancel={() => this.setState({ usersModalVisible: false })}
          onOk={this.submitUsers}
          data={this.state.editorData}
        />
      </Layout>
    );
  }
}
export default connect(({ account, organizationManage }) => ({
  account,
  organizationManage
}))(OrganizationManagementTable);
