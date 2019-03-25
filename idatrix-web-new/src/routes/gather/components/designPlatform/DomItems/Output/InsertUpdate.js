import React from "react";
import { connect } from "dva";
import Modal from "components/Modal.js";
import { Button, Form, Input, Select, Checkbox, Row, Col } from "antd";
import EditTable from "../../../common/EditTable";
import withDatabase from "../../../common/withDatabase";

const FormItem = Form.Item;
const ButtonGroup = Button.Group;
const { Option, OptGroup } = Select;

class UpdateDialog extends React.Component {
  constructor(props) {
    super(props);
    const { visible } = props.model;
    if (visible === true) {
      const { searchFields, updateFields } = props.model.config;
      let data = [];
      let data1 = [];
      if (searchFields) {
        let count = 0;
        for (let index of searchFields) {
          data.push({
            key: count,
            ...index
          });
          count++;
        }
      }
      if (updateFields) {
        let count1 = 0;
        for (let index of updateFields) {
          count1++;
          data1.push({
            key: count1,
            ...index
          });
        }
      }
      this.state = {
        fieldsList: [],
        searchFields: data,
        updateFields: data1,

        InputData: []
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
      let options = getInputSelect(data, "name");
      this.refs.searchTable.updateOptions({
        keyStream1: options,
        keyStream2: options
      });

      this.refs.editTable.updateOptions({
        updateStream: options
      });
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
    const { searchFields, updateFields } = config;
    const form = this.props.form;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      let sendFields1 = [];
      if (this.refs.searchTable) {
        if (this.refs.searchTable.state.dataSource.length > 0) {
          let arg = ["keyLookup", "keyCondition", "keyStream1", "keyStream2"];
          sendFields = formatTable(this.refs.searchTable.state.dataSource, arg);
        }
      } else {
        if (searchFields) {
          sendFields = searchFields;
        }
      }

      if (this.refs.editTable) {
        if (this.refs.editTable.state.dataSource.length > 0) {
          let arg = ["updateLookup", "updateStream", "update"];
          sendFields1 = formatTable(this.refs.editTable.state.dataSource, arg);
        }
      } else {
        if (updateFields) {
          sendFields1 = updateFields;
        }
			}
					
			const {
				schemaId,
				schema,
				databaseId,
				database,
				table,
				tableId,
				tableType
			} = this.props.databaseData;

      let obj = {};
      obj.transname = transname;
      obj.newname = text === values.text ? "" : values.text;
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        connection: database,
        schema,databaseId,
        table,schemaId,tableId,
				tableType,
        commit: values.commit,
        updateBypassed: values.updateBypassed,
        searchFields: sendFields,
        updateFields: sendFields1
      };
      saveStep(obj, key, data => {
        if (data.code === "200") {
          this.hideModal();
        }
      });
    });
  };

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
      table: ""
    });

    //调用高阶组件的通用方法
    getTableList(id);
  }

  handleGetFields(id, type) {
		if (id === undefined) return;
		
		const { owner } = this.props.model;
		const { getDbFields, getInputSelect } = this.props.model;
		const { getFieldList } = this.props;

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
        let options = getInputSelect(data, "name");
        this.refs.searchTable.updateOptions({
          keyLookup: options
        });
        this.refs.editTable.updateOptions({
          updateLookup: options
        });
      }
    );
  }

  getInputFieldsMap(name) {
    const { fieldsList, InputData } = this.state;
    const { get_Similarity } = this.props.model;
    let args = [];
    let count = 0;
    if (name === "searchTable") {
      for (let index of fieldsList) {
        args.push({
          key: count,
          keyLookup: index.name,
          keyCondition: "=",
          keyStream1: "",
          keyStream2: ""
        });
        count++;
      }

      let sameArgs = get_Similarity(args, InputData, "keyLookup", "keyStream1");
      this.refs.searchTable.updateTable(sameArgs, count);
    } else {
      for (let index of fieldsList) {
        args.push({
          key: count,
          updateLookup: index.name,
          updateStream: "",
          update: "true"
        });
        count++;
      }
      let sameArgs = get_Similarity(
        args,
        InputData,
        "updateLookup",
        "updateStream"
      );
      this.refs.editTable.updateTable(sameArgs, count);
    }
  }

  updateColumns = [
    {
      title: "表字段",
      dataIndex: "updateLookup",
      key: "updateLookup",
      width: "40%",
      selectable: true
    },
    {
      title: "流字段",
      dataIndex: "updateStream",
      key: "updateStream",
      width: "40%",
      selectable: true
    },
    {
      title: "更新",
      dataIndex: "update",
      key: "update",
      selectable: true,
      selectArgs: [
        <Select.Option key="true" value="true">
          是
        </Select.Option>,
        <Select.Option key="false" value="false">
          否
        </Select.Option>
      ]
    }
  ];

  /*查询表添加字段*/
  handleSearchAdd() {
    const data = {
      keyLookup: "",
      keyCondition: "=",
      keyStream1: "",
      keyStream2: ""
    };
    this.refs.searchTable.handleAdd(data);
  }

  /*添加字段*/
  handleUpdateAdd() {
    const data = {
      updateLookup: "",
      updateStream: "",
      update: "true"
    };
    this.refs.editTable.handleAdd(data);
  }

  handleDeleteFields = () => {
    this.refs.editTable.handleDelete();
  };

  handleSearchFields = () => {
    this.refs.searchTable.handleDelete();
  };

  render() {
    const { getFieldDecorator } = this.props.form;
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

    const searchColumns = [
      {
        title: "表字段",
        dataIndex: "keyLookup",
        key: "keyLookup",
        selectable: true
      },
      {
        title: "比较符",
        dataIndex: "keyCondition",
        key: "keyCondition",
        width: "20%",
        selectable: true,
        selectArgs: [
          <Select.Option key="1" value="=">
            =
          </Select.Option>,
          <Select.Option key="2" value="= ~NULL">
            = ~NULL
          </Select.Option>,
          <Select.Option key="3" value="<>">
            {"<>"}
          </Select.Option>,
          <Select.Option key="4" value="<">
            {"<"}
          </Select.Option>,
          <Select.Option key="5" value="<=">
            {"<="}
          </Select.Option>,
          <Select.Option key="6" value=">">
            {">"}
          </Select.Option>,
          <Select.Option key="7" value=">=">
            {">="}
          </Select.Option>,
          <Select.Option key="8" value="LIKE">
            LIKE
          </Select.Option>,
          <Select.Option key="9" value="BETWEEN">
            BETWEEN
          </Select.Option>,
          <Select.Option key="10" value="IS NULL">
            IS NULL
          </Select.Option>,
          <Select.Option key="11" value="IS NOT NULL">
            IS NOT NULL
          </Select.Option>
        ]
      },
      {
        title: "流里的字段1",
        dataIndex: "keyStream1",
        key: "keyStream1",
        width: "25%",
        selectable: true
      },
      {
        title: "流里的字段2",
        dataIndex: "keyStream2",
        key: "keyStream2",
        width: "25%",
        selectable: true
      }
    ];

    return (
      <Modal
        visible={visible}
        title="插入/更新"
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
                <Select onChange={this.getTableList.bind(this)}>
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
                initialValue: config.table === "lookup table" ? "" : table
              })(
                <Select onChange={this.handleGetFields.bind(this)}>
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
                { required: true, message: "提交记录数量不能为空" },
                { pattern: /^(^[0-9]*$)$/, message: "请输入数字!" }
              ]
            })(<Input />)}
          </FormItem>
          <FormItem
            {...formItemLayout2}
            style={{ marginBottom: "0px", marginLeft: "25%" }}
          >
            {getFieldDecorator("updateBypassed", {
              valuePropName: "checked",
              initialValue: config.updateBypassed
            })(<Checkbox>不执行任何更新</Checkbox>)}
          </FormItem>
          <div style={{ margin: "0 5%" }}>
            <Row style={{ marginBottom: "5px" }}>
              <Col span={12}>
                <p style={{ marginLeft: "5px" }}>用来查询的关键字(必填)：</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{ float: "right" }}>
                  <Button onClick={this.handleSearchAdd.bind(this)}>
                    添加字段
                  </Button>
                  <Button
                    onClick={() => {
                      this.getInputFieldsMap("searchTable");
                    }}
                  >
                    获取字段
                  </Button>
                  <Button onClick={this.handleSearchFields.bind(this)}>
                    删除字段
                  </Button>
                </ButtonGroup>
              </Col>
            </Row>

            <EditTable
              columns={searchColumns}
              rowSelection={true}
              dataSource={this.state.searchFields}
              tableStyle="editTableStyle5"
              size={"small"}
              scroll={{ y: 140 }}
              ref="searchTable"
              count={4}
            />
            <Row style={{ marginTop: "20px", marginBottom: "5px" }}>
              <Col span={12}>
                <p style={{ marginLeft: "5px" }}>插入或更新字段：</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{ float: "right" }}>
                  <Button onClick={this.handleUpdateAdd.bind(this)}>
                    添加字段
                  </Button>
                  <Button onClick={this.handleDeleteFields.bind(this)}>
                    删除字段
                  </Button>
                  <Button
                    onClick={() => {
                      this.getInputFieldsMap("editTable");
                    }}
                  >
                    获取与更新字段
                  </Button>
                </ButtonGroup>
              </Col>
            </Row>
            <EditTable
              columns={this.updateColumns}
              rowSelection={true}
              dataSource={this.state.updateFields}
              tableStyle="editTableStyle5"
              size={"small"}
              scroll={{ y: 140 }}
              ref="editTable"
              count={4}
            />
          </div>
        </Form>
      </Modal>
    );
  }
}
const InsertUpdate = Form.create()(UpdateDialog);

export default connect()(
  withDatabase(InsertUpdate, {
    isRead: false
  })
);
