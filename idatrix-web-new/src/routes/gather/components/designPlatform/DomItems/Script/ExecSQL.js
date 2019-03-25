/**
 * Created by Administrator on 2017/3/13.
 */
import React from "react";
import { Button, Form, Input, Radio, Select, Checkbox, Row, Col } from "antd";
import Modal from "components/Modal.js";
import { connect } from "dva";
import EditTable from "../../../common/EditTable";

import AceEditor from "react-ace";
import brace from "brace";

import "brace/mode/mysql";
import "brace/theme/github";
import "brace/ext/language_tools";
import withDatabase from "../../../common/withDatabase";

const { TextArea } = Input;
const FormItem = Form.Item;
const ButtonGroup = Button.Group;

class Exec extends React.Component {
  constructor(props) {
    super(props);
    const { config } = props.model;
    let tableFields = [];
    let count = 0;

    if (config.arguments) {
      for (let index of config.arguments) {
        tableFields.push({
          key: count,
          name: index.name
        });
        count++;
      }
    }
    this.state = {
      InputData: [],
      input_fields: tableFields,
      textAreaValue:
        decodeURIComponent(config.sql) != "null"
          ? decodeURIComponent(config.sql)
          : ""
    };
  }

  componentDidMount() {
    this.Request();
  }

  Request() {
    const {
      getInputFields,
      transname,
      text,
      getInputSelect
    } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj, data => {
      this.setState({
        InputData: data
      });
      let options = getInputSelect(data, "name");
      this.refs.editTable.updateOptions({
        name: options
      });
    });
  }

  formItemLayout5 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 }
  };
  formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 }
  };
  formItemLayout1 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 8 }
  };
  formItemLayout2 = {
    wrapperCol: { span: 20 }
  };
  formItemLayout6 = {
    labelCol: { span: 9 },
    wrapperCol: { span: 12 }
  };
  formItemLayout3 = {
    labelCol: { span: 6, style: { textAlign: "left" } },
    wrapperCol: { span: 14, style: { textAlign: "right" } }
  };
  formItemLayout4 = {
    labelCol: { span: 7 },
    wrapperCol: { span: 13 }
  };

  setModelHide() {
    const { dispatch } = this.props;
    dispatch({
      type: "items/hide",
      visible: false
    });
  }

  handleFormSubmit() {
    const { form } = this.props;
    const {
      panel,
      transname,
      description,
      key,
      saveStep,
      text,
      config,
      formatTable
		} = this.props.model;
				
    const {
			schemaId,
			schema,
			databaseId,
			database
		} = this.props.databaseData;

    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      if (this.refs.editTable) {
        if (this.refs.editTable.state.dataSource.length > 0) {
          let arg = ["name"];
          sendFields = formatTable(this.refs.editTable.state.dataSource, arg);
        }
      } else {
        if (config.arguments) {
          sendFields = config.arguments;
        }
      }

      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = text === values.text ? "" : values.text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        ...values,
        connection: database,
        sql: encodeURIComponent(this.state.textAreaValue),
        insertField: values.insertField,
        updateField: values.updateField,
        deleteField: values.deleteField,
        readField: values.deleteField,
				arguments: sendFields,
				schemaId,
				schema,
				databaseId,
      };

      saveStep(obj, key, data => {
        if (data.code === "200") {
          this.setModelHide();
        }
      });
    });
  }

  handleAdd = () => {
    const data = {
      name: ""
    };
    this.refs.editTable.handleAdd(data);
  };
  handleDeleteFields = () => {
    this.refs.editTable.handleDelete();
  };

  getInputFields() {
    const { InputData } = this.state;
    let args = [];
    let count = 0;
    for (let index of InputData) {
      args.push({
        key: count,
        name: index.name
      });
      count++;
    }
    this.refs.editTable.updateTable(args, count);
  }

  columns = [
    {
      title: "作为参数的字段",
      dataIndex: "name",
      key: "name",
      selectable: true
    }
  ];

  onTextAreaChange(newValue) {
    this.setState({
      textAreaValue: newValue
    });
	}
	
	//选择数据库链接
	getSchemaList(id){
		if(id === undefined) return;
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

		
		console.log(this.props.databaseData);

		const {
			schema,
			database,
			databaseList,
      schemaList,
		} = this.props.databaseData;


		const { getTableList } = this.props;

    const setDisabled = () => {
      if (getFieldValue("executedEachInputRow") === undefined) {
        return config.executedEachInputRow;
      } else {
        if (getFieldValue("executedEachInputRow")) {
          return getFieldValue("executedEachInputRow");
        } else {
          return false;
        }
      }
    };

    return (
      <Modal
        visible={visible}
        title="执行SQL脚本"
        wrapClassName="vertical-center-modal"
        style={{ zIndex: 50 }}
        maskClosable={false}
        footer={[
          <Button
            key="submit"
            type="primary"
            size="large"
            onClick={this.handleFormSubmit.bind(this)}
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
          <FormItem label="步骤名称" {...this.formItemLayout}>
            {getFieldDecorator("text", {
              initialValue: text,
              rules: [
                {
                  whitespace: true,
                  required: true,
                  message: "请输入步骤名称",
                  validator: handleCheckName,
                  message: "步骤名称已存在，请更改!"
                }
              ]
            })(<Input />)}
          </FormItem>
          <FormItem
            {...this.formItemLayout5}
            label="数据库连接"
            style={{ marginBottom: "8px" }}
          >
            {getFieldDecorator("connection", {
              initialValue: database,
              rules: [{ required: true, message: "请选择数据库链接" }]
            })(
              <Select placeholder="请选择数据库链接" 
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
            label="模式名称"
            style={{ marginBottom: "8px" }}
            {...this.formItemLayout5}
          >
            {getFieldDecorator("schema", {
              initialValue: schema,
              rules: [{ required: true, message: "请选择模式名称" }]
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
            {...this.formItemLayout2}
            label="要执行的SQL脚本（用 ; 号分隔语句，问号将被参数替换）"
            style={{ marginBottom: "8px", display: "none", marginLeft: "10%" }}
          >
            {getFieldDecorator("sql", {
              initialValue:
                decodeURIComponent(config.sql) != "null"
                  ? decodeURIComponent(config.sql)
                  : ""
            })(<TextArea />)}
          </FormItem>
          <p style={{ marginLeft: "8%" }}>
            要执行的SQL脚本（用 ; 号分隔语句，问号将被参数替换）
          </p>
          <div style={{ padding: "8px 30px" }}>
            <AceEditor
              mode="mysql"
              theme="github"
              onChange={this.onTextAreaChange.bind(this)}
              name="gather_tableInput"
              className="autoTextArea"
              showGutter={true}
              width={"100%"}
              height={"240px"}
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
          <Row style={{ marginLeft: "10%", marginBottom: "0px" }}>
            <Col span={7}>
              <FormItem
                style={{ marginBottom: "0px" }}
                {...this.formItemLayout}
              >
                {getFieldDecorator("executedEachInputRow", {
                  valuePropName: "checked",
                  initialValue: config.executedEachInputRow
                })(<Checkbox>执行每一行？</Checkbox>)}
              </FormItem>
            </Col>
            <Col span={9}>
              <FormItem
                style={{ marginBottom: "0px" }}
                {...this.formItemLayout}
              >
                {getFieldDecorator("singleStatement", {
                  valuePropName: "checked",
                  initialValue: config.singleStatement
                })(<Checkbox>作为一个语句执行</Checkbox>)}
              </FormItem>
            </Col>
            <Col span={6}>
              <FormItem
                style={{ marginBottom: "0px" }}
                {...this.formItemLayout}
              >
                {getFieldDecorator("replaceVariables", {
                  valuePropName: "checked",
                  initialValue: config.replaceVariables
                })(<Checkbox>交量替换</Checkbox>)}
              </FormItem>
            </Col>
          </Row>
          <Row style={{ marginLeft: "10%", marginBottom: "0px" }}>
            <Col span={7}>
              <FormItem
                style={{ marginBottom: "0px" }}
                {...this.formItemLayout}
              >
                {getFieldDecorator("setParams", {
                  valuePropName: "checked",
                  initialValue: config.setParams
                })(<Checkbox disabled={!setDisabled()}>绑定参数</Checkbox>)}
              </FormItem>
            </Col>
            <Col span={9}>
              <FormItem
                style={{ marginBottom: "0px" }}
                {...this.formItemLayout}
              >
                {getFieldDecorator("quoteString", {
                  valuePropName: "checked",
                  initialValue: config.quoteString
                })(<Checkbox disabled={!setDisabled()}>引用字符串</Checkbox>)}
              </FormItem>
            </Col>
          </Row>
          <div style={{ margin: "0px 30px 15px 30px" }}>
            <Row style={{ margin: "5px 0", width: "100%" }}>
              <Col span={12}>
                <ButtonGroup size={"small"}>
                  <Button
                    disabled={!setDisabled()}
                    onClick={this.handleAdd.bind(this)}
                  >
                    添加字段
                  </Button>
                  <Button
                    disabled={!setDisabled()}
                    onClick={this.handleDeleteFields.bind(this)}
                  >
                    删除字段
                  </Button>
                </ButtonGroup>
              </Col>
              <Col span={12} style={{ textAlign: "right" }}>
                <Button
                  disabled={!setDisabled()}
                  size={"small"}
                  onClick={this.getInputFields.bind(this)}
                >
                  获取字段
                </Button>
              </Col>
            </Row>
            <EditTable
              disabled={!setDisabled()}
              columns={this.columns}
              dataSource={this.state.input_fields}
              rowSelection={true}
              tableStyle="editTableStyle5"
              size={"small"}
              scroll={{ y: 140 }}
              ref="editTable"
              count={4}
            />
          </div>

          <FormItem
            label="包含插入状态的字段"
            {...this.formItemLayout6}
            style={{ marginBottom: "8px" }}
          >
            {getFieldDecorator("insertField", {
              initialValue: config.insertField
            })(<Input />)}
          </FormItem>
          <FormItem
            label="包含更新状态的字段"
            {...this.formItemLayout6}
            style={{ marginBottom: "8px" }}
          >
            {getFieldDecorator("updateField", {
              initialValue: config.updateField
            })(<Input />)}
          </FormItem>
          <FormItem
            label="包含读状态的字段"
            {...this.formItemLayout6}
            style={{ marginBottom: "8px" }}
          >
            {getFieldDecorator("readField", {
              initialValue: config.readField
            })(<Input />)}
          </FormItem>
          <FormItem
            label="包含删除状态的字段"
            {...this.formItemLayout6}
            style={{ marginBottom: "8px" }}
          >
            {getFieldDecorator("deleteField", {
              initialValue: config.deleteField
            })(<Input />)}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}

const ExecSQL = Form.create()(Exec);

export default connect()(withDatabase(ExecSQL));
