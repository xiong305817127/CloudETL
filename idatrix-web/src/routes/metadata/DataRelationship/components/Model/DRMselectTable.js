/**
 * Created by Administrator on 2017/8/26.
 */
import { Form, Button,Radio ,Input,Icon,Cascader,Tabs,Table,Row,Col,message} from 'antd';
import Style from './DRMselectTable.css'
const RadioGroup = Radio.Group;
const TabPane = Tabs.TabPane;
import { connect } from 'dva';
import TableList from "../../../../../components/TableList";
import Search from '../../../../../components/Search';
import Modal from 'components/Modal';
const FormItem = Form.Item;
/*import {SJGXGLisExists } from  '../../../../../services/metadata';*/
const DRMselectTable1 = ({drmselecttable,form,dispatch,account,metadataCommon})=>{

    const {selectedIds, visible,tableType,options,text,tableIndex,data,pagination,searchIndex,tabsIndex,loading,selectKey,selectedRowKeys,metaid,rsType,actionType } = drmselecttable;
    const { getFieldDecorator } = form;
    const { id, renterId } = account;
    const {departmentsOptions,tagsOptions,themeOptions,industryOptions,departmentsTree} = metadataCommon;
    const handleCancel = ()=>{
      if(selectedRowKeys.length >0 ){
        data.splice(0,data.length);
        selectKey.splice(0,selectKey.length);
        selectedRowKeys.splice(0,selectedRowKeys.length);
      }
      dispatch({
         type:"drmselecttable/hideModel"
      })
    };

    const handleOk = (e)=>{
      if(selectKey[0]){
           if(tableIndex === "2"){
                dispatch({
                  type:"drmnewfilemodel/changeModel",
                  payload:{
                    dataRight:[
                      {
                         key:selectKey[0].fileid,
                        dirName:selectKey[0].dirName,
                      }
                    ],
                    tableType:tableType,
                    fileTable:true,
                    rightName:selectKey[0].dirName
                  }
                });
                dispatch({
                  type:"drmnewfilemodel/TableAndFiledById",
                  payload:{
                    id:id,
                    actionType:"edit"
                   }
                });
             }else{
                 dispatch({
                    type:"drmselecttable/setMetaId",
                    payload:{
                       metaid:selectKey[0].metaid
                    }
                  });
                if(tableType === 2){
                      dispatch({
                        type:"drmnewfilemodel/SJGXGLisExists",
                        payload:{
                           id:selectKey[0].metaid,
                           rightmetaid:selectKey[0].metaid,
                           actionType:"right",
                           metaNameCn:selectKey[0].metaNameCn,
                           rsType:rsType,
                           tableType:tableType,
                        }
                    });
                }else{
                     dispatch({
                        type:"drmnewfilemodel/SJGXGLisExists",
                        payload:{
                           id:selectKey[0].metaid,
                           leftmetaid:selectKey[0].metaid,
                           actionType:"left",
                           metaNameCn:selectKey[0].metaNameCn,
                           rsType:rsType,
                           tableType:tableType,
                        }
                    });
                }
          }
      }else{
        if(tableType === 2){
              dispatch({
                type:"drmnewfilemodel/changeModel",
                payload:{
                  dataRight:[],
                  rightName:"",
                  metaid:selectKey[0].metaid
                }
              });
               dispatch({
                    type:"drmnewfilemodel/SJGXGLisExists",
                    payload:{
                       id:selectKey[0].metaid,
                       rightmetaid:selectKey[0].metaid,
                       actionType:"right",
                       metaNameCn:selectKey[0].metaNameCn,
                       rsType:rsType,
                       tableType:tableType,
                    }
                });
          }else{
            dispatch({
              type:"drmnewfilemodel/changeModel",
              payload:{
                dataLeft:[],
                leftName:"",
                metaid:selectKey[0].metaid
              }
            });
             dispatch({
                  type:"drmnewfilemodel/SJGXGLisExists",
                  payload:{
                     id:selectKey[0].metaid,
                     leftmetaid:selectKey[0].metaid,
                     actionType:"left",
                     metaNameCn:selectKey[0].metaNameCn,
                     rsType:rsType,
                     tableType:tableType,
                  }
              });
          }
      }
      handleCancel();
    };

  //选择部门级联
   const xuanzebumendid = (value, selectedOptions) => {
       dispatch({
         type:"drmselecttable/showModel",
         payload:{
           text: value[value.length - 1],
          /* text:selectedOptions.map(o => o.value).join(', ').pop(),*/
           textBuMenAnNiu: '取消选择'
         }
       })
    };

    //单选框变化
    const handleRadioChange = (e)=>{
      console.log(e);
      dispatch({
        type:"drmselecttable/showModel",
        payload:{
          tableIndex:e.target.value
        }
      });
      reload({
        tableIndex:e.target.value
      })
    };

    const showRadio = ()=>{
        if(tableType === 2){
            return(
              <RadioGroup onChange={handleRadioChange} value={tableIndex}>
                <Radio key="1" value="1">数据表类</Radio>
                <Radio key="2" value="2">文件类</Radio>
              </RadioGroup>
            )
        }else {
           return null;
        }
    };

    const columns = [
      {
        title: '表中文名称',
        width:'15%',
        dataIndex: 'metaNameCn',
        key: 'metaNameCn',
        render: (text,record) => {
          //这里的text和record没区别
          return <a onClick={()=>{return false}}>{text}</a>
        }
      },{
        width:'14%',
        title: '表英文名称',
        dataIndex: 'metaNameEn',
        key: 'metaNameEn',
      }, {
        title: '所属组织',
        width:'14%',
        dataIndex: 'dept',
        key: 'dept',
        render: (text) => {
          const found = departmentsOptions.find(it => it.value == text);
          return found ? found.label : '';
      },
    }, {
        title: '拥有者',
        dataIndex: 'owner',
        width:'11%',
        key: 'owner',
      }, {
        title: '行业',
        dataIndex: 'industry',
        width:'11%',
        key: 'industry',
        render: (text) => {
          const found = industryOptions.find(it => it.value == text);
          return found ? found.label : '';
      },
      }, {
        title: '主题',
        dataIndex: 'theme',
        width:'11%',
        key: 'theme',
        render: (text) => {
          const found = themeOptions.find(it => it.value == text);
          return found ? found.label : '';
      },
      },{ title: '标签',
        width:'11%',
        dataIndex: 'tag',
        key: 'tag',
        render: (text,record) => {
          const found = tagsOptions.find(it => it.value == text);
          return found ? found.label : '';
      },
      }
  ];
  const columns1 = [{
    title: '文件目录名称',
    width:'16%',
    dataIndex: 'dirName',
    key: 'dirName',
    render: (text,record) => {
      //这里的text和record没区别
      return <a onClick={()=>{return false}}>{text}</a>
    }
  },{
    width:'13%',
    title: '文件存储目录',
    dataIndex: 'storDir',
    key: 'storDir',
  }, {
    title: '所属组织',
    width:'13%',
    dataIndex: 'dept',
    key: 'dept',
     render: (text) => {
          const found = departmentsOptions.find(it => it.value == text);
          return found ? found.label : '';
      },
  }, {
    title: '拥有者',
    dataIndex: 'owner',
    width:'11%',
    key: 'owner',
  }, {
    title: '行业',
    dataIndex: 'industry',
    width:'11%',
    key: 'industry',
    render: (text) => {
          const found = industryOptions.find(it => it.value == text);
          return found ? found.label : '';
      },
  }, {
    title: '主题',
    dataIndex: 'theme',
    width:'11%',
    key: 'theme',
    render: (text) => {
          const found = themeOptions.find(it => it.value == text);
          return found ? found.label : '';
      },
  },{ title: '标签',
    width:'11%',
    dataIndex: 'tag',
    key: 'tag',
    render: (text,record) => {
          const found = tagsOptions.find(it => it.value == text);
          return found ? found.label : '';
      },
  }
  ];


    const handleTableChange = (e)=>{
      reload({
        current:e.current
      });
    };

    const  handleTabsChange = (e)=>{
      reload({
        tabsIndex:e
      });
      dispatch({
        type:"drmselecttable/showModel",
        payload:{
          tabsIndex:e
        }
      });
    };

  const handleSearch = (e)=>{
      dispatch({
        type:"drmselecttable/showModel",
        payload:{
          searchIndex:e
        }
      });
      reload({
        searchIndex:e
      });
  };

  const reload = (obj)=>{
      let tableIndex = obj.tableIndex !== undefined?obj.tableIndex:tableIndex;
      if(tableIndex === "2"){
        let dept = obj.text !== undefined?obj.text:text;
        let dirName = obj.searchIndex !== undefined?obj.searchIndex:searchIndex;
        let pageSize = obj.pageSize !== undefined?obj.pageSize:10;
        let current = obj.current !== undefined?obj.current:1;
        dispatch({
          type:"drmselecttable/showFileTable",
          payload:{
            obj:{
              "dept":dept,
              "dirName":dirName
            },
            paper:{
              pageSize:pageSize,
              current:current
            }
          }
        });
      }else{
        let metaType = obj.tabsIndex !== undefined?obj.tabsIndex:tabsIndex;
        let dept = obj.text !== undefined?obj.text:text;
        let metaNameCn = obj.searchIndex !== undefined?obj.searchIndex:searchIndex;
        let pageSize = obj.pageSize !== undefined?obj.pageSize:10;
        let current = obj.current !== undefined?obj.current:1;
        dispatch({
          type:"drmselecttable/showSelectTable",
          payload:{
            obj:{
              "metaType" : metaType === "all"?"":metaType,
              "dept":dept,
              "metaNameCn":metaNameCn,
              "renterId": renterId,
              "sourceId":2,
              "id":id,
              "status":1,
            },
            paper:{
              pageSize:pageSize,
              current:current
            }
          }
        });
      }
  };

  const rowSelection = {
    type:"radio",
    onChange: (selectedRowKeys, selectedRows) => {
      console.log(selectedRowKeys, selectedRows,"selectedRowKeys, selectedRows");
      dispatch({
        type:"drmselecttable/showModel",
        payload:{
          selectKey:selectedRows,
          selectedRowKeys:selectedRowKeys,
        }
      });
    },
    getCheckboxProps: record => {
     /*disabled:selectedRowKeys.filter(record.metaid ) > -1*/
      return{
      disabled:record.metaid === metaid
    }}
  };

    const showTable = ()=>{
        if(tableIndex === "1"){
            return (
              <div>
                <FormItem label="">
                  {getFieldDecorator('biaoleixing', {
                    initialValue:'all',
                  })(
                    <Tabs type="card" onChange={handleTabsChange}>
                      <TabPane tab="全部"   key='all' />
                     {/* <TabPane tab="事实表" key="1" />
                      <TabPane tab="聚合表" key="2"/>
                      <TabPane tab="查找表" key="3"/>
                      <TabPane tab="维度表" key="4"/>
                      <TabPane tab="宽表" key="5"/>
                      <TabPane tab="基础数据表" key="6"/>*/}
                    </Tabs>
                  )}
                </FormItem>
                <TableList
                  showIndex
                  rowSelection={rowSelection}
                  onRowClick={(record, index, event)=>{console.log(record)}}
                  onChange={handleTableChange}
                  pagination={pagination}
                  columns={columns}
                  dataSource={data}
                  loading={loading}
                  scroll={{y: 420 }}
                  style={{height:450}}
                  selections={false}
                />
              </div>
            )
        }else {
          return(
            <TableList
              showIndex
              rowSelection={rowSelection}
              onRowClick={(record, index, event)=>{console.log(record)}}
              pagination={pagination}
              onChange={handleTableChange}
              columns={columns1}
              loading={loading}
              dataSource={data}
              scroll={{y: 420 }}
              style={{height:450}}
              selections={false}
            />
            )
        }
    };
//placeholder={tableType==='2'?"可以按数据来源、表或者文件名称代码、行业、主题、标签进行模糊搜索":'可以按文件目录名、所属组织、行业、主题、标签进行模糊搜索'}
    return(
      <Modal
        title="选择要关联的数据表"
        visible={visible}
        width={900}
        onOk={handleOk}
        onCancel={handleCancel}
      >
        <Form >
          <div className={Style.DRMselectTable}>
            {
              // showRadio()
            }
            <div>
              <div className={Style.Searchzujian}>
                <Search
                  placeholder={tableType==='2'?"可以按表中文名称、表英文名称进行模糊搜索":'可以按表中文名称、表英文名称进行模糊搜索'}
                  onSearch={e => handleSearch(e)}
                  value={searchIndex}
                />
                  <span className={Style.xuanzebumen}>
                    &nbsp;
                    <FormItem label="">
                      {getFieldDecorator('bumen', {
                        initialValue:[],
                      })(
                        <Cascader placeholder="选择组织" options={departmentsTree} onChange={xuanzebumendid}>
                        </Cascader>
                      )}
                    </FormItem>
                  </span>
              </div>
              <div className={Style.selectTable}>
                {
                  showTable()
                }
              </div>

            </div>
          </div>
        </Form>
      </Modal>
    )
};

const DRMselectTable = Form.create()(DRMselectTable1);
export default connect(({ drmselecttable,account,metadataCommon }) => ({
  drmselecttable,account,metadataCommon
}))(DRMselectTable);
