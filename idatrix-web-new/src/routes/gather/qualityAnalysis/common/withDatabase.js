/**
 * 此高阶组件用于保存数据库信息
 */
import React, { Component } from "react";

const withDatabase = (ChildComponent, { isRead = "" } = {}) => {
  return class WarpComponent extends Component {
    constructor(props) {
      super(props);

      this.state = {
        database: "",
				databaseId: "",
				type:"",
        databaseList: [],

        schema: "",
        schemaId: "",
        schemaList: [],

        tableList: [],
        table: "",
        tableId: "",
        tableType: ""
      };
    }

    componentDidMount() {
      this.Request();
    }

    /**
     * 初始化查询数据库信息
     */
    async Request() {
      const { selectOption, config } = this.props.model;
      const { databaseId, schemaId, tableId } = config;

      //优先请求数据库列表
      await new Promise(resolve => {
        selectOption(data => {
          this.setState({
            databaseList: data
          });
          resolve();
        });
      });

      if (databaseId !== undefined && typeof databaseId === "number") {
				await this.getSchemaList(databaseId);
				if (schemaId !== undefined && typeof schemaId === "number") {
					await this.getTableList(schemaId);
					if (tableId !== undefined && typeof tableId === "number") {
						await this.getFieldList(tableId);
					}
				}
      }
    }

    /**
     * 通过id,查询schema信息,并保存
     */
    getSchemaList(id) {
      return new Promise(resolve => {
        if (id === undefined) resolve();
        const { getSchema } = this.props.model;
        const { databaseId, database, databaseList } = this.state;
        let item = { databaseId, database };

        if (databaseId !== id) {
					const obj = databaseList.filter(index => index.id === id)[0];
          item.databaseId = obj.id;
					item.database = obj.name;
					item.type = obj.type;
        }

        getSchema(
          { id: item.databaseId, name: item.database, isRead },
          data => {
            this.setState({
              ...item,
              schemaList: data
						});
            resolve();
          }
        );
      });
    }

    /**
     * 通过id,查询table信息,并保存
     */
    getTableList(id) {
      return new Promise(resolve => {
        if (id === undefined) resolve();
        const { getDbTable } = this.props.model;
        const { schema, schemaId, schemaList } = this.state;
        let item = { schema, schemaId };

        if (schemaId !== id) {
          const obj = schemaList.filter(index => index.schemaId === id)[0];
          item.schemaId = obj.schemaId;
          item.schema = obj.schema;
        }

        getDbTable({ id: item.schemaId, name: item.schema, isRead }, data => {
          this.setState({
            ...item,
            tableList: data
					});
					resolve();
        });
      });
    }

    /**
     * 当选择Table时,记录所选数据,也可请求字段列表
     */
    getFieldList(id,needFields) {
      if (id === undefined) return;
      const { tableList, table, tableId, tableType } = this.state;
      let item = { table, tableId, tableType };

      if (tableId !== Number(id)) {
        tableList.forEach(index => {
          index.forEach(r => {
            if (r.id === Number(id)) {
              item.table = r.name;
              item.tableId = r.id;
              item.tableType = r.type;
            }
          });
        });
      }

      this.setState({
        ...item
      });
    }

    render() {
      return (
        <ChildComponent
          getSchemaList={this.getSchemaList.bind(this)}
          getTableList={this.getTableList.bind(this)}
          getFieldList={this.getFieldList.bind(this)}
          databaseData={this.state}
          {...this.props}
        />
      );
    }
  };
};

export default withDatabase;
