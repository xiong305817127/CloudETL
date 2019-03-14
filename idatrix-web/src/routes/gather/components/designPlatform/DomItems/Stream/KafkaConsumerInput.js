import React from "react";
import { connect } from "dva";
import {
  Button,
  Form,
  Input,
  Radio,
  Select,
  Tabs,
  Row,
  Col,
  Table
} from "antd";
import Modal from "components/Modal.js";
import style from "./style.less";

const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const Option = Select.Option;
const RadioGroup = Radio.Group;
class ParquetOutput extends React.Component {
  constructor(props) {
    super(props);
    const { visible } = props.model;
    if (visible === true) {
      const { config } = props.model.config;

      this.state = {
        hadoopList: [],
        path: "",
        useFolder: true,
        value: "DIRECT",
        transList: [],
        topics: [],
        clusterName: "",
        configs:
          JSON.stringify(config) === "{}"
            ? {
                "auto.offset.rest": "",
                "ssl.key.password": "",
                "ssl.keystore.location": "",
                "ssl.keystore.password": "",
                "ssl.truststore.location": "",
                "ssl.truststore.password": ""
              }
            : config,
        optionArr:[],
        fields: {
          keyField: {
            kafkaName: "key",
            outputName: "key",
            outputType: "String"
          },
          messageField: {
            kafkaName: "message",
            outputName: "message",
            outputType: "String"
          },
          topicField: {
            kafkaName: "topic",
            outputName: "topic",
            outputType: "String"
          },
          offsetField: {
            kafkaName: "offset",
            outputName: "offset",
            outputType: "Integer"
          },
          partitionField: {
            kafkaName: "partition",
            outputName: "partition",
            outputType: "Integer"
          },
          timestampField: {
            kafkaName: "timestamp",
            outputName: "timestamp",
            outputType: "Integer"
          }
        }
      };
    }
  }

  componentDidMount() {
    this.Request();
    this.getOptions();
  }

  Request() {
    const {
      getHadoopServer,
      getTransList,
      transname,
      text,
      getInputFields,
      getInputSelect
    } = this.props.model;

    getHadoopServer(data => {
      console.log("Hadoop", data);
      this.setState({
        hadoopList: data
      });
    });
    getInputFields({ transname: transname, stepname: text }, data => {
      this.setState({
        InputData: data
      });
      if (this.refs.editTable) {
        let options = getInputSelect(data, "name");
        this.refs.editTable.updateOptions({
          name: options
        });
      }
    });
    getTransList(data => {
      console.log("translist", data);
      this.setState({
        transList: data
      });
    });
  }

  /**
   * 隐藏modal
   */
  hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type: "items/hide",
      visible: false
    });
  };

  /**
   * 新增一条内容
   */
  handleAddOption = () => {
    const newOptionArr = JSON.parse(JSON.stringify(this.state.optionArr));
    const newConfigs = JSON.parse(JSON.stringify(this.state.configs));
    
    newOptionArr.push({
      value: "",
      name: "param" + newOptionArr.length,
      key: "option" + newOptionArr.length
    });

    newConfigs["param" + newOptionArr.length] = "";

    this.setState({
      optionArr: newOptionArr,
      configs: newConfigs
    });
  };

  /**
   * 提交创建内容
   */
  handleCreate = e => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) {
        return;
      }
      const { inputFields } = this.props.model.config;
      const { formatTable } = this.props.model;
      let sendFields = [];
      if (this.refs.editTable) {
        if (this.refs.editTable.state.dataSource.length > 0) {
          let args = ["path", "name", "type", "nullable", "ifNullValue"];
          sendFields = formatTable(this.refs.editTable.state.dataSource, args);
        }
      } else {
        if (inputFields) {
          sendFields = inputFields;
        }
      }

      const { panel, transname, key, saveStep, text } = this.props.model;

      let obj = {
        transname: transname,
        newname: text === values.text ? "" : values.text,
        stepname: text,
        type: panel
      };

      // 拷贝values
      const newValues = JSON.parse(JSON.stringify(values));
      const deletedValues = {};

      Object.entries(newValues).forEach(val => {
        if (val[0].indexOf("option") === -1) {
          deletedValues[val[0]] = val[1];
        }
      });

      obj.config = {
        ...deletedValues,
        ...this.state.fields,
        config: this.state.configs
        // outputFields:sendFields
      };
      saveStep(obj, key, data => {
        if (data.code === "200") {
          this.hideModal();
        }
      });
    });
  };

  /**
   * 选中集群或直连时修改
   */
  selectConnection = () => {
    this.setState({
      value: this.state.value == "DIRECT" ? "CLUSTER" : "DIRECT"
    });
  };

  /**
   * 监听服务器连接配置的输入框
   * @param {Boolean} cluster : 是否为集群
   * @return {Function} e : 事件
   */
  handleServerConnectionChange = cluster => {
    return e => {
      const value = typeof e === "string" ? e : e.target.value;
      if (value !== "" && this.state.selected !== value) {
        const obj = {};
        const { transname, text, panel, getDetails } = this.props.model;

        obj.transName = transname;
        obj.stepName = text;
        obj.detailType = panel;
        obj.detailParam = {
          flag: "getTopic",
          directBootstrapServers: cluster ? "" : value,
          clusterName: cluster ? value : "" //alisa修改
        };

        console.log(obj);
        getDetails(obj, data => {
          this.setState({
            topics: data,
            selected: value
          });
        });
      }
    };
  };

  /**
   * 监控field部分的变动
   * 直接更新至state中
   */
  handleFieldChange = type => {
    return e => {
      const value = e.target.value;
      this.setState({
        fields: {
          ...this.state.fields,
          [type]: {
            ...this.state.fields[type],
            outputName: value
          }
        }
      });
    };
  };

  /**
   * 记录Option名称改变
   */
  handleOptionNameChange = (index, ifValue) => {
    return e => {
      const value = e.target.value;
      const configArr = JSON.parse(JSON.stringify(this.state.optionArr));
      const newConfig = {};

      // 判断是否为value修改，如果是则修改value[1]
      // 否则修改value[0]写入新的数组
      if (ifValue) {
        configArr[index] = {...configArr[index], value: value};
      } else {
        configArr[index] = {...configArr[index], name: value};
      }

      configArr.forEach(
        (val) =>
          val.name !== "" ?(newConfig[val.name] = val.value): null
      );
      // 更新至新的数组
      this.setState({
        configs: newConfig,
        optionArr: configArr
      });
    };
  };

  getOptions = () => {
    const { config } = this.props.model;

    const optionConfig =
      JSON.stringify(config.config) === "{}"
        ? this.state.configs
        : config.config;
    const dataOptionArr = Object.entries(optionConfig).map((val,index) => ({
      name: val[0],
      value: val[1],
      key: "option" + index
    }));

    this.setState({
      optionArr: dataOptionArr
    });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const { text, config, visible, handleCheckName } = this.props.model;
    const {
      keyField,
      messageField,
      partitionField,
      topicField,
      offsetField,
      timestampField
    } = config;

    const dataArr = [
      keyField,
      messageField,
      partitionField,
      topicField,
      offsetField,
      timestampField
    ];
    const formItemLayout2 = {
      labelCol: { span: 24 },
      wrapperCol: { span: 18 }
    };
    const formItemLayout3 = {
      labelCol: { span: 24 },
      wrapperCol: { span: 20 }
    };

    const data = [
      {
        kafkaName: "key",
        outputName: "key",
        outputType: "String"
      },
      {
        kafkaName: "message",
        outputName: "message",
        outputType: "String"
      },
      {
        kafkaName: "topic",
        outputName: "topic",
        outputType: "String"
      },
      {
        kafkaName: "partition",
        outputName: "partition",
        outputType: "Integer"
      },
      {
        kafkaName: "offset",
        outputName: "offset",
        outputType: "Integer"
      },
      {
        kafkaName: "timestamp",
        outputName: "timestamp",
        outputType: "Integer"
      }
    ];

    const columns = [
      {
        title: "输入名",
        dataIndex: "kafkaName",
        key: "kafkaName",
        width: "25%"
      },
      {
        title: "输出名",
        dataIndex: "outputName",
        key: "outputName",
        width: "35%",
        editable: true,
        render: (text, record, index) => {
          return (
            <FormItem className={style.formInputReset}>
              {getFieldDecorator(record.kafkaName + "Field", {
                initialValue: record.outputName,
                rules: [
                  { required: true, message: record.kafkaName + "不可为空" }
                ]
              })(
                <Input
                  placeholder={record.outputName}
                  onChange={this.handleFieldChange(record.kafkaName + "Field")}
                />
              )}
            </FormItem>
          );
        }
      },
      {
        title: "类型",
        dataIndex: "outputType",
        key: "outputType",
        width: "40%"
      }
    ];

    const dataOptionColumns = [
      {
        title: "名称",
        dataIndex: "name",
        key: "name",
        render: (text, record, index) => {
          return (
            <FormItem className={style.formInputReset}>
              {getFieldDecorator("option" + index, {
                initialValue: record.name,
                rules: [
                  {
                    validator: (rule, value, cb) => {
                      value === "" || isNaN(value) ? cb() : cb("key不可为数字");
                    },
                    message: "不可为数字"
                  }
                ]
              })(
                <Input
                  placeholder={record.name}
                  onChange={this.handleOptionNameChange(index)}
                />
              )}
            </FormItem>
          );
        }
      },
      {
        title: "值",
        dataIndex: "value",
        key: "value",
        render: (text, record, index) => {
          return (
            <FormItem className={style.formInputReset}>
              {getFieldDecorator("optionValue" + index, {
                initialValue: record.value
              })(
                <Input
                  placeholder={record.value}
                  onChange={this.handleOptionNameChange(index, true)}
                />
              )}
            </FormItem>
          );
        }
      }
    ];

    return (
      <Modal
        visible={visible}
        title="Kafka消费者"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        onCancel={this.hideModal}
        width={700}
        style={{ minHeight: "500px" }}
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
          <Row>
            <Col span={12}>
              <FormItem
                label={
                  <span>
                    步骤名称
                  </span>
                }
                {...formItemLayout3}
                style={{ marginBottom: "8px" }}
              >
                {getFieldDecorator("text", {
                  initialValue: text,
                  rules: [
                    {
                      whitespace: true,
                      required: true,
                      message: "请输入步骤名称！"
                    },
                    {
                      validator: handleCheckName,
                      message: "步骤名称已存在，请更改!"
                    }
                  ]
                })(<Input />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem label="目录/文件名" {...formItemLayout3}>
                {getFieldDecorator("transformationPath", {
                  initialValue: config.transformationPath
                })(
                  <Select>
                    {this.state.transList.map((val, index) => (
                      <Option value={val} key={"translit}" + index}>
                        {val}
                      </Option>
                    ))}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          <Tabs>
            <TabPane tab="设置" key="1">
              <Row>
                <Col span={12}>
                  <FormItem
                    label={<span>服务器连接设置：</span>}
                    {...formItemLayout2}
                    style={{ marginBottom: "8px" }}
                  >
                    {getFieldDecorator("connectionType", {
                      initialValue: config.connectionType
                        ? config.connectionType
                        : "DIRECT"
                    })(
                      <RadioGroup onChange={this.selectConnection}>
                        <Radio value={"DIRECT"}>直连</Radio>
                        <Radio value={"CLUSTER"}>集群</Radio>
                      </RadioGroup>
                    )}
                  </FormItem>
                  {this.state.value === "DIRECT" && (
                    <FormItem
                      label={<span>启动服务器：</span>}
                      {...formItemLayout3}
                      style={{ marginBottom: "8px" }}
                    >
                      {getFieldDecorator("directBootstrapServers", {
                        initialValue: config.directBootstrapServers,
                        rules: [
                          {
                            whitespace: true,
                            required: true,
                            message: "请输入步骤名称！"
                          }
                        ]
                      })(
                        <Input
                          onBlur={this.handleServerConnectionChange(false)}
                        />
                      )}
                    </FormItem>
                  )}
                  {this.state.value === "CLUSTER" && (
                    <FormItem
                      label={<span>服务器集群：（Hadoop Cluster）</span>}
                      {...formItemLayout3}
                      style={{ marginBottom: "8px" }}
                    >
                      {getFieldDecorator("clusterName", {
                        initialValue: config.clusterName
                          ? [config.clusterName]
                          : [],
                        rules: [
                          {
                            whitespace: true,
                            required: true,
                            message: "请输入步骤名称！"
                          }
                        ]
                      })(
                        <Select
                          onChange={this.handleServerConnectionChange(true)}
                        >
                          {this.state.hadoopList.map((val, index) => (
                            <Option value={val.name} key={"hadooplist" + index}>
                              {val.name}
                            </Option>
                          ))}
                        </Select>
                      )}
                    </FormItem>
                  )}
                </Col>

                <Col span={12}>
                  <FormItem
                    label={<span>消费组：</span>}
                    {...formItemLayout3}
                    style={{ marginBottom: "8px" }}
                  >
                    {getFieldDecorator("consumerGroup", {
                      initialValue: config.consumerGroup
                    })(<Input />)}
                  </FormItem>

                  <FormItem
                    label={<span>选择Topics</span>}
                    {...formItemLayout3}
                    style={{ marginBottom: "8px" }}
                  >
                    {getFieldDecorator("topics", {
                      initialValue: config.topics,
                      rules: [{ required: true, message: "请输入步骤名称！" }]
                    })(
                      <Select mode="tags">
                        {this.state.topics.map((val, index) => (
                          <Option value={val} key={"topics" + index}>
                            {val}
                          </Option>
                        ))}
                      </Select>
                    )}
                  </FormItem>
                </Col>
              </Row>
            </TabPane>
            <TabPane tab="批量作业" key="2">
              <Row>
                <Col span={12}>
                  <FormItem
                    label="记录数量"
                    {...formItemLayout3}
                    style={{ marginBottom: "8px" }}
                  >
                    {getFieldDecorator("batchSize", {
                      initialValue: config.batchSize ? config.batchSize : 1000
                    })(<Input />)}
                  </FormItem>

                  <FormItem
                    label="时间间隔"
                    {...formItemLayout3}
                    style={{ marginBottom: "8px" }}
                  >
                    {getFieldDecorator("batchDuration", {
                      initialValue: config.batchDuration
                        ? config.batchDuration
                        : 0
                    })(<Input />)}
                  </FormItem>
                </Col>
                <Col span={12} />
              </Row>
            </TabPane>
            <TabPane tab="字段名" key="3">
              <Row>
                <Col span={16}>
                  <Table
                    dataSource={
                      dataArr.every(val => val.outputName !== "")
                        ? dataArr
                        : data
                    }
                    columns={columns}
                    size="small"
                    pagination={false}
                    rowKey={"kafkaName"}
                  />
                </Col>
                <Col span={8} />
              </Row>
            </TabPane>

            <TabPane tab="配置项" key="4">
              <Row>
                <Col span={24} size={"small"} >
                  <Button size="small" style={{marginBottom:"16px"}} onClick={this.handleAddOption}>新增一行</Button>
                </Col>
                <Col span={24}>
                  <Table
                    dataSource={this.state.optionArr}
                    columns={dataOptionColumns}
                    size="small"
                    pagination={false}
                    rowKey={"key"}
                    style={{maxHeight:360,overflow:"scroll"}}
                  />
                </Col>
              </Row>
            </TabPane>
          </Tabs>
        </Form>
      </Modal>
    );
  }
}

export default connect()(Form.create()(ParquetOutput));
