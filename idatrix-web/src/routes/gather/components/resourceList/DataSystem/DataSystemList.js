import React from 'react';
import {Icon,Popconfirm,Row,Button} from 'antd';
import {connect} from 'dva';
import { test_DbConnection } from  '../../../../../services/gather';
import ListHeader from '../../common/ListHeader';
import ListTable from '../../common/ListTable';
import Empower from '../../../../../components/Empower';
import {Style} from '../ResourceContent.css';
import {withRouter} from 'react-router';

let Timer = null;

const DataSystemList = ({location,resourcecontent,canEdit,dispatch})=>{

  const {dataList,loading,total,needUpdate} = resourcecontent;


  const dataType = new Map([
    ["Normal",{
      text:"正常",
      color:"greenColor"
    }],
    ["Error",{
       text:"异常",
      color:"redColor"
    }],
    ["Parameter Error",{
      text:"参数错误",
      color:"redColor"
    }],
    ["Unknow",{
      text:"未知",
      color:"grayColor"
    }]
  ]);

  const columns = [
    {
      title: '数据库名称',
      dataIndex: 'name',
      key: 'name',
      width:"30%"
    }, {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width:"30%"
    },{
      title: '状态',
      dataIndex: 'testStatus',
      key: 'testStatus',
      width:"20%",
      render: (text,record) => {
        if(text){
            return(
              <div className={dataType.get(text).color}>{dataType.get(text).text}</div>
            )
        }else{
            return(
              <Icon type="loading" />
            )
        }
      }
    },{ title: '操作',
      dataIndex: 'x123',
      key: 'x123',
      render: (text,record) => {
        return (
          <div>
            <a onClick={()=>{handleClick(record.name)}} className="AStyle" ><Icon  type="edit" /></a>
            &nbsp;&nbsp;&nbsp;&nbsp;
            <Empower api={canEdit?"/db/deleteDbConnection.do":""} >
              <Popconfirm title="确认要删除该行吗?" onConfirm={()=>{handleDelete(record.name)}} disabled okText="是" cancelText="否">
                  <a className="AStyle" ><Icon type="delete" /></a>
              </Popconfirm>
            </Empower>
          </div>
        )
      }
    }
  ];


  const handleClick = (name)=>{
    dispatch({
      type:"resourcecontent/edit",
      payload:{
        model:"DataSystem",
        name:name
      }
    })
  };

  const handleDelete = (name)=>{
    const { query } = location;
    dispatch({
      type:"resourcecontent/delete",
      payload:{
        model:"DataSystem",
        name:name,
        keyword:query.keyword
      }
    })
  };

  const handleNewModel = ()=>{
    dispatch({
      type:"resourcecontent/changeStatus",
      payload:{
        view:"model",
        config:{}
      }
    })
  };

  if(needUpdate){
      if(dataList.length>0){
          if(Timer){
                clearTimeout(Timer);
          }
          Timer = setTimeout(()=>{
              dispatch({
                  type:"resourcecontent/changeStatus",
                  payload:{
                    needUpdate:false
                  }
              });
              let args = dataList;
              for(let index of args){
                try {
                  test_DbConnection({name:index.name}).then((res)=>{
                    const { code,data } = res.data;
                    if(code === "200"){
                        args.map(value=>{
                            if(value.name === index.name){
                                value.testStatus = data.message;
                            }
                            return value;
                        });
                        dispatch({
                          type:"resourcecontent/changeStatus",
                          payload:{
                            dataList:args
                          }
                        });
                    }
                  });
                }catch (e){
                  console.log(e);
                }
              }
          },200);
      }
  }

  return(
    <div id="ResourceContent"  >
      <ListHeader title="数据系统" location={location} onClick={()=>{handleNewModel()}} api={canEdit?"/db/saveDbConnection.do":""}  />
      <ListTable columns={columns} data={dataList} loading={loading} total={total} location={location}/>
    </div>
  )
};

export default withRouter(connect(({ resourcecontent }) => ({
  resourcecontent
}))(DataSystemList));





