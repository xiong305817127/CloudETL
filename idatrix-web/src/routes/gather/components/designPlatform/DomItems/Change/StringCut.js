import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable'

class CutDialog extends React.Component {

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
          let args = [  "fieldInStream", "fieldOutStream", "cutFrom", "cutTo"];
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
        fieldInStream:sendFields,
        ...values
      };

      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });

    });
  };


  /*增加字段*/
  handleAdd = ()=>{
    const data = {
      "fieldInStream": "",
      "fieldOutStream":"",
      "cutFrom":"",
      "cutTo":""
    }
    this.refs.editTable.handleAdd(data);
  }

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  }

  /*文件表格*/
  columns =  [{
    title: '输入流字段',
    dataIndex: 'fieldInStream',
    width:"28%",
    key: 'fieldInStream',
    selectable:true
  }, {
    title: '输出流字段',
    dataIndex: 'fieldOutStream',
    width:"28%",
    key: 'fieldOutStream',
    editable:true
  }, {
    title: '起始位置',
    dataIndex: 'cutFrom',
    width:"19%",
    key: 'cutFrom',
    editable:true
  } ,{
    title: '结束位置',
    dataIndex: 'cutTo',
    width:"19%",
    key: 'cutTo',
    editable:true
  }];

  handleFocus(){
    const { InputData } = this.state;

    let tabel1 = [];
    let count1 = 0;
    for(let index of InputData){
      tabel1.push({
        "key":count1,
        "fieldInStream": index.name,
        "fieldOutStream":"",
        "cutFrom":"",
        "cutTo":""
      });
      count1++;
    }
    this.refs.editTable.updateTable(tabel1,count1);
  };

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
        title="剪切字符串"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={650}
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
                <p style={{marginLeft:"5px"}}>要剪切的字段：</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{float:"right"}} >
                  <Button     onClick={this.handleAdd.bind(this)}>添加字段</Button>
                  <Button     onClick={this.handleFocus.bind(this)}>获取字段</Button>
                  <Button     onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                </ButtonGroup>
              </Col>
            </Row>
            <EditTable  columns={this.columns}   tableStyle="editTableStyle5" ref="editTable" scroll={{y: 140}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.dataSource}/>
          </div>
        </Form>
      </Modal>
    );
  }
}
const StringCut = Form.create()(CutDialog);

export default connect()(StringCut);
