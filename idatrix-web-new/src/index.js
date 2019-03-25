import 'babel-polyfill';
import dva from 'dva';
import 'antd/dist/antd.css';
import './skin.less';
import './index.less';
import __ from "lodash";
import { SITE_CUSTOM_THEME } from 'constants';
import 'moment/locale/zh-cn';
import 'antd/lib/style/v2-compatible-reset';
import baseInfo from "config/baseInfo.config.js"

// build之前注释这段代码
/*import Perf from 'react-addons-perf'
window.Perf = Perf*/

// 管理redux日志，会在正式环境关闭
import {createLogger} from 'redux-logger';
const __ONERROR = {onError:(e, dispatch)=>{console.log('接口调用异常：', e);}}
const __CONFIGS = process.env && process.env.NODE_ENV !== "production" 
                  ? __.assign(__ONERROR,
                    // {onAction:createLogger()}
                    )
                  :__ONERROR;


// 1. Initialize
const app = dva(__CONFIGS);

// 2. Plugins
// app.use({});

// 3. Model
// app.model(require('./models/login'));
app.model(require('./models/account'));
app.model(require('./models/system'));

// 4. Router
app.router(require('./router'));


// 5. Start
app.start('#root');

// 添加皮肤隔离样式
document.body.className = `skin-${SITE_CUSTOM_THEME}`;

//添加换肤功能
const headerDom = document.getElementsByTagName("head")[0];
const title = headerDom.getElementsByTagName("title")[0];
const keywords = headerDom.querySelector("meta[name='keywords']");
const description = headerDom.querySelector("meta[name='description']");
const link = headerDom.querySelector("link[rel='icon']");
title.innerHTML = baseInfo.siteName;
keywords.setAttribute("content",keywords.getAttribute("content").replace(/loading/g, baseInfo.siteName));
description.setAttribute("content",description.getAttribute("content").replace(/loading/g, baseInfo.siteName));
//如果为noLogo状态，不进行渲染
if(SITE_NAME && SITE_NAME != "noLogo"){
	link.setAttribute("href",link.getAttribute("href").replace(/faviconUrl/g, baseInfo.faviconUrl));
}


