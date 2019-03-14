/**
 * Created by Administrator on 2017/8/22.
 */
import React from "react";
import { connect } from "dva";
import { Link, withRouter } from "react-router";
import {
  Input,
  Button,
  Tabs,
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
import Style from "../Acquisition.css";
import { check_if_dsname_exists } from "../../../../services/metadata";
import TableList from "../../../../components/TableList";
import {
  getStoreDatabaseAcquition,
  CJLBlist
} from "../../../../services/metadataCommon";
import {
  SJCJgetdbinfo,
  SJCJgetTableInfo,
  SJCJgetDbFieldInfo,
  SJCJinsertTableFields,
  SJCJisExists
} from "../../../../services/AcquisitionCommon";
import {safeJsonParse } from "utils/utils";
import { strEnc, strDec } from "utils/EncryptUtil";
import dbDataType from "../../../../config/dbDataType.config";

const FormItem = Form.Item;
const Step = Steps.Step;
const RadioGroup = Radio.Group;
const Option = Select.Option;
const { TextArea } = Input;
const CheckboxGroup = Checkbox.Group;

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
class AcquisitionModel extends React.Component {
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
    pluginId: "MySQL",
    dataSource: [
      {
        pluginId: "Oracle",
        key: 1
      },
      {
        pluginId: "MySQL",
        key: 2
      } /*,{
          pluginId: 'Sybase',
          key:3,
        },{
          pluginId: 'SQL Server',
          key:4,
        },{
          pluginId: 'DB2',
          key:5,
        },{
          pluginId: 'PostgreSQL',
          key:6,
        }*/
    ]
  };
  columns = [
    {
      title: "数据源数据库类型",
      dataIndex: "pluginId",
      key: "pluginId"
    }
  ];
  // 修改字段
  ValuesClick(keyOfCol, e, record) {
    const { dispatch } = this.props;
    const args = this.props.acquisition;
    for (let index of args.selectRowLeft) {
      if (index.key == record.key) {
        index[keyOfCol] = e.target.value;
      }
    }
    dispatch({
      type: "acquisition/setMetaId",
      payload: { selectedRowKeys: args.selectRowLeft }
    });
  }

  columns2 = () => {
    const acquisition = this.props.acquisition.lengthss;
    const str = JSON.stringify(acquisition);
    const { getFieldDecorator } = this.props.form;
    return [
      {
        title: "需引入元数据表的字段名",
        dataIndex: "name",
        key: "name",
        width: "23%"
      },
      {
        title: "元数据字段类型",
        dataIndex: "metaType",
        key: "metaType",
        width: "18%",
        render: (text, record, index) => {
          return (
            <FormItem labelCol={{ span: 0 }}>
              {getFieldDecorator(`rows[${index}].metaType`, {
                initialValue: text
              })(
                <Select
                  value={text}
                  style={{ width: "130px" }}
                  onChange={value => {
                    this.Import("metaType", value, record);
                  }}
                >
                  {this.state.dbDataTypeList.map(dbtype => (
                    <Option key={dbtype} value={dbtype}>
                      {dbtype}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
          );
        }
      },
      {
        title: "数据源表字段类型",
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
      key: "tables"
    },
    {
      title: "新表名",
      dataIndex: "table",
      key: "table",
      render: (text, record, index) => {
        /*{validator:this.SJCJisExistsListName}
         validateTrigger: 'onBlur',*/
        const { getFieldDecorator, getFieldValue } = this.props.form;
        return (
          <FormItem>
            {getFieldDecorator(`tables.${text}`, {
              initialValue: text,
              rules: [
                { required: true, message: "字段名称不能为空" },
                { validator: this.SJCJisExistsListName }
              ]
            })(
              <Input
                style={{
                  position: "relative",
                  top: "10px",
                  marginBottom: "11px"
                }}
                value={text}
                onChange={value => {
                  this.ValuesClick("table", value, record);
                }}
              />
            )}
          </FormItem>
        );
      }
    }
  ];

  columnsOne = [
    {
      title: "数据源表名",
      dataIndex: "table",
      key: "table"
    }
  ];

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
    const { dispatch } = this.props;
    const acquisition = this.props.acquisition;
    this.props.form.validateFields({ force: false }, (err, values) => {
      if (!err) {
        if (this.state.current === 0) {
          values.password = strEnc(
            values.password,
            values.username,
            values.hostname,
            values.port
          );
          if (
            acquisition.pluginId === "" &&
            acquisition.pluginId &&
            undefined &&
            acquisition.selectedRowKeys.length === 0
          ) {
            message.error("请选择源数据库类型");
            return false;
          } else {
            let pId =
              this.state.pluginId ||
              acquisition.pluginId ||
              acquisition.selectedRows[0].pluginId;
            let bussChecked = this.state.checked;
            let obj = {};
            obj.hostname = values.hostname;
            obj.port = values.port;
            obj.username = values.username;
            obj.password = values.password;
            obj.databaseName = values.databaseName;
            obj.pluginId = this.state.pluginId;
            obj.schemaName = values.dsName;
            /* obj.dsType=acquisition.dsTypes;*/
            obj.type = values.type === true ? 3 : 0;
            /* let key = acquisition.selectedRows[0].key;*/
            SJCJgetdbinfo(obj).then(res => {
              if (res.data.code === "200" || res.data.code === "705") {
                const current = this.state.current + 1;
                this.setState({ current });
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
                    dsName: obj.schemaName,
                    infoDsId: values.dsId
                  }
                });
                SJCJgetTableInfo(obj).then(res => {
                  let args = [];
                  let i = 0;
                  let data1 = res.data.data.tables;
                  if (res.data.code === "200") {
                    for (let index of data1) {
                      args.push({
                        key: i++,
                        table: index.table,
                        tables: index.table
                      });
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
                          pluginId: obj.pluginId,
                          dsType: acquisition.dsTypes,
                          dsName: obj.schemaName
                        }
                      });
                      this.setState({
                        data1: args
                      });
                    }
                  }
                });
              } else {
                const current = (this.state.current = 0);
                this.setState({ current });
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
          const acquList = this.props.acquisition.options;
          const optionsKey = this.props.acquisition.optionsKey;
          const dataKey = this.props.acquisition.dataKey;
          dispatch({
            type: "acquisition/setMetaId",
            payload: {
              options: acquList,
              optionsKey: optionsKey,
              selectRowLeft: acquisition.selectRowLeft,
              selectRowKeysLeft: acquisition.selectRowKeysLeft,
              selectRowKeysRight: acquisition.selectRowKeysRight,
              selectRowRight: acquisition.selectRowRight,
              tableNames: acquisition.tablesNamelist,
              tableNameX: acquisition.data1,
              dataKey: dataKey
            }
          });
          if (acquisition.selectRowLeft.length === 0) {
            message.error("请选择表名");
            return false;
          } else if (
            acquisition.selectRowRight.length === 0 &&
            acquisition.data2.length === 0
          ) {
            message.error("请选择列名");
            return false;
          } else if (acquList[optionsKey]) {
            return false;
          } else {
            const current = this.state.current + 1;
            this.setState({ current });
          }
        }
      }
    });
  };

  prev() {
    const { dispatch } = this.props;
    const info = this.props.acquisition;
    dispatch({
      type: "acquisition/setMetaId",
      payload: {
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
        tableNames: info.optionsKey,
        tableNameX: info.data1,
        dsName: info.dsName
      }
    });
    const current = this.state.current - 1;
    this.setState({ current });
  }

  StartAcquisition() {
    const { id, renterId } = this.props.account;
    const { dsTypes, valueGrade } = this.props.acquisition;
    const { dispatch, acquisition } = this.props;
    console.log(acquisition.tablesNamelist, "acquisition...");
    this.props.form.validateFields((err, values) => {
      if (!err) {
        if (dsTypes.length === 0) {
          message.error("请选择存储的数据库类型");
          return false;
        } else if (valueGrade.length === 0) {
          message.error("请输入组织外公开等级");
          return false;
        } else {
          dispatch({
            type: "acquisition/setMetaId",
            payload: {
              data1: acquisition.data1,
              data2: acquisition.data2,
              selectedRowKeys: acquisition.selectedRowKeys,
              selectedRows: acquisition.selectedRows,
              selectRowLeft: acquisition.selectRowLeft,
              selectRowKeysRight: acquisition.selectRowKeysRight,
              selectRowRight: acquisition.selectRowRight,
              tablesNamelist: acquisition.tablesNamelist,
              tableNameX: acquisition.data1,
              optionsKey: acquisition.optionsKey
            }
          });
          if (this.state.type === false) {
            let metaNameEnss = [];
            for (var key in acquisition.selectRowLeft) {
              metaNameEnss.push({
                newTable: acquisition.selectRowLeft[key].table,
                oldTable: acquisition.selectRowLeft[key].tables
              });
            }

            let infoDsId = acquisition.infoDsId;
            let tableNames = acquisition.tablesNamelist;
            let metadataPropertyList = acquisition.keyList;
            let metaDataList = values;
            let optionsKey = acquisition.tableNames;
            values.metaNameEn = metaNameEnss;
            values.dsType = acquisition.dsTypes;
            values.dsId = acquisition.dsIdData;
            values.infoDsId = acquisition.infoDsId;
            let metadataValues =
              acquisition.selectRowRight.length === 0
                ? acquisition.data2
                : acquisition.selectRowRight;
            let data2List = acquisition.data2;
            let type = acquisition.type === true ? 3 : 0;
            let nameList = [];
            let shallList = [];
            for (var index in metadataValues) {
              nameList.push(metadataValues[index]);
              shallList.push({
                colName: nameList[index].name,
                frontDataType: nameList[index].type,
                IsPk: nameList[index].isPrimaryKey,
                IsNull: nameList[index].nullable,
                length: nameList[index].length,
                dataType: nameList[index].metaType
              });
            }
            let tablekey = { metadataPropertyList };
            let arr = { type, metaDataList, ...tablekey };
            SJCJinsertTableFields(arr).then(res => {
              if (res.data.code === "200") {
                message.success("成功");
                dispatch({
                  type: "acquisition/setMetaId",
                  payload: {
                    total: res.data.data.total,
                    success: res.data.data.success
                  }
                });
                let over = res.data.data.total - res.data.data.success;
                this.setState({
                  total: res.data.data.total,
                  success: res.data.data.success,
                  over: over
                });
                this.showModals();
              }
            });
          } else {
            let metaNameEnss = [];
            for (var key in acquisition.selectRowLeft) {
              metaNameEnss.push({
                newTable: acquisition.selectRowLeft[key].table,
                oldTable: acquisition.selectRowLeft[key].tables
              });
            }
            let infoDsId = acquisition.infoDsId;
            let dsId = acquisition.dsIdData;
            let tableNames = acquisition.tablesNamelist;
            let metaDataList = values;
            let optionsKey = acquisition.tableNames;
            values.metaNameEn = metaNameEnss;
            values.dsType = acquisition.dsTypes;
            values.dsId = acquisition.infoDsId;
            let metadataValues =
              acquisition.selectRowRight.length === 0
                ? acquisition.data2
                : acquisition.selectRowRight;
            let data2List = acquisition.data2;
            let type = acquisition.type === true ? 3 : 0;
            let nameList = [];
            let shallList = [];
            for (var index in metadataValues) {
              nameList.push(metadataValues[index]);
              shallList.push({
                colName: nameList[index].name,
                frontDataType: nameList[index].type,
                IsPk: nameList[index].isPrimaryKey,
                IsNull: nameList[index].nullable,
                length: nameList[index].length,
                dataType: nameList[index].metaType
              });
            }
            let metadataPropertyList = acquisition.keyList;
            let tablekey = { metadataPropertyList };
            let arr = { dsId, type, metaDataList, ...tablekey };
            SJCJinsertTableFields(arr).then(res => {
              if (res.data.code === "200") {
                message.success("成功");
                dispatch({
                  type: "acquisition/setMetaId",
                  payload: {
                    total: res.data.data.total,
                    success: res.data.data.success
                  }
                });
                let over = res.data.data.total - res.data.data.success;
                this.setState({
                  total: res.data.data.total,
                  success: res.data.data.success,
                  over: over
                });
                this.showModals();
              }
            });
          }
        }
      }
    });
  }

  headonRowClick = (record, index, event) => {
    const { dispatch, acquisition } = this.props;
    const state = this.state;
    var List = [];
    var ListsName = [];
    for (var key in record.tables) {
      //第一层循环取到各个list
      List = record.table;
      ListsName = record.tables;
    }

    dispatch({
      type: "acquisition/setMetaId",
      payload: {
        data1: acquisition.data1,
        hostname: acquisition.hostname,
        port: acquisition.port,
        username: acquisition.username,
        password: acquisition.password,
        databaseName: acquisition.databaseName,
        pluginId: acquisition.pluginId,
        selectRowLeft: acquisition.selectRowLeft,
        tableNameX: acquisition.data1,
        indexSelect: index
      }
    });

    let obj = {};
    obj.hostname = acquisition.hostname;
    obj.port = acquisition.port;
    obj.username = acquisition.username;
    obj.password = acquisition.password;
    obj.databaseName = acquisition.databaseName;
    obj.pluginId = acquisition.pluginId;
    obj.tableNames = [ListsName];
    obj.dsType = acquisition.dsTypes;
    let zoo = this.props.acquisition === 0;
    SJCJgetDbFieldInfo(obj).then(res => {
      let adlist = [];
      for (let index of res.data.data) {
        adlist.push(index);
        if (res.data.code === "200") {
          message.success("成功");
          let data2 = res.data.data[0].fields;
          let dataNameBest = res.data.data[0].table;
          let args = [];
          for (let index in data2) {
            if (data2[index].length <= 0) {
              data2[index].prontioan = data2[index].length;
            } else if (data2[index].precision <= 0) {
              data2[index].prontioan = data2[index].length;
            } else {
              data2[index].prontioan =
                data2[index].length + "," + data2[index].precision;
            }
            dispatch({
              type: "acquisition/setMetaId",
              payload: {
                tablesNamelist: List,
                tableNames: ListsName,
                data2: data2,
                lengthss: data2[index].prontioan,
                dataNameBest: dataNameBest,
                selectRowKeysRight: index.fields
              }
            });
            this.setState({
              data2: data2
            });
          }
        }
      }
    });
  };
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
    this.setState({
      ModalText: "The modal will be closed after two seconds",
      confirmLoading: true
    });
    setTimeout(() => {
      this.setState({
        visible: false,
        confirmLoading: false
      });
    }, 2000);
    this.handleSecher();
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
    console.log(value, dsName, acquisition.caijiList, "value,dsName");

    if (value) {
      dispatch({
        type: "acquisition/setMetaId",
        payload: {
          hostname: acquisition.caijiList.dbHostname,
          port: acquisition.caijiList.dbPort,
          username: acquisition.caijiList.dbUsername,
          password: acquisition.caijiList.dbPassword,
          databaseName: acquisition.caijiList.dbDatabasename,
          pluginId: acquisition.caijiList.dsType
        }
      });
    }

    let obj = {};
    obj.sourceId = "3";
    obj.renterId = account.renterId;
    /* obj.creator = account.username;*/
    obj.dsType = this.state.pluginId === "MySQL" ? "3" : "2";
    obj.dsName = value;

    getStoreDatabaseAcquition(obj).then(res => {
      const { code, total } = res.data;
      if (code === "200") {
        let add = [];
        for (let index of res.data.data.rows) {
          add.push(index);

          if (value === index.dsName) {
            dispatch({
              type: "acquisition/setMetaId",
              payload: {
                hostname: index.dbHostname,
                port: index.dbPort,
                username: index.dbUsername,
                password: index.dbPassword,
                databaseName: index.dbDatabasename,
                pluginId: index.dsType
              }
            });
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
    let optionsSelect;
    this.setState({ optionsSelect });
  };

  handleChangeClick = (value, dsName) => {
    /*判断数据库表中文名名称*/
    const account = this.props.account;
    const { dispatch } = this.props;
    let obj = {};
    obj.sourceId = "3";
    obj.renterId = account.renterId;
    /* obj.creator = account.username;*/
    obj.dsType = dsName === "MySQL" ? "3" : "2";
    getStoreDatabaseAcquition(obj).then(res => {
      const { code, total } = res.data;
      if (code === "200") {
        let add = [];
        for (let index of res.data.data.rows) {
          add.push(index);

          if (value === index.dsName) {
            dispatch({
              type: "acquisition/setMetaId",
              payload: {
                hostname: index.dbHostname,
                port: index.dbPort,
                username: index.dbUsername,
                password: index.dbPassword,
                databaseName: index.dbDatabasename,
                pluginId: index.dsType
              }
            });
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
    let optionsSelect;
    this.setState({ optionsSelect });
  };

  onChangeGroup(checkedValues) {
    /*文件目录复选框*/
  }
  handleSecher() {
    let sourceId = {
      sourceId: "3"
    };
    CJLBlist(sourceId).then(res => {
      const { code } = res.data;
      if (code === "200") {
        const { dispatch } = this.props;
        dispatch({
          type: "acquisition/setMetaId",
          payload: {
            datalist: res.data.data.rows
          }
        });
      }
    });
  }
  /*检测文件名 数据库中文名称*/
  handleGetNameScarch = (rule, value, callback) => {
    const { dispatch } = this.props;
    const infos = this.state;

    const { caijiList } = this.props.acquisition;

    console.log(caijiList);

    if (caijiList) {
      for (let index of caijiList) {
        if (index.dsName == value) {
          callback();
          return;
        }
      }
    }

    if (value && value !== infos.dsName) {
      if (Timer) {
        clearTimeout(Timer);
        Timer = null;
      }
      Timer = setTimeout(() => {
        let obj = {};
        obj.dsName = value;
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

  componentWillMount() {
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
  /*  
   componentWillUnmount() {
    this.handleChange();
   }*/
  history() {
    window.history.go(-1);
    this.handleSecher();
    const { dispatch } = this.props;
    dispatch({ type: "acquisition/closeModel" });
  }

  render() {
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
    const {
      pluginId,
      dataSource,
      visible,
      confirmLoading,
      current,
      info,
      type
    } = this.state;
    const {
      caijiList,
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
    const { dispatch } = this.props;
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
    const rowSelection = {
      selectedRowKeys,
      type: "radio",
      onChange: (selectedRowKeys, selectedRows) => {
        const { dispatch } = this.props;
        const infos = this.props.acquisition;
        dispatch({
          type: "acquisition/setMetaId",
          payload: {
            selectedRowKeys: selectedRowKeys,
            selectedRows: selectedRows
          }
        });
        this.handleChangeClick(
          selectedRows[0].pluginId,
          selectedRows[0].pluginId
        );
        this.setState({
          pluginId: selectedRows[0].pluginId
        });
      },
      getCheckboxProps: record => {
        let ss = record.pluginId ? "Oracle" : "MySQL";
        return {
          defaultChecked: record.pluginId === this.state.pluginId
        };
      }
    };
    const rowSelection1 = {
      selectedRowKeys: selectRowKeysLeft,
      onChange: (selectRowKeysLeft, selectRowLeft) => {
        const { dispatch, acquisition } = this.props;
        var List = [];
        for (var key in selectRowLeft) {
          List.push(selectRowLeft[key]);
          for (var student in List) {
            let row = [...List];
          }
        }
        dispatch({
          type: "acquisition/setMetaId",
          payload: {
            data1: acquisition.data1,
            hostname: acquisition.hostname,
            port: acquisition.port,
            username: acquisition.username,
            password: acquisition.password,
            databaseName: acquisition.databaseName,
            pluginId: acquisition.pluginId,
            selectRowLeft: List,
            selectRowKeysLeft: selectRowKeysLeft,
            oldTableKey: List
          }
        });
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
      /*  getCheckboxProps: record => {
          console.log(record,"record-----");
          return{
           defaultChecked:record
        }
      }*/
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
      labelCol: { span: 7 },
      wrapperCol: { span: 15 }
    };
    const formItemLayout4 = {
      labelCol: { span: 8 },
      wrapperCol: { span: 15 }
    };
    const formItemLayout5 = {
      labelCol: { span: 3 },
      wrapperCol: { span: 15 }
    };
    let renderShow;
    switch (this.state.current) {
      case 0:
        renderShow = (
          <div className={Style.center}>
            <TableList
              scroll={{ y: 240 }}
              rowSelection={rowSelection}
              pagination={false}
              className={Style.TabelListHost}
              dataSource={this.state.dataSource}
              columns={this.columns}
            />
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
                })(<Input placeholder="请输入数据库所在IP地址" />)}
              </FormItem>

              {pluginId === "MySQL" ? (
                <FormItem label="数据库名称" {...formItemLayout}>
                  {getFieldDecorator("databaseName", {
                    initialValue: infos.databaseName,
                    rules: [{ required: true, message: "请输入数据库名称" }]
                  })(<Input placeholder="请输入数据库名称" />)}
                </FormItem>
              ) : (
                <FormItem label="实例名称" {...formItemLayout}>
                  {getFieldDecorator("databaseName", {
                    initialValue: infos.databaseName,
                    rules: [{ required: true, message: "请输入实例名称" }]
                  })(<Input placeholder="请输入实例名称" />)}
                </FormItem>
              )}
              {pluginId === "MySQL" ? (
                <FormItem label="端口号" {...formItemLayout}>
                  {getFieldDecorator("port", {
                    initialValue: infos.port || "3306",
                    rules: [{ required: true, message: "请输入端口号" }]
                  })(<Input placeholder="请输入端口号" />)}
                </FormItem>
              ) : (
                <FormItem label="端口号" {...formItemLayout}>
                  {getFieldDecorator("port", {
                    initialValue: infos.port || "1521",
                    rules: [{ required: true, message: "请输入端口号" }]
                  })(<Input placeholder="请输入端口号" />)}
                </FormItem>
              )}

              <FormItem label="用户名" {...formItemLayout}>
                {getFieldDecorator("username", {
                  initialValue: infos.username,
                  rules: [{ required: true, message: "请输入用户名" }]
                })(<Input placeholder="请输入用户名" />)}
              </FormItem>
              <FormItem label="密码" {...formItemLayout}>
                {getFieldDecorator("password", {
                  initialValue: infos.password
                    ? strDec(
                        infos.password,
                        infos.username,
                        infos.hostname,
                        infos.port
                      )
                    : "",
                  rules: [{ required: true, message: "请输入密码" }]
                })(<Input type="password" placeholder="请输入密码" />)}
              </FormItem>
              {/*  <FormItem {...formItemLayout2} style={{ marginLeft:'18%'}}>
                    {getFieldDecorator('type', {
                       initialValue: infos.type || true,
                    })(
                    <Checkbox checked={type} onChange={this.onChangeOptions}>采集外部数据源表的同时，批量新建元数据表</Checkbox>
                    )}
                  </FormItem> */}
              <FormItem label="数据库类型" {...formItemLayout3}>
                {getFieldDecorator("dsType", {
                  initialValue: infos.dsType || "3"
                })(
                  <RadioGroup onChange={this.onChangeRadio} value={dsTypes}>
                    <Radio value="3">MySQL</Radio>
                    <Radio value="4">Hive</Radio>
                    <Radio value="5">Hbase</Radio>
                  </RadioGroup>
                )}
              </FormItem>
              <FormItem label="存储的数据库" {...formItemLayout}>
                {getFieldDecorator("dsId", {
                  initialValue: infos.dsId ? String(infos.dsId) : "",
                  rules: [{ required: true, message: "请选择存储的数据库" }]
                })(
                  <Select placeholder="选择数据库">
                    {storeDatabaseOptions.map(item => {
                      return (
                        <Option key={item.value} value={item.value}>
                          {item.label}
                        </Option>
                      );
                    })}
                  </Select>
                )}
              </FormItem>
              {/* {type === true ?(
                      <FormItem label="数据库类型" {...formItemLayout2}>
                        {getFieldDecorator('dsType', {
                          initialValue: infos.dsType || '3',
                        })(
                          <RadioGroup onChange={this.onChangeRadio} value={dsTypes}>
                            <Radio value="3">MySQL</Radio>
                            <Radio value="4">Hive</Radio>
                            <Radio value="5">Hbase</Radio>
                          </RadioGroup>
                        )}
                    </FormItem>
                    ):(null)}*/}
            </div>
          </div>
        );
        break;
      case 1:
        renderShow = (
          <div className={Style.center1}>
            {this.state.type === true ? (
              <Table
                scroll={{ y: 450 }}
                pagination={false}
                onRow={this.headonRowClick.bind(this)}
                rowClassName={(record, index) =>
                  index === indexSelect ? "rowColor" : ""
                }
                rowSelection={rowSelection1}
                style={{ color: "#fff" }}
                className={Style.TabelList}
                dataSource={data1}
                columns={this.columns1}
              />
            ) : null}
            {this.state.type === false ? (
              <Table
                scroll={{ y: 450 }}
                pagination={false}
                onRow={this.headonRowClick.bind(this)}
                rowClassName={(record, index) =>
                  index === indexSelect ? "rowColor" : ""
                }
                rowSelection={rowSelection1}
                style={{ color: "#fff" }}
                className={Style.TabelList}
                dataSource={data1}
                columns={this.columnsOne}
              />
            ) : null}

            {this.state.type === true ? (
              <TableList
                scroll={{ y: 450 }}
                pagination={false}
                rowSelection={rowSelection2}
                onRow={record => {
                  console.log(record);
                }}
                className={Style.TabelList1}
                dataSource={data2}
                columns={this.columns2()}
              />
            ) : null}
            {this.state.type === false ? (
              <TableList
                scroll={{ y: 450 }}
                pagination={false}
                rowSelection={rowSelection2}
                onRow={record => {
                  console.log(record);
                }}
                className={Style.TabelList1}
                dataSource={data2}
                columns={this.columnsTwo()}
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
                <FormItem label="数据来源组织: " {...formItemLayout3}>
                  {getFieldDecorator("dept", {
                    initialValue: dept
                  })(
                    <TreeSelect
                      allowClear
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
                    <Select placeholder="选择表拥有者">
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
      <div>
        <div style={{ width: "47%", marginLeft: "20%" }}>
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
              {/* {this.state.value === 1 ?  (<Button type="primary" style={{ marginLeft: 8 }} onClick={() => this.next()}>下一步</Button>) : null}
                    {this.state.value === 2 ?  (<Button type="primary" style={{ marginLeft: 8 }} >保存</Button>) : null}*/}

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
                  style={{ marginLeft: 8 }}
                  onClick={() => this.StartAcquisition()}
                >
                  开始采集
                </Button>
              ) : null}

              {this.state.value === 2 ? (
                <Button
                  type="primary"
                  style={{ marginLeft: 8 }}
                  onClick={() => this.StartAcquisition()}
                >
                  开始采集
                </Button>
              ) : null}
            </div>
          )}
          {this.state.current > 0 && (
            <div style={{ margin: "-16.3% 0% 0% -52%" }}>
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
          footer={null}
        >
          <Row>
            <Col span={8}>
              <FormItem label="总表数">
                {getFieldDecorator("total", {
                  initialValue: info.total
                })(<label className="ant-form-text">{this.state.total}</label>)}
              </FormItem>
            </Col>
            <Col span={8}>
              <FormItem label="采集成功表数">
                {getFieldDecorator("success", {
                  initialValue: info.success
                })(
                  <label className="ant-form-text">{this.state.success}</label>
                )}
              </FormItem>
            </Col>
            <Col span={8}>
              <FormItem label="采集失败表数">
                {getFieldDecorator("over", {
                  initialValue: info.over
                })(<label className="ant-form-text">{this.state.over}</label>)}
              </FormItem>
            </Col>
          </Row>
          {this.state.type === true ? (
            <div>
              <p>
                提示：可以在【外部数据源采集】-【生成元数据定义表】中继续编辑且生成实体表！
              </p>
              <div style={{ height: 30, textAlign: "center", marginTop: "5%" }}>
                <Button type="primary" onClick={() => this.handleOk()}>
                  <Link to="/MetadataDefine/drafts">去编辑</Link>
                </Button>
              </div>
            </div>
          ) : null}

          {this.state.type === false ? (
            <div>
              <p>
                提示：可以在【元数据定义】-【草稿箱】里面继续编辑生成实体表！
              </p>
              <div style={{ height: 30, textAlign: "center", marginTop: "5%" }}>
                <Button type="primary" onClick={() => this.handleOk()}>
                  <Link to="/DataAcquisition">去编辑</Link>
                </Button>
              </div>
            </div>
          ) : null}
        </Modal>
      </div>
    );
  }
}
const AcquisitionModelForm = Form.create()(AcquisitionModel);
export default withRouter(
  connect(({ acquisition, metadataCommon, system, account }) => ({
    acquisition,
    metadataCommon,
    system,
    account
  }))(AcquisitionModelForm)
);
