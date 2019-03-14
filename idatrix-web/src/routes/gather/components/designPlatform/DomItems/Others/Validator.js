/**
 * Created by Administrator on 2017/9/6.
 */
import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col,Card,Tabs } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const ButtonGroup = Button.Group;
const Option = Select.Option;
import EditTable from '../../../common/EditTable'

class ValidatorModel extends React.Component {

  constructor(props){
    super(props);
    const { validations } = props.model.config;
    let panesMap = new Map();
    let key = "";

    if(validations && validations.length>0){
        key = validations[0].name;
        for(let index of validations){
            panesMap.set(index.name,index);
        }
    }

    this.newTabIndex = 0;

    this.state = {
      activeKey: key,
      panesMap,
      InputFields:[],
      OutputFields:[],
      updateStatus:"isUpdate"
    };
  };

  componentDidMount(){
     const { activeKey,panesMap } = this.state;
    if(panesMap.size>0){
        this.setFields(panesMap.get(activeKey))
    };
    this.getInputFields();
  }


  getInputFields = ()=>{
      const { getInputFields,transname,text } = this.props.model;
      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      getInputFields(obj,data =>{
         this.setState({
           InputFields:data
         });
      })
  };

  getFields = ()=>{
    const { getFieldsValue } = this.props.form;
    let values =  getFieldsValue(["name", "fieldName", "maximumLength", "minimumLength", "nullAllowed", "onlyNullAllowed", "onlyNumericAllowed",
      "dataType", "dataTypeVerified", "conversionMask", "decimalSymbol", "groupingSymbol", "maximumValue", "minimumValue", "startString", "endString",
      "startStringNotAllowed", "endStringNotAllowed", "regularExpression", "regularExpressionNotAllowed", "errorCode", "errorDescription", "sourcingValues",
      "sourcingStepName", "sourcingField"]);

    values.allowedValues = [];
    if(this.refs.groupFields){
      if(this.refs.groupFields.state.dataSource.length>0){
        for(let index of this.refs.groupFields.state.dataSource){
            if(index.groupFields){
              values.allowedValues.push(index.groupFields);
            }
        }
      }
    }
    return values;
  };

  initFields = (key)=>{
    const { panesMap } = this.state;
    const { setFieldsValue } = this.props.form;
    let init = {
        "name": key,
        "fieldName": null,
        "maximumLength": null,
        "minimumLength": null,
        "nullAllowed": true,
        "onlyNullAllowed": false,
        "onlyNumericAllowed": false,
        "dataType": 0+"",
        "dataTypeVerified": false,
        "conversionMask": null,
        "decimalSymbol": null,
        "groupingSymbol": null,
        "maximumValue": null,
        "minimumValue": null,
        "startString": null,
        "endString": null,
        "startStringNotAllowed": null,
        "endStringNotAllowed": null,
        "regularExpression": null,
        "regularExpressionNotAllowed": null,
        "errorCode": null,
        "errorDescription": null,
        "sourcingValues": false,
        "sourcingStepName": null,
        "sourcingField": null
      };
      setFieldsValue({
        ...init
      });
      panesMap.set(key,{...init,allowedValues:[]});
      this.refs.groupFields.updateTable([],0);
      this.setState({
        activeKey:key,
        panesMap
      })
  };

  setFields = (obj)=>{
    const { setFieldsValue } = this.props.form;
    let { allowedValues } = obj;
    let data = [];
    let count = 0;
    if(allowedValues && allowedValues.length>0){
      for(let index of allowedValues){
        data.push({
          key:count,
          groupFields:index
        });
        count++;
      }
    }
    this.refs.groupFields.updateTable(data,count);
    if(allowedValues){
      delete obj.allowedValues;
    }
    setFieldsValue({
      ...obj,dataType:obj.dataType+""
    })
  };

  onChange = (targetKey) => {

    const { panesMap,activeKey } = this.state;
    let values = this.getFields();
    panesMap.set(activeKey,values);
    this.setFields(panesMap.get(targetKey));
    this.setState({ activeKey:targetKey });
  };
  onEdit = (targetKey, action) => {
    this[action](targetKey);
  };
  add = () => {
    const { panesMap,activeKey } = this.state;
    if(panesMap.size !== 0){
      let values = this.getFields();
      panesMap.set(activeKey,values);
    }

    let newKey = `新检验${this.newTabIndex++}`;
    while (panesMap.has(newKey)){
      newKey = `新检验${this.newTabIndex++}`;
    }
    this.initFields(newKey);
  };

  remove = (targetKey) => {
    const { activeKey,panesMap } = this.state;
    const { setFieldsValue } = this.props.form;
    let newKey = activeKey;
    if(targetKey === activeKey){
      panesMap.delete(targetKey);
      let panels =  [...panesMap.keys()];
      if(panesMap.size > 0){
        newKey = panels[panels.length -1];
        setFieldsValue({
          ...panesMap.get(newKey)
        })
      }else{
        newKey = "";
      }
    }else{
      panesMap.delete(targetKey);
    }
    this.setState({ panesMap, activeKey: newKey});
  };


  handleSelectChange = (e)=>{
      const { setFieldsValue } = this.props.form;
      if(e){
        const { getOutFields,transname } = this.props.model;

        let obj = {};
        obj.transname = transname;
        obj.stepname = e;
        getOutFields(obj,data =>{
          this.setState({
            OutputFields:data
          });
        })
      }else{
        this.setState({
          OutputFields:[]
        });
      }
      setFieldsValue({
        sourcingField:""
      })
  };

  hideModal = () => {
    const { dispatch,form } = this.props;
    const { panesMap } = this.state;
    dispatch({
      type:'items/hide',
      visible:false
    })
    form.resetFields();
    panesMap.clear();
    this.setState({
      activeKey: "",
      panesMap:panesMap,
      InputFields:[],
      OutputFields:[],
      updateStatus:"update"
    })
  };

  handleCreate = () => {
    const form = this.props.form;
    const { panel,transname,description,key,saveStep,text } = this.props.model;
    const { panesMap,activeKey } = this.state;
    panesMap.set(activeKey,this.getFields());
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        "validatingAll": values.validatingAll,
        "concatenatingErrors": values.concatenatingErrors,
        "concatenationSeparator": values.concatenationSeparator,
        validations:[...panesMap.values()]
      };

      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  };


  /*增加字段 keys*/
  handleAdd = ()=>{
    const data = {
      "groupFields":""
    }
    this.refs.groupFields.handleAdd(data);
  };
  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.groupFields.handleDelete();
  };

  /*文件表格*/
  columns =  [{
    title: '允许的值',
    dataIndex: 'groupFields',
    width:"98%",
    key: 'groupFields',
    editable:true,
  }];



  handleDescName = (rule,value, callback)=>{
      const { panesMap,activeKey } = this.state;

    if(value !== activeKey ){

      if(panesMap.has(value)){
        callback(true);
      }else{
          panesMap.set(value,panesMap[activeKey]);
          panesMap.delete(activeKey);
          this.setState({
            panesMap,activeKey:value
          });
        callback()
      }

    }else{
       callback()
    }
  };

  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName,prevStepNames } = this.props.model;
    const { panesMap,activeKey,InputFields,OutputFields } = this.state;



    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };


    const setDisabled = ()=>{
      if(getFieldValue("validatingAll") === undefined){
        return config.validatingAll;
      }else{
        if(getFieldValue("validatingAll")){
          return getFieldValue("validatingAll");
        }else {
          return false;
        }
      }
    };

    const setDisabled1 = ()=>{
      if(getFieldValue("concatenatingErrors") === undefined){
        return config.concatenatingErrors;
      }else{
        if(getFieldValue("concatenatingErrors")){
          return getFieldValue("concatenatingErrors");
        }else {
          return false;
        }
      }
    };

     const setDisabled2 = ()=>{
      if(getFieldValue("sourcingValues") === undefined){
        return config.sourcingValues;
      }else{
        if(getFieldValue("sourcingValues")){
          return getFieldValue("sourcingValues");
        }else {
          return false;
        }
      }
    };

    


    return (
      <Modal
        visible={visible}
        title="数据校验"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={750}
        footer={[
                  <Button key="submit" type="primary" size="large" onClick={this.handleCreate} >
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={this.hideModal}>取消</Button>,
                ]}
        onCancel = {this.hideModal}>
        <Form >
          <FormItem label="步骤名称"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('text', {
              initialValue:text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
            })(
              <Input />
            )}
          </FormItem>
          <FormItem label=""   style={{marginBottom:"8px",marginLeft:"15%"}}  {...formItemLayout1}>
            {getFieldDecorator('validatingAll', {
              valuePropName: 'checked',
              initialValue:config.validatingAll
            })(
              <Checkbox >报告所有错误，不止第一个错误</Checkbox>
            )}
          </FormItem>
          <Row style={{marginBottom:"8px",marginLeft:"15%"}} >
             <Col span={14}>
               <FormItem     {...formItemLayout1}>
                 {getFieldDecorator('concatenatingErrors', {
                   valuePropName: 'checked',
                   initialValue:config.concatenatingErrors
                 })(
                   <Checkbox  disabled={!setDisabled()}>只输出一行，指定连接错误的分隔符：</Checkbox>
                 )}
               </FormItem>
             </Col>
             <Col span={10}>
               <FormItem     {...formItemLayout1}>
                 {getFieldDecorator('concatenationSeparator', {
                   initialValue:config.concatenationSeparator
                 })(
                   <Input disabled={!setDisabled() || !setDisabled1()}/>
                 )}
               </FormItem>
             </Col>
          </Row>
          <div>
            <div style={{ margin:"0px 5%",marginBottom:"10px" }}>
              <Button onClick={this.add}>增加校验</Button>
            </div>
            <div style={{margin:"0px 5%",display:panesMap.size === 0?"none":"block"}} >
              <div id="noTabPane" >
              <Tabs
                hideAdd
                onChange={this.onChange}
                activeKey={activeKey}
                type="editable-card"
                onEdit={this.onEdit}
              >
                {[...panesMap.keys()].map(index => <TabPane tab={index} key={index}>{index}</TabPane>)}
              </Tabs>
            </div>
            <div style={{height:"550px",overflow:"scroll",overflowX:"hidden"}}>
            <FormItem
              style={{marginBottom:"8px"}}
              {...formItemLayout1}
              label="Text">
            </FormItem>

            <FormItem  label="检验描述" {...formItemLayout1}>
              {getFieldDecorator('name', {
                initialValue:"",
                rules: [{ whitespace:true, required: true, message: '请输入检验描述' },
                  {validator:this.handleDescName.bind(this),message: '检验描述已存在，请更改!' }]
              })(
                <Input/>
              )}
            </FormItem>
            <FormItem style={{marginBottom:"8px"}} label="要检验的字段名" {...formItemLayout1}>
              {getFieldDecorator('fieldName', {
                initialValue:""
              })(
                <Select   >
                  {
                    InputFields.map(index=>{
                       return(
                         <Option key={index.name} value={index.name}>{index.name}</Option>
                       )
                    })
                  }
                </Select>
              )}
            </FormItem>
            <FormItem  style={{marginBottom:"8px"}} label="错误代码" {...formItemLayout1}>
              {getFieldDecorator('errorCode', {
                initialValue:""
              })(
                <Input/>
              )}
            </FormItem>
            <FormItem style={{marginBottom:"8px"}} label="错误描述" {...formItemLayout1}>
              {getFieldDecorator('errorDescription', {
                initialValue:""
              })(
                <Input/>
              )}
            </FormItem>

            <FormItem
              style={{marginBottom:"8px"}}
              {...formItemLayout1}
              label="类型">
            </FormItem>

            <FormItem {...formItemLayout1}  style={{marginBottom:"0px",marginLeft:"13.5%"}}>
              {getFieldDecorator('dataTypeVerified', {
                valuePropName: 'checked',
                initialValue: config.dataTypeVerified,
              })(
                <Checkbox >检验数据类型</Checkbox>
              )}
            </FormItem>
            <FormItem style={{marginBottom:"8px"}} label="数据类型"  {...formItemLayout1}>
              {getFieldDecorator('dataType', {
                initialValue:""
              })(
                <Select  >
                  <Select.Option key="None" value="0">None</Select.Option>
                  <Select.Option key="Number" value="1">Number</Select.Option>
                  <Select.Option key="String" value="2">String</Select.Option>
                  <Select.Option key="Date" value="3">Date</Select.Option>
                  <Select.Option key="Boolean" value="4">Boolean</Select.Option>
                  <Select.Option key="Integer" value="5">Integer</Select.Option>
                  <Select.Option key="BigNumber" value="6">BigNumber</Select.Option>
                  <Select.Option key="Serializable" value="7">Serializable</Select.Option>
                  <Select.Option key="Binary" value="8">Binary</Select.Option>
                  <Select.Option key="Timestamp" value="9">Timestamp</Select.Option>
                  <Select.Option key="Internet Address" value="10">Internet Address</Select.Option>
                </Select>
              )}
            </FormItem>
            <FormItem style={{marginBottom:"8px"}} label="转换掩码" {...formItemLayout1}>
              {getFieldDecorator('conversionMask', {
                initialValue:""
              })(
                <Input/>
              )}
            </FormItem>
            <FormItem style={{marginBottom:"8px"}} label="小数点符号" {...formItemLayout1}>
              {getFieldDecorator('decimalSymbol', {
                initialValue:""
              })(
                <Input/>
              )}
            </FormItem>
            <FormItem style={{marginBottom:"8px"}} label="分组符号" {...formItemLayout1}>
              {getFieldDecorator('groupingSymbol', {
                initialValue:""
              })(
                <Input/>
              )}
            </FormItem>
            <FormItem
              style={{marginBottom:"8px"}}
              {...formItemLayout1}
              label="数据">
            </FormItem>
            <Row style={{marginBottom:"8px",marginLeft:"13.5%"}}>
                  <Col span={6}>
                    <FormItem {...formItemLayout1} style={{marginBottom:"0px",marginLeft:"13.5%"}}>
                      {getFieldDecorator('nullAllowed', {
                        valuePropName: 'checked',
                        initialValue: config.nullAllowed
                      })(
                        <Checkbox >允许空？</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={6}>
                    <FormItem {...formItemLayout1} style={{marginBottom:"0px",marginLeft:"13.5%"}}>
                      {getFieldDecorator('onlyNullAllowed', {
                        valuePropName: 'checked',
                        initialValue: config.onlyNullAllowed,
                      })(
                        <Checkbox >只允许空值</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                <Col span={6}>
                  <FormItem {...formItemLayout1} style={{marginBottom:"0px",marginLeft:"13.5%"}}>
                    {getFieldDecorator('onlyNumericAllowed', {
                      valuePropName: 'checked',
                      initialValue: config.onlyNumericAllowed,
                    })(
                      <Checkbox >只允许数值类型的数据</Checkbox>
                    )}
                  </FormItem>
                </Col>
            </Row>
            <FormItem style={{marginBottom:"8px"}} label="最大字符串长度" {...formItemLayout1}>
              {getFieldDecorator('maximumLength', {
                initialValue:""
              })(
                <Input/>
              )}
            </FormItem>
            <FormItem style={{marginBottom:"8px"}} label="最小字符串长度" {...formItemLayout1}>
              {getFieldDecorator('minimumLength', {
                initialValue:""
              })(
                <Input/>
              )}
            </FormItem>
            <FormItem style={{marginBottom:"8px"}} label="最大值" {...formItemLayout1}>
              {getFieldDecorator('maximumValue', {
                initialValue:""
              })(
                <Input/>
              )}
            </FormItem>
            <FormItem style={{marginBottom:"8px"}} label="最小值" {...formItemLayout1}>
              {getFieldDecorator('minimumValue', {
                initialValue:""
              })(
                <Input/>
              )}
            </FormItem>
            <FormItem style={{marginBottom:"8px"}} label="允许开始字符串" {...formItemLayout1}>
              {getFieldDecorator('startString', {
                initialValue:""
              })(
                <Input/>
              )}
            </FormItem>
            <FormItem style={{marginBottom:"8px"}} label="允许结束字符串" {...formItemLayout1}>
              {getFieldDecorator('endString', {
                initialValue:""
              })(
                <Input/>
              )}
            </FormItem>
            <FormItem style={{marginBottom:"8px"}} label="不允许开始字符串" {...formItemLayout1}>
              {getFieldDecorator('startStringNotAllowed', {
                initialValue:""
              })(
                <Input/>
              )}
            </FormItem>
            <FormItem style={{marginBottom:"8px"}} label="不允许结束字符串" {...formItemLayout1}>
              {getFieldDecorator('endStringNotAllowed', {
                initialValue:""
              })(
                <Input/>
              )}
            </FormItem>
            <FormItem style={{marginBottom:"8px"}} label="合法数据的正则表达式" {...formItemLayout1}>
              {getFieldDecorator('regularExpression', {
                initialValue:""
              })(
                <Input/>
              )}
            </FormItem>
            <FormItem style={{marginBottom:"8px"}} label="不合法数据的正则表达式" {...formItemLayout1}>
              {getFieldDecorator('regularExpressionNotAllowed', {
                initialValue:""
              })(
                <Input/>
              )}
            </FormItem>
            <div style={{margin:"0px 10%"}}>
              <ButtonGroup size={"small"} style={{marginBottom:"10px"}}>
                <Button  onClick={this.handleAdd}>添加字段</Button>
                <Button  onClick={this.handleDeleteFields.bind(this)}>删除字段</Button>
              </ButtonGroup>

              <EditTable  rowSelection={true} columns={this.columns} tableStyle="editTableStyle5" dataSource={[]} size={"small"} scroll={{y: 140}} ref="groupFields"   count={4}/>
            </div>
            <FormItem {...formItemLayout1} style={{marginBottom:"0px",marginLeft:"13.5%"}}>
              {getFieldDecorator('sourcingValues', {
                valuePropName: 'checked',
                initialValue: config.sourcingValues,
              })(
                <Checkbox >从其他的步骤获得允许的值</Checkbox>
              )}
            </FormItem>
            <FormItem style={{marginBottom:"8px"}} label="要读取步骤" {...formItemLayout1}>
              {getFieldDecorator('sourcingStepName', {
                initialValue:""
              })(
                <Select allowClear  onChange={this.handleSelectChange.bind(this)} disabled={setDisabled2() === false}>
                  {
                    prevStepNames.map(index =>{
                       return(
                         <Option value={index} key={index}>{index}</Option>
                       )
                    })
                  }
                </Select>
              )}
            </FormItem>
            <FormItem style={{marginBottom:"8px"}} label="要读取的字段" {...formItemLayout1}>
              {getFieldDecorator('sourcingField', {
                initialValue:""
              })(
                <Select allowClear  disabled={setDisabled2() === false}>
                  {
                    OutputFields.map(index=>{
                       return(
                         <Option value={index.name} key={index.name}>{index.name}</Option>
                       )
                    })
                  }
                </Select>
              )}
            </FormItem>
            </div>
          </div>
          </div>
        </Form>
      </Modal>
    );
  }
}
const Validator = Form.create()(ValidatorModel);
export default connect()(Validator);
