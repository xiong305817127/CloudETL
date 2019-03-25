/**
 * Created by Administrator on 2017/3/13.
 */
import { Row, Col, Form, Input, Popconfirm, Tooltip, Icon, Button, message } from 'antd'
import { connect } from 'dva';
import { jarUploadApi } from '../../../../../services/analysisTask';
import Upload from 'components/Upload';
import Modal from 'components/Modal';
import moment from "moment";

const FormItem = Form.Item;

const formItemLayout1 = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};

const formItemLayout2 = {
  labelCol: { span: 3 },
  wrapperCol: { span: 21 },
};

const paramPatt = /^[\w-.]*$/;

// 生成随机key值
const createKey = () => {
  return moment().format() + (Math.random() * 1e10);
};

class Spark extends React.Component{

  state = {
    id: '',
    name: '',
    config: {
      className: '',
      jarAttachementId: '',
      parameter: {},
      fileName: '',
    },
    args: [],
  }

  componentWillMount() {
    this.updateStateByProps(this.props);
  }

  componentWillReceiveProps(nextProps) {
    this.updateStateByProps(nextProps);
  }

  // 更新状态
  updateStateByProps(props) {
    const { items } = props;
    if (this.state.id !== items.id) {
      const args = [];
      if (items.config && typeof items.config.parameter === 'object') {
        Object.keys(items.config.parameter).forEach(key => {
          args.push({
            key: createKey(),
            name: key,
            value: items.config.parameter[key],
          });
        });
      }
      if (args.length === 0) {
        args.push({
          key: createKey(),
          name: 'args',
          value: '',
        });
      }
      this.setState({
        id: items.id,
        name: items.text,
        config: items.config ? items.config : {
          className: '',
          jarAttachementId: '',
          parameter: {},
          fileName: '',
        },
        args,
      });
    }
  }

  setModal1Hide = ()=>{
    const { dispatch } = this.props;

    dispatch({
      type:'items/hide',
      visible:false
    });
    this.props.form.resetFields();
  };

  saveItem = ()=>{
    const {id, saveItem } = this.props.items;
    this.props.form.validateFields((err, values) => {
      if (!err) {
        const obj = {};
        const parameter = {};
        this.state.args.forEach(item => {
          if (item.name) {
            parameter[item.name] = item.value;
          }
        });
        obj.id = id;
        obj.config = {
          className: values.className,
          jarAttachementId: this.state.config.jarAttachementId,
          fileName: values.fileName,
          parameter,
        };
        obj.name = values.name;
        saveItem(obj);
        this.setModal1Hide();
      }
    });
  };

  // 修改name
  changeName = (e) => {
    this.setState({
      name: e.target.value,
    })
  };

  // 修改配置
  changeConfig(key, e) {
    const { config } = this.state;
    config[key] = e.target.value;
    this.setState({ config });
  };

  // 修改参数
  changeArgs(e) {
    const { args } = this.state;
    args[0].value = e.target.value;
    this.setState({ args });
  }

  // 上传jar
  uploadJar = (res) => {
    const { file: { response }, event} = res;
    // 上传进度100
    if (event && event.percent === 100) {
      // console.log('上传完成');
    }
    // 服务器返回信息
    if (response) {
      if (response.code === 200) {
        const { config } = this.state;
        const { className, jarAttachementId, fileName } = response.data;
        config.className = className;
        config.jarAttachementId = jarAttachementId;
        config.fileName = fileName;
        this.setState({ config });
      }
    }
  };

  render(){
    const { config } = this.state;
    const {visible, panel, readonly } = this.props.items;
    const {getFieldDecorator}  = this.props.form;

    return(
      <Modal
        title={`节点编辑 - ${this.state.name}`}
        wrapClassName="vertical-center-modal"
        visible={visible}
        onOk={() => this.saveItem()}
        onCancel={() => this.setModal1Hide(false)}
        maskClosable={false}
      >
        <Form className="break-error">
          <Row>
            <Col span="12">
              <FormItem label="类型：" {...formItemLayout1}>
                {getFieldDecorator('type', {
                  initialValue: panel,
                })(
                  <Input disabled />
                )}
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem label="名称：" {...formItemLayout1}>
                {getFieldDecorator('name', {
                  initialValue: this.state.name,
                  rules: [
                    { required: true, message: '请输入节点名称' },
                    { message: '必须以字母开头，可使用字母、数字、下划线、中划线', pattern: /^[a-zA-Z][\w-]*$/ },
                  ],
                })(
                  <Input maxLength="20" disabled={readonly} onChange={this.changeName} />
                )}
              </FormItem>
            </Col>
          </Row>
          <FormItem label="Jar："  {...formItemLayout2}>
            {getFieldDecorator('fileName', {
              initialValue: config.fileName,
            })(
              <Input disabled={readonly} readOnly style={{width: '100%'}} addonAfter={(
                <Upload
                  disabled={readonly}
                  action={jarUploadApi}
                  showUploadList={false}
                  data={{
                    jarattachementid: config.jarAttachementId || 0,
                  }}
                  beforeUpload={()=>message.loading('正在上传，请稍候...')}
                  onChange={this.uploadJar}
                  style={{cursor: 'pointer'}}
                >
                  浏览
                </Upload>
              )} />
            )}
          </FormItem>
          <FormItem label="Class："  {...formItemLayout2}>
            {getFieldDecorator('className', {
              initialValue: config.className,
            })(
              <Input disabled={readonly} onChange={(e) => this.changeConfig('className', e)} />
            )}
          </FormItem>
          <FormItem label="Args："  {...formItemLayout2}>
            {getFieldDecorator('args', {
              initialValue: this.state.args[0].value,
            })(
              <Input maxLength="200" disabled={readonly} onChange={(e) => this.changeArgs(e)} />
            )}
          </FormItem>
        </Form>
      </Modal>
    )
  }
};

export default connect()(Form.create()(Spark));
