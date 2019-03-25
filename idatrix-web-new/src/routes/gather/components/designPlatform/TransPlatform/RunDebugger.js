import React from 'react'
import { connect } from 'dva'
import { Form,Input,Radio,Select,Checkbox,Row,Col,Button,Alert,Tabs,Table } from 'antd'
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;
const ButtonGroup = Button.Group;
const Option = Select.Option;
import { transArgs,disabledArgs } from '../../../constant';
import EditTable from '../../common/EditTable';
import Condition from '../../common/Condition'

//控制四个选项的disabled

let controlDid = false;


class Index extends React.Component{
     constructor(props){
        super(props);
        this.state={
            nameVisible:false,
            dataSource:[],
            dataSourceList:[],
            data:{},
            onChangeName:'',
            CheckboxName:'',
            name:'',
        }
    }
	  columns =  [{
	    title: '名称',
	    dataIndex: 'stepName',
	    key: 'stepName',
	    editable:true
	  }];

  componentDidMount(){
    let arge = [];
    for(let key of this.props.rundebugger.items){
        arge.push({
        	stepName:key.text
        });

        console.log(key,"key1111111111111111111111",arge);
    }

    this.setState({
    	dataSource:arge
    })
  };
	componentWillUpdate(){
		//改变值时都会调取这个生命周期
		/*this.setState({
			nameVisible:true
		})*/
	}


   handleClone(){
   	  const{dispatch}=this.props;
   	  let fa = this.state.CheckboxName?false:false;
     console.log(this.state.CheckboxName = false,"CheckboxName///handleClone",this);
   	  this.setState({
   	  	onChangeName:'',
   	  	CheckboxName:fa,
   	  })
   }

   handleHide(){
     const { dispatch } = this.props;
    dispatch({
      type:'rundebugger/hide',
      visible:false
    });
  };

   handleSubmit(e){
     const { form,rundebugger,dispatch } = this.props;
     const { actionName,model,viewId,selectedRows,runModel,dataSource } = rundebugger;
    e.preventDefault();
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
       const { data } = this.refs.editInput.props;

        dispatch({
          type:'rundebugger/batchRun',
          payload:{
            selectedRows:selectedRows,
            visible:false,
            debugExecDtos:{
              engineType:model,
              ...values
            }
          }
        });

    });
  };

  headonRowClick=(record, index, event)=>{
  	 if (record.stepName) {
  	 	 this.setState({
  	 	 	nameVisible:true,
  	 	 	name:record.stepName
  	 	 })
  	 }
  }

 onChangeName(e){
 	const{ onChangeName } = this.state;
    this.setState({
    	onChangeName:e.target.value
    })
 }

 CheckboxNames=(e)=>{
 	const{ CheckboxName } = this.state;
 	this.setState({
 		CheckboxName:e.target.checked
 	})
 }
 
 //运行转换
  runTrans(viewId) {
    const { name } = this.props.transspace;
    const { dispatch } = this.props;
    dispatch({
      type: 'runtrans/queryExecuteList',
      payload:{
        visible: true,
        actionName: name,
        viewId: viewId
      }
    });
     dispatch({
      type: 'rundebugger/hide',
      payload:{
        visible: false
      }
    });
  }


   render(){
     const { dispatch,form,rundebugger,cloudetlCommon } = this.props;
     const { getFieldDecorator,getFieldValue } = form;
     const { visible,model,executeList,runModel,dataSource } = rundebugger;
     const { transEngine } = cloudetlCommon;
     const { data,config,nameVisible } = this.state;

     console.log(config,"CheckboxName",this.state.name);
      const formItemLayout3 ={
	      labelCol: { span: 6 },
	      wrapperCol: { span: 10 },
	    };
     const conditionProps = {
        data:this.state.dataSourceList,
      }
     return(
       <Modal
         title={"转换调试接口"}
         wrapClassName="vertical-center-modal"
         visible={visible}
         onCancel={this.handleHide.bind(this)}
         width={1050}
         footer={[
         	<Button key="clone" size="large"  onClick={this.handleClone.bind(this)}>清除</Button>,
         	<Button key="Configuration" size="large"  onClick={this.runTrans.bind(this)}>配置</Button>,
            <Button key="submit" type="primary" size="large"  onClick={this.handleSubmit.bind(this)}>快速运行</Button>,
            <Button key="back" size="large"  onClick={this.handleHide.bind(this)}>取消</Button>
        ]}
       >
         <Form >
           <Row>
             <Col span={8}>
                <Table columns={this.columns} tableStyle="editTableStyle5" ref="editTable" scroll={{y: 300}}  size={"small"}
                 pagination={false} count={1} dataSource={this.state.dataSource} onRow={this.headonRowClick.bind(this)} />
             </Col>
             <Col span={16}>
                       {this.state.name ? (
                           <div>
                             <FormItem label="要获得的行数" {...formItemLayout3} style={{marginBottom:"8px"}}>
				                {getFieldDecorator('rowCount', {
				                })(
				                    <Input onChange={this.onChangeName}/>
				                )}
				              </FormItem>

				              <FormItem label="要获得的行数(预览)" {...formItemLayout3} style={{marginBottom:"8px"}}>
				                {getFieldDecorator('readingFirstRows', {
				                	valuePropName: 'checked',
				                })(
				                     <Checkbox onChange={this.CheckboxNames}/>
				                )}
				              </FormItem>

				              <FormItem label="满足条件时暂停转换" {...formItemLayout3} style={{marginBottom:"8px"}}>
				                {getFieldDecorator('pausingOnBreakPoint', {
				                	valuePropName: 'checked',
				                })(
				                    <Checkbox onChange={this.CheckboxNames.bind(this)}/> 
				                )}
				              </FormItem>

	                           <FormItem label="断点 暂停 条件" >
	                              <Condition  {...conditionProps} ref="editInput" onChange={this.onChange}/>
	                           </FormItem>
                         </div>
                       	):null}
                         
                		
             </Col>
           </Row>
         </Form >
       </Modal>
     )
   }
}

const RunDebugger = Form.create()(Index);

export default connect(({ rundebugger,cloudetlCommon,runtrans,transspace }) => ({
  rundebugger,cloudetlCommon,runtrans,transspace
}))(RunDebugger)

