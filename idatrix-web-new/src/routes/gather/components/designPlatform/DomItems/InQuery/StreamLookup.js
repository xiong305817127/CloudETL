import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col,Card } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
const Option = Select.Option;
import EditTable from '../../../common/EditTable';
const getType = new Map([[
  "Number","1"
],[
  "String","2"
],[
  "Date","3"
],[
  "Boolean","4"
],[
  "Integer","5"
],[
  "BigNumber","6"
],[
  "Binary","8"
],[
  "Timestamp","9"
],[
  "Internet Address","10"
]]);

class SwitchCase extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true) {
      const {keys,values} = props.model.config;
      let data = [];
      let data1 = [];
      if (keys) {
        let count = 0;
        for (let index of keys) {
          data.push({
            "key": count,
            ...index
          });
          count++;
        }
      }
      if (values) {
        let count = 0;
        for (let index of values) {
          data1.push({
            "key": count,
            ...index
          });
          count++;
        }
      }

      this.state = {
        dataSource:data,
        dataSource1:data1,
        OutputData:[],
        InputData:[]
      }
    }
  }

  componentDidMount(){
    this.Request();
  }

  Request(){
    const { getInputFields,transname,text,config,getInputSelect } = this.props.model;
    const { fromStep } = config;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj, data => {
      let args = getInputSelect(data,"name");
      this.refs.keys.updateOptions({
        name:args
      });
      this.setState({
        InputData:data
      });
    });
    if(fromStep){
      this.updateSelect(fromStep);
    }
  };

  updateSelect(name){
      const {transname,getInputSelect,getOutFields } = this.props.model;
      let obj = {};
      obj.transname = transname;
      obj.stepname = name;
      getOutFields(obj, data => {
        let args = getInputSelect(data,"name");
        this.refs.keys.updateOptions({
          field:args
        });
        this.refs.values.updateOptions({
          name:args
        });
        this.setState({
          OutputData:data
        });
      });
  }

  hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  };

  handleCreate = () => {

    const form = this.props.form;
    const { panel,transname,description,key,saveStep,text,config,formatTable } = this.props.model;
     const {keys} = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      let sendFields1 = [];
      if(this.refs.keys){
        if(this.refs.keys.state.dataSource.length>0){
          let args = [ "name", "field"];
          sendFields = formatTable(this.refs.keys.state.dataSource,args);
        }
      }else{
        if(keys){
          sendFields = keys
        }
      }
      if(this.refs.values){
        if(this.refs.values.state.dataSource.length>0){
          let args = [ "name", "rename","defaultValue","type"];
          sendFields1 = formatTable(this.refs.values.state.dataSource,args);
        }
      }else{
        if(values){
          sendFields1 = values
        }
      }
      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
         keys:sendFields,
         values:sendFields1,
         ...values
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
      "name":"",
      "field":""
    };
    this.refs.keys.handleAdd(data);
  };
    /*增加字段 values*/
  handleAdd1 = ()=>{
    const data = {
      "name":"",
      "rename":"",
      "defaultValue":"",
      "type":""
    }
    this.refs.values.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.keys.handleDelete();
  };
  /*删除字段*/
  handleDeleteFields1 = ()=>{
    this.refs.values.handleDelete();
  };

  handleGetKeys(){
    const { InputData } = this.state;
    let args = [];
    let count = 0;
    for(let index of InputData){
      args.push({
        key:count,
        name:index.name,
        field:index.name
      });
      count++;
    }
    this.refs.keys.updateTable(args,count);
  };

  handleCetValues(){
    const { OutputData } = this.state;
    let args = [];
    let count = 0;
    for(let index of OutputData){
      args.push({
        key:count,
        name:index.name,
        rename:"",
        field:"",
        defaultValue:"",
        type:getType.get(index.type)
      });
      count++;
    }
    this.refs.values.updateTable(args,count);
  }



render() {
    const { getFieldDecorator } = this.props.form;
    const { text,config,visible,handleCheckName,prevStepNames } = this.props.model;


    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 }
    };
    const formItemLayout = {
      wrapperCol: { span:18},
    };


  /*文件表格*/
  const columns =  [
    {
      title: '字段',
      dataIndex: 'name',
      width:"50%",
      key: 'name',
      selectable:true
    },{
      title: '查询字段',
      dataIndex: 'field',
      width:"50%",
      key: 'field',
      selectable:true
    }];
  /*文件表格*/
  const columns1 =  [
    {
      title: 'Field',
      dataIndex: 'name',
      width:"25%",
      key: 'name',
      selectable:true
    },{
      title: '新的名称',
      dataIndex: 'rename',
      width:"25%",
      key: 'rename',
      editable:true
    },{
      title: '默认',
      dataIndex: 'defaultValue',
      width:"25%",
      key: 'defaultValue',
      editable:true
    },{
      title: '类型',
      dataIndex: 'type',
      width:"25%",
      key: 'type',
      selectable:true,
      selectArgs:[
        <Select.Option key="1" value="1">Number</Select.Option>,
        <Select.Option key="3" value="3">Date</Select.Option>,
        <Select.Option key="2" value="2">String</Select.Option>,
        <Select.Option key="4" value="4">Boolean</Select.Option>,
        <Select.Option key="5" value="5">Integer</Select.Option>,
        <Select.Option key="6" value="6">BigNumber</Select.Option>,
        <Select.Option key="8" value="8">Binary</Select.Option>,
        <Select.Option key="9" value="9">Timestamp</Select.Option>,
        <Select.Option key="10" value="10">Internet Address</Select.Option>]
    }];

    return (

      <Modal
        visible={visible}
        title="流里的值查询"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={650}
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
          <FormItem label="查找步骤"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('fromStep', {
              initialValue:config.fromStep
            })(
              <Select  onChange={this.updateSelect.bind(this)}>
                {
                  prevStepNames.map(index=>{
                     return(
                        <Option value={index} key={index}>{index}</Option>
                     )
                  })
                }
              </Select>
            )}
          </FormItem>

          <div style={{margin:"10px 5% 0"}}>
            <Row style={{marginBottom:"5px"}}>
              <Col span={12}>
                <p style={{marginLeft:"5px"}}>查询值所需的关键字:</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{float:"right"}} >
                  <Button onClick={this.handleAdd.bind(this)}>添加字段</Button>
                  <Button onClick={this.handleGetKeys.bind(this)}>获取字段</Button>
                  <Button onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                </ButtonGroup>
              </Col>
            </Row>
            <EditTable columns={columns} tableStyle="editTableStyle5" ref="keys" rowSelection={true} size={"small"}  scroll={{y: 140}} count={1} dataSource={this.state.dataSource}/>
          </div>

          <div style={{margin:"10px 5% 0"}}>
            <Row style={{marginBottom:"5px"}}>
              <Col span={12}>
                <p style={{marginLeft:"5px"}}>指定用来接收的字段:</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{float:"right"}} >
                  <Button onClick={this.handleAdd1.bind(this)}>添加字段</Button>
                  <Button onClick={this.handleCetValues.bind(this)}>获取字段</Button>
                  <Button onClick={this.handleDeleteFields1.bind(this)} >删除字段</Button>
                </ButtonGroup>
              </Col>
            </Row>
            <EditTable columns={columns1} tableStyle="editTableStyle5" ref="values" rowSelection={true} size={"small"} scroll={{y: 140}} count={1} dataSource={this.state.dataSource1}/>
          </div>
          <Row style={{marginLeft:"11%",marginTop:"10px"}}>
             <Col span={12}>
               <FormItem  style={{marginBottom:"0px"}} {...formItemLayout}>
                 {getFieldDecorator('preserveMemory', {
                   valuePropName: 'checked',
                   initialValue:config.preserveMemory
                 })(
                   <Checkbox >保留内存(消耗CPU)</Checkbox>
                 )}
               </FormItem>
             </Col>
             <Col span={12}>
               <FormItem  style={{marginBottom:"0px"}} {...formItemLayout}>
                 {getFieldDecorator('integerPair', {
                   valuePropName: 'checked',
                   initialValue:config.integerPair
                 })(
                   <Checkbox > 排序的时候对数据行进行编码？</Checkbox>
                 )}
               </FormItem>
             </Col>
          </Row>
             <FormItem  style={{marginLeft:"11%",marginTop:"10px"}} {...formItemLayout}>
               {getFieldDecorator('sortedList', {
                 valuePropName: 'checked',
                 initialValue:config.sortedList
               })(
                 <Checkbox >是否用一个排序列表来存储值(能提供更好的内存使用)?</Checkbox>
               )}
             </FormItem>

        </Form>
      </Modal>
    );
  }
}
const FlowQueryForm = Form.create()(SwitchCase);
export default connect()(FlowQueryForm);
