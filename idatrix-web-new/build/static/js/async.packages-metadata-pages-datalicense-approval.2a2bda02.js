(window.webpackJsonp=window.webpackJsonp||[]).push([[12],{399:function(e,a,t){"use strict";t.r(a);var n=t(18),i=t(1),c=t.n(i),o=t(49),s=t(345);a.default=Object(o.immConnect)(function(e){return{licenseList:e.get("dataLicenseModal").get("licenseList"),licenseLoading:e.get("dataLicenseModal").get("licenseLoading"),pageNum:e.get("dataLicenseModal").get("pageNum"),total:e.get("dataLicenseModal").get("total"),selectKeys:e.get("dataLicenseModal").get("selectKeys"),noAgreeLoading:e.get("dataLicenseModal").get("noAgreeLoading"),visible:e.get("dataLicenseModal").get("visible")}},function(e){return{save:function(a){e({type:"dataLicenseModal/save",payload:Object(n.a)({},a)})},getLicenselist:function(a){e({type:"dataLicenseModal/approval",payload:Object(n.a)({},a)})},batchAgree:function(a){e({type:"dataLicenseModal/batchAgree",payload:Object(n.a)({},a)})},batchNoAgree:function(a){e({type:"dataLicenseModal/batchNoAgree",payload:Object(n.a)({},a)})}}})(function(e){var a=e.licenseList,t=e.licenseLoading,n=e.pageNum,i=e.total,o=e.getLicenselist,l=e.save,g=e.selectKeys,d=e.batchAgree,L=e.batchNoAgree,r=e.noAgreeLoading,p=e.visible;return c.a.createElement(s.default,{licenseList:a,licenseLoading:t,pageNum:n,total:i,getLicenselist:o,type:"approval",save:l,selectKeys:g,batchAgree:d,visible:p,noAgreeLoading:r,batchNoAgree:L})})}}]);