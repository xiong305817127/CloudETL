webpackJsonp([67],{442:function(e,t,u){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var s=u(9),n=function(e){return e&&e.__esModule?e:{default:e}}(s);t.default={namespace:"infostep",state:{executionId:"",stepMeasure:[]},reducers:{printStep:function(e,t){return t.stepMeasure&&t.stepMeasure.length>0?(0,n.default)({},e,{executionId:t.executionId,stepMeasure:t.stepMeasure}):e},cleanStep:function(e,t){return e.stepMeasure.splice(0),{executionId:"",stepMeasure:[]}}}},e.exports=t.default}});