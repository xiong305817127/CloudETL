/**
 * Created by Administrator on 2017/7/1.
 */
import React from 'react';
import { connect } from 'dva';
import {Dropdown,Menu,Row,Col } from 'antd';
import Style from './NewTask.css';

const NewTask = ({text,handleNewTask})=>{


  return(
    <div className={Style.NewTask} onClick={handleNewTask}>
      <Row  >
        <Col span={8} className={Style.detail}>&nbsp;</Col>
        <Col span={16}  className={Style.detailtask}>新建{text}</Col>
      </Row>
    </div>
  )
};

export  default connect()(NewTask);
