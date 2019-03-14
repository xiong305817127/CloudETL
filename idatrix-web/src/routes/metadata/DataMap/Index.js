/**
 * 数据地图
 */
import React from 'react';
import { withRouter } from 'react-router';
import { connect } from 'dva';
import { Checkbox, Table, Row, Col, Alert } from 'antd';
import DataMapGraph from 'components/DataMap';
import Search from './components/Search';

import Style from './style.less';
import { delayTime } from "config/datamap.config.js"

// 导入icon
import icon1 from 'assets/images/datamap/db1.png';
import icon2 from 'assets/images/datamap/db2.png';
import icon3 from 'assets/images/datamap/db3.png';
import icon4 from 'assets/images/datamap/db4.png';
import icon5 from 'assets/images/datamap/db5.png';

//利用定时器鉴别双击
let Timer = null;

const nodeColumns = [{
  title: '表中文名',
  dataIndex: 'table_name_cn',
}, {
  title: '表英文名',
  dataIndex: 'table_name_en',
}];

class AppPage extends React.Component {

  state = {
    columns: nodeColumns,
    currentType: '',
    currentNode: {},
    currentLink: {},
    dataSource: [],
  }

  // 点击搜索
  handleSearchSelect = (value) => {
    console.log(value)
  }

  // 点击节点
  handleClickNode = (value) => {
    if(Timer){
      clearTimeout(Timer);
      Timer = null;
    }
    Timer = setTimeout(()=>{
      const { dispatch } = this.props;
      const guid = (value.guid || '...').split('.');
      this.setState({
        currentType: 'node',
        currentNode: value,
        columns: nodeColumns,
        dataSource: [],
      }, () => {
        dispatch({
          type: 'DataMap/getTablesOfDB',
          payload: {
            guid: {
              system: guid[0],
              database: guid[1],
            },
            level: 40,
          },
        });
      });
    },delayTime);
  }

  // 点击链接
  handleClickLink = (value) => {
    console.log("单击");
    if(Timer){
        clearTimeout(Timer);
        Timer = null;
    }
    Timer = setTimeout(()=>{
      const { dispatch } = this.props;
      const sourceGuid = (value.source.id || '.').split('.');
      const targetGuid = (value.target.id || '.').split('.');
      this.setState({
        currentType: 'link',
        currentLink: value,
        columns: [{
          title: value.source.name,
          dataIndex: 'sourceNameCn',
        }, {
          title: value.target.name,
          dataIndex: 'targetNameCn',
        }],
        dataSource: [],
      }, () => {
        dispatch({
          type: 'DataMap/getTableRelation',
          payload: {
            levelType: 40,
            needCount: 1,
            startGuid: {
              system: sourceGuid[0],
              database: sourceGuid[1],
            },
            endGuid: {
              system: targetGuid[0],
              database: targetGuid[1],
            },
          },
        });
      });
    },delayTime);
  }

  // 双击节点
  handleDblClickNode = (value) => {
    console.log("双击");
    if(Timer){
      clearTimeout(Timer);
      Timer = null;
    }
    this.props.router.push({
      pathname: `/DataMap/table/${value.id}`,
    });
  }

  // 双击链接
  handleDblClickLink = (value) => {
    console.log("双击");
    if(Timer){
        clearTimeout(Timer);
        Timer = null;
    }
    this.props.router.push({
      pathname: `/DataMap/table/${value.source.id}/${value.target.id}`,
    });
  }

  render() {
    const { DBForceData, tablesOfDB, tablesForceData } = this.props.DataMap;
    const { columns, currentType, currentNode, currentLink } = this.state;
    const dataSource = currentType === 'node' ? tablesOfDB
      : tablesForceData.links.map((link, index) => ({
        key: index,
        sourceNameCn: tablesForceData.nodes.find(node => node.guid === link.source).table_name_cn,
        targetNameCn: tablesForceData.nodes.find(node => node.guid === link.target).table_name_cn,
      }));

    return (<div>
      <div
        className="padding_20"
      >
        <Search
          onSelect={this.handleSearchSelect}
          placeholder="可以按数据库中、英文名或文件夹名称进行模糊搜索"
        />
        <Checkbox>在结果中搜索</Checkbox>
      </div>
      <Row style={{ border: '1px solid #eee' }}>
        <Col span={currentType ? 18 : 24} style={{ borderRight: '1px solid #eee' }}>
          <Alert
            message="提示：数据库间的连线数字表示关联表的数量；双击数据库或连线数字进入数据表视图"
            type="error"
            closable
            style={{width:"90%",marginLeft: '20px'}}
          />

          <DataMapGraph
            type="db"
            nodesData={DBForceData.nodes}
            linksData={DBForceData.links}
            width="100%"
            height={600}
            onClickNode={this.handleClickNode}
            onDblClickNode={this.handleDblClickNode}
            onClickLink={this.handleClickLink}
            onDblClickLink={this.handleDblClickLink}
          />
          <Row className={Style.iconsWrap}>
            <Col span={4}><img className={Style.dbicon} src={icon1} alt="" /> MySql数据库</Col>
            <Col span={4}><img className={Style.dbicon} src={icon2} alt="" /> Hive数据库</Col>
            <Col span={4}><img className={Style.dbicon} src={icon3} alt="" /> Hbase数据库</Col>
            <Col span={4}><img className={Style.dbicon} src={icon4} alt="" /> 前置机数据库</Col>
            <Col span={4}><img className={Style.dbicon} src={icon5} alt="" /> 外租户数据库</Col>
          </Row>
        </Col>
        {currentType ? (
          <Col span={6}>
            {currentType === 'node' ? (
              <div className={Style.tableCaption}>{currentNode.dbName}的表</div>
            ) : (
              <div className={Style.tableCaption}>
                {currentLink.source.dbName}与{currentLink.target.dbName}之间关联的表
              </div>
            )}
            <Table
              columns={columns}
              className="stripe-table"
              dataSource={dataSource}
              pagination={false}
            />
          </Col>
        ) : null}
      </Row>
    </div>);
  }
}

export default connect(({ DataMap }) => ({
  DataMap: DataMap.toJS(),
}))(withRouter(AppPage));
