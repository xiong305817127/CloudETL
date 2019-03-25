/**
 * Created by Administrator on 2017/5/13.
 */
import React from 'react';
import { connect } from 'dva'
import { Form, Icon, Input, Button , Table,Menu, Dropdown,Cascader,Row,Col,message,Select, Upload,Steps} from 'antd';
import {get_metatable_id,add_table_struct,get_table_struct, importMetadataTable,edit_table_struct} from "../../../../services/metadata";
import {metaNameIsExists} from "../../../../services/metadataDefine";
import { downloadFile,deepCopy, dateFormat, createGUID ,findKeyByValue} from '../../../../utils/utils';
import { getDefaultLength } from '../../../../utils/metadataTools';
import dbDataType from '../../../../config/dbDataType.config';
import Uploads from '../../../../components/Upload';
import dbTypeValue from '../../../../config/dbTypeValue.config';
import Modal from 'components/Modal';
import { submitDecorator } from '../../../../utils/decorator';
import TableList from "components/TableList"
let Timer;
const FormItem = Form.Item;
const Option = Select.Option;
const Step = Steps.Step;
// 缺省记录
const defaultCol = {
  key: createGUID(),
  colName: '',
  description: '',
  dataType: 'int',
  isPk: '0',
  isNull: '1',
  length: '10',
};
const provinceData = ['0', '1'];
const cityData = {
  1: ['是'],
  0: ['否'],
};


const steps = [{
  title: 'First',
  content: 'First-content',
}, {
  title: 'Second',
  content: 'Second-content',
}, {
  title: 'Last',
  content: 'Last-content',
}];

@submitDecorator
class NewStorageTable1 extends React.Component {
  state = {
    visible: false,
    data2: [{...defaultCol}],
    loading1:false,
    status: 'editmodel',
    dsId: '',
    dbDataTypeList: dbDataType['mysql'],
    current: 0,
    ModelClone:"0",
    metaid:'',
    metaNameEn:'',
    metaNameCn:'',
    frequency:'',
    data1:[],
    id:'',
    selectedRowKeys:[],
     cities: cityData[provinceData[0]],
    secondCity: cityData[provinceData[0]][0],
  };

   next(metaid) {
      const { dispatch,newstoragetable } = this.props;
    this.props.form.validateFields((err, values) => {
      if (!err) {
         const current = this.state.current + 1;
         this.setState({ current });
      }
       if(values.metaNameCn == "" || values.metaNameEn == ""){
           message.error("请输入中英文名称");
       }else{
            dispatch({
                type:'newstoragetable/nameModel',
                model:"newTable",
                metaNameEn:values.metaNameEn,
                metaNameCn:values.metaNameCn,
                frequency:values.frequency,
              });
       }
    });

  }
  prev() {
    const { dispatch } = this.props;
    console.log(this.props,"this.propsqwqw");
   /* this.props.form.validateFields((err, values) => {
      if (!err) {*/
           const current = this.state.current - 1;
          this.setState({ current });
         /* const { dispatch } = this.props;*/
     /* }*/
        const obj =this.props.newstoragetable;
        console.log(obj.metaid,"obj");
            /* edit_table_struct(obj.metaid).then((res)=>{*/
             /* console.log(res,"res1");*/
              dispatch({
                type:'newstoragetable/model',
                visible:true,
                model:"newTable",
                metaNameEn:obj.metaNameEn,
                metaNameCn:obj.metaNameCn,
                metaid:obj.metaid,
                frequency:obj.frequency,
                dsId:this.state.dsId,
                serverName:obj.serverName,
                dbDatabasename:obj.dbDatabasename,
               /* data2:this.state.data2,*/
              });
           /* })*/
   /* });*/
  }
  clickMietId(){
     const { dispatch } = this.props;
    this.props.form.validateFields((values) => {

           const current = this.state.current + 1;
          this.setState({ current });
          const { dispatch } = this.props;


      console.log(values,"err, valueslllll");
        const obj =this.props.newstoragetable;
             edit_table_struct(obj.metaid).then((res)=>{
              console.log(res.data.data,"res1");
              dispatch({
                type:'newstoragetable/show',
                visible:true,
                model:"editmodel",
                metaid:obj.metaid,
                dsId:this.state.dsId,
                data2:res.data.data,
                serverName:this.props.newstoragetable.serverName,
                dbDatabasename:this.props.newstoragetable.dbDatabasename,
              });
             this.setState({ data2:res.data.data });
             console.log(this.state.data2,"1we");
            })
    });
  }
  /*检测文件名 数据库系统名称*/
  handleGetNameScarch = (rule,value, callback) => {
    console.log(rule,"rule,value,qqqqqq",value);
    console.log(this.props,"this.props.databasemodel");
    const { info } = this.props.newstoragetable;
    if(value && value !== this.props.newstoragetable){
      if(Timer){
        clearTimeout(Timer);
        Timer = null;
      }
      Timer = setTimeout(()=>{
        let obj = {};
        obj.metaNameEn = value;
        obj.dsType = 3;
        obj.dsId = this.props.newstoragetable.dsId;
        metaNameIsExists([obj]).then(({ data })=>{
          if (data && data.data && data.data[0] && data.data[0][value]) {
            /* message.error();*/
            callback(true)
          }else{

            callback()
          }
        });
      },300);
    }else{
      callback()
    }
  };
  CloneTable(){
    const { dispatch } = this.props;
      console.log(dispatch,this.state.current,"123");
    this.state.current = 0;
     dispatch({
          type:'newstoragetable/hide',
          visible:false,
          model:"editmodel",

        });
  }
  componentWillReceiveProps(nextProps) {
    if (nextProps.newstoragetable.visible && !this.props.newstoragetable.visible) {
      if(nextProps.newstoragetable.model=="editTable"){
        this.setState({
          data2:nextProps.newstoragetable.info,
          status:"editTable",
          dsId:nextProps.newstoragetable.dsId,
          metaid:nextProps.newstoragetable.metaid
        })
      }else if(nextProps.newstoragetable.model=="newTable"){
        this.setState({
          data2: [{ ...defaultCol }],
          status:"newTable",
          dsId:nextProps.newstoragetable.dsId,
           metaid:nextProps.newstoragetable.metaid
        })
      }
    }
  }
  handleCancel = () =>{
    const { dispatch } = this.props;
    this.state.current = 0;
    dispatch(
      {
        type:"newstoragetable/hide",
        visible:false
      }
    );
    this.props.form.resetFields();
  }
  handleChange(value) {
  }
  formItemLayout2 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 16 },
  };
  formItemLayout3 = {
    labelCol: { span: 7, offset: 0},
    wrapperCol: { span: 14 , offset: 0},
  };
  Import(keyOfCol,e,record){
    const args=this.state.data2;
    for(let index of args){
      if(index.key==record.key){
        index[keyOfCol]=e.target.value;
      }
    }
    this.setState({
      data2:args
    })
  }
  Import1(keyOfCol,value,record){
    const args=this.state.data2;
    args.some(item => {
      if (item.key == record.key) {
        item[keyOfCol]=value;
        // 切换数据类型时处理
        if (keyOfCol === 'dataType' && this.state.data2.dataType != value) {
          item.length = getDefaultLength(value);
        }

        if(keyOfCol === 'isPk' && record.isPk === "1"){
          item.isNull = '0';
            this.setState({
                cities: cityData[value],
                secondCity: cityData[value][0],
              });
        }

        return true;
      }
    });
    this.setState({
      data2:args
    });
  }

  // 修改类型时的回调
  handleChangeType(index, value, record) {
    this.props.form.resetFields([`rows[${index}].length`]);
    this.Import1('dataType', value, record);
  }

    // 修改字段
  modifyField(keyOfCol, value, record){
    const { dispatch } = this.props;
    const { viewFields } = this.props.datasystemsegistration;
    const payload = deepCopy(viewFields).find(row => row.key === record.key);
    payload[keyOfCol] = value;
    dispatch({ type: 'model/modifyField', payload });
  }

  makeColumns2 = () => {
    const { getFieldDecorator } = this.props.form;
    return [{
      title: '字段名称',
      dataIndex: 'colName',
      key: 'colName',
      render:(text,record,index)=>{
        return <FormItem labelCol={{span:0}}>
          {getFieldDecorator(`rows[${index}].colName`, {
            initialValue: text,
            rules:[
              { required: true, message: '字段名称不能为空' },
              { pattern: /^(?=[a-z])\w+$/i, message: '只能使用字母、数字、下划线，且必须以字母开头' },
              { validator: this.checkFieldName },
            ]
          })(
            <Input disabled={this.props.newstoragetable.model === "editmodel"} maxLength="50" onChange={(e)=>{this.Import('colName',e,record)}} />
          )}
        </FormItem>
      }
    },{
      title: '字段描述',
      dataIndex: 'description',
      key: 'description',
      render:(text,record)=>{
        return <FormItem labelCol={{span:0}}>
          <Input disabled={this.props.newstoragetable.model === "editmodel"} maxLength="200" value={text} onChange={(e)=>{this.Import('description',e,record)}} />
        </FormItem>
      }
    }, {
      title: '数据类型',
      dataIndex: 'dataType',
      key: 'dataType',
      width:"15%",

      render:(text, record, index)=>{
        return <FormItem labelCol={{span:0}}>
          <Select disabled={this.props.newstoragetable.model === "editmodel"} value={text} onChange={(value)=>{this.handleChangeType(index,value,record)}}>
            {this.state.dbDataTypeList.map(dbtype => (
              <Option key={dbtype} value={dbtype}>{dbtype}</Option>
            ))}
          </Select>
        </FormItem>
      }
    },{
      title: '长度',
      dataIndex: 'length',
      key: 'length',
      width:"10%",
      render:(text,record,index)=>{
        if (record.dataType === 'date') return null; // date类型没有长度
        if (record.dataType === 'datetime') return null; // date类型没有长度
        if (record.dataType === 'timestamp') return null; // date类型没有长度
        if (record.dataType === 'time') return null; // date类型没有长度
        if (record.dataType === 'year') return null; // date类型没有长度
        if (record.dataType === 'tinyblob') return null; // blob类型没有长度
        if (record.dataType === 'blob') return null; // blob类型没有长度
        if (record.dataType === 'longblob') return null; // blob类型没有长度
        const isString = typeof text === 'string';
        const value = isString ? text : Number(text);
        const isRequired = !(record.dataType === 'decimal' || record.dataType === 'numeric');
        return <FormItem labelCol={{span:0}}>
          {getFieldDecorator(`rows[${index}].length`, {
            initialValue: value,
            rules: [
              { required: isRequired, message: '长度不能为空' },
              { validator: this.checkLength(record) },
            ],
          })(
            <Input disabled={this.props.newstoragetable.model === "editmodel"} type={isString ? 'text' : 'number'} onChange={(e)=>{this.Import1('length',e.target.value,record)}} />
          )}
        </FormItem>

      }

    },{
      title: '是否主键',
      dataIndex: 'isPk',
      key: 'isPk',
      width:"10%",
      render:(text,record)=>{

        return <FormItem labelCol={{span:0}}>
          <Select disabled={this.props.newstoragetable.model === "editmodel"} value={text} onChange={(value)=>{this.Import1('isPk',value,record)}}>
            <Option key="none" value="1">是</Option>
            <Option key="left" value="0">否</Option>
          </Select>
        </FormItem>
      },
    },{
      title: '是否允许为空',
      dataIndex: 'isNull',
      key: 'isNull',
      width:"10%",
      render:(text,record)=>{
        return <FormItem labelCol={{span:0}}>
          <Select disabled={record.isPk === '1' || this.props.newstoragetable.model === "editmodel"} value={text} onChange={(value)=>{this.Import1('isNull',value,record)}}>
            <Option key="none" value="1">是</Option>
            <Option key="left" value="0">否</Option>
          </Select>
        </FormItem>
      },
    },{
      title: '操作',
      dataIndex: '',
      width:"10%",
      render: (text,record) => {
        if (this.props.newstoragetable.model === "editmodel") {
          return null;
        } else {
          return (
              <a onClick={()=>this.DeleteRow(record)} style={{ marginLeft: 5 }}>
                <Icon type="delete" className="op-icon"/>
              </a>
            );
        }
      }
    }];
  };

  DeleteRow(record){

    let args;
    args=this.state.data2.filter((index)=>{

      if(index.key == record.key && index.id === record.id){
        return false
      }
      return true
    })
    this.setState({
      data2:args
    });
  }

 addRow(metaid){
    const { dispatch }=this.props;
    // let args=[];
     let count;
     var tmp = [];
    // for(let index of this.state.data2){
    //   args.push({
    //     ...index,
    //     key:createGUID(),
    //   })
    // }

    console.log(this.props.newstoragetable.metaid,"recordrecordrecord",this.state.dsId);
    const args = this.state.data2 || [];
    args.push({...defaultCol, key: createGUID(),id:this.state.id, metaid: this.props.newstoragetable.dsId});
    console.log(args,"add");
    this.setState({
      data2:args,
      metaid:this.props.newstoragetable.dsId
    })
  }
  Request(pagination,dsId) {
    this.setState({
      loading: true
    });
    get_table_struct(pagination, dsId).then((res)=> {
      if (res.data) {
        const total = res.data.data.total;
        let args = res.data.data.rows;
        for (let index  of args) {
          index.key = index.metaid;
          if (index.iscreate === 1) {
            index.iscreate = "是";
          } else {
            index.iscreate = "否";
          }
        }
        pagination.total = total;
        this.setState({
          loading: false,
          data1: args,
          pagination: pagination
        })
      }
    })
  }

  saveTable(e){
    const {dispatch,newstoragetable}=this.props;
    e.preventDefault();
    this.props.form.validateFields({force: true}, (err, values) => {
      if (err) {
        return
      }
      
        dispatch({
              type: "newstoragetable/nameModel",
              metaNameCn:newstoragetable.metaNameCn,
              metaNameEn:newstoragetable.metaNameEn,
              frequency:newstoragetable.frequency,
            });

      console.log(this.state.data2,"data2");
      if(this.state.data2 === [] || this.state.data2 === null || this.state.data2 === undefined || this.state.data2 < 1 ){
         message.error("请先添加字段");
         return;
      }
      this.props.disableSubmit();
      if(this.state.status === "newTable"){
         const objS={};
          objS.metaNameCn = newstoragetable.metaNameCn;
          objS.metaNameEn = newstoragetable.metaNameEn;
        /*  objS.frequency = newstoragetable.frequency;*/
          /*objS.metaid = newstoragetable.metaid;*/
          objS.dsId=this.state.dsId;
          console.log(this.props,"newstoragetable");
          get_metatable_id(objS).then((res)=>{
            // const { metaid } = res.data.data;
            const metaid = res && res.data && res.data.data && res.data.data.metaid || '';
            if(this.state.data2.length>0){
              for(let index of  this.state.data2){
                index.metaid = metaid;
              }
              dispatch({
                type: "newstoragetable/model",
                metaid:metaid,
                metaNameCn:res.data.data.metaNameCn,
                metaNameEn:res.data.data.metaNameEn,
                frequency:res.data.data.frequency,
                serverName:this.props.newstoragetable.serverName,
              });
              this.setState({
                metaid:this.state.metaid,
                metaNameCn:this.state.metaNameCn,
                metaNameEn:this.state.metaNameEn,
                frequency:this.state.frequency,
              })
            } 
              const obj=values;
              obj.dsId=this.state.dsId;
              add_table_struct(this.state.data2).then((res)=>{
                const { code, data, msg } = res.data;
                this.props.enableSubmit();
                if(code === '200' && msg === "Success"){
                   this.setState({
                      current:0
                    });
                  message.success('保存字段成功');
                  dispatch({
                    type: "newstoragetable/hide",
                    visible:false
                  });
                  dispatch({
                    type: "storagetable/reloadList",
                    reload: true,
                  });
                  this.props.form.resetFields();
                }
              })
          }) 
          
      }else if(this.state.status === "editTable") {
        let args = [];
        if(this.state.data2.length>0){
          for(let index of  this.state.data2){
            if(!index.id){
              index.metaid = this.state.metaid;
              args.push(index);
            }
          }
          add_table_struct(args).then((res)=>{
            const { code, data, msg } = res.data;
            this.props.enableSubmit();
            if(code === '200' && msg === "Success"){
              this.setState({
                current:0
              });
              message.success('保存字段成功');
              dispatch({
                type: "newstoragetable/hide",
                visible:false
              });
            }
          });
        } else {
          dispatch({
            type: "newstoragetable/hide",
            visible:false
          });
        }
      }else{
        message.warning({
          message: '操作失败',
          description: '请先选择新建或修改表',
        });
      }
    });
  }
    // 上传状态改变
  uploadStatusChange(e) {
     const { dispatch } = this.props;
        const obj =this.props.newstoragetable;
        console.log(obj,"objshangchuang",e.file);
         const { response } = e.file || undefined || e.file.response.data.metapros;
          console.log(response,"response");
          dispatch({
            type:'newstoragetable/nameModel',
            data2:response.data.metapros,
          });
        /* edit_table_struct(obj.metaid).then((res)=>{
                   const { response } = e.file;
                    if (response) {
                      if (response.code == '0') {
                        message.success('导入成功');
                        console.log(res.data.metapros,"res1");
                          dispatch({
                            type:'newstoragetable/show',
                            metaid:obj.metaid,
                            visible:true,
                            dsId:this.state.dsId,
                            data2:res.data.metapros,
                            serverName:this.props.newstoragetable.serverName,
                            dbDatabasename:this.props.newstoragetable.dbDatabasename,
                          });
                      } else {
                        message.error(response.msg);
                      }
                    }
                  })*/
        this.setState({ data2:response.data.metapros });
        }
  // 下载模板
  downloadExcel = () => {
    downloadFile('files/excel-template/表结构注册-mysql表结构导入.xlsx');
  };

 rowSelection = {
    onChange: (selectedRowKeys, selectedRows) => {
      console.log(selectedRowKeys, selectedRows,"selectedRowKeys, selectedRows");
    },
    getCheckboxProps: record => {
      return{
      /*disabled:record.metaid === metaid*/
    }}
  };

  // 长度检验
  checkLength = (record) => (rule, value, callback) => {
    if (record.dataType === 'float' || record.dataType === 'real' || record.dataType === 'double') {
      if (!/^\d+,\d+$/.test(value)) {
        callback('缺少精度或格式错误');
        return;
      }
    }
    callback();
  }

  // 字段名验证
  checkFieldName = (rule, value, callback) => {
    const { getFieldError, getFieldValue, setFields } = this.props.form;
    const fields = getFieldValue('rows');
    const checkRepeat = (val) => {
      let repeatedCount = 0;
      fields.forEach((field) => {
        if (field.colName === val) {
          repeatedCount += 1;
        }
      });
      return repeatedCount > 1;
    };
    // 重新校验
    fields.forEach((field, index) => {
      const fieldName = `rows[${index}].colName`;
      const err = getFieldError(fieldName);
      const val = getFieldValue(fieldName);
      if (err && err.some(it => it === '字段名重复') && !checkRepeat(val)) {
        setFields({
          [fieldName]: { value: val },
        });
      }
    });
    if (checkRepeat(value)) {
      callback('字段名重复');
      return;
    }
    callback();
  }

     /* { this.state.current > 0 &&
       <Button disabled={model === "editmodel"} style={{float:"right",marginTop:"-25px",marginRight:"6px"}} onClick={() => this.prev()}>上一步</Button>
     }*/

  render(){
    console.log(this.state.data2,"111");
    const { current,ModelClone } = this.state;
    const { getFieldDecorator } = this.props.form;
    const {visible,model,status, metaid, dsId, info,metaNameEn,metaNameCn,frequency,serverName,dbDatabasename}=this.props.newstoragetable;
    console.log(this.props.newstoragetable.model,"yyyyy");
    const selectAfter = getFieldDecorator('frequencyUnits', {
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

     let renderShow;
    switch (this.state.current) {
      case 0:
        renderShow = (
          <div>
              <FormItem label="表英文名称：" {...this.formItemLayout2} >
                {getFieldDecorator('metaNameEn', {
                  initialValue:metaNameEn?metaNameEn:"",
                   validateFirst: true,
                   validateTrigger: 'onBlur',
                  rules: [
                    { required: true, message: '请输入表英文名称' },
                    { pattern: /^(?=[a-z])[0-9a-z_-]+$/, message: '只能使用小写字母、数字、下划线，且必须以字母开头' },
                    {validator:this.handleGetNameScarch.bind(this),message: '表英文名称已存在' }
                  ]
                })(
                  <Input disabled={model === "editmodel"} maxLength="50"/>
                )}
              </FormItem>

              <FormItem label="表中文名称：" {...this.formItemLayout2} >
                {getFieldDecorator('metaNameCn', {
                  initialValue:metaNameCn?metaNameCn:"",
                  rules: [
                    { required: true, message: '请输入表中文名称' },
                    { pattern: /(?=.*[\u4e00-\u9fa5])/, message: '请输入正确的表中文名称' },
                  ]
                })(
                  <Input disabled={model === "editmodel"} maxLength="50"/>
                )}
              </FormItem>

              <FormItem   label="数据提供频率: " {...this.formItemLayout2} style={{marginBottom:'8px'}}>
                {getFieldDecorator('frequency', {
                  initialValue:frequency?frequency:'',
                  rules: [
                    { pattern: /^[0-9]*[1-9][0-9]*$/, message: '请输入正整数' },
                  ]
                })(
                  <Input disabled={model === "editmodel"} addonAfter={selectAfter}  />
                )}
              </FormItem>
          </div>
        )
        break;
      case 1:
        renderShow = (
          <div>
              <Row>
                <Col span={12}>
                    <Uploads
                      name="sourceFile"
                      accept='application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
                      action={importMetadataTable+'?dbType=3'}
                      showUploadList={false}
                      onChange={this.uploadStatusChange.bind(this)}
                    >
                      <Button disabled={model === "editmodel"}>从Excel导入</Button>
                    </Uploads>

                 <Button disabled={model === "editmodel"} onClick={this.downloadExcel.bind(this)}>下载Excel模板</Button>
                </Col>
                <Col span={12}><Button disabled={model === "editmodel"} onClick={()=>{this.addRow()}} style={{float:"right"}}>添加字段</Button></Col>
              </Row>

                <TableList rowSelection={this.rowSelection} disabled={true}
                  pagination={false} loading={this.state.loading1} columns={this.makeColumns2()}
                  dataSource={this.state.data2} className="editor-table" />
          </div>
        )
        break;
      default:
        break;
    }
    return (
      <Modal visible={visible}
             onCancel ={this.handleCancel}
             width={this.state.current === 0 ? 500 : 1100}
             title={serverName+" "+dbDatabasename+"数据库表注册"}
             maskClosable={false}
             okText="查看表字段"
             footer={null}
      >
        <Form>
              <div className="steps-content">
                 {
                  renderShow
                 }
              </div>
              <div className="steps-action" style={{margin:30}}>
                {
                  this.state.current ? this.state.current < steps.length - 1 : this.state.current < steps.length - 0
                  &&
                       <div>
                         <Button disabled={model === "editmodel"} style={{float:"right",marginTop:"-25px"}} type="primary" onClick={() => this.next(metaid)}>下一步</Button>
                         <Button disabled={model === "newTable"} style={{float:"right",marginTop:"-25px",marginRight:'10px'}} type="primary" onClick={() => this.clickMietId(metaid)}>查看表结构</Button>
                    </div>

                }
                {
                  this.state.current === 1
                  &&
                  <div>
                    <Button disabled={this.props.submitLoading} style={{float:"right",marginTop:"-25px",marginLeft:"5px"}} type="primary" onClick={this.CloneTable.bind(this)}>取消</Button>
                     <Button disabled={model === "editmodel"} style={{float:"right",marginTop:"-25px"}} type="primary" onClick={this.saveTable.bind(this)} loading={this.props.submitLoading}>保存</Button>
                  </div>

                }

              </div>

        </Form>
      </Modal>
    );
  }
}
const NewStorageTable = Form.create()(NewStorageTable1);
export default connect(({ newstoragetable }) => ({
  newstoragetable
}))(NewStorageTable);
