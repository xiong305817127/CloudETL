/**
 * Created by Administrator on 2017/9/5.
 */
import React from 'react';
import {Icon,Popconfirm} from 'antd';
import {connect} from 'dva';
import { test_Server } from  '../../../../../services/gather';
import ListHeader from '../../common/ListHeader';
import ListTable from '../../common/ListTable';
import Empower from '../../../../../components/Empower';
import {Style} from '../ResourceContent.css';
import {withRouter} from 'react-router';

let Timer = null;

const ServerList = ({location,resourcecontent,dispatch,canEdit})=>{

  const {serverList,loading,total,needUpdate} = resourcecontent;

  const dataType = new Map([
    ["Online",{
      text:"正常",
      color:"greenColor"
    }],
    ["Error",{
      text:"异常",
      color:"redColor"
    }],
    ["Unknow",{
      text:"未知",
      color:"grayColor"
    }],
    ["Not Found",{
      text:"未找到",
      color:"redColor"
    }]
  ]);


  const columns = [
    {
      title: '服务器名称',
      dataIndex: 'name',
      key: 'name',
      width:"30%"
    }, {
      title: '是否主服务器',
      dataIndex: 'master',
      key: 'master',
      width:"30%"
    },{
      title: '状态',
      dataIndex: 'status',
      key: 'status',
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
            <Empower api={canEdit?"/cloud/deleteServer.do":""} >
              <Popconfirm title="确认要删除该行吗?" onConfirm={()=>{handleDelete(record.name)}}  okText="是" cancelText="否">
                <a className="AStyle" ><Icon type="delete" /></a>
              </Popconfirm>
            </Empower>
          </div>
        )
      }
    }
  ];

  if(needUpdate){
    if(serverList.length>0){
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
        let args = serverList;
        for(let index of args){
          try {
            test_Server({name:index.name}).then((res)=>{
              const { code,data } = res.data;
              if(code === "200"){
                args.map(value=>{
                  if(value.name === index.name){
                    value.status = data.message;
                  }
                  return value;
                });
                dispatch({
                  type:"resourcecontent/changeStatus",
                  payload:{
                    serverList:args
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

  const handleClick = (name)=>{
    dispatch({
      type:"resourcecontent/edit",
      payload:{
        model:"Server",
        name:name
      }
    })
  };

  const handleDelete = (name)=>{
    const { query } = location;
    dispatch({
      type:"resourcecontent/delete",
      payload:{
        model:"Server",
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

  return(
    <div id="ResourceContent"  >
      <ListHeader  location={location} onClick={()=>{handleNewModel()}} api={canEdit?"/cloud/saveServer.do":""} />
      <ListTable columns={columns} data={serverList} loading={loading} total={total} location={location}/>
    </div>
  )
};

export default withRouter(connect(({ resourcecontent }) => ({
  resourcecontent
}))(ServerList)) ;





