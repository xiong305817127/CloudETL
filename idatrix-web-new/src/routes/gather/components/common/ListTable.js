/**
 * Created by Administrator on 2017/9/5.
 */
import { Table, Pagination } from "antd";
import { withRouter } from "react-router";
import Style from "./ListTable.css";

const ListTable = ({
  columns,
  data,
  loading,
  total,
  router,
  location,
  pageSize,
  rowKey,
  expandedRowRender
}) => {
  const { query } = location;

  const onChange = e => {
    query.page = e;
    router.push(location);
  };

  return (
    <div id={Style.ListTable}>
      <Table
        rowKey={rowKey}
        className="components-table-demo-nested"
        columns={columns}
        dataSource={data}
        loading={loading}
        expandedRowRender={expandedRowRender}
        pagination={false}
      />
      {total > 0 ? (
        <Pagination
          showQuickJumper
          current={query.page ? parseInt(query.page) : 1}
          pageSize={pageSize ? parseInt(pageSize) : 8}
          total={total}
          onChange={onChange}
        />
      ) : null}
    </div>
  );
};

export default withRouter(ListTable);
