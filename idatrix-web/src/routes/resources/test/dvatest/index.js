import React from 'react';
import { Button } from 'antd';
import { connect } from 'dva';
import FileUpload from 'components/FileUpload/FileUpload.js';
import { API_BASE_CATALOG } from '../../../../constants';


const index = ({dispatch,dvatestModel})=>{

	const handleDelay = ()=>{
		dispatch({
			type:"dvatestModel/process"
		})
	}

	console.log(dvatestModel,"结果值");

	const fileUploadProps = {
		fileName:"files",
		uploadUrl: `${API_BASE_CATALOG}/dataUpload/saveOrUpdateUploadDataForFILE`,
		data:{
			formatType:"4"
		},
		handleCallback:(fileList)=>{
			console.log(fileList);
		}
	}

	return(
		<div>	
			<Button onClick={handleDelay}>延时测试</Button>
			<FileUpload {...fileUploadProps} />
		</div>	
	)
}

export default connect(({ dvatestModel })=>({ dvatestModel }))(index);