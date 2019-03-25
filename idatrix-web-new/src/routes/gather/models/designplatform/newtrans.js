import {
  getEdit_trans_attributes,
  getTrans_list
} from "../../../../services/gather";
import {
  getEdit_job_attributes,
  getJob_list
} from "../../../../services/gather1";

export default {
  namespace: "newtrans",
  state: {
    visible: false,
    actionName: "",
    info_name: "",
    viewId: "",
    status: "",
    description: "",
    params: null,
		copy_name: "",
		nameArgs:[],
		//复制时候的拥有这
		owner:""
  },
  reducers: {
    showTrans(state, action) {
      return {
        ...state,
        ...action.payload,
        actionName: "showTrans"
      };
    },
    newTrans(state, action) {
      return {
        ...state,
        ...action.payload,
        actionName: "newTrans"
      };
    },
    save(state, action) {
      return {
        ...state,
        ...action.payload
      };
    },
    hideTrans(state, action) {
      return {
        actionName: "",
        info_name: "",
        viewId: "",
        status: "",
        description: "",
        copy_name: "",
        visible: false
      };
    }
  },
  effects: {
    *getNewModel({ payload }, { select, call, put }) {
      let viewId = jsPlumbUtil.uuid();
      let data = [];

      if (payload.status === "job") {
        data = yield getJob_list({ isOnlyName: true,isMap:true });
      } else {
        data = yield getTrans_list({ isOnlyName: true,isMap:true });
      }

      yield put({
        type: "newTrans",
        payload: {
          visible: true,
          viewId: viewId,
          status: payload.status,
          nameArgs: data.data.data
        }
      });
    },
    *showTransModel({ payload }, { select, call, put }) {
      const { owner } = yield select(state => state.transheader);
      const { data } = yield getEdit_trans_attributes({
        name: payload.info_name,
        owner
      });
      const { description, params } = data.data;

      if (data) {
        yield put({
          type: "showTrans",
          payload: {
            info_name: payload.info_name,
            description: description ? description : "",
            params: params,
            visible: true,
            viewId: payload.viewId,
            status: payload.status
          }
        });
      }
    },
    *showJobModel({ payload }, { select, call, put }) {
      const { activeArgs } = yield select(state => state.jobheader);
      const { data } = yield getEdit_job_attributes({
        name: payload.info_name,
        owner: activeArgs.get(payload.info_name).owner
      });
      const { description, params } = data.data;
      if (data) {
        yield put({
          type: "showTrans",
          payload: {
            info_name: payload.info_name,
            description: description ? description : "",
            visible: true,
            params: params,
            viewId: payload.viewId,
            status: payload.status
          }
        });
      }
    }
  }
};
