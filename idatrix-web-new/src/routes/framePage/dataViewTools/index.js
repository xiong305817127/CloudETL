import Iframe from 'react-iframe';

const config = {
	width:"100%",
	height:"100%",
	className:"myClassname",
	display:"initial",
	position:"relative",
	allowFullScreen:true
}

let DATAVIEWTOOLS_URL = "";

if(CUSTOM_PARAMS && CUSTOM_PARAMS.DATAVIEWTOOLS_URL){
	DATAVIEWTOOLS_URL = CUSTOM_PARAMS.DATAVIEWTOOLS_URL;
}


const index = ()=>{
	
	return(
		<Iframe  config={config} url={DATAVIEWTOOLS_URL}   />
	)
}

export default index;