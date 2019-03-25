/**
 * 此高阶组件用于保存数据库信息
 */
import React, { Component } from "react";
import { Select } from "antd";

const Option = Select.Option;

const withHDFS = (
  ChildComponent,
  { isRead = "", reqHadoopServer = true,width = 200 } = {}
) => {
  return class WarpComponent extends Component {
    constructor(props) {
      super(props);

      this.state = {
        hdfsList: [],
        hadoopName: "",
        selectName: "",
        selectData: {}
      };
    }

    componentDidMount() {
      this.Request();
    }

    /**
     * 初始化查询数据库信息
     */
    Request() {
      //查询HDFS服务器
      if (reqHadoopServer) {
        this.getHadoopServer();
      }
      //查询HDFS根目录
      this.getHDFSList();
    }

    /**
     * 查询hadoop服务器
     */
    getHadoopServer() {
      const { getHadoopServer } = this.props.model;
      getHadoopServer(data => {
        if (data && data[0] && data[0].name) {
          this.setState({
            hadoopName: data[0].name
          });
        }
      });
    }

    /**
     * 查询HDFS根目录
     */
    getHDFSList() {
      const { get_HDFSRoots } = this.props.model;

      get_HDFSRoots(
        {
          isRead
        },
        data => {
          if (data.length > 0) {
            this.setState({
              hdfsList: data,
              selectName: `${data[0]}/`
            });
          }
        }
      );
    }

    //储存选中的值
    handleChange(value, name) {
      const { selectData } = this.state;
      if (name) {
        selectData[name] = value;
        this.setState({
          selectData
        });
      } else {
        this.setState({
          selectName: value
        });
      }
    }

    render() {
      const { selectName, hdfsList } = this.state;

      const selectBefore = name => {
				const { selectData } = this.state;
				let currentValue = selectName;
				if(name && selectData[name]){
					currentValue = selectData[name];
				}

        return (
          <Select
            style={{ width: width }}
            value={currentValue}
            onChange={value => {
              this.handleChange(value, name);
            }}
          >
            {hdfsList.map(index => (
              <Option key={index} value={`${index}/`} alt={`${index}/`} >
                {`${index}/`}
              </Option>
            ))}
          </Select>
        );
      };

      return (
        <ChildComponent
          hdfsData={this.state}
          selectBefore={selectBefore}
          {...this.props}
        />
      );
    }
  };
};

export default withHDFS;
