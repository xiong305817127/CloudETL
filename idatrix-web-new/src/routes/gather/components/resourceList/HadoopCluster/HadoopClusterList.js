import React from 'react';
import {Icon,Popconfirm} from 'antd';
import {connect} from 'dva';
import {withRouter} from 'react-router';
import Empower from '../../../../../components/Empower';
import ListHeader from '../../common/ListHeader';
import ListTable from '../../common/ListTable';
import {Style} from '../ResourceContent.css';


const HadoopClusterList = ({location,resourcecontent,dispatch,canEdit})=>{

  const {hadoopList,loading,total} = resourcecontent;

  const columns = [
    {
      title: 'Hadoop集群名称',
      dataIndex: 'name',
      key: 'name',
      width:"34%"
    }, {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width:"34%"
    },{ title: '操作',
      dataIndex: 'x123',
      key: 'x123',
      render: (text,record) => {
        return (
          <div>
            <a onClick={()=>{handleClick(record.name)}} className="AStyle" ><Icon  type="edit" /></a>
             &nbsp;&nbsp;&nbsp;&nbsp;
            <Empower api={canEdit?"/cloud/deleteHadoop.do":""} >
              <Popconfirm title="确认要删除该行吗?"  onConfirm={()=>{handleDelete(record.name)}}  okText="是" cancelText="否">
                <a  className="AStyle" ><Icon  type="delete" /></a>
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
        model:"HadoopCluster",
        name:name
      }
    })
  };

  const handleDelete = (name)=>{
    const { query } = location;
    dispatch({
      type:"resourcecontent/delete",
      payload:{
        model:"HadoopCluster",
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
      <ListHeader title="数据系统" location={location} onClick={()=>{handleNewModel()}}  api={canEdit?"/cloud/saveHadoop.do":""}/>
      <ListTable columns={columns} data={hadoopList} loading={loading} total={total} location={location}/>
    </div>
  )
};

export default withRouter(connect(({ resourcecontent }) => ({
    resourcecontent
  }))(HadoopClusterList));





