import React from "react";
import { connect } from "dva";
import TableList from '../../../components/TableList';
import Search from '../../../components/Search';
import Empower from '../../../components/Empower';
import moment from "moment";

const index =({ logoManage })=>{

	console.log(logoManage);
	const { dataSource,total,loading } = logoManage;

	console.log(logoManage);

	const columns = [
	    {
	        title: '服务名',
	        dataIndex: 'server',
	        className:"td-center",
	        width:"15%"
	    }, {
	        title: '请求地址',
	        dataIndex: 'resource',
	        className:"td-center",
	        width:"25%"
	    },
	    {
	        title: '请求方式',
	        dataIndex: 'methodType',
	        className:"td-center",
	        width:50,
	    },
	    {
	        title: '客户端地址',
	        dataIndex: 'clientIp',
	        className:"td-center",
	        width:50,
	    },
	    {
	        title: '用户名',
	        dataIndex: 'userName',
	        className:"td-center",
	        width:50,
	    },
	    {
	        title: '访问时间',
	        dataIndex: 'visitTime',
	        className:"td-center",
	        width:"15%",
	        render:(text)=>{
	        	return moment(text).format('YYYY-MM-DD HH:mm:ss');
	        }
	    },
	    {
	        title: '结果',
	        dataIndex: 'result',
	        className:"td-center",
	        width:50,
	        render:(text)=>text==="success"?"成功":"失败"
	    },
	]

	return(
		<TableList
		  rowKey="id"
          columns={columns}
          dataSource={dataSource}
          loading={loading}
          showIndex//序号
          pagination={{
            total,//从后台获取totalCount赋值total，以及组件自动计算分页
          }}
        />
	)
}

export default connect(({
	logoManage
})=>({ logoManage }))(index);