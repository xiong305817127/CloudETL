import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable'

class OperationsDialog extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true) {
      const {fieldInStream} = props.model.config;
      let data = [];
      if (fieldInStream) {
        let count = 0;
        for (let index of fieldInStream) {
          data.push({
            "key": count,
            ...index
          });
          count++;
        }
      }
      this.state = {
        dataSource:data,
        InputData:[]
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
      if(this.refs.editTable){
        let options = getInputSelect(data,"name");
        this.refs.editTable.updateOptions({
          fieldInStream:options
        });
      }
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
    const { fieldInStream } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let obj = {};
      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = [    "fieldInStream", "fieldOutStream", "trimType", "lowerUpper", "padding_type", "padChar", "padLen", "initCap", "maskXML", "digits", "removeSpecialCharacters"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,args);
        }
      }else{
        if(fieldInStream){
          sendFields = fieldInStream
        }
      }

      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        fieldInStream:sendFields
      };

      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });

    });
  }

  /*增加字段*/
  handleAdd = ()=>{
    const data = {
      "fieldInStream": null,
      "fieldOutStream": null,
      "trimType": "",
      "lowerUpper": "",
      "padding_type": "",
      "padChar": null,
      "padLen": null,
      "initCap": "",
      "maskXML": "",
      "digits": "",
      "removeSpecialCharacters": ""
    }
    this.refs.editTable.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };

  /*文件表格*/
  columns =  [{
    title: '输入流字段',
    dataIndex: 'fieldInStream',
    width:"10%",
    key: 'fieldInStream',
    selectable:true
  }, {
    title: '输出流字段',
    dataIndex: 'fieldOutStream',
    width:"10%",
    key: 'fieldOutStream',
    editable:true
  }, {
    title: '去除空字符串方式',
    dataIndex: 'trimType',
    width:"10%",
    key: 'trimType',
    selectable:true,
    selectArgs:[
      <Select.Option key="none" value="none">不去掉空格</Select.Option>,
      <Select.Option key="left" value="left">去掉左空格</Select.Option>,
      <Select.Option key="right" value="right">去掉右空格</Select.Option>,
      <Select.Option key="both" value="both">去掉左右两边空格</Select.Option>
    ]
  } ,{
    title: 'Lower/Upper',
    dataIndex: 'lowerUpper',
    width:"8%",
    key: 'lowerUpper',
    selectable:true,
    selectArgs:[
      <Select.Option key="0" value="0">none</Select.Option>,
      <Select.Option key="1" value="1">lower</Select.Option>,
      <Select.Option key="2" value="2">upper</Select.Option>
    ]
  },{
    title: 'Padding',
    dataIndex: 'padding_type',
    width:"7%",
    key: 'padding_type',
    selectable:true,
    selectArgs:[
      <Select.Option key="0" value="0">none</Select.Option>,
      <Select.Option key="1" value="1">left</Select.Option>,
      <Select.Option key="2" value="2">right</Select.Option>
    ]
  },{
    title: 'Pad char',
    dataIndex: 'padChar',
    width:"8%",
    key: 'padChar',
    editable:true
  },{
    title: 'Pad Length',
    dataIndex: 'padLen',
    width:"7%",
    key: 'padLen',
    editable:true
  },{
    title: 'initCap',
    dataIndex: 'initCap',
    width:"6%",
    key: 'initCap',
    selectable:true,
    selectArgs:[
      <Select.Option key="0" value="0">否</Select.Option>,
      <Select.Option key="1" value="1">是</Select.Option>
    ]
  },{
    title: 'Escape',
    dataIndex: 'maskXML',
    width:"11%",
    key: 'maskXML',
    selectable:true,
    selectArgs:[
      <Select.Option key="0" value="0">None</Select.Option>,
      <Select.Option key="1" value="1">Escape XML</Select.Option>,
      <Select.Option key="2" value="2">Use CDATA</Select.Option>,
      <Select.Option key="3" value="3">Unescape XML</Select.Option>,
      <Select.Option key="4" value="4">Escape SQL</Select.Option>,
      <Select.Option key="5" value="5">Escape HTML</Select.Option>,
      <Select.Option key="6" value="6">Unescape HTML</Select.Option>
    ]
  },{
    title: 'Digits',
    dataIndex: 'digits',
    width:"7%",
    key: 'digits',
    selectable:true,
    selectArgs:[
      <Select.Option key="0" value="0">none</Select.Option>,
      <Select.Option key="1" value="1">only</Select.Option>,
      <Select.Option key="2" value="2">remove</Select.Option>,
    ]
  },{
    title: '移除特殊字符',
    dataIndex: 'removeSpecialCharacters',
    key: 'removeSpecialCharacters',
    selectable:true,
    selectArgs:[
      <Select.Option key="0" value="0">none</Select.Option>,
      <Select.Option key="1" value="1">carriage return (CR)</Select.Option>,
      <Select.Option key="2" value="2">line feed (LF)</Select.Option>,
      <Select.Option key="3" value="3">carriage return & line feed</Select.Option>,
      <Select.Option key="4" value="4">horizontal tab</Select.Option>,
      <Select.Option key="5" value="5">space</Select.Option>,
    ]
  }];


  getFields(){
    const { InputData } = this.state;
    let args = [];
    let count =0;
    for(let index of InputData){
      args.push({
        "key":count,
        "fieldInStream": index.name,
        "fieldOutStream": "",
        "trimType": index.trimType,
        "lowerUpper": "0",
        "padding_type": "0",
        "padChar": "",
        "padLen": "",
        "initCap": "0",
        "maskXML": "0",
        "digits": "0",
        "removeSpecialCharacters": "0"
      });
      count++;
    }
    this.refs.editTable.updateTable(args,count);
  }



  render() {
    const { getFieldDecorator } = this.props.form;
    const { text,visible,handleCheckName } = this.props.model;

    const formItemLayout1 = {
      labelCol: { span: 7 },
      wrapperCol: { span: 14 },
    };

    return (

      <Modal
        visible={visible}
        title="字符串操作"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={850}
        footer={[
                  <Button key="submit" type="primary" size="large" onClick={this.handleCreate} >
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={this.hideModal}>取消</Button>,
                ]}
        onCancel = {this.hideModal}
      >
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
          <div style={{margin:"10px 5% 0"}}>
            <Row style={{marginBottom:"5px"}}>
              <Col span={12}>
                <p style={{marginLeft:"5px"}}>要处理的字段：</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{float:"right"}} >
                  <Button     onClick={this.handleAdd.bind(this)}>添加字段</Button>
                  <Button     onClick={this.getFields.bind(this)}>获取字段</Button>
                  <Button     onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                </ButtonGroup>
              </Col>
            </Row>
            <EditTable  columns={this.columns}   tableStyle="editTableStyle5" ref="editTable" scroll={{y: 140,x:1400}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.dataSource}/>
          </div>
        </Form>
      </Modal>
    );
  }
}
const StringOperations = Form.create()(OperationsDialog);

export default connect()(StringOperations);
