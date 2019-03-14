import React from "react";
import { Form, Select, Button } from "antd";
import { connect } from "dva";

const Option = Select.Option;

const EtlAuth = ({ dispatch, form, roles, currentRole }) => {
  const { getFieldDecorator } = form;
  const handleSubmit = () => {
    const { validateFields } = form;
    validateFields((errors, values) => {
      if (!errors) {
        dispatch({
          type: "resourcecontent/setRole",
          payload: {
            key: "SuperPrivilegeRoleId",
            value: values.authlist,
            others: roles.find(val => val.id === values.authlist)
          }
        });
      }
    });
  };
  return (
    <div style={{ padding: 16 }}>
      <Form>
        <Form.Item label={<span>选择一个管理员</span>}>
          {getFieldDecorator("authlist", {
            initialValue: currentRole.others ? currentRole.others.id : "",
            rules: [{ required: true, message: "至少选择一个管理员" }]
          })(
            <Select style={{ width: 120 }}>
              {roles.map(val => {
                return (
                  <Option
                    value={val.id}
                    key={"role" + val.id}
                    disabled={!val.isActive}
                  >
                    {val.name}
                  </Option>
                );
              })}
            </Select>
          )}
        </Form.Item>

        <Button onClick={handleSubmit}>保存修改</Button>
      </Form>
    </div>
  );
};

export default connect(({ resourcecontent }) => {
  return {
    roles: resourcecontent.roles,
    currentRole: resourcecontent.currentRole
  };
})(Form.create()(EtlAuth));
