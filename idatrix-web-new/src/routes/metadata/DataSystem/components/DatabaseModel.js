/**
 * Created by Administrator on 2017/5/22.
 */
import React from "react";
import { connect } from "dva";
import { Form, Input, Select, message } from "antd";
import { dbUsernameIsExists } from "services/metadataDataSystem";
import ClearInput from "components/utils/clearInput"

import {
  insert_database_table_fields,
  update_database_table_fields,
  get_front_pos,
  check_if_dsname_exists,
} from "../../../../services/metadata";
import Modal from "components/Modal";
import { submitDecorator } from "utils/decorator";
import { strEnc, strDec } from "../../../../utils/EncryptUtil";

const FormItem = Form.Item;
const { TextArea } = Input;

let Timer;

@submitDecorator
class DataModel extends React.Component {

  constructor() {
    super();

    this.state = {
      radioValue: "1",
      data: [],
      dsType: "",
      serverIp: "",
      dbPort: ""
    };

    this.handleGetName = this.handleGetName.bind(this);
    this.handleGetNameScarch = this.handleGetNameScarch.bind(this);
    this.handleGetDBUserName = this.handleGetDBUserName.bind(this);
  }

  handleSubmit = e => {
    e.preventDefault();
    this.props.form.validateFields({ force: true }, (err, values) => {
      if (!err) {
        this.props.disableSubmit();
        const { dispatch } = this.props;
        const { model } = this.props.databasemodel;
        let arr = {};
        arr.renterId = this.props.account.renterId;
        arr.sourceId = 1;
        arr.dsType = this.state.dsType;
        arr.status = 0;
        arr.dbDatabasename = values.dbDatabasename;

        console.log(values);
        arr.dbPassword = strEnc(
          values.dbpw,
          values.dbun,
          values.dbDatabasename
        );
        arr.dbUsername = values.dbun;
        arr.dsName = values.dsName;
        arr.remark = values.remark;
        arr.serverId = values.serverId;
        if (model === "newmodel") {
          insert_database_table_fields(arr).then(({ data }) => {
            this.props.enableSubmit();
            if (data && data.code === "200") {
              dispatch({
                type: "datasystemsegistration/changeView",
                payload: {
                  actionKey: "updatemodel"
                }
              });
              message.success("新建成功");
              this.hideModel();
            }
          });
        } else if (model === "newmodelres") {
          let arr1 = {};
          arr1.renterId = this.props.account.renterId;
          arr1.sourceId = 1;
          arr1.dsType = this.state.dsType;
          arr1.status = 0;
          arr1.dbDatabasename = values.dbDatabasename;
          arr1.dbUsername = values.dbun;
          arr1.dsName = values.dsName;
          arr1.remark = values.remark;
          arr1.serverId = values.serverId;
          arr1.type = "register";

          /**
           * 使用strEnc加密密码
           */
          arr1.dbPassword = strEnc(
            values.dbpw,
            values.dbun,
            values.dbDatabasename
          );
          insert_database_table_fields(arr1).then(({ data }) => {
            this.props.enableSubmit();
            if (data && data.code === "200") {
              dispatch({
                type: "datasystemsegistration/changeView",
                payload: {
                  actionKey: "updatemodel"
                }
              });
              message.success("新建成功");
              this.hideModel();
            }
          });
        } else if (model === "editmodel") {
          const { info } = this.props.databasemodel;
          values.dsId = info.dsId;
          let serverName = info.serverName;
          if (serverName === values.serverId) {
            values.serverId = info.frontEndServer.id;
          }

          const { renterId } = this.props.account;

          const dbPassword = strEnc(
            values.dbpw,
            values.dbun,
            values.dbDatabasename
          );
          update_database_table_fields(
            {
              ...values,
              renterId,
              dbPassword,
              dbUsername: values.dbun
            }).then(
              ({ data }) => {
                this.props.enableSubmit();
                if (data && data.code === "200") {
                  dispatch({
                    type: "datasystemsegistration/changeView",
                    payload: {
                      actionKey: "updatemodel"
                    }
                  });
                  message.success("修改成功");
                  this.hideModel();
                }
              }
            );
        }
      }
      this.props.enableSubmit();
    });
  };

  /*检测文件名 数据库名称*/
  handleGetName = (rule, value, callback) => {
    const { info, model } = this.props.databasemodel;
    const { form } = this.props;
    const serverId = form.getFieldValue("serverId");

    //编辑资源时不进行验证
    if (model === "editmodel") { 
      callback() 
    }else{
      if (value && value !== info.dbDatabasename) {
        if (Timer) {
          clearTimeout(Timer);
          Timer = null;
        }
        Timer = setTimeout(() => {
          let obj = {
            dbDatabasename: value,
            dsType: this.state.dsType,
            type: model === "newmodel" ? "create" : "register",
            serverId: serverId,
            sourceId: 1
          };
          check_if_dsname_exists(obj, { type: "dbname" }).then(res => {
            console.log(res, "check_get_name");
            const { data } = res.data;
            if (data === true) {
              callback(true);
            } else {
              callback();
            }
          });
        }, 300);
      } else {
        callback();
      }
    }
  };
  /*检测文件名 数据库系统名称*/
  handleGetNameScarch = (rule, value, callback) => {
    const { info, model } = this.props.databasemodel;

    //编辑资源时不进行验证
    if (model === "editmodel") {
      callback()
    } else {
      if (value && value !== info.dsName) {
        if (Timer) {
          clearTimeout(Timer);
          Timer = null;
        }
        Timer = setTimeout(() => {
          let obj = {
            dsName: value,
          };
          check_if_dsname_exists(obj).then(res => {
            console.log(res, "check_get_name");
            const { data } = res.data;
            if (data === true) {
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

  };

  /**
   * 此处原为校验密码是否存在
   * 但是接口时校验用户名是否存在
   */
  handleGetDBUserName = async (rule, value, callback) => {
    const { form } = this.props;
    const { model } = this.props.databasemodel;

    //编辑时屏蔽检验
    if (model === "editmodel") {
      callback();
    } else {
      const serverId = form.getFieldValue("serverId");
      const { data } = await dbUsernameIsExists({
        dbUsername: value,
        serverId,
        dsType: 3,
        type: model === "newmodel" ? "create" : "register"
        /*sourceId: 1,*/
      });
      if (data.data) {
        callback(rule.message);
      } else {
        callback();
      }
    }
  };

  hideModel() {
    const { dispatch, form } = this.props;
    dispatch({
      type: "databasemodel/hide",
      visible: false
    });
    this.state.data = [];
    form.resetFields();
  }

  formItemLayout1 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 17 }
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

  handleRadioChange(e) {
    this.setState({
      radioValue: e.target.value
    });
  }

  showRadioModel(readonly) {
    const { getFieldDecorator } = this.props.form;
    const { info } = this.props.databasemodel;
    if (this.state.radioValue === "oracle") {
      return (
        <div>
          <FormItem label="数据表空间: " {...this.formItemLayout1}>
            {getFieldDecorator("datatablespace", {
              initialValue: info.datatablespace,
              rules: [{ required: true, message: "请输入数据表空间" }]
            })(<Input disabled={readonly} />)}
          </FormItem>
          <FormItem label="索引表空间: " {...this.formItemLayout1}>
            {getFieldDecorator("indextablespace", {
              initialValue: info.indextablespace,
              rules: [{ required: true, message: "请输入索引表空间" }]
            })(<Input disabled={readonly} placeholder="请输入索引表空间" />)}
          </FormItem>
        </div>
      );
    } else if (this.state.radioValue === "sqlserver") {
      return (
        <div>
          <FormItem label="示例名称: " {...this.formItemLayout1}>
            {getFieldDecorator("instancename", {
              initialValue: info.instancename
            })(<Input disabled={readonly} placeholder="请输入示例名称" />)}
          </FormItem>
          {/**
             * *
              <FormItem label="端口号: "  {...this.formItemLayout1} >
             {getFieldDecorator('dbPort', {
               initialValue:info.dbPort,
               rules: [{ required: true, message: '请输入端口号' },
                      {pattern:/^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-5]{2}[0-3][0-5])$/, message: '请输入正确的端口号' }]
             })(
               <Input disabled={readonly} placeholder="请输入端口号"/>
             )}
           </FormItem>
             */}
        </div>
      );
    }
    /**
      *
       else{
        return (
          <FormItem   label="端口号: "  {...this.formItemLayout1} >
            {getFieldDecorator('dbPort', {
              initialValue:info.dbPort || '3306',
              rules: [{ required: true, message: '请输入端口号' },
                       {pattern:/^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-5]{2}[0-3][0-5])$/, message: '请输入正确的端口号' }]
            })(
              <Input disabled={readonly} placeholder="请输入端口号"/>
            )}
          </FormItem>
        )
     }
      *
      */
  }
  handleFocus = (value, dsType) => {
    console.log(value, "value11111", dsType);
    const { model } = this.props.databasemodel;
    if (model === "newmodelres") {
      let obj = {};
      obj.type = "";

      // 新增renter直接传入
      // edited by steven leo
      const { renterId } = this.props.account;
      obj.renterId = renterId;

      get_front_pos(obj).then(res => {
        const { data } = res.data;
        const rows = (data && data.rows) || [];
        for (let index of rows) {
          if (value === index.serverId) {
            this.setState({
              data: rows
            });
            return;
          }
        }
      });
    } else {
      let obj = {};
      obj.type = "create";

      // 新增renter直接传入
      // edited by steven leo
      const { renterId } = this.props.account;
      obj.renterId = renterId;

      get_front_pos(obj).then(res => {
        const { data } = res.data;
        const rows = (data && data.rows) || [];
        for (let index of rows) {
          if (value === index.serverId) {
            this.setState({
              data: rows
            });
            return;
          }
        }
      });
    }
  };
  componentDidMount() {
    this.onChange();
  }

  onChange = (value, dsType) => {
    const { model } = this.props.databasemodel;
    if (model === "newmodelres") {
      let obj = {};
      obj.type = "";

      // 新增renter直接传入
      // edited by steven leo
      const { renterId } = this.props.account;
      obj.renterId = renterId;

      get_front_pos(obj).then(res => {
        const { data } = res.data;
        const rows = (data && data.rows) || [];
        for (let index of rows) {
          console.log(index, "index=====index", value === index.id);
          if (value == index.id) {
            this.setState({
              dsType: index.dsType,
              dbPort: index.dbPort,
              serverIp: index.serverIp
            });
            return;
          }
        }
      });
    } else {
      let obj = {};
      obj.type = "create";

      // 新增renter直接传入
      // edited by steven leo
      const { renterId } = this.props.account;
      obj.renterId = renterId;

      get_front_pos(obj).then(res => {
        const { data } = res.data;
        const rows = (data && data.rows) || [];
        for (let index of rows) {
          if (value == index.id) {
            this.setState({
              dsType: index.dsType,
              dbPort: index.dbPort,
              serverIp: index.serverIp
            });
            return;
          }
        }
      });
    }
  };
  /*  { message: '用户名必须由3-20个字母或数字组成，且必须以字母开头', pattern: /^[a-z][a-z\d]{2,19}$/i},
    rules: [
            { required: true, message: '请输入数据库名称' },
            { pattern: /^(?=[a-z])[0-9a-z_-]+$/, message: '只能使用小写字母、数字、下划线，且必须以字母开头' },
          ]

            */
  render() {
    const { getFieldDecorator } = this.props.form;
    const { visible, info, model } = this.props.databasemodel;

    const options = this.state.data.map(d => (
      <Select.Option disabled={(model === "newmodel" && !d.dbPassword)} key={d.id} value={d.id + ""}>
        {d.serverName}
        {(model === "newmodel" && !d.dbPassword) ? "（请先设置该前置机密码）" : ""}
      </Select.Option>
    ));

    const editDisable = model === "editmodel" && info.status === "已生效"
    const str =
      model === "newmodelres"
        ? "注册"
        : "" || model === "editmodel"
          ? "编辑"
          : "" || model === "newmodel"
            ? "新建"
            : "";

    return (
      <Modal
        visible={visible}
        title={`${str}前置机数据库基本信息`}
        wrapClassName="vertical-center-modal DatabaseModel"
        onOk={this.handleSubmit}
        onCancel={() => {
          this.hideModel();
        }}
        maskClosable={false}
        confirmLoading={this.props.submitLoading}
      >
        {/*(2017.9.11新建元数据：)*/}
        <Form
          onSubmit={this.handleSubmit}
        // autoComplete="off"
        >
          <ClearInput />
          <FormItem
            label="所在前置机: "
            {...this.formItemLayout1}
            style={{ marginBottom: 10 }}
          >
            {getFieldDecorator("serverId", {
              initialValue: info.serverName,
              rules: [{ required: true, message: "请选择所在前置机" }]
            })(
              <Select
                disabled={editDisable}
                placeholder="请选择所在前置机"
                onChange={this.onChange}
                onFocus={this.handleFocus}
              >
                {options}
              </Select>
            )}
          </FormItem>

          <FormItem label="数据库中文名: " {...this.formItemLayout1}>
            {getFieldDecorator("dsName", {
              initialValue: info.dsName,
              validateTrigger: "onBlur",
              rules: [
                { required: true, message: "请输入数据库中文名称" },
                { validator: this.handleGetNameScarch, message: "数据库中文名已经存在" }
              ]
            })(
              <Input
                placeholder="请输入数据库中文名称"
                spellCheck={false}
                maxLength="50"
                disabled={editDisable}
              />
            )}
          </FormItem>

          {model === "newmodelres" || model === "editmodel" ? (
            <FormItem label="数据库名称: " {...this.formItemLayout1}>
              {getFieldDecorator("dbDatabasename", {
                initialValue: info.dbDatabasename,
                validateFirst: true,
                validateTrigger: "onBlur",
                rules: [
                  { required: true, message: "请输入数据库名称" },
                  { validator: this.handleGetName, message: "数据库名称已存在" },
                  {
                    pattern: /^(?=[a-z])[0-9a-z_]+$/,
                    message: "只能使用小写字母、数字、下划线，且必须以字母开头"
                  }
                ]
              })(
                <Input
                  placeholder="请输入需要创建的数据库名称"
                  disabled={editDisable}
                  maxLength="50"
                  spellCheck={false}
                />
              )}
            </FormItem>
          ) : (
              <FormItem label="数据库名称: " {...this.formItemLayout1}>
                {getFieldDecorator("dbDatabasename", {
                  initialValue: info.dbDatabasename,
                  validateFirst: true,
                  validateTrigger: "onBlur",
                  rules: [
                    { required: true, message: "请输入数据库名称" },
                    { validator: this.handleGetName, message: "数据库名称已存在" },
                    {
                      pattern: /^(?=[a-z])[0-9a-z_]+$/,
                      message: "只能使用小写字母、数字、下划线，且必须以字母开头"
                    }
                  ]
                })(
                  <Input
                    placeholder="请输入需要创建的数据库名称"
                    disabled={editDisable}
                    maxLength="50"
                    spellCheck={false}
                  />
                )}
              </FormItem>
            )}

          {/* {this.showRadioModel(readonly)} */}

          <FormItem label="数据库用户名: " {...this.formItemLayout1}>
            {getFieldDecorator("dbun", {
              initialValue: info.dbUsername,
              validateFirst: true,
              validateTrigger: "onBlur",
              rules: [
                { required: true, message: "请输入数据库用户名" },
                { validator: this.handleGetDBUserName, message: "用户名重复" }
              ]
            })(
              <Input
                disabled={editDisable}
                // autoComplete="new-password"
                placeholder="请输入要创建的数据库用户名称"
                spellCheck={false}
                maxLength="20"
              />
            )}
          </FormItem>

          {
            (model === "newmodelres" || model === "editmodel") ? (
              <FormItem label="数据库密码: " {...this.formItemLayout1}>
                {getFieldDecorator("dbpw", {
                  initialValue: info.dbPassword ? strDec(info.dbPassword, info.dbUsername, info.dbDatabasename) : "",
                  validateTrigger: "onBlur",
                  rules: [{ required: true, message: "请输入数据库密码" }]
                })(
                  <Input
                    disabled={editDisable}
                    // autoComplete="new-password"
                    maxLength="20"
                    placeholder="请输入数据库密码"
                    type="password"
                  />
                )}
              </FormItem>
            ) : (
                <FormItem label="数据库密码: " {...this.formItemLayout1}>
                  {getFieldDecorator("dbpw", {
                    initialValue: info.dbPassword ? strDec(info.dbPassword, info.dbUsername, info.dbDatabasename) : "",
                    validateTrigger: "onBlur",
                    rules: [
                      { required: true, message: "请输入数据库密码" },
                      {
                        message:
                          "至少8位，由大写字母、小写字母、特殊字符和数字组成",
                        pattern: /^(?=^.{8,}$)(?=.*\d)(?=.*[\W_]+)(?=.*[A-Z])(?=.*[a-z])(?!.*\n).*$/
                      },
                    ]
                  })(
                    <Input
                      disabled={editDisable}
                      // autoComplete="new-password"
                      maxLength="20"
                      placeholder="请输入数据库密码"
                      type="password"
                    />
                  )}
                </FormItem>
              )}

          <FormItem
            label="备注："
            {...this.formItemLayout1}
            style={{ marginBottom: "8px" }}
          >
            {getFieldDecorator("remark", {
              initialValue: info.remark
            })(
              <TextArea
                placeholder="请输入0-200的备注"
                maxLength="200"
                rows={4}
                spellCheck={false}
              />
            )}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}

const DatabaseModel = Form.create()(DataModel);
export default connect(({ databasemodel, account }) => ({
  databasemodel,
  account
}))(DatabaseModel);
