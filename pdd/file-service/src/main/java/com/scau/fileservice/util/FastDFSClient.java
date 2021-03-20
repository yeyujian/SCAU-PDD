package com.scau.fileservice.util;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @program: pdd
 * @description: fastDFS工具类
 * @create: 2020-12-06 00:37
 **/
@Component
public class FastDFSClient {

    @Autowired
    private FastFileStorageClient storage;

    /**
     * 文件上传到fastDFS
     *
     * @param file
     * @return 文件访问地址
     * @throws IOException
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 上传文件，并添加文件元数据
        //FilenameUtils.getExtension(multiFile.getOriginalFilename()) 返回文件格式
        StorePath storePath = storage.uploadFile(file.getInputStream(), file.getSize(),
                FilenameUtils.getExtension(file.getOriginalFilename()), null);

        return storePath.getGroup() + "/" + storePath.getPath();
    }

    public String uploadFile(File file) throws IOException {
        StorePath storePath = storage.uploadFile(new FileInputStream(file), FileUtils.sizeOf(file),
                FilenameUtils.getExtension(file.getName()), null);

        return storePath.getGroup() + "/" + storePath.getPath();
    }

    public String uploadImages(MultipartFile file) throws IOException {
        // 上传图片并同时生成一个缩略图
        StorePath storePath = storage.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(),
                FilenameUtils.getExtension(file.getOriginalFilename()), null);

        return storePath.getGroup() + "/" + storePath.getPath();
    }

    /**
     * 删除文件
     *
     * @param fileUrl
     */
    public boolean deleteFile(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            return false;
        }
        try {
            StorePath storePath = praseFromUrl(fileUrl);
            //System.out.println(storePath);
            storage.deleteFile(storePath.getGroup(), storePath.getPath());
            return true;
        } catch (FdfsUnsupportStorePathException e) {
            e.getMessage();
            return false;
        }
    }

    public static StorePath praseFromUrl(String filePath) {
        Validate.notNull(filePath, "解析文件路径不能为空", new Object[0]);
        int groupStartPos = getGroupStartPos(filePath);
        String groupAndPath = filePath.substring(groupStartPos);
        int pos = groupAndPath.indexOf("/");
        if (pos > 0 && pos != groupAndPath.length() - 1) {
            String group = groupAndPath.substring(0, pos);
            String path = groupAndPath.substring(pos + 1);
            return new StorePath(group, path);
        } else {
            throw new FdfsUnsupportStorePathException("解析文件路径错误,有效的路径样式为(group/path) 而当前解析路径为".concat(filePath));
        }
    }

    private static int getGroupStartPos(String filePath) {
        int pos = filePath.indexOf("group");
        if (pos == -1) {
            throw new FdfsUnsupportStorePathException("解析文件路径错误,被解析路径没有包含group,当前解析路径为".concat(filePath));
        } else {
            return pos;
        }
    }
}
