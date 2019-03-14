
import React from "react";
import { connect } from "dva";
import { Card,Tooltip,Icon,Button } from "antd";
import { hashHistory } from "dva/router";

// 引入immutable component，用于隔离mapstatetoprops的toJS转化
// 防止toJS引入新的对象导致重复渲染
import ImmComponent from "components/utils/toJS";
import TableList from "components/TableList";

//路由跳转
const buttonForReport = ({ target }) => {
    hashHistory.push(target.getAttribute("data-url"))
}

// column 
const columns = [{
    dataIndex: 'execId',
    key: 'execId',
    title: '执行ID',
    width: "300px"
}, {
    title: '开始时间',
    dataIndex: 'beginStr',
    key: 'beginStr',
    width: "25%"
}, {
    title: '结束时间',
    dataIndex: 'endStr',
    key: 'endStr',
    width: "25%"
},
{
    title: '操作',
    dataIndex: 'view',
    key: 'view',
    render: (text, record) => {
        return (
            <Tooltip placement="top" title="查看分析报告">
                <Icon style={{ fontSize: "16px", cursor: "pointer", marginLeft: "10px" }} data-url={`/gather/qualityAnalysis/report/reportChart?execId=${record.execId}`} onClick={buttonForReport} type="bar-chart" />
            </Tooltip>
        );
    }
}];

  //点击取消返回
  const goBack=()=>{
    hashHistory.goBack();
  }

const ReportComponent = ({ analysisList, name }) => {

    console.log(analysisList, "分析列表");

    return (
        <div className="padding_16">
         <Button type="primary" onClick={goBack}>返回</Button>
            <Card title={name} >
                {
                    // 检测类型
                    Object.prototype.toString.call(analysisList) === "[object Array]"
                    &&
                    (
                        <TableList
                            columns={columns}
                            dataSource={analysisList}

                            // 使用nodid作为唯一标识
                            rowKey={"execId"}

                        />
                    )
                }
            </Card>
        </div>
    );
};

export default connect(({ analysisReports }) => {
    // 获取store中的信息
    return {
        analysisList: analysisReports.get("analysisList"),
        name: analysisReports.get("name")
    };
})(
    ImmComponent(
        ReportComponent
    )
);
