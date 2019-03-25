import React from 'react';
import { Cascader, Select, Layout, Button, Tabs, Form, Input, Radio, Icon, Card, message } from 'antd';
import { connect } from 'dva';
import SliderBar from './SliderBar';
import { addResource, modifyResource } from '../../../services/securityResources';
import Empower from '../../../components/Empower'; // 导入授权组件
import Style from './style.css';
import Modal from 'components/Modal';

const confirm = Modal.confirm;
const InputGroup = Input.Group;
const { Sider, Content } = Layout;
const FormItem = Form.Item;
const Option = Select.Option;

// 表单宽高
const formItemLayout = {
  labelCol: {
    span: 2,
  },
  wrapperCol: {
    span: 6,
  },
};

class ResourcesManagingTable extends React.Component {
  state = {
  };

  componentWillReceiveProps(nextProps) {
    const { resourceView } = nextProps.resourcesManage;
    const { resourceView: oldView } = this.props.resourcesManage;
    const isAdding = /^_/.test(resourceView.id);
    if (isAdding && resourceView.id !== oldView.id) {
      setTimeout(() => {
        this.nameInput.focus();
      }, 300);
    }
  }

  // 提交表单
  handleSubmit = (e) => {
    const { dispatch } = this.props;
    e.preventDefault();
    this.props.form.validateFields(async (err, values) => {
      if (!err) {
        const { resourceView } = this.props.resourcesManage;
        const formData = Object.assign({}, resourceView, values);
        if (/^_/.test(formData.id)) { // 新增
          delete formData.id;
          const { data } = await addResource(formData);
          if (data.code === "200") {
            message.success('新增成功');
            this.reloadList();
            dispatch({
              type: 'resourcesManage/showResource',
              payload: Object.assign(formData, { id: data.entity.id }),
            });
            this.props.form.resetFields();
          } else if (data.msg.indexOf('资源已经存在') > -1) {
            this.props.form.setFields({
              url: {
                value: values.url,
                errors: [new Error(data.msg)],
              },
            });
          }
        } else { // 修改
          //formData['_method'] = 'put';
          const { data } = await modifyResource(formData);
          if (data.code === "200") {
            message.success('修改成功');
            this.reloadList();
            dispatch({
              type: 'resourcesManage/showResource',
              payload: formData,
            });
            this.props.form.resetFields();
          } else if (data.msg.indexOf('资源已经存在') > -1) {
            this.props.form.setFields({
              url: {
                value: values.url,
                errors: [new Error(data.msg)],
              },
            });
          } else {
            //message.error(data.message);
          }
        }
      }
    });
  }

  // 刷新资源列表
  reloadList() {
    const { dispatch } = this.props;
    dispatch({
      type: 'resourcesManage/getResourcesList',
      payload: {},
    });
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const { resourcesList, resourceView } = this.props.resourcesManage;
    const isAdding = /^_/.test(resourceView.id);

    return (<Layout>
      <Sider className={Style['sider-wrap']}>
        <SliderBar data={resourcesList} />
      </Sider>

      <Content style={{marginLeft: 10}}>
        <Card title="资源详情">
          <Form>
            <FormItem {...formItemLayout} label="名称：">
              {getFieldDecorator('name', {
                initialValue: isAdding ? '' : resourceView.name,
                rules: [
                  { required: true, whitespace: true, message: '请输入名称' },
                ],
              })(<Input ref={input => this.nameInput = input} maxLength={20} />)}
            </FormItem>
            <FormItem {...formItemLayout} label="类型：">
              {getFieldDecorator('type', {
                initialValue: resourceView.type,
              })(
                <Radio.Group disabled={!isAdding}>
                  <Radio value='菜单' disabled={resourceView.type === '系统'}>菜单</Radio>
                  <Radio value='按钮' disabled={resourceView.type === '系统'}>按钮</Radio>
                  <Radio value='系统' disabled={resourceView.type !== '系统'}>系统</Radio>
                </Radio.Group>
              )}
            </FormItem>
            <FormItem {...formItemLayout} label="URL：">
              {getFieldDecorator('url', {
                initialValue: resourceView.url || '',
                rules: [
                  { required: (resourceView.type === '菜单' || resourceView.type === '按钮'), whitespace: true, message: '请输入URL' },
                ],
              })(<Input disabled={resourceView.type !== '菜单' && resourceView.type !== '按钮'} maxLength={200} />)}
            </FormItem>
            <FormItem {...formItemLayout} label="描述：">
              {getFieldDecorator('urlDesc', {
                initialValue: resourceView.urlDesc,
              })(<Input type="textarea" disabled={resourceView.type !== '菜单' && resourceView.type !== '按钮'} maxLength={200} />)}
            </FormItem>
            <FormItem {...formItemLayout} label="显示：">
              {getFieldDecorator('isShow', {
                initialValue: resourceView.isShow,
              })(
                <Radio.Group disabled={resourceView.type !== '菜单' && resourceView.type !== '按钮'}>
                  <Radio value={true}>是</Radio>
                  <Radio value={false}>否</Radio>
                </Radio.Group>
              )}
            </FormItem>
            <div>
              <Empower api="/permission/update.shtml">
                <Button disabled={Object.keys(resourceView).length===0}
                  type="primary" style={{marginLeft: 200}} onClick={this.handleSubmit}>保存</Button>
              </Empower>
            </div>
          </Form>
        </Card>
      </Content>

    </Layout>);
  }
}
export default connect(({ resourcesManage }) => ({
  resourcesManage,
}))(Form.create()(ResourcesManagingTable));
