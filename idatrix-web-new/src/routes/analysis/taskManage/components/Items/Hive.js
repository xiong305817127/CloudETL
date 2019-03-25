/**
 * Created by Administrator on 2017/3/13.
 */
import { Row, Col, Table, Form, Input, Popconfirm, Tooltip, Icon, Button } from 'antd'
import { connect } from 'dva'
import Modal from 'components/Modal';
import moment from "moment";

const FormItem = Form.Item;
const Column = Table.Column;

// 生成随机key值
const createKey = () => {
  return moment().format() + (Math.random() * 1e10);
};

const formItemLayout1 = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};

class Hive extends React.Component{

  state = {
    id: '',
    name: '',
    list: [],
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
      const list = [];
      if (items.config && items.config.length) {
        items.config.forEach((item, index) => list.push({
          key: createKey(),
          index: index + 1,
          value: item,
        }))
      } else { // 默认给一个空行
        list.push({
          key: createKey(),
          index: 1,
          value: '',
        });
      }
      this.setState({
        id: items.id,
        name: items.text,
        list,
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

  // 保存节点
  saveItem = ()=>{
    const { list } = this.state;
    const {id,saveItem } = this.props.items;
    this.props.form.validateFields((err, values) => {
      if (!err) {
        const obj = {};
        obj.id = id;
        obj.config = list.map(item => item.value);
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

  // 添加行
  handleAddRow = () => {
    const { list } = this.state;
    const index = list.length + 1;
    list.push({
      key: createKey(),
      index,
      value: '',
    });
    this.setState({ list });
  };

  // 删除行
  handleDelete = (record) => {
    const { list } = this.state;
    let index = -1;
    list.some((item, i) => {
      index = i;
      return item.key === record.key;
    })
    list.splice(index, 1);
    list.forEach((item, index2) => item.index = index2 + 1);
    this.setState({ list });
  };

  // 编辑行
  changeRecord(e, record) {
    const { list } = this.state;
    list.some((item) => {
      const found = item.key === record.key;
      if (found) {
        item.value = e.target.value;
      }
      return found;
    })
    this.setState({ list });
  }

  render() {
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
          <Table
            dataSource={this.state.list}
            bordered
            pagination={false}
            style={{marginTop:20}}
            rowKey="key"
          >
            <Column title="序号" width={40} dataIndex="index" />
            <Column title="HQL" render={(text, record) => (
              <Input maxLength="200" disabled={readonly} value={record.value} onChange={(e) => this.changeRecord(e, record)} />
            )} />
            <Column title="" width={40} render={(text, record) => !readonly ? (
              <Popconfirm title="确认要删除该行吗？" onConfirm={() => this.handleDelete(record)}>
                <Tooltip title="删除"><a><Icon type="delete" className="op-icon" /></a></Tooltip>
              </Popconfirm>
            ) : null} />
          </Table>
          <div style={{marginTop:10}}>
            <Button disabled={readonly} onClick={this.handleAddRow}>添加行</Button>
          </div>
        </Form>
      </Modal>
    )
  }
};

export default connect()(Form.create()(Hive));
