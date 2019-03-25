import { Steps, Button, message } from 'antd';
import { connect } from 'dva';
import FirstFlowTable from './FirstFlowTable';
import SecondFlowTable from './SecondFlowTable';
import LastFlowTable from './LastFlowTable';
import { modifyTenant } from '../../../services/securityTenant';
import {hashHistory} from "dva/router"

const Step = Steps.Step;

const steps = [
  {
  title: '租户信息',
  content: '',
}, {
  title: '开通服务',
  content: '',
}, {
  title: '开通资源',
  content: '',
}];

class NewTableFlow extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      current: 0,
      renterName: '',
      adminAccount: '',
      adminName: '',
      adminPhone: '',
      adminEmail: '',
      openedService: '',
      openedResource: '',
    };
  }

  componentWillMount() {
    const { tenantManage: { list }, params: { id } } = this.props;
    const result = list.find(item => item.id == id);
    if (!result) {
      location.hash = '#/security/TenantManagementTable';
    } else {
      this.setState({ ...result });
    }
  }

  //下一步：
  next(formData, isEnd = false) {
    const current = this.state.current + 1;
    this.setState({ current, ...formData }, () => {
      if (isEnd) {
        this.submitFormData();
      }
    });
  }
  //上一步：
  prev() {
    const current = this.state.current - 1;
    this.setState({ current });
  }

  //完成：修改必须传id
  submitFormData(){
    const { params: { id } } = this.props;
    const formData = {
      renterName: this.state.renterName,
      adminAccount: this.state.adminAccount,
      adminName: this.state.adminName,
      adminPhone: this.state.adminPhone,
      adminEmail: this.state.adminEmail,
      openedService: this.state.openedService,
      openedResource: this.state.openedResource,
      id,
    };
    modifyTenant(formData).then(({ data })=>{
      if (data.code === "200") {
        message.success('新建或修改租户成功!');
        hashHistory.push('/security/TenantManagementTable');
      } else {
        message.error('修改租户失败 ' + data.msg ? data.msg : "" );
        this.setState({ current: 0 }); // 返回第一步修改
      }
    });
  }
  showTableModel(current) {
    switch (current) {
      case 0:
        return (<FirstFlowTable readonly data={{...this.state}} onNext={this.next.bind(this)} />);
      case 1:
        return (<SecondFlowTable readonly data={{...this.state}} onNext={this.next.bind(this)} onPrev={this.prev.bind(this)} />);
      case 2:
        return (<LastFlowTable readonly data={{...this.state}} onNext={this.next.bind(this)} onPrev={this.prev.bind(this)} />);
    }
  }

  render() {
    const { current } = this.state;
    return (
      <div id="NewTableFlow" style={{padding: 20, backgroundColor: '#fff'}}>
        {/*步骤内容：*/}
        <Steps current={current} style={{margin: '0 auto', width: '60%'}}>
          {/*遍历map步骤1.2.3*/}
          {steps.map(item => <Step key={item.title} title={item.title} />)}
        </Steps>
        {/*步骤2内容：*/}
        <div className="steps-content" style={{margin: 20}}>
          {this.showTableModel(this.state.current)}
        </div>
      </div>
    )}

}

export default connect(({ tenantManage }) => ({
  tenantManage,
}))(NewTableFlow);
