(window.webpackJsonp=window.webpackJsonp||[]).push([[7],{394:function(e,t,n){"use strict";n.r(t);n(123);var r=n(39),a=n(1),s=n.n(a),i=n(49),c=n(296);t.default=Object(i.immConnect)(function(e){return{sysList:e.get("user").get("sysList"),permits:e.get("user").get("permits")}},function(e){return{getPermitsAndDispatchWithRoutes:function(t){return function(){return e({type:"user/getPermitsAndDispatch",payload:{route:t}})}}}})(function(e){var t=e.sysList,n=e.getPermitsAndDispatchWithRoutes,a=e.permits;return s.a.createElement("div",null,s.a.createElement("p",null,"\u8fd9\u662f\u9996\u9875"),t.length>0&&t.map(function(e){var t=c.b[e.name];return s.a.createElement("p",{key:"".concat(e.name)},a[t]&&s.a.createElement("a",{href:"#/".concat(t)},t,"\uff08\u5df2\u6388\u6743\uff0c\u53ef\u76f4\u63a5\u8fdb\u5165\uff09"),!a[t]&&s.a.createElement(r.a,{onClick:n(t)},t,"\uff08\u672a\u6388\u6743\uff0c\u70b9\u51fb\u6309\u94ae\u6388\u6743\u5e76\u8fdb\u5165\uff09"))}))})}}]);