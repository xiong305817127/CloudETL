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

class SortedMerge extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    const { fields } = props.model.config;
    
      let data = [];
     
        let count = 0;
        for(var key in fields){  
         data.push({"fieldNames":key,"ascendings":fields[key], "key": count});   
          count++
          }  
        
        console.log(data,"datadata");
      this.state = {
        dataSource:data,
        visibleS:false,
        InputData:[],
        InputDataS:[],
        oldStepList:[],
      }
    
  }

      /*文件表格*/
  columns =  [{
    title: '字段名称',
    dataIndex: 'fieldNames',
    key: 'fieldNames',
    width:'50%',
    editable:true
  },{
    title: '升序',
    dataIndex: 'ascendings',
    key: 'ascendings',
    width:'50%',
    selectable:true,
    selectArgs:selectType.get("T/F")
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
     const { keyFieldsS  } = config;
      Modal.warning({
	    title: '警告',
	    content: '“请指定一个用来排序（升序）的键.如果输入是未排序的，输出有可能不正确！',
	  });
    form.validateFields((err, values) => {
      if (err) {
        return;
      }

       let sendFields = [];
        if(this.refs.editTable){
          if(this.refs.editTable.state.dataSource.length>0){
            let arg = ["fieldNames","ascendings"];
            sendFields = formatTable(this.refs.editTable.state.dataSource,arg)
          }
        }else{
          if(keyFieldsS){
            sendFields = keyFieldsS;
          }
      }
		      let list = [];
		      var arge = [];
          let newObj = {};

		       for(let key of this.refs.editTable.state.dataSource){
		          console.log(key);
              newObj[key.fieldNames] = key.ascendings
		       }

      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        ...values,
        "fields": newObj,
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
    const { getInputFields,transname,text,getInputSelect } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;

    getInputFields(obj, data => {
      let options = getInputSelect(data,"name");
      console.log(options,"options");
      this.refs.editTable.updateOptions({
          fieldNames:options,
        });
      this.setState({
         InputData:data,
         InputDataS:data,
         newStepList:data,
      })
     console.log(obj,"obj",data,"data");
    })
  };
  /*增加字段*/
  handleAdd = ()=>{
    const data = {
      "fieldNames": null,
      "ascendings":"是"?true:false,
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
       console.log(values,"values");
      if (err) {
        return;
      }
     if(values.step1 === null){
          return;
       }else{
        let args = [];
        let count = 0;

         let obj = {};
          obj.transname = transname;
          obj.stepname = text;

         getInputFields(obj, data => {
            for(let index of data){
            args.push({
              "key":count,
              "fieldNames": index.name,
              "ascendings": "是"?true:false,
            });
            console.log(args,"args");
            this.setState({
               dataSource:args
            })
            count++;
          }
         })
        
        this.refs.editTable.updateTable(args,count);
       }
    })
  }


  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;


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
        title="排序合并"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={650}
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
             <Col span={24} style={{marginLeft:"0"}}>
                <p style={{marginLeft:"5px"}}>字段</p>
                 <ButtonGroup size={"small"}>
                   <Button onClick={this.handleAdd.bind(this)}>添加字段</Button>
                   <Button onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                   <Button style={{width:"50%"}} onClick={this.handleFocus.bind(this)}>获取字段</Button>   
                </ButtonGroup>
                    <div>
                        <EditTable columns={this.columns} tableStyle="editTableStyle5" ref="editTable" scroll={{y: 300}} rowSelection={true} 
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
const SortedMergeList = Form.create()(SortedMerge);

export default connect()(SortedMergeList);
