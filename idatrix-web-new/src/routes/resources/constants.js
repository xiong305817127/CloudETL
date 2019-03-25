//资源目录常量
//["Dm","KingbaseES","access","dbf","dbase","sysbase","oracle","sql server","db2"],
//信息资源格式"
export const resourceType = {
	"电子文件":["OFD","wps","xml","txt","doc","docx","html","pdf","ppt","pptx"],
	"电子表格":["et","xls","xlsx"],
	"数据库":["MySQL","Oracle","DM","PostgreSQL"],
	"图形图像":["jpg","gif","bmp"],
	"流媒体":["swf","rm","mpg"],
	"自描述格式":[],
	"服务接口":[]
}


//可提交注册状态
export const canSubmitArgs = ["草稿","退回修改"];

//可退回修改，发布状态
export const canPubArgs = ["下架"];

//可下架
export const canRecallArgs = ["已发布"];

//交换方式
export const shareMethodArgs = {
	1:{
		title:"共享平台-数据库",
		oprater:"交换历史"
	},
	2:{
		title:"共享平台-文件下载",
		oprater:"文件下载"
	},
	3:{
		title:"共享平台-服务",
		oprater:"服务描述"
	}
}

//交换方式  我的资源
export const shareMethodList = {
	"订阅成功":{
		title:"共享平台-数据库",
		oprater:"交换历史"
	},
	
	"待审批":{
		title:"共享平台-服务",
		oprater:"服务描述"
	}
}

//数据类型
export const colTypeArgs = {
	"C":{ title:"字符型C" },
	"N":{ title:"数值型N" },
	"Y":{ title:"货币型Y" },
	"D":{ title:"日期型D" },
	"T":{ title:"日期时间型T" },
	"L":{ title:"逻辑型L" },
	"M":{ title:"备注型M" },
	"G":{ title:"通用型G" },
	"B":{ title:"双精度型B" },
	"I":{ title:"整型I" },
	"F":{ title:"浮点型F" }
}

//常用列表
export const normalList = new Map([
	["subscribeReason","订阅事由"],
	["deptName","订阅方"],
	["shareMethod","交换方式"],
	["endDate","订阅终止日期"],
	["subscribeUserName","申请人"],
	["subscribeTime","申请时间"],
	["status","订阅状态"],
	["approveTime","审批时间"],
	["suggestion","审批意见"]
])

//服务方式
export const serviceList = new Map([
	["subscribeReason","订阅事由"],
	["deptName","订阅方"],
	["shareMethod","交换方式"],
	["endDate","订阅终止日期"],
	["subscribeUserName","申请人"],
	["subscribeTime","申请时间"],
	["status","订阅状态"],
	["approveTime","审批时间"],
	["suggestion","审批意见"],
	["serviceUrl","服务地址"],
	["subKey","订阅代码"]
])

//数据库方式
export const dbList = new Map([
	["subscribeReason","订阅事由"],
	["deptName","订阅方"],
	["shareMethod","交换方式"],
	["endDate","订阅终止日期"],
	["subscribeUserName","申请人"],
	["subscribeTime","申请时间"],
	["status","订阅状态"],
	["approveTime","审批时间"],
	["suggestion","审批意见"],
	["terminalName","订阅方前置"],
	["ternimalDbName","订阅方数据库"]
])