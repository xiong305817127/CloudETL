import React from 'react';
import { connect } from 'dva';
import { Row, Col, Button } from 'antd';
import { withRouter } from 'react-router';
import Style from './GridModule.css';
import { inletDecorator } from '../homeDecorator';

@inletDecorator
class LittleModule extends React.Component {

  state = {
    inletsConfig: {},
  }

  /**
   * 生成子系统入口（表格列）
   * @param  {string}  sysName 系统入口名
   * @return {object}          返回Table.Col对象
   */
  createCol(sysName) {
    const col = this.props.inletsConfig[sysName];
    if (!col) return null;
    return (<Col key={col.index} span={6} className={Style.BoxOut}>
      <div className={Style.BoxIn} onClick={() => col.allow && this.props.openSystem(col.path)}>
        <img src={col.icon} />
        <h2 className={Style.title}>{col.title}</h2>
        <p className={Style.desc}>{col.desc}</p>
        <Button className={Style.detail} type="primary" disabled={!col.allow}>进入</Button>
      </div>
    </Col>);
  }

  render() {
    const { account: { sysList }, inletsConfig } = this.props;
    const inletList = Object.keys(inletsConfig).map(key => ({
      id: key,
    }));

    let rowList1 = []; // 获取第一行数组
    let rowList2 = []; // 获取第二行数组

    if (inletList.length > 4 && inletList.length <= 6) {
      // 只有5-6个时，上下两行各排3个
      rowList1 = inletList.slice(0, 3);
      rowList2 = inletList.slice(3, 6);
    } else {
      rowList1 = inletList.slice(0, 4);
      rowList2 = inletList.slice(4, 8);
    }

    return (
      <div id="ContentBox">
        <Row type="flex" justify="space-around" className={Style.BoxMargin}>
          {rowList1.map((col) => (this.createCol(col.id, col.allow)))}
        </Row>
        {rowList2.length > 0 ? (
        <Row type="flex" justify="space-around" className={Style.BoxMargin}>
          {rowList2.map((col) => (this.createCol(col.id, col.allow)))}
        </Row>
        ) : null}
      </div>
    )
  }
}

export default connect(({account, system})=>({
  account,
  system,
}))(withRouter(LittleModule));

