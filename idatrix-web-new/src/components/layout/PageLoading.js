/**
 * 页面加载进度条
 */
import React from 'react';
import { connect } from 'dva';
import { Progress } from 'antd';

let timer = null; // 记时器

class PageLoading extends React.Component {

  state = {
    percent: 10,
    status: 'active',
    visible: true,
  }

  componentWillMount() {
    clearTimeout(timer);
    timer = null;
  }

  componentWillReceiveProps(nextProps) {
    const { pageLoading } = nextProps.system;
    const { percent } = this.state;
    if (!pageLoading) {
      clearTimeout(timer);
      this.setState({
        percent: 100,
        status: 'success',
      }, () => {
        timer = setTimeout(() => {
          this.setState({ visible: false, percent: 0 });
          timer = null;
        }, 600);
      });
    } else if (timer === null) {
      this.setState({
        percent: 10,
        status: 'active',
        visible: true,
      }, () => {
        timer = setTimeout(() => {
          if (pageLoading && percent < 80) {
            this.setState({ percent: 80 });
          }
          timer = setTimeout(() => {
            if (pageLoading && percent < 85) {
              this.setState({ percent: 85 });
            }
            timer = setTimeout(() => {
              if (pageLoading && percent < 88) {
                this.setState({ percent: 88 });
              }
            }, 7000);
          }, 3000);
        }, 600);
        // 加载超时
        setTimeout(() => {
          if (pageLoading && timer !== null) {
            clearTimeout(timer);
            timer = null;
            // this.setState({ percent: 100, status: 'exception' });
          }
        }, 30e3);
      });
    }
  }

  componentWillUnmount() {
    clearTimeout(timer);
    timer = null;
  }

  render() {
    const { percent, status, visible } = this.state;
    const style = {
      lineHeight: 0,
      visibility: visible ? 'visible' : 'hidden',
      ...this.props.style,
    };
    return (visible ? (<Progress
      percent={percent}
      status={status}
      strokeWidth={3}
      showInfo={false}
      style={style}
    />) : null);
  }
}

export default connect(({ system }) => ({
  system,
}))(PageLoading);
