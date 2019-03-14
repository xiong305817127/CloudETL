// 文件目录预览组件
import React from 'react';
import { Icon } from 'antd';
import PropTypes from 'prop-types';
import Modal from 'components/Modal';
import TableList from 'components/TableList';
import { downloadFile } from 'utils/utils';
import { API_BASE_METADATA } from 'constants';
import filesize from 'filesize';

/**
 * const download = (record) => {
  console.log(record.fileName,"fileName----",record.fileLen,"-----",record.filePath);
  downloadFile(`${API_BASE_METADATA}/metadataFile/download?path=${fileName}`);
};
 */
const download=(record) =>{
    downloadFile(`${API_BASE_METADATA}/metadataFile/download`, 'POST', {
      fileName: record.fileName,
      filePath:record.filePath,
      fileLen:record.fileLen,
    });
    
  }
 
const columns = [
  {
    title: '名称',
    dataIndex: 'fileName',
  }, {
    title: '修改日期',
    dataIndex: 'modifiedTime',
  }, {
    title: '大小',
    dataIndex: 'fileLen',
    render: text => filesize(text),
  }, {
    title: '下载',
    dataIndex: 'filePath',
    render: (text, record) =>
   <Icon onClick={() => download(record)} type="download" className="op-icon" style={{ cursor: 'pointer' }} />
  },
];

class ViewDir extends React.Component {

  render() {
    return (<Modal
      title="文件目录窗口"
      visible={this.props.visible}
      onOk={this.props.onClose}
      onCancel={this.props.onClose}
      width={650}
    >
      <TableList
        rowKey="fileid"
        columns={columns}
        dataSource={this.props.data}
        pagination={false}
      />
    </Modal>);
  }
}

ViewDir.propTypes = {
  data: PropTypes.array.isRequired,
  visible: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default ViewDir;
