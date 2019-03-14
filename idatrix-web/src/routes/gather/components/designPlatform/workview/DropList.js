/**
 * Created by Administrator on 2017/7/1.
 */
import React, { Component } from "react";
import { connect } from "dva";
import { Dropdown, Menu, Row, Col, Spin, TreeSelect } from "antd";
import Style from "./DropList.css";

class DropList extends Component {
  constructor() {
    super();
  }

  componentDidMount(){
    const {getList} = this.props;
    getList();
  }

  render() {
    const { getList, taskList, handleClick } = this.props;

    const onChange = str => {
      handleClick(str);
    };

    const treeData = Object.entries(taskList).map(val => ({
      title: val[0],
      value: val[0],
      key: val[0],
      children: val[1].map(v => {
        const key = JSON.stringify({ name: v, owner: val[0] });
        return {
          title: v,
          key: key,
          value: key
        };
      })
    }));

    const SelectTask = () => (
      <TreeSelect
        style={{ width: 100 }}
        dropdownStyle={{ maxHeight: 400, overflow: "auto" }}
        treeData={treeData}
        placeholder="选择任务"
        treeDefaultExpandAll
        onChange={onChange}
      />
    );

    return (
      <div>
        <Row>
          <Col span={16} className={Style.detailtask}>
            <SelectTask />
          </Col>
        </Row>
      </div>
    );
  }
}

export default connect()(DropList);
