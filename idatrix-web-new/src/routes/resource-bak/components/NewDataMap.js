/**
 * 数据地图组件
 */
import React from 'react';
import { Checkbox,Button } from 'antd';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import Modal from 'components/Modal';

import TableList from '../../../components/TableList';
import D3DataMap from 'components/DataMap';
import { querySanKey } from '../../../services/dataResource';
import Style from './DataMap.css';

class DataMap extends React.Component {

    state = {
        showBlood: false,
        // 数据地图配置
        mapOption: {
            data: [],
            links: [],
            selectedIds: [],
        },

        //存的历史id
        historyId:[]
    }

    // 字段表
    fieldsColumns = [
        {
            title: '字段名称',
            dataIndex: 'colName',
        }, {
            title: '描述',
            dataIndex: 'description',
        },
    ];

    // 关系表
    relationColumns = [
        {
            title: '表1字段名称',
            dataIndex: 'fcolName',
        }, {
            title: '表1字段代码',
            dataIndex: 'fcolCode',
        }, {
            title: '表2字段名称',
            dataIndex: 'scolName',
        }, {
            title: '表2字段代码',
            dataIndex: 'scolCode',
        }, {
            title: '关联关系描述',
            dataIndex: 'rsdescription',
        },
    ];

    componentWillReceiveProps(nextProps) {
        // 当组件由隐藏变为显示时执行
        if (nextProps.visible && !this.props.visible) {
            const { id } = this.props;
            const { historyId } = this.state;

            this.setState({
                showBlood: false,
                historyId:[...historyId,id]
            });
            this.updateMap(id);
        }
    }

    // 血缘关系开关
    handleChangeBlood = (e) => {
        this.setState({
            showBlood: e.target.checked,
        });
    }


    // 返回
    onReturn = ()=>{
        const { historyId } = this.state;

        if(historyId.length>1){
            historyId.pop();
            this.updateMap(historyId[historyId.length-1]);
            this.setState({
                historyId
            })
        }

    }

    // 地图事件处理
    handleMapClick = (data) => {

        console.log(data,"点击节点数据");

        const { dispatch } = this.props;
        const { mapOption,historyId } = this.state;
        if (data.source !== undefined && data.target !== undefined) { //点击的是边
            console.log(data);
            console.log(data.id,"点击dataID");

            dispatch({
                type: 'dataResource/getFieldRelation',
                payload: {
                    id: data.id,
                },
            });
            mapOption.selectedIds = [data.sId, data.tId];
            this.setState({ mapOption, showBlood: true });
        } else { // 点击的是点


            if(historyId.length && data.id !== historyId[historyId.length-1]){
                 this.setState({ showBlood: false,historyId:[...historyId,data.id]});
            }

             this.setState({ showBlood: false});
           
            this.updateMap(data.id);
        }
    }

    // 更新数据地图
    updateMap = async (metaid) => {
        const { dispatch } = this.props;
        const { mapOption } = this.state;
        const { data } = await querySanKey({ metaid });
        const resData = data && data.data || {};
        const setData = mapOption.data;
        const setLinks = mapOption.links;
        setData.splice(0);
        setLinks.splice(0);
        (resData.nodes || []).forEach(item => {
            // 去重
            if (!setData.some(it => it.value === item.metaid || it.name === item.tableName)) {
                setData.push({
                    name: item.tableName,
                    id: item.metaid,
                    table_name_cn: item.tableName
                });
            }
        });
        (resData.links || []).forEach(item => {
            let sourceIndex = -1, targetIndex = -1;
            setData.forEach((it, index) => {
                // if (it.value === item.metaid) {
                if (it.id === item.childId) {
                    sourceIndex = it.id;
                }
                // if (it.value === item.childId) {
                if (it.id === item.metaid) {
                    targetIndex = it.id;
                }
            });
            // 去重
            if (sourceIndex > -1 && targetIndex > -1 && !setLinks.some(it => it.source === sourceIndex && it.target === targetIndex)) {
                setLinks.push({
                    source: sourceIndex,
                    target: targetIndex,
                    id: item.id,
                });
            }
        });
        mapOption.data = setData;
        mapOption.links = setLinks;
        mapOption.selectedIds = [metaid];

        console.log(setData);
        console.log(setLinks);

        this.setState({ mapOption });
        dispatch({
            type: 'dataResource/getMeta',
            payload: {
                metaid,
            }
        });
    }

    //关闭弹框

    handleClose = ()=>{
        const { onClose } = this.props;
        this.setState({
            historyId:[]
        });

        onClose();
    }


    render() {
        const { showBlood,historyId } = this.state;
        const { viewTable, fieldRelations } = this.props.dataResource;

        console.log(historyId,"历史ID");
        return (<Modal
            title="查看数据地图"
            visible={this.props.visible}
            onCancel={this.handleClose}
            width={800}

            footer = {[
                <Button key="sure" onClick={this.handleClose}>确定</Button>,  
                <Button key="return"  onClick={this.onReturn}  disabled={historyId.length>1?false:true} style={{float:"left"}}>返回</Button>
              ]}
        >
            <section className={Style.mapWrap}>
                <div className={Style.toolsWrap}>
                    {/*<Checkbox onChange={this.handleChangeBlood}>血缘分析</Checkbox>*/}
                </div>
                {this.state.mapOption.data.length > 0 ? (
                    <D3DataMap
                        nodesData={this.state.mapOption.data}
                        linksData={this.state.mapOption.links}
                        height={"100%"}
                        width={"100%"}
                        type={"table"}
                        analysisView={true}
                        onDblClickNode = {this.handleMapClick}
                    />
                ) : null}
            </section>
            {!showBlood ? (
                <TableList
                    rowKey='id'
                    columns={this.fieldsColumns}
                    dataSource={viewTable}
                    pagination={false}
                />
            ) : (
                <TableList
                    rowKey='id'
                    columns={this.relationColumns}
                    dataSource={fieldRelations}
                    pagination={false}
                />
            )}
        </Modal>);
    }
}

DataMap.propTypes = {
    id: PropTypes.number.isRequired,
    visible: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
};

export default connect(({ dataResource }) => ({
    dataResource,
}))(DataMap);
