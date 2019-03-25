/*alisa*/
import React from "react";
import { connect } from 'dva';
import { Form, Select, Button, Input, Checkbox, Tabs, Row, Col, AutoComplete, message } from 'antd';
import Modal from "components/Modal.js";
import EditTable from '../../../common/EditTable';

const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const Option = Select.Option;
const ButtonGroup = Button.Group;


class HBaseOutput extends React.Component {
  constructor(props) {
    super(props);
    const { visible, config } = props.model;
    if (visible === true) {
      const { mapping } = config;
      let data1 = [];
      let data2 = [];
      //获取表2values值
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
        data1 = [{
          "key": count,
          keyType: index.keyType,
          keyword:true,  //是否唯一值默认状态
          tupleFamilies: index.tupleFamilies,
          tupleMapping: index.tupleMapping,
        }]
      }

      this.state = {
        ElasticList1: data1, //用于存放表格1回显的数据
        ElasticList2: data2,  //用于存放表格2回显的数据
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
    const { namedClusterName, coreConfigURL, defaultConfigURL, targetTableName } = config;

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
          siteConfig: coreConfigURL, defaultConfig: defaultConfigURL, tableName: targetTableName
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
      //为获取Mappings,多增加参数
      if (type === "getMappings") {
        params.tableName = getFieldValue("targetTableName");
        //添加提示
        if (!params.namedClusterName || !params.siteConfig || !params.tableName) {
          message.warn("Hadoop集群、hbase-site.xml的URL以及HBase表名都不能为空！")
          return;
        }
      } else if (type === "getMappingInfo") {
        params.tableName = getFieldValue("targetTableName");
        params.mappingName = obj?obj:getFieldValue("targetMappingName");
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
            args.push({value: index })
          }
          this.setState({
            MappList: args
          })
        } else if (type === "getMappingInfo") {
          let args1 = [];
          let count = 0;
          for (let key of data.mappedColumns) {
            args1.push({
              ...key,
              keyword: false,
              key: count++
            })
          }
          delete data.mappedColumns;
          args = [{ ...data }];
          if(this.refs.columns1){
            this.refs.columns1.updateTable(args, 1);
            this.refs.columns2.updateTable(args1, count);
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

  //点击取消关闭Model弹出框
  setModelHide() {
    const { dispatch } = this.props;
    dispatch({
      type: 'items/hide',
      visible: false,
    });
  }

  //表单长宽格式间隔
  formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 },
  };

  //表单1参数
  columns1 = [
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
      editable: true,
    }];
  //表单2的参数设置
  columns2 = [
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
      width: "10%",
      render: (text) => <span>{text === true ? "是" : "否"}</span>,
    }, {
      title: '列族',
      dataIndex: 'columnFamily',
      key: 'columnFamily',
      width: "20%",
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

  //点击创建mapping静态列表，表一以及表二的
  handleCreateMapping = () => {
    //表3的静态列表
    const data = [{
      key: 0,
      "keyword": "key",
      "tupleMapping": true,
      "mappingName": "",
      "tableName": "Long",
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
    this.refs.columns1.updateTable(data, 1);
    this.refs.columns2.updateTable(data1, 4);
  }

  //新增表1一条表格
  handleAdd() {
    if(this.refs.columns1 && this.refs.columns1.state.dataSource.length >0){
      message.warn("key表只能存在唯一字段");
    }
    const data = {
      "keyword": "",
      "tupleMapping": true,
      "keyType": "",
      "tupleFamilies": "",
    }
    this.refs.columns1.handleAdd(data);
  }

  //删除表1一条表格
  handleDeleteFields() {
    this.refs.columns1.handleDelete();
  }

  //添加表2的表格框
  handleAddValue() {
    const data = {
      "alias": "",
      "columnFamily": "",
      "columnName": "",
      "keyword": false,
      "type": "",
      "index": "",
    }
    this.refs.columns2.handleAdd(data);
  }
  //删除表2的表格框
  handleDeleteFieldsValue() {
    this.refs.columns2.handleDelete();
  }

  handleCreate() {
    const form = this.props.form;
    const { panel, transname, description, key, saveStep, config, text, formatTable } = this.props.model;
    const { mapping } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];  //提交的时候把表1的数据保存到sendFields，然后进行过滤提交
      let sendFields1 = [];  //提交的时候把表2的数据保存到sendFields1，然后进行过滤提交
      let newMapping = mapping;
      //表单1的渲染
      if (this.refs.columns1) {
        if (this.refs.columns1.state.dataSource.length > 0) {
          let args = ["keyword", "tupleMapping","keyType", "tupleFamilies"];
          sendFields = formatTable(this.refs.columns1.state.dataSource, args);
        }
      } else {
        if (mapping) {
          sendFields = {};
        }
      }
      //表单2的渲染
      if (this.refs.columns2) {
        if (this.refs.columns2.state.dataSource.length > 0) {
          let args = ["alias", "columnFamily", "columnName", "type", "index", "keyword"];
          sendFields1 = formatTable(this.refs.columns2.state.dataSource, args);
        }
      } else {
        if (mapping) {
          sendFields1 = [];
        }
      }
      //当表1的值为数组时，格式化获得的新值
      if(sendFields instanceof Array){
        newMapping = {
          ...sendFields[0],
          mappingName: values.targetMappingName,
          tableName: values.targetTableName,
          mappedColumns:sendFields1
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
        mapping: newMapping
      }
      //保存调取接口
      saveStep(obj, key, data => {
        if (data.code === "200") {
          //保存成功关闭窗口
          this.setModelHide();
        }
      });
    })
  }



  //保存映射关系
  //保存映射关系的时候，格式与保存提交的mapping一致
  createMapping = () => {
    const { getDetails, transname, text, panel } = this.props.model;
    const { getFieldValue } = this.props.form;
    let table1 = this.refs.columns1.state.dataSource;
    let table2 = this.refs.columns2.state.dataSource;
    let namedClusterName = getFieldValue("namedClusterName");  //hadoop集群名称
    let siteConfig = getFieldValue("coreConfigURL");    // hbase-site.xml的URL名称
    let defaultConfig = getFieldValue("defaultConfigURL"); //hbase-site.xmlhbase-default.xml的UR
    let mappingName = getFieldValue("targetMappingName");
    let tableName = getFieldValue("targetTableName");

    if(!namedClusterName || !siteConfig || !mappingName || !tableName ){
      message.warn("Hadoop集群、hbase-site.xml的URL、HBase表名以及映射名称值都不能为空！");
      return false;
    }

    if(table1.length === 0){
      message.warn("表1必须有一条数据！");
      return false;
    }

    let mapping = {
      mappingName,tableName,
      keyword:table1[0].keyword,
      keyType:table1[0].keyType,
      tupleFamilies:table1[0].tupleFamilies,
      tupleMapping: true,
      mappedColumns:table2
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
      this.getSource("getMappings");
      message.success("创建成功！");
    });
  }
  //删除映射关系
  deleteMapping = () => {
    const { getDetails, transname, text, panel } = this.props.model;
    const { getFieldValue,resetFields } = this.props.form;
    let namedClusterName = getFieldValue("namedClusterName");  //hadoop集群名称
    let siteConfig = getFieldValue("coreConfigURL");    // hbase-site.xml的URL名称
    let defaultConfigURL = getFieldValue("defaultConfigURL"); //hbase-site.xmlhbase-default.xml的UR
    let tableName = getFieldValue("targetTableName");  //Hbase表名称
    let mappingName = getFieldValue("targetMappingName");  //映射名称

    if(!namedClusterName || !siteConfig || !mappingName || !tableName ){
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
      resetFields(["targetMappingName"]);
      message.success("删除成功！");
    });
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const { visible, config, text, handleCheckName } = this.props.model;

    console.log(visible, "科技");

    return (
      <Modal
        visible={visible}
        title="HBase输出"
        wrapClassName="vertical-center-modal"
        width={750}
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
                    <Select>
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
                  {getFieldDecorator('targetTableName', {
                    initialValue: config.targetTableName
                  })(
                    <Select onFocus={() => { this.getSource("getTables") }}>
                      {this.state.HbaseList.map(index => {
                        return (
                          <Option key={index.name}>{index.name}</Option>
                        )
                      })
                      }
                    </Select>
                  )}
                </FormItem>
                <FormItem label="映射名称" style={{ marginBottom: "8px" }} {...this.formItemLayout}>
                  {getFieldDecorator('targetMappingName', {
                    initialValue: config.targetMappingName
                  })(
                    <Select onFocus={() => { this.getSource("getMappings") }}  onSelect={(e) => { this.getSource("getMappingInfo", "other", e) }} >
                      {this.state.MappList.map(index => {
                        return (
                          <Option key={index.value}>{index.value}</Option>
                        )
                      })
                      }
                    </Select>
                  )}
                </FormItem>
                <FormItem style={{ marginBottom: "0px", marginLeft: "78px", textAlign: "center" }} {...this.formItemLayout}>
                  {getFieldDecorator('saveMappingToMeta', {
                    valuePropName: 'checked',
                    initialValue: config.saveMappingToMeta,
                  })(
                    <Checkbox>在步骤元数据库映射信息</Checkbox>
                  )}
                </FormItem>
                <FormItem style={{ marginBottom: "0px", marginLeft: "18px", textAlign: "center" }} {...this.formItemLayout}>
                  {getFieldDecorator('disableWriteToWAL', {
                    valuePropName: 'checked',
                    initialValue: config.disableWriteToWAL,
                  })(
                    <Checkbox>禁用写WAL</Checkbox>
                  )}
                </FormItem>
                <FormItem label="写缓冲区大小（字节）" style={{ marginBottom: "8px" }} {...this.formItemLayout}>
                  {getFieldDecorator('writeBufferSize', {
                    initialValue: config.writeBufferSize
                  })(
                    <Input />
                  )}
                </FormItem>

              </TabPane>
              <TabPane tab="创建/编辑映射" key="2">
                <FormItem label="HBase表名" style={{ marginBottom: "8px" }} {...this.formItemLayout}>
                  {getFieldDecorator('targetTableName', {
                    initialValue: config.targetTableName
                  })(
                    <Select onFocus={() => { this.getSource("getTables") }}>
                      {this.state.HbaseList.map(index => {
                        return (
                          <Option key={index.name}>{index.name}</Option>
                        )
                      })
                      }
                    </Select>
                  )}
                </FormItem>
                <FormItem label="映射名称" style={{ marginBottom: "8px" }} {...this.formItemLayout}>
                  {getFieldDecorator('targetMappingName', {
                    initialValue: config.targetMappingName
                  })(
                    <AutoComplete
                      style={{ width: '100%' }}
                      dataSource={this.state.MappList.map(index=>index.value)}
                      onFocus={() => { this.getSource("getMappings") }}
                      onSelect={(e) => { this.getSource("getMappingInfo", "other", e) }}
                    />
                  )}
                </FormItem>

                <Row style={{ margin: "5px 0", width: "100%" }}  >
                  <Col span={12} >
                    <ButtonGroup size={"small"}  >
                      <Button onClick={this.handleAdd.bind(this)}>添加字段</Button>
                      <Button onClick={() => { this.getSource("getMappingInfo") }}>获取字段</Button>
                    </ButtonGroup>
                  </Col>
                  <Col span={12} >
                    <ButtonGroup size={"small"} style={{ float: "right" }} >
                     
                      <Button onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                    </ButtonGroup>
                  </Col>
                </Row>
                <EditTable columns={this.columns1} size={"small"} dataSource={this.state.ElasticList1} scroll={{ y: 300 }} ref="columns1" rowSelection={true} count={0} />
                <Row style={{ margin: "5px 0", width: "100%" }} >
                  <Col span={12} size={"small"} >
                    <Button size={"small"} onClick={this.handleAddValue.bind(this)}>添加字段</Button>
                  </Col>
                  <Col span={12} size={"small"} >
                    <Button style={{ float: "right" }} size={"small"} onClick={this.handleDeleteFieldsValue.bind(this)} >删除字段</Button>
                  </Col>
                </Row>
                <EditTable columns={this.columns2} size={"small"} dataSource={this.state.ElasticList2} ref="columns2" scroll={{ y: 300 }} rowSelection={true} count={0} />
                <Row style={{ margin: "20px 10px 0px 10px" }}>
                  <ButtonGroup size={"small"} >
                    <Button type="primary" onClick={this.createMapping.bind(this)}>保存当前映射关系</Button>
                    <Button type="primary" onClick={this.deleteMapping.bind(this)}>删除当前映射关系</Button>
                    <Button onClick={this.handleCreateMapping.bind(this)}>创建源组</Button>
                  </ButtonGroup>
                </Row>
              </TabPane>
            </Tabs>
          </div>

        </Form>
      </Modal>
    );


  }
}
const HBasefrom = Form.create()(HBaseOutput);

export default connect()(HBasefrom);
