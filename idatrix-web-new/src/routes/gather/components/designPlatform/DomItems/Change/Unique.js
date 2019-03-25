import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col,Card } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable'

class UniqueDialog extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true) {
      const {compareFields} = props.model.config;
      let data = [];
      if (compareFields) {
        let count = 0;
        for (let index of compareFields) {
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
          compareField:options
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
    const { compareFields } = config;
     Modal.warning({
        title: '警告',
        content: '去掉重复行的功能需要已经根据指定字段排好序，如果你没有把输入排序，只会考虑相同的行！',
      });
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let obj = {};
      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = [  "compareField", "caseInsensitive"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,args);
        }
      }else{
        if(compareFields){
          sendFields = compareFields
        }
      }
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        compareFields:sendFields,
        ...values
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
      "compareField": "",
      "caseInsensitive":""
    };
    this.refs.editTable.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };


  /*文件表格*/
  columns =  [{
    title: '字段名称',
    dataIndex: 'compareField',
    width:"75%",
    key: 'compareField',
    selectable:true
  }, {
    title: '忽略大小写',
    dataIndex: 'caseInsensitive',
    width:"20%",
    key: 'caseInsensitive',
    selectable:true,
    selectArgs:[
      <Select.Option key="Y" value="true">是</Select.Option>,
      <Select.Option key="N" value="false">否</Select.Option>,
    ]
  }];

  handleFocus(){
    const { InputData } = this.state;
    let tabel1 = [];
    let count1 = 0;
    for(let index of InputData){
      tabel1.push({
        "key":count1,
        "compareField": index.name,
        "caseInsensitive":"false"
      });
      count1++;
    }
      this.refs.editTable.updateTable(tabel1,count1);
  }


  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;

    const formItemLayout1 = {
      labelCol: { span: 7 },
      wrapperCol: { span: 14 },
    };

    const setDisabled1 = ()=>{
      if(getFieldValue("countRows") === undefined){
        return !config.countRows;
      }else{
        if(getFieldValue("countRows")){
          return !getFieldValue("countRows");
        }else {
          return true;
        }
      }
    }

    const setDisabled = ()=>{
      if(getFieldValue("rejectDuplicateRow") === undefined){
        return !config.rejectDuplicateRow;
      }else{
        if(getFieldValue("rejectDuplicateRow")){
          return !getFieldValue("rejectDuplicateRow");
        }else {
          return true;
        }
      }
    }

    return (

      <Modal
        visible={visible}
        title="去除重复记录"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={650}
        footer={[
                  <Button key="submit" type="primary" size="large" onClick={this.handleCreate.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={this.hideModal.bind(this)}>取消</Button>,
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
          <div style={{margin:"0 5%"}}>
            <Card title="设置">
                <Row>
                   <Col span={8}>
                     <FormItem  style={{marginBottom:"8px"}}  {...formItemLayout1}>
                       {getFieldDecorator('countRows', {
                         valuePropName: 'checked',
                         initialValue:config.countRows
                       })(
                         <Checkbox >增加计数器到输出？</Checkbox>
                       )}
                     </FormItem>
                   </Col>
                   <Col span={16}>
                     <FormItem label="计数器字段"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
                       {getFieldDecorator('countField', {
                         initialValue:config.countField
                       })(
                         <Input disabled={setDisabled1()}/>
                       )}
                     </FormItem>
                   </Col>
                </Row>
                <Row>
                  <Col span={8}>
                    <FormItem  style={{marginBottom:"8px"}}  {...formItemLayout1}>
                      {getFieldDecorator('rejectDuplicateRow', {
                         valuePropName: 'checked',
                        initialValue:config.rejectDuplicateRow
                      })(
                        <Checkbox >重定向重复记录</Checkbox>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={16}>
                    <FormItem label="错误描述"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
                      {getFieldDecorator('errorDescription', {
                        initialValue:config.errorDescription
                      })(
                        <Input disabled={setDisabled()}/>
                      )}
                    </FormItem>
                  </Col>
                </Row>
            </Card>
          </div>

          <div style={{margin:"10px 5% 0"}}>
            <Row style={{marginBottom:"5px"}}>
              <Col span={12}>
                <p style={{marginLeft:"5px"}}>用来比较的字段(没有条目意味着: 比较完成了)：</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{float:"right"}} >
                  <Button onClick={this.handleAdd.bind(this)}>添加字段</Button>
                  <Button onClick={this.handleFocus.bind(this)}>获取字段</Button>
                  <Button onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                </ButtonGroup>
              </Col>
            </Row>
            <EditTable columns={this.columns} tableStyle="editTableStyle5" ref="editTable" scroll={{y: 140}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.dataSource}/>
          </div>
        </Form>
      </Modal>
    );
  }
}
const Unique = Form.create()(UniqueDialog);

export default connect()(Unique);
