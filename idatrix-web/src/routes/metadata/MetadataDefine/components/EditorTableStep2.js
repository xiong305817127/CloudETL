/**
 * 元数据定义 编辑器 第二步
 */
import React from 'react';
import { connect } from 'dva';
import { Button, Form, Table, Input, Select, message } from 'antd';
import {
  importMetadataTable,
  createMetadata,
  updateTableBase,
  batchInsertFields,
  createEntyTable,
  batchUpdateFields,
} from 'services/metadataDefine';
import Modal from 'components/Modal';
import { deepCopy, downloadFile, findKeyByValue } from 'utils/utils';
import { submitDecorator } from 'utils/decorator';
import dbDataType from 'config/dbDataType.config';
import dbTypeValue from 'config/dbTypeValue.config';
import Upload from 'components/Upload';
import ImportExistingFields from './ImportExistingFields';

import Style from '../style.css';

const FormItem = Form.Item;
const Option = Select.Option;

const getIndexByKey = (columns, key) => {
  let index = -1;
  columns.some((item, i) => {
    if (item.dataIndex === key) {
      index = i;
      return true;
    }
  });
  return index;
};

@submitDecorator
class Editor extends React.Component {

  state = {
    importExistingVisible: false,
    selectedRows: [],
    selectedRowKeys: [],
    editDeleted: [] // 用于记录是否删除
  };

  makeColumns = () => {
    const { getFieldDecorator } = this.props.form;
    const { view, direct } = this.props.metaDataDefine;
    const { dsType } = view;
    const tName = findKeyByValue(dbTypeValue, dsType);
    const { existLength, otherType, existPrecision } = dbDataType[tName];
    const columns = [
      {
        title: '列族',
        dataIndex: 'colFamily',
        width: "10%",
        render: (text, record, index) => {
          if (record.isPk == 1) return null; // 主键没有列族
          return <FormItem labelCol={{ span: 0 }}>
            {getFieldDecorator(`rows[${index}].colFamily`, {
              initialValue: text,
              rules: [
                { pattern: /^(?=[a-z])\w+$/i, message: '只能使用字母、数字、下划线，且必须以字母开头' },
              ],
            })(
              <Input disabled={record.isPk == 1 || direct} onChange={(e) => { this.modifyField('colFamily', e.target.value, record) }} />
            )}
          </FormItem>
        }
      }, {
        title: '字段名称',
        dataIndex: 'colName',
        render: (text, record, index) => {
          return <FormItem labelCol={{ span: 0 }}>
            {getFieldDecorator(`rows[${index}].colName`, {
              initialValue: text,
              rules: [
                { required: true, message: '字段名称不能为空' },
                { pattern: /^(?=[a-z])\w+$/i, message: '只能使用字母、数字、下划线，且必须以字母开头' },
                { validator: this.checkFieldName },
              ],
            })(
              <Input disabled={!!record.id || direct} maxLength="50" onChange={(e) => { this.modifyField('colName', e.target.value, record) }} />
            )}
          </FormItem>
        }
      }, {
        title: '字段描述',
        dataIndex: 'description',
        key: 'description',
        width: "20%",
        render: (text, record) => {
          return <FormItem labelCol={{ span: 0 }}>
            <Input defaultValue={text} maxLength="200" onChange={(e) => { this.modifyField('description', e.target.value, record) }} />
          </FormItem>
        }
      }, {
        title: '分区顺序',
        width: "15%",
        dataIndex: 'sequence',
        render: (text, record) => {
          return <FormItem labelCol={{ span: 0 }}>
            <Input defaultValue={text} disabled={direct} onChange={(e) => { this.modifyField('sequence', e.target.value, record) }} />
          </FormItem>
        }
      }, {
        title: '数据类型',
        dataIndex: 'dataType',
        key: 'dataType',
        width: "12%",
        render: (text, record, index) => {

          //屏蔽默认值，加非空验证
          //@edited by pwj   
          //const defValue = dsType == dbTypeValue['hbase'] ? 'varchar' : 'int';
          //const defValueOr = dsType == dbTypeValue['orcle'] ? 'varchar' : 'int';
          const typeList = [...existLength.keys(), ...otherType].map(index => index.toLowerCase()).sort();
          //let formatText = text;
          // if (formatText) {
          // formatText = formatText.toLowerCase();
          // }
          //value={formatText && found ? formatText : defValue && found ? formatText : defValueOr}
          let found = false;
          if (text) {
            let formatText = text.toLowerCase();
            found = (typeList || []).some(it => it === formatText);
          }

          return <FormItem labelCol={{ span: 0 }}>
            {getFieldDecorator(`rows[${index}].type`, {
              initialValue: found ? text : "请选择",
              rules: [
                { required: true, message: '数据类型不能为空' },
              ],
            })(
              <Select disabled={direct}  onChange={(value) => { this.handleChangeType(index, value, record) }} style={{ width: '100%' }}>
                {typeList.map(dbtype => (
                  <Option key={dbtype} value={dbtype}>{dbtype}</Option>
                ))}
              </Select>
            )}
          </FormItem>
        }
      }, {
        title: '长度',
        dataIndex: 'length',
        width: "7%",
        render: (text, record, index) => {
          let str = record.dataType.toUpperCase();
          if (existLength.has(str)) {
            const { maxLength, defaultLength } = existLength.get(str);
            let value = defaultLength + "";

            if (text) {
              value = text + "";
            }
            let bool = existLength.has(str);
            return (
              <FormItem labelCol={{ span: 0 }}>
                {getFieldDecorator(`rows[${index}].length`, {
                  initialValue: Number(value),
                  rules: [
                    { validator: this.checkLength(record, maxLength, bool) },
                    { required: true, message: '长度不能为空' },
                  ],
                })(
                  <Input type="number" disabled={direct} onChange={(e) => { this.modifyField('length', Number(e.target.value), record) }} />
                )}
              </FormItem>
            )
          };
          return (
            <FormItem labelCol={{ span: 0 }}>
              {getFieldDecorator(`rows[${index}].length`, {
                initialValue: text ? text : "--",
              })(
                <Input type="number" disabled={true} />
              )}
            </FormItem>
          );
        }
      }, {
        title: '精度',
        dataIndex: 'precision',
        key: 'precision',
        width: '6%',
        render: (text, record, index) => {

          let str = record.dataType.toUpperCase();
          console.log("数据类型",str, existPrecision);
          if (existPrecision.has(str)) {
            const { maxPrecision, defaultPrecision } = existPrecision.get(str);

            const isString = typeof text === 'string';
            const value = isString ? text : defaultPrecision + "" ? defaultPrecision + "" : "";
            return (
              <FormItem labelCol={{ span: 0 }}>
                {getFieldDecorator(`rows[${index}].precision`, {
                  initialValue: Number(value),
                  rules: [
                    { validator: this.checkPrecision(record, maxPrecision, `rows[${index}].`) },
                    { required: true, message: '精度不能为空' },
                  ],
                })(
                  <Input type="number" disabled={direct} onChange={(e) => { this.modifyField('precision', Number(e.target.value), record) }} />
                )}
              </FormItem>
            )
          };
          return (
            <FormItem labelCol={{ span: 0 }}>
              {getFieldDecorator(`rows[${index}].precision`, {
                initialValue: text
              })(
                <Input type="number" disabled={true} />
              )}
            </FormItem>
          );
        }
      }, {
        title: '是否主键',
        dataIndex: 'isPk',
        width: "7%",
        render: (text, record) => {
          return <FormItem labelCol={{ span: 0 }}>
            <Select defaultValue={text} disabled={direct} onChange={(value) => { this.modifyField('isPk', value, record) }}>
              <Option key="none" value="1">是</Option>
              <Option key="left" value="0">否</Option>
            </Select>
          </FormItem>
        },
      }, {
        title: '是否允许为空',
        dataIndex: 'isNull',
        key: 'isNull',
        width: "8%",
        render: (text, record) => {
          return <FormItem labelCol={{ span: 0 }}>
            <Select disabled={record.isPk == 1} disabled={direct} value={text} onChange={(value) => { this.modifyField('isNull', value, record) }}>
              <Option key="none" value="1">是</Option>
              <Option key="left" value="0">否</Option>
            </Select>
          </FormItem>
        },
      }
    ];

    // 如果是mysql
    if (Number(view.dsType) === dbTypeValue.mysql) {
      //columns.splice(getIndexByKey(columns, 'indexType'), 1); // 删除索引类型
      //columns.splice(getIndexByKey(columns, 'indexId'), 1); // 删除索引方法
      columns.splice(getIndexByKey(columns, 'sequence'), 1);
      columns.splice(getIndexByKey(columns, 'colFamily'), 1);
    }
    // 如果不是orcle
    if (Number(view.dsType) !== dbTypeValue.orcle) {
      //columns.splice(getIndexByKey(columns, 'indexType'), 1); // 删除索引类型
      //columns.splice(getIndexByKey(columns, 'indexId'), 1); // 删除索引方法
    }
    // 如果是hive
    if (Number(view.dsType) === dbTypeValue.hive) {
      //columns.splice(getIndexByKey(columns, 'sequence'), 1); // 删除分区顺序
      //columns.splice(getIndexByKey(columns, 'bucketing'), 1); // 删除分桶定义
      columns.splice(getIndexByKey(columns, 'precision'), 1);
      columns.splice(getIndexByKey(columns, 'isPk'), 1);
      columns.splice(getIndexByKey(columns, 'colFamily'), 1);
      //新增屏蔽  hive 长度、是否为空
      columns.splice(getIndexByKey(columns, 'length'), 1);
      columns.splice(getIndexByKey(columns, 'isNull'), 1);
    }
    // 如果是hbase
    if (Number(view.dsType) === dbTypeValue.hbase) {
      //columns.splice(getIndexByKey(columns, 'colFamily'), 1); // 删除列族
      columns.splice(getIndexByKey(columns, 'sequence'), 1);
      columns.splice(getIndexByKey(columns, 'precision'), 1);
      //新增屏蔽   字段描述、长度及是否允许为空
      columns.splice(getIndexByKey(columns, 'description'), 1);
      columns.splice(getIndexByKey(columns, 'length'), 1);
      columns.splice(getIndexByKey(columns, 'isNull'), 1);
    }
    // // 如果是hive
    // if (Number(view.dsType) === dbTypeValue.hive) {
    //   //columns.splice(getIndexByKey(columns, 'isPk'), 1); // 删除主键属性
    //   // 以下两条由于目前未实现，暂时隐藏
    //   //columns.splice(getIndexByKey(columns, 'sequence'), 1); // 暂时隐藏
    //   ///columns.splice(getIndexByKey(columns, 'bucketing'), 1); // 暂时隐藏
    // }

    return columns;
  };

  //根据是否直采，显示不同逻辑表格头
  //@edited by pwj 2018/09/27

  columns = [
    {
      title: '字段名称',
      dataIndex: 'colName',
      width: "20%",
      render: (text) => {
        return <FormItem labelCol={{ span: 0 }}>
          <Input defaultValue={text} disabled />
        </FormItem>
      }
    }, {
      title: '字段描述',
      dataIndex: 'description',
      key: 'description',
      render: (text, record) => {
        return <FormItem labelCol={{ span: 0 }}>
          <Input defaultValue={text} maxLength="200" onChange={(e) => { this.modifyField('description', e.target.value, record) }} />
        </FormItem>
      }
    }, {
      title: '数据类型',
      dataIndex: 'dataType',
      key: 'dataType',
      width: "12%",
      render: (text) => {
        return <FormItem labelCol={{ span: 0 }}>
          <Input defaultValue={text} disabled />
        </FormItem>
      }
    }, {
      title: '长度',
      dataIndex: 'length',
      width: "7%",
      render: (text) => {
        return <FormItem labelCol={{ span: 0 }}>
          <Input defaultValue={text} disabled />
        </FormItem>
      }
    }, {
      title: '精度',
      dataIndex: 'precision',
      key: 'precision',
      width: '6%',
      render: (text) => {
        return <FormItem labelCol={{ span: 0 }}>
          <Input defaultValue={text} disabled />
        </FormItem>
      }
    }, {
      title: '是否主键',
      dataIndex: 'isPk',
      width: "7%",
      render: (text) => {
        return <FormItem labelCol={{ span: 0 }}>
          <Input defaultValue={parseInt(text) === 0 ? "否" : "是"} disabled />
        </FormItem>
      }
    }, {
      title: '是否允许为空',
      dataIndex: 'isNull',
      key: 'isNull',
      width: "8%",
      render: (text) => {
        return <FormItem labelCol={{ span: 0 }}>
          <Input defaultValue={parseInt(text) === 0 ? "否" : "是"} disabled />
        </FormItem>
      }
    }
  ];

  // 修改字段
  modifyField(keyOfCol, value, record) {
    const { dispatch } = this.props;
    const { viewFields } = this.props.metaDataDefine;
    const payload = deepCopy(viewFields).find(row => row.key === record.key);
    payload[keyOfCol] = value;
    dispatch({ type: 'metaDataDefine/modifyField', payload });
  }

  // 修改类型时的回调
  handleChangeType(index, value, record) {
    this.props.form.resetFields([`rows[${index}].length`]);
    this.props.form.resetFields([`rows[${index}].precision`]);
    this.modifyField('dataType', value, record);
  }

  // 保存并生效
  async handleSubmit() {
    this.props.form.validateFields({ force: true }, async (err, values) => {
      if (err) { // 如果表单验证不通过
        return;
      }
      this.props.disableSubmit();
      const { account, dispatch } = this.props;
      const { view, viewMode, viewFields, direct } = this.props.metaDataDefine;
      const isCreating = viewMode === 'new';
      if (viewFields.length < 1) {
        message.error('请先添加字段');
        this.props.enableSubmit();
        return;
      }
      // 如果是hbase
      if (view.dsType === '5' && !viewFields.some(it => it.isPk == 1)) {
        message.error('缺少主键');
        this.props.enableSubmit();
        return;
      }
      const formData = deepCopy(viewFields.filter(row => row.status > 0));
      // const formData = (this.state.editDeleted.length > 0) 
      //                 ? tempFormData.concat(this.state.editDeleted)
      //                 : tempFormData;

      //删除mysql中无用字段
      if (view.dsType === '3') {
        const { dsType } = view;
        const tName = findKeyByValue(dbTypeValue, dsType);
        const { existLength, otherType, existPrecision } = dbDataType[tName];
        formData.forEach(item => {

          if (!existLength.has(item.dataType.toUpperCase())) {
            delete item.length;
          }
          if (!existPrecision.has(item.dataType.toUpperCase())) {
            delete item.precision
          }
        })
      }

      const metaid = view.metaid || await this.getId();

      let isFail = false;

      // 数据处理
      delete formData.id; // 去除model里的无关属性
      formData.forEach(row => {
        row.metaid = metaid;
        delete row.key;
      });
      if (isCreating) {
        const { data } = await batchInsertFields(formData);
        if(data.code !== '200'){
          this.props.enableSubmit();
          return false;
        }
      } else {
        const { data } = await updateTableBase([view]); // 更新基本信息
        if (data && data.code === '200') {
          // 需要修改字段
          if (viewFields.filter(row => row.status).length > 0) {
            await batchUpdateFields(formData);
          }
          // 需要插入历史版本
          /* 后端已自行生成历史版本，无需调用此逻辑
          if (viewFields.filter(row => row.status).length > 0) {
            const { data } = await insertHisVersion(formData);
            if (!data || data.code !== '0') {
              isFail = true;
              errMessage = data && data.msg || '添加历史版本失败';
            }
          }
          */
        } else {
          isFail = true;
        }
      }
      if (direct) {
        //直采模式的提交方式，只更新字段即可
        //@edited by pwj 2018/9/27
        if (!isFail) {
          window.location.href = "#/MetadataDefine";
          dispatch({ type: 'metaDataDefine/hideAllEditor' });
          dispatch({ type: 'metaDataDefine/getList', payload: this.props.system.query });
        } else {
          message.error(errMessage);
        }
      } else {
        if (!isFail) { // 前置步骤成功
          const query = {
            userId: account.id,
            ids: metaid,
          };
          const { data } = await createEntyTable(query);

          if (data && data.code === '200') {
            message.success('生效成功！');
            window.location.href = "#/MetadataDefine";
          }
          dispatch({ type: 'metaDataDefine/hideAllEditor' });
          dispatch({ type: 'metaDataDefine/getList', payload: this.props.system.query });
        } else { // 前置步骤失败
          message.error(errMessage);
        }
      }
      this.props.enableSubmit();
    });
  }

  handleCancel() {
    const { dispatch } = this.props;
    dispatch({ type: 'metaDataDefine/hideAllEditor' });
    // this.props.form.resetFields();
  }

  // 保存为草稿
  async handleSaveDraft() {
    this.props.form.validateFields({ force: true }, async (err, values) => {
      if (err) { // 如果表单验证不通过
        return;
      }
      message.info("保存中,请稍后...");
      this.props.disableSubmit('submitLoading2');
      const { dispatch, account } = this.props;
      const { view, viewMode, viewFields } = this.props.metaDataDefine;
      const isCreating = viewMode === 'new';
      const formData = deepCopy(view);
      if (viewFields.length < 1) {
        message.error('请先添加字段');
        this.props.enableSubmit('submitLoading2');
        return;
      }
      // 如果是hbase
      if (view.dsType === '5' && !viewFields.some(it => it.isPk == 1)) {
        message.error('缺少主键');
        this.props.enableSubmit('submitLoading2');
        return;
      }
      formData.creator = account.username;
      formData.dept = JSON.stringify(formData.dept);
      formData.storeDatabase = String(formData.dsId); // 兼容后端bug
      formData.databaseType = formData.dsType; // 兼容后端bug
      formData.isremove = 2;
      const { data } = await createMetadata(formData);

      if (data && data.code === '200') {
        const metaid = data.data.metaid;
        dispatch({ type: 'metaDataDefine/editView', payload: { metaid } });
        const fields = deepCopy(viewFields);
        // 数据处理
        delete fields.id; // 去除model里的无关属性
        fields.forEach(row => {
          row.metaid = metaid;
          delete row.key;
        });

        const { data: data2 } = await batchInsertFields(fields);
        console.log(data, "二");
        if (data2 && data2.code === '200') {
          message.success('已保存草稿');
          dispatch({ type: 'metaDataDefine/hideAllEditor' });
        }
      }
      this.props.enableSubmit('submitLoading2');
    });
  }

  // 上一步
  handleLastStep() {
    const { dispatch } = this.props;
    dispatch({ type: 'metaDataDefine/showEditor', step: 1 });
  }

  // 删除字段
  handleDeleteFields() {
    const { dispatch, metaDataDefine } = this.props;
    const { viewMode} = metaDataDefine;
    const isCreating = viewMode === 'new';

    dispatch({ type: 'metaDataDefine/delField', keys: this.state.selectedRows.map(r => r.key) });
  }

  // 添加字段
  handleAdd() {
    const { dispatch } = this.props;
    dispatch({ type: 'metaDataDefine/addField' });
  }

  // 下载Excel
  downloadExcel() {
    const { view } = this.props.metaDataDefine;
    const dsType = Number(view.dsType);
    let fileName = '';
    switch (dsType) {
      case dbTypeValue.hive: fileName = '元数据定义-hive表结构导入.xlsx'; break;
      case dbTypeValue.hbase: fileName = '元数据定义-hbase表结构导入.xlsx'; break;
      case dbTypeValue.orcle: fileName = '元数据定义-orcle表结构导入.xlsx'; break;
      default: fileName = '元数据定义-mysql表结构导入.xlsx'; break;
    }
    downloadFile(`files/excel-template/${fileName}`);
  }

  // 获取id
  async getId() {
    const { dispatch, account } = this.props;
    const { view, viewMode } = this.props.metaDataDefine;
    const isCreating = viewMode === 'new';
    if (view.metaid) {
      return view.metaid;
    } else if (isCreating && !view.metaid) {
      const formData = deepCopy(view);
      formData.creator = account.username;
      formData.dept = JSON.stringify(formData.dept);
      formData.storeDatabase = String(formData.dsId); // 兼容后端bug
      // formData.databaseType = formData.dsType; // 兼容后端bug
      delete formData.dsType; // 该字段不需要提交
      const { data } = await createMetadata(formData);
      const id = data && data.data && data.data.metaid;
      dispatch({ type: 'metaDataDefine/editView', payload: { metaid: id } });
      return id;
    }
  }

  // 上传前需要获取metaid
  beforeUpload() {
    const { view, viewMode } = this.props.metaDataDefine;
    const isCreating = viewMode === 'new';
    return new Promise(async (resolve, reject) => {
      if (isCreating && !view.metaid) {
        // await this.getId();
        setTimeout(resolve, 100);
      } else {
        resolve();
      }
    });
  }

  // 上传状态改变
  uploadStatusChange(e) {
    const { dispatch } = this.props;
		const { response } = e.file;
    if (response) {
      if (response.code === '200') {
        const viewFields = response.data && response.data.metadataProperties || [];
        message.success('导入成功');
        dispatch({ type: 'metaDataDefine/importExisting',status:"import", payload: viewFields });
      }else{
        if(response.msg){
          Modal.error({
            title: '导入数据错误',
            zIndex:1020,
            content: (
              <div>
                {
                  response.data && response.data.map(index=><p>{index}</p>)
                }
              </div>
            ),
            onOk() {},
          });
        }
      }
    }
  }

  // 长度检验
  checkLength = (record, maxLength, type) => (rule, value, callback) => {
    let num = Number(value);

    if (0 <= num && num <= maxLength) {
      if (num === 0 && !type) {
        callback(`长度范围为1~${maxLength}!`);
      }
      callback();
    } else {
      callback(`长度范围为0~${maxLength}!`);
    }
  }

  //精度校验
  checkPrecision = (record, maxLength, str) => (rule, value, callback) => {
    let num = Number(value);

    if (0 <= num && num <= maxLength) {
      const { getFieldValue } = this.props.form;

      // 修改长度的判断为精度的判断
      // edited by steven leo on 2018/12/26
      if (Number(getFieldValue(`${str}precision`)) < Number(value)) {
        callback("精度不能大于长度！");
      }
      callback();
    } else {
      callback(`精度范围为0~${maxLength}!`);
    }
  }

  // 字段名验证
  checkFieldName = (rule, value, callback) => {
    const { getFieldError, getFieldValue, setFields } = this.props.form;
    const fields = getFieldValue('rows');
    const checkRepeat = (val) => {
      let repeatedCount = 0;
      fields.forEach((field) => {
        if (field.colName === val) {
          repeatedCount += 1;
        }
      });
      return repeatedCount > 1;
    };
    // 重新校验
    fields.forEach((field, index) => {
      const fieldName = `rows[${index}].colName`;
      const err = getFieldError(fieldName);
      const val = getFieldValue(fieldName);
      if (err && err.some(it => it === '字段名重复') && !checkRepeat(val)) {
        setFields({
          [fieldName]: { value: val },
        });
      }
    });
    if (checkRepeat(value)) {
      callback('字段名重复');
      return;
    }
    callback();
  }

  // 从现有表导入
  handleImportExist = (fields) => {
    const { dispatch } = this.props;
    dispatch({ type: 'metaDataDefine/importExisting', payload: fields });
  }

  // 全选操作
  onChangeAllSelect(selectedRowKeys, selectedRows) {
    this.setState({ selectedRowKeys, selectedRows });
  }

  render() {

    const { metaDataDefine, submitLoading, submitLoading2 } = this.props;
    const { view, viewMode, viewFields, direct } = metaDataDefine;
    const isCreating = viewMode === 'new';
    const title = `数据表类元数据-${isCreating ? '新建' : '修改'}-步骤2-2`;
    const dataSource = deepCopy(viewFields).filter(row => row.status !== 2);

    console.log(viewFields,"数组类型");

    return <Modal
      title={title}
      visible={metaDataDefine.editorStep2Visible}
      onOk={this.handleSubmit.bind(this)}
      onCancel={this.handleCancel.bind(this)}
      maskClosable={false}
      closable={false}
      okText="保存"
      width={1200}
      footer={[
        <Button key="back" disabled={submitLoading || submitLoading2} size="large" onClick={this.handleCancel.bind(this)}>取消</Button>,
        isCreating ? <Button key="draft" disabled={submitLoading} type="primary" size="large" onClick={this.handleSaveDraft.bind(this)} loading={submitLoading2}>保存为草稿</Button> : null,
        <Button key="next" disabled={submitLoading2} type="primary" size="large" onClick={this.handleSubmit.bind(this)} loading={submitLoading}>保存并生效</Button>,
        <Button key="up" disabled={submitLoading || submitLoading2} type="primary" size="large" onClick={this.handleLastStep.bind(this)}>上一步</Button>
      ]}
    >
      {
        !direct ? (<div style={{ overflow: 'hidden' }} className={Style['btns-wrap']}>
          <Upload
            name="sourceFile"
            accept='application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            action={`${importMetadataTable}?dbType=${view.dsType}`}
            showUploadList={false}
            // beforeUpload={this.beforeUpload.bind(this)}
            onChange={this.uploadStatusChange.bind(this)}
            style={{ marginRight: 10 }}
          >
            <Button>从Excel导入</Button>
          </Upload>
          <Button onClick={this.downloadExcel.bind(this)}>下载Excel模板</Button>
          <Button onClick={() => { this.setState({ importExistingVisible: true }) }}>从现有表元数据定义引入字段</Button>
          <Button style={{ float: "right" }} onClick={this.handleDeleteFields.bind(this)}>删除</Button>
          <Button style={{ float: "right", marginRight: 10 }} onClick={this.handleAdd.bind(this)}>添加</Button>
        </div>) : null
      }
      <Form>
        {/*
          添加直采模式，表单展现方式
          @edited by pwj 2018/09/27
        */}
        {
          !direct ? (<Table
            showIndex
            rowKey="key"
            scroll={{ y: 450 }}
            columns={this.makeColumns()}
            dataSource={dataSource}
            rowSelection={{
              onChange: this.onChangeAllSelect.bind(this),
              selectedRowKeys: this.state.selectedRowKeys,
            }}
            pagination={false}
            style={{ marginTop: '20px' }}
            className="editor-table th-nowrap"
          />) : (<Table
            showIndex
            rowKey="key"
            scroll={{ y: 450 }}
            columns={this.columns}
            dataSource={dataSource}
            pagination={false}
            className="editor-table th-nowrap"
          />)
        }
      </Form>

      {this.state.importExistingVisible ? (
        <ImportExistingFields
          onOk={this.handleImportExist}
          onClose={() => this.setState({ importExistingVisible: false })}
        />
      ) : null}

    </Modal>
  }
}

export default connect(({ metaDataDefine, metadataCommon, account, system }) => ({
  metaDataDefine,
  metadataCommon,
  account,
  system,
}))(Form.create()(Editor));
