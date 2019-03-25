import React from 'react'
import { Table, Input,Select,Button,Row,Col } from 'antd';
import styles from './EditTable.less'
import Modal from "components/Modal.js";
import _ from "lodash";
import classnames from "classnames"

const ButtonGroup = Button.Group;
let optionProps = null;

class EditableCell extends React.Component {

  state = {
    value: this.props.value,
  }
  handleChange = (e) => {

    const value = e.target.value;
    this.setState({ value });
  }
  check = () => {
    if (this.props.onChange) {
      this.props.onChange(this.state.value);
    }
  };

  componentWillReceiveProps(nextProps){
      if(nextProps.value !== this.state.value){
        this.setState({
           value:nextProps.value
        })
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
      options:props.options
    }
  }

  handleChange = (e,option) => {
    this.setState({ value:e });
    if (this.props.onChange) {
      this.props.onChange(e,optionProps);
    }
  };

  handleSelect = (value,option)=>{
    if (this.props.onChange) {
      optionProps = option.props;
      this.props.onChange(value,option.props);
    }
  }

  componentWillReceiveProps(nextProps){
    if(nextProps.value !== this.state.value){
      this.setState({
        value:nextProps.value
      })
    }
  }

  getValue(value,args){
    let num = value;

    for(let index of args) {
      if (index.key == value || value == index.props.children || value == index.props.value) {
        num = index.props.children;
      }
    }
    return num;
  }

  render() {
    let   value = this.state.value;
    const { disabled,options,noChange } = this.props;

    if(options && options.length>0){
        value = this.getValue(value,options)
    }

    return (
    <Select   mode={noChange?false:"combobox"}
              filterOption={false}
              width="100%"
              value={value?value:""}
             onChange={this.handleChange}
             onSelect={this.handleSelect}
             disabled={disabled}>
      {options}
    </Select>
    );
  }
}

class EditTable extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      dataSource: _.cloneDeep(props.dataSource),
      count: props.dataSource.length,
      disabled:props.disabled,
      loading:false,
      options:{},
      selectedRowKeys:{},
      visible:false,
      oldDataSource:[],
      extendDisabled:props.extendDisabled?true:false
    };
    this.columns =  props.columns;

    for(let i=0;i<this.columns.length;i++){
      if(this.columns[i].editable){
        this.columns[i].render = (text, record, index) => {
          return (
            <EditableCell
              value={text}
              onChange={this.onCellChange({
                index,key:this.columns[i].dataIndex
              })}
              disabled={this.columns[i].disabled || this.props.disabled}
            />
          )
        }
      }else if(this.columns[i].selectable){
        this.columns[i].render = (value, record, index) => {
          if(value != undefined){
              value = value.toString();
          };
          
          return (
            <SelectCell
              value={value}
              onChange={this.onCellChange({
                index,
                key:this.columns[i].dataIndex,
                bindField:this.columns[i].bindField,
                bindFuc:this.columns[i].bindFuc,
                record
              })}
              disabled={this.props.disabled}
              noChange={this.columns[i].noChange?true:false}
              options={this.columns[i].selectArgs?this.columns[i].selectArgs:this.state.options[this.columns[i].dataIndex]}
              key={this.columns[i].dataIndex}
            />
          )
        }
      }
    }
  }

  onCellChange = ({index,key,bindField,bindFuc,record}) => {

    return (value,props) => {
      const dataSource = [...this.state.dataSource];
      dataSource[index][key] = value;

      if(bindField && value ){
        if(Array.isArray(bindField) && props){
          for(let name of bindField){
            dataSource[index][name] = bindFuc(name,props,record);   
          }
        }else{
            dataSource[index][bindField] = bindFuc(value,props,record);
        }
      }else{
        bindFuc && bindFuc(value,props,record);
      }

      this.setState({ dataSource });
    };
  };

  componentDidMount(){
    const { initFuc } = this.props;
    if(initFuc){
        initFuc(this);
    }
  };

  reloadTable(){
    this.state.dataSource.splice(0);
    this.setState({
      dataSource:[],
      count:0,
      loading:true
    })
  }

  updateTable(args,count){
    this.reloadTable();
    let con1 = false;
    let con2 = false;
    let con3 = false;

    if(args.length>0){
      let newArg = Object.keys(args[0]);
      if(newArg.includes("length")){
        con1 = true;
      }
      if(newArg.includes("precision")){
        con2 = true;
      }
      if(newArg.includes("position")){
        con3 = true;
      }
    }
    for(let index of args){
        if(con1){
          index.length = index.length?index.length:"-1";
        }
        if(con2){
          index.precision = index.precision?index.precision:"-1";
        }
        if(con3){
          index.position = index.position?index.position:"-1";
        }
    }
    this.setState({
      dataSource:args,
      count:count,
      loading:false
    })
  }

  handleAdd = (data) => {
    const { count, dataSource } = this.state;

    const newData = {
      key: count,
      ...data
    };
    this.setState({
      dataSource: [...dataSource, newData],
      count: count + 1
    });
  };

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
          } );
        this.setState({
          dataSource:args
        });
        this.state.selectedRowKeys.splice(0);
      }
    }
  };

   rowSelection = {
    onChange: (selectedRowKeys) => {
      this.setState({
        selectedRowKeys:selectedRowKeys
      })
    }
  };

  updateOptions = (obj)=>{
    const { options } = this.state;
    console.log(obj);
    console.log(options,"数据");

    this.setState({
      options:{
        ...options,
        ...obj
      }
    })
  };

  //上下移
  handleMove = (action)=>{
      const { selectedRowKeys,dataSource} = this.state;
      let args = dataSource;
      let keys = selectedRowKeys;
      let dataArgs = [];
      if(!keys.length || keys.length === 0){
          return false;
      }
      let selectArgs = args.filter(index=>{
        return keys.includes(index.key);
      });
      let selectArgsRow = args.map((index,key)=>{
          if(keys.includes(index.key)){
            return key;
          }
      });
      let newArgs = args.filter(index=>{
        return !keys.includes(index.key);
      });
      if(action === "up"){
         let key = 0;
          for(let index of selectArgsRow){
              if(typeof(index) !=="undefined"){
                  break;
              }
              key ++;
          }
          if(key >0){
             key = key-1
          }
          newArgs.splice(key,0,...selectArgs);
          dataArgs = newArgs;
      }else if(action === "down"){
        let key = selectArgsRow.length - 1;
        selectArgsRow.reverse();
        for(let index of selectArgsRow){
          if(typeof(index) !=="undefined"){
            break;
          }
          key --;
        }
        if(selectArgs.length === 1){
           if(key < selectArgsRow.length-1){
              key += 1
           }
        }
        newArgs.splice(key,0,...selectArgs);
        dataArgs = newArgs;
      }else if(action == "top"){
          dataArgs = selectArgs.concat(newArgs);
      }else{
          dataArgs =  newArgs.concat(selectArgs);
      }
      this.setState({
        dataSource:dataArgs
      });
  };

  //放大表格
  handleOpenModal(bool){
    const { dataSource } = this.state;
     this.setState({
        visible:bool,
       oldDataSource:dataSource
     })
  }

  handleResolve(){
    const { oldDataSource } = this.state;
    this.setState({
      dataSource:oldDataSource
    })
  }

  handleMinWidth(){
    let { dataSource } = this.state;

    if(dataSource.length>0){
      for(let index of dataSource){
          let args = Object.keys(index);
          index["trimType"] = "both";
          index["trimtypecode"] = "both";
          index["length"] = -1;;
          index["precision"] = -1;
          index["fieldLength"] = -1;
          index["fieldPrecision"] = -1;
      }
      this.setState({dataSource});
    }
    return false;
  }

  handleChangeDisabled(bool){
    this.setState({
      disabled:bool
    })
  }

  render() {
    const { dataSource,extendDisabled } = this.state;

    const setDisable = ()=> {
        if (dataSource.length > 1) {
          return false;
        }
        return true;
    };

    const columns = this.columns;

    const getDisplay = ()=>{
      if(dataSource.length > 0){
          for(let index of columns){
            if(index.dataIndex === "trimType" || index.dataIndex === "trimtypecode"){
              return false;
            }
          }
      }
      return true;
    } 
    
	
    return (
      <div  className={classnames(styles.editable,this.props.tableStyle)} >
        <Table {...this.props} loading={this.state.loading} bordered dataSource={this.state.dataSource} pagination={false} rowSelection={this.props.rowSelection?{...this.rowSelection,columnWidth:"40px"}:null}    columns={columns} />
        <div style={{display:extendDisabled?"none":"block"}}>
        <Row style={{marginTop:"5px"}}>
           <Col span={12}>
             <ButtonGroup size={"small"}>
               <Button disabled={setDisable()} onClick={()=>{this.handleMove("up")}}>上移</Button>
               <Button disabled={setDisable()} onClick={()=>{this.handleMove("down")}}>下移</Button>
			         <Button disabled={getDisplay()} style={{display:getDisplay()?"none":"inline-block"}} onClick={this.handleMinWidth.bind(this)}>最小宽度</Button>
             </ButtonGroup>
           </Col>
           <Col span={12} style={{textAlign:"right"}}>
             <ButtonGroup size={"small"}>
               <Button disabled={setDisable()} onClick={()=>{this.handleMove("top")}}>到顶部</Button>
               <Button disabled={setDisable()} onClick={()=>{this.handleMove("bottom")}}>到尾部</Button>
               <Button disabled={this.state.disabled} onClick={()=>{this.handleOpenModal(true)}}>放大表格</Button>
             </ButtonGroup>
           </Col>
        </Row>
        <Modal
          visible={this.state.visible}
          title="表格操作"
          wrapClassName="vertical-center-modal"
          width={"90%"}
          zIndex={1020}
          onCancel={()=>{this.handleOpenModal(false)}}
          footer={[
            <Button key="submit" type="primary"  onClick={()=>{this.handleOpenModal(false)}}>
              关闭
            </Button>
          ]}
        >
          <div className={classnames(styles.editable,this.props.tableStyle)} >
            <Row style={{marginBottom:"10px"}}>
              <Col span={12}>
                <ButtonGroup >
                  <Button disabled={setDisable()} onClick={()=>{this.handleMove("up")}}>上移</Button>
                  <Button disabled={setDisable()} onClick={()=>{this.handleMove("down")}}>下移</Button>
                  <Button disabled={setDisable()} onClick={()=>{this.handleMove("top")}}>到顶部</Button>
                  <Button disabled={setDisable()} onClick={()=>{this.handleMove("bottom")}}>到尾部</Button>
                </ButtonGroup>
              </Col>
              <Col span={12} style={{textAlign:"right"}}>
                <ButtonGroup >
                  <Button disabled={this.state.disabled} onClick={()=>{this.handleAdd({})}}>增加</Button>
                  <Button disabled={this.state.disabled} onClick={()=>{this.handleDelete()}}>删除</Button>
                  <Button disabled={this.state.disabled} onClick={()=>{this.handleResolve()}}>复原</Button>
                </ButtonGroup>
              </Col>
            </Row>
            <Table {...this.props} scroll={{y: 600}} loading={this.state.loading} bordered dataSource={this.state.dataSource} pagination={false} rowSelection={this.props.rowSelection?this.rowSelection:null}    columns={columns} />
          </div>
        </Modal>
        </div>
      </div>
    )
  }
}

export default EditTable;

