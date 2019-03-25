// 控件配置
export default {
  "spark": {
    type: 'spark',
    text: 'SparkSubmit',
    name: "SparkSubmit",
    imgUrl : require("./img/ACI.png"),
    radius: '8',
    empowerApi: '/task/definition/tasktype/sparksubmit',
  },
  "hadoopJava": {
    type: 'hadoopJava',
    text: 'HadoopJava',
    name: "HadoopJava",
    imgUrl : require("./img/CLG.png"),
    radius: '8',
    empowerApi: '/task/definition/tasktype/hadoopjava',
  },
  "command": {
    type: 'command',
    text: 'Command',
    name: "Command",
    imgUrl : require("./img/CLC.png"),
    radius: '8',
    empowerApi: '/task/definition/tasktype/command',
  },
  "hive": {
    type: 'hive',
    text: 'HiveJob',
    name: "HiveJob",
    imgUrl : require("./img/CSM.png"),
    radius: '8',
    empowerApi: '/task/definition/tasktype/hivejob',
  }
}
