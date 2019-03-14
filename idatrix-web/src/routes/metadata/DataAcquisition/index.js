import React from 'react';
import { connect } from 'dva';
import { withRouter } from 'react-router';
import TableList from "../../../components/TableList"
import { Radio, message, Popconfirm, Tooltip, Icon } from 'antd';
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;

import Style from './Acquisition.css';
import { XJCJdelete } from '../../../services/AcquisitionCommon'
import { CJLBlist } from '../../../services/metadataCommon'
import AcquisitionModel from './viwe/AcquisitionModel'
import AcquisitionNameModel from './viwe/DatabaseNameModel'
import AcquisitionRear from './viwe/AcquisitionRear'

class Acquisition extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			pagination: {
				current: 1,
				pageSize: 10
			},
			current: 0,
			visible: true,
			visibleTs: true,
			info: {},
			value: 0,
		}
	}


	columns = [
		{
			title: '数据源主机名称',
			dataIndex: 'dbHostname',
			key: 'dbHostname',
			width: '13%',
		}, {
			title: '数据源数据库类型',
			dataIndex: 'dsType',
			key: 'dsType',
			width: '10%',
			render: (text, record) => record.dsType === 3 ? 'MySQL' : 'Oracle'
		}, {
			title: '数据源数据库中文名称',
			dataIndex: 'dsName',
			key: 'dsName',
			width: '10%',
		}, {
			title: '数据源数据库名称',
			dataIndex: 'dbDatabasename',
			key: 'dbDatabasename',
			width: '15%',
			render: (text, record) => {
				/* const model = "6";*/
				return (
					<div onClick={() => this.handleView(record)}>
						<a href={"#/DataAcquisition/DatabaseNameModel/" + record.dsId + "/" + record.dbDatabasename}> {text}</a>
					</div>
				)
			}
		}, {
			title: '存储的数据库类型',
			dataIndex: 'storageDsType',
			key: 'storageDsType',
			width: '10%',
			render: (text, record) => record.storageDsType === 3 ? 'MySQL' : 'Oracle' || record.storageDsType === null ? null : null
		}, {
			title: '存储的数据库名称',
			dataIndex: 'storageDsName',
			key: 'storageDsName',
			width: '10%',
		}, {
			title: '创建人',
			dataIndex: 'creator',
			key: 'creator',
			width: '10%',
		}, {
			title: '创建日期',
			dataIndex: 'createTime',
			key: 'createTime',
			width: '10%',
		}, {
			title: '操作',
			dataIndex: '12',
			key: '12',
			width: '10%',
			render: (text, record, index) => {
				if (record.canEdited === true) {
					return (
						<Popconfirm title="是否删除?" onConfirm={() => this.onDelete(record)} disabled={record.canEdited === false}>
							<a href="#">
								<Tooltip title="删除" >
									<Icon type="delete" className="op-icon" />&nbsp;&nbsp;&nbsp;&nbsp;
                </Tooltip>
							</a>
						</Popconfirm>
					);
				} else {
					return null
				}

			},
		}];

	onDelete(record) {
		let Mid = {};
		Mid.dsId = record.dsId;
		XJCJdelete([Mid]).then((res) => {
			const { code } = res.data;
			if (code === "200") {
				message.success("删除成功");
				this.handleSecher();
			}
		})
	}
	onChangeModel(e) {
		console.log(e, "==========================");
		const model = e.target.value;
		const { router, location } = this.props;
		router.push({ ...location, query: { model } });
		this.setState({
			value: e.target.value,
		});
	}

	handleView(record) {
		let model = "6";
		const { router, location } = this.props;
		router.push({ ...location, query: { model } });
		this.setState({
			value: model,
		});
		const { dispatch } = this.props;
		dispatch({
			type: "acquisition/setMetaId",
			payload: {
				modelMet: "newModel",
				dsId: record.dsId,
				databaseName: record.dbDatabasename,
				hostname: record.dbHostname,
				port: record.dbPort,
				username: record.dbUsername,
				password: record.dbPassword,
				pluginId: record.dsType,
			}
		});
	}



	componentDidMount() {
		this.handleSecher();
	}
	componentWillMount() {
		this.handleSecher();
	}

	handleSecher() {
		const { renterId } = this.props.account;
		this.setState({
			loading: true
		});
		let sourceId = {
			sourceId: "3",
			/* creator:username,*/
			renterId: renterId,
		};
		const { query } = this.props.location;
		const pager = this.state.pagination;
		CJLBlist(sourceId, {
			current: query.page || 1,
			pageSize: query.pageSize || pager.pageSize,
		}).then((res) => {
			const { code, total } = res.data;
			if (code === "200") {
				const { dispatch } = this.props;
				dispatch({
					type: "acquisition/setMetaId",
					payload: {
						datalist: res.data.data.rows
					}
				});

				pager.total = total;
				res.data.data.rows.map((row, index) => {
					row.key = row.id;
					row.index = pager.pageSize * (pager.current - 1) + index + 1;
					return row;
				});
				this.setState({
					data: res.data.data.rows,
					pagination: pager,
					loading: false
				});
			}
		})

	}


	render() {
		const { pagination } = this.state;
		const { datalist } = this.props.acquisition;
		const { location, router } = this.props;
		const { query } = location;
		const model = query.model || "1";

		return (
			<div>
				{model === "1" ? (
					<RadioGroup onChange={this.onChangeModel.bind(this)} value={model} className={Style.indexTitle}>

						<RadioButton value="2"> <a href={"#/DataAcquisition/AcquisitionModel/"}>从数据源导入表定义</a> </RadioButton>
						{/*<RadioButton value="3">手工新建数据源表定义</RadioButton>*/}
						{/* <RadioButton value="4" disabled={visibleTs}>
                     生成元数据定义表
                 </RadioButton>*/}
					</RadioGroup >
				) : null}

				{model === "2" ? (<AcquisitionModel location={location} router={router} />) : null}
				{model === "4" ? (<AcquisitionRear location={location} router={router} />) : null}

				{model === "1" ? (
					<TableList showIndex pagination={pagination} loading={this.state.loading}
						style={{ width: "99%", margin: "10px" }} dataSource={datalist} columns={this.columns} />
				) : null}
				{model === "6" ? (<AcquisitionNameModel location={location} router={router} />) : null}

			</div>
		);
	}
}
export default withRouter(connect(({ acquisition, account }) => ({
	acquisition, account
}))(Acquisition));

