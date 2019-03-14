/**
 * Created by Administrator on 2017/8/22.
 */
import React from 'react';
import {connect} from 'dva';
import { withRouter } from 'react-router';
import { Input,Button,Tabs,Radio,Steps,Tooltip, message, Table,Icon,Popconfirm,Form,Select,Row, Col,TreeSelect,Checkbox } from 'antd';
import Modal from 'components/Modal';
const FormItem = Form.Item;
const Search = Input.Search;
const TabPane = Tabs.TabPane;
const Step = Steps.Step;
const RadioGroup = Radio.Group;
const Option = Select.Option;
const { TextArea } = Input;

import Style from '../Acquisition.css'
// import {check_if_dsname_exists} from '../../../../services/metadata'
import TableList from "../../../../components/TableList"
import { SJCJgetdbinfo,SJCJgetTableInfo,
         SJCJgetDbFieldInfo,SJCJinsertTableFields,
         SJCJisExists,XJCJModify,
         XJCJbatchToDelete } from '../../../../services/AcquisitionCommon'
import { getStoreDatabase,getAcquisition } from '../../../../services/metadataCommon'
import { edit_table_struct } from '../../../../services/metadata'
import { dateFormat, safeJsonParse,createGUID } from 'utils/utils'
import { ACQUISITION_DB_TYPE_LIST } from 'constants'
import dbDataType from '../../../../config/dbDataType.config';
import AcquisitionRearTable from './AcquisitionRearTable'
import { submitDecorator } from '../../../../utils/decorator';

const defaultCol = {
  key: createGUID(),
  colName: '',
  description: '',
  dataType: 'int',
  isPk: '0',
  isNull: '1',
  length: '4',
};


const provinceData = ['0', '1'];
const cityData = {
  1: ['是'],
  0: ['否'],
};
@submitDecorator
class AcquisitionNameModel extends React.Component{
  state = {
       pagination:{
          current:1,
          pageSize:10
        },
      current: 0,
      visible: false,
      visibleTs:true,
      info:{},
      /*value: 1,*/
      id:'',
      data2:[],
      dbDataTypeList: dbDataType['mysql'],
       cities: cityData[provinceData[0]],
    secondCity: cityData[provinceData[0]][0],
      
  };
    
     
   columns = [{
        title: '数据源数据表英文名',
        dataIndex: 'metaNameEn',
        key:'metaNameEn',
        width:'50%',
       render: (text,record) => {
        /* <a onClick={() =>this.handleViewModel()}> {text}</a>*/
        return (
          <a onClick={() =>this.handleViewModel(record)}> {text}</a>
          )
        }
      },{
        title: '是否已生成元数据定义',
        dataIndex: 'status',
        key:'status',
        width:'50%',
        render: (text,record) => record.status==="2" ? '是' : '否'
      }/*,{
      title: '改',
      dataIndex: 'operation',
      key:'operation',
      render: (text, record, index) => {
        return (
               <a onClick={()=>{this.handleNewViewModel(record)}}>
                <Tooltip title="编辑" >
                  <Icon type="edit" className="op-icon"/>&nbsp;&nbsp;&nbsp;&nbsp;
                </Tooltip>
              </a>
        );
      },
    }, {
      title: '删',
      dataIndex: 'delete',
       key:'delete',
      render: (text, record, index) => {
        return (
            <Popconfirm title="是否删除?" onConfirm={() => this.onDelete(record)}>
              <a href="#">
                  <Tooltip title="编辑" >
                    <Icon type="delete" className="op-icon"/>&nbsp;&nbsp;&nbsp;&nbsp;
                  </Tooltip>
              </a>
            </Popconfirm>
        );
      },
    }*/];  

    onDelete(record){
       let Mid={};
       Mid.metaid = record.metaid;

       XJCJbatchToDelete([Mid]).then((res)=>{
          if(res.data.code === "200"){
             message.success("删除成功");
             this.handleOnChonel();
          }
       })
     
    }

      columns1 = [{
        title: '字段英文名',
        dataIndex: 'colName',
        key:'colName',
        width:'23%',
      },{
        title: '类型',
        dataIndex: 'dataType',
        key:'dataType',
        width:'20%',
      },{
        title: '长度',
        dataIndex: 'length',
        key:'length',
        width:'23%',
      },{
        title: '是否主键',
        dataIndex: 'isPk',
        key:'isPk',
        width:'20%',
        render: (text,record) => record.isPk === 1 ? '是' : '否'
      },{
        title: '允许为空',
        dataIndex: 'isNull',
        key:'isNull',
        width:'20%',
        render: (text,record) => record.isNull === 1 ? '是' : '否'
      }];  


  Import(keyOfCol,value,record){
    const args=this.props.acquisition.FieldList;
    console.log(args,"++++++++++++++++++++++++this.props.acquisition.FieldList");
    args.some(item => {
      if (item.id == record.id) {
        item[keyOfCol]=value;
        // 切换数据类型时处理
       
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
     const { dispatch }=this.props;
       dispatch({
          type:"acquisition/setMetaId",
            payload:{
               FieldList:args
            }
        });
         this.setState({
            data2:args,
          })
  }


  componentDidMount() {
         this.handleOnChonel();
    }


     handleOnChonel(record){;
        let model = "6";
         const { query } = this.props.location;
         const pager = this.state.pagination;
         const { router, location } = this.props;
         router.push({ ...location, query: { model }});
         this.setState({
           value: model,
           loading:true
        });
          let dsId={
            sourceId:"3",
            dsId:this.props.acquisition.dsId || this.props.params.dsId
          };

          getAcquisition(dsId,{
          current: query.page || 1,
          pageSize: query.pageSize || pager.pageSize,
        }).then((res)=>{
            const {code ,total} = res.data;
            if(code === "200"){
              const { dispatch }=this.props;
               dispatch({
                  type:"acquisition/setMetaIdUpdata",
                    payload:{
                       modelMet:"newModelId",
                       dataBase:res.data.data.rows,
                      /* dataBasename:res.data.data.rows[0] || res.data.data.rows[0].dataSource.dbDatabasename,*/
                      /* metaid:res.data.data.rows[0].metaid*/
                    }
                });

                  pager.total = total;
                  res.data.data.rows.map( (row, index) => {
                    row.key = row.id;
                    row.index = pager.pageSize * (pager.current - 1) + index + 1;
                    return row;
                  });
                  this.setState({
                    data: res.data.data.rows,
                    pagination: pager,
                    loading: false
                  });

              }
          })
  }
      columns2 = () => {
        const { getFieldDecorator } = this.props.form;
        return [{
          title: '字段英文名',
          dataIndex: 'colName',
          key: 'colName',
            width:"20%",
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
                <Input maxLength="50" value={text} onChange={(e)=>{this.Import('colName',e,record)}} />
              )}
            </FormItem>
          }
        }, {
          title: '类型',
          dataIndex: 'dataType',
          key: 'dataType',
          width:"20%",
          render:(text,record,index)=>{
            return <FormItem labelCol={{span:0}}>
            {getFieldDecorator(`rows[${index}].dataType`, {
                initialValue: text,
                 })(
                <Select value={text} style={{width:"200px"}} onChange={(value)=>{this.Import('dataType',value,record)}}>
                  {this.state.dbDataTypeList.map(dbtype => (
                    <Option key={dbtype} value={dbtype}>{dbtype}</Option>
                  ))}
              </Select>
              )}
              
            </FormItem>
          }
        },{
          title: '长度',
          dataIndex: 'length',
          key: 'length',
          width:"15%",
          render:(text,record,index)=>{
            if (record.dataType === 'date') return null; // date类型没有长度
            if (record.dataType === 'datetime') return null; // date类型没有长度
            if (record.dataType === 'timestamp') return null; // date类型没有长度
            if (record.dataType === 'time') return null; // date类型没有长度
            if (record.dataType === 'year') return null; // date类型没有长度
            if (record.dataType === 'tinyblob') return null; // blob类型没有长度
            if (record.dataType === 'blob') return null; // blob类型没有长度
            if (record.dataType === 'longblob') return null; // blob类型没有长度
            return <FormItem labelCol={{span:0}}>
              {getFieldDecorator(`rows[${index}].length`, {
                initialValue: text,
                rules:[
                  { required: true, message: '长度不能为空' },
                  { validator: this.checkLength(record) },
                ]
              })(typeof text === 'string' ? (
                  <Input value={text} onChange={(e)=>{this.Import1('length',e.target.value,record)}} />
                ) : (
                   <Input type='number' value={Number(text)} onChange={(e)=>{this.Import('length',Number(e.target.value),record)}} />
                )
              )}
            </FormItem>

          }

        },{
          title: '是否主键',
          dataIndex: 'isPk',
          key: 'isPk',
          render:(text,record,index)=>{
            return <FormItem labelCol={{span:0}}>
             {getFieldDecorator(`rows[${index}].isPk`, {
                initialValue: text,
                   })(
                  <Select value={text} onChange={(value)=>{this.Import('isPk',value,record)}} style={{width:"100px"}}>
                    <Option key="none" value="1">是</Option>
                    <Option key="left" value="0">否</Option>
                  </Select>
              )}
            </FormItem>
          },
        },{
          title: '是否允许为空',
          dataIndex: 'isNull',
          key: 'isNull',
          render:(text,record,index)=>{
            return <FormItem labelCol={{span:0}}>
            {getFieldDecorator(`rows[${index}].isNull`, {
                initialValue: text,
                   })(
                  <Select disabled={record.isPk === '1'}value={text} onChange={(value)=>{this.Import('isNull',value,record)}} style={{width:"100px"}}>
                    <Option key="none" value="1">是</Option>
                    <Option key="left" value="0">否</Option>
                  </Select>
                  )}
                </FormItem>
          },
        },{
          title: '操作',
          dataIndex: '123',
          width:"10%",
          key: '2',
          render: (text, record, index) => {
             return (
               <a  onClick={(index)=>this.DeleteRow(record)}>
                <Tooltip title="编辑" >
                  <Icon type="delete" className="op-icon"/>&nbsp;&nbsp;&nbsp;&nbsp;
                </Tooltip>
              </a>
        );
      },
        }];
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

  DeleteRow(record){
     const { dispatch }=this.props;
     let args;
    args=this.props.acquisition.FieldList.filter((index)=>{
      if(index.id === record.id){
        return false
      }
      return true
    })
      dispatch({
        type:"acquisition/setMetaId",
          payload:{
             FieldList:args,
          }
      });
      this.setState({
        data2:args,
      })
    
  }


   handleViewModel(record){
    console.log(record,"record++++++++++++++++++");
       let id = record.metaid;
      edit_table_struct(id).then((res)=>{
            const {code } = res.data;
            if(code === "200"){
              
              const { dispatch }=this.props;
               dispatch({
                  type:"acquisition/setMetaIdUpdata",
                    payload:{
                       modelMet:"onClick",
                       FieldList:res.data.data,
                       metaid:record.metaid,
                    }
                });
                
              }
          })
      this.setState({
        visible:true,
      })
   }

   SibmitXJCJModify(){

   }

handleNewViewModel(record){
     let id = record.metaid;
      edit_table_struct(id).then((res)=>{
            const {code } = res.data;
            if(code === "200"){
              const { dispatch }=this.props;
               dispatch({
                      type:"acquisition/setMetaIdUpdata",
                        payload:{
                         modelMet:"newClick",
                         metaid:this.props.acquisition.metaid,
                          FieldList:res.data.data,
                          metaid:record.metaid,
                        }
                    });
               this.setState({
                  data2:res.data.data,
                })
              }
          })

      this.setState({
        visible:true
      })
   }

   handleCancelAlert = (e) =>{
      this.setState({
          visible: false
        });
   }

   MetadataTableHost(){
     const { dispatch }=this.props;
     dispatch({
            type:"acquisition/setMetaIdUpdata",
              payload:{
                 modelMet:"MetadataTableName",
              }
          });

   }
     history(){
       window.history.go(-1);
     }

     rowSelection = {
        onChange: (selectedRowKeysListName, selectedRowsListname) => {
          const { dispatch }=this.props;
           dispatch({
                  type:"acquisition/setMetaId",
                    payload:{
                       selectedRowKeysListName:selectedRowKeysListName,
                       selectedRowsListname:selectedRowsListname,
                    }
                });
                  if(selectedRowKeysListName.length == 0){
                     this.setState({
                      visibleTs:true
                     })
                  }else{
                    this.setState({
                      visibleTs:false
                     })
                  }
      },
    };


 saveTable(e){
    const {dispatch}=this.props;
    e.preventDefault();
    this.props.form.validateFields({force: true}, (err, values) => {
      if (err) {
        return
      }
     /* if(this.state.data2 === [] || this.state.data2 === null || this.state.data2 === undefined || this.state.data2 < 1 ){
         message.error("请先添加字段");
         return;
      }*/
   /*   this.props.disableSubmit();*/
       let joinlist={};
       joinlist = values.rows;
       let Arge=[];
       let i = 0;
          for(let index in joinlist){
              Arge.push({
                key:i++,
                metaid:this.props.acquisition.metaid,
             });
          }
          /*  joinlist.Arge[key].metaid = this.props.acquisition.metaid;*/
              XJCJModify(joinlist).then((res)=>{
                const { code, data, msg } = res.data;
               /* this.props.enableSubmit();*/
                if(code === '200' && msg === "Success"){
                   this.setState({
                      current:0
                    });
                  message.success('保存字段成功');
                  this.setState({
                    visible: false
                  });
                  this.handleOnChonel();
                 /* this.props.form.resetFields();*/
                }
           })  
        })        
      }


  addRow(metaid){
    console.log(metaid,"databaseNameModel+++++++++++++++++++++++++++++++++++++");
    const { dispatch }=this.props;
     let count;
     var tmp = [];
    const args = this.state.data2 || [];
    args.push({...defaultCol, key: createGUID(),id:this.state.id, metaid: this.props.acquisition.metaid,standard:0,status:0,tmType:0,versionid:0});
    console.log(args,"add++++++++++++++++++++++++");
    this.setState({
      data2:args,
      metaid:this.props.acquisition.metaid
    })
  }
    
    
  render() {
   
     const {dataBasename,dsTypes,modelMet,dataBase,FieldList ,valueGrade,data1,data2,data,indexSelect,selectedRows,selectedRowKeys,selectRowKeysRight,selectRowLeft,selectRowRight,selectRowKeysLeft} = this.props.acquisition;
     const { visible,info,dataSource,visibleTs,pagination } = this.state;
     const { getFieldDecorator } = this.props.form;
      const infos = this.props.acquisition;
      const { dispatch }=this.props;
      const { location, router } = this.props;
       /* record.dsType===3?'MySQL':'MySQL' || record.dsType===4?'Hive':'Hive' || record.dsType===5?'Hbase':'Hbase' */
        const database = dsTypes === 3 ? "MySQL":"MySQL" || dsTypes === 4 ? "Hive":"Hive" || dsTypes === 5 ? "Hbase":"Hbase"
        || dataBase[0].dataSource.dsTypes=== 3 ? "MySQL":"MySQL" || dataBase[0].dataSource.dsTypes=== 3 ? "Hive":"Hive" || dataBase[0].dataSource.dsTypes=== 3 ? "Hbase":"Hbase";
         const besaHostName = this.props.acquisition.databaseName  || this.props.params.dbDatabasename;
    return (
      <div>
      {modelMet === "newModelId" || modelMet === "newClick" || modelMet === "onClick" ||  modelMet === ""  ? (
           <Row>
               <Col span={8} className={Style.BestName}>
                  <div>
                     <label style={{marginRight:"5%"}}>数据库名称：<label>{besaHostName}</label></label>
                     <label>数据库类型：<label>{database}</label></label>
                  </div>
               </Col>
           </Row>
        ) : null}

         {modelMet === "newModelId" || modelMet === "newClick" || modelMet === "onClick" ||  modelMet === ""  ? (
            <Row className={Style.BestNameBotton}>
                <Col span={1} ><Button >
                 <a href={"#/DataAcquisition"}> 返回 </a>
                 </Button></Col>
               {/* <Col span={2}><Button > 在前置机生成实体表</Button></Col>*/}
               {/* <Col span={3}> <Button onClick={()=>this.MetadataTableHost()} disabled={visibleTs}>生成元数据定义表</Button></Col>*/}
           </Row>
        ) : null}


           {modelMet === "MetadataTableName" ? (<AcquisitionRearTable location={location} router={router} />) : null}

          {modelMet === "newModelId" || modelMet === "newClick" || modelMet === "onClick" ||  modelMet === ""  ? (
           <TableList showIndex loading={this.state.loading}
             pagination={pagination} style={{width:"99%",margin:"10px"}} dataSource={dataBase} columns={this.columns} />
        ) : null}
          
        
        
        <Modal title="采集进度"
            visible={visible}
            closable={true}
            width="1000px"
            onCancel={this.handleCancelAlert}
            footer={null}>
                <div>
                    {modelMet === "onClick" ? (
                      <TableList scroll={{y:450}} pagination={false} style={{width:"99%",margin:"10px"}} dataSource={FieldList} columns={this.columns1} />
                    ):null}
                    {modelMet === "newClick" ?(
                      <Form >
                          <div><Button style={{textAlign:"center",marginLeft:"90%"}} onClick={this.addRow.bind(this)}>添加字段</Button></div>
                          <TableList scroll={{y:450}} pagination={false} style={{width:"99%",margin:"10px"}} dataSource={FieldList} columns={this.columns2()} />
                      </Form>
                    ):null}
                </div>
                <div style={{textAlign:"center",marginLeft:"85%"}} >
                      <Button disabled={modelMet === "onClick"} onClick={this.saveTable.bind(this)}>保存</Button>
                      <Button disabled={modelMet === "onClick"} onClick={this.saveTable.bind(this)} style={{marginLeft:"10"}}>取消</Button>
                </div>
          </Modal>
        </div>
    );
  }
}
const AcquisitionModelForm = Form.create()(AcquisitionNameModel);
export default withRouter(connect(({acquisition,metadataCommon,system,account }) => ({
 acquisition,metadataCommon,system,account 
}))(AcquisitionModelForm));
