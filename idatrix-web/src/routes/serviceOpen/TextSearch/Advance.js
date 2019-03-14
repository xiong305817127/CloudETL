import React from 'react';
import { Table, Tabs } from 'antd';
import { withRouter } from 'react-router';
import Empower from '../../../components/Empower';
import Search from '../../../components/Search';

import Style from './style.css';

const TabPane = Tabs.TabPane;

class TextSearch extends React.Component{
  state = {
    activeKey: '1',
  };

  handleClickSearch = () => {

  };

   // 切换页签
  handleChangeTags(val) {
    const { dispatch } = this.props;
    this.setState({ activeKey: val });
  };

  render() {
    const { location: { query } } = this.props;
    return (<div>
      <header className={Style['head-wrap']}>
        <Search
          defaultValue={query.keyword || ''}
          onSearch={this.handleClickSearch}
          placeholder="请输入查询条件" />
      </header>

      <section className={Style['main-wrap']}>
        <Tabs activeKey={this.state.activeKey} type="card" style={{margin:10}} onChange={this.handleChangeTags}>
          <TabPane tab="搜索" key="1"></TabPane>
          <TabPane tab="索引信息" key="2"></TabPane>
          <TabPane tab="搜索历史" key="3"></TabPane>
        </Tabs>

      </section>
    </div>);
  }
};

export default withRouter(TextSearch);
