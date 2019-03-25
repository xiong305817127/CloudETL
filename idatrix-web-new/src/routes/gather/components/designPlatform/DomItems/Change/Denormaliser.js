import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable';
import { selectType } from '../../../../constant';

class DenorDialog extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true) {
      const {groupField,denormaliserTargetField} = props.model.config;
      let data = [];
      let data1 = [];
      if (groupField) {
        let count = 0;
        for (let index of groupField) {
          data.push({
            "key": count,
            "groupField":index
          });
          count++;
        }
      }
      if (denormaliserTargetField) {
        let count = 0;
        for (let index of denormaliserTargetField) {
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
          groupField:options
        });
      };
      if(this.refs.editTable1){
        let options = getInputSelect(data,"name");
        this.refs.editTable1.updateOptions({
          fieldname:options
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
    const { groupField,denormaliserTargetField } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }

      let sendFields = [];
      if(this.refs.editTable){
        if(this.refs.editTable.state.dataSource.length>0){
          let args = [  "groupField"];
           let args1 = formatTable(this.refs.editTable.state.dataSource,args);
          for(let index of args1){
            sendFields.push(index["groupField"]);
          }
        }
      }else{
        if(groupField){
          sendFields = groupField
        }
      }

      let sendFields1 = [];
      if(this.refs.editTable1){
        if(this.refs.editTable1.state.dataSource.length>0){
          let args = [  "fieldname", "keyvalue", "name", "typedesc", "format", "length", "precision", "decimalsymbol", "groupingsymbol", "currencysymbol","aggregationtypedesc","nullstring"];
          sendFields1 = formatTable(this.refs.editTable1.state.dataSource,args);
        }
      }else{
        if(denormaliserTargetField){
          sendFields1 = denormaliserTargetField
        }
      }
      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        groupField:sendFields,
        denormaliserTargetField:sendFields1,
        ...values
      };

      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });

    });
  };

  handleFocus(){
    const { InputData } = this.state;
    let tabel1 = [];
    let count1 = 0;
    for(let index of InputData){
        tabel1.push({
          key:count1,
          groupField:index.name
        });
        count1++;
    }
    this.refs.editTable.updateTable(tabel1,count1);
  }

  /*表格2获取字段*/
  handleFocus1(){
      const { getFieldValue } = this.props.form;
      const { InputData } = this.state;
      let str1 = getFieldValue("keyField");
      let str2 = this.refs.editTable.state.dataSource;
      let args1 = [];
      let args = [];
      let count = 0;
      if(str2.length>0){
          for(let index of str2){
              if(index["groupField"].trim()){
                args1.push(index["groupField"]);
              }
          }
      }
      if(str1 && str1.trim()){
          args1.push(str1);
      }

    for(let index of InputData){
      if(!args1.includes(index.name)){
        args.push({
          "key":count,
          "name": "字段"+count,
          "fieldname": index.name,
          "keyvalue": "",
          "typedesc": index.type,
          "format": "",
          "length": index.length == -1?"":index.length,
          "precision": index.length == -1?"":index.length,
          "decimalsymbol": index.decimalsymbol,
          "groupingsymbol": index.groupingsymbol,
          "currencysymbol": index.currencysymbol,
          "aggregationtypedesc": "-",
          nullstring:index.nullif,
        });
        count++;
      }
    }
    this.refs.editTable1.updateTable(args,count);
  }

  /*增加字段*/
  handleAdd = ()=>{
    const data = {
      "groupField": ""
    };
    this.refs.editTable.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };

  /*增加字段1*/
  handleAdd1 = ()=>{
    const data = {
      "fieldname": null,
      "keyvalue": null,
      "name": null,
      "typedesc": null,
      "format": null,
      "length": "",
      "precision": "",
      "decimalsymbol": null,
      "groupingsymbol": null,
      "currencysymbol": null,
      nullstring:"",
      "aggregationtypedesc": null
    }
    this.refs.editTable1.handleAdd(data);
  }

  /*删除字段1*/
  handleDeleteFields1 = ()=>{
    this.refs.editTable1.handleDelete();
  }

  /*表格1*/
  columns =  [{
    title: '分组字段',
    dataIndex: 'groupField',
    width:"92%",
    key: 'groupField',
    selectable:true
  }];

  /*表格2*/
  filedsColumns = [{
    title: '目标字段',
    dataIndex: 'name',
    key: 'name',
    width:"8%",
    editable:true
  },{
    title: '数据字段',
    dataIndex: 'fieldname',
    key: 'fieldname',
    width:"8%",
    selectable:true
  },{
    title: '关键字值',
    dataIndex: 'keyvalue',
    key: 'keyvalue',
    width:"8%",
    editable:true
  }, {
    title: '类型',
    dataIndex: 'typedesc',
    key: 'typedesc',
    width:"11%",
    selectable:true,
    selectArgs:selectType.get("type")
  }, {
    title: '格式化',
    dataIndex: 'format',
    key: 'format',
    editable:true
  },{
    title: '长度',
    dataIndex: 'length',
    key: 'length',
    width:"7%",
    editable:true
  }, {
    title: '精度',
    dataIndex: 'precision',
    key: 'precision',
    width:"7%",
    editable:true
  }, {
    title: '货币类型',
    dataIndex: 'currencysymbol',
    key: 'currencysymbol',
    width:"7%",
    editable:true
  },{
    title: '精度',
    dataIndex: 'decimalsymbol',
    key: 'decimalsymbol',
    width:"7%",
    editable:true
  },{
    title: '分组',
    dataIndex: 'groupingsymbol',
    key: 'groupingsymbol',
    width:"7%",
    editable:true
  },{
    title: 'Null if',
    dataIndex: 'nullstring',
    key: 'nullstring',
    width:"6%",
    editable:true,
  }, {
      title: '聚合',
      dataIndex: 'aggregationtypedesc',
      key: 'aggregationtypedesc',
      width:"16%",
      selectable:true,
      selectArgs:selectType.get("aggregation")
    }
  ];





  render() {
    const { getFieldDecorator } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;

    const formItemLayout1 = {
      labelCol: { span: 7 },
      wrapperCol: { span: 14 },
    };

    return (
      <Modal
        visible={visible}
        title="列转行"
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
          <FormItem
            {...formItemLayout1}
            label="关键字段"
            hasFeedback
            style={{marginBottom:"8px"}}
          >
            {getFieldDecorator('keyField', {
              initialValue: config.keyField
            })(
              <Select >
                {
                  this.state.InputData.map((index)=>
                    <Select.Option  key={index.name} value={index.name}>{index.name}</Select.Option>
                  )
                }
              </Select>
            )}
          </FormItem>
          <div style={{margin:"10px 5% 0"}}>
            <Row style={{marginBottom:"5px"}}>
              <Col span={12}>
                <p style={{marginLeft:"5px"}}>构成分组的字段：</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{float:"right"}} >
                  <Button     onClick={this.handleAdd.bind(this)}>添加字段</Button>
                  <Button     onClick={()=>{this.handleFocus()}}>获取字段</Button>
                  <Button     onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                </ButtonGroup>
              </Col>
            </Row>
            <EditTable  columns={this.columns}   tableStyle="editTableStyle5" ref="editTable" scroll={{y: 140}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.dataSource}/>
          </div>

          <div style={{margin:"10px 5% 0"}}>
            <Row style={{marginBottom:"5px"}}>
              <Col span={12}>
                <p style={{marginLeft:"5px"}}>目标字段：</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{float:"right"}} >
                  <Button     onClick={this.handleAdd1.bind(this)}>添加字段</Button>
                  <Button     onClick={this.handleFocus1.bind(this)}>获取字段</Button>
                  <Button     onClick={this.handleDeleteFields1.bind(this)} >删除字段</Button>
                </ButtonGroup>
              </Col>
            </Row>
            <EditTable  columns={this.filedsColumns}   tableStyle="editTableStyle5" ref="editTable1" scroll={{y: 300,x:1500}} rowSelection={true}  size={"small"} count={1}  dataSource = {this.state.dataSource1}/>
          </div>
        </Form>
      </Modal>
    );
  }
}
const Denormaliser = Form.create()(DenorDialog);

export default connect()(Denormaliser);
