import React from "react";
import { connect } from 'dva';
import { Button, Form, Input, Radio,Select,Tabs,Checkbox,Row,Col } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable';
import { selectType } from '../../../../constant';

class SelectDialog extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true){
      const { selectFields,deleteName,selectMetadataChange } = props.model.config;
      let data = [];

      if(selectFields){
        let count = 0;
        for(let index of selectFields){
          data.push({
            key:count,
            ...index
          });
          count++;
        }
      }

      let data1 = [];
      if(deleteName){
        let count = 0;
        for(let index of deleteName){
          data1.push({
            key:count,
            deleteName:index
          });
          count++;
        }
      }

      let data2 = [];
      if(selectMetadataChange){
        let count = 0;
        for(let index of selectMetadataChange){
          data2.push({
            key:count,
            ...index
          });
          count++;
        }
      }

      this.state = {
        dataSource:data,
        dataSource1:data1,
        dataSource2:data2,
        InputData:[],
        deleteData:[],
        selectData:[]
      }
    }
  };

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
        InputData:data,
        deleteData:data,
        selectData:data
      });
      if(this.refs.editTable){
        let options = getInputSelect(data,"name");
        this.refs.editTable.updateOptions({
          name:options
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
    console.log(this.props.model);
    const {panel,description,transname,key,saveStep,text,config,formatTable} = this.props.model;
    const { selectFields,deleteName,selectMetadataChange } = config;
    const form = this.props.form;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      if(this.refs.editTable){

        if(this.refs.editTable.state.dataSource.length>0){
          let arg = [ "name", "rename", "precision", "length"];
          sendFields = formatTable(this.refs.editTable.state.dataSource,arg)
        }
      }else{
        if(selectFields){
          sendFields = selectFields;
        }
      }

      let sendFields1 = [];
      if(this.refs.editTable1){

        if(this.refs.editTable1.state.dataSource.length>0){
          let arg = [ "deleteName"];
          let arg1 = formatTable(this.refs.editTable1.state.dataSource,arg);

          for(let index of arg1){
            sendFields1.push(index["deleteName"]);
          }
        }
      }else{
        if(deleteName){
          sendFields1 = deleteName;
        }
      }

      let sendFields2 = [];
      if(this.refs.editTable2){
        if(this.refs.editTable2.state.dataSource.length>0){
          let arg = [  "name", "rename", "type", "length", "precision", "conversionMask", "dateFormatLenient", "dateFormatLocale", "dateFormatTimeZone", "lenientStringToNumber", "encoding", "decimalSymbol", "groupingSymbol", "currencySymbol", "storageType"];
          sendFields2 = formatTable(this.refs.editTable2.state.dataSource,arg);
        }
      }else{
        if(selectMetadataChange){
          sendFields2 = selectMetadataChange;
        }
      }

      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        "selectFields": sendFields,
        "deleteName": sendFields1,
        "selectMetadataChange":sendFields2,
        ...values
      };

      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });

    });
  };

  handleChange(e){
      console.log(e);
      if(e === "2" && this.refs.editTable1){
        this.updateDelete(this.refs.editTable1)
      }else if(e === "3" && this.refs.editTable2){
        this.updateSelect(this.refs.editTable2);
      }
  }

  updateDelete(that){
      const { deleteData } = this.state;
      const { getInputSelect } = this.props.model;
      let tableData = this.refs.editTable.state.dataSource;
      if(tableData.length>0){
        for(let index of tableData){
          if(index.rename && index.rename.trim()){
            deleteData.map(elm =>{
              if(elm.name === index.name){
                elm.name = index.rename;
              }
              return elm;
            })
          }
        }
      }
      let options = getInputSelect(deleteData,"name");
      that.updateOptions({
        deleteName:options
      });
      this.setState({
        deleteData:deleteData
      });
  };

  updateSelect(that){
    const { deleteData } = this.state;
    const { getInputSelect } = this.props.model;
    let args = deleteData;
    let table1 = this.refs.editTable1;
    if(table1 && table1.state.dataSource.length>0){
      let argsDelete = [];
      for(let index of table1.state.dataSource){
          argsDelete.push(index.deleteName);
      }
      args = deleteData.filter(num =>{
          return !argsDelete.includes(num.name);
      });
    }
    let options = getInputSelect(args,"name");
    that.updateOptions({
      name:options
    });
    this.setState({
      selectData:args
    });
  }

  /*表格1*/
  Columns = [
    {
      title: '字段名称',
      dataIndex: 'name',
      key: 'name',
      width:"30%",
      selectable:true
    },{
      title: '改名为',
      dataIndex: 'rename',
      key: 'rename',
      width:"30%",
      editable:true
    } ,{
      title: '长度',
      dataIndex: 'length',
      key: 'length',
      width:"18%",
      editable:true
    }, {
      title: '精度',
      dataIndex: 'precision',
      key: 'precision',
      width:"17%",
      editable:true
    }
  ];
  handleAdd = ()=>{
    const data = {
      "name": "",
      "rename": "",
      "precision": "",
      "length": ""
    };
    this.refs.editTable.handleAdd(data);
  };
  handleDeleteFields = ()=>{
    this.refs.editTable.handleDelete();
  };
  /*表格2*/
  Columns1 = [
    {
      title: '字段名称',
      dataIndex: 'deleteName',
      key: 'deleteName',
      width:"95%",
      selectable:true
    }
  ];
  handleAdd1 (){
    const data = {
      "deleteName":null
    };
    this.refs.editTable1.handleAdd(data);
  };
  handleDeleteFields1 (){
    this.refs.editTable1.handleDelete();
  };
  initFuc1(that){
    this.updateDelete(that);
  };


  /*表格3*/
  Columns2 = [
    {
      title: '字段名称',
      dataIndex: 'name',
      key: 'name',
      width:"8%",
      selectable:true
    },{
      title: '改名为',
      dataIndex: 'rename',
      key: 'rename',
      width:"8%",
      editable:true
    } ,{
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width:"8%",
      selectable:true,
      selectArgs:selectType.get("numberType")
    } ,{
      title: '长度',
      dataIndex: 'length',
      key: 'length',
      width:"5%",
      editable:true
    }, {
      title: '精度',
      dataIndex: 'precision',
      key: 'precision',
      width:"5%",
      editable:true
    },{
      title: 'Binary to Normal?',
      dataIndex: 'storageType',
      key: 'storageType',
      selectable:true,
      width:"7%",
      selectArgs:[
        <Select.Option key="0" value="0">是</Select.Option>,
        <Select.Option key="-1" value="-1">否</Select.Option>
      ]
    }, {
      title: '格式',
      dataIndex: 'conversionMask',
      key: 'conversionMask',
      width:"5%",
      editable:true
    },{
      title: 'Date Format Lenient',
      dataIndex: 'dateFormatLenient',
      key: 'dateFormatLenient',
      width:"8%",
      selectable:true,
      selectArgs:selectType.get("T/F")
    },{
      title: 'Date Locale',
      dataIndex: 'dateFormatLocale',
      key: 'dateFormatLocale',
      width:"6%",
      editable:true
    },{
      title: 'Date Time Zone',
      dataIndex: 'dateFormatTimeZone',
      key: 'dateFormatTimeZone',
      width:"7%",
      editable:true
    },{
      title: 'Lenient number conversion',
      dataIndex: 'lenientStringToNumber',
      key: 'lenientStringToNumber',
      width:"10%",
      selectable:true,
      selectArgs:selectType.get("T/F")
    },{
      title: 'Encoding',
      dataIndex: 'encoding',
      key: 'encoding',
      width:"6%",
      selectable:true,
      selectArgs:[
        <Select.Option key="GBK" value="GBK">GBK</Select.Option>,
        <Select.Option key="ISO-8859-1" value="ISO-8859-1">ISO-8859-1</Select.Option>,
        <Select.Option key="GB2312" value="GB2312">GB2312</Select.Option>,
        <Select.Option key="UTF-8" value="UTF-8">UTF-8</Select.Option>,
        <Select.Option key="Big5" value="Big5">Big5</Select.Option>
      ]
    },{
      title: '十进制',
      dataIndex: 'decimalSymbol',
      key: 'decimalSymbol',
      width:"5%",
      editable:true
    },{
      title: '分组',
      dataIndex: 'groupingSymbol',
      key: 'groupingSymbol',
      width:"5%",
      editable:true
    },{
      title: '货币',
      dataIndex: 'currencySymbol',
      key: 'currencySymbol',
      editable:true
    }
  ];
  handleAdd2(){
    const data = {
      "name": "",
      "rename": "",
      "type":"",
      "length": "",
      "precision": "",
      "conversionMask": "",
      "dateFormatLenient": "",
      "dateFormatLocale": "",
      "dateFormatTimeZone": "",
      "lenientStringToNumber": "",
      "encoding": "",
      "decimalSymbol": "",
      "groupingSymbol": "",
      "currencySymbol": "",
      "storageType": ""
    };
    this.refs.editTable2.handleAdd(data);
  };
  handleDeleteFields2 (){
    this.refs.editTable2.handleDelete();
  };
  initFuc2(that){
    this.updateSelect(that);
  };

  handleFocus(){
    const { InputData } = this.state;
    let tabel1 = [];
    let count1 = 0;
    for(let index of InputData){
      tabel1.push({
        "key":count1,
        "name": index.name,
        "rename": "",
        "precision": index.precision,
        "length":index.length
      });
      count1++;
    }
    this.refs.editTable.updateTable(tabel1,count1);
  }

  getRemoveFields(){
    const { deleteData } = this.state;
    let count = 0;
    let table1 = [];
     for(let index of deleteData){
         table1.push({
             "key":count,
             "deleteName": index.name
           });
       count++;
     }
    this.refs.editTable1.updateTable(table1,count);
  }

  getFields(){
      const { selectData } = this.state;
      let args = [];
      let count =1;
      for(let index of selectData){
          args.push({
            "key":count,
            "name": index.name,
            "rename": null,
            "type": index.type == 0?"":index.type,
            "length":index.length == -1?"":index.length,
            "precision": index.length == -1?"":index.length,
            "conversionMask": index.conversionMask,
            "dateFormatLenient": "",
            "dateFormatLocale": null,
            "dateFormatTimeZone": null,
            "lenientStringToNumber": "",
            "encoding": null,
            "decimalSymbol": index.decimalsymbol,
            "groupingSymbol": index.groupingsymbol,
            "currencySymbol": index.currencysymbol,
            "storageType": index.storageType
          });
          count++;
      }
      this.refs.editTable2.updateTable(args,count);
  }

  formItemLayout5 = {
    labelCol: { span: 5 },
    wrapperCol: { span: 14 },
  };

  render() {
    const { getFieldDecorator} = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;

    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };
    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 16 },
    };



    return (
      <Modal
        visible={visible}
        title="字段选择"
        wrapClassName="vertical-center-modal"
        width={800}
        maskClosable={false}
        onCancel={this.hideModal}
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={this.handleCreate} >
                    确定
                  </Button>,
                  <Button key="back" size="large"  onClick={this.hideModal}>取消</Button>,
                ]}
      >
        <Form >
          <FormItem label="步骤名称"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('text', {
              initialValue: text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }],
            })(
              <Input />
            )}
          </FormItem>
          <Tabs onChange={this.handleChange.bind(this)}  style={{margin:"20px 3% 0 3%"}}  type="card">
            <TabPane tab="选择与修改" key="1">
              <Row style={{marginBottom:"5px"}}>
                <Col span={12}>
                  <p style={{marginLeft:"5px"}}>字段：</p>
                </Col>
                <Col span={12}>
                  <ButtonGroup size={"small"} style={{float:"right"}} >
                    <Button    onClick={this.handleAdd}>添加字段</Button>
                    <Button   onClick={this.handleFocus.bind(this)} >获取字段</Button>
                    <Button  onClick={this.handleDeleteFields.bind(this)}>删除字段</Button>
                  </ButtonGroup>
                </Col>
              </Row>
              <EditTable  rowSelection={true} columns={this.Columns} dataSource = {this.state.dataSource} tableStyle="editTableStyle5" size={"small"} scroll={{y: 140}} ref="editTable"   count={0}/>
              <FormItem  style={{marginBottom:"8px"}}  {...formItemLayout1}>
                {getFieldDecorator('selectingAndSortingUnspecifiedFields', {
                  valuePropName: 'checked',
                  initialValue:config.selectingAndSortingUnspecifiedFields
                })(
                  <Checkbox >使用DB来获取sequence？</Checkbox>
                )}
              </FormItem>
            </TabPane>
            <TabPane tab="移除" key="2">


                <Row style={{marginBottom:"5px"}}>
                  <Col span={12}>
                    <p style={{marginLeft:"5px"}}>移除字段：</p>
                  </Col>
                  <Col span={12}>
                    <ButtonGroup size={"small"} style={{float:"right"}} >
                      <Button    onClick={this.handleAdd1.bind(this)}>添加字段</Button>
                      <Button   onClick={this.getRemoveFields.bind(this)} >获取字段</Button>
                      <Button size={"small"}  onClick={this.handleDeleteFields1.bind(this)}>删除字段</Button>
                    </ButtonGroup>
                  </Col>
                </Row>

              <EditTable    initFuc={this.initFuc1.bind(this)}  rowSelection={true} columns={this.Columns1} dataSource = {this.state.dataSource1} tableStyle="editTableStyle5" size={"small"} scroll={{y: 140}} ref="editTable1"   count={0}/>
            </TabPane>
            <TabPane tab="元数据" key="3">
              <Row style={{marginBottom:"5px"}}>
                <Col span={12}>
                  <p style={{marginLeft:"5px"}}>需要改变元数据的字段：</p>
                </Col>
                <Col span={12}>
                  <ButtonGroup size={"small"} style={{float:"right"}} >
                    <Button    onClick={this.handleAdd2.bind(this)}>添加字段</Button>
                    <Button  onClick={this.getFields.bind(this)} >获取字段</Button>
                    <Button   size={"small"} onClick={this.handleDeleteFields2.bind(this)}>删除字段</Button>
                  </ButtonGroup>
                </Col>
              </Row>
              <EditTable initFuc={this.initFuc2.bind(this)} rowSelection={true} columns={this.Columns2} dataSource = {this.state.dataSource2} tableStyle="editTableStyle5" size={"small"} scroll={{y: 140,x:2000}} ref="editTable2"   count={0}/>
            </TabPane>
          </Tabs>
        </Form>
      </Modal>
    );
  }
}
const SelectValues = Form.create()(SelectDialog);

export default connect()(SelectValues);
