/**
 * 数据地图
 */
import React from 'react';
import { withRouter } from 'react-router';
import { connect } from 'dva';
import { Checkbox, Table, Row, Col, Alert, Tabs, Button } from 'antd';
import DataMapGraph from 'components/DataMap';
import Search from './components/Search';

import Style from './style.less';

import BaseInfo from './components/TableBaseInfo';
import FieldsList from './components/FieldsList';

const TabPane = Tabs.TabPane;

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
      relationDesc: '',
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
    const { dispatch } = this.props;
    const { sourceId, targetId } = this.props.params;
    const sourceGuid = sourceId.split('.');
    const targetGuid = targetId.split('.');
    dispatch({
      type: 'DataMap/getFieldRelation',
      payload: {
        levelType: 80,
        startGuid: {
          system: sourceGuid[0],
          database: sourceGuid[1],
          schema: sourceGuid[2],
          table: sourceGuid[3],
        },
        endGuid: {
          system: targetGuid[0],
          database: targetGuid[1],
          schema: targetGuid[2],
          table: targetGuid[3],
        },
      },
    });
    if (targetId) {
      this.setState({
        schemaColumns: [
          {
            title: '数据库1',
            dataIndex: 'sourceMetaNameCn',
          },
          {
            title: '数据库2',
            dataIndex: 'targetMetaNameCn',
          },
        ],
      });
    } else {
      this.setState({
        schemaColumns: [
          {
            title: '数据库中的表',
            dataIndex: 'metaNameCn',
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
    console.log(value);
  }

  handleClickLink = (value) => {
    const { dispatch } = this.props;
    const { source, target } = value;
    const sourceGuid = source.id.split('.');
    const targetGuid = target.id.split('.');
    this.setState({
      relationShow: true,
      baseInfoShow: true,
      currentLink: value,
      relationDesc: value.fieldRelationDesc || '',
      relationColumns: [
        {
          title: `${sourceGuid[3]}表字段列表`,
          dataIndex: 'field1',
        },
        {
          title: `${targetGuid[3]}表字段列表`,
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
  }

  handleDblClickNode = (value) => {
  }

  handleDblClickLink = (value) => {
  }

  render() {
    const { fieldsForceData } = this.props.DataMap;
    const fieldsSource = fieldsForceData.links.map((link, index) => {
      const guid1 = (link.source.id || link.source || '....').split('.');
      const guid2 = (link.target.id || link.target || '....').split('.');
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
        <Col span={this.state.relationShow ? 18 : 24} style={{ border: '1px solid #eee' }}>
          {/*<Alert
            message="提示：可以双击一个字段，把字段所在表定位到屏幕中心"
            type="error"
            closable
          />*/}
          <DataMapGraph
            type="field"
            nodesData={fieldsForceData.nodes}
            linksData={fieldsForceData.links}
            width="100%"
            height={this.state.baseInfoShow ? 400 : 600}
            onClickNode={this.handleClickNode}
            onDblClickNode={this.handleDblClickNode}
            onClickLink={this.handleClickLink}
            onDblClickLink={this.handleDblClickLink}
          />
          {this.state.baseInfoShow ? (
            <Tabs style={{ borderTop: '1px solid #eee' }}>
              <TabPane tab="字段关联详细信息" key="1">
                <pre style={{ minHeight: 100, padding: '0 10px' }}>{this.state.relationDesc}</pre>
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
