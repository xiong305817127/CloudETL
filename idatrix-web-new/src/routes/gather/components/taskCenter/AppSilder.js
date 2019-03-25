/**
 * Created by pwj on 2017/6/26.
 */

import React from 'react';
import { Link } from 'react-router';
import { connect } from 'dva'
import { Row,Col } from "antd"
import Style from './AppSilder.css'

const AppSilder = ({ taskcontent })=>{
    const { model,serverTotal,total } = taskcontent;

  return(
    <div className={Style.AppSilder}>
      <div className={Style.rowHeader}>
        <Link to="/gather/taskcenter/transcenter">
          <Row className={Style.HeaderRow+" "+Style[model ==="trans"?"changeClick":"change"]} >
            <Col span={8} className={Style.Col1}>&nbsp;</Col>
            <Col span={16} className={Style.Col2}>转换任务</Col>
          </Row>
        </Link>
        <Link to="/gather/taskcenter/jobscenter">
          <Row className={Style.HeaderRow+" "+Style[model ==="job"?"controlClick":"control"]} >
            <Col span={8} className={Style.Col1}>&nbsp;</Col>
            <Col span={16} className={Style.Col2}>调度任务</Col>
          </Row>
        </Link>
      </div>
      <div className={Style.rowContent}>
        <Row>
          <Col span={16}>{model ==="trans"?"转换":"调度"}流程总数 :</Col>
          <Col span={8} style={{textAlign:"right"}}>{total}</Col>
        </Row>
        <Row>
          <Col span={16} >服务器总数 :</Col>
          <Col span={8} style={{textAlign:"right"}}>{serverTotal}</Col>
        </Row>
      </div>
      <div className={Style.rowContent}>
        <div className={Style.status}>
          <Row>
            <Col span={4} className={Style.circleCol}>
              <div className={Style.circle+" initStatusBg"}>&nbsp;</div>
            </Col>
            <Col span={14} >等待执行</Col>
          </Row>
          <Row>
            <Col span={4} className={Style.circleCol}>
              <div className={Style.circle+" runStatusBg"}>&nbsp;</div>
            </Col>
            <Col span={14} >执行中</Col>
          </Row>
          <Row>
            <Col span={4} className={Style.circleCol}>
              <div className={Style.circle+" stopStatusBg"}>&nbsp;</div>
            </Col>
            <Col span={14} >告警状态</Col>
          </Row>
          <Row>
            <Col span={4} >
              <div className={Style.circle+" errorStatusBg"}>&nbsp;</div>
            </Col>
            <Col span={14} >执行故障</Col>
          </Row>
        </div>
      </div>
    </div>
  )
};


export default connect(({taskcontent})=>({
  taskcontent
}))(AppSilder);
