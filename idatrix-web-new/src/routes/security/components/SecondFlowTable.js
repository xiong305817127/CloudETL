import React from "react";
import { Checkbox, Button } from "antd";
import { connect } from "dva";

import Style from "./FlowTable.css";

const CheckboxGroup = Checkbox.Group;

class NextFlowTable extends React.Component {
  state = {
    checkedList: []
  };

  componentWillMount() {
    const { openedService } = this.props.data;
    this.setState({
      checkedList: (openedService || "").split(",")
    });
  }

  componentWillReceiveProps(nextProps) {
    const { openedService } = nextProps.data;
    this.setState({
      checkedList: (openedService || "").split(",")
    });
  }

  onChange = checkedList => {
    this.setState({
      checkedList
    });
  };

  next = () => {
    this.props.onNext({
      openedService: this.state.checkedList.join(",")
    });
  };

  render() {
    const { serviceProperties } = this.props.tenantManage;
    const options = Object.keys(serviceProperties).map(key => ({
      label: key,
      value: key
    }));
    const { readonly } = this.props;

    return (
      <div id="LastFlowTable">
        <CheckboxGroup
          className={Style["checkbox-wrap"]}
          options={options}
          defaultValue={this.state.checkedList}
          onChange={this.onChange}
        />
        <div style={{ marginTop: 50, textAlign: "center" }}>
          <Button onClick={this.props.onPrev}>上一步</Button>
          <Button
            style={{ marginLeft: 8 }}
            type="primary"
            onClick={this.next.bind(this)}
          >
            下一步
          </Button>
        </div>
      </div>
    );
  }
}

export default connect(({ tenantManage }) => ({
  tenantManage
}))(NextFlowTable);
