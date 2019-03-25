import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col,Card,Icon } from 'antd';
import Modal from "components/Modal.js";
const Option = Select.Option;
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
const confirm = Modal.confirm;
import EditTable from '../../../common/EditTable'

class MergeRows extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    const { keyFields,valueFields} = props.model.config;
    this.state={
       visibleS:false  
    };
      let data = [];
      let data1 = [];

    if(keyFields){
        let count = 0;
        for(let index of keyFields){
          data1.push({
            key:count,
            keyFields:index,
            ...index
          });
          count++;
        }
      }
     
     
      if (valueFields) {
        let count = 0;
        for (let index of valueFields) {
          data.push({
            "key": count,
            valueFields:index,
            ...index
          });
          count++;
        }
      }
      this.state = {
        dataSource:data,
        dataSourceS:data1,
        InputData:[],
        InputDataS:[],
        oldStepList:[],
      }
    
  }

      /*文件表格*/
  columns =  [{
    title: '关键字段',
    dataIndex: 'keyFields',
    key: 'keyFields',
    selectable:true,
  }];
  columns1 =  [{
    title: '数据字段',
    dataIndex: 'valueFields',
    key: 'valueFields',
    selectable:true,
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
     const { keyFields ,valueFields } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
       let sendFields = [];
        let sendFields1 = [];
        if(this.refs.editTable){
          if(this.refs.editTable.state.dataSource.length>0){
            let arg = ["keyFields"];
            sendFields = formatTable(this.refs.editTable.state.dataSource,arg)
          }
        }else{
          if(keyFields){
            sendFields = keyFields;
          }
      }

      if(this.refs.editTable1){
          if(this.refs.editTable1.state.dataSource.length>0){
            let arg = ["valueFields"];
            sendFields1 = formatTable(this.refs.editTable1.state.dataSource,arg)
          }
        }else{
          if(valueFields){
            sendFields1 = valueFields;
          }
      }
       let D1 = [];
       let D2 = [];
       for(let key of sendFields){
          D1.push(key.keyFields);
       }
        for(let key of sendFields1){
              D2.push(key.valueFields);
           }
       
      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        ...values,
        "keyFields": D1,
        "valueFields": D2
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
    const { getOutFields,transname,text,getInputSelect } = this.props.model;
    console.log(this.props.model,"this.props.model");
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getOutFields(obj, data => {
      let options = getInputSelect(data,"name");
      console.log(data,"data");
      this.refs.editTable.updateOptions({
          keyFields:options,
        });
       this.refs.editTable1.updateOptions({
          valueFields:options,
        });

      this.setState({
         InputData:data,
         InputDataS:data,
         newStepList:data,
      })
    })
  };
  /*增加字段*/
  handleAdd = ()=>{
    const data = {
      "keyFields": null
    }
    this.refs.editTable.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };
  /*增加字段*/
  handleAdd1 = ()=>{
    const data = {
      "valueFields": null
    }
    this.refs.editTable1.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields1 = ()=>{
    this.refs.editTable1.handleDelete();
  };

   handleFocus(){
    const { InputData } = this.state;
     const { getOutFields,transname,text,getInputSelect } = this.props.model;
     const form = this.props.form;
     form.validateFields((err, values) => {
     if(values.oldStep === null){
          return;
       }else{
        let obj = {};
        obj.transname = transname;
        obj.stepname = values.newStep;
        getOutFields(obj, data => {
           let args = [];
          let count = 0;
           for(let index of data){
            args.push({
              "key":count,
              "keyFields": index.name,
            });
            this.setState({
               dataSource:args
            })
            count++;
          }
          this.refs.editTable.updateTable(args,count);

       
        })
     }
    })
  }

  handleFocus1(){
    const { InputDataS } = this.state;
     const { getOutFields,transname,text,getInputSelect } = this.props.model;
     const form = this.props.form;
     form.validateFields((err, values) => {
     if(values.newStep === null){
          return;
       }else{
        let obj = {};
        obj.transname = transname;
        obj.stepname = values.newStep;
        getOutFields(obj, data => {
           let args = [];
          let count = 0;
           for(let index of data){
            args.push({
              "key":count,
              "valueFields": index.name,
            });
            this.setState({
               dataSourceS:args
            })
            count++;
          }
          this.refs.editTable1.updateTable(args,count);

        })
     }
    })

  }


  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName,prevStepNames } = this.props.model;
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
        title="合并记录"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={850}
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

          <FormItem label="旧数据源"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('oldStep', {
              initialValue:config.oldStep,
            })(
                <Select
                      allowClear
                      placeholder="从字段获取源">
                      {
                        prevStepNames?prevStepNames.map((index)=>(<Select.Option key={index}>{index}</Select.Option>)):''
                      }
                </Select>
            )}
          </FormItem>

          <FormItem label="新数据源"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('newStep', {
              initialValue:config.newStep,
            })(
              <Select
                      allowClear
                      placeholder="从字段获取源">
                      {
                        prevStepNames?prevStepNames.map((index)=>(<Select.Option key={index}>{index}</Select.Option>)):''
                      }
                </Select>
            )}
          </FormItem>

          <FormItem label="标志字段"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('flagField', {
              initialValue:config.flagField,
            })(
              <Input />
            )}
          </FormItem>
  
          <div style={{margin:"0 5%"}}>
              <Row style={{marginBottom:"5px"}}>
             <Col span={11} style={{marginLeft:"0"}}>
                <p style={{marginLeft:"5px"}}>匹配的关键字：</p>
                 <ButtonGroup size={"small"}>
                   <Button onClick={this.handleAdd.bind(this)}>添加字段</Button>
                   <Button onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                   <Button style={{width:"50%"}} onClick={this.handleFocus.bind(this)}>获取值字段</Button>   
                </ButtonGroup>
                    <div>
                        <EditTable columns={this.columns} tableStyle="editTableStyle5" ref="editTable" scroll={{y: 300}} rowSelection={true} 
                          size={"small"} count={4}  dataSource = {this.state.dataSourceS}/>
                    </div>
              </Col>
              <Col span={11} style={{marginLeft:"45"}}>
                <p style={{marginLeft:"5px"}}>数据字段：</p>
                 <ButtonGroup size={"small"}>
                   <Button onClick={this.handleAdd1.bind(this)}>添加字段</Button>
                   <Button onClick={this.handleDeleteFields1.bind(this)} >删除字段</Button>
                   <Button style={{width:"50%"}} onClick={this.handleFocus1.bind(this)}>获取值字段</Button>   
                </ButtonGroup>
                   <div>
                        <EditTable columns={this.columns1} tableStyle="editTableStyle5" ref="editTable1" scroll={{y: 300}} rowSelection={true} 
                    size={"small"} count={4}  dataSource = {this.state.dataSource}/>
                  
                   </div>
              </Col>
            </Row>

          
          </div>
        </Form>
      </Modal>
    );
  }
}
const Merge = Form.create()(MergeRows);

export default connect()(Merge);
