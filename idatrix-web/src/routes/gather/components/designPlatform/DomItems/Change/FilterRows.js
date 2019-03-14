import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Row,Col,Icon,Tree,Tooltip,message  } from 'antd';
import Modal from "components/Modal.js";
import Condition from '../../../common/Condition';
import FilterRowsModal from './FilterRowsModal';
import _ from 'lodash';

const confirm = Modal.confirm;
const Option = Select.Option;
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
const TreeNode = Tree.TreeNode;

let n = 0;

class FilterRows extends React.Component {

  //初始化
  constructor(props){
    super(props);

    const { condition } = props.model.config;

    this.state = {

      condition:this.getKey([condition]),
      InputData:[],

      //select节点详情
      selectedKeys:[],
      selectedNodes:[],
      expandedKeys:[],

      //控制编辑，新增Modal
      newView:false,
      action:"new"
    }
  }

  //请求输入字段
  componentDidMount(){
    this.Request();
  };

  //获取输入字段
  Request(){
    const { getInputFields,transname,text } = this.props.model;
    getInputFields({transname,stepname:text}, data => {
      this.setState({
         InputData:data,
      })
    })
  };

  //对condition进行排序 递归法
  getKey(args){
    return args.map(index=>{
      index.key = n++;
      if(index.conditions){
          index.conditions = this.getKey(index.conditions);
          return index;  
      }
      return index;
    })
  }


  //根据不同的key,获得不同的title
  getTitle(item,index,bool){
    if(bool){
      return (<span>{ item.negate || item.operators?(<span><strong>{this.getSelect(item.operators)}</strong>&nbsp;&nbsp;<strong style={{color:"red"}}>{item.negate?"非":""}</strong></span>):"无"}</span>);
    }else{
      if(index === 0){
        return (<span><strong style={{color:"red"}}>{item.negate?"非":""}</strong>&nbsp;&nbsp;{item.leftvalue}&nbsp;&nbsp;<strong>{item.function}</strong>&nbsp;&nbsp;{item.rightvalue || item.rightExactText}</span>);
      }else{
        return (<span><strong>{this.getSelect(item.operators)}</strong>&nbsp;&nbsp;<strong style={{color:"red"}}>{item.negate?"非":""}</strong>&nbsp;&nbsp;{item.leftvalue}&nbsp;&nbsp;<strong>{item.function}</strong>&nbsp;&nbsp;{item.rightvalue || item.rightExactText}</span>);
      }
    }
  }

  //根据select不同渲染不同数据
  getSelect(num){
    switch(num){
      case 1 : 
        return "Or";break;
      case 2 : 
        return "And";break;
      case 4 : 
        return "Or Not";break;
      case 5 : 
        return "And Not";break;
      case 6 : 
        return "XOR";break;
      default :
        return "";break;
    }
  }

  //增加、编辑节点
  addTreeNode(obj){
    const {selectedKeys} = this.state; 
    if(!selectedKeys.length){
      message.warning("请先选择父节点!");
    }else{
      this.setState({
        newView:true,
        action:"new"
      })
    }
  }

  //删除节点
  deleteTreeNode(){

    const that = this;
    const {selectedKeys,selectedNodes} = this.state;

    if(selectedKeys[0] === "0"){
        message.error("根节点不能删除！");
        return false;
    }

    const changeCondition = [...this.state.condition];
    confirm({
      title: '确定删除该节点吗?',
      zIndex:1020,
      onOk() {
        let num,args,keyArgs = [];

        let obj = null;
        let index1;
        that.loop(changeCondition,selectedKeys[0],(item,index,arr)=>{
          num = index;
          args = arr;
        });
        if(args){
          args.splice(num,1);
        }

        that.cleanTreeData(changeCondition,(item, index, arr)=>{
            obj = item;
            index1 = index;
        });

        if(obj){
          let cloneObj = _.cloneDeep(obj.conditions[0]);

          console.log(cloneObj);

          for(let index of Object.keys(obj)){
            if(index !=="negate" && index !== "operators"){
               obj[index] = cloneObj[index]
            }
          }

          if(index1 === 0){obj["operators"] = 0};
        }
        n = 0;
        let newData = that.getKey(changeCondition);

        for(let i=0;i<n;i++){ keyArgs.push(i+"");}


        that.state.selectedKeys.splice(0);  
        that.state.selectedNodes.splice(0);  
          
        that.setState({
          condition:newData,expandedKeys:keyArgs
        })
      }
    });
  }

  //编辑节点
  editTreeNode(obj){
    const {selectedKeys} = this.state; 
    if(!selectedKeys.length){
      message.warning("请先选择节点!");
    }else{
      this.setState({
        newView:true,
        action:"edit"
      })
    }
  }

  //选中树节点
  handleSelect(selectedKeys,e){
    const {selected,selectedNodes} = e;
    this.setState({selectedKeys,selectedNodes})
  }

  //编辑、新建Modal调用
  handleModalClick(action,config){
    if(action === "close"){
      this.setState({newView:false})
    }

    const { selectedNodes,selectedKeys } = this.state;
    if(config){
        if(config.negate && config.negate === "true"){
          config.negate = true;
       }else{
          config.negate = false;
       } 
       if(config.title){
          delete config.title;
       }
    }

    const changeCondition = [...this.state.condition];
    let obj = _.cloneDeep(selectedNodes[0].props);

    console.log(changeCondition,"信息");
    console.log(obj,"对象");
    if(action === "new"){
      if(obj.conditions){
        obj.conditions.push(config);
      }else{
        delete obj.title;
        obj.conditions = [{... obj,operators:"0"},config]
      }
      this.updateTreeData(selectedKeys[0],changeCondition,obj);
    }else if(action === "edit"){
      this.updateTreeData(selectedKeys[0],changeCondition,config);
    }
  }

  //treeData自净程序
  cleanTreeData = (data, callback) => data.forEach((item, index, arr)=>{
    
    if(item.conditions){
         console.log(item.conditions,"有条件的进入");
         console.log(callback);
        if(item.conditions.length === 1){
            return callback(item, index, arr);
        }else{
          return this.cleanTreeData(item.conditions,callback);
        }
    }
  })

   loop = (data, key, callback) => {
      data.forEach((item, index, arr) => {
        if (item.key === parseInt(key)) {
            return callback(item,index,arr)
        }
        if (item.conditions) {
          return this.loop(item.conditions, key, callback);
        }
      });
    };

  //更新树形结构数据
  updateTreeData(key,changeCondition,obj){
      let oldObj = null;
      let newData = null;
      let args = [];

      this.loop(changeCondition,key,(item)=>{
          oldObj = item;
      });

      for(let index of Object.keys(oldObj)){
          oldObj[index] = obj[index];
      }

      oldObj = obj;
      n = 0;
      newData = this.getKey(changeCondition);

      for(let i=0;i<n;i++){ args.push(i+"");}

      this.setState({condition:newData,newView:false,expandedKeys:args})
  }

  //


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

    const { condition } = this.state;

    console.log(condition[0]);
    
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
   
      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
         "sendFalseTo":values.sendFalseTo,
         "sendTrueTo": values.sendTrueTo,
         "condition": _.cloneDeep(condition[0])
      };

      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  }

  render() {

    const {condition,InputData,newView,selectedNodes,action,expandedKeys} = this.state;
    const { getFieldDecorator } = this.props.form;
    const { text,config,visible,handleCheckName,nextStepNames } = this.props.model;

    console.log(condition);

    const formItemLayout1 = { 
      labelCol: { span: 7 },
      wrapperCol: { span: 14 },
    };

    //渲染树节点
    const loop = data=> data.map((item,index)=>{
      if(item.conditions){
          return (<TreeNode firstNode={index===0} {...item} key={item.key} title={this.getTitle(item,index,true)} >{loop(item.conditions)}</TreeNode>)
      }
      return <TreeNode firstNode={index===0} {...item} key={item.key} title={this.getTitle(item,index,false)} />
    })

    return (
      <div>

          <Modal
            visible={visible}
            title="过滤记录"
            wrapClassName="vertical-center-modal"
            maskClosable={false}
            zIndex={800}
            width={850}
            footer={[
                      <Button key="submit" type="primary" size="large" onClick={this.handleCreate.bind(this)} >
                        确定
                      </Button>,
                      <Button key="back" size="large" onClick={this.hideModal.bind(this)}>取消</Button>,
                    ]}
            onCancel = {this.hideModal}>
            <Form >
              <FormItem label="步骤名称" {...formItemLayout1}>
                {getFieldDecorator('text', {
                  initialValue:text,
                  rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                    {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
                })(
                  <Input />
                )}
              </FormItem>
              <FormItem label="发送true数据给步骤"  {...formItemLayout1}>
                {getFieldDecorator('sendTrueTo', {
                  initialValue:config.sendTrueTo?config.sendTrueTo:"",
                })(
                  <Select allowClear>
                    {
                      nextStepNames?nextStepNames.map((index,key)=>(<Select.Option key={index}>{index}</Select.Option>)):null
                    }
                  </Select>
                )}
              </FormItem>
              <FormItem label="发送false数据给步骤"  {...formItemLayout1}>
                {getFieldDecorator('sendFalseTo', {
                  initialValue:config.sendFalseTo?config.sendFalseTo:"",
                })(
                  <Select allowClear>
                    {
                     nextStepNames ? nextStepNames.map((index,key)=>(<Select.Option key={index}>{index}</Select.Option>)):null
                    }
                  </Select>
                )}
              </FormItem>
              <div style={{padding:"10px 20px",border:"1px solid #e9e9e9",margin:"0 60px",maxHeight:"450px",overflow:"scroll"}}>
                <header style={{textAlign:"right"}}>
                  <Tooltip title="新增子节点">
                    <Button style={{margin:"0 5px"}} type="primary" size="small" icon="plus-square-o"
                            onClick={this.addTreeNode.bind(this)} />
                  </Tooltip>
                  <Tooltip title="编辑">
                    <Button style={{margin:"0 5px"}} type="primary" size="small" icon="edit"
                            onClick={this.editTreeNode.bind(this)} />
                  </Tooltip>
                  <Tooltip title="删除">
                    <Button style={{margin:"0 5px"}} type="primary" size="small" icon="delete"
                            onClick={this.deleteTreeNode.bind(this)} />
                  </Tooltip>            
               </header>
                <Tree
                  className="draggable-tree"
                  defaultExpandAll
                  expandedKeys={expandedKeys}
                  autoExpandParent={true}
                  onSelect = {this.handleSelect.bind(this)}
                >
                   {condition?loop(condition):null}
                </Tree>
              </div>      
            </Form>
          </Modal>
           <FilterRowsModal InputData={InputData} handleClick={this.handleModalClick.bind(this)} visible={newView} item={selectedNodes} action={action}/>  
      </div>
    );
  }
}
const Filter = Form.create()(FilterRows);

export default connect()(Filter);
