import React from 'react';
import {Icon,Popconfirm} from 'antd';
import {connect} from 'dva';
import ListHeader from '../../common/ListHeader';
import ListTable from '../../common/ListTable';
import Empower from '../../../../../components/Empower';
import {Style} from '../ResourceContent.css';
import {withRouter} from 'react-router';


const ClusterList = ({location,resourcecontent,dispatch})=>{

  const {clusterList,loading,total} = resourcecontent;

  const columns = [
    {
      title: '服务器集群名称',
      dataIndex: 'name',
      key: 'name',
      width:"34%"
    }, {
      title: '是否动态',
      dataIndex:'dynamic',
      key:'dynamic',
      width:"34%"
    },{ title: '操作',
      dataIndex: 'x123',
      key: 'x123',
      render: (text,record) => {
        return (
          <div>
              <a onClick={()=>{handleClick(record.name)}} className="AStyle" ><Icon  type="edit" /></a>
             &nbsp;&nbsp;&nbsp;&nbsp;
            <Empower api="/cloud/deleteCluster.do" >
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
            model:"Cluster",
            name:name
        }
    })
  };

  const handleDelete = (name)=>{
    const { query } = location;
    dispatch({
      type:"resourcecontent/delete",
      payload:{
        model:"Cluster",
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
      <ListHeader  location={location} onClick={()=>{handleNewModel()}}  api="/cloud/saveCluster.do"/>
      <ListTable columns={columns} data={clusterList} loading={loading} total={total} location={location}/>
    </div>
  )
};

export default withRouter(connect(({ resourcecontent }) => ({
  resourcecontent
}))(ClusterList));





