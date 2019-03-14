/**
 * 文件目录类编辑器
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Input, Radio, Row, Col, TreeSelect, Select, Button, message } from 'antd';
import { dateFormat, safeJsonParse } from 'utils/utils';
import Cascader from 'components/Cascader';
import Modal from 'components/Modal';
import { submitDecorator } from 'utils/decorator';
import { batchInsertFile, batchUpdateFile, metaFileIsExists } from 'services/metadataDefine';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const { TextArea } = Input;

@submitDecorator
class Editor extends React.Component {
  componentWillMount(){
    const { dispatch } = this.props;
    dispatch({ type: 'metadataCommon/getDepartments' });
    dispatch({ type: 'metadataCommon/getUsers' });
    dispatch({ type: 'metadataCommon/getHdfsTree' });
    dispatch({ type: 'metadataCommon/getAllResource' });
  }

  // 点击确定
  handleSubmit() {
    const { account, dispatch } = this.props;
    const { view, viewMode } = this.props.metaFileDefine;
    const isCreating = viewMode === 'new';
    this.props.form.validateFields(async (err, values) => {
      if (err) return;
      this.props.disableSubmit();
      values.renterId = account.renterId;
      if (isCreating) { // 新建
        console.log(values,"新建");
        const { data } = await batchInsertFile({ userId: account.id }, [{...values,dept: typeof values.dept === "string"? [values.dept]: values.dept}]);
        if (data && data.code === '200') {
          message.success('新建成功');
          dispatch({ type: 'metaFileDefine/hideEditor' });
          dispatch({ type: 'resourcesCommon/getHdfsTree' ,force:true});
          dispatch({ type: 'metaFileDefine/getList', payload: this.props.system.query });
          this.props.form.resetFields();
        }
      } else { // 修改
        values.fileid = view.fileid;
        const { data } = await batchUpdateFile({ userId: account.id }, [{...values,dept: typeof values.dept === "string"? [values.dept]: values.dept}]);
        if (data && data.code === '200') {
          message.success('修改成功');
					dispatch({ type: 'metaFileDefine/hideEditor' });
					dispatch({ type: 'resourcesCommon/getHdfsTree',force:true });
					dispatch({ type: 'metaFileDefine/getList', payload: this.props.system.query });
					
          this.props.form.resetFields();
        }
      }
      this.props.enableSubmit();
    });
  }

  handleCancel() {
    const { dispatch } = this.props;
    dispatch({ type: 'metaFileDefine/hideEditor' });
    this.props.form.resetFields();
  }

  // 检查表名是否冲突
  checkStorDir = async (rule, value, callback) => {
    const { view, viewMode } = this.props.metaFileDefine;
    const isCreating = viewMode === 'new';
    if (isCreating) {
      const { data } = await metaFileIsExists({
        storDir: value,
      });
      if (data && data.data === true) {
        callback('该HDFS路径已有对应文件目录，请重新选择');
        return;
      }
    }
    callback();
  };

  formItemLayout1 = {
    labelCol: { span: 6},
    wrapperCol: { span: 18 },
  };

  render() {
    const { metaFileDefine, metadataCommon } = this.props;
    const { getFieldDecorator } = this.props.form;
    const { departmentsTree, hdfsTree, hdfsPlanList, usersOptions, industryOptions, themeOptions, tagsOptions } = this.props.metadataCommon;
    const { view, viewMode } = metaFileDefine;
    const isCreating = viewMode === 'new';
    const title = `文件类元数据-${isCreating ? '新建' : '修改'}`;
    // 部门信息兼容处理
    let dept = Array.isArray(view.dept) ? view.dept : safeJsonParse(view.dept) || this.props.account.deptId && [String(this.props.account.deptId)];
    dept = Array.isArray(dept) ? dept : dept && [String(dept)];
    if (!departmentsTree) dept = null;

    return <Modal
      title={title}
      visible={metaFileDefine.editorVisible}
      onOk={this.handleSubmit.bind(this)}
      onCancel={this.handleCancel.bind(this)}
      maskClosable={false}
      closable={false}
      confirmLoading={this.props.submitLoading}
    >
      <Form>
        <FormItem  style={{marginBottom:'10px'}}  label="文件目录描述： " {...this.formItemLayout1}>
          {getFieldDecorator('dirName', {
            initialValue:view.dirName,
            rules:[{required:true,message:"请输入文件目录描述"}]
          })(
            <Input />
          )}
        </FormItem>
        <FormItem  style={{marginBottom:'10px'}}  label="对应的HDFS路径： " {...this.formItemLayout1}>
          {getFieldDecorator('storDir', {
            initialValue: safeJsonParse(view.storDir) || view.storDir,
            rules: [
              { required:true, message: '请选择HDFS路径'},
              { validator: this.checkStorDir },
            ]
          })(
            <Cascader options={hdfsTree} placeholder="请选择" changeOnSelect />
          )}
        </FormItem>
        <FormItem  style={{marginBottom:'10px'}}  label="选择组织： " {...this.formItemLayout1}>
          {getFieldDecorator('dept', {
            initialValue: dept,
          })(
            <TreeSelect
              placeholder="请选择组织"
              treeData={departmentsTree}
              treeDefaultExpandAll
              onChange={this.changeDepartment}
              dropdownStyle={{height: 300}}
              allowClear
            />
          )}
        </FormItem>
        <FormItem required  style={{marginBottom:'10px'}}  label="组织外公开等级： " {...this.formItemLayout1}>
          {getFieldDecorator('publicStats', {
            initialValue: view.publicStats? parseInt(view.publicStats) : 1,
          })(
            <RadioGroup onChange={this.onChange}>
              {/*<Radio value={0}>公开</Radio>*/}
              <Radio value={1}>授权公开</Radio>
              <Radio value={2}>不公开</Radio>
            </RadioGroup>
          )}
        </FormItem>
        <FormItem  style={{marginBottom:'10px'}}  label="拥有者： " {...this.formItemLayout1}>
          {getFieldDecorator('owner', {
            initialValue: view.owner,
            rules: [
              { required: true, message: "请选择表拥有者" },
            ]
          })(
            <Select placeholder={"选择拥有者"}>
              {usersOptions.map(item => {
                return <Option key={item.value} value={item.label}>{item.label}</Option>
              })}
            </Select>
          )}
        </FormItem>
        <FormItem  style={{marginBottom:'10px'}} label="行业： " {...this.formItemLayout1}>
          {getFieldDecorator('industry', {
            initialValue:view.industry,
          })(
            <Select  placeholder="选择行业">
              {industryOptions.map(item => {
                return <Option key={item.value} value={item.value}>{item.label}</Option>
              })}
            </Select>
          )}
        </FormItem>
        <FormItem  label=" 主题：" {...this.formItemLayout1} style={{marginBottom:"8px"}} >
          {getFieldDecorator('theme',{
            initialValue:view.theme,
          })(
            <Select placeholder="选择主题">
              {themeOptions.map(item => {
                return <Option key={item.value} value={item.value}>{item.label}</Option>
              })}
            </Select>
          )}
        </FormItem>
        <FormItem  label=" 标签：" {...this.formItemLayout1} style={{marginBottom:"8px"}} >
          {getFieldDecorator('tag',{
            initialValue:view.tag,
          })(
            <Select placeholder="选择标签">
              {tagsOptions.map(item => {
                return <Option key={item.value} value={item.value}>{item.label}</Option>
              })}
            </Select>
          )}
        </FormItem>
        <FormItem  label="备注：" {...this.formItemLayout1} style={{marginBottom:"8px"}} >
          {getFieldDecorator('remark',{
            initialValue: view.remark,
          })(<TextArea  placeholder="请输入备注" maxLength="200" style={{height:60}}/>)}
        </FormItem>
      </Form>
    </Modal>
  }
}

export default connect(({ metaFileDefine, metadataCommon, system, account }) => ({
  metaFileDefine,
  metadataCommon,
  system,
  account,
}))(Form.create()(Editor));
