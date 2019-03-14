import React from 'react';
import { Layout, Form } from 'antd';
import { connect } from 'dva';
import SliderBar from './SliderBar';
import Style from './index.less';
import SliderModal from './component/SliderModal';

const { Sider, Content } = Layout;
const FormItem = Form.Item;

// 表单宽高
const formItemLayout = {
  labelCol: {
    span: 12
  },
  wrapperCol: {
    span: 12,
  },
};

class Index extends React.Component {
  state = {
    config:{}
  };

  componentWillMount(){
    const { dispatch } = this.props;
    dispatch({type:"databaseModel/getResourcesFolder" })
  }

  componentWillReceiveProps(nextProps) {
    const { config } = nextProps.databaseModel;
    this.setState({ config })
  }

  render() {
    const { config } = this.state;

    return (<Layout>
      <Sider className={Style['sider-wrap']} width={"100%"} >
        <SliderBar/>
      </Sider>
      <Content style={{marginLeft: 10,width:"500px",display:"none"}} className={Style.databaseContent}>
          <div className={Style.cardHeader}>信息资源</div>
          <Form className={Style.formPadding}>

            <FormItem
              {...formItemLayout}
              label="信息资源名称："
            >
                <span className="ant-form-text">{config.resourceName}</span>
            </FormItem>
            <FormItem
              {...formItemLayout}
              label="信息资源代码："
            >
                <span className="ant-form-text">{config.resourceEncode}</span>
            </FormItem>
          </Form>
      </Content>
      <SliderModal config={config} />
    </Layout>);
  }
}
export default connect(({ databaseModel }) => ({
  databaseModel
}))(Form.create()(Index));
