/**
 * Created by Administrator on 2017/3/13.
 */
import { Row, Col, Form, Input, Popconfirm, Tooltip, Icon, Button, message, Table } from 'antd'
import { connect } from 'dva';
import { jarUploadApi } from '../../../../../services/analysisTask';
import Upload from 'components/Upload';
import Modal from 'components/Modal';
import moment from "moment";

const FormItem = Form.Item;
const Column = Table.Column;

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

class HadoopJava extends React.Component{

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

  // 上传jar
  uploadJar = (res) => {
    const { file: { response }, event} = res;
    // 上传进度100
    if (event && event.percent === 100) {
      // console.log('上传完成');
    }
    // 服务器返回信息
    if (response) {
      if (response.code === "200") {
        const { config } = this.state;
        const { className, jarAttachementId, fileName } = response.data;
        config.className = className;
        config.jarAttachementId = jarAttachementId;
        config.fileName = fileName;
        this.setState({ config });
      }
    }
  };

  // 添加参数
  handleAddRow = () => {
    const { args } = this.state;
    args.push({
      key: createKey(),
      name: '',
      value: '',
    });
    this.setState({ args });
  };

  // 删除参数
  handleDelete = (record) => {
    const { args } = this.state;
    let index = -1;
    args.some((item, i) => {
      index = i;
      return item.key === record.key;
    })
    args.splice(index, 1);
    args.forEach((item, index2) => item.index = index2 + 1);
    this.setState({ args });
  };

  // 编辑参数
  changeRecord(e, type, record) {
    const { args } = this.state;
    const value = e.target.value;
    if (type === 'name' && !paramPatt.test(value)) {
      return;
    }
    args.some((item) => {
      const found = item.key === record.key;
      if (found) {
        item[type] = value;
      }
      return found;
    })
    this.setState({ args });
  }

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
              <Input maxLength="200" disabled={readonly} onChange={(e) => this.changeConfig('className', e)} />
            )}
          </FormItem>
          {/*<FormItem label="Args："  {...formItemLayout2}>
            {getFieldDecorator('parameter', {
              initialValue: config.parameter,
            })(
              <Input disabled={readonly} onChange={(e) => this.changeConfig('parameter', e)} />
            )}
          </FormItem>*/}

          <Table
            dataSource={this.state.args}
            bordered
            pagination={false}
            style={{marginTop:20}}
            rowKey="key"
          >
            <Column title="参数名" width="30%" dataIndex="index" render={(text, record) => (
              <Input disabled={readonly} value={record.name} onChange={(e) => this.changeRecord(e, 'name', record)} />
            )} />
            <Column title="参数值" render={(text, record) => (
              <Input disabled={readonly} value={record.value} onChange={(e) => this.changeRecord(e, 'value', record)} />
            )} />
            <Column title="" width={40} render={(text, record) => !readonly ? (
              <Popconfirm title="确认要删除该参数吗？" onConfirm={() => this.handleDelete(record)}>
                <Tooltip title="删除"><a><Icon type="delete" className="op-icon" /></a></Tooltip>
              </Popconfirm>
            ) : null} />
          </Table>

          <div style={{marginTop:10}}>
            <Button disabled={readonly} onClick={this.handleAddRow}>添加参数</Button>
          </div>
        </Form>
      </Modal>
    )
  }
};

export default connect()(Form.create()(HadoopJava));
