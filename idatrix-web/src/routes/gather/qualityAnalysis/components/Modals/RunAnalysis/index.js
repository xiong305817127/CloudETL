/**
 * 执行质量分析/批量执行质量分析
 * @author pwj 2018/09/28
 */

import React from 'react'
import { connect } from 'dva'
import { Form, Radio, Select, Checkbox, Row, Col, Button, Alert, Tabs } from 'antd'
import { transArgs, disabledArgs } from '../../../constant';
import EditTable from '../../../../components/common/EditTable';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;
const ButtonGroup = Button.Group;
const Option = Select.Option;

//控制四个选项的disabled

let controlDid = false;


class Index extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            engineName: "Default-Local"   //记录引擎的值
        }
        this.handleHide = this.handleHide.bind(this);
        this.handleDeleteFields = this.handleDeleteFields.bind(this);
        this.handleDeleteFields1 = this.handleDeleteFields1.bind(this);
        this.initFuc = this.initFuc.bind(this)
    }

    componentWillReceiveProps(nextProps) {
        const { visible, params } = nextProps.runAnalysis;
        if (visible === true) {
            this.updateTable(params);
        }
    }

    //根据参数的变化，更新表格
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

    //表格初始化渲染数据
    initFuc(that) {
        const { params } = this.props.runAnalysis;
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

    //删除表格字段
    handleDeleteFields = () => {
        this.refs.editTable.handleDelete();
    };

    //表格增加字段
    handleAdd1 = () => {
        const data = { name: "", value: "" };
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
        dispatch({ type: 'runAnalysis/save', payload: { visible: false } });
    };

    handleSubmit(e) {
        e.preventDefault();
        const { form, runAnalysis, dispatch } = this.props;
        const { actionName, model, runType, dataSource } = runAnalysis;

        form.validateFields((err, values) => {
            if (err) {
                return;
            }
            let params = {};
            let variables = {};
            if (this.refs.editTable) {
                if (this.refs.editTable.state.dataSource.length > 0) {
                    params = this.formatTable(this.refs.editTable.state.dataSource)
                }
            }
            if (this.refs.editTable1) {
                if (this.refs.editTable1.state.dataSource.length > 0) {
                    variables = this.formatTable(this.refs.editTable1.state.dataSource)
                }
            } else {
                variables = this.formatTable(dataSource);
            }
            if (runType === "batch") {
                dispatch({
                    type: 'runAnalysis/batchRun',
                    payload: {
                        engineType: model,
                        ...values, params, variables
                    }
                });
            } else {
                dispatch({
                    type: "runAnalysis/runAnalysis",
                    payload: {
                        name: actionName,
                        configuration: {
                            engineType: model,
                            ...values
                        }
                    }
                });
            }
        });
    };



    //engine引擎切换
    onChangeClick = (engineName) => {
        if (engineName) {
            this.setState({ engineName })
        }
        const { model } = this.props.runAnalysis;
        this.onDisabledControl({ model, engineName });
    }

    //执行引擎切换
    onChange = (e) => {
        const { dispatch } = this.props;
        const { engineName } = this.state;
        const model = e.target.value;
        dispatch({
            type: 'runAnalysis/save',
            payload: { model }
        });
        this.onDisabledControl({ engineName, model })
    };


    //根据节点禁用选项
    useControl = () => {
        const { items } = this.props.runAnalysis;
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
        const { engineName } = this.state;

        const { form, runAnalysis, cloudetlCommon } = this.props;
        const { getFieldDecorator, getFieldValue } = form;
        const { visible, model, executeList, runType, dataSource } = runAnalysis;
        const { transEngine } = cloudetlCommon;

        const formItemLayout = {
            labelCol: { span: 6 },
            wrapperCol: { span: 14 }
        };

        const formItemLayout1 = {
            labelCol: { span: 6 },
            wrapperCol: { span: 14 }
        };
        const formItemLayout2 = {
            wrapperCol: { span: 24 }
        };
        const formItemLayout3 = {
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
                            rules: [{ required: true, message: '请选择分析引擎' }]
                        })(
                            <Select placeholder="请选择分析引擎名称" allowClear={true} onChange={this.onChangeClick}>
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
            const { items } = this.props.runAnalysis;
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

        return (
            <Modal
                title={"执行转换"}
                wrapClassName="vertical-center-modal"
                visible={visible}
                onCancel={this.handleHide}
                width={650}
                footer={[
                    <Button key="submit" type="primary" size="large" onClick={this.handleSubmit.bind(this)}>运行</Button>,
                    <Button key="back" size="large" onClick={this.handleHide}>取消</Button>
                ]}
            >
                <Form >
                    {
                        runType !== "batch" && transEngine === "true" && !useTrans() ? <Alert message="该转换存在本地节点，不能使用Trans引擎执行" type="warning" showIcon style={{ marginBottom: "20px" }} /> : null
                    }
                    <FormItem label="执行方式"  {...formItemLayout} style={{ marginBottom: "10px" }}>
                        <RadioGroup defaultValue="default" value={model} onChange={this.onChange} size="default">
                            <RadioButton value="default">执行引擎</RadioButton>
                            <RadioButton disabled={true} value="idatrix">Trans引擎</RadioButton>
                        </RadioGroup>
                        {/* disabled={runType === "batch" ? false : transEngine === "false" || !useTrans()} */}
                    </FormItem>
                    {
                        changeModel()
                    }

                    <Row gutter={20} style={{ display: "flex", flexDirection: "row" }}>
                        <Col span={1} ></Col>
                        <Col span={12} style={{ display: "flex", flexDirection: "column" }}>
                            <FormItem   {...formItemLayout2} style={{ marginBottom: "0px" }}>
                                {getFieldDecorator('clearingLog', {
                                    valuePropName: 'checked',
                                    initialValue: true
                                })(
                                    <Checkbox >运行前清除日志</Checkbox>
                                )}
                            </FormItem>
                            <FormItem {...formItemLayout2} style={{ marginBottom: "0px" }}>
                                {getFieldDecorator('safeMode', {
                                    valuePropName: 'checked',
                                    initialValue: false
                                })(
                                    <Checkbox >启用安全模式</Checkbox>
                                )}
                            </FormItem>
                            <FormItem {...formItemLayout2} style={{ marginBottom: "0px" }}>
                                {getFieldDecorator('gatherMetrics', {
                                    valuePropName: 'checked',
                                    initialValue: true
                                })(
                                    <Checkbox >收集性能指标</Checkbox>
                                )}
                            </FormItem>
                            <FormItem {...formItemLayout2} style={{ marginBottom: "0px" }}>
                                {getFieldDecorator('rebootAutoRun', {
                                    valuePropName: 'checked',
                                    initialValue: true
                                })(
                                    <Checkbox disabled={controlDid} >重启服务(运行中)后自动运行</Checkbox>
                                )}
                            </FormItem>

                            <FormItem {...formItemLayout2} style={{ marginBottom: "0px" }}>
                                {getFieldDecorator('breakpointsContinue', {
                                    initialValue: useControl ? false : true,
                                    valuePropName: 'checked',
                                })(
                                    <Checkbox onChange={this.onChongeClick} disabled={controlDid || useControl}> 如果运行中断,下次从中断处恢复运行</Checkbox>
                                )}
                            </FormItem>
                            <FormItem {...formItemLayout2} style={{ marginBottom: "0px" }}>
                                {getFieldDecorator('breakpointsRemote', {
                                    initialValue: false,
                                    valuePropName: 'checked',

                                })(
                                    <Checkbox disabled={controlDid || useControl || !getFieldValue("breakpointsContinue")}> 是否远程自动从中断处恢复运行</Checkbox>
                                )}
                            </FormItem>
                            <FormItem {...formItemLayout2} style={{ marginBottom: "0px" }}>
                                {getFieldDecorator('forceLocal', {
                                    initialValue: false,
                                    valuePropName: 'checked',
                                })(
                                    <Checkbox disabled={controlDid || useControl || !getFieldValue("breakpointsContinue")}> 当远程正在执行，强制切换本本地(终止远程运行)</Checkbox>
                                )}
                            </FormItem>
                        </Col>

                        <Col span={10} style={{ display: "flex", alignItems: "center" }}>
                            <FormItem label="日志级别" {...formItemLayout3} style={{ width: "100%" }}>
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
                        runType !== "batch" ? (
                            <Tabs style={{ margin: "20px 8% 0  8%" }} type="card">
                                <TabPane tab="参数" key="1">
                                    <Row style={{ margin: "5px 0", width: "100%" }}  >
                                        <Col span={12}>&nbsp;</Col>
                                        <Col span={12} style={{ textAlign: "right" }}>
                                            <Button size={"small"} onClick={this.handleDeleteFields}>删除字段</Button>
                                        </Col>
                                    </Row>
                                    <EditTable initFuc={this.initFuc} extendDisabled={true} rowSelection={true} columns={this.Columns} dataSource={[]} tableStyle="editTableStyle5" size={"small"} scroll={{ y: 300 }} ref="editTable" count={0} />
                                </TabPane>
                                <TabPane tab="环境变量" key="2">
                                    <Row style={{ margin: "5px 0", width: "100%" }}  >
                                        <Col span={12}>
                                            <ButtonGroup size={"small"} >
                                                <Button onClick={this.handleAdd1}>添加字段</Button>
                                            </ButtonGroup>
                                        </Col>
                                        <Col span={12} style={{ textAlign: "right" }}>
                                            <Button size={"small"} onClick={this.handleDeleteFields1}>删除字段</Button>
                                        </Col>
                                    </Row>
                                    <EditTable extendDisabled={true} rowSelection={true} columns={this.Columns1} dataSource={dataSource} tableStyle="editTableStyle5" size={"small"} scroll={{ y: 200 }} ref="editTable1" count={0} />
                                </TabPane>
                            </Tabs>
                        ):null
                    }
                </Form >
            </Modal>
        )
    }
}

export default connect(({ runAnalysis, cloudetlCommon }) => ({
    runAnalysis, cloudetlCommon
}))(Form.create()(Index))

