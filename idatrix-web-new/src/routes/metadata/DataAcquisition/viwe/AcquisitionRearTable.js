/**
 * Created by Administrator on 2017/8/22.
 */
import React from 'react';
import {connect} from 'dva';
import { Link,withRouter } from 'react-router';
import { Input,Button,Tabs,Radio,Steps, message, Table,Icon,Popconfirm,Form,Select,Row, Col,TreeSelect,Checkbox } from 'antd';
import Modal from 'components/Modal';
import Style from '../Acquisition.css'
import {check_if_dsname_exists,edit_table_struct} from '../../../../services/metadata'
import TableList from "../../../../components/TableList"
import { getAcquisition } from "../../../../services/metadataCommon"
import { SJCJgetdbinfo,SJCJgetTableInfo,SJCJgetDbFieldInfo,SJCJinsertTableFields,SJCJisExists } from '../../../../services/AcquisitionCommon'
import { dateFormat, safeJsonParse } from 'utils/utils'
import { ACQUISITION_DB_TYPE_LIST } from 'constants'

const FormItem = Form.Item;
const Search = Input.Search;
const TabPane = Tabs.TabPane;
const Step = Steps.Step;
const RadioGroup = Radio.Group;
const Option = Select.Option;
const { TextArea } = Input;

const steps = [{
  title: '选择表',
  content: 'Second-content',
}, {
  title: '设置元数据基础信息',
  content: 'Last-content',
}];
let Timer;


class AcquisitionRearTable extends React.Component{
  state = {
      current: 0,
      visible: false,
      info:{},
      value: 1,
  };
        // 修改字段
     ValuesClick(keyOfCol,e,record){
      const { dispatch }=this.props;
      const args=this.props.acquisition;

      for(let index of args.selectRowLeft){
        if(index.key==record.key){
          index[keyOfCol]=e.target.value;
        }
      }
      dispatch({
        type:"acquisition/setMetaId",
           payload:{
             selectedRowKeys:args.selectRowLeft,
          }
      });
  }

     columns2 =()=>{
    const acquisition=this.props.acquisition.lengthss;
    const str = JSON.stringify(acquisition); 
      return[{
        title: '需引入元数据表的字段名',
        dataIndex: 'colName',
        key:'colName',
        width:'23%',
      },{
        title: '数据类型',
        dataIndex: 'dataType',
        key:'dataType',
        width:'20%',
      },{
        title: '长度',
        dataIndex: 'length',
        key:'length',
        width:'18%',
      },{
        title: '是否主键',
        dataIndex: 'isPk',
        key:'isPk',
        width:'18%',
        render: (text) => text===1 ? '是' : '否'
      },{
        title: '允许为空',
        dataIndex: 'isNull',
        key:'isNull',
        width:'18%',
        render: (text) => text===1 ? '是' : '否'
      }];  
   }


   columns1 = [{
      title: '元数据表名',
      dataIndex: 'metaNameEn',
      key:'metaNameEn',
    }];

   /*校验表名称*/
    SJCJisExistsListName = (rule, value, callback) => {
      const { dispatch }=this.props;
      const data1 = this.props.acquisition.selectRowLeft;
      const { setFields } = this.props.form;
      const formData = data1.map(row => ({ metaNameEn: row.table }));
      SJCJisExists(formData).then(({ data })=>{
        callback();
        if (Array.isArray(data.data)) {
          const fields = {};
          data.data.forEach((table) => {
            const tableName = Object.keys(table)[0];
            if (table[tableName]) {
              fields[`tables.${tableName}`] = {
                value: tableName,
                errors: ['已存在该表名'],
              };
            }
             dispatch({
                  type:"acquisition/setMetaId",
                     payload:{
                       options:table,
                       optionsKey:tableName,
                    }
                });
          });
          setFields(fields);
        }
      });
    };

    /*检测数据库名称*/
  handleGetName = (rule,value, callback) => {
    const { info,dsType } = this.props.acquisition;
    if(value && value !== info.dbDatabasename){
      if(Timer){
        clearTimeout(Timer);
        Timer = null;
      }
      Timer = setTimeout(()=>{
        let obj = {};
        obj.dbDatabasename = value;
        obj.dsType = 3;
        check_if_dsname_exists(obj).then(( res)=>{
          const { data } = res.data;
          if(data === true){
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

   handleRearChonel(record){
      const { dispatch }=this.props;
        const acquisition =this.props.acquisition;
        let obj={};
      
        obj.dsId = acquisition.dataBase[0].dsId;
        obj.sourceId = 3;

       getAcquisition(obj).then((res)=>{
             let args=[];
              let i = 0;
              let data1 = res.data.data.rows;
               if(res.data.code === "200"){
                  for (let index in data1){
                  	 
                   args.push(index);
                    dispatch({
	                  type:"acquisition/setMetaIdUpdata",
	                    payload:{
	                       modelMet:"MetadataTableName",
	                       selectedRowKeys:acquisition.selectedRowKeys,
                           selectedRows:acquisition.selectedRows,
                            metaid:data1[index].metaid,
                            type:this.state.type,
                            dsIdData:res.data.data.tables,
                            data1:data1,
                            dsIdData:acquisition.dataBase[0].dsId,  
	                    }
	                });
                
              }
               this.setState({
                  data1:data1,
                 })
          }
         })
  }

   componentDidMount() {
         this.handleRearChonel();
    }
    componentWillMount(){
       this.handleRearChonel();
    }
/*   
   componentWillUnmount() {
    this.handleRearChonel();
   }
*/
 next = (e) => {
   const { dispatch }=this.props;
   const acquisition =this.props.acquisition;
/*    e.preventDefault();*/
    this.props.form.validateFields({ force: false }, (err, values) => {
     
      if (!err) {
         if(this.state.current === 0){
          const { dispatch,acquisition }=this.props;
          const acquList = this.props.acquisition.options;
          const optionsKey = this.props.acquisition.optionsKey;
          
          const dataKey = this.props.acquisition.dataKey;
                   dispatch({
                      type:"acquisition/setMetaId",
                         payload:{
                           options:acquList,
                           optionsKey:optionsKey,
                           selectRowLeft:acquisition.selectRowLeft,
                           selectRowKeysLeft:acquisition.selectRowKeysLeft,
                           selectRowKeysRight:acquisition.selectRowKeysRight,
                           selectRowRight:acquisition.selectRowRight,
                           tableNames:acquisition.tablesNamelist,
                           tableNameX:acquisition.data1,
                           dataKey:dataKey
                        }
                    });
                    /*let deptHo = dataKey === true;*/
              if(acquisition.selectRowLeft.length === 0){
                   message.error("请选择表名");
                   return false
              }else if(acquisition.selectRowRight.length === 0){
                  message.error("请选择列名");
                   return false
              }else if (acquList[optionsKey]) {
                    return false
              }
         }
         const current = this.state.current + 1;
          this.setState({ current });
      }
    });
  }

  prev() {
     const { dispatch }=this.props;
     const info =this.props.acquisition;
     dispatch({
            type:"acquisition/setMetaId",
               payload:{
                  selectedRowKeys:info.selectedRowKeys,
                   selectedRows:info.selectedRows,
                    data1:info.data1,
                    data2:info.data2,
                    selectRowLeft:info.selectRowLeft,
                    selectRowKeysRight:info.selectRowKeysRight,
                    selectRowRight:info.selectRowRight,
                    tableNames:info.tablesNamelist,
                    tableNameX:info.data1,
              }
          });
    const current = this.state.current - 1;
    this.setState({ current });
  }

  StartAcquisition(){
    const { id,renterId } = this.props.account;
    const { dsTypes ,valueGrade} = this.props.acquisition;
  const { dispatch,acquisition }=this.props;
    this.props.form.validateFields((err, values) => {
      if (!err) {
        if(dsTypes.length === 0){
          message.error("请选择存储的数据库类型");
          return false
       }else if(valueGrade.length === 0){
          message.error("请输入组织外公开等级");
          return false
       }else{
             dispatch({
                type:"acquisition/setMetaId",
                   payload:{
                      data1:acquisition.data1,
                      data2:acquisition.data2,
                      selectedRowKeys:acquisition.selectedRowKeys,
                      selectedRows:acquisition.selectedRows,
                      selectRowLeft:acquisition.selectRowLeft,
                      selectRowKeysRight:acquisition.selectRowKeysRight,
                      selectRowRight:acquisition.selectRowRight,
                      tablesNamelist:acquisition.tablesNamelist,
                      tableNameX:acquisition.data1,
                      optionsKey:acquisition.optionsKey,
                  }
              });
                if(this.state.type === false){
                    
                    let metaList=[];
                     for(var key in acquisition.selectRowLeft){
                        metaList.push(acquisition.selectRowLeft[key].table);
                     }
                    let tableNames = acquisition.tablesNamelist;

                    let metadataPropertyList = acquisition.keyList;

                    let metaDataList = values;
                    let optionsKey = acquisition.tableNames;
                    values.metaNameEn = metaList;
                   /* values.renterId = renterId;*/
                    values.dsType = acquisition.dsTypes;
                      values.dsId = acquisition.dsIdData;
                    let metadataValues=acquisition.selectRowRight;
                    let data2List = acquisition.data2;
                    let type = acquisition.type === true?3:0;
                     let nameList=[];
                      let shallList=[];
                    for(var index in metadataValues){
                        nameList.push(metadataValues[index]);
                        shallList.push({
                          colName: nameList[index].name,
                          frontDataType: nameList[index].type,
                          IsPk:nameList[index].isPrimaryKey,
                          IsNull:nameList[index].nullable,
                          length:nameList[index].length,
                        })
                    }

                     let tablekey={metadataPropertyList};
                    /*let arr = {type,metaDataList,...tablekey};*/
                    let arr = {type,metaDataList,...tablekey};

                  SJCJinsertTableFields(arr).then((res)=>{
                       if(res.data.code === "200"){
                          message.success("成功");
                          dispatch({
                            type:"acquisition/setMetaId",
                               payload:{
                                 total:res.data.data.total,
                                 success:res.data.data.success,
                              }
                          });
                         let over = res.data.data.total- res.data.data.success;
                          this.setState({
                              total:res.data.data.total,
                              success:res.data.data.success,
                              over:over,
                          })
                          this.showModals();
                        }
                     })
             }else{
                 let metaList=[];
                   for(var key in acquisition.selectRowLeft){
                      metaList.push(acquisition.selectRowLeft[key].table);
                   }

                  let dsId = acquisition.dsIdData;
                  let tableNames = acquisition.tablesNamelist;
                  let metaDataList = values;
                  let optionsKey = acquisition.tableNames;
                  values.metaNameEn = metaList;
                  /*values.renterId = renterId;*/
                  values.dsType = acquisition.dsTypes;
                  let metadataValues=acquisition.selectRowRight;
                  let data2List = acquisition.data2;
                  let type = acquisition.type === true?3:0;
                   let nameList=[];
                    let shallList=[];
                  for(var index in metadataValues){
                      nameList.push(metadataValues[index]);
                      shallList.push({
                        colName: nameList[index].name,
                        frontDataType: nameList[index].type,
                        IsPk:nameList[index].isPrimaryKey,
                        IsNull:nameList[index].nullable,
                        length:nameList[index].length,
                      })
                  }
                 let metadataPropertyList = acquisition.keyList;

                  let tablekey={metadataPropertyList};
                  let arr = {dsId,type,metaDataList,...tablekey};

                SJCJinsertTableFields(arr).then((res)=>{
                     if(res.data.code === "200"){
                        message.success("成功");
                        dispatch({
                          type:"acquisition/setMetaId",
                             payload:{
                               total:res.data.data.total,
                               success:res.data.data.success,
                            }
                        });
                       let over = res.data.data.total- res.data.data.success;
                        this.setState({
                            total:res.data.data.total,
                            success:res.data.data.success,
                            over:over,
                        })
                        this.showModals();
                      }
                   })
                }



              }
            }
        });
    }


     headonRowClick=(record, index, event)=>{
         const { dispatch,acquisition } = this.props;
         const state =this.state;
          var List = [];
             var ListsName=[];
               for(var key in record.tables){ //第一层循环取到各个list
                  List = record.table;
                  ListsName=record.tables;
               }
          console.log(record,"record....");
          dispatch({
              type:"acquisition/setMetaId",
                 payload:{
                   data1:acquisition.data1,
                    hostname:acquisition.hostname,
                    port:acquisition.port,
                    username:acquisition.username,
                    password:acquisition.password,
                    databaseName:acquisition.databaseName,
                    pluginId:acquisition.pluginId,
                    selectRowLeft:acquisition.selectRowLeft,
                    tableNameX:acquisition.data1,
                    indexSelect:index,
                }
            });
                        dispatch({
                         type: 'metadataCommon/getStoreDatabase',
                             dstype:acquisition.dsType,
                          });
                      /*if(acquisition.dsType === 3 ){
                         this.state.dbDataTypeList.mysql;
                      }else if(acquisition.dsType === 4 ){
                          this.state.dbDataTypeList.hive;
                      }else{
                         this.state.dbDataTypeList.Hbase;
                      }*/
               let obj={};
               obj.hostname = acquisition.datalist[0].dbHostname;
               obj.port  = acquisition.datalist[0].dbPort;
               obj.username  = acquisition.datalist[0].dbUsername;
               obj.password = acquisition.datalist[0].dbPassword;
               obj.databaseName = acquisition.datalist[0].dbDatabasename;
               obj.pluginId = acquisition.datalist[0].dsType?"MySQL":"Oracle";
               obj.tableNames=record.metaNameEn;
               obj.dsType = acquisition.datalist[0].dsType;
               let zoo = this.props.acquisition === 0;
               console.log(obj,"obj......",acquisition.datalist[0]);
                SJCJgetDbFieldInfo(obj).then((res)=>{
                  if(res.data.code === "200"){
                    message.success("成功");
                    let data2 = res.data.data[0].fields;
                    let dataNameBest =res.data.data[0].table;
                    let args=[];
                    for(let index in data2){
                       if(data2[index].length <= 0){
                          data2[index].prontioan = data2[index].length;
                        }else if(data2[index].precision <= 0){
                          data2[index].prontioan = data2[index].length;
                        }else{
                          data2[index].prontioan = data2[index].length +','+ data2[index].precision;
                        }
                          dispatch({
                            type:"acquisition/setMetaId",
                               payload:{
                                 tablesNamelist:List,
                                  tableNames:ListsName,
                                  data2:data2,
                                  lengthss:data2[index].prontioan,
                                  dataNameBest:dataNameBest,
                                  selectRowKeysRight:zoo,
                              }
                          });
                        this.setState({
                          data2:data2
                        })
                  }
             }
          })
      }

    

    onChangeRadio = (e) => {
      const { dsType } = this.props.acquisition;
       const { dispatch }=this.props;
        dispatch({
            type:"acquisition/setMetaId",
              payload:{
               dsType:e.target.value,
              }
          });
    }

    onChangeGrade = (e) => {
      const { valueGrade } = this.props.acquisition;
       const { dispatch }=this.props;
     dispatch({
            type:"acquisition/setMetaId",
              payload:{
               valueGrade:e.target.value,
              }
          });
      }
   formItemLayout1 = {
        labelCol: { span: 0 },
        wrapperCol: { span: 20 },
      };

    showModals(){
        this.setState({
        visible: true,
      });
    }
      handleOk = (e) =>{
      this.setState({
        ModalText: 'The modal will be closed after two seconds',
        confirmLoading: true,
      });
      setTimeout(() => {
        this.setState({
          visible: false,
          confirmLoading: false,
        });
      }, 2000);
    }
    
     handleCancelAlert = (e) =>{
      const { dispatch }=this.props;
        const current = this.state.current = 0;
          this.setState({ current });
        this.setState({
          visible: false,
        });
         dispatch({type:"acquisition/closeModel"});
      }


       onChangeClickCool = (e) => {
          console.log('radio checked', e.target.value);
          this.setState({
            value: e.target.value,
          });
        }

         onChangeChecked(e) {
            console.log(`checked = ${e.target.checked}`);
          }

       componentWillMount() {
            const { dispatch } = this.props;
            dispatch({ type: 'metadataCommon/getSourceTable' });
            dispatch({ type: 'metadataCommon/getUsers' });
            dispatch({ type: 'metadataCommon/getDepartments' });
            dispatch({ type: 'metadataCommon/getStoreDatabase' });
            dispatch({ type: 'metadataCommon/getUserByRenterId' });
            dispatch({ type: 'metadataCommon/getAllResource' });
            dispatch({ type: 'metadataCommon/getHdfsTree' });
            
      }
     /* componentDidMount (){
      	 const { dispatch } = this.props;
      	dispatch({ type:"acquisition/closeModel"});
      }*/

   history(){
   	window.history.go(-1)
   }


  render() {
      const { sourceTableOptions, departmentsOptions, departmentsTree, storeDatabaseOptions, usersOptions, industryOptions, themeOptions, tagsOptions } = this.props.metadataCommon;
      const {dataSource,visible, confirmLoading,current,info} = this.state;
     const {dsType,modelMet,valueGrade,data1,data2,data,dsIdData,indexSelect,selectedRows,selectedRowKeys,selectRowKeysRight,selectRowLeft,selectRowRight,selectRowKeysLeft} = this.props.acquisition;
     const { getFieldDecorator } = this.props.form;
      const infos = this.props.acquisition;
      const { dispatch }=this.props;
     console.log(modelMet,"modelMetRearTable----------");
  const rowSelection1 = {
    selectedRowKeys:selectRowKeysLeft,
      onChange: (selectRowKeysLeft, selectRowLeft) => {
           const { dispatch,acquisition } = this.props;
             var List = [];
              for(var key in selectRowLeft){ 
                  List.push(selectRowLeft[key])
                  for(var student in List){ 
                    let row =[...List];
                  }
               }

            dispatch({
              type:"acquisition/setMetaId",
                 payload:{
                   data1:acquisition.data1,
                    hostname:acquisition.hostname,
                    port:acquisition.port,
                    username:acquisition.username,
                    password:acquisition.password,
                    databaseName:acquisition.databaseName,
                    pluginId:acquisition.pluginId,
                    selectRowLeft:List,
                    selectRowKeysLeft:selectRowKeysLeft,
                }
            });
      },
     /* getCheckboxProps: record => ({
       selectedRowKeys:selectRowKeysLeft,
      }),*/
    };

    const rowSelection2 = {
      selectedRowKeys:selectRowKeysRight,
      onChange: (selectRowKeysRight, selectRowRight) => {
        const { dispatch }=this.props;
        const { keyList }= this.props.acquisition;
      let tableLi = this.props.acquisition.tableNames;
       let as = this.props.acquisition.keyList;

       
        /*let RightList = {tableLi,{selectRowRight}};*/
  /* keyList[tableLi] = selectRowRight;*/
           let nameList=[];
          let shallList=[];
        for(var index in selectRowRight){
            nameList.push(selectRowRight[index]);
            shallList.push({
              colName: nameList[index].name,
              frontDataType: nameList[index].type,
              IsPk:nameList[index].isPrimaryKey,
              IsNull:nameList[index].nullable,
              length:nameList[index].length,
            })
        }

       let obj = {};

        obj[tableLi] = shallList;
        
   let tablekey={[tableLi]:shallList};
    let keyFile = {...tablekey}
        let FileName = [];
         this.props.acquisition.dataNameBest = selectRowRight;
        dispatch({
           type:"acquisition/setMetaId",
              payload:{
               selectRowKeysRight:selectRowKeysRight,
               selectRowRight:selectRowRight,
               keyList:{
                 ...keyList,
                 ...obj
               },
              }
          });
       },

    };

    let dept = Array.isArray(infos.dept) ? infos.dept : safeJsonParse(infos.dept) || this.props.account.deptId && [String(this.props.account.deptId)];
    dept = Array.isArray(dept) ? dept : dept && [String(dept)];
     if (!departmentsTree) dept = null;

     const formItemLayout3 = {
      labelCol: { span: 7 },
      wrapperCol: { span: 15 },
    };
    const formItemLayout4 = {
      labelCol: { span: 8 },
      wrapperCol: { span: 15 },
    };
    const formItemLayout5 = {
      labelCol: { span: 3 },
      wrapperCol: { span: 15 },
    };

    let renderShow;
    switch (this.state.current) {
      case 0:
        renderShow = (
          <div className={Style.center1}>
              <Table scroll={{y:450}}
               pagination={false} 
               onRow={this.headonRowClick.bind(this)} 
               rowClassName = {(record,index) => index === indexSelect ? "rowColor":''}
               rowSelection={rowSelection1}
               style={{color:'#fff'}}
               className={Style.TabelList} dataSource={data1} columns={this.columns1} />

              <TableList scroll={{y:450}} pagination={false} rowSelection={rowSelection2} onRow={(record)=>{console.log(record)}}  className={Style.TabelList1} dataSource={data2} columns={this.columns2()} />
          </div>
        )
        break;
         case 1:
         renderShow = (
           <div className={Style.center2}>
               <Row>
                   <Col span={12}>
                      <FormItem label="数据来源组织: " {...formItemLayout3}>
                          {getFieldDecorator('dept', {
                            initialValue: dept,
                          })(
                             <TreeSelect
                                placeholder="选择组织"
                                treeData={departmentsTree}
                                treeDefaultExpandAll
                                dropdownStyle={{height: 300}}
                                allowClear
                              />
                          )}
                       </FormItem>
                  </Col>
                 <Col span={12}>
                 {/*? String(info.dsId):'4'*/}
                    <FormItem  label="存储的数据库" {...formItemLayout3}>
                      {getFieldDecorator('dsId', {
                        initialValue: infos.dsId ? String(infos.dsId) : '',
                        rules:[{required:true,message:"请选择存储的数据库"}]
                      })(
                        <Select placeholder="选择数据库">
                          {storeDatabaseOptions.map(item => {
                            return <Option key={item.value} value={item.value}>{item.label}</Option>
                          })}
                        </Select>
                      )}
                    </FormItem>
                 </Col>
               </Row>
               <Row>
                  <Col span={12}>
                   <FormItem label="组织外公开等级" {...formItemLayout4}>
                       {getFieldDecorator('publicStats', {
                          initialValue: infos.publicStats || '1',
                      })(
                        <RadioGroup onChange={this.onChangeGrade} value={valueGrade}>
                          <Radio value="1">授权公开</Radio>
                          <Radio value="2">不公开</Radio>
                        </RadioGroup>
                      )}
                    </FormItem>
                 </Col>
                 <Col span={12}>
                    <FormItem label="表拥有者： " {...formItemLayout3}>
                        {getFieldDecorator('owner', {
                          initialValue: infos.owner,
                          rules: [
                            { required: true, message: "请选择表拥有者" },
                          ]
                        })(
                          <Select placeholder="选择表拥有者">
                            {usersOptions.map(item => {
                              return <Option key={item.value} value={item.label}>{item.label}</Option>
                            })}
                          </Select>
                        )}
                      </FormItem>
                 </Col>
               </Row>

            
              <Row>
                 <FormItem label="标签：" {...formItemLayout5} >
                    {getFieldDecorator('tag',{
                      initialValue: infos.tag,
                      rules:[{required:true,message:"请选择标签"}]
                    })(
                      <Select  placeholder="选择标签">
                        {tagsOptions.map(item => {
                          return <Option key={item.value} value={item.value}>{item.label}</Option>
                        })}
                      </Select>
                    )}
                  </FormItem>
              </Row>
              <Row>
              <FormItem  label="备注：" {...formItemLayout5} >
                {getFieldDecorator('remark',{
                  initialValue: infos.remark,
                })(
                  <TextArea maxLength="200" autosize={{ minRows: 3, maxRows: 5 }} />
                )}
              </FormItem>

              </Row>
          </div>
        )
        break;
      default:
        break;
    }
    return (
      <div>
          <div style={{width:'39%',marginLeft:'24%'}}>
	        <Steps current={current}>
	         {steps.map(item => <Step key={item.title} title={item.title} style={{marginTop:"20px"}} />)}
	        </Steps>
	      </div>
           <div className="stepsContent">
         
                    <Form>
                        {
                          renderShow
                        }
                      </Form>
                       
           </div>
           <div className={Style.stepsAction}>
          {
            this.state.current < steps.length - 1
            &&
               <div>
                   <Button type="primary" style={{ marginLeft: 8 }} onClick={() => this.next()}>下一步</Button>
                   <Button type="primary" style={{ marginLeft: 8 }} onClick={()=> this.history()}>取消</Button>
               </div>
          }
          {
            this.state.current === steps.length - 1
            &&
                <div>
                     <Button type="primary" style={{ marginLeft: 8 }} onClick={() => this.StartAcquisition()}>开始采集</Button>
                     <Button type="primary" style={{ marginLeft: 8 }} onClick={()=> this.history()}>取消</Button>
                </div>
          }
          {
            this.state.current > 0
            &&
               <div>
                    <Button style={{margin:'-12% 0% 0% -36%',float:'left' }} onClick={() => this.prev()}>
	                  上一步
	                </Button>
               </div>
          }
        </div>

        <Modal title="采集结果"
            visible={visible}
            confirmLoading={confirmLoading}
            closable={true}
            onCancel={this.handleCancelAlert}
            footer={null}>
                  <Row>
                      <Col span={8}>
                         <FormItem label="总表数" >
                         {getFieldDecorator('total',{
                            initialValue: info.total,
                          })(
                           <span className="ant-form-text">{this.state.total}</span>
                          )}
                         </FormItem>
                      </Col>
                      <Col span={8}>
                         <FormItem label="采集成功表数">
                           {getFieldDecorator('success',{
                              initialValue: info.success,
                            })(
                              <span className="ant-form-text">{this.state.success}</span>
                            )}
                        </FormItem>
                      </Col>
                      <Col span={8}>
                        <FormItem  label="采集失败表数" >
                         {getFieldDecorator('over',{
                              initialValue: info.over,
                            })(
                             <span className="ant-form-text">{this.state.over}</span>
                            )}

                        </FormItem>
                      </Col>
                  </Row>
                  <div>提示：可以在【外部数据源采集】-【生成元数据定义表】中继续编辑且生成实体表！</div>
                     <div style={{height: 30,textAlign:'center',marginTop:'5%'}}>
                          <Button type="primary" onClick={() => this.handleOk()}>
                              <Link to="/MetadataDefine/drafts">去编辑</Link>
                          </Button>
                     </div>
      </Modal>

        </div>
    );
  }
}
const AcquisitionRearForm = Form.create()(AcquisitionRearTable);
export default withRouter(connect(({acquisition,metadataCommon,system,account }) => ({
 acquisition,metadataCommon,system,account 
}))(AcquisitionRearForm));
