/**
 * Created by Administrator on 2017/12/19 0019.
 */
//一、环境
import React from "react";
import { connect } from 'dva';
import { Form, Select, Button, Input, Checkbox, Tabs, Row, Col, message,Icon } from 'antd';
import Modal from 'components/Modal';
import EditTable from '../../../common/EditTable';
import { treeViewConfig,treeUploadConfig } from '../../../../constant';
const ButtonGroup = Button.Group;
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;

//二、渲染
class FuzzyMatchInput extends React.Component {
  //1.预加载数据:
  constructor(props) {
    super(props);
    const { visible,config ,prevStepNames } = props.model;
    console.log(prevStepNames,1111);
    if(visible === true) {
      const {values} = config;
      let data = [];
      let count1 = 0;
      if (values) {
        for (let index of values) {
          data.push({
            key: count1,
            ...index
          });
          count1++;
        }
      }
      this.state = {
        dataSource:data,
        InputData:[], //输入控件
        InputDataName:'', //选中的输出控件
      };
    }
  };
  //2.前后控件参数：
  componentDidMount(){
   
    // getOutFields(obj, data => {
    //   console.log(data,123);
    //   if(data){
    //     this.setState({InputDataName:data });
    //   }
    // });
  };
  //3.1.提交表单：
  handleFormSubmit = (e) =>{
    e.preventDefault();
    const form = this.props.form;
    const { panel,transname,description,key,saveStep,text,formatTable,config } = this.props.model;
    const {values} = config;
    form.validateFields((err, value) => {
      if(err){
        return
      }
      let sendFields1 = [];
      if(this.refs.editTable1){
        if(this.refs.editTable1.state.dataSource.length>0){
          let arg = ["field","name"];
          sendFields1 = formatTable(this.refs.editTable1.state.dataSource,arg)
        }
      }else{
        if(values){
          sendFields1 = values
        }
      }
      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = (text === value.text?"":value.text);
      obj.type = panel;
      obj.description = description;//控件基本参数+5
      obj.config = {//表单参数设置
        values:sendFields1,
        ...value
      };
      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.setModelHide();
        }
      });
    })
  };
  //3.2.关闭对话框：打开对话框--在初始化Model触发状态
  setModelHide (){
    const {  dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  }
  /**4.其他：*/
    //4.1.对话框布局
  formItemLayout = {
    labelCol: { span: 5 },
    wrapperCol: { span: 18 },
  };
  formItemLayout1 = {
    labelCol: { span: 4 },
    wrapperCol: { span: 15 },
  };
  formItemLayout2 = {
    labelCol: { span: 5 },
    wrapperCol: { span: 18 },
  };
  //4.2.自定义标题
  columns = [
    {
      title: '字段',
      dataIndex: 'field',
      key: 'field',
      width:"50%",
      selectable:true,
    }, {
      title: '改名为',
      dataIndex: 'name',
      key: 'name',
      // width:"50%",
      // selectable:false,
      editable:true,
    }];
  //4.3.表格方法：
  handleAdd1 = ()=>{
    const data = {
      "field": "",
      "name": "",
    };
    this.refs.editTable1.handleAdd(data);
  };
  handleAuto1 = ()=>{
     const { InputData,InputName } = this.state;
       const { getOutFields,transname,text,getInputSelect } = this.props.model;
       const form = this.props.form;
       form.validateFields((err, values) => {
          let obj = {};
          obj.transname = transname;
          obj.stepname = text;
          getOutFields(obj, data => {
          
             let args = [];
            let count = 0;
             for(let index of data){
              args.push({
                "key":count,
                "field": index.name,
              });
              this.setState({
                 dataSource:args
              })
              count++;
            }
            this.refs.editTable1.updateTable(args,count);
          })
      })
  };
  handleDelete1 = ()=>{
    this.refs.editTable1.handleDelete();
  };
  //4.4.表格下拉框：
  initFuc1(that){
    const { getInputSelect } = this.props.model;
    const { InputData } = this.state;
    let options = getInputSelect(InputData,"name");//对应数组InputData的属性名对应标题key:name
    // console.log(that,'获取model方法：updateOptions');
    that.updateOptions({
      field:options,//可下拉
    });
  };
  //4.5.选中匹配步骤：
  handleChangeSelect= (value)=>{
    
    const { getInputFields,getOutFields,transname,text } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = value;
    getOutFields(obj, data => {
      console.log(data,'重新分装')
      if(data){
        this.setState({
          InputData:data,
          /* InputDataName:value===this.state.InputData[0].origin?this.state.InputData[0].name:(this.state.InputData[1].name?this.state.InputData[1].name:this.state.InputData[2])*///选中控件名对应数组
        });
      }
    });
  };
  render() {
    const { getFieldDecorator, getFieldValue} = this.props.form;
    const { visible,config,text,handleCheckName } = this.props.model;
    const setDisabled = ()=>{
      if(getFieldValue("closervalue") === undefined){
        return config.closervalue;
      }else{
        if(getFieldValue("closervalue")){
          return getFieldValue("closervalue");//true
        }else {
          return false;
        }
      }
    };//获取近似值
    return (
      <Modal
        maskClosable={false}
        visible={visible}
        title="模糊匹配"
        onCancel={this.setModelHide.bind(this)}
        wrapClassName="vertical-center-modal"
        width={750}
        footer={[
          <Button key="submit" type="primary" size="large" onClick={this.handleFormSubmit.bind(this)}>确定</Button>,
          <Button key="back" size="large" onClick={this.setModelHide.bind(this)}>取消</Button>,
        ]}
      >
        <FormItem label="步骤名称"   {...this.formItemLayout}>
          {getFieldDecorator('text', {
            initialValue:text,
            rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
              {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
          })(
            <Input spellCheck={false}/>
          )}
        </FormItem>

        <Tabs onChange={this.callback} type="card" >
          <TabPane tab="内容" key="1" style={{border:"1px solid #D9D9D9"}}>
            <fieldset className="ui-fieldset">
              <legend>&nbsp;&nbsp;匹配流（源）</legend>
              <Form >
                <FormItem label="匹配步骤：" {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('mStep', {
                    initialValue:config.mStep?config.mStep:''
                  })(
                        <Select allowClear  onChange={this.handleChangeSelect}>
                              {
                               this.props.model.prevStepNames? this.props.model.prevStepNames.map((index,key)=>(<Select.Option key={index}>{index}</Select.Option>)):''
                              }
                        </Select>
                    
                  )}
                </FormItem>
                <FormItem label="匹配字段：" {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('lookupfield', {//初始值为空，选择不同控件对应不同值InputDataName
                    initialValue:this.state.InputDataName?this.state.InputDataName:''
                  })(
                    <Select
                      mode="combobox"
                      allowClear
                      placeholder="匹配字段"
                    >
                      {
                        this.state.InputData?this.state.InputData.map((index)=>(<Select.Option key={index.name}>{index.name}</Select.Option>)):null
                      }
                    </Select>
                  )}
                </FormItem>
              </Form>
            </fieldset>
            <fieldset className="ui-fieldset">
              <legend>&nbsp;&nbsp;主数据流</legend>
              <Form >
                <FormItem label="主要流字段：" {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('mainstreamfield', {
                    initialValue:config.mainstreamfield
                  })(
                    <Select
                      mode="combobox"
                      allowClear
                      placeholder="主要流字段"
                    >
                      {
                        this.state.InputData?this.state.InputData.map((index)=>(<Select.Option key={index.name}>{index.name}</Select.Option>)):null
                      }
                    </Select>
                  )}
                </FormItem>
              </Form>
            </fieldset>
            <fieldset className="ui-fieldset">
              <legend>&nbsp;&nbsp;设置</legend>
              <Form >
                <FormItem label="算法：" {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('algorithm', {
                    initialValue:config.algorithm
                  })(
                    <Select >
                      <Select.Option value="Levenshtein">Levenshtein</Select.Option>
                      <Select.Option value="Damerau Levenshtein">Damerau Levenshtein</Select.Option>
                      <Select.Option value="Needleman Wunsch">Needleman Wunsch</Select.Option>
                      <Select.Option value="Jaro">Jaro</Select.Option>
                      <Select.Option value="Jaro Winkler">Jaro Winkler</Select.Option>
                      <Select.Option value="Pair letters Similarity">Pair letters Similarity</Select.Option>
                      <Select.Option value="Metaphone">Metaphone</Select.Option>
                      <Select.Option value="Double Metaphone">Double Metaphone</Select.Option>
                      <Select.Option value="SoundEx">SoundEx</Select.Option>
                      <Select.Option value="Refined SoundEx">Refined SoundEx</Select.Option>
                    </Select>
                  )}
                </FormItem>
                <FormItem label="大小写敏感"   {...this.formItemLayout2} style={{marginBottom:0}}>
                  {getFieldDecorator('caseSensitive', {
                    valuePropName: 'checked',
                    initialValue:config.caseSensitive?config.caseSensitive:false,
                  })(
                    <Checkbox/>
                  )}
                </FormItem>
                <FormItem label="获取近似值"   {...this.formItemLayout2} style={{marginBottom:0}}>
                  {getFieldDecorator('closervalue', {
                    valuePropName: 'checked',
                    initialValue:config.closervalue?config.closervalue:false,
                  })(
                    <Checkbox/>
                  )}
                </FormItem>
                <FormItem label="最小值"   {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('minimalValue', {
                    initialValue:config.minimalValue,
                  })(
                    <Input spellCheck={false}/>
                  )}
                </FormItem>
                <FormItem label="最大值"   {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('maximalValue', {
                    initialValue:config.maximalValue,
                  })(
                    <Input spellCheck={false}/>
                  )}
                </FormItem>
                <FormItem label="值分隔符"   {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('separator', {
                    initialValue:config.separator,
                  })(
                    <Input spellCheck={false} disabled={setDisabled()}/>
                  )}
                </FormItem>
              </Form>
            </fieldset>
          </TabPane>
          <TabPane tab="字段" key="2" style={{border:"1px solid #D9D9D9",padding:15}}>
            <fieldset className="ui-fieldset" style={{border:"1px solid #D9D9D9"}}>
              <legend>&nbsp;&nbsp;输出字段</legend>
              <Form style={{marginBottom:20}}>
                <FormItem label="匹配字段"   {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('outputmatchfield', {
                    initialValue:config.outputmatchfield,
                  })(
                    <Input spellCheck={false}/>
                  )}
                </FormItem>
                <FormItem label="值字段"   {...this.formItemLayout2} style={{marginBottom:8}}>
                  {getFieldDecorator('outputvaluefield', {
                    initialValue:config.outputvaluefield,
                  })(
                    <Input spellCheck={false}/>
                  )}
                </FormItem>
              </Form>
            </fieldset>

            <fieldset className="ui-fieldset" style={{marginTop:15}}>
              <legend>&nbsp;&nbsp;匹配流中的字段</legend>
              <Row style={{marginBottom:15}}>
                <Col span={12}>
                  <ButtonGroup size={"small"}>
                    <Button key="1" onClick={this.handleAuto1.bind(this)}>获取字段</Button>
                    <Button key="2" onClick={this.handleAdd1.bind(this)}>添加字段</Button>
                  </ButtonGroup>
                </Col>
                <Col span={12} style={{textAlign:"right"}}>
                  <Button  size={"small"}  onClick={this.handleDelete1.bind(this)}>删除字段</Button>
                </Col>
              </Row>
              <EditTable
                columns={this.columns}
                dataSource={this.state.dataSource}
                scroll={{y: 300}}
                initFuc={this.initFuc1.bind(this)}
                rowSelection={true}
                size={"small"}
                count={0}
                ref="editTable1"
                tableStyle="editTableStyle5"
              />

            </fieldset>
          </TabPane>
        </Tabs>

      </Modal>
    );
  }
}
//三、传参、调用：
const FuzzyMatch = Form.create()(FuzzyMatchInput);
export default connect()(FuzzyMatch);
