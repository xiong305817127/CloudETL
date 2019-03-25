/**
 * 安全管理子系统公共信息model
 * 注：该model维护的是持久数据，故，可公用的数据才能在此维护
 *
 * 使用说明：
 * 以调用部门数据为例
 * >
 * >   // 第一步：dispatch数据相关的查询方法
 * >   componentWillMount() {
 * >     const { dispatch } = this.props;
 * >     dispatch({ type: 'securityCommon/getOrgList' });
 * >   }
 * >
 * >   // 第二步：引入需要的数据
 * >   render() {
 * >     const { organizationOptions, organizationTree } = this.props.securityCommon;
 * >     console.log(organizationOptions, organizationTree);
 * >   }
 * >
 */

import {
  getOrganizationList,
} from '../../services/securityCommon';
import { deepCopy, convertArrayToTree } from '../../utils/utils';

/**
 * 不可变原始state
 * 使用时注意先用deepCopy拷贝一份，防止污染
 */
const immutableState = {
  organizationOptions: [],   // 部门选项列表
  organizationTree: [],      // 部门树，可供Tree、TreeSelect、Cascader组件使用
};

export default {

  namespace: 'securityCommon',

  state: {
    ...deepCopy(immutableState),
  },

  effects: {
    /**
     * 查询部门选项数据
     * @param  {boolean} options.force  是否强制刷新
     */
    *getOrgList({ force,dataReady }, { put, select }) {
      const { account, securityCommon } = yield select(state => state);
      // if (securityCommon.organizationOptions.length > 0 && !force) return;
      let data = {};

      // 查看是否已经存在数据
      // 这是来自./organizationManage中的model.js提供的数据
      if(dataReady){
        console.log(dataReady,"采用备用数据")
        data = dataReady.data;
      }else{
        const result = yield getOrganizationList({
          renterId: account.renterId,
        });

        data = result.data.data;
      }
      if (Array.isArray(data.list)) {
        const list = data.list.map(item => ({
          label: item.deptName,
          value: String(item.id),
          parentId: item.parentId,
        }));
        const tree = convertArrayToTree(data.list, null, 'id', 'parentId', 'children', child => ({
          value: String(child.id),
          label: child.deptName,
        }));
        yield put({ type: 'save', payload: { organizationOptions: list, organizationTree: tree } });
      }
    },

  },

  reducers: {
    // 合并状态
    save(state, action) {
      return { ...state, ...action.payload };
    },
    // 清理状态
    clear() {
      return {
        ...deepCopy(immutableState),
      };
    },
  },
};
