import React from "react";

import { Table, Button } from "antd";
import styles from "../datauploader.less";

const table2 = () => {
  const columns = [
    {
      title: "字段名",
      dataIndex: "name",
      key: "name"
    },
    {
      title: "数值",
      dataIndex: "number",
      key: "number",
      width: "20%"
    },
    {
      title: "说明",
      dataIndex: "intro",
      key: "intro",
      width: "50%"
    }
  ];

  const data = [
    { name: "name", number: "", intro: "资源名称" },
    { name: "catalogId", number: "", intro: "目录分类id" },
    { name: "code", number: "", intro: "信息资源代码（信息资源的名称拼音）" },
    { name: "hitFrequency", number: "", intro: "更新周期" },
    { name: "dataKind", number: "", intro: "信息资源格式分类" },
    { name: "dataKindDes", number: "", intro: "信息资源格式分类备注" },
    { name: "dataType", number: "", intro: "信息资源格式类型" },
    { name: "shareWay", number: "", intro: "共享方式分类" },
    { name: "shareWayType", number: "", intro: "共享方式类型" },
    { name: "description", number: "", intro: "信息资源摘要" },
    { name: "innerOrg", number: "", intro: "提供方内部部门" },
    { name: "innerCode", number: "", intro: "提供方内部部门代码" },
    { name: "dataSource", number: "", intro: "信息资源提供方" },
    { name: "dataSourceCode", number: "", intro: "资源提供方代码" },
    { name: "categoryName", number: "", intro: "关联及类目名称" },
    { name: "stanComDate", number: "", intro: "发布日期（如：2018-04-02）" }
  ];

  return <Table columns={columns} dataSource={data} pagination={false} />;
};

const Btns = () => (
  <div className={styles.button_group}>
    <p>目录分类接口</p>
    <Button type="primary">目录分类接口列表</Button>
    <Button type="primary">目录分类详情接口</Button>
    <Button type="primary">新增上报目录分类接口</Button>
    <Button type="primary">修改目录分类接口</Button>
    <Button type="primary">删除目录分类接口</Button>
  </div>
);

table2.Btns = Btns;
export default table2;
