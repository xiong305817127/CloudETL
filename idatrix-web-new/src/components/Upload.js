/**
 * Upload二次封闭
 * 添加SSO会话信息
 */
import React from 'react';
import { Upload } from 'antd';
import { connect } from 'dva';

class NewUpload extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentWillMount() {
    this.updateStateByProps(this.props);
  }

  componentWillReceiveProps(nextProps) {
    this.updateStateByProps(nextProps);
  }

  updateStateByProps(props) {
    const newProps = { ...props };
    // 添加SSO会话信息
    newProps.headers = { ...props.headers, VT: props.account.vt };
    this.setState({ ...newProps });
  }

  render() {
    return <Upload {...this.state} />
  }
}

export default connect(({ account }) => ({
  account,
}))(NewUpload);
