/**
 * Created by Administrator on 2017/4/12.
 */
import React from 'react';
import { connect } from "dva";
import Style from './ToolsItem.css';
import Tools from './tools.config';


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

    return (
      <div className={Style.div+" "+Style[this.props.type]} onDoubleClick={()=>{return false}} draggable onDragStart={e=>{this.drag(e,this.props.type)}} >
        <span>{Tools[this.props.type].name}</span>
      </div>
    )
  }
}

export default connect()(ToolsItem)
