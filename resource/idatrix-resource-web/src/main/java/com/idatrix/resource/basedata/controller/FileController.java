package com.idatrix.resource.basedata.controller;

import com.idatrix.resource.basedata.service.IFileService;
import com.idatrix.resource.basedata.vo.FileVO;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件相关接口
 *
 * @author wzl
 */
@RequestMapping("file")
@Api(value = "/file", tags="基础功能-文件处理接口")
@RestController
public class FileController {

    @Autowired
    private IFileService fileService;

    @Autowired
    private UserUtils userUtils;

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ApiOperation(value = "文件上传", httpMethod = "POST")
    public Result<List<FileVO>> uploadFile(
            @RequestParam(value = "files") MultipartFile[] files,
            @ApiParam(value = "文件来源 默认为1，后续扩展使用", defaultValue = "1") Integer source)
            throws Exception {
        if (ArrayUtils.isEmpty(files)) {
            return Result.ok(new ArrayList<>());
        }
        return Result.ok(fileService
                .uploadFile(files, source, userUtils.getCurrentUserName()));
    }

    @ApiOperation(value = "删除文件", httpMethod = "DELETE")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    public Result delete(@ApiParam("文件id") Long id) throws Exception {
        fileService.delete(id);
        return Result.ok("删除成功");
    }

    @ApiOperation(value = "文件下载", httpMethod = "GET")
    @RequestMapping(value = "download", method = RequestMethod.GET)
    public Result download(HttpServletResponse response, Long id)
            throws Exception {
        String fileName = fileService.getFileById(id).getOriginFileName();
        InputStream inputStream = fileService.download(id);
        download(response, inputStream, fileName);
        return Result.ok("文件下载成功");
    }

    private void download(HttpServletResponse response, InputStream inputStream, String fileName)
            throws Exception {
        response.reset();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));

        OutputStream outputStream = response.getOutputStream();
        try {
            byte[] buf = new byte[1024 << 3];
            int bytesRead;
            while (-1 != (bytesRead = inputStream.read(buf, 0, buf.length))) {
                outputStream.write(buf, 0, bytesRead);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
