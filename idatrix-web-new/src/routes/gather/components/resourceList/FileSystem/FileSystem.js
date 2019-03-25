/**
 * Created by Administrator on 2017/9/6.
 */
/**
 * Created by Administrator on 2017/9/5.
 */
import Line from '../../common/Line';
import {connect} from 'dva';
import FileSystemList from './FileSystemList';


const FileSystem = ({location})=>{

  return(
    <div id="ResourceContent"  >
      <Line title="文件管理" size={"small"} />
      <FileSystemList location={location} />
    </div>
  )
};

export default connect()(FileSystem);
 