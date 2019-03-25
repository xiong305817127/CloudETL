import React from "react";

import { Table, Button } from "antd";

import styles from "../datauploader.less";

const table1 = () => {
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
    {
      name: "name",
      number: "",
      intro: "目录分类名称"
    },
    {
      name: "superId",
      number: "",
      intro:
        "如果为空字符串则目录分类新增到根节点组织下，如果为其它目录id（catalogId）则新增到对应的目录分类下"
    },
    {
      name: "description",
      number: "",
      intro: "目录分类描述"
    },
    {
      name: "code",
      number: "",
      intro: "目录分类代码"
    },
    {
      name: "itemOrder",
      number: "",
      intro: "排序"
    }
  ];

  return <Table columns={columns} dataSource={data} pagination={false} />;
};

const table1Btns = () => (
  <div className={styles.button_group}>
    <p>信息资源接口</p>
    <Button type="primary">查询目录分类对应信息资源列表接口</Button>
    <Button type="primary">查询信息资源下数据项列表接口</Button>
    <Button type="primary">新增信息资源和数据项接口</Button>
    <Button type="primary">修改信息资源和数据项接口</Button>
    <Button type="primary">删除信息资源和数据项接口</Button>
    <Button type="primary">查询市级及区县目录和信息资源接口</Button>
  </div>
);

table1.Btns = table1Btns;
export default table1;