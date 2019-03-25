import React from "react";

import { Table } from "antd";

export default () => {
  const columns = [
    {
      title: "字段名",
      dataIndex: "name",
      key: "name"
    },
    {
      title: "类型",
      dataIndex: "type",
      key: "type",
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
    {name:"name",type:"String",intro:"组织名称"},
    {name:"orgId",type:"Long",intro:"组织Id"},
    {name:"orderIndex",type:"String",intro:"排序索引"},
    {name:"superOrgid",type:"String",intro:"上级组织Id"},
    {name:"type",type:"String",intro:"组织类别"},
    {name:"orgInnerId",type:"String",intro:"内部组织Id"},
    {name:"superOrgInnerid",type:"String",intro:"上级内部Id"},
    {name:"children",type:"String",intro:"下级组织"},
    {name:"path",type:"String",intro:"路径"},
    {name:"address",type:"String",intro:"地址"},
    {name:"telNo",type:"String",intro:"联系方式"},
    {name:"describe",type:"String",intro:"描述"},
    {name:"linkman",type:"String",intro:"联系人"},
    {name:"id",type:"String",intro:"Id"},
    {name:"orgType",type:"String",intro:"组织类型"},
    {name:"isParent",type:"String",intro:"是否父节点"},
    {name:"hasCatalog",type:"String",intro:"是否有目录"},
  ];

  return <Table columns={columns} dataSource={data} pagination={false} />;
};
