import React from 'react';
import { connect } from 'dva';
import { Radio, TreeSelect, Tabs } from 'antd';
import { withRouter } from 'react-router';
import Search from 'components/Search';
import MetaData from './MetaData'; // 引入数据表类组件
import MetaFile from './MetaFile'; // 引入文件目录类组件
import ES from './ES'; // 引入文件目录类组件

const TabPane = Tabs.TabPane;
class AppPage extends React.Component {
  state= {
    showSearch: true
  }
  config = {
    table: {
      placeholder: '可以按表英文名、表中文名进行模糊搜索',
    },
    file: {
      placeholder: '可以按文件目录描述、文件存储目录进行模糊搜索',
    },
    es: {
      placeholder: '可以按索引编码、创建人进行模糊搜索',
    }
  };

  componentDidMount(){
    const { dispatch }=this.props;
    dispatch({ type: 'metadataCommon/getDepartments' });
  }

  // 点击搜索时
  handleClickSearch(keyword, dept) {
    const { router, location } = this.props;
    const query = {
      model: location.query.model,
      metaType: location.query.metaType,
      keyword,
      dept: dept ? dept : null
    };
    router.push({ ...location, query });
  }

  // 切换数据表类/文件目录类
  handleChangeType = (value)=>{
    this.setState({
      showSearch: false
    });
    const model = value;
    const { router, location } = this.props;
    setTimeout(()=>{
      this.setState({
        showSearch: true
      })
    },0)
    router.push({ ...location, query: { model }});
  }

  // 选择组织
  handleChangeDept(dept) {
    const { query: { keyword } } = this.props.location;
    this.handleClickSearch(keyword, dept);
  }

  render() {
    const { location, router } = this.props;
    const { query } = location;
    const model = query.model || 'table';
    const { departmentsTree } = this.props.metadataCommon;

    return <div>
      <header className="padding_20">
        <Search
          defaultValue={query.keyword || ''}
          onSearch={this.handleClickSearch.bind(this)}
          placeholder={this.config[model].placeholder} />
        {
          this.state.showSearch
          &&
          <TreeSelect
            placeholder="请选择组织"
            treeData={departmentsTree}
            onChange={(value)=>{this.handleChangeDept(value)}}
            treeDefaultExpandAll
            allowClear
            style={{ width: 200 }}
          />
        }
        <div className="padding_20_0" style={{paddingTop: 10}}>
          <Tabs activeKey={model} onChange={this.handleChangeType.bind(this)}>
            <TabPane tab="数据表类" key="table">
              {model === "table" ? <MetaData location={location} router={router} /> : null }
            </TabPane>
            <TabPane tab="文件目录类" key="file">
              { model === "file"  ? <MetaFile location={location} router={router} /> : null }
            </TabPane>
            <TabPane tab="ES索引" key="es">
              { model === "es"  ? <ES location={location} router={router} /> : null }
            </TabPane>
          </Tabs>
        </div>
      </header>
    </div>
  }
}

export default connect(({ metadataCommon }) => ({
  metadataCommon,
}))(withRouter(AppPage));
