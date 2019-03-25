import React from 'react';
import { Checkbox, Button, Row, Col } from 'antd';
import { connect } from 'dva';
import { DEFAULT_SUBMIT_DURATION } from 'constants';

import Style from './FlowTable.css'

const CheckboxGroup = Checkbox.Group;

class LastFlowTable extends React.Component{
  state = {
    checkedList: [],
    confirmLoading: false,
  };

  componentWillMount() {
    const { openedResource } = this.props.data;
    this.setState({
      checkedList: openedResource.split(',').map(item => item),
    });
  }

  componentWillReceiveProps(nextProps) {
    const { openedResource } = nextProps.data;
    this.setState({
      checkedList: openedResource.split(',').map(item => item),
    });
  }

  onChange = (checkedList) => {
    this.setState({
      checkedList,
    });
  };

  next = () => {
    this.setState({ confirmLoading: true })
    this.props.onNext({
      openedResource: this.state.checkedList.join(',')
    }, true);
    setTimeout(() => {
      try {
        this.setState({ confirmLoading: false })
      } catch (err) {}
    }, DEFAULT_SUBMIT_DURATION);
  }

  render() {
    const { readonly } = this.props;
    const { resourcesList } = this.props.tenantManage;
    const options = resourcesList.map(item => ({
      label: item.name,
      value: item.clientSystemId,
      disabled: item.clientSystemId === 'security' ? true : false,
    }));

    return (
      <div id="LastFlowTable">
        <CheckboxGroup
          className={Style['checkbox-wrap']}
          defaultValue={this.state.checkedList}
          onChange={this.onChange}
        >
          <Row>
            {options.map((item, index) => {
              return <Col key={index} span={6} style={{marginBottom: 20}}><Checkbox value={item.value} disabled={item.disabled}>{item.label}</Checkbox></Col>
            })}
          </Row>
        </CheckboxGroup>
        <div style={{marginTop: 50, textAlign: 'center'}}>
          <Button onClick={this.props.onPrev}>上一步</Button>
          <Button style={{marginLeft: 8}} type="primary" onClick={this.next.bind(this)} loading={this.state.confirmLoading}>完成</Button>
        </div>
      </div>
    )
  }
}

export default connect(({ tenantManage }) => ({
  tenantManage,
}))(LastFlowTable);
