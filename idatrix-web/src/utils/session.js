import { encode, decode } from 'base64-utf8';
import { Cookies } from 'react-cookie';

const cookies = new Cookies(); 

// 获取用户会话信息
export const getUserSession = () => {
  let session = cookies.get('VT') || cookies.get('LT');
  if (session) {
    return session;
  } else {
    return null;
  }
};

// 获取用户本地信息
export const getLocalUser = () => {
  let local = localStorage.getItem('userInfo');
  if (local) {
    return JSON.parse(decode(local));
  } else {
    return null;
  }
};

/**
 * 存储用户本地信息
 * @param  {json} userInfo json格式的用户信息
 */
export const saveLocalUser = (userInfo) => {
  let strInfo = JSON.stringify(userInfo);
  localStorage.setItem('userInfo', encode(strInfo));
};

// session跨标签解决方案
/*(function(){
  if (!sessionStorage.length) {
    localStorage.setItem('getSessionStorage', Date.now());
  };

  window.addEventListener('storage', function(event){
    if (event.key == 'getSessionStorage') {
      localStorage.setItem('sessionStorage', JSON.stringify(sessionStorage));
      localStorage.removeItem('sessionStorage');
    } else if (event.key == 'sessionStorage' && !sessionStorage.length) {
      let data = JSON.parse(event.newValue);
      for (let key in data) {
        sessionStorage.setItem(key, data[key]);
      }
    }
  });
})();*/
