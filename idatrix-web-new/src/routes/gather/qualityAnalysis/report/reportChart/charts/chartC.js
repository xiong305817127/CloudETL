import React from "react";
import ReactEcharts from "echarts-for-react";
import { Row, Col, Table, Progress } from "antd";
import configs from "./configs";

export default ({ FullData }) => {
  // 表格信息
  const columns = [
    {
      title: "参考值字段",
      dataIndex: "referenceValue",
      key: "referenceValue",
      render: text => {
        return (
          <span
            className={
              !configs.translateSets[text]
                ? configs.translateSetsError[text]
                  ? "f_bold f_red"
                  : "f_bold"
                : ""
            }
          >
            {configs.translateSets[text] ||
              configs.translateSetsError[text] ||
              text}
          </span>
        );
      }
    },
    {
      title: "参考值适用个数",
      dataIndex: "number",
      key: "number",
      sorter: (a, b) => a.number - b.number
    },
    {
      title: "参考值占比",
      dataIndex: "newNumber",
      render: (text, record, index) => {
        return Math.floor((text / record.total) * 100);
      },
      sorter: (a, b) => a.newNumber - b.newNumber
    },
    {
      title: "是否匹配",
      dataIndex: "match",
      key: "match",
      render: text => {
        return (
          <span className={!text ? "f_bold f_red" : ""}>
            {text ? "匹配" : "不匹配"}
          </span>
        );
      }
    },
    {
      title: "占比(当前匹配个数/总匹配次数)",
      dataIndex: "",
      render: (text, record, index) => {
        return (
          <Progress
            percent={(record.number / record.total) * 100}
            showInfo={false}
          />
        );
      }
    }
  ];

  // 获取option
  const getOption = FullData => ({
        title: {
            text: '世界人口总量',
            subtext: '数据来自网络'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['2011年', '2012年']
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01]
        },
        yAxis: {
            type: 'category',
            data: ['巴西','印尼','美国','印度','中国','世界人口(万)']
        },
        series: [
            {
                name: '2011年',
                type: 'bar',
                data: [18203, 23489, 29034, 104970, 131744, 630230]
            },
            {
                name: '2012年',
                type: 'bar',
                data: [19325, 23438, 31000, 121594, 134141, 681807]
            }
        ]
  });

  // 将FullData转化为echart配置信息
  const options = getOption(FullData);

  return (
    <div>
      <Row gutter={32}>
        {/* 处理饼图 */}
        <Col span={12}>
          <ReactEcharts option={options} style={{ height: 300 }} />
        </Col>

        {/* 处理表格 */}
        <Col span={12} style={{maxHeight:"600px",overflowY:"scroll"}}>
          {/* Tip：后续表格数据量很大时，如何优化，可以采用两种方式： */}
          {/* 1. 采用react-window，此组件可以对数据量较大的进行处理，处理速度极快 */}
          {/* 2. 采用分页。实际解决方法按照产品需求来定。*/}
          <Table
            columns={columns}
            dataSource={FullData}
            rowKey={"referenceValue"}
            // 控制分页
            pagination={false}
          />
        </Col>
      </Row>
    </div>
  );
};
