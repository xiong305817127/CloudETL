import React from 'react'
import { connect } from 'dva'
import { Form, Radio, Select, Checkbox, Row, Col, Button, Alert, Tabs, List, Menu, Input } from 'antd'
import Modal from "components/Modal.js";
import { transArgs, disabledArgs } from '../../../constant';
import EditTable from '../../common/EditTable';

//控制四个选项的disabled
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;
const ButtonGroup = Button.Group;
const Option = Select.Option;
let controlDid = false;


class Index extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      engineName: "Default-Local",   //记录引擎的值
      //预览展示
      newVisible: false,
      //默认选中项
      activeKey: "",
      //当前的集合数据
      newItems: []
    }
  }
  componentWillReceiveProps(nextProps) {
    const { visible, items } = nextProps.runtrans;
    const { newVisible } = this.state;
    if (visible === true) {
      if (newVisible === false) {
        //初始化
        this.setState({
          activeKey: items[0] ? items[0].text : "",
          newItems: items.map(index => {
            return {
              stepName: index.text,
              pausingOnBreakPoint: false,
              readingFirstRows: true,
              rowCount: 100,
              condition: null
            }
          })
        })
      }
      this.updateTable(nextProps.runtrans.params);
    }
  }


  updateTable(params) {
    let args = [];
    let i = 0;

    if (this.refs.editTable) {
      if (params) {
        for (let index of Object.keys(params)) {
          args.push({
            key: i++,
            name: index,
            defaultValue: params[index],
            value: params[index]
          })
        }
      }
      this.refs.editTable.updateTable(args, i);
    }
  };

  initFuc(that) {
    const { params } = this.props.runtrans;
    let args = [];
    let i = 0;

    if (params) {
      for (let index of Object.keys(params)) {
        args.push({
          key: i++,
          name: index,
          defaultValue: params[index],
          value: params[index]
        })
      }
    }
    that.updateTable(args, i);
  };

  handleDeleteFields = () => {
    this.refs.editTable.handleDelete();
  };

  handleAdd1 = () => {
    const data = {
      "name": "",
      "value": ""
    };
    this.refs.editTable1.handleAdd(data);
  };

  handleDeleteFields1 = () => {
    this.refs.editTable1.handleDelete();
  };



  Columns = [
    {
      title: '参数',
      dataIndex: 'name',
      key: 'name',
      width: "150px",
      editable: false
    }, {
      title: '默认值',
      dataIndex: 'defaultValue',
      key: 'defaultValue',
      width: "150px",
      editable: false,
    }, {
      title: '值',
      dataIndex: 'value',
      key: 'value',
      editable: true,
    }
  ];

  Columns1 = [
    {
      title: '变量',
      dataIndex: 'name',
      key: 'name',
      width: "50%",
      editable: true
    }, {
      title: '值',
      dataIndex: 'value',
      key: 'value',
      editable: true
    }
  ];

  /*格式化表格*/
  formatTable(obj) {
    let newObj = {};
    for (let index of obj) {
      newObj[index.name] = index.value;
    }
    return newObj;
  }

  handleHide() {
    const { dispatch, form } = this.props;
    form.resetFields();
    this.setState({ newVisible: false });
    dispatch({
      type: 'runtrans/hide',
      visible: false
    });
  };

  //点击确定功能
  handleSubmit(e) {
    const { form, runtrans, dispatch } = this.props;
    const { actionName, model, viewId, selectedRows, runModel, dataSource, owner } = runtrans;

    e.preventDefault();
    form.validateFields((err, values) => {
      if (err) {
        return;
      }

      let sendFields = {};
      let sendFields1 = {};
      if (this.refs.editTable) {
        if (this.refs.editTable.state.dataSource) {
          sendFields = this.formatTable(this.refs.editTable.state.dataSource)
        }
      }

      if (this.refs.editTable1) {
        if (this.refs.editTable1.state.dataSource) {
          sendFields1 = this.formatTable(this.refs.editTable1.state.dataSource)
        }
      } else {
        sendFields1 = this.formatTable(dataSource);
      }

      values.params = sendFields;
      values.variables = sendFields1;

      delete values.readingFirstRows;
      delete values.rowCount;

      if (runModel === "batch" && selectedRows.length > 0) {
        dispatch({
          type: 'runtrans/batchRun',
          payload: {
            selectedRows: selectedRows,
            visible: false,
            owner: owner,
            configuration: {
              engineType: model,
              ...values
            }
          }
        });
      } else if (runModel === "viewRun") {
        dispatch({
          type: "controltransplatform/executeTrans",
          payload: {
            actionName: actionName,
            obj: {
              name: actionName,
              configuration: {
                engineType: model,
                ...values
              }
            }
          }
        });
        this.handleHide();
      } else {
        dispatch({
          type: "transspace/initStep"
        });

        dispatch({
          type: "transdebug/executeTrans",
          payload: {
            viewId: viewId,
            actionName: actionName,
            obj: {
              name: actionName,
              configuration: {
                engineType: model,
                ...values
              }
            }
          }
        });
        this.handleHide();
      }
    });
  };

  //预览展示
  handlePreviewShow() {
    this.setState({ newVisible: true })
  }
  //隐藏预览，恢复默认值
  handlePreviewHide() {
    const { items } = this.props.runtrans;
    this.props.form.resetFields(["readingFirstRows", "rowCount"])
    this.setState({
      newItems: items.map(index => {
        return {
          stepName: index.text, pausingOnBreakPoint: false,
          readingFirstRows: false, rowCount: 100,
          condition: null
        }
      }),
      newVisible: false
    })
  }

  //预览功能选中
  handleMenuSelect({ key }) {
    const { newItems } = this.state;
    const { setFieldsValue } = this.props.form;
    const { readingFirstRows, rowCount } = newItems.filter(index => index.stepName === key)[0];
    setFieldsValue({ readingFirstRows, rowCount })
    this.setState({
      activeKey: key
    })
  }

  //值变化是触发
  handleValueChange(value, id) {
    const { activeKey, newItems } = this.state;
    console.log(value);
    console.log(id);
    console.log(activeKey);

    this.setState({
      newItems: newItems.map(index => {
        if (index.stepName === activeKey) {
          index[id] = value;
        }
        return index;
      })
    })
  }

  //预览功能
  handlePreview(e) {
    e.preventDefault();
    const { form, runtrans, dispatch } = this.props;
    const { actionName, model, viewId, dataSource } = runtrans;
    const { newItems } = this.state;

    console.log(newItems, "新的条目");

    form.validateFields((err, values) => {
      if (err) { return; }
      let sendFields = {};
      let sendFields1 = {};
      if (this.refs.editTable) {
        if (this.refs.editTable.state.dataSource) {
          sendFields = this.formatTable(this.refs.editTable.state.dataSource)
        }
      }

      if (this.refs.editTable1) {
        if (this.refs.editTable1.state.dataSource) {
          sendFields1 = this.formatTable(this.refs.editTable1.state.dataSource)
        }
      } else {
        sendFields1 = this.formatTable(dataSource);
      }

      values.params = sendFields;
      values.variables = sendFields1;
      delete values.readingFirstRows;
      delete values.rowCount;

      dispatch({ type: "transspace/initStep" });

      dispatch({
        type: "transdebug/executeTrans",
        payload: {
          viewId: viewId,
          actionName: actionName,
          obj: {
            name: actionName,
            configuration: {
              engineType: model,
              ...values
            },
            debugExecDtos: newItems
          }
        }
      });
      this.handleHide();
    })
  }



  //engine引擎切换
  onChangeClick = (engineName) => {
    if (engineName) {
      this.setState({ engineName })
    }
    const { model } = this.props.runtrans;
    this.onDisabledControl({ model, engineName });
  }

  //执行引擎切换
  onChange = (e) => {
    const { dispatch } = this.props;
    const { engineName } = this.state;

    dispatch({
      type: 'runtrans/changeModel',
      model: e.target.value
    });
    this.onDisabledControl({ engineName, model: e.target.value })
  };


  //根据节点禁用选项
  useControl = () => {
    const { items } = this.props.runtrans;

    for (let i = 0; i < items.length; i++) {
      if (disabledArgs.includes(items[i].panel)) {
        return false;
      }
    }
    return true;
  };


  //控制checked值
  onDisabledControl = ({ engineName = "Default-Local", model = "default" } = {}) => {
    const { setFieldsValue } = this.props.form;
    if (model === "default" && engineName === "Default-Local") {
      setFieldsValue({
        rebootAutoRun: true,
        breakpointsContinue: this.useControl(),
      })
    } else {
      setFieldsValue({
        rebootAutoRun: false,
        breakpointsContinue: false,
        breakpointsRemote: false,
        forceLocal: false,
      })
    }
  }



  render() {
    const { engineName, newVisible, activeKey } = this.state;
    const { form, runtrans, cloudetlCommon } = this.props;
    const { getFieldDecorator, getFieldValue } = form;
    const { visible, model, executeList, runModel, dataSource, items } = runtrans;
    const { transEngine } = cloudetlCommon;

    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 }
    };

    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 }
    };
    const formItemLayout3 = {
      wrapperCol: { span: 24 }
    };
    const formItemLayout4 = {
      labelCol: { span: 8 },
      wrapperCol: { span: 16 }
    };

    const changeModel = () => {
      if (model === "idatrix") {
        return false
      } else {
        return (
          <FormItem label="引擎列表"  {...formItemLayout1} >
            {getFieldDecorator('engineName', {
              initialValue: engineName,
              rules: [{ required: true, message: '请选择执行引擎' }]
            })(
              <Select placeholder="请选择引擎名称" allowClear={true} onChange={this.onChangeClick}>
                {
                  executeList.map((index) =>
                    <Select.Option key={index.name} value={index.name}>{index.name}</Select.Option>
                  )
                }
              </Select>
            )}
          </FormItem>
        )
      }
    };

    //判断是能够使用trans引擎

    const useTrans = () => {
      const { items } = this.props.runtrans;
      for (let i = 0; i < items.length; i++) {
        if (transArgs.includes(items[i].panel)) {
          return false;
        }
      }
      return true;
    };


    //控制整体四个的disabled
    controlDid = model === "idatrix" || engineName !== "Default-Local";
    //通过组件控制
    let useControl = !this.useControl();

    console.log(activeKey, "激活的按钮");

    return (
      <Modal
        title={"执行转换"}
        wrapClassName="vertical-center-modal"
        visible={visible}
        onCancel={this.handleHide.bind(this)}
        width={650}
        footer={[
          <Button key="preview" type="primary" disabled={engineName !== "Default-Local" && items.length === 0} style={{ textAlign: "left" }} onClick={this.handlePreviewShow.bind(this)}>预览</Button>,
          <Button key="submit" type="primary" onClick={this.handleSubmit.bind(this)}>运行</Button>,
          <Button key="back" onClick={this.handleHide.bind(this)}>取消</Button>
        ]}
      >
        <Form >
          {
            runModel !== "batch" && runModel !== "viewRun" && transEngine === "true" && !useTrans() ? <Alert message="该转换存在本地节点，不能使用Trans引擎执行" type="warning" showIcon style={{ marginBottom: "20px" }} /> : null
          }
          <FormItem label="执行方式"  {...formItemLayout} style={{ marginBottom: "10px" }}>
            <RadioGroup defaultValue="default" value={model} onChange={this.onChange} size="default">
              <RadioButton value="default">执行引擎</RadioButton>
              <RadioButton disabled={runModel === "batch" || runModel === "viewRun" ? false : transEngine === "false" || !useTrans()} value="idatrix">Trans引擎</RadioButton>
            </RadioGroup>
          </FormItem>
          {
            changeModel()
          }

          <Row gutter={20} style={{ display: "flex", flexDirection: "row" }}>
            <Col span={2}>&nbsp;</Col>
            <Col span={8} style={{ display: "flex", flexDirection: "column" }}>
              <FormItem   {...formItemLayout3} style={{ marginBottom: "0px" }}>
                {getFieldDecorator('clearingLog', {
                  valuePropName: 'checked',
                  initialValue: true
                })(
                  <Checkbox >运行前清除日志</Checkbox>
                )}
              </FormItem>
              <FormItem {...formItemLayout3} style={{ marginBottom: "0px" }}>
                {getFieldDecorator('safeMode', {
                  valuePropName: 'checked',
                  initialValue: false
                })(
                  <Checkbox >启用安全模式</Checkbox>
                )}
              </FormItem>
              <FormItem {...formItemLayout3} style={{ marginBottom: "0px" }}>
                {getFieldDecorator('gatherMetrics', {
                  valuePropName: 'checked',
                  initialValue: true
                })(
                  <Checkbox >收集性能指标</Checkbox>
                )}
              </FormItem>
              <FormItem {...formItemLayout3} style={{ marginBottom: "0px" }}>
                {getFieldDecorator('rebootAutoRun', {
                  valuePropName: 'checked',
                  initialValue: true
                })(
                  <Checkbox disabled={controlDid} >重启服务(运行中)后自动运行</Checkbox>
                )}
              </FormItem>

              <FormItem {...formItemLayout3} style={{ marginBottom: "0px" }}>
                {getFieldDecorator('breakpointsContinue', {
                  initialValue: useControl ? false : true,
                  valuePropName: 'checked',
                })(
                  <Checkbox onChange={this.onChongeClick} disabled={controlDid || useControl}> 如果运行中断,下次从中断处恢复运行</Checkbox>
                )}
              </FormItem>
              <FormItem {...formItemLayout3} style={{ marginBottom: "0px" }}>
                {getFieldDecorator('breakpointsRemote', {
                  initialValue: false,
                  valuePropName: 'checked',

                })(
                  <Checkbox disabled={controlDid || useControl || !getFieldValue("breakpointsContinue")}> 是否远程自动从中断处恢复运行</Checkbox>
                )}
              </FormItem>
              <FormItem {...formItemLayout3} style={{ marginBottom: "0px" }}>
                {getFieldDecorator('forceLocal', {
                  initialValue: false,
                  valuePropName: 'checked',
                })(
                  <Checkbox disabled={controlDid || useControl || !getFieldValue("breakpointsContinue")}> 当远程正在执行，强制切换本本地(终止远程运行)</Checkbox>
                )}
              </FormItem>
            </Col>

            <Col span={10} style={{ display: "flex", alignItems: "center" }}>
              <FormItem label="日志级别" {...formItemLayout4} style={{ width: "100%" }}>
                {getFieldDecorator('logLevel', {
                  initialValue: "Basic"
                })(
                  <Select  >
                    <Option value="Nothing">没有日志</Option>
                    <Option value="Error">错误日志</Option>
                    <Option value="Minimal">最小日志</Option>
                    <Option value="Basic">基本日志</Option>
                    <Option value="Detailed">详细日志</Option>
                    <Option value="Debug">调试</Option>
                    <Option value="Rowlevel">行级日志(非常详细)</Option>
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          {
            runModel !== "batch" ? (<Tabs style={{ margin: "20px 8% 0  8%" }} type="card">
              <TabPane tab="参数" key="1">
                <Row style={{ margin: "5px 0", width: "100%" }}  >
                  <Col span={12}>&nbsp;</Col>
                  <Col span={12} style={{ textAlign: "right" }}>
                    <Button size={"small"} onClick={this.handleDeleteFields.bind(this)}>删除字段</Button>
                  </Col>
                </Row>
                <EditTable initFuc={this.initFuc.bind(this)} extendDisabled={true} rowSelection={true} columns={this.Columns} dataSource={[]} tableStyle="editTableStyle5" size={"small"} scroll={{ y: 300 }} ref="editTable" count={0} />
              </TabPane>
              <TabPane tab="环境变量" key="2">
                <Row style={{ margin: "5px 0", width: "100%" }}  >
                  <Col span={12}>
                    <ButtonGroup size={"small"} >
                      <Button onClick={this.handleAdd1}>添加字段</Button>
                    </ButtonGroup>
                  </Col>
                  <Col span={12} style={{ textAlign: "right" }}>
                    <Button size={"small"} onClick={this.handleDeleteFields1.bind(this)}>删除字段</Button>
                  </Col>
                </Row>
                <EditTable extendDisabled={true} rowSelection={true} columns={this.Columns1} dataSource={dataSource} tableStyle="editTableStyle5" size={"small"} scroll={{ y: 300 }} ref="editTable1" count={0} />
              </TabPane>
            </Tabs>) : null
          }
          <Modal
            title="预览"
            visible={newVisible}
            onOk={this.handlePreview.bind(this)}
            onCancel={this.handlePreviewHide.bind(this)}
            zIndex={1020}
          >
            <Row gutter={16}>
              <Col span={12} >
                <div style={{ height: "300px", border: "1px solid #ccc", overflow: "auto" }} >
                  <Menu
                    selectedKeys={[activeKey]}
                    mode="inline"
                    onSelect={this.handleMenuSelect.bind(this)}
                  >
                    {items.map((item) => <Menu.Item key={item.text}> <span>{item.text}</span></Menu.Item>)}
                  </Menu>
                </div>
              </Col>
              <Col span={12} >
                <FormItem label="要获得的行数" {...formItemLayout3} style={{ marginBottom: "8px" }}>
                  {getFieldDecorator('rowCount', {
                    initialValue: 100,
                  })(
                    <Input onChange={(e) => { this.handleValueChange(e.target.value, "rowCount") }} />
                  )}
                </FormItem>

                <FormItem label="要获得的行数(预览)" {...formItemLayout3} style={{ marginBottom: "8px" }}>
                  {getFieldDecorator('readingFirstRows', {
                    valuePropName: 'checked',
                    initialValue: true,
                  })(
                    <Checkbox onChange={(e) => { this.handleValueChange(e.target.checked, "readingFirstRows") }} />
                  )}
                </FormItem>
              </Col>
            </Row>
          </Modal>
        </Form >
      </Modal>
    )
  }
}

const RunTrans = Form.create()(Index);

export default connect(({ runtrans, cloudetlCommon }) => ({
  runtrans, cloudetlCommon
}))(RunTrans)

