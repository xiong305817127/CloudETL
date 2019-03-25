import React from "react";
import { connect } from "dva";
import { Form, Select, Button, Input, Checkbox, message } from "antd";
import Modal from "components/Modal.js";

import AceEditor from "react-ace";
import brace from "brace";
import withDatabase from "../../../common/withDatabase";

import "brace/mode/mysql";
import "brace/theme/github";
import "brace/ext/language_tools";

const FormItem = Form.Item;
const { Option } = Select;

class InputDialog extends React.Component {
  constructor(props) {
    super(props);

    const { sql, schemaName, tableName } = props.model.config;

    this.state = {
      hasChange: false,
      textAreaValue:
        decodeURIComponent(sql) != "null" ? decodeURIComponent(sql) : ""
    };
  }

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
      table,
      tableId,
      tableType
    } = this.props.databaseData;
    const { textAreaValue, hasChange } = this.state;

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
        connection: database,
        sql: encodeURIComponent(textAreaValue),
        limit: values.limit,
        executeEachRow: values.executeEachRow,
        variablesActive: values.variablesActive,
        lazyConversionActive: values.lazyConversionActive,
        lookup: values.lookup,
        tableName: hasChange ? "" : table,
        tableId: hasChange ? "" : tableId,
        tableType: hasChange ? "" : tableType,
        schema,
        databaseId,
        schemaId
      };

      saveStep(obj, key, data => {
        if (data.code === "200") {
          this.hideModal();
        }
      });
    });
  };

  formSql(sql, dataType) {
    let str = "";
    if (sql.length === 1) {
      str = sql[0] + " \n";
    } else {
      for (let i = 0; i < sql.length; i++) {
        let field = sql[i];
        if (dataType === "DM7" && /[a-z]/.test(sql[i])) {
          field = `${field}`;
        }
        if (i != sql.length - 1) {
          str += "" + field + ", \n";
        } else {
          str = str + field + " \n";
        }
      }
    }

    return "select " + str + " from";
  }

  getSql(sql, id, tableInfo) {
    const { getFieldList } = this.props;
    getFieldList(id);
    const { table } = tableInfo;
    let name = "";

    const { schema, type } = this.props.databaseData;

    if (type === "POSTGRESQL" || type === "HIVE3") {
      name = table;
    } else if (type === "MYSQL") {
      name = "`" + table + "`";
    } else if (type === "DM7" || type === "ORACLE") {
      name = `${schema}."${table}"`;
    } else {
      name = schema + "." + table;
    }

    if (sql.length === 0 && name === "``") {
      return false;
    }

    if (sql.length === 0 && name != "") {
      this.setState({
        textAreaValue: "select * from " + name
      });
    } else {
      this.setState({
        textAreaValue: this.formSql(sql, type) + " " + name
      });
    }

    this.setState({
      hasChange: false
    });
  }

  onTextAreaChange(newValue) {
    this.setState({
      hasChange: true,
      textAreaValue: newValue
    });
  }

  handleSqlClick() {
    const { dispatch } = this.props;
    const { tableList, type } = this.props.databaseData;

    const { owner } = this.props.model;

    dispatch({
      type: "dbtable/querySchema",
      payload: {
        visible: true,
        tableList,
        dataType: type,
        fuc: this.getSql.bind(this),
        owner
      }
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
    const { getFieldDecorator } = this.props.form;
    const {
      text,
      config,
      visible,
      handleCheckName,
      prevStepNames
    } = this.props.model;
    const {
      databaseList,
      schemaList,
      database,
      schema
    } = this.props.databaseData;
    const { getTableList } = this.props;

    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 }
    };
    const formItemLayout3 = {
      wrapperCol: { span: 20 }
    };
    const formItemLayout4 = {
      labelCol: { span: 6, style: { textAlign: "left" } },
      wrapperCol: { span: 14, style: { textAlign: "right" } }
    };
    const formItemLayout5 = {
      labelCol: { span: 7 },
      wrapperCol: { span: 13 }
    };
    return (
      <Modal
        visible={visible}
        title="表输入"
        wrapClassName="vertical-center-modal"
        style={{ zIndex: 50 }}
        maskClosable={false}
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
          <FormItem label="步骤名称" {...formItemLayout1}>
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
            {...formItemLayout1}
            label="数据库连接"
            style={{ marginBottom: "8px" }}
          >
            {getFieldDecorator("connection", {
              initialValue: database,
              rules: [{ required: true, message: "请选择数据库链接" }]
            })(
              <Select
                placeholder="请选择数据库链接"
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
            style={{ marginBottom: "8px" }}
          >
            {getFieldDecorator("schema", {
              initialValue: schema
            })(
              <Select onChange={getTableList}>
                {schemaList.map(index => (
                  <Select.Option key={index.schemaId} value={index.schemaId}>
                    {index.schema}
                  </Select.Option>
                ))}
              </Select>
            )}
          </FormItem>
          <FormItem
            label="SQL"
            {...formItemLayout4}
            style={{ marginBottom: "0px", marginLeft: "10%" }}
          >
            <Button.Group size="small">
              <Button onClick={this.handleSqlClick.bind(this)}>
                获取SQL查询语句
              </Button>
            </Button.Group>
          </FormItem>
          <div style={{ padding: "8px 25px" }}>
            <AceEditor
              mode="mysql"
              theme="github"
              onChange={this.onTextAreaChange.bind(this)}
              name="gather_tableInput"
              className="autoTextArea"
              showGutter={true}
              width={"100%"}
              height={"300px"}
              fontSize={16}
              editorProps={{ $blockScrolling: true }}
              value={this.state.textAreaValue}
              wrapEnabled={true}
              setOptions={{
                enableBasicAutocompletion: true,
                enableLiveAutocompletion: true,
                enableSnippets: false,
                showLineNumbers: true,
                tabSize: 2
              }}
            />
          </div>

          <FormItem
            {...formItemLayout3}
            style={{ marginBottom: "0px", marginLeft: "10%" }}
          >
            {getFieldDecorator("lazyConversionActive", {
              valuePropName: "checked",
              initialValue: config.lazyConversionActive
            })(<Checkbox>允许简易转换</Checkbox>)}
          </FormItem>
          <FormItem
            {...formItemLayout3}
            style={{ marginBottom: "0px", marginLeft: "10%" }}
          >
            {getFieldDecorator("variablesActive", {
              valuePropName: "checked",
              initialValue: config.variablesActive
            })(<Checkbox>替换SQL里的变量</Checkbox>)}
          </FormItem>
          <FormItem
            label="从步骤插入数据"
            {...formItemLayout5}
            style={{ margin: "5px 0" }}
          >
            {getFieldDecorator("lookup", {
              initialValue: config.lookup
            })(
              <Select placeholder="请选择" allowClear>
                {prevStepNames.map(index => (
                  <Select.Option key={index} value={index}>
                    {index}
                  </Select.Option>
                ))}
              </Select>
            )}
          </FormItem>
          <FormItem
            {...formItemLayout3}
            style={{ margin: "0px", marginLeft: "10%" }}
          >
            {getFieldDecorator("executeEachRow", {
              valuePropName: "checked",
              initialValue: config.executeEachRow
            })(<Checkbox>执行每一行？</Checkbox>)}
          </FormItem>
          <FormItem
            {...formItemLayout5}
            label="记录数量限制"
            style={{ margin: "5px 0" }}
          >
            {getFieldDecorator("limit", {
              initialValue: config.limit
            })(<Input />)}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
const TableInput = Form.create()(InputDialog);

export default connect()(
  withDatabase(TableInput, {
    isRead: true
  })
);
