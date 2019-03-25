/**
 * 门户网站
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

let PORTAL_URL = "";

if(CUSTOM_PARAMS && CUSTOM_PARAMS.PORTAL_URL){
	PORTAL_URL = CUSTOM_PARAMS.PORTAL_URL;
}


const index = ()=>{

	return(
		<Iframe  config={config} url={PORTAL_URL}  />
	)
}

export default index;