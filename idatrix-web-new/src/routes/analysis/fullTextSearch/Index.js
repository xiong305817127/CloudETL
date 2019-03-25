import React from 'react';
import { connect } from 'dva';
import { Select, Input, message } from 'antd';
import { Link } from 'react-router';
import Search from 'components/Search';
import Empower from 'components/Empower';
import { safeJsonParse } from 'utils/utils';
import { getSolrFull } from 'services/analysisFullTextSearch';
import AceEditor from 'react-ace';
import 'brace/theme/xcode';
import 'brace/mode/javascript';

const Option = Select.Option;
const { TextArea } = Input;

class AppView extends React.Component {

  state = {
    indexCode: '',
    result: '',
    dsl: '',
  };

  // 点击搜索
  async onSearch(value) {
    const { indexCode } = this.state;
    if (!indexCode) {
      message.warn('请选择索引');
    } else {
      const { data } = await getSolrFull({
        index: indexCode,
        keyword: value,
			});
			const { code } = data;
      if (code === "200") {
        const { inputEndpoint, output, inputDsl } = data.data;
        const result = safeJsonParse(output);
        this.setState({
          result: result ? JSON.stringify(result, null, 2) : result,
          dsl: `${inputEndpoint}\n${inputDsl}`,
        });
      }
    }
  }

  // 选择索引
  handleChangeIndex = (value) => {
    this.setState({
      indexCode: value,
      result: '',
      dsl: '',
    });
  }

  render() {
    const { options } = this.props.FullTextSearch;
    const { result, dsl } = this.state;
    return (
      <div style={{ padding: 20, backgroundColor: '#fff',width:"100%" }}>
        <section>
          索引：<Select showSearch style={{ width: 160, marginRight: 10 }} onChange={this.handleChangeIndex}>
            {options.map(op => (<Option key={op.id} value={op.indexName}>{op.indexName}</Option>))}
          </Select>
          <Search
            placeholder="请输入关键词，例如：大数据"
            width="300px"
            onSearch={value => this.onSearch(value)}
          />
          <Empower api="/es/search/custom">
            <Link to="/analysis/FullTextSearch/custom">自定义搜索</Link>
          </Empower>
        </section>

        <section style={{ marginTop: 20 }}>
          <header style={{ marginBottom: 5 }}>输入</header>
          <TextArea disabled value={dsl} autosize={{ minRows: 10, maxRows: 10 }} />
        </section>

        <section style={{ marginTop: 20 }}>
          <header style={{ marginBottom: 5 }}>搜索结果</header>
          <AceEditor
            readOnly
            mode="javascript"
            theme="xcode"
            value={decodeURIComponent(result)}
            style={{ width: '100%', height: 360, border: '1px solid #ddd' }}
          />
        </section>
      </div>
    );
  }
}

export default connect(({ FullTextSearch }) => ({
  FullTextSearch: FullTextSearch.toJS(),
}))(AppView);
