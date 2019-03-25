package com.idatrix.resource.report.vo.response;

import java.util.List;
import lombok.Data;

@Data
public class CommonCountVO<T> {

    private List<T> topKList;

    private Long total;

    private CommonCountVO(List<T> topKList, Long total) {
        this.topKList = topKList;
        this.total = total;
    }

    public static <T> CommonCountVO<T> of(List<T> topKList, long total) {
        return new CommonCountVO<>(topKList, total);
    }
}
