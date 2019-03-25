/**
 * Modal组件二次封装
 * 使Modal组件支持拖放
 *
 * 注：maskClosable已被默认置为false，如需要开启，设置为true即可
 *
 * 新增属性：
 * @prop  {boolean} draggable   设置是否可拖放，默认为true
 */
import React from 'react';
import { Modal, Button } from 'antd';
import PropTypes from 'prop-types';

let startX = 0;
let startY = 0;

class NewModal extends React.Component {
  static propTypes = {
    draggable: PropTypes.bool,
  };

  static info = Modal.info;
  static success = Modal.success;
  static error = Modal.error;
  static warning = Modal.warning;
  static confirm = Modal.confirm;

  state = {
    draggable: true,
  };

  componentWillMount() {
    const draggable = this.props.draggable !== false;
    this.setState({
      draggable,
      x: 0,
      y: 0,
      originX: 0,
      originY: 0,
    });
  }

  componentWillReceiveProps(nextProps) {
    const draggable = nextProps.draggable !== false;
    this.setState({
      draggable,
    });
  }

  onDragStart = (e) => {
    const { pageX, pageY } = e;
    startX = pageX;
    startY = pageY;
  };

  onDrag = (e) => {
    const { pageX, pageY } = e;
    const { originX, originY } = this.state;
    if (pageX > 0 && pageY > 0) {
      this.setState({
        x: originX + (pageX - startX),
        y: originY + (pageY - startY),
      });
    }
  };

  onDragEnd = () => {
    const { x, y } = this.state;
    this.setState({
      originX: x,
      originY: y,
    });
  }

  render() {
    const { x, y } = this.state;
    const headStyle = {
      cursor: this.state.draggable ? 'move' : 'default',
    };

    const bodyStyle = {
      ...this.props.style,
      transform: `translate(${x}px,${y}px)`,
    };

    const Title = (<div
      style={headStyle}
      onDragStart={this.onDragStart}
      onDrag={this.onDrag}
      onDragEnd={this.onDragEnd}
      draggable
    >
      {this.props.title}
    </div>);

    const footer = typeof this.props.footer !== 'undefined' ? this.props.footer : [
      <Button
        key="1"
        size="large"
        onClick={this.props.onCancel}
        disabled={this.props.confirmLoading}
      >{this.props.cancelText || '取消'}</Button>,
      <Button
        key="2"
        type={this.props.okType || 'primary'}
        size="large"
        onClick={this.props.onOk}
        loading={this.props.confirmLoading}
      >{this.props.okText || '确定'}</Button>,
    ];

    return (<Modal
      {...this.props}
      title={Title}
      style={bodyStyle}
      maskClosable={this.props.maskClosable === true}
      footer={footer}
    />);
  }
}

export default NewModal;
