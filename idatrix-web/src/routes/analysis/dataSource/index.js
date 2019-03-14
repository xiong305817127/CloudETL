import  React  from 'react';
import { Layout,Button,Table,Tabs,message,Icon,Popconfirm,Tooltip  } from  'antd';
import { connect } from 'dva';
import TableList from "components/TableList"
import Empower from 'components/Empower';
import styles from "./index.less";
import { databaseType } from "config/jsplumb.config.js";
import DatabaseModal from './components/databaseModal';

const ButtonGroup = Button.Group;


const index = ({ dispatch,datasource })=>{

	const { loading,data,total } = datasource;

	console.log(datasource,"模型");

  	//新建 注册前置机
  	const handleDatabaseModal = (action,record)=>{
  		let info = {};
  		if(record){
  			info = record;
  		}
  		dispatch({
  			type:"datasource/getFrontList",
  			payload:{
  				action,info
  			}
  		})
  	}

  	const columns = [
	  {
	    title: '数据库名称',
	    dataIndex: 'dbDatabasename',
	    key: 'dbDatabasename'
	  }, {
	    title: '创建者',
	    dataIndex: 'creator',
	    key: 'creator'
	  }, {
	    title: '数据库中文名',
	    dataIndex: 'dsName',
	  }, {
	    title: '所在的前置机名称',
	    dataIndex: 'serverName',
	    key: 'serverName',
	  },{
	    title: 'IP地址',
	    dataIndex: 'serverIp',
	    key: 'serverIp',
	  },{
	    title: '数据库类型',
	    dataIndex: 'dsType',
	    key: 'dsType',
	     render: (text) =>{
	     	for(let index of databaseType){
	     		if(index.value === text){
	     			return index.name;
	     		}
	     	}
	     }
	  },{
	    title: '状态',
	    dataIndex: 'status',
	    key: 'status',
        render: (text) => text=== 0 ? '未生效' : '' || text=== 1 ? '已删除' : '' || text=== 2 ? '已生效' : ''
	  },{
	    title: '新建/注册',
	    dataIndex: 'type',
	    key: 'type',
	     render: (text) => text=== "register" ? '注册' : '新建'
	  },{
	    title: '备注',
	    dataIndex: 'remark',
	    key: 'remark',
	    render: (text) => (<div className="word25" title={text}>{text}</div>)
	  },{ title: '操作',
	      dataIndex: 'x123',
	      key: 'x123',
	      render: (text,record) => {
	        return (
						<a style={{marginRight: 10}} >
							<Tooltip title="编辑" >
								<Icon 
								onClick={()=>{ (record.canEdited && record.type === "register") ? handleDatabaseModal("edit",record): null}} 
								type="edit" 
								className="op-icon" 
								style={{opacity: (record.canEdited && record.type === "register") ? 1: 0.2,}} />
							</Tooltip>
						</a>
	        )
	      }
	    }
	];

	/*
		<Empower api="/dataSourceInfoRegController/update" disabled={!record.canEdited }>
	    </Empower>
	    <Empower api="/dataSourceInfoRegController/updateStatus" disabled>
	    </Empower>
	 */


	const handleTest = ()=>{
		dispatch({
			type:"datasource/getList",
			payload:{
				name:"test"
			}
		})
	}

	return(
	  <Layout className={styles.dataSource}>
        <div >
          <div style={{width:"100%"}}  >
            <ButtonGroup style={{margin:"20px"}}>
              {/*新建9.11*/}
                <Tooltip placement="bottom" title="新建：在前置机新建一个数据库">
                  <Button type="primary" onClick={()=>{handleDatabaseModal("new")}} style={{marginRight:10}}>新建</Button>
                </Tooltip>
                <Tooltip placement="bottom" title="注册：前置机外部数据库登记">
                  <Button type="primary" onClick={()=>{handleDatabaseModal("register")}} style={{marginRight:10}}>注册</Button>
                </Tooltip>
            </ButtonGroup>
          </div>
          <TableList showIndex   columns={columns}
             loading={loading}
             pagination={{total}}
             dataSource={data}/>
        </div>
        <DatabaseModal />
      </Layout>
	)
}

export default connect(({ datasource })=>({
	datasource
}))(index);