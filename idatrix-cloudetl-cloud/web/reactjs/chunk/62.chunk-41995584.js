webpackJsonp([62],{461:function(t,e,r){"use strict";function a(t){return t&&t.__esModule?t:{default:t}}Object.defineProperty(e,"__esModule",{value:!0});var n=r(29),u=a(n),s=r(74),i=a(s),d=r(9),f=a(d);r(75);var c=r(584);e.default={namespace:"dataDictionModel",state:{total:0,text:"",datasource:[],visibleShow:!1,loading:!1,dictNewlist:[],doceNewlist:[],datalist:[],chenckTrue:!1,diceName:""},subscriptions:{setup:function(t){var e=t.history,r=t.dispatch;return e.listen(function(t){var e=t.pathname,a=t.query;"/gather/dataDictionary"===e&&r({type:"getList",payload:(0,f.default)({},a,{page:a.page?a.page:1,size:a.pageSize?a.pageSize:10})})})}},effects:{getList:u.default.mark(function t(e,r){var a,n,s,d=e.payload,o=r.put,l=(r.select,r.call);return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.next=2,o({type:"save",payload:{loading:!0}});case 2:return t.next=4,l(c.dataDictList,(0,f.default)({},d));case 4:if(a=t.sent,n=a.data,0!==(s=n.retCode)){t.next=12;break}return t.next=10,o({type:"save",payload:{datasource:n.data?n.data.rows:[],total:n.data?n.data.total:0,loading:!1}});case 10:t.next=13;break;case 12:i.default.error(n.message);case 13:case"end":return t.stop()}},t,this)}),getdictNewlist:u.default.mark(function t(e,r){var a,n,s,d=e.payload,o=r.put,l=(r.select,r.call);return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.next=2,o({type:"save",payload:{loading:!0}});case 2:return t.next=4,l(c.getdictNewlist,(0,f.default)({},d));case 4:if(a=t.sent,n=a.data,0!==(s=n.retCode)){t.next=12;break}return t.next=10,o({type:"save",payload:{dictNewlist:n.data.data.dictName,doceNewlist:n.data.data.dictDesc,total:n.data?n.data.count:0,loading:!1}});case 10:t.next=13;break;case 12:i.default.error(n.message);case 13:case"end":return t.stop()}},t,this)}),GetSibmitdictNew:u.default.mark(function t(e,r){var a,n,s=e.payload,d=(e.str,r.call),o=(r.select,r.put);return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.next=2,d(c.GetSibmitdictNew,(0,f.default)({},s));case 2:if(a=t.sent,n=a.data,0!==n.retCode){t.next=12;break}return i.default.success("\u4fdd\u5b58\u6210\u529f"),t.next=8,o({type:"save",payload:{dataSource:s,visibleShow:!1}});case 8:return t.next=10,o({type:"getList",payload:{datasource:s}});case 10:t.next=12;break;case 12:case"end":return t.stop()}},t,this)}),GetdictDatastatus:u.default.mark(function t(e,r){var a,n,s=e.payload,d=(e.str,r.call),o=(r.select,r.put);return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.next=2,d(c.GetdictDatastatus,(0,f.default)({},s));case 2:if(a=t.sent,n=a.data,0!==n.retCode){t.next=10;break}return i.default.success("\u64cd\u4f5c\u6210\u529f"),t.next=8,o({type:"getList",payload:{datasource:s}});case 8:t.next=11;break;case 10:n.message&&i.default.error(n.message);case 11:case"end":return t.stop()}},t,this)})},reducers:{save:function(t,e){return(0,f.default)({},t,e.payload)},getConfigId:function(t,e){return(0,f.default)({},t,e.payload)}}},t.exports=e.default},584:function(t,e,r){"use strict";function a(t){return t&&t.__esModule?t:{default:t}}Object.defineProperty(e,"__esModule",{value:!0}),e.getQuaParentPath=e.getQuaFileList=e.get_exec_resume=e.get_exec_stop=e.get_exec_pause=e.get_TransLog=e.get_TransRecords=e.Trans_exec_configuration=e.getTransExecInfo=e.getTrans_exec_id=e.getTrans_status=e.edit_stepConfigs=e.getCluster_list=e.delete_hop=e.save_stepConfigs=e.addLine=e.getServer_list=e.get_db_schema=e.get_details=e.get_input_fields=e.save_step=e.check_step_name=e.get_output_fields=e.get_FileExist=e.trans_VariablesList=e.get_SftpList=e.getDataStore=e.get_db_table_fields=e.get_db_table=e.getHadoop_list=e.getDb_list2=e.get_ProcList=e.getWebUrl=e.edit_step=e.delete_step=e.move_step=e.add_step=e.getOpen_trans=e.getTrans_list=e.getDelete_trans=e.newTrans=e.saveTransAttributes=e.checkName=e.execBatchTrans=e.editTransAttributes=e.getDefaultEngineList=e.getTransList=e.getAnalysisReportsByNode=e.getAnalysisReports=e.getDictionary=e.GetdictNew=e.analysisCsvFile=e.postdataDict=e.getdictNewlist=e.getdataDict=e.dataDictList=e.GetSibmitdictNew=e.GetdictAllList=e.GetdictData=e.GetdictDatastatus=e.Getupdate=void 0;var n=r(29),u=a(n),s=r(105),i=a(s),d=r(41),f=a(d),c=(e.Getupdate=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/analysis/dict/update.do?",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.GetdictDatastatus=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/analysis/dict/status.do?",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.GetdictData=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/analysis/dictData/update.do?",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.GetdictAllList=function(){var t=(0,f.default)(u.default.mark(function t(e,r){return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/analysis/dictDataList/"+e.name+".do?page="+e.page,"size="+e.size));case 1:case"end":return t.stop()}},t,this)}));return function(e,r){return t.apply(this,arguments)}}(),e.GetSibmitdictNew=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/analysis/dict.do?",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.dataDictList=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/analysis/dictList.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getdataDict=function(){var t=(0,f.default)(u.default.mark(function t(e){return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",requests(l.API_BASE_QUALITY+"/analysis/dataDict/"+e.name+".do"));case 1:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getdictNewlist=function(){var t=(0,f.default)(u.default.mark(function t(e){return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",requests(l.API_BASE_QUALITY+"/analysis/dict/"+e.id+".do"));case 1:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.postdataDict=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/analysis/dataDict/update.do?",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.analysisCsvFile=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/analysis/analysisCsvFile.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.GetdictNew=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/analysis/dict.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getDictionary=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=-1===e.id?v+_.getDictionaryList:e.all?v+_.getDictionaryAll.replace("{id}",e.id):v+_.getDictionary.replace("{id}",e.id),t.abrupt("return",(0,o.default)(r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getAnalysisReports=function(){var t=(0,f.default)(u.default.mark(function t(e,r){var a,n;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:a="",t.t0=r,t.next="List"===t.t0?4:"Record"===t.t0?6:"Result"===t.t0?8:10;break;case 4:return a=_.getRecordList,t.abrupt("break",11);case 6:return a=_.getRecordInfo,t.abrupt("break",11);case 8:return a=_.getResultInfo,t.abrupt("break",11);case 10:return t.abrupt("return");case 11:return n=v+a+"?"+h.default.stringify(e),t.abrupt("return",(0,o.default)(n));case 13:case"end":return t.stop()}},t,this)}));return function(e,r){return t.apply(this,arguments)}}(),e.getAnalysisReportsByNode=function(){var t=(0,f.default)(u.default.mark(function t(e){var r,a;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"GET",headers:{"Content-Type":"application/json;charset=UTF-8"},credentials:"include"},a=v+_.getAnalysisReportByNode+"?"+h.default.stringify(e),t.abrupt("return",(0,o.default)(a,r));case 3:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getTransList=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/cloud/getTransList.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getDefaultEngineList=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/cloud/getDefaultEngineList.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.editTransAttributes=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/trans/editTransAttributes.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.execBatchTrans=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/trans/execBatchTrans.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.checkName=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)({name:e})},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/trans/checkTransName.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.saveTransAttributes=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/trans/saveTransAttributes.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.newTrans=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/trans/newTrans.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getDelete_trans=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/trans/deleteTrans.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getTrans_list=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/cloud/getTransList.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getOpen_trans=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/trans/openTrans.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.add_step=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/step/addStep.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.move_step=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/step/moveStep.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.delete_step=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/step/deleteStep.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.edit_step=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/step/editStep.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getWebUrl=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/ws/getOperations.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_ProcList=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(query),t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/db/getProc.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getDb_list2=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/db/getDbList2.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getHadoop_list=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/cloud/getHadoopList.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_db_table=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/db/getDbTables.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_db_table_fields=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/db/getDbTableFields.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getDataStore=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/cloud/getDataStore.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_SftpList=function(){var t=(0,f.default)(u.default.mark(function t(){return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/cloud/getSftpList.do"));case 1:case"end":return t.stop()}},t,this)}));return function(){return t.apply(this,arguments)}}(),e.trans_VariablesList=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/cloud/getVariables.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_FileExist=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/cloud/fileExist.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_output_fields=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)({transName:e.transname,stepName:e.stepname})},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/step/getOutputFields.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.check_step_name=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)({transName:e.transname,stepName:e.stepname})},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/step/checkStepName.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.save_step=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)({transName:e.transname,stepName:e.stepname,newName:e.newname,type:e.type,description:e.description,stepParams:e.config})},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/step/saveStep.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_input_fields=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)({transName:e.transname,stepName:e.stepname})},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/step/getInputFields.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_details=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/step/getDetails.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_db_schema=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)({name:e})},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/db/getDbSchema.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getServer_list=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/cloud/getServerList.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.addLine=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/hop/addHop.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.save_stepConfigs=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/step/saveStepConfigs.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.delete_hop=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/hop/deleteHop.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getCluster_list=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/cloud/getClusterList.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.edit_stepConfigs=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/step/editStepConfigs.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getTrans_status=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/trans/getTransStatus.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getTrans_exec_id=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/trans/getExecId.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getTransExecInfo=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/trans/getExecInfo.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.Trans_exec_configuration=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/trans/execTrans.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_TransRecords=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/trans/getTransRecords.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_TransLog=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/trans/getTransLogs.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_exec_pause=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/trans/execPause.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_exec_stop=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/trans/execStop.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.get_exec_resume=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r={method:"POST",body:(0,i.default)(e)},t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/trans/execResume.do",r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getQuaFileList=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/cloud/getFileList.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),e.getQuaParentPath=function(){var t=(0,f.default)(u.default.mark(function t(e){var r;return u.default.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return r=h.default.stringify(e),t.abrupt("return",(0,o.default)(l.API_BASE_QUALITY+"/cloud/getParentPath.do?"+r));case 2:case"end":return t.stop()}},t,this)}));return function(e){return t.apply(this,arguments)}}(),r(81)),o=a(c),l=r(14),p=r(42),h=a(p),v=l.API_BASE_QUALITY,_={getRecordList:"/analysis/getRecordList",getRecordInfo:"/analysis/getRecordInfo",getResultInfo:"/analysis/getResultInfo",getAnalysisReportByNode:"/analysis/getResultInfo",getDictionary:"/analysis/dictData/{id}.do",getDictionaryList:"/analysis/dictAllList.do",getDictionaryAll:"/analysis/dictDataAllList/{id}.do"}}});