import { connect }  from "dva";
import Modal from "components/Modal";
import { Form,Select,Button } from "antd";
import { intType } from "config/jsplumb.config.js";

const Option = Select.Option;
const FormItem = Form.Item;
let initValue  = "";

const index = ({ biModelId,form,dispatch })=>{
	const { visibleMain,mainDataSource,mainItem,dimension } = biModelId;
	const { getFieldDecorator } = form;

	console.log(biModelId,"数据");

	//表单提交
	const handelSure = (e)=>{
		e.preventDefault();
	    form.validateFields((err, values) => {
	    	let fields = mainItem.fields;
	      	if (!err) {
	      		if(initValue){
	      			dispatch({
	      				type:"biModelId/save",
	      				payload:{
	      					visibleMain:false,
	      					dimension:dimension.map(index=>{
	      						if(index.id === mainItem.id){
	      							index.primaryKey = values.mainKey;	  //本身主键
									index.foreignKey = values.mainKey;    //关联的外键
	      						}
	      						return index;
	      					})
	      				}
	      			})
	      		}else{
	      			dispatch({
			        	type:"biModelId/addMainKey",
			        	payload:{
			        		id:mainItem.id,		 //关联表Id
							tableName:mainItem.tableName,	 //关联表名
							Level:fields.filter(index=> !intType.includes(index.fieldType.toUpperCase())),        //字段
							primaryKey:values.mainKey,	  //本身主键
							foreignKey:values.mainKey,    //关联的外键
							name:mainItem.tableName,
							visible:true
			        	},
			        	measure:fields.filter(index=>intType.includes(index.fieldType.toUpperCase())),
			        })
	      		}	
		        form.resetFields();
	      	}
	    });
	};

	//取消
	const handelCancel = ()=>{
		form.resetFields();
		if(isExit){
			dispatch({
				type:"biModelId/save",
				payload:{ visibleMain:false }
			})
		}else{
			dispatch({type:"biModelId/save",payload:{
				mainTableId:"",
				mainDataSource:[],
				mainItem:null,
				visibleMain:false,
			}})
		}
	}

	//设定初始值
	const isExit = dimension.filter(index=> index.id === mainItem.id).length === 0?false:true;
	if(isExit){
		initValue = dimension.filter(index=> index.id === mainItem.id)[0].primaryKey
	}else{
		initValue = ""
	}

	return(
		<Modal 
			visible={visibleMain}
			title={isExit?"编辑自关联字段":"请选择自关联字段"} 
			footer={[
				<Button type="primary" key="sure" onClick={handelSure} >确定</Button>,
				<Button key="cancel" onClick={handelCancel} >取消</Button>
			]}

			style={{paddingBottom:"0px"}}
		>
	      	<Form>
		        <FormItem
		          label="关联字段"
		          labelCol={{ span: 5 }}
		          wrapperCol={{ span: 16 }}
		        >
		          {getFieldDecorator('mainKey', {
		          	initialValue:initValue,
		            rules: [
		            	{ required: true, message: '关联字段不能为空！' }
		            ],
		          })(
		            <Select>
						{
			         		mainDataSource.map(index=><Option key={index.id} value={index.name}>{index.name}</Option>)
						}
		      		</Select>
		          )}
		        </FormItem>
	        </Form>
		</Modal>
	)
}

export default connect(({
	biModelId
})=>({ biModelId }))(Form.create()(index));