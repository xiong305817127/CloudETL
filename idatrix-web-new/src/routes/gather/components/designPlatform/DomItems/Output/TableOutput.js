import React from "react";
import { connect } from "dva";
import {
  Button,
  Form,
  Input,
  Radio,
  Select,
  Tabs,
  Checkbox,
  Row,
  Col
} from "antd";
import Modal from "components/Modal.js";
import EditTable from "../../../common/EditTable";
import withDatabase from "../../../common/withDatabase";

const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const ButtonGroup = Button.Group;
const { Option, OptGroup } = Select;

class OutputDialog extends React.Component {
  constructor(props) {
    super(props);
    const { visible } = props.model;
    if (visible === true) {
      const { fields } = props.model.config;
      let data = [];
      let count = 0;
      if (fields) {
        for (let index of fields) {
          data.push({
            key: count,
            ...index
          });
          count++;
        }
      }
      this.state = {
        db_list: [],
        db_model: [],
        db_table: [],
        InputData: [],
        fieldsList: [],
        db_fields: data
      };
    }
  }

  componentDidMount() {
    this.Request();
  }

  Request() {
    const {
      config,
      getInputFields,
      transname,
      text,
      getInputSelect
    } = this.props.model;
    const { tableId } = config;

    if (tableId !== 0 && tableId) {
      this.handleGetFields(tableId, "one");
    }
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj, data => {
      this.setState({
        InputData: data
      });
      if (this.refs.editTable) {
        let options = getInputSelect(data, "name");
        this.refs.editTable.updateOptions({
          streamName: options
        });
      }
    });
  }

  getSchemaList(id) {
    if (id === undefined) return;
    const { setFieldsValue } = this.props.form;
    const { getSchemaList } = this.props;
    setFieldsValue({
      schema: "",
      table: ""
    });

    //调用高阶组件的通用方法
    getSchemaList(id);
  }

  getTableList(id) {
    if (id === undefined) return;
    const { setFieldsValue } = this.props.form;
    const { getTableList } = this.props;
    setFieldsValue({
      schema: "",
      table: ""
    });

    //调用高阶组件的通用方法
    getTableList(id);
  }

  handleGetFields(id, type) {
    if (id === undefined) return;
    const { getDbFields, getInputSelect } = this.props.model;
    const { getFieldList } = this.props;
    const { owner } = this.props.model;

    if (type !== "one") {
      getFieldList(id);
    }

    getDbFields(
      {
        id,
        owner
      },
      data => {
        this.setState({ fieldsList: data });
        if (this.refs.editTable) {
          let options = getInputSelect(data, "name");
          this.refs.editTable.updateOptions({
            columnName: options
          });
        }
      }
    );
  }

  initFuc(that) {
    const { getInputSelect } = this.props.model;
    const { InputData, fieldsList } = this.state;
    let options = getInputSelect(InputData, "name");
    let options1 = getInputSelect(fieldsList, "name");
    that.updateOptions({
      columnName: options1,
      streamName: options
    });
  }

  hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type: "items/hide",
      visible: false
    });
  };

  handleCreate = () => {
    const {
      panel,
      description,
      transname,
      key,
      saveStep,
      text,
      config,
      formatTable
    } = this.props.model;
    const { fields } = config;
    const form = this.props.form;
    const {
      database,
      schemaId,
      schema,
      databaseId,
      table,
      tableId,
      tableType
    } = this.props.databaseData;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      if (this.refs.editTable) {
        if (this.refs.editTable.state.dataSource.length > 0) {
          let arg = ["columnName", "streamName"];
          sendFields = formatTable(this.refs.editTable.state.dataSource, arg);
        }
      } else {
        if (fields) {
          sendFields = fields;
        }
      }

      if (values.selectData === "partitioningDaily") {
        values.partitioningDaily = true;
        values.partitioningMonthly = false;
      } else {
        values.partitioningDaily = false;
        values.partitioningMonthly = true;
      }
      let obj = {};
      obj.transname = transname;
      obj.newname = text === values.text ? "" : values.text;
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        connection: database,
        schemaId,
        schema,
        databaseId,
        table,
        tableId,
        tableType,
        commit: values.commit,
        truncate: values.truncate,
        ignoreErrors: values.ignoreErrors,
        useBatch: values.useBatch,
        specifyFields: values.specifyFields,
        partitioningEnabled: values.partitioningEnabled,
        partitioningField: values.partitioningField,
        partitioningDaily: values.partitioningDaily,
        partitioningMonthly: values.partitioningMonthly,
        tablenameInField: values.tablenameInField,
        tablenameField: values.tablenameField,
        tablenameInTable: values.tablenameInTable,
        returnKeys: values.returnKeys,
        returnField: values.returnField,
        fields: sendFields
      };

      saveStep(obj, key, data => {
        if (data.code === "200") {
          this.hideModal();
        }
      });
    });
  };

  handleAdd = () => {
    const data = {
      tableIndex: "",
      flowIndex: ""
    };
    this.refs.editTable.handleAdd(data);
  };

  handleDeleteFields = () => {
    this.refs.editTable.handleDelete();
  };

  getFields() {
    const { fieldsList } = this.state;
    const { get_Similarity } = this.props.model;
    let args = [];
    let count = 0;
    const { InputData } = this.state;

    for (let index of fieldsList) {
      args.push({
        key: count,
        columnName: index.name,
        streamName: ""
      });
      count++;
    }

    let sameArgs = get_Similarity(args, InputData, "columnName", "streamName");
    this.refs.editTable.updateTable(sameArgs, count);
  }

  render() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { text, config, visible, handleCheckName } = this.props.model;
    const {
      databaseList,
      schemaList,
      tableList,
      database,
      schema,
      table
    } = this.props.databaseData;

    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 }
    };
    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 11 }
    };
    const formItemLayout2 = {
      wrapperCol: { span: 20 }
    };
    const formItemLayout4 = {
      labelCol: { span: 8 },
      wrapperCol: { span: 16 }
    };
    const formItemLayout6 = {
      labelCol: { span: 13 },
      wrapperCol: { span: 11 }
    };

    const setDisabled = () => {
      if (getFieldValue("partitioningEnabled") === undefined) {
        return config.partitioningEnabled;
      } else {
        if (getFieldValue("partitioningEnabled")) {
          return getFieldValue("partitioningEnabled");
        } else {
          return false;
        }
      }
    };

    const setDisabled1 = () => {
      if (getFieldValue("useBatch") === undefined) {
        return config.useBatch;
      } else {
        if (getFieldValue("useBatch")) {
          return getFieldValue("useBatch");
        } else {
          return false;
        }
      }
    };

    const setDisabled2 = () => {
      if (getFieldValue("tablenameInField") === undefined) {
        return config.tablenameInField;
      } else {
        if (getFieldValue("tablenameInField")) {
          return getFieldValue("tablenameInField");
        } else {
          return false;
        }
      }
    };

    const setDisabled3 = () => {
      if (getFieldValue("returnKeys") === undefined) {
        return config.returnKeys;
      } else {
        if (getFieldValue("returnKeys")) {
          return getFieldValue("returnKeys");
        } else {
          return false;
        }
      }
    };

    const setDisabled4 = () => {
      if (getFieldValue("specifyFields") === undefined) {
        return config.specifyFields;
      } else {
        if (getFieldValue("specifyFields")) {
          return getFieldValue("specifyFields");
        } else {
          return false;
        }
      }
    };

    if (this.refs.editTable && this.refs.editTable.state) {
      console.log(this.refs.editTable.updater.isMounted);

      this.refs.editTable.setState({
        disabled: !setDisabled4()
      });
    }

    const columns = [
      {
        title: "表字段",
        dataIndex: "columnName",
        key: "columnName",
        width: "46%",
        selectable: true
      },
      {
        title: "流字段",
        dataIndex: "streamName",
        key: "streamName",
        width: "46%",
        selectable: true
      }
    ];

    return (
      <Modal
        visible={visible}
        title="表输出"
        wrapClassName="vertical-center-modal"
        okText="Create"
        width={650}
        maskClosable={false}
        onCancel={this.hideModal}
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
      >
        <Form>
          <FormItem
            label="步骤名称"
            {...formItemLayout}
            style={{ marginBottom: "8px" }}
          >
            {getFieldDecorator("text", {
              initialValue: text,
              rules: [
                {
                  whitespace: true,
                  whitespace: true,
                  required: true,
                  message: "请输入步骤名称"
                },
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
            hasFeedback
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
            <div>
              {getFieldDecorator("schema", {
                initialValue: schema
              })(
                <Select onChange={this.getTableList.bind(this)} allowClear>
                  {schemaList.map(index => (
                    <Select.Option key={index.schemaId} value={index.schemaId}>
                      {index.schema}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </div>
          </FormItem>
          <FormItem
            {...formItemLayout1}
            label="目标表"
            style={{ marginBottom: "8px" }}
          >
            <div>
              {getFieldDecorator("table", {
                initialValue: table
              })(
                <Select
                  onChange={this.handleGetFields.bind(this)}
                  disabled={setDisabled2()}
                >
                  {tableList.map((index, key) => (
                    <OptGroup key={key} label={key === 0 ? "表" : "视图"}>
                      {index.map(index => (
                        <Option key={index.id} value={index.id}>
                          {index.name}
                        </Option>
                      ))}
                    </OptGroup>
                  ))}
                </Select>
              )}
            </div>
          </FormItem>
          <FormItem
            {...formItemLayout1}
            label="提交记录数量"
            hasFeedback
            style={{ marginBottom: "8px" }}
          >
            {getFieldDecorator("commit", {
              initialValue: config.commit,
              rules: [
                {
                  whitespace: true,
                  required: true,
                  message: "提交记录数量不能为空!"
                },
                { pattern: /^(^[0-9]*$)$/, message: "请输入数字!" }
              ]
            })(<Input />)}
          </FormItem>
          <Row>
            <Col span={6}>
              <FormItem
                style={{ marginBottom: "0px", marginLeft: "65px" }}
                {...this.formItemLayout4}
              >
                {getFieldDecorator("truncate", {
                  valuePropName: "checked",
                  initialValue: config.truncate
                })(
                  <Checkbox disabled={setDisabled() || setDisabled2()}>
                    裁剪表
                  </Checkbox>
                )}
              </FormItem>
            </Col>
            <Col span={6}>
              <FormItem
                style={{ marginBottom: "0px", marginLeft: "40px" }}
                {...this.formItemLayout4}
              >
                {getFieldDecorator("ignoreErrors", {
                  valuePropName: "checked",
                  initialValue: config.ignoreErrors
                })(<Checkbox disabled={setDisabled1()}>忽略插入错误</Checkbox>)}
              </FormItem>
            </Col>
            <Col span={6}>
              <FormItem
                style={{ marginBottom: "0px", marginLeft: "40px" }}
                {...this.formItemLayout4}
              >
                {getFieldDecorator("specifyFields", {
                  valuePropName: "checked",
                  initialValue: config.specifyFields
                })(<Checkbox>指定数据库字段</Checkbox>)}
              </FormItem>
            </Col>
          </Row>
          <Tabs tabPosition={"right"}>
            <TabPane tab="主选项" key="1">
              <FormItem
                {...formItemLayout2}
                style={{ marginBottom: "0px", marginLeft: "13.5%" }}
              >
                {getFieldDecorator("partitioningEnabled", {
                  valuePropName: "checked",
                  initialValue: config.partitioningEnabled
                })(<Checkbox>表分区数据</Checkbox>)}
              </FormItem>
              <FormItem
                {...formItemLayout4}
                label="分区字段"
                style={{ marginBottom: "8px", marginLeft: "6%" }}
              >
                {getFieldDecorator("partitioningField", {
                  initialValue: config.partitioningField
                })(<Input disabled={!setDisabled()} />)}
              </FormItem>
              <FormItem
                {...formItemLayout2}
                style={{ marginBottom: "0px", marginLeft: "20%" }}
              >
                {getFieldDecorator("selectData", {
                  initialValue: config.partitioningMonthly
                    ? "partitioningMonthly"
                    : "partitioningDaily"
                })(
                  <Radio.Group disabled={!setDisabled()}>
                    <Radio value="partitioningMonthly">每个月分区数据</Radio>
                    <Radio value="partitioningDaily">每天分区数据</Radio>
                  </Radio.Group>
                )}
              </FormItem>
              <FormItem
                {...formItemLayout2}
                style={{ marginBottom: "0px", marginLeft: "13.5%" }}
              >
                {getFieldDecorator("useBatch", {
                  valuePropName: "checked",
                  initialValue: config.useBatch
                })(<Checkbox disabled={setDisabled3()}>使用批量插入</Checkbox>)}
              </FormItem>
              <FormItem
                {...formItemLayout2}
                style={{ marginBottom: "0px", marginLeft: "13.5%" }}
              >
                {getFieldDecorator("tablenameInField", {
                  valuePropName: "checked",
                  initialValue: config.tablenameInField
                })(<Checkbox>表名定义在一个字段里？</Checkbox>)}
              </FormItem>
              <FormItem
                label="包含表名的字段"
                {...formItemLayout4}
                style={{ marginBottom: "8px", marginLeft: "6%" }}
              >
                {getFieldDecorator("tablenameField", {
                  initialValue: config.tablenameField
                })(
                  <Select disabled={!setDisabled2()}>
                    {this.state.InputData.map(index => (
                      <Select.Option key={index.name} value={index.name}>
                        {index.name}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </FormItem>
              <FormItem
                {...formItemLayout2}
                style={{ marginBottom: "0px", marginLeft: "13.5%" }}
              >
                {getFieldDecorator("tablenameInTable", {
                  valuePropName: "checked",
                  initialValue: config.tablenameInTable
                })(
                  <Checkbox disabled={!setDisabled2()}>储存表名字段</Checkbox>
                )}
              </FormItem>
              <FormItem
                {...formItemLayout2}
                style={{ marginBottom: "0px", marginLeft: "13.5%" }}
              >
                {getFieldDecorator("returnKeys", {
                  valuePropName: "checked",
                  initialValue: config.returnKeys
                })(<Checkbox>返回一个自动产生的关键字</Checkbox>)}
              </FormItem>
              <FormItem
                label="自动产生的关键字的字段名称"
                {...formItemLayout6}
                style={{ marginBottom: "8px", marginLeft: "6%" }}
              >
                {getFieldDecorator("returnField", {
                  initialValue: config.returnField
                })(<Input disabled={!setDisabled3()} />)}
              </FormItem>
            </TabPane>
            <TabPane tab="数据库字段" key="2">
              <Row style={{ margin: "5px 0", width: "100%" }}>
                <Col span={12}>
                  <ButtonGroup size={"small"} style={{ marginLeft: "5%" }}>
                    <Button disabled={!setDisabled4()} onClick={this.handleAdd}>
                      添加字段
                    </Button>
                    <Button
                      disabled={!setDisabled4()}
                      onClick={this.handleDeleteFields.bind(this)}
                    >
                      删除字段
                    </Button>
                  </ButtonGroup>
                </Col>
                <Col span={12} style={{ textAlign: "right" }}>
                  <Button
                    disabled={!setDisabled4()}
                    size={"small"}
                    onClick={this.getFields.bind(this)}
                  >
                    获取字段
                  </Button>
                </Col>
              </Row>
              <div style={{ marginLeft: "10px" }}>
                <EditTable
                  rowSelection={true}
                  initFuc={this.initFuc.bind(this)}
                  columns={columns}
                  disabled={!setDisabled4()}
                  dataSource={this.state.db_fields}
                  tableStyle="editTableStyle5"
                  size={"small"}
                  scroll={{ y: 140 }}
                  ref="editTable"
                  count={4}
                />
              </div>
            </TabPane>
          </Tabs>
        </Form>
      </Modal>
    );
  }
}
const TableOutput = Form.create()(OutputDialog);

export default connect()(
  withDatabase(TableOutput, {
    isRead: false
  })
);
