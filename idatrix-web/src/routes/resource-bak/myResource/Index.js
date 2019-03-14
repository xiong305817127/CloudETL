import React from 'react';
import { connect } from 'dva';
import { Input, Button, Row, Col, Tag, Radio, Tabs, Tooltip, Icon } from 'antd';
import { withRouter, Link } from 'react-router';
import TableList from 'components/TableList';
import { getLabelByTreeValue } from 'utils/metadataTools';
import ViewTable from '../components/ViewTable';
import ViewDir from '../components/ViewDir';
import DataMap from '../components/NewDataMap';
import Search from '../../../components/Search';
import { DEFAULT_PAGE_SIZE } from '../../../constants';
import Style from './style.css';

const TabPane = Tabs.TabPane;

class Result extends React.Component {

  state = {
    result: {},
    resultType: 'table',
    ownerType: '1',
    viewTable: [],
    viewTableVisible: false,
    viewDirVisible: false,
    viewDataMapVisible: false,
    viewDataMapId: -1,
    keyword: '',
    current: 1,
    search: false
  }

  tableColumns = [
    {
      title: '数据表名称',
      dataIndex: 'metaNameCn',
      width: '30%',
      render: (text, record) => <a href="#"
        onClick={(e) => this.handleView(record, e)}>{text}</a>,
    }, {
      title: '所属组织',
      dataIndex: 'dept',
      render: (text) => {
        const { allDepartmentsOptions } = this.props.resourcesCommonChange;
        return getLabelByTreeValue(text, allDepartmentsOptions) || '';
      },
    }, {
      title: '行业',
      dataIndex: 'industry',
      render: (text) => {
        const { industryOptions } = this.props.resourcesCommonChange;
        return getLabelByTreeValue(text, industryOptions) || text;
      },
    // }, {
    //   title: '主题',
    //   dataIndex: 'theme',
    //   render: (text) => {
    //     const { themeOptions } = this.props.resourcesCommonChange;
    //     return getLabelByTreeValue(text, themeOptions) || text;
    //   },
    }, {
      title: '标签',
      dataIndex: 'tag',
      render: (text) => {
        const { tagsOptions } = this.props.resourcesCommonChange;
        return getLabelByTreeValue(text, tagsOptions) || text;
      },
    }, {
      title: '备注',
      dataIndex: 'remark',
      width: '300px',
      render: (text) => text && text !== 'null' ? (<div className="word25" title={text}>{text}</div>) : null,
    }, {
      title: '操作',
      width: 10,
      className: 'td-center',
      render: (text, record) => (<Tooltip title="数据地图">
        <a style={{fontSize: 16}}
          onClick={(e) => this.handleViewDataMap(record, e)}
        ><Icon type="global"></Icon></a>
      </Tooltip>)
    },
  ];

  fileColumns = [
    {
      title: '文件目录名称',
      dataIndex: 'dirName',
      width: '100px',
      render: (text, record) => <a href="#"
        onClick={(e) => this.handleDirView(record, e)}>{text}</a>,
    }, {
      title: '文件存在目录',
      dataIndex: 'storDir',
      width: '200px',
      render: (text) => {
        const { hdfsPlanList } = this.props.resourcesCommonChange;
        let result = [];
        try {
          const arr = typeof text === 'string' ? JSON.parse(text) : text;
          arr.forEach(id => {
            const found = hdfsPlanList.find(it => it.value == id);
            if (found) result.push(found.label);
          });
        } catch (err) {}
        return result.join('/');
      },
    }, {
      title: '所属组织',
      dataIndex: 'dept',
      render: (text) => {
        const { allDepartmentsOptions } = this.props.resourcesCommonChange;
        return getLabelByTreeValue(text, allDepartmentsOptions) || '';
      },
    }, {
      title: '行业',
      dataIndex: 'industry',
      render: (text) => {
        const { industryOptions } = this.props.resourcesCommonChange;
        return getLabelByTreeValue(text, industryOptions) || text;
      },
    // }, {
    //   title: '主题',
    //   dataIndex: 'theme',
    //   render: (text) => {
    //     const { themeOptions } = this.props.resourcesCommonChange;
    //     return getLabelByTreeValue(text, themeOptions) || text;
    //   },
    }, {
      title: '标签',
      dataIndex: 'tag',
      render: (text) => {
        const { tagsOptions } = this.props.resourcesCommonChange;
        return getLabelByTreeValue(text, tagsOptions) || text;
      },
    }, {
      title: '备注',
      dataIndex: 'remark',
      width: '300px',
      render: (text) => text && text !== 'null' ? (<div className="word25" title={text}>{text}</div>) : null,
    },
  ];

  componentDidMount() {
    const { dispatch } = this.props;
    this.mergeResult(this.props);
    dispatch({ type: 'resourcesCommonChange/getDepartments' });
    dispatch({ type: 'resourcesCommonChange/findOrgnazation' });
    dispatch({ type: 'resourcesCommonChange/getAllResource' });
    dispatch({ type: 'resourcesCommonChange/getHdfsTree' });
  }

  componentWillReceiveProps(nextProps) {
    this.mergeResult(nextProps);
  }

  // 合并状态
  mergeResult(props) {
    const { myResource: { result, resultType, viewTable, ownerType } } = props;
    this.setState({ result, resultType, viewTable, ownerType });
  }

  // 查看详情
  handleView(record, e) {
    const { dispatch } = this.props;
    if (e) e.preventDefault();
    this.setState({
      viewTableVisible: true,
      viewTableName: record.metaName,
    })
    dispatch({
      type: 'myResource/getMeta',
      payload: { metaid: record.metaid },
    })
  }

  // 查看目录详情
  handleDirView(record, e) {
    const { dispatch } = this.props;
    if (e) e.preventDefault();
    this.setState({
      viewDirVisible: true,
    })
    dispatch({
      type: 'myResource/getDir',
      payload: { fileid: record.fileid },
    })
  }

  // 查看数据地图
  handleViewDataMap = (record, e) => {
    e.preventDefault();
    this.setState({
      viewDataMapId: record.metaid,
    }, () => {
      this.setState({
        viewDataMapVisible: true,
      });
    });
  }

  // 切换类型
  handleChangeListType(e) {
    const { location, router } = this.props;
    const type = e.target.value;
    const query = {
      dept: '',
      ownerType: this.state.ownerType,
      owner: '',
      keyword: '',
    }
    router.push({
      ...location,
      query: { ...query, page: 1, type },
    });
  }

  // 切换拥有者
  handleChangeOwner(val) {
    const { location, router } = this.props;
    const query = {
      dept: '',
      ownerType: val,
      owner: '',
      keyword: '',
    }
    router.push({
      ...location,
      query: { ...query, page: 1, type: location.query.type },
    });
  }

  // 点击搜索
  handleClickSearch(keyword) {
    const { router, location: { pathname, query } } = this.props;
    this.setState({
      current: 1,
      search: true
    })
    router.push({
      pathname,
      query: Object.assign({}, query, { keyword },{page:1}),
    })

  }

  render() {
    const { result, resultType, viewTable, ownerType } = this.state;
    const { location: { query } } = this.props;

    return (<div>
      <header className="padding_20">
        <Search
          defaultValue={query.keyword || ''}
          onSearch={this.handleClickSearch.bind(this)}
          placeholder="可按数据资源名、资源所属组织、行业、标签模糊搜索"/>

        <div>
          <Radio.Group
            style={{marginTop:'15px'}}
            onChange={this.handleChangeListType.bind(this)}
            value={resultType}
          >
            <Radio value='table'>数据表类</Radio>
            <Radio value='file'>文件目录类</Radio>
          </Radio.Group>
        </div>
      </header>

      <section className="padding_20" style={{paddingTop: 0 }}>

        <Tabs activeKey={ownerType} onChange={this.handleChangeOwner.bind(this)}>
          <TabPane tab="我是拥有者（或创建者）" key="1"></TabPane>
          <TabPane tab="我被授权的" key="2"></TabPane>
        </Tabs>
        

        <TableList
          showIndex
          rowKey='__index'
          columns={resultType === 'table' ? this.tableColumns : this.fileColumns}
          dataSource={result.rows}
          pagination={{
            current: 1,
            total: result.total
          }}
        />

      </section>

      <ViewTable
        visible={this.state.viewTableVisible}
        tableName={this.state.viewTableName}
        data={viewTable}
        onClose={() => this.setState({viewTableVisible: false})}
      />

      <ViewDir
        visible={this.state.viewDirVisible}
        data={viewTable}
        onClose={() => this.setState({viewDirVisible: false})}
      />

      <DataMap
        visible={this.state.viewDataMapVisible}
        id={this.state.viewDataMapId}
        onClose={() => this.setState({viewDataMapVisible: false})}
      />
    </div>);
  }
}

export default connect(({ system, myResource, resourcesCommonChange }) => ({
  system,
  myResource,
  resourcesCommonChange,
}))(withRouter(Result));
