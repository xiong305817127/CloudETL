import React from 'react'
import { connect } from 'dva'
import { Form, Select } from 'antd'
import Modal from "components/Modal.js";

const FormItem = Form.Item
const Option = Select.Option

const index = ({ analysisConfig, dispatch, form }) => {

    const { visible, configs, clusterList } = analysisConfig;
    const { getFieldDecorator } = form;

    const handleCreate = () => {
        const { form, dispatch } = this.props;
        form.validateFields((err, values) => {
            if (err) {
                return;
            }
            dispatch({
                type: 'analysisConfig/saveConfig',
                payload:{ ...values }
            });
            form.resetFields();
        });
    }

    const handleCancel = () => {
        dispatch({ type: 'analysisConfig/reset' });
        form.resetFields();
    }

    //表单布局
    const formItemLayout = {
        labelCol: { span: 6 },
        wrapperCol: { span: 14 }
    };

    return (
        <Modal
            title="配置"
            wrapClassName="vertical-center-modal"
            visible={visible}
            onOk={handleCreate}
            onCancel={handleCancel}
        >
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="集群名称"
                    hasFeedback
                    style={{ marginBottom: "8px" }}
                >
                    {getFieldDecorator('clusterSchema', {
                        initialValue: configs.clusterSchema
                    })(
                        <Select placeholder="请选择spark集群" allowClear={true} >
                            {
                                clusterList.map((index) =>
                                    <Option key={index.name} value={index.name}>{index.name}</Option>
                                )
                            }
                        </Select>
                    )}
                </FormItem>
            </Form>
        </Modal>
    )
}

export default connect(({ analysisConfig }) => ({
    analysisConfig
}))(Form.create()(index))
