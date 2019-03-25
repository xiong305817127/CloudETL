/**
 * Created by pwj on 2018/2/5.
 */
//trans配置

//流程操作界面显示配置
export const defaultTransSettings = {
    Endpoint: ["Dot", {radius: 1}], //这个是控制连线终端那个小点的半径
    Connector:"StateMachine", //这个就是大类了
    PaintStyle : { lineWidth : 2, stroke : "#5c96bc",fill:"none" },
    HoverPaintStyle: {stroke: "#1e8151", line: 2 },//这个是鼠标放在连线上显示的效果宽度
    Anchor: "Continuous",
    ConnectionOverlays: [
      [ "Arrow", {
        location: 1,
        id: "arrow",
        width:10,
        length: 5,
        foldback: 0.1 //这些都是控制箭头的形状的
      } ],
      [ "Label", { label: " ", id: "label", cssClass: "aLabel" }]//这个是鼠标拉出来的线的属性
    ]
};

//起点默认配置
export const sourceTransConfig = {
  anchor: "Continuous",
  paintStyle : { lineWidth : 2, stroke : "#5c96bc",fill:"none" },
  connectorStyle: { stroke: "#5c96bc", strokeWidth: 1, outlineColor: "transparent", outlineWidth: 1 },
  connectionType:"basic",
  extract:{
    "action":"the-action"
  }
};

export const targetTransConfig = {
  dropOptions: { hoverClass: "dragHover" },
  anchor: "Continuous",
  allowLoopback: true
};

export const basicType = {
  connector: "StateMachine",
  paintStyle: { strokeStyle: "red", lineWidth: 4 },
  hoverPaintStyle: { strokeStyle: "blue" },
  overlays: [
    "Arrow"
  ]
};