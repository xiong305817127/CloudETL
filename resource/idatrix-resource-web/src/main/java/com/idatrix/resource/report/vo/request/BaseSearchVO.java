package com.idatrix.resource.report.vo.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 基础查询VO
 *
 * @author wzl
 */
@ApiModel
@Data
public class BaseSearchVO {

    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private static final String PATTERN = "yyyy-MM";

    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private static final String ZERO = "0";

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long renterId;

    @ApiModelProperty(value = "开始时间", hidden = true)
    private String startTime;

    @ApiModelProperty(value = "结束时间", hidden = true)
    private String endTime;

    @ApiModelProperty(value = "年月, 例如2019-01")
    private String years;

    @ApiModelProperty("默认为10")
    private Integer topK;

    public BaseSearchVO() {
        this.topK = 10;
    }

    /**
     * 根据年月设置开始时间，结束时间
     */
    public void setStartTimeAndEndTime() {
        if (StringUtils.isNoneBlank(years)) {
            this.setStartTime(years);
            this.setEndTime(getNextMonth(years));
        }

    }

    public String getNextMonth(String repeatDate) {
        String lastMonth;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dft = new SimpleDateFormat(PATTERN);
        int year = Integer.parseInt(repeatDate.substring(0, 4));
        String monthsString = repeatDate.substring(5, 7);
        int month;
        if (ZERO.equals(monthsString.substring(0, 1))) {
            month = Integer.parseInt(monthsString.substring(1, 2));
        } else {
            month = Integer.parseInt(monthsString.substring(0, 2));
        }
        cal.set(year, month, Calendar.DATE);
        lastMonth = dft.format(cal.getTime());
        return lastMonth;
    }
}
