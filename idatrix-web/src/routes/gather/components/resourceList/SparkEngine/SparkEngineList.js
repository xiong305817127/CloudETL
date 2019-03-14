import React from 'react';
import {Icon,Popconfirm,Row} from 'antd';
import {connect} from 'dva';
import {withRouter} from 'react-router';
import ListHeader from '../../common/ListHeader';
import Empower from '../../../../../components/Empower';
import ListTable from '../../common/ListTable';
import {Style} from '../ResourceContent.css';


const SparkEngineList = ({location,resourcecontent,dispatch})=>{

  const {sparkList,loading,total} = resourcecontent;

  const columns = [
    {
      title: 'Spark引擎名称',
      dataIndex: 'name',
      key: 'name',
      width:"30%"
    }, {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width:"30%"
    },{ title: '操作',
      dataIndex: 'x123',
      key: 'x123',
      render: (text,record) => {
        return (
          <div>
            <a onClick={()=>{handleClick(record.name)}} className="AStyle" ><Icon  type="edit" /></a>
            &nbsp;&nbsp;&nbsp;&nbsp;
            <Empower api="/cloud/deleteSpark.do" >
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
        model:"SparkEngine",
        name:name
      }
    })
  };

  const handleDelete = (name)=>{
    const { query } = location;
    dispatch({
      type:"resourcecontent/delete",
      payload:{
        model:"SparkEngine",
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
      <ListHeader location={location} onClick={()=>{handleNewModel()}} disabled={true}  api="/cloud/saveSpark.do"/>
      <ListTable columns={columns} data={sparkList} loading={loading} total={total} location={location}/>
    </div>
  )
};

export default withRouter(connect(({ resourcecontent }) => ({
  resourcecontent
}))(SparkEngineList));





