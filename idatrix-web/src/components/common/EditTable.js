import React ,{PropTypes} from 'react'
import { Table, Input, Icon,Select } from 'antd';
import Style from './EditTable.css'


class EditableCell extends React.Component {

  state = {
    value: this.props.value,
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

  render() {
    const { value} = this.state;
    const { disabled } = this.props;
    return (
      <div className="editable-cell">
        <div className="editable-cell-input-wrapper">
          <Input
            value={value}
            onChange={this.handleChange}
            onBlur={this.check}
            disabled={disabled}
          />
        </div>
      </div>
    );
  }
}

class SelectCell extends React.Component {
  constructor(props){
    super(props);
    this.state = {
      value: props.value,
    }
  }

  handleChange = (e) => {
    this.setState({ value:e });
  }
  check = () => {
    if (this.props.onChange) {
      this.props.onChange(this.state.value);
    }
  }

  render() {
    const { value} = this.state;

    const { disabled,options } = this.props;



    return (
    <Select  value={value}
             onBlur={this.check}
             onChange={this.handleChange}
             disabled={disabled}
        style={{width:"100%"}}
    >
      {options}
    </Select>
    );
  }
}



class EditTable extends React.Component {
  constructor(props) {
    super(props);
    let count = 0;
    if(props.count){
      count = parseInt(props.count);
    }

    this.state = {
      dataSource: props.dataSource,
      count:count,
      disabled:props.disabled,
      selectedRowKeys:[]
    };
    this.columns =  props.columns;
    for(let i=0;i<this.columns.length;i++){
      if(this.columns[i].editable){
        this.columns[i].render = (text, record, index) => {
          let disabled = false;
          if(record.id){
            disabled = true;
          }
         return  (
          <EditableCell
            value={text}
            onChange={this.onCellChange(index,this.columns[i].dataIndex)}
            disabled={disabled}
          />
        )}
      }else if(this.columns[i].selectable){
        this.columns[i].render = (value, record, index) => {
          let disabled = false;
          if(record.id){
            disabled = true;
          }
          return (
            <SelectCell
              value={value}
              onChange={this.onCellChange(index,this.columns[i].dataIndex)}
              disabled={disabled}
              options={this.columns[i].selectArgs}
            />
          )
        }
      }
    }
  }
  onCellChange = (index, key) => {
    return (value) => {
      const dataSource = [...this.state.dataSource];
      dataSource[index][key] = value;
      this.setState({ dataSource });
    };
  }


  handleAdd = (data) => {

    const { count, dataSource } = this.state;
    const newData = {
      key: count,
      ...data,
      editTableStatus:"new"
    };

    this.setState({
      dataSource: [...dataSource, newData],
      count: count + 1
    });
  }


  handleDelete = ()=>{
    if(this.state.dataSource && this.state.dataSource.length>0){
      let args = [];
      if(this.state.selectedRowKeys && this.state.selectedRowKeys.length>0){
          args =  this.state.dataSource.filter((index)=>{
            return ((index)=>{
                  for(let key of this.state.selectedRowKeys){
                      if(index.key === key){
                          return false;
                      }
                  }
                  return true;
            })(index)
          } )
        this.setState({
          dataSource:args
        })
      }
    }
  }

   rowSelection = {
    onChange: (selectedRowKeys,selectedRows) => {
      this.setState({
        selectedRowKeys:selectedRowKeys,
        selectedRows:selectedRows
      })
    },
     getCheckboxProps: record => {
       return ({
         disabled: record.id ? true:false
       })
     },
  };

  upDateTable(args,count){
      this.setState({
        dataSource:args,
        count:count
      })
  }


  handleChange(pagination){
    const pager = { ...this.state.pagination};

    pager.current = pagination.current;
    this.setState({
      pagination: pager,
    });
  }

  render() {

    const { dataSource } = this.state;

    const columns = this.columns;
    return (
      <div  className={this.props.tableStyle} >
        <Table {...this.props} bordered  onChange={this.handleChange.bind(this)}  dataSource={dataSource} pagination={false} rowSelection={this.props.rowSelection?this.rowSelection:null}    columns={columns} />
      </div>
    )
  }
}

export default EditTable;

