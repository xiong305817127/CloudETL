import React from "react";
import { Form,Select,Input,Row,Col } from 'antd';
import Modal from 'components/Modal.js';

const FormItem = Form.Item;
const Option = Select.Option;

 let obj = {
  "negate": false,
  "operators": 2,
  "leftvalue": "",
  "function": "=",
  "rightvalue": "",
  "rightExactName": "constant",
  "rightExactType": "Number",
  "rightExactText": "",
  "rightExactLength": -1,
  "rightExactPrecision": -1,
  "rightExactIsnull": false,
  "rightExactMask": "",
  "conditions":null
};

class Index extends React.Component {
    constructor(props){
      super(props);
      const { InputData,action,item } = props;

      let config = obj;

      if(item[0] && action === "edit"){
        config = props.item[0].props;
      }
      const {rightExactType,rightvalue,rightExactText} = config;

      this.state = {
        key:item[0]?item[0].key:0,
        config:config,
        selectValue:this.getModelView(rightvalue,rightExactText),
        InputType:rightExactType,
        //是否需要更新
        shouldUpdate:false
      }
    }

    componentWillReceiveProps(nextProps){
      const { item,action } = nextProps;
      const { shouldUpdate } = this.state;
      if(item[0] && action === 'edit' && shouldUpdate){
        this.setState({key:item[0].key,config:item[0].props,shouldUpdate:false,
          selectValue:this.getModelView(item[0].props.rightvalue,item[0].props.rightExactText)
        });
      }
    }

    getModelView(value1,value2){
      console.log(value1);
      console.log(value2);
      return value1 || value2 ? (value1?"1":"2") :"1"
    }

    //更换输入方式
    handleChange(value){
      this.setState({
        selectValue:value
      })
    };

    //更换类型
    handleTypeChange(value){
      const {setFieldsValue} = this.props.form;
      setFieldsValue({rightExactMask:""})
      this.setState({
        InputType:value
      })
    }

    //自定义筛选框
    selectInput(type){
      switch(type){
        case "Integer":
        case "Number" : 
          return [
          <Option key="1" value="#,##0.###">#,##0.###</Option>,
           <Option key="2" value="0.00">0.00</Option>,
           <Option key="3" value="0000000000000">0000000000000</Option>,
           <Option key="4" value="#.#">#.#</Option>,
           <Option key="5" value="#">#</Option>,
           <Option key="6" value="###,###,###.#">###,###,###.#</Option>,
           <Option key="7" value="#######.###">#######.###</Option>,
           <Option key="8" value="#####.###%">#####.###%</Option>,
          ];
          break;
        case "Date" :
          return [<Option key="0" value="yyyy/MM/dd HH:mm:ss.SSS">yyyy/MM/dd HH:mm:ss.SSS</Option>,
           <Option key="1" value="yyyy/MM/dd HH:mm:ss.SSS XXX">yyyy/MM/dd HH:mm:ss.SSS XXX</Option>,
           <Option key="2" value="yyyy/MM/dd HH:mm:ss">yyyy/MM/dd HH:mm:ss</Option>,
           <Option key="3" value="yyyy/MM/dd HH:mm:ss XXX">yyyy/MM/dd HH:mm:ss XXX</Option>,
           <Option key="4" value="yyyyMMddHHmmss">yyyyMMddHHmmss</Option>,
           <Option key="5" value="yyyy/MM/dd">yyyy/MM/dd</Option>,
           <Option key="6" value="yyyy-MM-dd">yyyy-MM-dd</Option>,
           <Option key="7" value="yyyy-MM-dd HH:mm:ss">yyyy-MM-dd HH:mm:ss</Option>,
           <Option key="8" value="yyyy-MM-dd HH:mm:ss XXX">yyyy-MM-dd HH:mm:ss XXX</Option>,
           <Option key="9" value="yyyyMMdd">yyyyMMdd</Option>,
           <Option key="10" value="MM/dd/yyyy">MM/dd/yyyy</Option>,
           <Option key="11" value="MM/dd/yyyy HH:mm:ss">MM/dd/yyyy HH:mm:ss</Option>,
           <Option key="12" value="MM-dd-yyyy">MM-dd-yyyy</Option>,
           <Option key="13" value="MM-dd-yyyy HH:mm:ss">MM-dd-yyyy HH:mm:ss</Option>,
           <Option key="14" value="MM/dd/yy">MM/dd/yy</Option>,
           <Option key="15" value="MM-dd-yy">MM-dd-yy</Option>,
           <Option key="16" value="dd/MM/yyyy">dd/MM/yyyy</Option>,
           <Option key="17" value="dd-MM-yyyy">dd-MM-yyyy</Option>,
           <Option key="18" value="yyyy-MM-dd'T'HH:mm:ss.SSSXXX">yyyy-MM-dd'T'HH:mm:ss.SSSXXX</Option>,
            <Option key="19" value="#.#">#.#</Option>];
          break;
        default :
          return [];    
      }
    }


    //点确实按钮
    handldOk(e){
      const {form,action,handleClick} = this.props;
      const { config,selectValue } = this.state;

      e.preventDefault();
      this.props.form.validateFieldsAndScroll((err, values) => {
        if (!err) {
          if(action === "edit"){
              if(selectValue === "1"){
                values.rightExactText = null;
              }else{
                values.rightvalue = null;
              }
          }

          handleClick(action,{...config,...values,operators:parseInt(values.operators)});
          this.setState({shouldUpdate:true})
          form.resetFields();
        }
      })
    }

    handleClose(){
       const {handleClick,form} = this.props;
      handleClick("close");
      this.setState({shouldUpdate:true})
      form.resetFields();
    }

    render(){
      console.log(this.props);
      const { getFieldDecorator } = this.props.form;
      const {visible,action,InputData} = this.props;
      const { config,InputType,selectValue } = this.state;

      console.log(this.state,"react机制");
      console.log(this.props,"react上层数据");
      const { item } = this.props;

      let show = true;
     
      if(action === "edit" && item){
        const {conditions} = item[0].props;
         console.log(conditions,"上级");
        if(conditions && conditions.length>0){
          show = false;
        }
      }


      const formItemLayout ={
        labelCol: { span: 6 },
        wrapperCol: { span: 14 },
      };

      const formItemLayout1 ={
        labelCol: { span: 8 },
        wrapperCol: { span: 14 },
      };

      const formItemLayout2 ={
        labelCol: { span: 6 },
        wrapperCol: { span: 16 },
      };

      return(
        <Modal
          visible={visible}
          title = {action==="new"?"新增节点":"编辑节点"}
          onCancel={this.handleClose.bind(this)}
          onOk={this.handldOk.bind(this)}
          width={850}
          zIndex={1020}
        >
          <Form>
            <Row>
              <Col span={12}>
                 <FormItem label="运算符" {...formItemLayout} >
                  {getFieldDecorator('operators', {
                    initialValue:config?config.operators+"":"",
                  })(
                      <Select allowClear>
                        <Option value="0">&nbsp;</Option>
                        <Option value="1">Or</Option>
                        <Option value="2">And</Option>
                        <Option value="4">Or Not</Option>
                        <Option value="5">And Not</Option>
                        <Option value="6" >XOR</Option>
                     </Select>
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem label="关系" {...formItemLayout} >
                  {getFieldDecorator('negate', {
                    initialValue:config?config.negate+"":"",
                  })(
                      <Select allowClear>
                        <Option value="false" >&nbsp;</Option>
                        <Option value="true" >非</Option>
                     </Select>
                  )}
                </FormItem>
              </Col>
            </Row> 
            {
              show?(
                <div>
                     <Row>
                    <Col span={10}>
                      <FormItem label="第一个条件项" {...formItemLayout1} >
                        {getFieldDecorator('leftvalue', {
                          initialValue:config?config.leftvalue:"",
                        })(
                            <Select allowClear>
                              {
                                InputData.map(index=>(<Option key={index.name} value={index.name}>{index.name}</Option>))
                              }
                           </Select>
                        )}
                      </FormItem>
                    </Col>
                    <Col span={6}>
                      <FormItem label="类型" {...formItemLayout2} >
                        {getFieldDecorator('function', {
                          initialValue: config?config.function:"",
                        })(
                            <Select >
                              <Option value="=">=</Option>
                              <Option value="<>">&lt;&gt;</Option>
                              <Option value="<">&lt;</Option>
                              <Option value=">">&gt;</Option>
                              <Option value=">=">&gt;=</Option>
                              <Option value="<=" >&lt;=</Option>
                              <Option value="REGEXP" >REGEXP</Option>
                              <Option value="IS NULL" >IS NULL</Option>
                              <Option value="IS NOT NULL" >IS NOT NULL</Option>
                              <Option value="IN LIST" >IN LIST</Option>
                              <Option value="CONTAINS" >CONTAINS</Option>
                              <Option value="STARTS WITH" >STARTS WITH</Option>
                              <Option value="ENDS WITH" >ENDS WITH</Option>
                              <Option value="LIKE" >LIKE</Option>
                              <Option value="TRUE" >TRUE</Option>
                           </Select>
                        )}
                      </FormItem>
                    </Col>
                    <Col span={8}>
                      <Row>
                        <Col span={8} style={{textAlign:"right"}}>条件项类型：</Col>
                        <Col span={14}>
                          <Select value={selectValue} onChange ={this.handleChange.bind(this)}>
                              <Option value="1">输入项</Option>
                              <Option value="2">自定义</Option>
                           </Select>
                        </Col>
                      </Row>
                    </Col>
                  </Row> 
                  
                  {
                    selectValue === "1"? (
                      <FormItem label="第二个条件项" {...formItemLayout} >
                        {getFieldDecorator('rightvalue', {
                          initialValue:config?config.rightvalue:"",
                        })(
                            <Select allowClear>
                              {
                                InputData.map(index=>(<Option key={index.name} value={index.name}>{index.name}</Option>))
                              }
                           </Select>
                        )}
                      </FormItem>
                    ):(
                      <div>
                        <FormItem label="类型" {...formItemLayout} >
                        {getFieldDecorator('rightExactType', {
                          initialValue: config?config.rightExactType:"Number"
                        })(
                            <Select  onChange={this.handleTypeChange.bind(this)}>
                              <Option value="Number">Number</Option>
                              <Option value="String">String</Option>
                              <Option value="Date">Date</Option>
                              <Option value="Boolean">Boolean</Option>
                              <Option value="Integer">Integer</Option>
                              <Option value="BigNumber">BigNumber</Option>
                              <Option value="Binary">Binary</Option>
                              <Option value="Timestamp">Timestamp</Option>
                              <Option value="Internet Address">Internet Address</Option>
                           </Select>
                        )}
                      </FormItem>
                      <FormItem label="值" {...formItemLayout} >
                        {getFieldDecorator('rightExactText', {
                          initialValue: config?config.rightExactText:""
                        })(
                            <Input />
                        )}
                      </FormItem>
                      <FormItem label="转换格式" {...formItemLayout} >
                        {getFieldDecorator('rightExactMask', {
                          initialValue: config?config.rightExactMask:"",
                        })(
                          <Select allowClear>
                            {this.selectInput(InputType)}
                          </Select>
                        )}
                      </FormItem>
                      <FormItem label="长度" {...formItemLayout} >
                        {getFieldDecorator('rightExactLength', {
                          initialValue:config?config.rightExactLength:"",
                        })(
                            <Input />
                        )}
                      </FormItem>
                      <FormItem label="精度" {...formItemLayout} >
                        {getFieldDecorator('rightExactPrecision', {
                          initialValue: config?config.rightExactPrecision:"",
                        })(
                            <Input />
                        )}
                      </FormItem>
                      </div>    
                    )
                  }
                </div>
              ):null
            }
          </Form>
        </Modal>
      )
    }
}

export default (Form.create()(Index));