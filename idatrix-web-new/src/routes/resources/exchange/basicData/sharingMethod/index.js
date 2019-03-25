import React, { Component } from "react";
import { Form, Row, Col, Input, Icon, Button, Select } from 'antd';
const FormItem = Form.Item;
import { connect } from 'dva';
import TableList from 'components/TableList';
import { withRouter, hashHistory } from 'react-router';
import { deepCopy, downloadFile, findKeyByValue } from 'utils/utils';

class index extends Component {
  constructor() {
    super();
    this.state = {
      selectedRowKeys: [],
      selectedRows:[],
    };
    this.onClickSobmit=this.onClickSobmit.bind(this);
    this.onClickEdit = this.onClickEdit.bind(this);
    this.handleDelect = this.handleDelect.bind(this);
    this.handleAddFields = this.handleAddFields.bind(this);
  }


  makeColumns = () => {
    const { getFieldDecorator } = this.props.form;
    const { status,dataId } = this.props.sharingMethodModel;
    return [{
      title: '代码',
      dataIndex: 'code',
      key: 'code',
      width: '30%',
      render: (text, record, index) => {
        return <FormItem labelCol={{ span: 0 }}>
          {getFieldDecorator(`code.${index}`, {
            initialValue: text,
          })(
            <Input disabled={status === 4} style={{marginTop:15}} maxLength="50" onChange={(e) => { this.modifyField(`code.${index}`, e.target.value, record) }} />
          )}
        </FormItem>
      }
    }, {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      width: '30%',
      render: (text, record, index) => {
        return <FormItem labelCol={{ span: 0 }}>
          {getFieldDecorator(`name.${index}`, {
            initialValue: text,

          })(
            <Input disabled={status === 4} style={{marginTop:15}} maxLength="50" onChange={(e) => { this.modifyField(`name.${index}`, e.target.value, record) }} />
          )}
        </FormItem>
      }
    }, {
      title: '是否使用',
      dataIndex: 'useFlag',
      key: 'useFlag',
      width: '30%',
      render: (text) =>text ===true?"是":"否" 
    }]
  }

  
  // 修改字段
  modifyField(keyOfCol, value, record) {
    const { dispatch } = this.props;
    const { datalist,viewFields } = this.props.sharingMethodModel;
    const data = deepCopy(viewFields).map(row => {
      if (row.key === record.key) {
        row[keyOfCol] = value;
      }
      return row;
    });

    dispatch({ type: "sharingMethodModel/setMetaId", payload: { datalist: data } });
  }

  //点击增加表单
  handleAddFields = () => {
    const { dispatch } = this.props;
    dispatch({ type: "sharingMethodModel/addField"})
    dispatch({ type: "sharingMethodModel/save", payload: { status:1,show:false } })
  };

  //提交表单
  onClickSobmit = () => {
    const { datalist } = this.props.sharingMethodModel;
    const { dispatch, form } = this.props;
    form.validateFields((err, values) => {
     
      if (!err) {
        const tempArr = Object.entries(values);
        const dictionaryVO = [];
        tempArr.forEach((val) => {
          val[1].forEach((v, i) => {
            if (!dictionaryVO[i]) {
              datalist.forEach((vals) => {
                dictionaryVO.push({
                  id: vals.id===0?"":vals.id,
                  typeParentId:0,
                  useFlag:"",
                  type: "share",
                })
               
              })
             
            }
            dictionaryVO[i][val[0]] = v;
          })
        });

        console.log(dictionaryVO);
        dispatch({ type: "sharingMethodModel/getSobmit", payload: { dictionaryVO } })
        dispatch({ type: "sharingMethodModel/save", payload: { status:4 } })
      }
    })
  }

    handleDelect=(e)=>{
      const { dispatch, sharingMethodModel } = this.props;
      const {selectedRowKeys,selectedRows}=this.state;
      
      let objId=[];
      selectedRows.forEach((val) => {
        objId.push(val.id);
        let selectKey = objId.join(',')
        if(selectKey === "0"){
          dispatch({ 
            type: 'classificationModel/delField',  status:1,
            keys: selectedRows.map(r => r.key)
          });
        }else{
        
          if(!selectedRows) return false;
            dispatch({ 
              type: 'classificationModel/getDeletelist',
                payload:{
                     id: selectKey
                }
            });
        }
      })
    }
     
    onClickEdit=()=>{
      const { dispatch } = this.props;
      dispatch({ type: "sharingMethodModel/save", payload: { status:3 } })
    }


    onSelectChange = (selectedRowKeys, selectedRows) => {
      this.setState({ selectedRowKeys, selectedRows });
    }

  render() {
    const { loading, pagination, viewFields ,datalist,status,updataTime,show } = this.props.sharingMethodModel;
    const {selectedRowKeys}=this.state;
    const data =show===false?deepCopy(viewFields):datalist;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };
      return (
        <div style={{ margin: 20 }}>
        <Form className="btn_std_group">
          <Row >
            <Col style={{ float: "left", margin: '10' }} span={5}>最近更新时间：{updataTime !== ""?updataTime:"暂无" } </Col>

            <Col style={{ float: "right" }} className="antdfontSize" span={2} onClick={this.onClickSobmit}>
              <Button type="primary" className="antdMargin">保存 </Button>
            </Col>
           <Col style={{ float: "right" }} className="antdfontSize" span={2} onClick={this.onClickEdit}>
               <Button type="primary" className="antdMargin">编辑</Button>
          </Col> 

            <Col style={{ float: "right" }} className="antdfontSize" span={2} onClick={this.handleDelect}>
               <Icon type="delete" className="op-icon antdMargin"  />删除
            </Col>
            <Col style={{ float: "right" }} className="antdfontSize" span={2} onClick={this.handleAddFields}>
              <Icon type="plus-square" className="op-icon antdMargin" />新增
               </Col>
          </Row>

        </Form>
        <div >
          <TableList
            showIndex
            type="checkbox"
            style={{ marginTop: 20 }}
            columns={this.makeColumns()}
            loading={loading}
            dataSource={data}
            pagination={false}
            rowSelection={rowSelection}
            ref="editTable"
            scroll={{ y: 600 }}
          />
        </div>
      </div>
      );
    };
  }

export default connect(({ sharingMethodModel }) => ({ sharingMethodModel }))(withRouter(Form.create()(index)));
