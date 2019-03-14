//新增权限控制功能  不同系统是否拥有完整权限
//@edit by pwj  2018/11/5
//质量权限      quality
//BI权限        bi     
//跳神算子权限    shen    
//home页面大屏  dataMap 

// examples==>  premit:["quality","bi"]

const lengin = {
  siteName:"灵机大数据",
  version: '1.0.0.0',
  copyright: 'Copyright ©灵机大数据技术有限公司版权所有',
  logo:require("assets/lengin/logo.png"),
  logoColour:require("assets/lengin/logo-colour.png"),
  faviconUrl:"./favicon/favicon.lengin.ico",
  iconType:"lengin-systemlogo",
  premit:["quality","bi"]
};

const chongqing = {
  siteName: '重庆农村云大数据中心',
  version: '1.0.0.0',
  copyright: 'Copyright ©重庆农村云大数据中心版权所有',
  logo:require("assets/chongqing/logo-colour.png"),
  logoColour:require("assets/chongqing/logo-colour.png"),
  faviconUrl:"./favicon/favicon.chongqing.ico",
  iconType:"chongqing-systemlogo",
  premit:["quality","bi","shen","yunyang"]
};

const cangzhou = {
  siteName: '沧州大数据',
  version: '1.0.0.0',
  copyright: 'Copyright ©灵机大数据技术有限公司版权所有',
  logo:require("assets/cangzhou/logo.png"),
  logoColour:require("assets/cangzhou/logo-colour.png"),
  faviconUrl:"./favicon/favicon.cangzhou.ico",
  iconType:"cangzhou-systemlogo",
  premit:["dataMap"]
};

const showPlatform = ()=>{
	switch(SITE_NAME){	
		case "cangzhou":
			return cangzhou;
		case "chongqing":
			return chongqing;
		case "lengin":
			return lengin;
		default:
			return lengin;
	}
}

const PlatformParams = showPlatform();

//适配自定义主题
PlatformParams.siteName = CUSTOM_PARAMS && CUSTOM_PARAMS.SITE_NAME?CUSTOM_PARAMS.SITE_NAME:PlatformParams.siteName;
PlatformParams.copyright = CUSTOM_PARAMS && CUSTOM_PARAMS.COPY_RIGHT?CUSTOM_PARAMS.COPY_RIGHT:PlatformParams.copyright;
//适配自定义权限
PlatformParams.premit = CUSTOM_PARAMS && CUSTOM_PARAMS.premit?CUSTOM_PARAMS.premit:PlatformParams.premit;

export default PlatformParams;