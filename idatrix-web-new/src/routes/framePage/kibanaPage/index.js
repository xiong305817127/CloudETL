/**
 * kibanaPage 页面
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

let KIBANA_URL = "";

if(CUSTOM_PARAMS && CUSTOM_PARAMS.KIBANA_URL){
	KIBANA_URL = CUSTOM_PARAMS.KIBANA_URL;
}


const index = ()=>{

	return(
		<Iframe  config={config} url={KIBANA_URL}  />
	)
}

export default index;