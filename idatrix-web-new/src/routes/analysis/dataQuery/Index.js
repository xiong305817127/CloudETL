/**数据查询2.0优化版*/
//一、环境：
import React from 'react'; // 核心
import { connect } from 'dva'; // 传参
import { Row, Col, Form, Input, Layout, Tabs, Button, Alert, Tree, message, Icon,Table } from 'antd';// 组件标签
import TableList from 'components/TableList'; // 自定义表格
import Empower, { isEmpowered } from 'components/Empower'; // 自定义权限
import { searchHistoryList, getSchemaInfo,getDataNewList, actionDataNewList } from 'services/analysis'; // API接口
import AceEditor from 'react-ace';
import 'brace/mode/mysql';
import 'brace/theme/xcode';
import 'brace/ext/language_tools';

import updateCompletions from 'utils/updateSQLCompletions';

import Style from './Index.css'; // 自定义样式

/**
 * 封装自动完成所需数据格式
 * @param  {object} data   表及字段信息
 * @param  {string} dbname 库名称
 * @return {object}        [description]
 */
function createSchemaInfo(data, dbname) {
  const tables = {};
  Object.keys(data).forEach((key) => {
    if (Array.isArray(data[key])) {
      tables[key] = data[key].map(it => ({
        table_schema: dbname,
        table_name: key,
        column_name: it,
      }));
    }
  });
  return { [dbname]: tables };
}

const { Sider, Content } = Layout; // 组件套件
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const TreeNode = Tree.TreeNode;
const { TextArea } = Input;
//二.渲染：
class DataQueryTable extends React.Component {
  //1.初始化:
  constructor(props) {
    super(props);//构造函数:预加载表单所有参数props
    this.columnsHistory = [
      {
        title: '时间',
        dataIndex: 'createTime',
        width: 250
      }, {
        title: '状态',
        dataIndex: 'status',
        width: 200,
        render: (text, record, index) => ( record.results[0].status === 'success' ? '成功' : (record.results[0].status === 'failed' ? '失败' : '超时'))
      }, {
        title: '查询语句',
        dataIndex: 'sql',
        render: (text, record, index) => (record.results[0].sql)
      }
    ];
    this.placeholderOptions = {
      'splDefault': "请选择二级菜单以下对应的选项，再执行操作。",
      'HIVE': '示例: select * from table_name;',
      'HBASE': "示例1: select * from table_name limit 1000 offset 100;\n示例2: upsert into table_name(c1,c2,c3) values('v1','v2','v3');\n示例3: delete from table_name where name like 'foo%'; ",
      'MYSQL': "示例1: select * from table_name；\n示例2: insert into table_name (c1,c2,c3) values('v1','v2','v3');\n示例3: delete from table_name where name like 'foo%'；\n示例4: update table_name set c1=v1 where c2=v2; "
    };
    this.state = {
      dbList: [],//菜单列表：数组类型
      selected: false,//左侧菜单被选与否
      prepSql: '',//sql执行:语句参数，通过表单绑定
      userName: '',//正在输入的sql语句参数，文本域事件绑定
      resultColumns: [],//执行结果：标题
      resultData: [],//执行结果：数据
      prepStorage: '',//执行结果参数：一级菜单列表名称，如HIVE
      prepDS: '',//执行参数：二级菜单同级ds_name
      prepSchema:'', //执行参数 获取schamename
      placeholderValue: 'splDefault',//文本域提示文本
      //执行历史参数和结果：
      storage: null,//执行历史参数：一级菜单列表名称，如HIVE
      dataHistory: [],//执行历史数据
      activeKey: '1',//标签页：默认查询结果页面
      //报错提示框
      alertSuccess: false,
      alertError: false,
      alertWarning: false,
      alertResult: false,
      alertMsg: '',
      loading: false,
      resultLength:1000
    };
    this.DataSource=[];
  }
  //2.预加载菜单数据：
  componentWillMount() {
    getDataNewList().then(res => {
			// console.log("1.菜单列表：",res);
      const { code,data } = res.data;
      console.log(data,"data============");
      if(code === "200"){
        this.setState({
          dbList: data || [],
        })
      }
    })
  }
  //3.左侧菜单:点击树节点触发，获取的是选择项值，可以自定义传参titleValue
  onSelect = async (selectedKeys, e) => {
    // console.log('111:',selectedKeys);
     console.log(e.node.props.titleDefault,'点击菜单列表:',e.node.props);
    this.state.storage = e.node.props.headMenu;
    if(this.state.activeKey === '2' && e.selected){//当切换到查询页面，且选中时
      this.onLoadingSearch();
    };
    this.setState({
      selected:e.selected,//选中判断
      placeholderValue:e.node.props.titleDefault,//执行语句的提示信息
      prepStorage:e.node.props.headMenu,//执行参数1 第二层的dbname
      prepDS:e.node.props.titleDefault,//执行参数2  第二层的dsname
      prepSchema:e.node.props.actionValue,//执行参数3  最后一层的Schema名称
      alertResult: false,
      alertWarning:false,
      alertSuccess:false,
      alertError:false,
      alertMsg: null,
    });
    // 查询数据库相关信息，并更新自动完成提示
    const schemaId = e.node.props.titleDefault;
   if(e.node.props.pos !== "0-0-0" && e.node.props.pos !== "0-0-1" &&
      e.node.props.pos !== "0-0-2" && e.node.props.pos !== "0-0-3" &&
      e.node.props.pos !== "0-0-4" && e.node.props.pos !== "0-0-5"){
      if (schemaId) {
        const { data } = await getSchemaInfo({ schemaId });
        const info = data && data.code ==="200" || {};
        const schemaInfo = createSchemaInfo(info, schemaId);
        updateCompletions(schemaInfo);
      }
   }

  
  };
  onExpand = (expandedKeys) => {
    // console.log('折叠菜单时触发:', expandedKeys);
  };
  //查询历史：条件是先选择菜单，查询失败有提示，查询成功不提示
  onLoadingSearch =()=>{
    let obj = {};
    obj.storage = this.state.storage;//选中菜单才有参数
    obj.rows = 20;
    searchHistoryList(obj).then(res=>{
      // console.log('查询参数和历史：',obj,res);
      if(res.data && res.data.code== "200"){
        // console.log(111,res.data.data);
        this.setState({
          dataHistory:res.data.data,
          activeKey: this.state.activeKey,
          alertResult:false,
          alertWarning:false,
          alertSuccess:false,//不提示成功弹框
          alertError:false,
          alertMsg: null,//res.data.data.msg:成功
        })
        // this.state.dataHistory?setTimeout(message.success('执行成功'),30000):null;
      }else{
        this.setState({
          dataHistory: null,//清空查询历史
          alertWarning: false,
          alertResult: false,
          alertSuccess:false,
          alertError:false,//由于后台未给数据，另做提示内容：超时或语句错误
          alertMsg: null,//res.data.msg
        });
        setTimeout(message.warning('查询历史失败，请稍后重试。'),30000)
      }
    });
  }
  //4..执行按钮：
  handleSubmit = (e) => {
    e.preventDefault();
    this.setState({
      resultData: null,//清空数据：异步
      resultColumns: null,
      alertWarning: false,
      alertResult: false,
      alertSuccess:false,
      alertError:false,//由于后台未给数据，另做提示内容：超时或语句错误
      alertMsg: null,//res.data.msg
      activeKey:'1',//切换到查询结果页面
    });
    this.props.form.validateFields((err,values) =>{
      if(!err){
        this.state.loading=true;
         console.log(this.state,"输入的值",values);
        let obj = {};
       // obj.prepStorage = this.state.prepStorage;//HIVE等一级菜单参数
      //  obj.prepDS = this.state.prepDS;//DS_NAME
        obj.prepSql = values.prepSql;//输入的语句
        obj.prepSchemaId = this.state.prepSchema; //获取schemaname
        actionDataNewList(obj).then( res => {
					// console.log(111, res );
					const { code } = res.data;
          if(code === "200") {
            //判断类型输出：再输出格式转换，成功输出或错误提醒loading关闭
            this.setState({loading:false});
            if(res.data.data && res.data.data.type ==='update'){//【update类型】成功、失败和超时都不在表格内输出
              if(res.data.data.results && res.data.data.results[0].status === "success" ){
                this.setState({
                  resultColumns:null,//不在表格内输出
                  resultData: null,
                  alertWarning:false,
                  alertSuccess:true, //update类型成功
                  alertError:false,
                  alertMsg:'耗时'+res.data.data.expendTime+'秒；'+res.data.data.results[0].result
                })
              }else {
                this.setState({
                  resultColumns:null,//不在表格内输出
                  resultData: null,
                  alertWarning:false,
                  alertSuccess:false,
                  alertError:true, //update类型失败
                  alertMsg:'耗时'+res.data.data.expendTime+'秒；'+res.data.data.results[0].result
                })
              };
            }else{//【select类型】：可以查询到结果，但可能查询结果为空
              if (res.data.data.results && res.data.data.results[0].status === "success") {
                //【动态输出结果】：resultColumns + resultData
                if(res.data.data.results[0] && res.data.data.results[0].result){
                  // console.log(Object.keys(res.data.data.results[0]));//取出键值对中的名称
                 // console.log(JSON.parse(res.data.data.results[0].result).columns,111);//{"columns":["NAME","ID"]}取标题
                  //console.log(JSON.parse(res.data.data.results[0].result).values,'内容');//内容
                  const resultColumns = (JSON.parse(res.data.data.results[0].result).columns || []).map((key, index) => ({
                    title: key,width: 200,dataIndex: key,
                    render:(text)=><div>{text}</div>
                  }));
                  const resultData = (JSON.parse(res.data.data.results[0].result).values || []).map((col, index) => {
                    // console.log(col,":内容");
                    const data ={};
                    (JSON.parse(res.data.data.results[0].result).columns || []).forEach((colName, index) =>{
                      // console.log(colName,":标题");
                      data[colName] = col[index];
                      // console.log(data);
                    })
                    return data;
                  });
                  this.setState({
                    resultColumns:resultColumns,
                    resultData: resultData,
                    resultLength:resultColumns.length*200+50,
                    alertWarning:false,
                    alertError:false,
                    alertSuccess:true,
                    alertMsg:'耗时'+res.data.data.expendTime+'秒。',
                  });
                }else{
                  this.setState({
                    resultColumns:null,//执行失败也要清空表格
                    resultData: null,
                    alertWarning:false,
                    alertError:false,
                    alertSuccess:true,
                    alertMsg:'耗时'+res.data.data.expendTime+'秒。',
                  });
                }
              }else{//select类型执行失败
                this.setState({
                  resultColumns:null,//执行失败也要清空表格
                  resultData: null,
                  alertWarning:false,
                  alertSuccess:false,
                  alertError:true,
                  alertMsg:'耗时'+res.data.data.expendTime+'秒；'+res.data.data.results[0].result
                })
              }
            }
          }else{//最外层报错
            this.setState({
              resultColumns:null,//执行失败也要清空表格
              resultData: null,
              alertWarning:false,
              alertSuccess:false,
              loading: false,
              alertError: res.err ? false : true,
              alertMsg: res.err ? null : res.data.msg
            })
          }
        })
      }
    });
  };
  //5.切换栏:查询结果/查询历史
  changeTag =(a) => {
    // console.log("切换到",a);
    // console.log(this.state.selected);
    this.setState({
      activeKey: a,
      alertWarning: this.state.selected?false:true,
      alertSuccess: false,//关闭其他警告
      alertError: false,
      alertResult:false,
      alertMsg:null,
      placeholderValue: 'splDefault'
    })
    if(a === '2' && this.state.selected){
      this.onLoadingSearch();
    }
  };
  //6.其他：自动清空执行表单：
  emitEmpty = () => {
    this.userNameInput.focus();
    this.props.form.resetFields();
    this.setState({ userName: '' });
  };
  onChangeUserName = (e) => {
    // console.log("正在输入的内容",e.target.value);
    this.setState({ userName: e.target.value });
  };

  //模拟输入
  handleTask = ()=>{
      const res = { 
        data:{}
      }
      const resultColumns = (JSON.parse(res.data.data.results[0].result).columns || []).map((key, index) => ({
        title: key,width: 200,dataIndex: key,
        render:(text)=><div>{text}</div>
      }));
      const resultData = (JSON.parse(res.data.data.results[0].result).values || []).map((col, index) => {
        // console.log(col,":内容");
        const data ={};
        (JSON.parse(res.data.data.results[0].result).columns || []).forEach((colName, index) =>{
          // console.log(colName,":标题");
          data[colName] = col[index];
          // console.log(data);
        })
        return data;
      });
      this.setState({
        resultColumns:resultColumns,
        resultData: resultData,
        resultLength:resultColumns.length*200+50,
        alertWarning:false,
        alertError:false,
        alertSuccess:true,
        alertMsg:'耗时'+res.data.data.expendTime+'秒。',
      });
  }

  render(){
    const dbList = this.state.dbList;
    console.log(this.state,"this.state=====",dbList);
    const { getFieldDecorator } = this.props.form;
    const suffix = this.state.userName? <Icon type="close-circle" onClick={this.emitEmpty} /> : null;


      console.log(Object.keys(dbList),"dbList");
    return(
      <Layout  style={{minheight:'100%'}}>
        {/*SQL查询菜单*/}
        <Sider style={{backgroundColor:'#ffffff',overflow:'auto'}} >
          <Tree
            defaultExpandAll={true}//默认展开所有树节点
            onSelect={this.onSelect}//点击树节点触发，获取的是选择项值
            onExpand={this.onExpand}//获取展开项是一个数字数组
          >
            <TreeNode key="数据系统" title="数据系统" >
              {
                Object.keys(dbList).map(key=> {
                  console.log(key,"key====================",dbList[key])
                  return (
                    // <TreeNode key={key} title={key}  headMenu={key} titleDefault="splDefault" >
                    //  {
                       //  Object.keys(dbList).map((keyChild, index)=> {
                          // console.log(keyChild, index,"keyChild, index============");
                        //  return (
                            <TreeNode key={dbList[key].dbType} title={dbList[key].dbType} actionValue={dbList[key].dbType} headMenu={dbList[key].dbType}>
                              {
                                 dbList[key].serverList?dbList[key].serverList.map((tableName, item)=> {
                                  console.log(tableName, item,"tableName, item");
                                  //titleDefault是传的值
                                  return (
                                    <TreeNode key={tableName.ip} title={tableName.ip} actionValue={tableName.schemaId} headMenu={dbList[key].dbType} titleDefault={tableName.schemaId}>
                                        {
                                         tableName.schemaList?tableName.schemaList.map((name, val)=> {
                                            console.log(name, val,"name, val");
                                            //titleDefault是传的值
                                            return (
                                              <TreeNode key={name.schemaName} title={name.schemaName} actionValue={name.schemaId} headMenu={dbList[key].dbType} titleDefault={name.schemaId}>
                                                   {
                                                      name.tableViewList?name.tableViewList.map((names, vals)=> {
                                                          console.log(name, val,"name, val");
                                                          //titleDefault是传的值
                                                          return (
                                                            <TreeNode key={names.metaName} title={names.metaName} actionValue={name.schemaId} headMenu={dbList[key].dbType} titleDefault={name.schemaId}>
                                                            
                                                            </TreeNode>
                                                          );
                                                        }):[]
                                                      }
                                              </TreeNode>
                                            );
                                          }):[]
                                        }
                                    </TreeNode>
                                  );
                                }):[]
                              }
                            </TreeNode>
                      //    );
                      //  })
                    //   } 
                    // </TreeNode>
                  );
                })
              }
            </TreeNode>
          </Tree>
        </Sider>
        {/*SQL编辑区+标签页(查询结果/历史)*/}
        <Content style={{marginLeft:10, backgroundColor:'#ffffff',minHeight:780}}>
          {/*1.SQL编辑框：*/}
          <Form  onSubmit={this.handleSubmit} style={{margin:'20px'}}>
            <FormItem >
              {getFieldDecorator('prepSql', {
                rules: [{ required: true, message: 'SQL语句不能为空' }],
              })(
                <AceEditor
                  mode="mysql"
                  theme="xcode"
                  name="prepSql"
                  fontSize={14}
                  showPrintMargin={true}
                  showGutter={true}
                  highlightActiveLine={true}
                  style={{ width: '100%', height: 120, border: '1px solid #ddd',marginBottom:20 }}
                  readOnly={!this.state.prepDS}
                  setOptions={{
                    enableBasicAutocompletion: true,
                    enableLiveAutocompletion: true,
                    enableSnippets: false,
                    showLineNumbers: true,
                    tabSize: 2,
                  }}
                />
              )}
              <div style={{ color: '#aaa',position:'relative',top:14 }}>{this.placeholderOptions[this.state.placeholderValue]}</div>
            </FormItem>
            <Row>
              {/*自动清空图标:*/}
              <Col offset={23}><a style={{fontSize:'16px',position:'absolute',top:'-115px',right:20}}>{suffix}</a></Col>
              {/*执行按钮:*/}
              {/*<Button onClick={this.handleTask.bind(this)}>测试执行</Button>  */}
              <Col offset={21}>
                <Empower api="/db/sql/execute">
                  <Button style={{marginTop:-40,position:'absolute',right:20}} loading={this.state.loading} htmlType="submit" type="primary"  disabled={!this.state.prepDS}>
                    执行
                  </Button>
                </Empower>
              </Col>
            </Row>
          </Form>
          {/*2.提示框*/}
          {/*错误显示提醒，alertMsg */}
          <div  style={{margin:'0px 20px'}}>
            {this.state.alertWarning ? (
              <Alert
                message="提示："
                description="请选择左侧菜单选项，查看对应的执行历史。"
                type="warning"
                showIcon
                closable
                onClose={()=>this.setState({alertWarning:false})}
                style={{marginBottom:'20px',color:'#ffbf7a'}}
              />
            ) : null}

            {this.state.alertSuccess ? (
              <Alert
                message="成功："
                description={this.state.alertMsg}
                type="success"
                showIcon
                closable
                onClose={()=>this.setState({ alertSuccess:false})}
                style={{marginBottom:'20px'}}
              />
            ) : null}

            {this.state.alertError ? (
              <Alert
                message="失败："
                description={this.state.alertMsg}
                type="error"
                showIcon
                closable
                onClose={()=>this.setState({alertError:false})}
                style={{marginBottom:'20px',color:'red'}}
              />
            ) : null}
          </div>
          <Tabs
            defaultActiveKey="1"
            type="card"
            onChange={this.changeTag.bind(this)}
            activeKey={this.state.activeKey}
            style={{margin:10,overflow:"scroll"}}
          >
            {/*标签1：查询结果，标题和数据均来自后台*/}
            <TabPane tab="执行结果" key="1" >
              <TableList
                showIndex
                scroll={{x:parseInt(`${this.state.resultLength}`),y:600}}
                columns={this.state.resultColumns}//模拟数据this.state.resultColumns；this.columns
                dataSource={this.state.resultData}//模拟数据this.state.resultData；this.dataSourc
                pagination={false}
              />
            </TabPane>
            {/*标签2：查询历史:*/}
            {isEmpowered('/db/sql/history', this.props.system) ? (
              <TabPane tab="执行历史" key="2" >
                <TableList
                  rowKey={record => record.id}
                  columns={this.columnsHistory}
                  dataSource={this.state.dataHistory}
                  dateFormat
                  pagination={false}
                  rowClassName={(record, index) => record.results[0].status === 'success'? '': (record.results[0].status === 'failed'? Style.rowRed:Style.rowOrange)}
                />
              </TabPane>
            ) : null}
          </Tabs>
        </Content>
      </Layout>
    )
  }
}
//三.调用：
const App = Form.create()(DataQueryTable);
export default connect(({ system }) => ({
  system,
}))(App);
