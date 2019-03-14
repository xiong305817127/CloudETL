/**
 * 数据地图组件   暂时废弃
 */
import React from 'react';
import { Checkbox } from 'antd';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import Modal from 'components/Modal';

import TableList from '../../../components/TableList';
import DataMapChart from './DataMapChart';
import { querySanKey } from '../../../services/dataResource';
import Style from './DataMap.css';

class DataMap extends React.Component {

  state = {
    showBlood: false,
    // 数据地图配置
    mapOption: {
      data: [],
      links: [],
      selectedIds: [],
    }
  }

  // 字段表
  fieldsColumns = [
    {
      title: '字段名称',
      dataIndex: 'colName',
    }, {
      title: '描述',
      dataIndex: 'description',
    },
  ];

  // 关系表
  relationColumns = [
    {
      title: '表1字段名称',
      dataIndex: 'fcolName',
    }, {
      title: '表1字段代码',
      dataIndex: 'fcolCode',
    }, {
      title: '表2字段名称',
      dataIndex: 'scolName',
    }, {
      title: '表2字段代码',
      dataIndex: 'scolCode',
    }, {
      title: '关联关系描述',
      dataIndex: 'rsdescription',
    },
  ];

  componentWillReceiveProps(nextProps) {
    // 当组件由隐藏变为显示时执行
    if (nextProps.visible && !this.props.visible) {
      const { id } = this.props;
      this.setState({
        showBlood: false,
      });
      this.updateMap(id);
    }
  }

  // 血缘关系开关
  handleChangeBlood = (e) => {
    this.setState({
      showBlood: e.target.checked,
    });
  }

  // 地图事件处理
  handleMapClick = (data) => {
    const { dispatch } = this.props;
    const { mapOption } = this.state;
    if (data.source !== undefined && data.target !== undefined) { //点击的是边
      console.log(data);
      console.log(data.id,"点击dataID");

      dispatch({
        type: 'dataResource/getFieldRelation',
        payload: {
          id: data.id,
        },
      });
      mapOption.selectedIds = [data.sId, data.tId];
      this.setState({ mapOption, showBlood: true });
    } else { // 点击的是点
      this.setState({ showBlood: false });
      this.updateMap(data.value);
    }
  }

  // 更新数据地图
  updateMap = async (metaid) => {
    const { dispatch } = this.props;
    const { mapOption } = this.state;
    const { data } = await querySanKey({ metaid });
    const resData = data && data.data || {};
    const setData = mapOption.data;
    const setLinks = mapOption.links;
    setData.splice(0);
    setLinks.splice(0);
    (resData.nodes || []).forEach(item => {
      // 去重
      if (!setData.some(it => it.value === item.metaid || it.name === item.tableName)) {
        setData.push({
          name: item.tableName,
          value: item.metaid,
        });
      }
    });
    (resData.links || []).forEach(item => {
      let sourceIndex = -1, targetIndex = -1;
      setData.forEach((it, index) => {
        // if (it.value === item.metaid) {
        if (it.value === item.childId) {
          sourceIndex = index;
        }
        // if (it.value === item.childId) {
        if (it.value === item.metaid) {
          targetIndex = index;
        }
      });
      // 去重
      if (sourceIndex > -1 && targetIndex > -1 && !setLinks.some(it => it.source === sourceIndex && it.target === targetIndex)) {
        setLinks.push({
          source: sourceIndex,
          target: targetIndex,
          id: item.id,
          // sId: item.metaid,
          // tId: item.childId,
          sId: item.childId,
          tId: item.metaid,
          lineStyle: {
            normal: {
              color: item.rsType === 1 ? 'rgb(250,189,139)' : '#dddddd',
              type: item.rsType === 1 ? 'dashed' : 'solid',
            }
          }
        });
      }
    });
    mapOption.data = setData;
    mapOption.links = setLinks;
    mapOption.selectedIds = [metaid];
    this.setState({ mapOption });
    dispatch({
      type: 'dataResource/getMeta',
      payload: {
        metaid,
      }
    });
  }

  render() {
    const { showBlood } = this.state;
    const { viewTable, fieldRelations } = this.props.dataResource;
    return (<Modal
      title="查看数据地图"
      visible={this.props.visible}
      onOk={this.props.onClose}
      onCancel={this.props.onClose}
      width={800}
    >
      <section className={Style.mapWrap}>
        <div className={Style.toolsWrap}>
          {/*<Checkbox onChange={this.handleChangeBlood}>血缘分析</Checkbox>*/}
        </div>
        {this.state.mapOption.data.length > 0 ? (
          <DataMapChart
            data={this.state.mapOption.data}
            links={this.state.mapOption.links}
            selectedIds={this.state.mapOption.selectedIds}
            onClick={this.handleMapClick}
          />
        ) : null}
      </section>
      {!showBlood ? (
        <TableList
          rowKey='id'
          columns={this.fieldsColumns}
          dataSource={viewTable}
          pagination={false}
        />
      ) : (
        <TableList
          rowKey='id'
          columns={this.relationColumns}
          dataSource={fieldRelations}
          pagination={false}
        />
      )}
    </Modal>);
  }
}

DataMap.propTypes = {
  id: PropTypes.number.isRequired,
  visible: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default connect(({ dataResource }) => ({
  dataResource,
}))(DataMap);
