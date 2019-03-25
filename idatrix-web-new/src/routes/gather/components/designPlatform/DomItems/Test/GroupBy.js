import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col,Card } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable'

class GroupBy extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true) {
      const {groupFields,subjectField} = props.model.config;
      let data = [];
      let data1 = [];
      if (groupFields) {
        let count = 0;
        for (let index of groupFields) {
          data.push({
            "key": count,
            "groupFields":index
          });
          count++;
        }
      }
      if (subjectField) {
        let count = 0;
        for (let index of subjectField) {
          data1.push({
            "key": count,
            ...index
          });
          count++;
        }
      }
      this.state = {
        InputData:[],
        dataSource:data,
        dataSource1:data1
      }
    }
  }

  componentDidMount(){
    this.Request();
  }

  Request(){
    const { getInputFields,transname,text,getInputSelect } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj, data => {
      this.setState({
        InputData:data
      });
      let options = getInputSelect(data,"name");
      this.refs.groupFields.updateOptions({
        groupFields:options
      });
      this.refs.subjectField.updateOptions({
        subjectField:options
      });
    })
  };

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
     const {groupFields,subjectField} = config;
      Modal.warning({
        title: '警告',
        content: '分组的功能需要已经根据指定字段排好序，如果你没有把输入排序，只会考虑相同的行！',
      });
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      let sendFields1 = [];
      if(this.refs.groupFields){
        if(this.refs.groupFields.state.dataSource.length>0){
          let args = [ "groupFields"];
          let nums = formatTable(this.refs.groupFields.state.dataSource,args);
          for(let index of nums){
            sendFields.push(index["groupFields"]);
          }
        }
      }else{
        if(groupFields){
          sendFields = groupFields
        }
      }
      if(this.refs.subjectField){
        if(this.refs.subjectField.state.dataSource.length>0){
          let args = [ "aggregateField", "subjectField","aggregateType","valueField"];
          sendFields1 = formatTable(this.refs.subjectField.state.dataSource,args);
        }
      }else{
        if(subjectField){
          sendFields1 = subjectField
        }
      }

      if(!values.passAllRows){
        values.prefix = "";
        values.directory = "";
      };
      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
         groupFields:sendFields,
         subjectField:sendFields1,
         ...values
      };
      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });

    });
  }

  /*增加字段 keys*/
  handleAdd = ()=>{
    const data = {
      "groupFields":""
    };
    this.refs.groupFields.handleAdd(data);
  };
    /*增加字段 values*/
  handleAdd1 = ()=>{
    const data = {
      "aggregateField":"",
      "subjectField":"",
      "aggregateType":"",
      "valueField":""
    };
    this.refs.subjectField.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.groupFields.handleDelete();
  };
  /*删除字段*/
  handleDeleteFields1 = ()=>{
    this.refs.subjectField.handleDelete();
  };

  /*文件表格*/
  columns =  [
    {
    title: '分组字段',
    dataIndex: 'groupFields',
    width:"98%",
    key: 'groupFields',
    selectable:true
  }];
    /*文件表格*/
  columns1 =  [
    {
    title: '名称',
    dataIndex: 'aggregateField',
    width:"25%",
    key: 'aggregateField',
    editable:true
  },{
    title: 'Subject',
    dataIndex: 'subjectField',
    width:"25%",
    key: 'subjectField',
    selectable:true
  },{
    title: '类型',
    dataIndex: 'aggregateType',
    width:"35%",
    key: 'aggregateType',
    selectable:true,
    selectArgs:[
      <Select.Option  key="0" value="0">-</Select.Option>,
      <Select.Option key="1" value="1">求和</Select.Option>,
      <Select.Option key="2" value="2">平均</Select.Option>,
      <Select.Option key="3" value="3">Median</Select.Option>,
      <Select.Option key="4" value="4">Percentile</Select.Option>,
      <Select.Option key="5" value="5">最小</Select.Option>,
      <Select.Option key="6" value="6">最大</Select.Option>,
      <Select.Option key="7" value="7">个数</Select.Option>,
      <Select.Option key="8" value="8">使用 , 连接同组字符串</Select.Option>,
      <Select.Option key="9" value="9">第一个非空值</Select.Option>,
      <Select.Option key="10" value="10">最后一个非空值</Select.Option>,
      <Select.Option key="11" value="11">第一个值</Select.Option>,
      <Select.Option key="12" value="12">最后一个值</Select.Option>,
      <Select.Option key="13" value="13">累计求和(对所有行)</Select.Option>,
      <Select.Option key="14" value="14">累计平均(对所有行)</Select.Option>,
      <Select.Option key="15" value="15">标准差</Select.Option>,
      <Select.Option key="16" value="16">使用指定字符连接同组字符串</Select.Option>,
      <Select.Option key="17" value="17">不同值的数目 (N)</Select.Option>,
      <Select.Option key="18" value="18">行数（无字段参数</Select.Option>,
    ]
  },{
    title: '值',
    dataIndex: 'valueField',
    width:"15%",
    key: 'valueField',
    editable:true
  }];


  handleFocus(){
    const { InputData } = this.state;
    let tabel1 = [];
    let count1 = 0;

    for(let index of InputData){
      tabel1.push({
        "key":count1,
        "groupFields": index.name
      });
      count1++;
    }
    this.refs.groupFields.updateTable(tabel1,count1);
  }

handleFocus1(){
    const { InputData } = this.state;
    let tabel1 = [];
    let count1 = 0;
    for(let index of InputData){
      tabel1.push({
        "key":count1,
        "aggregateField": index.name,
        "subjectField": index.name
      });
      count1++;
    }
    this.refs.subjectField.updateTable(tabel1,count1);
  }


render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;

    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };

  const setDisabled = ()=>{
      if(getFieldValue("passAllRows") === undefined){
        return config.passAllRows;
      }else{
        if(getFieldValue("passAllRows")){
          return getFieldValue("passAllRows");
        }else {
          return false;
        }
      }
    };

  const setDisabled1 = ()=>{
    if(getFieldValue("addingLineNrInGroup") === undefined){
      return config.addingLineNrInGroup;
    }else{
      if(getFieldValue("addingLineNrInGroup")){
        return getFieldValue("addingLineNrInGroup");
      }else {
        return false;
      }
    }
  };

    return (
      <Modal
        visible={visible}
        title="分组"
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
          <FormItem label=""   style={{marginBottom:"8px",marginLeft:"25%"}}  {...formItemLayout1}>
               {getFieldDecorator('passAllRows', {
                  valuePropName: 'checked',
                  initialValue:config.passAllRows
                  })(
                 <Checkbox >包括所有的行</Checkbox>
               )}
           </FormItem>
          <FormItem label="排序目录"   style={{marginBottom:"8px",display:"none"}}  {...formItemLayout1}>
            {getFieldDecorator('directory', {
              initialValue:config.directory
            })(
              <Input />
            )}
             <Button style={{display:"none"}}  disabled>浏览</Button>
          </FormItem>
          <FormItem label="临时文件前缀"   style={{marginBottom:"8px",display:"none"}}  {...formItemLayout1}>
            {getFieldDecorator('prefix', {
              initialValue:config.prefix,
            })(
              <Input />
            )}
           </FormItem>
           <FormItem label=""   style={{marginBottom:"8px",marginLeft:"25%"}}  {...formItemLayout1}>
               {getFieldDecorator('addingLineNrInGroup', {
                  valuePropName: 'checked',
                  initialValue:config.addingLineNrInGroup
                  })(
                 <Checkbox  disabled={!setDisabled() }>增加行号，每组重新开始</Checkbox>
               )}
           </FormItem>
           <FormItem label="行号列名"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('lineNrInGroupField', {
              initialValue:config.lineNrInGroupField
            })(
              <Input disabled={!setDisabled() || !setDisabled1()}/>
            )}
          </FormItem>
          <FormItem label=""   style={{marginBottom:"8px",marginLeft:"25%"}}  {...formItemLayout1}>
               {getFieldDecorator('alwaysGivingBackOneRow', {
                  valuePropName: 'checked',
                  initialValue:config.alwaysGivingBackOneRow
                  })(
                 <Checkbox >总返回一个结果行</Checkbox>
               )}
           </FormItem>
          <div style={{margin:"10px 5% 0"}}>
            <Row style={{marginBottom:"5px"}}>
              <Col span={12}>
                <p style={{marginLeft:"5px"}}>构成分组的字段:</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{float:"right"}} >
                  <Button onClick={this.handleAdd.bind(this)}>添加字段</Button>
                  <Button onClick={this.handleFocus.bind(this)}>获取字段</Button>
                  <Button onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                </ButtonGroup>
              </Col>
            </Row>
            <EditTable columns={this.columns} tableStyle="editTableStyle5"  scroll={{y: 140}} ref="groupFields" rowSelection={true} size={"small"} count={1} dataSource={this.state.dataSource}/>
          </div>

          <div style={{margin:"10px 5% 0"}}>
            <Row style={{marginBottom:"5px"}}>
              <Col span={12}>
                <p style={{marginLeft:"5px"}}>聚合:</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{float:"right"}} >
                  <Button onClick={this.handleAdd1.bind(this)}>添加字段</Button>
                  <Button onClick={this.handleFocus1.bind(this)}>获取字段</Button>
                  <Button onClick={this.handleDeleteFields1.bind(this)} >删除字段</Button>
                </ButtonGroup>
              </Col>
            </Row>
            <EditTable columns={this.columns1} tableStyle="editTableStyle5"  scroll={{y: 140}} ref="subjectField" rowSelection={true} size={"small"} count={1} dataSource={this.state.dataSource1}/>
          </div>
        </Form>
      </Modal>
    );
  }
}
const GroupingForm = Form.create()(GroupBy);
export default connect()(GroupingForm);
