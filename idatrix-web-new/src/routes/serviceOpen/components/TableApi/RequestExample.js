import React from 'react';
import { Tabs } from 'antd';
import AceEditor from 'react-ace';
import 'brace/theme/xcode';
import 'brace/mode/java';
import 'brace/mode/csharp';
import 'brace/mode/php';
import 'brace/mode/curly';
import 'brace/mode/python';
import 'brace/mode/c_cpp';
import 'brace/mode/javascript';

const TabPane = Tabs.TabPane;

class RequestExample extends React.Component {

  //3.输出组件页面：
  render() {
    const { cCode, curlCode, javaCode, netCode, nodeJSCode, phpCode, pythonCode } = this.props.data || {};
    return(
      <div id="RequestExample" ref="codepane">
        {cCode || curlCode || javaCode || netCode || nodeJSCode || phpCode || pythonCode ? (
          <Tabs type="card">
            {javaCode ? (
              <TabPane tab="JAVA" key="java">
                <AceEditor
                  readOnly
                  mode="java"
                  theme="xcode"
                  value={decodeURIComponent(javaCode)}
                  style={{ width: '100%', height: 200, border: '1px solid #ddd' }}
                />
              </TabPane>
            ) : ''}
            {netCode ? (
              <TabPane tab=".NET" key="csharp">
                <AceEditor
                  readOnly
                  mode="csharp"
                  theme="xcode"
                  value={decodeURIComponent(netCode)}
                  style={{ width: '100%', height: 200, border: '1px solid #ddd' }}
                />
              </TabPane>
            ) : ''}
            {phpCode ? (
              <TabPane tab="PHP" key="php">
                <AceEditor
                  readOnly
                  mode="php"
                  theme="xcode"
                  value={decodeURIComponent(phpCode)}
                  style={{ width: '100%', height: 200, border: '1px solid #ddd' }}
                />
              </TabPane>
            ) : ''}
            {curlCode ? (
              <TabPane tab="CURL" key="curly">
                <AceEditor
                  readOnly
                  mode="curly"
                  theme="xcode"
                  value={decodeURIComponent(curlCode)}
                  style={{ width: '100%', height: 200, border: '1px solid #ddd' }}
                />
              </TabPane>
            ) : ''}
            {pythonCode ? (
              <TabPane tab="Python" key="python">
                <AceEditor
                  readOnly
                  mode="python"
                  theme="xcode"
                  value={decodeURIComponent(pythonCode)}
                  style={{ width: '100%', height: 200, border: '1px solid #ddd' }}
                />
              </TabPane>
            ) : ''}
            {cCode ? (
              <TabPane tab="C/C++" key="c_cpp">
                <AceEditor
                  readOnly
                  mode="c_cpp"
                  theme="xcode"
                  value={decodeURIComponent(cCode)}
                  style={{ width: '100%', height: 200, border: '1px solid #ddd' }}
                />
              </TabPane>
            ) : ''}
            {nodeJSCode ? (
              <TabPane tab="NodeJS" key="javascript">
                <AceEditor
                  readOnly
                  mode="javascript"
                  theme="xcode"
                  value={decodeURIComponent(nodeJSCode)}
                  style={{ width: '100%', height: 200, border: '1px solid #ddd' }}
                />
              </TabPane>
            ) : ''}
          </Tabs>
        ) : (
          <div style={{ textAlign: 'center' }}>
            <span><i className="anticon anticon-frown-o"></i> 暂无数据</span>
          </div>
        )}
      </div>
    );
  }
}

export default RequestExample;

