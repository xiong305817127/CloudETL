import Modal from "components/Modal";
import TableList from "components/TableList";
import { connect } from "dva";
import { Card, message, Button } from "antd";
import styles from "./Debug.css";

const index = ({ infolog, dispatch, transheader }) => {
  const { dataSource, columns, title, previewVisible, executionId } = infolog;
  const {
    get_exec_stop,
    get_exec_resume,
    execMorePreview
  } = transheader.methods;

  const getColumns = () => {
    return columns.map(index => {
      return {
        title: index,
        dataIndex: index,
        key: index,
        width: "150px"
      };
    });
  };

  const handleCancel = () => {
    dispatch({ type: "infolog/save", payload: { previewVisible: false } });
  };

  //终止转换
  const stopTrans = () => {
    get_exec_stop({ executionId }).then(res => {
      const { code } = res.data;
      if (code === "200") {
        message.success("执行已终止");
        handleCancel();
      }
    });
  };

  //暂停转换
  const pauseTrans = () => {
    get_exec_resume({ executionId }).then(res => {
      const { code } = res.data;
      if (code === "200") {
        handleCancel();
      }
    });
  };

  //获得更多
  const getMore = () => {
    execMorePreview({ executionId }).then(res => {
      const { code } = res.data;
      if (code === "200") {
        handleCancel();
      }
    });
  };

  return (
    <Modal
      title="预览"
      visible={previewVisible}
      width={1000}
      onCancel={handleCancel}
      footer={[
        <Button key="run" onClick={stopTrans}>
          停止运行
        </Button>,
        <Button key="back" onClick={pauseTrans}>{`停止预览(${title})`}</Button>,
        <Button key="submit" type="primary" onClick={getMore}>
          获取更多行
        </Button>,
        <Button key="close" onClick={handleCancel}>
          关闭
        </Button>
      ]}
    >
      <Card title={title} className={styles.card}>
        <TableList
          dataSource={dataSource}
          columns={getColumns()}
          scroll={{ y: 600 }}
          pagination={false}
        />
      </Card>
    </Modal>
  );
};

export default connect(({ infolog, transheader }) => ({
  infolog,
  transheader
}))(index);
