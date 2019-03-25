import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable';
import { selectType } from '../../../../constant';

class ReplaceDialog extends React.Component {

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
  };

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
          fieldInStream:options,
          replaceFieldByString:options
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
          let args = ["fieldInStream", "fieldOutStream", "useRegEx", "replaceString", "replaceByString", "setEmptyString", "replaceFieldByString", "wholeWord", "caseSensitive"];
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
      "useRegEx":"",
      "replaceString": null,
      "replaceByString": null,
      "setEmptyString": "",
      "replaceFieldByString": null,
      "wholeWord": "",
      "caseSensitive":""
    };
    this.refs.editTable.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };

  /*文件表格*/
  columns =  [
    {
    title: '输入流字段',
    dataIndex: 'fieldInStream',
    width:"13%",
    key: 'fieldInStream',
    selectable:true
  }, {
    title: '输出流字段',
    dataIndex: 'fieldOutStream',
    width:"12%",
    key: 'fieldOutStream',
    editable:true
  }, {
    title: '使用正则表达式',
    dataIndex: 'useRegEx',
    width:"12%",
    key: 'useRegEx',
    selectable:true,
    selectArgs:selectType.get("1/0")
  } ,{
    title: '搜索',
    dataIndex: 'replaceString',
    width:"8%",
    key: 'replaceString',
    editable:true
  },{
    title: '使用...替换',
    dataIndex: 'replaceByString',
    width:"8%",
    key: 'replaceByString',
    editable:true
  },{
    title: '设置为空串？',
    dataIndex: 'setEmptyString',
    width:"12%",
    key: 'setEmptyString',
    selectable:true,
    selectArgs:selectType.get("T/F")
  } ,{
    title: '使用字段替换',
    dataIndex: 'replaceFieldByString',
    width:"12%",
    key: 'replaceFieldByString',
    selectable:true
  } ,{
    title: '整个单词匹配',
    dataIndex: 'wholeWord',
    width:"12%",
    key: 'wholeWord',
    selectable:true,
    selectArgs:selectType.get("1/0")
  } ,{
    title: '大小写敏感',
    dataIndex: 'caseSensitive',
    key: 'caseSensitive',
    selectable:true,
    selectArgs:selectType.get("1/0")
  }];

  getFields(){
    const { InputData } = this.state;
    let args = [];
    let count = 0;
    for(let index of InputData){
      args.push({
        "key":count,
        "fieldInStream": index.name,
        "fieldOutStream": "",
        "useRegEx": "0",
        "replaceString": null,
        "replaceByString": null,
        "setEmptyString": false,
        "replaceFieldByString": null,
        "wholeWord": "0",
        "caseSensitive": "0"
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
        title="字符串替换"
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
            <EditTable  columns={this.columns}   tableStyle="editTableStyle5" ref="editTable" scroll={{y: 300,x:1100}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.dataSource}/>
          </div>
        </Form>
      </Modal>
    );
  }
}
const ReplaceString = Form.create()(ReplaceDialog);

export default connect()(ReplaceString);
