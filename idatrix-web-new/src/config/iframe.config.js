/**
 * 配置嵌入框架对应地址
 */
const commonConfig = {
	width:"100%",
    height:"750px",
    className:"myClassname",
    display:"initial",
    position:"relative",
    allowFullScreen:true
};

const INIT_URL = IFRAME_URL?IFRAME_URL:"";

export const iframeConfig = [
	{name:'new',route:"/analysis/ReportForms/New",url:`${INIT_URL}/#/new`,config:{...commonConfig}},
	{name:'open',route:"/analysis/ReportForms/Open",url:`${INIT_URL}/#/open`,config:{...commonConfig}},
	{name:'dashboard',route:"/analysis/ReportForms/Dashboards",url:`${INIT_URL}/#/dashboard`,config:{...commonConfig}},
]
