import React from 'react';
import { connect } from 'dva';
import { Select, Tabs, Input, Button, Spin, message } from 'antd';
import { safeJsonParse } from 'utils/utils';
import { getSolrCustom, getSolrMetadata, getSearchHistory } from 'services/analysisFullTextSearch';
import AceEditor from 'react-ace';
import * as ace from 'brace';
import 'brace/theme/xcode';
import 'brace/mode/javascript';

const Option = Select.Option;
const TabPane = Tabs.TabPane;

const defaultQuery = { query: { query_string: { query: '' } } };

const historyPageSize = 10; // 搜索历史分页大小

class AppView extends React.Component {

  state = {
    indexCode: '',
    result: '',
    method: 'POST',
    endpoint: '/',
    dsl: JSON.stringify(defaultQuery, null, 2),
    metadata: '',
    history: [],
    historyLoding: false,
    historyPage: 1,
    historyTotal: 0,
  };

  // 点击搜索
  onSearch = async () => {
    const { indexCode, method, endpoint, dsl } = this.state;
    if (!indexCode) {
      message.warn('请选择索引');
    } else {
      const { data } = await getSolrCustom({
        index: indexCode,
        method,
        endpoint,
        dsl,
			});
			const { code } = data;
      if (code === "200") {
        const result = safeJsonParse(data.data);
        this.setState({
          result: result ? JSON.stringify(result, null, 2) : result,
        });
      }
    }
  }

  // 点击刷新
  onRefreshMeta = async () => {
    const { indexCode } = this.state;
    if (!indexCode) {
      message.warn('请选择索引');
    } else {
      const { data } = await getSolrMetadata({
        index: indexCode,
			});
			const { code } = data;
      if (code === "200") {
        const result = safeJsonParse(data.data);
        this.setState({
          metadata: result ? JSON.stringify(result, null, 2) : result,
        });
      }
    }
  }

  // 加载搜索历史
  loadHistory = async () => {
    const { indexCode, history, historyPage } = this.state;
    if (!indexCode) {
      message.warn('请选择索引');
    } else {
      this.setState({
        historyLoding: true,
      });
      const { data } = await getSearchHistory({
        index: indexCode,
        pageNum: historyPage,
        pageSize: historyPageSize,
			});
			const { code } = data;
      if (code === "200") {
        const { results } = data.data;
        this.setState({
          history: historyPage === 1 ? results : history.concat(results),
          historyPage: historyPage + 1,
          historyLoding: false,
          historyTotal: data.data.total,
        }, () => {
          this.state.history.forEach((item, index) => {
            this.highlight(`dsl-editor-${index}`);
          });
        });
      } 
    }
  }

  handleChangeTab = (tab) => {
    if (tab === 'metadata') {
      this.onRefreshMeta();
    } else if (tab === 'history') {
      this.setState({ historyPage: 1 }, () => {
        this.loadHistory();
      });
    }
  }

  // 选择索引
  handleChangeIndex = (value) => {
    this.setState({
      indexCode: value,
      endpoint: `/${value}/_search`,
      result: '',
      metadata: '',
      history: [],
      historyPage: 1,
    });
  }

  // 切换method
  handleChangeMethod = (value) => {
    this.setState({
      method: value,
    });
  }

  // 输入endpoint
  handleChangeEndpoint = (e) => {
    this.setState({
      endpoint: e.target.value,
    });
  }

  // 输入DSL
  handleChangeDSL = (value) => {
    this.setState({
      dsl: value,
    });
  }

  handleHistoryScroll = (e) => {
    const { scrollTop, scrollHeight, clientHeight } = e.target;
    if (this.state.historyLoding) return;
    if (Math.ceil(this.state.historyTotal / historyPageSize) < this.state.historyPage) return;
    if (scrollHeight - clientHeight === scrollTop) {
      this.loadHistory();
    }
  }

  highlight(id) {
    const dom = document.getElementById(id);
    if (!dom || dom.className.indexOf('ace-xcode') > -1) return;
    const editor = ace.edit(id);
    editor.setTheme('ace/theme/xcode');
    editor.getSession().setMode('ace/mode/javascript');
    editor.setReadOnly(true);
    editor.resize(true);
  }

  render() {
    const { options } = this.props.FullTextSearch;
    return (
      <div style={{ padding: 20, backgroundColor: '#fff' }}>
        <section>
          索引：<Select showSearch style={{ width: 160, marginRight: 10 }} onChange={this.handleChangeIndex}>
            {options.map(op => (<Option key={op.id} value={op.indexName}>{op.indexName}</Option>))}
          </Select>
        </section>

        <Tabs type="card" style={{ marginTop: 20 }} onChange={this.handleChangeTab}>
          <TabPane tab="搜索" key="search">
            <section style={{ marginTop: 20 }}>
              <header style={{ marginBottom: 10 }}>
                <Select style={{ width: 80 }} value={this.state.method} onChange={this.handleChangeMethod}>
                  <Option value="GET">GET</Option>
                  <Option value="POST">POST</Option>
                </Select>
                <Input
                  style={{ width: 360, marginLeft: 10 }}
                  value={this.state.endpoint}
                  onChange={this.handleChangeEndpoint}
                />
                <Button style={{ marginLeft: 10 }} type="primary" onClick={this.onSearch}>提交</Button>
              </header>
              <AceEditor
                mode="javascript"
                theme="xcode"
                onChange={this.handleChangeDSL}
                showPrintMargin={true}
                showGutter={true}
                highlightActiveLine={true}
                value={this.state.dsl}
                style={{ width: '100%', height: 180, border: '1px solid #ddd' }}
                setOptions={{
                  enableBasicAutocompletion: false,
                  enableLiveAutocompletion: false,
                  enableSnippets: false,
                  showLineNumbers: true,
                  tabSize: 4,
                }}
              />
            </section>
            <section style={{ marginTop: 20 }}>
              <header style={{ marginBottom: 5 }}>搜索结果</header>
              <AceEditor
                readOnly
                mode="javascript"
                theme="xcode"
                value={decodeURIComponent(this.state.result)}
                style={{ width: '100%', height: 340, border: '1px solid #ddd' }}
              />
            </section>
          </TabPane>

          <TabPane disabled={!this.state.indexCode} tab="索引信息" key="metadata">
            <section style={{ marginTop: 20 }}>
              <header style={{ marginBottom: 10 }}>
                GET twitter/_settings,_mappings
                <Button style={{ marginLeft: 10 }} type="primary" onClick={this.onRefreshMeta}>刷新</Button>
              </header>
            </section>
            <section style={{ marginTop: 20 }}>
              <header style={{ marginBottom: 5 }}>结果</header>
              <AceEditor
                readOnly
                mode="javascript"
                theme="xcode"
                value={decodeURIComponent(this.state.metadata)}
                style={{ width: '100%', height: 500, border: '1px solid #ddd' }}
              />
            </section>
          </TabPane>

          <TabPane disabled={!this.state.indexCode} tab="搜索历史" key="history">
            <section
              ref="history"
              style={{ minHeight: 200, maxHeight: 600, overflow: 'auto' }}
              onScroll={this.handleHistoryScroll}
            >
              {this.state.history.map((item, index) => (
                <div key={index}>
                  <p>{item.createTime}</p>
                  <div id={`dsl-editor-${index}`} style={{ height: 100, marginBottom: 20 }}>{decodeURIComponent(item.dsl)}</div>
                </div>
              ))}
              {this.state.historyLoding ? (<Spin style={{ display: 'block', margin: '0 auto' }} />) : null}
            </section>
          </TabPane>
        </Tabs>

      </div>
    );
  }
}

export default connect(({ FullTextSearch }) => ({
  FullTextSearch: FullTextSearch.toJS(),
}))(AppView);
