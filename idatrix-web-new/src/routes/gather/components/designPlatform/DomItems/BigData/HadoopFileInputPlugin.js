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
import withHdfs from "../../../common/withHdfs";

const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const Option = Select.Option;
const ButtonGroup = Button.Group;

class HadoopInputDialog extends React.Component {
  constructor(props) {
    super(props);
    const { visible, prevStepNames } = props.model;
    if (visible === true) {
      const { inputFiles, filters, fields } = props.model.config;

      let data = [];
      let data1 = [];
      let data2 = [];

      if (inputFiles) {
        let count = 0;
        for (let index of inputFiles) {
          data.push({
            key: count,
            ...index
          });
          count++;
        }
      }
      if (filters) {
        let count = 0;
        for (let index of filters) {
          data1.push({
            key: count,
            ...index
          });
          count++;
        }
      }
      if (fields) {
        let count = 0;
        for (let index of fields) {
          data2.push({
            key: count,
            ...index
          });
          count++;
        }
      }
      this.state = {
        fileSource: data,
        filterSource: data1,
        fieldsSource: data2,
        tableSelect: prevStepNames
      };
    }
  }

  handleAdd = () => {
    const { getFieldValue } = this.props.form;
    const { getFileExist } = this.props.model;
		const { hadoopName,selectName } = this.props.hdfsData;

    const file = hadoopName;
    const files = getFieldValue("files");

    let obj = {
      type: "hdfs",
      path: `${file}::${selectName}${files}`,
      depth: ""
    };

    getFileExist(obj, data => {
      if (data === "200") {
        const data = {
          sourceConfigurationName: file,
          fileName: `${selectName}${files}`,
          fileMask: "",
          excludeFileMask: "",
          fileRequired: "N",
          includeSubFolders: "N"
        };
        this.refs.editTable.handleAdd(data);
      }
    });
  };

  handleDeleteFields = () => {
    this.refs.editTable.handleDelete();
  };

  handleAdd1 = () => {
    const data = {
      filterString: "",
      filterPosition: "",
      filterIsLastLine: "false",
      filterIsPositive: "false"
    };
    this.refs.editTable1.handleAdd(data);
  };

  handleDeleteFields1 = () => {
    this.refs.editTable1.handleDelete();
  };

  handleAdd2() {
    const data = {
      name: "",
      type: "",
      format: "",
      currency: "",
      decimal: "",
      group: "",
      nullif: "",
      ifnull: "",
      position: "",
      length: "",
      precision: "",
      trimType: "",
      repeat: ""
    };
    this.refs.editTable2.handleAdd(data);
  }

  handleDeleteFields2() {
    this.refs.editTable2.handleDelete();
  }

  setModelHide() {
    const { dispatch } = this.props;
    dispatch({
      type: "items/hide",
      visible: false
    });
  }

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
    const { inputFiles, filters, fields } = config;

    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      let sendFields1 = [];
      let sendFields2 = [];
      if (this.refs.editTable) {
        if (this.refs.editTable.state.dataSource.length > 0) {
          let args = [
            "sourceConfigurationName",
            "fileName",
            "fileMask",
            "excludeFileMask",
            "fileRequired",
            "includeSubFolders"
          ];
          sendFields = formatTable(this.refs.editTable.state.dataSource, args);
        }
      } else {
        if (inputFiles) {
          sendFields = inputFiles;
        }
      }
      if (this.refs.editTable1) {
        if (this.refs.editTable1.state.dataSource.length > 0) {
          let args = [
            "filterString",
            "filterPosition",
            "filterIsLastLine",
            "filterIsPositive"
          ];

          sendFields1 = formatTable(
            this.refs.editTable1.state.dataSource,
            args
          );
        }
      } else {
        if (filters) {
          sendFields1 = filters;
        }
      }
      if (this.refs.editTable2) {
        if (this.refs.editTable2.state.dataSource.length > 0) {
          let args = [
            "name",
            "type",
            "format",
            "currency",
            "decimal",
            "group",
            "nullif",
            "ifnull",
            "position",
            "length",
            "precision",
            "trimType",
            "repeat"
          ];
          sendFields2 = formatTable(
            this.refs.editTable2.state.dataSource,
            args
          );
        }
      } else {
        if (fields) {
          sendFields2 = fields;
        }
      }

      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = text === values.text ? "" : values.text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        acceptingFilenames: values.acceptingFilenames,
        passingThruFields: values.passingThruFields,
        acceptingField: values.acceptingField,
        acceptingStepName: values.acceptingStepName,
        inputFiles: sendFields,
        content: {
          separator: values.separator,
          enclosure: values.enclosure,
          breakInEnclosureAllowed: values.breakInEnclosureAllowed,
          escapeCharacter: values.escapeCharacter,
          header: values.header,
          nrHeaderLines: values.nrHeaderLines,
          footer: values.footer,
          nrFooterLines: values.nrFooterLines,
          lineWrapped: values.lineWrapped,
          nrWraps: values.nrWraps,
          layoutPaged: values.layoutPaged,
          nrLinesPerPage: values.nrLinesPerPage,
          nrLinesDocHeader: values.nrLinesDocHeader,
          noEmptyLines: values.noEmptyLines,
          includeFilename: values.includeFilename,
          filenameField: values.filenameField,
          includeRowNumber: values.includeRowNumber,
          rowNumberByFile: values.rowNumberByFile,
          rowNumberField: values.rowNumberField,
          fileFormat: values.fileFormat,
          encoding: values.encoding,
          rowLimit: values.rowLimit,
          fileType: values.fileType,
          fileCompression: values.fileCompression,
          dateFormatLenient: values.dateFormatLenient,
          dateFormatLocale: values.dateFormatLocale
        },
        errorHandling: {
          errorIgnored: values.errorIgnored,
          skipBadFiles: values.skipBadFiles,
          fileErrorField: values.fileErrorField,
          fileErrorMessageField: values.fileErrorMessageField,
          errorLineSkipped: values.errorLineSkipped,
          errorCountField: values.errorCountField,
          errorFieldsField: values.errorFieldsField,
          errorTextField: values.errorTextField,
          warningFilesDestinationDirectory:
            values.warningFilesDestinationDirectory,
          warningFilesExtension: values.warningFilesExtension,
          errorFilesDestinationDirectory: values.errorFilesDestinationDirectory,
          errorFilesExtension: values.errorFilesExtension,
          lineNumberFilesDestinationDirectory:
            values.lineNumberFilesDestinationDirectory,
          lineNumberFilesExtension: values.lineNumberFilesExtension
        },
        filters: sendFields1,
        fields: sendFields2,
        additionalOutputFields: {
          shortFilenameField: values.shortFilenameField,
          pathField: values.pathField,
          hiddenField: values.hiddenField,
          lastModificationField: values.lastModificationField,
          uriField: values.uriField,
          rootUriField: values.rootUriField,
          extensionField: values.extensionField,
          sizeField: values.sizeField
        }
      };
      saveStep(obj, key, data => {
        if (data.code === "200") {
          this.setModelHide();
        }
      });
    });
  }

  /*文件表格*/
  fileColumns = [
    {
      title: "Environment",
      dataIndex: "sourceConfigurationName",
      width: "22%",
      key: "sourceConfigurationName",
      editable: true
    },
    {
      title: "File",
      dataIndex: "fileName",
      width: "20%",
      key: "fileName",
      editable: true
    },
    {
      title: "通配符",
      dataIndex: "fileMask",
      width: "15%",
      key: "fileMask",
      editable: true
    },
    {
      title: "通配符号(排除)",
      dataIndex: "excludeFileMask",
      width: "15%",
      key: "excludeFileMask",
      editable: true
    },
    {
      title: "要求",
      dataIndex: "fileRequired",
      key: "fileRequired",
      width: "12%",
      selectable: true,
      selectArgs: selectType.get("Y/N")
    },
    {
      title: "包含子目录",
      dataIndex: "includeSubFolders",
      key: "includeSubFolders",
      selectable: true,
      width: "13%",
      selectArgs: selectType.get("Y/N")
    }
  ];

  /*过滤表格*/
  filterColumns = [
    {
      title: "过滤字符串",
      dataIndex: "filterString",
      key: "filterString",
      editable: true
    },
    {
      title: "过滤器位置",
      dataIndex: "filterPosition",
      key: "filterPosition",
      editable: true
    },
    {
      title: "停止在过滤器",
      dataIndex: "filterIsLastLine",
      key: "filterIsLastLine",
      width: "17%",
      selectable: true,
      selectArgs: selectType.get("T/F")
    },
    {
      title: "积极匹配",
      dataIndex: "filterIsPositive",
      key: "filterIsPositive",
      selectable: true,
      width: "12%",
      selectArgs: selectType.get("T/F")
    }
  ];

  filedsColumns = [
    {
      title: "名称",
      dataIndex: "name",
      key: "name",
      width: "8%",
      editable: true
    },
    {
      title: "类型",
      dataIndex: "type",
      key: "type",
      width: "11%",
      selectable: true,
      selectArgs: selectType.get("type")
    },
    {
      title: "格式",
      dataIndex: "format",
      key: "format",
      editable: true
    },
    {
      title: "位置",
      dataIndex: "position",
      key: "position",
      width: "5%",
      editable: true
    },
    {
      title: "长度",
      dataIndex: "length",
      key: "length",
      width: "5%",
      editable: true
    },
    {
      title: "精度",
      dataIndex: "precision",
      key: "precision",
      width: "5%",
      editable: true
    },
    {
      title: "货币类型",
      dataIndex: "currency",
      key: "currency",
      width: "10%",
      editable: true
    },
    {
      title: "小数",
      dataIndex: "decimal",
      key: "decimal",
      width: "10%",
      editable: true
    },
    {
      title: "分组",
      dataIndex: "group",
      key: "group",
      width: "10%",
      editable: true
    },
    {
      title: "Null if",
      dataIndex: "nullif",
      key: "nullif",
      width: "10%",
      editable: true
    },
    {
      title: "默认",
      dataIndex: "ifnull",
      key: "ifnull",
      editable: true
    },
    {
      title: "去除空字符串方式",
      dataIndex: "trimType",
      key: "trimType",
      width: "10%",
      selectable: true,
      selectArgs: selectType.get("trimType")
    },
    {
      title: "重复",
      dataIndex: "repeat",
      key: "repeat",
      width: "5%",
      selectable: true,
      selectArgs: selectType.get("T/F")
    }
  ];

  formItemLayout1 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 }
  };
  formItemLayout = {
    wrapperCol: { span: 18 }
  };
  formItemLayout2 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 11 }
  };

  formItemLayout3 = {
    labelCol: { span: 8 },
    wrapperCol: { span: 14 }
  };
  formItemLayout4 = {
    labelCol: { span: 5 },
    wrapperCol: { span: 16 }
  };
  formItemLayout5 = {
    labelCol: { span: 5 },
    wrapperCol: { span: 14 }
  };
  formItemLayout6 = {
    labelCol: { span: 7 },
    wrapperCol: { span: 14 }
  };
  formItemLayout7 = {
    labelCol: { span: 9 },
    wrapperCol: { span: 14 }
  };

  formItemLayout8 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 15 }
  };

  formItemLayout9 = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 }
  };

  handleGetOutField() {
    const { getDetails, transname, text, panel, inputFiles } = this.props.model;
    const form = this.props.form;
    form.validateFields((err, values) => {
      let obj = {};
      let sendFields = [];
      let obj1 = {
        separator: values.separator,
        enclosure: values.enclosure,
        breakInEnclosureAllowed: values.breakInEnclosureAllowed,
        escapeCharacter: values.escapeCharacter,
        header: values.header,
        nrHeaderLines: values.nrHeaderLines,
        footer: values.footer,
        nrFooterLines: values.nrFooterLines,
        lineWrapped: values.lineWrapped,
        nrWraps: values.nrWraps,
        layoutPaged: values.layoutPaged,
        nrLinesPerPage: values.nrLinesPerPage,
        nrLinesDocHeader: values.nrLinesDocHeader,
        noEmptyLines: values.noEmptyLines,
        includeFilename: values.includeFilename,
        filenameField: values.filenameField,
        includeRowNumber: values.includeRowNumber,
        rowNumberByFile: values.rowNumberByFile,
        rowNumberField: values.rowNumberField,
        fileFormat: values.fileFormat,
        encoding: values.encoding,
        rowLimit: values.rowLimit,
        fileType: values.fileType,
        fileCompression: values.fileCompression,
        dateFormatLenient: values.dateFormatLenient,
        dateFormatLocale: values.dateFormatLocale
      };
      obj.transName = transname;
      obj.stepName = text;
      obj.detailType = panel;

      if (this.refs.editTable) {
        for (let index of this.refs.editTable.state.dataSource) {
          sendFields.push({
            sourceConfigurationName: index.sourceConfigurationName,
            fileName: index.fileName,
            fileMask: index.fileMask,
            excludeFileMask: index.excludeFileMask,
            fileRequired: index.fileRequired,
            includeSubFolders: index.includeSubFolders
          });
        }
      } else {
        if (inputFiles) {
          sendFields = inputFiles;
        }
      }
      obj.detailParam = {
        flag: "getFields",
        fileName:
          "hdfs::" +
          sendFields[0].sourceConfigurationName +
          "::" +
          sendFields[0].fileName,
        content: obj1
      };

      getDetails(obj, data => {
        if (data) {
          let args = [];
          let count = 0;
          for (let index of data) {
            count++;
            args.push({
              key: count,
              name: index[0],
              type: index[1],
              format: "",
              currency: "",
              decimal: "",
              group: "",
              nullif: "",
              ifnull: "",
              position: "-1",
              length: "-1",
              precision: "-1",
              trimType: 0,
              repeat: false
            });
          }
          this.refs.editTable2.updateTable(args, count);
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

  /*文件模板*/
  getFieldList(name) {
    const { dispatch } = this.props;
    const { getFieldValue } = this.props.form;
    const { formatFolder, panel } = this.props.model;
		const { hadoopName,selectName } = this.props.hdfsData;
		
		console.log(formatFolder(getFieldValue("files")));

    let str = hadoopName;

    if (str && str.trim()) {
      let obj = treeViewConfig.get(panel)[name];
      let path = formatFolder(`${selectName}${getFieldValue("files")}`)
        ? formatFolder(`${selectName}${getFieldValue("files")}`)
        : "/";
      let updateModel = this.setFolder.bind(this);
      dispatch({
        type: "treeview/showTreeModel",
        payload: {
          ...obj,
          obj: {
            ...obj.obj,
            path: str + "::" + path
          },
          prefixStr: str + "::",
          updateModel: updateModel
        }
      });
    } else {
      message.error("Hadoop环境不能为空！");
    }
  }

  /*设置文件名*/
  setFolder(str) {
    const { setFieldsValue } = this.props.form;
    if (str) {
			const { selectName } = this.props.hdfsData;
			const newStr = str.replace(selectName,"");
      setFieldsValue({
        files: newStr
      });
    }
  }

  render() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { visible, config, text, handleCheckName } = this.props.model;
    const { hadoopName } = this.props.hdfsData;
    const { selectBefore } = this.props;

    const setDisabled = () => {
      if (getFieldValue("acceptingFilenames") === undefined) {
        return config.acceptingFilenames;
      } else {
        if (getFieldValue("acceptingFilenames")) {
          return getFieldValue("acceptingFilenames");
        } else {
          return false;
        }
      }
    };

    const setDisabled1 = () => {
      if (getFieldValue("header") === undefined) {
        return config.content.header;
      } else {
        if (getFieldValue("header")) {
          return getFieldValue("header");
        } else {
          return false;
        }
      }
    };

    const setDisabled2 = () => {
      if (getFieldValue("footer") === undefined) {
        return config.content.footer;
      } else {
        if (getFieldValue("footer")) {
          return getFieldValue("footer");
        } else {
          return false;
        }
      }
    };

    const setDisabled3 = () => {
      if (getFieldValue("lineWrapped") === undefined) {
        return config.content.lineWrapped;
      } else {
        if (getFieldValue("lineWrapped")) {
          return getFieldValue("lineWrapped");
        } else {
          return false;
        }
      }
    };

    const setDisabled4 = () => {
      if (getFieldValue("layoutPaged") === undefined) {
        return config.content.layoutPaged;
      } else {
        if (getFieldValue("layoutPaged")) {
          return getFieldValue("layoutPaged");
        } else {
          return false;
        }
      }
    };

    const setDisabled5 = () => {
      if (getFieldValue("includeFilename") === undefined) {
        return config.content.includeFilename;
      } else {
        if (getFieldValue("includeFilename")) {
          return getFieldValue("includeFilename");
        } else {
          return false;
        }
      }
    };

    const setDisabled6 = () => {
      if (getFieldValue("includeRowNumber") === undefined) {
        return config.content.includeRowNumber;
      } else {
        if (getFieldValue("includeRowNumber")) {
          return getFieldValue("includeRowNumber");
        } else {
          return false;
        }
      }
    };

    const setDisabled7 = () => {
      if (getFieldValue("errorIgnored") === undefined) {
        return config.errorHandling.errorIgnored;
      } else {
        if (getFieldValue("errorIgnored")) {
          return getFieldValue("errorIgnored");
        } else {
          return false;
        }
      }
    };

    const setDisabled8 = () => {
      if (getFieldValue("skipBadFiles") === undefined) {
        return config.errorHandling.skipBadFiles;
      } else {
        if (getFieldValue("skipBadFiles")) {
          return getFieldValue("skipBadFiles");
        } else {
          return false;
        }
      }
    };

    return (
      <Modal
        visible={visible}
        title="Hadoop File Input"
        wrapClassName="vertical-center-modal"
        width={800}
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
                <div style={{ margin: "5px 0", width: "100%" }}>
                  <FormItem
                    {...this.formItemLayout1}
                    style={{ marginBottom: "8px" }}
                    label="Hadoop环境"
                  >
                    <span className="ant-form-text">{hadoopName}</span>
                  </FormItem>
                  <FormItem
                    label="源文件"
                    {...this.formItemLayout1}
                    style={{ marginBottom: "8px" }}
                  >
                    {getFieldDecorator("files", {
                      initialValue: ""
                    })(
                      <Input
                        addonBefore={selectBefore()}
                        disabled={setDisabled()}
                      />
                    )}
                    <Button
                      onClick={() => {
                        this.getFieldList("model");
                      }}
                      disabled={setDisabled()}
                    >
                      浏览
                    </Button>
                  </FormItem>

                  <Row style={{ marginBottom: "5px" }}>
                    <Col span={12}>
                      <p style={{ marginLeft: "5px" }}>选中的文件：</p>
                    </Col>
                    <Col span={12}>
                      <ButtonGroup size={"small"} style={{ float: "right" }}>
                        <Button
                          onClick={this.handleAdd.bind(this)}
                          disabled={setDisabled()}
                        >
                          添加文件
                        </Button>
                        <Button
                          onClick={this.handleDeleteFields.bind(this)}
                          disabled={setDisabled()}
                        >
                          删除文件
                        </Button>
                      </ButtonGroup>
                    </Col>
                  </Row>
                </div>
                <EditTable
                  disabled={setDisabled()}
                  columns={this.fileColumns}
                  tableStyle="editTableStyle5"
                  ref="editTable"
                  scroll={{ y: 140 }}
                  rowSelection={true}
                  size={"small"}
                  count={1}
                  dataSource={this.state.fileSource}
                />
                <p style={{ marginTop: 15 }}>从上一步骤获取文件名</p>
                <FormItem
                  style={{ marginBottom: "0px", marginLeft: "60px" }}
                  {...this.formItemLayout}
                >
                  {getFieldDecorator("acceptingFilenames", {
                    valuePropName: "checked",
                    initialValue: config.acceptingFilenames
                  })(<Checkbox>从以前的步骤接受文件名</Checkbox>)}
                </FormItem>
                <FormItem
                  style={{ marginBottom: "0px", marginLeft: "60px" }}
                  {...this.formItemLayout}
                >
                  {getFieldDecorator("passingThruFields", {
                    valuePropName: "checked",
                    initialValue: config.passingThruFields
                  })(
                    <Checkbox disabled={!setDisabled()}>
                      从以前的步骤接受字段名
                    </Checkbox>
                  )}
                </FormItem>
                <FormItem
                  label="步骤读取的文件名来自："
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout3}
                >
                  {getFieldDecorator("acceptingStepName", {
                    initialValue: config.acceptingStepName
                  })(
                    <Select disabled={!setDisabled()}>
                      {this.state.tableSelect.map(index => (
                        <Select.Option key={index}>{index}</Select.Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
                <FormItem
                  label="在输入里的字段被当做文件名："
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout3}
                >
                  {getFieldDecorator("acceptingField", {
                    initialValue: config.acceptingField
                  })(<Input disabled={!setDisabled()} />)}
                </FormItem>
              </TabPane>
              <TabPane tab="内容" key="2">
                <FormItem
                  label="文件类型"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("fileType", {
                    initialValue: config.content.fileType
                  })(
                    <Select>
                      <Option value="None">CSV</Option>
                    </Select>
                  )}
                </FormItem>
                <FormItem
                  label="分隔符"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout5}
                >
                  {getFieldDecorator("separator", {
                    initialValue: config.content.separator
                  })(<Input />)}
                  <Button onClick={this.insertTab.bind(this)}>插入TAB</Button>
                </FormItem>
                <FormItem
                  label="文本限定符"
                  style={{ marginBottom: "5px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("enclosure", {
                    initialValue: config.content.enclosure
                  })(<Input />)}
                </FormItem>
                <FormItem
                  style={{ marginBottom: "5px", marginLeft: "80px" }}
                  {...this.formItemLayout}
                >
                  {getFieldDecorator("breakInEnclosureAllowed", {
                    valuePropName: "checked",
                    initialValue: config.content.breakInEnclosureAllowed
                  })(<Checkbox>在文本限定符里允许换行？</Checkbox>)}
                </FormItem>
                <FormItem
                  label="逃逸字符"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("escapeCharacter", {
                    initialValue: config.content.escapeCharacter
                  })(<Input />)}
                </FormItem>
                <Row style={{ marginLeft: "10%", marginBottom: "8px" }}>
                  <Col span={4}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("header", {
                        valuePropName: "checked",
                        initialValue: config.content.header
                      })(<Checkbox>头部？</Checkbox>)}
                    </FormItem>
                  </Col>
                  <Col span={20}>
                    <FormItem
                      label="头部行数量"
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout6}
                    >
                      {getFieldDecorator("nrHeaderLines", {
                        initialValue: config.content.nrHeaderLines
                      })(<Input disabled={setDisabled1()} />)}
                    </FormItem>
                  </Col>
                </Row>
                <Row style={{ marginLeft: "10%", marginBottom: "8px" }}>
                  <Col span={4}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("footer", {
                        valuePropName: "checked",
                        initialValue: config.content.footer
                      })(<Checkbox>尾部？</Checkbox>)}
                    </FormItem>
                  </Col>
                  <Col span={20}>
                    <FormItem
                      label="尾部行数量"
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout6}
                    >
                      {getFieldDecorator("nrFooterLines", {
                        initialValue: config.content.nrFooterLines
                      })(<Input disabled={setDisabled2()} />)}
                    </FormItem>
                  </Col>
                </Row>
                <Row style={{ marginLeft: "10%", marginBottom: "8px" }}>
                  <Col span={4}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("lineWrapped", {
                        valuePropName: "checked",
                        initialValue: config.content.lineWrapped
                      })(<Checkbox>包装行？</Checkbox>)}
                    </FormItem>
                  </Col>
                  <Col span={20}>
                    <FormItem
                      label="以时间包装的行数"
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout6}
                    >
                      {getFieldDecorator("nrWraps", {
                        initialValue: config.content.nrWraps
                      })(<Input disabled={setDisabled3()} />)}
                    </FormItem>
                  </Col>
                </Row>
                <Row style={{ marginLeft: "10%", marginBottom: "8px" }}>
                  <Col span={4}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("layoutPaged", {
                        valuePropName: "checked",
                        initialValue: config.content.layoutPaged
                      })(<Checkbox>分页布局？</Checkbox>)}
                    </FormItem>
                  </Col>
                  <Col span={20}>
                    <FormItem
                      label="每页记录的行数"
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout6}
                    >
                      {getFieldDecorator("nrLinesPerPage", {
                        initialValue: config.content.nrLinesPerPage
                      })(<Input disabled={setDisabled4()} />)}
                    </FormItem>
                  </Col>
                </Row>
                <FormItem
                  label="文档头部行"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("nrLinesDocHeader", {
                    initialValue: config.content.nrLinesDocHeader
                  })(<Input disabled={setDisabled4()} />)}
                </FormItem>

                <Row style={{ marginLeft: "10%", marginBottom: "8px" }}>
                  <Col span={4}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("noEmptyLines", {
                        valuePropName: "checked",
                        initialValue: config.content.noEmptyLines
                      })(<Checkbox>没有空行</Checkbox>)}
                    </FormItem>
                  </Col>
                  <Col span={20}>
                    <FormItem
                      label="压缩"
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout4}
                    >
                      {getFieldDecorator("fileCompression", {
                        initialValue: config.content.fileCompression
                      })(
                        <Select>
                          <Option value="None">None</Option>
                          <Option value="Zip">Zip</Option>
                          <Option value="GZip">GZip</Option>
                          <Option disabled value="Hadoop-snappy">
                            Hadoop-snappy
                          </Option>
                          <Option value="Snappy">Snappy</Option>
                        </Select>
                      )}
                    </FormItem>
                  </Col>
                </Row>

                <Row style={{ marginLeft: "10%", marginBottom: "8px" }}>
                  <Col span={4}>
                    <FormItem
                      style={{ marginBottom: "0px", marginLeft: "-25px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("includeFilename", {
                        valuePropName: "checked",
                        initialValue: config.content.includeFilename
                      })(<Checkbox>在输出包括字段名？</Checkbox>)}
                    </FormItem>
                  </Col>
                  <Col span={18}>
                    <FormItem
                      label="包含文件名的字段名称"
                      style={{ marginBottom: "8px" }}
                      {...this.formItemLayout7}
                    >
                      {getFieldDecorator("filenameField", {
                        initialValue: config.content.filenameField
                      })(<Input disabled={!setDisabled5()} />)}
                    </FormItem>
                  </Col>
                </Row>

                <Row style={{ marginLeft: "10%", marginBottom: "8px" }}>
                  <Col span={8}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("includeRowNumber", {
                        valuePropName: "checked",
                        initialValue: config.content.includeRowNumber
                      })(<Checkbox>输出包含行数？</Checkbox>)}
                    </FormItem>
                  </Col>
                  <Col span={8}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("rowNumberByFile", {
                        valuePropName: "checked",
                        initialValue: config.content.rowNumberByFile
                      })(
                        <Checkbox disabled={!setDisabled6()}>
                          按文件取行号
                        </Checkbox>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <FormItem
                  label="行数字段名称"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("rowNumberField", {
                    initialValue: config.content.rowNumberField
                  })(<Input disabled={!setDisabled6()} />)}
                </FormItem>
                <FormItem
                  label="格式"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("fileFormat", {
                    initialValue: config.content.fileFormat
                  })(
                    <Select>
                      <Option value="None">DOS</Option>
                      <Option value="Zip">Unix</Option>
                      <Option value="GZip">mixed</Option>
                    </Select>
                  )}
                </FormItem>
                <FormItem
                  label="编码方式"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("encoding", {
                    initialValue: config.content.encoding
                      ? config.content.encoding
                      : "GBK"
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
                <FormItem
                  label="记录数量限制"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("rowLimit", {
                    initialValue: config.content.rowLimit
                  })(<Input />)}
                </FormItem>
                <Row style={{ marginLeft: "10%", marginBottom: "8px" }}>
                  <Col span={8}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("dateFormatLenient", {
                        valuePropName: "checked",
                        initialValue: config.content.dateFormatLenient
                      })(<Checkbox>解析日期时是否严格要求？</Checkbox>)}
                    </FormItem>
                  </Col>
                  <Col span={16}>
                    <FormItem
                      label="本地日期格式"
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout6}
                    >
                      {getFieldDecorator("dateFormatLocale", {
                        initialValue: config.content.dateFormatLocale
                      })(
                        <Select>
                          <Option value="zh_CN">zh_CN</Option>
                          <Option value="zh_HK">zh_HK</Option>
                          <Option value="en_US">en_US</Option>
                          <Option value="zh">zh</Option>
                        </Select>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <p style={{ marginLeft: "5%", marginTop: 15 }}>结果文件名</p>
                <FormItem
                  style={{ marginBottom: "0px", marginLeft: "60px" }}
                  {...this.formItemLayout}
                >
                  {getFieldDecorator("acceptingFilenames", {
                    valuePropName: "checked",
                    initialValue: config.acceptingFilenames
                  })(<Checkbox>添加文件名</Checkbox>)}
                </FormItem>
              </TabPane>
              <TabPane tab="错误处理" key="3">
                <Row style={{ marginLeft: "10%", marginBottom: "8px" }}>
                  <Col span={8}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("errorIgnored", {
                        valuePropName: "checked",
                        initialValue: config.errorHandling.errorIgnored
                      })(<Checkbox>忽略错误？</Checkbox>)}
                    </FormItem>
                  </Col>
                  <Col span={8}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("skipBadFiles", {
                        valuePropName: "checked",
                        initialValue: config.errorHandling.skipBadFiles
                      })(
                        <Checkbox disabled={!setDisabled7()}>
                          忽略错误文件
                        </Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={8}>
                    <FormItem
                      style={{ marginBottom: "0px" }}
                      {...this.formItemLayout}
                    >
                      {getFieldDecorator("errorLineSkipped", {
                        valuePropName: "checked",
                        initialValue: config.errorHandling.errorLineSkipped
                      })(
                        <Checkbox disabled={!setDisabled7()}>
                          跳过错误行？
                        </Checkbox>
                      )}
                    </FormItem>
                  </Col>
                </Row>
                <FormItem
                  label="错误文件字段名"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("fileErrorField", {
                    initialValue: config.errorHandling.fileErrorField
                  })(<Input disabled={!setDisabled7() || !setDisabled8()} />)}
                </FormItem>
                <FormItem
                  label="文件错误信息字段名"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout8}
                >
                  {getFieldDecorator("fileErrorMessageField", {
                    initialValue: config.errorHandling.fileErrorMessageField
                  })(<Input disabled={!setDisabled7() || !setDisabled8()} />)}
                </FormItem>
                <FormItem
                  label="错误计数字段"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("errorCountField", {
                    initialValue: config.errorHandling.errorCountField
                  })(<Input disabled={!setDisabled7()} />)}
                </FormItem>
                <FormItem
                  label="错误字段文件名"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("errorFieldsField", {
                    initialValue: config.errorHandling.errorFieldsField
                  })(<Input disabled={!setDisabled7()} />)}
                </FormItem>
                <FormItem
                  label="错误文本字段"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("errorTextField", {
                    initialValue: config.errorHandling.errorTextField
                  })(<Input disabled={!setDisabled7()} />)}
                </FormItem>
                <Row>
                  <Col span={15}>
                    <FormItem
                      label="告警文件目录"
                      style={{ marginBottom: "8px" }}
                      {...this.formItemLayout9}
                    >
                      {getFieldDecorator("warningFilesDestinationDirectory", {
                        initialValue:
                          config.errorHandling.warningFilesDestinationDirectory
                      })(<Input disabled={!setDisabled7()} />)}
                    </FormItem>
                  </Col>
                  <Col span={8}>
                    <FormItem
                      label="扩展名"
                      style={{ marginBottom: "8px" }}
                      {...this.formItemLayout9}
                    >
                      {getFieldDecorator("warningFilesExtension", {
                        initialValue: config.errorHandling.warningFilesExtension
                      })(<Input disabled={!setDisabled7()} />)}
                    </FormItem>
                  </Col>
                </Row>
                <Row>
                  <Col span={15}>
                    <FormItem
                      label="错误文件目录"
                      style={{ marginBottom: "8px" }}
                      {...this.formItemLayout9}
                    >
                      {getFieldDecorator("errorFilesDestinationDirectory", {
                        initialValue:
                          config.errorHandling.errorFilesDestinationDirectory
                      })(<Input disabled={!setDisabled7()} />)}
                    </FormItem>
                  </Col>
                  <Col span={8}>
                    <FormItem
                      label="扩展名"
                      style={{ marginBottom: "8px" }}
                      {...this.formItemLayout9}
                    >
                      {getFieldDecorator("errorFilesExtension", {
                        initialValue: config.errorHandling.errorFilesExtension
                      })(<Input disabled={!setDisabled7()} />)}
                    </FormItem>
                  </Col>
                </Row>
                <Row>
                  <Col span={15}>
                    <FormItem
                      label="失败行数文件目录"
                      style={{ marginBottom: "8px" }}
                      {...this.formItemLayout9}
                    >
                      {getFieldDecorator(
                        "lineNumberFilesDestinationDirectory",
                        {
                          initialValue:
                            config.errorHandling
                              .lineNumberFilesDestinationDirectory
                        }
                      )(<Input disabled={!setDisabled7()} />)}
                    </FormItem>
                  </Col>
                  <Col span={8}>
                    <FormItem
                      label="扩展名"
                      style={{ marginBottom: "8px" }}
                      {...this.formItemLayout9}
                    >
                      {getFieldDecorator("lineNumberFilesExtension", {
                        initialValue:
                          config.errorHandling.lineNumberFilesExtension
                      })(<Input disabled={!setDisabled7()} />)}
                    </FormItem>
                  </Col>
                </Row>
              </TabPane>
              <TabPane tab="过滤" key="4">
                <Row style={{ margin: "5px 0", width: "100%" }}>
                  <Col span={12}>
                    <Button size={"small"} onClick={this.handleAdd1.bind(this)}>
                      添加字段
                    </Button>
                  </Col>
                  <Col span={12}>
                    <Button
                      style={{ float: "right" }}
                      size={"small"}
                      onClick={this.handleDeleteFields1.bind(this)}
                    >
                      删除字段
                    </Button>
                  </Col>
                </Row>
                <EditTable
                  columns={this.filterColumns}
                  tableStyle="editTableStyle5"
                  ref="editTable1"
                  scroll={{ y: 300 }}
                  rowSelection={true}
                  size={"small"}
                  count={1}
                  dataSource={this.state.filterSource}
                />
              </TabPane>
              <TabPane tab="字段" key="5">
                <Row style={{ margin: "5px 0", width: "100%" }}>
                  <Col span={12} size={"small"}>
                    <Button size={"small"} onClick={this.handleAdd2.bind(this)}>
                      添加字段
                    </Button>
                    <Button
                      size={"small"}
                      onClick={this.handleGetOutField.bind(this)}
                    >
                      获取字段
                    </Button>
                  </Col>

                  <Col span={12} size={"small"}>
                    <Button
                      style={{ float: "right" }}
                      size={"small"}
                      onClick={this.handleDeleteFields2.bind(this)}
                    >
                      删除字段
                    </Button>
                  </Col>
                </Row>
                <EditTable
                  columns={this.filedsColumns}
                  tableStyle="editTableStyle5"
                  ref="editTable2"
                  scroll={{ y: 300, x: 1500 }}
                  rowSelection={true}
                  size={"small"}
                  count={1}
                  dataSource={this.state.fieldsSource}
                />
              </TabPane>
              <TabPane tab="其他输出字段" key="6">
                <FormItem
                  label="文件名字段"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("shortFilenameField", {
                    initialValue:
                      config.additionalOutputFields.shortFilenameField
                  })(<Input />)}
                </FormItem>
                <FormItem
                  label="扩展名字段"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("extensionField", {
                    initialValue: config.additionalOutputFields.extensionField
                  })(<Input />)}
                </FormItem>
                <FormItem
                  label="路径字段"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("pathField", {
                    initialValue: config.additionalOutputFields.pathField
                  })(<Input />)}
                </FormItem>
                <FormItem
                  label="文件大小字段"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("sizeField", {
                    initialValue: config.additionalOutputFields.sizeField
                  })(<Input />)}
                </FormItem>
                <FormItem
                  label="是否为隐藏文件字段"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("hiddenField", {
                    initialValue: config.additionalOutputFields.hiddenField
                  })(<Input />)}
                </FormItem>
                <FormItem
                  label="最后修改时间字段"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("lastModificationField", {
                    initialValue:
                      config.additionalOutputFields.lastModificationField
                  })(<Input />)}
                </FormItem>
                <FormItem
                  label="Uri字段"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("uriField", {
                    initialValue: config.additionalOutputFields.uriField
                  })(<Input />)}
                </FormItem>
                <FormItem
                  label="Root uri字段"
                  style={{ marginBottom: "8px" }}
                  {...this.formItemLayout4}
                >
                  {getFieldDecorator("rootUriField", {
                    initialValue: config.additionalOutputFields.rootUriField
                  })(<Input />)}
                </FormItem>
              </TabPane>
            </Tabs>
          </div>
        </Form>
      </Modal>
    );
  }
}
const HadoopFileInputPlugin = Form.create()(HadoopInputDialog);

export default connect()(withHdfs(HadoopFileInputPlugin,{
	isRead:true
}));
