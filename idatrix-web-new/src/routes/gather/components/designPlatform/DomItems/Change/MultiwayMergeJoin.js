import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col,Card,Icon,Popover } from 'antd';
import Modal from "components/Modal.js";
const Option = Select.Option;
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
const confirm = Modal.confirm;
import EditTable from '../../../common/EditTable'
class MultiwayMergeJoin extends React.Component {

 constructor(props){
    super(props);
    const { visible } = props.model;
 
     if(visible === true) {
      const {keys} = props.model.config;
     /* let b = keys.join("-");
      var arr = b.split(",");*/
      let data = [];
   
        let count = 0;
        for (let index in keys) {
          data.push({
            "key": count,
            name:keys[index],
            /*...index*/
          });
          count++;
        }
       console.log(data,"dataSource",keys);
      this.state = {
        dataSource:data,
       /* InputData:[],*/
        dataIndexKeys:[],
        KeysNameData:[],  //存储返回表单数据
        selectData:[],
        visibleS:false,
        Modelname:[],//选择表名称
        NmameBle:[],
        InputName:[], //获取连接秘钥的名称
      }
    }
  }

  /*componentDidMount(){
    this.Request();
  };

  Request(){
    const { getOutFields,transname,text,getInputSelect } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;

    getOutFields(obj, data => {
     
      this.setState({
         InputData:data,
         dataSource:data
      })
    })
  };*/

   /*文件表格*/
  columns =  [{
    title: '名称',
    dataIndex: 'keys',
    key: 'keys',
    editable:true
  }];

 hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  };

   hideModal1 = () => {
    const { dispatch } = this.props;
    this.setState({
      visibleS:false
    });
  };

  handleCreate = () => {
    const form = this.props.form;
    const { panel,transname,description,key,saveStep,text,config,formatTable } = this.props.model;
     const { keys } = config;

    form.validateFields((err, values) => {
      if (err) {
        return;
      }
        let sendFields = [];
        if(this.refs.editTable){
          if(this.refs.editTable.state.dataSource.length>0){
            let arg = ["keys"];
            sendFields = formatTable(this.refs.editTable.state.dataSource,arg)
          }
        }else{
          if(keys){
            sendFields = keys;
          }
      }
      let keysArg = [];
      let valuesArg = [];  
      for(let index of Object.keys(values)){
          if(index.indexOf("intervalInMs") !== -1){
               values[index] && (valuesArg.push(values[index]))
          }
          if(index.indexOf("rowLimit") !== -1){
             values[index] && (keysArg.push(values[index]))
          }
      }  
      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        "joinType":values.joinType,
        "inputSteps":keysArg,
        "keys":valuesArg
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
        "keys": null,
    }
    this.refs.editTable.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };


  onChangeKeyS(){
     const {setFieldsValue} = this.props.form;
    const { panel,transname,description,key,saveStep,text,config,formatTable } = this.props.model;
     const { keys } = config;
       
      let str = "";
      let list = [];
      var arge = [];
       for(let key of this.refs.editTable.state.dataSource){
           str += (key.keys+",");
           for (var i = 0; i < 0; i++) {  
           var bullet =key; 
              list[i] = bullet;
            }
       }
        str = str.substring(0,str.length - 1);
       setFieldsValue({
          [this.state.Modelname]:str
       })
       
       this.setState({
           dataIndexKeys:this.refs.editTable.state.dataSource,
           selectData:str,
       })
      this.hideModal1();
      
  }

     setModal1Visible(keys,name) { 
       const form = this.props.form;;
       const { getFieldDecorator,getFieldValue,setFieldsValue } = this.props.form;
       const {config}=this.props.model;

      let keys1 = getFieldValue(keys);
      let name1 = getFieldValue(name);

      var arr = keys1.split(",");
      let data = [];
   
        let count = 0;
        for (let index in arr) {
          data.push({
            "key": count,
            keys:arr[index],
            /*...index*/
          });
          count++;
        }
         console.log(data,"data  keys");
        this.setState({
             visibleS:true,
             Modelname:keys,
            /* KeysNameData:config.keys, */
             dataSource:data,
             InputName:name1
           });

     this.refs.editTable.updateTable(data,count);
 
     }

     formItemLayout2 = { 
        labelCol: { span: 7 },
        wrapperCol: { span: 14 },
     }

    handleFocus(){
      const { InputData,InputName } = this.state;
       const { getOutFields,transname,text,getInputSelect } = this.props.model;
       const form = this.props.form;
       form.validateFields((err, values) => {
          let obj = {};
          obj.transname = transname;
          obj.stepname = InputName;
          getOutFields(obj, data => {
          
             let args = [];
            let count = 0;
             for(let index of data){
              args.push({
                "key":count,
                "keys": index.name,
              });
              this.setState({
                 dataSourceS:args
              })
              count++;
            }
            this.refs.editTable.updateTable(args,count);
          })
      })
    }

  onChange = (data) =>{
  }

  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;
       /*config.keys = config.keys[0];
      config.keys=config.keys.split(',');*/
      const{keys,inputSteps } = config;
    const formItemLayout1 = { 
      labelCol: { span: 7 },
      wrapperCol: { span: 14 },
      }
      
    return (
      <Modal
        visible={visible}
        title="Multiway Merge Join"
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

        <Modal
        visible={this.state.visibleS}
        title="keys"
        maskClosable={false}
        wrapClassName="vertical-center-modal"
        footer={[
                  <Button key="submit" type="primary" size="large" onClick={this.onChangeKeyS.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={this.hideModal1.bind(this)}>取消</Button>,
                ]}
         onCancel = {this.hideModal1}>
            
            <Form>
                <Row style={{marginBottom:"5px"}}>
                  <Col span={12}>
                    <ButtonGroup size={"small"} style={{float:"right"}} >
                      <Button  size={"small"} onClick={this.handleFocus.bind(this)} >获取字段</Button>
                      <Button onClick={this.handleAdd.bind(this)}>添加字段</Button>
                      <Button onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                    </ButtonGroup>
                </Col>
              </Row>
             <EditTable columns={this.columns} tableStyle="editTableStyle5" ref="editTable" scroll={{y: 300}} rowSelection={true}  size={"small"} count={1} dataSource={this.state.dataSource}/>
         </Form>  
        </Modal>

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
          
          {this.props.model.prevStepNames?this.props.model.prevStepNames.map((index,key)=>(
            
             <Row key={index}>
              <Col span={10}>
                  <FormItem label="输入步骤"   style={{marginBottom:"8px"}}  {...this.formItemLayout2}>
                    {getFieldDecorator(`rowLimit${key}`, {
                      initialValue:inputSteps[key]?inputSteps[key]:"",
                    })(
                       <Select allowClear>
                              {
                               this.props.model.prevStepNames? this.props.model.prevStepNames.map((index,key)=>(<Select.Option key={index}>{index}</Select.Option>)):''
                              }
                        </Select>
                    )}
                  </FormItem>
              </Col>
              <Col span={10}>
                  <FormItem label="选择关键字"   style={{marginBottom:"8px"}}  {...this.formItemLayout2}>
                    {getFieldDecorator( `intervalInMs${key}`, {
                      initialValue:inputSteps[key]?keys[key]:"",
                    })(
                        <Input disabled/>
                    )}
                 </FormItem>
              </Col>
              <Col span={3}>
                    <Button type="primary" onClick={()=>{this.setModal1Visible(`intervalInMs${key}`,`rowLimit${key}`)}}>选择键</Button>
              </Col>
          </Row>)):null }

            <FormItem label="连接类型"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
              {getFieldDecorator('joinType', {
                initialValue:config.joinType,
              })(
                  <Select >
                      <Option key="INNER" value="INNER">INNER</Option>
                       <Option key="FULL OUTER" value="FULL OUTER">FULL OUTER</Option>
                 </Select>
              )}
           </FormItem>

           
           </Form>
      </Modal>
    );
  }
}
const MultiwayMergeJoinList = Form.create()(MultiwayMergeJoin);

export default connect()(MultiwayMergeJoinList);
