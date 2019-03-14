import React from "react";
import { connect } from "dva";
import RightTable from "./ModelId/components/RightTable";
import LeftTable from "./ModelId/components/LeftTable";
import styles from "./ModelId/index.less";
import classnames from "classnames";
import { Switch } from "antd";
import { defaultSettings, sourceConfig, targetConfig } from "config/jsplumb.config.js";

class index extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			Instance: null
		}
	}

	componentDidMount() {
		const mainContent = this.refs.mainContent;
		const Instance = jsPlumb.getInstance({
			...defaultSettings,
			Container: mainContent
		});
		Instance.registerConnectionType("basic", { anchor: "Continuous", connector: "StateMachine" });
		this.setState({ Instance });
	}

	componentDidUpdate() {
		this.initItemsView();
	}

	initItemsView() {
		const { Instance } = this.state;
		const { lines } = this.props.biModelId;
		if (Instance) {
			Instance.deleteEveryConnection();
			Instance.deleteEveryEndpoint();
			(lines || []).map(index => {
				Instance.connect({
					source: index.sourceId,
					target: index.targetId,
					overlays: [
						["Label", { label: index.count + "", id: "label", location: -40, cssClass: "aLabel" }]
					]
				});
			});
		}
	}

	handleCheckChange = (obj) => {
		const { dispatch } = this.props;
		dispatch({
			type: "biModelId/save",
			payload: {
				...obj
			}
		})
	}

	render() {
		const { items, dimensionView, measureView } = this.props.biModelId;

		console.log(this.props.biModelId, "主表自读");

		return (
			<div className={styles.modelConfig}>
				<div className={classnames(styles.content, styles.viewContent)}>
					<div className={styles.modelContent}>
						<div className={styles.upPart}
							ref="mainContent"
						>
							{
								items.map(index => {
									return (
										<div
											id={index.id}
											ref={index.id}
											key={index.id}
											style={{ top: index.y, left: index.x }}
											className={styles.item}>
											<span
												className={styles.text}
											>{index.tableName}</span>
											<span className={styles.img} ><img className={styles.imgTag} src={require("assets/images/analysis/line_icon.png")} /></span>
										</div>
									)
								})
							}
						</div>
						<div className={styles.downPart}>
							<div className={styles.left}>
								<div className={styles.header}>
									<div><span className={styles.split}>|</span>&nbsp;&nbsp;维度</div>
									<div className={styles.switch}>
										<Switch size="small" checkedChildren="可见" checked={dimensionView} onChange={(e) => { this.handleCheckChange({ dimensionView: e }) }} unCheckedChildren="全部" />
									</div>
								</div>
								<div className={styles.tables}><LeftTable canEdit={false} /></div>
							</div>
							<div className={styles.right}>
								<div className={styles.header}>
									<div><span className={styles.split}>|</span>&nbsp;&nbsp;度量</div>
									<div className={styles.switch}>
										<Switch size="small" checkedChildren="可见" unCheckedChildren="全部" onChange={(e) => { this.handleCheckChange({ measureView: e }) }} checked={measureView} />
									</div>
								</div>
								<div className={styles.tables}><RightTable canEdit={false} /></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		)
	}
}

export default connect(({
	biModelId
}) => ({ biModelId }))(index);