
import React from "react";
import { connect } from 'dva';
import { Form, Select, Button, Input, Checkbox, Tabs, Row, Col, Card } from 'antd';
import Modal from "components/Modal.js";
import { treeViewConfig, treeUploadConfig } from '../../../../constant';
import EditTable from '../../../common/EditTable';

let Timer;
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const Option = Select.Option;
const ButtonGroup = Button.Group;

class ExecelOutputDialog extends React.Component {

    constructor(props) {
        super(props);
        const { visible } = props.model;
        if (visible === true) {
            const { outputFields } = props.model.config;
            let data = [];
            let count = 0;
            if (outputFields) {
                for (let index of outputFields) {
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
                path: ""
            }
        }
    }

    componentDidMount() {
        this.Request();
    }

    Request() {
        const { getInputFields, transname, text, getDataStore } = this.props.model;
        let obj = {};
        obj.transname = transname;
        obj.stepname = text;
        getInputFields(obj, data => {
            this.setState({
                InputData: data
            })
        });
        let obj1 = {};
        obj1.type = "output";
        obj1.path = "";
        getDataStore(obj1, data => {
            const { path } = data;
            this.setState({
                path: path
            })
        })
    };

    initFuc(that) {
        const { getInputSelect } = this.props.model;
        const { InputData } = this.state;
        let options = getInputSelect(InputData, "name");
        that.updateOptions({
            name: options
        });
    };

    optionGroups = [
        <Option key="yyyy/MM/dd HH:mm:ss.SSS" value="yyyy/MM/dd HH:mm:ss.SSS">yyyy/MM/dd HH:mm:ss.SSS</Option>,
        <Option key="yyyy/MM/dd HH:mm:ss.SSS XXX" value="yyyy/MM/dd HH:mm:ss.SSS XXX">yyyy/MM/dd HH:mm:ss.SSS XXX</Option>,
        <Option key="yyyy/MM/dd HH:mm:ss" value="yyyy/MM/dd HH:mm:ss">yyyy/MM/dd HH:mm:ss</Option>,
        <Option key="yyyy/MM/dd HH:mm:ss XXX" value="yyyy/MM/dd HH:mm:ss XXX">yyyy/MM/dd HH:mm:ss XXX</Option>,
        <Option key="yyyyMMddHHmmss" value="yyyyMMddHHmmss">yyyyMMddHHmmss</Option>,
        <Option key="yyyy/MM/dd" value="yyyy/MM/dd">yyyy/MM/dd</Option>,
        <Option key="yyyy-MM-dd" value="yyyy-MM-dd">yyyy-MM-dd</Option>,
        <Option key="yyyy-MM-dd HH:mm:ss" value="yyyy-MM-dd HH:mm:ss">yyyy-MM-dd HH:mm:ss</Option>,
        <Option key="yyyy-MM-dd HH:mm:ss XXX" value="yyyy-MM-dd HH:mm:ss XXX">yyyy-MM-dd HH:mm:ss XXX</Option>,
        <Option key="yyyyMMdd" value="yyyyMMdd">yyyyMMdd</Option>,
        <Option key="MM/dd/yyyy" value="MM/dd/yyyy">MM/dd/yyyy</Option>,
        <Option key="MM/dd/yyyy HH:mm:ss" value="MM/dd/yyyy HH:mm:ss">MM/dd/yyyy HH:mm:ss</Option>,
        <Option key="MM-dd-yyyy" value="MM-dd-yyyy">MM-dd-yyyy</Option>,
        <Option key="MM-dd-yyyy HH:mm:ss" value="MM-dd-yyyy HH:mm:ss">MM-dd-yyyy HH:mm:ss</Option>,
        <Option key="MM/dd/yy" value="MM/dd/yy">MM/dd/yy</Option>,
        <Option key="MM-dd-yy" value="MM-dd-yy">MM-dd-yy</Option>,
        <Option key="dd/MM/yyyy" value="dd/MM/yyyy">dd/MM/yyyy</Option>,
        <Option key="dd-MM-yyyy" value="dd-MM-yyyy">dd-MM-yyyy</Option>,
        <Option key="yyyy-MM-dd'T'HH:mm:ss.SSSXXX" value="yyyy-MM-dd'T'HH:mm:ss.SSSXXX">yyyy-MM-dd'T'HH:mm:ss.SSSXXX</Option>,
    ];

    formItemLayout1 = {
        labelCol: { span: 6 },
        wrapperCol: { span: 14 }
    };
    formItemLayout3 = {
        labelCol: { span: 9 },
        wrapperCol: { span: 14 }
    };
    formItemLayout2 = {
        labelCol: { span: 6 },
        wrapperCol: { span: 12 }
    };
    formItemLayout = {
        wrapperCol: { span: 18 }
    };

    formItemLayout4 = {
        labelCol: { span: 4 },
        wrapperCol: { span: 12 },
    };

    columns = [
        {
            title: '名称',
            dataIndex: 'name',
            key: 'name',
            width: "35%",
            selectable: true
        }, {
            title: '类型',
            dataIndex: 'typedesc',
            width: "25%",
            key: 'typedesc',
            selectable: true,
            selectArgs: [<Select.Option key="Number" value="Number">Number</Select.Option>,
            <Select.Option key="Date" value="Date">Date</Select.Option>,
            <Select.Option key="String" value="String">String</Select.Option>,
            <Select.Option key="Boolean" value="Boolean">Boolean</Select.Option>,
            <Select.Option key="Integer" value="Integer">Integer</Select.Option>,
            <Select.Option key="BigNumber" value="BigNumber">BigNumber</Select.Option>,
            <Select.Option key="Binary" value="Binary">Binary</Select.Option>,
            <Select.Option key="Timestamp" value="Timestamp">Timestamp</Select.Option>,
            <Select.Option key="Internet Address" value="Internet Address">Internet Address</Select.Option>
            ]

        }, {
            title: '格式',
            dataIndex: 'format',
            key: 'format',
            editable: true,
        }
    ];

    handleAdd = () => {
        this.refs.editTable.handleAdd({
            name: "",
            typedesc: "",
            format: ""
        });
    };

    handleGetFields = () => {
        const { InputData } = this.state;
        let args = [];
        let count = 0;
        for (let index of InputData) {
            args.push({
                key: count,
                typedesc: index.type,
                ...index
            });
            count++;
        }

        this.refs.editTable.updateTable(args, count);
    };

    setModelHide() {
        const { dispatch } = this.props;
        dispatch({
            type: 'items/hide',
            visible: false
        });
    }

    handleFormSubmit() {
        const form = this.props.form;
        const { panel, transname, description, key, saveStep, config, text, formatTable } = this.props.model;
        const { outputFields } = config;

        form.validateFields((err, values) => {
            if (err) {
                return;
            }
            let sendFields = [];
            if (this.refs.editTable) {
                if (this.refs.editTable.state.dataSource.length > 0) {
                    let arg = ["name", "typedesc", "format"];
                    sendFields = formatTable(this.refs.editTable.state.dataSource, arg)
                }
            } else {
                if (outputFields) {
                    sendFields = fields
                }
            }
            let obj = {};
            obj.transname = transname;
            obj.stepname = text;
            obj.newname = (text === values.text ? "" : values.text);
            obj.type = panel;
            obj.description = description;
            obj.config = {
                ...values,
                outputFields: sendFields
            }
            saveStep(obj, key, data => {
                if (data.code === "200") {
                    this.setModelHide();
                }
            });
        })
    }

    getFieldList() {
        const { dispatch } = this.props;
				const { panel } = this.props.model;
				const { getFieldValue,formatFolder } = this.props.form;
				let path = formatFolder(getFieldValue("fileName"));
				let viewPath = "";
				if(path.substr(0,1) !== "/"){
					path = `${this.state.path}${path}`
				}
				viewPath = path;
        let obj = treeViewConfig.get(panel)["list"];;
        let updateModel = this.setFolder.bind(this);

        dispatch({
            type: "treeview/showTreeModel",
            payload: {
                ...obj,
                obj: {
                    ...obj.obj,path
                },
                viewPath: viewPath,
                updateModel: updateModel
            }
        })
    };

    //獲得模板
    getFieldList1(name) {
        const { dispatch } = this.props;
        const { panel } = this.props.model;
        let obj = treeViewConfig.get(panel)[name];
        let updateModel = name ==="model1"?this.setFolder1.bind(this):this.setFolder2.bind(this);
        let type = obj.obj.type;
        let viewPath = "";

        dispatch({
            type: "treeview/showTreeModel",
            payload: {
                ...obj,
                obj: {
                    ...obj.obj,
                    type: type
                },
                viewPath: viewPath,
                updateModel: updateModel
            }
        })
    };

    //設置模板
    setFolder1() {
        const { setFieldsValue } = this.props.form;
        if (str) {
            setFieldsValue({
                "templateFileName": str
            })
        }
    }

    //設置模板
    setFolder2() {
        const { setFieldsValue } = this.props.form;
        if (str) {
            setFieldsValue({
                "headerImage": str
            })
        }
    }

    /*调用文件上传组件*/
    handleFileUpload(name) {
        const { dispatch } = this.props;
        const { panel } = this.props.model;
        let obj = treeUploadConfig.get(panel)[name];

        dispatch({
            type: "uploadfile/showModal",
            payload: {
                ...obj,
                visible: true
            }
        });
    };


    /*设置文件名*/
    setFolder(str) {
        if (!str) return false;
        const { setFieldsValue } = this.props.form;
        setFieldsValue({
            "fileName": str.trim()
        })
    };

    handleDeleteFields = () => {
        this.refs.editTable.handleDelete();
    };

    handleUseChange(e) {
        this.setState({
            useFolder: e.target.checked
        })
    }

    /**
     * 改变指定日期时间格式，重置日期，时间
     */
    handleChange(e) {
        const { setFieldsValue } = this.props.form;
        if (e.target.checked) {
            setFieldsValue({ dateInFilename: false, timeInFilename: false });
        }
    }


    render() {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { visible, config, text, handleCheckName, isMetacube } = this.props.model;
        const { path } = this.state;


        const setDisabled1 = () => {
            if (getFieldValue("protectsheet") === undefined) {
                return config.protectsheet;
            } else {
                if (getFieldValue("protectsheet")) {
                    return getFieldValue("protectsheet");
                } else {
                    return false;
                }
            }
        };

        const setDisabled2 = () => {
            if (getFieldValue("usetempfiles") === undefined) {
                return config.usetempfiles;
            } else {
                if (getFieldValue("usetempfiles")) {
                    return getFieldValue("usetempfiles");
                } else {
                    return false;
                }
            }
        };

        const setDisabled3 = () => {

            if (getFieldValue("specifyFormat") === undefined) {
                return config.specifyFormat;
            } else {
                if (getFieldValue("specifyFormat")) {
                    return getFieldValue("specifyFormat");
                } else {
                    return false;
                }
            }
        };

        const setDisabled4 = () => {
            if (getFieldValue("templateEnabled") === undefined) {
                return config.templateEnabled;
            } else {
                if (getFieldValue("templateEnabled")) {
                    return getFieldValue("templateEnabled");
                } else {
                    return false;
                }
            }
        };

        return (
            <Modal
                visible={visible}
                title="Excel输出"
                wrapClassName="vertical-center-modal"
                width={750}
                footer={[
                    <Button key="submit" type="primary" size="large" onClick={this.handleFormSubmit.bind(this)}>确定</Button>,
                    <Button key="back" size="large" onClick={this.setModelHide.bind(this)}>取消</Button>,
                ]}
                maskClosable={false}
                onCancel={this.setModelHide.bind(this)}
            >
                {/* <ClearInput /> */}
                <Form >
                    <FormItem label="步骤名称"  {...this.formItemLayout1}>
                        {getFieldDecorator('text', {
                            initialValue: text,
                            rules: [{ whitespace: true, required: true, message: '请输入步骤名称' },
                            { validator: handleCheckName, message: '步骤名称已存在，请更改!' }]
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <div style={{ margin: "0 5%" }}>
                        <Tabs type="card">
                            <TabPane tab="文件" key="1">
																<p style={{ textAlign:"center" }} >浏览：可输入浏览的根目录，默认为：{path}</p>
                                <FormItem label="文件名称"  {...this.formItemLayout2}>
                                    {getFieldDecorator('fileName', {
                                        initialValue: config.fileName,
                                    })(
                                        <Input />
                                    )}
                                    <Button onClick={() => { this.getFieldList() }}>浏览</Button>
                                </FormItem>
                                <Row style={{ marginLeft: "10%" }}>
                                    <Col span={12}>
                                        <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                                            {getFieldDecorator('createparentfolder', {
                                                valuePropName: 'checked',
                                                initialValue: config.createparentfolder
                                            })(
                                                <Checkbox disabled={isMetacube}>创建父目录</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                    <Col span={12}>
                                        <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                                            {getFieldDecorator('doNotOpenNewFileInit', {
                                                valuePropName: 'checked',
                                                initialValue: config.doNotOpenNewFileInit
                                            })(
                                                <Checkbox>启动时不创建文件</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                </Row>
                                <FormItem label="扩展名" style={{ marginBottom: "8px" }} {...this.formItemLayout1}>
                                    {getFieldDecorator('extension', {
                                        initialValue: config.extension
                                    })(
                                        <Input />
                                    )}
                                </FormItem>
                                <Row style={{ marginLeft: "10%" }}>
                                    <Col span={12}>
                                        <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                                            {getFieldDecorator('stepNrInFilename', {
                                                valuePropName: 'checked',
                                                initialValue: config.stepNrInFilename
                                            })(
                                                <Checkbox>文件名里包含步骤数？</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                </Row>
                                <Row style={{ marginLeft: "10%" }}>
                                    <Col span={12}>
                                        <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                                            {getFieldDecorator('dateInFilename', {
                                                valuePropName: 'checked',
                                                initialValue: config.dateInFilename
                                            })(
                                                <Checkbox disabled={setDisabled3()}>文件名里包含日期？</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                    <Col span={12}>
                                        <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                                            {getFieldDecorator('timeInFilename', {
                                                valuePropName: 'checked',
                                                initialValue: config.timeInFilename
                                            })(
                                                <Checkbox disabled={setDisabled3()}>文件名里包含时间？</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                </Row>
                                <Row style={{ marginLeft: "10%" }}>
                                    <Col span={12}>
                                        <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                                            {getFieldDecorator('specifyFormat', {
                                                valuePropName: 'checked',
                                                initialValue: config.specifyFormat
                                            })(
                                                <Checkbox onChange={this.handleChange.bind(this)} >指定日期时间格式？</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                    <Col span={12}>
                                        <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                                            {getFieldDecorator('addToResultFilenames', {
                                                valuePropName: 'checked',
                                                initialValue: config.addToResultFilenames
                                            })(
                                                <Checkbox>结果中添加文件名？</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                </Row>
                                <FormItem label="日期时间格式" style={{ marginBottom: "8px" }} {...this.formItemLayout1}>
                                    {getFieldDecorator('dateTimeFormat', {
                                        initialValue: config.dateTimeFormat
                                    })(
                                        <Select disabled={!setDisabled3()}>
                                            {
                                                this.optionGroups
                                            }
                                        </Select>
                                    )}
                                </FormItem>
                            </TabPane>
                            <TabPane tab="内容" key="2">

                                <Row style={{ marginLeft: "10%" }}>
                                    <Col span={8}>
                                        <FormItem style={{ marginBottom: "10px" }} {...this.formItemLayout}>
                                            {getFieldDecorator('append', {
                                                valuePropName: 'checked',
                                                initialValue: config.append
                                            })(
                                                <Checkbox>追加</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                    <Col span={8}>
                                        <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                                            {getFieldDecorator('headerEnabled', {
                                                valuePropName: 'checked',
                                                initialValue: config.headerEnabled
                                            })(
                                                <Checkbox >头</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                    <Col span={8}>
                                        <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                                            {getFieldDecorator('footerEnabled', {
                                                valuePropName: 'checked',
                                                initialValue: config.footerEnabled
                                            })(
                                                <Checkbox >尾</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                </Row>
                                <FormItem label="编码" style={{ marginBottom: "8px" }} {...this.formItemLayout1}>
                                    {getFieldDecorator('encoding', {
                                        initialValue: config.encoding ? config.encoding : "GBK"
                                    })(
                                        <Select>
                                            <Option value="GBK">GBK</Option>
                                            <Option value="ISO-8859-1">ISO-8859-1</Option>
                                            <Option value="GB2312">GB2312</Option>
                                            <Option value="UTF-8">UTF-8</Option>
                                            <Option value="Big5">Big5</Option>
                                        </Select>
                                    )}
                                </FormItem>
                                <FormItem label="分拆...每一行" style={{ marginBottom: "8px" }} {...this.formItemLayout1}>
                                    {getFieldDecorator('splitEvery', {
                                        initialValue: config.splitEvery
                                    })(
                                        <Input />
                                    )}
                                </FormItem>
                                <FormItem label="工作表名称" style={{ marginBottom: "8px" }} {...this.formItemLayout1}>
                                    {getFieldDecorator('sheetname', {
                                        initialValue: config.sheetname
                                    })(
                                        <Input />
                                    )}
                                </FormItem>
                                <Row style={{ marginLeft: "10%" }}>
                                    <Col span={8}>
                                        <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                                            {getFieldDecorator('protectsheet', {
                                                valuePropName: 'checked',
                                                initialValue: config.protectsheet
                                            })(
                                                <Checkbox >保护工作表？</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                    <Col span={16}>
                                        <FormItem label="密码" style={{ marginBottom: "8px" }} {...this.formItemLayout1}>
                                            {getFieldDecorator('password', {
                                                initialValue: config.password
                                            })(
                                                <Input disabled={!setDisabled1()} type="password" autoComplete={false} />
                                            )}
                                        </FormItem>
                                    </Col>
                                </Row>
                                <Row style={{ marginLeft: "10%" }}>
                                    <Col span={8}>
                                        <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                                            {getFieldDecorator('autoSizeColumns', {
                                                valuePropName: 'checked',
                                                initialValue: config.autoSizeColumns
                                            })(
                                                <Checkbox >自动调整列大小</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                    <Col span={16}>
                                        <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                                            {getFieldDecorator('nullIsBlank', {
                                                valuePropName: 'checked',
                                                initialValue: config.nullIsBlank
                                            })(
                                                <Checkbox >保留NULL值</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                </Row>
                                <Row style={{ marginLeft: "10%" }}>
                                    <Col span={8}>
                                        <FormItem  {...this.formItemLayout}>
                                            {getFieldDecorator('usetempfiles', {
                                                valuePropName: 'checked',
                                                initialValue: config.usetempfiles
                                            })(
                                                <Checkbox >使用临时文件</Checkbox>
                                            )}
                                        </FormItem>
                                    </Col>
                                    <Col span={16}>
                                        <FormItem label="临时文件目录" {...this.formItemLayout1}>
                                            {getFieldDecorator('tempdirectory', {
                                                initialValue: config.tempdirectory
                                            })(
                                                <Input disabled={!setDisabled2()} />
                                            )}
                                        </FormItem>
                                    </Col>
                                </Row>
                                <Card>
                                    <Row style={{ marginLeft: "10%" }}>
                                        <Col span={12}>
                                            <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                                                {getFieldDecorator('templateEnabled', {
                                                    valuePropName: 'checked',
                                                    initialValue: config.templateEnabled
                                                })(
                                                    <Checkbox >使用模板</Checkbox>
                                                )}
                                            </FormItem>
                                        </Col>
                                        <Col span={12}>
                                            <FormItem style={{ marginBottom: "8px" }} {...this.formItemLayout}>
                                                {getFieldDecorator('templateAppend', {
                                                    valuePropName: 'checked',
                                                    initialValue: config.templateAppend
                                                })(
                                                    <Checkbox disabled={!setDisabled4()} >追加Excel模板</Checkbox>
                                                )}
                                            </FormItem>
                                        </Col>
                                    </Row>
                                    <FormItem label="Excel模板"    {...this.formItemLayout4}>
                                        <div>
                                            {getFieldDecorator('templateFileName', {
                                                initialValue: config.templateFileName
                                            })(
                                                <Input disabled={!setDisabled4()} />
                                            )}
                                            <Button disabled={!setDisabled4()} onClick={() => { this.getFieldList1("model1") }} >浏览</Button>
                                            <Button disabled={!setDisabled4()} onClick={() => { this.handleFileUpload("model") }}>上传</Button>
                                        </div>
                                    </FormItem>
                                </Card>
                            </TabPane>
                            <TabPane tab="格式" key="3">
                                <Card title="表头字体">
                                    <Row>
                                        <Col span={12}>
                                            <FormItem style={{ marginBottom: "8px" }} label="表头字体" {...this.formItemLayout3}>
                                                {getFieldDecorator('headerFontName', {
                                                    initialValue: config.headerFontName
                                                })(
                                                    <Select>
                                                        <Option value="Arial">Arial</Option>
                                                        <Option value="Courier">Courier</Option>
                                                        <Option value="Tahoma">Tahoma</Option>
                                                        <Option value="Times">Times</Option>
                                                    </Select>
                                                )}
                                            </FormItem>
                                        </Col>
                                        <Col span={12}>
                                            <FormItem style={{ marginBottom: "8px" }} label="表头字体大小" {...this.formItemLayout3}>
                                                {getFieldDecorator('headerFontSize', {
                                                    initialValue: config.headerFontSize
                                                })(
                                                    <Input />
                                                )}
                                            </FormItem>
                                        </Col>
                                    </Row>
                                    <Row style={{ marginLeft: "10%" }}>
                                        <Col span={12}>
                                            <FormItem style={{ marginBottom: "0px" }} {...this.formItemLayout}>
                                                {getFieldDecorator('headerFontBold', {
                                                    valuePropName: 'checked',
                                                    initialValue: config.headerFontBold
                                                })(
                                                    <Checkbox>表头字体加粗？</Checkbox>
                                                )}
                                            </FormItem>
                                        </Col>
                                        <Col span={12}>
                                            <FormItem style={{ marginBottom: "8px" }} {...this.formItemLayout}>
                                                {getFieldDecorator('headerFontItalic', {
                                                    valuePropName: 'checked',
                                                    initialValue: config.headerFontItalic
                                                })(
                                                    <Checkbox>表头字体倾斜？</Checkbox>
                                                )}
                                            </FormItem>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col span={12}>
                                            <FormItem style={{ marginBottom: "8px" }} label="表头字体下划线" {...this.formItemLayout3}>
                                                {getFieldDecorator('headerFontUnderline', {
                                                    initialValue: config.headerFontUnderline
                                                })(
                                                    <Select>
                                                        <Option value="No underline">无</Option>
                                                        <Option value="Single">单条</Option>
                                                        <Option value="Single accounting">单条(会计用)</Option>
                                                        <Option value="Double">双条</Option>
                                                        <Option value="Double accounting">双条(会计用)</Option>
                                                    </Select>
                                                )}
                                            </FormItem>
                                        </Col>
                                        <Col span={12}>
                                            <FormItem style={{ marginBottom: "8px" }} label="表头字体方向" {...this.formItemLayout3}>
                                                {getFieldDecorator('headerFontOrientation', {
                                                    initialValue: config.headerFontOrientation
                                                })(
                                                    <Select>
                                                        <Option value="Horizontal">水平</Option>
                                                        <Option value="Minus 45">负45度</Option>
                                                        <Option value="Minus 90">负90度</Option>
                                                        <Option value="Plus 45">正45度</Option>
                                                        <Option value="Plus 90">正90度</Option>
                                                        <Option value="Stacked">Stacked</Option>
                                                        <Option value="Vertical">垂直</Option>
                                                    </Select>
                                                )}
                                            </FormItem>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col span={12}>
                                            <FormItem style={{ marginBottom: "8px" }} label="表头字体颜色" {...this.formItemLayout3}>
                                                {getFieldDecorator('headerFontColor', {
                                                    initialValue: config.headerFontColor
                                                })(
                                                    <Select>
                                                        <Option value="None">无</Option>
                                                        <Option value="BLACK">黑色</Option>
                                                        <Option value="WHITE">白色</Option>
                                                        <Option value="RED">红色</Option>
                                                        <Option value="BLUE">蓝色</Option>
                                                        <Option value="GREEN">绿色</Option>
                                                        <Option value="YELLOW">黄色</Option>
                                                        <Option value="PINK">粉色</Option>
                                                        <Option value="GOLD">金色</Option>
                                                    </Select>
                                                )}
                                            </FormItem>
                                        </Col>
                                        <Col span={12}>
                                            <FormItem style={{ marginBottom: "8px" }} label="表头背景颜色" {...this.formItemLayout3}>
                                                {getFieldDecorator('headerBackgroundColor', {
                                                    initialValue: config.headerBackgroundColor
                                                })(
                                                    <Select>
                                                        <Option value="None">无</Option>
                                                        <Option value="BLACK">黑色</Option>
                                                        <Option value="WHITE">白色</Option>
                                                        <Option value="RED">红色</Option>
                                                        <Option value="BLUE">蓝色</Option>
                                                        <Option value="GREEN">绿色</Option>
                                                        <Option value="YELLOW">黄色</Option>
                                                        <Option value="PINK">粉色</Option>
                                                        <Option value="GOLD">金色</Option>
                                                    </Select>
                                                )}
                                            </FormItem>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col span={12}>
                                            <FormItem style={{ marginBottom: "8px" }} label="表头高度" {...this.formItemLayout3}>
                                                {getFieldDecorator('headerRowHeight', {
                                                    initialValue: config.headerRowHeight
                                                })(
                                                    <Input />
                                                )}
                                            </FormItem>
                                        </Col>
                                        <Col span={12}>
                                            <FormItem style={{ marginBottom: "8px" }} label="表头对齐方式" {...this.formItemLayout3}>
                                                {getFieldDecorator('headerAlignment', {
                                                    initialValue: config.headerAlignment
                                                })(
                                                    <Select>
                                                        <Option value="General">常规</Option>
                                                        <Option value="Left">左对齐</Option>
                                                        <Option value="Right">右对齐</Option>
                                                        <Option value="Center">居中</Option>
                                                        <Option value="Fill">填充</Option>
                                                        <Option value="Justify">两端对齐</Option>
                                                    </Select>
                                                )}
                                            </FormItem>
                                        </Col>
                                    </Row>
                                    <FormItem style={{ marginBottom: "8px" }} label="添加图片" {...this.formItemLayout4}>
                                        <div>
                                            {getFieldDecorator('headerImage', {
                                                initialValue: config.headerImage
                                            })(
                                                <Input />
                                            )}
                                            <Button  onClick={() => { this.getFieldList1("model2") }} >浏览</Button>
                                            <Button  onClick={() => { this.handleFileUpload("list") }}>上传</Button>
                                        </div>
                                    </FormItem>
                                </Card>
                                <Card title="表数据字体">
                                    <Row>
                                        <Col span={12}>
                                            <FormItem style={{ marginBottom: "8px" }} label="数据字体" {...this.formItemLayout3}>
                                                {getFieldDecorator('rowFontName', {
                                                    initialValue: config.rowFontName
                                                })(
                                                    <Select>
                                                        <Option value="Arial">Arial</Option>
                                                        <Option value="Courier">Courier</Option>
                                                        <Option value="Tahoma">Tahoma</Option>
                                                        <Option value="Times">Times</Option>
                                                    </Select>
                                                )}
                                            </FormItem>
                                        </Col>
                                        <Col span={12}>
                                            <FormItem style={{ marginBottom: "8px" }} label="数据字体大小" {...this.formItemLayout3}>
                                                {getFieldDecorator('rowFontSize', {
                                                    initialValue: config.rowFontSize
                                                })(
                                                    <Input />
                                                )}
                                            </FormItem>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col span={12}>
                                            <FormItem style={{ marginBottom: "8px" }} label="数据字体颜色" {...this.formItemLayout3}>
                                                {getFieldDecorator('rowFontColor', {
                                                    initialValue: config.rowFontColor
                                                })(
                                                    <Select>
                                                        <Option value="None">无</Option>
                                                        <Option value="BLACK">黑色</Option>
                                                        <Option value="WHITE">白色</Option>
                                                        <Option value="RED">红色</Option>
                                                        <Option value="BLUE">蓝色</Option>
                                                        <Option value="GREEN">绿色</Option>
                                                        <Option value="YELLOW">黄色</Option>
                                                        <Option value="PINK">粉色</Option>
                                                        <Option value="GOLD">金色</Option>
                                                    </Select>
                                                )}
                                            </FormItem>
                                        </Col>
                                        <Col span={12}>
                                            <FormItem style={{ marginBottom: "8px" }} label="数据背景颜色" {...this.formItemLayout3}>
                                                {getFieldDecorator('rowBackgroundColor', {
                                                    initialValue: config.rowBackgroundColor
                                                })(
                                                    <Select>
                                                        <Option value="None">无</Option>
                                                        <Option value="BLACK">黑色</Option>
                                                        <Option value="WHITE">白色</Option>
                                                        <Option value="RED">红色</Option>
                                                        <Option value="BLUE">蓝色</Option>
                                                        <Option value="GREEN">绿色</Option>
                                                        <Option value="YELLOW">黄色</Option>
                                                        <Option value="PINK">粉色</Option>
                                                        <Option value="GOLD">金色</Option>
                                                    </Select>
                                                )}
                                            </FormItem>
                                        </Col>
                                    </Row>
                                </Card>
                            </TabPane>
                            <TabPane tab="字段" key="4">
                                <div >
                                    <Row style={{ margin: "5px 0", width: "100%" }}  >
                                        <Col span={12}  >
                                            <ButtonGroup size={"small"}>
                                                <Button onClick={this.handleAdd.bind(this)}>添加字段</Button>
                                                <Button onClick={() => { this.handleGetFields() }}>获取字段</Button>
                                            </ButtonGroup>
                                        </Col>
                                        <Col span={12}>
                                            <Button style={{ float: "right" }} size={"small"} onClick={this.handleDeleteFields.bind(this)}>删除字段</Button>
                                        </Col>
                                    </Row>
                                    <EditTable initFuc={this.initFuc.bind(this)} columns={this.columns} rowSelection={true} dataSource={this.state.dataSource} tableStyle="editTableStyle5" size={"small"} scroll={{ y: 140 }} ref="editTable" count={0} />
                                </div>
                            </TabPane>
                        </Tabs>
                    </div>
                </Form>
            </Modal>
        );
    }
}
const ExecelOutput = Form.create()(ExecelOutputDialog);

export default connect()(ExecelOutput);
