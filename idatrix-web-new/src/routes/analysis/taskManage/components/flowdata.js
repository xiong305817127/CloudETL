/**
 * Created by Administrator on 2017/4/20.
 */
import { Icon,message } from 'antd'
//import { add_hop,invert_hop,delete_hop,move_step } from '../../../../../services/gather'

const divIsExist = (id)=>{
  let exits = false;

  if(data.itemsId.length>0){
    for(let i=0;i<data.itemsId.length;i++){
      if(data.itemsId[i] === id){
        exits = true;
      }
    }
  }
  if(!exits){
    data.itemsId.push(id);
    return false;
  }else{
    return true;
  }
}

const initDiv = (id)=>{
  if(!divIsExist(id)){
    let  el = document.getElementById(id);
    if (el.draggable) {
      data.Instance.draggable(el,{
        containment:data.Instance.getContainer(),
        start: function () {
        },
        drag: function (event, ui) {
          jsPlumb.repaintEverything();
        },
        stop: function (even) {
          let obj = {};
          obj.x = even.finalPos[0];
          obj.y = even.finalPos[1];
          data.updateItemPos(even.el.id,obj);
          jsPlumb.repaintEverything();
        }
      });
    }
    data.Instance.makeSource(el, { //设置连接的源实体，就是这一头
      filter: "."+el.firstChild.className,
      anchor: "Continuous",
      connectorStyle: { stroke: "#5c96bc", strokeWidth: 1, outlineColor: "transparent", outlineWidth: 1 },
      connectionType:"basic",
      extract:{
        "action":"the-action"
      },
      Container:data.Instance.getContainer(),
    });
    data.Instance.makeTarget(el, {
      dropOptions: { hoverClass: "dragHover" },
      anchor: "Continuous",
      Container:data.Instance.getContainer(),
      allowLoopback: true,
    });
    data.Instance.fire("jsPlumbDemoNodeAdded", el);
  }
};


const getInstance = ()=>{
  return data.Instance;
}

const removeLine = (id) =>{
  for(let i =0;i<data.lines.length;i++){
    if(data.lines[i].connection.id === id){
      data.Instance.detach(data.lines[i]);
      data.lines.splice(i,1);
    }
  }
};

/*判断连线状态：重复与否*/
const getLineType = (key1,key2)=>{
  if(data.lines.length>0){
    for(let i=0;i<data.lines.length;i++){
      if(data.lines[i].sourceId === key1 && data.lines[i].targetId === key2 ){
        return "repeat";
      }else if(data.lines[i].targetId === key1 && data.lines[i].sourceId === key2){
        return "opposite";
      }
    }
  }else {
    return "normal"
  }
};

const setInstance = (paper)=>{
  if(getInstance()){
    data.Instance =  jsPlumb.getInstance({
      Endpoint: ["Dot", {radius: 1}], //这个是控制连线终端那个小点的半径
      Connector:"StateMachine", //这个就是大类了
      PaintStyle : { lineWidth : 3, stroke : "#ccc",fill:"red" },
      HoverPaintStyle: {stroke: "#1e8151", line: 2 },//这个是鼠标放在连线上显示的效果宽度
      ConnectionOverlays: [
        [ "Arrow", {
          location: 1,
          id: "arrow",
          width:10,
          length: 5,
          foldback: 0.1 //这些都是控制箭头的形状的
        } ],
        [ "Label", { label: " ", id: "label", cssClass: "aLabel" }]//这个是鼠标拉出来的线的属性
      ],
      Container: paper
    });
    data.Instance.registerConnectionType("basic", { anchor:"Continuous", connector:"StateMachine" });
    data.Instance.bind("connection", function (info) {
      data.lines.push(info);
      var timer = null;
      info.connection.getOverlay("label").canvas.onclick = function () {
        timer && clearTimeout(timer);
        timer = setTimeout(function(){
          removeLine(info.connection.id);
          data.Instance.connect({ source: info.targetId, target:info.sourceId, type:"basic" })
          message.success('连线转置成功');
        },300);
      }

      info.connection.getOverlay("label").canvas.ondblclick = function () {
        timer && clearTimeout(timer);
        removeLine(info.connection.id);
        message.success('连线删除成功');
      }
    });
    data.Instance.bind("beforeDrop", function (_ref) {
      if(_ref.sourceId === _ref.targetId ){
        return false;
      }else if(getLineType(_ref.sourceId,_ref.targetId) === "repeat"){
        message.success('连线不能重复');
        return false;
      }else if(getLineType(_ref.sourceId,_ref.targetId) === "opposite"){
        message.success('连线不能逆转');
        return false;
      }
      return true;
    });
    data.Instance.bind("drag", function (info) {
      console.log(info);
    });
    jsPlumb.fire("jsPlumbDemoLoaded", data.Instance);
  }
};



const getItem = (key)=>{
  for(var index of data.items){
    if(index.id === key){
      return index;
    }
  }
};

const deleteItemLines = (id)=>{

  for(let index of data.lines){
    if(index.targetId === id || index.sourceId ===id){
      try {
        data.Instance.detach(index);
      } catch (err) {}
    }
  }
  for(let i=0;i<data.lines.length; i++ ){
    if(data.lines[i].targetId === id || data.lines[i].sourceId ===id){
      data.lines.splice(i,1);
    }
  }
}

const updateItemsId = (args)=>{
  var newArgs = new Array();

  for(let m=0;m<data.itemsId.length;m++){
    for(let n=0;n<args.length;n++){
      if(data.itemsId[m] === args[n].id){
        newArgs.push(data.itemsId[m]);
        break;
      }
    }
  }

  return newArgs;
};

/*获得节点的名字*/
const getItemName = (id)=>{
  for(let index of data.items){
    if(index.id === id){
      return index.text
    }
  }
}

const nameExist = (text)=>{
  let bool = false;
  for(let index of data.itemsName){
    if(text === index){
      bool = true;
    }
  }
  return bool;
};

const getNewName =(text)=>{
  let newname = text;
  if(data.itemsName.length === 0){
    return text;
  }else{
    let i = 1;
    while(nameExist(newname)){
      i++;
      newname = text+i;
    }
    return newname;
  }
}



/*添加节点后的更新*/
const updateItems = (args,name)=>{
  data.itemsName.length = 0;
  data.items = args;

  data.itemsId =  updateItemsId(args);
  for(var index of data.items){
    initDiv(index.id);
  }
  for(var item of args){
    data.itemsName.push(item.text);
  }
  setViewName(name);
};

/*初始化所有数据*/
const initItems = (lines,items,dom,name)=>{

  data.status = "init";
  setViewName(name);
  data.items = items;
  setInstance(dom);

  for(var index of items){
    initDiv(index.id);
    data.itemsName.push(index.text);
  }

  // console.log(data.itemsName);
  for(var index1 of lines){
    data.getInstance().connect({ source: index1.sourceId, target:index1.targetId, type:"basic" })
  }
  data.status = "open";
};

/*重置所有数据*/
const resetData = ()=>{
  data.Instance.deleteEveryConnection();
  data.Instance.deleteEveryEndpoint();
  data.items.length = 0;
  if( data.lines.length>0){
    for(let index of  data.lines){
      try {
        data.Instance.detach(index);
      } catch (err) {}
    }
  }
  data.lines.length = 0;
  data.itemsId.length = 0;
  data.itemsName.length = 0;
  data.setViewName("");
}


const setViewName = (name)=>{
  data.viewname = name;
}

const getViewName = ()=>{
  return data.viewname;
}

const updateItemPos = (id,obj)=>{
    for(let index of data.items){
        if(index.id === id){
            index.x = obj.x;
            index.y = obj.y;
        }
    }
};

const getItems = ()=>{
  return data.items;
};

const getLines = ()=>{
  return data.lines;
};


const data = {
  status:"",
  updateItemPos:updateItemPos,


  viewname:"",
  setViewName:setViewName,
  getViewName:getViewName,
  items:[],
  getItems:getItems,
  getItemName:getItemName,
  itemsId:[],
  getItem:getItem,
  itemsName:[],
  getNewName:getNewName,
  lines:[],
  getLines:getLines,
  deleteItemLines:deleteItemLines,
  Instance:{},
  getInstance:getInstance,

  initItems:initItems,
  updateItems:updateItems,


  resetData:resetData
}

export default data;
