import Iframe from 'react-iframe';

const config = {
	width:"100%",
	height:"100%",
	className:"myClassname",
	display:"initial",
	position:"relative",
	allowFullScreen:true
}

let BBS_URL = "http://10.0.0.116:8081/";

if(CUSTOM_PARAMS && CUSTOM_PARAMS.BBS_URL){
	BBS_URL = CUSTOM_PARAMS.BBS_URL;
}

const index = ()=>{

	return(
		<Iframe  config={config} url={BBS_URL}  />
	)
}

export default index;