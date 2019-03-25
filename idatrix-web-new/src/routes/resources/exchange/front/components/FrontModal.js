/**
 * 前置管理弹框
 * 
 * author pwj 2018/9/22
 */
import React from "react";
import { connect } from 'dva';
import { Button, Form, Input, Select, Card, Cascader } from 'antd';
import Modal from 'components/Modal';

const FormItem = Form.Item;
const Option = Select.Option;

const formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 },
};

const displayRender = (label,selectedOptions) => {
    console.log(selectedOptions,"selectedOptions");
        
    return label[label.length - 1];
}

const index = ({ frontModel, resourcesCommon, form, dispatch }) => {

    const { visible, config, status, frontList, databaseList, schemaList, userList } = frontModel;
    const { departmentsTree } = resourcesCommon;
    const { getFieldDecorator, setFieldsValue } = form;

    console.log(resourcesCommon);

    //确定
    const handleOK = (e) => {
        e.preventDefault();
        form.validateFields((err, values) => {
            if (err) return;
            if (status === "str") {
                //编辑
                console.log(values,"编辑");
            } else {
                //新增
                console.log(values,"新增");
            }

        })
    }

    //取消
    const handleCancel = () => {
        form.resetFields();
        dispatch({
            type: "frontModel/save",
            payload: {
                visible: false
            }
        })
    }

    //选择部门
    const handleDeptChange = (e) => {
        console.log(e,"e----------");
        if (!e || e.length === 0) return;
        //重置数据
        setFieldsValue({
            tmName: "", tmDBName: "", sftpUsername: "", schema: ""
        });
        //查询服务器列表
        dispatch({
            type: "frontModel/getDeptServer",
            payload: parseInt(e[e.length - 1])
        });
    }

    //选择服务器
    const handleServerChange = (e) => {
        if (e !== 0 && !e) return;
        //重置数据
        setFieldsValue({
            tmDBName: "", sftpUsername: "", schema: ""
        });
        //查询数据库列表
        dispatch({ type: "frontModel/getFSDatabase", payload: e })
        //查询用户信息列表
        dispatch({ type: "frontModel/getFSSftp", payload: e })
    }

    //选择数据库
    const handleDatabaseChange = (e) => {
        if (e !== 0 && !e) return;
        //重置数据
        setFieldsValue({
            sftpUsername: ""
        });
        //查询schema列表
        dispatch({
            type: "frontModel/getDbSchemasByDsId",
            payload: e
        })
    }

    //选择schema
    const handleSchemaChange = () => {

    }

    


    return (
        <Modal
            title={status ? "编辑前置机" : "新增前置机"}
            visible={visible}
            width={600}
            footer={[
                <Button key="back" size="default" onClick={handleCancel}>取消</Button>,
                <Button key="submit" type="primary" size="default" onClick={handleOK}>确定</Button>,
            ]}
            onCancel={handleCancel} >
            <Form>
                <FormItem label="类型" {...formItemLayout} style={{ marginBottom: "8px" }}>
                    <span className="ant-form-text">前置机</span>
                </FormItem>
                <FormItem label="部门" {...formItemLayout}>
                    {getFieldDecorator('deptId', {
                        initialValue: config.deptId ? config.deptId : [],
                        rules: [{ required: true, message: '请选择部门' }]
                    })(
                        <Cascader allowClear={false} placeholder="请选择部门"
                            displayRender={displayRender}
                            options={departmentsTree} onChange={handleDeptChange}
                            expandTrigger="hover" style={{ width: '100%' }} />
                    )}
                </FormItem>
                <FormItem label="服务器" {...formItemLayout}>
                    {getFieldDecorator('tmName', {
                        initialValue: config.tmName ? config.tmName : "",
                        validateTrigger: 'onBlur',
                        rules: [{ required: true, message: '请选择服务器' }]
                    })(
                        <Select allowClear placeholder="请选择所在前置机"
                            onChange={handleServerChange} style={{ width: '100%' }}>
                            {
                                frontList.map((index) =>
                                    <Option key={index.id} value={index.id}>{index.serverName}</Option>)
                            }
                        </Select>
                    )}
                </FormItem>
                <FormItem label="类型" {...formItemLayout} style={{ marginBottom: "8px" }}>
                    <span className="ant-form-text">数据库</span>
                </FormItem>
                <FormItem label="数据库实例" {...formItemLayout}>
                    {getFieldDecorator('tmDBName', {
                        initialValue: config.tmDBName ? config.tmDBName : "",
                        validateTrigger: 'onBlur',
                        rules: [{ required: true, message: '请选择数据库实例' }]
                    })(
                        <Select allowClear placeholder="请选择数据库实例"
                            onChange={handleDatabaseChange} style={{ width: '100%' }}>
                            {
                                databaseList.map((index) =>
                                    <Option key={index.dsId} value={index.dsId}>{index.dbDatabasename}</Option>)
                            }
                        </Select>
                    )}
                </FormItem>
                <FormItem label="端口" {...formItemLayout} style={{ marginBottom: "8px" }}>
                    <span className="ant-form-text">{config.tmDBPort ? config.tmDBPort : ""}</span>
                </FormItem>
                <FormItem label="数据库类型" {...formItemLayout} style={{ marginBottom: "8px" }}>
                    <span className="ant-form-text">{config.tmDBType ? config.tmDBType : ""}</span>
                </FormItem>
                <FormItem label="模式" {...formItemLayout}>
                    {getFieldDecorator('schema', {
                        initialValue: config.schema ? config.schema : "",
                        validateTrigger: 'onBlur'
                    })(
                        <Select allowClear placeholder="请选择schema"
                            onChange={handleSchemaChange} style={{ width: '100%' }}>
                            {
                                schemaList.map((index) => <Option key={index.id} value={index.name}>{index.name}</Option>)
                            }
                        </Select>
                    )}
                </FormItem>
                <Card>
                    <FormItem label="类型" {...formItemLayout} style={{ marginBottom: "8px" }}>
                        <span className="ant-form-text">sFTP</span>
                    </FormItem>

                    <FormItem label="根目录" {...formItemLayout} style={{ marginBottom: "8px" }}>
                        {getFieldDecorator('sftpSwitchRoot', {
                            initialValue: config.sftpSwitchRoot || "/switch",
                            rules: [{ required: false, message: '请选择数据库根目录' }]
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem label="端口" {...formItemLayout} style={{ marginBottom: "8px" }}>
                        {getFieldDecorator('sftpPort', {
                            initialValue: config.sftpPort,
                            rules: [{ required: false, message: '请选择数据库端口' }]
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem label="用户名" {...formItemLayout}>
                        {getFieldDecorator('sftpUsername', {
                            initialValue: config.sftpUsername || "",
                            rules: [{ required: false, message: '请选择数据用户名' }]
                        })(
                            <Select allowClear placeholder="请输入用户名" style={{ width: '100%' }}>
                                {
                                    userList.map((index) =>
                                        <Select.Option key={index.id} value={index.ftpUser + ""}>{index.ftpUser}</Select.Option>
                                    )
                                }
                            </Select>
                        )}
                    </FormItem>
                </Card>
            </Form>
        </Modal>
    )
}

export default connect(({
    frontModel, resourcesCommon
}) => ({ frontModel, resourcesCommon }))(Form.create()(index))