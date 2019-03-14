import React from 'react';
import { connect } from 'dva';
import { withRouter } from 'react-router';
import {  Row, Col, Tag, TreeSelect } from 'antd';
import Search from 'components/Search';
import Style from './style.less';

class Directory extends React.Component {

  state = {
    selectedList: [],
    resource: {},
    current: 'mail',
    dept: '',
    themesAlpha: '',
    themesList: [],
    tagsAlpha: '',
    tagsList: [],
  }

  componentDidMount() {
    const { dataResource, dispatch } = this.props;
    dispatch({ type: 'resourcesCommonChange/getDepartments' });
    dispatch({ type: 'resourcesCommonChange/findOrgnazation' });
    this.setState(dataResource);
  }

  componentWillReceiveProps(nextProps) {
    const { dataResource } = nextProps;
    this.setState(dataResource);
    this.filterThemes('');
    this.filterTags('');
  }

  // 删除条件
  handleCloseItem(removedTag) {
    const selectedList = this.state.selectedList.filter(tag => tag !== removedTag)
    this.setState({ selectedList });
  }

  // 选择条件
  handleClickItem(tag) {
    const keyword = tag.keyword;
    const { selectedList } = this.state;
    selectedList.push(keyword);
    this.setState({ selectedList: [...(new Set(selectedList))] });
  }

  // 点击搜索按钮
  handleSearchClick = (keyword)=>{
    const { router, dataResource: { originResource } } = this.props;
    const { selectedList, dept } = this.state;
    const result = (originResource || []).filter((item) => selectedList.indexOf(item.keyword) > -1);
    const tags = [];
    const themes = [];
    const industrys = [];
    result.forEach((item) => {
      if (item.type === '标签') {
        // tags.push(item.keyword);
        tags.push(item.id);
      } else if (item.type === '主题') {
        // themes.push(item.keyword);
        themes.push(item.id);
      } else if (item.type === '行业') {
        // industrys.push(item.keyword);
        industrys.push(item.id);
      }
    });
    router.push({
      pathname: `/resources/directory/result`,
      query: {
        tags: tags.join(','),
        themes: themes.join(','),
        industrys: industrys.join(','),
        keyword,
        dept,
      },
    });
  }

  // 选择组织
  handleChangeDept(dept) {
    this.setState({ dept });
  }

  // 过滤主题
  filterThemes(alpha) {
    const themes = this.props.dataResource.resource['主题'] || {};
    let themesList = [];
    if (!alpha) { // 全部
      Object.keys(themes).map(key => themes[key].forEach((item, idx) => themesList.push(item)));
    } else {
      themesList = themes[alpha] ? themes[alpha].map((item, idx) => item) : [];
    }
    this.setState({
      themesAlpha: alpha,
      themesList,
    })
  }

  // 过滤标签
  filterTags(alpha) {
    const tags = this.props.dataResource.resource['标签'] || {};
    let tagsList = [];
    if (!alpha) { // 全部
      Object.keys(tags).map(key => tags[key].forEach((item, idx) => tagsList.push(item)));
    } else {
      tagsList = tags[alpha] ? tags[alpha].map((item, idx) => item) : [];
    }
    this.setState({
      tagsAlpha: alpha,
      tagsList,
    })
  }

  /**
   * 生成过滤器
   * @param  {string}   alpha 当前激活的首字母
   * @param  {Function} cb    鼠标经过时的回调函数
   * @return {ReactDOM}       返回reactDOM
   */
  createFilter(alpha, cb) {
    return (<div className={Style.alphabet}>
      <a className={alpha ? '' : Style.active}
        onMouseOver={() => cb('')}
      >全部</a>
      {'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('').map((item) => (
        <a
          key={item}
          className={item === alpha ? Style.active : ''}
          onMouseOver={() => cb(item)}
        >{item}</a>
      ))}
    </div>)
  }

  render() {
    const { resource, selectedList, tagsList, tagsAlpha } = this.state;
    const { resourcesCommonChange: { allDepartmentsTree } } = this.props;
    const industrys = resource['行业'] || {};
    // const themes = resource['主题'] || {};
    // const tags = resource['标签'] || {};

    return (<div>
      <div className="padding_20">
        <TreeSelect
          allowClear
          placeholder="请选择组织"
          treeData={allDepartmentsTree}
          onChange={(value)=>{this.handleChangeDept(value)}}
          treeDefaultExpandAll
          style={{ width: 200,marginRight:"20px" }}
          dropdownStyle={{maxHeight: 600, overflow: 'auto' }}
        />
        <Search
          onSearch={this.handleSearchClick}
          placeholder="可按数据资源名、资源所属组织、行业、标签模糊搜索" />

      </div>
      <Row>
        <Col span="2" className={Style.label} style={{lineHeight: '30px'}}>已选条件：</Col>
        <Col>
          {selectedList.map((item, index) => {
            return (
              <Tag closable key={item}
                className={Style.tag}
                afterClose={() => this.handleCloseItem(item)}
              >{item}</Tag>);
          })}
        </Col>
      </Row>

      {/* 条件选择 */}
      <section className={Style['condition'] + " padding_20"}>
        <Row type="flex" className={Style.row}>
          <Col span="2" className={Style.titl}>行业</Col>
          <Col span="22">
            <Row style={{padding: 20}}>
              {Object.keys(industrys).map(key => industrys[key].map((item, idx) => (
                <Col span="3" className={Style['col']}>
                  <a key={idx} onClick={() => this.handleClickItem(item)}>{item.keyword}</a>
                </Col>
              )))}
            </Row>
          </Col>
        </Row>
        {/*<Row type="flex" className={Style.row}>
          <Col span="2" className={Style.titl}>主题</Col>
          <Col span="22">
            {this.createFilter(themesAlpha, this.filterThemes.bind(this))}
            <Row className={Style['cond-list-wrap']}>
              {themesList.map((item, idx) => (
                <Col key={idx} span="3" className={Style['col']}>
                  <a onClick={() => this.handleClickItem(item)}>{item.keyword}</a>
                </Col>
              ))}
            </Row>
          </Col>
        </Row>*/}
        <Row type="flex" className={Style.row}>
          <Col span="2" className={Style.titl}>标签</Col>
          <Col span="22">
            {this.createFilter(tagsAlpha, this.filterTags.bind(this))}
            <Row className={Style['cond-list-wrap']}>
              {tagsList.map((item, idx) => (
                <Col key={idx} span="3" className={Style['col']}>
                  <a onClick={() => this.handleClickItem(item)}>{item.keyword}</a>
                </Col>
              ))}
            </Row>
          </Col>
        </Row>
      </section>

    </div>);
  }
}

export default connect(({ system, dataResource, account, resourcesCommonChange }) => ({
  system,
  account,
  dataResource,
  resourcesCommonChange,
}))(withRouter(Directory));
