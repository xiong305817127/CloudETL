/**
 * 跳转到登录页组件
 */
import Modal from 'components/Modal';
import { hashHistory } from "dva/router";
import { loginOut } from 'services/login';

const hashPatt = /[?&]_k=.+$/;

let skiping = false; // 是否即将跳转
let isOtherUser = false; 

// 跳转到登录页
export const goLogin = (text) => {
  if (skiping) return;
  const loginUrl = '/login';

  //屏蔽退出后返回退出的页面
  //@edit By pwj   2018/11/3
  // const backUrl = window.location.hash.replace(hashPatt, '');
  // const loginUrl = (backUrl === '#/' || backUrl === '#/home') ? '/login' :
  //   '/login?backUrl=' + encodeURIComponent(backUrl);

  skiping = true;
  // if (backUrl === '#/') { // 如果是直接打开主页，并且未登录，则直接跳转
  //   hashHistory.push(loginUrl);
  //   return;
  // }

  let tip = text?text:"登录已过期，请重新登录";

  Modal.confirm({
    content: `${tip}`,
    okText: '立即登录',
    cancelText:"取消",
    zIndex:1050,
    onOk: () => {
      loginOut().then(res=>{
				const { code } = res.data;
				if(code === "200"){
					hashHistory.push(loginUrl);
				}
      });
    },
    onCancel: () => {
      skiping = false;
    },
  });
  // message.warn('登录失效，请重新登录');
};

// 重置状态
export const resetGoLogin = () => {
  skiping = false;
};

// 更新第三方登录
export const resetOtherUser = (bool) => {
  isOtherUser = bool;
};

// 更新第三方登录
export const getOtherUser = () => {
  return isOtherUser;
};