import React, { Component } from "react";

// 样式
import styles from "./tableGenerator.less"

// 引入组件
import { Form, Input, Table, Popconfirm, Button, Tag, message, Row, Col } from "antd";
const FormItem = Form.Item;

/**
 * 用于显示单个的Editable输入框或文字
 * @param {Object} props 组件的props属性
 * @param {contex} context 组件的上下文
 */
const EditableCell = (
  { dataIndex, record, ...restProps, handleEditing, handleSave, handleDelete, editable },
  { form }
) => {

  /**
   * 如果要获取当前的field属性，请在父级的td中获取
   */
  const { getFieldDecorator, setFieldsValue, validateFields } = form;
  const handleSaveBtn = handleSave
    ?
    () => validateFields((err, values) => {
      if (!err) {
        const tempArr = Object.entries(values);
        let obj = {};
        tempArr.forEach(val => {
          obj[val[0].split(record.key)[0]] = val[1]
        });
        handleSave(obj, record.key);
      } else {
        console.log(err);
      }
    })
    : null;

  const handleEditingBtn = handleEditing ? handleEditing : null;
  const handleDeleteBtn = handleDelete ? handleDelete : null;

  return (
    <td {...restProps} className={styles.initialTd}>

      {/* 编辑和保存按钮 */}
      {
        dataIndex == "editing"
        &&
        (
          !record.editing
            ?
            (<a onClick={handleEditingBtn} className={styles.func}>编辑</a>)
            :
            (<a onClick={handleSaveBtn} className={styles.func}>保存</a>)
        )
      }

      {/* 删除按钮 */}
      {
        dataIndex == "editing" &&
        (
          <Popconfirm title="删除此行？" okText="删除" cancelText="取消" onConfirm={handleDeleteBtn}>
            <a href="#">删除</a>
          </Popconfirm>
        )
      }

      {/* 输入框显示 */}
      {
        record && dataIndex != "editing" && editable &&
        (
          <FormItem style={{ display: record.editing ? "block" : "none" }}>
            {
              getFieldDecorator(
                dataIndex + record.key,
                {
                  rules: [{ required: true, message: "请填写", min: 1 }],
                  initialValue: record[dataIndex] ? record[dataIndex] : ""
                }
              )(
                <Input
                  onChange={(value) => { setFieldsValue({ [dataIndex + record.key]: value }); }}
                />
              )
            }
          </FormItem>
        )
      }
      {
        record
        &&
        dataIndex != "editing"
        &&
        (!record.editing || !editable)
        &&
        (record && typeof record[dataIndex] != "undefined"
          ?
          (
            typeof record[dataIndex] == "boolean"
              ? record[dataIndex] ? "是" : "否"
              : record[dataIndex]
          )
          : ""
        )
      }
    </td>
  );
};

EditableCell.contextTypes = {
  form: React.PropTypes.object.isRequired
};

class EditableRow extends Component {
  constructor() {
    super();
  }

  getChildContext() {
    const { form } = this.props;
    return {
      form: form
    };
  }

  render() {
    const { index, form, ...props } = this.props;
    return (<tr {...props} />)
  }
}

EditableRow.childContextTypes = {
  form: React.PropTypes.object.isRequired
};

const TableFormRow = Form.create()(EditableRow);

class TableGenerator extends Component {
  constructor(props) {
    super(props);

    this.state = {
      dataSource: this.getDataSource(props.dataSource ? props.dataSource : []),
      dataSourceInitial: this.getDataSource(props.dataSource ? props.dataSource : []),
      columnList: this.columnGenerator(
        props.column ? props.column : props.columnList,
        props.column ? props.dataSource.length : 0
      ),
    };
  }

  componentWillReceiveProps(nextProp,prevProp) {
    this.setState({
      dataSource: this.getDataSource(nextProp.dataSource ? nextProp.dataSource : []),
      dataSourceInitial: this.getDataSource(nextProp.dataSource ? nextProp.dataSource : []),
      columnList: this.columnGenerator(
        nextProp.column ? nextProp.column : nextProp.columnList,
        nextProp.column ? nextProp.dataSource.length : 0
      ),
    })
  }


  getDataSource = (dataSource) => {
    return dataSource.map(
      val => val && ({ parent: val.parent, data: val && val.data && val.data.map((v, i) => ({ ...v, editing: false, key: i })) })
    )
  }


  columnGenerator = (columnList, groupLength) => {
    const list = groupLength ? (new Array(groupLength)).toString().split(",").map(() => columnList) : columnList;
    const generate = (val, index) => {
      return val && val.map(
        v => (
          {
            ...v,
            onCell: record => ({
              record,
              dataIndex: v.dataIndex,
              title: v.title,
              key: v.key,
              editable: typeof v.editable === "undefined" || v.editable ? true : false
            })
          }
        )
      ).concat([
        {
          title: "操作",
          dataIndex: "editing",
          key: "editing",
          onCell: (record) => ({
            record,
            dataIndex: "editing",
            handleEditing: () => this.setEditing(record.key, index),
            handleSave: (newValue, key) => this.saveEditing(newValue, key, index),
            handleDelete: () => this.deleteRow(record.key, index)
          })
        }
      ])
    }
    return groupLength ? list.map((v, i) => generate(v, i)) : generate(list, 0);
  }

  saveEditing = (newValue, key, index) => {
    this.setState({
      dataSource: this.state.dataSource.map(
        (val, i) =>
          i == index
            ?
            {
              parent: val.parent,
              data: val.data.map(
                v => (v.key == key ? { ...v, editing: !v.editing, ...newValue } : v)
              )
            }
            :
            val
      )
    })
  }

  setEditing = (key, index) => {
    this.setState({
      dataSource: this.state.dataSource.map(
        (val, i) =>
          i == index
            ?
            {
              parent: val.parent,
              data: val.data.map(
                v => ({ ...v, editing: v.key == key ? !v.editing : v.editing })
              )
            }
            :
            val
      )
    })
  }

  deleteRow = (key, index) => {
    this.setState({
      dataSource: this.state.dataSource.map(
        (val, i) =>
          i == index
            ?
            {
              parent: val.parent,
              data: val.data.filter(v => v.key !== key)
            }
            :
            val
      )
    })
  }

  addRow = (tableTag) => {
    this.setState({
      dataSource: this.state.dataSource.map(
        (v, i) => {
          return i == tableTag
            ? {
              parent: v.parent,
              data: v.data ? v.data.concat([this.getEmpty(tableTag)]) : [this.getEmpty(tableTag)]
            }
            : v
        }
      )
    })
  }

  getEmpty = (tableTag) => {
    const column = this.state.column ? this.state.column : this.state.columnList[tableTag];
    let tempObj = {};

    column.forEach(val => {
      tempObj[val.dataIndex] = typeof val.default != "undefined" ? val.default : "";
    });

    tempObj.editing = true;
    tempObj.key = this.state.dataSource[tableTag].data.length;

    return tempObj;
  }

  resetRow = (index) => {
    this.setState({
      dataSource: this.state.dataSource.map((val, i) =>
        index == i
          ?
          this.state.dataSourceInitial[index]
          :
          val
      )
    })
  }

  submit = (cb) => {
    const { dataSource } = this.state;

    if (dataSource.some(v => v.data.some(val => val.editing))) {
      message.info("您有未保存的数据，请先点击保存！");
    } else {
      cb(dataSource);
    }
  }

  render() {
    const { header, IfShowHeader } = this.props;
    console.log(IfShowHeader, "IfShowHeader====")
    return (
      <div>
        {
          header
          &&
          header({
            submit: (cb) => this.submit(cb)
          })
        }
        {
          this.state.dataSource.length > 0
          &&
          this.state.dataSource.map((val, index) => {
            return (
              <div className={styles.item}>
                <p>
                  <Row>
                    <Col> {val.parent.title ? val.parent.title : "资源格式分类:"}<label>{val.parent.name}</label> </Col>
                  </Row>


                  <span>
                    <Button size="small" type="primary" onClick={() => this.addRow(index)}>新增行</Button>
                    <Popconfirm title="是否重置" okText="重置" cancelText="取消" onConfirm={() => this.resetRow(index)}>
                      <Button size="small" >重置</Button>
                    </Popconfirm>
                  </span>
                </p>
                <Table
                  components={{
                    body: {
                      row: TableFormRow,
                      cell: EditableCell
                    }
                  }}
                  size="small"
                 // showHeader={IfShowHeader ? false:true }
                  columns={this.state.columnList[index]}
                  dataSource={this.state.dataSource[index].data}
                  pagination={false}
                />
              </div>
            )
          })
        }

      </div>

    );
  }
}

export default TableGenerator;
