import React from "react";

import { Input } from "antd";
import styles from "../datauploader.less";

export default () => {
  return (
    <div className={styles.button_group}>
      <p>状态</p>
      <Input value={""} placeholder={"EX:平台服务器返回代码"} />

      <p>返回值</p>
      <Input.TextArea value={""} placeholder={"返回值"} />
    </div>
  );
};
