package com.idatrix.resource.basedata.service;

import com.idatrix.resource.basedata.po.FilePO;
import com.idatrix.resource.basedata.vo.FileQueryVO;
import com.idatrix.resource.basedata.vo.FileVO;
import java.io.InputStream;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * 附件服务接口
 *
 * @author wzl
 */
public interface IFileService {

    List<FilePO> getFilesBySourceAndParentIdAndCreator(Integer source, Long parentId,
            String creator);

    /**
     * 文件上传
     *
     * @param files 要上传的文件
     * @param source 文件来源 若为空 则默认为1 表示服务说明文档
     * @param username 当前用户
     * @return List<FilePO>
     */
    List<FileVO> uploadFile(MultipartFile[] files, Integer source, String username)
            throws Exception;

    /**
     * 获取文件
     */
    FilePO getFile(FileQueryVO queryVO);

    /**
     * 根据id获取文件
     */
    FilePO getFileById(Long id) throws Exception;

    /**
     * 删除文件
     *
     * @param id 文件id
     */
    void delete(Long id) throws Exception;

    /**
     * 根据ids批量更新parentId
     *
     * @param ids 文件id列表
     */
    void batchUpdateParentIdByIds(List<Long> ids, Long parentId);

    /**
     * 根据文件id返回文件流
     */
    InputStream download(Long id) throws Exception;
}
