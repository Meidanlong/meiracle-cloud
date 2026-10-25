package cn.meiracle.file.export.domain.response;

import java.io.Serializable;

import lombok.Data;

/**
 * 文件下载结果（Dubbo 跨进程传输，必须可序列化）。
 *
 * @author meiracle-file
 */
@Data
public class FileDownloadResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文件字节内容 */
    private byte[] content;

    /** 文件名 */
    private String fileName;

    /** 内容类型 */
    private String contentType;
}
