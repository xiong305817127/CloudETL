import React from "react";
import { connect } from "dva";
import { withRouter } from 'react-router';
import Modal from "components/Modal";
import { Form,Input,Row,Col,DatePicker,Button,Select,Checkbox } from "antd";
import { shareMethodArgs,colTypeArgs } from "../../constants";
import EditTable from '../../../gather/components/common/EditTable';
import moment from 'moment';
import _ from "lodash";

const { TextArea } = Input;
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
const Option = Select.Option;


class index extends React.Component{

	constructor(){
		super();
		console.log(this);
	}

	componentWillReceiveProps(nextProps){
		const { visible } = nextProps.subscriptionModal;

		if(!visible){
			console.log(visible,"值");
			this.props.form.resetFields();
		}
	}


	initFuc(that){
	  	const { searchSource,visible } = this.props.subscriptionModal;
	  	if(visible && searchSource.length>0){
			let args = [];
		 	searchSource.map(index=>{
		 		args.push(<Option key={index.colName} {...index} value={index.colName}>{index.colName}</Option>)
		 	})

	 		that.updateOptions({
	          colName:args
	        });
		}
	};



	searchEdition = [
		{
		    title: "参数名称",
		    dataIndex: 'colName',
		    key: 'colName',
		    width:"50%",
		    bindField:["colType","tableColCode"],
		    bindFuc:(name,options)=>{
		    	if(options){
					return options[name];
		    	}
		    },
		    selectable:true
		},
		{
			title: '数据类型',
		    dataIndex: 'colType',
		    key: 'colType',
		    width:"20%",
		    render:(text)=>{
		    	return (<span>{text?colTypeArgs[text].title:""}</span>)
		    }
		},
		{
			title: '参数代码',
		    dataIndex: 'tableColCode',
		    key: 'tableColCode',
		}
    ]

    Subscriptions = [
    	{
		    title: "序号",
		    dataIndex: 'key',
		    key: 'key',
		    width:"50px",
		    render:(text)=>(<span>{text++}</span>)
		},		
		{
		    title:"信息项名称",
		    dataIndex: 'colName',
		    key: 'colName',
		    width:"45%"
		},
		{
		    title: '数据类型',
		    dataIndex: 'colType',
		    key: 'colType',
		    width:"16%",
		     render:(text)=>{
		    	return (<span>{text?colTypeArgs[text].title:""}</span>)
		    }
	  	},
	  	{
		    title: '信息项编码',
		    dataIndex: 'tableColCode',
		    key: 'tableColCode',
		    width:"18%",
	  	},
		{
		    title: '订阅必选',
		    dataIndex: 'requiredFlag',
		    key: 'requiredFlag',
		    render:(text,record)=>{
		    	const { disabledKey } = this.props.subscriptionModal;

		    	return (<Checkbox defaultChecked={text} disabled={disabledKey.includes(record.id)} checked={text} onChange={(e)=>{this.handleTableChange(e.target.checked,record)}} />)
		    }
		}
	];

	//关闭
	onCancel = ()=>{
		const { dispatch } = this.props;

		dispatch({
			type:"subscriptionModal/save",
			payload:{
				visible:false
			}
		})
	}

	//增加
	handleAdd = ()=>{
		const data = {
	      "colName": "",
	      "colType": "",
	      "tableColCode": ""
	    };
	    this.refs.searchEditionTable.handleAdd(data);
	}

	//删除
	handleDelete = ()=>{
		this.refs.searchEditionTable.handleDelete();
	}

	//表格更新
	handleTableChange = (value,record)=>{
		let args = [];
		let count = 0;

		if(this.refs.editTable){
			for(let index of this.refs.editTable.state.dataSource){
				if(index.id === record.id){
					args.push({...index,"requiredFlag":value})
				}else{
					args.push({...index})
				}	
				count++;
			}
		}
		this.refs.editTable.updateTable(args,count);
	}

	//全部订阅
	handleSubscription = ()=>{
		let args = [];
		let count = 0;
		if(this.refs.editTable){
			for(let index of this.refs.editTable.state.dataSource){
				args.push({...index,"requiredFlag":true})	
				count++;
			}
		};
		this.refs.editTable.updateTable(args,count);
	};

	//取消订阅
	handleCancelSub = ()=>{
		const { config } = this.props.subscriptionModal;
		if(config.inputDbioList){
			let key = 1;
			let args = [];
			for(let index of config.inputDbioList){
				args.push({
					...index,key:key++
				})
			}
			this.refs.editTable.updateTable(args,args.length);
		}
	}

	//点击订阅
	handleOk = (e)=>{
		e.preventDefault();
	    this.props.form.validateFields((err, values) => {
	      if (!err) {
	        const { dispatch } = this.props;
	        const { config } = this.props.subscriptionModal;

	        let inputDbioList = [];
	        let outputDbioList = [];
	        if(this.refs.editTable){
	        	inputDbioList = this.refs.editTable.state.dataSource;
	        }
	         if(this.refs.searchEditionTable){
	        	outputDbioList = this.refs.searchEditionTable.state.dataSource;
	        }
			const {queryList } = this.props.indexModel;
	        dispatch({
	        	type:"subscriptionModal/getSubscriptionAdd",
	        	payload:{
	        		...values,
	        		deptName:config.deptName,
	        		resourceId:config.resourceId,
	        		shareMethod:config.shareMethod,
	        		endDate:values['endDate'].format('YYYY-MM-DD'),
	        		inputDbioList,
					outputDbioList,
					page:queryList.page,
				    pageSize:queryList.pageSize,
	        	}
			})
			/**const {queryList } = this.props.indexModel;
	        dispatch({type:"indexModel/getList",payload:{
				page:queryList.page,
				pageSize:queryList.pageSize,
			}}) */
	      }
	    });
	}

	render(){
		const {location,router}=this.props;
		const { visible,config,name,searchSource,subscriptionSource } = this.props.subscriptionModal;
		const { getFieldDecorator } = this.props.form;

		 const formItemLayout = {
	      labelCol: { span: 6 },
	      wrapperCol: { span: 14 },
	    }

		const dateFormat = "YYYY-MM-DD";

		return(
			<Modal
				visible={visible}
				width={800}
				title={`订阅事由(${name})`}
				onCancel={this.onCancel}
				onOk={this.onCancel}
				footer={[
		            <Button key="back" onClick={this.onCancel}>取消</Button>,
		            <Button key="submit" type="primary" onClick={this.handleOk}>
		              订阅
		            </Button>,
		          ]}
			>
				<Form>
					<FormItem
			          label="订阅事由"
			          style={{marginBottom:"8px"}}
			          {...formItemLayout}
			        >
			          {getFieldDecorator('subscribeReason', {
			            rules: [{ required: true, message: '请填写订阅原因！' }],
			          })(
			            <TextArea />
			          )}
			        </FormItem>
			        <FormItem
			          label="订阅方"
			           style={{marginBottom:"8px"}}
			          {...formItemLayout}
			        >
			           <span className="ant-form-text">{config.deptName}</span>
			        </FormItem>
			        <FormItem
			          label="交换方式"
			          style={{marginBottom:"8px"}}
			          {...formItemLayout}
			        >
			          <span className="ant-form-text">{config.shareMethod?shareMethodArgs[config.shareMethod].title:""}</span>
			        </FormItem>
			       <FormItem
			          label="订阅终止日期"
			          style={{marginBottom:"8px"}}
			          {...formItemLayout}
			        >
			          {getFieldDecorator('endDate',{
			            initialValue:moment(config.endDate, dateFormat)
			          })(
			            <DatePicker disabledDate={current => { return current.isBefore(moment(Date.now()).add(-1, 'days')) }}  format={dateFormat} allowClear={false} />
			          )}
			        </FormItem>
			        {
			        	config && config.shareMethod === 3?(
			        		 <FormItem
					          label="服务地址"
					           style={{marginBottom:"8px"}}
					          {...formItemLayout}
					        >
					           <span className="ant-form-text">{config.serviceUrl}</span>
					        </FormItem>
			        	):null
			        }
				</Form>
				{
					config.outputDbioList?(
						<div style={{margin:"0px 10%"}}>
							<h3>查询条件</h3>
			            	<Row style={{margin:"10px 0",width:"100%"}}  >
				                <Col span={12}>
				                    <Button  size={"small"} onClick={this.handleAdd.bind(this)}>新增条件</Button>
				                </Col>
				                <Col span={12}  style={{textAlign:"right"}}>
				                    <Button  size={"small"} onClick={this.handleDelete.bind(this)}>删除条件</Button>
				                </Col>
			              	</Row>
			      		  <EditTable initFuc={this.initFuc.bind(this)}  rowSelection={true} columns={this.searchEdition}  dataSource = {searchSource} tableStyle="editTableStyle5" size={"small"} scroll={{y: 140}} ref="searchEditionTable"   count={0}/>
						</div>
					):null
				}
				{
					config.inputDbioList?(
						<div style={{margin:"20px 10%"}}>
							<Row>
								<Col span={12}>
									<h3  style={{marginBottom:"10px"}}>订阅信息项</h3>
								</Col>
								<Col span={12} style={{textAlign:"right"}}>
									<ButtonGroup size={"small"} >
				                      <Button key="1" onClick={this.handleSubscription.bind(this)}>全部订阅</Button>
				                      <Button key="2" onClick={this.handleCancelSub.bind(this)}>取消订阅</Button>
				                    </ButtonGroup>
								</Col>
							</Row>
							
			      		  	<EditTable extendDisabled={true} columns={this.Subscriptions} dataSource = {subscriptionSource} tableStyle="editTableStyle5" size={"small"} scroll={{y: 140}} ref="editTable"   count={0}/>
						</div>
					):null
				}
			</Modal>
		)
	}
}

export default connect(({ subscriptionModal,indexModel,indexType })=>({ subscriptionModal,indexModel,indexType }))(withRouter(Form.create()(index)));