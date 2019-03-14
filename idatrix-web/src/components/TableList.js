/**
 * TableList组件，基于antd Table组件
 * 整合分页处理、序号自动计算等功能
 * 用法及参数、配置与Table完全相同，可参看 https://ant.design/components/table-cn/
 *
 * 新增属性：
 * @prop  {boolean} showIndex 是否显示序号，将根据分页信息自动计算
 */
import React from 'react';
import { Table } from 'antd';
import { connect } from 'dva';
import qs from 'qs';
import PropTypes from 'prop-types';
import { DEFAULT_PAGE_SIZE } from '../constants';

let loadingTimer = 0;

const originSelectedRows = []; // selectRows起源
const originChangeRows = []; // changeRow起源

class TableList extends React.Component {
  static propTypes = {
    // 是否显示序号，将根据分页信息自动计算
    showIndex: PropTypes.bool,
  }
  state = {
    showIndex: true,
    loading: true,
    cachePagination: {},
    useRouter:true,        //是否采用路由控制
    pagination: {
      current: 1,
      pageSize: DEFAULT_PAGE_SIZE,
      showSizeChanger: true,
      showQuickJumper: true,
      pageSizeOptions: [
        '10', '20', '30', '40', '50', '60', '70', '80', '90', '100', '200', '300',
      ],
    },
  }
  componentDidMount() {
    const props = { ...this.props };
    const dataSource = props.dataSource;
    // 有数据时自动关闭loading
    if (dataSource && dataSource.length) {
      props.loading = false;
    }
    this.mergeStates(props);
    loadingTimer = setTimeout(() => {
      if (this.state.loading) {
        try {
          this.setState({ loading: false });
        } catch (err) {}
      }
    }, 2000);
  }
  componentWillReceiveProps(nextProps) {
    const props = { ...nextProps };
    props.loading = !!props.loading; // 如父组件没传入loading，则默认设置false

    this.mergeStates(props);
  }
  componentWillUnmount() {
    clearTimeout(loadingTimer);
  }
  /**
   * 生成序号列
   * @param  {object} states 组件状态
   * @return {object}        新状态
   */
  createIndex(states) {
    const { columns, dataSource } = states;
    let { current, pageSize } = this.state.cachePagination;
    current = current || this.state.pagination.current || 1;
    pageSize = pageSize || this.state.pagination.pageSize || DEFAULT_PAGE_SIZE;
    // 自动添加序号列
    if (columns && !columns.some(item => item.dataIndex === '__index')) {
      states.columns.unshift({
        dataIndex: '__index',
        title: '序号',
        width: 30,
        style: { textAlign: 'right' },
      });
    }
    // 计算序号
    if (dataSource && Array.isArray(dataSource)) {
      states.dataSource.map((row, index) => {
        row.__index = pageSize * (current - 1) + index + 1;
        return row;
      });
    }
    return states;
  }
  /**
   * 状态合并处理
   * @param  {object}  nextStates  用于合并的新状态
   */
  mergeStates(nextStates) {
    const newStates = { ...nextStates };
    const { query } = this.props.system;
    const { cachePagination, pagination,useRouter } = this.state;
    Object.assign(pagination, cachePagination, newStates.pagination);
    // 分页校正
    if (pagination && query.page && useRouter) {
      pagination.current = parseInt(query.page);
    } else if (cachePagination.current && pagination.current !== cachePagination.current) {
      pagination.current = cachePagination.current;
    }
    // 分页数校正
    if (pagination && query.pageSize  && useRouter) {
      pagination.pageSize = parseInt(query.pageSize);
    } else if (cachePagination.pageSize && pagination.pageSize !== cachePagination.pageSize) {
      pagination.pageSize = cachePagination.pageSize;
    }
    Object.assign(newStates, { pagination: newStates.pagination !== false ? pagination : false });
    if (newStates.showIndex) {
      this.createIndex(newStates);
    }
    newStates.className = (newStates.className || '') + ' stripe-table th-nowrap';
    this.setState(newStates);
  }

  // 代理rowSelection，以便清理分页selected
  createRowSelection() {
    if (!this.props.rowSelection) return null;
    const rowSelection = { ...this.props.rowSelection };
    rowSelection.selectedRowKeys = rowSelection.selectedRowKeys || [];
    if (typeof rowSelection.onChange === 'function') {
      rowSelection.onChange = (selectedRowKeys, selectedRows) => {
        originSelectedRows.splice(0);
        selectedRows.forEach(row => originSelectedRows.push(row));
        this.props.rowSelection.onChange(selectedRowKeys, originSelectedRows);
      };
    }
    if (typeof rowSelection.onSelect === 'function') {
      rowSelection.onSelect = (record, selected, selectedRows) => {
        originSelectedRows.splice(0);
        selectedRows.forEach(row => originSelectedRows.push(row));
        this.props.rowSelection.onSelect(record, selected, originSelectedRows);
      };
    }
    if (typeof rowSelection.onSelectAll === 'function') {
      rowSelection.onSelectAll = (selected, selectedRows, changeRows) => {
        originSelectedRows.splice(0);
        selectedRows.forEach(row => originSelectedRows.push(row));
        originChangeRows.splice(0);
        changeRows.forEach(row => originChangeRows.push(row));
        this.props.rowSelection.onSelectAll(selected, originSelectedRows, originChangeRows);
      };
    }
    if (typeof rowSelection.onSelectInvert === 'function') {
      rowSelection.onSelectInvert = (selectedRows) => {
        originSelectedRows.splice(0);
        selectedRows.forEach(row => originSelectedRows.push(row));
        this.props.rowSelection.onSelectInvert(originSelectedRows);
      };
    }
    return rowSelection;
  }

  handleChange(pagination) {
    // 清理rowSelection
    const rowSelection = this.state.rowSelection;
    if (rowSelection && rowSelection.selectedRowKeys) {
      rowSelection.selectedRowKeys.splice(0);
    }
    if (rowSelection && rowSelection.selectedRows) {
      rowSelection.selectedRows.splice(0);
    }
    originSelectedRows.splice(0);

    this.setState({ cachePagination: pagination, loading: true });
    if (typeof this.props.onChange === 'function') {
      this.props.onChange(pagination);
    } else { // 若无自定义onChange事件，则自动通过路由传递分页信息
      const { pathname, query, _k } = this.props.system;
      let hash = `#${pathname}?`;
      query.page = pagination.current;
      query.pageSize = pagination.pageSize;
      hash += qs.stringify(query);
      hash += _k ? `&_k=${_k}` : '';
      window.location.hash = hash;
    }
  }

  render() {
    return (<Table {...this.state} onChange={this.handleChange.bind(this)} />);
  }
}
export default connect(({ system }) => ({
  system,
}))(TableList);
