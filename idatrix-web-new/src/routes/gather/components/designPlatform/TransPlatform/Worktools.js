import React from "react";
import { Menu, Row, Col,AutoComplete } from "antd";
import { connect } from "dva";
import Style from "./Worktools.css";
import ToolsItem from "../../common/ToolsItem";
import Tools from "../../config/Tools";
import Empower from "../../../../../components/Empower";
import StyleS from "../../common/ToolsTitle.css";

let Timer = null;

const Worktools = ({ worktools, dispatch }) => {
  const { model1 } = worktools;

  const handleChange = value => {
    if (Timer) {
      clearTimeout(Timer);
    }
    Timer = setTimeout(() => {
      dispatch({
        type: "worktools/changeModel",
        payload: {
          model1: value
        }
      });
    }, 200);
  };

  const config = new Map([
    ["输入", "0"],
    ["输出", "1"],
    ["大数据", "2"],
    ["脚本", "3"],
    ["转换", "4"],
    ["批量加载", "5"],
    ["作业", "9"],
    ["查询", "6"],
    ["连接", "7"],
    ["流程", "8"],
    ["统计", "10"],
    ["检验", "11"],
    ["流", "13"],
    /* ['其他','other']*/
  ]);

  const other = new Map([["流程", "7"], ["检验", "8"], ["批量加载", "5"]]);

  const getArgs = () => {
    let args = Object.keys(Tools);
    let args1 = [];
    if (model1 && model1.trim()) {
      if (config.has(model1)) {
        args1 = args.filter(index => {
          if (
            Tools[index].typeFeil === "trans" &&
            config.get(model1) != "other" &&
            Tools[index].model == config.get(model1)
          ) {
            return true;
          } else if (config.get(model1) === "other") {
            if ([...other.values()].includes(Tools[index].model)) {
              return true;
            }
          } else {
            return false;
          }
        });
      } else {
        let value = model1.toLowerCase();
        args1 = args.filter(index => {
          if (
            Tools[index].typeFeil === "trans" &&
            (Tools[index].type.toLowerCase().indexOf(value) >= 0 ||
              Tools[index].text.toLowerCase().indexOf(value) >= 0 ||
              Tools[index].name.toLowerCase().indexOf(value) >= 0)
          ) {
            return true;
          } else {
            return false;
          }
        });
      }
    } else {
      args1 = args.filter(index => {
        if (Tools[index].typeFeil === "trans" && Tools[index].model == "0") {
          return true;
        } else {
          return false;
        }
      });
    }
 
    return args1.filter(index=>[...config.values()].includes(Tools[index].model));
  };

  return (
    <div
      id="worktools"
    >
      <Row className={Style.headercolor}>
        <Col span={8} className={StyleS.control}>
          &nbsp;
        </Col>
        <Col span={14}>控件</Col>
      </Row>
      <Row className={Style.resselect}>
        <Col span={8} className={Style.type}>
          类型
        </Col>
        <Col span={14}>
          <AutoComplete
            dataSource={[...config.keys()]}
            placeholder="请搜索或选择"
            onChange={handleChange}
            value={model1}
          />
          {/* <Select
            mode="combobox"
            placeholder="请搜索或选择"
            defaultActiveFirstOption={false}
            showArrow={false}
            filterOption={false}
            value={model1}
            style={{ width: 130 }}
            onChange={handleChange}
          >
            {[...config.keys()].map(index => {
              return (
                <Option value={index} key={index}>
                  {index}
                </Option>
              );
            })}
          </Select> */}
        </Col>
      </Row>
      <Menu
        className={Style.menuStyle}
        mode="inline"
        width={215}
        inlineIndent="0"
      >
        {getArgs().map(index => {
          console.log(index)
          return (
            <Empower
              key={index}
              api={Tools[index].empowerApi}
              disable-type="hide"
            >
              <li className="liStyle">
                <ToolsItem type={index} />
              </li>
            </Empower>
          );
        })}
      </Menu>
    </div>
  );
};

export default connect(({ worktools }) => ({
  worktools
}))(Worktools);
