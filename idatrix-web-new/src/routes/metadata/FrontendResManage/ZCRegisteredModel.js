import React from "react";
import { connect } from "dva";
import {
  Button,
  Form,
  Input,
  Col,
  Row,
  TreeSelect,
  message,
  Radio,
  Tabs
} from "antd";
import {
  new_front_server,
  update_front_server,
  check_front_server,
  getDepartmentTree,
  get_frontserver_table_fields,
  SJGXGTestLink,
  SJGXGisDuplicateIp
} from "../../../services/metadata";
import { convertArrayToTree } from "../../../utils/utils";
import { strEnc, strDec } from "utils/EncryptUtil";
import Modal from "components/Modal";
import { databaseType } from "config/jsplumb.config.js";
import { submitDecorator } from "utils/decorator";
import ClearInput from "components/utils/clearInput"

const RadioGroup = Radio.Group;
const FormItem = Form.Item;
const { TabPane } = Tabs;
const { TextArea } = Input;
let Timer;

@submitDecorator
class ZCRegisteredModel extends React.Component {
  state = {
    info: {},
    visibleTs: true,
    visibleTest: true,
    value: 3
  };

  componentWillMount() {
    const { dispatch } = this.props;
    dispatch({ type: "metadataCommon/getDepartments" });
  }

  handleSubmit = e => {
    this.props.disableSubmit();
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) {
        this.props.enableSubmit();
        return;
      }

      /**
       * 更换dbUser为dbPort作为加密方式
       * （加密和解密相同）
       * edited by steven leo on 2018/09/21
       */
      values.dbPassword = strEnc(
        values.dbPassword,
        values.dbPort,
        values.serverIp
      );
      /* this.props.disableSubmit();*/
      const { dispatch } = this.props;
      const { model, info } = this.props.mfservermodel;
      values.status = 0;
      values.dstype = this.state.value;
      if (model === "newmodel") {
        const { renterId } = this.props.account;
        new_front_server({ ...values, renterId }).then(({ data }) => {
          this.props.enableSubmit();
          if (data && data.code === "200") {
            dispatch({
              type: "frontendfesmanage/changeView",
              payload: {
                actionKey: "updatemodel"
              }
            });
            this.setState({
              visibleTs: true
            });
          } else if (data && data.code === "601") {
            message.error("密码强度不符合");
          } else if (data && data.code === "602") {
            message.error("连接失败，请检查服务器配置");
          } else if (data && data.code === "606") {
            message.error("请求失败");
          } 
        });
      } else if (model === "editmodel") {
        let arr = values;

        values.id = this.props.mfservermodel.id;
        console.log(arr, "arr9999");

        const { renterId } = this.props.account;
        update_front_server({ ...arr, renterId }).then(({ data }) => {
          this.props.enableSubmit();
          if (data && data.code === "200") {
            dispatch({
              type: "frontendfesmanage/changeView",
              payload: {
                actionKey: "updatemodel"
              }
            });
            this.setState({
              visibleTs: true
            });
          } else if (data && data.code === "601") {
            message.error("密码强度不符合");
          } else if (data && data.code === "602") {
            message.error("连接失败，请检查服务器配置");
          } else if (data && data.code === "606") {
            message.error("请求失败");
          } 
        });
      }
      this.props.enableSubmit();
      this.hideModel();
    });
  };

  hideModel() {
    const { dispatch, form } = this.props;
    this.setState({
      visibleTs: true,
      visibleTest: true
    });
    dispatch({
      type: "mfservermodel/hide",
      visible: false
    });
    form.resetFields();
  }
  formItemLayout1 = {
    labelCol: { span: 10, offset: 0 },
    wrapperCol: { span: 14, offset: 0 }
  };
  formItemLayoutS = {
    labelCol: { span: 6, offset: 0 },
    wrapperCol: { span: 2, offset: 0 }
  };
  formItemLayout2 = {
    labelCol: { span: 9 },
    wrapperCol: { span: 14 }
  };
  formItemLayout3 = {
    labelCol: { span: 2 },
    wrapperCol: { span: 21 }
  };
  formItemLayout4 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 15 }
  };
  formItemLayout5 = {
    labelCol: { span: 3 },
    wrapperCol: { span: 18 }
  };
  formItemLayout6 = {
    labelCol: { span: 5, offset: 0 },
    wrapperCol: { span: 19, offset: 0 }
  };

  /*IP地址的校验*/
  SJGXGisDuplicateIp = (rule, value, callback) => {
    const { info } = this.props.mfservermodel;
    const { renterId } = this.props.account;
    if (value && value !== info.serverIp) {
      if (Timer) {
        clearTimeout(Timer);
        Timer = null;
      }
      Timer = setTimeout(() => {
        /*renterId serverIp*/
        let obj = {};
        obj.serverIp = value;
        obj.renterId = renterId;
        obj.dsType = this.state.value;
        SJGXGisDuplicateIp(obj).then(res => {
          console.log(res, "res");
          if (res.data.code === "200") {
            /* message.error("该名称已存在,请重新输入");*/
            callback();
          } else if (res.data.code === "630") {
            callback("该IP地址已经创建了前置机，不能再次创建");
          } else {
            message.error(res.data.msg);
          }
        });
      }, 300);
    } else {
      callback();
    }
  };

  /*检测文件名*/
  handleCheckName = (rule, value, callback) => {
    const { info } = this.props.mfservermodel;
    if (value && value !== info.serverName) {
      if (Timer) {
        clearTimeout(Timer);
        Timer = null;
      }
      Timer = setTimeout(() => {
        let obj = {};
        obj.serverName = value;

        const { renterId } = this.props.account;
        check_front_server({ ...obj, renterId }).then(res => {
          if (res.data.data === true) {
            /* message.error("该名称已存在,请重新输入");*/
            callback(true);
          } else {
            callback();
          }
        });
      }, 300);
    } else {
      callback();
    }
  };

  transToName(value) {
    const args = this.state.department;
  }
  onChange1(value) {
    this.transToName(value);
  }

  handleClickHost = (rule, value, callback) => {
    const { dispatch } = this.props;
    this.props.form.validateFields((err, value) => {
      if (err) {
        return;
      }
      /**
       * 更换dbUser为dbPort
       */
      console.log(value,"value====ceshi")

      //return false;

      value.dbPassword = strEnc(value.dbPassword,value.dbPort+"",value.serverIp);
      this.props.disableSubmit();
      let arge = {};
      arge.serverIp = value.serverIp;
      arge.dbPort = value.dbPort;
      arge.dbUser = value.dbUser;
      arge.dbPassword = value.dbPassword;
      arge.dstype = this.state.value;

      SJGXGTestLink(arge).then(({ data }) => {
        if (data && data.code === "0") {
          message.success("测试连接成功");
          this.setState({
            visibleTs: false,
            visibleTest: false
          });
        } else {
          this.setState({
            visibleTest: true
          });
         
        }
      });
      this.props.enableSubmit();
    });
  };
  onChangeClike() {
    this.setState({
      visibleTs: true,
      visibleTest: true,
      value: this.state.value
    });
  }

  onChange = e => {
    console.log("radio checked", e.target.value);
    const { setFieldsValue } = this.props.form;

    this.setState({
      value: e.target.value
    });
    
    let value = e.target.value === 14 ? 5236 : 3306;
    for (let index of databaseType) {
      if (index.value === e.target.value) {
        value = index.port;
      }
    }
    setFieldsValue({ dbPort: value });
  };

  /* {validator:this.SJGXGisDuplicateIp.bind(this),message: '该IP地址已经创建了前置机' }*/

  render() {
    const { mfservermodel, form } = this.props;
    const { getFieldDecorator } = form;
    const { visible, info, model, id } = mfservermodel;
    const { visibleTs, visibleTest, value } = this.state;
    const { departmentsTree } = this.props.metadataCommon;

    let organization = [];
    console.log(info.dbPort,info.dbPort  ? info.dbPort : (this.state.value == 14) ? 5236 : 3306 )
    try {
      const tmp = JSON.parse(info.organization);
      organization = Array.isArray(tmp) ? tmp : [tmp];
    } catch (err) {}
     

    return (
      <Modal
        visible={visible}
        title="前置机基本信息"
        wrapClassName="vertical-center-modal"
        width={750}
        maskClosable={false}
        footer={[
          <Button
            key="back"
            size="large"
            onClick={() => {
              this.hideModel();
            }}
          >
            取消
          </Button>,
          <Button key="submit" type="primary" size="large" onClick={this.handleSubmit} loading={this.props.submitLoading}>确定</Button>,
          // <Button size="large" disabled={visibleTest  !== true} style={{float: "left"}} onClick={this.handleClickHost} loading={this.props.submitLoading}>测试连接</Button>,
        ]}
        onCancel={this.hideModel.bind(this)}
      >
        <Form>
          <ClearInput />
          <FormItem {...this.formItemLayout1} style={{ marginBottom: "10px" }}>
            {getFieldDecorator("dsType", {
              initialValue: info.dsType || 3
            })(
              <RadioGroup
                style={{ margin: "1% 0% 4% 36%" }}
                onChange={this.onChange}
                disabled={model === "editmodel"}
              >
                <Radio key={3} value={3}>
                  MySQL
                </Radio>
                <Radio key={2} value={2}>
                  Oracle
                </Radio>
                <Radio key={14} value={14}>
                  DM
                </Radio>
                <Radio key={8} value={8}>
                  PostgreSQL
                </Radio>
              </RadioGroup>
            )}
          </FormItem>

          <Row>
            <Col span={12}>
              <FormItem
                label="前置机名称: "
                {...this.formItemLayout1}
                style={{ marginBottom: "10px" }}
              >
                {getFieldDecorator("serverName", {
                  initialValue: info.serverName,
                  rules: [
                    { required: true, message: "请输入前置机名称" },
                    {
                      validator: this.handleCheckName.bind(this),
                      message: "前置机名称已存在"
                    }
                  ]
                })(
                  <Input
                    placeholder="请输入前置机名称"
                    maxLength="50"
                    disabled={model === "showclickmodel"}
                  />
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                label="IP地址: "
                {...this.formItemLayout1}
                style={{ marginBottom: "10px" }}
              >
                {getFieldDecorator("serverIp", {
                  initialValue: info.serverIp,
                  rules: [
                    { required: true, message: "请输入IP地址" },
                    {
                      pattern: /^(\d{1,3}\.){3}\d{1,3}$/,
                      message: "请输入正确的IP地址"
                    }
                  ]
                })(
                  <Input
                    placeholder="请输入IP地址"
                    maxLength="20"
                    onChange={this.onChangeClike.bind(this)}
                    disabled={model === "showclickmodel"}
                  />
                )}
              </FormItem>
            </Col>

            <Col span={12}>
              <FormItem
                label="数据库端口: "
                {...this.formItemLayout1}
                style={{ marginBottom: "10px" }}
              >
                {getFieldDecorator("dbPort", {
                  initialValue: info.dbPort || 3306 ,
                  rules: [
                    { required: true, message: "请输入端口号" },
                    {
                      pattern: /^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-5]{2}[0-3][0-5])$/,
                      message: "请输入正确的端口号"
                    }
                  ]
                })(
                  <Input
                    placeholder="请输入端口号"
                    onChange={this.onChangeClike.bind(this)}
                    disabled={model === "showclickmodel"}
                  />
                )}
              </FormItem>
            </Col>
            {/*<Col span={8}>
                  <FormItem   label="密码: " style={{marginLeft:"20px"}}>
                    {getFieldDecorator('serverPassword', {
                      initialValue:info.serverPassword,
                      rules: [{ required: true, message: '请输入密码' }]
                    })(
                      <Input type="password"  onChange={this.onChangeClike.bind(this)} disabled={model === "showclickmodel"}/>
                    )}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem   label="FTP端口: " style={{marginLeft:"20px"}}>
                    {getFieldDecorator('ftpPort', {
                      initialValue:info.ftpPort
                    })(
                      <Input  onChange={this.onChangeClike.bind(this)} disabled={model === "showclickmodel"}/>
                    )}
                  </FormItem>
                </Col>*/}

            <Col span={24}>
              <FormItem
                label="数据库管理员账号: "
                {...this.formItemLayout6}
                style={{ marginBottom: "10px" }}
              >
                {getFieldDecorator("dbUser", {
                  initialValue: info.dbUser,
                  rules: [
                    { required: false, message: "请输入数据库管理员账号" },
                    {
                      validator: this.handleCheckName.bind(this),
                      message: "已存在该名称"
                    }
                  ]
                })(
                  <Input
                    disabled={model === "showclickmodel"}
                    placeholder="填写的数据库用户名必须要有权限创建数据库用户、数据库和表"
                    onChange={this.onChangeClike.bind(this)}
                  />
                )}
              </FormItem>
            </Col>

            <Col span={24}>
              <FormItem
                label="数据库管理员密码："
                {...this.formItemLayout6}
                style={{ marginBottom: "10px" }}
              >
                {getFieldDecorator("dbPassword", {
                  // 更换dbUser 为 dbPort 作为firstKey
                  // edited by Steven Leo on 2018/09/21
                  initialValue: info.dbPassword
                    ? strDec(info.dbPassword, info.dbPort, info.serverIp)
                    : "",
                  rules: [
                    { required: false, message: "请输入数据库管理员密码" }
                  ]
                })(
                  <Input
                    disabled={model === "showclickmodel"}
                    placeholder="请输入数据库用户密码"
                    type="password"
                    onChange={this.onChangeClike.bind(this)}
                  />
                )}
              </FormItem>
            </Col>

            <Col span={24}>
              <FormItem label="对接的组织(可多选)：" {...this.formItemLayout6}>
                {getFieldDecorator("organization", {
                  initialValue: organization,
                  rules: [{ required: true, message: "请输入对接的组织" }]
                })(
                  <TreeSelect
                    placeholder="请选择组织"
                    treeData={departmentsTree}
                    onChange={value => {
                      this.onChange1(value);
                    }}
                    disabled={model === "showclickmodel"}
                    treeDefaultExpandAll={false}
                    multiple
                    allowClear
                  />
                )}
              </FormItem>
            </Col>

            <Col span={24}>
              <FormItem label="位置信息：" {...this.formItemLayout6}>
                {getFieldDecorator("positionInfo", {
                  initialValue: info.positionInfo
                })(
                  <Input
                    maxLength="200"
                    placeholder="请填写机房、机柜、机架等信息"
                    disabled={model === "showclickmodel"}
                  />
                )}
              </FormItem>
            </Col>

            <Col span={12}>
              <FormItem
                label="创建者: "
                {...this.formItemLayout1}
                style={{ marginBottom: "10px" }}
              >
                {getFieldDecorator("manager", {
                  initialValue: this.props.account.username,
                  rules: [{ required: true, message: "请输入创建人姓名" }]
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                label="创建人手机: "
                {...this.formItemLayout1}
                style={{ marginBottom: "10px" }}
              >
                {getFieldDecorator("phone", {
                  initialValue: this.props.account.phone,
                  rules: [{ required: true, message: "请输入创建人手机" }]
                })(<Input disabled />)}
              </FormItem>
            </Col>

            <Col span={12}>
              <FormItem
                label="创建人邮箱: "
                {...this.formItemLayout1}
                style={{ marginBottom: "10px" }}
              >
                {getFieldDecorator("mail", {
                  initialValue: this.props.account.email,
                  rules: [{ required: true, message: "请输入创建人邮箱" }]
                })(<Input disabled />)}
              </FormItem>
            </Col>

            <Col span={24}>
              <FormItem
                label="备注："
                {...this.formItemLayout6}
                style={{ marginBottom: "10px" }}
              >
                {getFieldDecorator("remark", {
                  initialValue: info.remark
                })(
                  <TextArea
                    rows={4}
                    maxLength="200"
                    spellCheck={false}
                    disabled={model === "showclickmodel"}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
}
const ZCRegisteredModelList = Form.create()(ZCRegisteredModel);
export default connect(({ mfservermodel, account, metadataCommon }) => ({
  mfservermodel,
  account,
  metadataCommon
}))(ZCRegisteredModelList);
