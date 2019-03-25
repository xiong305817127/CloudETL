/**
 * Created by Administrator on 2017/6/30.
 */
import React from 'react';
import { Row, Col } from 'antd';
import Style from './Line.css'
import { SITE_CUSTOM_THEME } from 'constants';


const Line = ({ size, title }) => {

	const getLine = () => {
		switch (SITE_CUSTOM_THEME) {
			case "default":
				return <Row id={Style.line} className={size ? Style[size] : Style["default"]} >
					<Col className={Style.colTitle}> {title}</Col>
					<Col className={Style.colLine} > <hr className={Style.colHr} /></Col>
				</Row>;
			default:
				return <Row id={Style.line} className={size ? Style[size] : Style["default"]} >
					<Col className={Style.colLine} > <hr className={Style.colHr} /></Col>
				</Row>
		}
	};

	return (
		getLine()
	)
};

export default Line;