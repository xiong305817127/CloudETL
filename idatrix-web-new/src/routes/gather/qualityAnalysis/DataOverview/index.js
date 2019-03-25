import React from 'react'
import { Table, Form, Button,Select } from 'antd';
import { connect } from 'dva';
import styles from "./style.less";
import ReactEcharts from 'echarts-for-react';
import { withRouter,hashHistory } from 'react-router';
const Option = Select.Option;
class index extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            hover:false,
            hover1:false,
            hover2:false,
            hover3:false,
            hover4:false
        };
    }
    mouseoutClick(){
        hashHistory.push("/gather/qualityAnalysis/indexViewFour");
        // hashHistory.push("/gather/qualityAnalysis/indexViewOne");
        this.setState({
            hover:true,
            hover1:false,
            hover2:false,
            hover3:false,
            hover4:false
        })
    }
    mouseoutClick1(){
        hashHistory.push("/gather/qualityAnalysis/indexViewFives");
        // hashHistory.push("/gather/qualityAnalysis/indexViewTwo");
        this.setState({
            hover:false,
            hover1:true,
            hover2:false,
            hover3:false,
            hover4:false
        })
    }
    mouseoutClick2(){
        hashHistory.push("/gather/qualityAnalysis/indexViewOne");
        // hashHistory.push("/gather/qualityAnalysis/indexViewThere");
        this.setState({
            hover:false,
            hover1:false,
            hover2:true,
            hover3:false,
            hover4:false
        })
    }
    mouseoutClick3(){
        hashHistory.push("/gather/qualityAnalysis/indexViewThere");
        // hashHistory.push("/gather/qualityAnalysis/indexViewFour");
        this.setState({
            hover:false,
            hover1:false,
            hover2:false,
            hover3:true,
            hover4:false
        })
    }
    mouseoutClick4(){
        hashHistory.push("/gather/qualityAnalysis/indexViewTwo");
        // hashHistory.push("/gather/qualityAnalysis/indexViewFives");
        this.setState({
            hover:false,
            hover1:false,
            hover2:false,
            hover3:false,
            hover4:true
        })
    }


    render() {
        const {hover,hover1,hover2,hover3,hover4}=this.state;
        console.log(hover,"hover");
        return(
            <div className={styles.indexTitle}> 
                <div className={hover===true?styles.manginColor:styles.mangin} onClick={this.mouseoutClick.bind(this)}>
                    <div className={styles.RZReport}></div>
                    <p>日志统计报表</p>
                </div>
                <div className={hover1===true?styles.manginColor:styles.mangin} onClick={this.mouseoutClick1.bind(this)}>
                    <div className={styles.SSZReport}></div>
                    <p>神算子平台报表</p>
                </div>
                <div className={hover2===true?styles.manginColor:styles.mangin} onClick={this.mouseoutClick2.bind(this)}>
                    <div className={styles.SJJCReport}></div>
                    <p>数据采集&集成</p>
                </div>
                <div className={hover3===true?styles.manginColor:styles.mangin} onClick={this.mouseoutClick3.bind(this)}>
                    <div className={styles.SJTSReport}></div>
                    <p>数据分析&探索BI</p>
                </div>
                <div className={hover4===true?styles.manginColor:styles.mangin} onClick={this.mouseoutClick4.bind(this)}>
                    <div className={styles.SJGXReport}></div>
                    <p>数据共享交换</p>
                </div>
          </div>
        )
    }
}



export default connect(({ DataOverviewModel }) => ({
    DataOverviewModel
}))(index)
