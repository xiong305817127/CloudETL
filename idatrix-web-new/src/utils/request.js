import fetch from 'dva/fetch';
import { message } from 'antd';
import { goLogin } from './goLogin';
import { DEFAULT_EXCEPTION_MESSAGE } from '../constants';
import { getUserSession } from './session';
import __ from "lodash";
import qs from "qs";
import { _ErrorReporter } from "./utils";

const messageValveDuration = 5; // 错误提示信息弹出后多少秒内不再弹出，默认5秒
let messageValveOn = false; // 错误提示信息开关

// 创建一个请求锁数组，用于保存
let REQUEST_LOCK_LIST = [];

//对特殊请求Url做处理，返回异常时不使用message
let specialUrl = [
  "/security/u/submitLogin.shtml",
  "/security/u/logout.shtml",
  "/security/u/preLogin.shtml"
]

//对特殊请求Url做处理，返回错误信息时需要特殊处理，不使用统一处理
// let specialReturnUrl = [
//   "/metadataTable/importMetadata"
// ]


/**
 * 添加请求锁
 * @param {string} url request url
 * @@@ writtenBy Steven Leo on 2018/09/13  
 */
function LockRequest(url, options) {
  const requestData = qs.parse(url);

  // 将请求分为GET和POST请求
  // 两者使用Object封装后，使用lodash的some进行对比
  const requestFullGETData = { url: requestData, options: options };
  const requestFullPOSTData = { url: url, options: options };

  // 处理get请求
  if (!__.isEmpty(requestData)) {
    if (!__.some(REQUEST_LOCK_LIST, requestFullGETData)) {
      REQUEST_LOCK_LIST.push(requestFullGETData);
      return true;
    }
  }

  // 处理post请求
  else {
    if (!__.some(REQUEST_LOCK_LIST, requestFullPOSTData)) {
      REQUEST_LOCK_LIST.push(requestFullPOSTData);
      return true;
    }
  }

  // 如果发现请求已经加锁，则直接返回false
  // 说明请求可以继续执行，不返回 600 返回码
  return false;
}

/**
 * 请求解锁
 * @param {string} url
 * @param {object} options
 * @@@ written by Steven Leo on 2018/09/13
 */
function UNLOCK(url, options) {
  const requestData = qs.parse(url);
  const requestFullGETData = { url: requestData, options: options };
  const requestFullPOSTData = { url: url, options: options };

  if (!__.isEmpty(requestData)) {
    if (__.some(REQUEST_LOCK_LIST, requestFullGETData)) {
      REQUEST_LOCK_LIST.splice(__.findIndex(REQUEST_LOCK_LIST, requestFullGETData), 1);
    }
  }

  // 处理post请求
  else {
    if (__.some(REQUEST_LOCK_LIST, requestFullPOSTData)) {
      REQUEST_LOCK_LIST.splice(__.findIndex(REQUEST_LOCK_LIST, requestFullPOSTData), 1);
    }
  }
}

// 错误处理函数
async function errorHandling(resData,url) {
	const data = await resData;
	const { code } = data;
  // 发送错误日志
  _ErrorReporter({
    type: "request-catch",
    error: data,
    request: {}
  });

  if(url && specialUrl.includes(url)){
      return false;
  }
  // if (data.status === 403 || data.status === 7001) {
  if (code === "300" ||code === "444" ||url && url.indexOf('/security/u/login.shtml') !== -1) {
    // 登录失效
    messageValveOn = true;
    goLogin();
    setTimeout(() => (messageValveOn = false), 10e3);
  }
  else if (data.status === 600) {

    // 重复进行的request，不做处理
    // 直接返回
    console.log("错误被捕获，不要发起重复请求！")
    return;
  }else if(code === "777"){
    messageValveOn = true;
    goLogin("当前账号已在它处登录，请重新登录！");
    setTimeout(() => (messageValveOn = false), 10e3);
  }else{
    // 其它错误
    if (!messageValveOn) {
      messageValveOn = true;
      //准确定位错误信息
      const list = ["resultMsg","message","msg","errMsg","errMessage"];
      const tag = list.find(val => typeof data[val] !== "undefined");
      const errorInfo = data && data[tag] ? data[tag] : DEFAULT_EXCEPTION_MESSAGE;      //请求异常提示，避免重复出现
      message.error(errorInfo, messageValveDuration, () => {
        messageValveOn = false
      });
    }
  }
}

function parseJSON(response) {

  let contentType = response.headers.get('content-type');

  if (contentType.includes("text/xml")) {
    return response.text();
  }

  return response.json();
}

function checkStatus(response,url) {
  if (response.status >= 200 && response.status < 300) {
    return response;
  }

  const error = new Error(response.statusText);
  error.response = response;
  errorHandling(response,url);
  throw error;
}


/**
 * Requests a URL, returning a promise.
 *
 * @param  {string} url       The URL we want to request
 * @param  {object} [options] The options we want to pass to "fetch"
 * @return {object}           An object containing either "data" or "err"
 */
export default function request(url, options) {

  console.log("发出请求",url);

  // 检查是否有请求锁
  if (!LockRequest(url, options)) {
    console.log(REQUEST_LOCK_LIST, "出现请求锁");
    // 如果出现请求锁，直接返回600错误，在errorHandler进行处理
    // 一般情况不做弹窗处理
    return Promise
      .resolve({ status: 600, msg:"请勿重复点击确认。",message:"请勿重复点击确认。",statusText: "请勿发起重复请求！" })
      .then((data) => {
        errorHandling(data);
        return { data }
      });
  } else {
    const session = getUserSession();

    // 配置默认headers
    const headers = Object.assign(
      {
        'Content-Type': 'application/json;charset=UTF-8',
        'cache': 'default',
      },
      options && options.headers
    );

    if (session) {
      headers['VT'] = session;
    }

    // 配置默认设置
    const settings = Object.assign(
      {
        method: 'GET',
        mode: 'cors',
        credentials: options && options.omit ? "omit" : 'include',
      },
      options, { headers }
    );

    // 修复url中多余的斜杠
    const fixUrl = url.replace(/\/\//g, '/').replace(/:\/([^/])/, '://$1');

    // 非GET方式不允许缓存
    if (settings.method.toUpperCase() !== 'GET') {
      settings['Cache-Control'] = 'no-cache';
    }

    return fetch(fixUrl, settings)
      .then(checkStatus,url)
      .then(parseJSON)
      .then((data) => {
        // 请求结束后，立即解锁
        UNLOCK(url, options);
        if (
          //新增统一错误处理，请求码
          //@edited by pwj  2018/11/12
          typeof data.status !== "undefined" && data.status !== 200
          || (typeof data.code !== "undefined" && !(["200", "0", 200, 0].includes(data.code)))
          || (typeof data.retCode !== "undefined" && data.retCode !== 0 && data.code !== 0)
        ) {
          // 发送错误日志
          _ErrorReporter({
            type: "request-data",
            error: data,
            request: {
              url: url,
              options: options
            }
          });

					//对个别请求需要返回错误信息时
					// if(data.url && specialReturnUrl.some(index=>{
					// 	console.log(index,"参数");
					// 	console.log(data.url,"参数");
					// 	console.log(data.url.indexOf(index) !== -1,"参数");
					//   return 	data.url.indexOf(index) !== -1
					// })){
					// 		return { data }
					// }else{
					//		errorHandling(data,url);
					// }

					errorHandling(data,url);
        };
        return { data };
      })
      .catch((err) => {
        // 请求结束，立即解锁
        UNLOCK(url, options);
        return { err };
      });
  }

}
