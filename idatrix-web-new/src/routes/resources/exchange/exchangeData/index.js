import { connect } from "dva";
import ReactDOM from "react-dom";
import ReactEcharts from "echarts-for-react";
import { withRouter, hashHistory } from "dva/router";
import {
  Row,
  Col,
  Icon,
  Button,
  Form,
  Input,
  Select,
  Tooltip,
  DatePicker,
  Card
} from "antd";
import { API_BASE_CATALOG } from "constants";
import { uploadFile, convertArrayToTree, downloadFile } from "utils/utils";
const FormItem = Form.Item;
const Option = Select.Option;
import TableList from "components/TableList";
import ControlJobPlatform from "../../../gather/components/taskCenter/controlPlatform/ControlJobPlatform";
import RunJob from "../../../gather/components/designPlatform/JobPlatform/RunJob";
import __ from "lodash";
import {strEnc} from "utils/EncryptUtil"
import { Cookies } from 'react-cookie';

const cookie = new Cookies(); 
import {
  getStatistics,
  getRunning,
} from "services/DirectoryOverview";  //接口返回

const formItemLayout1 = {
  labelCol: { span: 6 },
  wrapperCol: { span: 17 }
};
/**
 * 查询输入框
 * 单独用CustomizedForm包裹成一个新的组件
 */
const CustomizedForm = Form.create({
  onFieldsChange(props, changedFields) {
    props.onChange(changedFields);
  },
  onValuesChange(_, values) { }
})(props => {
  const {headClick} = props;
  const { getFieldDecorator } = props.form;
  return (
    <Row gutter={20}>
      <Col span={6} >
        <FormItem label="作业名称" {...formItemLayout1}>
          {getFieldDecorator("taskName", {})(<Input />)}
        </FormItem>
      </Col>
      <Col span={6}>
        <FormItem label={"部门"} {...formItemLayout1}>
          {getFieldDecorator("deptName", {})(<Input />)}
        </FormItem>
      </Col>
      <Col span={6}>
        <FormItem label={"类型"} {...formItemLayout1}>
          {getFieldDecorator("taskType", {})(
            <Select allowClear={true} style={{ width: "100%" }}>
              <Option value="All">全部</Option>
              <Option value="DB">数据库</Option>
              <Option value="FILE">文件类</Option>
            </Select>
          )}
        </FormItem>
      </Col>
      <Col span={6}>
        <FormItem label={"状态"} {...formItemLayout1}>
          {getFieldDecorator("status", {})(
            <Select style={{ width: "100%" }}>
              <Option value=" ">全部</Option>
              <Option value="WAIT_IMPORT">等待入库</Option>
              <Option value="IMPORTING">入库中</Option>
              <Option value="IMPORT_COMPLETE">已入库</Option>
              <Option value="IMPORT_ERROR">入库失败</Option>
              <Option value="STOP_IMPORT">终止入库</Option>
            </Select>
          )}
        </FormItem>
      </Col>
      <Col span={24} className="search_btn ">
        <Button type="primary" onClick={headClick}>
          查询
        </Button>
      </Col>
    </Row>
  );
});

class exchangeDatalist extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      fields: {
        taskName: { value: "" },  //查询作业名称
        deptName: { value: "" },  //查询部门
        taskType: { value: "" },   //查询类型
        status: { value: "" }   // 查询状态
      },
      pagination: {   //分页默认值
        page: 1,
        pageSize: 10
      },
      monthNamelist: "",  //保存图表x值
      dateCountlist: "",  //保存图表y值
      count: "",     //图表总数量
      total: 0,     //分页总数量
      data: [],     //表格渲染的数组
      option: {     //图表渲染
        tooltip: {
          trigger: "axis"
        },
        xAxis: [{
          type: "category",
          data: [],
        },{
          type: "category",
          data: [],
        },
      ],
        yAxis: [
          {
            name: "数据量（个）",  //显示y轴名称
            type: "value",
            splitLine: {
              show: false
             }
          },
          {
            name: "作业数量（条）",
            type: "value",
            min: 0,
            max: 250,
            splitArea : {show : true},
            splitLine: {
              show: false
            }
          }
        ],
        grid: {
          left: '3%',
          right: '2%',
          bottom: '3%',
          top: "15%",
          show: true,
          containLabel: true,
          borderWidth: 0,
          borderColor: "#4d4d4d"
        },
        series: [
          {
            name: "数据量",
            data: [],
            type: "bar",
           
            itemStyle : {
              normal : {
                  color:'#56c9e9'
              },
                areaStyle: {
                  color:'#56c9e9'
               }
             },
          },
          {
            name: "纵轴作业数量",
            data: [],
            type: "line",
            smooth: true,
           
            itemStyle : {
              normal : {
                  color:'#85e0a4'
              },
               areaStyle: {
                  color:'skyblue'
               }
             },
            
          }
        ]
      }
    };

    //点击 数据绑定
    this.headClick = this.headClick.bind(this);
    this.paginationChange = this.paginationChange.bind(this);
  }

  columns = [
    {
      title: "作业名称",
      dataIndex: "taskName",
      key: "taskName"
    },
    {
      title: "作业编号",
      dataIndex: "etlSubcribeId",
      key: "etlSubcribeId"
    },
    {
      title: "部门",
      dataIndex: "deptName",
      key: "deptName"
    },
    {
      title: "类型",
      dataIndex: "taskType",
      key: "taskType",
      render: text => {
        const alRoot = text === "DB" ? "数据库" : "文件类型";
        return <span>{alRoot}</span>;
      }
    },
    {
      title: "开始时间",
      dataIndex: "startTime",
      key: "startTime"
    },
    {
      title: "结束时间",
      dataIndex: "endTime",
      key: "endTime"
    },
    {
      title: "数据量",
      dataIndex: "dataCount",
      key: "dataCount"
    },
    {
      title: "作业状态",
      dataIndex: "status",
      key: "status",
      render: text => {
        const alRoot =
          text === "WAIT_IMPORT"
            ? "等待入库"
            : "" || text === "IMPORTING"
              ? "入库中"
              : "" || text === "IMPORT_COMPLETE"
                ? "已入库"
                : "" || text === "IMPORT_ERROR"
                  ? "入库失败"
                  : "" || text === "STOP_IMPORT"
                    ? "终止入库"
                    : "";
        return <span>{alRoot}</span>;
      }
    },
    {
      title: "操作",
      dataIndex: "x",
      key: "x",
      render: (text, record, index) => {
        if (record.status === "WAIT_IMPORT" || record.status === "IMPORT_ERROR") {
          return <div />;
        } else {
          return (
            <div>
              <a>
                <Tooltip title="监控">
                  <Icon
                    style={{ fontSize: "16px", cursor: "pointer" }}
                    onClick={e => {
                      this.handleControl(e, record.etlSubcribeId,record.creator);
                    }}
                    type="eye"
                  />
                  &nbsp;&nbsp;&nbsp;&nbsp;
                </Tooltip>
              </a>
            </div>
          );
        }
      }
    }
  ];

  columns1 = [
    {
      title: "上报任务",
      dataIndex: "taskName",
      key: "taskName"
    },
    {
      title: "开始时间",
      dataIndex: "startTime",
      key: "startTime"
    }
  ];

  componentWillReceiveProps(nextProps) {
    const { pagination, data, loading, total } = nextProps.exchangDataModel;
    console.log(nextProps.exchangDataModel, "nextProps.exchangDataModel");
    const { dispatch } = this.props;
    // this.setState({ pagination, data, loading, total });
  }

  componentDidMount() {
    // this.headClick();
    this.require();
  }

  /*查询接口*/
  require() {
    const { dispatch } = this.props;
    /*  const {series,xAxis} = this.state.option;*/
    getStatistics().then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        if (data) {
          let seriesa = [];
          let xAxisa = [];
          let xAxisTwo = [];
          for (let key of data.describes) {
            seriesa.push(key.month);
            xAxisa.push(key.dateCount);
            xAxisTwo.push(key.taskCount);
          }
          let series = [
            { data: [...xAxisa], type: "bar" },
            { data: [...xAxisTwo], type: "line" }
          ];
          let xAxis = { data: [...seriesa] };
          const option = { ...this.state.option, series, xAxis };
          this.setState({
            monthNamelist: series,
            dateCountlist: xAxis,
            count: res.data.data.count,
            /*...this.state,*/
            option: option
          });
        }
      }
    });

    getRunning().then(res => {
      const { code, data } = res.data;
      if (code === "200") {
        dispatch({
          type: "exchangDataModel/setMetaId",
          payload: {
            dataList: data.taskInfo,
            exCount: data.count
          }
        });
      }
    });
  }

  /*查询列表接口*/
  headClick=(setPage)=>{
    const { dispatch } = this.props;
    const fields = this.state.fields;  //获取查询方法值
    let arr = {};
    arr.taskName = fields.taskName.value;   //查询作业名称
    arr.deptName = fields.deptName.value;    //查询部门名称
    arr.taskType = fields.taskType.value !== "All" ? fields.taskType.value : "";   //查询类型
    arr.status = fields.status.value;    //查询状态值
    arr.pageNum = 1;

    /**
     * 直接调用getOverview方法
     * 不建议使用
     */

    dispatch({
      type: "exchangDataModel/getList",
      payload: arr
    });
  }

  /**
   * 新增分页加载的onchange方法
   * 取代TableList中的方法
   * @param {Object} pagination 
   */
  paginationChange(pagination){
    const {location} = this.props;
    const {fields} = this.state;
    let arr = {};
    arr.taskName = fields.taskName.value;
    arr.deptName = fields.deptName.value;
    arr.taskType = fields.taskType.value !== "All" ? fields.taskType.value : "";
    arr.status = fields.status.value;

    // 通过router触发注册事件
    const querys = __.assign(location,{query:{...arr,page: pagination.current,pageSize: pagination.pageSize}});

    hashHistory.push(querys);
  }

  /*打开监控*/
  handleControl = (e, name, creator) => {
    const { dispatch } = this.props;
    const { username, renterId } = this.props.account;
    let ower = strEnc(creator, username, ""+renterId, "GBXVDHSKENCNJDUSBZACXBLMKDICDHJNBC");
    cookie.set('resource_owner', ower, { Path: '/T' });
    dispatch({
      type: "controljobplatform/openJobs",
      payload: {
        visible: true,
        transName: name
      }
    });
  };


  /*获取搜索框的值*/
  handleFormChange = changedFields => {
    this.setState(({ fields }) => ({
      fields: { ...fields, ...changedFields }
    }));
  };

  render() {

    const { option, count } = this.state;
    const {
      dataList,
      exCount,
      data,
      pagination
    } = this.props.exchangDataModel;

    const fields = this.state.fields;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 12 }
    };
    return (
      <div style={{ margin: 20 }}>
        <Row gutter={20}>
            <Col span={17}>
              <Card
                  className="resetCardTitle"
                  title="作业统计"
                extra={<span className="bold_b">作业总量{count}</span>}
              >
              <ReactEcharts
                option={option}
                style={{ height: "260px", width: "98%" }}
                notMerge={false}
                lazyUpdate={true}
                theme={"theme_name"}
                onChartReady={this.onChartReadyCallback}
                onEvents={this.EventsDict}
              />
              </Card>
            </Col>
            <Col span={7}>
              <Card
                  className="resetCardTitle"
                  title={"正在进行的作业（最新5条）"}
                  extra={<span className="bold_b">进行中:{exCount}条</span>}
                >
                <div style={{height:260}}>
                  <TableList
                    showIndex
                    onRowClick={() => {
                      return false;
                    }}
                    columns={this.columns1}
                    dataSource={dataList}
                    className="th-nowrap"
                    pagination={false}
                  />
                </div>
              </Card>
            </Col>
        </Row>
        <div className="btn_std_group">
          <Form className="ant-advanced-search-form">
            <Row>
              <CustomizedForm {...fields} onChange={this.handleFormChange} headClick={this.headClick} />
            </Row>
          </Form>
        </div>

        <div>
          <TableList
            showIndex
            onRowClick={() => {
              return false;
            }}
            rowKey = {(record)=>record.taskName}
            style={{ marginTop: 20 }}
            columns={this.columns}
            dataSource={data}
            className="th-nowrap"
            pagination= {pagination}
            onChange={this.paginationChange}
          />
        </div>

        <ControlJobPlatform />
        <RunJob />
      </div>
    );
  }
}
const exchangeDatalistForm = Form.create()(exchangeDatalist);
export default connect(({ exchangDataModel, resourcesCommon, account }) => ({
  exchangDataModel,
  resourcesCommon,
  account
}))(withRouter(exchangeDatalistForm));
