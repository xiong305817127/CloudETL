/**
 * Created by Steven Leo on 2018/10/08.
 */

import React from "react"
import {Modal, Button} from "antd"

/**
 * 这是一个HOC（High-Order Component）
 * 用于增强组件的可配置项和功能
 * @param {Object} Options / 检测配置项
 * @return {React Component}  / 返回一个新的组件
 */
export default (Plugin,Options)=>{


    return class PluginComponent extends React.Component{
        constructor(){
            super();

            // ready用于判断请求是否完成，如果完成，则开启button，否则禁用
            // inputData用于判断上一步的内容
            this.state = {
                ready: false,
                InputData: [],
                dictionaryList: []
            }
        }

        /**
         * 请求方法
         * 此处应绑定Model，后续需要优化
         */
        Request = ()=>{
            const { getInputFields,transname,text,prevStepNames} = this.props.model;
            if(!prevStepNames || prevStepNames.length === 0 ){
                this.setState({
                    tip: "需要有前置步骤才可以配置此步骤"
                })
            }else{
                let obj = {};
                obj.transname = transname;
                obj.stepname = text;
                getInputFields(obj, data => {
                    this.setState({
                      InputData: data,
                      ready: true
                    });
                });
            }
        };

        /**
         * 隐藏model
         */
        hideModal = () => {
            const { dispatch } = this.props;
            dispatch({
                type:'domItems/hide',
                visible: false
            });
        };

        /**
         * 获取字典
         * 从Options中读取dictionaryId
         */
        GetDictionary = ()=>{
            if(!Options || typeof Options.dictionaryId === "undefined"){
                return;
            }

            const { GetDic } = this.props.model;

            GetDic({id:Options.dictionaryId},(data)=>{

                this.setState({
                    dictionaryList: data ? data : []
                });
            });
        }

        /**
         * 初始化数据
         */
        componentDidMount = ()=>{
						//是否需要上级节点，默认需要
						if(Options && Options.notNeedPreNodes){
							this.setState({ ready:true })
						}else{
							this.Request();
						}

            this.GetDictionary();
        }

        /**
         * 使用子组件的handleCreate
         */
        handleCreate = ()=>{
            const { saveStep, key} = this.props.model;

            // 务必使用子组件的handleCreate方法
            this.instanceComponent.handleCreate((obj)=>{
                saveStep(obj,key,(data)=>{
                    if(data.code === "200"){
                        this.hideModal();
                    }
                })
            });
        }

        /**
         * 传递给子组件，用于判断select选框是否为nodata
         */
        ifNoName = (rule,value,callback)=>{
            if(value === "nodata"){
                callback(rule.message)
            }
        
            callback();
        }

        render(){
            const { visible,text } = this.props.model;

            // 此处nodename被上面的text替换掉了
            // 注意：text是指stepName
            // const {nodeName} = config;

            return (
                <Modal
                    visible={visible}
                    title={text} //此处为stepName
                    wrapClassName="vertical-center-modal"
                    width={Options && Options.width ? Options.width : 500}
                    footer={[
                        <Button key="submit" type="primary" size="large" disabled={!this.state.ready} onClick={this.handleCreate} >
                            确定
                        </Button>,
                        <Button key="back" size="large" onClick={this.hideModal}>取消</Button>,
                    ]}
                    onCancel = {this.hideModal}
                >   
                    {
                        this.state.ready &&
                        <Plugin
                            {...this.props}  // props
                            data={this.state}  // data
                            ifNoName={this.ifNoName}  
                            hideModal={this.hideModal}
                            ref={instanceComponent => this.instanceComponent = instanceComponent} // 获取实例化的子组件
                        />
                    }

                    {
                        !this.state.ready &&
                        <p>{this.state.tip && <span>需要指定上一步。</span>}</p>
                    }

                </Modal>    
            )
        }
    }
}