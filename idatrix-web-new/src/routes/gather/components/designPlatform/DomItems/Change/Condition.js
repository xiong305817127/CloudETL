import React from "react";
import { connect } from 'dva';
import PropTypes from 'prop-types'
import { Row, Col ,Input,Select , Button,  Icon  } from 'antd';

const Option = Select.Option;

class Condition extends React.Component {
  constructor (props) {
    super(props)
    const { data,onChange } = props
    let c={
		    "negate": false,
		    "operators": 2,
		    "leftvalue": null,
		    "function": "=",
		    "rightvalue": null,
		    "rightExactName": "constant",
		    "rightExactType": null,
		    "rightExactText": null,
		    "rightExactLength": -1,
		    "rightExactPrecision": -1,
		    "rightExactIsnull": false,
		    "rightExactMask": null,
		    "conditions": null
  }
    this.state = {
      loading: false,
      data:data?data:c,
      onChange:onChange?onChange:function(d){},
    }
  }

  componentWillMount(){
	  // 组件生成前(render前)调用的钩子
  }
  
  componentDidMount () {
	  // 组件生成后(render后)调用的钩子,只调用一次
  }

  componentWillReceiveProps (nextProps) {
	  // 当props发生变化时执行，初始化render时不执行，在这个回调函数里面，你可以根据属性的变化，通过调用this.setState()来更新你的组件状态，旧的属性还是可以通过this.props来获取,这里调用更新状态是安全的，并不会触发额外的render调用
   
  }
  
  getData(){
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

  onChange = (event,key) =>{
	  
	  let conditionKey ;
      let value ;
	  
      let elem = event.target
      if(elem && elem.dataset.conditionkey){
         conditionKey = elem.dataset.conditionkey
         value = "\""+elem.value+"\""
      }else{
    	  conditionKey = key
          value = event
      }
      
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
			    "rightExactName": "constant",
			    "rightExactType": null,
			    "rightExactText": null,
			    "rightExactLength": -1,
			    "rightExactPrecision": -1,
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
	  const margin = depth*30 ;
	  const style={ width: 480}
	  style.marginLeft=margin+"px"
	 return (<Row align="middle" key={c.leftvalue+"-"+c.rightvalue+"-"+parseInt(100*Math.random())} style={style}>
	    <Col span={3}>
		    <Select defaultValue="false" value={c.negate+""}  onChange={v => {this.onChange(v,prefix+".negate")}}  data-conditionKey={prefix+".negate"} style={{ width: 60 }} >
			    <Option value="false" ></Option>
			    <Option value="true" >非</Option>
		    </Select>
	    </Col>
	    <Col span={5}>
	    	<Input placeholder="请输入第一个条件项"  onChange={this.onChange}  data-conditionKey={prefix+".leftvalue"} value={c.leftvalue} />
	    </Col>
	    <Col span={3}>
		    <Select defaultValue="=" value={c.function}  onChange={v => {this.onChange("\""+v+"\"",prefix+".function")}}  data-conditionKey={prefix+".function"} style={{ width: 80 }} >
			    <Option value="=">=</Option>
			    <Option value="!=">!=</Option>
			    <Option value=">=">&gt;=</Option>
			    <Option value="<=" >&lt;=</Option>
		    </Select>
	    </Col>
	    <Col span={5}>
	    	<Input placeholder="请输入第二个条件项"  onChange={this.onChange} data-conditionKey={prefix+".rightvalue"}  value={c.rightvalue} />
	    </Col>
	    <Col span={5}>
	    	<Input placeholder="请输入第二个条件值"  onChange={this.onChange} data-conditionKey={prefix+".rightExactText"} value={c.rightExactText} />
	    </Col>
	    <Col span={3}>
	    	{ prefix != "" && <Button type="primary" shape="circle" icon="close" size="small" onClick={e => { this.onDelClick(prefix,index)}} /> }
		    <Button type="primary" shape="circle" icon="plus" size="small" onClick={e => { this.onAddClick(prefix,true)}} />
	    </Col>
	 </Row>)
  }
  
  addOperatorRow = (operator,negate ,depth,prefix,index) => {
	  
	  const margin = depth*30 ;
	  const style={ width: 480}
	  style.marginLeft=margin+"px"
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
			    <Option value="false" ></Option>
			    <Option value="true" >非</Option>
		    </Select>
	    </Col>}
    	 <Col span={3}>
    	 	{
    	 		negate!= null && 	<Button type="primary" shape="circle" icon="close" size="small" onClick={e => { this.onDelClick(prefix,index)}} />
    	 	}
	    	<Button type="primary" shape="circle" icon="plus" size="small" onClick={e => { this.onAddClick(prefix,false)}} />
	    </Col>
	 </Row>)
  }
  
  addNegateRow = (negate ,depth,prefix) => {
	  
	  const margin = depth*30 ;
	  const style={ width: 480}
	  style.marginLeft=margin+"px"
	 return (<Row align="middle" key={negate+"-"+parseInt(100*Math.random())} style={style}>
		 <Col span={3}>
		    <Select defaultValue="false" value={negate+""}  onChange={v => {this.onChange(v,prefix+".negate")}} data-conditionKey={prefix+".negate"} style={{ width: 60 }} >
			    <Option value="false" ></Option>
			    <Option value="true" >非</Option>
		    </Select>
	    </Col>
	    <Col span={3}>
    	<Button type="primary" shape="circle" icon="plus" size="small" onClick={e => { this.onAddClick(prefix,false)}} />
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
    const { data, ...conProps } = this.state
    const row = this.renderConditions(data,0,"");
    return (<div>
	    {row}
    </div>)
  }
}


Condition.propTypes = {
  data: PropTypes.object,
}

export default Condition