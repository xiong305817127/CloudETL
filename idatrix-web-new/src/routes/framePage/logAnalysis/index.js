/**
 * 日志分析页面
 */
import Iframe from 'react-iframe';

const config = {
	width:"100%",
	height:"100%",
	className:"myClassname",
	display:"initial",
	position:"relative",
	allowFullScreen:true
}

let LOG_URL = "";

if(CUSTOM_PARAMS && CUSTOM_PARAMS.LOG_URL){
	LOG_URL = CUSTOM_PARAMS.LOG_URL;
}


const index = ()=>{

	return(
		<Iframe  config={config} url={LOG_URL}  styles={{
			top:-36
		}} />
	)
}

export default index;