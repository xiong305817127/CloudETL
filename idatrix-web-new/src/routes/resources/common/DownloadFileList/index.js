
import { Button,Input,Row,Col,Form } from 'antd';
import { connect } from 'dva';
import styles from './index.less';
import { ListDownloadApi } from 'services/DirectoryOverview';
import TableList from "components/TableList";
import { withRouter } from 'react-router';
import {API_BASE_CATALOG } from 'constants';
import {downloadFile } from 'utils/utils';

/*const fakeDataUrl = 'https://randomuser.me/api/?results=5&inc=name,gender,email,nat&noinfo';*/
const FormItem = Form.Item;

class LoadMoreList extends React.Component {
  state = {
    loading: true,
    loadingMore: false,
    showLoadingMore: true,
    data: [],
     pagination:{
	    page:1,
	    pageSize:10
	 },
	 total:0,
	  fields: {
        name: { value: '',},
      },
  }
	  
  Columns = [
	 {
	    title: '文件名称',
	    dataIndex: 'pubFileName',
	    key: 'pubFileName',
	    width:"30%",
	    render:(text,record)=>{
	    	return(
				<a  onClick={()=>{ this.headClickDolwe(record) }}>{text}</a>
			)
	    }
	  },{
	    title: '文件大小',
	    dataIndex: 'fileSize',
	    key: 'fileSize',
	    width:"10%"
	  },{
	    title: "更新时间",
	    dataIndex: 'updateTime',
	    key: 'updateTime',
	    width:"25%"
	  },{
	      title: '文件下载',
	      key: 'oprater',
	      dataIndex: 'oprater',
	      render:(text,record)=>{
          return(
            <a onClick={()=>{ this.headClickDolwe(record) }}>文件下载</a>
          )
		}
	  }
	];

	//点击下载
	headClickDolwe(record){
    // let obj={};
    // obj.fileId =record.id;
    // ListDownloadApi(obj).then((res) => {
    //   const { code, data, msg } = res.data;
     // if(code === "200"){
        downloadFile(`${API_BASE_CATALOG}/dataUpload/download?`+"fileId="+ record.id);
    //   }else{
    //     message.success(msg);
    //   }
    // })
		 
	}

	
  render() {
    const {form,location,router,downloadfileListModel}=this.props;
    const {getFieldDecorator} = form;
    const { loading, total, data } = downloadfileListModel;
    const { query } = location;
    
    /*查询接口*/
    const require=()=>{
      form.validateFields((err, values) => {
        if (err) {
          return;
        }
        for(let index of Object.keys(query)){
          delete query[index]
        };
         let val = values.catalogId;
        for(let index of Object.keys(values)){
          if(values[index]){
            query[index] = values[index]
          }else{
            delete query[index]
          }
                
      }
     
        query.page = 1;
        router.push({...location,query})
      })
    }
    const formItemLayout1 = {
      labelCol: {span:3},
      wrapperCol:{span:18},
    }
    
    const {name} = query;

    return (
    	<div className={styles.downloadContent}>
    	   <Form>
    	      <Row gutter={24}>
            <Col span={20} style={{ display:'block'}}>
                <FormItem label="文件名称" {...formItemLayout1} style={{marginTop:10}}>
                  {getFieldDecorator("name",{
                     initialValue:name?name:"",
                  })(
                    <Input/>
                  )}
                </FormItem>
            </Col>
                    <Col span={20} className="form-btn-style">
                      <Button type="primary" onClick={require} style={{float:'right',margin: '0px 0px 15px 0px'}}> 查询</Button>
                    </Col>
                 </Row>
    	   </Form>
    		   <TableList 
		          showIndex
		          loading={loading}
		          columns={this.Columns}
		          dataSource={data}
              pagination={{total: total}}
		          useRouter={false}
		        />
     	</div>
    );
  }
}

export default connect(({ downloadfileListModel,mysubscriptionsModel })=>({ downloadfileListModel,mysubscriptionsModel}))(withRouter(Form.create()(LoadMoreList)));
