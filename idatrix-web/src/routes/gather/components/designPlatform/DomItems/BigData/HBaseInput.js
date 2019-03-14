/*alisa */
import React from "react";
import { connect } from 'dva';
import { Form, Select, Button, Input, Checkbox, Tabs, Row, Col, AutoComplete, Radio, message } from 'antd';
import Modal from "components/Modal.js";
import EditTable from '../../../common/EditTable';
import { selectType } from '../../../../constant';

const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;
const ButtonGroup = Button.Group;


class HBaseInput extends React.Component {
  constructor(props) {
    super(props);
    const { visible, config } = props.model;
    if (visible === true) {
      const { outputFieldsDefinition, filtersDefinition, mapping } = config;
      let data = [];
      let data1 = [];
      let data2 = [];
      let data3 = [];
      //获取表1values值
      if (outputFieldsDefinition) {
        let count = 0;
        for (let index of outputFieldsDefinition) {
          data.push({
            "key": count++,
            keyword: "否",//是否唯一值默认状态
            ...index
          })
        }
      }
      //获取表2values值
      if (filtersDefinition) {
        let count = 0;
        for (let index of filtersDefinition) {
          data1.push({
            "key": count++,
            ...index
          })
        }
      }

      if (mapping) {
        if (mapping.mappedColumns) {
          let count = 0;
          for (let index of mapping.mappedColumns) {
            data2.push({
              "key": count++,
              ...index
            })
          }
        };
        data3 = [{
          "key": 0,
          keyType: index.keyType,
          keyword: true,  //是否唯一值默认状态
          tupleFamilies: index.tupleFamilies,
          tupleMapping: index.tupleMapping,
        }]
      }

      this.state = {
        ElasticList1: data, //用于存放表格1回显的数据
        ElasticList2: data1,  //用于存放表格2回显的数据

        ElasticList3: data3,  //用于存放表格3回显的数据
        ElasticList4: data2,  //用于存放表格4回显的数据

        hodoopList: [],  //获取hodoop集群
        HbaseList: [],   //获取Hbase表名
        MappList: [],    //获取存放关系
      }
    }
  }
  //进入界面请求hadoop接口
  componentDidMount() {
    this.Request();
  }

  /*hadoop查询*/
  Request() {
    const { getHadoopServer, config } = this.props.model;
    const { namedClusterName, coreConfigURL, defaultConfigURL, sourceTableName } = config;

    getHadoopServer(data => {
      if (data) {
        this.setState({
          hodoopList: data
        })
      }
    });

    if (namedClusterName && coreConfigURL) {
      //获取HBase表列名
      this.getSource("getTables", "one", {
        namedClusterName,
        siteConfig: coreConfigURL, defaultConfig: defaultConfigURL
      });

      //获取映射关系
      if (targetTableName) {
        this.getSource("getMappings", "one", {
          namedClusterName,
          siteConfig: coreConfigURL, defaultConfig: defaultConfigURL, tableName: sourceTableName
        });
      }
    }
  };

  //获取Hbase列名，获取Mapping列表公共方法
  //@edit by pwj   
  getSource(type, action, obj) {
    const { getDetails, transname, text, panel } = this.props.model;
    const { getFieldValue } = this.props.form;

    let params = {}
    //如果为首次请求,直接使用传入参数
    if (action === "one") {
      params = obj;
    } else {
      params.namedClusterName = getFieldValue("namedClusterName");  //hadoop集群名称
      params.siteConfig = getFieldValue("coreConfigURL");    // hbase-site.xml的URL名称
      params.defaultConfig = getFieldValue("defaultConfigURL");

      console.log(params, "参数1");
      //为获取Mappings,多增加参数
      if (type === "getMappings") {
        params.tableName = getFieldValue("sourceTableName");
        //添加提示
        if (!params.namedClusterName || !params.siteConfig || !params.tableName) {
          message.warn("Hadoop集群、hbase-site.xml的URL以及HBase表名都不能为空！")
          return;
        }
      } else if (type === "getMappingInfo") {
        params.tableName = getFieldValue("sourceTableName");
        params.mappingName = obj ? obj : getFieldValue("sourceMappingName");
        //添加提示
        if (!params.namedClusterName || !params.siteConfig || !params.tableName || !params.mappingName) {
          message.warn("Hadoop集群、hbase-site.xml的URL、映射名称以及HBase表名都不能为空！")
          return;
        }
      } else {
        //添加提示
        if (!params.namedClusterName || !params.siteConfig) {
          message.warn("Hadoop集群、hbase-site.xml的URL都不能为空！")
          return;
        }
      }
    }

    getDetails({
      transName: transname,
      stepName: text,
      detailType: panel,
      detailParam: {
        flag: type,
        ...params
      }
    }, data => {
      if (data) {
        let args = [];

        if (type === "getMappings") {
          for (let index of data) {
            args.push({ value: index })
          }
          this.setState({
            MappList: args
          })
        } else if (type === "getMappingInfo") {
          let args1 = [];
          let count = 0;
          //更新表1
          this.updateTable1(data);
          for (let key of data.mappedColumns) {
            args1.push({
              ...key,
              keyword: false,
              key: count++
            })
          }
          delete data.mappedColumns;
          args = [{ ...data }];
          //如果表单已经初始化
          if (this.refs.columns3) {
            this.refs.columns3.updateTable(args, 1);
            this.refs.columns4.updateTable(args1, count);
          }
        } else {
          for (let index of data) {
            args.push({ name: index })
          }
          this.setState({
            HbaseList: args
          })
        }
      }
    });
  }

  //更新表1的公共方法
  updateTable1(mapping) {
    let args = [];
    let count = 0;

    if (mapping) {
      args.push({
        key: count++,
        alias: mapping.keyword,
        keyword: mapping.tupleMapping,
        hbaseType: mapping.keyType,
        format: mapping.tupleFamilies
      });
      if (mapping.mappedColumns) {
        for (let index of mapping.mappedColumns) {
          args.push({
            key: count++,
            alias: index.alias,
            keyword: index.keyword,
            family: index.columnFamily,
            columnName: index.columnName,
            hbaseType: index.type,
            format: index.index,
          })
        }
      }
    }
    this.refs.columns1.updateTable(args, count);
  }

  //关闭弹框
  setModelHide() {
    const { dispatch } = this.props;
    dispatch({
      type: 'items/hide',
      visible: false,
    })
  }
  formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 },
  };

  columns1 = [
    {
      title: '别名',
      dataIndex: 'alias',
      key: 'alias',
      width: "15%",
    }, {
      title: '唯一key',
      dataIndex: 'keyword',
      key: 'keyword',
      width: "15%",
      render: (text) => <span>{text === true ? "是" : "否"}</span>,
    }, {
      title: '列族',
      dataIndex: 'family',
      key: 'family',
      width: "20%",
    }, {
      title: '列名称',
      dataIndex: 'columnName',
      key: 'columnName',
      width: "20%",
    }, {
      title: '类型',
      dataIndex: 'hbaseType',
      key: 'hbaseType',
      width: "15%",
      selectable: true,
      selectArgs: [
        <Select.Option key="String" value="String">String</Select.Option>,
        <Select.Option key="Integer" value="Integer">Integer</Select.Option>,
        <Select.Option key="Long" value="Long">Long</Select.Option>,
        <Select.Option key="Float" value="Float">Float</Select.Option>,
        <Select.Option key="Double" value="Double">Double</Select.Option>,
        <Select.Option key="Boolean" value="Boolean">Boolean</Select.Option>,
        <Select.Option key="Date" value="Date">Date</Select.Option>,
        <Select.Option key="BigNumber" value="BigNumber">BigNumber</Select.Option>,
        <Select.Option key="Serializable" value="Serializable">Serializable</Select.Option>,
        <Select.Option key="Binary" value="Binary">Binary</Select.Option>,
      ]
    }, {
      title: "格式",
      dataIndex: "format",
      key: "format",
      width: "20%",
      editable: true
    }];


  columns2 = [
    {
      title: '别名',
      dataIndex: 'alias',
      key: 'alias',
      width: "15%",
      editable: true
    }, {
      title: '类型',
      dataIndex: 'fieldType',
      key: 'fieldType',
      width: "20%",
      selectable: true,
      selectArgs: [
        <Select.Option key="String" value="String">String</Select.Option>,
        <Select.Option key="Integer" value="Integer">Integer</Select.Option>,
        <Select.Option key="Long" value="Long">Long</Select.Option>,
        <Select.Option key="Float" value="Float">Float</Select.Option>,
        <Select.Option key="Double" value="Double">Double</Select.Option>,
        <Select.Option key="Boolean" value="Boolean">Boolean</Select.Option>,
        <Select.Option key="Date" value="Date">Date</Select.Option>,
        <Select.Option key="BigNumber" value="BigNumber">BigNumber</Select.Option>,
        <Select.Option key="Serializable" value="Serializable">Serializable</Select.Option>,
        <Select.Option key="Binary" value="Binary">Binary</Select.Option>,
      ]
    }, {
      title: '比较值',
      dataIndex: 'comparisonType',
      key: 'comparisonType',
      width: "20%",
      selectable: true,
      selectArgs: selectType.get("symbol")
    }, {
      title: '操作者',
      dataIndex: 'constant',
      key: 'constant',
      width: "15%",
      editable: true,
    }, {
      title: '是否带符号',
      dataIndex: 'signedComparison',
      key: 'signedComparison',
      width: "15%",
      selectable: true,
      selectArgs: [<Select.Option key="Y" value="Y">是</Select.Option>,
      <Select.Option key="N" value="N">否</Select.Option>
      ]
    }, {
      title: '格式',
      dataIndex: 'format',
      key: 'format',
      width: "10%",
      editable: true
    }];

  columns3 = [
    {
      title: '别名',
      dataIndex: 'keyword',
      key: 'keyword',
      width: "30%",
      editable: true,
    }, {
      title: '唯一key',
      dataIndex: 'tupleMapping',
      key: 'tupleMapping',
      width: "15%",
      render: (text) => <span>{text === true ? "是" : "否"}</span>
    }, {
      title: '类型',
      dataIndex: 'keyType',
      key: 'keyType',
      width: "25%",
      selectable: true,
      selectArgs: [
        <Select.Option key="String" value="String">String</Select.Option>,
        <Select.Option key="Integer" value="Integer">Integer</Select.Option>,
        <Select.Option key="Long" value="Long">Long</Select.Option>,
        <Select.Option key="Float" value="Float">Float</Select.Option>,
        <Select.Option key="Double" value="Double">Double</Select.Option>,
        <Select.Option key="Boolean" value="Boolean">Boolean</Select.Option>,
        <Select.Option key="Date" value="Date">Date</Select.Option>,
        <Select.Option key="BigNumber" value="BigNumber">BigNumber</Select.Option>,
        <Select.Option key="Serializable" value="Serializable">Serializable</Select.Option>,
        <Select.Option key="Binary" value="Binary">Binary</Select.Option>,
      ]
    }, {
      title: '索引值',
      dataIndex: 'tupleFamilies',
      key: 'tupleFamilies',
      editable: true
    }];

  columns4 = [
    {
      title: '别名',
      dataIndex: 'alias',
      key: 'alias',
      width: "20%",
      editable: true,
    }, {
      title: '唯一key',
      dataIndex: 'keyword',
      key: 'keyword',
      width: "15%",
      render: (text) => <span>{text === true ? "是" : "否"}</span>,
    }, {
      title: '列族',
      dataIndex: 'columnFamily',
      key: 'columnFamily',
      width: "15%",
      selectable: true,
      selectArgs: [],
    }, {
      title: '列名称',
      dataIndex: 'columnName',
      key: 'columnName',
      width: "20%",
      editable: true,
    }, {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: "20%",
      selectable: true,
      selectArgs: [
        <Select.Option key="String" value="String">String</Select.Option>,
        <Select.Option key="Integer" value="Integer">Integer</Select.Option>,
        <Select.Option key="Long" value="Long">Long</Select.Option>,
        <Select.Option key="Float" value="Float">Float</Select.Option>,
        <Select.Option key="Double" value="Double">Double</Select.Option>,
        <Select.Option key="Boolean" value="Boolean">Boolean</Select.Option>,
        <Select.Option key="Date" value="Date">Date</Select.Option>,
        <Select.Option key="BigNumber" value="BigNumber">BigNumber</Select.Option>,
        <Select.Option key="Serializable" value="Serializable">Serializable</Select.Option>,
        <Select.Option key="Binary" value="Binary">Binary</Select.Option>,
      ]
    }, {
      title: '索引值',
      dataIndex: 'index',
      key: 'index',
      width: "10%",
      editable: true,
    }
  ]

  //保存接口
  handleCreate() {
    const form = this.props.form;
    const { panel, transname, description, key, saveStep, config, text, formatTable } = this.props.model;
    const { outputFieldsDefinition, filtersDefinition, mappedColumns, mapping } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }

      let sendFields = [];  //提交的时候把表1的数据保存到sendFields，然后进行过滤提交
      let sendFields1 = [];  //提交的时候把表4的数据保存到sendFields，然后进行过滤提交
      let sendFields2 = [];   //提交的时候把表2的数据保存到sendFields，然后进行过滤提交
      let sendFields3 = [];   //提交的时候把表3的数据保存到sendFields，然后进行过滤提交

      let newMapping = mapping;
      //表单1的渲染
      if (this.refs.columns1) {
        if (this.refs.columns1.state.dataSource.length > 0) {
          let args = ["alias", "keyword", "family", "columnName", "hbaseType", "format"];
          sendFields = formatTable(this.refs.columns1.state.dataSource, args);
        }
      } else {
        if (outputFieldsDefinition) {
          sendFields = outputFieldsDefinition
        }
      }
      //表单4的渲染
      if (this.refs.columns2) {
        if (this.refs.columns2.state.dataSource.length > 0) {
          let args = ["alias", "fieldType", "comparisonType", "signedComparison", "constant", "format"];
          sendFields1 = formatTable(this.refs.columns2.state.dataSource, args);
        }
      } else {
        if (filtersDefinition) {
          sendFields1 = filtersDefinition
        }
      }
      //表单3的渲染
      if (this.refs.columns3) {
        if (this.refs.columns3.state.dataSource.length > 0) {
          let args = ["keyword", "tupleMapping", "keyType", "tupleFamilies"];
          sendFields2 = formatTable(this.refs.columns3.state.dataSource, args);
        }
      } else {
        if (mapping) {
          sendFields2 = {};
        }
      }
      //表单2的渲染
      if (this.refs.columns4) {
        if (this.refs.columns4.state.dataSource.length > 0) {
          let args = ["alias", "columnFamily", "columnName", "type", "index", "keyword"];
          sendFields3 = formatTable(this.refs.columns4.state.dataSource, args);
        }
      } else {
        if (mapping) {
          sendFields3 = []
        }
      }

      //当表1的值为数组时，格式化获得的新值
      if (sendFields2 instanceof Array) {
        newMapping = {
          ...sendFields2[0],
          mappingName: values.targetMappingName,
          tableName: values.targetTableName,
          mappedColumns: sendFields3
        }
      }

      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = (text === values.text ? "" : values.text);
      obj.type = panel;
      obj.description = description;
      obj.config = {
        ...values,
        saveMappingToMeta:true,
        mapping: newMapping,
        outputFieldsDefinition: sendFields,
        filtersDefinition: sendFields1,
      };
      saveStep(obj, key, data => {
        if (data.code === "200") {
          this.setModelHide();
        }
      });
    })
  }

  //key表的增删改
  handleAdd1() {
    if (this.refs.columns3 && this.refs.columns3.state.dataSource.length > 0) {
      message.warn("key表只允许添加一行！");
      return false;
    }
    const data = {
      "keyword": "",
      "tupleMapping": true,
      "keyType": "String",
      "tupleFamilies": "",
    }
    this.refs.columns3.handleAdd(data);
  }
  handleDeleteFields1() {
    this.refs.columns3.handleDelete();
  }

  //value的增删改
  handleAddValue() {
    const data = {
      "alias": "",
      "columnFamily": "",
      "columnName": "",
      "keyword": false,
      "type": "String",
      "index": "",
    }
    this.refs.columns4.handleAdd(data);
  }
  handleDeleteFieldsValue() {
    this.refs.columns4.handleDelete();
  }

  handleAdd2() {
    const data = {
      "alias": "",
      "fieldType": "",
      "comparisonType": "",
      "signedComparison": "",
      "constant": "",
      "format": "",
    }
    this.refs.columns2.handleAdd(data);
  }
  handleDeleteFields2() {
    this.refs.columns2.handleDelete();
  }

  //点击创建mapping静态列表
  handleCreateMapping = () => {
    //表3的静态列表
    const data = [{
      key: 0,
      "keyword": "key",
      "tupleMapping": true,
      "keyType": "String",
      "tupleFamilies": "",
    }];
    //表4的静态列表
    const data1 = [{
      key: 0,
      "alias": "Family",
      "columnFamily": "",
      "columnName": "",
      "keyword": false,
      "type": "String",
      "index": "",
    }, {
      key: 1,
      "alias": "Column",
      "columnFamily": "",
      "columnName": "",
      "keyword": false,
      "type": "String",
      "index": "",
    }, {
      key: 2,
      "alias": "Value",
      "columnFamily": "",
      "columnName": "",
      "keyword": false,
      "type": "String",
      "index": "",
    }, {
      key: 3,
      "alias": "Timestamp",
      "columnFamily": "",
      "columnName": "",
      "keyword": false,
      "type": "String",
      "index": "",
    }]
    this.refs.columns3.updateTable(data, 1);
    this.refs.columns4.updateTable(data1, 4);
  }

  //保存映射关系
  //保存映射关系的时候，格式与保存提交的mapping一致
  createMapping = () => {
    const { getDetails, transname, text, panel } = this.props.model;
    const { getFieldValue } = this.props.form;
    let table1 = this.refs.columns3.state.dataSource;
    let table2 = this.refs.columns4.state.dataSource;
    let namedClusterName = getFieldValue("namedClusterName");  //hadoop集群名称
    let siteConfig = getFieldValue("coreConfigURL");    // hbase-site.xml的URL名称
    let defaultConfig = getFieldValue("defaultConfigURL"); //hbase-site.xmlhbase-default.xml的UR
    let mappingName = getFieldValue("sourceMappingName");
    let tableName = getFieldValue("sourceTableName");

    if (!namedClusterName || !siteConfig || !mappingName || !tableName) {
      message.warn("Hadoop集群、hbase-site.xml的URL、HBase表名以及映射名称值都不能为空！");
      return false;
    }

    if (table1.length === 0) {
      message.warn("表1必须有一条数据！");
      return false;
    }

    let mapping = {
      mappingName, tableName,
      keyword: table1[0].keyword,
      keyType: table1[0].keyType,
      tupleFamilies: table1[0].tupleFamilies,
      tupleMapping: true,
      mappedColumns: table2
    }

    getDetails({
      transName: transname,
      stepName: text,
      detailType: panel,
      detailParam: {
        flag: "createMapping",
        namedClusterName,
        siteConfig,
        defaultConfig,
        mapping,
      }
    }, data => {
      //更新表1
      this.updateTable1(mapping);
      this.getSource("getMappings");
      message.success("创建成功！");
    });
  }
  //删除映射关系
  deleteMapping = () => {
    const { getDetails, transname, text, panel } = this.props.model;
    const { getFieldValue, resetFields } = this.props.form;
    let namedClusterName = getFieldValue("namedClusterName");  //hadoop集群名称
    let siteConfig = getFieldValue("coreConfigURL");    // hbase-site.xml的URL名称
    let defaultConfigURL = getFieldValue("defaultConfigURL"); //hbase-site.xmlhbase-default.xml的UR
    let tableName = getFieldValue("sourceTableName");  //Hbase表名称
    let mappingName = getFieldValue("sourceMappingName");  //映射名称

    if (!namedClusterName || !siteConfig || !mappingName || !tableName) {
      message.warn("Hadoop集群、hbase-site.xml的URL、HBase表名以及映射名称值都不能为空！");
      return false;
    }

    getDetails({
      transName: transname,
      stepName: text,
      detailType: panel,
      detailParam: {
        flag: "deleteMapping",
        namedClusterName,
        siteConfig,
        defaultConfigURL,
        mappingName,
        tableName,
      }
    }, data => {
      this.getSource("getMappings");
      resetFields(["sourceMappingName"]);
      this.refs.columns3.updateTable([], 0);
      this.refs.columns4.updateTable([], 0);
      this.refs.columns1.updateTable([], 0);
      message.success("删除成功！");
    });
  }

  //切换Hbasename 重置mapName
  handleHbaseChange() {
    const { setFieldsValue } = this.props.form;
    setFieldsValue({ sourceMappingName: "" });
  }

  //切换Mapping 更新表格
  handleMappingChange() {
    this.getSource("getMappingInfo");
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const { visible, config, text, handleCheckName } = this.props.model;

    return (
      <Modal
        visible={visible}
        title="HBase输入"
        wrapClassName="vertical-center-modal"
        width={770}
        footer={[
          <Button key="submit" type="primary" size="large" onClick={this.handleCreate.bind(this)}>确定</Button>,
          <Button key="back" size="large" onClick={this.setModelHide.bind(this)}>取消</Button>,
        ]}
        maskClosable={false}
        onCancel={this.setModelHide.bind(this)}
      >
        <Form >
          <FormItem label="步骤名称"  {...this.formItemLayout}>
            {getFieldDecorator('text', {
              initialValue: text,
              rules: [{ whitespace: true, required: true, message: '请输入步骤名称' },
              { validator: handleCheckName, message: '步骤名称已存在，请更改!' }]
            })(
              <Input />
            )}
          </FormItem>
          <div style={{ margin: "0 5%" }}>
            <Tabs type="card">
              <TabPane tab="配置连接" key="1">
                <FormItem label="Hadoop集群" style={{ marginBottom: "8px" }} {...this.formItemLayout}>
                  {getFieldDecorator('namedClusterName', {
                    initialValue: config.namedClusterName
                  })(
                    <Select >
                      {this.state.hodoopList.map(index => {
                        return (
                          <Option key={index.name}>{index.name}</Option>
                        )
                      })
                      }
                    </Select>
                  )}
                </FormItem>
                <FormItem label="hbase-site.xml的URL" style={{ marginBottom: "8px" }} {...this.formItemLayout}>
                  {getFieldDecorator('coreConfigURL', {
                    initialValue: config.coreConfigURL ? config.coreConfigURL : "${haddopPluginConfigPath}/hbase-site.xml"
                  })(
                    <Input />
                  )}
                </FormItem>

                <FormItem label="hbase-default.xml的URL" style={{ marginBottom: "8px" }} {...this.formItemLayout}>
                  {getFieldDecorator('defaultConfigURL', {
                    initialValue: config.defaultConfigURL
                  })(
                    <Input />
                  )}
                </FormItem>

                <FormItem label="HBase表名" style={{ marginBottom: "8px" }} {...this.formItemLayout}>
                  {getFieldDecorator('sourceTableName', {
                    initialValue: config.sourceTableName
                  })(
                    <Select onFocus={() => { this.getSource("getTables") }} onChange={this.handleHbaseChange.bind(this)}  >
                      {this.state.HbaseList.map(index => {
                        return (
                          <Option key={index.name} value={index.name} >{index.name}</Option>
                        )
                      })
                      }
                    </Select>
                  )}
                </FormItem>
                <FormItem label="映射名称" style={{ marginBottom: "8px" }} {...this.formItemLayout}>
                  {getFieldDecorator('sourceMappingName', {
                    initialValue: config.sourceMappingName
                  })(
                    <Select onFocus={() => { this.getSource("getMappings") }} onSelect={(e) => { this.getSource("getMappingInfo", "ohter", e) }} >
                      {this.state.MappList.map(index => {
                        return (
                          <Option key={index.value} value={index.value} >{index.value}</Option>
                        )
                      })
                      }
                    </Select>
                  )}
                </FormItem>
                <FormItem label="启动扫描的键值（包括）" style={{ marginBottom: "8px" }} {...this.formItemLayout}>
                  {getFieldDecorator('keyStart', {
                    initialValue: config.keyStart,
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="存储表的键值（含）" style={{ marginBottom: "8px" }} {...this.formItemLayout}>
                  {getFieldDecorator('keyStop', {
                    initialValue: config.keyStop,
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="扫描行缓存大小" style={{ marginBottom: "8px" }} {...this.formItemLayout}>
                  {getFieldDecorator('scannerCacheSize', {
                    initialValue: config.scannerCacheSize
                  })(
                    <Input />
                  )}
                </FormItem>
                <Row style={{ margin: "5px 0", width: "100%" }}  >
                  <Col span={12} >
                    <Button size={"small"} onClick={() => { this.getSource("getMappingInfo") }} >获取字段</Button>
                  </Col>
                </Row>
                <EditTable columns={this.columns1} dataSource={this.state.ElasticList1} extendDisabled={true} scroll={{ y: 300 }} size={"small"} ref="columns1" count={0} />
              </TabPane>
              <TabPane tab="创建/编辑映射" key="2">
                <FormItem label="HBase表名" style={{ marginBottom: "8px" }} {...this.formItemLayout}>
                  {getFieldDecorator('sourceTableName', {
                    initialValue: config.sourceTableName
                  })(
                    <Select onFocus={() => { this.getSource("getTables") }} onChange={this.handleHbaseChange.bind(this)} >
                      {this.state.HbaseList.map(index => {
                        return (
                          <Option key={index.name} value={index.name} >{index.name}</Option>
                        )
                      })
                      }
                    </Select>
                  )}
                </FormItem>
                <FormItem label="映射名称" style={{ marginBottom: "8px" }} {...this.formItemLayout}>
                  {getFieldDecorator('sourceMappingName', {
                    initialValue: config.sourceMappingName ? config.sourceMappingName : ""
                  })(
                    <AutoComplete
                      style={{ width: '100%' }}
                      dataSource={this.state.MappList.map(index => index.value)}
                      onFocus={() => { this.getSource("getMappings") }}
                      onSelect={(e) => { this.getSource("getMappingInfo", "other", e) }}
                    />
                  )}
                </FormItem>

                <Row style={{ margin: "5px 0", width: "100%" }}  >
                  <Col span={12} >
                    <ButtonGroup size={"small"} >
                      <Button onClick={this.handleAdd1.bind(this)}>添加字段</Button>
                      <Button onClick={() => { this.getSource("getMappingInfo") }}>获取字段</Button>
                    </ButtonGroup>
                  </Col>
                  <Col span={12} style={{ textAlign: "right" }} >
                    <ButtonGroup size={"small"} >
                      <Button onClick={this.handleDeleteFields1.bind(this)} >删除字段</Button>
                    </ButtonGroup>
                  </Col>
                </Row>
                <EditTable columns={this.columns3} dataSource={this.state.ElasticList3} ref="columns3" size={"small"} scroll={{ y: 300 }} rowSelection={true} count={0} />
                <Row style={{ margin: "5px 0", width: "100%" }} >
                  <Col span={12} size={"small"} >
                    <Button size={"small"} onClick={this.handleAddValue.bind(this)}>添加字段</Button>
                  </Col>
                  <Col span={12} size={"small"} >
                    <Button style={{ float: "right" }} size={"small"} onClick={this.handleDeleteFieldsValue.bind(this)} >删除字段</Button>
                  </Col>
                </Row>
                <EditTable columns={this.columns4} dataSource={this.state.ElasticList4} scroll={{ y: 300 }} size={"small"} ref="columns4" rowSelection={true} count={0} />
                <Row style={{ margin: "20px 0px 0px 0px" }}>
                  <ButtonGroup>
                    <Button onClick={this.createMapping.bind(this)}>保存当前映射关系</Button>
                    <Button onClick={this.deleteMapping.bind(this)}>删除当前映射关系</Button>
                    <Button onClick={this.handleCreateMapping.bind(this)}>创建源组</Button>
                  </ButtonGroup>
                </Row>
              </TabPane>
              <TabPane tab="过滤结果集" key="3">
                <FormItem   {...this.formItemLayout}>
                  {getFieldDecorator('matchAnyFilter', {
                    initialValue: config.matchAnyFilter
                  })(
                    <RadioGroup>
                      <Radio value={true}>全部匹配</Radio>
                      <Radio value={false}>匹配任何</Radio>
                    </RadioGroup>
                  )}
                </FormItem>
                <Row style={{ margin: "5px 0", width: "100%" }}  >
                  <Col span={12} size={"small"} >
                    <Button size={"small"} onClick={this.handleAdd2.bind(this)}>添加字段</Button>

                  </Col>
                  <Col span={12} size={"small"} >
                    <Button style={{ float: "right" }} size={"small"} onClick={this.handleDeleteFields2.bind(this)} >删除字段</Button>
                  </Col>
                </Row>
                <EditTable columns={this.columns2} dataSource={this.state.ElasticList2} scroll={{ y: 300 }} size={"small"} ref="columns2" rowSelection={true} count={0} />
              </TabPane>
            </Tabs>
          </div>
        </Form>
      </Modal>
    );


  }
}
const HBaseinfrom = Form.create()(HBaseInput);

export default connect()(HBaseinfrom);
