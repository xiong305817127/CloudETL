import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col,Card,Icon } from 'antd';
import Modal from "components/Modal.js";
const Option = Select.Option;
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
const confirm = Modal.confirm;
import EditTable from '../../../common/EditTable'

class SetVariable extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    const { fieldName,usingFormatting } = props.model.config;
    this.state={
       visibleS:false  
    };
      let data = [];

    if(fieldName){
        let count = 0;
        for(let index of fieldName){
          data.push({
            key:count,
            ...index
          })
          count++;
        }
      }
      this.state = {
        dataSource:data,
        InputData:[],
        InputDataS:[],
        oldStepList:[],
      }
    
  }

      /*文件表格*/
  columns =  [{
    title: '字段名称',
    dataIndex: 'fieldName',
    key: 'fieldName',
    width:'30%',
    editable:true
  },{
    title: '变量名',
    dataIndex: 'variableName',
    key: 'variableName',
    width:'30%',
    editable:true
  },{
    title: '变量活动类型',
    dataIndex: 'variableType',
    key: 'variableType',
    width:'20%',
    selectable:true,
    selectArgs:[
       <Select.Option key="0" value="0">Valid in the Java Virtual Machine</Select.Option>,
       <Select.Option key="1" value="1">Valid in the parent job</Select.Option>,
       <Select.Option key="2" value="2">Valid in the grand-parent job</Select.Option>,
       <Select.Option key="3" value="3">Valid in the root job</Select.Option>,
    ]
  },{
    title: 'Default value',
    dataIndex: 'defaultValue',
    key: 'defaultValue',
    width:'20%',
    editable:true
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
     const { fieldName  } = config;
    
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
       Modal.warning({
	    title: '警告',
	    content: '“您在此步骤中定义的变量不能用于此转换。这仅仅是因为一个转换中的所有步骤都并行运行而没有执行的某个顺序。作为替代的正确用法，您可以设置要在作业的第一次转换中使用的变量。！',
	  });

       let sendFields = [];
        if(this.refs.editTable){
          if(this.refs.editTable.state.dataSource.length>0){
            let arg = ["fieldName","variableName","variableType","defaultValue"];
            sendFields = formatTable(this.refs.editTable.state.dataSource,arg)
          }
        }else{
          if(fieldName){
            sendFields = fieldName;
          }
      }
	    console.log(sendFields,"sendFields");      
      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        /*...values,*/
        "usingFormatting":values.usingFormatting,
        "fieldName": sendFields,
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
    })
  };
  /*增加字段*/
  handleAdd = ()=>{
    const data = {
        "fieldname": null,
	    "variableName": null,
	    "variableType": null,
	    "defaultValue": null,
    }
    this.refs.editTable.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };

  handleFocus(){
     const { getInputFields,transname,text } = this.props.model;
    const { InputData } = this.state;
     const form = this.props.form;
     form.validateFields((err, values) => {
      if (err) {
        return;
      }
        let args = [];
        let count = 0;
         let obj = {};
          obj.transname = transname;
          obj.stepname = text;

         getInputFields(obj, data => {
            for(let index of data){
            args.push({
              "key":count,
              "fieldName": index.name,
            });
            this.setState({
               dataSource:args
            })
            count++;
          }
         })
        console.log(args,"数值");
        this.refs.editTable.updateTable(args,count);
       
    })
  }
  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;
    console.log(config.usingFormatting,"return111111");
    const formItemLayout1 = { 
      labelCol: { span: 7 },
      wrapperCol: { span: 14 },
    };
    const formItemLayout2 = {
      labelCol: { span: 4 },
      wrapperCol: { span: 10 },
    };
    return (
      <Modal
        visible={visible}
        title="设置变量"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={900}
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
             <FormItem  style={{marginBottom:"8px"}}  {...formItemLayout2}>
                {getFieldDecorator('usingFormatting', {
                  valuePropName: 'checked',
                  initialValue:config.usingFormatting,
                })(
                    <Checkbox >Apple formatting</Checkbox>
                )}
              </FormItem>
              <Row style={{marginBottom:"5px"}}>
                <Col span={12}>
                  <p style={{marginLeft:"5px"}}>要处理的字段：</p>
                </Col>
                <Col span={12}>
                  <ButtonGroup size={"small"} style={{float:"right"}} >
                    <Button     onClick={this.handleAdd.bind(this)}>添加字段</Button>
                    <Button     onClick={this.handleFocus.bind(this)}>获取字段</Button>
                    <Button     onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                  </ButtonGroup>
                </Col>
              </Row>
              <EditTable columns={this.columns} tableStyle="editTableStyle5" ref="editTable" rowSelection={true} 
                      scroll={{y: 600}} size={"small"} count={4}  dataSource = {this.state.dataSource}/>
          </div>
        </Form>
      </Modal>
    );
  }
}
const SetVariableList = Form.create()(SetVariable);
export default connect()(SetVariableList);
  