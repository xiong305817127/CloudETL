//一、定义：环境、封装标签
import React from 'react';
import { Popconfirm, Table, message, Button, Icon, Row, Col, Tooltip } from 'antd';
import { withRouter } from 'react-router';
import TableList from '../../../components/TableList';
import Search from '../../../components/Search';
import Empower from '../../../components/Empower';
const { Column } = Table;
//引入组件：新建
import AddEasyTable from "../components/TableApi/AddEasyTable";
// 导入接口:
import {getTableList, addTableList, editTableList, deleteTableList,} from '../../../services/service';

//二、输出：主干
class MyAppTable extends React.Component{
  //1.初始化
  state = {
    data: [],
    pagination: {
      current: 1,
      pageSize: 10,
    },
    editorVilible: false,
    editorFields: {},
    routerListened: false, // 是否已监听路由
    isMounted: false, // 组件是否已挂载
  };
  //2.页面加载后
  componentDidMount() {
    const { pagination } = this.state;
    pagination.current = parseInt(this.props.location.query.page);
    this.setState({
      pagination,
      isMounted: true,
    }, () => {
      // 监听路由，当查询参数改变时，重新加载列表
      if (!this.state.routerListened) {
        this.props.router.listen(location => {
          if (location.pathname === '/MyAppTable' && this.state.isMounted) {
            this.loadList();
          }
        });
        this.setState({
          routerListened: true
        });
      }
    });
  }
  // 执行事件：加载列表
  loadList() {
    const { query } = this.props.location;
    const pager = { ...this.state.pagination };
    const loadFunc = getTableList;
    this.setState({
      loading: true,
    });
    loadFunc({
      pageNum: query.page || 1,
      pageSize: query.pageSize || pager.pageSize,

      // 此处由query为encodeURIComponent改为unEncoded
      keyword: query.keyword,
    }).then(res => {
      if (res.data && res.data.data) {
        const { total, results } = res.data.data;
        pager.total = total;
        results.map( (row, index) => {
          row.key = row.id;
          row.index = pager.pageSize * (pager.current - 1) + index + 1;
          return row;
        });
        this.setState({
          data: results,
          pagination: pager,
          loading: false,
        });
      }
    });
  }
  componentWillUnmount() {
    this.state.isMounted = false;
  }
  // 新增 / 编辑
  handleSubmitEditer(fields) {
    if (fields.id) {
// 1.编辑：未编辑内容设置提醒
      // console.log(fields,111);
      if(fields.name === this.state.editorFields.name && fields.description === this.state.editorFields.description ){
        //错误提示：
        message.error("应用名称和描述没有改动");
        //错误提示：setFields = this.props.form.setFields
      }else {
        editTableList(fields).then(res => {
          if (res.data && res.data.flag) {
            const { data } = this.state;
            data.map(row => {
              if (row.id === fields.id) {
                Object.assign(row, fields, { isUpdated: true });
              }
              return row;
            });
            this.setState({ data, editorVilible: false });
            // message.success('已修改');
            this.loadList();
          } else {
            // message.error(res.data.message);
            this.setState({ editorVilible: false });
          }
        });
      }
// 2.新增
    } else {
      addTableList(fields).then(res => {
        if (res.data && res.data.flag) {
          // message.success('新增成功');
          this.loadList();
        } else {
          // message.error(res.data.message);
        }
        this.setState({ editorVilible: false });
      });
    }
  }
  //搜索：encodeURIComponent()
  handleSearch(keyword) {
    console.log(keyword,111);
    const location = this.props.location;
    if (keyword) {
      location.query.keyword = keyword;
    } else {
      delete location.query.keyword;
    }
    console.log(keyword,11111);
    this.props.router.push(location);
  }
  // 分页切换时执行
  handleTableChange(pagination) {
    const location = this.props.location;
    location.query.page = pagination.current;
    location.query.pageSize = pagination.pageSize;
    this.props.router.push(location);
  }
  // 打开新增弹窗
  onAdd() {
    this.setState({
      editorFields: {
        id: '',
        name: '',
        description: '',
      },
      editorVilible: true,
    });
  }
  // 打开编辑弹窗:防止新建内容影响、调整激活状态的顺序
  onEdit(record, index) {
    console.log(record,111);
    this.setState({
      editorFields: {
        id: record.id,
        name: record.name,
        description: record.description,
      },
      editorVilible: true,
    });
  }
  //删除行：
  onDelete(record) {
    const { id } = record;
    deleteTableList([{ id }]).then(res => {
      this.loadList()
    });
  }
  // 获取行样式
  getRowClassName(record, index) {
    return '';
    // return record.isUpdated ? Style['updated-row'] : '';
  }
  //输出页面结构、内容
  render(){
    const { query } = this.props.location;//传参
    return(
      <div id="MyAppTable" style={{backgroundColor: '#fff'}}>
        {/*搜索*/}
        <header style={{padding:20, marginLeft: 50, marginBottom: 20}}>
          <Search
            onSearch={val => this.handleSearch(val)}
            defaultValue={query.keyword}
            placeholder="输入应用名称或者AppKey"
          />
        </header>
        {/*新建*/}
        <Row style={{backgroundColor:'white'}}>
          <Col span={24}>
            <Empower api="/app/add">
              <Button type="primary" style={{marginLeft:'20px'}} onClick={() => this.onAdd()}>新建</Button>
            </Empower>
          </Col>
          <AddEasyTable
            okCb={fields => this.handleSubmitEditer(fields)}
            cancelCb={() => {this.setState({ editorVilible: false })}}
            modalVisible={this.state.editorVilible}
            fields={this.state.editorFields}
          />
        </Row>
        {/*表格*/}
        <Row style={{backgroundColor:'#fff'}}>
          <TableList
            dataSource={this.state.data}
            pagination={this.state.pagination}
            showIndex
            onChange={this.handleTableChange.bind(this)}
            rowClassName={this.getRowClassName.bind(this)}
            className="th-nowrap "
            style={{backgroundColor:'white',padding:'20px'}}
            loading={this.state.loading}
          >
            {/*表格标题+内容*/}
            <Column title="序号" dataIndex="__index" key="id"  width={80} />
            <Column title="应用名称" dataIndex="name" key="name"/>
            <Column title="应用说明" dataIndex="description" key="description" render={(text, record, index) => (<div className="word25" title={text}>{text}</div>)} />
            <Column title="识别码(AppKey)" dataIndex="code" key="code" />
            <Column title=" 应用密钥(AppSecret)" dataIndex="secret" key="secret" />
            {/*<Column title="权限状态" dataIndex="status" key="status"/>*/}
            <Column className="editable-row-operations td-nowrap" title="操作" dataIndex="edit"  key="edit"
                    render={(text, record, index) => (
                      <div>
                        <Empower api="/app/update">
                          <a onClick={() => this.onEdit(record, index)}>
                            <Tooltip title="编辑" >
                              <Icon type="edit" className="op-icon"/>&nbsp;&nbsp;&nbsp;&nbsp;
                            </Tooltip>
                          </a>
                        </Empower>
                        {/*<Empower api="/app/delete" disabled={false}>
                          <Popconfirm disabled placement="topLeft" title="确认要删除该行吗？" onConfirm={() => this.onDelete(record, index)} >
                            <a>
                              <Tooltip title="删除" disabled>
                              <Icon disabled type="delete" className="op-icon"/>
                            </Tooltip>
                            </a>
                          </Popconfirm>
                        </Empower>*/}
                      </div>
                    )}
            />
          </TableList>
        </Row>
      </div>
    )
  }
};

//三、执行：
export default withRouter(MyAppTable);
