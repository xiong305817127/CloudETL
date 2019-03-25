import { Table, Input, Icon, Button, Popconfirm } from 'antd';
//1.行内编辑组件：input.value
class EditableCell extends React.Component {
  state = {
    value: this.props.value,
    editable: false,
  }
  handleChange = (e) => {
    const value = e.target.value;
    this.setState({ value });
  }
  check = () => {
    this.setState({ editable: false });
    if (this.props.onChange) {
      this.props.onChange(this.state.value);
    }
  }
  edit = () => {
    this.setState({ editable: true });
  }
  render() {
    const { value, editable } = this.state;
    //输出到指代内容列
    return (
      <div className="editable-cell">
        {
          editable ?
            <div className="editable-cell-input-wrapper">
              <Input
                value={value}
                onChange={this.handleChange}
                onPressEnter={this.check}
              />
              <Icon
                type="check-circle-o"
                className="editable-cell-icon-check"
                onClick={this.check}
              />
            </div>
            :
            <div className="editable-cell-text-wrapper">
              {value || ' '}
              <Icon
                type="question-circle-o"
                className="editable-cell-icon"
                onClick={this.edit}
              />
            </div>
        }
      </div>
    );
  }
}
//2.操作组件：新增、删除
class OperateRowModel extends React.Component {
  constructor(props) {
    super(props);
    this.columns = [{
      title: '序号',
      dataIndex: 'num',
      width: '4%',
    }, {
      title: '标题0',
      dataIndex: 'code',
      width: '15%',
      render: (text, record, index) => (
        <EditableCell
          value={text}
          onChange={this.onCellChange(index, 'code')}
        />
      ),
    }, {
      title: '标题1',
      dataIndex: 'name',
      width: '15%',
      render: (text, record, index) => (
        <EditableCell
          value={text}
          onChange={this.onCellChange(index, 'name')}
        />
      ),
    }, {
      title: '固定标题1',
      dataIndex: 'age',
    }, {
      title: '固定标题2',
      dataIndex: 'address',
    }, {
      title: '操作',
      dataIndex: 'operation',
      //输出内容：点击删除
      render: (text, record, index) => {
        return (
          this.state.dataSource.length > 1 ?
            (
              <Popconfirm title="确定删除?" onConfirm={() => this.onDelete(index)}>
                <a href="#">删除</a>
              </Popconfirm>
            ) : null
        );
      }
    }];
    //初始化可编辑内容
    this.state = {
      dataSource: [{
        key: '0',
        num:'1',
        code:'内容1',
        name: '内容2',
        age: '111',
        address: '1111',
      }, {
        key: '1',
        num:'2',
        code:'内容1',
        name: '内容2',
        age: '222',
        address: '2222',
      }],
      count: 2,
    };
  }
  //删除行事件
  onCellChange = (index, key) => {
    return (value) => {
      const dataSource = [...this.state.dataSource];
      dataSource[index][key] = value;
      this.setState({ dataSource });
    };
  }
  onDelete = (index) => {
    const dataSource = [...this.state.dataSource];
    dataSource.splice(index, 1);
    this.setState({ dataSource });
  }
  //添加行事件：
  handleAdd = () => {
    const { count, dataSource } = this.state;
    const newData = {
      key: count,
      num:``,
      code:`内容 ${count}`,
      name: `内容 ${count}`,
      age: ``,
      address: ``,
    };
    this.setState({
      dataSource: [...dataSource, newData],
      count: count + 1,
    });
  }

  render() {
    //指代
    const { dataSource } = this.state;
    const columns = this.columns;
    //输出：按钮+表格
    return (
      <div>
        <Button className="editable-add-btn" onClick={this.handleAdd}>添加行</Button>
        <Table bordered dataSource={dataSource} columns={columns} pagination={false}/>
      </div>
    );
  }
}

export default OperateRowModel;
