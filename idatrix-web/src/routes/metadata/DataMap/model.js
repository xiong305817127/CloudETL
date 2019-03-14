/**
 * 元数据定义 - ES索引类model
 */

import Immutable from 'immutable';
import { queryRelationship, queryNode } from 'services/datamap';
import { dbtype } from 'config/datamap.config';

const immutableState = Immutable.fromJS({
  DBForceData: {
    links: [],
    nodes: [],
  },
  tablesForceData: {
    links: [],
    nodes: [],
  },
  fieldsForceData: {
    links: [],
    nodes: [],
  },
  tablesOfDB: [],
  fieldsOfTable: [],
  tableDetail: {}, // 表详情
  searchList: [],
});

/**
 * 清理forceData中的无效数据
 * @param  {array} nodes 节点数组
 * @param  {array} links 关系数组
 * @return {object}      返回清理后的数据
 */
const cleanForceData = (nodes, links) => {
  const newNodes = (nodes || []).map(node => ({
    ...node,
    id: node.id || node.guid,
  }));
  const newLinks = (links || []).filter((link) => {
    return newNodes.some(node => node.id == link.source)
      && newNodes.some(node => node.id == link.target);
  });
  return {
    nodes: newNodes,
    links: newLinks,
  };
};

export default {

  namespace: 'DataMap',

  state: immutableState,

  effects: {
    // 搜索接口
    *search({ payload }, { put }) {
      const { data } = yield queryNode(payload);
      if (Array.isArray(data.data)) {
        yield put({ type: 'save', payload: { searchList: data.data } });
      } else {
        yield put({ type: 'save', payload: { searchList: [] } });
      }
    },
    // 获取数据库关系
    *getDBRelation({ payload }, { put }) {
      const { data } = yield queryRelationship(payload);
      const resData = data && data.data || {};
      const { nodes, links } = cleanForceData(resData.nodes, resData.links);

      console.log(nodes,"节点");
      console.log(links,"关联");

      nodes.forEach(node => {
        node.typeName = dbtype[node.type];
        node.dbName = (node.guid || '.').split('.')[1];
      });
      yield put({ type: 'save', payload: { DBForceData: { nodes, links } } });
    },
    // 获取表关系
    *getTableRelation({ payload }, { put }) {
      const { data } = yield queryRelationship(payload);
      const resData = data && data.data || {};
      const { nodes, links } = cleanForceData(resData.nodes, resData.links);
      nodes.forEach(node => {
        const guid = node.guid.split('.');
        node.dbName = guid[1];
      });
      yield put({ type: 'save', payload: { tablesForceData: { nodes, links } } });
    },
    // 获取字段关系
    *getFieldRelation({ payload }, { put }) {
      const { data } = yield queryRelationship(payload);
      const resData = data && data.data || {};
      const { nodes, links } = cleanForceData(resData.nodes, resData.links);
      nodes.forEach(node => {
        const guid = node.guid.split('.');
        node.dbName = guid[1];
      });
      yield put({ type: 'save', payload: { fieldsForceData: { nodes, links } } });
    },
    // 获取数据库内的表
    *getTablesOfDB({ payload }, { put }) {
      const { data } = yield queryNode(payload);
      let tablesOfDB = [];
      if (Array.isArray(data.data)) {
        tablesOfDB = data.data.map((it, index) => ({
          key: index,
          ...it.extra,
          guid: it.guid,
          table_name_en: it.guid.table,
        }));
      }
      yield put({ type: 'save', payload: { tablesOfDB } });
    },
    // 获取表详情
    *getTableDetail({ payload }, { put }) {
      const { data } = yield queryNode(payload);
      let tableDetail = {};
      if (Array.isArray(data.data)) {
        tableDetail = data.data[0].extra;
      }
      yield put({ type: 'save', payload: { tableDetail } });
    },
    // 获取表内的字段
    *getFieldsOfTable({ payload }, { put }) {
      const { data } = yield queryNode(payload);
      let fieldsOfTable = [];
      if (Array.isArray(data.data)) {
        fieldsOfTable = data.data.map((it, index) => ({ key: index, ...it }));
      }
      yield put({ type: 'save', payload: { fieldsOfTable } });
    },
  },

  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname }) => {
        if (pathname === '/DataMap') {
          dispatch({
            type: 'getDBRelation',
            payload: {
              levelType: 20,
              needCount: 1,
            },
          });
        }
      });
    },
  },

  reducers: {
    save(state, action) {
      return state.merge(action.payload);
    },
  },

};
