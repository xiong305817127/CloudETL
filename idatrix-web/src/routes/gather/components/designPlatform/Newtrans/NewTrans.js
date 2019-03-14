/**
 * Created by Administrator on 2018/1/22.
 */
import React from "react";
import { Button, Form, Input, Row, Col, Select, message } from "antd";
import Modal from "components/Modal.js";
import { connect } from "dva";
import EditTable from "../../common/EditTable";
import {
  getCheck_trans_name,
  getSave_trans_attributes
} from "../../../../../services/gather";
import {
  getCheck_job_name,
  getSave_job_attributes
} from "../../../../../services/gather1";

const { TextArea } = Input;
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
const SelectOption = Select.Option;

let Timer;

class Index extends React.Component {
  getText() {
    const { actionName, status } = this.props.newtrans;

    let str = "";
    if (actionName === "newTrans") {
      str = "新建";
    } else {
      str = "编辑";
    }
    if (status === "trans") {
      str += "转换";
    } else {
      str += "调度";
    }
    return str;
  }

  getName() {
    const { status } = this.props.newtrans;
    if (status === "trans") {
      return "转换名称";
    } else {
      return "调度名称";
    }
  }

  getCopyName() {
    const { status } = this.props.newtrans;
    if (status === "trans") {
      return "复制转换名";
    } else {
      return "复制调度名";
    }
  }

  handleCheckTrans(rule, value, callback) {
    const { info_name } = this.props.newtrans;
    const { owner } = this.props.transheader;

    if (value && info_name != value) {
      if (Timer) {
        clearTimeout(Timer);
        Timer = null;
      }
      Timer = setTimeout(() => {
        getCheck_trans_name({ name: value, owner: owner }).then(res => {
          const { code, data } = res.data;
          if (code === "200") {
            const { result } = data;
            if (result === true) {
              callback(true);
            } else {
              callback();
            }
          }
        });
      }, 300);
    } else {
      callback();
    }
  }

  handleCheckJob(rule, value, callback) {
    const { info_name } = this.props.newtrans;
    if (value && info_name != value) {
      if (Timer) {
        clearTimeout(Timer);
        Timer = null;
      }
      Timer = setTimeout(() => {
        getCheck_job_name(value).then(res => {
          const { code, data } = res.data;
          console.log(code === "200");
          if (code === "200") {
            const { result } = data;
            console.log(result);
            if (result === true) {
              callback(true);
            } else {
              callback();
            }
          }
        });
      }, 300);
    } else {
      callback();
    }
  }

  checkNewName() {
    const { status } = this.props.newtrans;

    if (status === "trans") {
      return this.handleCheckTrans.bind(this);
    } else {
      return this.handleCheckJob.bind(this);
    }
  }

  formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 }
  };

  setModelHide() {
    const { form, dispatch } = this.props;
    form.resetFields();
    dispatch({
      type: "newtrans/hideTrans"
    });
  }

  /*格式化表格*/
  formatTable(obj) {
    let newObj = {};
    for (let index of obj) {
      newObj[index.name] = index.value;
    }
    return newObj;
  }

  handleSure() {
    const { form, dispatch } = this.props;
    const {
      actionName,
      status,
      info_name,
      viewId,
      params,
      copy_name
    } = this.props.newtrans;
    const { owner } = this.props.transheader;
    const { activeArgs } = this.props.jobheader;

    form.validateFields((err, values) => {
      console.log(err);

      if (err) {
        return;
      }
      values.viewId = viewId;

      if (actionName === "showTrans") {
        let sendFields = {};
        if (this.refs.editTable) {
          if (this.refs.editTable.state.dataSource.length > 0) {
            sendFields = this.formatTable(this.refs.editTable.state.dataSource);
          }
        } else {
          if (fields) {
            sendFields = params;
          }
        }
        values.params = sendFields;
      }

      if (status === "trans" && actionName === "newTrans") {
        if (copy_name) {
          values.copy_name = copy_name;
        } else {
          values.copy_name = "";
        }
        dispatch({
          type: "transheader/saveNewTrans",
          payload: { ...values }
        });
        dispatch({
          type: "designplatform/changeStatus",
          payload: {
            status: "trans"
          }
        });
      } else if (status === "trans" && actionName === "showTrans") {
        values.name = info_name;
        values.newname = values.info_name === info_name ? "" : values.info_name;

        getSave_trans_attributes({ ...values, owner }).then(res => {
          const { code } = res.data;
          if (code === "200") {
            message.success("保存成功！");
            if (values.newname) {
              dispatch({
                type: "designplatform/queryTransList",
                payload: {
                  isMap: true
                }
              });
              dispatch({
                type: "transheader/changeName",
                payload: {
                  name: values.name,
                  newname: values.newname
                }
              });
              dispatch({
                type: "transspace/updateData",
                payload: {
                  name: values.newname
                }
              });
            }
          }
        });
      } else if (status === "job" && actionName === "newTrans") {
        if (copy_name) {
          values.copy_name = copy_name;
        } else {
          values.copy_name = "";
        }
        dispatch({
          type: "jobheader/saveNewTrans",
          payload: { ...values }
        });
        dispatch({
          type: "designplatform/changeStatus",
          payload: {
            status: "job"
          }
        });
      } else if (status === "job" && actionName === "showTrans") {
        values.name = info_name;
        values.newname = values.info_name === info_name ? "" : values.info_name;

        getSave_job_attributes({ ...values, owner: activeArgs.get(info_name).owner }).then(res => {
          const { code } = res.data;
          if (code === "200") {
            message.success("保存成功！");
            if (values.newname) {
              dispatch({
                type: "jobheader/changeName",
                payload: {
                  name: values.name,
                  newname: values.newname
                }
              });
              dispatch({
                type: "jobspace/updateData",
                payload: {
                  name: values.newname
                }
              });
            }
          }
        });
      }
      this.setModelHide();
    });
  }

  Columns1 = [
    {
      title: "命名参数",
      dataIndex: "name",
      key: "name",
      width: "45%",
      editable: true
    },
    {
      title: "默认值",
      dataIndex: "value",
      key: "value",
      editable: true
    }
  ];

  dataSource1 = [];

  handleFocus() {
    const { InputData } = this.state;
    let tabel1 = [];
    let count1 = 0;

    for (let index of InputData) {
      tabel1.push({
        key: count1,
        name: index.name,
        type: index.type,
        format: null,
        currencyType: index.currencySymbol,
        decimal: index.decimalSymbol,
        group: index.groupingSymbol,
        nullif: null,
        trimType: index.trimType,
        length: index.length,
        precision: index.precision
      });
      count1++;
    }
  }

  componentWillReceiveProps(nextProps) {
    const { visible } = nextProps.newtrans;
    if (visible === true) {
      this.updateTable(nextProps.newtrans.params);
    }
  }

  updateTable(params) {
    let args = [];
    let i = 0;

    if (this.refs.editTable) {
      if (params) {
        for (let index of Object.keys(params)) {
          args.push({
            key: i++,
            name: index,
            value: params[index]
          });
        }
      }
      this.refs.editTable.updateTable(args, i);
    }
  }

  initFuc(that) {
    const { params } = this.props.newtrans;
    let args = [];
    let i = 0;

    if (params) {
      for (let index of Object.keys(params)) {
        args.push({
          key: i++,
          name: index,
          value: params[index]
        });
      }
    }
    that.updateTable(args, i);
  }

  handleAdd1() {
    const data = {
      name: "",
      value: ""
    };
    this.refs.editTable.handleAdd(data);
  }
  handleDeleteFields1() {
    this.refs.editTable.handleDelete();
  }

  handleSelectChange(name) {
    const { dispatch } = this.props;
    dispatch({
      type: "newtrans/save",
      payload: {
        copy_name: name
      }
    });
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const {
      visible,
      info_name,
      description,
      actionName,
      nameArgs
    } = this.props.newtrans;

    return (
      <Modal
        visible={visible}
        title={this.getText()}
        wrapClassName="vertical-center-modal"
        footer={[
          <Button
            key="submit"
            type="primary"
            size="large"
            onClick={this.handleSure.bind(this)}
          >
            确定
          </Button>,
          <Button
            key="back"
            size="large"
            onClick={this.setModelHide.bind(this)}
          >
            取消
          </Button>
        ]}
        onCancel={this.setModelHide.bind(this)}
      >
        <Form>
          <FormItem label={this.getName()} {...this.formItemLayout}>
            {getFieldDecorator("info_name", {
              initialValue: info_name,
              rules: [
                { whitespace: true, required: true, message: "请输入名称" },
                {
                  validator: this.checkNewName(),
                  message: "名称已存在，请更改!"
                }
              ]
            })(<Input />)}
          </FormItem>
          {actionName === "newTrans" ? (
            <FormItem label={this.getCopyName()} {...this.formItemLayout}>
              {getFieldDecorator("copy_name", {
                initialValue: ""
              })(
                <Select
                  onChange={this.handleSelectChange.bind(this)}
                  allowClear
                >
                  {nameArgs.map(index => (
                    <SelectOption key={index} value={index}>
                      {index}
                    </SelectOption>
                  ))}
                </Select>
              )}
            </FormItem>
          ) : null}
          <FormItem label="描述" {...this.formItemLayout}>
            {getFieldDecorator("description", {
              initialValue: description
            })(<TextArea />)}
          </FormItem>
          {actionName !== "newTrans" ? (
            <div style={{ margin: "0 5%" }}>
              <Row style={{ marginBottom: "5px" }}>
                <Col span={12}>
                  <p style={{ marginLeft: "5px" }}>命名参数：</p>
                </Col>
                <Col span={12}>
                  <ButtonGroup size={"small"} style={{ float: "right" }}>
                    <Button onClick={this.handleAdd1.bind(this)}>
                      添加字段
                    </Button>
                    <Button
                      size={"small"}
                      onClick={this.handleDeleteFields1.bind(this)}
                    >
                      删除字段
                    </Button>
                  </ButtonGroup>
                </Col>
              </Row>
              <EditTable
                initFuc={this.initFuc.bind(this)}
                extendDisabled={true}
                rowSelection={true}
                columns={this.Columns1}
                dataSource={[]}
                tableStyle="editTableStyle5"
                size={"small"}
                scroll={{ y: 240 }}
                ref="editTable"
                count={4}
              />
            </div>
          ) : null}
        </Form>
      </Modal>
    );
  }
}

const TaskConfig = Form.create()(Index);

export default connect(({ newtrans, transheader,jobheader }) => ({
  newtrans,
  transheader,
  jobheader
}))(TaskConfig);
