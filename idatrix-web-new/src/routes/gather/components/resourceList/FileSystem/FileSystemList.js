import React from 'react';
import {Icon,Popconfirm,Tabs} from 'antd';
import {connect} from 'dva';
import {withRouter} from 'react-router';
import ListHeader from '../../common/ListHeader';
import ListTable from '../../common/ListTable';
import Empower from 'components/Empower';
import { fileType } from '../../../constant';
const TabPane = Tabs.TabPane;


const FileSystemList = ({location,resourcecontent,router,dispatch})=>{

  const {fileList,loading,total} = resourcecontent;
  const { query } = location;

  const columns = [
    {
      title: '文件名称',
      dataIndex: 'fileName',
      key: 'fileName',
      width:"34%"
    }, {
      title: '文件路径',
      dataIndex: 'path',
      key: 'path',
      width:"34%"
    },
    { title: '下载',
      dataIndex: 'download',
      key: 'download',
      render: (text,record) => {
        return (
          <Empower api="/cloud/downloadFile.do" >
            <a onClick={()=>{Download(record)}} className="AStyle" ><Icon  type="download" /></a>
          </Empower>
        )
      }
    },
    { title: '删除',
      dataIndex: 'delete',
      key: 'delete',
      render: (text,record) => {
        return (
          <Empower api="/cloud/deleteFile.do" >
            <Popconfirm title="确认要删除该文件吗?" onConfirm={()=>{handleDelete(record.path)}}  okText="是" cancelText="否">
                <a className="AStyle" ><Icon type="delete" /></a>
            </Popconfirm>
          </Empower>
        )
      }
    }
  ];


  const Download = (record)=>{
    let type = query.type?query.type:"txt";
    dispatch({
       type:"resourcecontent/downloadFile",
        payload:{
          type:type,
          path:record.path,
          fileName:record.fileName
        }
    })
  };

  const handleDelete = (name)=>{
    const { query } = location;
    dispatch({
      type:"resourcecontent/deleteFile",
      payload:{
        path:name,
        type:query.type
      }
    })
  };

  const callback = (e)=>{
    let type = "";
    if(e !== undefined){
      type = e;
    }
    router.push({...location,query:{type:type}});
  };


  return(
    <div id="ResourceContent"  >
      <ListHeader title="执行引擎" location={location} showBtn={true} />
      <section style={{backgroundColor: '#fff', padding:"10px", marginTop:"20px"}}>
        <div id="filesystem">
					<Tabs onChange={callback} type="card"  activeKey={fileType.has(query.type)?query.type:'txt'}>
						{
							[...fileType.entries()].map(([key,value])=>{
								return(
									<TabPane tab={value} style={{ display:"none" }} key={key}>{key}</TabPane>
								)
							})
						}
					</Tabs>
        </div>
      <	ListTable columns={columns} data={fileList} loading={loading} total={total} pageSize={8} location={location}/>
      </section>
    </div>
  )
};

export default withRouter(connect(({ resourcecontent }) => ({
  resourcecontent
}))(FileSystemList));


