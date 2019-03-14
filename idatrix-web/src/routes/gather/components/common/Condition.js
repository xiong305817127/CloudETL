import React from "react";
import { connect } from 'dva';
import { withRouter } from 'react-router';
import PropTypes from 'prop-types'
import {  get_output_fields,check_step_name } from "../../../../services/gather"
import { Row, Col ,Input,Select , Button,  Icon,Form  } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const Option = Select.Option;

class Condition extends React.Component {
  constructor (props) {
    super(props);
    const { data,onChange,visibleS ,text,dataSource,focus} = props;
    console.log(dataSource,"dataSourcedataSource");
    
    this.state = {
      loading: false,
      data:data?data:c,
      onChange:onChange?onChange:function(d){},
      visibleS:false,
      visibleText:false,
     
    }

    let c={
		    "negate": false,
		    "operators": 2,
		    "leftvalue": null,
		    "function": "=",
		    "rightvalue": null,
		    "rightExactName": 'constants',
		    "rightExactType": '',
		    "rightExactText": '',
		    "rightExactLength": '',
		    "rightExactPrecision": '',
		    "rightExactIsnull": false,
		    "rightExactMask": null,
		    "conditions": null
    }

  }

  getData= ()=>{
	  return this.state.data ;
  }
  
  updateData = (conditionkey,value) => {
	  let self = this ;
	  if(conditionkey){
    	  eval( "self.setState(state => { state.data"+conditionkey+"="+value+" ; " +
    			  "self.state.onChange(state.data);"+		
    	  	"})")
      }
  }
componentDidMount(){
	
      
}
    onAddModel=(prefix,isChild)=>{
    	console.log(prefix,isChild,"prefix,isChild");
	  	this.setState({
	    	visibleS:true
	    })
	  }

  onChange = (event,key) =>{
	  
	  let conditionKey ;
      let value ;
      let elem = event.target
      if(elem && elem.dataset.conditionkey){
         conditionKey = elem.dataset.conditionkey
        /* value = "\""+elem.value+"\""*/
         value = elem.value
      }else{
    	  conditionKey = key
          value = event
      }
      console.log(conditionKey,value,"conditionKey,value");
      this.updateData(conditionKey,value);
  }
  
  onDelClick = (prefix,delIndex) => {
	  
	  let parentPrefix;
	  
	  let index = prefix.lastIndexOf(".")
	  if(index != -1 ){
		  parentPrefix=prefix.substring(0,index)
	  }else{
		 return ;
	  }
	  
	  let self = this ;
      eval("self.setState(state => { var cc = state.data"+parentPrefix+".conditions; cc.splice(delIndex,1); " +
      		"if(cc.length == 1){" +
      		"var newObj = JSON.parse(JSON.stringify(state.data"+parentPrefix+".conditions[0]));"+	
      		"state.data"+parentPrefix+"=newObj ;"+
      		"}"+
      		"self.state.onChange(state.data);"+
      		" })")
      		
  }

  
  onAddClick = (prefix,isChild) => {
	  let parentPrefix;
	  let index = prefix.lastIndexOf(".")
	  if(index != -1 && !isChild){
		  parentPrefix=prefix.substring(0,index)
	  }else{
		  parentPrefix=prefix
	  }
	  let c={
			    "negate": false,
			    "operators": 2,
			    "leftvalue": null,
			    "function": "=",
			    "rightvalue": null,
			    "rightExactName": '',
			    "rightExactType": '',
			    "rightExactText": '',
			    "rightExactLength": '',
			    "rightExactPrecision": '',
			    "rightExactIsnull": false,
			    "rightExactMask": null,
			    "conditions": null
	  }

	  let self = this ;
      eval("self.setState(state => { if(!state.data"+parentPrefix+".conditions){" +
      		"var newObj = JSON.parse(JSON.stringify(state.data"+parentPrefix+"));" +
      		"state.data"+parentPrefix+".conditions=[];" +
      		"state.data"+parentPrefix+".conditions.push(newObj)"+
      		"} " +
      		"state.data"+parentPrefix+".conditions.push(c);" +
      		"self.state.onChange(state.data);"+				
      		" })")
   }


  
  addDataRow = (c,depth,prefix,index) => {
	  if(!c){
		  return ;
	  }
	  const { InputData } = this.props;

	  const margin = depth*30 ;
	  const style={ width: "100%"}
	  style.marginLeft=margin+"px"
	return (<Row align="middle" key={c.leftvalue+"-"+c.rightvalue+"-"+parseInt(100*Math.random())} style={style}>
	    <Col span={2}>
		    <Select key={c.negate} defaultValue="false" value={c.negate+""}  onChange={v => {this.onChange("\""+v+"\"",prefix+".negate")}}  data-conditionKey={prefix+".negate"} style={{ width: 60 }} >
			    <Option value="false">{" "}</Option>
			    <Option value="true" >非</Option>
		    </Select>
	    </Col>
	    <Col span={5}>
	        <Select allowClear placeholder="请输入第一个条件项"  onSelect={v=>this.onChange("\""+v+"\"",prefix+".leftvalue")}  data-conditionKey={prefix+".leftvalue"} value={c.leftvalue}>
              {
                InputData?InputData.map((index)=>(<Select.Option key={index.name} value={index.name}>{index.name}</Select.Option>)):''
              }
	        </Select>
	    </Col>
	    <Col span={3}>
		    <Select defaultValue="=" value={c.function}  onChange={v => {this.onChange("\""+v+"\"",prefix+".function")}}  data-conditionKey={prefix+".function"} style={{ width: 80 }} >
			    <Option key="=" value="=">  =  </Option>
			    <Option key="<>" value="<>">{"<>"}</Option>
			    <Option key="<" value="<">{"<"}</Option>
			    <Option key="<=" value="<=">{"<="}</Option>
			    <Option key=">" value=">">{">"}</Option>
			    <Option key=">=" value=">=">{">="}</Option>
			    <Option key="REGEXP" value="REGEXP">REGEXP</Option>
			    <Option key="IS NULL" value="IS NULL">IS NULL</Option>
			    <Option key="IN LIST" value="IN LIST">IN LIST</Option>
			    <Option key="CONTAINS" value="CONTAINS">CONTAINS</Option>
			    <Option key="STARTS WITH" value="STARTS WITH">STARTS WITH</Option>
			    <Option key="ENDS WITH" value="ENDS WITH">ENDS WITH</Option>
			    <Option key="LIKE" value="LIKE">LIKE</Option>
			    <Option key="TRUE" value="TRUE">TRUE</Option>
		    </Select>
	    </Col>
	    <Col span={5}>
	       <Select allowClear placeholder="请选择第二个条件项"  onSelect={v=>this.onChange("\""+v+"\"",prefix+".rightvalue")} data-conditionKey={prefix+".rightvalue"}  value={c.rightvalue}>
              {
                InputData?InputData.map((index)=>(<Select.Option key={index.name} value={index.name}>{index.name}</Select.Option>)):''
              }
	        </Select>
	    </Col>
	         
		    
	       <Col span={2}>
	           <Button type="primary" onClick={v =>{this.onAddModel(prefix,true)}}>添加值</Button>
	       </Col>
	    <Col span={3}>
	    	{ prefix != "" && <Button type="primary" style={{marginLeft:20}} shape="circle" icon="close" size="small" onClick={e => { this.onDelClick(prefix,index)}} /> }
		    <Button type="primary" style={{marginLeft:20}} shape="circle" icon="plus" size="small" onClick={e => { this.onAddClick(prefix,true)}} />
	    </Col>
	    {
	    	this.state.visibleS == true ? (
                 <div style={{width: '71%'}}>
		           <Col span={6}>                                                                                                                                                                                                                                                                                                                                                                                  
				    	<Input placeholder="替换第二个值"  onChange={this.onChange} data-conditionKey={prefix+".rightExactName"} value={c.rightExactName} />
				     </Col>
				     <Col span={6}>   
				          <Select onSelect={v=>this.onChange("\""+v+"\"",prefix+".rightExactType")} data-conditionKey={prefix+".rightExactType"} value={c.rightExactType}>
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
				     </Col>
				     <Col span={5}>   
				        <Select onSelect={v=>this.onChange("\""+v+"\"",prefix+".rightExactText")} data-conditionKey={prefix+".rightExactText"} value={c.rightExactText}>
                             <Option value="#,##0.###">#,##0.###</Option>
                             <Option value="0.00">0.00</Option>
                             <Option value="0000000000000">0000000000000</Option>
                             <Option value="#.#">#.#</Option>
                             <Option value="#">#</Option>
                             <Option value="###,###,###.#">###,###,###.#</Option>
                             <Option value="#######.###">#######.###</Option>
                             <Option value="#####.###%">#####.###%</Option>
                             <Option value="yyyy/MM/dd HH:mm:ss">yyyy/MM/dd HH:mm:ss</Option>
                             <Option value="#,##0.###">#,##0.###</Option>
                      </Select>                                                                                                                                                                                                                                                                                                                                                                               
				    	<Input placeholder="转换格式" onChange={this.onChange} data-conditionKey={prefix+".rightExactText"} value={c.rightExactText} />
				     </Col>
				     <Col span={3}>                                                                                                                                                                                                                                                                                                                                                                                  
				    	<Input placeholder="长度" onChange={this.onChange} data-conditionKey={prefix+".rightExactLength"} value={c.rightExactLength} />
				     </Col>
				     <Col span={3}>                                                                                                                                                                                                                                                                                                                                                                                  
				    	<Input placeholder="精度" onChange={this.onChange} data-conditionKey={prefix+".rightExactPrecision"} value={c.rightExactPrecision} />
				     </Col>
		        </div>
	    	):null
	    }
	 </Row>
	 )
  }
  
  addOperatorRow = (operator,negate ,depth,prefix,index) => { 
	  
	  const margin = depth*30 ;
	  const style={ width: 480}
	  style.marginLeft=margin+"px"
	  style.marginBottom = "15px"
	  style.marginTop = "10px"
	 return (<Row align="middle" key={operator+"-"+negate+"-"+parseInt(100*Math.random())} style={style}>
		 <Col span={4}>
		    <Select defaultValue="2" value={operator+""}  onChange={v => {this.onChange(v,prefix+".operators")}}  data-conditionKey={prefix+".operators"} style={{ width: 80 }} >
		    	<Option value="1">Or</Option>
			    <Option value="2">And</Option>
			    <Option value="4">Or Not</Option>
			    <Option value="5" >And Not</Option>
			    <Option value="6" >XOR</Option>
		    </Select>
		</Col>
		<Col span={1}>
    	
    	</Col>
    	{ negate!= null &&  <Col span={3}>
		    <Select defaultValue="false" value={negate+""}   onChange={v => {this.onChange(v,prefix+".negate")}}  data-conditionKey={prefix+".negate"} style={{ width: 60 }} >
			    <Option key="false" value="false" >{" "}</Option>
			    <Option key="true" value="true" >非</Option>
		    </Select>
	    </Col>}
    	 <Col span={3}>
    	 	{
    	 		negate!= null && 	
    	 		<Button type="primary" shape="circle" icon="close" size="small" onClick={e => { this.onDelClick(prefix,index)}} />
    	 	}
	    	<Button type="primary" shape="circle" icon="plus" size="small" onClick={e => { this.onAddClick(prefix,false)}} />
	    </Col>
	 </Row>)
  }
  
  addNegateRow = (negate ,depth,prefix) => {
	  
	  const margin = depth*30 ;
	  const style={ width: 0}
	  style.marginLeft=margin+"px"
	 return (<Row align="middle" key={negate+"-"+parseInt(100*Math.random())} style={style}>
		 <Col span={3}>
		    <Select defaultValue="false" value={negate+""}  onChange={v => {this.onChange(v,prefix+".negate")}} data-conditionKey={prefix+".negate"} style={{ width: 60 }} >
			     <Option key="false" value="false" >{" "}</Option>
			    <Option key="true" value="true" >非</Option>
		    </Select>
	    </Col>
	    <Col span={3}>
    	<Button type="primary" shape="circle" icon="plus" size="small" onClick={e => { this.onAddClick(prefix,false)}} style={{marginLeft:100}} />
    	</Col>
	 </Row>)
  }
  



  renderConditions = (data,depth,prefix,indexNo) => {
	
	  let result = [] ;
	  if(data.conditions && data.conditions!=null && data.conditions.length >0){
		  let lastDepth = depth ;
		  let nextDepth = depth+1 ;
		  if(depth == 0){
			  result.push( this.addNegateRow(data.negate ,lastDepth,prefix) );
		  }
		  data.conditions.map((item,index) => {
			  if( index != 0 ){
				  let nextNegate=( item.conditions== null )?null:item.negate;
				  lastDepth=nextDepth
				  result.push( this.addOperatorRow(item.operators,nextNegate ,lastDepth,prefix+".conditions["+index+"]",index) );
			  }
			  result.push(this.renderConditions(item,nextDepth,prefix+".conditions["+index+"]" ,index))
			  
		   });
	  }else{
		  result.push( this.addDataRow(data,depth,prefix,indexNo) );
	  }
	  return result ;
  }



 
  render () {
  	const { getFieldDecorator,getFieldValue } = this.props.form;
    const {dataSource,visibleS, text,data, ...conProps } = this.state;


     const formItemLayout3 ={
	      labelCol: { span: 6 },
	      wrapperCol: { span: 10 },
	    };
    const row = this.renderConditions(data,0,"");
    return (<div>
	    {row}
      {/*
          <Modal
	        visible={visibleS}
	        title="值输入"
	        wrapClassName="vertical-center-modal"
	        width={750}
	        maskClosable={false}
	        onCancel={this.hideModal}
	        footer={[
	                  <Button key="submit" type="primary" size="large"  onClick={this.handleCreate} > 确定</Button>,
	                  <Button key="back" size="large"  onClick={this.hideModal}>取消</Button>,
	                ]}>

	          <Form>
	               <FormItem label="类型" {...formItemLayout3} style={{marginBottom:"8px"}}>
		                {getFieldDecorator('rightExactType', {
		                  
		                })(
		                    <Select style={{ width: 200 }}>
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
		              <FormItem label="值" {...formItemLayout3} style={{marginBottom:"8px"}}>
		                {getFieldDecorator('rightExactName', {
		                })(
		                    <Input />
		                )}
		              </FormItem>
		              <FormItem label="转换格式" {...formItemLayout3} style={{marginBottom:"8px"}}>
		                {getFieldDecorator('rightExactText', {
		                })(
		                        <Select style={{ width: 200 }}>
		                                 <Option value="#,##0.###">#,##0.###</Option>
		                                 <Option value="0.00">0.00</Option>
		                                 <Option value="0000000000000">0000000000000</Option>
		                                 <Option value="#.#">#.#</Option>
		                                 <Option value="#">#</Option>
		                                 <Option value="###,###,###.#">###,###,###.#</Option>
		                                 <Option value="#######.###">#######.###</Option>
		                                 <Option value="#####.###%">#####.###%</Option>
		                                 <Option value="yyyy/MM/dd HH:mm:ss">yyyy/MM/dd HH:mm:ss</Option>
		                                 <Option value="#,##0.###">#,##0.###</Option>
		                          </Select>
		                       
		                )}
		              </FormItem>
		              <FormItem label="长度" {...formItemLayout3} style={{marginBottom:"8px"}}>
		                {getFieldDecorator('rightExactLength', {
		                })(
		                    <Input />
		                )}
		              </FormItem>
		              <FormItem label="精度" {...formItemLayout3} style={{marginBottom:"8px"}}>
		                {getFieldDecorator('rightExactPrecision', {
		                })(
		                    <Input />
		                )}
		              </FormItem>
	          </Form>

	   </Modal>
      */}

    </div>)
  }
}


Condition.propTypes = {
  data: PropTypes.object,
}
const ConditionList = Form.create()(Condition);

export default connect()(ConditionList);



