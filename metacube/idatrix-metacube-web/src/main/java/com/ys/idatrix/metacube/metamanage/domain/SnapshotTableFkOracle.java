package com.ys.idatrix.metacube.metamanage.domain;

import java.util.Date;

public class SnapshotTableFkOracle {
    private Long id;

    private Integer versions;

    private Long fkId;

    private String name;

    private String columnIds;

    private Long referenceSchemaId;

    private Long referenceTableId;

    private Long referenceRestrain;

    private String referenceColumn;

    private String deleteTrigger;

    private Boolean isEnabled;

    private Long tableId;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersions() {
        return versions;
    }

    public void setVersions(Integer versions) {
        this.versions = versions;
    }

    public Long getFkId() {
        return fkId;
    }

    public void setFkId(Long fkId) {
        this.fkId = fkId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getColumnIds() {
        return columnIds;
    }

    public void setColumnIds(String columnIds) {
        this.columnIds = columnIds == null ? null : columnIds.trim();
    }

    public Long getReferenceSchemaId() {
        return referenceSchemaId;
    }

    public void setReferenceSchemaId(Long referenceSchemaId) {
        this.referenceSchemaId = referenceSchemaId;
    }

    public Long getReferenceTableId() {
        return referenceTableId;
    }

    public void setReferenceTableId(Long referenceTableId) {
        this.referenceTableId = referenceTableId;
    }

    public Long getReferenceRestrain() {
        return referenceRestrain;
    }

    public void setReferenceRestrain(Long referenceRestrain) {
        this.referenceRestrain = referenceRestrain;
    }

    public String getReferenceColumn() {
        return referenceColumn;
    }

    public void setReferenceColumn(String referenceColumn) {
        this.referenceColumn = referenceColumn == null ? null : referenceColumn.trim();
    }

    public String getDeleteTrigger() {
        return deleteTrigger;
    }

    public void setDeleteTrigger(String deleteTrigger) {
        this.deleteTrigger = deleteTrigger == null ? null : deleteTrigger.trim();
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier == null ? null : modifier.trim();
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}