import { Tag } from 'antd';
import Style from './TableBaseInfo.less';

export default (props) => {
  const { data } = props;
  return (<table className={Style.table}>
    <tbody>
      <tr>
        <td className={Style.label}>表中文名：</td>
        <td>{data.table_name_cn}</td>
        <td className={Style.label}>表英文名：</td>
        <td>{data.name}</td>
      </tr>
      <tr>
        <td className={Style.label}>所属组织：</td>
        <td>{data.org}</td>
        <td className={Style.label}>创建者：</td>
        <td>{data.creator}</td>
      </tr>
      <tr>
        <td className={Style.label}>拥有者：</td>
        <td>{data.owner}</td>
        <td className={Style.label}>创建日期：</td>
        <td>{data.create_date}</td>
      </tr>
      <tr>
        <td className={Style.label}>行业：</td>
        <td>{data['industry ']}</td>
        <td className={Style.label}></td>
        <td></td>
      </tr>
      <tr>
        <td className={Style.label}>标签：</td>
        <td>
          {(data.tags || []).map(tag => <Tag color="orange">{tag}</Tag>)}
        </td>
        <td className={Style.label}></td>
        <td></td>
      </tr>
    </tbody>
  </table>);
};
