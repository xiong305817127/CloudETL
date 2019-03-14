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
    labelCol: { span: 8 },
    wrapperCol: { span: 12 }
  }
  render(){
    const {  getFieldDecorator } = this.props.form;
    const { info } = this.props;
    return(
      <Layout style={{height:"100%",background:"white"}}>
        <Content>
          <Form style={{margin:"10px 0"}}>
            <Row>
              <Col span={12}>
                <FormItem   label="API接口地址: " {...this.formItemLayout} style={{marginBottom:'8px'}}>
                      {getFieldDecorator('platformConnInfo', {
                        initialValue:"",
                        rules: [{ required: true, message: '请输入连接名称' }]
                      })(
                        <Input />
                      )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem   label="节点IP: " {...this.formItemLayout} style={{marginBottom:'8px'}}>
                      {getFieldDecorator('platformConnInfo', {
                        initialValue:"",
                        rules: [{ required: true, message: '请输入连接名称' }]
                      })(
                        <Input />
                      )}
                </FormItem>
              </Col>
            </Row>    
            <Row>
              <Col span={12}>
                <FormItem   label="Index: " {...this.formItemLayout} style={{marginBottom:'8px'}}>
                      {getFieldDecorator('platformConnInfo', {
                        initialValue:"",
                        rules: [{ required: true, message: '请输入连接名称' }]
                      })(
                        <Input />
                      )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem   label="Type: " {...this.formItemLayout} style={{marginBottom:'8px'}}>
                      {getFieldDecorator('platformConnInfo', {
                        initialValue:"",
                        rules: [{ required: true, message: '请输入连接名称' }]
                      })(
                        <Input/>
                      )}
                </FormItem>
              </Col>
            </Row>    
          </Form>
        </Content>
      </Layout>
    )
  }
}

const Tab2 = Form.create()(Model);
export default connect()(Tab2)
