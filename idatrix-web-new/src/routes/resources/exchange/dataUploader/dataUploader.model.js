import { message } from 'antd';

export default {
	namespace: "dataUploader",
	state: {
		data: 1
	},

	subscriptions: {

	},
	effects: {
		
	},
	reducers: {
		save(state, action) {
			return { ...state, ...action.payload }
		}
	}
}