import { connect } from 'dva';
import ReactDOM from 'react-dom';
import ReactEcharts from 'echarts-for-react';
import { withRouter, hashHistory } from 'react-router';
import { Tree, Radio, Row, Col, Icon, Divider, Button, Form, Input, Select, Steps, DatePicker, Upload, message, Tooltip, Popconfirm, Checkbox, Table } from 'antd';
const { Column, ColumnGroup } = Table
import { API_BASE_CATALOG } from 'constants';
import { uploadFile, convertArrayToTree, downloadFile } from 'utils/utils';
const FormItem = Form.Item;
const Option = Select.Option;
const Step = Steps.Step;
import TableList from "components/TableList";

import TableGenerator from "../../exchange/basicData/resourceFormat/components/TableGenerator";
import Modal from 'components/Modal';
import FileUpload from 'components/FileUpload/FileUpload';
import style from './style.less';
const dateFormat = 'YYYY/MM/DD';

import {
  MLZYdeleteDLRecordById, MLZYgetAllDateUploadRecords, MLZYsaveOrUpdateUploadData, MLZYgetPub, MLZYisExistedResourceFile, MLZYdownloadTemplate,
  getETLTaskDetailInfoById, updateDateByBrowse, MLZYupdateBatchDataUploadDetails
} from 'services/DirectoryOverview';
import { List } from 'immutable';
const steps = [{
  title: '上报数据-1',
}, {
  title: '上报数据-2',
}];

const steps1 = [{
  title: '上报数据-1',
}, {
  title: '上报数据-2',
}, {
  title: '上报数据-3',
}];

const formItemLayout1 = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
}
const CustomizedForm = Form.create({
  onFieldsChange(props, changedFields) {
    props.onChange(changedFields);
  },
  onValuesChange(_, values) {
  },
})((props) => {
  const { getFieldDecorator } = props.form;
  const { request, handleReported } = props;
  return (
    <Row>
      <Col span={8} >
        <FormItem label="资源名称" {...formItemLayout1}>
          {getFieldDecorator("name", {
          })(
            <Input />
          )}
        </FormItem>
      </Col>
      <Col span={8} >
        <FormItem label={"资源代码"} {...formItemLayout1} >
          {getFieldDecorator("code", {
          })(
            <Input />
          )}
        </FormItem>
      </Col>
      <Col span={8} >
        <FormItem label={"文件名称"} {...formItemLayout1}>
          {getFieldDecorator("pubFileName", {
          })(
            <Input />
          )}
        </FormItem>
      </Col>
      <Col span={8} >
        <FormItem label={"状态"} {...formItemLayout1} >
          {getFieldDecorator("status", {
            initialValue: "全部",
          })(
            <Select style={{ width: '100%' }} size="large">
              <Option value="">全部</Option>
              <Option value="WAIT_IMPORT">等待入库</Option>
              <Option value="IMPORTING">入库中</Option>
              <Option value="IMPORT_COMPLETE">已入库</Option>
              <Option value="IMPORT_ERROR">入库失败</Option>
              <Option value="STOP_IMPORT">终止入库</Option>
            </Select>
          )}
        </FormItem>
      </Col>
      <Col span={8}></Col>
      <Col span={8} className="search_btn">
        <Button type="primary" onClick={request} > 查询</Button>
        <Button style={{ marginLeft: 10 }} type="primary" onClick={handleReported} >上报数据</Button>
      </Col>
    </Row>
  );
});

class Myreporting extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      fields: {
        name: { value: '', },
        code: { value: '', },
        pubFileName: { value: '', },
        status: { value: '', },
      },
      current: 0,
      visible: false,
      pagination: {
        page: 1,
        pageSize: 10
      },
      total: 0,
      info: '',  //定义的类型
      nextdata: [],  //返回上一步的取值
      fileList: [],  //获取文件的类型
      fileNameH: "",  //上传的文件名称
      dataType: '',   //拿到上一步的类型
      deptCode: '',    //拿到上一步的编码
      resourceId: '',  //拿到上一步的id
      selectListPut: [],  //点击上报获取的下拉list
      resourceFullCode: '', //资源编码
      formatType: '',       //资源类型
      resId: '',            //资源id
      resourceNameS: '',    //资源名称
      dataList: [],        //渲染表格数组
      visibleList: false,
      visibleLogs: false,//点击上报状态
      visibleOK: false, //点击执行日志
      typeList: "",
      dataLog: [],
      sqlType: [],
      log: "",
      formatInfo: "",
      visibleKey: true,
      uploading: false,
      checked: true,
      dataSize: "",   //待处理数据量
      visibleSize: false,   //显示处理数量
      dataRoios: [],  //选择文档导入
      fileList: [],  //导入数据的存放
      titleTrue:false,
    }
  }

  next() {
    this.props.form.validateFields({ force: false }, (err, values) => {
      if (!err) {

        const current = this.state.current + 1;
        this.setState({ current, nextdata: values });
      }
    })
  }

  nextExcel() {
    const { dispatch } = this.props;
    this.props.form.validateFields({ force: false }, (err, values) => {
      if (!err) {
        dispatch({
          type: "reportingModel/getBrowseFormDataTitle",
          payload: {
            resourceId: this.state.resId
          }
        })

        const current = this.state.current + 1;
        this.setState({ current, nextdata: values });
      }
    })
  }

  prev() {
    const { nextdata } = this.state;
    this.setState({
      dataType: nextdata.dataType,
      deptCode: nextdata.deptCode,
      resourceId: nextdata.resourceId,
    })
    const current = this.state.current - 1;
    this.setState({ current });
  }
  /*表格*/
  columns = [
    {
      title: '上报作业',
      dataIndex: 'importTaskId',
      key: 'importTaskId',
      width: '10%',
      render: (text, record) => {
        return (
          <a title={record.subscribeId} onClick={() => { this.heandOperation(record.id) }}>{text}</a>
        )
      }
    }, {
      title: '资源代码',
      dataIndex: 'code',
      key: 'code',
      width: '8%',
    }, {
      title: '资源名称',
      dataIndex: 'name',
      key: 'name',
      width: '10%',
    }, {
      title: '文件名',
      dataIndex: 'pubFileName',
      key: 'pubFileName',
      width: '18%',
    }, {
      title: '数据批次',
      dataIndex: 'dataBatch',
      key: 'dataBatch',
      width: '7%',
      render: (text) => {
        return (
          <span>{text ? text : 0}</span>
        )
      }
    }, {
      title: '上传时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: '10%',
    }, {
      title: '入库时间',
      dataIndex: 'importTime',
      key: 'importTime',
      width: '10%',
    }, {
      title: '入库数据量',
      dataIndex: 'importCount',
      key: 'importCount',
      width: '6%',
    }, {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: '5%',
      render: (text) => {
        const all = text === "WAIT_IMPORT" ? "等待入库" : "" || text === "IMPORTING" ? "入库中" : "" || text === "IMPORT_COMPLETE" ? "已入库" : "" ||
          text === "IMPORT_ERROR" ? "入库失败" : "" || text === "STOP_IMPORT" ? "终止入库" : "";
        return (
          <span>{all}</span>
        )
      }
    }, {
      title: '资源格式',
      dataIndex: 'dataType',
      key: 'dataType',
      width: '6%',
      render: (text) => {
        const all = text === "FILE" ? "文件类型" : "数据库";
        return (
          <span>{all}</span>
        )
      }
    }, {
      title: '操作',
      key: 'x12',
      width: '6%',
      render: (text, record) => {
        return (<div>
          <a onClick={() => { this.handleDelect(record.id) }}>
            <Tooltip title="删除" >
              删除&nbsp;&nbsp;&nbsp;&nbsp;
            </Tooltip>
          </a>
        </div>)
      }
    }];
  /*弹出日志框*/
  columnslog = [
    {
      title: '名称',
      dataIndex: 'taskName',
      key: 'taskName',
      render: (text) => {
        return (
          <span>{text ? text : 0}</span>
        )
      }
    }, {
      title: '状态',
      dataIndex: 'curStatus',
      key: 'curStatus',
      render: (text) => {
        return (
          <span>{text ? text : 0}</span>
        )
      }
    }, {
      title: '操作者',
      dataIndex: 'operator',
      key: 'operator',
      render: (text) => {
        return (
          <span>{text ? text : 0}</span>
        )
      }
    }, {
      title: '开始时间',
      dataIndex: 'startTime',
      key: 'startTime',
      render: (text) => {
        return (
          <span>{text ? text : 0}</span>
        )
      }
    }, {
      title: '结束时间',
      dataIndex: 'endTime',
      key: 'endTime',
      render: (text) => {
        return (
          <span>{text ? text : 0}</span>
        )
      }
    }, {
      title: '操作',
      key: 'x12',
      render: (text, record) => {
        return (<div>
          <a onClick={() => { this.handleLogeOK() }}>
            <Tooltip title="查看执行日志" >
              查看执行日志
            </Tooltip>
          </a>
        </div>)
      }
    }];

  /*第二步筛选表格*/
  columnsOptian = [
    {
      title: '已上传文件',
      dataIndex: 'name',
      key: 'name',
      width: '20%',
    }, {
      title: '文件展示名',
      dataIndex: 'pubFileName',
      key: 'pubFileName',
      width: '35%',
      render: (text, record, index) => {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        return <FormItem style={{ margin: "0px" }}>
          {getFieldDecorator(`rows[${index}].pubFileName`, {
            initialValue: record.pubFileName,
            rules: [
              { validator: (rule, value, callback) => { this.SJCJisExistsListName(rule, value, callback) } }
            ]
          })(
            <Input onChange={(e) => { this.ValuesClick('pubFileName', e.target.value, record) }} />
          )}
        </FormItem>
      }
    }, {
      title: '文件描述',
      dataIndex: 'fileDescription',
      key: 'fileDescription',
      width: '35%',
      render: (text, record, index) => {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        return <FormItem style={{ margin: "0px" }}>
          {getFieldDecorator(`fileDescription.${index}`, {

          })(
            <Input onChange={(e) => { this.ValuesClick('fileDescription', e.target.value, record) }} />
          )}
        </FormItem>
      }
    }, {
      title: '覆盖',
      dataIndex: 'isExisted',
      key: 'isExisted',
      width: '5%',
      render: (text, record, index) => {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        return <FormItem style={{ margin: "0px", paddingLeft: "20px" }}>
          {getFieldDecorator(`rows[${index}].isExisted`, {
            initialValue: record.isExisted,
          })(
            <Checkbox onChange={(e) => { this.ValuesClick('isExisted', e.target.checked, record) }} />
          )}
        </FormItem>
      }
    }];


  ValuesClick(keyOfCol, value, record) {
    const { fileList } = this.state;
    const item = fileList.find(row => row.id === record.id);
    item[keyOfCol] = value;
    this.setState({
      fileList
    })
  }

  /*校验表名称*/
  SJCJisExistsListName = (rule, value, callback) => {
    const { fileList, resId } = this.state;
    let num = 0;
    let fileType = "";
    let canCover = false;

    let args = [];
    for (let index of fileList) {
      if (index.pubFileName === value) {
        fileType = index.fileType;
        canCover = index.isExisted;
        num++;
      }
      args.push(index.pubFileName);
    }
    if (num > 1) {
      callback("表格内展示名不允许重复！");
    }
    if (value) {
      MLZYisExistedResourceFile({
        pubFileName: [`${value}.${fileType}`],
        resourceId: resId
      }).then((res) => {
        const { code, flag } = res.data;
        if (!flag) {
          if (canCover) {
            callback();
          } else {
            callback("文件名已存在，如需覆盖请勾选！");
          }
        } else {
          callback();
        }
      })

    } else {
      callback("文件名不可为空!");
    }
  };

  heandOperation = (id) => {
    let arr = {};
    arr.id = id;
    getETLTaskDetailInfoById(arr).then((res) => {
      const { code, data, total, message } = res.data;
      if (code === "200") {
        this.setState({
          visibleLogs: true,
          dataLog: data.results,
        })
      }
    })
  }

  //点击弹框取消
  handleCancel = () => {
    this.setState({
      visible: false,
      current: 0,
      fileList: [],
    })
    this.props.form.resetFields();
  }


  /*点击上报 查询数据*/
  handleReported = () => {
    MLZYgetPub().then((res) => {
      const { code, message, data } = res.data;
      if (code === "200") {
        this.setState({
          selectListPut: data,
          current: 0,
          dataType: '',
          deptCode: '',
          resourceId: '',
          pubFileName: '',
          resourceFullCode: "",
          uploading: false,
        })
      }
    })
    this.setState({
      visible: true
    })
  }
  /*点击上报功能*/
  thisReported = (e) => {
    const { dispatch } = this.props;
    const { fileList, resId, formatType, typeList } = this.state;

    const { dataTableSubmit,dataTitleSuo,RadioCokie,visibleSuccess } =this.props.reportingModel; //dataTable
    this.props.form.validateFields({ force: true }, (err, values) => {
      if (!err) {
        let args = [];
        const dataBatch = values['dataBatch'].format('YYYY-MM-DD');
        if (formatType === 3) {
          if(RadioCokie === "Online"){
            setTimeout(() => {
              updateDateByBrowse({
                browseData: dataTableSubmit,
                titleData:dataTitleSuo,
                resourceId: resId, dataBatch, formatType,
                dataBatch:dataBatch
              }).then((res) => {
                const { code, message, data } = res.data;
                if (code === "200") {
                  this.setState({
                    visible: false,
                    fileList: [],
                    resourceFullCode: "",
                    uploading: true,
                    titleTrue:true,
                   
                  })
                  dispatch({
                    type: "reportingModel/setMetaId",
                    payload: {
                      RadioCokie:"Online",
                      successData:data,
                      visibleSuccess:true,
                    }
                  })
                  message.success("数据更新成功");
                  this.request();
                  this.props.form.resetFields();
                }
              })
            }, 300);
          }else if (fileList.length === 0 && RadioCokie === "Online") {
            message.error("请先上传文件！");
            return false;
          } else {
            setTimeout(() => {
              let oMyForm = new FormData();
              oMyForm.append("file", fileList[0]);
              oMyForm.append("resourceId", resId);
              oMyForm.append("dataBatch", dataBatch);
              oMyForm.append("formatType", typeList);
              uploadFile(`${API_BASE_CATALOG}/dataUpload/saveOrUpdateUploadDataForDB`, oMyForm, (request) => {
                const { status, response } = request;
                var data = JSON.parse(response);
                ReactDOM.findDOMNode(this.refs['submitAction']).reset();
                if (data.code === "200") {
                  message.success('文件上传成功！');
                  this.setState({
                    visible: false,
                    fileList: [],
                    visibleKey: true,
                    visibleSize: true,
                    resourceFullCode: "",
                    dataSize: data.data,
                    uploading: false
                  })
                  this.request();
                  this.props.form.resetFields();
                } else {
                  this.setState({
                    visibleKey: true,
                  });
                  message.error(data.message);
                }
              })
            }, 300);

          }

        } else {
          if (fileList.length === 0) {
            message.error("请先上传文件！");
            return false;
          }
          for (let index of fileList) {
            args.push({
              id: index.id,
              fileDescription: index.fileDescription,
              pubFileName: `${index.pubFileName}`
              /*pubFileName:`${index.pubFileName}.${index.fileType}`*/
            })
          }
          setTimeout(() => {
            MLZYupdateBatchDataUploadDetails({
              dataUploadDetailVOList: args,
              resourceId: resId, dataBatch, formatType,
              dataType: "FILE"
            }).then((res) => {
              const { code, message, data } = res.data;
              if (code === "200") {
                this.setState({
                  visible: false,
                  fileList: [],
                  resourceFullCode: "",
                  uploading: true,
                })
                message.success("数据更新成功");
                this.request();
                this.props.form.resetFields();
              }
            })
          }, 300);

        }
      }
    });
    // dispatch({
    //   type: "reportingModel/setMetaId",
    //   payload: {
    //     RadioCokie:"",
    //     titleTrue:"",
    //     dataTable:[{
    //       parent:1,
    //       data:[]
    //     }],
    //     dataTitel:[]
    //   }
    // })
  }

  componentDidMount() {
    this.request();
  }
  componentWillReceiveProps(nextProps) {
    const { pagination, dataList, loading, total } = nextProps.reportingModel;
    const { dispatch } = this.props;
    this.setState({ pagination, dataList, loading, total })
  }

  /*表格刷新查询*/
  request = () => {
    const fields = this.state.fields;
    const pager = this.state.pagination;
    const { query } = this.props.location;
    const { dispatch } = this.props;

    let arr = {};
    arr.name = fields.name.value;
    arr.code = fields.code.value;
    arr.pubFileName = fields.pubFileName.value;
    arr.status = fields.status.value;
    arr.page = 1;
    arr.pageSize = query.pageSize ? query.pageSize : 10;
    MLZYgetAllDateUploadRecords(arr, {
      page: 1,
      pageSize: query.pageSize || pager.pageSize,
    }).then((res) => {
      const { code, data, total, message } = res.data;

      if (data === null) {

        this.setState({
          dataList: [],
          pagination: {
            total: 0
          },
        })
      } else if (code === "200") {
        pager.total = data.total;
        data.results.map((row, index) => {
          row.key = row.id;
          row.index = pager.pageSize * (pager.pager - 1) + index + 1;
          return row;
        });
        dispatch({
          type: "reportingModel/setMetaId",
          payload: {
            dataList: data.results,
            pagination: pager,
          }
        })
        this.setState({
          dataList: data.results,
          pagination: pager,
        })

      } else {
        dispatch({
          type: "sourceserviceModel/setMetaId",
          payload: {
            dataList: [],
          }
        })
        this.setState({
          dataList: [],
          pagination: "",
        })
      }
    });
  }

  /*下载模板*/
  updataLast() {
    const { resId } = this.state;
    //下载：0_0
    downloadFile(`${API_BASE_CATALOG}/dataUpload/downloadTemplate?` + "resourceId=" + resId);
  }

  /*查询资源分类*/
  headMLZYgetPub = (value, dsName) => {
    console.log(value, "value,dsName", this.state);
    const { selectListPut } = this.state;

    for (let index of selectListPut) {
      if (index.id === value) {
        console.log(index, "selectListPut.resourceName");
        this.setState({
          resourceFullCode: index.resourceFullCode,
          formatType: index.formatType,
          resId: index.id,
          resourceNameS: index.resourceName,
          typeList: index.formatType,
          formatInfo: index.formatInfo,
          fileList: [],
        })
      }
    }
  }
  /*管理删除*/
  handleDelect = (id) => {
    let arr = {};
    arr.id = id;
    MLZYdeleteDLRecordById(arr).then((res) => {
      const { code, data, message } = res.data;
      if (code === "200") {
        message.success("删除成功")
      }
      this.request();
    });
  }

  /*获取搜索框的值*/
  handleFormChange = (changedFields) => {
    this.setState(({ fields }) => ({
      fields: { ...fields, ...changedFields },
    }));
  }

  /*点击上传文件按钮*/
  updataClick = () => {
    this.setState({
      visibleList: true
    })
  }

  /*点击上报作业 确定*/
  handleOkLoge() {
    this.setState({
      visibleLogs: false,
      current: 0,
      resourceFullCode: "",
    })
  }

  /*点击上报作业 取消*/
  handleCancelLoge() {
    this.setState({
      visibleLogs: false,
      current: 0,
      resourceFullCode: "",
      titleTrue:true
    })
   
  }

  /*点击执行日志*/
  handleLogeOK() {
    const { dataLog } = this.state;
    for (let key of dataLog) {
      this.setState({
        visibleOK: true,
        log: decodeURIComponent(key.log),
      })
    }
  }

  /*关闭执行日志*/
  handleLogeNo() {
    this.setState({
      visibleOK: false
    })
  }

  handleSizeNo() {
    this.setState({
      visibleSize: false
    })
  }
  //点击第二步 选择上报方式，是为在线填报还是Excel上报方式
  onClickRadio = (e) => {
    const { dispatch } = this.props;
    dispatch({
      type: "reportingModel/setMetaId",
      payload: {
        RadioCokie:e.target.value
      }
    })
   
  }

 //Excel表单提交内容
  submit = values => {
    const { dispatch,reportingModel } = this.props;
    const {dataTitel} = reportingModel;
    let list=[];
    let lists=[];
    var valArr=[];
   
    values.forEach(val => {
      for(let key in val.data){
        console.log(values,"values===============",dataTitel);
        dispatch({
          type: "reportingModel/setMetaId",
          payload: {
            dataTitel:dataTitel,
            dataTable:values,
           
          }
        })
        // delete val.data[key].editing;  //删除表单editing的字段
        // delete val.data[key].key;      //删除表单key的字段
        list = Object.keys(val.data[key]);
        valArr = Object.values(val.data[key]);
        lists.push(valArr);
      }
    
    });
    dispatch({
      type: "reportingModel/setMetaId",
      payload: {
        dataTableSubmit:lists,
        dataTitleSuo:list,
       
        status:true
      }
    })
  }
  //点击是否需要表头
  onClickTrue=(e)=>{
    this.setState({
      titleTrue:e.target.checked
    })
    
  }
  handleSuccess(){
    const { dispatch } = this.props;
    dispatch({
      type: "reportingModel/setMetaId",
      payload: {
        visibleSuccess:false
      }
    })
  }


  render() {
    const {form,dispatch,reportingModel}=this.props;
    const { getFieldDecorator } = form;
    const { visible, current, info, fileList, resourceId, resourceFullCode, formatType, typeList, pagination,
            dataSize, visibleSize,selectListPut, resourceNameS, dataList, visibleLogs, dataLog, resId,
            visibleOK, log, formatInfo, uploading,titleTrue } = this.state;
    const { dataTitel, dataTable,status ,RadioCokie,successData,visibleSuccess} = reportingModel;
    let updata = formatType === 3 ? "数据库" : "文件类型";   //是否展示数据格式是数据库类型还是文件类型
    const _this = this;  //重新定义一下this
    let titleFlag = titleTrue===true?1:0;
    const fields = this.state.fields;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 12 },
    };
     //上传多个文件列表
    const fileUploadProps = {
      fileName: "files",
      uploadUrl: `${API_BASE_CATALOG}/dataUpload/saveOrUpdateUploadDataForFILE`,
      data: {
        formatType: typeList,
        resourceId: resId
      },
      handleCallback: (fileList) => {
        let dataList = [];
        let key = 1;
        console.log(fileList, "文件");
        for (let index of fileList) {
          const { code, data } = index.response;
          if (index.status === "done" && code === "200") {
            dataList.push({
              key: key++,
              id: data[0].id,
              name: data[0].pubFileName,
              pubFileName: data[0].pubFileName,
              /*pubFileName:data[0].pubFileName.split(".")[0],*/
              fileType: data[0].pubFileName.split(".")[1],
              isExisted: false,
              fileDescription: ""
            })
          }
        }
        _this.setState({ fileList: dataList })
      }
    }
    //上个单个文件 数据库类型导入 在线填报
    const props = {
      action: '//jsonplaceholder.typicode.com/posts/',
      onRemove: (file) => {
        this.setState(({ fileList }) => {
          const index = fileList.indexOf(file);
          const newFileList = fileList.slice();
          newFileList.splice(index, 1);
          return {
            fileList: newFileList,
          };
        });
      },
      beforeUpload: (file) => {
        this.setState(({ fileList }) => ({
          fileList: [...fileList, file],
        }));
        return false;
      },
      fileList: this.state.fileList,
    };
    //导入单个文件 选择为Excel上报
    const onChange = (info) => {
      let arg = [];
      let argArr = [];
      let fileList = info.fileList;
      fileList = fileList.slice(-2);
      fileList = fileList.map((file) => {
        if (file.response) {
          file.url = file.response.url;
        }
        return file;
      });
      fileList = fileList.filter((file) => {
        if (file.response) {
          return file.response.status === 'success';
        }
        return true;
      });
      this.setState({ fileList });
      console.log(info.file.response, "info.file.response");
      let filst = info.file.response.data ===null?[]:info.file.response.data.browseData;
     let Arg=[];
     dataTitel.forEach(item=>{Arg.push(item.title) })

     filst.forEach(item=>{
      let obj = {};
      item.forEach((index,key)=>{ obj[Arg[key]] = index; });
      arg.push(obj);
    })
    argArr.push({ parent:1, data:arg  })

      //判断success为ture就是导入成功
      //并且刷新表单
      console.log(info.file.response,"info.file.responsedddddddd");
   
      if (info.file.response.code === "200") {
        if (info.file.status === 'done') {
         // message.success(info.file.response.msg);
          this.setState({
            fileStuat: "show",
          })
        } else if (info.file.status === 'error') {
          message.error("上传失败");
        }
      }else{
            message.error(info.file.response.msg);
         }
     
      dispatch({
        type: "reportingModel/setMetaId",
        payload: {
          dataTable:argArr
        }
      })
      
    }

    //点击导入文档
    //调取接口，传入参数 选择为Excel上报
    const propsFile = {
      fileName: "file",
      action: API_BASE_CATALOG + '/dataUpload/importFormDataIntoBrowse',
      data: { titleFlag: titleFlag},
      onChange: onChange,
    };

    let formatL = formatType === 1 ? "电子文件" : "" || formatType === 2 ? "电子表格" : "" || formatType === 3 ? "数据库" : ""
      || formatType === 4 ? "图形图像" : "" || formatType === 5 ? "流媒体" : "" || formatType === 6 ? "自描述格式" : "" || formatType === 7 ? "服务接口" : "";
    let arrAg = fileList.length === 0 ? "" : true;
   

    console.log(dataTitel,"dataTitel");
    return (
      <div style={{ margin: 20 }}>
        <Form className="btn_std_group" name="submitAction" id="submitAction" ref="submitAction">
          <Row gutter={24}>
            <CustomizedForm
              {...fields}
              onChange={this.handleFormChange}
              request={this.request}
              handleReported={this.handleReported}
            />
          </Row>
          <Modal
            title="上报数据"
            visible={visible}
            width={"800px"}
            style={{ transform: 'translate(0px 0px)' }}
            onCancel={this.handleCancel.bind(this)}
            footer={null}
           >
            
            <div>
              {formatType === 3 ? (
                <Steps current={current} style={{ width: "60%", marginLeft: "20%", marginBottom: "5%" }}>
                  {steps1.map(item => <Step key={item.title} title={item.title} />)}
                </Steps>
              ) : (
                  <Steps current={current} style={{ width: "60%", marginLeft: "20%", marginBottom: "5%" }}>
                    {steps.map(item => <Step key={item.title} title={item.title} />)}
                  </Steps>
                )}

              {current === 0 ? (
                <div style={{ margin: 20 }}>
                  <FormItem label="资源名称" {...formItemLayout}>
                    {getFieldDecorator('resourceId', {
                      initialValue: resourceId,
                      rules: [{ required: true, message: '请选择资源名称' }]
                    })(
                      <Select allowClear onChange={this.headMLZYgetPub.bind(this)} style={{ width: "100%" }}>
                        {selectListPut.map(item => {
                          return <Option key={item.id} value={item.id}>{item.resourceName}</Option>
                        })}
                      </Select>
                    )}
                  </FormItem>

                  <FormItem label="资源编码" {...formItemLayout}>
                    {getFieldDecorator('deptCode', {
                      initialValue: resourceFullCode,
                    })(
                      <Input disabled />
                    )}
                  </FormItem>

                  <FormItem label="资源格式" {...formItemLayout}>
                    {getFieldDecorator('dataType', {
                      initialValue: updata || [],
                    })(
                      <Input disabled />
                    )}
                  </FormItem>
                  {formatType === 6 || formatType === 7 ? (
                    <FormItem label="温馨提示" {...formItemLayout}>
                      <label>此选择的格式不支持上报！</label>
                    </FormItem>
                  ) : ""}

                </div>
              ) : null}
              {current === 1 ? (
                <div>
                  {formatType === 3 ? (
                    <Row>
                      <Col>
                        <FormItem label="上报方式" {...formItemLayout}>
                            {getFieldDecorator('RadioCokie', {
                                initialValue:RadioCokie,
                              })(
                                <Radio.Group defaultValue="Online" buttonStyle="solid" onChange={this.onClickRadio.bind(this)}>
                                  <Radio.Button value="Online">在线填报</Radio.Button>
                                  <Radio.Button value="Excel">Excel上报</Radio.Button>
                                </Radio.Group>
                              )}
                        </FormItem>
                      </Col>
                    </Row>
                  ) : (
                      <div>
                        <FormItem label="资源名称" {...formItemLayout} >
                          <label disabled>{resourceNameS}</label>
                        </FormItem>
                        {formatType === 6 ? (
                          <div></div>
                        ) : (
                            <FormItem label="文件" {...formItemLayout} >
                              <FileUpload {...fileUploadProps} disabled={formatType === 7} />
                            </FormItem>
                          )}

                        <FormItem label="上传类型格式" {...formItemLayout}>
                          <label>{formatL}/{formatInfo}</label>
                        </FormItem>

                        <FormItem label="数据批次" {...formItemLayout}>
                          {getFieldDecorator('dataBatch', {
                            initialValue: info.dataBatch,
                            rules: [{ required: true, message: '请选择系统时间' }]
                          })(
                            <DatePicker format={dateFormat} style={{ width: "100%" }} />
                          )}
                        </FormItem>
                        <Row>
                          <Col span={24}>
                            <span style={{ fontWeight: "bold" }}>已上传文件列表</span>&nbsp;(&nbsp;<span style={{ color: 'red' }}>*</span>&nbsp;如文件名重复，勾选覆盖可覆盖原文件&nbsp;)
                          </Col>
                        </Row>
                        <TableList
                          showIndex
                          onRowClick={() => { return false }}
                          style={{ marginTop: 10 }}
                          columns={this.columnsOptian}
                          loading={this.state.loading}
                          dataSource={fileList}
                          pagination={false}
                          scroll={{ y: 300, x: 260 }}
                          className="th-nowrap editStepTable"
                        />
                      </div>
                    )}
                </div>

              ) : null}

              {current === 2 ? (
                <div>
                  {RadioCokie === "Online" ? (
                      <div>

                      <Row>
                        <Col span={14} offset={2}>
                          <FormItem label="资源名称" {...formItemLayout}>
                            {getFieldDecorator('userName', {
                              initialValue: resourceNameS,
                            })(
                              <Input disabled />
                            )}
                          </FormItem>
                        </Col>
                        <Col span={8}>
                            <Upload {...propsFile} fileList={this.state.fileList}>
                              <Button >
                                <Icon type="upload" /> 导入Excel文件
                                    </Button>
                            </Upload>
                        </Col>
                      </Row>

                      <Row>
                          <Col span={14} offset={2}>
                              <FormItem label="数据批次" {...formItemLayout}>
                                {getFieldDecorator('dataBatch', {
                                  initialValue: info.dataBatch,
                                  rules: [{ required: true, message: '请选择数据批次！' }]
                                })(
                                  <DatePicker format={dateFormat} />
                                )}
                              </FormItem>
                          </Col>
                          <Col span={8}>
                              <FormItem {...formItemLayout}>
                                {getFieldDecorator('titleTrue', {
                                  valuePropName: 'checked',
                                  initialValue: false
                                })(
                                  <Checkbox style={{marginLeft:"22%",width:"100%"}} onChange={this.onClickTrue.bind(this)}>是否包含标题</Checkbox>
                                )}
                              </FormItem>
                             
                          </Col>
                      </Row>
                     
                      
                     

                      <div className={style.view}>

                        {dataTitel.length > 0 &&
                          <TableGenerator
                            rowKey="key"
                           // IfShowHeader={false}
                            column={dataTitel}//模拟数据dataTitel
                            dataSource={dataTable}//模拟数据dataTable
                            pagination={false}
                            header={({ submit }) => (
                              <div>
                                <Button type="primary" onClick={() => submit(this.submit)}> 保存表单信息</Button>
                              </div>
                            )}
                          />
                }

                      </div>
                    </div>
                  ) : (
                    <div>
                    <FormItem label="excel上传" {...formItemLayout}>
                      <a target="_blank"><label type="download" onClick={this.updataLast.bind(this)}>模板下载</label></a>
                    </FormItem>
                    <FormItem label="文件" {...formItemLayout} disabled={arrAg === false}>
                      <Upload {...props} disabled={arrAg === false}>
                        <Button disabled={arrAg === false}>
                          <Icon type="upload" /> 单个上传文件
                                </Button>
                      </Upload>
                    </FormItem>
                    <FormItem label="资源名称" {...formItemLayout}>
                      {getFieldDecorator('userName', {
                        initialValue: resourceNameS,
                      })(
                        <Input disabled />
                      )}
                    </FormItem>
                    <FormItem label="数据批次" {...formItemLayout}>
                      {getFieldDecorator('dataBatch', {
                        initialValue: info.dataBatch,
                        rules: [{ required: true, message: '请选择数据批次！' }]
                      })(
                        <DatePicker format={dateFormat} />
                      )}
                    </FormItem>
                    <Row style={{ marginTop: "10%", border: "1px dashed #ccc", padding: "20px" }}>
                      <Col span={24} style={{ color: "#0faedb", marginBottom: "13px" }}>上传说明</Col>
                      <Col span={24} >
                        <p>1、下载Excel模板再进行上传。</p>
                        <p>2、每个Excel中只使用一个工作表。</p>
                        <p>3、Excel中不使用函数。</p>
                        <p>4、Excel中不使用合并单元格。</p>
                        <p>5、Excel会自动将一些数字转为科学计数法，如2.22222E+25，如果这种情况出现，请将列设置为文本格式，或自定义格式为0。</p>
                        <p>6、时间日期请设置为文本格式，统一为“YYYY-MM-DD”格式，如果表示时间范围，如统计月份，则填写年和月，如“2014年6月”。</p>
                      </Col>
                    </Row>

                  </div>
                    
                    )}
                </div>
              ) : null}

              {formatType === 3 ? (
                <div className={style.stepsAction}>
                  <Row>
                  {
                    this.state.current < steps1.length - 1
                    &&
                    <Col>
                      {RadioCokie === "Online" ? (
                        <Button type="primary" disabled={formatType === 7} style={{ float: 'right', marginLeft: 10 }} onClick={() => this.nextExcel()}>下一步</Button>
                      ) : (<Button type="primary" disabled={formatType === 7} style={{ float: 'right', marginLeft: 10 }} onClick={() => this.next()}>下一步</Button>)}

                      {/* <Button type="primary" style={{ float: 'right', marginLeft: 10 }} onClick={() => this.handleCancel()}>取消</Button> */}
                    </Col>
                  }
                  {
                    this.state.current === steps1.length - 1
                    &&
                     <Col>
                          {RadioCokie === "Online" ? (
                            <Button type="primary" disabled={status === false} style={{ float: 'right', marginLeft: 10 }} onClick={this.thisReported} loading={uploading}>上传</Button>
                          ):(
                            <Button type="primary" style={{ float: 'right', marginLeft: 10 }} onClick={this.thisReported} loading={uploading}>上传</Button>
                          )}
                           
                     </Col>
                  }
                  {
                    current > 0
                    && (
                      <Col>
                        <Button type="primary" style={{ marginLeft: 8 }} onClick={() => this.prev()}>
                          上一步
                          </Button>
                         {/* <Button type="primary" style={{float:'right'}} onClick={() => this.handleCancel()}>取消</Button>  */}
                      </Col>
                    )
                  }
                 </Row>
                </div>
              ) : (
                  <div className={style.stepsAction}>
                    {
                      this.state.current < steps.length - 1
                      &&
                      <Row>
                        <Button type="primary" disabled={formatType === 7} style={{ float: 'right', marginLeft: 10 }} onClick={() => this.next()}>下一步</Button>
                        <Button type="primary" style={{ float: 'right', marginLeft: 10 }} onClick={() => this.handleCancel()}>取消</Button>
                      </Row>
                    }
                    {
                      this.state.current === steps.length - 1
                      &&
                      <Button type="primary" style={{ float: 'right', marginLeft: 10 }} onClick={this.thisReported} loading={uploading}>上传</Button>
                    }
                    {
                      this.state.current > 0
                      &&
                      <div>
                        <Button type="primary" style={{ marginLeft: 8 }} onClick={() => this.prev()}>
                          上一步
                      </Button>
                        {/* <Button type="primary" style={{float:'right'}} onClick={() => this.handleCancel()}>取消</Button> */}
                      </div>
                    }
                  </div>
                )}
            </div>
          </Modal>

          <Modal
            title="执行日志"
            visible={visibleOK}
            width={"50%"}
            footer={[
              <Button key="back" size="large" onClick={this.handleLogeNo.bind(this)}>取消</Button>,
              <Button key="submit" type="primary" size="large" onClick={this.handleLogeNo.bind(this)}>确定</Button>,
            ]}
            onCancel={this.handleLogeNo.bind(this)} >
            <pre style={{ maxHeight: "600px", overflow: "scroll", whiteSpace: "pre-line" }}> {log === "null" ? '暂无日志' : log}</pre>
          </Modal>

        </Form>

        <Modal
          title="执行信息"
          visible={visibleLogs}
          width={"50%"}
          footer={[
            <Button key="back" size="large" onClick={this.handleCancelLoge.bind(this)}>取消</Button>,
            <Button key="submit" type="primary" size="large" onClick={this.handleOkLoge.bind(this)}>确定</Button>,
          ]}
          onCancel={this.handleCancelLoge.bind(this)} >
          <TableList
            showIndex
            onRowClick={() => { return false }}
            style={{ marginTop: 20 }}
            columns={this.columnslog}
            loading={this.state.loading}
            dataSource={dataLog}
            pagination="false"
            className="th-nowrap"
          />
        </Modal>

        <Modal
          title="执行数量"
          visible={visibleSize}
          width={"50%"}
          footer={[
            <Button key="back" size="large" onClick={this.handleSizeNo.bind(this)}>取消</Button>,
            <Button key="submit" type="primary" size="large" onClick={this.handleSizeNo.bind(this)}>确定</Button>,
          ]}
          onCancel={this.handleSizeNo.bind(this)} >
          <Row>
            <Col>上传文件成功，待处理数据：{dataSize}条</Col>
          </Row>
        </Modal>

        <Modal
          title="上报成功"
          visible={visibleSuccess}
          width={"50%"}
          footer={[
            <Button key="back" size="large" onClick={this.handleSuccess.bind(this)}>取消</Button>,
            <Button key="submit" type="primary" size="large" onClick={this.handleSuccess.bind(this)}>确定</Button>,
          ]}
          onCancel={this.handleSuccess.bind(this)} >
          <Row>
            <Col>上报成功，待处理数据：{successData?successData:"--"}条</Col>
          </Row>
        </Modal>


        <div>
          <TableList
            showIndex
            onRowClick={() => { return false }}
            style={{ marginTop: 20 }}
            columns={this.columns}
            loading={this.state.loading}
            dataSource={dataList}
            pagination={pagination}
            className="th-nowrap"
          />
        </div>
      </div>

    );
  }
}
const MyreportingForm = Form.create()(Myreporting);
export default connect(({ reportingModel, account }) => ({
  reportingModel, account
}))(MyreportingForm);