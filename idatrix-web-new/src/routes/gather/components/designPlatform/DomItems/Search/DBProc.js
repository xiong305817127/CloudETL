/**
 * Created by Administrator on 2017/12/19 0019.
 */
//一、环境
import React from "react";
import { connect } from "dva";
import { Form, Select, Button, Input, Checkbox, Row, Col, message } from "antd";
import Modal from "components/Modal";
import EditTable from "../../../common/EditTable";
import withDatabase from "../../../common/withDatabase";
import { get_ProcList } from "../../../../../../services/gather";

const ButtonGroup = Button.Group;
const FormItem = Form.Item;
const { Option } = Select;

//二、渲染
class DBProcInput extends React.Component {
  //1.预加载数据:
  constructor(props) {
    super(props);
    const { visible, config } = props.model;
    if (visible === true) {
      const { argument } = config;
      let count = 0;
      let data = [];
      if (argument) {
        for (let index of argument) {
          data.push({
            key: count,
            ...index
          });
          count++;
        }
      }
      // console.log(data1,111);
      this.state = {
        dataSource: data,
				InputData: [], //下拉选项：控件传过来的数组
				procedureList:[]
      };
    }
  }
  //2.前后控件参数：
  componentDidMount() {
		const { getInputFields, transname, text,config } = this.props.model;
		const { connection,schemaId } = config;

    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj, data => {
      // console.log(data,123);
      if (data) {
        this.setState({ InputData: data });
      }
		});

		if(connection !== undefined && schemaId !== undefined){
			this.setProcedureList({
				connection,schemaId
			})
		}
  }
  //3.1.提交表单：
  handleFormSubmit = e => {
    e.preventDefault();
    const form = this.props.form;
    const {
      panel,
      transname,
      description,
      key,
      saveStep,
      text,
      formatTable,
      config
    } = this.props.model;
    const { argument } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields1 = [];
      if (this.refs.editTable1) {
        if (this.refs.editTable1.state.dataSource.length > 0) {
          let arg = ["field", "name", "type"];
          sendFields1 = formatTable(this.refs.editTable1.state.dataSource, arg);
        }
      } else {
        if (argument) {
          sendFields1 = argument;
        }
      }

      const {
        schemaId,
        schema,
        databaseId,
        database
      } = this.props.databaseData;

      let obj = {};
      obj.transname = transname;
      obj.stepname = text;
      obj.newname = text === values.text ? "" : values.text;
      obj.type = panel;
      obj.description = description; //控件基本参数+5
      obj.config = {
        //表单参数设置
        argument: sendFields1,
        ...values,
        schemaId,
        databaseId,
       	schema,
        connection: database
      };
      saveStep(obj, key, data => {
        if (data.code === "200") {
          this.setModelHide();
        }
      });
    });
  };
  //3.2.关闭对话框：打开对话框--在初始化Model触发状态
  setModelHide() {
    const { dispatch } = this.props;
    dispatch({
      type: "items/hide",
      visible: false
    });
  }
  /**4.其他：*/
  //4.1.对话框布局
  formItemLayout1 = {
    labelCol: { span: 5 },
    wrapperCol: { span: 11 }
  };
  formItemLayout2 = {
    labelCol: { span: 5 },
    wrapperCol: { span: 18 }
  };
  formItemLayout3 = {
    labelCol: { span: 5 },
    wrapperCol: { span: 15 }
  };
  formItemLayout4 = {
    labelCol: { span: 4 },
    wrapperCol: { span: 18 }
  };
  formItemLayout5 = {
    labelCol: { span: 3 },
    wrapperCol: { span: 19 }
  };
  //4.2.自定义标题
  columns = [
    {
      title: "名称",
      dataIndex: "field",
      key: "field",
      width: "30%",
      selectable: true
    },
    {
      title: "方向",
      dataIndex: "name",
      key: "name",
      width: "30%",
      selectable: true,
      selectArgs: [
        <Select.Option key="IN" value="IN">
          IN
        </Select.Option>,
        <Select.Option key="OUT" value="OUT">
          OUT
        </Select.Option>,
        <Select.Option key="INOUT" value="INOUT">
          INOUT
        </Select.Option>
      ]
    },
    {
      title: "类型",
      dataIndex: "type",
      key: "type",
      // width:"50%",
      selectable: true,
      selectArgs: [
        <Select.Option key="Number" value="Number">
          Number
        </Select.Option>,
        <Select.Option key="String" value="String">
          String
        </Select.Option>,
        <Select.Option key="Date" value="Date">
          Date
        </Select.Option>,
        <Select.Option key="Boolean" value="Boolean">
          Boolean
        </Select.Option>,
        <Select.Option key="Integer" value="Integer">
          Integer
        </Select.Option>,
        <Select.Option key="BigNumber" value="BigNumber">
          BigNumber
        </Select.Option>,
        <Select.Option key="Binary" value="Binary">
          Binary
        </Select.Option>,
        <Select.Option key="Timestamp" value="Timestamp">
          Timestamp
        </Select.Option>,
        <Select.Option key="Binary" value="Binary">
          Binary
        </Select.Option>,
        <Select.Option key="Internet Address" value="Internet Address">
          Internet Address
        </Select.Option>
      ]
    }
  ];
  //4.3.表格方法：
  handleAdd1 = () => {
    const data = {
      field: "",
      name: "IN",
      type: "String"
    };
    this.refs.editTable1.handleAdd(data);
  };
  handleAuto1 = () => {
    if (this.state.InputData.length > 0) {
      let args = [];
      let count = 0;
      for (let index of this.state.InputData) {
        args.push({
          key: count,
          field: index.name, //只有name内容来之控件
          name: "IN",
          type: "String"
        });
        count++;
      }
      this.refs.editTable1.updateTable(args, count);
    } else {
      message.info("未找到对应的控件字段");
    }
  };
  handleDelete1 = () => {
    this.refs.editTable1.handleDelete();
  };
  initFuc1(that) {
    const { getInputSelect } = this.props.model;
    const { InputData } = this.state;
    let options = getInputSelect(InputData, "name"); //对应数组InputData的属性名对应标题key:name
    // console.log(that,'获取model方法：updateOptions');
    that.updateOptions({
      field: options, //可下拉
      name: options, //可下拉
      type: options //可下拉
    });
  }

	//切换数据库
	getSchemaList = (id)=>{
		if (id === undefined) return;
    const { setFieldsValue } = this.props.form;
    const { getSchemaList } = this.props;

    setFieldsValue({
      schema: "",
      procedure: ""
    });

    //调用高阶组件的通用方法
    getSchemaList(id);
	}

	changeSchema(id){
		if (id === undefined) return;
		const { database } = this.props.databaseData;
		const { setFieldsValue } = this.props.form;
		const { getTableList } = this.props;
    setFieldsValue({
      procedure: ""
    });
		getTableList(id);
		this.setProcedureList({
			connection:database,
			schemaId:id
		})
	}

	setProcedureList(obj){
		const { transname,owner } = this.props.model;
		get_ProcList({
			...obj,transname,owner
		}).then(res=>{
			const { data } = res.data;
			const { code } = data;
			if(code === "200" && data.data && data.data instanceof Array ){
				this.setState({ procedureList: data.data });
			}
		})
	}


  render() {
    const { getFieldDecorator } = this.props.form;
    const { visible, config, text, handleCheckName } = this.props.model;
    const {
      databaseList,
      schemaList,
      schema,
      database
		} = this.props.databaseData;
		const { procedureList } = this.state;

    return (
      <Modal
        maskClosable={false}
        visible={visible}
        title="调用DB存储过程"
        onCancel={this.setModelHide.bind(this)}
        wrapClassName="vertical-center-modal"
        width={750}
        footer={[
          <Button
            key="submit"
            type="primary"
            size="large"
            onClick={this.handleFormSubmit.bind(this)}
          >
            确定
          </Button>,
          <Button
            key="back"
            size="large"
            onClick={this.setModelHide.bind(this)}
          >
            取消
          </Button>
        ]}
      >
        <FormItem label="步骤名称" {...this.formItemLayout2}>
          {getFieldDecorator("text", {
            initialValue: text,
            rules: [
              { whitespace: true, required: true, message: "请输入步骤名称" },
              { validator: handleCheckName, message: "步骤名称已存在，请更改!" }
            ]
          })(<Input spellCheck={false} />)}
        </FormItem>

        <div style={{ border: "1px solid #D9D9D9" }}>
          <fieldset className="ui-fieldset">
            <legend>&nbsp;&nbsp;设置</legend>
            <Form>
              <FormItem
                label="数据库连接："
                {...this.formItemLayout2}
                style={{ marginBottom: 8 }}
              >
                {getFieldDecorator("connection", {
                  initialValue: database,
                  rules: [{ required: true, message: "请选择数据库链接" }]
                })(
                  <Select
                    allowClear
                    placeholder="请选择数据库连接"
                    onChange={this.getSchemaList.bind(this)}
                  >
                    {databaseList.map(index => (
                      <Option key={index.id} value={index.id}>
                        {index.name}
                      </Option>
                    ))}
                  </Select>
                )}
              </FormItem>
              <FormItem
                label="模式名称"
								{...this.formItemLayout2}
                style={{ marginBottom: 8 }}
              >
                {getFieldDecorator("schema", {
                  initialValue: schema,
                  rules: [{ required: true, message: "请选择模式名称" }]
                })(
                  <Select onChange={this.changeSchema.bind(this)}>
                    {schemaList.map(index => (
                      <Select.Option
                        key={index.schemaId}
                        value={index.schemaId}
                      >
                        {index.schema}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </FormItem>
              <FormItem
                label="储存过程名称"
                {...this.formItemLayout2}
                style={{ marginBottom: 8 }}
              >
                {getFieldDecorator("procedure", {
                  initialValue: config.procedure?config.procedure:""
                })(
                  <Select
                    placeholder="选择数据库连接匹配名称或自定义名称"
                    allowClear
                    onChange={this.changeSchema.bind(this)}
                  >
                    {procedureList.map(index => (
                      <Select.Option  key={index} value={index}>{index}</Select.Option>
                    ))}
                  </Select>
                )}
              </FormItem>
              <FormItem
                label="启用自动提交"
                {...this.formItemLayout2}
                style={{ marginBottom: 8 }}
              >
                {getFieldDecorator("autoCommit", {
                  valuePropName: "checked",
                  initialValue: config.autoCommit ? config.autoCommit : false
                })(<Checkbox />)}
              </FormItem>
              <FormItem
                label="返回值名称"
                {...this.formItemLayout2}
                style={{ marginBottom: 8 }}
              >
                {getFieldDecorator("resultName", {
                  initialValue: config.resultName
                })(<Input spellCheck={false} />)}
              </FormItem>
              <FormItem
                label="返回值类型："
                {...this.formItemLayout2}
                style={{ marginBottom: 8 }}
              >
                {getFieldDecorator("resultType", {
                  initialValue: config.resultType
                })(
                  <Select placeholder="选择返回值类型">
                    <Select.Option value="Number">Number</Select.Option>
                    <Select.Option value="Date">Date</Select.Option>
                    <Select.Option value="String">String</Select.Option>
                    <Select.Option value="Boolean">Boolean</Select.Option>
                    <Select.Option value="Integer">Integer</Select.Option>
                    <Select.Option value="BigNumber">BigNumber</Select.Option>
                    <Select.Option value="Binary">Binary</Select.Option>
                    <Select.Option value="Timestamp">Timestamp</Select.Option>
                    <Select.Option value="Internet Address">
                      Internet Address
                    </Select.Option>
                  </Select>
                )}
              </FormItem>
            </Form>
          </fieldset>

          <fieldset className="ui-fieldset">
            <legend>&nbsp;&nbsp;参数</legend>
            <Row style={{ margin: "0 15px 15px" }}>
              <Col span={12}>
                <ButtonGroup size={"small"}>
                  <Button key="1" onClick={this.handleAuto1.bind(this)}>
                    获取字段
                  </Button>
                  <Button key="2" onClick={this.handleAdd1.bind(this)}>
                    添加字段
                  </Button>
                </ButtonGroup>
              </Col>
              <Col span={12} style={{ textAlign: "right" }}>
                <Button size={"small"} onClick={this.handleDelete1.bind(this)}>
                  删除字段
                </Button>
              </Col>
            </Row>
            <div style={{ margin: 15 }}>
              <EditTable
                columns={this.columns}
                dataSource={this.state.dataSource}
                scroll={{ y: 300 }}
                initFuc={this.initFuc1.bind(this)}
                rowSelection={true}
                size={"small"}
                count={0}
                ref="editTable1"
                tableStyle="editTableStyle5"
              />
            </div>
          </fieldset>
        </div>
      </Modal>
    );
  }
}
//三、传参、调用：
const DBProc = Form.create()(DBProcInput);
export default connect()(withDatabase(DBProc,{
	isRead:false
}));
