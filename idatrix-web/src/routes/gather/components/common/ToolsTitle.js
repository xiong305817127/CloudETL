/**
 * Created by Administrator on 2017/6/26.
 */
import React from 'react';
import { Row,Col } from 'antd'
import Style from './ToolsTitle.css'


export default class ToolsTitle extends React.Component{

    render(){
        return(
           <div className={Style.ToolsTitle} >
              <Row >
                  <Col span={8} className={Style[this.props.type]}>&nbsp;</Col>
                  <Col span={16}>{this.props.title}</Col>
              </Row>
           </div>
        )
    }
}
