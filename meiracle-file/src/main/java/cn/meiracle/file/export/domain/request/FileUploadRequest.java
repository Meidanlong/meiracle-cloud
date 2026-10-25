package cn.meiracle.file.export.domain.request;

import java.io.Serializable;

import lombok.Data;

/**
 * 文件上传请求（Dubbo 跨进程传输，必须可序列化）。
 *
 * @author meiracle-file
 */
@Data
public class FileUploadRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文件字节内容 */
    private byte[] content;

    /** 原始文件名（含后缀），用于推导存储后缀与展示名 */
    private String fileName;

    /** 内容类型，如 image/png、video/mp4 */
    private String contentType;

    /** 业务模块标识，用于分目录隔离（可选，如 user / ai / order） */
    private String bizModule;
}
