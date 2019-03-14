/**
 * Created by Administrator on 2017/12/27.
 */
import React from "react";
import { connect } from 'dva';
import { Button, Form, Input, Radio,Select,Tabs,Checkbox,Row,Col,message} from 'antd';
import Modal from "components/Modal.js";
import parse from 'yargs-parser';
import { treeViewConfig } from '../../../../constant';
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const ButtonGroup = Button.Group;
const Option = Select.Option;
const RadioGroup = Radio.Group;
const RadioButton = Radio.Button;
const { TextArea } = Input;

import EditTable from '../../../common/EditTable';

class Sqoop extends React.Component {
  constructor(props) {
    super(props);
    const { initDefaultObj } = props.model;
    const { customArgumentsMap,clusterName,database,schema,mode,commandLine} = props.model.config;

    let model = "simple";
    let advancedView = "ADVANCED_LIST";

    if(mode && mode != "QUICK_SETUP"){
      model = "complex";
      advancedView = mode;
    }

    let initObj = {
      "archives": null,
      "files": null,
      "libjars": null,
      "batch": "",
      "binDir": null,
      "call": "",
      "className": null,
      "clearStagingTable": "",
      "columns": "",
      "connect": null,
      "connectionManager": null,
      "connectionParamFile": null,
      "direct": "",
      "driver": null,
      "enclosedBy": null,
      "escapedBy": null,
      "exportDir": "",
      "fieldsTerminatedBy": null,
      "hadoopHome": null,
      "hadoopMapredHome": null,
      "hcatalogDatabase": null,
      "hcatalogHome": null,
      "hcatalogPartitionKeys": null,
      "hcatalogPartitionValues": null,
      "hcatalogTable": null,
      "hiveHome": null,
      "hivePartitionKey": null,
      "hivePartitionValue": null,
      "inputEnclosedBy": null,
      "inputEscapedBy": null,
      "inputFieldsTerminatedBy": null,
      "inputLinesTerminatedBy": null,
      "inputNullNonString": null,
      "inputNullString": null,
      "inputOptionallyEnclosedBy": null,
      "jarFile": null,
      "linesTerminatedBy": null,
      "mapColumnHive": null,
      "mapColumnJava": null,
      "mapreduceJobName": null,
      "mysqlDelimiters": null,
      "nullNonString": null,
      "nullString": null,
      "numMappers": null,
      "optionallyEnclosedBy": null,
      "outdir": null,
      "packageName": null,
      "password": null,
      "passwordAlias": null,
      "passwordFile": null,
      "relaxedIsolation": null,
      "skipDistCache": null,
      "stagingTable": "",
      "table": null,
      "updateKey": "",
      "updateMode": "",
      "username": null,
      "validate": null,
      "validationFailureHandler": null,
      "validationThreshold": null,
      "validator": null,
      "verbose": null
    };

    let defaultObj = initDefaultObj(initObj,props.model.config);

    this.state = {

      defaultObj: defaultObj,
      customObj: customArgumentsMap,


      clusterName: clusterName,
      database: database,

      schema: schema,

      mode:mode,
      commandLine:commandLine,


      hodoopList: [],
      db_list: [], //数据库名称列表
      db_model:[], //数据库模式
      db_table:[], //表名列表

      model:model,        //高级模式
      advancedView: advancedView,

    }
  }


  componentDidMount(){
    this.Request();
  }

  Request(){
    const { selectOption,config,getHadoopServer } = this.props.model;
    const { connection,schema } = config;
    getHadoopServer(data =>{this.setState({hodoopList:data})});
    selectOption(data => this.setState({db_list:data}));
    if(connection){
      this.getSchemaList(connection,"one");
    }
    if(connection && schema){
      this.getTableList(schema,"one");
    }
  };

  getSchemaList(e,type){

    const { setFieldsValue } = this.props.form;
    const { getSchema } = this.props.model;
    if(type != "one"){
      setFieldsValue({
        "schema":"",
        "table":""
      });
    }
    getSchema({name:e},data =>{
      this.setState({
        db_model:data,
        database:e
      })
    })
  }

  getTableList(e,type){
    const { setFieldsValue,getFieldValue } = this.props.form;
    const { getDbTable } = this.props.model;
    if(type != "one"){
      setFieldsValue({
        "table":""
      });
    }
    let name = getFieldValue("database");
    if(name) {
      let obj = {};
      obj.connection = name;
      obj.schema = e;
      getDbTable(obj, data =>this.setState({
        schema:e,
        db_table: data
      }))
    }
  };

  setClusterName(e){
     this.setState({
       clusterName:e
     });
  }

  /*表格1*/

  hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  };

  handleCreate = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) {
        return;
      }
      const {panel,description,transname,key,saveEntry,text} = this.props.model;
      const {getFieldValue} = this.props.form;
      const { model,advancedView,clusterName,database,schema,defaultObj,commandLine,customObj } = this.state;
      const { exportDir,table  } = defaultObj;

      let obj = {};
      let paramsObj = {};

      obj.jobName = transname;
      obj.newName = (text === values.text?"":values.text);
      obj.entryName = text;
      obj.type = panel;
      obj.description = description;

      paramsObj.clusterName = clusterName?clusterName:getFieldValue("clusterName");
      paramsObj.database = database?database:getFieldValue("database");
      paramsObj.schema= schema?schema:getFieldValue("schema");
      paramsObj.exportDir= exportDir?exportDir:getFieldValue("exportDir");
      paramsObj.table = table?table:getFieldValue("table");

      if(model === "simple"){
          paramsObj.mode = "QUICK_SETUP";
      }else{
          if(advancedView === "ADVANCED_LIST"){
              let sendFields1 = {};
              let sendFields2 = {};
              if(this.refs.editTable){
                if(this.refs.editTable.state.dataSource.length>0){
                  sendFields1 = this.renderArgsToObj(this.refs.editTable.state.dataSource);
                }
              }else{
                if(defaultObj){
                  sendFields1 = defaultObj
                }
              }

            if(this.refs.editTable1){
              if(this.refs.editTable1.state.dataSource.length>0){
                sendFields2 = this.renderArgsToObj(this.refs.editTable1.state.dataSource);
              }
            }else{
              if(customObj){
                sendFields2 = customObj
              }
            }
            paramsObj.customArgumentsMap = sendFields2;
            paramsObj = {
              ...paramsObj,
              ...sendFields1
            }

          }else{
            paramsObj.mode = "ADVANCED_COMMAND_LINE";
            paramsObj.commandLine = getFieldValue("commandLine");
          }
      }

      obj.jobName = transname;
      obj.newName = (text === values.text?"":values.text);
      obj.entryName = text;
      obj.type = panel;
      obj.description = description;
      obj.parallel= values.parallel;
      obj.entryParams = paramsObj;

      saveEntry(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  };


  /*新方法*/

  /*文件模板*/
  getFieldList(){
    const {dispatch} = this.props;
    const {getFieldValue} = this.props.form;
    const {formatFolder,panel} = this.props.model;

    let obj = treeViewConfig.get(panel)["model"];

    let str = getFieldValue("clusterName");
    let str1 = formatFolder(getFieldValue("exportDir"));

    let updateModel = this.setFolder.bind(this);

    let viewPath = "";
    let needUpFolder = false;

    if(str && str.trim()){
      let obj1 = {
        type:"hdfs",
        path:str+"::"+ (str1?str1:"/"),
        depth:1
      };
      let str2 = str+"::";

      dispatch({
        type:"treeview/showTreeModel",
        payload:{
          ...obj,
          obj:obj1,
          needUpFolder:needUpFolder,
          updateModel:updateModel,
          prefixStr:str2,
          viewPath:viewPath
        }
      })
    }
  };

  /*设置文件名*/
  setFolder(str){
    if(!str){
      return;
    }
    let str1 = str;
    const { setFieldsValue } = this.props.form;
    setFieldsValue({
      "exportDir":str1
    });
  };

  handleUseChange(e){
    this.setState({
      useFolder:e.target.checked
    })
  }

  /*表格*/
  Columns = [
    {
      title: '参数',
      dataIndex: 'args',
      key: 'args',
      width:"50%"
    },{
      title: '值',
      dataIndex: 'value',
      key: 'value',
      editable:true
    }
  ];

  /*表格1*/
  Columns1 = [
    {
      title: '字段名',
      dataIndex: 'args',
      key: 'args',
      width:"50%",
      editable:true
    },{
      title: '值',
      dataIndex: 'value',
      key: 'value',
      editable:true
    }
  ];

  /*表格1*/
  handleAdd = ()=>{
    let data = {
      "args": "",
      "value": ""
    };

    this.refs.editTable1.handleAdd(data);
  };
  /*表格1*/
  handleDeleteFields = ()=>{
    this.refs.editTable1.handleDelete();
  };

  initFuc(that){
    const { defaultObj } = this.state;
    const { objToArgs } = this.props.model;

    let args = objToArgs(defaultObj);

    if(args.length>0){
      that.updateTable(args,args.length - 1);
    }
  };


  initFuc1(that){
    const { customObj } = this.state;
    const { objToArgs } = this.props.model;

    let args = objToArgs(customObj);


    if(args.length>0){
      that.updateTable(args,args.length - 1);
    }

  };

  /*高，低级选项切换*/
  handleChangeModel() {
      const { model,advancedView,defaultObj } = this.state;
      const { getFieldsValue,getFieldValue } = this.props.form;

      if(model==="simple"){
        const {table,exportDir} = getFieldsValue(["table","exportDir"]);
        let newObj = {
          ...defaultObj,
          table:table,
          exportDir:exportDir
        };
        this.setState({
          model:"complex",
          defaultObj:newObj
        });
        if(advancedView === "ADVANCED_LIST"){
             this.renderListView(newObj);
        }else{
              this.renderCommondLine(newObj);
        }
      }else{
        let obj = {};
        if(advancedView === "ADVANCED_LIST"){
            if(this.refs.editTable){
               obj = this.renderArgsToObj(this.refs.editTable.state.dataSource);
            }
            this.setState({
              model:"simple",
              defaultObj:obj
            });
        }else{
            const commandLine = getFieldValue("commandLine");
            this.renderCommondView(commandLine,(bool,newObj,customObj)=>{
                if(bool){
                  this.setState({
                    model:"simple",
                    defaultObj:newObj,
                    customObj:customObj
                  });
                }
            });
        }
      }
  }

  renderArgsToObj(args){
    let obj = {};
    for(let index of args){
        if(index["args"]){
            obj[index["args"]] = index["value"]
        }
      }
    return obj;
  };

  renderListView(obj){
    const { objToArgs } = this.props.model;
    let args = objToArgs(obj);

    if(args.length>0 && this.refs.editTable){
      this.refs.editTable.updateTable(args,args.length - 1);
    }
  }

  renderCommondLine(obj){
      const { defaultObj,customObj } = this.state;

      let defaultObj1 = {};
      let customObj1 = {};

      if(obj){
        defaultObj1 = obj;
      }else{
         if(this.refs.editTable){
           defaultObj1 = this.renderArgsToObj(this.refs.editTable.state.dataSource);
         }else{
           defaultObj1 = defaultObj;
         }
      }
      if(this.refs.editTable1){
        customObj1 = this.renderArgsToObj(this.refs.editTable1.state.dataSource);
      }else{
        customObj1 = customObj;
      }

      let str1 = "";
      let str2 = "";
      let args1 = [];
      let args2 = [];

      for(let index of  Object.keys(defaultObj1)){
          if(defaultObj1[index]){
            let newIndex = "";
            for(let elem of index){
              let elems = elem;
              if(/[A-Z]/.test(elem)){
                elems = "-"+elem.toLowerCase();
              }
              newIndex += elems;
            }
            args1.push(newIndex+" "+defaultObj1[index])
          }
      }
      if(args1.length>0){
        str1 = " --"+args1.join(" --");
      }
    for(let index of  Object.keys(customObj1)){
          let index1 = "null";
          let value1 = "null";
          if(index){
            index1 = index
          }
          if(customObj1[index]){
            value1 = customObj1[index];
          }
          args2.push(index1+"="+value1+" ")
    }

    if(args2.length>0){
      str2 = "-D "+args2.join("-D ");
    }

    this.setState({
      commandLine:str2+" "+str1,
      defaultObj:defaultObj1,
      customObj:customObj1
    })

  }

  renderCommondView(args,callback){
    const { defaultObj } = this.state;
    let initArgs = [];

    for(let index of args.split(" ")){
      if(index){
        initArgs.push(index);
      }
    }

      if(initArgs.length ===0){
        callback(true,defaultObj,{});
        return false;
      }

      let parseObj =  parse(initArgs.join(' '));

      console.log(parseObj);

      let newArgs = Object.keys(parseObj);
      let argsArr = [];

      for(let index of newArgs){
          if(index != "D" && index != "_" && index.indexOf("-") === -1){
            argsArr.push(index);
          }
      }

      for(let index of argsArr){
          if(Object.keys(defaultObj).includes(index)){
            defaultObj[index] = parseObj[index];
          }else{
              message.error("参数异常:"+index);
              callback(false);
              return false;
          }
      }

      if(parseObj["_"].length > 0){
          let str = "";
          for(let index of parseObj["_"]){
             str += " "+index
          }
          message.error("参数异常:"+str);
          callback(false);
          return false;
      }
      this.renderListView(defaultObj);

      const { D } = parseObj;
      let customObj = {};

      if(D){
          if( typeof D === "string"){
            let args = D.split("=");
            customObj[args[0]] = args[args.length - 1];
          }else{
            for(let index of D){
              let args = index.split("=");
              customObj[args[0]] = args[args.length - 1];
            }
          }
      }


      if(this.refs.editTable1){
        const { objToArgs } = this.props.model;
        let args = objToArgs(customObj);

        if(args.length>0){
          this.refs.editTable1.updateTable(args,args.length - 1);
        }
      }

      callback(true,defaultObj,customObj);
  };

  /*表格与命令行视图切换*/
  handleViewChange(e){
    const { getFieldValue } = this.props.form;

    if(e.target.value === "ADVANCED_LIST"){
      const commandLine = getFieldValue("commandLine");
      this.renderCommondView(commandLine,(bool,newObj,customObj)=>{
          if(bool){
            this.setState({
              advancedView:e.target.value,
              defaultObj:newObj,
              customObj:customObj
            })
          }
      });
    }else{
      this.renderCommondLine();
      this.setState({
        advancedView:e.target.value
      })
    }
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const { text,visible,handleCheckJobName,nextStepNames,parallel } = this.props.model;
    const { hodoopList,advancedView,model,defaultObj,commandLine,database,schema,clusterName } = this.state;
    const { exportDir,table  } = defaultObj;

    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 }
    };

    return (
      <Modal
        visible={visible}
        title="Sqoop 输出"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        onCancel={this.hideModal.bind(this)}
        width={650}
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={this.handleCreate.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large"  onClick={this.hideModal.bind(this)}>取消</Button>,
                ]}
      >
        <Form >

          <FormItem label="作业项名称"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('text', {
              initialValue: text,
              rules: [{ whitespace:true, required: true, message: '请输入作业项名称' },
                {validator:handleCheckJobName,message: '作业项名称已存在，请更改!' }]
            })(
              <Input  />
            )}
          </FormItem>
          {nextStepNames.length >= 2 ?(
              <FormItem {...formItemLayout} style={{marginBottom:"8px"}}>
                  {getFieldDecorator('parallel', {
                    valuePropName: 'checked',
                    initialValue: parallel,
                  })(
                    <Checkbox disabled={nextStepNames.length <= 1} style={{left:'11rem'}}>下一步骤并行运行</Checkbox>
                  )}
                </FormItem>
            ):null }
          {
            model === "simple"?
            <div>
              <fieldset className="ui-fieldset">
                <legend>&nbsp;&nbsp;资源</legend>
                <FormItem label="Hadoop集群"   {...formItemLayout} style={{marginBottom:"8px"}}>
                  {getFieldDecorator('clusterName', {
                    initialValue: clusterName?clusterName:""
                  })(
                    <Select  onChange={this.setClusterName.bind(this)}>
                      { hodoopList.map(index=>{
                        return(
                          <Option key={index.name}>{index.name}</Option>
                        )
                      })
                      }
                    </Select>
                  )}
                </FormItem>
                <FormItem label="目标目录"   {...formItemLayout} style={{marginBottom:8}}>
                  {getFieldDecorator('exportDir', {
                    initialValue:exportDir?exportDir:""
                  })(
                    <Input spellCheck={false}/>
                  )}
                  <Button onClick={()=>{this.getFieldList()}}>浏览</Button>
                </FormItem>
              </fieldset>
              <fieldset className="ui-fieldset">
                <legend>&nbsp;&nbsp;目标</legend>
                <FormItem
                  {...formItemLayout}
                  label="数据库连接"
                  hasFeedback
                  style={{marginBottom:"8px"}}
                >
                  {getFieldDecorator('database', {
                    initialValue: database?database:""

                  })(
                    <Select placeholder="请选择数据库链接"   onChange={this.getSchemaList.bind(this)}>
                      {
                        this.state.db_list.map((index)=>
                          <Select.Option  key={index.name}   value={index.name}>{index.name}</Select.Option>
                        )
                      }
                    </Select>
                  )}
                </FormItem>
                <FormItem
                  {...formItemLayout}
                  label="目标模式"
                  hasFeedback
                  style={{marginBottom:"8px"}}
                >
                  <div>
                    {getFieldDecorator('schema', {
                      initialValue: schema?schema:""
                    })(
                      <Select onChange={this.getTableList.bind(this)}  allowClear>
                        {
                          this.state.db_model.map((index)=>
                            <Select.Option  key={index} value={index}>{index}</Select.Option>
                          )
                        }
                      </Select>
                    )}
                  </div>
                </FormItem>
                <FormItem label="表名"   {...formItemLayout} style={{marginBottom:8}}>
                  {getFieldDecorator('table', {
                    initialValue:table?table:""
                  })(
                    <Select   mode="combobox">
                      {
                        this.state.db_table.map((index)=>
                          <Select.Option  key={index.table} value={index.table}>{index.table}</Select.Option>
                        )
                      }
                    </Select>
                  )}
                </FormItem>
              </fieldset>
            </div>:
            <div style={{marginTop:"20px"}}>
              <Row>
                 <Col span={3} >&nbsp;</Col>
                 <Col span={12}>
                   <RadioGroup value={advancedView} defaultValue={advancedView} onChange={this.handleViewChange.bind(this)} >
                     <RadioButton value="ADVANCED_LIST">表格视图</RadioButton>
                     <RadioButton value="ADVANCED_COMMAND_LINE">命令视图</RadioButton>
                   </RadioGroup>
                 </Col>
              </Row>
              {
                advancedView === "ADVANCED_LIST"?
                  <div style={{margin:"10px 12%"}}>
                    <Tabs type="card">
                      <TabPane tab="默认" key="1">
                        <EditTable    columns={this.Columns}
                                      extendDisabled={true}
                                      initFuc={this.initFuc.bind(this)}
                                      dataSource = {[]} count={1} tableStyle="editTableStyle5" size={"small"} scroll={{y: 300}}  rowSelection={true} ref="editTable" />
                      </TabPane>
                      <TabPane tab="定制" key="2">
                        <Row style={{textAlign:"right",marginBottom:"5px"}}>
                          <ButtonGroup size={"small"}  >
                            <Button     onClick={this.handleAdd.bind(this)}>添加</Button>
                            <Button     onClick={this.handleDeleteFields.bind(this)} >删除</Button>
                          </ButtonGroup>
                        </Row>

                        <EditTable    columns={this.Columns1}
                                      initFuc={this.initFuc1.bind(this)}
                                      dataSource = {[]} count={1} tableStyle="editTableStyle5" size={"small"} scroll={{y: 300}}  rowSelection={true} ref="editTable1" />
                      </TabPane>
                    </Tabs>
                  </div>:
                  <div  style={{margin:"10px 12%"}}>
                    <FormItem   style={{marginBottom:8}}>
                      {getFieldDecorator('commandLine', {
                        initialValue:commandLine?commandLine:""
                      })(
                        <TextArea type="TextArea"  style={{ height: 200 }}/>
                      )}
                    </FormItem>

                  </div>
              }

            </div>
          }
          <Row  style={{marginTop:"20px"}}>
            <Col span={20} style={{textAlign:"right"}}>
              <Button onClick={this.handleChangeModel.bind(this)}>{model==="simple"?"高级选项":"快速设置"}</Button>
            </Col>
          </Row>


        </Form>
      </Modal>
    );
  }
}
const SqoopExport = Form.create()(Sqoop);
export default connect()(SqoopExport);
