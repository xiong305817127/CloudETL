import React from "react";
import ReactDOM from "react-dom";
import { connect } from "dva";
import { withRouter } from "react-router";
import {
  Form,
  Icon,
  TreeSelect,
  Input,
  Button,
  message,
  Popconfirm,
  Tooltip,
  Row,
  Col
} from "antd";
const FormItem = Form.Item;
const { TextArea } = Input;
import { API_BASE_METADATA, DEFAULT_PAGE_SIZE } from "../../../constants";
import { uploadFile, convertArrayToTree } from "../../../utils/utils";
import { getLabelByTreeValue } from "utils/metadataTools";
import TableList from "../../../components/TableList";
import Search from "../../../components/Search";
import Empower from "../../../components/Empower"; // 导入授权组件
// import Style from '../DataRelationship/style.css';
import {
  getDepartmentTree,
  SJBZCXsearch,
  SJBZCXxinjian,
  SJBZCXDelete
} from "../../../services/metadata";
import Modal from "components/Modal";
import { submitDecorator } from "utils/decorator";
@submitDecorator
class DataStandardView extends React.Component {
  //1.初始化
  state = {
    pagination: {
      current: 1,
      pageSize: 10
    },
    data: [],
    loading: false,

    //选择部门
    options: [],
    text: "",
    textBuMenAnNiu: "",

    routerListened: false, // 是否已监听路由
    isMounted: false, // 组件是否已挂载
    visible: false
    /*dataSource:[],
     remark:'',
     title:'',
     visible: false,
     selectedRowKeys:[],
     id:''*/
  };
  //选择部门级联
  handleChangeDept = (value, selectedOptions) => {
    this.setState(
      {
        /*text: selectedOptions.map(o => o.value).join(', '),*/
        text: value,
        textBuMenAnNiu: "取消选择"
      },
      () => {
        this.RequestList();
      }
    );
  };

  //更新表格
  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: "metadataCommon/getDepartments" });
    this.setState(
      {
        isMounted: true
      },
      () => {
        const { router } = this.props;
        if (!this.state.routerListened) {
          router.listen(location => {
            if (
              location.pathname === "/DataStandardView" &&
              this.state.isMounted
            ) {
              this.RequestList();
            }
          });
          this.setState({
            routerListened: true
          });
        }
      }
    );
  }

  componentWillUnmount() {
    this.setState({
      options: [],
      text: "",
      textBuMenAnNiu: "",
      isMounted: false
    });
    const location = this.props.location;
    delete location.query.keyword;
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: "metadataCommon/getDepartments" });
    dispatch({ type: "metadataCommon/getAllResource" });
    this.RequestList();
  }

  componentWillReceiveProps(nextProps) {
    this.RequestList();
  }

  RequestList() {
    this.setState({
      loading: true
    });
    const { query } = this.props.location;
    const pager = this.state.pagination;
    const { id, renterId } = this.props.account;
    let obj = {
      dept: this.state.text,
      keyword: query.keyword ? decodeURIComponent(query.keyword) : "",
      renterId: renterId
    };
    SJBZCXsearch(obj, {
      current: query.page || 1,
      pageSize: query.pageSize || pager.pageSize
    }).then(res => {
      if (res.data && res.data.data) {
        const { total, rows } = res.data.data;
        pager.total = total;
        rows.map((row, index) => {
          row.key = row.id;
          row.index = pager.pageSize * (pager.current - 1) + index + 1;
          return row;
        });
        console.log(pager);
        this.setState({
          data: rows,
          pagination: pager,
          loading: false
        });
      }
    });
  }

  Search(e) {
    console.log(e);
    const location = this.props.location;
    if (e && e.trim()) {
      location.query.keyword = encodeURIComponent(e);
    } else {
      delete location.query.keyword;
    }
    this.props.router.push(location);
  }

  columns = [
    {
      title: "文档标题",
      dataIndex: "title",
      key: "title",
      render: (text, record) => {
        //一个未声明的函数onClick={()=>{this.handleIconClick(record)}}
        return (
          <a
            onClick={() => {
              this.GetXiaZai(record);
            }}
          >
            {text}
          </a>
        );
      }
    },
    {
      title: "所属组织",
      dataIndex: "dept",
      key: "dept",
      render: text => {
        const { departmentsOptions } = this.props.metadataCommon;
        return getLabelByTreeValue(text, departmentsOptions);
      }
    },
    {
      title: "大小(KB)",
      dataIndex: "size",
      key: "size"
    },
    {
      title: "上传日期",
      dataIndex: "createTime",
      key: "createTime",
      width: "10%"
    },
    { title: "上传人", dataIndex: "modifier", key: "modifier", width: "10%" },
    {
      title: "备注",
      dataIndex: "remark",
      key: "remark",
      render: text => (
        <div className="word25" title={text}>
          {text}
        </div>
      )
    },
    {
      title: "操作",
      key: "x123",
      render: (text, record) => {
        return (
          <div>
            <Empower
              api="/DataStandardController/Delete"
              disabled={!record.canEdited}
            >
              <Popconfirm
                placement="topLeft"
                title="确认要删除该行吗？"
                onConfirm={() => {
                  this.getDelete(record);
                }}
              >
                <a>
                  <Tooltip title="删除">
                    <Icon type="delete" className="op-icon" />
                    &nbsp;&nbsp;&nbsp;&nbsp;
                  </Tooltip>
                </a>
              </Popconfirm>
            </Empower>
            <Empower api="/FileController/download">
              <a target="_blank">
                <Tooltip title="下载">
                  <Icon
                    onClick={() => this.GetXiaZai(record)}
                    type="download"
                    className="op-icon"
                  />
                </Tooltip>
              </a>
            </Empower>
          </div>
        );
      }
    }
  ];

  //点击打开文件
  openFile(record) {
    window.open("${API_BASE_METADATA}/fileOperate/show/`+ record.title");
  }

  //新建弹出框《start》
  showModals() {
    this.setState({
      visible: true
    });
  }

  handelChange(e) {
    let remark = e.target.value;
    this.setState({
      remark: remark
    });
  }

  handleOk = e => {
    const { id, renterId } = this.props.account;
    let file;
    let file1 = e.target.name;
    let remark = e.target.value;
    var oMyForm = new FormData();
    console.log(this.refs.file1.files[0], "this.refs.file1.files[0]");
    oMyForm.append("sourceFile", this.refs.file1.files[0]);
    oMyForm.append("remark", this.state.remark ? this.state.remark : "");
    oMyForm.append("renterId", this.props.account.renterId);
    if (this.refs.file1.files[0] === undefined) {
      //alisa on 2018-09-26-17-50
      message.error("请选择要上传的文件");
    } else {
      this.props.disableSubmit();
      uploadFile(
        `${API_BASE_METADATA}/fileOperate/upload` +
          "?renterId=" +
          this.props.account.renterId,
        oMyForm,
        request => {
          console.log(request);
          const { status } = request;
          const { response } = request;

          var obj = JSON.parse(response);
          ReactDOM.findDOMNode(this.refs["submitAction"]).reset();

          if (obj.code === "200") {
            message.success("文件上传成功！");

            oMyForm.append("sourceFile", "");
            oMyForm.append("remark", " ");
            oMyForm.append("renterId", "");
            this.setState({
              visible: false
            });
            this.refs.wordMager.style.display = "none";
            this.RequestList();
            this.Search();
          } else if (obj.code === "13") {
            message.error("文件已存在，请重新选择！");
            oMyForm.append("sourceFile", "");
            oMyForm.append("remark", " ");
            oMyForm.append("renterId", "");
            ReactDOM.findDOMNode(this.refs["submitAction"]).reset();
          } else if (obj.code === "12") {
            message.error("文件上传格式暂不支持，请重新选择！");
            oMyForm.append("sourceFile", "");
            oMyForm.append("remark", " ");
            oMyForm.append("renterId", "");
            ReactDOM.findDOMNode(this.refs["submitAction"]).reset();
          } else if (obj.code === "700") {
            this.refs.wordMager.style.display = "block";
            console.log(this.refs.wordMager, "wordMager");
            /* message.error('请选择需要文件上传的文件');*/
          } else {
            message.error("文件上传失败！");
            ReactDOM.findDOMNode(this.refs["submitAction"]).reset();
          }
          this.props.enableSubmit();
        }
      );
    }
  };

  handleCancelAlert = e => {
    this.setState({
      visible: false
    });
  };
  handleCancel = () => {
    console.log("Clicked cancel button");
    this.setState({
      visible: false
    });
  };
  // 新建弹出框《end》

  handleModeChange = e => {
    const mode = this.value;
    this.setState({ mode });
    this.setState({
      visible: true
    });
  };
  //删除文件接口
  getDelete(record) {
    let ids = record.id;
    SJBZCXDelete(ids).then(res => {
      this.Search();
    });
  }
  //下载：0_0
  GetXiaZai(record) {
    console.log(record, "111111");
    window.open(
      `${API_BASE_METADATA}/fileOperate/download/` +
      record.title +
      "?creater=" +
      record.modifier);
  }
  render() {
    const { query } = this.props.location;
    const pagination = this.state.pagination;
    const { getFieldDecorator } = this.props.form;
    const { visible } = this.state;
    const { departmentsTree } = this.props.metadataCommon;
    return (
      <Row>
        {/*1.搜索className={Style.DataRelationshipManagement} className={Style.xuanzebumen}*/}
        <Col span={24}>
          <div className="padding_20">
            <Search
              placeholder="可以按文档标题进行模糊搜索"
              onSearch={e => {
                this.Search(e);
              }}
              defaultValue={
                query.keyword ? decodeURIComponent(query.keyword) : ""
              }
            />
            <span>
              <TreeSelect
                allowClear
                placeholder="请选择组织"
                treeData={departmentsTree}
                onChange={value => {
                  this.handleChangeDept(value);
                }}
                treeDefaultExpandAll
                style={{ width: 200 }}
              />
            </span>
          </div>
        </Col>
        {/*2.表格和按钮*/}
        <Col span={24}>
          {/*2.1.新建对话框*/}
          <Button
            type="primary"
            
            className="margin_0_20"
            onClick={() => this.showModals()}
          >
            上传文件
          </Button>
          <Modal
            title="数据标准文档上传(仅支持txt，pdf，word，excel格式)"
            ref="ModalUp"
            visible={visible}
            onCancel={this.handleCancel}
            maskClosable={false}
            footer={[
              <Button
                key="back"
                size="large"
                loading={this.state.loading}
                onClick={this.handleCancelAlert}
              >
                取消
              </Button>,
              <Button
                key="next"
                id="fileBut"
                type="primary"
                size="large"
                onClick={this.handleOk.bind(this)}
                loading={this.props.submitLoading}
              >
                上传
              </Button>
            ]}
          >
            {/* className={Style.GetFil} className="login-form"*/}
            <Form
              onSubmit={this.handleSubmit}
              name="submitAction"
              id="submitAction"
              ref="submitAction"
            >
              <FormItem
                label="上传内容"
                labelCol={{ span: 4 }}
                wrapperCol={{ span: 18 }}
              >
                <input type="file" ref="file1" name="filename" />
                <div
                  ref="wordMager"
                  style={{
                    fontSize: "9px",
                    height: "10px",
                    color: "red",
                    display: "none"
                  }}
                >
                  请选择需要上传的文件
                </div>
              </FormItem>
              {/*className={Style.BoxP}*/}
              <FormItem
                label="备注"
                labelCol={{ span: 4 }}
                wrapperCol={{ span: 18 }}
                // required
              >
                <TextArea
                  ref="remark"
                  onChange={this.handelChange.bind(this)}
                  name="remark"
                  placeholder=""
                  autosize={{ minRows: 3, maxRows: 6 }}
                  rows={4}
                  spellCheck={false}
                  maxLength="200"
                />
              </FormItem>
            </Form>
          </Modal>

          {/*2.2.表格*/}
          <TableList
            showIndex
            onRowClick={() => {
              return false;
            }}
            pagination={pagination}
            ref="editTable"
            columns={this.columns}
            dataSource={this.state.data}
            loading={this.state.loading}
            className="th-nowrap padding_20"
          />
        </Col>
      </Row>
    );
  }
}
const DataStandard = Form.create()(DataStandardView);
export default withRouter(
  connect(({ account, metadataCommon }) => ({
    account,
    metadataCommon
  }))(DataStandard)
);
