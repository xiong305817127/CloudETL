/**
 * Created by Administrator on 2017/3/13.
 */
import React from 'react'
import { Row,Col,Button,Menu,Table,Select,Spin } from 'antd';
import Modal from "components/Modal.js";
import { connect } from 'dva'
import './DbTable.css'

const Option = Select.Option;

const DbTable = ({dbtable,dispatch})=>{
    const {visible,connection,schema,tableList,loading,fuc,tableFields,selectKeys,tablename,dataType,schemalist,loading1} = dbtable;

  const columns = [{
    title: '表字段名',
    dataIndex: 'name',
    width:"100%"
  }];

  const handleSure = ()=>{
    let str = "";
    let schemaName = "";
    let tableName = tablename;
    if(dataType === "POSTGRESQL" ||  dataType === "HIVE3"){
      str = tablename
    }else if(dataType === "MYSQL" ){
      str = "`"+tablename+"`"
    }else if(dataType === "DM7" || dataType === "ORACLE" ){
      str = `"${schema}"."${tablename}"`
    } else{
      schemaName = schema;
      str = schema+"."+tablename
    };
    fuc(selectKeys,str,schemaName,tableName,dataType);
    handleHide();
  };

 const handleHide = ()=>{
    selectKeys.splice(0);
    dispatch({
      type:'dbtable/hide'
    })
  };

  const handleChange = (e)=>{
      selectKeys.splice(0);
     dispatch({
        type:'dbtable/queryTable',
        payload:{
          connection:connection,
          schema:e
        }
      });
  };

  const handleClick = (e)=>{
    selectKeys.splice(0);
    dispatch({
      type:'dbtable/queryTableFields',
      payload:{
        connection:connection,
        schema:schema,
        table:e.key
      }
    });
  };
  const rowSelection = {
    onChange: (selectedRowKeys) => {
      dispatch({
         type:"dbtable/show",
          payload:{
            selectKeys:selectedRowKeys
          }
      });
    }
  };

    return(
      <Modal
        visible={visible}
        title={connection}
        wrapClassName="vertical-center-modal out-model"
        okText="Create"
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={handleSure}>
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={handleHide}>取消</Button>
                ]}
        onCancel={handleHide}
        maskClosable={false}
      >

        <Row style={{ marginBottom:"20px"}}>
           <Col span={5} style={{height:"100%",lineHeight:"28px",textAlign:"right"}}>模式名称：</Col>
           <Col span={16}>
             <Select value={schema} onChange={handleChange} style={{ width:"100%"}} mode="vertical">
               {
                 schemalist.map((index)=>{
                   return(
                     <Option key={index} value={index}>
                       {index}
                     </Option>
                   )
                 })
               }
             </Select >
           </Col>
        </Row>

        <Row gutter={16}>
          <Col span={10} style={{height:300,overflowY:"scroll",overflowX:"hidden",border:"1px solid #e9e9e9",borderRadius:"4px" }}>
            <div >
              <Spin spinning={loading1}>
                <div style={{ width: 200,height:300}}>
                <Menu onClick={handleClick} style={{ width: 200}} mode="vertical">
                  {
                    tableList.map((index)=>{
                      return(
                        <Menu.Item key={index.table}>{index.table}</Menu.Item>
                      )
                    })
                  }
                </Menu>
                </div>
              </Spin>
            </div>
          </Col>
          <Col span={14} >
            <div id="dbtable">
              <Table  loading={loading} bordered={false} size={"small"} rowSelection = {rowSelection}
                      scroll={{ y: 245 }} pagination={false} dataSource={tableFields} columns={columns} />
            </div>
          </Col>
        </Row>
      </Modal>
    )
};

export default connect(({ dbtable }) => ({
  dbtable
}))(DbTable)
