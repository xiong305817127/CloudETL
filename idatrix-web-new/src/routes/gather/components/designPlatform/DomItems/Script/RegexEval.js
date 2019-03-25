/**
 * Created by Administrator on 2017/3/13.
 */
import React from 'react';
import { Button, Form, Input, Checkbox, Row, Col, Select, Tabs, Card, Alert,List } from 'antd';
import Modal from "components/Modal.js";
import { connect } from 'dva'
import EditTable from '../../../common/EditTable';
import AceEditor from 'react-ace';
import brace from 'brace';

import 'brace/mode/java';
import 'brace/theme/github';
import 'brace/ext/language_tools';


const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const ButtonGroup = Button.Group;

//页面验证值定时器 
let Timer = null;

class Regex extends React.Component {

    constructor(props) {
        super(props);

        this.handleTestVisible = this.handleTestVisible.bind(this);
        this.handleDeleteFields = this.handleDeleteFields.bind(this);
        this.onTextAreaChange = this.onTextAreaChange.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
        this.handleValueChange = this.handleValueChange.bind(this);

        const { visible } = props.model;
        if (visible === true) {
            const { fieldNames, script } = props.model.config;
            let data = [];
            let count = 0;
            if (fieldNames) {
                for (let index of fieldNames) {
                    data.push({
                        key: count,
                        ...index
                    });
                    count++;
                }
            }
            this.state = {
                dataSource: data,
                InputData: [],
                //正则表达式
                script: script ? decodeURIComponent(script) : "",
                //测试正则表达式
                testVisible: false,
                //提示信息
                msgInfo: "",
                //信息状态  true 正确 false 语法错误
                msgStatus: true,
                //分组
                value4Group: []
            }
        }
    }

    componentDidMount() {
        //获取输入字段
        this.Request();
    }

    Request() {
        const { getInputFields, transname, text } = this.props.model;
        let obj = {};
        obj.transname = transname;
        obj.stepname = text;
        getInputFields(obj, data => {
            this.setState({
                InputData: data
            })
        });
    };

    setModelHide() {
        const { dispatch } = this.props;
        dispatch({
            type: 'items/hide',
            visible: false
        });
    };

    handleAdd = () => {
        this.refs.editTable.handleAdd({
            "fieldName": "", "fieldType": "", "fieldFormat": "", "fieldGroup": "", "fieldDecimal": "", "fieldLength": "", "fieldPrecision": "",
            "fieldCurrency": "", "fieldNullIf": "", "fieldIfNull": "", "fieldTrimType": ""
        });
    };
    handleDeleteFields = () => {
        this.refs.editTable.handleDelete();
    };

    handleFormSubmit() {
        const form = this.props.form;
        const { panel, transname, description, key, saveStep, text, config, formatTable } = this.props.model;
        const { fieldNames } = config;
        const { script } = this.state;

        form.validateFields((err, values) => {
            if (err) {
                return;
            }
            let sendFields = [];
            if (this.refs.editTable) {
                if (this.refs.editTable.state.dataSource.length > 0) {
                    let arg = ["fieldName", "fieldType", "fieldFormat", "fieldGroup", "fieldDecimal", "fieldLength", "fieldPrecision",
                        "fieldCurrency", "fieldNullIf", "fieldIfNull", "fieldTrimType"
                    ];
                    sendFields = formatTable(this.refs.editTable.state.dataSource, arg)
                }
            } else {
                if (fieldNames) {
                    sendFields = fieldNames;
                }
            }

            delete values.value1;
            delete values.value2;
            delete values.value3;
            delete values.value4;

            let obj = {};
            obj.transname = transname;
            obj.newname = (text === values.text ? "" : values.text);
            obj.stepname = text;
            obj.type = panel;
            obj.description = description;
            obj.config = {
                ...values,
                script: script?encodeURIComponent(script):"",
                "fieldNames": sendFields
            }
            saveStep(obj, key, data => {
                if (data.code === "200") {
                    this.setModelHide();
                }
            });
        })
    }

    columns = [{
        title: '新字段',
        dataIndex: 'fieldName',
        key: 'fieldName',
        width: "15%",
        editable: true,
    }, {
        title: '类型',
        dataIndex: 'fieldType',
        key: 'fieldType',
        width: "12%",
        selectable: true,
        selectArgs: [
            <Select.Option key="None" value="None">None</Select.Option>,
            <Select.Option key="Number" value="Number">Number</Select.Option>,
            <Select.Option key="String" value="String">String</Select.Option>,
            <Select.Option key="Date" value="Date">Date</Select.Option>,
            <Select.Option key="Boolean" value="Boolean">Boolean</Select.Option>,
            <Select.Option key="Integer" value="Integer">Integer</Select.Option>,
            <Select.Option key="BigNumber" value="BigNumber">BigNumber</Select.Option>,
            <Select.Option key="Serializable" value="Serializable">Serializable</Select.Option>,
            <Select.Option key="Binary" value="Binary">Binary</Select.Option>,
            <Select.Option key="Timestamp" value="Timestamp">Timestamp</Select.Option>,
            <Select.Option key="Internet Address" value="Internet Address">Internet Address</Select.Option>
        ]
    }, {
        title: '长度',
        dataIndex: 'fieldLength',
        key: 'fieldLength',
        width: "6%",
        editable: true,
    }, {
        title: '精度',
        dataIndex: 'fieldPrecision',
        width: "6%",
        key: 'fieldPrecision',
        editable: true,
    }, {
        title: '格式',
        dataIndex: 'fieldFormat',
        width: "6%",
        key: 'fieldFormat',
        editable: true,
    }, {
        title: '分组符号',
        dataIndex: 'fieldGroup',
        width: "8%",
        key: 'fieldGroup',
        editable: true,
    }, {
        title: '十进制',
        dataIndex: 'fieldDecimal',
        width: "6%",
        key: 'fieldDecimal',
        editable: true,
    }, {
        title: '货币',
        dataIndex: 'fieldCurrency',
        width: "6%",
        key: 'fieldCurrency',
        editable: true,
    }, {
        title: "如果...则置为空",
        dataIndex: 'fieldIfNull',
        key: 'fieldIfNull',
        width: "12%",
        editable: true,
    }, {
        title: "默认",
        dataIndex: 'fieldNullIf',
        key: 'fieldNullIf',
        width: "6%",
        editable: true,
    }, {
        title: "删除空格",
        dataIndex: 'fieldTrimType',
        key: 'fieldTrimType',
        selectable: true,
        selectArgs: [<Select.Option key="none" value="0">不去掉空格</Select.Option>,
        <Select.Option key="left" value="1">去掉左空格</Select.Option>,
        <Select.Option key="right" value="2">去掉右空格</Select.Option>,
        <Select.Option key="both" value="3">去掉左右两边空格</Select.Option>
        ]
    }
    ];

    //更新正则输入框的值
    onTextAreaChange(value) {
        this.setState({
            script: value
        });
        this.handleValueChange();
    }

    //打开正则测试框
    handleTestVisible() {
        this.setState({
            testVisible: true
        })
    }

    //关闭测试框
    handleCancel() {
        this.setState({
            testVisible: false
        })
    }

    //测试值
    handleValueChange() {

        const { getDetails, transname, text, panel } = this.props.model;
        const { getFieldsValue, setFields, getFieldValue } = this.props.form;
        const { script } = this.state;

        if (Timer) {
            clearTimeout(Timer);
            Timer = null;
        }
        Timer = setTimeout(() => {
            getDetails({
                transName: transname,
                stepName: text,
                detailType: panel,
                detailParam: {
                    ...getFieldsValue(["canoneq", "caseinsensitive", "comment", "dotall", "multiline", "unicode", "unix", "value1",
                        "value2", "value3", "value4"]),
                    flag: "testRegex", script: encodeURIComponent(script)
                }
            }, data => {
                const { value1Result, value2Result, value3Result, value4Result, value4Group, compileMessage, compileResult } = data;
                this.setState({ msgInfo: compileMessage, msgStatus: compileResult, value4Group: value4Group ? value4Group : [] })

                setFields({
                    value1: { value: getFieldValue("value1"), errors: value1Result ? null : [new Error(" ")] },
                    value2: { value: getFieldValue("value2"), errors: value2Result ? null : [new Error(" ")] },
                    value3: { value: getFieldValue("value3"), errors: value3Result ? null : [new Error(" ")] },
                    value4: { value: getFieldValue("value4"), errors: value4Result ? null : [new Error(" ")] }
                })
            })
        }, 300)
    }

    render() {
        const { visible, config, text, handleCheckName } = this.props.model;
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { testVisible, script, msgInfo, msgStatus, value4Group } = this.state;

        const formItemLayout = {
            labelCol: { span: 6 },
            wrapperCol: { span: 14 },
        };

        const setDisabled = () => {
            if (getFieldValue("allowcapturegroups") === undefined) {
                return config.allowcapturegroups;
            } else {
                if (getFieldValue("allowcapturegroups")) {
                    return getFieldValue("allowcapturegroups");
                } else {
                    return false;
                }
            }
        };

        return (
            <Modal
                visible={visible}
                title="正则表达式"
                wrapClassName="vertical-center-modal"
                width={750}
                maskClosable={false}
                footer={[
                    <Button key="submit" type="primary" size="large" onClick={() => { this.handleFormSubmit() }}>
                        确定
                  </Button>,
                    <Button key="back" size="large" onClick={() => { this.setModelHide(); }}>取消</Button>,
                ]}
                onCancel={() => { this.setModelHide() }}
            >
                <Form >
                    <FormItem label="步骤名称"  {...formItemLayout}>
                        {getFieldDecorator('text', {
                            initialValue: text,
                            rules: [{ whitespace: true, required: true, message: '请输入步骤名称' },
                            { validator: handleCheckName, message: '步骤名称已存在，请更改!' }]
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <Tabs type="card">
                        <TabPane tab="步骤设置" key="setting">
                            <FormItem label="要匹配的字段"  {...formItemLayout} style={{ marginBottom: "8px" }}>
                                {getFieldDecorator('matcher', {
                                    initialValue: config.matcher
                                })(
                                    <Select>
                                        {
                                            this.state.InputData.map((index) =>
                                                <Select.Option key={index.name} value={index.name}>{index.name}</Select.Option>
                                            )
                                        }
                                    </Select>
                                )}
                            </FormItem>
                            <FormItem label="结果字段名"  {...formItemLayout} style={{ marginBottom: "8px" }}>
                                {getFieldDecorator('resultfieldname', {
                                    initialValue: config.resultfieldname
                                })(
                                    <Input />
                                )}
                            </FormItem>
                            <Row style={{ marginLeft: "10%" }}>
                                <Col span={10}>
                                    <FormItem style={{ marginBottom: "0px" }} {...formItemLayout}>
                                        {getFieldDecorator('allowcapturegroups', {
                                            valuePropName: 'checked',
                                            initialValue: config.allowcapturegroups
                                        })(
                                            <Checkbox>为每个捕获组创建一个字段</Checkbox>
                                        )}
                                    </FormItem>
                                </Col>
                                <Col span={7}>
                                    <FormItem style={{ marginBottom: "0px" }} {...formItemLayout}>
                                        {getFieldDecorator('replacefields', {
                                            valuePropName: 'checked',
                                            initialValue: config.replacefields
                                        })(
                                            <Checkbox disabled={!setDisabled()} >替换先前字段</Checkbox>
                                        )}
                                    </FormItem>
                                </Col>
                                <Col span={7}>
                                    <FormItem style={{ marginBottom: "0px" }} {...formItemLayout}>
                                        {getFieldDecorator('usevar', {
                                            valuePropName: 'checked',
                                            initialValue: config.usevar
                                        })(
                                            <Checkbox>变量替换</Checkbox>
                                        )}
                                    </FormItem>
                                </Col>
                            </Row>
                            <Row style={{ padding: "0px 35px 10px" }}>
                                <Col span={12}>正则表达式：</Col>
                                <Col span={12} style={{ textAlign: "right" }} >
                                    <Button size={"small"} onClick={this.handleTestVisible}  >测试正则表达式</Button>
                                </Col>
                            </Row>
                            <div style={{ padding: "0px 35px" }}>
                                <AceEditor
                                    mode="java"
                                    theme="github"
                                    onChange={this.onTextAreaChange}
                                    name="gather_tableInput"
                                    className="autoTextArea"
                                    showGutter={true}
                                    width={"100%"}
                                    height={"100px"}
                                    fontSize={16}
                                    editorProps={{ $blockScrolling: true }}
                                    value={decodeURIComponent(script)}
                                    wrapEnabled={true}
                                    setOptions={{
                                        enableBasicAutocompletion: true,
                                        enableLiveAutocompletion: true,
                                        enableSnippets: false,
                                        showLineNumbers: true,
                                        tabSize: 2
                                    }}
                                />
                            </div>
                        </TabPane>
                        <TabPane tab="内容" key="Content">
                            <Card title="正则设置" style={{ margin: "0px 35px" }} >
                                <Row style={{ marginLeft: "10%" }}>
                                    <Col span={12}>
                                        <FormItem style={{ marginBottom: "0px" }} {...formItemLayout}>
                                            {getFieldDecorator('canoneq', {
                                                valuePropName: 'checked',
                                                initialValue: config.canoneq
                                            })(
                                                <Checkbox>正规分解匹配</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                    <Col span={12}>
                                        <FormItem style={{ marginBottom: "0px" }} {...formItemLayout}>
                                            {getFieldDecorator('caseinsensitive', {
                                                valuePropName: 'checked',
                                                initialValue: config.caseinsensitive
                                            })(
                                                <Checkbox>忽略大小写</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                </Row>
                                <Row style={{ marginLeft: "10%" }}>
                                    <Col span={12}>
                                        <FormItem style={{ marginBottom: "0px" }} {...formItemLayout}>
                                            {getFieldDecorator('comment', {
                                                valuePropName: 'checked',
                                                initialValue: config.comment
                                            })(
                                                <Checkbox>在表达式中允许有空格和注释</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                    <Col span={12}>
                                        <FormItem style={{ marginBottom: "0px" }} {...formItemLayout}>
                                            {getFieldDecorator('dotall', {
                                                valuePropName: 'checked',
                                                initialValue: config.dotall
                                            })(
                                                <Checkbox>点字符(.)全部匹配模式</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                </Row>
                                <Row style={{ marginLeft: "10%" }}>
                                    <Col span={12}>
                                        <FormItem style={{ marginBottom: "0px" }} {...formItemLayout}>
                                            {getFieldDecorator('multiline', {
                                                valuePropName: 'checked',
                                                initialValue: config.multiline
                                            })(
                                                <Checkbox>启用多行模式</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                    <Col span={12}>
                                        <FormItem style={{ marginBottom: "0px" }} {...formItemLayout}>
                                            {getFieldDecorator('unicode', {
                                                valuePropName: 'checked',
                                                initialValue: config.unicode
                                            })(
                                                <Checkbox>Unicode 忽略大小写</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                </Row>
                                <Row style={{ marginLeft: "10%" }}>
                                    <Col span={12}>
                                        <FormItem style={{ marginBottom: "0px" }} {...formItemLayout}>
                                            {getFieldDecorator('unix', {
                                                valuePropName: 'checked',
                                                initialValue: config.unix
                                            })(
                                                <Checkbox>Unix 行模式</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                </Row>
                            </Card>
                        </TabPane>
                    </Tabs>
                    <div style={{ margin: "10px 35px 15px" }}>
                        <Row style={{ margin: "5px 0", width: "100%" }}  >
                            <Col span={12}>
                                捕获组字段
                            </Col>
                            <Col span={12} style={{ textAlign: "right" }}>
                                <ButtonGroup size={"small"}  >
                                    <Button onClick={this.handleAdd} disabled={!setDisabled()} >添加字段</Button>
                                    <Button onClick={this.handleDeleteFields} disabled={!setDisabled()} >删除字段</Button>
                                </ButtonGroup>
                            </Col>
                        </Row>
                        <EditTable columns={this.columns} dataSource={this.state.dataSource} rowSelection={true} disabled={!setDisabled()} size={"small"} scroll={{ y: 140, x: 1000 }} ref="editTable" count={4} />
                    </div>

                    <Modal
                        title="正则表达式测试"
                        visible={testVisible}
                        zIndex={1030}
                        width={700}
                        onCancel={this.handleCancel}
                        onOk={ this.handleCancel }
                    >
                        <AceEditor
                            mode="java"
                            theme="github"
                            onChange={this.onTextAreaChange}
                            name="gather_tableInput"
                            className="autoTextArea"
                            showGutter={true}
                            width={"100%"}
                            height={"100px"}
                            fontSize={16}
                            editorProps={{ $blockScrolling: true }}
                            value={decodeURIComponent(script)}
                            wrapEnabled={true}
                            setOptions={{
                                enableBasicAutocompletion: true,
                                enableLiveAutocompletion: true,
                                enableSnippets: false,
                                showLineNumbers: true,
                                tabSize: 2
                            }}
                        />
                        <div style={{ marginTop: "10px" }} >
                            {
                                msgInfo ? (
                                    <div>
                                        <p>语法提示：</p>
                                        <Alert message={msgInfo} type={msgStatus ? "info" : "error"} />
                                    </div>
                                ) : null
                            }
                        </div>
                        <div style={{ marginTop: "10px" }} >
                            <p>测试值：</p>
                            <FormItem label={'测试值1'} style={{ marginBottom: "0px" }} {...formItemLayout}>
                                {getFieldDecorator('value1', {
                                    initialValue: "",
                                })(
                                    <Input onChange={this.handleValueChange} />
                                )}
                            </FormItem>
                            <FormItem label={'测试值2'} style={{ marginBottom: "0px" }} {...formItemLayout}>
                                {getFieldDecorator('value2', {
                                    initialValue: "",

                                })(
                                    <Input onChange={this.handleValueChange} />
                                )}
                            </FormItem>
                            <FormItem label={'测试值3'}
                                style={{ marginBottom: "0px" }}

                                {...formItemLayout}>
                                {getFieldDecorator('value3', {
                                    initialValue: ""
                                })(
                                    <Input onChange={this.handleValueChange} />
                                )}
                            </FormItem>
                        </div>
                        <div style={{ marginTop: "10px" }} >
                            <p>捕获：</p>
                            <FormItem label={'捕获值'} style={{ marginBottom: "0px" }}  {...formItemLayout}>
                                {getFieldDecorator('value4', {
                                    initialValue: ""
                                })(
                                    <Input onChange={this.handleValueChange} />
                                )}
                            </FormItem>
                            <List
                                size="small"
                                header={<div>捕获分组</div>}
                                style={{ marginTop:"10px",marginLeft:"100px",width:"450px",height: "180px",overflow:"auto" }}
                                bordered
                                dataSource={value4Group}
                                renderItem={item => (<List.Item>{item}</List.Item>)}
                            />
                        </div>
                    </Modal>
                </Form>
            </Modal>
        )
    }
}

const RegexEval = Form.create()(Regex);

export default connect()(RegexEval);
