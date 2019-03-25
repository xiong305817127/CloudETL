import React from "react";
import { connect } from "dva";
import { Form, Select, Button, Input, Checkbox, Card } from "antd";
import Modal from "components/Modal.js";
import witdDatabase from "../../../common/withDatabase";

const FormItem = Form.Item;

class SequenceDialog extends React.Component {
  hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type: "items/hide",
      visible: false
    });
  };

  handleCreate = () => {
    const form = this.props.form;
    const {
      panel,
      transname,
      description,
      key,
      saveStep,
      text
		} = this.props.model;
		const {
			schemaId,
			schema,
			databaseId,
			database,
		} = this.props.databaseData;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let obj = {};

      obj.transname = transname;
      obj.newname = text === values.text ? "" : values.text;
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
				...values,
				connection:database,
				databaseId,
				schemaName:schema,
				schemaId
      };

      saveStep(obj, key, data => {
        if (data.code === "200") {
          this.hideModal();
        }
      });
    });
  };

  onValueChange(e) {
    const { setFieldsValue } = this.props.form;
    setFieldsValue({
      useCounter: !e.target.checked
    });
  }

  onValueChange1(e) {
    const { setFieldsValue } = this.props.form;
    setFieldsValue({
      useDatabase: !e.target.checked
    });
  }

  //增加数据库调用高阶组件方法
  getSchemaList(id) {
    if (id === undefined) return;
    const { setFieldsValue } = this.props.form;
    const { getSchemaList } = this.props;

    setFieldsValue({
      schema: ""
    });

    //调用高阶组件的通用方法
    getSchemaList(id);
  }

  render() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { text, config, visible, handleCheckName } = this.props.model;
    const {
      databaseList,
      schemaList,
      database,
      schema
    } = this.props.databaseData;
    const { getTableList } = this.props;

    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 15 }
    };

    const setDisabled = () => {
      if (getFieldValue("useDatabase") === undefined) {
        return !config.useDatabase;
      } else {
        if (getFieldValue("useDatabase")) {
          return !getFieldValue("useDatabase");
        } else {
          return true;
        }
      }
    };

    return (
      <Modal
        visible={visible}
        title="增加序列"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={650}
        footer={[
          <Button
            key="submit"
            type="primary"
            size="large"
            onClick={this.handleCreate}
          >
            确定
          </Button>,
          <Button key="back" size="large" onClick={this.hideModal}>
            取消
          </Button>
        ]}
        onCancel={this.hideModal}
      >
        <Form>
          <FormItem
            label="步骤名称"
            style={{ marginBottom: "8px" }}
            {...formItemLayout1}
          >
            {getFieldDecorator("text", {
              initialValue: text,
              rules: [
                { whitespace: true, required: true, message: "请输入步骤名称" },
                {
                  validator: handleCheckName,
                  message: "步骤名称已存在，请更改!"
                }
              ]
            })(<Input />)}
          </FormItem>
          <FormItem
            label="值的名称"
            style={{ marginBottom: "8px" }}
            {...formItemLayout1}
          >
            {getFieldDecorator("valuename", {
              initialValue: config.valuename
            })(<Input />)}
          </FormItem>
          <div style={{ margin: "0 5%" }}>
            <Card title="使用数据库来生成序列">
              <FormItem
                style={{ marginBottom: "8px", marginLeft: "10%" }}
                {...formItemLayout1}
              >
                {getFieldDecorator("useDatabase", {
                  valuePropName: "checked",
                  initialValue: config.useDatabase,
                  onChange: this.onValueChange.bind(this)
                })(<Checkbox>使用DB来获取sequence？</Checkbox>)}
              </FormItem>

              <FormItem
                {...formItemLayout1}
                label="数据库连接"
                hasFeedback
                style={{ marginBottom: "8px" }}
              >
                {getFieldDecorator("connection", {
                  initialValue: database
                })(
                  <Select
                    disabled={setDisabled()}
                    onChange={this.getSchemaList.bind(this)}
                  >
                    {databaseList.map(index => (
                      <Option key={index.id} value={index.id}>
                        {index.name}
                      </Option>
                    ))}
                  </Select>
                )}
              </FormItem>
              <FormItem
                {...formItemLayout1}
                label="模式名称"
                hasFeedback
                style={{ marginBottom: "8px" }}
              >
                {getFieldDecorator("schemaName", {
                  initialValue: schema
                })(
                  <Select
                    disabled={setDisabled()}
                    onChange={getTableList}
                    showSearch
                    optionFilterProp="children"
                    filterOption={(input, option) =>
                      option.props.value
                        .toLowerCase()
                        .indexOf(input.toLowerCase()) >= 0
                    }
                  >
                    {schemaList.map(index => (
                      <Select.Option
                        key={index.schemaId}
                        value={index.schemaId}
                      >
                        {index.schema}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </FormItem>
              <FormItem
                {...formItemLayout1}
                label="Sequence名称"
                hasFeedback
                style={{ marginBottom: "8px" }}
              >
                {getFieldDecorator("sequenceName", {
                  initialValue: config.sequenceName
                })(<Input disabled={setDisabled()} />)}
              </FormItem>
            </Card>
            <Card title="使用转换计算器来生成序列">
              <FormItem
                style={{ marginBottom: "8px", marginLeft: "10%" }}
                {...formItemLayout1}
              >
                {getFieldDecorator("useCounter", {
                  valuePropName: "checked",
                  initialValue: config.useCounter,
                  onChange: this.onValueChange1.bind(this)
                })(<Checkbox>使用计数器来计算sequence？</Checkbox>)}
              </FormItem>

              <FormItem
                {...formItemLayout1}
                label="计数器名称(可选)"
                hasFeedback
                style={{ marginBottom: "8px" }}
              >
                {getFieldDecorator("counterName", {
                  initialValue: config.counterName
                })(<Input disabled={!setDisabled()} />)}
              </FormItem>
              <FormItem
                {...formItemLayout1}
                label="起始值"
                hasFeedback
                style={{ marginBottom: "8px" }}
              >
                {getFieldDecorator("startAt", {
                  initialValue: config.startAt
                })(<Input disabled={!setDisabled()} />)}
              </FormItem>
              <FormItem
                {...formItemLayout1}
                label="增长根据"
                hasFeedback
                style={{ marginBottom: "8px" }}
              >
                {getFieldDecorator("incrementBy", {
                  initialValue: config.incrementBy
                })(<Input disabled={!setDisabled()} />)}
              </FormItem>
              <FormItem
                {...formItemLayout1}
                label="最大值"
                hasFeedback
                style={{ marginBottom: "8px" }}
              >
                {getFieldDecorator("maxValue", {
                  initialValue: config.maxValue
                })(<Input disabled={!setDisabled()} />)}
              </FormItem>
            </Card>
          </div>
        </Form>
      </Modal>
    );
  }
}
const Sequence = Form.create()(SequenceDialog);

export default connect()(witdDatabase(Sequence));
