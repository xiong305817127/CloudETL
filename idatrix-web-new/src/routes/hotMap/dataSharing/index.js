import Styles from "./style.less";
import Chart5 from "../components/charts/chart5"
import Chart6 from "../components/charts/chart6"
import Chart7 from "../components/charts/chart7"

/*
* 布局方式
* PAGE: dataSharing
* ---------------------------
*        |            |     |
*    5   |      6     |  7  |
*        |            |     |
* ---------------------------
* 
*/
export default ()=>{
    return (
        <div className={Styles.layout_content}>
            <div className={Styles.height_init + " flex_r"}>
                <div className={Styles.left_share + " flex_c"}>

                    {/* 注意！！！！ */}
                    {/* 此处不可调用组件，只能输入方法，要用{} */}
                    {Chart5}
                </div>
                <div className={Styles.right_share+ " flex_c"}>
                    <Chart6 />
                </div>
            </div>

            {/* 第四部分 */}
            <div className={Styles.part + " " + Styles.part4 + " " + Styles.height_init + " flex_c"}>
                <Chart7 />
            </div>
        </div>
    )
}