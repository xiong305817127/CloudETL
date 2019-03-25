import fetch from 'dva/fetch';
import { message } from 'antd';
import { goLogin } from './goLogin';
import qs from "querystring";
import { getUserSession } from './session';


const messageValveDuration = 5e3; // 错误提示信息弹出后多少秒内不再弹出，默认5秒
let messageValveOn = false; // 错误提示信息开关
const Error_Message = "文件下载失败";

// 错误处理函数
async function errorHandling(resData) {
  const data = await resData;
	const { code } = data;
  if (code === "300" || code === "444") {
    // 登录失效
    messageValveOn = true;
    goLogin();
    setTimeout(() => (messageValveOn = false), 10e3);
  }else if(code === "777"){
		messageValveOn = true;
    goLogin("当前账号已在它处登录，请重新登录！");
    setTimeout(() => (messageValveOn = false), 10e3);
	} else {
    // 其它错误
    if (!messageValveOn) {
      messageValveOn = true;
      message.error(`${data.msg?data.msg:Error_Message}`);
      setTimeout(() => (messageValveOn = false), messageValveDuration);
    }
  }
}

function checkStatus(response) {
  if (response.status >= 200 && response.status < 300) {
    return response;
  }
  const error = new Error(response.statusText);
  error.response = response;
  errorHandling(response);
  throw error;
}

function parseData(response){
		let contentType = response.headers.get('content-type');
		if(contentType.indexOf("application/json") !== -1){
			return response.json();
		}
		return response.blob();	
}

/**
 * Requests a URL, returning a promise.
 *
 * @param  {string} url       下载的url
 * @param  {object} [options] 必须存在fileName,即下载下来的文件名,各种类型的参数
 * @return {object}           
 */
export default function downRequest(url, options) {
  const session = getUserSession();
  // 配置默认headers
  const headers = Object.assign({
		'Content-Type': 'application/x-www-form-urlencoded',
		'credentials': 'include',
  }, options && options.headers);
  if (session) {
    headers['VT'] = session;
  }

  let settings  = Object.assign({
    method: 'GET',
    mode: 'cors',
    credentials: 'include'
  }, {...options,headers });

  // 修复url中多余的斜杠
  let fixUrl = url.replace(/\/\//g, '/').replace(/:\/([^/])/, '://$1');
	fixUrl = `${fixUrl}?${qs.stringify(options.body)}`;

	if(settings.method.toUpperCase() === "GET"){
		delete settings.body;
	}

  return fetch(fixUrl, settings)
		.then(checkStatus)
		.then(parseData)
    .then(data => {
				if(data.type === "application/octet-stream"){
					var a = document.createElement('a');
					var url = window.URL.createObjectURL(data);
					var filename = options.body.fileName;
					a.href = url;
					a.download = filename;
					a.click();
					window.URL.revokeObjectURL(url);
				}else{
					const { code } = data;
					if(code === "200"){
						return data;
					}else{
						errorHandling(data);
					}
				}
    })
}
