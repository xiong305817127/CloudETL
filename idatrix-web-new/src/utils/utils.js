import fecha from "fecha";
import qs from "qs";
import Chance from "chance";
import { getUserSession } from "./session";
import safeParse from "safe-json-parse/tuple";
import Modal from "components/Modal";
const confirm = Modal.confirm;
import { API_BASE_DOWNLOAD_FILE } from "../constants";

/**
 * getFormData
 * 获取全部
 * @param {obj} fields
 */
export const getFormData = fields => {
  const obj = {};
  for (let i in fields) {
    obj[i] = typeof fields[i].value !== "undefined" ? fields[i].value : "";
  }
  return obj;
};

/**
 * 函数curry工具
 */
export const CurryFunc = (list, newObj) => {
  return Object.assign.apply(
    null,
    Object.entries(list).map(value => ({
      [value[0]]: obj => {
        return value[1].apply(null, [
          typeof obj === "object" ? Object.assign(obj, newObj) : newObj
        ]);
      }
    }))
  );
};

/**
 * 日期格式化函数
 * @param  {Date}   date   将要格式化的日期
 * @param  {String} format 转化格式，默认YYYY-MM-DD hh:mm:ss
 * @return {string}        返回字符串形式的格式化后的日期
 */
export const dateFormat = (date, format = "YYYY-MM-DD HH:mm:ss") => {
  if (!date) return date;
  if (!date instanceof Date) {
    date = new Date(date);
  }
  return fecha.format(date, format);
};

/**
 * 复制字符串到粘贴板
 * @param  {string} str 将要复制的字符串
 */
export const copyString = str => {
  const copyDom = document.createElement("input");
  copyDom.setAttribute("type", "text");
  copyDom.setAttribute("value", str);
  copyDom.setAttribute("style", "width:1;height:1");
  document.body.appendChild(copyDom);
  return new Promise(resolve => {
    setTimeout(() => {
      try {
        copyDom.select();
        document.execCommand("Copy");
        document.body.removeChild(copyDom);
        resolve(true);
      } catch (err) {
        resolve(false);
      }
    }, 100);
  });
};

/**
 * 下载文件
 * @param  {string} url    文件/接口地址
 * @param  {String} method 请求方式，缺省为GET，也可用POST
 * @param  {object} data   要额外发送的表单数据
 */
export const downloadFile = (filename, method = "GET", data = null) => {
  const url = filename;
  const oldIframe = document.getElementById("__downloadfile_iframe__");
  if (oldIframe) {
    document.body.removeChild(oldIframe);
  }
  const iframe = document.createElement("iframe");
  const form = document.createElement("form");

  // 解出url中的data
  const pathname = url.replace(/\?.*$|#.*$/g, "");
  const query =
    url.indexOf("?") > -1 ? qs.parse(url.replace(/^.+\?|#.*$/g, "")) : {};
  const session = getUserSession();
  if (session) {
    query["VT"] = session;
  }

  const formData = Object.assign(method === "GET" ? query : {}, data);
  console.log(formData, "formData00000", data);
  iframe.setAttribute("id", "__downloadfile_iframe__");
  iframe.setAttribute("style", "display:none");
  form.setAttribute("method", method);
  form.setAttribute("action", pathname + "?" + qs.stringify(query));
  Object.keys(formData).forEach(key => {
    const input = document.createElement("input");
    input.setAttribute("name", key);
    input.setAttribute("value", formData[key]);
    form.appendChild(input);
  });
  // 启动
  document.body.appendChild(iframe);
  iframe.contentDocument.body.appendChild(form);
  form.submit();
};

/**
 * 深拷贝
 * 可确保任何情况下，对象都能被正确深拷贝
 * @param  {object} obj 将要拷贝的对象
 * @return {object}     深拷贝后的对象
 */
export const deepCopy = obj => {
  if (typeof obj !== "object" || obj === null) return obj;
  let result;
  if (Array.isArray(obj)) {
    result = [];
    obj.forEach(item => {
      result.push(
        typeof item === "object" && !(item instanceof Date)
          ? deepCopy(item)
          : item
      );
    });
  } else {
    result = {};
    Object.keys(obj).forEach(key => {
      result[key] =
        typeof obj[key] === "object" && !(obj[key] instanceof Date)
          ? deepCopy(obj[key])
          : obj[key];
    });
  }
  // }
  return result;
};

/**
 * 将具有parentId关联关系的偏平数组(一维)组装成树形
 * 子节点封装于childList属性
 * @param  {array}           list      原始数组
 * @param  {[type]}          rootId    根节点rootId值
 * @param  {String}          key       主键名，缺省为id
 * @param  {String}          parentKey 父节点主键名，缺省为parentId
 * @param  {String}          childName 将生成的子节点主键名，缺省为childList
 * @param  {function(child)} formater  格式化函数，将根据返回值格式化child，例如：return {value: id, child.label: child.name}
 * @return {array}                     返回数组形式的树型
 */
export const convertArrayToTree = (
  list,
  rootId,
  key = "id",
  parentKey = "parentId",
  childName = "childList",
  formater = null
) => {
  if (!Array.isArray(list)) return [];
  const newList = deepCopy(list);
  const result = [...newList.filter(item => item[parentKey] == rootId)];
  result.forEach(item => {
    const children = convertArrayToTree(
      list,
      item[key],
      key,
      parentKey,
      childName,
      formater
    );
    item[childName] = children.length === 0 ? null : children;
  });
  return typeof formater === "function"
    ? result.map(child => {
        return Object.assign(
          {
            [childName]: child[childName]
          },
          formater(child)
        );
      })
    : result;
};

/*
 * 带参数的文件上传
 * @param url 为上传url
 * @param callback 回掉函数
 * */
export const uploadFile = (url, data, callback) => {
  var request = new XMLHttpRequest();
  request.open("POST", url);
  const session = getUserSession();
  if (session) {
    request.setRequestHeader("VT", session);
  }
  request.send(data);
  request.onload = () => {
    callback(request);
  };
};

/**
 * 通过value值逆向查找object的key值
 * @param  {object} obj   待查对旬
 * @param  {string} value 指定的value值
 * @return {string}       命中的key值或undefined
 */
export const findKeyByValue = (obj, value) => {
  return Object.keys(obj).find(key => obj[key] == value);
};

/**
 * 生成全局唯一标识符
 * @return {string} 16进制唯一标识符
 */
export const createGUID = () => {
  const chance = new Chance();
  return chance.guid();
};

/**
 * 安全解析json函数
 * @param  {string} str 要解析的字符串
 * @return {object}     解析成功的对象、数组
 */
export const safeJsonParse = str => {
  const result = safeParse(str);
  return result[1];
};

/**
 * 根据id获取其在树形上的上游结构链
 * @param  {array}  tree         树形
 * @param  {number} id           id
 * @param  {string} idName       树形里id的名称，缺省为id
 * @param  {string} childrenName 树形里子节点的名称，缺省为children
 * @return {array}               返回从根节点到目标节点的链，若未找到，则返回null
 */
export const getChainByChildId = (
  tree,
  id,
  idName = "id",
  childrenName = "children"
) => {
  const result = [];
  const strId = String(id);
  let hit = false; // 是否命中
  const find = (deep, tree, id, idName, childrenName) => {
    tree.some(child => {
      result.splice(deep);
      result.push(child);
      hit = String(child[idName]) === strId;
      if (!hit && child[childrenName]) {
        find(deep + 1, child[childrenName], id, idName, childrenName);
      }
      return hit;
    });
  };
  find(0, tree, id, idName, childrenName);
  return hit ? result : null;
};

/**
 * 将指定节点全屏
 * @param  {DOM} dom 要全屏的dom节点，默认为body
 */
export const fullScreen = (dom = document.body) => {
  if (document.documentElement.requestFullscreen) {
    dom.requestFullscreen();
  } else if (document.documentElement.webkitRequestFullScreen) {
    dom.webkitRequestFullScreen();
  } else if (document.documentElement.mozRequestFullScreen) {
    dom.mozRequestFullScreen();
  } else if (document.documentElement.msRequestFullscreen) {
    dom.msRequestFullscreen();
  }
};

/**
 * 退出全屏
 */
export const exitFullscreen = () => {
  if (document.exitFullscreen) {
    document.exitFullscreen();
  } else if (document.webkitCancelFullScreen) {
    document.webkitCancelFullScreen();
  } else if (document.mozCancelFullScreen) {
    document.mozCancelFullScreen();
  } else if (document.msCancelFullScreen) {
    document.msCancelFullScreen();
  }
};

/**
 *
 * @param {obj} confirm配置参数
 * @param {boolean} true/false 确定/取消
 */
export const sureConfirm = (obj, callback) => {
  confirm({
    title: "确定提交吗?",
    onOk: () => {
      callback(true);
    },
    onCancel: () => {
      callback(false);
    },
    ...obj
  });
};

/**
 * 新建一个new image对象，用于发送错误码
 * 请求时，发送请求分类和请求体
 */
export const _ErrorReporter = ({ type, request, error }) => {
  return;
  // let ImageBody = window.ImageBody = new Image();
  // const newRequest = typeof request === "object"
  //   ? JSON.stringify(request)
  //   : request;
  // const newError = typeof error === "object"
  //   ? JSON.stringify(error)
  //   : error;

  // ImageBody.src =  _Error_Report_URL + "?" + type + "="  + (newRequest + newError);

  // // 清空对象
  // ImageBody = null;
};
