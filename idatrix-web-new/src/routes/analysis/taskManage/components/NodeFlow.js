/**
 * 节点流程图组件
 */

import React from 'react';
import { Layout,Form,Input,Icon } from  'antd';
import tools from './tools.config';

import Style from './Flow.css';

class NodeFlow extends React.Component{

  state = {
    items: []
  }

  componentWillMount() {
    this.updateStateByProps(this.props);
  }

  componentWillReceiveProps(nextProps) {
    this.updateStateByProps(nextProps);
  }

  updateStateByProps(props) {
    const { data, nodes } = props;
    const items = (data.stepList || []).map((it) => {
      const found = nodes.find(node => node.name == it.name) || {status:''};
      switch (found.status) {
        case 'PAUSE':
          it.dragClass = 'pauseStyle';
        break;
        case 'RUNNING':
          it.dragClass = 'runningStyle';
        break;
        case 'SUCCEEDED':
          it.dragClass = 'successStyle';
        break;
        case 'CANCELLED':
        case 'FAILED':
          it.dragClass = 'errStyle';
        break;
        case 'PREPARING':
          it.dragClass = 'pauseStyle';
        break;
      };
      return {
        id: `task-node-${it.id}`,
        dataId: it.id,
        dragClass: it.dragClass || 'dragNormal',
        imgUrl: tools[it.type].imgUrl,
        panel: it.type,
        text: it.name,
        x: parseInt(it.location.xloc),
        y: parseInt(it.location.yloc),
      }
    });
    this.setState({ items }, () => this.connectNode());
  }

  // 连接节点
  connectNode() {
    const { data } = this.props;
    const { items } = this.state;

    const Instance = jsPlumb.getInstance({
      Endpoint: ["Dot", {radius: 1}], //这个是控制连线终端那个小点的半径
      Connector:"StateMachine", //这个就是大类了
      PaintStyle : { stroke: "#5c96bc", strokeWidth: 1, outlineColor: "transparent", outlineWidth: 1 },
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
      Container: 'workspace_container',
    });
    Instance.registerConnectionType("basic", { anchor:"Continuous", connector:"StateMachine" });
    (data.hopList || []).forEach((line) => {
      const result1 = items.find(it => it.text === line.from);
      const result2 = items.find(it => it.text === line.to);
      Instance.connect({ source: result1.id, target: result2.id, type:"basic" });
    });
  }

  render() {

    return(
      <div className={Style.mainContent}>
        <div id="workspace_container" className={Style.divContent +" workspace_contain2"} style={{height: 500}}>
          {this.state.items.map(item=> {
            return (
              <div className={Style.drop+" "+item.dragClass}
                   id={item.id} key={item.id}
                   style={{left:item.x,top:item.y, cursor: 'default'}}>
                <img className={Style.img} src={item.imgUrl}/>
                <div className={Style.span}>{item.text}</div>
              </div>
            )
          })}
        </div>
      </div>
    )
  }
}

export default NodeFlow;
