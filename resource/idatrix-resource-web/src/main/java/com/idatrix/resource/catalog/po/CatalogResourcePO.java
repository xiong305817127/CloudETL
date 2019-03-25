package com.idatrix.resource.catalog.po;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Robin Wing on 2018-5-28.
 */
@Getter
@Setter
public class CatalogResourcePO implements Comparable<CatalogResourcePO>{

    private Long catalogId;

    private Long resourceId;

    private int depth;

    @Override
    public int compareTo(CatalogResourcePO crPO) {
        return depth - crPO.getDepth();
    }
}
