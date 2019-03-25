import React from "react";
import { Icon, Popconfirm, Table, Pagination, Tooltip,message } from "antd";
import { connect } from "dva";
import ListHeader from "../../common/ListHeader";
import Empower from "../../../../../components/Empower";
import { withRouter } from "react-router";
import Style from "../../common/ListTable.css";
import "../ResourceContent.css"


const DataSystemList = ({ location, resourcecontent, canEdit, dispatch }) => {
  const { dataList, loading, total, schemaList } = resourcecontent;
  const { query } = location;

  const onChange = e => {
    query.page = e;
    router.push(location);
  };

  const columns = [
    {
      title: "数据库名称",
      dataIndex: "name",
      key: "name",
      width: "50%"
    },
    {
      title: "类型",
      dataIndex: "type",
      key: "type",
      width: "30%"
    },

    {
      title: "操作",
      dataIndex: "x123",
      key: "x123",
      render: (text, record) => {
        return (
          <a
						disabled ={!schemaList[`${record.databaseId}`]}
            onClick={() => {
              handleTestAllDb(record.databaseId,record.owner);
            }}
            className="AStyle"
          >
            <Tooltip title="数据库测试(所有)">
              <Icon type="reload" />
            </Tooltip>
          </a>
        );
      }
    }
  ];

  const expandedRowRender = record => {
    const columns = [
      { title: "schema名称",width:"50%" ,dataIndex: "schema", key: "schema" },
      {
        title: "状态",
        dataIndex: "status",
        key: "status",
        render: (text) => {
          switch(text){
						case "init":
							return "待测试";
						case "loading":
							return <Icon type="loading" />
						case "success":
							return <span style={{ color:"#b7eb8f" }} >连接成功</span>
						case "failed":
							return <span style={{ color:"#ffa39e" }} >连接失败</span>
						default:
							return "待测试";
					}
        }
      },
      {
        title: "所有者",
        dataIndex: "owner",
        key: "owner"
      },
      {
        title: "操作",
        dataIndex: "x123",
        key: "x123",
        render: (text, cloumnRecord) => {
          return (
            <div>
              <a
                onClick={() => {
                  handleClick(cloumnRecord.schema, cloumnRecord.schemaId);
                }}
                className="AStyle"
              >
                <Icon type="edit" />
              </a>
              &nbsp;&nbsp;&nbsp;&nbsp;
              <a
                onClick={() => {
                  testSingleDb(cloumnRecord.schemaId,cloumnRecord.owner,record.databaseId);
                }}
                className="AStyle"
              >
                <Tooltip title="数据库测试(当前)">
                  <Icon type="reload" />
                </Tooltip>
              </a>
							&nbsp;&nbsp;&nbsp;&nbsp;
              <Empower api={canEdit ? "/db/deleteDbConnection.do" : ""}>
                <Popconfirm
                  title="确认要删除该行吗?"
                  onConfirm={() => {
                    handleDelete(record.name);
                  }}
                  disabled
                  okText="是"
                  cancelText="否"
                >
                  <a className="AStyle">
                    <Icon type="delete" />
                  </a>
                </Popconfirm>
              </Empower>
            </div>
          );
        }
      }
    ];

    return (
      <Table
        rowKey="schemaId"
        columns={columns}
        dataSource={
          schemaList[`${record.databaseId}`]
            ? schemaList[`${record.databaseId}`]
            : []
        }
        pagination={false}
      />
    );
  };

  const handleClick = (name, id) => {
    dispatch({
      type: "resourcecontent/edit",
      payload: {
        model: "DataSystem",
        name,
        id
      }
    });
  };

  const handleDelete = name => {
    const { query } = location;
    dispatch({
      type: "resourcecontent/delete",
      payload: {
        model: "DataSystem",
        name: name,
        keyword: query.keyword
      }
    });
  };

  const handleNewModel = () => {
    dispatch({
      type: "resourcecontent/changeStatus",
      payload: {
        view: "model",
        config: {}
      }
    });
  };

  //展开的行发生变化时
  const onExpand = (expanded, record) => {
    console.log(expanded);
    console.log(record);
    if (expanded) {
      dispatch({
        type: "resourcecontent/getSchemaList",
        payload: {
          id: record.databaseId,
          name: record.name
        }
      });
    }
	};
	
	//测试所有的schemaList
	const handleTestAllDb = (id,owner)=>{
		message.info("测试全部数据库时间较长,请耐心等候!");
		dispatch({
			type:"resourcecontent/testAllDb",
			payload:{
				id,owner
			}
		})
	}

	//测试单个数据库链接
	const testSingleDb = (id,owner,databaseId)=>{
		dispatch({
			type:"resourcecontent/testSingleDb",
			payload:{
				id,owner,databaseId
			}
		})
	}

  return (
    <div id="ResourceContent">
      <ListHeader
        title="数据系统"
        location={location}
        onClick={() => {
          handleNewModel();
        }}
        api={canEdit ? "/db/saveDbConnection.do" : ""}
      />
      <div id={Style.ListTable}>
        <Table
          rowKey="databaseId"
          className="components-table-demo-nested"
          columns={columns}
          dataSource={dataList}
          loading={loading}
          expandedRowRender={expandedRowRender}
          pagination={false}
          onExpand={onExpand}
        />
        {total > 0 ? (
          <Pagination
            showQuickJumper
            current={query.page ? parseInt(query.page) : 1}
            pageSize={8}
            total={total}
            onChange={onChange}
          />
        ) : null}
      </div>
    </div>
  );
};

export default withRouter(
  connect(({ resourcecontent }) => ({
    resourcecontent
  }))(DataSystemList)
);
