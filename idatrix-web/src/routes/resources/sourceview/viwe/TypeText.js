import React from "react";
import { connect } from "dva";
import {withRouter, hashHistory} from "dva/router";
import __ from "lodash";

// import ReactEcharts from "echarts-for-react";
import {
  Tree,
  Menu,
  Dropdown,
  Row,
  Col,
  Icon,
  Tabs,
  Radio,
  Button,
  Form,
  Input,
  Tooltip,
  message,
  Select
} from "antd";
const FormItem = Form.Item;
const TreeNode = Tree.TreeNode;
const { TabPane } = Tabs;
const Option = Select.Option;
import Modal from "components/Modal";
/*import style from './../index.less';*/
import TableList from "../../../../components/TableList";
/*import ReactEcharts from 'echarts-for-react';*/
import { MLZYgetLib } from "../../../../services/DirectoryOverview";
import CheckView from "../../common/CheckView/index";
import Subscription from "../../common/Subscription/index";


class TypeText extends React.Component {

  constructor(){
    super();

    this.state = {
      visible: false,
      data: [],
      data1: [],
      loading: false,
      //选择部门
      options: [],
      BaseText: [],
      info: "",
      ifSearched: false  // 选择使用
    }

    this.changePagination = this.changePagination.bind(this);
  }

  /**
   * 虽然这个方法很全面
   * 但是，好像一点用也没有
   * 暂时注释掉，而且会导致重复update和请求锁
   * @edited by steven Leo on 2018/09/19
   */
  // request() {
  //   const pager = this.state.pagination;
  //   const { query } = this.props.location;
  //   // const { dispatch } = this.props;
  //   if (this.props.route.breadcrumbName === "基础库") {
  //     let arr = {};
  //     arr.libName = "base";
  //     arr.page = 1;
  //     arr.pageSize = query.pageSize ? query.pageSize : 10;
  //     MLZYgetLib(arr, {
  //       page: 1,
  //       pageSize: query.pageSize || pager.pageSize
  //     }).then(res => {
  //       const { code, data, total, message } = res.data;
  //       if (code === 200) {
  //         pager.total = data.total;
  //         data.results.map((row, index) => {
  //           row.key = row.id;
  //           row.index = pager.pageSize * (pager.page - 1) + index + 1;
  //           return row;
  //         });

  //         this.setState({
  //           BaseText: data.results,
  //           pagination: pager
  //         });
  //       } else {
  //         message.error(message);
  //         this.setState({
  //           BaseText: [],
  //           pagination: pager
  //         });
  //       }
  //     });
  //   } else if (this.props.route.breadcrumbName === "部门库") {
  //     let arr = {};
  //     arr.libName = "department";
  //     MLZYgetLib(arr, {
  //       page: 1,
  //       pageSize: query.pageSize || pager.pageSize
  //     }).then(res => {
  //       const { code, data, total, message } = res.data;
  //       if (code === 200) {
  //         pager.total = data.total;
  //         data.results.map((row, index) => {
  //           row.key = row.id;
  //           row.index = pager.pageSize * (pager.pager - 1) + index + 1;
  //           return row;
  //         });
  //         this.setState({
  //           BaseText: data.results,
  //           pagination: pager
  //         });
  //       } else {
  //         message.error(message);
  //         this.setState({
  //           BaseText: [],
  //           pagination: pager
  //         });
  //       }
  //     });
  //   } else if (this.props.route.breadcrumbName === "主题库") {
  //     let arr = {};
  //     arr.libName = "topic";
  //     MLZYgetLib(arr, {
  //       page: 1,
  //       pageSize: query.pageSize || pager.pageSize
  //     }).then(res => {
  //       const { code, data, total, message } = res.data;
  //       if (code === 200) {
  //         pager.total = data.total;
  //         data.results.map((row, index) => {
  //           row.key = row.id;
  //           row.index = pager.pageSize * (pager.pager - 1) + index + 1;
  //           return row;
  //         });
  //         this.setState({
  //           BaseText: data.results,
  //           pagination: pager
  //         });
  //       } else {
  //         message.error(message);
  //         this.setState({
  //           BaseText: [],
  //           pagination: pager
  //         });
  //       }
  //     });
  //   }
  // }

  componentWillReceiveProps(nextProps) {
    console.log(nextProps.indexType, "nextProps.indexType");
    const { pagination, BaseText, loading, total } = nextProps.indexType;
    const { dispatch } = this.props;
    this.setState({ pagination, BaseText, loading: false, total });
  }

  //更新表格
  componentDidMount() {
    /**
     * model（../model/indexType.js）中，已经订阅了了subscriptions
     * 会自动触发getList方法，故此处会重复触发
     * @edited by steven leo on 2018/09/19
     */
    // this.request();
  }

  columns = [
    {
      title: "资源分类",
      dataIndex: "catalogName",
      key: "catalogName",
      width: "20%"
    },
    {
      title: "资源代码",
      dataIndex: "resourceCode",
      key: "resourceCode",
      width: "15%"
    },
    {
      title: "资源名称",
      dataIndex: "resourceName",
      key: "resourceName",
      width: "15%"
    },
    {
      title: "提供方",
      dataIndex: "deptName",
      key: "deptName",
      width: "8%"
    },
    {
      title: "提供方代码",
      dataIndex: "deptCode",
      key: "deptCode",
      width: "12%"
    },
    {
      title: "数据量",
      dataIndex: "dataCount",
      key: "dataCount",
      width: "6%"
    },
    {
      title: "数据更新时间",
      dataIndex: "updateTime",
      key: "updateTime",
      width: "15%"
    },
    {
      title: "操作",
      key: "x12",
      width: "10%",
      render: (text, record) => {
        let hoRoor =
          record.subscribeFlag === 1
            ? "订阅"
            : "" || record.subscribeFlag === 2
              ? "已订阅"
              : "";
        return (
          <div>
            <a
              onClick={() => {
                this.handleClick(record);
              }}
            >
              查看
            </a>
            &nbsp;&nbsp;
            <a
              disabled={
                record.subscribeFlag === 0 || record.subscribeFlag === 2
              }
              onClick={() => {
                this.handleSubscription(record);
              }}
            >
              {hoRoor}
            </a>
          </div>
        );
      }
    }
  ];

  handleClick(record) {
    const { dispatch } = this.props;
    dispatch({
      type: "checkview/getEditResource",
      payload: { id: record.id, statue: "hide",type:"count" }
    });
  }

  //订阅
  handleSubscription(record) {
    const { dispatch } = this.props;
    dispatch({
      type: "subscriptionModal/getSubDetail",
      name: record.resourceName,
      status: "str",
      payload: { resourceId: record.id, status: "str" }
    });
  }

  changePagination(pagination){

    const {location, form} = this.props;

    // 获取field value
    let keys = ["name","code","deptName","deptCode"];
    let obj = form.getFieldsValue(keys);
    
    // 通过router触发注册事件
    const querys = __.assign(location,{query:{
      ...obj,
      page: pagination.current,
      pageSize: pagination.pageSize}}
    );

    hashHistory.push(querys);
  }

  onChange = e => {
    this.setState({ size: e.target.value });
  };

  /*查询数据字段*/
  handleEdit = e => {
    
    // 直接调用model方法
    this.changePagination({
      current: 1,
      pageSize: 10
    });
  };

  render() {
    const { visible, BaseText, info } = this.state;
    const { pagination } = this.props.indexType;

    console.log(pagination,"检测是否有分页")
    const { getFieldDecorator } = this.props.form;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 18 }
    };

    return (
      <div style={{ margin: 20 }}>
        <Form className="btn_std_group">
          <Row gutter={24}>
            <Col span={8}>
              <FormItem label={"资源名称"} {...formItemLayout}>
                {getFieldDecorator("name", {
                  initialValue: info.name ? info.name : ""
                })(<Input />)}
              </FormItem>
            </Col>
            <Col span={8}>
              <FormItem label={"资源代码"} {...formItemLayout}>
                {getFieldDecorator("code", {
                  initialValue: info.code ? info.code : ""
                })(<Input />)}
              </FormItem>
            </Col>

            <Col span={8}>
              <FormItem label={"提供方名称"} {...formItemLayout}>
                {getFieldDecorator("deptName", {
                  initialValue: info.deptName ? info.deptName : ""
                })(<Input />)}
              </FormItem>
            </Col>
            <Col span={8}>
              <FormItem label={"提供方代码"} {...formItemLayout}>
                {getFieldDecorator("deptCode", {
                  initialValue: info.deptCode ? info.deptCode : ""
                })(<Input />)}
              </FormItem>
            </Col>
            <Col span={8}></Col>
            <Col span={8} className="search_btn">
              <Button type="primary" onClick={this.handleEdit}>
                查询
              </Button>
            </Col>
          </Row>
        </Form>

        <Row>
          <Col span={24}>
            <TableList
              showIndex
              style={{ marginTop: 20 }}
              columns={this.columns}
              loading={this.state.loading}
              dataSource={BaseText}
              pagination={pagination}
              onChange = {this.changePagination}
            />
          </Col>
        </Row>
        <CheckView />
        <Subscription />
      </div>
    );
  }
}
const TypeTextForm = Form.create()(TypeText);
export default connect(({ indexType }) => ({
  indexType
}))(withRouter(TypeTextForm));
