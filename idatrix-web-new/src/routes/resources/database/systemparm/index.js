import React from "react";
import { Form, Cascader, Input, Select, Button } from "antd";
import { connect } from "dva";

const FormItem = Form.Item;
const Option = Select.Option;

const index = ({ systemparmModel, form, resourcesCommon, dispatch }) => {
  const { config, fileRoot, originFileRoot, filesId, fileId } = systemparmModel;
  const { hdfsTree, rolesList } = resourcesCommon;
  const { getFieldDecorator } = form;
  console.log(rolesList, "rolesList");
  const formItemLayout = {
    labelCol: {
      xs: { span: 24 },
      sm: { span: 8 }
    },
    wrapperCol: {
      xs: { span: 24 },
      sm: { span: 14 }
    }
  };

  const tailFormItemLayout = {
    wrapperCol: {
      xs: {
        span: 24,
        offset: 0
      },
      sm: {
        span: 16,
        offset: 8
      }
    }
  };

  //文件资源目录
  const handleChange = (ids, options) => {
    if (filesId === options) {
      message.error("文件类资源目录与上报文件目录不能一致");
    } else {
      let args = [];
      for (let index of options) {
        args.push(index.label);
      }
      dispatch({
        type: "systemparmModel/save",
        payload: { fileRoot: args.join("/"), fileId: options }
      });
    }
  };

  //上报文件目录
  const handleChange1 = (ids, options) => {
    if (fileId === options) {
      message.error("文件类资源目录与上报文件目录不能一致");
    } else {
      let args = [];
      for (let index of options) {
        args.push(index.label);
      }
      dispatch({
        type: "systemparmModel/save",
        payload: { originFileRoot: args.join("/"), filesId: options }
      });
    }
  };

  //保存
  const handleSubmit = e => {
    e.preventDefault();

    form.validateFields((err, values) => {
      if (!err) {
        const { id } = config;

        const fileRootPath = hdfsTree.find(v => v.id == values.fileRootIds);
        const originRootPath = hdfsTree.find(
          v => v.id == values.originFileRootIds
        );

        dispatch({
          type: "systemparmModel/handleSubmit",
          payload: {
            ...values,
            id: id ? id : 0,
            fileRoot: fileRootPath.allPathname + "/",
            originFileRoot: originRootPath.allPathname + "/",
            fileRootIds: values.fileRootIds,
            originFileRootIds: values.originFileRootIds,
            deptAdminRole: parseInt(values.deptAdminRole),
            subApproverRole: parseInt(values.subApproverRole),
            centerAdminRole: parseInt(values.centerAdminRole),
            deptStaffRole: parseInt(values.deptStaffRole),
            importInterval: parseInt(values.importInterval),
            dbUploadSize: parseInt(values.dbUploadSize),
            fileUploadSize: parseInt(values.fileUploadSize)
          }
        });
      }
    });
  };

  //檢測名字
  const handleCheckName = (rule, value, callback) => {
    const { getFieldValue } = form;
    let newValue = getFieldValue("fileRootIds");

    if (value && value === newValue) {
      callback(true);
    } else {
      callback();
    }
  };

  return (
    <div style={{ margin: "30px 0" }}>
      <Form style={{ maxWidth: "600px" }}>
        <FormItem label="文件类资源目录" {...formItemLayout}>
          {getFieldDecorator("fileRootIds", {
            initialValue:
              config && config.fileRootIds ? config.fileRootIds.toString() : "",
            rules: [{ required: true, message: "请选择文件类资源目录！" }]
          })(
            <Select>
              {hdfsTree &&
                hdfsTree.map(v => (
                  <Option value={v.id.toString()} key={v.id}>
                    {v.allPathname}
                  </Option>
                ))}
            </Select>
          )}
        </FormItem>
        <FormItem label="上报文件目录" {...formItemLayout}>
          {getFieldDecorator("originFileRootIds", {
            initialValue:
              config && config.originFileRootIds
                ? config.originFileRootIds.toString()
                : "",
            rules: [
              { required: true, message: "请输入上报文件目录！" },
              {
                validator: handleCheckName,
                message: "上报文件目录不能与文件类资源目录重复！"
              }
            ]
          })(
            <Select>
              {hdfsTree &&
                hdfsTree.map(v => (
                  <Option value={v.id.toString()} key={v.id}>
                    {v.allPathname}
                  </Option>
                ))}
            </Select>
          )}
        </FormItem>
        <FormItem label="导入扫描时间间隔" {...formItemLayout}>
          {getFieldDecorator("importInterval", {
            initialValue: config.importInterval ? config.importInterval : 5,
            rules: [{ required: true, message: "请输入导入扫描时间间隔！" }]
          })(<Input type="number" addonAfter={<span>分钟</span>} />)}
        </FormItem>
        <FormItem label="数据库类上传文件大小" {...formItemLayout}>
          {getFieldDecorator("dbUploadSize", {
            initialValue: config.dbUploadSize ? config.dbUploadSize : 12,
            rules: [{ required: true, message: "请输入数据库类上传文件大小！" }]
          })(<Input type="number" addonAfter={<span>Mb</span>} />)}
        </FormItem>
        <FormItem label="文件类上传文件大小" {...formItemLayout}>
          {getFieldDecorator("fileUploadSize", {
            initialValue: config.fileUploadSize ? config.fileUploadSize : 20,
            rules: [{ required: true, message: "请输入文件类上传文件大小！" }]
          })(<Input type="number" addonAfter={<span>Mb</span>} />)}
        </FormItem>
        <FormItem label="部门管理员" {...formItemLayout}>
          {getFieldDecorator("deptAdminRole", {
            initialValue: config.deptAdminRole ? config.deptAdminRole + "" : "",
            rules: [{ required: true, message: "请输入部门管理员！" }]
          })(
            <Select placeholder="请选择部门管理员">
              {rolesList.map(index => (
                <Option key={index.id} value={index.id + ""}>
                  {index.name}
                </Option>
              ))}
            </Select>
          )}
        </FormItem>
        <FormItem label="数据中心管理员" {...formItemLayout}>
          {getFieldDecorator("centerAdminRole", {
            initialValue: config.centerAdminRole
              ? config.centerAdminRole + ""
              : "",
            rules: [{ required: true, message: "请选择数据中心管理员！" }]
          })(
            <Select placeholder="请选择数据中心管理员">
              {rolesList.map(index => (
                <Option key={index.id} value={index.id + ""}>
                  {index.name}
                </Option>
              ))}
            </Select>
          )}
        </FormItem>
        <FormItem label="订阅审批角色" {...formItemLayout}>
          {getFieldDecorator("subApproverRole", {
            initialValue: config.subApproverRole
              ? config.subApproverRole + ""
              : "",
            rules: [{ required: true, message: "请选择订阅审批角色！" }]
          })(
            <Select placeholder="请选择订阅审批角色">
              {rolesList.map(index => (
                <Option key={index.id} value={index.id + ""}>
                  {index.name}
                </Option>
              ))}
            </Select>
          )}
        </FormItem>
        <FormItem label="目录填报角色" {...formItemLayout}>
          {getFieldDecorator("deptStaffRole", {
            initialValue: config.deptStaffRole ? config.deptStaffRole + "" : "",
            rules: [{ required: true, message: "请输入目录填报角色！" }]
          })(
            <Select placeholder="请选择目录填报角色">
              {rolesList.map(index => (
                <Option key={index.id} value={index.id + ""}>
                  {index.name}
                </Option>
              ))}
            </Select>
          )}
        </FormItem>
        <FormItem {...formItemLayout} label="上次更新时间">
          <span className="ant-form-text">{config.updateTime}</span>
        </FormItem>
        <FormItem {...tailFormItemLayout}>
          <Button type="primary" onClick={handleSubmit}>
            保存
          </Button>
        </FormItem>
      </Form>
    </div>
  );
};

export default connect(({ systemparmModel, resourcesCommon }) => ({
  systemparmModel,
  resourcesCommon
}))(Form.create()(index));
