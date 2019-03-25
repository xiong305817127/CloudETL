import { Table, Input, Popconfirm } from 'antd';
//子组件:input.value
class EditableCell extends React.Component {
  //1.初始化：编辑
  state = {
    value: this.props.value,
    editable: this.props.editable || false,
  }
  //生命周期1：
  componentWillReceiveProps(nextProps) {
    if (nextProps.editable !== this.state.editable) {
      this.setState({ editable: nextProps.editable });
      if (nextProps.editable) {
        this.cacheValue = this.state.value;
      }
    }
    if (nextProps.status && nextProps.status !== this.props.status) {
      if (nextProps.status === 'save') {
        this.props.onChange(this.state.value);
      } else if (nextProps.status === 'cancel') {
        this.setState({ value: this.cacheValue });
        this.props.onChange(this.cacheValue);
      }
    }
  }
  //生命周期2：
  shouldComponentUpdate(nextProps, nextState) {
    return nextProps.editable !== this.state.editable ||
      nextState.value !== this.state.value;
  }
  //3.修改事件
  handleChange(e) {
    const value = e.target.value;
    this.setState({ value });
  }
  //输出内容：编辑框input+编辑value
  render() {
    const { value, editable } = this.state;
    return (
      <div>
        {
          editable ?
            <div>
              <Input
                value={value}
                onChange={e => this.handleChange(e)}
              />
            </div>
            :
            <div className="editable-row-text">
              {value.toString() || ' '}
            </div>
        }
      </div>
    );
  }
}
//父组件:table
class EdiRowTable extends React.Component {
  //1.初始化：标题+内容+编辑事件！
  constructor(props) {
    super(props);
    //标题不可编辑
    this.columns = [{
      title: '序号',
      dataIndex: 'num',
      width: '5%',
      render: (text, record, index) => this.renderColumns(this.state.data, index, 'key', text),
    }, {
      title: '标题1',
      dataIndex: 'name',
      width: '25%',
      render: (text, record, index) => this.renderColumns(this.state.data, index, 'name', text),
    }, {
      title: '标题2',
      dataIndex: 'age',
      width: '15%',
      render: (text, record, index) => this.renderColumns(this.state.data, index, 'age', text),
    }, {
      title: '标题3固定值',
      dataIndex: 'address',
      width: '40%',
      render: (text, record, index) => this.renderColumns(this.state.data, index, 'address', text),
    }, {
      title: '操作',
      dataIndex: 'operation',
      //点击编辑！！！
      render: (text, record, index) => {
        const { editable } = this.state.data[index].name;
        return (
          <div className="editable-row-operations">
            {
              editable ?
                <span>
                  <a onClick={() => this.editDone(index, '保存')}>保存</a>
                  <Popconfirm title="确定取消?" onConfirm={() => this.editDone(index, '取消')}>
                    <a>/取消</a>
                  </Popconfirm>
                </span>
                :
                <span>
                  <a onClick={() => this.edit(index)}>编辑</a>
                </span>
            }
          </div>
        );
      },
    }];
    //内容可编辑
    this.state = {
      data: [{
        key: '0',
        num: {
          editable: false,
          value: '1',
        },
        name: {
          editable: false,
          value: '内容1',
        },
        age: {
          editable: false,
          value: '内容2',
        },
        address: {
          value: '内容3',
        },
      }],
    };
  }
  renderColumns(data, index, key, text) {
    const { editable, status } = data[index][key];
    if (typeof editable === 'undefined') {
      return text;
    }
    return (<EditableCell
      editable={editable}
      value={text}
      onChange={value => this.handleChange(key, index, value)}
      status={status}
    />);
  }
  //点击编辑：三则运算
  handleChange(key, index, value) {
    const { data } = this.state;
    data[index][key].value = value;
    this.setState({ data });
  }
  edit(index) {
    const { data } = this.state;
    Object.keys(data[index]).forEach((item) => {
      if (data[index][item] && typeof data[index][item].editable !== 'undefined') {
        data[index][item].editable = true;
      }
    });
    this.setState({ data });
  }
  editDone(index, type) {
    const { data } = this.state;
    Object.keys(data[index]).forEach((item) => {
      if (data[index][item] && typeof data[index][item].editable !== 'undefined') {
        data[index][item].editable = false;
        data[index][item].status = type;
      }
    });
    this.setState({ data }, () => {
      Object.keys(data[index]).forEach((item) => {
        if (data[index][item] && typeof data[index][item].editable !== 'undefined') {
          delete data[index][item].status;
        }
      });
    });
  }
  //输出编辑内容
  render() {
    const { data } = this.state;
    //数据来源
    const dataSource = data.map((item) => {
      const obj = {};
      Object.keys(item).forEach((key) => {
        obj[key] = key === 'key' ? item[key] : item[key].value;
      });
      return obj;
    });
    //标题
    const columns = this.columns;
    //输出表格：
    return <Table bordered dataSource={dataSource} columns={columns} />;

  }
}

export default EdiRowTable;
