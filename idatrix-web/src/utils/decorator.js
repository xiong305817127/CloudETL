/**
 * 定义各种组件装饰器
 */
import React from 'react';
import { DEFAULT_SUBMIT_DURATION } from 'constants';

/**
 * 提交表单修饰器
 * 将会向组件注入以下属性
 * @property {boolean}  submitLoading 当前submit按钮是否禁用（即正在向后端提交表单）
 * @property {function} disableSubmit 先禁用submit按钮，超时后自动启用，一般用于ajaxBefore
 * @property {function} enableSubmit  立即使submit按钮可用，一般用于ajaxSuccess
 */
export const submitDecorator = Component => class extends React.Component {
  state = {
    submitLoading: false,
  };

  /**
   * 先禁用submit按钮，超时后自动启用
   * @param  {String} propName 将向组件注入的loading状态名称，默认submitLoading
   */
  disableSubmit(propName = 'submitLoading') {
    this.setState({ [propName]: true });
    setTimeout(() => {
      this.setState({ [propName]: false });
    }, DEFAULT_SUBMIT_DURATION);
  }

  /**
   * 立即使submit按钮可用
   * @param  {String} propName 将向组件注入的loading状态名称，默认submitLoading
   */
  enableSubmit(propName = 'submitLoading') {
    this.setState({ [propName]: false });
  }

  render() {
    return (
      <Component
        {...this.props}
        {...this.state}
        disableSubmit={this.disableSubmit.bind(this)}
        enableSubmit={this.enableSubmit.bind(this)}
      />
    );
  }
};
