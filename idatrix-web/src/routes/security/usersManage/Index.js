/**用户管理2.0版本*/
//一、环境：
import React from "react"; //核心
import { connect } from "dva"; //传参
import MD5 from "md5"; //数字加密
import Upload from "../../../components/Upload"; //自定义组件：上传模板文件
import Empower from "../../../components/Empower"; // 自定义组件：授权
import TableList from "../../../components/TableList"; // 自定义组件：表格
import Search from "../../../components/Search"; // 自定义组件：搜索
import Modal from "../../../components/Modal"; // 自定义组件：对话框
import OrgAndRole from "./OrgAndRole"; // // 自定义组件：分配用户到--组织或角色
import { API_BASE_SECURITY } from "./../../../constants"; //服务API路径
import { deepCopy, downloadFile } from "../../../utils/utils"; //下载模板文件：API接口
import {
  getList,
  newUser,
  updateUser,
  deleteUser
} from "../../../services/usersManage"; //其他API接口:增删改查
import {
  Form,
  Input,
  Button,
  Popconfirm,
  Icon,
  Tooltip,
  message,
  Select
} from "antd"; //组件：标签
// import { strEnc, strDec } from "utils/EncryptUtil";
import md5 from "md5";

const FormItem = Form.Item; //组件：标签套件
const Option = Select.Option;

let tempPassword = "";

class UserManagementTable extends React.Component {
  //1.初始化页面：
  constructor(props) {
    super(props);
    this.state = {
      //列表参数、数据
      pageNo: 1,
      pageSize: 10,
      totalCount: 0,
      dataSource: [],
      //搜索内容
      findContent: "",
      //对话框弹框
      newVisible: false,
      editVisible: false,
      userVisible: false, //分配用户
      roleVisible: false, //分配角色
      orgAndRoleVisible: false,
      orgAndRoleTitle: "",
      //修改内容：修改
      username: null,
      realName: null,
      pswd: null,
      sex: null,
      age: null,
      email: null,
      phone: null,
      cardId: null,
      //勾选：导出、修改、删除当前行
      selectedRowKeys: [],
      ids: [],
      editId: "",
      //穿梭框：内容、传输
      targetKeys: null,
      mockUserData: null,
      mockRoleData: null,
      //下载：
      fileName: "", //上传名称
      filePath: "", //上传路径
      fileLen: "", //上传字节大小
      //密码校正：
      confirmDirty: false
      //新增错误：用户名存在
      // resultError: '',
    };
    //自定义标题、模拟数据
    this.sexOptions = { "1": "男", "2": "女", "3": "" };
    this.columns = [
      {
        title: "用户账号",
        dataIndex: "username"
      },
      {
        title: "真实姓名",
        dataIndex: "realName"
      },
      {
        title: "性别",
        dataIndex: "sex",
        render: (text, record, index) => this.sexOptions[record.sex]
      },
      {
        title: "年龄",
        dataIndex: "age"
      },
      {
        title: "身份证号码",
        dataIndex: "cardId",
        className: "td-nowrap"
      },
      {
        title: "手机",
        dataIndex: "phone",
        className: "td-nowrap"
      },
      {
        title: "邮箱",
        dataIndex: "email",
        className: "td-nowrap"
      },
      {
        title: "所属角色",
        dataIndex: "roleNames"
      },
      {
        title: "所属组织机构",
        dataIndex: "deptId",
        render: text => {
          const { organizationOptions } = this.props.securityCommon;
          const found = organizationOptions.find(
            org => text && org.value == text
          );
          return found ? found.label : "";
        }
      },
      {
        title: "操作",
        className: "td-nowrap",
        render: (text, record, index) => (
          <span>
            {/*<Empower api="/organization/addUserToOrg.shtml">
             <a onClick={()=>this.showOrgModal(record)} style={{marginRight:10}}>
             <Tooltip title="分配组织" ><Icon type="global" className="op-icon"/></Tooltip>
             </a>
             </Empower>*/}
            <Empower api="/role/addUsersToRoles.shtml">
              <a
                onClick={() => this.showOrgAndRoleModal(record)}
                style={{ marginRight: 10 }}
              >
                <Tooltip title="组织或角色">
                  <Icon type="team" className="op-icon" />
                </Tooltip>
              </a>
            </Empower>
            <Empower api="/member/update.shtml">
              <a
                onClick={() => this.showEditModal(record, index)}
                style={{ marginRight: 10 }}
              >
                <Tooltip title="修改">
                  <Icon type="edit" className="op-icon" />
                </Tooltip>
              </a>
            </Empower>
            <Empower api="/member/delete.shtml">
              <a>
                <Popconfirm
                  title="你确定要删除吗？"
                  okText="确定"
                  cancelText="取消"
                  onConfirm={() => this.onDelete(record, index)}
                >
                  <Tooltip title="删除">
                    <Icon type="delete" className="op-icon" />
                  </Tooltip>
                </Popconfirm>
              </a>
            </Empower>
          </span>
        )
      }
    ];
  }
  //预加载表格数据：getlist
  componentWillMount() {
    //this.props（方法，参数）
    const { dispatch, account } = this.props;
    dispatch({ type: "securityCommon/getOrgList" });
    this.reloadList();
  }
  //刷新页面:自定义传参,刷新表格后，清空搜索框、全选操作
  reloadList = page => {
    const obj = {};
    obj.pageNo = page ? page : this.state.pageNo; //默认为第1页
    obj.pageSize = this.state.pageSize; //默认每页为10条
    obj.findContent = this.state.findContent; //搜索值
    getList(obj).then(res => {
      if (res && res.data) {
        this.setState({
          dataSource: res.data.data.list,
          totalCount: res.data.data.totalCount,
          findContent: null,
          selectedRowKeys: [],
          ids: []
        });
      }
    });
  };
  //2.搜索:
  handleSearch(findContent) {
    this.state.findContent = findContent;
    this.reloadList(1);
  }
  //3.新建对话框，对话框务必清空表单
  handleNewContent() {
    this.props.form.resetFields(); //无效
    this.setState({
      newVisible: true, //对话框
      username: null,
      realName: null,
      pswd: null,
      sex: null,
      age: null,
      email: null,
      phone: null,
      cardId: null
    });
  }
  //新建提交：
  handleSubmit = e => {
    e.preventDefault();
    this.props.form.validateFields({ force: true }, (err, values) => {
      if (!err) {
        const obj = {};
        obj.username = values.username;
        obj.realName = values.realName;
        obj.sex = values.sex === "男" ? 1 : values.sex === "女" ? 2 : 3;
        obj.age = values.age;
        obj.email = values.email;
        obj.cardId = values.cardId;
        obj.phone = values.phone;
        obj.pswd = MD5(`#${values.pswd}`); //需要加密后传输时使用

        newUser(obj).then(res => {
          if (res.data && res.data.code === "200") {
            tempPassword = "";
            message.success(res.data.msg || res.resultMsg);
            this.setState({ newVisible: false });
            this.reloadList();
          } else {
            //错误提示：自定义
            if (res.data.resultMsg.indexOf("用户") > -1) {
              this.props.form.setFields({
                username: {
                  value: values.username,
                  errors: [new Error(res.data.msg || res.data.resultMsg)]
                }
              });
            } else if (res.data.resultMsg.indexOf("邮箱") > -1) {
              this.props.form.setFields({
                email: {
                  value: values.email,
                  errors: [new Error(res.data.msg || res.data.resultMsg)]
                }
              });
            } else if (res.data.resultMsg.indexOf("手机") > -1) {
              this.props.form.setFields({
                phone: {
                  value: values.phone,
                  errors: [new Error(res.data.msg || res.data.resultMsg)]
                }
              });
            } else {
              //message.error(res.data.message || res.data.resultMsg);
            }
          }
        });
      }
    });
  };
  //4.点击修改对话框，清空之前表单数据,将旧数据赋值到表单中
  showEditModal = record => {
    this.props.form.resetFields();
    this.setState({
      editVisible: true,
      editId: record.id,
      username: record.username,
      realName: record.realName,
      pswd: record.pswd, //密码修改时不显示(不可逆)
      sex: this.sexOptions[record.sex],
      age: record.age,
      email: record.email,
      phone: record.phone,
      cardId: record.cardId
    });
  };
  //修改：确认
  editHandleSubmit = e => {
    e.preventDefault();
    this.props.form.validateFields({ force: true }, (err, values) => {
      if (!err) {
        const obj = {};
        obj.id = this.state.editId;
        // obj.username = values.username;
        obj.realName = values.realName;
        //obj.pswd  = MD5(`#${values.pswd}`);
        obj.pswd =
          values.pswd === tempPassword ? md5(`#${values.pswd}`) : values.pswd;
        obj.sex = values.sex === "男" ? 1 : values.sex === "女" ? 2 : 3;
        obj.age = values.age;
        obj.email = values.email;
        obj.phone = values.phone;
				obj.cardId = values.cardId;
				
        updateUser(obj).then(res => {
          if (res.data && res.data.code === "200") {
            tempPassword = "";
            this.setState({ editVisible: false });
            message.success(res.data.msg || res.data.resultMsg);
            this.reloadList();
          } else {
            //错误提示：自定义
            if (res.data.msg.indexOf("用户") > -1) {
              this.props.form.setFields({
                username: {
                  value: values.username,
                  errors: [new Error(res.data.msg || res.data.resultMsg)]
                }
              });
            } else if (res.data.msg.indexOf("邮箱") > -1) {
              this.props.form.setFields({
                email: {
                  value: values.email,
                  errors: [new Error(res.data.msg || res.data.resultMsg)]
                }
              });
            } else if (res.data.msg.indexOf("手机") > -1) {
              this.props.form.setFields({
                phone: {
                  value: values.phone,
                  errors: [new Error(res.data.msg || res.data.resultMsg)]
                }
              });
            } else {
              //data && data.message && message.error(res.data.message || res.data.resultMsg);
            }
          }
        });
      }
    });
  };
  //5.删除行：ids
  onDelete(record) {
    const obj = { ids: record.id };
    deleteUser(obj).then(res => {
      if (res.data.code === "200") {
        message.success("删除成功");
        this.reloadList();
      } else {
        //message.error('删除失败,请重试');
      }
    });
  }
  /**6.【穿梭框】*/
  //穿梭框1：分配组织--修改按钮已注释
  showOrgModal = record => {
    const newState = {
      userVisible: true
    };
    if (record) {
      newState.ids = [record.id];
    }
    this.setState(newState);
  };
  //穿梭框2：修改用户所属组织/关联的角色
  showOrgAndRoleModal = record => {
    const newState = {
      orgAndRoleVisible: true,
      orgAndRoleTitle: `用户【${record.realName}】所属组织和关联的角色`
    };
    if (record) {
      newState.userId = record.id;
      newState.orgId = record.deptId;
    }
    this.setState(newState);
  };
  //穿梭框3：showRoleModal分配角色，修改按钮已删除
  componentDidMount() {
    tempPassword = "";
    this.getMock(); //生命周期：弹框后触发getMock()穿梭框
  }
  //穿梭框：内容、传输
  getMock = () => {
    this.setState({
      mockData: null,
      targetKeys: null
    });
  };
  //穿梭框：子选项indexOf返回某个指定的字符串值在字符串中首次出现的位置
  filterOption = (inputValue, option) => {
    // console.log('穿梭框子选项：',inputValue,option);
    return option.description.indexOf(inputValue) > -1;
  };
  //穿梭框：改变状态状态
  handleChange = targetKeys => {
    // console.log( '穿梭框改变状态状态：', targetKeys);
    this.setState({
      targetKeys: null
    });
  };
  /**7.其他*/
  // 选中操作：导出ids数组转换成字符串join，且需将数组存在状态控制清空全选
  onSelectChange = selectedRowKeys => {
    // console.log('RowKey', selectedRowKeys);
    // console.log('被选RowKey: ', selectedRowKeys.join(","));
    this.setState({
      selectedRowKeys: selectedRowKeys,
      ids: selectedRowKeys.join(",")
    });
  };
  //上传文件:对话框响应response
  uploadStatusChange(info) {
    if (info.file.status !== "uploading") {
    }
    if (info.file.status === "done") {
      // message.success(info.file.response.message);
      const {
        data: { err_link },
        msg,
        data
      } = info.file.response;
      if (err_link) {
        Modal.info({
          content: (
            <div>
              <p>{data.message}</p>
              <div style={{ marginTop: 20 }}>
                <a
                  onClick={() =>
                    downloadFile(`${API_BASE_SECURITY}${err_link}`)
                  }
                >
                  >>点击下载错误记录
                </a>
              </div>
            </div>
          )
        });
      } else {
        message.success(msg);
      }
      this.reloadList();
    } else if (info.file.status === "error") {
      message.error(`${info.file.name} 导入失败`);
    }
  }
  //下载/导出：用户权限限制+url(userDownloadApi)+默认GET/POST+选中参数id（1,2,3，...）
  DownloadClick = () => {
    const url = `${API_BASE_SECURITY}/member/export.shtml`;
    const id = this.state.ids;
    downloadFile(url + "?ids=" + id);
  };
  // 下载模板
  downloadExcel = () => {
    downloadFile("files/excel-template/用户导入.xlsx");
  };
  //校正密码
  handleConfirmBlur = e => {
    const value = e.target.value;
    if (!this.state.newVisible) {
      const { getFieldValue, setFieldsValue } = this.props.form;
      const confirmPassWord = getFieldValue("confirm");

      if (confirmPassWord == "" || confirmPassWord === this.state.pswd) {
        setFieldsValue({
          confirm: this.state.pswd
        });
      }
    }
    this.setState({ confirmDirty: this.state.confirmDirty || !!value });
  };
  //确认密码
  checkPassword = (rule, value, callback) => {
    const form = this.props.form;
    if (value && value !== form.getFieldValue("pswd")) {
      callback("两次输入的密码必须一致");
    } else {
      callback();
    }
  };
  checkConfirm = (rule, value, callback) => {
    const form = this.props.form;
    if (value && this.state.confirmDirty) {
      form.validateFields(["confirm"], { force: true });
    }
    callback();
  };
  //跳转分页：
  onChange = page => {
    this.setState(
      {
        pageNo: page.current,
        pageSize: page.pageSize,
        totalCount: page.total
      },
      () => {
        this.reloadList();
      }
    );
  };

  /**
   * 获得焦点时，清空密码
   */
  clearPassword = e => {
    if (!this.state.newVisible) {
      const { setFieldsValue } = this.props.form;
      setFieldsValue({
        pswd: ""
      });
    }
  };
  setNewPassword = e => {
    if (!this.state.newVisible) {
      tempPassword = e.target.value;
    }
  };
  reviewPassword = () => {
    if (!this.state.newVisible) {
      const { getFieldValue, setFieldsValue } = this.props.form;
      const pswd = getFieldValue("pswd");
      if (pswd === "" || pswd === this.state.pswd) {
        setFieldsValue({
          pswd: this.state.pswd
        });
      }
    }
  };
  clearConPassword = () => {
    if (!this.state.newVisible) {
      const { setFieldsValue } = this.props.form;
      setFieldsValue({
        confirm: ""
      });
    }
  };
  render() {
    const { getFieldDecorator } = this.props.form;
    const allList = deepCopy(this.state.allList);
    const neeCheck = this.state.newVisible
      ? true
      : tempPassword == ""
      ? false
      : true;

    return (
      <div style={{ backgroundColor: "#fff" }}>
        {/*自定义搜索：1个*/}
        <header style={{ padding: "20px 50px" }}>
          <Search
            placeholder="可以按用户账号、真实姓名进行模糊搜索"
            onSearch={this.handleSearch.bind(this)}
          />
        </header>
        {/*按钮操作：4个*/}
        <section>
          {/*新建/修改按钮+对话框：*/}
          <Empower api="/member/add.shtml">
            <Button
              style={{ marginLeft: 10 }}
              onClick={this.handleNewContent.bind(this)}
              type="primary"
            >
              新建
            </Button>
          </Empower>
          <Modal
            maskClosable={false}
            title={this.state.newVisible ? "新建用户" : "修改用户信息"}
            visible={this.state.newVisible || this.state.editVisible}
            onCancel={() => {
              tempPassword = "";
              this.setState({ newVisible: false, editVisible: false });
            }}
            // onOk={() => this.setState({ newVisible: false}||{editVisible: false})}//自定义提交按钮
            footer={[
              <Button
                key="back"
                size="large"
                onClick={() => {
                  tempPassword = "";
                  this.setState({ newVisible: false, editVisible: false });
                }}
              >
                取消
              </Button>,
              <Button
                key="submit"
                type="primary"
                size="large"
                onClick={
                  this.state.newVisible
                    ? this.handleSubmit
                    : this.editHandleSubmit
                }
              >
                确定
              </Button>
            ]}
          >
            <Form>
              <FormItem
                labelCol={{ span: 5 }}
                wrapperCol={{ span: 18 }}
                label="用户账号"
              >
                {getFieldDecorator("username", {
                  initialValue: this.state.username,
                  validateFirst: true,
                  rules: [
                    {
                      required: true,
                      message: "用户账号必须由3-20个字母或数字组成",
                      pattern: /^[a-zA-Z\d]{3,20}$/
                    }
                  ]
                })(
                  <Input
                    placeholder="用户账号（不可修改）"
                    maxLength="20"
                    type="text"
                    disabled={this.state.newVisible ? false : true}
                    spellCheck={false}
                  />
                )}
              </FormItem>

              <FormItem
                labelCol={{ span: 5 }}
                wrapperCol={{ span: 18 }}
                label="真实姓名"
              >
                {getFieldDecorator("realName", {
                  initialValue: this.state.realName,
                  validateFirst: true,
                  rules: [
                    {
                      required: true,
                      message: "请填写用户真实姓名，最大长度50个字符"
                    }
                  ]
                })(
                  <Input
                    placeholder="用户的真实姓名（实名制）"
                    type="text"
                    maxLength="50"
                    spellCheck={false}
                  />
                )}
              </FormItem>

              <FormItem
                labelCol={{ span: 5 }}
                wrapperCol={{ span: 18 }}
                label="密码"
              >
                {getFieldDecorator("pswd", {
                  initialValue: this.state.pswd ? this.state.pswd : null,
                  rules: [
                    {
                      required: neeCheck,
                      message: "密码长度须为6~18之间，且不允许纯字母或纯数字",
                      pattern: neeCheck
                        ? /^(?!^\d+$)(?!^[a-z]+$).{6,18}$/i
                        : /^(?!^\d+$)(?!^[a-z]+$).{6,32}$/i
                    },
                    { validator: this.checkConfirm }
                  ]
                })(
                  <Input
                    placeholder="设置用户密码"
                    type="password"
                    maxLength="18"
                    onFocus={this.clearPassword}
                    onBlur={this.reviewPassword}
                    onChange={this.setNewPassword}
                    spellCheck={false}
                  />
                )}
              </FormItem>

              <FormItem
                labelCol={{ span: 5 }}
                wrapperCol={{ span: 18 }}
                label="确认密码"
                hasFeedback
              >
                {getFieldDecorator("confirm", {
                  initialValue: this.state.pswd ? this.state.pswd : null,
                  rules: [
                    {
                      required: true,
                      message: "确认密码不能为空"
                    },
                    {
                      validator: this.checkPassword
                    }
                  ]
                })(
                  <Input
                    placeholder="确认密码"
                    type="password"
                    onBlur={this.handleConfirmBlur}
                    onFocus={this.clearConPassword}
                    maxLength="18"
                    spellCheck={false}
                  />
                )}
              </FormItem>

              <FormItem
                labelCol={{ span: 5 }}
                wrapperCol={{ span: 18 }}
                label="身份证号码"
              >
                {getFieldDecorator("cardId", {
                  initialValue: this.state.cardId,
                  validateFirst: true,
                  rules: [
                    {
                      required: true,
                      message: "请输入正确的身份证号码",
                      pattern: /^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/
                    }
                  ]
                })(
                  <Input
                    placeholder="用户的身份证号码"
                    maxLength="18"
                    spellCheck={false}
                  />
                )}
              </FormItem>

              <FormItem
                labelCol={{ span: 5 }}
                wrapperCol={{ span: 18 }}
                label="邮箱"
              >
                {getFieldDecorator("email", {
                  initialValue: this.state.email,
                  validateFirst: true,
                  rules: [
                    {
                      type: "email",
                      message: "请输入正确的邮箱"
                    },
                    {
                      required: true,
                      message: "邮箱不能为空"
                    }
                  ]
                })(
                  <Input
                    placeholder="用户的有效邮箱"
                    maxLength="100"
                    spellCheck={false}
                  />
                )}
              </FormItem>

              <FormItem
                labelCol={{ span: 5 }}
                wrapperCol={{ span: 18 }}
                label="手机"
              >
                {getFieldDecorator("phone", {
                  initialValue: this.state.phone,
                  validateFirst: true,
                  rules: [
                    {
                      required: true,
                      message: "请输入正确的手机号码",
                      pattern: /^1[34578]\d{9}$/
                    }
                  ]
                })(
                  <Input
                    placeholder="用户的手机号码"
                    maxLength="11"
                    spellCheck={false}
                  />
                )}
              </FormItem>

              <FormItem
                labelCol={{ span: 5 }}
                wrapperCol={{ span: 18 }}
                label="性别"
              >
                {getFieldDecorator("sex", {
                  initialValue: this.state.sex,
                  rules: [{ required: false, message: "请选择性别" }]
                })(
                  <Select placeholder="请选择下拉选项">
                    <Option value="男">男</Option>
                    <Option value="女">女</Option>
                  </Select>
                )}
              </FormItem>

              <FormItem
                labelCol={{ span: 5 }}
                wrapperCol={{ span: 18 }}
                label="年龄"
              >
                {getFieldDecorator("age", {
                  initialValue: this.state.age,
                  rules: [
                    {
                      required: false,
                      message: "请输入真实的年龄",
                      pattern: /^\d+$/
                    }
                  ]
                })(
                  <Input
                    placeholder="用户的真实年龄"
                    maxLength="4"
                    spellCheck={false}
                  />
                )}
              </FormItem>
            </Form>
          </Modal>
          {/*导出内容：下载文件*/}
          <Empower api="/member/export.shtml">
            <Button
              style={{ marginLeft: 10 }}
              onClick={this.DownloadClick.bind(this)}
              disabled={this.state.ids.length === 0}
              type="primary"
            >
              导出
            </Button>
          </Empower>
          {/*导入内容：上传文件*/}
          <Empower api="/member/import.shtml">
            <Upload
              name="file"
              accept="application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
              action={`${API_BASE_SECURITY}/member/import.shtml`}
              onChange={this.uploadStatusChange.bind(this)}
              showUploadList={false}
              // beforeUpload={()=>message.loading('正在导入，请稍候...')}
            >
              <Button style={{ marginLeft: 10 }} type="primary">
                批量导入
              </Button>
            </Upload>
          </Empower>
          {/*下载模板：后台定制*/}
          <Empower api="/member/import.shtml">
            <Button
              style={{ marginLeft: 10 }}
              onClick={this.downloadExcel.bind(this)}
              type="primary"
            >
              下载模板
            </Button>
          </Empower>
        </section>
        {/*自定义表格：1个*/}
        <TableList
          style={{ margin: 10 }}
          className="th-nowrap "
          rowKey={record => record.id}
          showIndex
          columns={this.columns}
          dataSource={this.state.dataSource}
          pagination={{ total: this.state.totalCount }}
          onChange={this.onChange}
          rowSelection={{
            selectedRowKeys: this.state.selectedRowKeys, //被选默认为空数组
            onChange: this.onSelectChange
          }}
        />
        {/*修改内容：穿梭框1个*/}
        <OrgAndRole
          title={this.state.orgAndRoleTitle}
          visible={this.state.orgAndRoleVisible}
          id={this.state.userId}
          orgId={this.state.orgId}
          onCancel={() => this.setState({ orgAndRoleVisible: false })}
          onOk={() => {
            this.setState({ orgAndRoleVisible: false });
            this.reloadList();
          }}
        />
      </div>
    );
  }
}
//三.调用：
const WrappedApp = Form.create()(UserManagementTable);
export default connect(({ system, account, usersManage, securityCommon }) => ({
  system,
  account,
  usersManage,
  securityCommon
}))(WrappedApp);
