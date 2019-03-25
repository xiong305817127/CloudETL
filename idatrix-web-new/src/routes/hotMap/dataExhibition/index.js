import Styles from "./style.less";
import Chart1 from "../components/charts/chart1";
import Chart2 from "../components/charts/chart2";
import Chart3 from "../components/charts/chart3";
import Chart4 from "../components/charts/chart4";

export default ()=>{
    return (
        <div className={Styles.layout_content}>
            <div className={Styles.height_init + " flex_c"}>
                <div className="flex_r flex_1">

                    {/* 第一部分 */}
                    <div className={Styles.part + " " + Styles.part1 + " flex_1 flex_c"}>
                        <Chart1 />
                    </div>

                    {/* 第二部分 */}
                    <div className={Styles.part + " " + Styles.part2 + " flex_2 flex_c"}>
                        {Chart2 }
                    </div>
                </div>

                {/* 第三部分 */}
                <div className={Styles.part + " " + Styles.part3 + " flex_1 flex_c"}>
                    <Chart3 />
                </div>
            </div>

            {/* 第四部分 */}
            <div className={Styles.part + " " + Styles.part4 + " " + Styles.height_init + " flex_c"}>
                {Chart4}
            </div>
        </div>
    )
}