import  React  from 'react'
import { Tabs,Input,Table, Button } from 'antd';
import AceEditor from 'react-ace';
import 'brace/theme/xcode';
import 'brace/mode/javascript';
import 'brace/mode/xml';

const TabPane = Tabs.TabPane;

class RespondExample extends React.Component{

  //3.输出组件页面：
  render(){
    const { responseJson, responseXml } = this.props.data || {};
    return(
      <div id="RequestExample" ref="codepane">
        {responseXml || responseXml ? (
          <Tabs type="card">
            {responseXml ? (
              <TabPane tab="XML示例" key="xml">
                <AceEditor
                  readOnly
                  mode="xml"
                  theme="xcode"
                  value={decodeURIComponent(responseXml)}
                  style={{ width: '100%', height: 300, border: '1px solid #ddd' }}
                />
              </TabPane>
            ) : ''}
            {responseJson ? (
              <TabPane tab="JSON示例" key="javascript">
                <AceEditor
                  readOnly
                  mode="javascript"
                  theme="xcode"
                  value={decodeURIComponent(responseJson)}
                  style={{ width: '100%', height: 300, border: '1px solid #ddd' }}
                />
              </TabPane>
            ) : ''}
          </Tabs>
        ) : (
          <div style={{textAlign: 'center'}}>
            <span><i className="anticon anticon-frown-o"></i> 暂无数据</span>
          </div>
        )}
      </div>
    )
  }
}

export default RespondExample;
