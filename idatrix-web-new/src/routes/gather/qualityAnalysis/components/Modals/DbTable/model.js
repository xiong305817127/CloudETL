import {
  get_db_table_fields
} from "services/quality";

/*数据库链接*/
export default {
  namespace: "analysisDbtable",
  state: {
    tableList: [],
    tableFields: [],
    selectKeys: [],
    schemalist: [],

    //表种类列表
    viewList: [],
    //表种类默认为
    viewType: "",
    tables: [],
    owner: "",
    //选择的表id
    id: "",

    visible: false,
    loading: false,
    loading1: false,

    dataType: "",
    fuc: null
  },
  reducers: {
    show(state, action) {
      return {
        ...state,
        ...action.payload
      };
    },
    showLoading(state, action) {
      return {
        ...state,
        loading: true
      };
    },
    hide(state, action) {
      return {
        tableList: [],
        tableFields: [],
        selectKeys: [],
        schemalist: [],

        //表种类列表
        viewList: [],
        //表种类默认为
        viewType: "",
        tables: [],
        owner: "",
        //选择的表id
        id: "",

        visible: false,
        loading: false,
        loading1: false,

        dataType: "",
        fuc: null
      };
    }
  },
  effects: {
    //job列表
    *querySchema({ payload }, { put }) {
      const { tableList } = payload;
      let viewList = [
        {
          value: "table",
          label: "表"
        },
        {
          value: "view",
          label: "视图"
        }
      ];

			//更改视图列表内容
			let viewType = "";
			viewList.length = tableList.length;
			if(viewList.length > 0){
				viewType = "table"
			}
      const tables = tableList.length > 0 ? tableList[0] : [];

      yield put({
        type: "show",
        payload: {
          ...payload,
          viewList,
					tables,
					viewType
        }
      });
    },
    *queryTableFields({ payload }, { put }) {
      yield put({ type: "showLoading" });
      const { data } = yield get_db_table_fields({ ...payload, isRead: true });
      const { code } = data;
      let fields = [];
      if (code === "200" && data.data && data.data instanceof Array) {
        for (let index of data.data) {
          fields.push({
            key: index.fieldName,
            name: index.fieldName
          });
        }
      }

      yield put({
        type: "show",
        payload: {
          ...payload,
          tableFields: fields,
          loading: false
        }
      });
    }
  }
};
