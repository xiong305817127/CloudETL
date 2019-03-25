import React from 'react';
import { connect } from 'dva';
import { Row, Col, Button, Form, Input, Select, Cascader, Tooltip, message,Divider } from 'antd';
import TableList from "components/TableList";
import Modal from 'components/Modal';
import FileUpload from 'components/FileUpload/FileUpload';
import { MLZYgetAllServicePages, MLZYgetServiceById, MLZYsaveOrUpdateServer, MLZYdeleteServiceById,getWSDLContents,ServerdDelete } from 'services/DirectoryOverview';
import Style from '../style.css';
import __ from "lodash";
import { sureConfirm } from "utils/utils";
import moment from "moment";
import { API_BASE_CATALOG } from 'constants';
import {downloadFile } from 'utils/utils';


const FormItem = Form.Item;
const Option = Select.Option;
const { TextArea } = Input;

const formItemLayout1 = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
}

const CustomizedForm1 = Form.create({
  onFieldsChange(props, changedFields) {
    props.onChange(changedFields);
  },
  onValuesChange(_, values) {
  },
})((props) => {
  const { getFieldDecorator } = props.form;
  const { handleClickBySearch, handleEdit1, handleDelete,BaseText,selectedRows} = props;

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
        <Button style={{ marginLeft: 10 }} type="primary" onClick={handleEdit1.bind(this)} >新增</Button>
        <Button disabled={BaseText.length === 0 || selectedRows.length === 0} style={{ marginLeft: 10 }} type="primary" onClick={handleDelete} >删除</Button>
      </Col>
    </Row>
  );
});


class ServerModel extends React.Component {
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
          value: '',
        },
        username: {
          value: 'benjycui',
        },
        usernamese: {
          value: '999',
        },
      },
      pagination: {
        page: 1,
        pageSize: 10
      },
      BaseText: [],
      info: '',
      visible: false,  //新增弹出框
      serverDatile:false,//详情弹出框
      option: [],
      selectedRows: [],
      SType: '',
      preInfo: "",
      ServerId:[],
    }
  }
//model发生变化时执行
  componentWillReceiveProps(nextProps) {
    const { pagination, BaseText, loading, total } = nextProps.shareserviceModel;
    this.setState({ pagination, BaseText, loading, total });
  }


  /*表单*/
  columns = [
    {
      title: '服务代码',
      dataIndex: 'serviceCode',
      key: 'serviceCode',
      width: '10%',
      filterMultiple: false,
      onFilter: (value, record) => record.serviceCode.indexOf(value) === 0,
      sorter: (a, b) => a.serviceCode.length - b.serviceCode.length,
    }, {
      title: '服务名称',
      dataIndex: 'serviceName',
      key: 'serviceName',
      width: '10%',
    }, {
      title: '提供方',
      dataIndex: 'providerName',
      key: 'providerName',
      width: '20%',
      render: (text, record) => {
        return `${text}(${record.serviceCode})`
      }
    }, {
      title: '服务类型',
      dataIndex: 'serviceType',
      key: 'serviceType',
      width: '5%',
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

  columns1 = [ {
    title: '文件名称',
    dataIndex: 'originFileName',
    key: 'originFileName',
    width: '70%',
  }, {
    title: '操作',
    key: 'x12',
    width: '10%',
    render: (text, record) => {
      return (<div>
        <a onClick={() => { this.handleClickDetile(record.id) }}>
          <Tooltip title="下载文件" >
            下载
          </Tooltip>
        </a>
      </div>)
    }
  }]
//下载详情文件
  handleClickDetile=(id)=>{
    downloadFile(`${API_BASE_CATALOG}/file/download?`+"id="+ id);
  }
  /*查看信息*/
  handleClick = (id) => {
    const { dispatch } = this.props;
 

    /*原服务单体查询*/
    MLZYgetServiceById({ id }).then((res) => {
      const { code, data } = res.data;
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
      let ar = {};
      ar.providerId = a;
      ar.providerName = a1;
      ar.remark = data.remark;
      ar.serviceCode = data.serviceCode;
      ar.serviceName = data.serviceName;
      ar.serviceType = data.serviceType;
      ar.url = data.url;
      ar.wsdl = data.wsdl;
      ar.technicalSupportUnit=data.technicalSupportUnit;
      ar.technicalSupportContact=data.technicalSupportContact;
      ar.technicalSupportContactNumber=data.technicalSupportContactNumber;
      ar.requestExample=data.requestExample;
      ar.successfulReturnExample = data.successfulReturnExample;
      ar.failureReturnExample=data.failureReturnExample;
      if (code === "200") {
        this.setState({
          info: ar,
          serverModel: "NewSelect",
          visible: true,
          serverId: id
        })
        dispatch({
          type: "shareserviceModel/setMetaId",
          payload: {
            serverModel: "selectClick",
            info: ar,
            visible: true,
            serverId: id,
          }
        })
      }
    })
  }

  /*表格删除数据*/
  handleDelete = () => {
    const { selectedRows } = this.state;
    console.log(selectedRows,"selectedRows===");
    if(!selectedRows) return false;
    sureConfirm({
      title:"确定删除页面选中数据吗？"
    },(bool)=>{
      if(bool){
         /*资源服务删除*/
         MLZYdeleteServiceById({ id: selectedRows }).then((res) => {
          const { code } = res.data;
          if (code === "200") {
            message.success("删除成功");
            this.setState({
              selectedRows: []
            });
            this.rowSelection.selectedRowKeys = [];
            this.searchAll();
          }
        });
      }
    })
  }

  rowSelection = {
    onChange: (selectedRowKeys, selectedRows) => {
      let selectKey = selectedRowKeys.join(',')
      this.setState({
        selectedRows: selectKey
      }),

      this.rowSelection.selectedRowKeys = selectedRowKeys
    },
    selectedRowKeys: []
  };

  /*点击新增弹框*/
  handleEdit1() {
    const { dispatch, form } = this.props;
    dispatch({
      type: "shareserviceModel/showModel",
      payload: {
        serverModel: "select",
        visible: true,
      }
    })
    this.setState({
      info: {}
    })
    form.resetFields();
  }

  /*点击编辑*/
  handleEdit = (id) => {
    const { dispatch, shareserviceModel } = this.props;
    let arr = {};
    arr.id = id;
    /*资源服务单体查询*/
    MLZYgetServiceById(arr).then((res) => {
      const { code, data, message } = res.data;
      console.log(data, "datadatadatadata");
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
      let ar = {};
      ar.providerId = a;
      ar.providerName = a1;
      ar.remark = data.remark;
      ar.serviceCode = data.serviceCode;
      ar.serviceName = data.serviceName;
      ar.serviceType = data.serviceType;
      ar.url = data.url;
      ar.wsdl = data.wsdl;
      ar.technicalSupportUnit=data.technicalSupportUnit;
      ar.technicalSupportContact=data.technicalSupportContact;
      ar.technicalSupportContactNumber=data.technicalSupportContactNumber;
      ar.requestExample=data.requestExample;
      ar.successfulReturnExample = data.successfulReturnExample;
      ar.failureReturnExample=data.failureReturnExample;
      if (code === "200") {
        this.setState({
          info: ar,
          serverModel: "NewSelect",
          visible: true,
          serverId: id
        })
        dispatch({
          type: "shareserviceModel/setMetaId",
          payload: {
            info: ar,
            serverModel: "NewSelect",
            visible: true,
            serverId: id,
            dataDateilList:data
          }
        })

      }
    })
  }

  /*点击去下弹框*/
  handleCancel() {
    const { dispatch, form } = this.props;

    dispatch({
      type: "shareserviceModel/hideModel",
      payload: {
        visible: false,
      }
    })
    form.resetFields();
  }

  /*点击保存*/
  handleOk = (e) => {
    e.preventDefault();
    const { dispatch, shareserviceModel, form } = this.props;
    const { ServerId } =this.state;
    this.props.form.validateFields({ force: false }, (err, values) => {
      if (!err) {
        /*资源服务区分《《新增》*/
        if (shareserviceModel.serverModel === "select") {
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

          arr.technicalSupportUnit=values.technicalSupportUnit;
          arr.technicalSupportContact=values.technicalSupportContact;
          arr.technicalSupportContactNumber=values.technicalSupportContactNumber;
          arr.requestExample=values.requestExample;
          arr.successfulReturnExample = values.successfulReturnExample;
          arr.failureReturnExample=values.failureReturnExample;
          arr.fileIds=ServerId;
        

          MLZYsaveOrUpdateServer(arr).then((res) => {
            const { code, data, message } = res.data;
            if (code === "200") {
              message.success("保存成功");
              this.searchAll();
              dispatch({
                type: "shareserviceModel/hideModel",
                payload: {
                  visible: false,
                  visibleHide: true,
                }
              })
              form.resetFields();
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
          /*源服务区分新增《《编辑》》*/
        } else if (shareserviceModel.serverModel === "NewSelect") {
          let a = values.providerId;
          let b = a.join(",");
          let a1 = this.state.option == "" ? shareserviceModel.info.providerName : this.state.option;
          let b1 = a1.join(",");
          var index = b1.lastIndexOf(",");
          b1 = b1.substring(index + 1, b1.length);

          let arr = {};
          arr.id = shareserviceModel.serverId;
          arr.providerId = b;
          arr.serviceName = values.serviceName;
          arr.providerName = b1;
          arr.serviceCode = values.serviceCode;
          arr.serviceType = values.serviceType;
          arr.remark = values.remark;
          arr.url = values.url;
          arr.wsdl = values.wsdl;

          arr.technicalSupportUnit=values.technicalSupportUnit;
          arr.technicalSupportContact=values.technicalSupportContact;
          arr.technicalSupportContactNumber=values.technicalSupportContactNumber;
          arr.requestExample=values.requestExample;
          arr.successfulReturnExample = values.successfulReturnExample;
          arr.failureReturnExample=values.failureReturnExample;
          arr.fileIds=ServerId;
          MLZYsaveOrUpdateServer(arr).then((res) => {
            const { code, data, message } = res.data;
            if (code === "200") {

              message.success("保存成功");
              this.searchAll();
              dispatch({
                type: "shareserviceModel/hideModel",
                payload: {
                  visible: false,
                  visibleHide: true,
                }
              })
              form.resetFields();
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
    // this.searchAll();
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
   
  }


  /**
   * 检索全部接口
   * 此方法搭配 handleClickBySearch 使用
   */
  searchAll = ()=>{
    const fields = this.state.fields;
    const { dispatch } = this.props;
    const pager = this.state.pagination;
    const { query } = this.props.location;
    let arr = {};
    arr.serviceName = fields.serviceName.value;
    arr.serviceCode = fields.serviceCode.value;
    arr.providerName = fields.providerName.value;
    arr.serviceType = fields.serviceType.value;
    arr.page = 1;
    arr.pageSize = query.pageSize ? query.pageSize : 10;

    // 开始检索
    MLZYgetAllServicePages(
        arr, {
        page: 1,
        pageSize: query.pageSize || pager.pageSize,
    })
      .then((res) => {
        const { code, data, total, message } = res.data;

        // 注意判断数据是否为空的情况
        if (code === "200" && data !== null) {
          pager.total = data.total;
          data.results.map((row, index) => {
            row.key = row.id;
            row.index = pager.pageSize * (pager.pager - 1) + index + 1;
            return row;
          });
          dispatch({
            type: "shareserviceModel/setMetaId",
            payload: {
              BaseText: data.results,
              pagination: pager,
              loading: false,
            }
         })
        } else {
          dispatch({
            type: "shareserviceModel/setMetaId",
            payload: {
              BaseText: [],
              pagination: pager,
              loading: false,
            }
          })
        }
      });
  }

  /*
   * 点击查询触发
  */
  handleClickBySearch = () => {
    const fields = this.state.fields;

    //检测是否存在空值，如果有，则
    if(__.every(fields,{value:""})){
      message.warning("请输入查询内容，如服务名、服务代码、提供方");
    }else{
      this.searchAll();
    }
  }

  handleFormChange = (changedFields) => {
    this.setState(({ fields }) => ({
      fields: { ...fields, ...changedFields },
    }));
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

  onChangelist = (pagination, filters, sorter) => {
    this.setState({
      loading: false
    })
  }

  handleChange = (value) => {
    this.setState({ SType: value })
  }
  //点击获取详情
  handleDetails=()=>{
     this.setState({
      serverDatile:true
     })
  }

  //详情取消按钮
  handleCancelDetile=()=>{
    this.setState({
      serverDatile:false
     })
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const { visible, serverModel,visibleHide,dataDateilList } = this.props.shareserviceModel;
    const {  departmentsTree } = this.props.resourcesCommon;
    const { info, BaseText, pagination, SType, selectedRows,ServerId,serverDatile } = this.state;
    const fields = this.state.fields;
    let serverModelList = serverModel === "NewSelect" ? "修改共享服务":"" ||serverModel === "select" ?"新增共享服务":""||serverModel === "selectClick" ?"查看共享服务":"";
    const _this = this;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 }
    };
    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 11 }
    };
    const formItemLayout2={
      labelCol: { span: 8 },
      wrapperCol: { span: 12 }
    }
    function displayRender(label) {
      return label[label.length - 1];
    }

    const fileUploadProps = {
      fileName:"files",
      uploadUrl: `${API_BASE_CATALOG}/file/upload`,
      onRemove:(file)=>{
        let data = file.response.data;
        let id;
        for(let key of data){ if(key.id){ id = key.id; }}
        ServerdDelete(id).then((res)=>{ let code = res.data.code;if(code==="200"){message.info(res.data.data) }})
      },
      handleCallback:(fileList)=>{
        console.log(fileList,"fileList====");
        for(let index of fileList){
          for(let key of index.response.data){
            ServerId.push(key.id);
          }
        }
        _this.setState({ServerId:ServerId})
      }
    }

    return (
      <div style={{ margin: 20 }}>
        <Form className="btn_std_group">
            <CustomizedForm1 
              {...fields} 
              onChange={this.handleFormChange}
              onChange={this.handleFormChange} 
              handleClickBySearch={this.handleClickBySearch} 
              handleEdit1={this.handleEdit1.bind(this)}
              handleDelete={this.handleDelete}
              selectedRows={selectedRows}
              BaseText={BaseText}
            />
          <Modal
            title={serverModelList}
            visible={visible}
            width={600}
            footer={null}
            onCancel={this.handleCancel.bind(this)} >
            <FormItem label="提供方" {...formItemLayout}>
              {getFieldDecorator('providerId', {
                initialValue: info.providerId ? info.providerId : [],
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
                <Select disabled={serverModel === "selectClick"} onChange={this.handleChange} style={{ width: '100%' }}>
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
                <TextArea disabled={serverModel === "selectClick"} rows={3} maxLength="500" />
              )}
            </FormItem>
            <Row>
              <Col>
                <FormItem label="访问地址" {...formItemLayout1}>
                  {getFieldDecorator('url', {
                    initialValue: info.url,
                    validateTrigger: 'onBlur',
                    rules: [{ required: true, message: '请输入访问地址' },
                    { pattern: /[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\.?/, message: '请输入正确的地址' }]
                  })(
                    <TextArea disabled={serverModel === "selectClick"} rows={2} maxLength="500" />
                  )}
                  <Button style={{ float: "right", margin: "9px -4.5rem 0px 0px" }} disabled={serverModel === "selectClick" || visibleHide === false} onClick={this.getXmlDetails.bind(this)}>获取</Button>
                </FormItem>

              </Col>
            </Row>


            <Divider orientation="left">技术支持</Divider>
            <FormItem label="技术支持单位" {...formItemLayout2}>
              {getFieldDecorator('technicalSupportUnit', {
                initialValue: info.technicalSupportUnit,
                rules: [{ required: true, message: '请输入技术支持单位' }]
              })(
                <Input maxLength="500" placeholder="请输入技术支持单位"  disabled={serverModel=== "selectClick"}/>
              )}
            </FormItem>
            <FormItem label="技术支持单位联系人" {...formItemLayout2}>
              {getFieldDecorator('technicalSupportContact', {
                initialValue: info.technicalSupportContact,
                rules: [{ required: true, message: '请输入技术支持单位联系人' }]
              })(
                <Input maxLength="500" placeholder="请输入技术支持单位联系人" disabled={serverModel=== "selectClick"}/>
              )}
            </FormItem>
            <FormItem label="技术支持单位联系电话" {...formItemLayout2}>
              {getFieldDecorator('technicalSupportContactNumber', {
                initialValue: info.technicalSupportContactNumber,
                rules: [{ required: true, message: '请输入技术支持单位联系电话' }]
              })(
                <Input maxLength="500" placeholder="请输入技术支持单位联系电话" disabled={serverModel=== "selectClick"}/>
              )}
            </FormItem>
             {
               serverModel!== "selectClick"?(
                <FormItem label="说明文档" {...formItemLayout} >
                  <FileUpload {...fileUploadProps} />
                </FormItem>
               ):null}
            

          
            <FormItem label="请求示例" {...formItemLayout}>
              {getFieldDecorator('requestExample', {
                initialValue: info.requestExample,
              })(
                <TextArea rows={3} disabled={serverModel=== "selectClick"}/>
              )}
            </FormItem>
            <FormItem label="成功返回示例" {...formItemLayout}>
              {getFieldDecorator('successfulReturnExample', {
                initialValue: info.successfulReturnExample,
              })(
                <TextArea rows={3} disabled={serverModel=== "selectClick"}/>
              )}
            </FormItem>
            <FormItem label="失败返回示例" {...formItemLayout}>
              {getFieldDecorator('failureReturnExample', {
                initialValue: info.failureReturnExample,
              })(
                <TextArea rows={3} disabled={serverModel=== "selectClick"}/>
              )}
            </FormItem>
            <Row>
              <Col style={{float:"right"}}>
                <Button key="back" size="large" style={{marginRight:"10"}} onClick={this.handleCancel.bind(this)}>取消</Button>
                <Button key="submit" disabled={serverModel === "selectClick" || visibleHide === false} type="primary" size="large" onClick={this.handleOk}>确定</Button>
              </Col>
             
               { serverModel !== "select"?(
                  <Col style={{float:"left"}} >
                    <Button key="Details" size="large"  type="primary" onClick={this.handleDetails.bind(this)}>查看详情</Button>
                  </Col>
                    
               ):null}
              
            </Row>
             
          </Modal>
          


          <Modal
            title={"服务详情"}
            visible={serverDatile}
            maskClosable={false}
            width={600}
            footer={[
              <Button key="back" size="large" onClick={this.handleCancelDetile.bind(this)}>取消</Button>
            ]}
             onCancel={this.handleCancelDetile.bind(this)} >
            <div className={Style.view}>
                <table style={{width: '100%'}}>
                    <tbody>
                      <tr>
                        <td>服务名称:</td>
                        <td>{dataDateilList.serviceName}</td>
                      </tr>
                      <tr>
                        <td>服务代码:</td>
                        <td>{dataDateilList.serviceCode}</td>
                      </tr>
                      <tr>
                        <td>服务类型:</td>
                        <td>{dataDateilList.serviceType}</td>
                      </tr>
                      {/* <tr>
                        <td>服务提供方:</td>
                        <td>{dataDateilList.serviceName}</td>
                      </tr> */}
                      <tr>
                        <td>访问地址:</td>
                        <td>{dataDateilList.url}</td>
                      </tr>
                      <tr>
                        <td>技术支持单位:</td>
                        <td>{dataDateilList.technicalSupportUnit}</td>
                      </tr>
                      <tr>
                        <td>技术支持单位联系人:</td>
                        <td>{dataDateilList.technicalSupportContact}</td>
                      </tr>
                      <tr>
                        <td>技术支持单位联系电话:</td>
                        <td>{dataDateilList.technicalSupportContactNumber}</td>
                      </tr>
                      <tr>
                        <td>服务说明:</td>
                        <td>{dataDateilList.remark}</td>
                      </tr>
                    </tbody>
                  </table>                       
            </div>
                <TableList
                  showIndex
                  style={{ marginTop: 20 }}
                  columns={this.columns1}
                  dataSource={dataDateilList.fileList}
                  pagination={false}
                  className="th-nowrap"
              />
              <FormItem label="请求示例" {...formItemLayout}>
                {getFieldDecorator('requestExample', {
                  initialValue: dataDateilList.requestExample,
                })(
                  <TextArea rows={3} disabled={serverModel=== "selectClick"}/>
                )}
              </FormItem>
              <FormItem label="成功返回示例" {...formItemLayout}>
                {getFieldDecorator('successfulReturnExample', {
                  initialValue: dataDateilList.successfulReturnExample,
                })(
                  <TextArea rows={3} disabled={serverModel=== "selectClick"}/>
                )}
              </FormItem>
              <FormItem label="失败返回示例" {...formItemLayout}>
                {getFieldDecorator('failureReturnExample', {
                  initialValue: dataDateilList.failureReturnExample,
                })(
                  <TextArea rows={3} disabled={serverModel=== "selectClick"}/>
                )}
              </FormItem>                    
            </Modal> 
        </Form>
        <div >
          <TableList
            showIndex
            rowSelection={this.rowSelection}
            onRowClick={() => { return false }}
            style={{ marginTop: 20 }}
            columns={this.columns}
            loading={this.state.loading}
            dataSource={BaseText}
            pagination={pagination}
            rowKey={record=>record.id}
            className="th-nowrap"
            onChange={this.onChangelist}
          />
        </div>
      </div>

    );
  }
}
const ServerModelForm = Form.create()(ServerModel);

export default connect(({ shareserviceModel, resourcesCommon }) => ({
  shareserviceModel, resourcesCommon
}))(ServerModelForm);