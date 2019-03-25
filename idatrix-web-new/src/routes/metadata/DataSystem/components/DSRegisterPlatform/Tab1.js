/**
 * Created by Administrator on 2017/5/10.
 */
import  React  from 'react';
import { Layout,Button,Table,notification,Form,Input,Row,Col} from  'antd';
import {connect} from 'dva';
const { Content} = Layout;
const FormItem = Form.Item;

class Model extends React.Component{
  state = {
    data: [],
    pagination: {
      current:1
    },
    loading: false,
    selectedRowKeys:[],
    selectedRows:[]
  };
 
  formItemLayout = {
    labelCol: { span: 2 },
    wrapperCol: { span: 10 }
  }
  render(){
    const { info } = this.props;
    return(
      <Layout style={{height:"100%",background:"white"}}>
        <Content>
          <Row>
            <Col span={12}>
              {/* <Form style={{margin:"10px 0"}}>
                <FormItem   label="连接地址: " {...this.formItemLayout} style={{marginBottom:'8px'}}>
                  <Row>
                    <Col span={24}>
                    {getFieldDecorator('platformConnInfo', {
                      initialValue:info.connUrl,
                      rules: [{ required: true, message: '请输入连接名称' }]
                    })(
                      <Input disabled />
                    )}
                    
                    </Col>
                  </Row>
                </FormItem>
              </Form> */}
              <FormItem 
                label={
                    <span>链接地址</span>
                }
                label="链接地址"
                className="resetFormWithFlex"
              >
                  <Input value={info.connUrl} disabled  />
              </FormItem>
            </Col>
          </Row>
        </Content>
      </Layout>
    )
  }
}

const Tab1 = Form.create()(Model);
export default connect()(Tab1)
