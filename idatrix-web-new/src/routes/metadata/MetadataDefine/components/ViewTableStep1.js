/**
 * 元数据定义 查看窗口 第一步
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Input, Radio, Row, Col, TreeSelect, Select, Button } from 'antd';
import { dateFormat } from 'utils/utils';
import { getLabelByTreeValue } from 'utils/metadataTools';
import Style from '../style.css';
import Modal from 'components/Modal';

class View extends React.Component {

  // componentWillMount(){
  //   const { dispatch } = this.props;
  //   dispatch({ type: 'metadataCommon/getSourceTable' });
  //   dispatch({ type: 'metadataCommon/getUsers' });
  //   dispatch({ type: 'metadataCommon/getDepartments' });
  //   dispatch({ type: 'metadataCommon/getStoreDatabase' });
  //   dispatch({ type: 'metadataCommon/getAllResource' });
  // }

  // 点击查看表字段
  handleSubmit() {
    const { dispatch } = this.props;
    const { view } = this.props.metaDataDefine;
    dispatch({ type: 'metaDataDefine/showView', step: 2});
    dispatch({ type: 'metaDataDefine/getFieldsById', id: view.metaid});
  }

  handleCancel() {
    const { dispatch } = this.props;
    dispatch({ type: 'metaDataDefine/hideAllView' });
  }

  render() {
    const { metaDataDefine, metadataCommon } = this.props;
    const { sourceTableOptions, departmentsOptions, departmentsTree, storeDatabaseOptions, usersOptions, industryOptions, themeOptions, tagsOptions } = metadataCommon;
    const { view, viewMode } = metaDataDefine;

    return <Modal
      title="数据表类元数据基本信息"
      visible={metaDataDefine.viewStep1Visible}
      onOk={this.handleSubmit.bind(this)}
      onCancel={this.handleCancel.bind(this)}
      maskClosable={false}
      okText="查看表字段"
      width={850}
    >
      <div className={Style.view}>
        版本标识：{view.version}
        <table style={{width: '100%'}}>
          <tbody>
            {/*<tr>
              <td>元数据表类型：</td>
              <td>{
                (()=>{
                  switch(view.metaType){
                    case "1":
                      return "事实表"
                    case "2":
                      return "聚合表"
                    case "3":
                      return "查找表"
                    case "4":
                      return "维度表"
                    case "5":
                      return "宽表"
                    case "6":
                      return "基础数据表"
                    default:
                      return ""
                  }
                })()
              }</td>
            </tr>*/}
            {/*view.metaType=="1"?<tr>
              <td>前置机数据来源表：</td>
              <td>{(() => {
                const s = view.sourceTable;
                const foundDb = sourceTableOptions.find(db => s && s[0] && db.value == s[0]);
                const foundTable = (foundDb && foundDb.children || []).find(t => s && s[1] && t.value == s[1]);
                let str = foundDb ? foundDb.label : '';
                str += foundTable ? ` / ${foundTable.label}` : '';
                return str;
              })()}</td>
            </tr>:null */}
            <tr>
              <td>数据所属组织：</td>
              <td>{getLabelByTreeValue(view.dept, departmentsOptions)}</td>
            </tr>
            <tr>
              <td>数据库类型：</td>
              <td>{({
                '2': 'Oracle',
                '3': 'MySQL',
                '4': 'Hive',
                '5': 'Hbase',
                '14':'DM',
                '8':"PostgreSql"
              })[view.dsType]}</td>
            </tr>
            <tr>
              <td>存储的数据库：</td>
              <td>{view.dataSource && view.dataSource.dbDatabasename}</td>
            </tr>
            <tr>
              <td>组织外公开等级：</td>
              <td>{({
                  '0': '公开',
                  '1': '授权公开',
                  '2': '不公开',
                })[view.publicStats]}</td>
            </tr>
            <tr>
              <td>表拥有者：</td>
              <td>{view.owner}</td>
            </tr>
            <tr>
              <td>表创建者：</td>
              <td>{view.creator}</td>
            </tr>
            <tr>
              <td>表中文名称：</td>
              <td>{view.metaNameCn}</td>
            </tr>
            <tr>
              <td>表英文名称：</td>
              <td>{view.metaNameEn}</td>
            </tr>
            <tr>
              <td>行业：</td>
              <td>{getLabelByTreeValue(view.industry, industryOptions)}</td>
            </tr>
            {/*<tr>
              <td>主题：</td>
              <td>{getLabelByTreeValue(view.theme, themeOptions)}</td>
            </tr>*/}
            <tr>
              <td>标签：</td>
              <td>{getLabelByTreeValue(view.tag, tagsOptions)}</td>
            </tr>
            <tr>
              <td>备注：</td>
              <td>{view.remark}</td>
            </tr>
          </tbody>
        </table>
      </div>

    </Modal>
  }
}

export default connect(({ metaDataDefine, metadataCommon }) => ({
  metaDataDefine,
  metadataCommon,
}))(View);
