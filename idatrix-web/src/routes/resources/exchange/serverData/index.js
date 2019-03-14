import { connect } from "dva";
import {
  Row,
  Col,
  Icon,
  Button,
  Form,
  Input,
  Select,
  message,
  Tooltip,
  Divider,
  DatePicker,
  Tabs
} from "antd";

const FormItem = Form.Item;
const Option = Select.Option;
const TabPane = Tabs.TabPane;
const { RangePicker } = DatePicker;
import TableList from "components/TableList";
import Modal from "components/Modal";
import moment from "moment";
const dateFormat = "YYYY/MM/DD";
import {
  getAllServiceLog,
  getServiceLogDetailById
} from "services/DirectoryOverview";

class serverlist extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      startTime: "",   //查询开始时间
      endTime: "",     //查询结束时间
      dateString: "",  //获取时间参数
      pagination: {
        page: 1,  //分页的页数
        pageSize: 10,   //分页的条数
        total: 0     //初始化为0
      },
      total: 0,
      data: []   //列表渲染存放的数据
    };

    /**
     * 统一绑定bind(this)
     * edited by steven leo on 2018/09/22
     */
    this.onChange = this.onChange.bind(this);  //查询时间参数
    this.handleClickSelect = this.handleClickSelect.bind(this);  //点击服务查询按钮
    this.handleClickOne = this.handleClickOne.bind(this);   //初始化执行的列表参数接口
    this.handleOK = this.handleOK.bind(this);   //点击弹出框保存按钮
    this.handleNo = this.handleNo.bind(this);   //点击取消返回按钮
  }

  columns = [
    {
      title: "服务代码",
      dataIndex: "serviceCode",
      key: "serviceCode"
    },
    {
      title: "服务名称",
      dataIndex: "serviceName",
      key: "serviceName"
    },
    {
      title: "服务类型",
      dataIndex: "serviceType",
      key: "serviceType"
    },
    {
      title: "调用方",
      dataIndex: "callerDeptName",
      key: "callerDeptName"
    },
    {
      title: "调用时间",
      dataIndex: "callTime",
      key: "callTime"
    },
    {
      title: "时长",
      dataIndex: "execTime",
      key: "execTime"
    },
    {
      title: "是否成功",
      dataIndex: "isSuccess",
      key: "isSuccess",
      render:(text)=>{
        const Success = text ===1?"成功":"失败";
        return(
          <span>{Success}</span>
        )
      }
    },
    {
      title: "操作",
      dataIndex: "endTime",
      key: "endTime",
      render: (text, record) => {
        return (
          <div>
            <a
              onClick={() => {
                this.handleClickSelect(record.id);
              }}
            >
              <Tooltip title="服务详情">
                服务详情&nbsp;&nbsp;&nbsp;&nbsp;
              </Tooltip>
            </a>
          </div>
        );
      }
    }
  ];
  /**
   * @param {点击服务详情弹框} id 
   * 并且把参数携带过去
   */
  handleClickSelect(id) {
    let ids = {};
    ids.id = id;
    const { dispatch } = this.props;
    getServiceLogDetailById(ids).then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        dispatch({
          type: "serverDataModel/showModel",
          payload: {
            logData: data,
            visible: true
          }
        });
      }
    });
  }
  /**
   * 初始化执行渲染的参数
   */
  componentWillMount() {
    this.handleClickOne();
  }

  componentWillReceiveProps(nextProps) {
    const { dispatch } = this.props;
    console.log(nextProps.serverDataModel, "nextProps.serverDataModel");
    const { pagination, data, loading, total } = nextProps.serverDataModel;
    this.setState({ pagination, data, loading, total });
  }

  /*点击查询接口*/
  handleClickOne = e => {
    const fields = this.state.fields;
    const { dispatch } = this.props;
    const pager = this.state.pagination;
    const { query } = this.props.location;
    /*资源服务点击查询数据接口*/
    this.props.form.validateFields((err, values) => {
      let arr = {};
      arr.serviceCode = values.serviceCode;
      arr.serviceName = values.serviceName;
      arr.callerDeptName = values.callerDeptName;

      /**
       * 当选择为all的时候，
       * 默认传空，则后台返回为全部类型
       * 如果传All，后台无法判断
       * @edited by steven leo 2018/09/20
       */
      arr.serviceType = values.serviceType === "All" ? "" : values.serviceType;
      arr.startTime = this.state.startTime;
      (arr.endTime = this.state.endTime), (arr.isSuccess = values.isSuccess);

      arr.page = 1;
      arr.pageSize = query.pageSize ? query.pageSize : 10;
      getAllServiceLog(arr, {
        page: 1,
        pageSize: query.pageSize || pager.pageSize
      }).then(res => {
        const { code, data, total, message } = res.data;
        if (code === "200") {
          pager.total = data !== null && data.total ? data.total : 0;
          data.results.map((row, index) => {
            row.key = row.id;
            row.index = pager.pageSize * (pager.pager - 1) + index + 1;
            return row;
          });
          dispatch({
            type: "serverDataModel/pager",
            payload: {
              data: data.results,
              pagination: pager,
              loading: false
            }
          });

          this.setState({ pagination: pager, data: data.results });
        } else {
          dispatch({
            type: "serverDataModel/setMetaId",
            payload: {
              data: [],
              pagination: pager,
              loading: false
            }
          });
          pager.total = 0;
          this.setState({ pagination: pager, data: [] });
        }
      });
    });
  };

  onChange(date, dateString) {
    this.setState({
      startTime: dateString[0],
      endTime: dateString[1]
    });
  }

  handleOK() {
    const { dispatch } = this.props;
    dispatch({
      type: "serverDataModel/hideModel",
      payload: {
        visible: false
      }
    });
  }

  handleNo() {
    const { dispatch } = this.props;
    dispatch({
      type: "serverDataModel/hideModel",
      payload: {
        visible: false
      }
    });
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const { text, visible, logData } = this.props.serverDataModel;
    const { pagination, data } = this.state;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 12 }
    };
    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 18 }
    };
    return (
      <div style={{ margin: 20 }}>
        <Form className="ant-advanced-search-form btn_std_group">
          <Row gutter={24}>
              <Col span={6}>
                <FormItem label="服务代码" {...formItemLayout1}>
                  {getFieldDecorator("serviceCode", {})(<Input />)}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label={"服务名称"} {...formItemLayout1}>
                  {getFieldDecorator("serviceName", {})(<Input />)}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label={"服务类型"} {...formItemLayout1}>
                  {getFieldDecorator("serviceType", {})(
                    <Select style={{ width: "100%" }}>
                      <Option value="All">全部</Option>
                      <Option value="SOAP">SOAP</Option>
                      <Option value="RESTful">RESTful</Option>
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label={"调用方"} {...formItemLayout1}>
                  {getFieldDecorator("callerDeptName", {})(<Input />)}
                </FormItem>
              </Col>
              <Col span={6}>
                <FormItem label={"是否成功"} {...formItemLayout1}>
                  {getFieldDecorator("isSuccess", {
                    initialValue: ""
                  })(
                    <Select style={{ width: "100%" }}>
                      <Option value="">全部</Option>
                      <Option value="0">失败</Option>
                      <Option value="1">成功</Option>
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col span={6}>
                {/* 新增时间判断，只能选择今日之前的时间 */}
                {/* Edited By Steven Leo on 2018/09/18 */}

                <FormItem label={"调用时间"} {...formItemLayout1}>
                  {getFieldDecorator("callTime", {})(
                    <RangePicker
                      format={dateFormat}
                      onChange={this.onChange}
                      disabledDate={curr =>
                        !(curr && curr < moment().endOf("day"))
                      }
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={6}></Col>
              <Col span={6} className="search_btn">
              <Button type="primary" onClick={this.handleClickOne}>
                查询
              </Button>
            </Col>
          </Row>
          <Modal
            title="服务详情"
            visible={visible}
            width={"50%"}
            footer={[
              <Button key="back" size="large" onClick={this.handleNo}>
                取消
              </Button>,
              <Button
                key="submit"
                type="primary"
                size="large"
                onClick={this.handleOK}
              >
                确定
              </Button>
            ]}
            onCancel={this.handleNo}
          >
            <Row>
              <Col span={10}>
                调用时间：
                {logData.callTime}
              </Col>
              <Col span={6}>
                执行时长：
                {(logData.execTime/1000).toFixed(3)}s
              </Col>
              <Col span={6}>
                错误信息：
                {logData.errorMessage}
              </Col>
            </Row>
            <Tabs defaultActiveKey="1">
              <TabPane tab="输入" key="1">
                {logData.input}
              </TabPane>
              <TabPane tab="输出" key="2">
                {logData.output}
              </TabPane>
              <TabPane tab="错误日志" key="3">
                {logData.errorStack}
              </TabPane>
            </Tabs>
          </Modal>
        </Form>
        <div>
          <TableList
            showIndex
            onRowClick={() => {
              return false;
            }}
            style={{ marginTop: 20 }}
            columns={this.columns}
            dataSource={data}
            className="th-nowrap"
            pagination={pagination}
          />
        </div>
      </div>
    );
  }
}
const serverForm = Form.create()(serverlist);
export default connect(({ serverDataModel }) => ({
  serverDataModel
}))(serverForm);
