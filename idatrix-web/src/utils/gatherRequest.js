import fetch from 'dva/fetch';
import { message } from 'antd';
import { goLogin } from './goLogin';
import { DEFAULT_EXCEPTION_MESSAGE } from '../constants';
import { getUserSession } from './session';
import _ from "lodash";
const messageValveDuration = 5; // 错误提示信息弹出后多少秒内不再弹出，默认5秒
let messageValveOn = false; // 错误提示信息开关

//重复请求锁
const REPEAT_REQUEST_URL = [];

//对特殊请求Url做处理，返回异常时不使用message
let specialUrl = [
  "/security/u/submitLogin.shtml",
  "/security/u/logout.shtml",
  "/security/u/preLogin.shtml"
]


// 错误处理函数
async function errorHandling(resData) {

  const data = await resData;

  if (data && specialUrl.includes(data.url)) {
    return false;
  }

  if (data.code === "300" || data && data.url && data.url.indexOf('/security/u/login.shtml') > -1) {
    // 登录失效
    messageValveOn = true;
    goLogin();
    setTimeout(() => (messageValveOn = false), 10e3);
  } else if (data.status === 600) {
    console.log("错误被捕获，请勿重复发起请求！",REPEAT_REQUEST_URL);
    return;
  } else if (data.code === "444") {
    messageValveOn = true;
    goLogin();
    setTimeout(() => (messageValveOn = false), 10e3);
  } else if (data.code === "777" ) {
    messageValveOn = true;
    goLogin("当前账号已在它处登录，请重新登录！");
    setTimeout(() => (messageValveOn = false), 10e3);
  } else {
    // 其它错误
    if (!messageValveOn) {
      messageValveOn = true;
      //准确定位错误信息
      const list = ["resultMsg","message","msg","errMsg","errMessage","Message","Msg"];

      const tag = list.find(
            val => typeof data[val] !== "undefined"
        );
      const errorInfo = 
        data && data[tag] 
        ? data[tag] 
        : DEFAULT_EXCEPTION_MESSAGE;

      //请求异常提示，避免重复出现
      message.error(errorInfo, messageValveDuration, () => {
        messageValveOn = false
      });
    }
  }
}


function parseJSON(response) {
  return response.json();
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

function formatGetRequest(body) {
  let str = "";
  var obj = JSON.parse(body);

  if (obj && Object.keys(obj).length > 0) {
    for (let index of Object.keys(obj)) {
      str += "&" + index + "=" + encodeURIComponent(obj[index])
    }
    str = "?" + str.substring(1);
  }
  return str;
};

/**
 * 查询请求是否存在，并自动加锁
 * @param {string} url 
 * @param {object} options
 * 
 * @callback {object} 是否存在
 */
function isLocked(url, options) {
  let obj = { url, ...options };

  if (!_.isEmpty(obj)) {
    if (_.some(REPEAT_REQUEST_URL, obj)) {
      return true;
    }
    REPEAT_REQUEST_URL.push(obj);
    return false;
  }
}

/**
 * 解锁请求
 * @param {string} url 
 * @param {object} options 
 */
function unLock(url, options) {
  let obj = { url, ...options };
  if (!_.isEmpty(obj) && _.some(REPEAT_REQUEST_URL, obj)) {
    REPEAT_REQUEST_URL.splice(_.findIndex(REPEAT_REQUEST_URL, obj), 1);
  }
}

/**
 * Requests a URL, returning a promise.
 *
 * @param  {string} url       The URL we want to request
 * @param  {object} [options] The options we want to pass to "fetch"
 * @return {object}           An object containing either "data" or "err"
 */
export default function request(url, options) {

  if (isLocked(url, options)) {
    return Promise
      .resolve({ status: 600, statusText: "请勿发起重复请求！" })
      .then(data => {
        errorHandling(data);
        return { data };
      })
  }

  const session = getUserSession();
  // 配置默认headers
  const headers = Object.assign({
    'Content-Type': 'application/json;charset=UTF-8',
  }, options && options.headers);
  if (session) {
    headers['VT'] = session;
  }

  let settings = {};

  //自动格式化GET请求路径
  if (options.method.toUpperCase() === "GET") {
    settings = Object.assign({
      method: options.method.toUpperCase(),
      mode: 'cors',
      credentials: 'include'
    }, { headers });
  } else {
    settings = Object.assign({
      method: 'GET',
      mode: 'cors',
      credentials: 'include'
    }, options, { headers });
  }

  // 修复url中多余的斜杠
  let fixUrl = url.replace(/\/\//g, '/').replace(/:\/([^/])/, '://$1');
  // 非GET方式不允许缓存
  if (settings.method.toUpperCase() !== 'GET') {
    settings['Cache-Control'] = 'no-cache';
  }

  if (settings.method.toUpperCase() !== 'POST') {
    if (options && options.body) {
      fixUrl += formatGetRequest(options.body);
    }
  }

  return fetch(fixUrl, settings)
    .then(checkStatus)
    .then(parseJSON)
    .then((data) => {
      //解锁重复请求
      unLock(url, options);

      if (
        //新增统一错误处理，请求码
        //@edited by pwj  2018/11/12
        // || (typeof data.code !== "undefined" && !(["200","0",200,0].includes(data.code))) 
        // || (typeof data.retCode !== "undefined" && data.retCode !== 0 && data.code !== 0)

        typeof data.status !== "undefined" && data.status !== 200
        || (typeof data.code !== "undefined" && data.code !== "200")
      ) {
        errorHandling(data);
        // 发送错误日志
        _ErrorReporter({
          type: "request-data",
          error: data,
          request: {
            url: url,
            options: options
          }
        });

      };

      return { data };
    })
    .catch((err) => {
      //解锁重复请求
      unLock(url, options);
      return { err };
    });
}
