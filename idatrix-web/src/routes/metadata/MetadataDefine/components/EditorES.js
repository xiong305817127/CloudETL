/**
 * ES索引类编辑器
 */
import React from 'react';
import { connect } from 'dva';
import { Tabs, Form, Input, Radio, Row, Col, Table, TreeSelect, Select, Button, Popconfirm, Icon, message } from 'antd';
import { dateFormat, safeJsonParse, createGUID, deepCopy } from 'utils/utils';
import Modal from 'components/Modal';
import { submitDecorator } from 'utils/decorator';
import { insertES, modifyES } from 'services/metadataDefine';

import Style from '../style.css';

const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const { TextArea } = Input;

@submitDecorator
class Editor extends React.Component {
  state = {
    panes: [{
      title: '映射类别1',
      key: '1',
      data: [],
    }],
    activeKey: '1',
    paneLoaded: false,
  };

  newTabIndex = 2;

  componentWillMount(){
    const { dispatch } = this.props;
    // dispatch({ type: 'metadataCommon/getDepartments' });
  }

  componentWillReceiveProps(nextProps) {
    const { viewMode, view } = this.props.metaES;
    if (viewMode !== 'new' && !this.state.paneLoaded) {
      const panes = view.viewTabs.map((tab, index) => {
        const pane = {
          title: tab.typeName,
          key: String(index + 1),
          data: tab.fields,
          closable: index > 0
        };
        return pane;
      });
      this.setState({ panes, paneLoaded: true });
    }
  }

  // 校验各标签页下的表单
  checkTabsForm(err) {
    if (err.tabs) { // 切换到第一个有错误的tab
      let errTabIndex = 0;
      err.tabs.some((item, index) => {
        if (item) {
          errTabIndex = index;
          return true;
        }
      });
      const activeKey = this.state.panes[errTabIndex].key;
      this.setState({ activeKey });
    }
  }

  // 点击确定
  handleSubmit(drafBool = false) {
    const { dispatch } = this.props;
    const { view, viewMode } = this.props.metaES;
    const { panes } = this.state;
    const isDraf = drafBool === true;
    this.props.form.validateFields({ force: true }, async (err, values) => {
      if (err) { // 如果表单验证不通过
        this.checkTabsForm(err);
        return;
      }
      this.props.disableSubmit(isDraf ? 'submitLoading2' : 'submitLoading');
      const formData = deepCopy(values);

      // 因切换标签会千万form数据项丢失，需要进行数据修复
      formData.tabs.forEach((tab, tabIndex) => {
        if (!tab.fields && panes[tabIndex].data) {
          tab.fields = panes[tabIndex].data;
        }
      });
      if (isDraf === true) formData.status = 2;
      if (viewMode === 'new') { // 新建
        const { data } = await insertES(formData);
        if (data && data.code === '200') {
          message.success(isDraf ? '保存成功' : '发布成功');
          dispatch({ type: 'metaES/hideEditor' });
          dispatch({ type: 'metaES/getList', payload: this.props.system.query });
          this.props.form.resetFields();
        }
      } else if (viewMode === 'edit') { // 修改
        /*Modal.confirm({
          title: '是否重建索引？',
          content: '重建索引需要花费一定的时间',
          okText: '重建',
          cancelText: '仅发布',
        });
        return;*/
        formData.indexId = view.indexId;
        // 处理从未开启过的标签
        formData.tabs.forEach((tab, index) => {
          if (!tab.fields && this.state.panes[index].data) {
            tab.fields = this.state.panes[index].data;
          }
        });
        const { data } = await modifyES(formData);
        if (data && data.code === '200') {
          message.success('修改成功');
          dispatch({ type: 'metaES/hideEditor' });
          dispatch({ type: 'metaES/getList', payload: this.props.system.query });
          this.props.form.resetFields();
        }
      } else {
        dispatch({ type: 'metaES/hideEditor' });
        this.props.form.resetFields();
      }
      this.props.enableSubmit(isDraf ? 'submitLoading2' : 'submitLoading');
    });
  }

  // 保存为草稿
  handleSaveDraft() {
    this.handleSubmit(true);
  }

  handleCancel() {
    const { dispatch } = this.props;
    dispatch({ type: 'metaES/hideEditor' });
    this.props.form.resetFields();
  }

  // 切换标签
  handleChangeTab = (activeKey) => {
    this.setState({ activeKey });
  };

  // 编辑标签
  handleEditTab = (targetKey, action) => {
    if (action === 'add') {
      this.handleAddTab();
    } else if (action === 'remove') {
      this.handleRemoveTab(targetKey);
    }
  };

  // 新增标签
  handleAddTab = () => {
    const { panes } = this.state;
    const activeKey = createGUID();
    panes.push({ title: `映射类别${this.newTabIndex}`, key: activeKey, data: [] });
    this.newTabIndex ++;
    this.setState({ panes, activeKey });
  };

  // 删除标签
  handleRemoveTab = (targetKey) => {
    if (this.state.panes.length <= 1) return;
    Modal.confirm({
      content: '确认要删除该类别吗？',
      onOk: () => {
        const panes = this.state.panes.filter(pane => pane.key !== targetKey);
        let activeKey = this.state.activeKey;
        if (activeKey === targetKey) {
          activeKey = panes[panes.length - 1].key;
        }
        this.setState({ panes, activeKey });
      },
    });
  };

  // 修改映射类别
  handleChangeTypeName = (paneIndex, value) => {
    const { panes } = this.state;
    panes[paneIndex].title = value;
    this.setState({ panes });
  }

  // 删除行
  handleDeleteRow = (paneIndex, rowIndex) => {
    const { panes } = this.state;
    const { tabs } = this.props.form.getFieldsValue();
    // 缓存表单数据，解决删除行后数据丢失的bug
    tabs.forEach((tab, tabIndex) => {
      if (Array.isArray(tab.fields)) {
        tab.fields.forEach((field, index) => {
          Object.assign(panes[tabIndex].data[index], field);
        });
      }
    });
    panes[paneIndex].data.splice(rowIndex, 1);
    const newPanes = deepCopy(panes);
    // 清空一次表单，解决往前删除BUG
    this.setState({ panes: [] }, () => {
      this.setState({ panes: newPanes });
    });
  };

  // 添加行
  handleAddRow = (paneIndex) => {
    const { panes } = this.state;
    panes[paneIndex].data.push({
      key: createGUID(),
      fieldName: '',
      fieldType: '2',
      isIk: '1',
      wordBreaker: 'english',
      isStore: '1',
      isAllField: '1',
      isSourceField: '1',
    });
    this.setState({ panes });
  };

  // 字段名验证
  checkFieldName = paneIndex => (rule, value, callback) => {
    const { getFieldError, getFieldValue, setFields } = this.props.form;
    const tabs = getFieldValue('tabs');
    const { fields } = tabs[paneIndex];
    const checkRepeat = (val) => {
      let repeatedCount = 0;
      fields.forEach((field) => {
        if (field.fieldName === val) {
          repeatedCount += 1;
        }
      });
      return repeatedCount > 1;
    };
    // 重新校验
    fields.forEach((field, index) => {
      const fieldName = `tabs[${paneIndex}].fields[${index}].fieldName`;
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

  makeColumns = (paneIndex) => {
    const { metaES } = this.props;
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const isReading = metaES.viewMode === 'read';
    const pre = `tabs[${paneIndex}].fields`;
    return [
      {
        title: '字段名称',
        dataIndex: 'fieldName',
        width:"8%",
        render: (text, record, index) => {
          return <FormItem labelCol={{span:0}}>
            {getFieldDecorator(`${pre}[${index}].fieldName`, {
              initialValue: text,
              rules:[
                { required: true, message: '字段名称不能为空' },
                { pattern: /^(?!_)[\u4e00-\u9fa5\w]+$/, message: '由中文、英文、数字、下划线组成，不能以下划线开头' },
                { validator: this.checkFieldName(paneIndex) },
              ]
            })(
              <Input disabled={isReading} maxLength="20" />
            )}
          </FormItem>
        }
      }, {
        title: '字段类型',
        dataIndex: 'fieldType',
        width:"5%",
        render:(text, record, index)=>{
          return <FormItem labelCol={{span:0}}>
            {getFieldDecorator(`${pre}[${index}].fieldType`, {
              initialValue: String(text),
            })(
              <Select disabled={isReading} style={{width: '100%'}}>
                <Option key="1">date</Option>
                <Option key="2">string</Option>
                {/*<Option key="3">date range</Option>*/}
              </Select>
            )}
          </FormItem>
        }
      }, {
        title: '是否分词',
        dataIndex: 'isIk',
        width:"5%",
        render:(text, record, index)=>{
          const fieldType = getFieldValue(`${pre}[${index}].fieldType`);
          const disabled = fieldType === '1';
          return <FormItem labelCol={{span:0}}>
            {getFieldDecorator(`${pre}[${index}].IsIk`, {
              initialValue: disabled ? '0': String(text),
            })(
              <Select disabled={isReading || disabled} style={{width: '100%'}}>
                <Option key="1">是</Option>
                <Option key="0">否</Option>
              </Select>
            )}
          </FormItem>
        }
      }, {
        title: '分词器',
        dataIndex: 'wordBreaker',
        width:"5%",
        render:(text, record, index)=>{
          const isIk = getFieldValue(`${pre}[${index}].IsIk`);
          const noWord = isIk == '0';
          return <FormItem labelCol={{span:0}}>
            {getFieldDecorator(`${pre}[${index}].wordBreaker`, {
              initialValue: noWord ? null : text,
            })(
              <Select disabled={isReading || noWord} style={{width: '100%'}}>
                <Option key="standard">standard</Option>
                <Option key="simple">simple</Option>
                <Option key="whitespace">whitespace</Option>
                <Option key="keyword">keyword</Option>
                <Option key="english">english</Option>
              </Select>
            )}
          </FormItem>
        }
      }, {
        title: '是否存储',
        dataIndex: 'isStore',
        width:"5%",
        render:(text, record, index)=>{
          return <FormItem labelCol={{span:0}}>
            {getFieldDecorator(`${pre}[${index}].IsStore`, {
              initialValue: String(text),
            })(
              <Select disabled={isReading} style={{width: '100%'}}>
                <Option key="1">是</Option>
                <Option key="0">否</Option>
              </Select>
            )}
          </FormItem>
        }
      }, {
        title: 'All Field',
        dataIndex: 'isAllField',
        width:"5%",
        render:(text, record, index)=>{
          return <FormItem labelCol={{span:0}}>
            {getFieldDecorator(`${pre}[${index}].IsAllField`, {
              initialValue: String(text),
            })(
              <Select disabled={isReading} style={{width: '100%'}}>
                <Option key="1">是</Option>
                <Option key="0">否</Option>
              </Select>
            )}
          </FormItem>
        }
      }, {
        title: 'Source Field',
        dataIndex: 'isSourceField',
        width:"3%",
        className: 'th-nowrap',
        render:(text, record, index)=>{
          return <FormItem labelCol={{span:0}}>
            {getFieldDecorator(`${pre}[${index}].IsSourceField`, {
              initialValue: String(text),
            })(
              <Select disabled={isReading} style={{width: '100%'}}>
                <Option key="1">是</Option>
                <Option key="0">否</Option>
              </Select>
            )}
          </FormItem>
        }
      }, {
        width: '1%',
        render:(text, record, index)=>{
          if (isReading) return null;
          return <FormItem labelCol={{span:0}} style={{ paddingLeft: 10 }}>
            <Popconfirm title="确定要删除该字段吗？" onConfirm={()=>{this.handleDeleteRow(paneIndex, index)}}>
              <a><Icon type="delete" className="op-icon"/></a>
            </Popconfirm>
          </FormItem>
        }
      },
    ];
  };

  formItemLayout1 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 18 },
  };

  formItemLayout2 = {
    labelCol: { span: 3 },
    wrapperCol: { span: 21 },
  };

  render() {
    const { metaES } = this.props;
    const { getFieldDecorator } = this.props.form;
    const { view, viewMode } = metaES;
    const isCreating = viewMode === 'new';
    const isEditing = viewMode === 'edit';
    const isReading = viewMode === 'read';
    const title = `索引定义-${isCreating ? '新建' : (isReading ? '查看' : '修改')}`;

    return <Modal
      title={title}
      visible={metaES.editorVisible}
      width="800"
      closable={false}
      footer={[
        <Button key="back" size="large" onClick={this.handleCancel.bind(this)}>取消</Button>,
        isReading ? <Button key="sure" type="primary" size="large" onClick={this.handleCancel.bind(this)}>确定</Button> : null,
        isCreating || (isEditing && view.status === 2) ? <Button key="draft" type="primary" size="large" onClick={this.handleSaveDraft.bind(this)} loading={this.props.submitLoading2}>保存为草稿</Button> : null,
        !isReading ? <Button key="next" type="primary" size="large" onClick={this.handleSubmit.bind(this)} loading={this.props.submitLoading}>发布</Button> : null,
      ]}
    >
      <Form>
        <section>
          <Row>
            <Col span="12">
              <FormItem label="索引编码" {...this.formItemLayout1}>
                {getFieldDecorator('indexCode', {
                  initialValue:view.indexCode,
                  rules:[
                    { required: true, message: '请输入索引编码' },
                    { pattern: /^(?!_)[0-9a-z_\u4e00-\u9fa5]+$/, message: '由中文、小写英文、数字、下划线组成，不能以下划线开头' },
                  ],
                })(
                  <Input disabled={!isCreating} maxLength="20" placeholder="请输入索引编码" />
                )}
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem label="版本号" {...this.formItemLayout1}>
                {getFieldDecorator('currentVersion', {
                  initialValue:view.currentVersion,
                  rules:[
                    { required: true, message: '请输入版本号' },
                    { pattern: /^[v|\d][a-z0-9.]*$/, message: '由英文v（小写）、数字、点组成，必须以英文v（小写）或数字开头' },
                  ],
                })(
                  <Input disabled={isReading} maxLength="10" placeholder="请输入版本号" />
                )}
              </FormItem>
            </Col>
          </Row>
          <FormItem label="说明" {...this.formItemLayout2} >
            {getFieldDecorator('description',{
              initialValue: view.description,
              rules:[{ required: true, message: '请输入说明' }]
            })(<TextArea disabled={isReading} placeholder="请输入说明" maxLength="200" style={{height:60}}/>)}
          </FormItem>
        </section>
        <Tabs
          onChange={this.handleChangeTab}
          activeKey={this.state.activeKey}
          type="editable-card"
          hideAdd={isReading}
          onEdit={this.handleEditTab}
        >
          {this.state.panes.map((pane, paneIndex) => (
            <TabPane tab={pane.title} key={pane.key} closable={!isReading && pane.closable}>
              <Row>
                <Col span="12">
                  <FormItem label="映射类别名称" labelCol={{span:8}} wrapperCol={{span:10}}>
                    {getFieldDecorator('tabs['+paneIndex+'].typeName',{
                      initialValue: pane.title,
                      rules:[
                        { required: true, message: '映射类别名称' },
                        { pattern: /^(?!_)[\u4e00-\u9fa5\w]+$/, message: '由中文、英文、数字、下划线组成，不能以下划线开头' },
                      ],
                    })(<Input disabled={isReading} maxLength="20"
                      onChange={(e)=>this.handleChangeTypeName(paneIndex, e.target.value)} placeholder="请输入映射类别名称" />)}
                  </FormItem>
                </Col>
                <Col span="12" style={{textAlign: 'right'}}>
                  {!isReading ? <Button onClick={() => this.handleAddRow(paneIndex)}>添加行</Button> : null}
                </Col>
              </Row>
              <Table
                rowKey="key"
                columns={this.makeColumns(paneIndex)}
                dataSource={pane.data}
                pagination={false}
                className="editor-table"
              />
            </TabPane>
          ))}
        </Tabs>
      </Form>
    </Modal>
  }
}

export default connect(({ metaES, system, account }) => ({
  metaES,
  system,
  account,
}))(Form.create()(Editor));
