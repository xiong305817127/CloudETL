/**
 * Created by Administrator on 2017/8/16.
 * 数据集成公共常量
 */
import { Select } from 'antd';
import { SITE_CUSTOM_THEME } from 'constants';

/*上传文件大小限制 单位(M)*/

const fileSizeLimit = 20;

//状态类型
export const statusType = new Map(
  [
    ["Undefined", "未定义"],
    ["Waiting" , "等待执行"],

    ["Running","执行中"],
    ["Preparing executing", "准备执行"],
    ["Initializing" , "执行初始化"],

    ["Paused","暂停"],

    ["TimeOut" , "执行超时"],
    ["Failed" , "执行失败"],
    ["Unknown" , "未知异常"],

    ["Finished (with errors)","完成（有错误)"],
    ["Stopped" , "终止"],
    ["Halting" , "挂起"],

    ["Finished","完成"]
  ]
);

//状态类型
export const statusType1 = new Map(
  [
    ["等待执行","wait"],
    ["执行中","run"],
    ["告警状态","warn"],
    ["执行故障","error"]
  ]
);

//初始状态
export const initStatus = new Map([
  ["Waiting","等待执行"],
  ["Undefined", "等待执行"]
]);
export const finishStatus = new Map([
  ["Finished","完成"] 
]);


//运行状态
export const runStatus = new Map([
  ["Running","执行中"],
  ["Preparing executing", "准备执行"],
  ["Initializing" , "执行初始化"]
]);
//暂停状态
export const pauseStatus = new Map([
  ["Paused","暂停"]
]);

//终止状态
export const stopStatus = new Map([
  ["Finished (with errors)","完成（有错误)"],
  ["Stopped" , "终止"],
  ["Halting" , "挂起"]
]);


//错误状态
export const errorStatus = new Map([
  ["TimeOut" , "超时"],
  ["Failed" , "失败"],
  ["Unknown" , "未知异常"]
]);


//状态切换  延时更新时间
export const delayTime = 200;

//请求间隔时间
export const spaceTime = 2000;

//loading消失时间时间
export const  delayLoading = 5000;

//是否进行请求
export const requestMap = new Map([
  ["needRequest",true]
]);


//转换分页请求默认
export const transPageSize = 8;
//调度分页请求默认
export const jobPageSize = 8;


//数组内存在  禁用trans引擎
export const transArgs = ["AccessInput","CsvInput","JsonInput","ExcelInput","TextFileInput","GetFileNames","Flattener","InsertUpdate","ScriptValueMod",
                       "DBLookup","SetVariable","SystemInfo","MergeJoin","JoinRows","SortedMerge","GetVariable","MultiwayMergeJoin","RowGenerator","MergeRows","Dummy","FilterRows","FuzzyMatch","DBProc","Validator",
                       "ConcatFields","Rest","WebServiceLookup","HTTPPOST"];

//数组组件禁用   中断恢复，本地执行
export const disabledArgs = ["JsonOutput","ClosureGenerator","SortRows","StreamLookup","UniqueRowsByHashSet","JoinRows"];


//文件浏览请求配置
export const treeViewConfig = new Map([
  ["AccessInput",{
    model:{
      obj:{
        type:"access",
        path:"",
        depth:1
      },
      needType:"file",
      needFileName:true,
      needUpFolder:false
    },
    list:{
      obj:{
        type:"input",
        path:"",
        depth:1,
        filterType:"access"
      },
      needType:"all",
      needFileName:true,
      needUpFolder:false
    }
  }],
  ["ExcelInput",{
    model:{
      obj:{
        type:"excel",
        path:"",
        depth:1
      },
      needType:"file",
      needUpFolder:false,
      needFileName:true
    },
    list:{
      obj:{
        type:"input",
        path:"",
        depth:1,
        filterType:"excel"
      },
      needType:"all",
      needFileName:true,
      needUpFolder:false
    }
  }],
   ["ReadContentInput",{
    list:{
      obj:{
        type:"input",
        path:"",
        depth:1,
        filterType:"data"
      },
      needType:"all",
      needFileName:true,
      needUpFolder:false
    }
  }],
  ["CsvInput",{
    model:{
      obj:{
        type:"csv",
        path:"",
        depth:1
      },
      needType:"file",
      needUpFolder:false,
      needFileName:true
    },
    list:{
      obj:{
        type:"input",
        path:"",
        depth:1,
        filterType:"csv"
      },
      needType:"all",
      needFileName:true,
      needUpFolder:false
    }
  }],
  ["TextFileInput", {
    model:{
      obj:{
        type:"txt",
        path:"",
        depth:1
      },
      needType:"file",
      needUpFolder:false,
      needFileName:true
    },
    list:{
      obj:{
        type:"input",
        path:"",
        depth:1,
        filterType:"txt"
      },
      needType:"all",
      needFileName:true,
      needUpFolder:false
    }
  }],
  ["GetFileNames", {
		list:{
      obj:{
        type:"input",
        path:"",
        depth:1
      },
      needType:"all",
      needFileName:true,
      needUpFolder:false
    }
  }],
  ["TextFileOutput", {
    list:{
      obj:{
        type:"input",
        path:"",
        depth:1
      },
      needType:"all",
      needFileName:false,
      needUpFolder:false
    }
  }],
  ["ExcelOutput", {
    model1:{
      obj:{
        type:"excel",
        path:"",
        depth:1
      },
      needType:"file",
      needUpFolder:false,
      needFileName:true
    },
    model2:{
      obj:{
        type:"data",
        path:"",
        depth:1
      },
      needType:"file",
      needFileName:true,//文件后缀
      needUpFolder:false//路径选择
    },
    list:{
      obj:{
        type:"input",
        path:"",
        depth:1
      },
      needType:"all",
      needFileName:false,
      needUpFolder:false
    }
  }],
  ["WebServiceLookup", {
    list:{
      obj:{
        type:"input",
        path:"",
        depth:1
      },
      needType:"all",
      needFileName:true,//文件后缀
      needUpFolder:false//路径选择
    }
  }],
  ["Rest", {//浏览请求：命名一致
    list:{
      obj:{
        type:"input",//列表对应model的类型data
        path:"",
        depth:1
      },
      needType:"all",
      needFileName:true,//文件后缀
      needUpFolder:false//路径选择
    }
	}],
  ["JsonInput", {//浏览请求：命名一致
    model:{
      obj:{
        type:"json",
        path:"",
        depth:1
      },
      needType:"file",
      needUpFolder:false,
      needFileName:true
    },
    list:{
      obj:{
        type:"input",//参数data
        path:"",
        depth:1,
        filterType:"json"
      },
      needType:"all",
      needFileName:true,//文件后缀
      needUpFolder:false//路径选择
    }
	}],
  ["JsonOutput", {//浏览请求：命名一致
    list:{
      obj:{
        type:"input",
        path:"",
        depth:1
      },
      needType:"all",
      needFileName:false,
      needUpFolder:false
    }
	}],
  ["HadoopFileInputPlugin", {
    model:{
      obj:{
        type:"hdfs",
        path:"",
        depth:1
      },
      needType:"all",
      needFileName:true,
      needUpFolder:false
    }
  }],
  ["HadoopFileOutputPlugin", {
    model:{
      obj:{
        type:"hdfs",
        path:"",
        depth:1
      },
      needType:"all",
      needFileName:false,
      needUpFolder:false
    }
	}],
  ["SFTP", {
    model:{
      obj:{
        type:"input",
        path:"",
        depth:1
      },
      needType:"file",
      needUpFolder:false,
      needFileName:true
    },
    remote:{
      obj:{
        type:"input",
        path:"",
        depth:1
      },
      needType:"folder",
      needUpFolder:false,
      needFileName:false
    },
    list:{
      obj:{
        type:"sftp",
        path:"",
        depth:1
      },
      needType:"folder",
      needFileName:false,
      needUpFolder:false
    }
	}],
  ["SFTPPUT", {
    model:{
      obj:{
        type:"input",
        path:"",
        depth:1
      },
      needType:"file",
      needFileName:true,
      needUpFolder:false
    },
    list:{
      obj:{
        type:"sftp",
        path:"",
        depth:1
      },
      needType:"folder",
      needFileName:true,
      needUpFolder:false
    },
    remote:{
      obj:{
        type:"input",
        path:"",
        depth:1
      },
      needType:"folder",
      needUpFolder:false,
      needFileName:false
    }
	}],
  ["HadoopCopyFilesPlugin", {
    model:{
      needType:"all",
      needFileName:true,
      needUpFolder:false
    },
    list:{
			obj:{
        type:"input",
        path:"",
        depth:1
      },
      needType:"all",
      needFileName:true,
      needUpFolder:false
    }
	}],
  ["ParquetInput", {
    model:{
      needType:"all",
      needFileName:true,
      needUpFolder:false
    },
    list:{
      needType:"all",
      needFileName:true,
      needUpFolder:false
    }
  }],
  ["ParquetOutput", {
    model:{
      needType:"all",
      needFileName:true,
      needUpFolder:false
    },
    list:{
      needType:"all",
      needFileName:true,
      needUpFolder:false
    }
	}],
  ["SET_VARIABLES", {
    list:{
      obj:{
        type:"input",
        path:"",
        depth:1
      },
      needType:"all",
      needFileName:true,
      needUpFolder:false
    }
	}],
  ["COPY_FILES", {
    model:{
      obj:{
        type:"data",
        path:"",
        depth:1
      },
      needType:"all",
      needUpFolder:false,
      needFileName:true
    },
    list:{
      obj:{
        type:"input",
        path:"",
        depth:1
      },
      needType:"all",
      needFileName:true,
      needUpFolder:false
    }
	}],
		//待修改
  ["SqoopExport", {
    model:{
      obj:{
        type:"hdfs",
        path:"",
        depth:1
      },
      needType:"all",
      needFileName:true,
      needUpFolder:false
    }
  }],
  ["SqoopImport", {
    model:{
      obj:{
        type:"hdfs",
        path:"",
        depth:1
      },
      needType:"all",
      needFileName:true,
      needUpFolder:false
    }
  }]
]);

//文件上传请求配置
export const treeUploadConfig = new Map([
  ["AccessInput",{
    model:{
      model:"access",
      filterType:"access"
    },
    list:{
      model:"data",
      filterType:"access"
    }
  }],
  ["ExcelInput",{
    model:{
      model:"excel",
      filterType:"excel"
    },
    list:{
      model:"data",
      filterType:"excel"
    }
  }],
  ["ExcelOutput",{
    model:{
      model:"excel",
      filterType:"excel"
    },
    list:{
      model:"data",
      filterType:""
    }
  }],
  ["ReadContentInput",{
    model:{
      model:"",
      filterType:""
    },
    list:{
      model:"data",
      filterType:""
    }
  }],
  ["CsvInput",{
    model:{
      model:"csv",
      filterType:"csv"
    },
    list:{
      model:"data",
      filterType:"csv"
    }
  }],
  ["TextFileInput", {
    model:{
      model:"txt",
      filterType:"txt"
    },
    list:{
      model:"data",
      filterType:"txt"
    }
  }],
  ["Rest", {//对应上传名称和类型
    model:{
      model:"",
      filterType:""
    },
    list:{
      model:"data",
      filterType:""
    }
  }],
  ["JsonInput", {//对应上传名称和类型
    model:{
      model:"json",
      filterType:"json"
    },
    list:{
      model:"data",
      filterType:"json"
    }
  }],
  ["JsonOutput", {//对应上传名称和类型
    model:{
      model:"json",
      filterType:"json"
    },
    list:{
      model:"data",
      filterType:"json"
    }
  }],
  ["WebServiceLookup", {//WDSL文件格式
    model:{
      model:"WSDL",
      filterType:"WSDL"
    },
    list:{
      model:"data",
      filterType:"WSDL"
    }
  }]
]);

//资源中心文件类型
export const fileType = new Map([
  ["txt" , "Txt模板"],
  ["excel" , "Excel模板"],
  ["access" , "Access模板"],
  ["access" , "Access模板"],
  ["csv" , "Csv模板"],
	["data" , "资源文件"],
	["output" , "输出文件"]
]);

export const selectType = new Map([
  ["Y/N",[<Select.Option key="Y" value="Y">是</Select.Option>,<Select.Option key="N" value="N">否</Select.Option>]],
  ["1/0",[ <Select.Option key="0" value="0">否</Select.Option>, <Select.Option key="1" value="1">是</Select.Option>]],
  ["T/F",[<Select.Option key="true" value="true">是</Select.Option>,<Select.Option key="false" value="false">否</Select.Option>]],
  ["trimType",[
    <Select.Option key="none" value="none">不去掉空格</Select.Option>,
    <Select.Option key="left" value="left">去掉左空格</Select.Option>,
    <Select.Option key="right" value="right">去掉右空格</Select.Option>,
    <Select.Option key="both" value="both">去掉左右两边空格</Select.Option>,
  ]],
  ["numberTrimType",[
    <Select.Option key="none" value="0">不去掉空格</Select.Option>,
    <Select.Option key="left" value="1">去掉左空格</Select.Option>,
    <Select.Option key="right" value="2">去掉右空格</Select.Option>,
    <Select.Option key="both" value="3">去掉左右两边空格</Select.Option>
  ]],
  ["type",[
    <Select.Option key="Number" value="Number">Number</Select.Option>,
    <Select.Option key="Date" value="Date">Date</Select.Option>,
    <Select.Option key="String" value="String">String</Select.Option>,
    <Select.Option key="Boolean" value="Boolean">Boolean</Select.Option>,
    <Select.Option key="Integer" value="Integer">Integer</Select.Option>,
    <Select.Option key="BigNumber" value="BigNumber">BigNumber</Select.Option>,
    <Select.Option key="Binary" value="Binary">Binary</Select.Option>,
    <Select.Option key="Timestamp" value="Timestamp">Timestamp</Select.Option>,
    <Select.Option key="Internet Address" value="Internet Address">Internet Address</Select.Option>
  ]],
  ["numberType",[
    <Select.Option key="1" value="1">Number</Select.Option>,
    <Select.Option key="2" value="2">String</Select.Option>,
    <Select.Option key="3" value="3">Date</Select.Option>,
    <Select.Option key="4" value="4">Boolean</Select.Option>,
    <Select.Option key="5" value="5">Integer</Select.Option>,
    <Select.Option key="6" value="6">BigNumber</Select.Option>,
    <Select.Option key="8" value="8">Binary</Select.Option>,
    <Select.Option key="9" value="9">Timestamp</Select.Option>,
    <Select.Option key="10" value="10">Internet Address</Select.Option>
  ]],
  ["dateType",[
    <Select.Option key="yyyy/MM/dd HH:mm:ss.SSS" value="yyyy/MM/dd HH:mm:ss.SSS">yyyy/MM/dd HH:mm:ss.SSS</Select.Option>,
    <Select.Option  key="yyyy/MM/dd HH:mm:ss.SSS XXX" value="yyyy/MM/dd HH:mm:ss.SSS XXX">yyyy/MM/dd HH:mm:ss.SSS XXX</Select.Option>,
    <Select.Option key="yyyy/MM/dd HH:mm:ss" value="yyyy/MM/dd HH:mm:ss">yyyy/MM/dd HH:mm:ss</Select.Option>,
    <Select.Option key="yyyy/MM/dd HH:mm:ss XXX" value="yyyy/MM/dd HH:mm:ss XXX">yyyy/MM/dd HH:mm:ss XXX</Select.Option>,
    <Select.Option key="yyyyMMddHHmmss" value="yyyyMMddHHmmss">yyyyMMddHHmmss</Select.Option>,
    <Select.Option key="yyyy/MM/dd" value="yyyy/MM/dd">yyyy/MM/dd</Select.Option>,
    <Select.Option key="yyyy-MM-dd" value="yyyy-MM-dd">yyyy-MM-dd</Select.Option>,
    <Select.Option key="yyyy-MM-dd HH:mm:ss" value="yyyy-MM-dd HH:mm:ss">yyyy-MM-dd HH:mm:ss</Select.Option>,
    <Select.Option key="yyyy-MM-dd HH:mm:ss XXX" value="yyyy-MM-dd HH:mm:ss XXX">yyyy-MM-dd HH:mm:ss XXX</Select.Option>,
    <Select.Option key="yyyyMMdd" value="yyyyMMdd">yyyyMMdd</Select.Option>,
    <Select.Option key="MM/dd/yyyy" value="MM/dd/yyyy">MM/dd/yyyy</Select.Option>,
    <Select.Option key="MM/dd/yyyy HH:mm:ss" value="MM/dd/yyyy HH:mm:ss">MM/dd/yyyy HH:mm:ss</Select.Option>,
    <Select.Option key="MM-dd-yyyy" value="MM-dd-yyyy">MM-dd-yyyy</Select.Option>,
    <Select.Option key="MM-dd-yyyy HH:mm:ss" value="MM-dd-yyyy HH:mm:ss">MM-dd-yyyy HH:mm:ss</Select.Option>,
    <Select.Option key="MM/dd/yy" value="MM/dd/yy">MM/dd/yy</Select.Option>,
    <Select.Option key="MM-dd-yy" value="MM-dd-yy">MM-dd-yy</Select.Option>,
    <Select.Option key="dd/MM/yyyy" value="dd/MM/yyyy">dd/MM/yyyy</Select.Option>,
    <Select.Option key="dd-MM-yyyy" value="dd-MM-yyyy">dd-MM-yyyy</Select.Option>,
    <Select.Option key="yyyy-MM-dd'T'HH:mm:ss.SSSXXX" value="yyyy-MM-dd'T'HH:mm:ss.SSSXXX">yyyy-MM-dd'T'HH:mm:ss.SSSXXX</Select.Option>
  ]],
  ["date",[
    <Select.Option key="yyyy/MM/dd HH:mm:ss.SSS" value="yyyy/MM/dd HH:mm:ss.SSS">yyyy/MM/dd HH:mm:ss.SSS</Select.Option>,
    <Select.Option  key="yyyy/MM/dd HH:mm:ss.SSS XXX" value="yyyy/MM/dd HH:mm:ss.SSS XXX">yyyy/MM/dd HH:mm:ss.SSS XXX</Select.Option>,
    <Select.Option key="yyyy/MM/dd HH:mm:ss" value="yyyy/MM/dd HH:mm:ss">yyyy/MM/dd HH:mm:ss</Select.Option>,
    <Select.Option key="yyyy/MM/dd HH:mm:ss XXX" value="yyyy/MM/dd HH:mm:ss XXX">yyyy/MM/dd HH:mm:ss XXX</Select.Option>,
    <Select.Option key="yyyyMMddHHmmss" value="yyyyMMddHHmmss">yyyyMMddHHmmss</Select.Option>,
    <Select.Option key="yyyy/MM/dd" value="yyyy/MM/dd">yyyy/MM/dd</Select.Option>,
    <Select.Option key="yyyy-MM-dd" value="yyyy-MM-dd">yyyy-MM-dd</Select.Option>,
    <Select.Option key="yyyy-MM-dd HH:mm:ss" value="yyyy-MM-dd HH:mm:ss">yyyy-MM-dd HH:mm:ss</Select.Option>,
    <Select.Option key="yyyy-MM-dd HH:mm:ss XXX" value="yyyy-MM-dd HH:mm:ss XXX">yyyy-MM-dd HH:mm:ss XXX</Select.Option>,
    <Select.Option key="yyyyMMdd" value="yyyyMMdd">yyyyMMdd</Select.Option>,
    <Select.Option key="MM/dd/yyyy" value="MM/dd/yyyy">MM/dd/yyyy</Select.Option>,
    <Select.Option key="MM/dd/yyyy HH:mm:ss" value="MM/dd/yyyy HH:mm:ss">MM/dd/yyyy HH:mm:ss</Select.Option>,
    <Select.Option key="MM-dd-yyyy" value="MM-dd-yyyy">MM-dd-yyyy</Select.Option>,
    <Select.Option key="MM-dd-yyyy HH:mm:ss" value="MM-dd-yyyy HH:mm:ss">MM-dd-yyyy HH:mm:ss</Select.Option>,
    <Select.Option key="MM/dd/yy" value="MM/dd/yy">MM/dd/yy</Select.Option>,
    <Select.Option key="MM-dd-yy" value="MM-dd-yy">MM-dd-yy</Select.Option>,
    <Select.Option key="dd/MM/yyyy" value="dd/MM/yyyy">dd/MM/yyyy</Select.Option>,
    <Select.Option key="dd-MM-yyyy" value="dd-MM-yyyy">dd-MM-yyyy</Select.Option>,
    <Select.Option key="yyyy-MM-dd'T'HH:mm:ss.SSSXXX" value="yyyy-MM-dd'T'HH:mm:ss.SSSXXX">yyyy-MM-dd'T'HH:mm:ss.SSSXXX</Select.Option>,
    <Select.Option key="#,##0.###" value="#,##0.###">#,##0.###</Select.Option>,
    <Select.Option key="0.00" value="0.00">0.00</Select.Option>,
    <Select.Option key="0000000000000" value="0000000000000">0000000000000</Select.Option>,
    <Select.Option key="#.#" value="#.#">#.#</Select.Option>,
    <Select.Option key="#" value="#">#</Select.Option>,
    <Select.Option key="###,###,###.#" value="###,###,###.#">###,###,###.#</Select.Option>,
    <Select.Option key="#######.###" value="#######.###">#######.###</Select.Option>,
    <Select.Option key="#######.###" value="#######.###">#######.###</Select.Option>,
    <Select.Option key="#####.###%" value="#####.###%">#####.###%</Select.Option>
  ]],
  ["number",[
    <Select.Option value="0">-</Select.Option>,
    <Select.Option value="1">Set field to constant value A</Select.Option>,
    <Select.Option value="2">Create a copy of field A</Select.Option>,
    <Select.Option value="3">A + B</Select.Option>,
    <Select.Option value="4">A - B</Select.Option>,
    <Select.Option value="5">A * B</Select.Option>,
    <Select.Option value="6">A / B</Select.Option>,
    <Select.Option value="7">A * A</Select.Option>,
    <Select.Option value="8">SQRT( A )</Select.Option>,
    <Select.Option value="9">100 * A / B</Select.Option>,
    <Select.Option value="10">A -( A * B / 100 )</Select.Option>,
    <Select.Option value="11">A + ( A * B / 100 )</Select.Option>,
    <Select.Option value="12">A + B * C</Select.Option>,
    <Select.Option value="13">SQRT( A*A + B*B )</Select.Option>,
    <Select.Option value="14">ROUND( A )</Select.Option>,
    <Select.Option value="15">ROUND( A , B )</Select.Option>,
    <Select.Option value="16">STDROUND( A )</Select.Option>,
    <Select.Option value="17">STDROUND( A , B )</Select.Option>,
    <Select.Option value="18">CEIL( A )</Select.Option>,
    <Select.Option value="19">FLOOR( A )</Select.Option>,
    <Select.Option value="20">NVL( A, B )</Select.Option>,
    <Select.Option value="21">Date A + B Days</Select.Option>,
    <Select.Option value="22">Year of date A</Select.Option>,
    <Select.Option value="23">Month of date A</Select.Option>,
    <Select.Option value="24">Day of year of date A</Select.Option>,
    <Select.Option value="25">Day of month of date A</Select.Option>,
    <Select.Option value="26">Day of week of date A</Select.Option>,
    <Select.Option value="27">Week of year of date A</Select.Option>,

    <Select.Option value="28">ISO8601 Week of year of date A</Select.Option>,
    <Select.Option value="29">ISO8601 Year of date A</Select.Option>,
    <Select.Option value="30">Byte to hex encode of string A</Select.Option>,
    <Select.Option value="31">Hex to byte decode of string A</Select.Option>,
    <Select.Option value="32">Char to hex encode of string A</Select.Option>,
    <Select.Option value="33">Hex to char decode of string A"</Select.Option>,
    <Select.Option value="34">Checksum of a file A using CRC-32</Select.Option>,
    <Select.Option value="35">Checksum of a file A using Adler-32</Select.Option>,
    <Select.Option value="36">Checksum of a file A using MD5</Select.Option>,
    <Select.Option value="37">Checksum of a file A using SHA-1</Select.Option>,
    <Select.Option value="38">Levenshtein Distance (source A and target B)</Select.Option>,
    <Select.Option value="39">Metaphone of A (phonetics)</Select.Option>,
    <Select.Option value="40">Double metaphone of A(phonetics)</Select.Option>,
    <Select.Option value="41">Absolute value ABS( A )</Select.Option>,
    <Select.Option value="42">Remove time from a date A</Select.Option>,
    <Select.Option value="43">Date A - Date B (in days)</Select.Option>,
    <Select.Option value="44">A + B + C</Select.Option>,
    <Select.Option value="45">First letter of each word of a string A in capital</Select.Option>,
    <Select.Option value="46">UpperCase of a string A</Select.Option>,
    <Select.Option value="47">FLOOR( A )</Select.Option>,
    <Select.Option value="48">Mask XML content from string A</Select.Option>,
    <Select.Option value="49">Protect (CDATA) XML content from string A</Select.Option>,
    <Select.Option value="50">Remove CR from a string A</Select.Option>,
    <Select.Option value="51">Remove LF from a string A</Select.Option>,
    <Select.Option value="52">Remove CRLF from a string A</Select.Option>,
    <Select.Option value="53">Remove TAB from a string A</Select.Option>,
    <Select.Option value="54">Return only digits from string A</Select.Option>,
    <Select.Option value="55">Remove digits from string A</Select.Option>,

    <Select.Option value="56">Return the length of a string A"</Select.Option>,
    <Select.Option value="57">Load file content in binary</Select.Option>,
    <Select.Option value="58">Add time B to date A</Select.Option>,
    <Select.Option value="59">Quarter of date A</Select.Option>,
    <Select.Option value="60">variable substitution in string A</Select.Option>,
    <Select.Option value="61">Unescape XML content</Select.Option>,
    <Select.Option value="62">Escape HTML content</Select.Option>,
    <Select.Option value="63">Unescape HTML content</Select.Option>,
    <Select.Option value="64">Escape SQL content</Select.Option>,
    <Select.Option value="65">Date A - Date B (working days)</Select.Option>,
    <Select.Option value="66">Date A + B Months</Select.Option>,
    <Select.Option value="67">Check if an XML file A is well formed</Select.Option>,
    <Select.Option value="68">Check if an XML string A is well formed</Select.Option>,
    <Select.Option value="69">Get encoding of file A</Select.Option>,
    <Select.Option value="70">DamerauLevenshtein distance between String A and String B</Select.Option>,
    <Select.Option value="71">NeedlemanWunsch distance between String A and String B</Select.Option>,
    <Select.Option value="72">Jaro similitude between String A and String B</Select.Option>,
    <Select.Option value="73">JaroWinkler similitude between String A and String B</Select.Option>,
    <Select.Option value="74">SoundEx of String A</Select.Option>,
    <Select.Option value="75">RefinedSoundEx of String A</Select.Option>,
    <Select.Option value="76">Date A + B Hours</Select.Option>,
    <Select.Option value="77">Date A + B Minutes</Select.Option>,
    <Select.Option value="78">Date A - Date B (milliseconds)</Select.Option>,
    <Select.Option value="79">Date A - Date B (seconds)</Select.Option>,
    <Select.Option value="80">Date A - Date B (minutes)</Select.Option>,
    <Select.Option value="81">Date A - Date B (hours)</Select.Option>,
    <Select.Option value="82">Hour of Day of Date A</Select.Option>,
    <Select.Option value="83">Minute of Hour of Date A</Select.Option>,
    <Select.Option value="84">Second of Minute of Date A</Select.Option>,
    <Select.Option value="85">ROUND_CUSTOM( A , B )</Select.Option>,
    <Select.Option value="86">ROUND_CUSTOM( A , B , C )</Select.Option>,
    <Select.Option value="87">Date A + B Seconds</Select.Option>,
    <Select.Option value="88">Remainder of A / B</Select.Option>,

  ]],
  ["aggregation",[<Select.Option key="none" value="0">-</Select.Option>,
    <Select.Option key="none" value="SUM">和</Select.Option>,
    <Select.Option key="left" value="AVERAGE">平均值</Select.Option>,
    <Select.Option key="right" value="MIN">最小值</Select.Option>,
    <Select.Option key="both" value="MAX">最大值</Select.Option>,
    <Select.Option key="both" value="COUNT_ALL">值的数量</Select.Option>,
    <Select.Option key="both" value="CONCAT_COMMA">分隔连接字符串</Select.Option>
  ]],
   ["symbol",[
    <Select.Option key="0" value="=">{"="}</Select.Option>,
    <Select.Option key="1" value="<>">{"<>"}</Select.Option>,
    <Select.Option key="2" value="<">{"<"}</Select.Option>,
    <Select.Option key="3" value="<=">{"<="}</Select.Option>,
    <Select.Option key="4" value=">">{">"}</Select.Option>,
    <Select.Option key="5" value=">=">{">="}</Select.Option>,
    <Select.Option key="6" value="LIKE">LIKE</Select.Option>,
    <Select.Option key="7" value="BETWEEN">BETWEEN</Select.Option>,
    <Select.Option key="8" value="IS NULL">IS NULL</Select.Option>,
    <Select.Option key="9" value="IS NOT NULL">IS NOT NULL</Select.Option>,
  ]],
   ["symboloutput",[
      <Select.Option key="0" value="=">{"="}</Select.Option>,
      <Select.Option key="1" value="<>">{"<>"}</Select.Option>,
      <Select.Option key="2" value="<">{"<"}</Select.Option>,
      <Select.Option key="3" value="<=">{"<="}</Select.Option>,
      <Select.Option key="4" value=">">{">"}</Select.Option>,
      <Select.Option key="5" value=">=">{">="}</Select.Option>,
      <Select.Option key="6" value="6">Substring</Select.Option>,
      <Select.Option key="Starts from" value="Starts from">Starts from</Select.Option>,
      <Select.Option key="Regular expression" value="Regular expression">Regular expression</Select.Option>,
  ]],
  
]
);

export const getScreenSize = ()=>{
    let obj = {};
    let height = document.body.clientHeight;
    let width = document.body.clientWidth;
  switch (SITE_THEME){
    case 'government':
          obj.moveX = 220;
          obj.moveY = 100;
          return obj;
    default :
      if(height >= 900 && width>= 1440){
        obj.moveX = 0;
        obj.moveY = 120;
      }else{
        obj.moveX = 0;
        obj.moveY = 0
      }
      return obj;
  }
};
