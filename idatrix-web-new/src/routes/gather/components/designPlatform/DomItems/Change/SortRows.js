import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable'

class SortRowsDialog extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true) {
      const {fieldName} = props.model.config;
      let data = [];
      if (fieldName) {
        let count = 0;
        for (let index of fieldName) {
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
          fieldName:options
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
    const { fieldName } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let obj = {};
      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = ["fieldName", "ascending", "caseSensitive", "collatorEnabled", "collatorStrength", "preSortedField"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,args);
        }
      }else{
        if(fieldName){
          sendFields = fieldName
        }
      }
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        fieldName:sendFields,
        ...values
      };

      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });

    });
  }

  handleFocus(){
    const { InputData } = this.props.model;
    let tabel1 = [];
    let count1 = 0;
    for(let index of InputData){
      tabel1.push({
        "key":count1,
        "fieldName": index.name,
        "ascending": "true",
        "caseSensitive": "",
        "collatorEnabled": "",
        "collatorStrength": "",
        "preSortedField": "",
        "selectOptions":{
          fieldName:options
        }
      });
      count1++;
    }
    this.refs.editTable.updateTable(tabel1,count1);
  }


  /*增加字段*/
  handleAdd = ()=>{
    const data = {
      "fieldName": null,
      "ascending": "",
      "caseSensitive": "",
      "collatorEnabled": "",
      "collatorStrength": "",
      "preSortedField": ""
    };
    this.refs.editTable.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  }

  /*文件表格*/
  columns =  [{
    title: '字段名称',
    dataIndex: 'fieldName',
    key: 'fieldName',
    selectable:true,
    width:"20%"
  }, {
    title: '升序',
    dataIndex: 'ascending',
    key: 'ascending',
    width:"10%",
    selectable:true,
    selectArgs:[
      <Select.Option key="0" value="false">否</Select.Option>,
      <Select.Option key="1" value="true">是</Select.Option>,
    ]
  }, {
    title: '大小写敏感',
    dataIndex: 'caseSensitive',
    key: 'caseSensitive',
    width:"12%",
    selectable:true,
    selectArgs:[
      <Select.Option key="0" value="false">否</Select.Option>,
      <Select.Option key="1" value="true">是</Select.Option>,
    ]
  } ,{
    title: '基于当前区域的排序?',
    dataIndex: 'collatorEnabled',
    key: 'collatorEnabled',
    width:"29%",
    selectable:true,
    selectArgs:[
      <Select.Option key="0" value="false">否</Select.Option>,
      <Select.Option key="1" value="true">是</Select.Option>,
    ]
  } ,{
    title: '校对强度',
    dataIndex: 'collatorStrength',
    key: 'collatorStrength',
    width:"12%",
    selectable:true,
    selectArgs:[
      <Select.Option key="0" value="0">0</Select.Option>,
      <Select.Option key="1" value="1">1</Select.Option>,
      <Select.Option key="2" value="2">2</Select.Option>,
      <Select.Option key="3" value="3">3</Select.Option>,
    ]
  },{
    title: '优先?',
    dataIndex: 'preSortedField',
    key: 'preSortedField',
    selectable:true,
    selectArgs:[
      <Select.Option key="0" value="false">否</Select.Option>,
      <Select.Option key="1" value="true">是</Select.Option>,
    ]
    } ];


  render() {
    const { getFieldDecorator } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;

    const formItemLayout1 = {
      labelCol: { span:6 },
      wrapperCol: { span: 14 },
    };
    const formItemLayout2 = {
      labelCol: { span:20},
      wrapperCol: { span: 4 },
    };

    return (

      <Modal
        visible={visible}
        title="排序记录"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={900}
        footer={[
                  <Button key="submit" type="primary" size="large" onClick={this.handleCreate} >
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={this.hideModal}>取消</Button>,
                ]}
        onCancel = {this.hideModal}
      >
        <Form >
          <FormItem label="步骤名称"    {...formItemLayout1}>
            {getFieldDecorator('text', {
              initialValue:text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
            })(
              <Input />
            )}
          </FormItem>
          <FormItem
            {...formItemLayout1}
            label="排序目录"
            hasFeedback
            style={{marginBottom:"8px"}}
          >
            {getFieldDecorator('directory', {
              initialValue: config.directory
            })(
              <Input />
            )}
          </FormItem>
          <FormItem label="临时文件前缀"  {...formItemLayout1} style={{margin:"5px 0"}}>
            {getFieldDecorator('prefix', {
              initialValue: config.prefix
            })(
              <Input />
            )}
          </FormItem>

          <FormItem label="排序缓存大小"  {...formItemLayout1} style={{margin:"5px 0"}}>
          {getFieldDecorator('sortSize', {
            initialValue: config.sortSize
          })(
            <Input  placeholder="内存里存放的记录数"/>
          )}
        </FormItem>
          <FormItem label="未使用内存限值"  {...formItemLayout1} style={{margin:"5px 0"}}>
            {getFieldDecorator('freeMemoryLimit', {
              initialValue: config.freeMemoryLimit
            })(
              <Input  />
            )}
          </FormItem>
          <Row style={{marginTop:"20px"}}>
             <Col span={7}>
               <FormItem label="压缩临时文件" style={{marginBottom:"8px",marginLeft:"30%"}}  {...formItemLayout2}>
                 {getFieldDecorator('compressFiles', {
                   valuePropName: 'checked',
                   initialValue:config.compressFiles
                 })(
                   <Checkbox />
                 )}
               </FormItem>
             </Col>
             <Col  span={10}>
               <FormItem  style={{marginBottom:"8px"}} >
                 {getFieldDecorator('compressFilesVariable', {
                   valuePropName: 'checked',
                   initialValue:config.compressFilesVariable
                 })(
                   <Input/>
                 )}
               </FormItem>
             </Col>
          </Row>
          <FormItem  style={{marginBottom:"8px",marginLeft:"25%"}}  {...formItemLayout1}>
            {getFieldDecorator('usevar', {
              valuePropName: 'checked',
              initialValue:config.usevar
            })(
              <Checkbox >仅仅传递非重复的记录?(仅仅校验关键字)</Checkbox>
            )}
          </FormItem>

          <div style={{margin:"10px 5% 0"}}>
            <Row style={{marginBottom:"5px"}}>
              <Col span={12}>
                <p style={{marginLeft:"5px"}}>字段</p>
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
const SortRows = Form.create()(SortRowsDialog);

export default connect()(SortRows);
