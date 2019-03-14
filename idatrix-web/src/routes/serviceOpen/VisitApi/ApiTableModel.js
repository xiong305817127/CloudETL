import  React  from 'react'
import { Collapse, Button } from 'antd';
const Panel = Collapse.Panel;
//获取后台详情页数据
import { getServiceDetails } from '../../../services/service';
import ComonStaticApi from '../components/TableApi/CommonStaticApi';
import CommonStaticApiRequest from "../components/TableApi/CommonStaticApiRequest";
import RequestApiTable from "../components/TableApi/RequestApiTable";
import RespondApiTable from "../components/TableApi/RespondApiTable";
import RequestExample from "../components/TableApi/RequestExample";
import RespondExample from "../components/TableApi/RespondExample";

class ApiTableModel extends React.Component{

  state = {
    data: {
      addressMap:{},
      inputMetaData: '[]',
      outputMetaData: '[]',
    }
  }

  constructor(props){
    super(props);
  }

  componentDidMount() {
    const serviceId = this.props.params.id;
    getServiceDetails({ serviceId }).then(res => {
      if (res.data && res.data.data) {
        this.setState({ data: res.data.data });
      }
    });
  }

  //3.输出组件页面：
  render(){
    const data = this.state.data;
    // console.log(data)
    return(
      <div id="ApiTableModel" style={{backgroundColor:'#fff',padding:'10px 0'}}>
        <div style={{ margin:10 }}><a href="#/service/ServiceTableVisitApi"><Button type='primary'>返回</Button></a></div>
        <div style={{ margin:20 }}><h3>API名称：{data.serviceCode}</h3></div>
        <Collapse style={{margin:10}} defaultActiveKey={['1']}>
          <Panel header="【公共参数】" key="1" >
            <ComonStaticApi data={data.addressMap}/>
            <CommonStaticApiRequest data={data}/>
          </Panel>

          <Panel header="【请求参数】" key="2">
            <RequestApiTable data={JSON.parse(data.inputMetaData)}/>
          </Panel>

          <Panel header="【响应参数】" key="3">
            <RespondApiTable data={JSON.parse(data.outputMetaData)} />
          </Panel>

          <Panel header="【请求示例】" key="4">
            <RequestExample data={data.serviceInvokeSample}/>
          </Panel>

          <Panel header="【响应示例】" key="5">
            <RespondExample data={data.serviceInvokeSample}/>
          </Panel>

        </Collapse>

      </div>

    )
  }
}

export default ApiTableModel;

