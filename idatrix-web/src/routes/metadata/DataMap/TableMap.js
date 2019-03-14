/**
 * 数据地图
 */
import React from 'react';
import { withRouter } from 'react-router';
import { connect } from 'dva';
import { Checkbox, Table, Row, Col, Alert, Tabs, Button } from 'antd';
import DataMapGraph from 'components/DataMap';
import { deepCopy } from 'utils/utils';
import Search from './components/Search';
import BaseInfo from './components/TableBaseInfo';
import FieldsList from './components/FieldsList';
import { delayTime } from "config/datamap.config.js"

import Style from './style.less';

const TabPane = Tabs.TabPane;
let Timer = null;

class AppPage extends React.Component {

  constructor(props,context){
    super(props,context);
    this.state = {
      baseInfoShow: false,
      relationShow: false,
      currentType: '',
      currentNode: {},
      currentLink: {
        source: {},
        target: {},
      },
      relationColumns: [
        {
          title: 'A表字段列表',
          dataIndex: 'field1',
        },
        {
          title: 'B表字段列表',
          dataIndex: 'field2',
        },
        {
          title: '关联关系描述',
          dataIndex: 'desc',
        },
      ],
    }
  }

  componentWillMount() {
    this.reload();
  }

  componentWillReceiveProps(nextProps) {
    const { tablesForceData } = nextProps.DataMap;
    if (!this.state.currentType) {
      if (this.props.params.targetId && tablesForceData.links[0]) {
        this.setState({
          currentType: 'link',
          currentLink: {
            source: { id: tablesForceData.links[0].source },
            target: { id: tablesForceData.links[0].target },
          },
        });
      } else if (tablesForceData.nodes[0]) {
        this.setState({
          currentType: 'node',
          currentNode: {
            id: tablesForceData.nodes[0].id,
          },
        });
      }
    }
  }

  // 重新加载数据
  reload = () => {
    const { dispatch } = this.props;
    const { sourceId, targetId } = this.props.params;
    const sourceGuid = (sourceId || '.').split('.');
    const targetGuid = (targetId || sourceId || '.').split('.'); // 点击节点进来时，没有targetId
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
    if (targetId) {
      this.setState({
        schemaColumns: [
          {
            title: sourceGuid[1],
            dataIndex: 'sourceNameCn',
          },
          {
            title: targetGuid[1],
            dataIndex: 'targetNameCn',
          },
        ],
      });
    } else {
      this.setState({
        schemaColumns: [
          {
            title: '数据库中的表',
            dataIndex: 'nameCn',
          },
        ],
      });
    }
  }

  // 点击搜索
  handleSearchSelect = (value) => {
    console.log(value);
  }

  handleClickNode = (value) => {
    if(Timer){
      clearTimeout(Timer);
      Timer = null;
    }
    Timer = setTimeout(()=>{
      const { dispatch } = this.props;
      const guid = (value.guid || '...').split('.');
      this.setState({
        baseInfoShow: true,
        currentType: 'node',
        currentNode: value,
      }, () => {
        dispatch({
          type: 'DataMap/getTableDetail',
          payload: {
            guid: {
              system: guid[0],
              database: guid[1],
              schema: guid[2],
              table: guid[3],
            },
            level: 40,
          },
        });
        dispatch({
          type: 'DataMap/getFieldsOfTable',
          payload: {
            guid: {
              system: guid[0],
              database: guid[1],
              schema: guid[2],
              table: guid[3],
            },
            level: 80,
          },
        });
      });
    },delayTime);
  }

  // 单击链接
  handleClickLink = (value) => {
    if(Timer){
      clearTimeout(Timer);
      Timer = null;
    }
    Timer = setTimeout(()=>{
      const { dispatch } = this.props;
      const { source, target } = value;
      const sourceGuid = source.id.split('.');
      const targetGuid = target.id.split('.');
      this.setState({
        relationShow: true,
        currentType: 'link',
        currentLink: value,
        relationColumns: [
          {
            title: `${value.source.table_name_cn}表字段列表`,
            dataIndex: 'field1',
          },
          {
            title: `${value.target.table_name_cn}表字段列表`,
            dataIndex: 'field2',
          },
          {
            title: '关联关系描述',
            dataIndex: 'desc',
          },
        ],
      }, () => {
        dispatch({
          type: 'DataMap/getFieldRelation',
          payload: {
            levelType: 80,
            startGuid: {
              system: sourceGuid[0],
              database: sourceGuid[1],
              table: sourceGuid[3],
            },
            endGuid: {
              system: targetGuid[0],
              database: targetGuid[1],
              table: targetGuid[3],
            },
          },
        });
      });
    },delayTime);
  }

  // 双击链接
  handleDblClickLink = (value) => {
    if(Timer){
      clearTimeout(Timer);
      Timer = null;
    }
    this.props.router.push({
      pathname: `/DataMap/fields/${value.source.id}/${value.target.id}`,
    });
  };

  // 选择血缘
  handleChangeBlood = (checked) => {
    const { dispatch } = this.props;
    const { sourceId } = this.props.params;
    const { currentNode, currentLink, currentType } = this.state;
    const sourceGuid = (sourceId || '.').split('.');
    const guid = (currentType === 'node' ? currentNode.id : currentLink.source.id).split('.');
    const table = guid[guid.length - 1];
    if (checked) {
      this.setState({
        currentType: 'node',
        currentNode: { id: guid.join('.') },
      });
      dispatch({
        type: 'DataMap/getTableRelation',
        payload: {
          levelType: 40,
          needCount: 1,
          querySibshipAndImpact: true,
          endGuid: {
            system: sourceGuid[0],
            database: sourceGuid[1],
            table,
          },
        },
      });
    } else {
      this.reload();
    }
  }

  // 选择影响
  handleChangeEffect = (checked) => {
    const { dispatch } = this.props;
    const { sourceId } = this.props.params;
    const { currentNode, currentLink, currentType } = this.state;
    const sourceGuid = (sourceId || '.').split('.');
    const guid = (currentType === 'node' ? currentNode.id : currentLink.source.id).split('.');
    const table = guid[guid.length - 1];
    if (checked) {
      this.setState({
        currentType: 'node',
        currentNode: { id: guid.join('.') },
      });
      dispatch({
        type: 'DataMap/getTableRelation',
        payload: {
          levelType: 40,
          needCount: 1,
          querySibshipAndImpact: true,
          startGuid: {
            system: sourceGuid[0],
            database: sourceGuid[1],
            table,
          },
        },
      });
    } else {
      this.reload();
    }
  }

  render() {
    const { currentType, currentNode, currentLink } = this.state;
    const { tablesForceData, fieldsForceData, fieldsOfTable, tableDetail } = this.props.DataMap;
    const tables = deepCopy(tablesForceData);
    const dataSource = this.props.params.targetId ? tables.links.map((link, index) => ({
      key: index,
      sourceNameCn: tables.nodes.find(node => node.guid === link.source).table_name_cn,
      targetNameCn: tables.nodes.find(node => node.guid === link.target).table_name_cn,
    })) : tables.nodes.map((node, index) => ({
      key: index,
      nameCn: node.table_name_cn,
    }));
    const fieldsSource = fieldsForceData.links.map((link, index) => {
      const guid1 = (link.source || '....').split('.');
      const guid2 = (link.target || '....').split('.');
      return {
        key: index,
        field1: guid1[4],
        field2: guid2[4],
        desc: '',
      };
    });
    return (<div>
      <div className={Style.headWrap}>
        <Button
          icon="arrow-left"
          title="后退"
          onClick={()=>{this.context.router?this.context.router.goBack():this.props.history.goBack()}}
          style={{ float: 'right' }}
        />
        <Search
          onSelect={this.handleSearchSelect}
          placeholder="可以按数据库中英文名称或文件夹名称进行模糊搜索"
        />
        <Checkbox>在结果中搜索</Checkbox>
      </div>
      <Row style={{ border: '1px solid #eee' }}>
        <Col span={6}>
          <Table
            columns={this.state.schemaColumns}
            className="stripe-table"
            dataSource={dataSource}
            pagination={false}
          />
        </Col>
        <Col span={this.state.relationShow ? 12 : 18} style={{ border: '1px solid #eee' }}>
          <Alert
            message="提示：单击表名，下方显示表的基本信息；双击连线数字进入数据地图-表间字段关系界面"
            type="error"
            closable
          />
          <DataMapGraph
            type="table"
            nodesData={tables.nodes}
            linksData={tables.links}
            width="100%"
            height={this.state.baseInfoShow ? 400 : 600}
            onClickNode={this.handleClickNode}
            onClickLink={this.handleClickLink}
            onDblClickLink={this.handleDblClickLink}
            onChangeBlood={this.handleChangeBlood}
            onChangeEffect={this.handleChangeEffect}
            selection={{
              type: currentType,
              nodeId: currentNode.id,
              sourceId: currentLink.source.id,
              targetId: currentLink.target.id,
            }}
          />
          {this.state.baseInfoShow ? (
            <Tabs style={{ borderTop: '1px solid #eee' }}>
              <TabPane tab="基本信息" key="1">
                <BaseInfo data={tableDetail} />
              </TabPane>
              <TabPane tab="字段列表" key="2">
                <FieldsList dataSource={fieldsOfTable} />
              </TabPane>
            </Tabs>
          ) : null}
        </Col>
        {this.state.relationShow ? (
          <Col span={6}>
            <div className={Style.tableCaption}>表间关联字段列表</div>
            <Table
              columns={this.state.relationColumns}
              className="stripe-table"
              dataSource={fieldsSource}
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
