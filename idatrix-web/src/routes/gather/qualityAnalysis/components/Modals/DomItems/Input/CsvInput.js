import React from "react";
import { connect } from 'dva';
import { Button, Form, Input, Radio, Select, Tabs, Checkbox, Row, Col, Upload, message } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../../../components/common/EditTable';
import { treeViewConfig, treeUploadConfig } from '../../../../../constant';

class CsvDialog extends React.Component {


  constructor(props) {
    super(props);
    const { visible } = props.model;
    if (visible === true) {
      const { inputFields } = props.model.config;
      let data = [];
      if (inputFields) {
        for (let index of inputFields) {
          data.push({
            key: index.name,
            ...index
          })
        }
      }
      this.state = {
        searchFields: data,
        path: "",
        useFolder: true
      }
    }
  };

  componentDidMount() {
    const { getDataStore } = this.props.model;
    let obj1 = {};
    obj1.type = "data";
    obj1.path = "";
    getDataStore(obj1, data => {
      const { path } = data;
      this.setState({
        path: path
      })
    })
  }

  hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'domItems/hide',
      visible: false
    });
  };

  handleCreate = () => {
    const { panel, description, transname, key, saveStep, text, config, formatTable } = this.props.model;
    const { searchFields } = config;
    const form = this.props.form;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      if (this.refs.searchTable) {
        if (this.refs.searchTable.state.dataSource.length > 0) {
          let args = ["name", "type", "format", "currencysymbol", "decimalsymbol", "groupsymbol", "length", "precision", "trimType"];
          sendFields = formatTable(this.refs.searchTable.state.dataSource, args);
        }
      } else {
        if (searchFields) {
          sendFields = searchFields;
        }
      }

      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text ? "" : values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        ...values,
        inputFields: sendFields
      };

      saveStep(obj, key, data => {
        if (data.code === "200") {
          this.hideModal();
        }
      });
    });
  };

  /*查询表添加字段*/
  handleSearchAdd() {
    const data = {
      name: "",
      type: "",
      format: "",
      currencysymbol: "",
      decimalsymbol: "",
      groupsymbol: "",
      length: "",
      precision: "",
      trimType: "none"
    };
    this.refs.searchTable.handleAdd(data);
  };

  handleSearchFields = () => {
    this.refs.searchTable.handleDelete();
  };

  columns = [
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      width: "10%",
      editable: true
    }, {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: "15%",
      selectable: true,
      selectArgs: [<Select.Option key="1" value="1">Number</Select.Option>,
      <Select.Option key="3" value="3">Date</Select.Option>,
      <Select.Option key="2" value="2">String</Select.Option>,
      <Select.Option key="4" value="4">Boolean</Select.Option>,
      <Select.Option key="5" value="5">Integer</Select.Option>,
      <Select.Option key="6" value="6">BigNumber</Select.Option>,
      <Select.Option key="8" value="8">Binary</Select.Option>,
      <Select.Option key="9" value="9">Timestamp</Select.Option>,
      <Select.Option key="10" value="10">Internet Address</Select.Option>
      ]
    }, {
      title: '格式',
      dataIndex: 'format',
      key: 'format',
      width: "11%",
      editable: true
    }, {
      title: '长度',
      dataIndex: 'length',
      key: 'length',
      width: "6%",
      editable: true
    }, {
      title: '精度',
      dataIndex: 'precision',
      key: 'precision',
      width: "6%",
      editable: true
    }, {
      title: '货币符号',
      dataIndex: 'currencysymbol',
      key: 'currencysymbol',
      width: "11%",
      editable: true
    }, {
      title: '小数点符号',
      dataIndex: 'decimalsymbol',
      key: 'decimalsymbol',
      width: "11%",
      editable: true
    }, {
      title: '分组符号',
      dataIndex: 'groupsymbol',
      key: 'groupsymbol',
      width: "11%",
      editable: true
    }, {
      title: "去掉空格类型",
      dataIndex: 'trimType',
      key: 'trimType',
      selectable: true,
      selectArgs: [<Select.Option key="none" value="none">不去掉空格</Select.Option>,
      <Select.Option key="left" value="left">去掉左空格</Select.Option>,
      <Select.Option key="right" value="right">去掉右空格</Select.Option>,
      <Select.Option key="both" value="both">去掉左右两边空格</Select.Option>,
      ]
    }
  ];

  /*获取字段*/
  handleGetOutField() {
    const { getDetails, transname, text, panel } = this.props.model;
    const { getFieldValue } = this.props.form;
    let obj = {};
    obj.transName = transname;
    obj.stepName = text;
    obj.detailType = panel;

    let fileName = "";
    if (getFieldValue("templeteFile")) {
      fileName = "csv::" + getFieldValue("templeteFile");
    } else if (getFieldValue("filename")) {
      fileName = "data::" + getFieldValue("filename");
    }

    if (fileName) {
      obj.detailParam = {
        flag: "getFields",
        fileName: fileName,
        delimiter: getFieldValue("delimiter"),
        enclosure: getFieldValue("enclosure"),
        headerPresent: getFieldValue("headerPresent"),
        encoding: getFieldValue("encoding")
      }
      getDetails(obj, data => {
        if (data) {
          let args = [];
          let count = 0;
          for (let index of data) {
            count++;
            args.push({
              key: count,
              "name": index[0],
              "type": index[1],
              "format": "",
              "currencysymbol": null,
              "decimalsymbol": null,
              "groupsymbol": null,
              "length": "",
              "precision": "",
              "trimType": "none"
            })
          }
          this.refs.searchTable.updateTable(args, count);
        }
      })
    } else {
      message.error("文件与文件模板不可都为空！")
    }



  }

  /*插入分隔符*/
  insertTab() {
    const { setFieldsValue, getFieldValue } = this.props.form;
    let str = getFieldValue("delimiter");

    str = "\t" + str;

    setFieldsValue({
      delimiter: str
    });

  }


  /*设置文件名*/
  setFolder(str) {
    const { setFieldsValue } = this.props.form;


    if (str) {
      let str1 = str;
      if (this.state.useFolder) {
        str1 = this.state.path + str;
      }
      setFieldsValue({
        "filename": str1
      })
    }
  };

  /*文件模板*/
  getFieldList(name) {
    const { dispatch } = this.props;
    const { getFieldValue } = this.props.form;
    const { formatFolder, panel } = this.props.model;

    let obj = treeViewConfig.get(panel)[name];
    let path = name === "model" ? "" : formatFolder(getFieldValue("filename"));
    let updateModel = name === "model" ? this.setFolder1.bind(this) : this.setFolder.bind(this);

    let type = "";
    let viewPath = "";

    if (name != "model") {
      if (!this.state.useFolder) {
        type = "";
      } else {
        type = obj.obj.type;
        viewPath = this.state.path;
      }
    } else {
      type = obj.obj.type;
    }

    dispatch({
      type: "treeview/showTreeModel",
      payload: {
        ...obj,
        obj: {
          ...obj.obj,
          path: path,
          type: type
        },
        action:"quality",
        viewPath: viewPath,
        updateModel: updateModel
      }
    })
  };

  setFolder1(str) {
    const { setFieldsValue } = this.props.form;
    if (str) {
      setFieldsValue({
        "templeteFile": str
      })
    }
  };
  /*调用文件上传组件*/
  handleFileUpload(name) {
    const { dispatch } = this.props;
    const { panel } = this.props.model;
    let obj = treeUploadConfig.get(panel)[name];

    dispatch({
      type: "uploadfile/showModal",
      payload: {
        ...obj,
        action:"quality",
        visible: true
      }
    });
  };

  handleUseChange(e) {
    this.setState({
      useFolder: e.target.checked
    })
  }

  formItemLayout1 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 12 },
  };



  render() {
    const { getFieldDecorator } = this.props.form;
    const { text, config, visible, handleCheckName } = this.props.model;
    const { path, useFolder } = this.state;

    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };

    const formItemLayout2 = {
      labelCol: { span: 9 },
      wrapperCol: { span: 15 },
    };


    return (
      <Modal
        visible={visible}
        title="CSV文件输入"
        wrapClassName="vertical-center-modal limitText"
        okText="Create"
        width={800}
        maskClosable={false}
        onCancel={this.hideModal}
        footer={[
          <Button key="submit" type="primary" size="large" onClick={this.handleCreate} >
            确定
                  </Button>,
          <Button key="back" size="large" onClick={this.hideModal}>取消</Button>,
        ]}
      >
        <Form >
          <FormItem label="步骤名称"  {...formItemLayout} style={{ marginBottom: "8px" }}>
            {getFieldDecorator('text', {
              initialValue: text,
              rules: [{ whitespace: true, required: true, message: '请输入步骤名称' },
              { validator: handleCheckName, message: '步骤名称已存在，请更改!' }],
            })(
              <Input />
            )}
          </FormItem>
          <Row style={{ lineHeight: "40px" }}>
            <Col span={16} >
              <FormItem label="文件模板" style={{ marginBottom: "8px" }} {...formItemLayout2}>
                {getFieldDecorator('templeteFile', {
                  initialValue: config.templeteFile
                })(
                  <Input />
                )}
              </FormItem>
            </Col>
            <Col span={8} >
              <Button onClick={() => { this.getFieldList("model") }} >浏览</Button>
              <Button onClick={() => { this.handleFileUpload("model") }}>上传</Button>
            </Col>
          </Row>
          <FormItem label="服务器目录" style={{ marginBottom: "8px" }} {...this.formItemLayout1}>
            <Row>
              <Col span={18} className="ant-form-text">{path}</Col>
              <Col span={6} >
                <Checkbox onChange={this.handleUseChange.bind(this)} checked={useFolder}>是否使用</Checkbox>
              </Col>
            </Row>
          </FormItem>
          <Row style={{ lineHeight: "40px" }}>
            <Col span={16} >
              <FormItem label="文件" style={{ marginBottom: "8px" }} {...formItemLayout2}>
                {getFieldDecorator('filename', {
                  initialValue: config.filename
                })(
                  <Input />
                )}
              </FormItem>
            </Col>
            <Col span={8} >
              <Button onClick={() => { this.getFieldList("list") }}>浏览</Button>
              <Button onClick={() => { this.handleFileUpload("list") }} disabled={!useFolder}>上传</Button>
            </Col>
          </Row>
          <Row style={{ lineHeight: "40px" }}>
            <Col span={16} >
              <FormItem label="列分隔符"   {...formItemLayout2}>
                {getFieldDecorator('delimiter', {
                  initialValue: config.delimiter,
                  rules: [{ required: true, message: '请输入列分隔符' }]
                })(
                  <Input />
                )}
              </FormItem>
            </Col>
            <Col span={8} >
              <Button onClick={this.insertTab.bind(this)}>插入制表符</Button>
            </Col>
          </Row>
          <FormItem label="封闭符"  {...formItemLayout} style={{ marginBottom: "8px" }}>
            {getFieldDecorator('enclosure', {
              initialValue: config.enclosure
            })(
              <Input />
            )}
          </FormItem>
          <FormItem label="NIO 缓存大小"  {...formItemLayout} style={{ marginBottom: "8px" }}>
            {getFieldDecorator('bufferSize', {
              initialValue: config.bufferSize
            })(
              <Input />
            )}
          </FormItem>
          <Row style={{ marginLeft: "12%" }}>
            <Col span={12}>
              <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                {getFieldDecorator('lazyConversionActive', {
                  valuePropName: 'checked',
                  initialValue: config.lazyConversionActive
                })(
                  <Checkbox>简易转换？</Checkbox>
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                {getFieldDecorator('headerPresent', {
                  valuePropName: 'checked',
                  initialValue: config.headerPresent
                })(
                  <Checkbox >包含列头行</Checkbox>
                )}
              </FormItem>
            </Col>

          </Row>
          <FormItem style={{ marginBottom: "0px", marginLeft: "12%" }} {...this.formItemLayout}>
            {getFieldDecorator('isaddresult', {
              valuePropName: 'checked',
              initialValue: config.isaddresult
            })(
              <Checkbox >将文件添加到结果文件中</Checkbox>
            )}
          </FormItem>
          <FormItem label="行号字段(可选)"  {...formItemLayout} style={{ marginBottom: "8px" }}>
            {getFieldDecorator('rowNumField', {
              initialValue: config.rowNumField
            })(
              <Input />
            )}
          </FormItem>
          <Row style={{ marginLeft: "12%" }}>
            <Col span={12}>
              <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                {getFieldDecorator('runningInParallel', {
                  valuePropName: 'checked',
                  initialValue: config.runningInParallel
                })(
                  <Checkbox>并发运行？</Checkbox>
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                {getFieldDecorator('newlinePossibleInFields', {
                  valuePropName: 'checked',
                  initialValue: config.newlinePossibleInFields
                })(
                  <Checkbox >字段中有回车换行？</Checkbox>
                )}
              </FormItem>
            </Col>
          </Row>
          <FormItem label="文件编码" style={{ marginBottom: "8px" }} {...this.formItemLayout1}>
            {getFieldDecorator('encoding', {
              initialValue: config.encoding ? config.encoding : "GBK"
            })(
              <Select>
                <Select.Option value="GBK">GBK</Select.Option>
                <Select.Option value="ISO-8859-1">ISO-8859-1</Select.Option>
                <Select.Option value="GB2312">GB2312</Select.Option>
                <Select.Option value="UTF-8">UTF-8</Select.Option>
                <Select.Option value="Big5">Big5</Select.Option>
              </Select>
            )}
          </FormItem>
          <div style={{ margin: "0 5%" }}>
            <Row style={{ marginBottom: "5px" }}>
              <Col span={12}>
                <ButtonGroup size={"small"}>
                  <Button onClick={this.handleSearchAdd.bind(this)}>添加字段</Button>
                  <Button onClick={this.handleGetOutField.bind(this)}>获取字段</Button>
                </ButtonGroup>
              </Col>
              <Col span={12}>
                <Button style={{ float: "right" }} size={"small"} onClick={this.handleSearchFields.bind(this)}>删除字段</Button>
              </Col>
            </Row>

            <EditTable columns={this.columns} rowSelection={true} dataSource={this.state.searchFields} tableStyle="editTableStyle5" size={"small"} scroll={{ x: 1000, y: 140 }} ref="searchTable" count={4} />
          </div>

        </Form>
      </Modal>
    );
  }
}
const CsvInput = Form.create()(CsvDialog);

export default connect()(CsvInput);
