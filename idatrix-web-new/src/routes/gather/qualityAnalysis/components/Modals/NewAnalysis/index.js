/**
 * Created by Administrator on 2018/1/22.
 */
import React from 'react'
import { Button, Form, Input, Row, Col, Select, message } from 'antd';
import Modal from "components/Modal.js";
import { connect } from 'dva'
import EditTable from '../../../../components/common/EditTable';
import { checkName } from 'services/quality'

const { TextArea } = Input;
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
const SelectOption = Select.Option;

let Timer;

class Index extends React.Component {

    constructor() {
        super();
        this.checkName = this.checkName.bind(this);
        this.handleSure = this.handleSure.bind(this);
        this.setModelHide = this.setModelHide.bind(this);
        this.initFuc = this.initFuc.bind(this);
        this.handleDeleteFields1 = this.handleDeleteFields1.bind(this);
        this.handleAdd1 = this.handleAdd1.bind(this);
    }

    //检查分析名字是否重复
    checkName(rule, value, callback) {
        const { name } = this.props.newAnalysis;
        if (value && name != value) {
            if (Timer) {
                clearTimeout(Timer);
                Timer = null;
            }
            Timer = setTimeout(() => {
                checkName(value).then((res) => {
                    const { code, data } = res.data;
                    if (code === "200") {
                        const { result } = data;
                        if (result === true) {
                            callback(true)
                        } else {
                            callback()
                        }
                    }
                });
            }, 300);
        } else {
            callback()
        }
    };

    //布局
    formItemLayout = {
        labelCol: { span: 6 },
        wrapperCol: { span: 14 }
    };

    setModelHide() {
        const { form, dispatch } = this.props;
        form.resetFields();
        dispatch({
            type: "newAnalysis/reset"
        })
    };

    /*格式化表格*/
    formatTable(obj) {
        let newObj = {};
        for (let index of obj) {
            newObj[index.name] = index.value;
        }
        return newObj;
    }

    handleSure() {
        const { form, dispatch } = this.props;
        const { actionType, params } = this.props.newAnalysis;

        form.validateFields((err, values) => {
            if (err) return;

            if (actionType === "edit") {
                let sendFields = {};
                if (this.refs.editTable) {
                    if (this.refs.editTable.state.dataSource.length > 0) {
                        sendFields = this.formatTable(this.refs.editTable.state.dataSource)
                    }
                } else {
                    if (fields) {
                        sendFields = params;
                    }
                }
                values.params = sendFields;
            }

            //新建分析
            if ( actionType === "new") {
                dispatch({
                    type: "newAnalysis/newAnalysis",
                    payload: { ...values }
                });
            } else if ( actionType === "edit") {
                dispatch({
                    type: "newAnalysis/saveTransAttributes",
                    payload: { ...values }
                })
            }
        })
    };

    Columns1 = [
        {
            title: '命名参数',
            dataIndex: 'name',
            key: 'name',
            width: "45%",
            editable: true
        }, {
            title: '默认值',
            dataIndex: 'value',
            key: 'value',
            editable: true,
        }
    ];

    componentWillReceiveProps(nextProps) {
        const { visible,params } = nextProps.newAnalysis;
        const { form } = this.props;
        if (visible === true) {
            this.updateTable(params);
        }else{
            form.resetFields();
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
                        value: params[index]
                    })
                }
            }
            this.refs.editTable.updateTable(args, i);
        }
    };

    initFuc(that) {
        const { params } = this.props.newAnalysis;
        let args = [];
        let i = 0;

        if (params) {
            for (let index of Object.keys(params)) {
                args.push({
                    key: i++,
                    name: index,
                    value: params[index]
                })
            }
        }
        that.updateTable(args, i);
    };

    handleAdd1() {
        const data = {
            "name": "",
            "value": ""
        };
        this.refs.editTable.handleAdd(data);
    };
    handleDeleteFields1() {
        this.refs.editTable.handleDelete();
    };

    render() {
        const { getFieldDecorator } = this.props.form;
        const { visible, name, description, actionType, nameArgs } = this.props.newAnalysis;

        return (
            <Modal
                visible={visible}
                title={actionType === "new" ? "新建分析" : "编辑分析"}
                wrapClassName="vertical-center-modal"
                footer={[
                    <Button key="submit" type="primary" size="large" onClick={this.handleSure}>
                        确定
                  </Button>,
                    <Button key="back" size="large" onClick={this.setModelHide}>取消</Button>,
                ]}
                onCancel={this.setModelHide}
            >
                <Form >
                    <FormItem label="分析名称"  {...this.formItemLayout} >
                        {getFieldDecorator('name', {
                            initialValue: name,
                            rules: [{ whitespace: true, required: true, message: '请输入名称' },
                            { validator: this.checkName, message: '名称已存在，请更改!' }
                            ]
                        })(
                            <Input />
                        )}
                    </FormItem>
                    {
                        actionType === "new" ? <FormItem label="复制分析名称"  {...this.formItemLayout} >
                            {getFieldDecorator('newName', {
                                initialValue: "",
                            })(
                                <Select allowClear>
                                    {
                                        nameArgs.map(index => (<SelectOption key={index} value={index}>{index}</SelectOption>))
                                    }
                                </Select>
                            )}
                        </FormItem> : null
                    }
                    <FormItem label="描述"  {...this.formItemLayout}>
                        {getFieldDecorator('description', {
                            initialValue: description
                        })(
                            <TextArea />
                        )}
                    </FormItem>
                    {
                        actionType !== "new" ?
                            <div style={{ margin: "0 5%" }}>
                                <Row style={{ marginBottom: "5px" }}>
                                    <Col span={12}>
                                        <p style={{ marginLeft: "5px" }}>命名参数：</p>
                                    </Col>
                                    <Col span={12}>
                                        <ButtonGroup size={"small"} style={{ float: "right" }} >
                                            <Button onClick={this.handleAdd1}>添加字段</Button>
                                            <Button onClick={this.handleDeleteFields1}>删除字段</Button>
                                        </ButtonGroup>
                                    </Col>
                                </Row>
                                <EditTable initFuc={this.initFuc} extendDisabled={true} rowSelection={true} columns={this.Columns1} dataSource={[]} size={"small"} scroll={{ y: 240 }} ref="editTable" count={0} />
                            </div> : null
                    }
                </Form>
            </Modal>
        )
    }
}


const TaskConfig = Form.create()(Index);

export default connect(({ newAnalysis }) => ({
    newAnalysis
}))(TaskConfig)
