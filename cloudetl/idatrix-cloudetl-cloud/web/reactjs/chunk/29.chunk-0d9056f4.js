webpackJsonp([29],{1554:function(e,t){e.exports={Content:"Content___nVN7u"}},429:function(e,t,n){"use strict";function o(e){return e&&e.__esModule?e:{default:e}}Object.defineProperty(t,"__esModule",{value:!0});var l=n(153),i=o(l),r=n(37),a=o(r),s=n(40),u=o(s),f=n(38),d=o(f),p=n(39),c=o(p),h=n(587),v=o(h);n(588);var m=n(3),y=o(m),g=n(816),b=o(g),C=n(1554),_=o(C),O=v.default.Sider,x=v.default.Content,w=function(e){function t(){var e,n,o,l;(0,a.default)(this,t);for(var r=arguments.length,s=Array(r),u=0;u<r;u++)s[u]=arguments[u];return n=o=(0,d.default)(this,(e=t.__proto__||(0,i.default)(t)).call.apply(e,[this].concat(s))),o.state={collapsed:!1,mode:"inline"},o.onCollapse=function(e){o.setState({collapsed:e,mode:e?"vertical":"inline"})},l=n,(0,d.default)(o,l)}return(0,c.default)(t,e),(0,u.default)(t,[{key:"render",value:function(){var e={list:[{name:"\u6570\u636e\u7cfb\u7edf",path:"/gather/resourcelist/DataSystem",icon:"database"},{name:"\u670d\u52a1\u5668",path:"/gather/resourcelist/Server",icon:"desktop"},{name:"\u670d\u52a1\u5668\u96c6\u7fa4",path:"/gather/resourcelist/Cluster",icon:"appstore-o"},{name:"Hadoop\u96c6\u7fa4",path:"/gather/resourcelist/HadoopCluster",icon:"exception"},{name:"Spark\u5f15\u64ce",path:"/gather/resourcelist/SparkEngine",icon:"star-o"},{name:"\u6267\u884c\u5f15\u64ce",path:"/gather/resourcelist/ExecutionEngine",icon:"trademark"},{name:"\u6587\u4ef6\u6a21\u677f\u7ba1\u7406",path:"/gather/resourcelist/FileSystem",icon:"folder"}],needAutoUrl:"/gather/resourcelist",autoShowList:["/gather/resourcelist/DataSystem","/gather/resourcelist/Server","/gather/resourcelist/Cluster","/gather/resourcelist/SparkEngine","/gather/resourcelist/ExecutionEngine","/gather/resourcelist/FileSystem"]};return y.default.createElement(v.default,{id:"ResourceList"},y.default.createElement(O,{id:"common-sidebar",collapsible:!0,collapsed:this.state.collapsed,onCollapse:this.onCollapse},y.default.createElement(b.default,{menu:e})),y.default.createElement(x,{className:_.default.Content},this.props.children))}}]),t}(y.default.Component);t.default=w,e.exports=t.default},478:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var o=n(104),l=function(e){return e&&e.__esModule?e:{default:e}}(o),i={};t.default=function(e,t){e||i[t]||((0,l.default)(!1,t),i[t]=!0)},e.exports=t.default},481:function(e,t,n){e.exports=n(1)(239)},499:function(e,t,n){"use strict";function o(e){return e&&e.__esModule?e:{default:e}}Object.defineProperty(t,"__esModule",{value:!0});var l=n(52),i=o(l),r=n(37),a=o(r),s=n(40),u=o(s),f=n(38),d=o(f),p=n(39),c=o(p),h=n(9),v=o(h),m=n(3),y=function(e){if(e&&e.__esModule)return e;var t={};if(null!=e)for(var n in e)Object.prototype.hasOwnProperty.call(e,n)&&(t[n]=e[n]);return t.default=e,t}(m),g=n(533),b=o(g),C=n(51),_=o(C),O=n(534),x=o(O),w=function(e,t){var n={},o=(0,v.default)({},e);return t.forEach(function(t){e&&t in e&&(n[t]=e[t],delete o[t])}),{picked:n,omitted:o}},M=function(e){function t(e){(0,a.default)(this,t);var n=(0,d.default)(this,(t.__proto__||Object.getPrototypeOf(t)).call(this,e));return n.onVisibleChange=function(e){var t=n.props.onVisibleChange;"visible"in n.props||n.setState({visible:!n.isNoTitle()&&e}),t&&!n.isNoTitle()&&t(e)},n.onPopupAlign=function(e,t){var o=n.getPlacements(),l=Object.keys(o).filter(function(e){return o[e].points[0]===t.points[0]&&o[e].points[1]===t.points[1]})[0];if(l){var i=e.getBoundingClientRect(),r={top:"50%",left:"50%"};l.indexOf("top")>=0||l.indexOf("Bottom")>=0?r.top=i.height-t.offset[1]+"px":(l.indexOf("Top")>=0||l.indexOf("bottom")>=0)&&(r.top=-t.offset[1]+"px"),l.indexOf("left")>=0||l.indexOf("Right")>=0?r.left=i.width-t.offset[0]+"px":(l.indexOf("right")>=0||l.indexOf("Left")>=0)&&(r.left=-t.offset[0]+"px"),e.style.transformOrigin=r.left+" "+r.top}},n.saveTooltip=function(e){n.tooltip=e},n.state={visible:!!e.visible||!!e.defaultVisible},n}return(0,c.default)(t,e),(0,u.default)(t,[{key:"componentWillReceiveProps",value:function(e){"visible"in e&&this.setState({visible:e.visible})}},{key:"getPopupDomNode",value:function(){return this.tooltip.getPopupDomNode()}},{key:"getPlacements",value:function(){var e=this.props,t=e.builtinPlacements,n=e.arrowPointAtCenter,o=e.autoAdjustOverflow;return t||(0,x.default)({arrowPointAtCenter:n,verticalArrowShift:8,autoAdjustOverflow:o})}},{key:"isHoverTrigger",value:function(){var e=this.props.trigger;return!e||"hover"===e||!!Array.isArray(e)&&e.indexOf("hover")>=0}},{key:"getDisabledCompatibleChildren",value:function(e){if((e.type.__ANT_BUTTON||"button"===e.type)&&e.props.disabled&&this.isHoverTrigger()){var t=w(e.props.style,["position","left","right","top","bottom","float","display","zIndex"]),n=t.picked,o=t.omitted,l=(0,v.default)({display:"inline-block"},n,{cursor:"not-allowed"}),i=(0,v.default)({},o,{pointerEvents:"none"}),r=(0,m.cloneElement)(e,{style:i,className:null});return y.createElement("span",{style:l,className:e.props.className},r)}return e}},{key:"isNoTitle",value:function(){var e=this.props,t=e.title,n=e.overlay;return!t&&!n}},{key:"render",value:function(){var e=this.props,t=this.state,n=e.prefixCls,o=e.title,l=e.overlay,r=e.openClassName,a=e.getPopupContainer,s=e.getTooltipContainer,u=e.children,f=t.visible;"visible"in e||!this.isNoTitle()||(f=!1);var d=this.getDisabledCompatibleChildren(y.isValidElement(u)?u:y.createElement("span",null,u)),p=d.props,c=(0,_.default)(p.className,(0,i.default)({},r||n+"-open",!0));return y.createElement(b.default,(0,v.default)({},this.props,{getTooltipContainer:a||s,ref:this.saveTooltip,builtinPlacements:this.getPlacements(),overlay:l||o||"",visible:f,onVisibleChange:this.onVisibleChange,onPopupAlign:this.onPopupAlign}),f?(0,m.cloneElement)(d,{className:c}):d)}}]),t}(y.Component);t.default=M,M.defaultProps={prefixCls:"ant-tooltip",placement:"top",transitionName:"zoom-big-fast",mouseEnterDelay:.1,mouseLeaveDelay:.1,arrowPointAtCenter:!1,autoAdjustOverflow:!0},e.exports=t.default},502:function(e,t,n){"use strict";n(71)},505:function(e,t,n){"use strict";function o(e){return e&&e.__esModule?e:{default:e}}Object.defineProperty(t,"__esModule",{value:!0});var l=n(52),i=o(l),r=n(9),a=o(r),s=n(37),u=o(s),f=n(40),d=o(f),p=n(38),c=o(p),h=n(39),v=o(h),m=n(3),y=function(e){if(e&&e.__esModule)return e;var t={};if(null!=e)for(var n in e)Object.prototype.hasOwnProperty.call(e,n)&&(t[n]=e[n]);return t.default=e,t}(m),g=n(54),b=n(481),C=o(b),_=n(53),O=o(_),x=n(51),w=o(x),M=n(521),k=o(M),S=n(478),P=o(S),E=n(531),j=o(E),N=n(532),A=o(N),T=function(e){function t(e){(0,u.default)(this,t);var n=(0,c.default)(this,(t.__proto__||Object.getPrototypeOf(t)).call(this,e));n.inlineOpenKeys=[],n.handleClick=function(e){n.handleOpenChange([]);var t=n.props.onClick;t&&t(e)},n.handleOpenChange=function(e){n.setOpenKeys(e);var t=n.props.onOpenChange;t&&t(e)},(0,P.default)(!("onOpen"in e||"onClose"in e),"`onOpen` and `onClose` are removed, please use `onOpenChange` instead, see: https://u.ant.design/menu-on-open-change."),(0,P.default)(!("inlineCollapsed"in e&&"inline"!==e.mode),"`inlineCollapsed` should only be used when Menu's `mode` is inline.");var o=void 0;return"defaultOpenKeys"in e?o=e.defaultOpenKeys:"openKeys"in e&&(o=e.openKeys),n.state={openKeys:o||[]},n}return(0,v.default)(t,e),(0,d.default)(t,[{key:"getChildContext",value:function(){return{inlineCollapsed:this.getInlineCollapsed(),antdMenuTheme:this.props.theme}}},{key:"componentWillReceiveProps",value:function(e,t){var n=this.props.prefixCls;if("inline"===this.props.mode&&"inline"!==e.mode&&(this.switchModeFromInline=!0),"openKeys"in e)return void this.setState({openKeys:e.openKeys});if(e.inlineCollapsed&&!this.props.inlineCollapsed||t.siderCollapsed&&!this.context.siderCollapsed){var o=(0,g.findDOMNode)(this);this.switchModeFromInline=!!this.state.openKeys.length&&!!o.querySelectorAll("."+n+"-submenu-open").length,this.inlineOpenKeys=this.state.openKeys,this.setState({openKeys:[]})}(!e.inlineCollapsed&&this.props.inlineCollapsed||!t.siderCollapsed&&this.context.siderCollapsed)&&(this.setState({openKeys:this.inlineOpenKeys}),this.inlineOpenKeys=[])}},{key:"setOpenKeys",value:function(e){"openKeys"in this.props||this.setState({openKeys:e})}},{key:"getRealMenuMode",value:function(){var e=this.getInlineCollapsed();if(this.switchModeFromInline&&e)return"inline";var t=this.props.mode;return e?"vertical":t}},{key:"getInlineCollapsed",value:function(){var e=this.props.inlineCollapsed;return void 0!==this.context.siderCollapsed?this.context.siderCollapsed:e}},{key:"getMenuOpenAnimation",value:function(e){var t=this,n=this.props,o=n.openAnimation,l=n.openTransitionName,i=o||l;if(void 0===o&&void 0===l)switch(e){case"horizontal":i="slide-up";break;case"vertical":case"vertical-left":case"vertical-right":this.switchModeFromInline?(i="",this.switchModeFromInline=!1):i="zoom-big";break;case"inline":i=(0,a.default)({},k.default,{leave:function(e,n){return k.default.leave(e,function(){t.switchModeFromInline=!1,t.setState({}),"vertical"!==t.getRealMenuMode()&&n()})}})}return i}},{key:"render",value:function(){var e=this.props,t=e.prefixCls,n=e.className,o=e.theme,l=this.getRealMenuMode(),r=this.getMenuOpenAnimation(l),s=(0,w.default)(n,t+"-"+o,(0,i.default)({},t+"-inline-collapsed",this.getInlineCollapsed())),u={openKeys:this.state.openKeys,onOpenChange:this.handleOpenChange,className:s,mode:l};"inline"!==l?(u.onClick=this.handleClick,u.openTransitionName=r):u.openAnimation=r;var f=this.context.collapsedWidth;return!this.getInlineCollapsed()||0!==f&&"0"!==f&&"0px"!==f?y.createElement(C.default,(0,a.default)({},this.props,u)):null}}]),t}(y.Component);t.default=T,T.Divider=b.Divider,T.Item=A.default,T.SubMenu=j.default,T.ItemGroup=b.ItemGroup,T.defaultProps={prefixCls:"ant-menu",className:"",theme:"light",focusable:!1},T.childContextTypes={inlineCollapsed:O.default.bool,antdMenuTheme:O.default.string},T.contextTypes={siderCollapsed:O.default.bool,collapsedWidth:O.default.oneOfType([O.default.number,O.default.string])},e.exports=t.default},518:function(e,t,n){e.exports=n(1)(93)},521:function(e,t,n){"use strict";function o(e){return e&&e.__esModule?e:{default:e}}function l(e,t,n){var o=void 0,l=void 0;return(0,r.default)(e,"ant-motion-collapse",{start:function(){t?(o=e.offsetHeight,e.style.height="0px",e.style.opacity="0"):(e.style.height=e.offsetHeight+"px",e.style.opacity="1")},active:function(){l&&s.default.cancel(l),l=(0,s.default)(function(){e.style.height=(t?o:0)+"px",e.style.opacity=t?"1":"0"})},end:function(){l&&s.default.cancel(l),e.style.height="",e.style.opacity="",n()}})}Object.defineProperty(t,"__esModule",{value:!0});var i=n(530),r=o(i),a=n(518),s=o(a),u={enter:function(e,t){return l(e,!0,t)},leave:function(e,t){return l(e,!1,t)},appear:function(e,t){return l(e,!0,t)}};t.default=u,e.exports=t.default},530:function(e,t,n){e.exports=n(1)(314)},531:function(e,t,n){"use strict";function o(e){return e&&e.__esModule?e:{default:e}}Object.defineProperty(t,"__esModule",{value:!0});var l=n(9),i=o(l),r=n(37),a=o(r),s=n(40),u=o(s),f=n(38),d=o(f),p=n(39),c=o(p),h=n(3),v=function(e){if(e&&e.__esModule)return e;var t={};if(null!=e)for(var n in e)Object.prototype.hasOwnProperty.call(e,n)&&(t[n]=e[n]);return t.default=e,t}(h),m=n(53),y=o(m),g=n(481),b=n(51),C=o(b),_=function(e){function t(){(0,a.default)(this,t);var e=(0,d.default)(this,(t.__proto__||Object.getPrototypeOf(t)).apply(this,arguments));return e.onKeyDown=function(t){e.subMenu.onKeyDown(t)},e.saveSubMenu=function(t){e.subMenu=t},e}return(0,c.default)(t,e),(0,u.default)(t,[{key:"render",value:function(){var e=this.props,t=e.rootPrefixCls,n=e.className,o=this.context.antdMenuTheme;return v.createElement(g.SubMenu,(0,i.default)({},this.props,{ref:this.saveSubMenu,popupClassName:(0,C.default)(t+"-"+o,n)}))}}]),t}(v.Component);_.contextTypes={antdMenuTheme:y.default.string},_.isSubMenu=1,t.default=_,e.exports=t.default},532:function(e,t,n){"use strict";function o(e){return e&&e.__esModule?e:{default:e}}Object.defineProperty(t,"__esModule",{value:!0});var l=n(9),i=o(l),r=n(37),a=o(r),s=n(40),u=o(s),f=n(38),d=o(f),p=n(39),c=o(p),h=n(3),v=function(e){if(e&&e.__esModule)return e;var t={};if(null!=e)for(var n in e)Object.prototype.hasOwnProperty.call(e,n)&&(t[n]=e[n]);return t.default=e,t}(h),m=n(481),y=n(53),g=o(y),b=n(499),C=o(b),_=function(e){function t(){(0,a.default)(this,t);var e=(0,d.default)(this,(t.__proto__||Object.getPrototypeOf(t)).apply(this,arguments));return e.onKeyDown=function(t){e.menuItem.onKeyDown(t)},e.saveMenuItem=function(t){e.menuItem=t},e}return(0,c.default)(t,e),(0,u.default)(t,[{key:"render",value:function(){var e=this.context.inlineCollapsed,t=this.props;return v.createElement(C.default,{title:e&&1===t.level?t.children:"",placement:"right",overlayClassName:t.rootPrefixCls+"-inline-collapsed-tooltip"},v.createElement(m.Item,(0,i.default)({},t,{ref:this.saveMenuItem})))}}]),t}(v.Component);_.contextTypes={inlineCollapsed:g.default.bool},_.isMenuItem=1,t.default=_,e.exports=t.default},533:function(e,t,n){e.exports=n(1)(1482)},534:function(e,t,n){"use strict";function o(e){return"boolean"==typeof e?e?s:u:(0,r.default)({},u,e)}function l(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{},t=e.arrowWidth,n=void 0===t?5:t,l=e.horizontalArrowShift,i=void 0===l?16:l,s=e.verticalArrowShift,u=void 0===s?12:s,d=e.autoAdjustOverflow,p=void 0===d||d,c={left:{points:["cr","cl"],offset:[-4,0]},right:{points:["cl","cr"],offset:[4,0]},top:{points:["bc","tc"],offset:[0,-4]},bottom:{points:["tc","bc"],offset:[0,4]},topLeft:{points:["bl","tc"],offset:[-(i+n),-4]},leftTop:{points:["tr","cl"],offset:[-4,-(u+n)]},topRight:{points:["br","tc"],offset:[i+n,-4]},rightTop:{points:["tl","cr"],offset:[4,-(u+n)]},bottomRight:{points:["tr","bc"],offset:[i+n,4]},rightBottom:{points:["bl","cr"],offset:[4,u+n]},bottomLeft:{points:["tl","bc"],offset:[-(i+n),4]},leftBottom:{points:["br","cl"],offset:[-4,u+n]}};return Object.keys(c).forEach(function(t){c[t]=e.arrowPointAtCenter?(0,r.default)({},c[t],{overflow:o(p),targetOffset:f}):(0,r.default)({},a.placements[t],{overflow:o(p)})}),c}Object.defineProperty(t,"__esModule",{value:!0});var i=n(9),r=function(e){return e&&e.__esModule?e:{default:e}}(i);t.getOverflowOptions=o,t.default=l;var a=n(535),s={adjustX:1,adjustY:1},u={adjustX:0,adjustY:0},f=[0,0]},535:function(e,t,n){"use strict";t.__esModule=!0;var o={adjustX:1,adjustY:1},l=[0,0],i=t.placements={left:{points:["cr","cl"],overflow:o,offset:[-4,0],targetOffset:l},right:{points:["cl","cr"],overflow:o,offset:[4,0],targetOffset:l},top:{points:["bc","tc"],overflow:o,offset:[0,-4],targetOffset:l},bottom:{points:["tc","bc"],overflow:o,offset:[0,4],targetOffset:l},topLeft:{points:["bl","tl"],overflow:o,offset:[0,-4],targetOffset:l},leftTop:{points:["tr","tl"],overflow:o,offset:[-4,0],targetOffset:l},topRight:{points:["br","tr"],overflow:o,offset:[0,-4],targetOffset:l},rightTop:{points:["tl","tr"],overflow:o,offset:[4,0],targetOffset:l},bottomRight:{points:["tr","br"],overflow:o,offset:[0,4],targetOffset:l},rightBottom:{points:["bl","br"],overflow:o,offset:[4,0],targetOffset:l},bottomLeft:{points:["tl","bl"],overflow:o,offset:[0,4],targetOffset:l},leftBottom:{points:["br","bl"],overflow:o,offset:[-4,0],targetOffset:l}};t.default=i},586:function(e,t,n){"use strict";n(71),n(593)},587:function(e,t,n){"use strict";function o(e){return e&&e.__esModule?e:{default:e}}Object.defineProperty(t,"__esModule",{value:!0});var l=n(607),i=o(l),r=n(608),a=o(r);i.default.Sider=a.default,t.default=i.default,e.exports=t.default},588:function(e,t,n){"use strict";n(71),n(610)},593:function(e,t){},596:function(e,t,n){"use strict";n(71),n(727),n(586)},607:function(e,t,n){"use strict";function o(e){return e&&e.__esModule?e:{default:e}}function l(e){return function(t){return function(n){function o(){return(0,p.default)(this,o),(0,m.default)(this,(o.__proto__||Object.getPrototypeOf(o)).apply(this,arguments))}return(0,g.default)(o,n),(0,h.default)(o,[{key:"render",value:function(){var n=e.prefixCls;return C.createElement(t,(0,f.default)({prefixCls:n},this.props))}}]),o}(C.Component)}}Object.defineProperty(t,"__esModule",{value:!0});var i=n(52),r=o(i),a=n(157),s=o(a),u=n(9),f=o(u),d=n(37),p=o(d),c=n(40),h=o(c),v=n(38),m=o(v),y=n(39),g=o(y),b=n(3),C=function(e){if(e&&e.__esModule)return e;var t={};if(null!=e)for(var n in e)Object.prototype.hasOwnProperty.call(e,n)&&(t[n]=e[n]);return t.default=e,t}(b),_=n(53),O=o(_),x=n(51),w=o(x),M=function(e,t){var n={};for(var o in e)Object.prototype.hasOwnProperty.call(e,o)&&t.indexOf(o)<0&&(n[o]=e[o]);if(null!=e&&"function"==typeof Object.getOwnPropertySymbols)for(var l=0,o=Object.getOwnPropertySymbols(e);l<o.length;l++)t.indexOf(o[l])<0&&(n[o[l]]=e[o[l]]);return n},k=function(e){function t(){return(0,p.default)(this,t),(0,m.default)(this,(t.__proto__||Object.getPrototypeOf(t)).apply(this,arguments))}return(0,g.default)(t,e),(0,h.default)(t,[{key:"render",value:function(){var e=this.props,t=e.prefixCls,n=e.className,o=e.children,l=M(e,["prefixCls","className","children"]),i=(0,w.default)(n,t);return C.createElement("div",(0,f.default)({className:i},l),o)}}]),t}(C.Component),S=function(e){function t(){(0,p.default)(this,t);var e=(0,m.default)(this,(t.__proto__||Object.getPrototypeOf(t)).apply(this,arguments));return e.state={siders:[]},e}return(0,g.default)(t,e),(0,h.default)(t,[{key:"getChildContext",value:function(){var e=this;return{siderHook:{addSider:function(t){e.setState({siders:[].concat((0,s.default)(e.state.siders),[t])})},removeSider:function(t){e.setState({siders:e.state.siders.filter(function(e){return e!==t})})}}}}},{key:"render",value:function(){var e=this.props,t=e.prefixCls,n=e.className,o=e.children,l=e.hasSider,i=M(e,["prefixCls","className","children","hasSider"]),a=(0,w.default)(n,t,(0,r.default)({},t+"-has-sider",l||this.state.siders.length>0));return C.createElement("div",(0,f.default)({className:a},i),o)}}]),t}(C.Component);S.childContextTypes={siderHook:O.default.object};var P=l({prefixCls:"ant-layout"})(S),E=l({prefixCls:"ant-layout-header"})(k),j=l({prefixCls:"ant-layout-footer"})(k),N=l({prefixCls:"ant-layout-content"})(k);P.Header=E,P.Footer=j,P.Content=N,t.default=P,e.exports=t.default},608:function(e,t,n){"use strict";function o(e){return e&&e.__esModule?e:{default:e}}Object.defineProperty(t,"__esModule",{value:!0});var l=n(52),i=o(l),r=n(9),a=o(r),s=n(37),u=o(s),f=n(40),d=o(f),p=n(38),c=o(p),h=n(39),v=o(h),m=n(3),y=function(e){if(e&&e.__esModule)return e;var t={};if(null!=e)for(var n in e)Object.prototype.hasOwnProperty.call(e,n)&&(t[n]=e[n]);return t.default=e,t}(m),g=n(51),b=o(g),C=n(154),_=o(C),O=n(53),x=o(O),w=n(73),M=o(w),k=n(609),S=o(k),P=function(e,t){var n={};for(var o in e)Object.prototype.hasOwnProperty.call(e,o)&&t.indexOf(o)<0&&(n[o]=e[o]);if(null!=e&&"function"==typeof Object.getOwnPropertySymbols)for(var l=0,o=Object.getOwnPropertySymbols(e);l<o.length;l++)t.indexOf(o[l])<0&&(n[o[l]]=e[o[l]]);return n};if("undefined"!=typeof window){var E=function(e){return{media:e,matches:!1,addListener:function(){},removeListener:function(){}}};window.matchMedia=window.matchMedia||E}var j={xs:"480px",sm:"576px",md:"768px",lg:"992px",xl:"1200px",xxl:"1600px"},N=function(){var e=0;return function(){var t=arguments.length>0&&void 0!==arguments[0]?arguments[0]:"";return e+=1,""+t+e}}(),A=function(e){function t(e){(0,u.default)(this,t);var n=(0,c.default)(this,(t.__proto__||Object.getPrototypeOf(t)).call(this,e));n.responsiveHandler=function(e){n.setState({below:e.matches});var t=n.props.onBreakpoint;t&&t(e.matches),n.state.collapsed!==e.matches&&n.setCollapsed(e.matches,"responsive")},n.setCollapsed=function(e,t){"collapsed"in n.props||n.setState({collapsed:e});var o=n.props.onCollapse;o&&o(e,t)},n.toggle=function(){var e=!n.state.collapsed;n.setCollapsed(e,"clickTrigger")},n.belowShowChange=function(){n.setState({belowShow:!n.state.belowShow})},n.uniqueId=N("ant-sider-");var o=void 0;"undefined"!=typeof window&&(o=window.matchMedia),o&&e.breakpoint&&e.breakpoint in j&&(n.mql=o("(max-width: "+j[e.breakpoint]+")"));var l=void 0;return l="collapsed"in e?e.collapsed:e.defaultCollapsed,n.state={collapsed:l,below:!1},n}return(0,v.default)(t,e),(0,d.default)(t,[{key:"getChildContext",value:function(){return{siderCollapsed:this.state.collapsed,collapsedWidth:this.props.collapsedWidth}}},{key:"componentWillReceiveProps",value:function(e){"collapsed"in e&&this.setState({collapsed:e.collapsed})}},{key:"componentDidMount",value:function(){this.mql&&(this.mql.addListener(this.responsiveHandler),this.responsiveHandler(this.mql)),this.context.siderHook&&this.context.siderHook.addSider(this.uniqueId)}},{key:"componentWillUnmount",value:function(){this.mql&&this.mql.removeListener(this.responsiveHandler),this.context.siderHook&&this.context.siderHook.removeSider(this.uniqueId)}},{key:"render",value:function(){var e,t=this.props,n=t.prefixCls,o=t.className,l=t.theme,r=t.collapsible,s=t.reverseArrow,u=t.trigger,f=t.style,d=t.width,p=t.collapsedWidth,c=P(t,["prefixCls","className","theme","collapsible","reverseArrow","trigger","style","width","collapsedWidth"]),h=(0,_.default)(c,["collapsed","defaultCollapsed","onCollapse","breakpoint","onBreakpoint"]),v=this.state.collapsed?p:d,m=(0,S.default)(v)?v+"px":String(v),g=0===parseFloat(String(p||0))?y.createElement("span",{onClick:this.toggle,className:n+"-zero-width-trigger"},y.createElement(M.default,{type:"bars"})):null,C={expanded:s?y.createElement(M.default,{type:"right"}):y.createElement(M.default,{type:"left"}),collapsed:s?y.createElement(M.default,{type:"left"}):y.createElement(M.default,{type:"right"})},O=this.state.collapsed?"collapsed":"expanded",x=C[O],w=null!==u?g||y.createElement("div",{className:n+"-trigger",onClick:this.toggle,style:{width:m}},u||x):null,k=(0,a.default)({},f,{flex:"0 0 "+m,maxWidth:m,minWidth:m,width:m}),E=(0,b.default)(o,n,n+"-"+l,(e={},(0,i.default)(e,n+"-collapsed",!!this.state.collapsed),(0,i.default)(e,n+"-has-trigger",r&&null!==u&&!g),(0,i.default)(e,n+"-below",!!this.state.below),(0,i.default)(e,n+"-zero-width",0===parseFloat(m)),e));return y.createElement("div",(0,a.default)({className:E},h,{style:k}),y.createElement("div",{className:n+"-children"},this.props.children),r||this.state.below&&g?w:null)}}]),t}(y.Component);t.default=A,A.__ANT_LAYOUT_SIDER=!0,A.defaultProps={prefixCls:"ant-layout-sider",collapsible:!1,defaultCollapsed:!1,reverseArrow:!1,width:200,collapsedWidth:80,style:{},theme:"dark"},A.childContextTypes={siderCollapsed:x.default.bool,collapsedWidth:x.default.oneOfType([x.default.number,x.default.string])},A.contextTypes={siderHook:x.default.object},e.exports=t.default},609:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var o=function(e){return!isNaN(parseFloat(e))&&isFinite(e)};t.default=o,e.exports=t.default},610:function(e,t){},727:function(e,t){},796:function(e,t,n){"use strict";function o(e){return e&&e.__esModule?e:{default:e}}function l(e,t,n){for(var o="",l=e;l.length>0&&!o;)k.STANDALONE_ETL?o=l[0]:function(){var e=t.find(function(e){return e.path===l[0]});if(e&&e.empowerApi){var i=n.find(function(t){return t.url===e.empowerApi});i&&i.isShow?o=l[0]:l.splice(0,1)}else o=l[0]}();return o&&(window.location.href="#"+o),o}function i(e,t,n,o){var r=arguments.length>4&&void 0!==arguments[4]?arguments[4]:"",a=t.list,s=t.autoShowList,u=t.needAutoUrl,f=r;return a.forEach(function(t){Array.isArray(t.list)?f=i(e,t,n,o,f):0===e.indexOf(t.path)&&t.path.length>f.length&&(f=t.path)}),s&&s.length>0&&e===u&&(f=l(s,o,n)),f}Object.defineProperty(t,"__esModule",{value:!0});var r=n(73),a=o(r),s=n(153),u=o(s),f=n(37),d=o(f),p=n(40),c=o(p),h=n(38),v=o(h),m=n(39),y=o(m),g=n(505),b=o(g);n(502),n(596);var C=n(3),_=o(C),O=n(152),x=n(162),w=n(53),M=o(w),k=n(14),S=b.default.SubMenu,P=function(e){function t(){var e,n,o,l;(0,d.default)(this,t);for(var i=arguments.length,r=Array(i),a=0;a<i;a++)r[a]=arguments[a];return n=o=(0,v.default)(this,(e=t.__proto__||(0,u.default)(t)).call.apply(e,[this].concat(r))),o.rootSubmenuKeys=[],o.state={selectedKey:"",openKeys:[],permitsList:[]},o.onOpenChange=function(e){var t=e.find(function(e){return-1===o.state.openKeys.indexOf(e)});-1===o.rootSubmenuKeys.indexOf(t)?o.setState({openKeys:e}):o.setState({openKeys:t?[t]:[]})},l=n,(0,v.default)(o,l)}return(0,y.default)(t,e),(0,c.default)(t,[{key:"componentWillMount",value:function(){this.updateStateByProps(this.props)}},{key:"componentWillReceiveProps",value:function(e){this.updateStateByProps(e)}},{key:"updateStateByProps",value:function(e){var t=this,n=e.system,o=e.menu,l=n.routesConfig,r=n.permits[n.currentSystemId]||[];this.rootSubmenuKeys.splice(0),o.list.map(function(e){t.rootSubmenuKeys.push(e.path)});var a=i(n.pathname,o,r,l);this.setState({selectedKey:a,permitsList:r})}},{key:"createMenu",value:function(e){var t=this,n=this.state.permitsList,o=this.props.system.routesConfig;return e.map(function(e,l){if(Array.isArray(e.list)){var i=t.createMenu(e.list).filter(function(e){return null!==e});return i.length>0?_.default.createElement(S,{key:e.path,title:_.default.createElement("span",null,_.default.createElement(a.default,{type:e.icon}),_.default.createElement("span",null,e.title))},i):null}if(!k.STANDALONE_ETL){var r=o.find(function(t){return t.path===e.path});if(r&&r.empowerApi){var s=n.find(function(e){return e.url===r.empowerApi});return s&&s.isShow?_.default.createElement(b.default.Item,{key:e.path},_.default.createElement(x.Link,{to:e.path},_.default.createElement(a.default,{type:e.icon}),_.default.createElement("span",{className:"nav-text"},s.name))):null}}return _.default.createElement(b.default.Item,{key:e.path},_.default.createElement(x.Link,{to:e.path},_.default.createElement(a.default,{type:e.icon}),_.default.createElement("span",{className:"nav-text"},e.name)))})}},{key:"render",value:function(){var e=this.props.menu,t=this.state,n=t.selectedKey,o=t.openKeys;return _.default.createElement(b.default,{mode:"inline",openKeys:o,onOpenChange:this.onOpenChange,selectedKeys:[n],ref:"menu",inlineCollapsed:this.props.collapsed},this.createMenu(e.list))}}]),t}(_.default.Component);P.propTypes={menu:M.default.object.isRequired},t.default=(0,O.connect)(function(e){return{system:e.system}})(P),e.exports=t.default},816:function(e,t,n){"use strict";function o(e){return e&&e.__esModule?e:{default:e}}Object.defineProperty(t,"__esModule",{value:!0});var l=n(153),i=o(l),r=n(37),a=o(r),s=n(40),u=o(s),f=n(38),d=o(f),p=n(39),c=o(p),h=n(3),v=o(h),m=n(796),y=o(m),g=n(817),b=(o(g),function(e){function t(){return(0,a.default)(this,t),(0,d.default)(this,(t.__proto__||(0,i.default)(t)).apply(this,arguments))}return(0,c.default)(t,e),(0,u.default)(t,[{key:"render",value:function(){return v.default.createElement(y.default,{menu:this.props.menu})}}]),t}(v.default.Component));t.default=b,e.exports=t.default},817:function(e,t){}});