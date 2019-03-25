import { connect } from "dva";
import { message, Select } from "antd";
import "./index.less";
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
//   getDictionary
// } from "services/quality";

import * as qualityFuncs from "services/quality";
import { CurryFunc } from "../../../../../../utils/utils";
import TableInput from "./Input/TableInput";
import Others from "./Others";
import Unknown from "./Unknown";
import TextFileInput from "./Input/TextFileInput";
import CsvInput from "./Input/CsvInput";
import AccessInput from "./Input/AccessInput";
import ExcelInput from "./Input/ExcelInput";

// 质量分析
import CharacterAnalysis from "./QC/CharacterAnalysis";
import NumberAnalysis from "./QC/NumberAnalysis";
import CertificatesAnalysis from "./QC/CertificatesAnalysis";
import DateAnalysis from "./QC/DateAnalysis";
// import CustomAnalysis from "./QC/CustomAnalysis"
import AnalysisReport from "./QC/AnalysisReport";

//新增冗余率  pwj
import Redundance from "./QC/Redundance";

let Timer;
const DomShow = ({ domItems, dispatch, cloudetlCommon, account }) => {
  const { isMetacube } = cloudetlCommon;
  const owner = account.loginUser.username;

  const CurryFuncList = CurryFunc(qualityFuncs, { owner: owner });

  const {
    getDb_list2,
    getHadoop_list,
    get_db_table,
    get_db_table_fields,
    getDefaultEngineList,
    getDataStore,
    getTrans_list,
    get_SftpList,
    trans_VariablesList,
    get_FileExist,
    get_output_fields,
    check_step_name,
    save_step,
    get_input_fields,
    get_details,
    get_db_schema,
    getServer_list,
    getDictionary
  } = CurryFuncList;

  const items = domItems;

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
  const getSchema = (query, callback) => {
    get_db_schema(query).then(res => {
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
            type: "designSpace/changeItemName",
            payload: {
              newName: obj.newname,
              key
            }
          });
        }
        dispatch({ type: "domItems/hide" });
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
        if (index[index1] && index[index1].toString().trim()) {
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
  items.getInputSelectMultiway = getInputSelectMultiway;
  items.get_Similarity = get_Similarity;
  items.GetDic = GetDic;

  items.getSftpList = getSftpList;
  items.initDefaultObj = initDefaultObj;
  items.objToArgs = objToArgs;

  /*检测路径*/
  items.handleCheckPathName = handleCheckPathName;

  console.log(items.panel);

  switch (items.panel) {
    case "TableInput":
      return <TableInput model={items} />;
    case "TextFileInput":
      return <TextFileInput model={items} />;
    case "CsvInput":
      return <CsvInput model={items} />;
    case "AccessInput":
      return <AccessInput model={items} />;
    case "ExcelInput":
      return <ExcelInput model={items} />;
    case "CharacterAnalysis":
      return <CharacterAnalysis model={items} />;
    case "DateAnalysis":
      return <DateAnalysis model={items} />;
    case "NumberAnalysis":
      return <NumberAnalysis model={items} />;
    case "CertificatesAnalysis":
      return <CertificatesAnalysis model={items} />;
    case "AnalysisReport":
      return <AnalysisReport model={items} />;
    case "Redundance":
      return <Redundance model={items} />;
    case "UNKNOWN":
      return <Unknown model={items} />;
    default:
      return (
        <Others visible={items.visible} maskClosable={false} text="ssssss" />
      );
  }
};

export default connect(({ domItems, cloudetlCommon, account }) => ({
  domItems,
  cloudetlCommon,
  account
}))(DomShow);
