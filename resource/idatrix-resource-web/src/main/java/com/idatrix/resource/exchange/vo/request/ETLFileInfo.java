package com.idatrix.resource.exchange.vo.request;

import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.FileTransmitDto;
import lombok.Data;

import java.util.List;

/**
 * ETL文件上传SFTP处理信息
 */
@Data
public class ETLFileInfo {

    /*ETL本地文件信息*/
    private LocalFileInfo localFileInfo;


    /*ETL文件拷贝信息*/
    private List<FileTransmitDto> fileTransList;
}


