import React from "react";
import { connect } from "dva";
import {
  Form,
  Select,
  Button,
  Input,
  Checkbox,
  Tabs,
  Row,
  Col,
  message
} from "antd";
import Modal from "components/Modal.js";
import { treeViewConfig, selectType } from "../../../../constant";
import EditTable from "../../../common/EditTable";
import HadoopOutputModel from "../Model/HadoopOutputModel";
import withHdfs from "../../../common/withHdfs";

const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const Option = Select.Option;
const ButtonGroup = Button.Group;

class HadoopOutputDialog extends React.Component {
  constructor(props) {
    super(props);
    const { visible } = props.model;

    if (visible === true) {
      let data = [];
      const { fields } = props.model.config;
      if (fields) {
        let count = 0;
        for (let index of fields) {
          data.push({
            key: count,
            ...index
          });
          count++;
        }
      }
      this.state = {
        dataSource: data,
        field_list: [],
        visible: false
      };
    }
  }

  setModelHide() {
    const { dispatch } = this.props;
    dispatch({
      type: "items/hide",
      visible: false
    });
  }

  formItemLayout1 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 }
  };
  formItemLayout = {
    wrapperCol: { span: 18 }
  };

  formItemLayout2 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 12 }
  };

  columns = [
    {
      title: "名称",
      dataIndex: "name",
      key: "name",
      width: "12%",
      editable: true
    },
    {
      title: "类型",
      dataIndex: "type",
      width: "13%",
      key: "type",
      selectable: true,
      selectArgs: selectType.get("type")
    },
    {
      title: "格式",
      dataIndex: "format",
      key: "format",
      width: "10%",
      editable: true
    },
    {
      title: "长度",
      dataIndex: "length",
      key: "length",
      width: "8%",
      editable: true
    },
    {
      title: "精度",
      dataIndex: "precision",
      key: "precision",
      width: "8%",
      editable: true
    },
    {
      title: "货币",
      dataIndex: "currencyType",
      key: "currencyType",
      width: "8%",
      editable: true
    },
    {
      title: "小数",
      dataIndex: "decimal",
      key: "decimal",
      width: "8%",
      editable: true
    },
    {
      title: "分组",
      dataIndex: "group",
      key: "group",
      width: "8%",
      editable: true
    },
    {
      title: "去除字符串方式",
      dataIndex: "trimType",
      key: "trimType",
      width: "15%",
      selectable: true,
      selectArgs: selectType.get("trimType")
    },
    {
      title: "null",
      dataIndex: "nullif",
      key: "nullif",
      editable: true
    }
  ];

  handleAdd = () => {
    const data = {
      name: "",
      type: "",
      format: "",
      currencyType: "",
      decimal: "",
      group: "",
      nullif: "",
      trimType: "",
      length: "",
      precision: ""
    };
    this.refs.editTable.handleAdd(data);
  };
  handleDeleteFields = () => {
    this.refs.editTable.handleDelete();
  };

  handleFormSubmit() {
    const form = this.props.form;
    const {
      panel,
      transname,
      description,
      key,
      saveStep,
      config,
      text,
      formatTable
    } = this.props.model;
    const { fields } = config;
    const { hadoopName, selectName } = this.props.hdfsData;

    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      if (this.refs.editTable) {
        if (this.refs.editTable.state.dataSource.length > 0) {
          let arg = [
            "name",
            "type",
            "format",
            "currencyType",
            "decimal",
            "group",
            "nullif",
            "trimType",
            "length",
            "precision"
          ];
          sendFields = formatTable(this.refs.editTable.state.dataSource, arg);
        }
      } else {
        if (fields) {
          sendFields = fields;
        }
      }

      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = text === values.text ? "" : values.text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        fields: sendFields,
        ...values,
        sourceConfigurationName: hadoopName,
        fileName: values.fileName
      };
      saveStep(obj, key, data => {
        if (data.code === "200") {
          this.setModelHide();
        }
      });
    });
  }

  /*插入分隔符*/
  insertTab() {
    const { setFieldsValue, getFieldValue } = this.props.form;
    let str = getFieldValue("separator");

    str = "\t" + str;

    setFieldsValue({
      separator: str
    });
  }

  /*编辑*/
  handleModelShow() {
    const { dispatch } = this.props;
    dispatch({
      type: "hadoopoutputmodel/show",
      visible: true
    });
  }

  /*获取字段*/
  handleGetFields = method => {
    const { getInputFields, transname, text } = this.props.model;
    let args = [];
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj, data => {
      if (data) {
        let args = [];
        let count = 0;
        for (let index of data) {
          args.push({
            key: count,
            currencyType: index.currencySymbol,
            decimal: index.decimalSymbol,
            group: index.groupingSymbol,
            ...index
          });
          count++;
        }
        if (method === "input") {
          this.setState({
            field_list: args
          });
        } else {
          this.refs.editTable.updateTable(args, count);
        }
      }

      this.setState({
        dataSource: args
      });
    });
  };

  /*文件模板*/
  getFieldList(name) {
    const { dispatch } = this.props;
    const { panel } = this.props.model;
    const { hadoopName, selectName } = this.props.hdfsData;

    let obj = treeViewConfig.get(panel)[name];
    let path = hadoopName;
    let updateModel = this.setFolder.bind(this);
    if (path && path.trim()) {
      dispatch({
        type: "treeview/showTreeModel",
        payload: {
          ...obj,
          obj: {
            ...obj.obj,
            path: `${path}::${selectName}`
          },
          prefixStr: path + "::",
          updateModel: updateModel
        }
      });
    } else {
      message.error("请先选择Hadoop集群！");
    }
  }

  /*设置文件名*/
  setFolder(str) {
    const { setFieldsValue } = this.props.form;
    if (str) {
      setFieldsValue({
        fileName: str
      });
    }
  }

  render() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { visible, config, text, handleCheckName } = this.props.model;
    const { hadoopName } = this.props.hdfsData;
    const { selectBefore } = this.props;

    const setDisabled2 = () => {
      if (getFieldValue("fileNameInField") === undefined) {
        return config.fileNameInField;
      } else {
        if (getFieldValue("fileNameInField")) {
          return getFieldValue("fileNameInField");
        } else {
          return false;
        }
      }
    };

    const setDisabled3 = () => {
      if (getFieldValue("specifyFormat") === undefined) {
        return config.specifyFormat;
      } else {
        if (getFieldValue("specifyFormat")) {
          return getFieldValue("specifyFormat");
        } else {
          return false;
        }
      }
    };

    return (
      <Modal
        visible={visible}
        title="Hadoop File Output"
        wrapClassName="vertical-center-modal"
        width={750}
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
        maskClosable={false}
        onCancel={this.setModelHide.bind(this)}
      >
        <Form>
          <FormItem label="步骤名称" {...this.formItemLayout1}>
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
          <div style={{ margin: "0 5%" }}>
            <Tabs type="card">
              <TabPane tab="文件" key="1">
                <FormItem
                  {...this.formItemLayout1}
                  style={{ marginBottom: "8px" }}
                  label="Hadoop环境"
                >
                  <span className="ant-form-text">{hadoopName}</span>
                </FormItem>
                <FormItem
                  {...this.formItemLayout1}
                  style={{ marginBottom: "8px" }}
                  label="根路径"
                >
                  { selectBefore()}
                </FormItem>
                <FormItem label="目录/文件" {...this.formItemLayout1}>
                  {getFieldDecorator("fileName", {
                    initialValue: config.fileName
                  })(
                    <Input
                      disabled={setDisabled2()}
                    />
                  )}
                  <Button
                    disabled={setDisabled2()}
                    onClick={() => {
                      this.getFieldList("model");
                    }}
                  >
                    浏览
                  </Button>
                </FormItem>
                <Row style={{ marginLeft: "10%" }}>
                  <Col span={12}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("createParentFolder", {
                        valuePropName: "checked",
                        initialValue: config.createParentFolder
                      })(
                        <Checkbox disabled={setDisabled2()}>
                          创建父目录
                        </Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={12}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("doNotOpenNewFileInit", {
                        valuePropName: "checked",
                        initialValue: config.doNotOpenNewFileInit
                      })(
                        <Checkbox disabled={setDisabled2()}>
                          启动时不创建文件
                        </Checkbox>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <Row>
                  <Col span={12}>
                    <FormItem
                      style={{ marginLeft: "20%", marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("fileNameInField", {
                        valuePropName: "checked",
                        initialValue: config.fileNameInField
                      })(<Checkbox>从字段中获取文件名</Checkbox>)}
                    </FormItem>
                  </Col>
                </Row>
                <FormItem
                  label="文件名字段"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout1}
                >
                  {getFieldDecorator("fileNameField", {
                    initialValue: config.fileNameField
                  })(
                    <Select
                      disabled={!setDisabled2()}
                      onFocus={() => {
                        this.handleGetFields("input");
                      }}
                    >
                      {this.state.field_list.map(index => (
                        <Select.Option key={index.name} value={index.name}>
                          {index.name}
                        </Select.Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
                <FormItem
                  label="扩展名"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout1}
                >
                  {getFieldDecorator("extention", {
                    initialValue: config.extention
                  })(<Input />)}
                </FormItem>
                <Row style={{ marginLeft: "10%" }}>
                  <Col span={12}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("stepNrInFilename", {
                        valuePropName: "checked",
                        initialValue: config.stepNrInFilename
                      })(
                        <Checkbox disabled={setDisabled2()}>
                          文件名里包含步骤数？
                        </Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={12}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("haspartno", {
                        valuePropName: "checked",
                        initialValue: config.haspartno
                      })(
                        <Checkbox disabled={setDisabled2()}>
                          文件名里包含数据分区号？
                        </Checkbox>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <Row style={{ marginLeft: "10%" }}>
                  <Col span={12}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("addDate", {
                        valuePropName: "checked",
                        initialValue: config.addDate
                      })(
                        <Checkbox disabled={setDisabled2() || setDisabled3()}>
                          文件名里包含日期？
                        </Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={12}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("addTime", {
                        valuePropName: "checked",
                        initialValue: config.addTime
                      })(
                        <Checkbox disabled={setDisabled2() || setDisabled3()}>
                          文件名里包含时间？
                        </Checkbox>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <Row style={{ marginLeft: "10%" }}>
                  <Col span={12}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("specifyFormat", {
                        valuePropName: "checked",
                        initialValue: config.specifyFormat
                      })(
                        <Checkbox disabled={setDisabled2()}>
                          指定日期时间格式？
                        </Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={12}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("addToResultFilenames", {
                        valuePropName: "checked",
                        initialValue: config.addToResultFilenames
                      })(
                        <Checkbox disabled={setDisabled2()}>
                          结果中添加文件名？
                        </Checkbox>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <FormItem
                  label="日期时间格式"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout1}
                >
                  {getFieldDecorator("dateTimeFormat", {
                    initialValue: config.dateTimeFormat
                  })(
                    <Select disabled={setDisabled2() || !setDisabled3()}>
                      {selectType.get("dateType")}
                    </Select>
                  )}
                </FormItem>
              </TabPane>
              <TabPane tab="内容" key="2">
                <Row style={{ marginLeft: "10%" }}>
                  <Col span={12}>
                    <FormItem
                      style={{ marginBottom: "10px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("append", {
                        valuePropName: "checked",
                        initialValue: config.append
                      })(<Checkbox>追加方式</Checkbox>)}
                    </FormItem>
                  </Col>
                </Row>
                <FormItem
                  label="分隔符"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout2}
                >
                  {getFieldDecorator("separator", {
                    initialValue: config.separator
                  })(<Input />)}
                  <Button onClick={this.insertTab.bind(this)}>插入TAB</Button>
                </FormItem>
                <FormItem
                  label="封闭符"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout1}
                >
                  {getFieldDecorator("enclosure", {
                    initialValue: config.enclosure
                  })(<Input />)}
                </FormItem>
                <Row style={{ marginLeft: "10%" }}>
                  <Col span={10}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("enclosureForced", {
                        valuePropName: "checked",
                        initialValue: config.enclosureForced
                      })(<Checkbox>强制在字段周围加封闭符？</Checkbox>)}
                    </FormItem>
                  </Col>
                  <Col span={7}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("header", {
                        valuePropName: "checked",
                        initialValue: config.header
                      })(<Checkbox>头部？</Checkbox>)}
                    </FormItem>
                  </Col>
                  <Col span={7}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("footer", {
                        valuePropName: "checked",
                        initialValue: config.footer
                      })(<Checkbox>尾部？</Checkbox>)}
                    </FormItem>
                  </Col>
                </Row>

                <FormItem
                  label="格式"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout1}
                >
                  {getFieldDecorator("format", {
                    initialValue: config.format
                  })(
                    <Select>
                      <Option value="DOS">
                        CR+LF terminated (Windows, DOS)
                      </Option>
                      <Option value="UNIX">LF terminated (Unix)</Option>
                      <Option value="CR">CR terminated</Option>
                      <Option value="None">No new-line terminated</Option>
                    </Select>
                  )}
                </FormItem>
                <FormItem
                  label="压缩"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout1}
                >
                  {getFieldDecorator("compression", {
                    initialValue: config.compression
                  })(
                    <Select>
                      <Option value="None">None</Option>
                      <Option value="Zip">Zip</Option>
                      <Option value="GZip">GZip</Option>
                      <Option value="Snappy">Snappy</Option>
                      <Option value="Hadoop-snappy">Hadoop-snappy</Option>
                    </Select>
                  )}
                </FormItem>
                <FormItem
                  label="编码"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout1}
                >
                  {getFieldDecorator("encoding", {
                    initialValue: config.encoding ? config.encoding : "GBK"
                  })(
                    <Select>
                      <Option value="GBK">GBK</Option>
                      <Option value="ISO-8859-1">ISO-8859-1</Option>
                      <Option value="GB2312">GB2312</Option>
                      <Option value="UTF-8">UTF-8</Option>
                      <Option value="Big5">Big5</Option>
                    </Select>
                  )}
                </FormItem>
                <Row style={{ marginLeft: "10%" }}>
                  <Col span={12}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("pad", {
                        valuePropName: "checked",
                        initialValue: config.pad
                      })(<Checkbox>字段右填充或裁剪？</Checkbox>)}
                    </FormItem>
                  </Col>
                  <Col span={12}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("fastDump", {
                        valuePropName: "checked",
                        initialValue: config.fastDump
                      })(<Checkbox>快速数据存储(无格式)？</Checkbox>)}
                    </FormItem>
                  </Col>
                </Row>

                <FormItem
                  label="分拆...每一行"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout1}
                >
                  {getFieldDecorator("splitevery", {
                    initialValue: config.splitevery
                  })(<Input disabled={setDisabled2()} />)}
                </FormItem>
                <FormItem
                  label="添加文件结束行"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout1}
                >
                  {getFieldDecorator("endedLine", {
                    initialValue: config.endedLine
                  })(<Input disabled={setDisabled2()} />)}
                </FormItem>
              </TabPane>
              <TabPane tab="字段" key="3">
                <div>
                  <Row style={{ margin: "5px 0", width: "100%" }}>
                    <Col span={12}>
                      <ButtonGroup size={"small"}>
                        <Button onClick={this.handleAdd.bind(this)}>
                          添加字段
                        </Button>
                        <Button
                          onClick={() => {
                            this.handleGetFields("table");
                          }}
                        >
                          获取字段
                        </Button>
                      </ButtonGroup>
                    </Col>
                    <Col span={12} style={{ textAlign: "right" }}>
                      <Button
                        size={"small"}
                        onClick={this.handleDeleteFields.bind(this)}
                      >
                        删除字段
                      </Button>
                    </Col>
                  </Row>
                  <EditTable
                    columns={this.columns}
                    rowSelection={true}
                    dataSource={this.state.dataSource}
                    tableStyle="editTableStyle5"
                    size={"small"}
                    scroll={{ y: 140, x: 1000 }}
                    ref="editTable"
                    count={4}
                  />
                </div>
              </TabPane>
            </Tabs>
            <HadoopOutputModel />
          </div>
        </Form>
      </Modal>
    );
  }
}
const HadoopFileOutput = Form.create()(HadoopOutputDialog);

export default connect()(
  withHdfs(HadoopFileOutput, {
		isRead: false,
		width:360
  })
);
