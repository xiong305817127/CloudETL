/**
 * 元数据定义 文件目录查看窗口
 */
import React from 'react';
import { connect } from 'dva';
import { Tooltip} from 'antd';
import { dateFormat } from 'utils/utils';
import { getLabelByTreeValue } from 'utils/metadataTools';
import Style from '../style.css';
import Styles from '../style.less';
import Modal from 'components/Modal';

class View extends React.Component {

  componentWillMount(){
    const { dispatch } = this.props;
    dispatch({ type: 'metadataCommon/getDepartments' });
    dispatch({ type: 'metadataCommon/getHdfsTree' });
    dispatch({ type: 'metadataCommon/getAllResource' });
  }

  handleCancel() {
    const { dispatch } = this.props;
    dispatch({ type: 'metaFileDefine/hideView' });
  }

  render() {
    const { metaFileDefine, metadataCommon } = this.props;
    const { departmentsTree, departmentsOptions, hdfsTree, hdfsPlanList, usersOptions, industryOptions, themeOptions, tagsOptions } = this.props.metadataCommon;
    const { view, viewMode } = metaFileDefine;

    return <Modal
      title="文件类元数据基本信息"
      visible={metaFileDefine.viewVisible}
      onOk={this.handleCancel.bind(this)}
      onCancel={this.handleCancel.bind(this)}
      maskClosable={false}
    >
      <div className={Style.view}>
        <table style={{width: '100%'}}>
          <tbody className={Styles.tbody}>
            <tr>
              <td>文件目录描述：</td>
              <td>{view.dirName}</td>
            </tr>
            <tr>
              <td>对应hdfs路径：</td>
              <td>{(()=>{
                const result = [];
                const text = view.storDir || [];
                try {
                  const arr = typeof text === 'string' ? JSON.parse(text) : text;
                  arr.forEach(id => {
                    const found = hdfsPlanList.find(it => it.value == id);
                    if (found) result.push(found.label);
                  });
                } catch (err) {}
                return <Tooltip title={result.join('/')}><span>{result.join('/')}</span></Tooltip>
              })()}</td>
            </tr>
            <tr>
              <td>组织：</td>
              <td>{getLabelByTreeValue(view.dept, departmentsOptions)}</td>
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
              <td>拥有者：</td>
              <td>{view.owner}</td>
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

export default connect(({ metaFileDefine, metadataCommon }) => ({
  metaFileDefine,
  metadataCommon,
}))(View);
