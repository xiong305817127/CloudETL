/**
 * Created by Administrator on 2017/9/5.
 */
import {Table,Pagination} from 'antd';
import {connect} from 'dva';
import {withRouter} from 'react-router';
import Style from './ListTable.css';

const ListTable = ({columns,data,loading,total,router,location,pageSize})=>{

  const { query } = location;

  const onChange = (e)=>{
      query.page = e;
      router.push(location);
  };


  const showPagination = ()=>{
     if(total>0){
        return(
          <Pagination showQuickJumper  current={query.page?parseInt(query.page):1}  pageSize={pageSize?parseInt(pageSize):8} total={total} onChange={onChange} />
        )
     }else{
        return null;
     }
  };
  console.log(data);
  console.log(location);

    return(
      <div id={Style.ListTable}>
        <Table
          className="components-table-demo-nested"
          columns={columns}
          dataSource={data}
          loading={loading}
          pagination = {false}
        />
        {
            showPagination()
        }
      </div>
    )
};

export default withRouter(ListTable);
