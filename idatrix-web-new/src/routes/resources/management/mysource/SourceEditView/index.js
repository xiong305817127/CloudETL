import React from "react";
import {
  Form,
  Cascader,
  Input,
  Select,
  Radio,
  Button,
  Row,
  Col,
  Tooltip,
  message,
  Icon
} from "antd";
import { connect } from "dva";
import styles from "./index.less";
import EditTable from "../../../../gather/components/common/EditTable";
import { colTypeArgs } from "../../../constants";
import {
  getSchemasByDsId,
  getRentTablesBySchemaId
} from "services/metadataCommon";
import { hashHistory } from "react-router";
import _ from "lodash";

const FormItem = Form.Item;
const Option = Select.Option;
const { TextArea } = Input;
const RadioGroup = Radio.Group;
const RadioButton = Radio.Button;
const ButtonGroup = Button.Group;

const formatTreeList = (label, value, code, data) =>
  data.map(index => {
    index.value = index[value];
    index.label = index[label];
    index.code = index[code];
    if (index.children) {
      formatTreeList(label, value, code, index.children);
      return index;
    }
    return index;
  });

const checkIfTable = (rule, value, callback) => {
  if (value.length !== 2) {
    callback("请选择物理表");
  }
  callback();
};

//把数组的内容转换成字符串
const formatArgsToNumber = data => {
  if (!data) return;
  return data.map(index => parseInt(index));
};

//获得下拉框
const getInputSelect = (data, type) => {
  let args = [];
  for (let index of data) {
    args.push(
      <Option key={index[type]} {...index} value={index[type]}>
        {index[type]}
      </Option>
    );
  }
  return args;
};

//必填项
const getImportInfo = (info, title, type) => {
  return (
    <Tooltip title={title ? title : "必填项"}>
      <span>{info}</span>
      &nbsp;
      {type === "need" ? (
        <span style={{ fontSize: "16px", color: "red" }}>*</span>
      ) : (
        <Icon
          style={{ fontSize: "10px", color: "#faad14" }}
          type="exclamation-circle"
        />
      )}
    </Tooltip>
  );
};

//表单值发生变化
const handleFormChange = ({ dispatch }, changedValues) => {
  if (Object.keys(changedValues)[0] === "suggestion") {
    return false;
  }
  dispatch({ type: "sourceEditView/save", payload: { formChange: true } });
};

class index extends React.Component {
  constructor() {
    super();
    this.state = {
      canEdit: false,
      wuliEdit: false,
      sourceEdit: false
    };
  }

  componentWillMount() {
    const { dispatch, params, location } = this.props;
    dispatch({ type: "sourceEditView/getResourcesFolder" });
    dispatch({ type: "resourcesCommon/getDepartments" });
    dispatch({ type: "resourcesCommon/getAllDepartments" });
    dispatch({ type: "resourcesCommon/getServicesList" });
    dispatch({ type: "sourceEditView/getResourceTypeDict" });
    dispatch({ type: "sourceEditView/getResourceShareDict" });
    const { query } = location;
    if (params.id === "edit") {
      // dispatch({ type: "sourceEditView/getFields", payload: { name: e[0], tableName: label } });
      dispatch({
        type: "sourceEditView/getEditResource",
        payload: { id: query.id }
      });
    } else if (params.id === "new") {
      dispatch({ type: "sourceEditView/clear" });
    } else if (params.id === "check") {
      dispatch({ type: "sourceEditView/save", payload: { checkview: true } });
      dispatch({
        type: "sourceEditView/getEditResource",
        payload: { id: query.id }
      });
    }
  }

  componentWillReceiveProps(nextProps) {
    const {
      fieldsList,
      controlVisible,
      dataSource,
      shoudleUpdate
    } = nextProps.sourceEditView;
    const { dispatch } = this.props;

    if (this.refs.editTable) {
      let options = getInputSelect(fieldsList, "fieldName");
      this.refs.editTable.updateOptions({
        tableColCode: options
      });
    }
    if (shoudleUpdate && this.refs.editTable && controlVisible !== "database") {
      this.refs.editTable.updateTable(dataSource, dataSource.length);
      dispatch({
        type: "sourceEditView/save",
        payload: { shoudleUpdate: false }
      });
    }
  }

  columns = [
    {
      title: getImportInfo("信息项名称", "必填项", "need"),
      dataIndex: "colName",
      key: "colName",
      width: "18%",
      editable: true
    },
    {
      title: "数据类型",
      dataIndex: "colType",
      key: "colType",
      width: "12%",
      selectable: true,
      selectArgs: (() => {
        let args = [];
        for (let index of Object.keys(colTypeArgs)) {
          args.push(
            <Option key={index} value={index}>
              {colTypeArgs[index].title}
            </Option>
          );
        }
        return args;
      })()
    },
    {
      title: "物理表列名",
      dataIndex: "tableColCode",
      key: "tableColCode",
      width: "18%",
      bindField: "tableColType",
      bindFuc: (value, options) => {
        const { fieldType, fieldLength } = options;
        let str = fieldLength ? `(${fieldLength})` : "";
        if (options) {
          const { fieldType, fieldLength } = options;
          let str = fieldLength ? `(${fieldLength})` : "";
          return fieldType + str;
        }

        return fieldType + str;
      },
      selectable: true
    },
    {
      title: "列类型",
      dataIndex: "tableColType",
      key: "tableColType",
      width: "10%",
      disabled: true,
      editable: true
    },
    {
      title: getImportInfo("日期格式", "列类型为date时，才有效", "warning"),
      dataIndex: "dateFormat",
      key: "dateFormat",
      width: "20%",
      selectable: true,
      selectArgs: [
        <Option key="yyyy-MM-dd" value="yyyy-MM-dd">
          yyyy-MM-dd
        </Option>,
        <Option key="yyyy-MM-dd HH:mm:ss" value="yyyy-MM-dd HH:mm:ss">
          yyyy-MM-dd HH:mm:ss
        </Option>,
        <Option key="yyyy-MM-dd HH:mm" value="yyyy-MM-dd HH:mm">
          yyyy-MM-dd HH:mm
        </Option>,
        <Option key="yyyy/MM/dd" value="yyyy/MM/dd">
          yyyy/MM/dd
        </Option>,
        <Option key="yyyy/MM/dd HH:mm:ss" value="yyyy/MM/dd HH:mm:ss">
          yyyy/MM/dd HH:mm:ss
        </Option>,
        <Option key="yyyy/MM/dd HH:mm" value="yyyy/MM/dd HH:mm">
          yyyy/MM/dd HH:mm
        </Option>
      ]
    },
    {
      title: "唯一标识",
      dataIndex: "uniqueFlag",
      key: "uniqueFlag",
      width: "10%",
      noChange: true,
      selectable: true,
      selectArgs: [
        <Option key="true" value="true">
          是
        </Option>,
        <Option key="false" value="false">
          否
        </Option>
      ],
      bindField: "requiredFlag",
      bindFuc: (value, record) => {
        const { requiredFlag } = record;
        if (requiredFlag === "true") {
          return value === "false";
        } else {
          return value === "true";
        }
      }
    },
    {
      title: "订阅必选",
      dataIndex: "requiredFlag",
      key: "requiredFlag",
      selectable: true,
      noChange: true,
      selectArgs: [
        <Option key="true" value="true">
          是
        </Option>,
        <Option key="false" value="false">
          否
        </Option>
      ],
      bindField: "uniqueFlag",
      bindFuc: (value, record) => {
        const { uniqueFlag } = record;
        if (uniqueFlag === "true") {
          return value === "false";
        } else {
          return value === "true";
        }
      }
    }
  ];

  columns1 = [
    {
      title: getImportInfo("信息项名称", "必填项", "need"),
      dataIndex: "colName",
      key: "colName",
      width: "60%",
      editable: true
    },
    {
      title: "数据类型",
      dataIndex: "colType",
      key: "colType",
      selectable: true,
      selectArgs: (() => {
        let args = [];
        for (let index of Object.keys(colTypeArgs)) {
          args.push(
            <Option key={index} value={index}>
              {colTypeArgs[index].title}
            </Option>
          );
        }
        return args;
      })()
    }
  ];

  //资源分类
  handleChange = (e, options) => {
    if (!e) return;
    const { dispatch } = this.props;
    if (e.length > 0) {
      let args = [];
      for (let index of options) {
        args.push(index.code);
      }
      dispatch({
        type: "sourceEditView/save",
        payload: {
          catalogCode: args.join("") + "/"
        }
      });
    }
  };

  //资源提供方
  handleInputChange = (e, options) => {
    if (!e) return;
    const { dispatch } = this.props;
    dispatch({
      type: "sourceEditView/save",
      payload: {
        deptCode: options[options.length - 1].unifiedCreditCode,
        deptName: options[options.length - 1].label
      }
    });
  };

  //资源格式发生改变
  handleFormatTypeChange = (e, options) => {
    if (!e) return;
    const { dispatch, form } = this.props;
    let str = "other";
    if (e[0] === "3") {
      str = "database";
      form.setFieldsValue({ shareMethod: "1" });
      if (options && options.length > 0) {
        dispatch({
          type: "sourceEditView/getDatabase",
          payload: { dsType: options[1].code.toUpperCase() },
          force: true
        });
      }
    } else if (e[0] === "6") {
      form.setFieldsValue({ shareMethod: "2" });
      str = "info";
    } else if (e[0] === "7") {
      form.setFieldsValue({ shareMethod: "3" });
      str = "service";
    } else {
      form.setFieldsValue({ shareMethod: "2" });
    }

    if (options[1]) {
      const { code } = options[1];
      dispatch({
        type: "sourceEditView/save",
        payload: { name: e[0], tableName: code }
      });
    }
    dispatch({ type: "sourceEditView/save", payload: { controlVisible: str } });
  };

  // 资源格式数据加载
  // 物理表名修改
  loadData = selectedOptions => {
    const targetOption = selectedOptions[selectedOptions.length - 1];
    targetOption.loading = true;
    const { dispatch, sourceEditView } = this.props;
    const { value, dsId, valuelist } = targetOption;
    const { renterId } = this.props.account;
    const { bindTables } = sourceEditView;

    // 第二层数据
    const newDsId = selectedOptions[0].dsId;
    const { label } = selectedOptions[0];

    // 修改newDsId为schemaId
    getRentTablesBySchemaId({
      rentId: renterId,
      schemaId: newDsId
      // schemaId: valuelist
    }).then(res => {
      const { data, code } = res.data;
      if (code === "200") {
        const newBindTables = [...bindTables];
        const firstIndex = newBindTables.findIndex(val => {
          return val.label === label;
        });

        if (data && data.tableList && data.tableList.length !== 0) {
          newBindTables[firstIndex].loading = false;
          newBindTables[firstIndex].children = data.tableList.map(val => {
            return {
              value: val.id,
              label: val.name,
              idLeaf: true
            };
          });
        } else {
          newBindTables[firstIndex].children = [
            { value: "暂无数据表", label: "暂无数据表", disabled: true }
          ];
          newBindTables[firstIndex].isLeaf = true;
          newBindTables[firstIndex].loading = false;
        }

        dispatch({ type: "sourceEditView/save", payload: { newBindTables } });
      }
    });
    // 如果leaf不为true，则会触发length===2，动态加载内容
    // if (selectedOptions.length !== 1) {
    //   // 第二层数据
    //   const newDsId = selectedOptions[0].dsId;
    //   const { label } = selectedOptions[0];

    //   getRentTablesBySchemaId({
    //     rentId: renterId,
    //     dsId: newDsId,
    //     schemaId: valuelist
    //   }).then(res => {
    //     const { data, code } = res.data;
    //     if (code === "200") {
    //       const newBindTables = [...bindTables];
    //       const firstIndex = newBindTables.findIndex(val => {
    //         return val.label === label;
    //       });

    //       const secondIndex = newBindTables[firstIndex].children.findIndex(
    //         val => {
    //           return val.label === selectedOptions[1].label;
    //         }
    //       );

    //       if (data.tables.length !== 0) {
    //         newBindTables[firstIndex].children[secondIndex].loading = false;
    //         newBindTables[firstIndex].children[
    //           secondIndex
    //         ].children = data.tables.map(val => {
    //           return {
    //             value: val.id,
    //             label: val.tableName
    //           };
    //         });
    //       } else {
    //         newBindTables[firstIndex].children[secondIndex].children = [
    //           { value: "暂无数据表", label: "暂无数据表", disabled: true }
    //         ];
    //         newBindTables[firstIndex].children[secondIndex].isLeaf = true;
    //         newBindTables[firstIndex].children[secondIndex].loading = false;
    //       }

    //       dispatch({ type: "sourceEditView/save", payload: { newBindTables } });
    //     }
    //   });
    // } else {
    //   if (selectedOptions.length === 1) {
    //     getSchemasByDsId(dsId).then(res => {
    //       const { data, code } = res.data;
    //       targetOption.loading = false;
    //       if (code === "200") {
    //         for (let index of bindTables) {
    //           if (index.value === value) {
    //             index.children = [];
    //             for (let child of data) {
    //               index.children.push({
    //                 value: child.name,
    //                 valuelist: child.id,
    //                 label: child.name,
    //                 isLeaf: false
    //               });
    //             }
    //           }
    //         }
    //         dispatch({ type: "sourceEditView/save", payload: { bindTables } });
    //       }
    //     });
    //   }
    // }
  };

  //资源格式 选择完成
  handleResourceChange = (e, options) => {
    if (!e || !e[1]) return;
    const { dispatch } = this.props;
    /*alisa   2018-10-19  修改*/
    if (options[1]) {
      const { value } = options[1];
      dispatch({
        type: "sourceEditView/getFields",
        payload: { name: e[0], tableName: value }
      });
    } else {
      dispatch({
        type: "sourceEditView/getFields",
        payload: { name: e[0], tableName: value }
      });
    }
  };

  //共享条件变化
  handleShareTypeChange = e => {
    const { dispatch } = this.props;
    dispatch({ type: "sourceEditView/save", payload: { shareType: e } });
  };

  //取消
  handleCancel = () => {
    const { dispatch } = this.props;
    dispatch({ type: "sourceEditView/clear" });
    hashHistory.goBack();
  };

  //提交
  handleSubmit = (e, str) => {
    e.preventDefault();
    const { dispatch } = this.props;
    const {
      config,
      deptCode,
      deptName,
      catalogCode
    } = this.props.sourceEditView;
    let resourceColumnVOList = [];

    this.props.form.validateFields((err, val) => {
      const values = _.cloneDeep(val);

      if (!err) {
        let args = catalogCode.split("/");
        values.deptCode = deptCode;
        values.deptName = deptName;
        values.catalogCode = args[0];
        values.shareDeptArray = formatArgsToNumber(values.shareDeptArray);
        values.deptNameIdArray = formatArgsToNumber(
          values.deptNameIdArray
        ).join();

        // 如果没有编辑，则显示原有id
        values.catalogIdArray = values.catalogIdArray
          ? values.catalogIdArray
          : config.catalogIdArray;
        values.bindTableId = values.bindTableId
          ? values.bindTableId
          : config.bindTableId;
        values.formatType = val.formatType
          ? val.formatType[0]
          : config.formatType[0];
        values.formatInfo = val.formatType
          ? val.formatType[1]
          : config.formatInfo; //args[args.length-1];
        values.catalogIdArrayFake = null;
        if (values.bindTableId) {
          values.libTableId = values.bindTableId.join();
          values.bindTableId = parseInt(values.bindTableId[1]);
        }
        values.id = config.id ? config.id : 0;

        if (this.refs.editTable) {
          const data = this.refs.editTable.state.dataSource;
          let args = [];
          for (let index of data) {
            if (!index.colName) {
              let num = index.key;
              args.push(++num);
            }
          }
          if (args.length > 0) {
            message.error("提交失败！信息项" + args.join() + "项为空！");
            return false;
          }
          resourceColumnVOList = data;
        }
        dispatch({
          type: "sourceEditView/saveFormInfo",
          payload: { ...values, resourceColumnVOList },
          str
        });
      }
    });
  };

  //表格的增删
  handleAdd() {
    const { controlVisible } = this.props.sourceEditView;
    let data = null;

    if (controlVisible === "database") {
      data = {
        colName: "",
        colType: "C",
        tableColCode: "",
        TableColType: "",
        dateFormat: "",
        uniqueFlag: "false",
        requiredFlag: "false"
      };
    } else {
      data = {
        colName: "",
        colType: "C"
      };
    }
    this.refs.editTable.handleAdd(data);
  }
  handleDelete() {
    this.refs.editTable.handleDelete();
  }

  //不同意
  handleNoAgree = () => {
    const { dispatch } = this.props;
    const { getFieldValue } = this.props.form;
    const { config } = this.props.sourceEditView;
    const suggestion = getFieldValue("suggestion");

    if (suggestion && suggestion.trim()) {
      dispatch({
        type: "sourceEditView/getProcess",
        payload: { id: config.id, suggestion, action: "reject" }
      });
    } else {
      message.warn("审批拒绝时，审批意见不能为空！");
    }
  };

  //同意
  handleAgree = () => {
    const { getFieldValue } = this.props.form;
    const { dispatch } = this.props;
    const { formChange, dataSource, config } = this.props.sourceEditView;
    let change = false;
    const suggestion = getFieldValue("suggestion");

    if (this.refs.editable) {
      let newDataSource = this.refs.editable.state.dataSource;

      if (dataSource.length !== newDataSource.length) {
        change = true;
      } else {
        let i = 0;
        for (let index of dataSource) {
          for (let item of Object.keys(index)) {
            if (dataSource[i][item] !== newDataSource[i][item]) {
              change = true;
            }
          }
          i++;
        }
      }
    }

    if (formChange || change) {
      message.warning("参数值已经发生改变，请先保存后重试！");
    } else {
      dispatch({
        type: "sourceEditView/getProcess",
        payload: { id: config.id, suggestion, action: "agree" }
      });
    }
  };

  handleEdit = () => {
    this.setState({
      canEdit: true
    });
  };

  handleWuliEdit = () => {
    this.setState({
      wuliEdit: true
    });
  };

  handleSourceEdit = () => {
    this.setState({
      sourceEdit: true,
      wuliEdit: true
    });
  };

  //是否向社会开放
  handleOpenChange(e) {
    if (!e.target.value) return;
    const { dispatch } = this.props;
    dispatch({
      type: "sourceEditView/save",
      payload: { open: e.target.value === "1" }
    });
  }

  //验证资源分类级别
  checkId(rule, value, callback) {
    if (value && value.length >= 4) {
      callback();
    }
    callback("请选择符合规范的资源分类(大于等于四级)！");
  }

  loadDataType = selectedOptions => {
    const { dispatch } = this.props;
    const { resourcesList } = this.props.sourceEditView;

    const targetOption = selectedOptions[selectedOptions.length - 1];
    targetOption.loading = true;

    return new Promise(resolve => {
      dispatch({
        type: "sourceEditView/getResourcesFolder",
        resolve,
        targetOption,
        resourcesList,
        payload: { id: targetOption.id }
      });
    });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const {
      departmentsTree,
      allDepartments,
      servicesList
    } = this.props.resourcesCommon;
    const {
      config,
      controlVisible,
      shareType,
      catalogCode,
      bindTables,
      dataSource,
      checkview,
      open,
      ResourceFormat,
      ResourceDict,
      resourcesList
    } = this.props.sourceEditView;

    // const options = formatTreeList(
    // 	"resourceName",
    // 	"id",
    // 	"resourceEncode",
    // 	resourcesList
    // );

    const formItemLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      }
    };

    const children = [];
    for (let index of allDepartments) {
      index.id = String(index.id);
      children.push(
        <Option key={index.id} value={index.id}>
          {index.deptName}
        </Option>
      );
    }

    const displayRender = label => {
      return label[label.length - 1];
    };

    /* { pattern:/^[0-9a-zA-Z\_\u4e00-\u9fa5]+$/, message: '资源名称不能存在非法字符!' },*/
    return (
      <div className={styles.sourceEditView}>
        <div className={styles.innerContent}>
          <Form>
            <Row gutter={24}>
              <Col span={12}>
                {config.catalogName && !this.state.canEdit ? (
                  <FormItem label="资源分类" {...formItemLayout}>
                    {getFieldDecorator("catalogIdArrayFake", {
                      initialValue: config.catalogName
                        ? config.catalogName.split("/")
                        : [],
                      rules: [
                        { required: true, message: "资源分类不能为空！" },
                        { validator: this.checkId.bind(this) }
                      ]
                    })(
                      <span>
                        <Input
                          value={config.catalogName}
                          disabled
                          style={{ width: "70%" }}
                        />
                        <span
                          style={{
                            cursor: "pointer",
                            color: "red",
                            marginLeft: "8px"
                          }}
                          onClick={this.handleEdit}
                        >
                          点击修改
                        </span>
                      </span>
                    )}
                  </FormItem>
                ) : (
                  <FormItem label="资源分类" {...formItemLayout}>
                    {getFieldDecorator("catalogIdArray", {
                      initialValue: [],
                      rules: [
                        { required: true, message: "资源分类不能为空！" },
                        { validator: this.checkId.bind(this) }
                      ]
                    })(
                      <Cascader
                        placeholder="请选择资源分类"
                        options={resourcesList}
                        loadData={this.loadDataType}
                        onChange={this.handleChange.bind(this)}
                      />
                    )}
                  </FormItem>
                )}
              </Col>
              <Col span={12}>
                <FormItem label="资源名称" {...formItemLayout}>
                  {getFieldDecorator("name", {
                    initialValue: config.name ? config.name : "",
                    rules: [{ required: true, message: "资源名称不能为空！" }]
                  })(<Input placeholder="请填写资源名称" maxLength={50} />)}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem label="资源代码" {...formItemLayout}>
                  {getFieldDecorator("seqNum", {
                    initialValue: config.seqNum ? config.seqNum : "",
                    rules: [
                      { required: true, message: "资源代码不能为空！" },
                      {
                        pattern: /^[0-9]*$/,
                        message: "资源代码只能阿拉伯数字！"
                      }
                    ]
                  })(
                    <Input
                      addonBefore={catalogCode}
                      maxLength={6}
                      placeholder="请填写资源代码"
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem label="资源提供方" {...formItemLayout}>
                  {getFieldDecorator("deptNameIdArray", {
                    initialValue: config.deptNameIdArray
                      ? config.deptNameIdArray
                      : [],
                    rules: [{ required: true, message: "资源提供方不能为空！" }]
                  })(
                    <Cascader
                      placeholder="请选择资源提供方"
                      options={departmentsTree}
                      displayRender={displayRender}
                      onChange={this.handleInputChange.bind(this)}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem label="资源摘要" {...formItemLayout}>
                  {getFieldDecorator("remark", {
                    initialValue: config.remark ? config.remark : "",
                    rules: [{ required: true, message: "资源摘要不能为空！" }]
                  })(
                    <TextArea
                      placeholder="请填写资源摘要"
                      maxLength={500}
                      autosize
                    />
                  )}
                </FormItem>
              </Col>

              <Col span={12}>
                {!this.state.sourceEdit && (
                  <FormItem label="资源格式" {...formItemLayout}>
                    {getFieldDecorator("formatTypeFake")(
                      <span>
                        <Input
                          value={config.formatInfo}
                          disabled
                          style={{ width: "70%" }}
                        />
                        <span
                          style={{
                            cursor: "pointer",
                            color: "red",
                            marginLeft: "8px"
                          }}
                          onClick={this.handleSourceEdit}
                        >
                          点击修改
                        </span>
                      </span>
                    )}
                  </FormItem>
                )}
                {this.state.sourceEdit && (
                  <FormItem label="资源格式" {...formItemLayout}>
                    {getFieldDecorator("formatType", {
                      initialValue: config.formatType ? config.formatType : [],
                      rules: [{ required: true, message: "资源格式不能为空！" }]
                    })(
                      <Cascader
                        placeholder="请选择资源格式"
                        fieldNames={{
                          label: "name",
                          value: "code",
                          children: "childrenList"
                        }}
                        options={ResourceFormat}
                        onChange={this.handleFormatTypeChange}
                      />
                    )}
                  </FormItem>
                )}
              </Col>
              <Col
                span={12}
                style={{
                  display: controlVisible === "other" ? "none" : "block"
                }}
              >
                {controlVisible === "info" ? (
                  <FormItem label="自定义格式描述" {...formItemLayout}>
                    {getFieldDecorator("formatInfoExtend", {
                      initialValue: config.formatInfoExtend
                        ? config.formatInfoExtend
                        : "",
                      rules: [
                        { required: true, message: "自定义格式描述不能为空" }
                      ]
                    })(
                      <TextArea placeholder="请填写自定义格式描述" autosize />
                    )}
                  </FormItem>
                ) : null}
                {controlVisible === "database" && !this.state.wuliEdit ? (
                  <FormItem {...formItemLayout} label="物理表名">
                    {getFieldDecorator("bindTableIdFake")(
                      <span>
                        <Input
                          value={config.libTableId}
                          disabled
                          style={{ width: "70%" }}
                        />
                        <span
                          style={{
                            cursor: "pointer",
                            color: "red",
                            marginLeft: "8px"
                          }}
                          onClick={this.handleWuliEdit}
                        >
                          点击修改
                        </span>
                      </span>
                    )}
                  </FormItem>
                ) : null}
                {controlVisible === "database" && this.state.wuliEdit ? (
                  <FormItem {...formItemLayout} label="物理表名" hasFeedback>
                    {getFieldDecorator("bindTableId", {
                      initialValue: config.bindTableId
                        ? config.bindTableId
                        : "",
                      validateTrigger: "onChange",
                      rules: [
                        { required: true, message: "物理表名不能为空!" },
                        {
                          validator: checkIfTable,
                          message: "请选择对应的物理表或数据库"
                        }
                      ]
                    })(
                      <Cascader
                        placeholder="请选择物理表名"
                        options={bindTables}
                        loadData={this.loadData}
                        onChange={this.handleResourceChange}
                        changeOnSelect
                      />
                    )}
                  </FormItem>
                ) : null}
                {controlVisible === "service" ? (
                  <FormItem {...formItemLayout} label="服务名称">
                    {getFieldDecorator("bindServiceId", {
                      initialValue: config.bindServiceId
                        ? config.bindServiceId + ""
                        : "",
                      rules: [{ required: true, message: "服务名不能为空！" }]
                    })(
                      <Select placeholder="请选择服务名">
                        {servicesList.map(index => {
                          return (
                            <Option key={index.id} value={index.id + ""}>
                              {index.serviceName}
                            </Option>
                          );
                        })}
                      </Select>
                    )}
                  </FormItem>
                ) : null}
              </Col>
              <Col span={12}>
                <FormItem {...formItemLayout} label="共享类型" hasFeedback>
                  {getFieldDecorator("shareType", {
                    initialValue: config.shareType ? config.shareType : "1",
                    rules: [{ required: true, message: "共享类型不能为空" }]
                  })(
                    <Select
                      placeholder="请选择共享类型"
                      onChange={this.handleShareTypeChange.bind(this)}
                    >
                      <Option value="1">无条件共享</Option>
                      <Option value="2">有条件共享</Option>
                      <Option value="3">不予共享</Option>
                    </Select>
                  )}
                </FormItem>
              </Col>

              {shareType !== "3" ? (
                <div>
                  {shareType === "2" ? (
                    <Col span={12}>
                      <FormItem label="共享条件" {...formItemLayout}>
                        {getFieldDecorator("shareCondition", {
                          initialValue: config.shareCondition
                            ? config.shareCondition
                            : "",
                          rules: [
                            { required: true, message: "共享条件不能为空！" }
                          ]
                        })(<TextArea placeholder="请填写共享条件" autosize />)}
                      </FormItem>
                    </Col>
                  ) : null}
                  <Col span={12}>
                    <FormItem label="共享部门" {...formItemLayout}>
                      {getFieldDecorator("shareDeptArray", {
                        initialValue: config.shareDeptArray
                          ? config.shareDeptArray
                          : []
                      })(
                        <Select
                          mode="tags"
                          size={"default"}
                          placeholder="请选择共享部门"
                          style={{ width: "100%" }}
                        >
                          {children}
                        </Select>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={12}>
                    <FormItem {...formItemLayout} label="共享方式" hasFeedback>
                      {getFieldDecorator("shareMethod", {
                        initialValue: config.shareMethod
                          ? config.shareMethod + ""
                          : "2",
                        rules: [
                          { required: true, message: "共享方式不能为空！" }
                        ]
                      })(
                        <Select placeholder="请选择共享方式">
                          {ResourceDict.map(index => (
                            <Option value={index.code}>{index.name}</Option>
                          ))}
                        </Select>
                      )}
                    </FormItem>
                  </Col>
                </div>
              ) : null}
              <Col span={12}>
                <FormItem {...formItemLayout} label="更新周期">
                  {getFieldDecorator("refreshCycle", {
                    initialValue: config.refreshCycle
                      ? config.refreshCycle + ""
                      : "3",
                    rules: [{ required: true, message: "请选择更新周期！" }]
                  })(
                    <RadioGroup size={"small"}>
                      <RadioButton value="1">实时</RadioButton>
                      <RadioButton value="2">每日</RadioButton>
                      <RadioButton value="3">每周</RadioButton>
                      <RadioButton value="4">每月</RadioButton>
                      <RadioButton value="5">每季度</RadioButton>
                      <RadioButton value="6">每半年</RadioButton>
                      <RadioButton value="7">每年</RadioButton>
                    </RadioGroup>
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem {...formItemLayout} label="是否向社会开放">
                  {getFieldDecorator("openType", {
                    initialValue: config.openType ? config.openType + "" : "0",
                    rules: [
                      { required: true, message: "请选择是否向社会开放！" }
                    ]
                  })(
                    <RadioGroup
                      size={"small"}
                      onChange={this.handleOpenChange.bind(this)}
                    >
                      <RadioButton value="0">否</RadioButton>
                      <RadioButton value="1">是</RadioButton>
                    </RadioGroup>
                  )}
                </FormItem>
              </Col>
              {open ? (
                <Col span={12}>
                  <FormItem label="开放条件" {...formItemLayout}>
                    {getFieldDecorator("openCondition", {
                      initialValue: config.openCondition
                        ? config.openCondition
                        : "",
                      rules: [{ required: true, message: "开放条件不能为空！" }]
                    })(<TextArea placeholder="请填写开放条件" autosize />)}
                  </FormItem>
                </Col>
              ) : null}
              {checkview ? (
                <Col span={12}>
                  <FormItem label="审批意见" {...formItemLayout}>
                    {getFieldDecorator("suggestion", {
                      initialValue: config.suggestion ? config.suggestion : ""
                    })(<TextArea autosize />)}
                  </FormItem>
                </Col>
              ) : null}
            </Row>
          </Form>
          {controlVisible === "database" ? (
            <div style={{ margin: "0px 5%" }}>
              <Row style={{ margin: "10px 0", width: "100%" }}>
                <Col span={12}>
                  <ButtonGroup size={"small"}>
                    <Button onClick={this.handleAdd.bind(this)}>新增行</Button>
                    <Button onClick={this.handleDelete.bind(this)}>
                      删除行
                    </Button>
                  </ButtonGroup>
                </Col>
                {/* <Col span={12}  style={{textAlign:"right"}}>
		                      <Button size={"small"}>批量导入</Button>
		                    </Col>*/}
              </Row>
              <EditTable
                columns={this.columns}
                rowSelection={true}
                dataSource={dataSource}
                tableStyle="editTableStyle5"
                size={"small"}
                scroll={{ y: 140 }}
                ref="editTable"
                count={4}
              />
            </div>
          ) : null}
          {controlVisible !== "database" ? (
            <div style={{ margin: "0px 5%" }}>
              <Row style={{ margin: "10px 0", width: "100%" }}>
                <Col span={12}>
                  <ButtonGroup size={"small"}>
                    <Button onClick={this.handleAdd.bind(this)}>新增行</Button>
                    <Button onClick={this.handleDelete.bind(this)}>
                      删除行
                    </Button>
                  </ButtonGroup>
                </Col>
                {/* <Col span={12}  style={{textAlign:"right"}}>
		                      <Button size={"small"}>批量导入</Button>
		                    </Col>*/}
              </Row>
              <EditTable
                columns={this.columns1}
                rowSelection={true}
                dataSource={dataSource}
                tableStyle="editTableStyle5"
                size={"small"}
                scroll={{ y: 140 }}
                ref="editTable"
                count={4}
              />
            </div>
          ) : null}
          {!checkview ? (
            <Row>
              <Col span={24} style={{ textAlign: "center" }}>
                <Button
                  onClick={this.handleCancel.bind(this)}
                  style={{ marginRight: "20px" }}
                >
                  取消
                </Button>
                <Button
                  onClick={e => {
                    this.handleSubmit(e);
                  }}
                  type="primary"
                >
                  确定
                </Button>
              </Col>
            </Row>
          ) : (
            <Row>
              <Col span={24} style={{ textAlign: "center" }}>
                <Button
                  onClick={this.handleCancel.bind(this)}
                  style={{ marginRight: "20px" }}
                >
                  取消
                </Button>
                <Button
                  onClick={this.handleNoAgree.bind(this)}
                  style={{ marginRight: "20px" }}
                >
                  不同意
                </Button>
                <Button
                  onClick={e => {
                    this.handleSubmit(e, "save");
                  }}
                  type="primary"
                  style={{ marginRight: "20px" }}
                >
                  保存
                </Button>
                <Button onClick={this.handleAgree.bind(this)} type="primary">
                  同意
                </Button>
              </Col>
            </Row>
          )}
        </div>
      </div>
    );
  }
}

export default connect(({ resourcesCommon, sourceEditView, account }) => ({
  resourcesCommon,
  sourceEditView,
  account
}))(Form.create({ onValuesChange: handleFormChange })(index));
