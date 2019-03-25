import { searchResourceData } from 'services/catalog';

export default {
	namespace: "retrievalModel",

	state: {
		total: 0,
		dataSource: [],
		loading: false
	},
	subscriptions: {
		setup({ history, dispatch }) {
			return history.listen(({ pathname, query }) => {
				if (pathname === "/resources/retrieval") {
					dispatch({
						type: "getList", payload: {
							...query,
							page: query.page ? query.page : 1,
							pageSize: query.pageSize ? query.pageSize : 10
						}
					})
				}
			})
		},
	},
	effects: {
		*getList({ payload }, { put, call }) {
			yield put({ type: "save", payload: { loading: true } });

			let formatTypeV = [];
			if(payload.formatTypeV){
				formatTypeV = [payload.formatTypeV]
			}

			const { data } = yield call(searchResourceData, {
				...payload,
				formatTypeV,
				highlightPostTag: "</span>",
				highlightPreTag: "<span class='font_highlight'>",
			});
			const { code } = data;

			if (code === "200") {
				yield put({
					type: 'save',
					payload: {
						dataSource: data.data && data.data.rows ? data.data.rows : [],
						total: data.data && data.data.total ? data.data.total : 0,
						loading: false
					}
				})
			}
		}
	},
	reducers: {
		save(state, action) {
			return { ...state, ...action.payload }
		}
	}
}