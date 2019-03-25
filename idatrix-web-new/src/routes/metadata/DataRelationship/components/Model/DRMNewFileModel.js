/**
 * Created by Administrator on 2017/8/26.
 */
import { Button,Input ,Radio,Form,Table,message ,Icon ,Row,Col,Tree } from 'antd';
import { connect } from 'dva'
import {deleteFiledById } from  '../../../../../services/metadata';
import Modal from 'components/Modal';
import TreeList from './../../NewIndex.js';

const RadioGroup = Radio.Group;
const FormItem = Form.Item;
const Search = Input.Search;
const TreeNode = Tree.TreeNode;

const DRMNewFileModel1 = ({drmnewfilemodel,form,dispatch,account,drmselecttable})=>{

    const {value,data1,selectKey,selectedRowKeys, data,statusList,dataLeft,dataRight,
          loading,loadingLeft,loadingRight,visible,radioType,fileTable,leftName,rightName,
          selectLeft,selectRight,updataTable,rsType,actionType,relationId,selectedRowKeysRight,selectedRowKeysLeft } = drmnewfilemodel;
   const {dataTable,searchIndex,text} = drmselecttable;


    const { getFieldDecorator } = form;
    const { id,renterId } = account;
    console.log(drmselecttable,drmnewfilemodel,"======");
    /*data.splice(0,data.length);*/
    const columnsLeft = [
      {
        title: '字段名称',
        dataIndex: 'colName',
        key: 'colName',
        width:'16%'
      },{
        title: '字段描述',
        dataIndex: 'description',
        key: 'description',
        width:'16%'
      },  {
        title: '数据类型',
        dataIndex: 'dataType',
        key: 'dataType',
        width:'12%'
      }, {
        title: '长度',
        dataIndex: 'length',
        key: 'length',
        width:'10%'
      }, {
        title: '是否主键',
        dataIndex: 'isPk',
        key: 'isPk',
        width:'12%',
        render: (text,record) => {
          //这里的text和record没区别
          return <span>{text==1?'是':'否'}</span>
        }
      }, {
        title: '是否维度',
        dataIndex: 'isDemension',
        key: 'isDemension',
        width:'12%',
        render: (text,record) => {
          //这里的text和record没区别
          return <span>{text===1?'是':'否'}</span>
        }
      },{ title: '是否度量',
        dataIndex: 'isMetric',
        key: 'isMetric',
        width:'12%',
        render: (text,record) => {
          //这里的text和record没区别
          return <span>{text===1?'是':'否'}</span>
        }
      }
    ];

  const columns = [
    {
      title: '表1字段名称',
      dataIndex: 'fcolName',
      key: 'fcolName',
      width:'18%'
    },{
      title: '表2字段名称',
      dataIndex: 'scolName',
      key: 'scolName',
      width:'18%'
    },{
      title: '关联关系描述',
      dataIndex: 'rsdescription',
      key: 'rsdescription',
      width:'18%',
      render: (text,record) => {
        return <Input onChange={(e)=>{
              record.rsdescription = e.target.value;
        }} defaultValue={text} />
      },
    },{ title: '操作',
      dataIndex: 'delete',
      key: 'delete',
      width:'16%',
      render: (text,record) => {
        //这里的text和record没区别
        return <a><Icon onClick={()=>{handleDelete(record)}} type="delete" /></a>
      }
    }];

    const columnsFile = [
      {
        title: '文件名',
        dataIndex: 'dirName',
        key: 'dirName',
        width: '100%'
      }
    ];

 const columnsTables = [
      {
        title: '表中文名称',
        dataIndex: 'metaNameCn',
        key: 'metaNameCn',
        width: '50%'
      },{
        title: '表英文名称',
        dataIndex: 'metaNameEn',
        key: 'metaNameEn',
        width: '50%'
      }
    ];


  const handleDelete = (record) =>{
    data.forEach(item => {
      if( item.key == record.key){
        item.status = 2;
      }
    });
    dispatch({
      type: 'drmnewfilemodel/changeModel',
      payload: { data },
    });
    };

  //确定
  const handleOk = ()=>{
    drmnewfilemodel.selectedRowKeysLeft = null;
    drmnewfilemodel.selectedRowKeysRight=null;
    drmnewfilemodel.selectKey=[];
    drmnewfilemodel.selectedRowKeys=[];
   
    try{
      let obj = {
        "tableName": leftName,
         rsType: rsType,
        "childTable": rightName,
        };
      let data1 = data[0];
      let dataList={
          createTime:data1.createTime,
          creator:data1.creator,
          fcolName:data1.fcolName,
          fmetaid:data1.fmetaid,
          fprosid:data1.fprosid,
          id:data1.id,
          key:data1.key,
          relationId:data1.relationId,
          rsdescription:data1.rsdescription,
          scolName:data1.scolName,
          smetaid:data1.smetaid,
          sprosid:data1.sprosid,
          status:2
          }
      if(fileTable){
          if(selectLeft.key && selectRight.key){
              obj = {
                "dataRelation": {
                  "metaid": selectLeft.metaid,
                  "childId": selectRight.key,
                  "tableType": "2",
                  "tableNameEn":colName,
                  "childTableEn":description,
                },
                dataFieldRelation:[],
                relationId,
              }
          }else{
             throw e;
          }
      }else{
        let args = {};
        if(data && data.length>0){
          console.log(data,"data:args");
          args = {
            ...obj,
            "metaid": data[0].fmetaid,
            "childId": data[0].smetaid,
            "tableType": "1",
          };
        }
         obj = {
            dataRelation:args,     
            dataFieldRelation:data,relationId,id,
            relationId
         };
      };
      
      if(actionType === "edit"){
        console.log(dispatch,"dispatch.savebiuanjiFields");
                  dispatch({
                    type:"drmnewfilemodel/savebiuanjiFields",
                    status:"3" ,
                    payload:{
                      obj:obj,
                      status:3,
                      Request:updataTable
                    }
                  })     
      }else{
        dispatch({
        type:"drmnewfilemodel/saveFields",
        payload:{
          obj:obj,
           status:"1" ,
          Request:updataTable
        }
      })
    }
     dispatch({
          type:"drmnewfilemodel/hideModel"
      })
      dispatch({
          type:"drmselecttable/hideModel"
      })

	 dispatch({
        type:"drmselecttable/setMetaId",
        payload:{
           metaid:""
        }
      });
    }catch (e){
        message.error("请先选择表字段！");
    }
  };
  //取消
  const handleCancel = ()=>{
  	dispatch({
        type:"drmselecttable/setMetaId",
        payload:{
           metaid:"",
           data:"",
        }
      });
      dispatch({
          type:"drmnewfilemodel/hideModel"
      })

  };

  const handelChange = (e)=>{
    dispatch({
      type:"drmnewfilemodel/changeModel",
      payload:{
        rsType:e.target.value
      }
    })
  };

  const formItemLayout = {
    labelCol: { span: 3 },
    wrapperCol: { span: 14 }
  };

  const formItemLayout1 = {
    labelCol: { span: 8 },
    wrapperCol: { span: 14 }
  };

  //点击确定
  const showTable = (model)=>{
    dispatch({
      type:"drmselecttable/showSelectTable",
      payload:{
        obj:{
          "metaType" :"",
          "dept":"",
          "metaNameCn":"",
          "renterId":renterId,
          "sourceId":2,
          "id":id,
          "model":model,
          "status":1
        },
        paper:{
          pageSize:10,
          current:1
        }
      }
    });
    form.resetFields();
  };

  const rowSelectionLeft = {
    type:"radio",
    selectedRowKeysLeft,
    onChange: (selectedRowKeys, selectedRows) => {
      console.log(selectedRows,"selectedRows",selectedRowKeys,"rowSelectionLeft");
      dispatch({
        type:"drmnewfilemodel/changeModel",
        payload:{
           selectLeft:selectedRows[0],
           selectedRowKeysLeft:selectedRowKeys,
        }
      });
    },
  };

  const rowSelectionRight = {
    type:"radio",
    selectedRowKeysRight,
    onChange: (selectedRowKeys, selectedRows) => {
      console.log(selectedRows,",selectedRows",selectedRowKeys,"selectedRowKeys=rowSelectionRight");
      dispatch({
        type:"drmnewfilemodel/changeModel",
        payload:{
           selectRight:selectedRows[0],
           selectedRowKeysRight:selectedRowKeys,
        }
      });
    },
  };

  const buildNewTable = ()=>{
      if(selectLeft.metaid && selectRight.metaid){
          let args = data;
          let key = selectLeft.colName+selectLeft.metaid+selectRight.colName+selectRight.metaid;
          if(data){
              for(let index of data){
                  if(index.key === key){
                    message.warning("不能重复关联");
                    return false;
                  }
              };
              args.push({
                fmetaid:selectLeft.metaid,
                fprosid:selectLeft.id,
                fcolName:selectLeft.colName,
                fcolCode:selectLeft.colCode,
                smetaid:selectRight.metaid,
                sprosid:selectRight.id,
                scolName:selectRight.colName,
                scolCode:selectRight.colCode,
                rsdescription:'',
                key: key,
                status:actionType === "edit"?3:1 
              });
              dispatch({
                type:"drmnewfilemodel/changeModel",
                payload:{
                  data:args
                }
              });
          }
      }else{
         message.error("请先选择表字段！");
      }
  };

    return(
      <Modal
        title="手工建立数据关系"
        visible={visible}
        width={"90%"}
        onOk={handleOk}
        onCancel={handleCancel} >
        <Form layout="inline" >
         <div style={{height:'4rem',textAlign:'center'}}>1 请从左侧点击选择拖放两个数据表到右侧的数据表1和数据表2的字段列表处；2 通过拖动数据表1的字段到数据表2的相关联字段来建立关联</div>
         <div style={{width:'97%',height:'65%'}}>
           <div span={24} style={{width:'75%',margin:'0% -18% 0% 1%'}}>
          {actionType === "new" ?(<TreeList />):(<div style={{margin:'0% auto'}}>编辑无法更改表名</div>)}  
               {/*  <Table
                    onRow={(record)=>{console.log(record,"+++++++++++++++++++")}}
                    rowSelection={rowSelectionTable}
                    loading={loadingLeft}
                    columns={columnsTables}
                    dataSource={dataTable}
                    scroll={{y: 400 }}
                    pagination={false}
                    className="th-nowrap "
                  />
               <FormItem label="关系类型：" required >
                <RadioGroup onChange={handelChange} defaultValue={rsType}>
                  <Radio value="1">引用关系</Radio>
                  <Radio value="2">生成关系</Radio>
                </RadioGroup>
              </FormItem>
             */}
            </div>
         </div>

          <Row style={{marginBottom:'0%',width:'80%',margin:'0% 0% 1% 17%'}}>
            <Col span={12} style={{width:'50%',position:'relative',left:'9%'}}>
              <FormItem label="数据表1" required {...formItemLayout} style={{width:"100%"}} >
                <Col span={22}>
                  {getFieldDecorator('tableName', {
                    initialValue: leftName
                  })(
                    <Input disabled={true}  placeholder="请输入数据表"/>
                  )}
                </Col>
                {/* <Col span={2}>
                  <Button disabled={actionType === "edit"}  onClick={()=>{showTable('left')}}>选择</Button>
                </Col>*/}
              </FormItem>
            </Col>
            <Col span={12}  style={{position:'relative',width:'50%', left:'5%'}}>
              <FormItem
                label="数据表2"
                required
                {...formItemLayout1}
                style={{width:"100%"}}
              >
                <Col span={20}>
                  {getFieldDecorator('childTable', {
                    initialValue: rightName
                  })(
                    <Input disabled={true} placeholder="请输入数据表" />
                  )}
                </Col>
               {/* <Col span={4}><Button disabled={actionType === "edit"}  onClick={()=>{showTable('right')}}>选择</Button></Col>*/}
              </FormItem>
            </Col>
            <Col span={12} style={{ overflow:"scroll"}}>
              <Table
                id="div1" 
                ondrop="drop(event)" 
                ondragover="allowDrop(event)"
                onRow={(record)=>{console.log(record)}}
                rowSelection={rowSelectionLeft}
                loading={loadingLeft}
                columns={columnsLeft}
                dataSource={dataLeft}
                pagination={false}
                className="th-nowrap "
                locale={
                  {
                    emptyText: ()=>{
                      return (
                        <div style={{minHeight:"360px",backgroundColor:"#f0f0f0", position: "relative"}}>
                          <span style={{position:"absolute",top: "50%", left:"50%",transform: "translate(-50%,-50%)"}}>
                          请拖动左侧表格内容至此，然后点击下方关联按钮
                          </span>
                        </div>
                      )
                    }
                  }
                }
              />
            </Col>
            <Col span={12} style={{overflow:"scroll"}}>
              <Table
                onRow={(record)=>{console.log(record)}}
                rowSelection={rowSelectionRight}
                loading={loadingRight}
                columns={!fileTable?columnsLeft:columnsFile}
                dataSource={dataRight}
                pagination={false}
                className="th-nowrap"
                locale={
                  {
                    emptyText: ()=>{
                      return (
                        <div style={{minHeight:"360px",backgroundColor:"#f0f0f0", position: "relative"}}>
                          <span style={{position:"absolute",top: "50%", left:"50%",transform: "translate(-50%,-50%)"}}>
                          请拖动左侧表格内容至此，然后点击下方关联按钮
                          </span>
                        </div>
                      )
                    }
                  }
                }
              />
            </Col>
            <Col span={24} style={{position:'relative'}}>
              {
                !fileTable?
                <div style={{marginTop:'3%',position:'relative',textAlign:"center"}}>
                  <Button type="primary" onClick={buildNewTable}>建立关联</Button>
                </div>:null
              }
            </Col>
            <Col span={24} style={{zIndex:'999'}}>
              {
                !fileTable?<div>
                  <Table
                    onRow={(record)=>{console.log(record)}}
                    loading={loading}
                    columns={columns}
                    dataSource={data.filter(index => index.status != 2)}
                    scroll={{y: 300 }}
                    pagination={false}
                    className="th-nowrap "
                  />
                </div>:null
              }
            </Col>
          </Row>
        </Form>
      </Modal>
    )
};

const DRMNewFileModel = Form.create()(DRMNewFileModel1);
export default connect(({ drmnewfilemodel,account,drmselecttable }) => ({
  drmnewfilemodel,account,drmselecttable
}))(DRMNewFileModel);
