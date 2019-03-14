import React from "react";
import style from "./style.less";
import { Progress } from "antd";
import createFragment from "react-addons-create-fragment";
import { resources } from "../../configs";

const number = resources.total;
const perData = resources.variation;

// 数字转为array
const number_to_arr = num => {
  let arr = (new Array(10)).map(()=>0);
  let start = 0;
  const str = num.toString();
  const length = str.length;

  if (length > 10) {
    return {
      list:  (new Array(10)).map(()=>9),
      start: 10 - length
    };
  } else {
    start = 10 - length;
    for (let i of str) {
      arr[start++] = i;
    }
    return {
      list: arr,
      start: 10 - length
    };
  }
};

const data = number_to_arr(number);

export default createFragment({

  // 为了复用复用父级组件样式，使用fragment
  first: (
    <div className="flex_1 padding_16 flex_c" style={{ paddingBottom: 0 }}>
      <div className="flex_1">
        <p className={style.baseFont + " padding_8"}>统计数据（条数）</p>
      </div>
      <div className="flex_2 flex_c">
        <ul className={style.number_list + " flex_r flex_1"}>

          {/* 将转化后的数组显示为数字 */}
          {data.list.map((val, index) => {
            return (
              <li key={val + index}>
                <div className={style.wrapped_div}>
                  <div className={style.num_back} />
                  <div
                    className={style.num}
                    style={{ opacity: index < data.start ? 0.4 : 1 }}
                  >
                    {index < data.start ? 0 : val}
                  </div>
                </div>
              </li>
            );
          })}
        </ul>
      </div>
    </div>
  ),

  // 为了复用复用父级组件样式，使用fragment
  second: (
    <div className="flex_1 padding_16 flex_c" style={{ paddingTop: 0 }}>
      <div>
        <p
          className={style.baseFont + " padding_8"}
          style={{ paddingBottom: 0 }}
        >
          资源格式数量及占比
        </p>
      </div>
      <div className="flex_2 flex_c">
        <ul className={style.number_list + " flex_r flex_1"}>
          {perData.map((val, index) => {
            return (
              <li
                key={val[0] + index}
                style={{ padding: "8px", textAlign: "center" }}
                className={style.init_progress}
              >
                <Progress
                  strokeColor="#81d5f8"
                  format={percent => (
                    <div>
                      <span
                        className={style.baseColor}
                        style={{ display: "block", fontSize: "14px" }}
                      >
                        {percent}%
                      </span>
                      <span
                        className={style.baseColor_1}
                        style={{ display: "block", fontSize: "8px" }}
                      >
                        {val[1]}
                      </span>
                    </div>
                  )}
                  percent={(100 * (val[1] / number)).toFixed(3)}
                  type="dashboard"
                  width={80}
                />
                <p
                  className={style.baseColor}
                  style={{ textAlign: "center", fontSize: "10px" }}
                >
                  {val[0]}
                </p>
              </li>
            );
          })}
        </ul>
      </div>
    </div>
  )
});
