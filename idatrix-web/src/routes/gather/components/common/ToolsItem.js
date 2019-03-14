/**
 * Created by Administrator on 2017/4/12.
 */
import React from 'react'
import { connect } from "dva"
import Style from './ToolsItem.css'
import Tools from "../config/Tools"

class ToolsItem extends React.Component{
  constructor(props){
    super(props);
    this.state = {
      dispatch:props.dispatch
    }
  }


  drag(e,type){
    e.dataTransfer.setData('type',type);
  }

  render(){
    const { type } = this.props;

    return (
      <div  className={Style.div} onDoubleClick={()=>{return false}} draggable onDragStart={e=>{this.drag(e,type)}} >
          <img className={Style.img} src={Tools[type].imgData} title={Tools[type].text} draggable="false" />
          <div className={Style.name} draggable="false">{Tools[type].name}</div>
      </div>
    )
  }
}

export default connect()(ToolsItem)
