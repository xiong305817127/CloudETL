import React, { Component } from "react";
import { Form, Row, Col, Input, Icon, Button, Select } from "antd";
const FormItem = Form.Item;
import { connect } from "dva";
import TableList from "components/TableList";
import TableGenerator from "./components/TableGenerator";

import { withRouter, hashHistory } from "react-router";
import { deepCopy, downloadFile, findKeyByValue } from "utils/utils";
class index extends Component {
  constructor() {
    super();
    this.state = {
      selectedRowKeys: [],
      selectedRows: []
    };
  }

  //提交表单
  onClickSobmit = () => {
    const { dataObj } = this.props.resourceFormatModel;
    const { dispatch, form } = this.props;
    form.validateFields((err, values) => {
      console.log(dataObj, "values");
      if (!err) {
        const tempArr = Object.entries(values);
        const dictionaryVO = [];
        dataObj.forEach((vue, ine) => {
          tempArr.forEach(val => {
            val[1].forEach((v, i) => {
              if (!dictionaryVO[i]) {
                dictionaryVO[i] = {
                  id: vue.id,
                  // code:val.code,
                  // name:val.name,
                  type: "type",
                  typeParentId: vue.typeParentId,
                  useFlag: vue.useFlag
                };
              }
              dictionaryVO[i][val[0]] = v;
            });
          });
        });

        console.log(dictionaryVO, "dictionaryVO");
        dispatch({
          type: "resourceFormatModel/getSobmit",
          payload: { dictionaryVO }
        });
        dispatch({ type: "resourceFormatModel/save", payload: { status: 4 } });
      }
    });
  };

  submit = values => {
    let dictionary = [];
    const { dispatch } = this.props;
    values.forEach(val => {
      if (val.parent) {
        dictionary.push(val.parent);
      }
      val.data.forEach(v => {
        dictionary.push({
          ...v,
          typeParentId: val.parent.id,
          
          editing: null,
          key: null,
          type: "type"
        });
      });
    });

    dispatch({
      type: "resourceFormatModel/getSubmit",
      payload: { dictionaryVO: dictionary }
    });
  };

  getDataSource = list => {
    let tempDataSourceList = [];
    let tempObj = {};
    list.forEach(val => {
      if (val.typeParentId == 0) {
        tempDataSourceList.push({ parent: val });
      } else {
        if (!tempObj[val.typeParentId]) {
          tempObj[val.typeParentId] = [];
        }

        tempObj[val.typeParentId].push(val);
      }
    });

    return tempDataSourceList.map(val => ({
      ...val,
      data: tempObj[val.parent.id] ? tempObj[val.parent.id] : []
    }));
  };

  render() {
    const {
      loading,
      viewFields,
      datalist,
      updataTime,
      show,
      dataObj
    } = this.props.resourceFormatModel;

    const dataSource = this.getDataSource(dataObj);

    const column = [
      {
        title: "代码",
        dataIndex: "code",
        key: "code",
        width: "25%"
      },
      {
        title: "名称",
        dataIndex: "name",
        key: "name",
        width: "25%"
      },
      {
        title: "是否使用",
        dataIndex: "useFlag",
        key: "useFlag",
        default: false,
        editable: false,
        width: "10%"
      }
    ];
    return (
      <div style={{ margin: 20 }}>
        <TableGenerator
          //IfShowHeader={false}
          column={column}
          dataSource={dataSource}
          header={({ submit }) => (
            <div>
              <Button type="primary" onClick={() => submit(this.submit)}>
                保存
              </Button>
            </div>
          )}
        />
      </div>
    );
  }
}

export default connect(({ resourceFormatModel }) => ({ resourceFormatModel }))(
  withRouter(Form.create()(index))
);
