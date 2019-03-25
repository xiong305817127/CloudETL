/**
 * 文件上传公共组件
 * @param  {String} uploadUrl      //文件上传的Url
 * @param  {String} title          	//弹框的title  默认"文件上传"
 * @param  {String} fileName         //向后台传送 代表文件的字段名  默认 file
 * @param  {Object} data          	//向后台传送 除覆盖的数据
 * @param  {Object} uploadBtn          	//自定义点击样式
 * @param  {Boolean} fileCover      //是否启用文件覆盖 
 * @param  {String} coverName      //启用覆盖，向后台传值的名称 	  默认 isCover	
 * @param  {String} btnTitle         //按钮的title  默认"文件上传"
 * @param  {String} upTitle         //上传文件提示  默认"请选择文件上传"
 * @param  {Fuction} handleCallback  //点击确定执行的方法,返回上传文件列表
 */
import React from 'react';
import { Upload, message, Button, Icon,Checkbox  } from 'antd';
import Modal from 'components/Modal';
import { connect } from 'dva';
import PropTypes from "prop-types";
import { cloneDeep } from 'lodash';

const initState = {
	//modal展示
	visible : false,
	//文件存在是否覆盖
	checkValue : true,
	//上传的文件列表
	fileList : []
}

class FileUpload extends React.Component{

	constructor(props){
		super(props);
		this.state = {...cloneDeep(initState)}
	}

	//确定
	handleSure(){
		const { handleCallback } = this.props;
		const { fileList } = this.state;
		if(handleCallback){
			handleCallback(fileList)
		}
		this.handleCancel();
	}
	//取消
	handleCancel(){
		this.setState({...cloneDeep(initState)})
	}
	//文件存在,是否覆盖
	handleChange(e){
		this.setState({
			checkValue:e.target.checked
		})
	}

	//弹出模态框
	handleClick(){
		this.setState({
			visible:true
		})
	}


	render(){
		const {
			uploadUrl = "",
			title = "文件上传",
			btnTitle = "文件上传",
			upTitle = "请选择文件上传",
			fileCover = false,
			multiple = true,
			coverName = "isCover",
			fileName = "file",
			data = {},
			uploadBtn,
			onRemove
		} = this.props;

		const {visible,checkValue,fileList} = this.state;

		const sendData = {...data,[coverName+""]:checkValue};
		if(!fileCover){
			delete sendData[coverName+""]
		}

		const _this = this;
		const props = {
		  name: `${fileName}`,
		  action: `${uploadUrl}`,
		  headers: {
		    authorization: 'authorization-text',
		    VT:this.props.account.vt
			},
			onRemove: onRemove,
		  data:{...sendData},
		  onChange(info) {
		    if (info.file.status !== 'uploading') {
		      console.log(info.file, info.fileList);
				}
		    if (info.file.status === 'done' && info.file.response && info.file.response.code === "200") {
		      message.success(`${info.file.name} 文件上传成功！请点击确认执行导入`);
		    } else if (info.file.status === 'error') {
		      message.error(`${info.file.name} 文件上传失败！`);
		    } else{
				   if(info.file.response && info.file.response !== "200"){
						info.fileList.map(index=>{
		    			if(index.uid === info.file.uid){
		    				index.status = "error";
		    				index.response = info.file.response.msg;	
							}
							if(index.status === "error"){
								message.error(info.file.response.msg);
							}
		    			return index;
		    		})
					}
		    }

		   	if(!multiple){
		   		info.fileList.splice(0,info.fileList.length-1);
		   	}

		    _this.setState({
	            fileList:info.fileList
	        });
		  },
		};

		return(
			<span>
				{
					uploadBtn?uploadBtn(this.handleClick.bind(this)) : <Button onClick = {this.handleClick.bind(this)}><Icon type="upload" />{btnTitle}</Button>
				}
				<Modal
		        	visible={visible}
		        	title={title}
		        	wrapClassName="vertical-center-modal out-model"
		        	onCancel={this.handleCancel.bind(this)}
			        footer={[
		              <Button key="submit" type="primary"  onClick={this.handleSure.bind(this)}>
		                确定
		              </Button>,
		              <Button key="back"  onClick={this.handleCancel.bind(this)}>取消</Button>,
		            ]}
			    >
		          <div style={{float:"right",marginTop:"6px",marginRight:"20px"}}>
		            <Checkbox checked={checkValue} disabled={!fileCover} onChange={this.handleChange.bind(this)}>若文件已存在，是否覆盖</Checkbox>
		          </div>
		          <Upload {...props} multiple={multiple} fileList={fileList}>
		            <Button>
		              <Icon type="upload" /> {upTitle}
		            </Button>
		          </Upload>
			    </Modal>
			</span>	
		)
	}
}

FileUpload.propTypes = {
	uploadUrl:PropTypes.string.isRequired,
	title:PropTypes.string,
	btnTitle:PropTypes.string,
	upTitle:PropTypes.string,
	handleCallback:PropTypes.func,
	fileCover:PropTypes.bool,
	multiple:PropTypes.bool,
	coverName:PropTypes.string,
	fileName:PropTypes.string,
	data:PropTypes.object,
	uploadBtn:PropTypes.func
}

export default connect(({
	account
})=>({ account }))(FileUpload);