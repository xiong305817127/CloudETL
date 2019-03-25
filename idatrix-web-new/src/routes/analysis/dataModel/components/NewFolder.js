import Modal from "components/Modal";
import { Form,Button,Input } from 'antd';
import { connect } from 'dva';

const FormItem = Form.Item;

let Timer = null;

const index = ({ biDatamodel,dispatch,form })=>{

	const { newFolder } = biDatamodel;
	const { getFieldDecorator } = form;
	
	//表单提交
	const handelSure = (e)=>{
		e.preventDefault();
	    form.validateFields((err, values) => {
	      if (!err) {
	        dispatch({
	        	type:"biDatamodel/addNewFolder",
	        	payload:{...values}
	        })
	        handelCancel();
	      }
	    });
	};

	//取消
	const handelCancel = ()=>{
		form.resetFields();
		dispatch({type:"biDatamodel/save",payload:{newFolder:false}})
	}

	//验证文件夹名称
	const handleCheckFolderName = (rule, value, callback)=>{
		if(Timer){
			clearTimeout(Timer);
			Timer = null;
		}
		if(value){
			Timer = setTimeout(()=>{
				dispatch({type:"biDatamodel/isExistFolder",payload:{name:value},callback})
			},1000)
		}
	}

	return(
		<Modal 
			visible={newFolder}
			title="新建文件夹"
			onCancel={handelCancel}
			footer={[
				<Button type="primary" key="sure" onClick={handelSure} >确定</Button>,
				<Button key="cancel" onClick={handelCancel} >取消</Button>
			]}

			style={{paddingBottom:"0px"}}
		>
	      	<Form>
		        <FormItem
		          label="文件夹名"
		          labelCol={{ span: 5 }}
		          wrapperCol={{ span: 16 }}
		        >
		          {getFieldDecorator('name', {
		            rules: [
		            	{ required: true, message: '文件夹名不能为空！' },
		            	{ validator: handleCheckFolderName}
		            ],
		          })(
		            <Input placeholder="请输入新建文件夹名" />
		          )}
		        </FormItem>
	        </Form>
		</Modal>
	)
}

export default connect(({
	biDatamodel
})=>({ biDatamodel }))(Form.create()(index))
