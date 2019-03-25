/**
 * 重写前置机管理界面
 * 
 * author pwj
 */
import { connect } from 'dva';
import { Row, Col, Button, Form, Input, Popconfirm } from 'antd';
import { withRouter } from "dva/router";
import TableList from "components/TableList";
import FrontModal from "./components/FrontModal";

const FormItem = Form.Item;

const formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 16 },
}

const index = ({ location, form, frontModel,dispatch }) => {

    const { getFieldDecorator } = form;
    const { loading, dataSource, total } = frontModel;
    const { query } = location;

    //查询
    const handleSearch = () => {
        form.validateFields((err, values) => {
            if (err) {
                return;
            }
            for (let index of Object.keys(values)) {
                if (values[index]) {
                    query[index] = values[index]
                } else {
                    delete query[index]
                }
            }
            query.page = 1;
            router.push({ ...location, query })
        })
    }

    //查看
    const handleView = (id) => {

    }

    //编辑
    const handleEdit = (id) => {
        console.log(id);
        dispatch({ 
            type:"frontModel/getTerminalManageRecordById",
            payload:{id},
            status:"new",
        })
    }

    //删除
    const handleDelete = (id) => {

    }

    //新增
    const handleAdd = () => {
        //请求部门信息
        dispatch({ type:"resourcesCommon/getDepartments" });
        //打开编辑弹框
        dispatch({
            type:"frontModel/save",
            payload:{
                status:"str",
                visible:true,
                config:{}
            }
        })
    }

    //表格header列名
    const columns = [
        {
            title: '部门',
            dataIndex: 'deptName',
            key: 'deptName',
        }, {
            title: '前置机',
            dataIndex: 'tmName',
            key: 'tmName',
        }, {
            title: '数据库',
            dataIndex: 'tmDBName',
            key: 'tmDBName',
        }, {
            title: 'sFTP',
            dataIndex: 'sftpSwitchRoot',
            key: 'sftpSwitchRoot',
            render: (text) => text ? "无" : "已安装"
        }, {
            title: '操作',
            dataIndex: 'endTime',
            key: 'endTime',
            render: (text, record) => {
                return (
                    <div>
                        <a onClick={()=>{ handleView(record.id) }}>
                            查看&nbsp;&nbsp;
                        </a>
                        <a onClick={()=>{ handleEdit(record.id) }}>
                            编辑&nbsp;&nbsp;
                        </a>
                        <Popconfirm title="确定删除吗?" onConfirm={() => { handleDelete(record.id) }} okText="是" cancelText="否">
                            <a>删除&nbsp;&nbsp;</a>
                        </Popconfirm>
                    </div>
                )
            }
        }];

    return (
        <div>
            <Form className="ant-advanced-search-form">
                <Row gutter={24} >
                    <Col span={9} >
                        <FormItem label="部门名称" {...formItemLayout}>
                            {getFieldDecorator("deptName", {
                            })(
                                <Input />
                            )}
                        </FormItem>
                    </Col>
                    <Col span={9} >
                        <FormItem label={"数据库"} {...formItemLayout} >
                            {getFieldDecorator("dbName", {
                            })(
                                <Input />
                            )}
                        </FormItem>
                    </Col>
                    <Col span={24} className="form-btn-style" >
                        <Button type="primary" onClick={handleSearch} > 查询</Button>
                        <Button style={{ marginLeft: 8 }} type="primary" onClick={handleAdd}> 新增</Button>
                    </Col>
                </Row>
            </Form>
            <TableList
                showIndex
                rowKey="id"
                columns={columns}
                loading={loading}
                dataSource={dataSource}
                className="th-nowrap"
                pagination={{
                    total: total
                }}
            />
            <FrontModal />
        </div>
    )
}

export default connect(({ frontModel
}) => ({ frontModel }))(withRouter(Form.create()(index)));