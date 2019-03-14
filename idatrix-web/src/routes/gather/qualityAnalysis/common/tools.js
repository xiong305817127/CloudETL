/**
 * Created by Administrator on 2017/3/7.
 */

const Tools = {
   "TableInput":{
     type:'TableInput',
     text:'表输入',
     name:"表",
     imgUrl : "./img/32/TIP.png",
     imgData:require('../../components/designPlatform/TransPlatform/img/svg/TIP.svg'),
     radius:'8',
     typeFeil:'trans',
     model:'0',
     empowerApi:"/cloud/transtype/TableInput"
   },
  "AccessInput":{
    type:'AccessInput',
    text:'Access输入',
    name:"Access",
    imgUrl : "./img/32/ACI.png",
    imgData:require('../../components/designPlatform/TransPlatform/img/svg/ACI.svg'),
    radius:'8',
    typeFeil:'trans',
    model:'0',
    empowerApi:"/cloud/transtype/AccessInput"
  },
  "CsvInput":{
    type:'CsvInput',
    text:'Csv文件输入',
    name:"Csv文件",
    imgUrl : "./img/32/CSV.png",
    imgData:require('../../components/designPlatform/TransPlatform/img/svg/CSV.svg'),
    radius:'8',
    typeFeil:'trans',
    model:'0',
    empowerApi:"/cloud/transtype/CsvInput"
  },
  "ExcelInput":{
    type:'ExcelInput',
    text:'Excel输入',
    name:"Excel",
    imgUrl : "./img/32/XLI.png",
    imgData:require('../../components/designPlatform/TransPlatform/img/svg/XLI.svg'),
    radius:'8',
    typeFeil:'trans',
    model:'0',
    empowerApi:"/cloud/transtype/ExcelInput"
  },
  "TextFileInput":{
    type:'TextFileInput',
    text:'文本文件输入',
    name:"文本文件",
    imgUrl : "./img/32/TFI.png",
    imgData:require('../../components/designPlatform/TransPlatform/img/svg/TFI.svg'),
    radius:'8',
    typeFeil:'trans',
    model:'0',
    empowerApi:"/cloud/transtype/TextFileInput"
  },
    
  "CharacterAnalysis": {
    type: 'CharacterAnalysis',   //标准值匹配
    text: '标准值匹配',
    name: '标准值匹配',
    imgUrl: "./img/32/inSmall.png",
    imgData: require('../../components/designPlatform/TransPlatform/img/32/inSmall.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '12',
    empowerApi:"/cloud/transtype/CharacterAnalysis"
  },
  "NumberAnalysis": {
    type: 'NumberAnalysis',   
    text: '电话匹配',
    name: '电话匹配',
    imgUrl: "./img/32/POTH.png",
    imgData: require('../../components/designPlatform/TransPlatform/img/32/POTH.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '12',
    empowerApi:"/cloud/transtype/NumberAnalysis"
  },
  "CertificatesAnalysis": {
    type: 'CertificatesAnalysis',   
    text: '证件匹配',
    name: '证件匹配',
    imgUrl: "./img/32/PHON.png",
    imgData: require('../../components/designPlatform/TransPlatform/img/32/PHON.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '12',
    empowerApi:"/cloud/transtype/CertificatesAnalysis"
  },
  "DateAnalysis": {
    type: 'DateAnalysis',   
    text: '日期匹配',
    name: '日期匹配',
    imgUrl: "./img/32/DATA.png",
    imgData: require('../../components/designPlatform/TransPlatform/img/32/DATA.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '12',
    empowerApi:"/cloud/transtype/DateAnalysis"
  },
  "CustomAnalysis": {
    type: 'CustomAnalysis',   
    text: '自定义匹配',
    name: '自定义匹配',
    imgUrl: "./img/32/inSmall.png",
    imgData: require('../../components/designPlatform/TransPlatform/img/32/inSmall.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '999', // 暂不开发
    empowerApi:"/cloud/transtype/CustomAnalysis"
  },
  "AnalysisReport": {
    type: 'AnalysisReport',   
    text: '报表生成',
    name: '报表生成',
    imgUrl: "./img/32/EXIC.png",
    imgData: require('../../components/designPlatform/TransPlatform/img/32/EXIC.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '12',
    empowerApi:"/cloud/transtype/AnalysisReport"
	},
	"Redundance": {
    type: 'Redundance',
    text: '冗余率',
    name: '冗余率',
    imgUrl: "./img/32/RDC.png",
    imgData: require('../../components/designPlatform/TransPlatform/img/32/RDC.png'),
    radius: '8',
    typeFeil: 'trans',
    model: '12',
    empowerApi: "/cloud/transtype/Redundance"
  },
};

export default Tools
