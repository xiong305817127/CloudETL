/**
 * jsplumb流程图默认配置
 */
export const defaultSettings = {
    Endpoint: ["Dot", {radius: 1}], //这个是控制连线终端那个小点的半径
    Connector:["Flowchart",{midpoint:0.1}], //这个就是大类了
    PaintStyle : { lineWidth : 2, stroke : "#ccc",fill:"none" },
    HoverPaintStyle: {stroke: "RGB(64,185,252)", line: 2 },//这个是鼠标放在连线上显示的效果宽度
    Anchor: ["Right","Left"],
    ConnectionOverlays: [
      [ "Arrow", {
        location: 1,
        id: "arrow",
        width:10,
        length: 5,
        foldback: 0.1 //这些都是控制箭头的形状的
      } ],
     // [ "Label", {label:"222",location:-30,cssClass: "aLabel" }]//这个是鼠标拉出来的线的属性
    ]
};

//起点默认配置
export const sourceConfig = {
  anchor:["Right"],
  extract:{
    "action":"the-action"
  },
};

export const targetConfig = {
  anchor:["Left"],
  maxConnections:1
};


//初始位置配置
export const initPos = {
    startX:40,
    startY:40,
    splitX:120,
    splitY:30,
    width:200,
    height:33
}


//mysql数值类型配置
export const intType = ["TINYINT","SMALLINT","MEDIUMINT","INT","BIGINT","INTEGER",
    "DECIMAL","NUMERIC","FLOAT","REAL","DOUBLE","BIT","NUMBER"];

//拖拽取值参数
export const params = ["column","fieldType","id","name","visible"];

//算法
export const calc = ["sum","count","min","max","avg","distinct count","distinct-count"];

//数据模型支持的数据库
export const databaseType = [
  {name:"MySql",value:3,port:3306},
  {name:"Oracle",value:2,port:1521},
  {name:"PostgreSQL",value:8,port:5432},
  {name:"DM",value:14,port:5236}
];


// {name:"PostgreSQL",value:8,port:5432},
// {name:"DM",value:14,port:5236},
// {name:"Hive",value:4,port:3306},
// {name:"Hbase",value:5,port:3306},