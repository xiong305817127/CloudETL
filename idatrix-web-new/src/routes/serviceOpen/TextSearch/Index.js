import React from 'react';
import { Table } from 'antd';
import { withRouter } from 'react-router';
import Empower from '../../../components/Empower';
import Search from '../../../components/Search';

import Style from './style.css';

class TextSearch extends React.Component{
  handleClickSearch = () => {

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
      </section>
    </div>);
  }
};

export default withRouter(TextSearch);
