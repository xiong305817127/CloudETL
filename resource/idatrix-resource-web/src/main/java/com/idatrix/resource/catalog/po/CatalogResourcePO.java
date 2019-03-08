package com.idatrix.resource.catalog.po;

/**
 * Created by Robin Wing on 2018-5-28.
 */
public class CatalogResourcePO implements Comparable<CatalogResourcePO>{

    private Long catalogId;

    private Long resourceId;

    private int depth;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public int compareTo(CatalogResourcePO crPO) {
        return depth - crPO.getDepth();
    }
}
