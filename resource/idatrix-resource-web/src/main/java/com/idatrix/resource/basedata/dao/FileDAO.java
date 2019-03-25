package com.idatrix.resource.basedata.dao;

import com.idatrix.resource.basedata.po.FilePO;
import com.idatrix.resource.basedata.vo.FileQueryVO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 附件数据访问层
 *
 * @author wzl
 */
public interface FileDAO {

    List<FilePO> getFilesBySourceAndParentIdAndCreator(@Param("source") Integer source,
            @Param("parentId") Long parentId, @Param("creator") String creator);

    int insert(FilePO filePO);

    int update(FilePO filePO);

    int batchUpdateParentIdByIds(@Param("ids") List<Long> ids, @Param("parentId") Long parentId);

    FilePO getFile(FileQueryVO queryVO);

    FilePO getFileById(@Param("id") Long id);

}
