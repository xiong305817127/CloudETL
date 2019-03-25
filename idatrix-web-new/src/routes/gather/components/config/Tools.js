/**
 * Created by Administrator on 2017/3/7.
 */
const Tools = {
  "TableInput": {
    type: 'TableInput',
    text: '表输入',
    name: "表",
    imgUrl: "./img/32/TIP.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/TIP.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '0',
    empowerApi: "/cloud/transtype/TableInput"
  },
  "TableOutput": {
    type: 'TableOutput',
    text: '表输出',
    name: "表",
    imgUrl: "./img/32/TOP.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/TOP.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '1',
    empowerApi: "/cloud/transtype/TableOutput"
  },
  "AccessInput": {
    type: 'AccessInput',
    text: 'Access输入',
    name: "Access",
    imgUrl: "./img/32/ACI.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/ACI.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '0',
    empowerApi: "/cloud/transtype/AccessInput"
  },
  "CsvInput": {
    type: 'CsvInput',
    text: 'Csv文件输入',
    name: "Csv文件",
    imgUrl: "./img/32/CSV.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/CSV.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '0',
    empowerApi: "/cloud/transtype/CsvInput"
  },
  "ExcelInput": {
    type: 'ExcelInput',
    text: 'Excel输入',
    name: "Excel",
    imgUrl: "./img/32/XLI.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/XLI.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '0',
    empowerApi: "/cloud/transtype/ExcelInput"
  },
  "InsertUpdate": {
    type: 'InsertUpdate',
    text: '插入/更新',
    name: "插入/更新",
    imgUrl: "./img/32/INU.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/INU.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '1',
    empowerApi: "/cloud/transtype/InsertUpdate"
  },
  "GetFileNames": {
    type: 'GetFileNames',
    text: '获取文件名',
    name: "获取文件名",
    imgUrl: "./img/32/GFN.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/GFN.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '0',
    empowerApi: "/cloud/transtype/GetFileNames"
  },
  "TextFileInput": {
    type: 'TextFileInput',
    text: '文本文件输入',
    name: "文本文件",
    imgUrl: "./img/32/TFI.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/TFI.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '0',
    empowerApi: "/cloud/transtype/TextFileInput"
  },
  "TextFileOutput": {
    type: 'TextFileOutput',
    text: '文本文件输出',
    name: "文本文件",
    imgUrl: "./img/32/TFO.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/TFO.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '1',
    empowerApi: "/cloud/transtype/TextFileOutput"
  },

  "ExecSQL": {
    type: 'ExecSQL',
    text: '执行SQL脚本',
    name: "ExecSQL",
    imgUrl: "./img/32/SQL.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SQL.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '3',
    empowerApi: "/cloud/transtype/ExecSQL"
  },
  "ScriptValueMod": {
    type: 'ScriptValueMod',
    text: 'JavaScript代码',
    name: "JavaScript",
    imgUrl: "./img/32/SCR_exe.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SCR_exe.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '3',
    empowerApi: "/cloud/transtype/ScriptValueMod"
  },

  "HadoopFileInputPlugin": {
    type: 'HadoopFileInputPlugin',
    text: 'Hadoop File Input',
    name: "HDFS输入",
    imgUrl: "./img/32/HDI.png",
    imgData: require('../designPlatform/TransPlatform/img/32/HDI.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '2',
    empowerApi: "/cloud/transtype/HadoopFileInputPlugin"
  },
  "HadoopFileOutputPlugin": {
    type: 'HadoopFileOutputPlugin',
    text: 'Hadoop File Output',
    name: "HDFS输出",
    imgUrl: "./img/32/HDO.png",
    imgData: require('../designPlatform/TransPlatform/img/32/HDO.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '2',
    empowerApi: "/cloud/transtype/HadoopFileOutputPlugin"
  },
  "Update": {
    type: 'Update',
    text: '更新',
    name: "Update",
    imgUrl: "./img/32/UPD.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/UPD.svg'),
    radius: '8',
    empowerApi: "/cloud/transtype/Update"
  },

  "SetValueField": {
    type: 'SetValueField',
    text: '设置字段值',
    name: "设置字段值",
    imgUrl: "./img/32/SVF.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SVF.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/SetValueField"
  },
  "Calculator": {
    type: 'Calculator',
    text: '计算器',
    name: "计算器",
    imgUrl: "./img/32/CLC.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/CLC.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/Calculator"
  },
  "Normaliser": {
    type: 'Normaliser',
    text: '行转列',
    name: '行转列',
    imgUrl: "./img/32/NRM.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/NRM.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/Normaliser"
  },
  "Flattener": {
    type: 'Flattener',
    text: '行扁平化',
    name: '行扁平化',
    imgUrl: "./img/32/FLA.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/FLA.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/Flattener"
  },
  "FieldsChangeSequence": {
    type: 'FieldsChangeSequence',
    text: '根据字段值来改变序列',
    name: '字段变序列',
    imgUrl: "./img/32/CSEQ.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/CSEQ.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/FieldsChangeSequence"
  },
  "NumberRange": {
    type: 'NumberRange',
    text: '数值范围',
    name: '数值范围',
    imgUrl: "./img/32/NRI.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/NRI.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/NumberRange"
  },
  "SortRows": {
    type: 'SortRows',
    text: '排序记录',
    name: '排序记录',
    imgUrl: "./img/32/SRT.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SRT.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/SortRows"
  },
  "FieldSplitter": {
    type: 'FieldSplitter',
    text: '拆分字段',
    name: '拆分字段',
    imgUrl: "./img/32/SPL.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SPL.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/FieldSplitter"
  },
  "SetValueConstant": {
    type: 'SetValueConstant',
    text: '将字段设置为常量',
    name: '字段变常量',
    imgUrl: "./img/32/SVC.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SVC.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/SetValueConstant"
  },
  "ReplaceString": {
    type: 'ReplaceString',
    text: '字符串替换',
    name: '字符串替换',
    imgUrl: "./img/32/RST.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/RST.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/ReplaceString"
	},
	"Desensitization": {
    type: 'Desensitization',
    text: '数据脱敏',
    name: '数据脱敏',
    imgUrl: "./img/32/DST.png",
    imgData: require('../designPlatform/TransPlatform/img/32/DST.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/Desensitization"
  },
  "StringOperations": {
    type: 'StringOperations',
    text: '字符串操作',
    name: '字符串操作',
    imgUrl: "./img/32/STM.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/STM.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/StringOperations"
  },
  "SelectValues": {
    type: 'SelectValues',
    text: '字段选择',
    name: '字段选择',
    imgUrl: "./img/32/SEL.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SEL.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/SelectValues"
  },
  "CheckSum": {
    type: 'CheckSum',
    text: '增加校验列',
    name: '增加校验列',
    imgUrl: "./img/32/CSM.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/CSM.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/CheckSum"
  },
  "Sequence": {
    type: 'Sequence',
    text: '增加序列',
    name: '增加序列',
    imgUrl: "./img/32/CSEQ.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/CSEQ.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/Sequence"
  },
  "GetSlaveSequence": {
    type: 'GetSlaveSequence',
    text: 'GET ID from slave server',
    name: '获取ID',
    imgUrl: "./img/32/SEQ.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SEQ.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/GetSlaveSequence"
  },
  "Constant": {
    type: 'Constant',
    text: '增加常量',
    name: '增加常量',
    imgUrl: "./img/32/CST.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/CST.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/Constant"
  },
  "UniqueRowsByHashSet": {
    type: 'UniqueRowsByHashSet',
    text: '唯一行(哈希值)',
    name: '唯一行',
    imgUrl: "./img/32/URH.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/URH.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/UniqueRowsByHashSet"
  },
  "Unique": {
    type: 'Unique',
    text: '去除重复记录',
    name: '去重复记录',
    imgUrl: "./img/32/UNQ.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/UNQ.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/Unique"
  },
  "StringCut": {
    type: 'StringCut',
    text: '剪切字符串',
    name: '剪切字符串',
    imgUrl: "./img/32/SRC.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SRC.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/StringCut"
  },
  "Denormaliser": {
    type: 'Denormaliser',
    text: '列转行',
    name: '列转行',
    imgUrl: "./img/32/UNP.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/UNP.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/Denormaliser"
  },
  "SplitFieldToRows3": {
    type: 'SplitFieldToRows3',
    text: '列拆分为多行',
    name: '列拆为多行',
    imgUrl: "./img/32/SFtR.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SFtR.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/SplitFieldToRows3"
  },
  "ValueMapper": {
    type: 'ValueMapper',
    text: '值映射',
    name: '值映射',
    imgUrl: "./img/32/VMAP.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/VMAP.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/ValueMapper"
  },
  "ConcatFields": {
    type: 'ConcatFields',
    text: '连接字段',
    name: '连接字段',
    imgUrl: "./img/32/ConcatFields.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/ConcatFields.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/ConcatFields"
  },
  "ClosureGenerator": {
    type: 'ClosureGenerator',
    text: '闭包生成器',
    name: '闭包生成器',
    imgUrl: "./img/32/CLC.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/CLC.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/ClosureGenerator"
  },
  "AddXML": {
    type: 'AddXML',
    text: 'XML生成器',
    name: 'XML生成器',
    imgUrl: "./img/32/add_xml.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/add_xml.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '4',
    empowerApi: "/cloud/transtype/AddXML"
  },
  "HBaseOutput": {
    type: 'HBaseOutput',   //修改
    text: 'HBase输出',
    name: 'HBase输出',
    imgUrl: "./img/32/HBOUT.png",
    imgData: require('../designPlatform/TransPlatform/img/32/HBOUT.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '2',
    empowerApi: "/cloud/transtype/HBaseOutput"
  },
  "HBaseInput": {
    type: 'HBaseInput',   //修改
    text: 'HBase输入',
    name: 'HBase输入',
    imgUrl: "./img/32/HBINP.png",
    imgData: require('../designPlatform/TransPlatform/img/32/HBINP.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '2',
    empowerApi: "/cloud/transtype/HBaseInput"
  },
  "ElasticSearchBulk5": {
    type: 'ElasticSearchBulk5',   //修改
    text: 'ES批量加载',
    name: 'ES批量加载',
    imgUrl: "./img/32/ELI.png",
    imgData: require('../designPlatform/TransPlatform/img/32/ELI.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '5',
    empowerApi: "/cloud/transtype/ElasticSearchBulk"
  },
  "StreamLookup": {
    type: 'StreamLookup',   //修改
    text: '流查询',
    name: '流查询',
    imgUrl: "./img/32/SLU.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SLU.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '6',
    empowerApi: "/cloud/transtype/StreamLookup"
  },
  "SwitchCase": {
    type: 'SwitchCase',   //修改
    text: 'Switch / Case',
    name: 'Switch/Case',
    imgUrl: "./img/32/SWC.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SWC.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '8',
    empowerApi: "/cloud/transtype/SwitchCase"
  },
  "GroupBy": {
    type: 'GroupBy',   //修改
    text: '分组',
    name: '分组',
    imgUrl: "./img/32/GRP.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/GRP.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '10',
    empowerApi: "/cloud/transtype/GroupBy"
  },
  "FilterRows": {
    type: 'FilterRows',   //修改
    text: '过滤记录',
    name: '过滤记录',
    imgUrl: "./img/32/FLT.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/FLT.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '8',
    empowerApi: "/cloud/transtype/FilterRows"
  },
  "Validator": {
    type: 'Validator',   //修改
    text: '数据检验',
    name: '数据检验',
    imgUrl: "./img/32/VLD.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/VLD.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '11',
    empowerApi: "/cloud/transtype/Validator"
  },
  "HTTP": {
    type: 'HTTP',   //路由名称和请求类型必须一致
    text: 'Http客户端',
    name: 'Http客户端',
    imgUrl: "./img/32/WEB.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/WEB.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '6',
    empowerApi: "/cloud/transtype/HTTP"
  },
  "HTTPPOST": {
    type: 'HTTPPOST',
    text: 'HttpPost',
    name: 'HttpPost',
    imgUrl: "./img/32/HTP.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/HTP.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '6',
    empowerApi: "/cloud/transtype/HTTPPOST"
  },
  "Rest": {
    type: 'Rest',
    text: 'Rest客户端',
    name: 'Rest客户端',
    imgUrl: "./img/32/REST.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/REST.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '6',
    empowerApi: "/cloud/transtype/Rest"
  },
  "WebServiceLookup": {
    type: 'WebServiceLookup',
    text: 'Web服务查询',
    name: 'Web查询',
    imgUrl: "./img/32/WSL.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/WSL.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '6',
    empowerApi: "/cloud/transtype/WebServiceLookup"
  },
  "JsonInput": {
    type: 'JsonInput',
    text: 'Json输入',
    name: 'Json',
    imgUrl: "./img/32/JSI.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/JSI.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '0',
    empowerApi: "/cloud/transtype/JsonInput"
  },
  "MergeRows": {
    type: 'MergeRows',   //修改
    text: '合并记录',
    name: '合并记录',
    imgUrl: "./img/32/MRG.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/MRG.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '7',
    empowerApi: "/cloud/transtype/MergeRows"
  },
  "JsonOutput": {
    type: 'JsonOutput',
    text: 'Json输出',
    name: 'Json',
    imgUrl: "./img/32/JSO.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/JSO.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '1',
    empowerApi: "/cloud/transtype/JsonOutput"
  },
  "FuzzyMatch": {
    type: 'FuzzyMatch',
    text: '模糊匹配',
    name: '模糊匹配',
    imgUrl: "./img/32/FZM.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/FZM.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '6',
    empowerApi: "/cloud/transtype/FuzzyMatch"
  },
  "DBProc": {
    type: 'DBProc',
    text: '调用DB存储过程',
    name: 'DB存储过程',
    imgUrl: "./img/32/PRC.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/PRC.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '6',
    empowerApi: "/cloud/transtype/DBProc"
  },
  "DynamicSQLRow": {
    type: 'DynamicSQLRow',
    text: '执行动态 SQL',
    name: '动态SQL',
    imgUrl: "./img/32/DSR.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/DSR.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '6',
    empowerApi: "/cloud/transtype/DynamicSQLRow"
  },
  "Dummy": {
    type: 'Dummy',   //修改
    text: '空操作',
    name: '空操作',
    imgUrl: "./img/32/DUM.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/DUM.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '8',
    empowerApi: "/cloud/transtype/Dummy"
  },
  "RowGenerator": {
    type: 'RowGenerator',   //修改
    text: '生成记录',
    name: '生成记录',
    imgUrl: "./img/32/GEN.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/GEN.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '0',
    empowerApi: "/cloud/transtype/RowGenerator"
  },
  "MultiwayMergeJoin": {
    type: 'MultiwayMergeJoin',   //修改
    text: '多方式合并',
    name: '多方式合并',
    imgUrl: "./img/32/MJOINS.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/MJOINS.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '7',
    empowerApi: "/cloud/transtype/MultiwayMergeJoin"
  },
  "MergeJoin": {
    type: 'MergeJoin',   //修改
    text: '记录集',
    name: '记录集',
    imgUrl: "./img/32/MJOIN.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/MJOIN.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '7',
    empowerApi: "/cloud/transtype/MergeJoin"
  },
  "SortedMerge": {
    type: 'SortedMerge',   //修改
    text: '排序合并',
    name: '排序合并',
    imgUrl: "./img/32/SMG.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SMG.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '7',
    empowerApi: "/cloud/transtype/SortedMerge"
  },
  "GetVariable": {
    type: 'GetVariable',   //修改
    text: '获取变量',
    name: '获取变量',
    imgUrl: "./img/32/GVA.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/GVA.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '9',
    empowerApi: "/cloud/transtype/GetVariable"
  },
  "JoinRows": {
    type: 'JoinRows',   //修改
    text: '记录关联',
    name: '记录关联',
    imgUrl: "./img/32/JRW.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/JRW.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '7',
    empowerApi: "/cloud/transtype/JoinRows"
  },
  "SystemInfo": {
    type: 'SystemInfo',   //修改
    text: '系统信息',
    name: '系统信息',
    imgUrl: "./img/32/SYS.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SYS.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '0',
    empowerApi: "/cloud/transtype/SystemInfo"
  },
  "SetVariable": {
    type: 'SetVariable',   //修改
    text: '设置变量',
    name: '设置变量',
    imgUrl: "./img/32/SVA.png",
    imgData: require('../designPlatform/TransPlatform/img/32/SVA.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '9',
    empowerApi: "/cloud/transtype/SetVariable"
  },
  "DBLookup": {
    type: 'DBLookup',   //修改
    text: '数据库查询',
    name: '数据库查询',
    imgUrl: "./img/32/DLU.png",
    imgData: require('../designPlatform/TransPlatform/img/32/DLU.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '9',
    empowerApi: "/cloud/transtype/DBLookup"
  },
  "ParquetInput": {
    type: 'ParquetInput',   //修改
    text: 'Parquet输入',
    name: 'Parquet输入',
    imgUrl: "./img/svg/PI.svg",
    imgData: require('../designPlatform/TransPlatform/img/svg/PI.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '2',
    empowerApi: "/cloud/transtype/ParquetInput"
  },
  "ParquetOutput": {
    type: 'ParquetOutput',   //修改
    text: 'Parquet输出',
    name: 'Parquet输出',
    imgUrl: "./img/svg/PO.svg",
    imgData: require('../designPlatform/TransPlatform/img/svg/PO.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '2',
    empowerApi: "/cloud/transtype/ParquetOutput"
  },
  "Formula": {
    type: 'Formula',   //修改
    text: '公式',
    name: '公式',
    imgUrl: "./img/svg/FRM.svg",
    imgData: require('../designPlatform/TransPlatform/img/svg/FRM.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '3',
    empowerApi: "/cloud/transtype/Formula"
  },
  "ReadContentInput": {
    type: 'ReadContentInput',   //修改
    text: '读取内容',
    name: '读取内容',
    imgUrl: "./img/32/inSmall.png",
    imgData: require('../designPlatform/TransPlatform/img/32/inSmall.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '0',
    empowerApi: "/cloud/transtype/ReadContentInput"
  },

  "CharacterAnalysis": {
    type: 'CharacterAnalysis',   //标准值匹配
    text: '标准值匹配',
    name: '标准值匹配',
    imgUrl: "./img/32/DICO.png",
    imgData: require('../designPlatform/TransPlatform/img/32/DICO.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '12',
    empowerApi: "/cloud/transtype/CharacterAnalysis"
  },
  "NumberAnalysis": {
    type: 'NumberAnalysis',   //标准值匹配
    text: '电话匹配',
    name: '电话匹配',
    imgUrl: "./img/32/POTH.png",
    imgData: require('../designPlatform/TransPlatform/img/32/POTH.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '12',
    empowerApi: "/cloud/transtype/NumberAnalysis"
  },
  "CertificatesAnalysis": {
    type: 'CertificatesAnalysis',   //标准值匹配
    text: '证件匹配',
    name: '证件匹配',
    imgUrl: "./img/32/PHON.png",
    imgData: require('../designPlatform/TransPlatform/img/32/PHON.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '12',
    empowerApi: "/cloud/transtype/CertificatesAnalysis"
  },
  "DateAnalysis": {
    type: 'DateAnalysis',   //标准值匹配
    text: '日期匹配',
    name: '日期匹配',
    imgUrl: "./img/32/DATA.png",
    imgData: require('../designPlatform/TransPlatform/img/32/DATA.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '12',
    empowerApi: "/cloud/transtype/DateAnalysis"
    // empowerApi:"/cloud/transtype/DateAnalysis"
  },
  "CustomAnalysis": {
    type: 'CustomAnalysis',   //标准值匹配
    text: '自定义匹配',
    name: '自定义匹配',
    imgUrl: "./img/32/inSmall.png",
    imgData: require('../designPlatform/TransPlatform/img/32/inSmall.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '12',
    empowerApi: "/cloud/transtype/CustomAnalysis"
    // empowerApi:"/cloud/transtype/CustomAnalysis"
  },
  "AnalysisReport": {
    type: 'AnalysisReport',   //标准值匹配
    text: '报表生成',
    name: '报表生成',
    imgUrl: "./img/32/EXIC.png",
    imgData: require('../designPlatform/TransPlatform/img/32/EXIC.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '12',
    empowerApi: "/cloud/transtype/AnalysisReport"
  },
  "RegexEval": {
    type: 'RegexEval',
    text: '正则表达式',
    name: "正则表达式",
    imgUrl: "./img/32/RGE.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/RGE.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '3',
    empowerApi: "/cloud/transtype/RegexEval"
  },
  "ExcelOutput": {
    type: 'ExcelOutput',
    text: 'Excel输出',
    name: 'Excel',
    imgUrl: "./img/32/XLO.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/XLO.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '1',
    empowerApi: "/cloud/transtype/ExcelOutput"
  },
  "KafkaConsumerInput": {
    type: 'KafkaConsumerInput',
    text: 'kafka消费者',
    name: 'kafka消费者',
    imgUrl: "./img/svg/KafkaConsumerInput.svg",
    imgData: require('../designPlatform/TransPlatform/img/svg/KafkaConsumerInput.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '13',
    empowerApi: "/cloud/transtype/KafkaConsumerInput"
    //empowerApi: "/cloud/transtype/ExcelOutput"
  },
  "KafkaProducerOutput": {
    type: 'KafkaProducerOutput',
    text: 'kafka生产者',
    name: 'kafka生产者',
    imgUrl: "./img/svg/KafkaProducerOutput.svg",
    imgData: require('../designPlatform/TransPlatform/img/svg/KafkaProducerOutput.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '13',
    empowerApi: "/cloud/transtype/KafkaProducerOutput"
    //empowerApi: "/cloud/transtype/ExcelOutput"
  },
  "RecordsFromStream": {
    type: 'RecordsFromStream',
    text: '结果中取流',
    name: '结果中取流',
    imgUrl: "./img/svg/KafkaProducerOutput.svg",
    imgData: require('../designPlatform/TransPlatform/img/svg/get-records-from-stream.svg'),
    radius: '8',
    typeFeil: 'trans',
    model: '13',
    empowerApi: "/cloud/transtype/RecordsFromStream"
    //empowerApi: "/cloud/transtype/ExcelOutput"
  },

  /*"SetVariable":{
    type:'SetVariable',   //修改
    text:'设置变量',
    name:'设置变量',
    imgUrl : "./img/32/SVA.png",
    imgData:require('../designPlatform/TransPlatform/img/32/SVA.png'),
    radius:'8',
    typeFeil:'trans',
    model:'8',
    empowerApi:"/cloud/transtype/Validator"
   // empowerApi:"/cloud/transtype/SetVariable"
  },*/

  /*job*/
  "SUCCESS": {
    type: 'SUCCESS',
    text: '成功',
    name: '成功',
    imgUrl: "./img/32/SUC.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SUC.svg'),
    radius: '8',
    typeFeil: 'job',
    model: '00',
    empowerApi: "/cloud/jobtype/SUCCESS"
  },
  "EVAL": {
    type: 'EVAL',   //修改
    text: 'JavaScirpt',
    name: 'JavaScirpt',
    imgUrl: "./img/32/autodoc.png",
    imgData: require('../designPlatform/JobPlatform/img/32/autodoc.png'),
    radius: '8',
    typeFeil: 'job',
    model: '04',
    empowerApi: "/cloud/jobtype/EVAL"
  },
  "DELAY": {
    type: 'DELAY',   //修改
    text: '等待',
    name: '等待',
    imgUrl: "./img/32/DLT.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/DLT.svg'),
    radius: '8',
    typeFeil: 'job',
    model: '05',
    empowerApi: "/cloud/jobtype/DELAY"
  },
  "HadoopCopyFilesPlugin": {
    type: 'HadoopCopyFilesPlugin',   //修改
    text: 'Hadoop Copy Files',
    name: 'HDFS',
    imgUrl: "./img/32/HDC.png",
    imgData: require('../designPlatform/JobPlatform/img/32/HDC.png'),
    radius: '8',
    typeFeil: 'job',
    model: '03',
    empowerApi: "/cloud/jobtype/HadoopCopyFilesPlugin"
  },
  "SPECIAL": {
    type: 'SPECIAL',   //修改
    text: 'START',
    name: 'START',
    imgUrl: "./img/32/STR.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/STR.svg'),
    radius: '8',
    typeFeil: 'job',
    model: '00',
    empowerApi: "/cloud/jobtype/SPECIAL"
  },
  "DUMMY": {
    type: 'DUMMY',   //修改
    text: 'DUMMY',
    name: 'DUMMY',
    imgUrl: "./img/32/DUM.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/DUM.svg'),
    radius: '8',
    typeFeil: 'job',
    model: '00',
    empowerApi: "/cloud/jobtype/DUMMY"
  },
  "SET_VARIABLES": {
    type: 'SET_VARIABLES',   //修改
    text: '设置变量',
    name: '设置变量',
    imgUrl: "./img/32/SVA.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SVA.svg'),
    radius: '8',
    typeFeil: 'job',
    model: '00',
    empowerApi: "/cloud/jobtype/SET_VARIABLES"
  },
  "SIMPLE_EVAL": {
    type: 'SIMPLE_EVAL',   //修改
    text: '检验字段的值',
    name: '检验字段值',
    imgUrl: "./img/32/SEV.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SEV.svg'),
    radius: '8',
    typeFeil: 'job',
    model: '05',
    empowerApi: "/cloud/jobtype/SIMPLE_EVAL"
  },
  "SFTP": {
    type: 'SFTP',   //修改
    text: 'SFTP 下载',
    name: 'SFTP下载',
    imgUrl: "./img/32/SFT.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SFT.svg'),
    radius: '8',
    typeFeil: 'job',
    model: '01',
    empowerApi: "/cloud/jobtype/SFTP"
  },
  "SFTPPUT": {
    type: 'SFTPPUT',   //修改
    text: 'SFTP 上传',
    name: 'SFTP上传',
    imgUrl: "./img/32/SFP.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/SFP.svg'),
    radius: '8',
    typeFeil: 'job',
    model: '01',
    empowerApi: "/cloud/jobtype/SFTPPUT"
  },
  "COPY_FILES": {
    type: 'COPY_FILES',   //修改
    text: '复制文件',
    name: '复制文件',
    imgUrl: "./img/32/CPY.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/CPY.svg'),
    radius: '8',
    typeFeil: 'job',
    model: '02',
    empowerApi: "/cloud/jobtype/COPY_FILES"
  },
  "JOB": {
    type: 'JOB',   //修改
    text: '作业',
    name: '作业',
    imgUrl: "./img/32/JOB.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/JOB.svg'),
    radius: '8',
    typeFeil: 'job',
    model: '00',
    empowerApi: "/cloud/jobtype/JOB"
  },
  "TRANS": {
    type: 'TRANS',   //修改
    text: '转换',
    name: '转换',
    imgUrl: "./img/32/TRN.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/TRN.svg'),
    radius: '8',
    typeFeil: 'job',
    model: '00',
    empowerApi: "/cloud/jobtype/TRANS"
  },
  "SqoopExport": {
    type: 'SqoopExport',   //修改
    text: 'Sqoop 输出',
    name: 'Sqoop输出',
    imgUrl: "./img/32/sqoop-export.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/sqoop-export.svg'),
    radius: '8',
    typeFeil: 'job',
    model: '03',
    empowerApi: "/cloud/jobtype/SqoopExport"
  },
  "SqoopImport": {
    type: 'SqoopImport',   //修改
    text: 'Sqoop 输入',
    name: 'Sqoop输入',
    imgUrl: "./img/32/sqoop-import.png",
    imgData: require('../designPlatform/TransPlatform/img/svg/sqoop-import.svg'),
    radius: '8',
    typeFeil: 'job',
    model: '03',
    empowerApi: "/cloud/jobtype/SqoopImport"
  },
  //不存在的情况
  "UNKNOWN": {
    type: 'UNKNOWN',   //修改
    text: '未知',
    name: '未知',
    imgUrl: "./img/32/UNKNOWN.png",
    imgData: require('../designPlatform/JobPlatform/img/32/UNKNOWN.png'),
    radius: '8',
    typeFeil: 'UNKNOWN',
    model: 'UNKNOWN'
  },
  "SHELL": {
    type: 'Shell',   //修改 分类/传递给后台
    text: 'Shell', //用于显示
    name: 'Shell', // 同text
    imgUrl: "./img/32/SHL.png", // 图片
    imgData: require('../designPlatform/JobPlatform/img/32/SHL.png'), // 图片
    radius: '8', // 圆角 
    typeFeil: 'job', // 类型 trans
    model: '03', // 同工作平台的分类
    empowerApi: "/cloud/jobtype/SHELL" // 权限控制
  },

};

export default Tools
