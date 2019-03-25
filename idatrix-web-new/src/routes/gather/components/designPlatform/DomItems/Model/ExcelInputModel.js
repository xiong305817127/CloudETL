import React from 'react';
import { connect } from 'dva';
import { Transfer,Button } from 'antd';
import Modal from "components/Modal.js";

class ExcelInputModel extends React.Component {
  state = {
    mockData: [],
    targetKeys: [],
  }

  componentWillReceiveProps(nextProps){

    console.log(nextProps);
      const { tableNames }  = nextProps.excelinputmodel;
      let args = [];
      let count = 0;
      for(let index of tableNames){
          args.push({
             key: count,
              title:index
          })
        count++;
      }

      this.setState({
        mockData:args
      })
  }

  filterOption = (inputValue, option) => {
    return option.title.indexOf(inputValue) > -1;
  }
  handleChange = (targetKeys) => {
    this.setState({ targetKeys });
  }

  handleCreate(){
    const { handleSheetUpdate } = this.props.excelinputmodel;
    console.log(this.state.mockData);
    console.log(this.state.targetKeys);
    let args = [];
    if(this.state.targetKeys.length>0){
          for(let index of this.state.targetKeys){
              args.push(this.state.mockData[index])
          }
    }
    if(args.length>0){
      handleSheetUpdate(args);
    }
    this.setModelHide();
  }
  setModelHide(){
     const { dispatch } = this.props;
      this.setState({
        mockData: [],
        targetKeys: []
      });
      dispatch({
        type:"excelinputmodel/hide",
        visible:false,
      })
  }

  render() {

    const { visible } = this.props.excelinputmodel;

    return (
    <Modal
      title="选择工作表"
      wrapClassName="vertical-center-modal  out-model"
      visible={visible}
      footer={[
            <Button key="submit" type="primary" size="large" onClick={this.handleCreate.bind(this)}>确定</Button>,
            <Button key="back" size="large" onClick={this.setModelHide.bind(this)}>取消</Button>,
        ]}
      maskClosable={false}
      onCancel ={this.setModelHide.bind(this)}
    >
      <Transfer
        listStyle={{
          width: 222,
          height: 300,
        }}
        dataSource={this.state.mockData}
        showSearch
        filterOption={this.filterOption}
        targetKeys={this.state.targetKeys}
        onChange={this.handleChange}
        render={item => item.title}
        searchPlaceholder="请输入"
        notFoundContent="暂无数据列表"
      />
    </Modal>
    );
  }
}

export default connect(({ excelinputmodel })=>({
  excelinputmodel
}))(ExcelInputModel);
