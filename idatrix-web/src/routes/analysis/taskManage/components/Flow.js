/**
 * 流程图组件
 */

import React from 'react';
import { connect } from 'dva';
import { Layout, Form, Input, Icon, Row, Col, Button, message } from 'antd';
import { Link, withRouter } from 'react-router';
import ToolsItem from './ToolsItem';
import FlowSpace from './FlowSpace';
import ItemsShow from './ItemsShow';
import DeleteTip from './DeleteTip/DeleteModel';
import tools from './tools.config';
import { getNewName } from './flowdata';
import Empower from '../../../../components/Empower';
import Modal from 'components/Modal';

import Style from './Flow.css';

const { Sider, Content, Header } = Layout;
const FormItem = Form.Item;


class FlowModel extends React.Component {

  componentWillMount() {
    const { dispatch, id } = this.props;
    dispatch({
      type: 'flowspace/getTaskView',
      payload: id,
    });
  };



  // 修改名称
  changeName = (e) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'flowspace/updateStatus',
      payload: { name: e.target.value, status: 'UNSAVED' },
    });
  }

  // 修改描述
  changeDescription = (e) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'flowspace/updateStatus',
      payload: { description: e.target.value, status: 'UNSAVED' },
    });
  }

  handleError = (msg) => {
    const { form } = this.props;
    if (msg && msg.indexOf('任务名称') > -1) {
      form.setFields({
        name: {
          value: form.getFieldValue('name'),
          errors: [new Error(msg)],
        }
      });
    }
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const { flowspace } = this.props;
    const notEditable = flowspace.status !== 'SAVED' && flowspace.status !== 'UNEXECUTED' && flowspace.status !== 'UNSAVED';
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 16 },
    };

    return (
      <Layout className={Style.Flow}>
        <Form layout="inline" style={{ height: "100%", width: "100%", display: "flex", flexDirection: "column" }}>
          <Header className={Style.Header}>
            <Row>
              <Col span={10}>
                <FormItem
                  label="任务名称"
                  {...formItemLayout}
                  style={{ width:"100%" }}
                >
                  {getFieldDecorator('name', {
                    initialValue: flowspace.name,
                    rules: [
                      { required: true, message: '请输入任务名称' },
                      { message: '必须以字母开头，可使用字母、数字、下划线、中划线', pattern: /^[a-zA-Z][\w-]*$/ },
                    ]
                  })(
                    <Input maxLength="50" disabled={notEditable} onInput={this.changeName}  />
                  )}
                </FormItem>
              </Col>
              <Col span={10}>
                <FormItem
                  label="任务描述"
                  {...formItemLayout}
                  style={{ width:"100%" }}
                >
                  {getFieldDecorator('description', {
                    initialValue: flowspace.description,
                    rules: [{ required: true, message: '请输入任务描述' }]
                  })(
                    <Input maxLength="250" disabled={notEditable} onInput={this.changeDescription} />
                  )}
                </FormItem>
              </Col>
              <Col span={4} style={{ textAlign: 'right' }}>
                <Link style={{ fontSize: 18 }} to="/analysis/TaskManage/help"><Icon type="question-circle-o" /></Link>
              </Col>
            </Row>
          </Header>
          <Layout className={Style.layout} >
            <Sider className={Style.sider} width={80}>
              <ul style={{ alignItems: "center" }}>
                {Object.keys(tools).map((key) => {
                  return <li key={key}>
                    <Empower api={tools[key].empowerApi} disable-type="hide">
                      <ToolsItem type={key} />
                    </Empower>
                  </li>
                })}
              </ul>
            </Sider>
            <Content className={Style.mainContent}>
              <FlowSpace id={this.props.id} onError={this.handleError} route={this.props.route} router={this.props.router} />
              <DeleteTip />
              <ItemsShow />
            </Content>
          </Layout>
        </Form>
      </Layout>
    )
  }
}


const Flow = Form.create()(FlowModel);

export default connect(({ taskManage, flowspace }) => ({
  taskManage, flowspace
}))(withRouter(Flow));
