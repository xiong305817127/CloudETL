webpackJsonp([38],{456:function(t,e,r){"use strict";function n(t){return t&&t.__esModule?t:{default:t}}Object.defineProperty(e,"__esModule",{value:!0});var a=r(29),u=n(a),s=r(9),i=n(s),o=r(56),f=n(o),c=r(584),d=r(736),l=function(t){return new f.default(function(e){setTimeout(function(){e()},t)})},p={logList:[],showInfo:!1,StepMeasure:[],visible:!1,name:"",isReload:!0};e.default={namespace:"analysisInfo",state:(0,i.default)({},p),reducers:{save:function(t,e){return(0,i.default)({},t,e.payload)},clear:function(t,e){return t.logList.splice(0),t.StepMeasure.splice(0),(0,i.default)({},e.payload)}},subscriptions:{setup:function(t){var e=t.history,r=t.dispatch;e.listen(function(t){var e=t.pathname;r("/gather/qualityAnalysis/designSpace"===e?{type:"save",payload:{isReload:!0}}:{type:"save",payload:{isReload:!1}})})}},effects:{getExecuteStatus:u.default.mark(function t(e,r){var n,a,s,o,f,h,v,_,y,A,g,S,b,m,w,T,I,x,P=e.payload,L=e.status,k=r.call,E=r.put,U=r.select;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.next=2,U(function(t){return t.analysisInfo});case 2:return n=t.sent,a=n.logList,s=n.isReload,t.next=7,U(function(t){return t.designSpace});case 7:if(o=t.sent,f=o.name,h=o.view,!f||P.name===f){t.next=12;break}return t.abrupt("return");case 12:return t.next=14,k(c.getTrans_exec_id,(0,i.default)({},P));case 14:if(v=t.sent,_=v.data,y=_.retCode,A=_,0!==y){t.next=67;break}if(g=A.data.executionId){t.next=26;break}return t.next=23,E({type:"save",payload:(0,i.default)({},p)});case 23:return t.next=25,E({type:"designSpace/save",payload:{status:"Waiting"}});case 25:return t.abrupt("return");case 26:return t.next=28,k(c.getTransExecInfo,{executionId:g});case 28:if(S=t.sent,b=S.data,0!==y){t.next=65;break}return m=b.data,w=m.ExecLog,T=m.StepMeasure,I=m.StepStatus,x=w.log,x&&x.trim()&&a.push({key:jsPlumbUtil.uuid(),log:decodeURIComponent(x.trim().replace(/[\r\n]/g,""))}),t.next=37,E({type:"save",payload:{logList:a,showInfo:!0,StepMeasure:T.map(function(t,e){return t.key=e,t})}});case 37:if(!(d.runStatus.has(L)&&s&&h)){t.next=46;break}return t.next=40,k(l,1e3);case 40:return t.next=42,E({type:"getStatus",payload:P});case 42:return t.next=44,E({type:"designSpace/updateStepStatus",payload:{style:"dragNormal",StepStatus:I,status:L}});case 44:t.next=63;break;case 46:if(!d.stopStatus.has(L)){t.next=51;break}return t.next=49,E({type:"designSpace/updateStepStatus",payload:{style:"stopStyle",StepStatus:I,status:L}});case 49:t.next=63;break;case 51:if(!d.errorStatus.has(L)){t.next=56;break}return t.next=54,E({type:"designSpace/updateStepStatus",payload:{style:"errorStyle",StepStatus:I,status:L}});case 54:t.next=63;break;case 56:if(!d.pauseStatus.has(L)){t.next=61;break}return t.next=59,E({type:"designSpace/save",payload:{status:L}});case 59:t.next=63;break;case 61:return t.next=63,E({type:"designSpace/updateStepStatus",payload:{style:"success",StepStatus:I,status:L}});case 63:t.next=67;break;case 65:return t.next=67,E({type:"clear",payload:(0,i.default)({},p,P)});case 67:case"end":return t.stop()}},t,this)}),getStatus:u.default.mark(function t(e,r){var n,a,s,o,f=e.payload,d=r.put,l=r.call;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.next=2,l(c.getTrans_status,(0,i.default)({},f));case 2:if(n=t.sent,a=n.data,0!==(s=a.retCode)){t.next=9;break}return o=a.data.status,t.next=9,d({type:"getExecuteStatus",payload:f,status:o});case 9:case"end":return t.stop()}},t,this)}),initInfo:u.default.mark(function t(e,r){var n=e.payload,a=r.put;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.next=2,a({type:"clear",payload:(0,i.default)({},p,n)});case 2:return t.next=4,a({type:"getStatus",payload:n});case 4:case"end":return t.stop()}},t,this)})}},t.exports=e.default},524:function(t,e,r){t.exports={default:r(568),__esModule:!0}},527:function(t,e,r){"use strict";var n=r(30),a=r(79),u=r(164),s=r(43),i=r(163),o=r(82),f=r(160),c=r(107),d=r(171),l=r(170)("id"),p=r(109),h=r(57),v=r(172),_=r(80),y=Object.isExtensible||h,A=_?"_s":"size",g=0,S=function(t,e){if(!h(t))return"symbol"==typeof t?t:("string"==typeof t?"S":"P")+t;if(!p(t,l)){if(!y(t))return"F";if(!e)return"E";a(t,l,++g)}return"O"+t[l]},b=function(t,e){var r,n=S(e);if("F"!==n)return t._i[n];for(r=t._f;r;r=r.n)if(r.k==e)return r};t.exports={getConstructor:function(t,e,r,a){var c=t(function(t,u){i(t,c,e),t._i=n.create(null),t._f=void 0,t._l=void 0,t[A]=0,void 0!=u&&f(u,r,t[a],t)});return u(c.prototype,{clear:function(){for(var t=this,e=t._i,r=t._f;r;r=r.n)r.r=!0,r.p&&(r.p=r.p.n=void 0),delete e[r.i];t._f=t._l=void 0,t[A]=0},delete:function(t){var e=this,r=b(e,t);if(r){var n=r.n,a=r.p;delete e._i[r.i],r.r=!0,a&&(a.n=n),n&&(n.p=a),e._f==r&&(e._f=n),e._l==r&&(e._l=a),e[A]--}return!!r},forEach:function(t){for(var e,r=s(t,arguments.length>1?arguments[1]:void 0,3);e=e?e.n:this._f;)for(r(e.v,e.k,this);e&&e.r;)e=e.p},has:function(t){return!!b(this,t)}}),_&&n.setDesc(c.prototype,"size",{get:function(){return o(this[A])}}),c},def:function(t,e,r){var n,a,u=b(t,e);return u?u.v=r:(t._l=u={i:a=S(e,!0),k:e,v:r,p:n=t._l,n:void 0,r:!1},t._f||(t._f=u),n&&(n.n=u),t[A]++,"F"!==a&&(t._i[a]=u)),t},getEntry:b,setStrong:function(t,e,r){c(t,e,function(t,e){this._t=t,this._k=e,this._l=void 0},function(){for(var t=this,e=t._k,r=t._l;r&&r.r;)r=r.p;return t._t&&(t._l=r=r?r.n:t._t._f)?"keys"==e?d(0,r.k):"values"==e?d(0,r.v):d(0,[r.k,r.v]):(t._t=void 0,d(1))},r?"entries":"values",!r,!0),v(e)}}},528:function(t,e,r){"use strict";var n=r(30),a=r(25),u=r(78),s=r(108),i=r(79),o=r(164),f=r(160),c=r(163),d=r(57),l=r(83),p=r(80);t.exports=function(t,e,r,h,v,_){var y=a[t],A=y,g=v?"set":"add",S=A&&A.prototype,b={};return p&&"function"==typeof A&&(_||S.forEach&&!s(function(){(new A).entries().next()}))?(A=e(function(e,r){c(e,A,t),e._c=new y,void 0!=r&&f(r,v,e[g],e)}),n.each.call("add,clear,delete,forEach,get,has,set,keys,values,entries".split(","),function(t){var e="add"==t||"set"==t;t in S&&(!_||"clear"!=t)&&i(A.prototype,t,function(r,n){if(!e&&_&&!d(r))return"get"==t&&void 0;var a=this._c[t](0===r?0:r,n);return e?this:a})}),"size"in S&&n.setDesc(A.prototype,"size",{get:function(){return this._c.size}})):(A=h.getConstructor(e,t,v,g),o(A.prototype,r)),l(A,t),b[t]=A,u(u.G+u.W+u.F,b),_||h.setStrong(A,t,v),A}},529:function(t,e,r){var n=r(160),a=r(110);t.exports=function(t){return function(){if(a(this)!=t)throw TypeError(t+"#toJSON isn't generic");var e=[];return n(this,!1,e.push,e),e}}},568:function(t,e,r){r(167),r(168),r(169),r(569),r(570),t.exports=r(24).Map},569:function(t,e,r){"use strict";var n=r(527);r(528)("Map",function(t){return function(){return t(this,arguments.length>0?arguments[0]:void 0)}},{get:function(t){var e=n.getEntry(this,t);return e&&e.v},set:function(t,e){return n.def(this,0===t?0:t,e)}},n,!0)},570:function(t,e,r){var n=r(78);n(n.P,"Map",{toJSON:r(529)("Map")})},584:function(t,e,r){"use strict";function n(t){return t&&t.__esModule?t:{default:t}}Object.defineProperty(e,"__esModule",{value:!0}),e.getQuaParentPath=e.getQuaFileList=e.get_exec_resume=e.get_exec_stop=e.get_exec_pause=e.get_TransLog=e.get_TransRecords=e.Trans_exec_configuration=e.getTransExecInfo=e.getTrans_exec_id=e.getTrans_status=e.edit_stepConfigs=e.getCluster_list=e.delete_hop=e.save_stepConfigs=e.addLine=e.getServer_list=e.get_db_schema=e.get_details=e.get_input_fields=e.save_step=e.check_step_name=e.get_output_fields=e.get_FileExist=e.trans_VariablesList=e.get_SftpList=e.getDataStore=e.get_db_table_fields=e.get_db_table=e.getHadoop_list=e.getDb_list2=e.get_ProcList=e.getWebUrl=e.edit_step=e.delete_step=e.move_step=e.add_step=e.getOpen_trans=e.getTrans_list=e.getDelete_trans=e.newTrans=e.saveTransAttributes=e.checkName=e.execBatchTrans=e.editTransAttributes=e.getDefaultEngineList=e.getTransList=e.getAnalysisReportsByNode=e.getAnalysisReports=e.getDictionary=e.GetdictNew=e.analysisCsvFile=e.postdataDict=e.getdictNewlist=e.getdataDict=e.dataDictList=e.GetSibmitdictNew=e.GetdictAllList=e.GetdictData=e.GetdictDatastatus=e.Getupdate=void 0;var a=r(29),u=n(a),s=r(105),i=n(s),o=r(41),f=n(o),c=(e.Getupdate=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/analysis/dict/update.do?",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.GetdictDatastatus=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/analysis/dict/status.do?",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.GetdictData=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/analysis/dictData/update.do?",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.GetdictAllList=function(){var t=(0,f.default)(u.default.mark(function t(e,r){return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/analysis/dictDataList/"+e.name+".do?page="+e.page,"size="+e.size));case 1:case"end":return t.stop()}},t,this)}));return function(e,r){return t.apply(this,arguments)}}(),e.GetSibmitdictNew=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/analysis/dict.do?",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.dataDictList=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/analysis/dictList.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getdataDict=function(){var t=(0,f.default)(u.default.mark(function t(e){return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",requests(l.API_BASE_QUALITY+"/analysis/dataDict/"+e.name+".do"));case 1:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getdictNewlist=function(){var t=(0,f.default)(u.default.mark(function t(e){return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",requests(l.API_BASE_QUALITY+"/analysis/dict/"+e.id+".do"));case 1:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.postdataDict=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/analysis/dataDict/update.do?",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.analysisCsvFile=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/analysis/analysisCsvFile.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.GetdictNew=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/analysis/dict.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getDictionary=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=-1===e.id?v+_.getDictionaryList:e.all?v+_.getDictionaryAll.replace("{id}",e.id):v+_.getDictionary.replace("{id}",e.id),t.abrupt("return",(0,d.default)(r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getAnalysisReports=function(){var t=(0,f.default)(u.default.mark(function t(e,r){var n,a;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:n="",t.t0=r,t.next="List"===t.t0?4:"Record"===t.t0?6:"Result"===t.t0?8:10;break;case 4:return n=_.getRecordList,t.abrupt("break",11);case 6:return n=_.getRecordInfo,t.abrupt("break",11);case 8:return n=_.getResultInfo,t.abrupt("break",11);case 10:return t.abrupt("return");case 11:return a=v+n+"?"+h.default.stringify(e),t.abrupt("return",(0,d.default)(a));case 13:case"end":return t.stop()}},t,this)}));return function(e,r){return t.apply(this,arguments)}}(),e.getAnalysisReportsByNode=function(){var t=(0,f.default)(u.default.mark(function t(e){var r,n;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"GET",headers:{"Content-Type":"application/json;charset=UTF-8"},credentials:"include"},n=v+_.getAnalysisReportByNode+"?"+h.default.stringify(e),t.abrupt("return",(0,d.default)(n,r));case 3:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getTransList=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/cloud/getTransList.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getDefaultEngineList=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/cloud/getDefaultEngineList.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.editTransAttributes=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/trans/editTransAttributes.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.execBatchTrans=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/trans/execBatchTrans.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.checkName=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)({name:e})},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/trans/checkTransName.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.saveTransAttributes=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/trans/saveTransAttributes.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.newTrans=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/trans/newTrans.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getDelete_trans=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/trans/deleteTrans.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getTrans_list=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/cloud/getTransList.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getOpen_trans=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/trans/openTrans.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.add_step=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/step/addStep.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.move_step=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/step/moveStep.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.delete_step=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/step/deleteStep.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.edit_step=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/step/editStep.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getWebUrl=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/ws/getOperations.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_ProcList=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(query),t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/db/getProc.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getDb_list2=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/db/getDbList2.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getHadoop_list=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/cloud/getHadoopList.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_db_table=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/db/getDbTables.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_db_table_fields=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/db/getDbTableFields.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getDataStore=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/cloud/getDataStore.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_SftpList=function(){var t=(0,f.default)(u.default.mark(function t(){return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/cloud/getSftpList.do"));case 1:case"end":return t.stop()}},t,this)}));return function(){return t.apply(this,arguments)}}(),e.trans_VariablesList=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/cloud/getVariables.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_FileExist=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/cloud/fileExist.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_output_fields=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)({transName:e.transname,stepName:e.stepname})},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/step/getOutputFields.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.check_step_name=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)({transName:e.transname,stepName:e.stepname})},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/step/checkStepName.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.save_step=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)({transName:e.transname,stepName:e.stepname,newName:e.newname,type:e.type,description:e.description,stepParams:e.config})},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/step/saveStep.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_input_fields=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)({transName:e.transname,stepName:e.stepname})},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/step/getInputFields.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_details=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/step/getDetails.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_db_schema=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)({name:e})},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/db/getDbSchema.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getServer_list=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/cloud/getServerList.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.addLine=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/hop/addHop.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.save_stepConfigs=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/step/saveStepConfigs.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.delete_hop=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/hop/deleteHop.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getCluster_list=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/cloud/getClusterList.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.edit_stepConfigs=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/step/editStepConfigs.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getTrans_status=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/trans/getTransStatus.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getTrans_exec_id=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/trans/getExecId.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getTransExecInfo=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/trans/getExecInfo.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.Trans_exec_configuration=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/trans/execTrans.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_TransRecords=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/trans/getTransRecords.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_TransLog=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/trans/getTransLogs.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_exec_pause=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/trans/execPause.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_exec_stop=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/trans/execStop.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_exec_resume=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/trans/execResume.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getQuaFileList=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/cloud/getFileList.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getQuaParentPath=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,d.default)(l.API_BASE_QUALITY+"/cloud/getParentPath.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),r(81)),d=n(c),l=r(14),p=r(42),h=n(p),v=l.API_BASE_QUALITY,_={getRecordList:"/analysis/getRecordList",getRecordInfo:"/analysis/getRecordInfo",getResultInfo:"/analysis/getResultInfo",getAnalysisReportByNode:"/analysis/getResultInfo",getDictionary:"/analysis/dictData/{id}.do",getDictionaryList:"/analysis/dictAllList.do",getDictionaryAll:"/analysis/dictDataAllList/{id}.do"}},736:function(t,e,r){"use strict";Object.defineProperty(e,"__esModule",{value:!0}),e.getScreenSize=e.worktoolsType=e.disabledArgs=e.transArgs=e.errorStatus=e.stopStatus=e.pauseStatus=e.runStatus=e.finishStatus=e.initStatus=e.ERROR_POINT=e.DEFAULT_PAGE=e.DEFAULT_PAGESIZE=void 0;var n=r(524),a=function(t){return t&&t.__esModule?t:{default:t}}(n);e.DEFAULT_PAGESIZE=10,e.DEFAULT_PAGE=1,e.ERROR_POINT="\u8bf7\u6c42\u5f02\u5e38\uff0c\u8bf7\u5237\u65b0\u9875\u9762\u540e\u91cd\u8bd5\uff01",e.initStatus=new a.default([["Waiting","\u7b49\u5f85\u6267\u884c"],["Undefined","\u7b49\u5f85\u6267\u884c"]]),e.finishStatus=new a.default([["Finished","\u5b8c\u6210"]]),e.runStatus=new a.default([["Running","\u6267\u884c\u4e2d"],["Preparing executing","\u51c6\u5907\u6267\u884c"],["Initializing","\u6267\u884c\u521d\u59cb\u5316"]]),e.pauseStatus=new a.default([["Paused","\u6682\u505c"]]),e.stopStatus=new a.default([["Finished (with errors)","\u5b8c\u6210\uff08\u6709\u9519\u8bef)"],["Stopped","\u7ec8\u6b62"],["Halting","\u6302\u8d77"]]),e.errorStatus=new a.default([["TimeOut","\u8d85\u65f6"],["Failed","\u5931\u8d25"],["Unknown","\u672a\u77e5\u5f02\u5e38"]]),e.transArgs=["AccessInput","CsvInput","JsonInput","ExcelInput","TextFileInput","GetFileNames","Flattener","InsertUpdate","ScriptValueMod","DBLookup","SetVariable","SystemInfo","MergeJoin","JoinRows","SortedMerge","GetVariable","MultiwayMergeJoin","RowGenerator","MergeRows","Dummy","FilterRows","FuzzyMatch","DBProc","Validator","ConcatFields","Rest","WebServiceLookup","HTTPPOST"],e.disabledArgs=["JsonOutput","ClosureGenerator","SortRows","StreamLookup","UniqueRowsByHashSet","JoinRows"],e.worktoolsType=new a.default([["\u8d28\u91cf\u5206\u6790","12"],["\u8f93\u5165","0"]]),e.getScreenSize=function(){var t={},e=document.body.clientHeight,r=document.body.clientWidth;switch(SITE_THEME){case"government":return t.moveX=220,t.moveY=100,t;default:return e>=900&&r>=1440?(t.moveX=0,t.moveY=120):(t.moveX=0,t.moveY=0),t}}}});