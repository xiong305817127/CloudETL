import React from 'react';
import { connect } from 'dva';
import {  Row, Col, Button, Form, Input, Select, Cascader, Tooltip, message } from 'antd';
const FormItem = Form.Item;
const Option = Select.Option;
const { TextArea } = Input;
import TableList from "components/TableList";
import Modal from 'components/Modal';
import __ from "lodash";
import { sureConfirm ,getFormData} from "utils/utils";
import moment from "moment";

import {
  MLZYsaveOrUpdate, MLZYgetAllSourceServicePages, MLZYdeleteSourceServiceById, MLZYgetSourceServiceById,getWSDLContents
} from 'services/DirectoryOverview';
import { getServices } from 'services/catalog';
const formItemLayout1 = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
}
const CustomizedForm = Form.create({
  onFieldsChange(props, changedFields) {
    props.onChange(changedFields);
  }
})((props) => {
  const { getFieldDecorator } = props.form;
  const { handleClickBySearch, handleEdit1, handleDelete,selectedRows} = props;
  return (
    <Row gutter={20}>
      <Col span={8}>
        <FormItem label="服务名称" {...formItemLayout1}>
          {getFieldDecorator("serviceName", {
          })(
            <Input />
          )}
        </FormItem>
      </Col>
      <Col span={8}>
        <FormItem label={"服务代码"} {...formItemLayout1} >
          {getFieldDecorator("serviceCode", {
          })(
            <Input />
          )}
        </FormItem>
      </Col>
      <Col span={8}>
        <FormItem label={"提供方"} {...formItemLayout1}>
          {getFieldDecorator("providerName", {
          })(
            <Input />
          )}
        </FormItem>
      </Col>
      <Col span={8}>
        <FormItem label={"服务类型"} {...formItemLayout1} >
          {getFieldDecorator("serviceType", {
            initialValue: "all",
          })(
            <Select style={{ width: '100%' }}>
              <Option value="all">全部</Option>
              <Option value="SOAP">SOAP</Option>
              <Option value="RESTful">RESTful</Option>
            </Select>
          )}
        </FormItem>
      </Col>
      <Col span={8}></Col>
      <Col span={8} className="search_btn">
          <Button type="primary" onClick={handleClickBySearch} >查询</Button>
          <Button style={{ marginLeft: 10 }} type="primary" onClick={handleEdit1} >新增</Button>
          <Button disabled={selectedRows === ""} style={{ marginLeft: 10 }} type="primary" onClick={handleDelete} >删除</Button>
      </Col>
    </Row>
  );
});

class Server extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      fields: {
        serviceName: {
          value: '',
        },
        serviceCode: {
          value: '',
        },
        providerName: {
          value: '',
        },
        serviceType: {
          value: 'all'
        },
      },
      pagination: {
        current: 1,
        pageSize: 10
      },
      total: '',
      BaseText: [],
      info: '',
      visible: false,
      option: [],
      serverId: '',
      serverModel: '',
      selectedRows: "",
      SType: '',
      loading: false,
    }
  }


  /*表单*/
  columns = [
    {
      title: '服务代码',
      dataIndex: 'serviceCode',
      key: 'serviceCode',
      width: "10%",
      /*filterMultiple: false,
      onFilter: (value, record) => record.serviceCode.indexOf(value) === 0,
      sorter: (a, b) => a.serviceCode.length - b.serviceCode.length,*/
    }, {
      title: '服务名称',
      dataIndex: 'serviceName',
      key: 'serviceName',
      width: "10%",
    }, {
      title: '提供方',
      dataIndex: 'providerName',
      key: 'providerName',
      width: "15%",
      render: (text, record) => {
        return `${text}(${record.serviceCode})`
      }
    }, {
      title: '服务类型',
      dataIndex: 'serviceType',
      key: 'serviceType',
      width: "5%",
    }, {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: '15%',

    }, {
      title: '创建人',
      dataIndex: 'creator',
      key: 'creator',
      width: '5%',

    }, {
      title: '修改者',
      dataIndex: 'modifier',
      key: 'modifier',
      width: '5%',

    }, {
      title: '变更时间',
      dataIndex: 'modifyTime',
      key: 'modifyTime',
      width: '15%',
      render:(text)=>moment(text).format("YYYY-MM-DD HH:mm:ss")
    }, {
      title: '操作',
      key: 'x12',
      width: '10%',
      render: (text, record) => {
        return (<div>
          <a onClick={() => { this.handleClick(record.id) }}>
            <Tooltip title="查看" >
              查看&nbsp;&nbsp;&nbsp;&nbsp;
            </Tooltip>
          </a>
          <a onClick={() => { this.handleEdit(record.id) }}>
            <Tooltip title="编辑" >
              编辑&nbsp;&nbsp;&nbsp;&nbsp;
            </Tooltip>
          </a>

        </div>)
      }
    }];

  /*查看信息*/
  handleClick = (id) => {
    const { dispatch } = this.props;
    dispatch({
      type: "sourceserviceModel/setMetaId",
      payload: {
        serverModel: "selectClick",
        loading: false,
      }
    })
    let arrs = {};
    arrs.id = id;
    /*原服务单体查询*/
    MLZYgetSourceServiceById(arrs).then((res) => {
      const { code, data, message } = res.data;
      let arge = [];
      let str = data.providerId;
      let str1 = data.providerName;
      var a = str.split(',');
      var a1 = str1.split(',');
      for (let ket of a) {
        for (let ket1 of a1) {
          arge.push({
            label: ket,
            value: ket1
          })
        }
      }
      let ars = {};
      ars.providerId = a;
      ars.providerName = a1;
      ars.remark = data.remark;
      ars.serviceCode = data.serviceCode;
      ars.serviceName = data.serviceName;
      ars.serviceType = data.serviceType;
      ars.url = data.url;
      ars.wsdl = data.wsdl;
      if (code === "200") {
        dispatch({
          type: "sourceserviceModel/setMetaId",
          payload: {
            info: ars,
            visible: true,
            serverId: id
          }
        })
      }
    })
  }

  /*表格删除数据*/
  handleDelete = () => {
    const { selectedRows } = this.state;
    if (!selectedRows) return false;
    sureConfirm({
      title: "确定删除页面选中数据吗？"
    }, (bool) => {
      if (bool) {
        /*源服务管理删除*/
        MLZYdeleteSourceServiceById({ id:selectedRows }).then((res) => {
          const { code } = res.data;
          if (code === "200") {
            message.success("删除成功")
            this.setState({
              selectedRows: []
            })
            this.rowSelection.selectedRowKeys = [];
            this.searchAll();
          }else{
            res.data.message && message.warning(res.data.message);
          }
        });
      }
    })
  }

  rowSelection = {
    onChange: (selectedRowKeys) => {
      console.log("查看我是否更新",selectedRowKeys)
      let selectKey = selectedRowKeys.join(',')
      this.setState({
        selectedRows: selectKey
      })

      this.rowSelection.selectedRowKeys = selectedRowKeys
    },
    selectedRowKeys: []
  };

  /*点击新增弹框*/
  handleEdit1() {
    const { dispatch } = this.props;
    dispatch({
      type: "sourceserviceModel/showModel",
      payload: {
        serverModel: "select",
        visible: true,
      }
    })
    this.setState({
      info: {}
    })
    this.props.form.resetFields();
  }

  componentWillReceiveProps(nextProps) {
    console.log(nextProps.sourceserviceModel, "nextProps.sourceserviceModel")
    const { pagination, BaseText, loading, total } = nextProps.sourceserviceModel;
    const { dispatch } = this.props;
    this.setState({ pagination, BaseText, loading, total })
  }

  /*点击编辑*/
  handleEdit = (id) => {
    const { dispatch } = this.props;
    let arrs = {};
    arrs.id = id;
    /*原服务单体查询*/
    MLZYgetSourceServiceById(arrs).then((res) => {
      const { code, data, message } = res.data;
      let arge = [];
      let str = data.providerId;
      let str1 = data.providerName;
      var a = str.split(',');
      var a1 = str1.split(',');
      for (let ket of a) {
        for (let ket1 of a1) {
          arge.push({
            label: ket,
            value: ket1
          })
        }
      }
      let ars = {};
      ars.providerId = a;
      ars.providerName = a1;
      ars.remark = data.remark;
      ars.serviceCode = data.serviceCode;
      ars.serviceName = data.serviceName;
      ars.serviceType = data.serviceType;
      ars.url = data.url;
      ars.wsdl = data.wsdl;
      console.log(ars, "ars");
      if (code === "200") {
        dispatch({
          type: "sourceserviceModel/setMetaId",
          payload: {
            info: ars,
            serverModel: "NewSelect",
            visible: true,
            serverId: id,
          }
        })
      }
    })
  }

  /*点击去下弹框*/
  handleCancel() {
    const { dispatch, form } = this.props;
    dispatch({
      type: "sourceserviceModel/hideModel",
      payload: {
        visible: false,
        info: {},
      }
    })
    form.resetFields();
  }

  /*点击保存*/
  handleOk = (e) => {
    e.preventDefault();
    const { dispatch, sourceserviceModel } = this.props;
    /* const { info,serverId,serverModel } = this.props.sourceserviceModel;*/
    this.props.form.validateFields((err, values) => {
      if (!err) {
        /*源服务区分《《新增》》编辑*/
        if (sourceserviceModel.serverModel === "select") {
          let a = values.providerId;
          let b = a.join(",");
          let a1 = this.state.option;
          let b1 = a1.join(",");
          var index = b1.lastIndexOf(",");
          b1 = b1.substring(index + 1, b1.length);

          let arr = {};
          arr.providerId = b;
          arr.serviceName = values.serviceName;
          arr.providerName = b1;
          arr.serviceCode = values.serviceCode;
          arr.serviceType = values.serviceType;
          arr.remark = values.remark;
          arr.url = values.url;
          arr.wsdl = values.wsdl;

          MLZYsaveOrUpdate(arr).then((res) => {
            const { code, data, message } = res.data;
            if (code === "200") {
              message.success("保存成功");
               this.setState({
                 loading:false
               })
              dispatch({
                type: "sourceserviceModel/hideModel",
                payload: {
                  visible: false,
                  visibleHide: true,
                }
              })
              this.props.form.resetFields();
              this.searchAll();
            } else {
              this.setState({
                loading:false
              })
              dispatch({
                type: "shareserviceModel/setMetaId",
                payload: {
                  visible: false,
                  visibleHide: true,
                }
              })
            }
          });

          /*源服务区分新增《《编辑》》*/
        } else if (sourceserviceModel.serverModel === "NewSelect") {
          let a = values.providerId;
          let b = a.join(",");
          let a1 = this.state.option == "" ? sourceserviceModel.info.providerName : this.state.option;
          let b1 = a1.join(",");
          var index = b1.lastIndexOf(",");
          b1 = b1.substring(index + 1, b1.length);

          let arr = {};
          arr.id = sourceserviceModel.serverId;
          arr.providerId = b;
          arr.serviceName = values.serviceName;
          arr.providerName = b1;
          arr.serviceCode = values.serviceCode;
          arr.serviceType = values.serviceType;
          arr.remark = values.remark;
          arr.url = values.url;
          arr.wsdl = values.wsdl;
          MLZYsaveOrUpdate(arr).then((res) => {
            const { code, data, message } = res.data;
            if (code === "200") {

              dispatch({
                type: "sourceserviceModel/hideModel",
                payload: {
                  visible: false,
                  visibleHide: true,
                }
              })
              message.success("保存成功");
              this.props.form.resetFields();
              this.searchAll();
            } else {
              dispatch({
                type: "shareserviceModel/setMetaId",
                payload: {
                  visible: false,
                  visibleHide: true,
                }
              })
            }
          });
        }

      }
    });
  }
  /*初始化渲染服务提供方接口*/
  componentWillMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'resourcesCommon/getDepartments' });
    // this.searchAll()
  }


  /*渲染服务提供方数据的点击获取事件*/
  onChange = (e, label) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'resourcesCommon/getDepartments',
      payload: {
        deptCode: label[label.length - 1].code,
        deptName: label[label.length - 1].label
      }
    });

    let arge = [];
    for (let index of label) {
      arge.push(index.label);
    }
    this.setState({
      option: arge
    })
  }

  /**
   * 查询逻辑
   * 所有查询经过此方法
   */
  searchAll() {
    const fields = this.state.fields;
    const pager = this.state.pagination;
    const { query } = this.props.location;

    //源服务点击查询数据接口
    let arr = getFormData(fields);
    arr.page = 1;
    arr.pageSize = query.pageSize ? query.pageSize : 10;
    MLZYgetAllSourceServicePages(
      {
        ...arr 
      },
      {
        page: 1,
        pageSize: query.pageSize ? query.pageSize : 10
      }
    )
      .then((res) => {
        const { code, data } = res.data;
        if (code === "200" && data) {
          pager.total = data && data.total?data.total:0;
          data.results.map((row, index) => {
            row.index = pager.pageSize * (pager.current - 1) + index + 1;
            return row;
          });
          this.setState({
            BaseText: data && data.results ? data.results : [],
            pagination: pager
          })
        }

        if(data === null ){
          pager.total = 0;
          this.setState({
            BaseText: [],
            pagination: pager
          })
        }
      });
  }

  /*
   *点击查询接口
   */
  handleClickBySearch = (e) => {
    const fields = this.state.fields;

    // 检测所有字段是否为空，如果为空则提示警告
    if (__.every(fields, { value: "" })) {
      message.warning("请输入您的查询内容。")
    } else {
      this.searchAll();
    }

  }

  onChangelist = (pagination, filters, sorter) => {
    this.setState({
      loading: false
    })
  }

  handleFormChange = (changedFields) => {
    this.setState(({ fields }) => ({
      fields: { ...fields, ...changedFields },
    }));
  }

  handleChange = (value) => {
    this.setState({ SType: value })
  }
  //获取服务详情
  getXmlDetails() {
    const { getFieldValue, setFieldsValue } = this.props.form;
    let url = getFieldValue("url");
    console.log(url, "url");
    //if (!url) return;

    getWSDLContents( {"url":url}).then((res) => {
      if(res.data.code === "200"){
        setFieldsValue({ wsdl: res.data.data });
        this.setState({ preInfo: res.data.data })
      }
    })
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const { departmentsTree } = this.props.resourcesCommon;
    const { BaseText, pagination, SType, selectedRows } = this.state;
    const { info, visible, serverModel, visibleHide } = this.props.sourceserviceModel;
    const fields = this.state.fields;
    let serverModelList = serverModel === "NewSelect" ? "修改源服务" : "新增源服务";


    console.log(BaseText,"基础数组");
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 14 }
    };
    const formItemLayout1 = {
      labelCol: { span: 8 },
      wrapperCol: { span: 11 }
    };
    function displayRender(label) {
      return label[label.length - 1];
    }

    return (
      <div style={{ margin: 20 }}>
        <Form className="btn_std_group">
          <CustomizedForm {...fields} 
            onChange={this.handleFormChange} 
            handleClickBySearch={this.handleClickBySearch} 
            handleEdit1={this.handleEdit1.bind(this)}
            handleDelete={this.handleDelete}
            selectedRows={selectedRows}
          />
          <Modal
            title={serverModelList}
            visible={visible}
            width={"40%"}
            footer={[
              <Button key="back" size="large" onClick={this.handleCancel.bind(this)}>取消</Button>,
              <Button key="submit" disabled={serverModel === "selectClick" || visibleHide === false} type="primary" size="large" onClick={this.handleOk}>确定</Button>,
            ]}
            onCancel={this.handleCancel.bind(this)} >
            <FormItem label="提供方" {...formItemLayout}>
              {getFieldDecorator('providerId', {
                initialValue: info.providerId,
                rules: [{ required: true, message: '请输入服务提供方' }]
              })(
                <Cascader disabled={serverModel === "selectClick"} placeholder="请输入服务提供方" displayRender={displayRender} options={departmentsTree} onChange={this.onChange} expandTrigger="hover" style={{ width: '100%' }} />
              )}
            </FormItem>
            <FormItem label="服务名称" {...formItemLayout}>
              {getFieldDecorator('serviceName', {
                initialValue: info.serviceName,
                rules: [{ required: true, message: '请输入服务名称' }]
              })(
                <Input disabled={serverModel === "selectClick"} maxLength="255" />
              )}
            </FormItem>
            <FormItem label="服务代码" {...formItemLayout}>
              {getFieldDecorator('serviceCode', {
                initialValue: info.serviceCode,
                rules: [{ required: true, message: '请输入服务代码' }]
              })(
                <Input disabled={serverModel === "selectClick"} maxLength="500" placeholder="请填写服务代码" />
              )}
            </FormItem>
            <FormItem label="服务类型" {...formItemLayout}>
              {getFieldDecorator('serviceType', {
                initialValue: info.serviceType,
                rules: [{ required: true, message: '请输入服务类型' }]
              })(
                <Select onChange={this.handleChange} style={{ width: '100%' }} disabled={serverModel === "selectClick"}>
                  <Option value="SOAP">SOAP</Option>
                  <Option value="RESTful">RESTful</Option>
                </Select>
              )}
            </FormItem>
            <FormItem label="服务说明" {...formItemLayout}>
              {getFieldDecorator('remark', {
                initialValue: info.remark,
                rules: [{ required: true, message: '请输入服务说明' }]
              })(
                <TextArea rows={3} maxLength="500" disabled={serverModel === "selectClick"} />
              )}
            </FormItem>
            <FormItem label="访问地址" {...formItemLayout1}>
              {getFieldDecorator('url', {
                initialValue: info.url,
                rules: [{ required: true, message: '请输入访问地址' },
                { pattern: /[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\.?/, message: '请输入正确的地址' }]
              })(
                <TextArea rows={2} maxLength="500" disabled={serverModel === "selectClick"} />
              )}
              <Button style={{ float: "right", margin: "9px -4.5rem 0px 0px" }} onClick={this.getXmlDetails.bind(this)}>获取</Button>
            </FormItem>
            {
              SType === "SOAP" ? (
                <FormItem label="服务描述" {...formItemLayout}>
                  {getFieldDecorator('wsdl', {
                    initialValue: info.wsdl,
                    rules: [{ required: true, message: '请输入服务描述' }]
                  })(
                    <TextArea rows={10} maxLength="500" disabled />
                  )}
                </FormItem>
              ) : (
                  <FormItem label="服务描述" {...formItemLayout}>
                    {getFieldDecorator('wsdl', {
                      initialValue: info.wsdl,
                    })(
                      <TextArea disabled rows={10} />
                    )}
                  </FormItem>
                )}
          </Modal>
        </Form>

        <div>
          <TableList
            showIndex
            rowSelection={this.rowSelection}
            onRowClick={() => { return false }}
            rowKey={record=>record.id}
            style={{ marginTop: 20 }}
            columns={this.columns}
            loading={this.state.loading}
            dataSource={BaseText}
            pagination={pagination}
            className="th-nowrap"
          />
        </div>
      </div>
    );
  }
}
const ServerForm = Form.create()(Server);
export default connect(({ sourceserviceModel, resourcesCommon }) => ({
  sourceserviceModel, resourcesCommon
}))(ServerForm);