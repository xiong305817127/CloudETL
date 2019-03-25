import React from "react";
import BaseComponent from "components/BaseComponent";

import { connect } from "dva";
import { Link, withRouter } from "react-router";
import {
  Input,
  Button,
  Radio,
  Steps,
  message,
  Table,
  Form,
  Select,
  Row,
  Col,
  TreeSelect,
  Checkbox
} from "antd";
import Modal from "components/Modal";
const FormItem = Form.Item;
const Step = Steps.Step;
const RadioGroup = Radio.Group;
const Option = Select.Option;
const { TextArea } = Input;
const CheckboxGroup = Checkbox.Group;
import Style from "../style.css";
import __ from "lodash";
import { check_if_dsname_exists } from "../../../../services/metadata";
import TableList from "../../../../components/TableList";
import {
  getStoreDatabase,
} from "../../../../services/metadataCommon";
import {
  SJCJgetTableInfo,
  GETSCHEMALIST,
  SJCJisExists
} from "../../../../services/AcquisitionCommon";
import { safeJsonParse, deepCopy } from "utils/utils";
import dbDataType from "../../../../config/dbDataType.config";
import { databaseType } from "config/jsplumb.config";
const steps = [
  {
    title: "配置数据库",
    content: "First-content"
  },
  {
    title: "选择表",
    content: "Second-content"
  },
  {
    title: "设置元数据基础信息",
    content: "Last-content"
  }
];
let Timer;
class Acquisition extends BaseComponent {
  state = {
    current: 0,
    visible: false,
    info: {},
    value: 1,
    type: true,
    data2: [],
    optionsSelect: [],
    FildData: [],
    dbDataTypeList: dbDataType["mysql"],
    pluginId: "",
    caijiList: [],
    nextList: true,
    tempObj: {},
    expandedTables: [],
    expandedRowKeys: []
  };

  columns2 = () => {
    const acquisition = this.props.acquisition.lengthss;
    const str = JSON.stringify(acquisition);
    const { getFieldDecorator } = this.props.form;
    return [
      {
        title: "字段名称",
        dataIndex: "name",
        key: "name",
        width: "23%"
      },
      {
        title: "字段类型",
        dataIndex: "type",
        key: "type",
        width: "20%"
      },
      {
        title: "长度",
        dataIndex: "prontioan",
        key: "prontioan",
        width: "15%"
      },
      {
        title: "是否主键",
        dataIndex: "isPrimaryKey",
        key: "isPrimaryKey",
        width: "17%",
        render: text => (text === 1 ? "是" : "否")
      },
      {
        title: "是否允许为空",
        dataIndex: "nullable",
        key: "nullable",
        width: "15%",
        render: text => (text === 1 ? "是" : "否")
      }
    ];
  };
  columns1 = [
    {
      title: "原表名",
      dataIndex: "tables",
      key: "tables",
      width: "40%"
    },
    {
      title: "表中文名/注释",
      dataIndex: "targetTableCn",
      key: "targetTableCn",
      width: "30%",
      render: (text, record) => {
        const { getFieldDecorator } = this.props.form;
        return (
          <FormItem style={{ marginBottom: "0px" }}>
            {getFieldDecorator(`targetTableCn.${record.key}`, {
              initialValue: text,
              rules:[
                // {validator: this.checkIfEmpty,message: "此表为必填项"}         
              ]
            })(
              <Input
                placeholder="可编辑"
                onChange={e => {
                  this.modifyField(`targetTableCn`, e.target.value, record);
                }}
              />
            )}
          </FormItem>
        );
      }
    },
    {
      title: "操作",
      render: (text, record) => {
        // 此处使用table检索详细的表字段，注意：table对应的是sourseTableEn
        // edited by steven leo on 2018/09/25
        return (
          <Button onClick={() => this.tableExpandRequest(null,record)}>查看字段</Button>
        );
      }
    }
  ];

  /**
   * 检查表格中的英文名是否为空
   */

  
  /**
   * 通过按钮展开子表单
   * written by steven leo on 2018/09/25
   */
  expandTable = key => {
    console.log(key,this.state);
    let tempArr = this.state.expandedRowKeys;
    let index = tempArr.indexOf(key);
    let tempArrs = [];

    if(index === -1){
      tempArrs = tempArr.concat(key);

      this.setState({
        expandedRowKeys: tempArrs
      });
    }else{
      tempArr.splice(index,1);

      this.setState({
        expandedRowKeys: tempArr
      });
    }
  };

  /**
   * 检索表详情
   */
  showDetails = table => {
    const { dispatch } = this.props;
    dispatch({
      type: "acquisition/GetTableDetails",
      payload: {
        tableName: table,
        ...this.state.tempObj
      }
    });
  };

  /**
   * 点击table展开时的回调
   */
  tableExpandRequest = (expanded, record) => {
    this.showDetails(record.table);
    this.expandTable(record.key);
  };

  /**
   * 检测描述修改
   */
  changeTableDetails = (type, value, record) => {
    const {expandedTables} = this.props.acquisition;
    const {dispatch} = this.props;
    // 获取表字段
    const data = expandedTables[record.table];
    const newData = data.map(val=> (val.colName == type ? {...val,description:value} : val ));
    
    dispatch({type:"acquisition/setMetaId",
      payload: {
        expandedTables: {
          ...expandedTables,
          [record.table]: newData
        }
      }
    });
  };


  /**
   * 表格详细信息
   */
  tableDetails = expandedTables => {
    const { getFieldDecorator } = this.props.form;

    return (record, index, indent, expanded) => {
      const columns = [
        { title: "字段", dataIndex: "colName", key: "colName" },
        { title: "类型", dataIndex: "dataType", key: "dataType" },
        {
          title: "为空",
          dataIndex: "isNull",
          key: "isNull",
          render: text => (text === "0" ? "否" : "是")
        },
        {
          title: "主键",
          dataIndex: "isPk",
          key: "isPk",
          render: text => (text === "0" ? "否" : "是")
        },
        { title: "长度", dataIndex: "length", key: "length" },
        {
          title: "精度",
          dataIndex: "precision",
          key: "precision",
          render: text => (text ? text : "无")
        },
        {
          title: "描述",
          dataIndex: "description",
          key: "description",
          render: (text, recordExpanded) => {
            console.log();
            return (
              <FormItem style={{ marginBottom: "0px" }}>
                {getFieldDecorator(`description${recordExpanded.colName}`, {
                  initialValue: text
                })(
                  <Input
                    placeholder="可编辑"
                    onChange={e => {
                      this.changeTableDetails(
                        recordExpanded.colName,
                        e.target.value,
                        record
                      );
                    }}
                  />
                )}
              </FormItem>
            );
          }
        }
      ];

      return (
        <Table
          columns={columns}
          dataSource={expandedTables ? expandedTables[record.table] : []}
          pagination={false}
          rowKey={"colName"}
        />
      );
    };
  };

  // 修改字段
  modifyField(keyOfCol, value, record) {
    const { dispatch } = this.props;
    const { data1 } = this.props.acquisition;
    console.log(value, "修改后的值");
    const data = deepCopy(data1).map(row => {
      if (row.key === record.key) {
        row[keyOfCol] = value;
      }
      return row;
    });

    console.log(data)
    dispatch({ type: "acquisition/setMetaId", payload: { data1: data } });
  }

  columnsTwo = () => {
    const acquisition = this.props.acquisition.lengthss;
    const str = JSON.stringify(acquisition);
    return [
      {
        title: "列名",
        dataIndex: "name",
        key: "name",
        width: "23%"
      },
      {
        title: "数据类型",
        dataIndex: "type",
        key: "type",
        width: "20%"
      },
      {
        title: "长度",
        dataIndex: "prontioan",
        key: "prontioan",
        width: "18%"
      },
      {
        title: "是否主键",
        dataIndex: "isPrimaryKey",
        key: "isPrimaryKey",
        width: "18%",
        render: text => (text === 1 ? "是" : "否")
      },
      {
        title: "允许为空",
        dataIndex: "nullable",
        key: "nullable",
        width: "18%",
        render: text => (text === 1 ? "是" : "否")
      }
    ];
  };
  Import(keyOfCol, value, record) {
    const args = this.props.acquisition.data2;
    args.some(item => {
      if (item.id == record.id) {
        item[keyOfCol] = value;
        // 切换数据类型时处理
        if (keyOfCol === "isPk" && record.isPk === "1") {
          item.isNull = "0";
          this.setState({
            cities: cityData[value],
            secondCity: cityData[value][0]
          });
        }
        return true;
      }
    });
    const { dispatch } = this.props;
    dispatch({
      type: "acquisition/setMetaId",
      payload: {
        data2: args
      }
    });
    this.setState({
      data2: args
    });
  }
  /*校验表名称*/
  SJCJisExistsListName = (rule, value, callback) => {
    const { dispatch } = this.props;
    const data1 = this.props.acquisition.selectRowLeft;
    const { setFields } = this.props.form;
    const formData = data1.map(row => ({
      metaNameEn: row.table,
      dsId: this.props.acquisition.infoDsId
    }));
    SJCJisExists(formData).then(({ data }) => {
      callback();
      if (Array.isArray(data.data)) {
        const fields = {};
        data.data.forEach(table => {
          const tableName = Object.keys(table)[0];
          if (table[tableName]) {
            fields[`tables.${tableName}`] = {
              value: tableName,
              errors: ["已存在该表名"]
            };
          }
          dispatch({
            type: "acquisition/setMetaId",
            payload: {
              options: table,
              optionsKey: tableName
            }
          });
        });
        setFields(fields);
      }
    });
  };

  /*检测数据库名称*/
  handleGetName = (rule, value, callback) => {
    const { info, dsType } = this.props.acquisition;
    if (value && value !== info.dbDatabasename) {
      if (Timer) {
        clearTimeout(Timer);
        Timer = null;
      }
      Timer = setTimeout(() => {
        let obj = {};
        obj.dbDatabasename = value;
        obj.dsType = 3;
        check_if_dsname_exists(obj).then(res => {
          const { data } = res.data;
          if (data === true) {
            callback(true);
          } else {
            callback();
          }
        });
      }, 300);
    } else {
      callback();
    }
  };
  next = e => {
    const { dispatch, form } = this.props;
    const acquisition = this.props.acquisition;

    form.validateFields({ force: false }, (err, values) => {
      this.setState({
        nextList: false
      });
      if (!err) {
        if (this.state.current === 0) {
          /*  values.password = strEnc(values.password,values.username,values.hostname,values.port);*/
          if (
            acquisition.pluginId === "" &&
            acquisition.pluginId &&
            undefined &&
            acquisition.selectedRowKeys.length === 0
          ) {
            message.error("请选择源数据库类型");
            return false;
          } else {
            let bussChecked = this.state.checked;
            let obj = {};
            obj.hostname = values.hostname;
            obj.port = values.port;
            obj.username = values.username;
            obj.password = values.password;
            obj.databaseName = values.databaseName;
            obj.pluginId =
              acquisition.pluginId === 14
                ? "DM7"
                : (() => {
                    let name = "";
                    for (let index of databaseType) {
                      if (index.value === acquisition.pluginId) {
                        name = index.name.toUpperCase();
                        return name;
                      }
                    }
                  })();
            obj.type = values.type === true ? 3 : 0;

            // 将obj保存，提供给检索schema使用
            this.setState({ tempObj: obj });
            
            GETSCHEMALIST(obj).then(res => {
              if (res.data.code === "200" || res.data.code === "705") {
                const current = this.state.current + 1;
                this.setState({ current });
                dispatch({
                  type: "acquisition/setMetaId",
                  payload: {
                    // selectedRowKeys:acquisition.selectedRowKeys,
                    // selectedRows:acquisition.selectedRows,
                    // hostname:obj.hostname,
                    // port:obj.port,
                    // username:obj.username,
                    // password:obj.password,
                    // databaseName:obj.databaseName,
                    // pluginId:obj.pluginId,
                    // /* key:key,*/
                    // dsTypes:acquisition.dsTypes,
                    // type:this.state.type,
                    // dsIdData:res.data.data,
                    // dsName:obj.schemaName,
                    schemaList: res.data.data
                  }
                });
              } else {
                const current = (this.state.current = 0);
                this.setState({
                  current,
                  nextList: true
                });
                dispatch({
                  type: "acquisition/setMetaId",
                  payload: {
                    selectedRowKeys: acquisition.selectedRowKeys,
                    selectedRows: acquisition.selectedRows,
                    hostname: obj.hostname,
                    port: obj.port,
                    username: obj.username,
                    password: obj.password,
                    databaseName: obj.databaseName,
                    pluginId: obj.pluginId,
                    /* key:key,*/
                    dsTypes: acquisition.dsTypes,
                    type: this.state.type,
                    dsIdData: res.data.data,
                    dsName: obj.dsName,
                    infoDsId: values.dsId
                  }
                });
              }
            });
          }
        } else if (this.state.current === 1) {
          const { dispatch, acquisition } = this.props;

          const {
            selectRowLeft,
            selectRowKeysLeft,
            acquList,
            data1
          } = this.props.acquisition;
          if (acquisition.selectRowLeft.length === 0 || 
            data1
              .filter((val,index)=>acquisition.selectRowKeysLeft.indexOf(index) !== -1)
              .some((val)=>typeof val.targetTableCn === "undefined" || val.targetTableCn === "")
            ) 
            {
            message.error("请选择表名并输入表格注释");
            this.setState({
              nextList: true
            });
            return false;
          } else {
            const current = this.state.current + 1;
            this.setState({ current });
          }
        }
      }else{
        this.setState({
          current:0,
          nextList: true
        });
      }
    });
  };
  prev() {
    const { dispatch } = this.props;
    const info = this.props.acquisition;

    const obj = {
      selectedRowKeys: info.selectedRowKeys,
      selectedRows: info.selectedRows,
      hostname: info.hostname,
      port: info.port,
      username: info.username,
      password: info.password,
      databaseName: info.databaseName,
      pluginId: info.pluginId,
      data1: info.data1,
      data2: info.data2,
      selectRowLeft: info.selectRowLeft,
      selectRowKeysRight: info.selectRowKeysRight,
      selectRowRight: info.selectRowRight,
      /* tableNames:info.optionsKey,*/
      tableNames: info.tableNames,
      tableNameX: info.data1,
      dsName: info.dsName
    };

    console.log(obj);
    dispatch({
      type: "acquisition/setMetaId",
      payload: obj});
    const current = this.state.current - 1;
    this.setState({ current, nextList: true });
  }

  /**
   * 获取表格修改内容
   * edited by steven leo on 2018/09/26
   */
  getTablePairs = ()=>{
    const {selectRowKeysLeft,expandedTables, data1} = this.props.acquisition;
    let tempArr = [];
    for(let i in data1){

      // 注意使用parseInt来检测是否存在选中的key，
      // 如果没有则，跳过
      if(selectRowKeysLeft.indexOf(parseInt(i)) !== -1) { 
        let tempObj = {
          sourceTableEn: data1[i].table,
          targetTableEn: data1[i].tables,
          targetTableCn: data1[i].targetTableCn,

           // 如果没有展开过table则表明没有修改过，此处可以做更深判断，比如用immmutable.is
           // 留作后续改善
           // edited by steven leo on 2018/09/26
          hasChangedProperty: expandedTables[data1[i].table]?true:false,
          metadataProperties: expandedTables[data1[i].table]
        }
        tempArr.push(tempObj);
      }
    } 

    return tempArr;
  }

  StartAcquisition() {
    const { id, renterId } = this.props.account;
    const { dispatch, acquisition, form } = this.props;
    console.log(acquisition, "参数");

    /**
     * directMetaTableInfo中还要传tablePairs字段,
     * 这部分交给tablePairs方法处理
     * edited by steven leo on 2018/09/26
     */
    form.validateFields((err, values) => {
      if(err){
        message.info("请检查填写是否完整");
      }else{

        // 从model数据中提取数据
        const tablePairs = this.getTablePairs();

        // 发送数据请求
        dispatch({type:"acquisition/start",payload:{
          databaseName : acquisition.databaseName,
          username : acquisition.username,
          password : acquisition.password,
          hostname : acquisition.hostname,
          port : acquisition.port,
          pluginId : acquisition.pluginId === 14
          ? "DM7"
          : (() => {
              let name = "";
              for (let index of databaseType) {
                if (index.value === acquisition.pluginId) {
                  name = index.name.toUpperCase();
                  return name;
                }
              }
            })(),
          schemaName: acquisition.schemaName, // model中记录的是dsName，为schemaName
          tableNames: acquisition.selectRowLeft.map(val=>val.table),
          type: 0, // 默认为0，根据后端开发人员表示，这个字段没起什么用
          directMetaTableInfo: {
            ...values,
            dept: values.dept,
            dsId: acquisition.dsId,
            dsType: acquisition.dsTypes,
            tablePairs: tablePairs
          }
        }});
      }
    });
  }
  onChangeRadio = e => {
    const { dsTypes } = this.props.acquisition;
    const { dispatch } = this.props;
    dispatch({
      type: "acquisition/setMetaId",
      payload: {
        dsTypes: e.target.value
      }
    });
    dispatch({
      type: "metadataCommon/getStoreDatabase",
      dstype: e.target.value
    });
  };
  onChangeGrade = e => {
    const { valueGrade } = this.props.acquisition;
    const { dispatch } = this.props;
    dispatch({
      type: "acquisition/setMetaId",
      payload: {
        valueGrade: e.target.value
      }
    });
  };
  formItemLayout1 = {
    labelCol: { span: 0 },
    wrapperCol: { span: 20 }
  };
  showModals() {
    this.setState({
      visible: true
    });
  }
  handleOk = e => {
    const { metaDataDefine, dispatch } = this.props;
    dispatch({ type: "metaDataDefine/hideAllViewAcquisition" });
  };
  handleCancelAlert = e => {
    const { dispatch } = this.props;
    const current = (this.state.current = 0);
    this.setState({ current });
    this.setState({
      visible: false
    });
    dispatch({ type: "acquisition/closeModel" });
  };
  onChangeChecked = e => {
    /*文件表类型或者文件目录类型*/
    this.setState({
      value: e.target.value
    });
  };
  onChangeOptions = e => {
    /*是否需要元数据表名称*/
    const { dispatch } = this.props;
    dispatch({
      type: "acquisition/setMetaId",
      payload: {
        type: e.target.checked
      }
    });
    this.setState({
      type: e.target.checked
    });
  };
  handleChange = (value, dsName) => {
    /*判断数据库表中文名名称*/
    const account = this.props.account;
    const { dispatch, acquisition } = this.props;
    const {form } = this.props;
    let obj = {};
    obj.sourceId = "1";
    obj.renterId = account.renterId;
    obj.dsName = value;
    obj.userId = account.id;

    getStoreDatabase(obj).then(res => {
      const { code, total } = res.data;
      if (code === "200") {
        let add = [];
        for (let index of res.data.data.rows) {
          /* add.push(index);*/

          if (value === index.dsName) {

            dispatch({
              type: "acquisition/setMetaId",
              payload: {
                /*hostname:index.dbHostname,*/
                port: index.frontEndServer.dbPort,
                username: index.dbUsername,
                password: index.dbPassword,
                databaseName: index.dbDatabasename,
                pluginId: index.dsType,
                hostname: index.frontEndServer.serverIp,
                manager: index.frontEndServer.manager,
                dsId: index.dsId,
                dsName: value
              }
            });

            console.log(form);
            form.setFieldsValue({
              port: index.frontEndServer.dbPort,
              username: index.dbUsername,
              password: index.dbPassword,
              databaseName: index.dbDatabasename,
              hostname: index.frontEndServer.serverIp
            });

            return;
          }
        }
        dispatch({
          type: "acquisition/setMetaId",
          payload: {
            caijiList: res.data.data.rows
          }
        });
      }
    });

    this.setState({
      selectedDb: value
    });
  };

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: "metadataCommon/getSourceTable" });
    dispatch({ type: "metadataCommon/getUsers" });
    dispatch({ type: "metadataCommon/getDepartments" });
    dispatch({ type: "metadataCommon/getStoreDatabase" });
    dispatch({ type: "metadataCommon/getUserByRenterId" });
    dispatch({ type: "metadataCommon/getAllResource" });
    dispatch({ type: "metadataCommon/getHdfsTree" });
    dispatch({ type: "acquisition/closeModel" });
    this.handleChange();
  }

  history() {
    const { metaDataDefine, dispatch } = this.props;
    dispatch({ type: "metaDataDefine/hideAllViewAcquisition" });
  }

  /**
    value {string} 选择的表schema值
   */
  selectSchema = value => {
    const { tempObj } = this.state;
    const { dispatch, acquisition } = this.props;
    const obj = tempObj;

    // 获取数据表信息
    SJCJgetTableInfo(__.assign(tempObj, { schemaName: value })).then(res => {
      console.log("trigger");
      this.setState({
        nextList: true
      });
      let args = [];
      let i = 0;

      console.log(args);
      if (res.data.code === "200") {
        let data1 = res.data.data;
        for (let index of data1) {
          args.push({
            key: i++,
            table: index.targetTableEn,
            tables: index.sourceTableEn,
            targetTableCn: index.targetTableCn
          });
        }
        dispatch({
          type: "acquisition/setMetaId",
          payload: {
            data1: args,
            selectedRowKeys: acquisition.selectedRowKeys,
            selectedRows: acquisition.selectedRows,
            hostname: obj.hostname,
            port: obj.port,
            username: obj.username,
            password: obj.password,
            databaseName: obj.databaseName,
            pluginId: acquisition.pluginId,
            dsType: acquisition.dsTypes,
            schemaName: value
          }
        });
        this.setState({
          data1: args
        });
      }
    });
  };

  render() {
    const { metaDataDefine, metadataCommon, account } = this.props;
    const {
      sourceTableOptions,
      departmentsOptions,
      departmentsTree,
      storeAcquisitionDatabaseOptions,
      storeDatabaseOptions,
      usersOptions,
      industryOptions,
      themeOptions,
      tagsOptions
    } = this.props.metadataCommon;
    const { visible, confirmLoading, current, info, type } = this.state;
    const {
      expandedTables,
      caijiList,
      pluginId,
      dsTypes,
      valueGrade,
      data1,
      data2,
      data,
      indexSelect,
      selectedRows,
      selectedRowKeys,
      selectRowKeysRight,
      selectRowLeft,
      selectRowRight,
      selectRowKeysLeft
    } = this.props.acquisition;
    const { getFieldDecorator } = this.props.form;
    const infos = this.props.acquisition;
    let selectCaiJiList = caijiList ? caijiList : [];

    console.log("新的采集选中：",infos);
    const options = [
      { label: "xls，xlst", value: "1" },
      { label: "txt", value: "2" },
      { label: "doc，docx", value: "3" },
      { label: "pdf", value: "4" },
      { label: "XML", value: "5" },
      { label: "JSON", value: "6" },
      { label: "压缩包（rar，zip，7z...）", value: "7" },
      { label: "图片（jpg，png，tiff，gif，bmp，jpeg，raw...）", value: "8" }
    ];

    const rowSelection1 = {
      selectedRowKeys: selectRowKeysLeft,
      onChange: (selectRowKeysLeft, selectRowLeft) => {
        const { dispatch } = this.props;
        var tableNames = [];
        console.log(selectRowKeysLeft, "选择右边");
        for (var index of selectRowLeft) {
          tableNames.push(index.table);
        }
        console.log(tableNames, "更新tableNames");
        dispatch({
          type: "acquisition/setMetaId",
          payload: {
            selectRowLeft: selectRowLeft,
            tableNames,
            selectRowKeysLeft: selectRowKeysLeft
          }
        });
        if (selectRowLeft.length > 0) {
          this.setState({
            nextList: true
          });
        }
      }
    };
    const rowSelection2 = {
      selectedRowKeys: selectRowKeysRight,
      onChange: (selectRowKeysRight, selectRowRight) => {
        const { dispatch } = this.props;
        const { keyList } = this.props.acquisition;
        let tableLi = this.props.acquisition.tableNames;
        let as = this.props.acquisition.keyList;
        let nameList = [];
        let shallList = [];
        for (var index in selectRowRight) {
          nameList.push(selectRowRight[index]);
          shallList.push({
            colName: nameList[index].name,
            frontDataType: nameList[index].type,
            IsPk: nameList[index].isPrimaryKey,
            IsNull: nameList[index].nullable,
            length: nameList[index].length,
            dataType: nameList[index].metaType
          });
        }
        let obj = {};
        obj[tableLi] = shallList;
        let tablekey = { [tableLi]: shallList };
        let keyFile = { ...tablekey };
        let FileName = [];
        this.props.acquisition.dataNameBest = selectRowRight;
        dispatch({
          type: "acquisition/setMetaId",
          payload: {
            selectRowKeysRight: selectRowKeysRight,
            selectRowRight: selectRowRight,
            keyList: {
              ...keyList,
              ...obj
            }
          }
        });
      }
    };

    let dept = Array.isArray(infos.dept)
      ? infos.dept
      : safeJsonParse(infos.dept) ||
        (this.props.account.deptId && [String(this.props.account.deptId)]);
    dept = Array.isArray(dept) ? dept : dept && [String(dept)];
    if (!departmentsTree) dept = null;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 10 }
    };
    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 8 }
    };
    const formItemLayout2 = {
      labelCol: { span: 8 },
      wrapperCol: { span: 14 }
    };
    const formItemLayout3 = {
      labelCol: { span: 8 },
      wrapperCol: { span: 15 }
    };
    const formItemLayout4 = {
      labelCol: { span: 9 },
      wrapperCol: { span: 15 }
    };
    const formItemLayout5 = {
      labelCol: { span: 3 },
      wrapperCol: { span: 15 }
    };
    const formItemLayoutS = {
      labelCol: { span: 3 },
      wrapperCol: { span: 10 }
    };
    let renderShow;
    switch (this.state.current) {
      case 0:
        renderShow = (
          <div className={Style.center}>
            <div className={Style.fromList}>
              <FormItem label="数据库中文名称: " {...formItemLayout}>
                {getFieldDecorator("dsName", {
                  initialValue: infos.dsName,
                  validateTrigger: "onBlur",
                  rules: [
                    { required: true, message: "请输入数据库中文名称" },
                    {
                      validator: this.handleGetNameScarch,
                      message: "数据库中文名称已存在"
                    }
                  ]
                })(
                  <Select
                    mode="combobox"
                    {...formItemLayout}
                    onChange={this.handleChange}
                    filterOption={false}
                    placeholder="请输入自定义数据库中文名称"
                  >
                    {selectCaiJiList.map(item => {
                      return (
                        <Option key={item.dsName} value={item.dsName}>
                          {item.dsName}
                        </Option>
                      );
                    })}
                  </Select>
                )}
              </FormItem>
              <FormItem label="IP地址" {...formItemLayout}>
                {getFieldDecorator("hostname", {
                  initialValue: infos.hostname,
                  rules: [{ required: true, message: "请输入数据库所在IP地址" }]
                })(<Input disabled placeholder="请输入数据库所在IP地址" />)}
              </FormItem>

              {pluginId === 3 ? (
                <FormItem label="数据库名称" {...formItemLayout}>
                  {getFieldDecorator("databaseName", {
                    initialValue: infos.databaseName,
                    rules: [{ required: true, message: "请输入数据库名称" }]
                  })(<Input disabled placeholder="请输入数据库名称" />)}
                </FormItem>
              ) : (
                <FormItem label="实例名称" {...formItemLayout}>
                  {getFieldDecorator("databaseName", {
                    initialValue: infos.databaseName,
                    rules: [{ required: true, message: "请输入实例名称" }]
                  })(<Input disabled placeholder="请输入实例名称" />)}
                </FormItem>
              )}
              {pluginId === 3 ? (
                <FormItem label="端口号" {...formItemLayout}>
                  {getFieldDecorator("port", {
                    initialValue: infos.port || "3306",
                    rules: [{ required: true, message: "请输入端口号" }]
                  })(<Input disabled placeholder="请输入端口号" />)}
                </FormItem>
              ) : (
                <FormItem label="端口号" {...formItemLayout}>
                  {getFieldDecorator("port", {
                    initialValue: infos.port || "1521",
                    rules: [{ required: true, message: "请输入端口号" }]
                  })(<Input disabled placeholder="请输入端口号" />)}
                </FormItem>
              )}

              <FormItem label="用户名" {...formItemLayout}>
                {getFieldDecorator("username", {
                  initialValue: infos.username,
                  rules: [{ required: true, message: "请输入用户名" }]
                })(<Input disabled placeholder="请输入用户名" />)}
              </FormItem>
              <FormItem label="密码" {...formItemLayout}>
                {getFieldDecorator("password", {
                  initialValue: infos.password,
                  rules: [{ required: true, message: "请输入密码" }]
                })(<Input disabled type="password" placeholder="请输入密码" />)}
              </FormItem>
              {/* <FormItem {...formItemLayout2} style={{ marginLeft:'18%'}}>
                      {getFieldDecorator('type', {
                        initialValue: infos.type || true,
                      })(
                      <Checkbox checked={type} onChange={this.onChangeOptions}>采集外部数据源表的同时，批量新建元数据表</Checkbox>
                      )}
                    </FormItem> */}
            </div>
          </div>
        );
        break;
      case 1:
        // onRowClick={this.headonRowClick.bind(this)}
        renderShow = (
          <div className={Style.center1}>
            {/* 添加schema下拉选项 */}
            {infos.schemaList && (
              <div>
                <span>模式名称：</span>

                {/* 默认是选中第一个选项 */}
                <Select
                  defaultValue={"请选择"}
                  style={{ width: 300, margin: 15 }}
                  onSelect={this.selectSchema}
                >
                  {infos.schemaList.map((val, i) => (
                    <Option value={val} key="schemaList{i}">
                      {val}
                    </Option>
                  ))}
                </Select>
              </div>
            )}

            {this.state.type === true ? (
              <TableList
                scroll={{ y: 450 }}
                pagination={false}
                rowClassName={(record, index) =>
                  index === indexSelect ? "rowColor" : ""
                }
                rowSelection={rowSelection1}
                style={{ color: "#fff" }}
                className={Style.TabelList}
                dataSource={data1}
                columns={this.columns1}
                expandedRowRender={this.tableDetails(expandedTables)}
                onExpand={this.tableExpandRequest}
                expandedRowKeys={this.state.expandedRowKeys}
                expandRowByClick={false}
              />
            ) : null}
            {/* {this.state.type === false ? (
              <Table scroll={{ y: 450 }}
                pagination={false}
                rowClassName={(record, index) => index === indexSelect ? "rowColor" : ''}
                rowSelection={rowSelection1}
                style={{ color: '#fff' }}
                className={Style.TabelList} dataSource={data1} columns={this.columnsOne} />
            ) : null} */}
            {this.state.type === false ? (
              <TableList
                scroll={{ y: 450 }}
                pagination={false}
                rowSelection={rowSelection2}
                onRowClick={record => {
                  console.log(record);
                }}
                className={Style.TabelList1}
                dataSource={data2}
                columns={this.columnsTwo()}
                expandedRowRender={this.tableDetails}
                onExpand={this.tableExpandRequest}
                expandRowByClick={false}
              />
            ) : null}
          </div>
        );
        break;
      case 2:
        renderShow = (
          <div className={Style.center2}>
            <Row>
              <Col span={12}>
                <FormItem label="所属组织: " {...formItemLayout3}>
                  {getFieldDecorator("dept", {
                    initialValue: dept
                  })(
                    <TreeSelect
                      disabled
                      placeholder="选择组织"
                      treeData={departmentsTree}
                      treeDefaultExpandAll
                      dropdownStyle={{ height: 300 }}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={12}>
                <FormItem label="组织外公开等级" {...formItemLayout4}>
                  {getFieldDecorator("publicStats", {
                    initialValue: infos.publicStats || "1"
                  })(
                    <RadioGroup
                      onChange={this.onChangeGrade}
                      value={valueGrade}
                    >
                      <Radio value="1">授权公开</Radio>
                      <Radio value="2">不公开</Radio>
                    </RadioGroup>
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem label="表拥有者： " {...formItemLayout3}>
                  {getFieldDecorator("owner", {
                    initialValue: infos.owner,
                    rules: [{ required: true, message: "请选择表拥有者" }]
                  })(
                    <Select placeholder="选择表拥有者" >
                      {usersOptions.map(item => {
                        return (
                          <Option key={item.value} value={item.label}>
                            {item.label}
                          </Option>
                        );
                      })}
                    </Select>
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={12}>
                <FormItem
                  style={{ marginBottom: "10px" }}
                  label="行业： "
                  {...formItemLayout3}
                >
                  {getFieldDecorator("industry", {
                    initialValue: infos.industry,
                    rules: [{ required: true, message: "请选择行业" }]
                  })(
                    <Select placeholder="选择行业">
                      {industryOptions.map(item => {
                        return (
                          <Option key={item.value} value={item.value}>
                            {item.label}
                          </Option>
                        );
                      })}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem label="标签：" {...formItemLayout3}>
                  {getFieldDecorator("tag", {
                    initialValue: infos.tag,
                    rules: [{ required: true, message: "请选择标签" }]
                  })(
                    <Select placeholder="选择标签">
                      {tagsOptions.map(item => {
                        return (
                          <Option key={item.value} value={item.value}>
                            {item.label}
                          </Option>
                        );
                      })}
                    </Select>
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <FormItem label="备注：" {...formItemLayout5}>
                {getFieldDecorator("remark", {
                  initialValue: infos.remark
                })(
                  <TextArea
                    maxLength="200"
                    autosize={{ minRows: 3, maxRows: 5 }}
                  />
                )}
              </FormItem>
            </Row>
          </div>
        );
        break;
      default:
        break;
    }
    return (
      <Modal
        title="新建采集字段"
        visible={metaDataDefine.viewAcquisitionVisible}
        maskClosable={false}
        closable={false}
        footer={null}
        width={800}
      >
        <div>
          <div style={{ width: "86%", marginLeft: "8%" }}>
            {this.state.value === 1 ? (
              <Steps current={current}>
                {steps.map(item => (
                  <Step
                    key={item.title}
                    title={item.title}
                    style={{ marginTop: "20px" }}
                  />
                ))}
              </Steps>
            ) : null}
          </div>
          {/*  <RadioGroup onChange={this.onChangeChecked} value={this.state.value} style={{marginLeft:'7%'}}>
                <Radio className={Style.radioStyle} value={1}>数据表类型</Radio>
                <Radio className={Style.radioStyle} value={2}>文件目录类型</Radio>
              </RadioGroup>*/}
          <div className="stepsContent">
            <Form>
              {this.state.value === 1 ? renderShow : null}
              {this.state.value === 2 ? (
                <div>
                  <FormItem label="目录路径" {...formItemLayout1}>
                    {getFieldDecorator("hostFile", {
                      initialValue: infos.hostFile,
                      rules: [{ required: true, message: "请输入目录路径" }]
                    })(<Input placeholder="请输入主机名称" />)}
                  </FormItem>
                  <FormItem label="目录路径" {...formItemLayout1}>
                    {getFieldDecorator("hostnameUrl", {
                      initialValue: infos.hostnameUrl,
                      rules: [{ required: true, message: "请输入目录路径" }]
                    })(<Input placeholder="请输入主机名称" />)}
                  </FormItem>
                  <FormItem label="目录中的文件类型" {...formItemLayout1}>
                    {getFieldDecorator("hostnametype", {
                      initialValue: infos.hostnametype
                    })(
                      <CheckboxGroup
                        options={options}
                        onChange={this.onChangeGroup.bind(this)}
                      />
                    )}
                  </FormItem>
                  <FormItem label="备注：" {...formItemLayout1}>
                    {getFieldDecorator("remark", {
                      initialValue: infos.remark
                    })(
                      <TextArea
                        maxLength="200"
                        autosize={{ minRows: 3, maxRows: 5 }}
                      />
                    )}
                  </FormItem>
                </div>
              ) : null}
            </Form>
          </div>
          <div className={Style.stepsAction}>
            {this.state.current < steps.length - 1 && (
              <div>
                {this.state.current === 1 &&
                this.props.acquisition.type === false ? (
                  <Button
                    type="primary"
                    style={{ marginLeft: 8 }}
                    onClick={() => this.StartAcquisition()}
                  >
                    保存
                  </Button>
                ) : (
                  <Button
                    type="primary"
                    style={{ marginLeft: 8 }}
                    onClick={() => this.next()}
                    disabled={this.state.nextList === false}
                  >
                    下一步
                  </Button>
                )}
                <Button
                  type="primary"
                  style={{ marginLeft: 20 }}
                  onClick={() => this.history()}
                >
                  取消
                </Button>
              </div>
            )}
            {this.state.current === steps.length - 1 && (
              <div>
                {this.state.value === 1 ? (
                  <Button
                    type="primary"
                    style={{ marginRight: "-1%" }}
                    onClick={() => this.StartAcquisition()}
                  >
                    开始采集
                  </Button>
                ) : null}
                {this.state.value === 2 ? (
                  <Button
                    type="primary"
                    style={{ marginRight: "-1%" }}
                    onClick={() => this.StartAcquisition()}
                  >
                    开始采集
                  </Button>
                ) : null}
                <Button
                  type="primary"
                  style={{ marginLeft: 20 }}
                  onClick={() => this.history()}
                >
                  取消
                </Button>
              </div>
            )}
            {this.state.current > 0 && (
              <div style={{ margin: "-4% 23% 0% 0%" }}>
                {this.state.value === 1 ? (
                  <Button onClick={() => this.prev()}>上一步</Button>
                ) : null}
              </div>
            )}
          </div>
          <Modal
            title="采集结果"
            visible={visible}
            confirmLoading={confirmLoading}
            closable={true}
            onCancel={this.handleCancelAlert}
            zIndex={1030}
            footer={null}
          >
            <Row>
              <Col span={8}>
                <FormItem label="总表数">
                  {getFieldDecorator("total", {
                    initialValue: info.total
                  })(
                    <label className="ant-form-text">{this.state.total}</label>
                  )}
                </FormItem>
              </Col>
              <Col span={8}>
                <FormItem label="采集成功表数">
                  {getFieldDecorator("success", {
                    initialValue: info.success
                  })(
                    <label className="ant-form-text">
                      {this.state.success}
                    </label>
                  )}
                </FormItem>
              </Col>
              <Col span={8}>
                <FormItem label="采集失败表数">
                  {getFieldDecorator("over", {
                    initialValue: info.over
                  })(
                    <label className="ant-form-text">{this.state.over}</label>
                  )}
                </FormItem>
              </Col>
            </Row>
            {this.state.type === true ? (
              <div>
                <div
                  style={{ height: 30, textAlign: "center", marginTop: "5%" }}
                >
                  <Button type="primary" onClick={() => this.handleOk()}>
                    <Link to="/MetadataDefine">去查看</Link>
                  </Button>
                </div>
              </div>
            ) : null}

            {this.state.type === false ? (
              <div>
                <div
                  style={{ height: 30, textAlign: "center", marginTop: "5%" }}
                >
                  <Button type="primary" onClick={() => this.handleOk()}>
                    <Link to="/MetadataDefine">去查看</Link>
                  </Button>
                </div>
              </div>
            ) : null}
          </Modal>
        </div>
      </Modal>
    );
  }
}
const AcquisitionModelForm = Form.create()(Acquisition);
export default withRouter(
  connect(
    ({ acquisition, metadataCommon, system, account, metaDataDefine }) => ({
      acquisition:acquisition.toJS(),
      metadataCommon,
      system,
      account,
      metaDataDefine
    })
  )(AcquisitionModelForm)
);
