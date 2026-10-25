package cn.meiracle.file.export.domain.response;

import java.io.Serializable;

import lombok.Data;

/**
 * 文件上传结果（Dubbo 跨进程传输，必须可序列化）。
 *
 * @author meiracle-file
 */
@Data
public class FileUploadResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 存储键，后续下载/删除/取链均以此为标识 */
    private String pathKey;

    /** 可访问 url */
    private String url;

    /** 实际保存的文件名 */
    private String fileName;

    /** 文件大小（字节） */
    private long size;

    /** 内容类型 */
    private String contentType;
}
