import React from "react";
import { connect } from 'dva';
import { Row, Col, Button, Form, Input, Select, Tooltip, Popconfirm, Cascader, message, Card, Divider } from 'antd';

const __message = message;

const FormItem = Form.Item;
const Option = Select.Option;
import TableList from "components/TableList";
import Modal from 'components/Modal';

import {
  deleteTerminalManageRecordById, getTerminalManageRecordsByCondition, saveOrUpdateTerminalManage, getTerminalManageRecordById,
  getDeptServer, getFSDatabase, getFSSftp, isExistedTerminalManageRecord
} from 'services/DirectoryOverview';  //调用接口参数
import { getDbSchemasByDsId } from 'services/metadataCommon'; //调用接口参数

const formItemLayout1 = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
}
//CustomizedForm封装组件
const CustomizedForm = Form.create({
  onFieldsChange(props, changedFields) {
    props.onChange(changedFields);
  },

  onValuesChange(_, values) {
    //console.log(values);
  },
})((props) => {
  const { getFieldDecorator } = props.form;
  return (
    <div>
      <Col span={8} style={{ display: 'block' }}>
        <FormItem label="部门名称" {...formItemLayout1}>
          {getFieldDecorator("deptName", {
          })(
            <Input />
          )}
        </FormItem>
      </Col>
      <Col span={8} style={{ display: 'block' }}>
        <FormItem label={"数据库"} {...formItemLayout1} >
          {getFieldDecorator("dbName", {
          })(
            <Input />
          )}
        </FormItem>
      </Col>
    </div>
  );
});

class frontlist extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      fields: {
        deptName: { value: '', },
        dbName: { value: '', },
      },
      pagination: {
        page: 1,
        pageSize: 10
      },
      serverip: "",
      total: 0,
      data: [],
    }
  }


  columns = [
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
      render: (text) => {
        const alRoot = text === "" ? "无" : "已安装";
        return (
          <span>{alRoot}</span>
        )
      }
    }, {
      title: '操作',
      dataIndex: 'endTime',
      key: 'endTime',
      render: (text, record) => {
        return (<div>
          <a onClick={() => { this.handleClickSelect(record.id) }}>
            <Tooltip title="查看" >
              查看&nbsp;&nbsp;&nbsp;&nbsp;
            </Tooltip>
          </a>
          <a onClick={() => { this.handleClick(record.id) }}>
            <Tooltip title="编辑" >
              编辑&nbsp;&nbsp;&nbsp;&nbsp;
            </Tooltip>
          </a>

          <Popconfirm title="确定删除吗?" onConfirm={() => { this.handleDelect(record.id) }} onCancel={() => { this.cancel }} okText="是" cancelText="否">
            <a>
              <Tooltip title="删除" >
                删除&nbsp;&nbsp;&nbsp;&nbsp;
	          </Tooltip>
            </a>
          </Popconfirm>
        </div>)
      }
    }];
//是否确认删除的提示返回
  cancel = (e) => {
    console.log(e);
  }

  /*点击删除*/
  handleDelect = (id) => {
    let ids = {};
    ids.id = id;
    deleteTerminalManageRecordById(ids).then((res) => {
      const { code, data, message } = res.data;
      if (code === "200") {
        this.require();  //删除成功调用渲染表格接口
      } 
    })
  }

  /*点击编辑*/
  handleClick = (id) => {
    const { dispatch } = this.props;
    let ids = {};
    ids.id = id;
    getTerminalManageRecordById(ids).then((res) => {
      const { code, data, message } = res.data;
      let arge = [];
      let strList = data.deptId;
      let strList1 = data.deptName;
      /* debugger;
      var a = strList[strList.length-1];
      var a1 = strList1[strList1.length-1]*/
      var a = strList.split(','); //获取部门数据是个数组，需要转成字符串
      var a1 = strList1.split(',');
      for (let ket of a) {
        for (let ket1 of a1) {
          arge.push({
            label: ket,
            value: ket1
          })
        }
      }
      let datalist = {};
      datalist.deptId = a;
      datalist.deptName = a1;
      datalist.tmName = data.tmName;
      datalist.tmIP = data.tmIP;
      datalist.tmDBName = data.tmDBName;
      datalist.tmDBPort = data.tmDBPort;
      datalist.sftpSwitchRoot = data.sftpSwitchRoot;
      datalist.sftpPort = data.sftpPort;
      datalist.hdfsSwitchRoot = "/switch";  //先默认为/switch路径
      datalist.tmDBType = data.tmDBType;
      datalist.sftpUsername = data.sftpUsername;
      datalist.tmDBId = data.tmDBId;
      datalist.schemaName = data.schemaName;
      if (code === "200") {
        dispatch({
          type: "frontModel/showModel",
          payload: {
            visible: true,
            text: datalist,
            model: "typeId",
            newidTy: data.id,
          }
        })
      }
    })
    this.props.form.resetFields();  //保存成功后清楚输入框的数据
  }

  /*點擊查看*/
  handleClickSelect = (id) => {
    const { dispatch } = this.props;
    let ids = {};
    ids.id = id;
    getTerminalManageRecordById(ids).then((res) => {
      const { code, data, message } = res.data;
      let arge = [];
      let str = data.deptId;
      let str1 = data.deptName;
      var a = str.split(',');//查看的时候类型需要转换
      var a1 = str1.split(','); //部门id和部门名称都需要转换对应
      for (let ket of a) {
        for (let ket1 of a1) {
          arge.push({
            label: ket,
            value: ket1
          })
        }
      }
      let datalist = {};
      datalist.deptId = a;
      datalist.deptName = a1;
      datalist.tmName = data.tmName;
      datalist.tmIP = data.tmIP;
      datalist.tmDBName = data.tmDBName;
      datalist.tmDBPort = data.tmDBPort;
      datalist.sftpSwitchRoot = data.sftpSwitchRoot;
      datalist.sftpPort = data.sftpPort;
      datalist.hdfsSwitchRoot = "/switch";
      datalist.tmDBType = data.tmDBType;
      datalist.sftpUsername = data.sftpUsername;
      datalist.schemaName = data.schemaName;
      if (code === "200") {
        dispatch({
          type: "frontModel/showModel",
          payload: {
            visible: true,
            text: datalist,
            model: "typeSelect",   // model: "typeSelect",输入框禁止输入
            newidTy: data.id,
          }
        })
      }
    })
    this.props.form.resetFields();   //清空数据
  }
//从model里面拿到数据然后再保存到state里面去
  componentWillReceiveProps(nextProps) {
    const { pagination, data, loading, total } = nextProps.frontModel;
    const { dispatch } = this.props;
    this.setState({ pagination, data, loading, total })
  }

  //初始化获取部门接口，渲染数据
  componentWillMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'resourcesCommon/getAllDepartments' }); 
    dispatch({ type: 'resourcesCommon/getDepartments' });
  }
  /*查询接口*/
  require() {
    const fields = this.state.fields;
    const { dispatch } = this.props;
    const pager = this.props.frontModel.pagination;
    const { query } = this.props.location;
    let arr = {};
    arr.deptName = fields.deptName.value;
    arr.dbName = fields.dbName.value;
    arr.pageNum = 1;
    arr.pageSize = query.pageSize ? query.pageSize : 10;
    getTerminalManageRecordsByCondition(arr, {
      pageNum: 1,
      pageSize: query.pageSize || pager.pageSize,
    }).then((res) => {
      const { code, data} = res.data;
      if (code === "200") {  //返回结果是否成功为200则是成功
        if (data === null) {  //data如果等于null前端会报错，所以需要再做个判断，如果为null就不返回
          dispatch({
            type: "frontModel/setMetaId",
            payload: { data: [], pagination: pager, }
          })
          this.setState({ data: [], pagination: pager, })
        } else {
          pager.total = data.total == null ? 1 : data.total;
          pager.total = data.total;
          data.results.map((row, index) => {
            row.key = row.id;
            row.index = pager.pageSize * (pager.page - 1) + index + 1;
            return row;
          });
          dispatch({
            type: "frontModel/setMetaId",
            payload: { data: data.results, pagination: pager, }
          })
          this.setState({ pagination: pager, data: data.results, })
        }
      } else {
        dispatch({
          type: "frontModel/setMetaId",
          payload: { data: [], pagination: pager, }
        })
        this.setState({ data: [], pagination: pager, })
      }
    });
  }


  /*点击弹出框取消*/
  handleNo() {
    const { dispatch } = this.props;
    dispatch({
      type: "frontModel/hideModel",
      payload: {
        visible: false,
        text: "",
      }
    })
    this.props.form.resetFields();  //取消后，清除数据
  }



  /*点击弹出框确定 保存*/
  handleOK() {
    const { dispatch } = this.props;
    const { model, deptId, deptCode, deptName, newidTy, dbUser, database, text, dsId, tmDBName, tmName, tmDBId, serverip } = this.props.frontModel;
    this.props.form.validateFields((err, values) => {
      let a = values.deptId;
      let b = a.join(",");
      if (!err) {
        if (model === "newtype") {   //这是新建的保存
          let exid = {};
          exid.deptId = deptId;
          exid.id = null;  //新建的时候id默认为空，不传值
          isExistedTerminalManageRecord(exid).then((res) => {
            const { code, data, message } = res.data;
            if (code === "200") {
              let dsids = "";
              for (let index of database) {
                dsids = index.dsId;
              }

              let arr = {};
              arr.deptId = b;
              arr.tmDBId = tmDBId;
              arr.deptFinalId = deptId;
              arr.deptCode = deptCode;
              arr.deptName = deptName;
              arr.tmName = tmName;
              arr.tmIP = serverip;
              /* arr.tmDbId=dsId;*/
              arr.tmDBName = tmDBName;
              arr.tmDBPort = values.tmDBPort;
              arr.sftpSwitchRoot = values.sftpSwitchRoot;
              arr.sftpPort = values.sftpPort;
              arr.hdfsSwitchRoot = "/switch";
              arr.tmDBType = values.tmDBType;
              arr.schemaName = values.schemaName;
              arr.sftpUsername = Object.prototype.toString.call(values.sftpUsername) === "[object Array]" ? "" : values.sftpUsername;
              //arr将所有的值都拼凑后台所需的格式进行保存
              saveOrUpdateTerminalManage(arr).then((res) => {
                const { code, message } = res.data;
                if (code === "200") {
                  //保存后关闭弹出框
                  dispatch({
                    type: "frontModel/hideModel",
                    payload: {
                      visible: false,
                    }
                  })
                  __message.success("保存成功");
                  this.props.form.resetFields();  //保存后将数据清除
                  this.require();
                } 
              })
            } 
          }).catch(err => {
            console.log(err);
          });

        } else if (model === "typeId") {  //model === "typeId" 为编辑
          let finId = values.deptId[values.deptId.length - 1]
          let exid = {};
          exid.deptId = finId;  //部门类型
          exid.id = newidTy;    //编辑id
          //arr1将所有的值都拼凑后台所需的格式进行保存
          isExistedTerminalManageRecord(exid).then((res) => {   //这是修改的保存
            const { code, data, message } = res.data;
            if (code === "200") {
              let arr1 = {};
              arr1.id = newidTy;
              arr1.deptId = b;
              arr1.deptFinalId = finId;
              arr1.deptCode = deptCode === "" ? text.deptCode : deptCode;
              arr1.deptName = deptName === "" ? text.deptName : deptName;
              arr1.tmName = tmName === "" ? text.tmName : tmName;
              arr1.tmDBId = tmDBId === "" ? text.tmDBId : tmDBId;
              arr1.tmIP = serverip === "" ? text.tmIP : serverip;
              arr1.tmDBName = tmDBName === "" ? text.tmDBName : tmDBName;
              arr1.tmDBPort = values.tmDBPort;
              arr1.sftpSwitchRoot = values.sftpSwitchRoot;
              arr1.sftpPort = values.sftpPort;
              arr1.hdfsSwitchRoot = "/switch";
              arr1.tmDBType = values.tmDBType;
              arr1.schemaName = values.schemaName;
              arr1.sftpUsername = Object.prototype.toString.call(values.sftpUsername) === "[object Array]" ? "" : values.sftpUsername;

              saveOrUpdateTerminalManage(arr1).then((res) => {
                const { code, data, message } = res.data;
                if (code === "200") {
                  dispatch({
                    type: "frontModel/hideModel",
                    payload: {
                      visible: false,
                    }
                  })
                  this.props.form.resetFields();
                  this.require();
                  __message.success("保存成功");
                } 
              })
            } 
          })

        }

      }
    })

  }


  /*点击新增*/
  handletrue() {
    const { dispatch } = this.props;
    dispatch({
      type: "frontModel/showModel",
      payload: {
        visible: true,
        model: "newtype",
      }
    })
    this.props.form.resetFields();
  }

  /*获取搜索框的值*/
  handleFormChange = (changedFields) => {
    this.setState(({ fields }) => ({
      fields: { ...fields, ...changedFields },
    }));
  }
  /*选择部门*/
  onChange = (e, label) => {

    const { dispatch } = this.props;
    const { setFieldsValue } = this.props.form;
    getDeptServer(parseInt(label[label.length - 1].value)).then((res) => {
      const { code, data } = res.data;
      if (code === '200') {
        setFieldsValue({
          tmName: "", tmDBName: "",schemaName:""
        })
        dispatch({
          type: 'frontModel/setMetaId',
          payload: {
            dataServer: data,
            deptId: label[label.length - 1].value,   //获取部门id以及名称
            dbUser: name,
            deptCode: label[label.length - 1].code,
            deptName: label[label.length - 1].label,

          }
        });
      }
    })
  }

  /*选择数据库*/
  handleChange = (value, label, id) => {
    console.log(value, label, id,"value, label, id1111");
    let ids = label === undefined ? "" : label.key; //label初始化是为空，所以在这里校验一下为空就不返回了
    let valueDateli = value.substr(value.indexOf(',') + 1);
    const { dispatch } = this.props;
    getFSDatabase(ids).then((res) => {
      const { code, data } = res.data;
      if (code === '200') {
        dispatch({
          type: 'frontModel/setMetaId',
          payload: {
            Sid: label.key,   //数据库id
            database: data,
            tmName: label.props.children,  //数据库的名称树形结构
            serverip: valueDateli,   //当前选择数据库的名称
          }
        });
      }
    })
  }

  /*获取数据库信息*/
  handleChangeBase = (value, label) => {
    console.log(label,"labelcinxi");
    const { dispatch } = this.props;
    const { database, Sid } = this.props.frontModel;
    let dbPort = "";
    let dsType = "";
    /**
     * 遍历部门接口，获取他的ip和类型
     */
    for (let index of database) {
      dbPort = index.frontEndServer.dbPort;
      dsType = index.dsType;
    }
    //获取sftp接口并获取ip端口和名称和类型
    getFSSftp(Sid).then((res) => {
      const { code, data } = res.data;
      if (code === "200") {
        dispatch({
          type: 'frontModel/setMetaId',
          payload: {
            tmDBPort: dbPort,
            tmDBType: dsType,
            sFTPbase: data,
            tmDBId: label.key,
            tmDBName: label.props.children
          }
        })
      }
    })
    //获取前置信息接口
    getDbSchemasByDsId(label.key).then((res) => {
      const { code, data } = res.data;
      if (code === "200") {
        let arge=[];
          for (var i in data){
               arge.push({
                  name:data[i]
               })
            }
        dispatch({
          type: 'frontModel/setMetaId',
             payload:{
              schemaList:arge
             }
        })
      }
    })
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const { departmentsTree } = this.props.resourcesCommon;
    const { text, visible, model, dataServer, database, tmDBPort, tmDBType, sFTPbase,schemaList } = this.props.frontModel;
    const fields = this.state.fields;
    const { pagination, data } = this.state;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };
    console.log(schemaList,"schemaList");
    function displayRender(label) {
      return label[label.length - 1];
    }
    let type = model === "newtype" ? "前置新增" : "前置编辑";
    let Basetype = tmDBType === 2 ? "oracle" : "" || tmDBType === 3 ? "mysql" : "" || tmDBType === 4 ? "hive" : "" || tmDBType === 5 ? "hbase" : "" || tmDBType === 14 ? "dm" : "" || tmDBType === 8 ? "PostgreSQL" : "";

    return (
      <div style={{ margin: 20 }}>
        <Form className="ant-advanced-search-form" >
          <div style={{padding:"0 10px 0 10px"}}>
            <Row gutter={20} className="btn_std_group">
              <CustomizedForm {...fields} onChange={this.handleFormChange} />
              <Col span={24} className="search_btn">
                <Button type="primary" onClick={this.require.bind(this)}> 查询</Button>
                <Button style={{ marginLeft: 10 }} type="primary" onClick={this.handletrue.bind(this)}> 新增</Button>
              </Col>
            </Row>
          </div>

          <Modal
            title={type}
            visible={visible}
            width={600}
            footer={[
              <Button key="back" size="large" onClick={this.handleNo.bind(this)}>取消</Button>,
              <Button disabled={model === "typeSelect"} key="submit" type="primary" size="large" onClick={this.handleOK.bind(this)}>确定</Button>,
            ]}
            onCancel={this.handleNo.bind(this)} >

            <Divider>前置机</Divider>
          {/* <Card style={{ width: "100%" }}> */}
            <FormItem label="部门" {...formItemLayout}>
              {getFieldDecorator('deptId', {
                initialValue: text.deptId ? text.deptId : [],
                rules: [{ required: true, message: '请选择部门' }]
              })(
                <Cascader allowClear={false} disabled={model === "typeSelect"} placeholder="请选择部门" displayRender={displayRender} options={departmentsTree} onChange={this.onChange} expandTrigger="hover" style={{ width: '100%' }} />
              )}
            </FormItem>
            <FormItem label="服务器" {...formItemLayout}>
              {getFieldDecorator('tmName', {
                initialValue: text.tmName ? text.tmName : [],
                validateTrigger: 'onBlur',
                rules: [{ required: true, message: '请选择服务器' }]
              })(
                <Select allowClear disabled={model === "typeSelect"} placeholder="请选择所在前置机" onChange={this.handleChange} style={{ width: '100%' }}>
                  {
                    dataServer.map((index,text) =>
                      <Option key={index.id} value={index.serverName+","+index.serverIp}>{index.serverName}</Option>)

                  }
                </Select>
              )}
            </FormItem>
           {/* </Card> */}
            <Divider >数据库</Divider>
            <FormItem label="数据库实例" {...formItemLayout}>
              {getFieldDecorator('tmDBName', {
                initialValue: text.tmDBName ? text.tmDBName : [],
                validateTrigger: 'onBlur',
                rules: [{ required: true, message: '请选择数据库实例' }]
              })(
                <Select allowClear disabled={model === "typeSelect"} placeholder="请选择数据库实例" onChange={this.handleChangeBase} style={{ width: '100%' }}>
                  {
                    database.map((index) =>
                      <Option key={index.dsId} value={index.dbDatabasename + "," + index.dsId}>{index.dbDatabasename}</Option>)
                  }
                </Select>
              )}
            </FormItem>
            {/* <Card style={{ width: "100%"}}>*/}
             <FormItem label="模式" {...formItemLayout}>
                {getFieldDecorator('schemaName', {
                    initialValue: text.schemaName ? text.schemaName : "",
                    validateTrigger: 'onBlur'
                })(
                    <Select allowClear placeholder="请选择模式名称" style={{ width: '100%' }} >
                        {
                            schemaList.map((index) => <Option key={index.id} value={index.name}>{index.name}</Option>)
                        }
                    </Select>
                )}
            </FormItem>
           {/* </Card>*/} 
            {/* 修改必选项未填写时的提示 */}
            {/* Edited By Steven Leo */}
            <FormItem label="端口" {...formItemLayout} style={{marginTop:"10px"}}>
              {getFieldDecorator('tmDBPort', {
                initialValue: tmDBPort || text.tmDBPort,
              })(
                <Input disabled />
              )}
            </FormItem>
            <FormItem label="数据库类型" {...formItemLayout}>
              {getFieldDecorator('tmDBType', {
                initialValue: Basetype || text.tmDBType,
              })(
                <Input disabled />
              )}
            </FormItem>
            <Divider >sFTP</Divider>
           
            
            {/* 屏蔽schema
                @edited by Pwj 2018/09/26
            <FormItem label="schema" {...formItemLayout}>
              {getFieldDecorator('schema', {
                initialValue: text.schema ? text.schema :"",
                validateTrigger: 'onBlur',
                rules: [{ required: true, message: '请选择schame' }]
              })(
                <Select allowClear disabled={model === "typeSelect"} placeholder="请选择schame" onChange={this.handleChangeBase} style={{ width: '100%' }}>
                {
                  database.map((index) => <Option key={index.id} value={index.dbDatabasename}>{index.dbDatabasename}</Option>)
                }
                </Select>
              )}
            </FormItem> */}
          {/* 修改一下三个字段为非必填字段，
            require: false
            @edited by Steven Leo 2018/09/19 
         */
          }
          <FormItem label="根目录" {...formItemLayout}>
            {getFieldDecorator('sftpSwitchRoot', {
              initialValue: text.sftpSwitchRoot || "/switch",
              rules: [{ required: false, message: '请选择数据库根目录' }]
            })(
              <Input disabled={model === "typeSelect"} />
            )}
          </FormItem>
          <FormItem label="端口" {...formItemLayout}>
            {getFieldDecorator('sftpPort', {
              initialValue: text.sftpPort,
              rules: [{ required: false, message: '请选择数据库端口' }]
            })(
              <Input disabled={model === "typeSelect"} />
            )}
          </FormItem>
          <FormItem label="用户名" {...formItemLayout}>
            {getFieldDecorator('sftpUsername', {
              initialValue: text.sftpUsername || [],
              rules: [{ required: false, message: '请选择数据用户名' }]
            })(
              <Select allowClear disabled={model === "typeSelect"} placeholder="请输入用户名" style={{ width: '100%' }}>
                {
                  sFTPbase.map((index) =>
                    <Select.Option key={index.id} value={index.ftpUser + ""}>{index.ftpUser}</Select.Option>
                  )
                }
              </Select>
            )}
          </FormItem>           
      </Modal>       
    </Form >
      <div>
        <TableList
          showIndex
          onRowClick={() => { return false }}
          style={{ marginTop: 20 }}
          columns={this.columns}
          dataSource={data}
          className="th-nowrap"
          pagination={pagination}
        />
      </div>
    </div >
      
    );
  }
}
const frontForm = Form.create()(frontlist);
export default connect(({ frontModel, resourcesCommon }) => ({
  frontModel, resourcesCommon
}))(frontForm);