/**
 * Created by Administrator on 2017/6/30.
 */
import React from 'react';
import {Layout,Row,Col} from 'antd';
import { Router, Route, Link, hashHistory } from 'react-router';
const {Content} = Layout;
import { SITE_CUSTOM_THEME } from 'constants';
import AppSilder from './taskCenter/AppSilder'
import ControlPlatform from './taskCenter/controlPlatform/ControlPlatform';


import Style from './TaskCenter.css'


const TaskCenter = ({ children})=>{
  


  return(
    <div id="TaskCenter">

        <Row>
            <Col span={4}  xl={3}>
                  <div>
                    <AppSilder />
                  </div>
            </Col>
            <Col span={20} xl={21}>
              <Content className={Style.Content}>
                { children }
              </Content>
            </Col>
        </Row>
    </div>
  )
}

export default  TaskCenter;
