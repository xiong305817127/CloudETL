import React from "react";
import { connect } from "dva";
import {
  Button,
  Form,
  Input,
  Radio,
  Select,
  Tabs,
  Row,
  Col,
  Table
} from "antd";
import Modal from "components/Modal.js";
import style from "./style.less";
import { selectType } from "../../../../constant";
import EditableTable from "components/common/EditTable";

const FormItem = Form.Item;

class ParquetOutput extends React.Component {
  constructor(props) {
    super(props);
    const { visible } = props.model;
    if (visible === true) {
      const { config } = props.model.config;
      this.state = {};
    }
  }

  /**
   * 隐藏modal
   */
  hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type: "items/hide",
      visible: false
    });
  };

  /**
   * 提交创建内容
   */
  handleCreate = e => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) {
        return;
      }
      const { panel, transname, key, saveStep, text } = this.props.model;

      let obj = {
        transname: transname,
        newname: text === values.text ? "" : values.text,
        stepname: text,
        type: panel
      };

      // 拷贝获取field
      const fieldnames = this.getData();

      obj.config = {
        ...values,
        fieldnames: fieldnames
      };
      saveStep(obj, key, data => {
        if (data.code === "200") {
          this.hideModal();
        }
      });
    });
  };

  // 获取table中的数据
  getData = () => {
    return this.table.state.dataSource
    .filter((val)=> val.type !== "" && val.type !== "" && val.filedname !== "" );
  };

  /**
   * 新增数据
   */
  addData = () => {
    this.table.handleAdd({
      fieldname: "",
      type: "",
      length: "",
      precision: ""
    });
  };
  /**
   * 新增数据
   */
  delete = () => {
    this.table.handleDelete();
  };

  getTable = table => {
    this.table = table;
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const { text, config, visible , handleCheckName} = this.props.model;
    const { fieldnames } = config;

    const formItemLayout3 = {
      labelCol: { span: 24 },
      wrapperCol: { span: 20 }
    };

    const dataSource =
      fieldnames.length === 0
        ? [
            {
              key: 0,
              fieldname: "",
              type: 0,
              length: "",
              precision: ""
            }
          ]
        : fieldnames
          .map((val,i)=>({...val,type: val.type.toString(),key: i}));
    const columns = [
      {
        title: "名称",
        dataIndex: "fieldname",
        key: "fieldname",
        editable: true,
        width: "30%"
      },
      {
        title: "类型",
        dataIndex: "type",
        key: "type",
        selectable: true,
        selectArgs: selectType.get("numberType"),
        width: "30%"
      },
      {
        title: "长度",
        dataIndex: "length",
        key: "length",
        editable: true,
        width: "20%"
      },
      {
        title: "精度",
        dataIndex: "precision",
        key: "precision",
        editable: true,
        width: "20%"
      }
    ];

    return (
      <Modal
        visible={visible}
        title="从结果中读取流"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        onCancel={this.hideModal.bind(this)}
        width={700}
        style={{ minHeight: "500px" }}
        footer={[
          <Button
            key="submit"
            type="primary"
            size="large"
            onClick={this.handleCreate}
          >
            确定
          </Button>,
          <Button key="back" size="large" onClick={this.hideModal}>
            取消
          </Button>
        ]}
      >
        <Form>
          <Row>
          <Col span={12}>
              <FormItem
                label={
                  <span style={{ textAlign: "left", marginBottom: 0 }}>
                    步骤名称
                  </span>
                }
                {...formItemLayout3}
                style={{ marginBottom: "8px" }}
              >
                {getFieldDecorator("text", {
                  initialValue: text,
                  rules: [
                    {
                      whitespace: true,
                      required: true,
                      message: "请输入步骤名称！"
                    },
                    {
                      validator: handleCheckName,
                      message: "步骤名称已存在，请更改!"
                    }
                  ]
                })(<Input />)}
              </FormItem>
            </Col>
            <Col span={12}></Col>
            <Col span={24}>
              <Row style={{margin:"5px 0",width:"100%"}}  >
                <Col span={12} size={"small"} >
                  <Button size={"small"} onClick={this.addData}>添加字段</Button>
                </Col>

                <Col span={12} size={"small"} >
                  <Button style={{float:"right"}}  size={"small"} onClick={this.delete}>删除字段</Button>
                </Col>
              </Row>
              <EditableTable
                dataSource={dataSource}
                columns={columns}
                ref={this.getTable}
                count={dataSource.length}
                rowSelection={true}
                size="small"
              />
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
}

export default connect()(Form.create()(ParquetOutput));
