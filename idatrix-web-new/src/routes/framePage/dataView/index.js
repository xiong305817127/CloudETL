import Iframe from 'react-iframe';

const config = {
	width:"100%",
	height:"100%",
	className:"myClassname",
	display:"initial",
	position:"relative",
	allowFullScreen:true
}

let DATAVIEW_URL = "http://10.0.0.83:5601/app/kibana#/dashboard/c72e9b70-f6a4-11e8-a1e0-536204da1605?embed=true&_g=(refreshInterval%3A(display%3AOff%2Cpause%3A!f%2Cvalue%3A0)%2Ctime%3A(from%3Anow-2y%2Cmode%3Aquick%2Cto%3Anow))";

if(CUSTOM_PARAMS && CUSTOM_PARAMS.DATAVIEW_URL){
	DATAVIEW_URL = CUSTOM_PARAMS.DATAVIEW_URL;
}


const index = ()=>{

	return(
		<Iframe  config={config} url={DATAVIEW_URL}  styles={{
			top:-36
		}} />
	)
}

export default index;