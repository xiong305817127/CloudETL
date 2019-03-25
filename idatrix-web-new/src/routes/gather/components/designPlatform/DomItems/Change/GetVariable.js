import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col,Card,Icon } from 'antd';
import Modal from "components/Modal.js";
const Option = Select.Option;
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
const confirm = Modal.confirm;
import EditTable from '../../../common/EditTable'
import { selectType } from '../../../../constant';
class GetVariable extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    const { fieldDefinitions } = props.model.config;

    this.state={
       visibleS:false  
    };
      let data = [];

    if(fieldDefinitions){
        let count = 0;
        for(let index of fieldDefinitions){
          data.push({
            key:count++,
            ...index
          })
        }
      }
      this.state = {
        dataSource:data,
        InputData:[],
        InputDataS:[],
        oldStepList:[],
        message:'',
        validatorName:''
      }
    
  }


      /*文件表格*/
  columns =  [{
    title: '变量',
    dataIndex: 'variablestring',
    key: 'variablestring',
    width:'21%',
    selectable:true,
    bindField:"fieldname",
    bindFuc:(value)=>{
      let str = value.split("{")[1].split("}")[0];
      return str;
    }
  },{
    title: '名称',
    dataIndex: 'fieldname',
    key: 'fieldname',
    width:'21%',
     selectable:true,
  },{
    title: '类型',
    dataIndex: 'fieldtype',
    key: 'fieldtype',
    width:'8%',
    selectable:true,
    selectArgs:selectType.get("numberType"),
  },{
    title: '格式',
    dataIndex: 'fieldformat',
    key: 'fieldformat',
    width:'10%',
     selectable:true,
    selectArgs:selectType.get("dateType")
  },{
    title: '长度',
    dataIndex: 'currency',
    key: 'currency',
    width:'5%',
    editable:true
  },{
    title: '精度',
    dataIndex: 'decimal',
    key: 'decimal',
    width:'5%',
    editable:true
  },{
    title: '货币类型',
    dataIndex: 'group',
    key: 'group',
    width:'6%',
    editable:true
  },{
    title: '小数',
    dataIndex: 'length',
    key: 'length',
    width:'5%',
    editable:true
  },{
    title: '分组',
    dataIndex: 'precision',
    key: 'precision',
    width:'5%',
    editable:true
  },{
    title: '去除空格类型',
    dataIndex: 'trimType',
    key: 'trimType',
    selectable:true,
    selectArgs:selectType.get("trimType")
  }];
 

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
     const { fieldDefinitions  } = config;
    
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
       let sendFields = [];
        if(this.refs.editTable){
          if(this.refs.editTable.state.dataSource.length>0){
            let arg = ["fieldname","variablestring","fieldtype","fieldformat","currency","decimal","group","length","precision","trimType"];
            sendFields = formatTable(this.refs.editTable.state.dataSource,arg)
          }
        }else{
          if(fieldDefinitions){
            sendFields = fieldDefinitions;
          }
      }

      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        ...values,
        "fieldDefinitions": sendFields,
      };

      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  }
   formItemLayout3 = {
      labelCol: { span: 7 },
      wrapperCol: { span: 14 },
    };

  onChangeVis(){
  	 this.setState({
  	 	visibleS:false
  	 })
  }
  componentDidMount(){
    this.Request();
  };
  Request(){
    const { getVariables,transname,text,getInputSelect,getInputSelectMultiway } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getVariables(obj, data => {
      var keys=[];
      var values=[];
      
       let options = getInputSelectMultiway("$","{",Object.keys(data),"","}");
      
       let options1 = getInputSelectMultiway("","",Object.keys(data),"","");
       
      
       this.refs.editTable.updateOptions({
          variablestring:options,
          fieldname:options1
        });
    })
  };
  /*增加字段*/
  handleAdd = ()=>{
    const data = {
      "fieldname": "",
	    "variablestring": "",
	    "fieldtype": "",
	    "fieldformat": "",
	    "currency": "",
	    "decimal": "",
	    "group": "",
	    "length": "",
	    "precision": "",
	    "trimType": "none"
    }
    this.refs.editTable.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };



  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName,getInputSelectMultiway } = this.props.model;
    const formItemLayout1 = { 
      labelCol: { span: 7 },
      wrapperCol: { span: 14 },
    };
    const formItemLayout2 = {
      labelCol: { span: 4 },
      wrapperCol: { span: 21 },
    };
    return (
      <Modal
        visible={visible}
        title="获取变量"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={1200}
        footer={[
                  <Button key="submit" type="primary" size="large" onClick={this.handleCreate.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={this.hideModal.bind(this)}>取消</Button>,
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
          <div style={{margin:"0 5%"}}>
              <Row style={{marginBottom:"5px"}}>
                <Col span={12}>字段</Col>
                <Col span={12} >
                  <ButtonGroup size={"small"} style={{float:"right"}}>
                    <Button onClick={this.handleAdd.bind(this)}>添加字段</Button>
                    <Button onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>  
                  </ButtonGroup>                  
                </Col>
            </Row>
            <div>
                <EditTable columns={this.columns} tableStyle="editTableStyle5" ref="editTable" scroll={{y: 300,x:1300}} rowSelection={true} 
                  size={"small"}   dataSource = {this.state.dataSource}/>
            </div>
          </div>
        </Form>
      </Modal>
    );
  }
}
const GetVariableList = Form.create()(GetVariable);

export default connect()(GetVariableList);
