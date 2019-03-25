import React from 'react';
import {connect} from 'dva';
import { Button, Form, Input, Radio,Select,Col,Row,Table,Icon,message } from 'antd';
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import { get_table_struct,edit_table_struct,get_metatable_id,add_table_struct,search_table_struct } from '../../../../services/metadata';
import EditTable from '../../../../components/common/EditTable';
import Modal from 'components/Modal';

class RegisterModel extends React.Component{
  Import(keyOfCol,e,record){
    const args=this.state.dataSource;
    for(let index of args){
      if(index.key==record.key){
        index[keyOfCol]=e.target.value;
      }
    }
    this.setState({
      dataSource:args
    })
  }
  state = {
    pagination:{},
    pagination1:{
      current:1,
      pageSize:4
    },
    data1:[],
    data2:[],
    loading:false,
    loading1:false,
    count:0,
    status:"newTable",
    info:{},
    metaid:"",
    selectedRowKeys:[],
    selectedRows:[],
    metaName:"",
    oldMetaName:""
  }

  columns1 = [
    {
      title: '序号',
      dataIndex: 'key',
      key: 'key'
    },{
      title: '数据表名称',
      dataIndex: 'metaName',
      key: 'metaName'
    }, {
      title: '提供频率',
      dataIndex: 'frequency',
      key: 'frequency',
    },{
      title: '是否已生成实体表',
      dataIndex: 'iscreate',
      key: 'iscreate',
    },{
      title: '状态',
      dataIndex: 'status',
      key: 'status',
    },{ title: '操作',
      dataIndex: '',
      key: 'x',
      render: (text,record) => {
      return <Icon onClick={()=>{this.handleIconClick(record)}} type="edit" className="op-icon"/>
  }}
];

handleIconClick(record){
  this.setState({
    loading1: true
  });
  edit_table_struct(record.metaid).then((res)=>{
    let args = res.data.data;
  let count = 0;
  for(let index of args){

    index.key =  count++;
    if(!index.isNull){
      index.isNull = '1'
    }

  }

  this.refs.editTable.upDateTable([],0);
  this.refs.editTable.upDateTable(args,count);

  const {setFieldsValue } = this.props.form;
  setFieldsValue({
    metaName:record.metaName,
    frequency:record.frequency
  });

  this.setState({
    loading1:false,
    status:"editTable",
    metaid:record.metaid
  })
})
  setTimeout(()=>{
    if(this.state.loading){
    try{this.setState({loading:false})}catch(err){
      console.log(err)
    }
  }
},4000)
}


columns2 = [
  {
    title: '列名称',
    dataIndex: 'colName',
    key: 'colName',
    editable:true
  },{
    title: '列代码',
    dataIndex: 'colCode',
    key: 'colCode',
    editable:true
  }, {
    title: '数据类型',
    dataIndex: 'dataType',
    key: 'dataType',
    width:"12%",
    selectable:true,
    selectArgs:[<Select.Option key="int" value="int">int</Select.Option>,
  <Select.Option key="float" value="float">float</Select.Option>,
<Select.Option key="double" value="double">double</Select.Option>,
<Select.Option key="smallint" value="smallint">smallint</Select.Option>,
<Select.Option key="bigint" value="bigint">bigint</Select.Option>,
<Select.Option key="numeric" value="numeric">numeric</Select.Option>,
<Select.Option key="bit" value="bit">bit</Select.Option>,
<Select.Option key="real" value="real">real</Select.Option>,
<Select.Option key="varchar" value="varchar">varchar</Select.Option>,
<Select.Option key="char" value="char">char</Select.Option>,
<Select.Option key="date" value="date">date</Select.Option>,
<Select.Option key="time" value="time">time</Select.Option>,
<Select.Option key="year" value="year">year</Select.Option>,
<Select.Option key="datetime" value="datetime">datetime</Select.Option>,
<Select.Option key="timestamp" value="timestamp">timestamp</Select.Option>,
<Select.Option key="text" value="text">text</Select.Option>,
<Select.Option key="longtext" value="longtext">longtext</Select.Option>,
<Select.Option key="blob" value="blob">blob</Select.Option>,
<Select.Option key="longblob" value="longblob">longblob</Select.Option>,
<Select.Option key="enum" value="enum">enum</Select.Option>,
<Select.Option key="set" value="set">set</Select.Option>,
<Select.Option key="binary" value="binary">binary</Select.Option>,
<Select.Option key="point" value="point">point</Select.Option>,
<Select.Option key="decimal" value="decimal">decimal</Select.Option>,
<Select.Option key="mediumint" value="mediumint">mediumint</Select.Option>,
<Select.Option key="raw" value="raw">raw</Select.Option>,
]
},{
  title: '长度',
    dataIndex: 'length',
    key: 'length',
    width:"10%",
    editable:true,
     render:(text,record)=>{
        const value = text ? text : (record.dataType == 'double' ? '8,2' : '');
        return <Input value={value} onChange={(e)=>{this.Import('length',e,record)}} />
      }
},{
  title: '是否主键',
    dataIndex: 'isPk',
    key: 'isPk',
    width:"10%",
    selectable:true,
    selectArgs:[<Select.Option key="none" value="1">是</Select.Option>,
    <Select.Option key="left" value="0">否</Select.Option>
]
},{
  title: '是否允许为空',
    dataIndex: 'isNull',
    key: 'isNull',
    width:"10%",
    selectable:true,
    selectArgs:[<Select.Option key="none" value="1">是</Select.Option>,
<Select.Option key="left" value="0">否</Select.Option>
]
}];

handleSubmit = (e) => {

};

hideModel(){
  const {dispatch,form} = this.props;
  dispatch({
    type:"dsregistermodel/hide",
    visible:false
  });
  form.resetFields();
}

rowSelection={
  onChange: (selectedRowKeys,selectedRows) => {
    this.setState({
    selectedRowKeys:selectedRowKeys,
    selectedRows:selectedRows
    })
  }
};

formItemLayout4 = {
  labelCol: { span: 6 },
  wrapperCol: { span: 15 },
};

componentWillReceiveProps(nextProps){
  console.log(nextProps);
  if(nextProps.dsregistermodel){
    const { model } = nextProps.dsregistermodel;
    if( model === "exportModel" ||  model === "deleteModel" ||  model === "newTableModel" ){
      if(model === "deleteModel"){
        this.setState({
          selectedRowKeys1:[],
          selectedRows1:[]
        })
      }
      const pager = { ...this.state.pagination };
      pager.current = 1;
      this.setState({
        pagination: pager,
      });
      if(this.state.oldMetaName){
        this.Request1(this.state.oldMetaName,{
          current:1,
          pageSize:4
        });
      }else if(nextProps.dsregistermodel.info && nextProps.dsregistermodel.info.dsId){
        const info=this.state.info;
        info.dsId=nextProps.dsregistermodel.info.dsId;
        this.setState({ info });
        console.log(this.state.info.dsId);
        this.Request({
          current:1,
          pageSize:4
        },this.state.info.dsId);
      }
    }
  }
}
Request(pagination,dsId){
  this.setState({
    loading: true
  });
  get_table_struct(pagination,dsId).then((res)=>{
    if(res.data){
    const  total  = res.data.data.total;
    let args = res.data.data.rows;
    for(let index  of args){
      index.key = index.metaid;
      if(index.iscreate === 1){
        index.iscreate = "是";
      }else{
        index.iscreate = "否";
      }
    }
        for (let index  of rows) {
              if (index.status === 0) {
                index.status = "未生效";
              }else if(index.status === 1){
                index.status = "已删除";
              } else if(index.status === 2){
                index.status = "已生效";
              }
            }
    pagination.total = total;
    this.setState({
      loading: false,
      data1:args,
      pagination:pagination
    })
  }
})
  setTimeout(()=>{
    if(this.state.loading){
    try{this.setState({loading:false})}catch(err){
      console.log(err)
    }
  }
},4000)
}

handleTableChange (pagination){
  const pager = { ...this.state.pagination};
  pager.current = pagination.current;
  this.setState({
    pagination: pager,
  });
  if(this.state.oldMetaName){
    this.Request1(this.state.oldMetaName,pagination);
  }else{
    this.Request(pagination,this.state.info.dsId);
  }
}

handleAdd = ()=>{
  const data = {
    name:"",
    type:"",
    length:"",
    format:"",
    precision:""
  };
  this.refs.editTable.handleAdd(data);
}

handleDelete = ()=>{
  this.refs.editTable.handleDelete();
};

/*新建表*/
newTable(){
  const { form } = this.props;
  const { setFieldsValue } = form;
  form.resetFields();

  this.refs.editTable.upDateTable([],0);
  setFieldsValue({
    metaName:"",
    frequency:""
  });
  this.setState({
    status:"newTable"
  })
}
/*保存并定义字段*/
saveTable(e){
  e.preventDefault();
  if(this.state.status === "newTable"){
    this.props.form.validateFields((err, values) => {
      if (err) {
        return
      }
      values.frequency = values.frequency+values.prefix;

    get_metatable_id(values).then((res)=>{
      const { metaid } = res.data.data;
    if(this.refs.editTable){
      if(this.refs.editTable.state.dataSource.length>0){
        for(let index of  this.refs.editTable.state.dataSource){
          index.metaid = metaid;
        }
        add_table_struct(this.refs.editTable.state.dataSource).then((res)=>{

          const { data,msg } = res.data;
        if(data>0 && msg === "Success"){
          const { form } = this.props;
          const { setFieldsValue } = form;
          form.resetFields();

          this.refs.editTable.upDateTable([],0);
          setFieldsValue({
            metaName:"",
            frequency:""
          });
          message.success({
            message: '保存字段成功'
          });
        }
      })
      }
    }
    this.Request({
      current:1,
      pageSize:4
    },this.state.info.dsId);
  })
  });

  }else if(this.state.status === "editTable") {

    if(this.refs.editTable){
      let args = [];
      if(this.refs.editTable.state.dataSource.length>0){
        for(let index of  this.refs.editTable.state.dataSource){
          if(!index.id){
            index.metaid = this.state.metaid;
            args.push(index);
          }
        }
        add_table_struct(args).then((res)=>{
          const { data,msg } = res.data;

        if(data>0 && msg === "Success"){
          const { form } = this.props;
          const { setFieldsValue } = form;
          form.resetFields();

          this.refs.editTable.upDateTable([],0);
          setFieldsValue({
            metaName:"",
            frequency:""
          });
          message.success({
            message: '保存字段成功'
          });
          this.setState({
            status:"newTable"
          })
        }
      })
      }
    }
  }else{
    message.waring({
      message: '操作失败',
      description: '请先选择新建或修改表',
    });
  }
}


handleExport(){
  const {dispatch} = this.props;
  if(this.state.selectedRowKeys.length === 0){
    message.warning({
      message: '通知信息',
      description: '请先选择将要导出的表',
    });
  }else{
    dispatch({
      type:"deletetip/export",
      visible:true,
      model:"dsRegister",
      tip:"导出",
      allSelected:this.state.selectedRowKeys,
      currentSelected:this.state.selectedRows
    })
  }
}

handleTableDelete(){
  const {dispatch} = this.props;
  if(this.state.selectedRowKeys.length === 0){
    message.warning({
      message: '通知信息',
      description: '请先选择将要删除的表'
    });
  }else{
    dispatch({
      type:"deletetip/export",
      visible:true,
      model:"dsRegister",
      tip:"删除",
      allSelected:this.state.selectedRowKeys,
      currentSelected:this.state.selectedRows
    })
  }
}

handleNewTable(){
  const {dispatch} = this.props;
  if(this.state.selectedRowKeys.length === 0){
    message.warning({
      message: '通知信息',
      description: '请先选择将要生成实体表的选项'
    });
  }else{
    dispatch({
      type:"deletetip/export",
      visible:true,
      model:"dsRegister",
      tip:"生成实体表",
      allSelected:this.state.selectedRowKeys,
      currentSelected:this.state.selectedRows
    })
  }
  this.Request1();
}

Request1(metaName,pagination){
  this.setState({
    loading:true
  })
  let obj = {};
  obj.metaName = metaName;
  search_table_struct(obj,pagination).then((res)=>{
    if(res.data){
    const  total  = res.data.data.total;
    let args = res.data.data.rows;

    for(let index  of args){
      index.key = index.metaid;
      if(index.iscreate === "1"){
        index.iscreate = "是";
      }else{
        index.iscreate = "否";
      }
    }
    pagination.total = total;
    this.setState({
      loading: false,
      data1:args,
      oldMetaName:this.state.metaName,
      pagination:pagination
    })
  }
})
  setTimeout(()=>{
    if(this.state.loading){
    try{this.setState({loading:false})}catch(err){
      console.log(err)
    }
  }
},4000)
}
handleSearch(){
  if(this.state.oldMetaName !== this.state.metaName){
    this.Request1(this.state.metaName,{current:1,pageSize:4});
  }
}
handleSearchChange(e){
  if(e.target.value.trim()){

    this.setState({
      metaName:e.target.value.trim()
    })
  }
}
render(){
  const { dsregistermodel,form } = this.props;
  const { getFieldDecorator } = form;
  const { visible,info } = dsregistermodel;
  const selectAfter = getFieldDecorator('prefix', {
      initialValue: 'day',
    })(
    <Select style={{ width: 70 }}>
      <Select.Option value="day">天</Select.Option>
      <Select.Option value="hour">小时</Select.Option>
      <Select.Option value="miunte">分钟</Select.Option>
      <Select.Option value="week">周</Select.Option>
      <Select.Option value="month">月</Select.Option>
      <Select.Option value="quarter">季度</Select.Option>
    </Select>
  );
  return(
    <Modal
      visible={visible}
      title="前置机基本信息"
      wrapClassName="vertical-center-modal"
      width={1000}
      footer={[
       <Button key="back" size="large" onClick={this.hideModel.bind(this)}>取消</Button>,
       <Button key="submit" type="primary" size="large"  onClick={this.saveTable.bind(this)}>确定</Button>
       ]}
      onCancel={this.hideModel.bind(this)}
    >
        <Row>

          <Col span={12}>
            <ButtonGroup style={{marginLeft:"5px",marginBottom:"10px"}}>
              <Button  onClick={this.handleExport.bind(this)}>导出</Button>
              <Button onClick={this.handleTableDelete.bind(this)}>删除</Button>
              <Button onClick={this.handleNewTable.bind(this)}>生成数据库实体表</Button>
            </ButtonGroup>
          </Col>

          <Col span={12}>
            <Input placeholder="可以根据数据表名称模糊搜索" onChange={this.handleSearchChange.bind(this)} style={{width:"75%"}} />
            <Button onClick={this.handleSearch.bind(this)}>搜索</Button>
          </Col>

        </Row>

        <Table
          onRow={(record)=>{console.log(record)}}
          rowSelection={this.rowSelection}
          onChange={this.handleTableChange.bind(this)}
          pagination={this.state.pagination}
          loading={this.state.loading}
          columns={this.columns1}
          dataSource={this.state.data1}
        />

        <ButtonGroup style={{marginLeft:"5px",marginBottom:"10px"}}>
          <Button  onClick={this.newTable.bind(this)}>新建表</Button>
          {/*<Button onClick={this.saveTable.bind(this)}>保存字段定义</Button>
           <Button onClick={this.newTable.bind(this)}>取消</Button>*/}
        </ButtonGroup>

        <Form style={{margin:"10px 0"}}>

          <Col span={12}>
            <FormItem   label="数据表名称: " {...this.formItemLayout4} style={{marginBottom:'8px'}}>
            {getFieldDecorator('metaName', {
              initialValue:this.state.info.metaName,
              rules: [{ required: true, message: '请输入数据表名称' }]
            })(
            <Input disabled={this.state.status === "editTable" ? true:false} />
            )}
          </FormItem>
          </Col>

          <Col span={12}>
            <FormItem   label="数据提供频率: " {...this.formItemLayout4} style={{marginBottom:'8px'}}>
              {getFieldDecorator('frequency', {
                initialValue:this.state.info.frequency,
              })(
              <Input disabled={this.state.status === "editTable" ? true:false}  addonAfter={selectAfter}  />
              )}
            </FormItem>
          </Col>

        </Form>

        <Col span={12} >
          <div style={{marginLeft:"5px",lineHeight:"38px"}}>
            字段列表：
          </div>
        </Col>

        <Col span={12} >
            <ButtonGroup  style={{float:"right",marginBottom:"10px",marginRight:"5px"}}>
              <Button  disabled>从Excel导入</Button>
              <Button  disabled = {this.state.status === "newTable" || this.state.status === "editTable" ? false:true}  onClick={this.handleAdd.bind(this)}>添加字段</Button>
              <Button  disabled = {this.state.status === "newTable" || this.state.status === "editTable" ? false:true} onClick={this.handleDelete.bind(this)} >删除行</Button>
          </ButtonGroup>
        </Col>

        <EditTable
          rowSelection={this.rowSelection}
          disabled={true}
          ref="editTable"
          tableStyle="editTableStyle6"
          pagination={"false"}
          scroll={{  y: 240 }}
          loading={this.state.loading1}
          columns={this.columns2}
          dataSource={this.state.data2}
        />

    </Modal>
)
}
}

const DSRegisterModel = Form.create()(RegisterModel);

export default connect(({ dsregistermodel }) => ({
  dsregistermodel
}))(DSRegisterModel)
