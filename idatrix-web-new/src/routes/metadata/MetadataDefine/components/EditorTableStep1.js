/**
 * 元数据定义 编辑器 第一步
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Input, Radio, Row, Col, Select, TreeSelect, Button } from 'antd';
import { safeJsonParse } from 'utils/utils';
import OptimizeModal from './OptimizeModal';
import { metaNameIsExists } from 'services/metadataDefine';
import Modal from 'components/Modal';
import { databaseType } from "config/jsplumb.config.js"
import { debuglog } from 'util';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;
const { TextArea } = Input;

class Editor extends React.Component {
  state = {
    optimizeModalVisible: false,
    optimize: {},
  }

  checkIfIncludeSpecial = (rule, value, cb)=>{
    const reg = new RegExp(/[+\-\*\/]/g);
    if(value && value.match(reg)){
      cb("不能包含特殊字符，例如：+ - * /")
    }else{
      cb()
    }
  }

  componentDidMount() {
    const { dispatch, metaDataDefine: { view } } = this.props;
    dispatch({ type: 'metadataCommon/getSourceTable' });
    dispatch({ type: 'metadataCommon/getUsers' });
    dispatch({ type: 'metadataCommon/getDepartments' });
    if(view){
      if (view.dsType) {
        dispatch({ type: 'metadataCommon/getStoreDatabase', dstype: view.dsType });
      } else {
        dispatch({ type: 'metadataCommon/getStoreDatabase' });
      }
    }
    dispatch({ type: 'metadataCommon/getAllResource' });
  }

  // 点击下一步
  handleSubmit() {
    const { view, viewMode } = this.props.metaDataDefine;
    const isCreating = viewMode === 'new';
    this.props.form.validateFields(async (err, values) => {
      if (err) return;
      const { dispatch } = this.props;
      dispatch({ type: 'metaDataDefine/editView', payload: values });
      dispatch({ type: 'metaDataDefine/showEditor', step: 2 });
      if (!isCreating) {
        dispatch({ type: 'metaDataDefine/getFieldsById', id: view.metaid });
      } else {
        // 清空字段列表
        dispatch({ type: 'metaDataDefine/save', payload: { viewFields: [] } });
      }
      this.props.form.resetFields();
    });
  }

  handleCancel() {
    const { dispatch } = this.props;
    dispatch({ type: 'metaDataDefine/hideAllEditor' });
    this.props.form.resetFields();
  }

  // 调优选项
  handleOptimize = (values) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'metaDataDefine/editView', payload: {
        optimize: values,
      }
    });
    this.setState({ optimizeModalVisible: false });
  }

  // 切换数据库类型
  onChangeDBType = (e) => {
    const { dispatch } = this.props;
    const value = e.target.value;
    const { setFieldsValue } = this.props.form;
    dispatch({
      type: 'metaDataDefine/editView', payload: {
        dsType: value,
        dsId: '',  // 清空之前选择的数据库，以防串库
      }
    });
    setFieldsValue({ dsId: '' });  // 清空之前选择的数据库，以防串库
    dispatch({ type: 'metadataCommon/getStoreDatabase', dstype: value });
  }

  // 检查表名是否冲突
  checkMetaName = async (rule, value, callback) => { 
    const { viewMode ,view } = this.props.metaDataDefine;

    if(viewMode === "draft" && view.metaNameEn ===  value){
      callback();
    }else{
      const { data } = await metaNameIsExists([{
          metaNameEn: value,
          dsId: this.props.form.getFieldValue('dsId'),
        }]);
      if (data && data.data && data.data[0] && data.data[0][value]) {
        if(viewMode === 'edit'){
          callback();
        }else{
          callback('表名已存在');
        }
      }
      callback();
    }
  };

  formItemLayout1 = {
    labelCol: { span: 5, offset: 0 },
    wrapperCol: { span: 8, offset: 0 },
  };
  formItemLayout3 = {
    labelCol: { span: 10, offset: 0 },
    wrapperCol: { span: 14, offset: 0 },
  };
  formItemLayout2 = {
    labelCol: { span: 5, offset: 0 },
    wrapperCol: { span: 14, offset: 0 },
  };
  formItemLayout4 = {
    labelCol: { span: 5, offset: 0 },
    wrapperCol: { span: 18, offset: 0 },
  };

  render() {
    const { metaDataDefine, metadataCommon, account } = this.props;
    const { getFieldDecorator } = this.props.form;
    const { departmentsTree, storeDatabaseOptions, usersOptions, industryOptions, tagsOptions } = metadataCommon;
    const { view, viewMode, direct } = metaDataDefine;
    const isCreating = viewMode === 'new';
    const isEditing = viewMode === 'edit';
    const title = `数据表类元数据-${isCreating ? '新建' : '修改'}-步骤2-1`;
    //const defMetaType = this.props.system.query.metaType || '1';
    // 部门信息兼容处理
    const newStoreDatabaseOptions = storeDatabaseOptions.sort((a,b)=>{
      return a.label.localeCompare(b.label);
    });
    let dept = Array.isArray(view.dept) ? view.dept : safeJsonParse(view.dept) || account.deptId && [String(account.deptId)];
    dept = Array.isArray(dept) ? dept : dept && [String(dept)];
    if (!departmentsTree) dept = null;

    return <Modal
      title={title}
      visible={metaDataDefine.editorStep1Visible}
      onOk={this.handleSubmit.bind(this)}
      onCancel={this.handleCancel.bind(this)}
      maskClosable={false}
      closable={false}
      okText="下一步"
      width={960}
    >
      <Form>
        {/*<FormItem style={{marginBottom:'10px'}}  label="版本标识： " {...this.formItemLayout1}>
          {getFieldDecorator('version', {
            initialValue: view.version,
          })(
            <Input type="number" placeholder="只能是整型数字" />
          )}
        </FormItem>*/}
        {/*<FormItem label="元数据表类型： "  style={{marginBottom:'8px'}} {...this.formItemLayout4}>
          {getFieldDecorator('metaType', {
            initialValue: view.metaType || defMetaType,
          })(
            <RadioGroup disabled={isEditing}>
              <Radio value="1">事实表</Radio>
              <Radio value="2">聚合表</Radio>
              <Radio value="3">查找表</Radio>
              <Radio value="4">维度表</Radio>
              <Radio value="5">宽表</Radio>
              <Radio value="6">基础数据表</Radio>
            </RadioGroup>
          )}
        </FormItem>*/}
        <Row>
          {/*<Col span={12}>
            <FormItem style={{marginBottom:'10px'}} label="选择前置机数据来源表: " {...this.formItemLayout3}>
              {getFieldDecorator('sourceTable', {
                initialValue: view.sourceTable,
              })(
                <Cascader
                  placeholder="选择表"
                  options={sourceTableOptions}
                  loadData={(selectedOptions)=>{
                    const targetOption = selectedOptions[selectedOptions.length - 1];
                    targetOption.loading = true;
                    this.props.dispatch({type: 'metadataCommon/getSourceTable', dsId: targetOption.value });
                  }}
                  changeOnSelect
                />
              )}
            </FormItem>
          </Col>*/}
          <Col span={12}>
            <FormItem style={{ marginBottom: '10px' }} label="版本标识" {...this.formItemLayout3}>
              {getFieldDecorator('version', {
                initialValue: view.version,
              })(
                <Input type="number" disabled={direct} placeholder="只能是整型数字" />
              )}
            </FormItem>
          </Col>
          <Col span={12}>
            <FormItem style={{ marginBottom: '10px' }} label="数据所属组织: " {...this.formItemLayout3}>
              {getFieldDecorator('dept', {
                initialValue: dept,
              })(
                /*<Cascader
                  options={departmentsTree}
                  placeholder="选择组织"
                />*/
                <TreeSelect
                  placeholder="选择组织"
                  treeData={departmentsTree}
                  treeDefaultExpandAll
                  dropdownStyle={{ height: 300 }}
                  disabled={direct}
                  allowClear
                />
              )}
            </FormItem>
          </Col>
        </Row>
        <Row>
          {/* 
              若为直采编辑界面，直接显示数据库类型
              @edited by pwj 2018/9/27
          */}
          <Col span={12}>
          <FormItem style={{ marginBottom: '10px' }} label="数据库类型: " {...this.formItemLayout3}>
            {
              direct 
              ?
              <span>{databaseType.filter(index=>index.value ==view.dsType)[0].name}</span>
              :
              getFieldDecorator('dsType', {
                initialValue: typeof view.dsType !=="undefined" && view.dsType !="" ? view.dsType : '3',
              })(
                  <RadioGroup disabled={isEditing || direct} onChange={this.onChangeDBType} >
                    <Radio value="3">MySQL</Radio>
                    <Radio value="4">Hive</Radio>
                    <Radio value="5">Hbase</Radio>
                  </RadioGroup>
              )

            }

            {/* {view.dsType == 4 || view.dsType == 5 ? (
              <Button
                size="small"
                onClick={() => this.setState({ optimizeModalVisible: true })}
              >调优选项</Button>
            ) : null} */}
          </FormItem>
          </Col>
          <Col span={12}>
            <FormItem style={{ marginBottom: '10px' }} label="存储的数据库：" {...this.formItemLayout3}>
              {getFieldDecorator('dsId', {
                initialValue: view.dsId ? String(view.dsId) : '',
                rules: [{ required: true, message: "请选择存储的数据库" }]
              })(
                <Select disabled={isEditing || direct} placeholder="选择数据库">
                  {newStoreDatabaseOptions.map(item => {
                    return <Option key={item.value} value={item.value}>{item.label}</Option>
                  })}
                </Select>
              )}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <FormItem style={{ marginBottom: '10px' }} label="实体表公开等级" {...this.formItemLayout3}>
              {getFieldDecorator('publicStats', {
                initialValue: view.publicStats || '1',
              })(
                <RadioGroup disabled={direct}>
                  {/*<Radio value="0">公开</Radio>*/}
                  <Radio value="1">授权公开</Radio>
                  <Radio value="2">不公开</Radio>
                </RadioGroup>
              )}
            </FormItem>
          </Col>
          <Col span={12}>
            <FormItem style={{ marginBottom: '10px' }} label="表拥有者： " {...this.formItemLayout3}>
              {getFieldDecorator('owner', {
                initialValue: view.owner || account.username,
                rules: [
                  { required: true, message: "请选择表拥有者" },
                ]
              })(
                <Select placeholder="选择表拥有者" disabled={direct}>
                  {usersOptions.map(item => {
                    return <Option key={item.value} value={item.label}>{item.label}</Option>
                  })}
                </Select>
              )}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <FormItem style={{ marginBottom: '10px' }} label="表中文名称： " {...this.formItemLayout3}>
              {getFieldDecorator('metaNameCn', {
                initialValue: view.metaNameCn,
                rules: [
                  { required: true, message: "请填写表中文名称" },
                  { pattern: /(?=.*[\u4e00-\u9fa5])/, message: '表中文名称必须包含汉字' }
                ]
              }
              )(
                <Input placeholder="请输入表名称" />
              )}
            </FormItem>
          </Col>
          <Col span={12}>
            <FormItem style={{ marginBottom: '10px' }} label="表英文名称： " {...this.formItemLayout3}>
              {getFieldDecorator('metaNameEn', {
                initialValue: view.metaNameEn,
                validateTrigger: 'onBlur',
                rules: [
                  { required: true, message: '请填写表英文名称' },
                  { pattern: /^(?=[a-z])[\w-]+$/i, message: '只能使用字母、数字、下划线，且必须以字母开头' },
                  { validator: this.checkIfIncludeSpecial },
                  { validator: this.checkMetaName }
                ]
              })(
                <Input disabled={isEditing} placeholder="请输入表代码" />
              )}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <FormItem style={{ marginBottom: '10px' }} label="行业： " {...this.formItemLayout3}>
              {getFieldDecorator('industry', {
                initialValue: view.industry,
                rules: [{ required: true, message: "请选择行业" }]
              })(
                <Select placeholder="选择行业">
                  {industryOptions.map(item => {
                    return <Option key={item.value} value={item.value}>{item.label}</Option>
                  })}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col span={12}>
            {/*<FormItem style={{marginBottom:'10px'}} label="主题： " {...this.formItemLayout3}>
              {getFieldDecorator('theme', {
                initialValue: view.theme,
                rules:[{required:true,message:"请选择主题"}]
              })(
                <Select  placeholder="选择主题">
                  {themeOptions.map(item => {
                    return <Option key={item.value} value={item.value}>{item.label}</Option>
                  })}
                </Select>
              )}
            </FormItem>*/}
            <FormItem label="标签：" {...this.formItemLayout3} style={{ marginBottom: "10px" }} >
              {getFieldDecorator('tag', {
                initialValue: view.tag,
                rules: [{ required: true, message: "请选择标签" }]
              })(
                <Select placeholder="选择标签">
                  {tagsOptions.map(item => {
                    return <Option key={item.value} value={item.value}>{item.label}</Option>
                  })}
                </Select>
              )}
            </FormItem>
          </Col>
        </Row>
        <FormItem label="备注：" {...this.formItemLayout2} style={{ marginBottom: "8px" }} >
          {getFieldDecorator('remark', {
            initialValue: view.remark,
          })(
            <TextArea placeholder="请输入备注" maxLength="200" style={{ height: 60 }} />
          )}
        </FormItem>
      </Form>

      {/* 加载调优弹窗 */}
      <OptimizeModal
        visible={this.state.optimizeModalVisible}
        type={view.dsType}
        readonly={!isCreating}
        optimize={view.optimize || {}}
        onOk={this.handleOptimize}
        onCancel={() => this.setState({ optimizeModalVisible: false })}
      />
    </Modal>
  }
}

export default connect(({ metaDataDefine, metadataCommon, system, account }) => ({
  metaDataDefine,
  metadataCommon,
  system,
  account,
}))(Form.create()(Editor));
