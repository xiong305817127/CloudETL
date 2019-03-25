import { connect } from "dva";
import { message, Select } from "antd";
import "./DomItems/ItemsStyle.css";
// import {
//   getDb_list2,
//   getHadoop_list,
//   get_db_table,
//   get_db_table_fields,
//   getDefaultEngineList,
//   getDataStore,
//   getTrans_list,
//   get_SftpList,
//   trans_VariablesList,
//   get_FileExist,
//   get_output_fields,
//   check_step_name,
//   save_step,
//   get_input_fields,
//   get_details,
//   get_db_schema,
//   getServer_list,
//   getProcList,
//   get_mappings,
//   get_mappingInfo,
//   get_createMapping,
//   get_deleteMapping,
//   getDictionary
// } from "../../../../services/gather";
import * as gatherFuncs from "../../../../services/gather";
import * as gatherFuncsJob from "../../../../services/gather1";
import { CurryFunc } from "../../../../utils/utils";
import Desensitization from "./DomItems/Change/Desensitization"; // 编码字符 pwj
import TableInput from "./DomItems/Input/TableInput";
import TableOutput from "./DomItems/Output/TableOutput";
import Others from "./DomItems/Others";
import Unknown from "./DomItems/Unknown";
import Shell from "./DomItems/Script/Shell";
import ExecSQL from "./DomItems/Script/ExecSQL";
import ScriptValueMod from "./DomItems/Script/ScriptValueMod";
import Formula from "./DomItems/Script/Formula";
import TextFileOutput from "./DomItems/Output/TextFileOutput";
import TextFileInput from "./DomItems/Input/TextFileInput";
import InsertUpdate from "./DomItems/Output/InsertUpdate";
import GetFileNames from "./DomItems/Input/GetFileNames";
import CsvInput from "./DomItems/Input/CsvInput";
import AccessInput from "./DomItems/Input/AccessInput";
import ExcelInput from "./DomItems/Input/ExcelInput";
import HadoopFileInputPlugin from "./DomItems/BigData/HadoopFileInputPlugin";
import HadoopFileOutputPlugin from "./DomItems/BigData/HadoopFileOutputPlugin";
import ClosureGenerator from "./DomItems/Change/ClosureGenerator";
import GetSlaveSequence from "./DomItems/Change/GetSlaveSequence";
import ValueMapper from "./DomItems/Change/ValueMapper";
import SplitFieldToRows3 from "./DomItems/Change/SplitFieldToRows3";
import Denormaliser from "./DomItems/Change/Denormaliser";
import StringCut from "./DomItems/Change/StringCut";
import ConcatFields from "./DomItems/Change/ConcatFields";
import Unique from "./DomItems/Change/Unique";
import UniqueRowsByHashSet from "./DomItems/Change/UniqueRowsByHashSet";
import Constant from "./DomItems/Change/Constant";
import Sequence from "./DomItems/Change/Sequence";
import CheckSum from "./DomItems/Change/CheckSum";
import SelectValues from "./DomItems/Change/SelectValues";
import StringOperations from "./DomItems/Change/StringOperations";
import ReplaceString from "./DomItems/Change/ReplaceString";
import SetValueConstant from "./DomItems/Change/SetValueConstant";
import FieldSplitter from "./DomItems/Change/FieldSplitter";
import SortRows from "./DomItems/Change/SortRows";
import FilterRows from "./DomItems/Change/FilterRows"; //过滤记录
import SetValueField from "./DomItems/Change/SetValueField";
import Calculator from "./DomItems/Change/Calculator";
import Normaliser from "./DomItems/Change/Normaliser";
import Flattener from "./DomItems/Change/Flattener";
import FieldsChangeSequence from "./DomItems/Change/FieldsChangeSequence";
import NumberRange from "./DomItems/Change/NumberRange";
import AddXML from "./DomItems/Change/AddXML";
import ElasticSearchBulk from "./DomItems/ElasticSearchBulkInsert/ElasticSearchBulk";
import HBaseOutput from "./DomItems/BigData/HBaseOutput";
import HBaseInput from "./DomItems/BigData/HBaseInput";
import StreamLookup from "./DomItems/InQuery/StreamLookup";
import SwitchCase from "./DomItems/Process/SwitchCase";
import GroupBy from "./DomItems/Test/GroupBy";
import Validator from "./DomItems/Others/Validator";
import JsonInput from "./DomItems/Input/JsonInput";
import JsonOutput from "./DomItems/Output/JsonOutput";
import HttpClient from "./DomItems/Search/HttpClient";
import HttpPost from "./DomItems/Search/HttpPost";
import RestClient from "./DomItems/Search/RestClient";
import WebServiceLookup from "./DomItems/Search/WebServiceLookup";
import FuzzyMatch from "./DomItems/Search/FuzzyMatch";
import DBProc from "./DomItems/Search/DBProc";
import DynamicSQLRow from "./DomItems/Search/DynamicSQLRow";
import ParquetInput from "./DomItems/BigData/ParquetInput";
import ParquetOutput from "./DomItems/BigData/ParquetOutput";

//正则表达式 RegexEval
import RegexEval from "./DomItems/Script/RegexEval";
//Excel输出
import ExcelOutput from "./DomItems/Output/ExcelOutput";

// 质量分析
import CharacterAnalysis from "./DomItems/QC/CharacterAnalysis";
import NumberAnalysis from "./DomItems/QC/NumberAnalysis";
import CertificatesAnalysis from "./DomItems/QC/CertificatesAnalysis";
import DateAnalysis from "./DomItems/QC/DateAnalysis";
// import CustomAnalysis from "./DomItems/QC/CustomAnalysis"
import AnalysisReport from "./DomItems/QC/AnalysisReport";

//卡夫卡组件
import KafkaConsumerInput from "./DomItems/Stream/KafkaConsumerInput";
import KafkaProducerOutput from "./DomItems/Stream/KafkaProducerOutput";
import RecordsFromStream from "./DomItems/Stream/RecordsFromStream";

import ReadContentInput from "./DomItems/Change/ReadContentInput";

//测试
import ModelTest from "./DomItems/Test/ModelTest";

import DBLookup from "./DomItems/Change/DBLookup"; //数据库查询
import SetVariable from "./DomItems/Change/SetVariable"; //设置变量
import SystemInfo from "./DomItems/Change/SystemInfo"; //获取系统信息
import MergeJoin from "./DomItems/Change/MergeJoin"; //记录集连接
import JoinRows from "./DomItems/Change/JoinRows"; //记录关联 (笛卡尔输出)
import SortedMerge from "./DomItems/Change/SortedMerge"; //排序合并
import GetVariable from "./DomItems/Change/GetVariable"; //MultiwayMergeJoin
import MultiwayMergeJoin from "./DomItems/Change/MultiwayMergeJoin"; //MultiwayMergeJoin
import RowGenerator from "./DomItems/Change/RowGenerator"; //生成记录
import MergeRows from "./DomItems/Change/MergeRows"; //合并记录
import Dummy from "./DomItems/Change/Dummy"; //空操作
import START from "./JobItem/General/START"; //通用
import HadoopCopyFilesPlugin from "./JobItem/BigData/HadoopCopyFilesPlugin"; //BigData
import SqoopImport from "./JobItem/BigData/SqoopImport"; //BigData
import SqoopExport from "./JobItem/BigData/SqoopExport"; //BigData
import Variables from "./JobItem/General/SET_VARIABLES"; //设置变量
import DELAY from "./JobItem/General/DELAY"; //等待
import SIMPLE_EVAL from "./JobItem/Condition/SIMPLE_EVAL"; //校验字段的值
import SUCCESS from "./JobItem/General/SUCCESS"; //成功
import SFTP from "./JobItem/FileTranster/SFTP"; //下载
import SFTPPUT from "./JobItem/FileTranster/SFTPPUT"; //上传
import EVAL from "./JobItem/Script/EVAL"; //javascirpt
import COPY_FILES from "./JobItem/FileManagement/COPY_FILES"; //文件管理
import JOB from "./JobItem/General/JOB"; //作业
import TRANS from "./JobItem/General/TRANS"; //作业
import DUMMY from "./JobItem/General/DUMMY"; //作业
import { O_NONBLOCK } from "constants";

let Timer;
const DomShow = ({ items, dispatch, cloudetlCommon, owner }) => {
  const CurryFuncList = CurryFunc(gatherFuncs, { owner: owner });
  const CurryFuncListJob = CurryFunc(gatherFuncsJob, { owner: owner });

  const {
    getDb_list2, // obj
    getHadoop_list, // obj
    get_db_table, // obj
    get_db_table_fields, // obj
    getDefaultEngineList, // obj
    getDataStore, // obj
    getTrans_list, //obj
    get_SftpList, // get
    trans_VariablesList, // obj
    get_FileExist, // obj
    get_output_fields, // obj
    check_step_name, // obj
    save_step, // obj
    get_input_fields, // obj
    get_details, // obj
    get_db_schema, // url-> obj
    getServer_list, // obj
    get_ProcList, // obj
    get_mappings, // obj -> deperated
    get_mappingInfo, // obj->deperated
    get_createMapping, // obj->deperated
    get_deleteMapping, // obj->deperated
		getDictionary, // obj->deperated
		getHdfsRoots
  } = CurryFuncList;

  const {
    check_entry_name,
    save_entry,
    getJobDetailsList,
    getJob_list
  } = CurryFuncListJob;

  const { isMetacube } = cloudetlCommon;

  // 获取字典
  const GetDic = (obj, cb) => {
    getDictionary(obj).then(res => {
      const { code } = res.data;

      if (code === "200") {
        cb(res.data.data);
      }
    });
  };

  /*获得数据库列表*/
  const selectOption = callback => {
    getDb_list2().then(res => {
      const { code } = res.data;
      if (code === "200" && res.data.data && res.data.data instanceof Array) {
        let data = res.data.data.map(r => ({
          name: r.name,
          type: r.type,
          id: r.databaseId,
          owner: r.owner
        }));
        callback(data);
      }
    });
  };
  /*获得模式名*/
  const getSchema = (name, callback) => {
    get_db_schema(name).then(res => {
      const { code, data } = res.data;
      if (code === "200" && data && data instanceof Array) {
				callback(data);
      }
    });
  };

  /*获得表名*/
  const getDbTable = (obj, callback) => {
    get_db_table(obj).then(res => {
      const { code, data } = res.data;
      if (code === "200" && data) {
        const { tableList, viewList } = data;
        let selectArgs = [];
        if (tableList && tableList instanceof Array) {
          selectArgs.push(tableList);
        }
        if (viewList && viewList instanceof Array) {
          selectArgs.push(viewList);
        }
        callback(selectArgs);
      }
    });
  };
  /*获得字段名*/
  const getDbFields = (obj, callback) => {
    get_db_table_fields(obj).then(res => {
      const { code,data } = res.data;
      if (code === "200" && data instanceof Array ) {
        let args = [];
				data.forEach(index=>{
					args.push({
						key: index.fieldName,
            name: index.fieldName,
            type: index.fieldType
					})
				})
        callback(args);
      }
    });
  };

  const getVariables = (obj, callback) => {
    trans_VariablesList(obj).then(res => {
      const { code, data } = res.data;
      console.log(code, data, "code,data");
      if (code === "200") {
        callback(data);
      }
    });
  };

  const saveStep = (obj, key, callback) => {
    save_step(obj).then(res => {
      if (res.data.code === "200") {
        if (obj.newname) {
          dispatch({
            type: "transspace/changeItemName",
            text: obj.newname,
            key: key
          });
        }
        message.success("保存节点成功");
      }
      callback(res.data);
    });
  };

  /*获得output*/
  const getOutFields = (obj, callback) => {
    get_output_fields(obj).then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        callback(data);
      }
    });
  };

  /*获得input*/
  const getInputFields = (obj, callback) => {
    get_input_fields(obj).then(res => {
      const { code, data } = res.data;
      console.log(code, data, "code, data");
      if (code === "200") {
        callback(data);
      }
    });
  };

  /*检测转换文件名*/
  const handleCheckName = (rule, value, callback) => {
    const { transname, text } = items;
    if (value && value != text) {
      if (Timer) {
        clearTimeout(Timer);
        Timer = null;
      }
      Timer = setTimeout(() => {
        let obj = {};
        obj.stepname = value;
        obj.transname = transname;
        check_step_name(obj).then(res => {
          const { code, data } = res.data;
          if (code === "200") {
            const { existed } = data;
            if (existed === true) {
              callback(true);
            } else {
              callback();
            }
          }
        });
      }, 300);
    } else {
      callback();
    }
  };

  /*获得详情*/
  const getDetails = (obj, callback) => {
    get_details(obj).then(res => {
      const { code, data, message } = res.data;
      if (code === "200") {
        callback(data, message);
      }
    });
  };

  /*格式化表格*/
  const formatTable = (obj, obj1) => {
    let args = [];
    for (let index of obj) {
      let obj = {};
      for (let index1 of obj1) {
        if (
          Object.prototype.toString.call(index[index1]) !== "[object Null]" &&
          typeof index[index1] !== "undefined" &&
          index[index1].toString().trim()
        ) {
          obj[index1] = index[index1];
        }
      }
      if (JSON.stringify(obj) != "{}") {
        args.push(obj);
      }
    }

    return args;
  };

  /*获取hadoop*/
  const getHadoopServer = callback => {
    getHadoop_list().then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        callback(data);
      }
    });
  };

  /*检验文件夹是否存在*/
  const getFileExist = (obj, callback) => {
    get_FileExist(obj).then(res => {
      const { code } = res.data;
      if (code === "200") {
        callback(code);
      }
    });
  };

  //获得服务器
  const getServerList = callback => {
    getServer_list().then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        callback(data);
      }
    });
  };

  /*格式化文件夹*/
  const formatFolder = data => {
    let str = "";
    if (data) {
      let num = data.lastIndexOf("/");
      if (num === -1) {
        str = "";
      } else {
        str = data.substring(0, num + 1);
      }
    }
    return str;
  };
  //获取trans列表
  const getTransList = callback => {
    getTrans_list({ isOnlyName: true }).then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        callback(data);
      }
    });
  };
  //获取运行配置
  const getEngineList = callback => {
    getDefaultEngineList().then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        callback(data);
      }
    });
  };

  //获取存储过程名称
  const getProcList = callback => {
    get_ProcList().then(res => {
      console.log(res, "getProcList");
      const { code, data } = res.data;
      if (code === "200") {
        callback(data);
      }
    });
  };

  //获得下拉框
  const getInputSelect = (data, type, symbol) => {
    let args = [];
    let sign = symbol || "";
    for (let index of data) {
      args.push(
        <Select.Option
          key={type ? index[type] : index}
          value={sign + (type ? index[type] : index)}
        >
          {sign + (type ? index[type] : index)}
        </Select.Option>
      );
    }
    return args;
  };

  //获得下拉框 Multiway Merge Join组件专用
  const getInputSelectMultiway = (symbol, symbol1, data, type, symbol2) => {
    let args = [];
    let sign = symbol || "";
    let sign1 = symbol1 || "";
    let sign2 = symbol2 || "";
    for (let index of data) {
      args.push(
        <Select.Option
          key={type ? index[type] : index}
          value={sign + sign1 + (type ? index[type] : index) + sign2}
        >
          {sign + sign1 + (type ? index[type] : index) + sign2}
        </Select.Option>
      );
    }
    return args;
  };

  //获得服务器目录
  const get_DataStore = (obj, callback) => {
    getDataStore(obj).then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        callback(data);
      }
    });
  };

  //简易的匹配,输出
  const get_Similarity = (args1, args2, source, target, str) => {
    let field = "name";
    // let fieldArgs = [];
    if (str) {
      field = str;
    }

    let newArgs = args1.map(item => {
      for (let index of args2) {
        if (
          index[field].toUpperCase() === item[source].toUpperCase() ||
          index[field].toUpperCase().indexOf(item[source].toUpperCase()) !==
            -1 ||
          item[source].toUpperCase().indexOf(index[field].toUpperCase()) !== -1
        ) {
          item[target] = index[field];
          return item;
        }
      }
      return item;
    });
    return newArgs;
	};
	
	//获得HDFS跟路径
  const get_HDFSRoots = (obj, callback) => {
    getHdfsRoots(obj).then(res => {
      const { code, data } = res.data;
      if (code === "200" && data && data instanceof Array) {
        callback(data);
      }
    });
  };

  /*job*/
  /*获得详情*/
  const getJobDetails = (obj, callback) => {
    getJobDetailsList(obj).then(res => {
      const { code, data } = res.data;
      if (res.data.msg === "success") {
        message.success("测试成功！");
      }

      if (code === "200") {
        callback(data);
      }
    });
  };

  /*检测路径*/
  const handleCheckPathName = (rule, value, callback) => {
    if (value) {
      if (value.indexOf("/") < 0) {
        callback(true);
      }
      callback();
    } else {
      callback();
    }
  };

  /*检测job文件名*/
  const handleCheckJobName = (rule, value, callback) => {
    const { transname, text } = items;
    if (value && value != text) {
      if (Timer) {
        clearTimeout(Timer);
        Timer = null;
      }
      Timer = setTimeout(() => {
        let obj = {};
        obj.jobName = transname;
        obj.entryName = value;
        check_entry_name(obj).then(res => {
          const { code, data } = res.data;
          if (code === "200") {
            const { existed } = data;
            if (existed === true) {
              callback(true);
            } else {
              callback();
            }
          }
        });
      }, 300);
    } else {
      callback();
    }
  };
  const saveEntry = (obj, key, callback) => {
    save_entry(obj).then(res => {
      if (res.data.code === "200") {
        if (obj.newName) {
          dispatch({
            type: "jobspace/changeItemName",
            text: obj.newName,
            key: key
          });
        }
        message.success("保存节点成功");
      }
      callback(res.data);
    });
  };

  const getJobList = callback => {
    getJob_list({ isOnlyName: true }).then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        callback(data);
      }
    });
  };

  const getSftpList = callback => {
    get_SftpList().then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        callback(data);
      }
    });
  };

  const initDefaultObj = (initObj, defaultObj) => {
    let obj = initObj;
    let args = Object.keys(initObj);

    for (let index of args) {
      obj[index] = defaultObj[index];
    }

    return obj;
  };

  const objToArgs = obj => {
    if (!obj) {
      return [];
    }

    let initArgs = Object.keys(obj);

    let dataSource = [];

    let count = 0;
    for (let index of initArgs) {
      dataSource.push({
        key: count,
        args: index,
        value: obj[index]
      });
      count++;
    }

    return dataSource;
  };

  /*trans*/
  items.selectOption = selectOption;
  items.getDbTable = getDbTable;
  items.getDbFields = getDbFields;
  items.getOutFields = getOutFields;
  items.handleCheckName = handleCheckName;
  items.saveStep = saveStep;
  items.getInputFields = getInputFields;
  items.getDetails = getDetails;
  items.formatTable = formatTable;
  items.getSchema = getSchema;
  items.getHadoopServer = getHadoopServer;
  items.getFileExist = getFileExist;
  items.formatFolder = formatFolder;
  items.getServerList = getServerList;
  items.getTransList = getTransList;
  items.getEngineList = getEngineList;
  items.getInputSelect = getInputSelect;
  items.getDataStore = get_DataStore;
  items.getVariables = getVariables;
  items.isMetacube = isMetacube;
  items.getProcList = getProcList;
  items.getInputSelectMultiway = getInputSelectMultiway;
  items.get_Similarity = get_Similarity;
  items.get_HDFSRoots = get_HDFSRoots;

  /*job*/
  items.handleCheckJobName = handleCheckJobName;
  items.saveEntry = saveEntry;
  items.getJobDetails = getJobDetails;
  items.getJobList = getJobList;
  items.getSftpList = getSftpList;
  items.initDefaultObj = initDefaultObj;
  items.objToArgs = objToArgs;

  /*检测路径*/
  items.handleCheckPathName = handleCheckPathName;

  /**字典 */
	items.GetDic = GetDic;
	items.owner = owner;

  switch (items.panel) {
    case "CharacterAnalysis":
      return <CharacterAnalysis model={items} />;
    case "NumberAnalysis":
      return <NumberAnalysis model={items} />;
    case "CertificatesAnalysis":
      return <CertificatesAnalysis model={items} />;
    case "DateAnalysis":
      return <DateAnalysis model={items} />;
    case "AnalysisReport":
      return <AnalysisReport model={items} />;
    case "TableInput":
      return <TableInput model={items} />;
    case "TableOutput":
      return <TableOutput model={items} />;
    case "ExecSQL":
      return <ExecSQL model={items} />;
    case "ScriptValueMod":
      return <ScriptValueMod model={items} />;
    case "TextFileOutput":
      return <TextFileOutput model={items} />;
    case "TextFileInput":
      return <TextFileInput model={items} />;
    case "InsertUpdate":
      return <InsertUpdate model={items} />;
    case "GetFileNames":
      return <GetFileNames model={items} />;
    case "CsvInput":
      return <CsvInput model={items} />;
    case "AccessInput":
      return <AccessInput model={items} />;
    case "ExcelInput":
      return <ExcelInput model={items} />;
    case "HadoopFileInputPlugin":
      return <HadoopFileInputPlugin model={items} />;
    case "HadoopFileOutputPlugin":
      return <HadoopFileOutputPlugin model={items} />;
    case "ClosureGenerator":
      return <ClosureGenerator model={items} />;
    case "GetSlaveSequence":
      return <GetSlaveSequence model={items} />;
    case "ValueMapper":
      return <ValueMapper model={items} />;
    case "SplitFieldToRows3":
      return <SplitFieldToRows3 model={items} />;
    case "Denormaliser":
      return <Denormaliser model={items} />;
    case "StringCut":
      return <StringCut model={items} />;
    case "ConcatFields":
      return <ConcatFields model={items} />;
    case "Unique":
      return <Unique model={items} />;
    case "UniqueRowsByHashSet":
      return <UniqueRowsByHashSet model={items} />;
    case "Constant":
      return <Constant model={items} />;
    case "Sequence":
      return <Sequence model={items} />;
    case "CheckSum":
      return <CheckSum model={items} />;
    case "SelectValues":
      return <SelectValues model={items} />;
    case "StringOperations":
      return <StringOperations model={items} />;
    case "ReplaceString":
      return <ReplaceString model={items} />;
    case "SetValueConstant":
      return <SetValueConstant model={items} />;
    case "FieldSplitter":
      return <FieldSplitter model={items} />;
    case "SortRows":
      return <SortRows model={items} />;
    case "SetValueField":
      return <SetValueField model={items} />;
    case "Calculator":
      return <Calculator model={items} />;
    case "Normaliser":
      return <Normaliser model={items} />;
    case "Flattener":
      return <Flattener model={items} />;
    case "FieldsChangeSequence":
      return <FieldsChangeSequence model={items} />;
    case "NumberRange":
      return <NumberRange model={items} />;
    case "AddXML":
      return <AddXML model={items} />;
    case "SPECIAL":
      return <START model={items} />;
    case "SUCCESS":
      return <SUCCESS model={items} />;
    case "EVAL":
      return <EVAL model={items} />;
    case "DELAY":
      return <DELAY model={items} />;
    case "HadoopCopyFilesPlugin":
      return <HadoopCopyFilesPlugin model={items} />;
    case "SET_VARIABLES":
      return <Variables model={items} />;
    case "SIMPLE_EVAL":
      return <SIMPLE_EVAL model={items} />;
    case "SFTP":
      return <SFTP model={items} />;
    case "SFTPPUT":
      return <SFTPPUT model={items} />;
    case "COPY_FILES":
      return <COPY_FILES model={items} />;
    case "JOB":
      return <JOB model={items} />;
    case "TRANS":
      return <TRANS model={items} />;
    case "DUMMY":
      return false;
    case "ElasticSearchBulk":
      return <ElasticSearchBulk model={items} />;
    case "HBaseOutput":
      return <HBaseOutput model={items} />;
    case "HBaseInput":
      return <HBaseInput model={items} />;
    case "StreamLookup":
      return <StreamLookup model={items} />;
    case "SwitchCase":
      return <SwitchCase model={items} />;
    case "GroupBy":
      return <GroupBy model={items} />;
    case "Validator":
      return <Validator model={items} />;
    case "HTTP":
      return <HttpClient model={items} />;
    case "HTTPPOST":
      return <HttpPost model={items} />;
    case "Rest":
      return <RestClient model={items} />;
    case "WebServiceLookup":
      return <WebServiceLookup model={items} />;
    case "JsonInput":
      return <JsonInput model={items} />;
    case "JsonOutput":
      return <JsonOutput model={items} />;
    case "FuzzyMatch":
      return <FuzzyMatch model={items} />;
    case "DBProc":
      return <DBProc model={items} />;
    case "DynamicSQLRow":
      return <DynamicSQLRow model={items} />;
    case "SqoopExport":
      return <SqoopExport model={items} />;
    case "SqoopImport":
      return <SqoopImport model={items} />;
    case "UNKNOWN":
      return <Unknown model={items} />;
    case "SHELL":
      return <Shell model={items} />;
    case "FilterRows":
      return <FilterRows model={items} />;
    case "MergeRows":
      return <MergeRows model={items} />;
    case "Dummy":
      return <Dummy model={items} />;
    case "RowGenerator":
      return <RowGenerator model={items} />;
    case "MultiwayMergeJoin":
      return <MultiwayMergeJoin model={items} />;
    case "MergeJoin":
      return <MergeJoin model={items} />;
    case "SortedMerge":
      return <SortedMerge model={items} />;
    case "GetVariable":
      return <GetVariable model={items} />;
    case "JoinRows":
      return <JoinRows model={items} />;
    case "SystemInfo":
      return <SystemInfo model={items} />;
    case "SetVariable":
      return <SetVariable model={items} />;
    case "DBLookup":
      return <DBLookup model={items} />;
    case "ParquetInput":
      return <ParquetInput model={items} />;
    case "ParquetOutput":
      return <ParquetOutput model={items} />;
    case "Formula":
      return <Formula model={items} />;
    case "ReadContentInput":
      return <ReadContentInput model={items} />;
    case "ExcelOutput":
      return <ExcelOutput model={items} />;
    case "RegexEval":
      return <RegexEval model={items} />;
    case "KafkaConsumerInput":
      return <KafkaConsumerInput model={items} />;
    case "KafkaProducerOutput":
      return <KafkaProducerOutput model={items} />;
    case "RecordsFromStream":
      return <RecordsFromStream model={items} />;
    case "Desensitization":
      return <Desensitization model={items} />;
    default:
      return (
        <Others visible={items.visible} maskClosable={false} text="ssssss" />
      );
  }
  // if(items.panel === "AnalysisReport"){
  //   return (
  //     <AnalysisReport model={items} />
  //   );
  // }else if(items.panel === "TableInput"){
  //       return (
  //         <TableInput model={items}     />
  //       )
  // }else if(items.panel === "TableOutput"){
  //     return (
  //       <TableOutput  model={items}   />
  //     )
  // }else if(items.panel === "ExecSQL"){
  //     return(
  //       <ExecSQL  model={items} />
  //     )
  // }else if(items.panel === "ScriptValueMod"){
  //     return(
  //       <ScriptValueMod  model={items}  />
  //     )
  // }else if(items.panel === "TextFileOutput"){
  //   return(
  //     <TextFileOutput  model={items}   />
  //   )
  // }else if(items.panel === "TextFileInput"){
  //   return(
  //     <TextFileInput  model={items}   />
  //   )
  // }else if(items.panel === "InsertUpdate"){
  //   return(
  //     <InsertUpdate  model={items}   />
  //   )
  // }else if(items.panel === "GetFileNames"){
  //   return(
  //     <GetFileNames  model={items}   />
  //   )
  // }else if(items.panel === "CsvInput"){
  //   return(
  //     <CsvInput  model={items}   />
  //   )
  // }else if(items.panel === "AccessInput"){
  //   return(
  //     <AccessInput  model={items}   />
  //   )
  // }else if(items.panel === "ExcelInput"){
  //   return(
  //     <ExcelInput  model={items}   />
  //   )
  // }else if(items.panel === "HadoopFileInputPlugin"){
  //   return(
  //     <HadoopFileInputPlugin  model={items}   />
  //   )
  // }else if(items.panel === "HadoopFileOutputPlugin"){
  //   return(
  //     <HadoopFileOutputPlugin  model={items}   />
  //   )
  // }else if(items.panel === "ClosureGenerator"){
  //    return(
  //      <ClosureGenerator   model={items} />
  //    )
  // }else if(items.panel === "GetSlaveSequence"){
  //   return(
  //     <GetSlaveSequence   model={items} />
  //   )
  // }else if(items.panel === "ValueMapper"){
  //   return(
  //     <ValueMapper   model={items} />
  //   )
  // } else if(items.panel === "SplitFieldToRows3"){
  //   return(
  //     <SplitFieldToRows3   model={items} />
  //   )
  // }  else if(items.panel === "Denormaliser"){
  //   return(
  //     <Denormaliser   model={items} />
  //   )
  // }  else if(items.panel === "StringCut"){
  //   return(
  //     <StringCut   model={items} />
  //   )
  // }else if(items.panel === "ConcatFields"){
  //   return(
  //     <ConcatFields   model={items} />
  //   )
  // }else if(items.panel === "Unique"){
  //   return(
  //     <Unique   model={items} />
  //   )
  // }else if(items.panel === "UniqueRowsByHashSet"){
  //   return(
  //     <UniqueRowsByHashSet   model={items} />
  //   )
  // }else if(items.panel === "Constant"){
  //   return(
  //     <Constant   model={items} />
  //   )
  // }else if(items.panel === "Sequence"){
  //   return(
  //     <Sequence   model={items} />
  //   )
  // }else if(items.panel === "CheckSum"){
  //   return(
  //     <CheckSum   model={items} />
  //   )
  // }else if(items.panel === "SelectValues"){
  //   return(
  //     <SelectValues   model={items} />
  //   )
  // }else if(items.panel === "StringOperations"){
  //   return(
  //     <StringOperations   model={items} />
  //   )
  // }else if(items.panel === "ReplaceString"){
  //   return(
  //     <ReplaceString   model={items} />
  //   )
  // }else if(items.panel === "SetValueConstant"){
  //   return(
  //     <SetValueConstant   model={items} />
  //   )
  // } else if(items.panel === "FieldSplitter"){
  //   return(
  //     <FieldSplitter   model={items} />
  //   )
  // }else if(items.panel === "SortRows"){
  //   return(
  //     <SortRows   model={items} />
  //   )
  // }else if(items.panel === "SetValueField"){
  //   return(
  //     <SetValueField   model={items} />
  //   )
  // }else if(items.panel === "Calculator"){
  //   return(
  //     <Calculator   model={items} />
  //   )
  // }else if(items.panel === "Normaliser"){
  //   return(
  //     <Normaliser   model={items} />
  //   )
  // }else if(items.panel === "Flattener"){
  //   return(
  //     <Flattener   model={items} />
  //   )
  // }else if(items.panel === "FieldsChangeSequence"){
  //   return(
  //     <FieldsChangeSequence   model={items} />
  //   )
  // }else if(items.panel === "NumberRange"){
  //   return(
  //     <NumberRange   model={items} />
  //   )
  // }else if(items.panel === "AddXML"){
  //   return(
  //     <AddXML model={items} />
  //   )
  // } else if(items.panel === "SPECIAL"){
  //   return(
  //     <START model={items}   />
  //   )
  // } else if(items.panel === "SUCCESS"){
  //   return(
  //     <SUCCESS   model={items} />
  //   )
  // }else if(items.panel === "EVAL"){
  //   return(
  //     <EVAL  model={items} />
  //   )
  // }else if(items.panel === "DELAY"){
  //   return(
  //     <DELAY  model={items} />
  //   )
  // }else if(items.panel === "HadoopCopyFilesPlugin"){
  //   return(
  //     <HadoopCopyFilesPlugin  model={items} />
  //   )
  // }else if(items.panel === "SET_VARIABLES"){
  //   return(
  //     <Variables model={items} />
  //   )
  // }else if(items.panel === "SIMPLE_EVAL"){
  //   return(
  //     <SIMPLE_EVAL model={items} />
  //   )
  // }else if(items.panel === "SFTP"){
  //   return(
  //     <SFTP model={items} />
  //   )
  // }else if(items.panel === "SFTPPUT"){
  //   return(
  //     <SFTPPUT model={items} />
  //   )
  // }else if(items.panel === "COPY_FILES"){
  //   return(
  //     <COPY_FILES model={items} />
  //   )
  // }else if(items.panel === "JOB"){
  //   return(
  //     <JOB model={items}  />
  //   )
  // }else if(items.panel === "TRANS"){
  //   return(
  //     <TRANS model={items} />
  //   )
  // }else if(items.panel === "DUMMY"){
  //   return false;
  // }else if(items.panel === "ElasticSearchBulk"){
  //   return(
  //     <ElasticSearchBulk model={items} />
  //   )
  // }else if(items.panel === "HBaseOutput"){
  //   return(
  //     <HBaseOutput model={items} />
  //   )
  // }else if(items.panel === "HBaseInput"){
  //   return(
  //     <HBaseInput model={items} />
  //   )
  // }else if(items.panel === "StreamLookup"){
  //   return(
  //     <StreamLookup model={items} />
  //   )
  // }else if(items.panel === "SwitchCase"){
  //   return(
  //     <SwitchCase model={items} />
  //   )
  // }else if(items.panel === "GroupBy"){
  //   return(
  //     <GroupBy model={items} />
  //   )
  // }else if(items.panel === "Validator"){
  //    return(
  //      <Validator  model={items} />
  //      //<SetVariables  model={items}/>  //设置变量
  //      //<JsonOutput model={items} />
  //    )
  // }else if(items.panel === "HTTP"){
  //   return(
  //     <HttpClient  model={items} />
  //   )
  // }else if(items.panel === "HTTPPOST"){
  //   return(
  //     <HttpPost  model={items} />
  //   )
  // }else if(items.panel === "Rest"){
  //   return(
  //     <RestClient  model={items} />
  //   )
  // }else if(items.panel === "WebServiceLookup"){
  //   return(
  //     <WebServiceLookup  model={items} />
  //   )
  // }else if(items.panel === "JsonInput"){
  //   return(
  //     <JsonInput  model={items} />
  //   )
  // }else if(items.panel === "JsonOutput"){
  //   return(
  //     <JsonOutput  model={items} />
  //   )
  // }else if(items.panel === "FuzzyMatch"){
  //   return(
  //     <FuzzyMatch  model={items} />
  //   )
  // }else if(items.panel === "DBProc"){
  //   return(
  //     <DBProc  model={items} />
  //   )
  // }else if(items.panel === "DynamicSQLRow"){
  //   return(
  //     <DynamicSQLRow  model={items} />
  //   )
  // }else if(items.panel === "SqoopExport"){
  //   return(
  //     <SqoopExport  model={items} />
  //   )
  // }else if(items.panel === "SqoopImport"){
  //   return(
  //     <SqoopImport  model={items} />
  //   )
  // }else if(items.panel === "UNKNOWN"){
  //   return(
  //     <Unknown model={items}/>
  //   )
  // }else if(items.panel === "SHELL"){
  //   return(
  //     <Shell model={items}/>
  //   )
  // }else if(items.panel === "FilterRows"){
  //   return(
  //     <FilterRows model={items}/>
  //   )
  // }else if(items.panel === "MergeRows"){
  //   return(
  //     <MergeRows model={items}/>
  //   )
  // }else if(items.panel === "Dummy"){
  //   return(
  //     <Dummy model={items}/>
  //   )
  // }else if(items.panel === "RowGenerator"){
  //   return(
  //     <RowGenerator model={items}/>
  //   )
  // }else if(items.panel === "MultiwayMergeJoin"){
  //   return(
  //     <MultiwayMergeJoin model={items}/>
  //   )
  // }else if(items.panel === "MergeJoin"){
  //   return(
  //     <MergeJoin model={items}/>
  //   )
  // }else if(items.panel === "SortedMerge"){
  //   return(
  //     <SortedMerge model={items}/>
  //   )
  // }else if(items.panel === "GetVariable"){
  //   return(
  //     <GetVariable model={items}/>
  //   )
  // }else if(items.panel === "JoinRows"){
  //   return(
  //     <JoinRows model={items}/>
  //   )
  // }else if(items.panel === "SystemInfo"){
  //   return(
  //     <SystemInfo model={items}/>
  //   )
  // }else if(items.panel === "SetVariable"){
  //   return(
  //     <SetVariable model={items}/>
  //   )
  // }else if(items.panel === "DBLookup"){
  //   return(
  //     <DBLookup model={items}/>
  //   )
  // }else if(items.panel === "ParquetInput"){
  //   return(
  //     <ParquetInput model={items}/>
  //   )
  // }else if(items.panel === "ParquetOutput"){
  //   return(
  //     <ParquetOutput model={items}/>
  //   )
  // }else if(items.panel === "Formula"){
  //   return(
  //     <Formula model={items}/>
  //   )
  // }else if(items.panel === "ReadContentInput"){
  //   return(
  //     <ReadContentInput model={items}/>
  //   )
  // }else {
  //   return (
  //     <Others visible={items.visible}  maskClosable={false}  text="ssssss"/>
  //   )
  // }
};

export default connect(({ items, cloudetlCommon }) => ({
  items,
  cloudetlCommon
}))(DomShow);
