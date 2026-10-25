package cn.meiracle.file.export.service;

import cn.meiracle.file.export.domain.request.FileUploadRequest;
import cn.meiracle.file.export.domain.response.FileDownloadResult;
import cn.meiracle.file.export.domain.response.FileUploadResult;

/**
 * 文件中心对外暴露的 Dubbo 服务契约。
 *
 * <p>该接口是 meiracle-file 与调用方（如各业务模块）之间的唯一契约边界：
 * 调用方通过 {@code cn.meiracle:meiracle-file} 依赖即可引用本接口与 DTO，
 * 无需感知底层存储实现（本地 / 未来 MinIO）。</p>
 *
 * @author meiracle-file
 */
public interface FileService {

    /**
     * 上传文件。
     *
     * @param request 上传请求（含文件字节、原始文件名、内容类型、业务模块）
     * @return 上传结果（含存储键 pathKey、可访问 url、大小等）
     */
    FileUploadResult upload(FileUploadRequest request);

    /**
     * 根据存储键下载文件。
     *
     * @param pathKey 上传时返回的存储键
     * @return 下载结果（含文件字节、文件名、内容类型）
     */
    FileDownloadResult download(String pathKey);

    /**
     * 根据存储键删除文件。
     *
     * @param pathKey 存储键
     * @return 是否删除成功
     */
    boolean delete(String pathKey);

    /**
     * 根据存储键获取可访问 url。
     *
     * @param pathKey 存储键
     * @return 可访问 url（不可访问或不存在时返回 null）
     */
    String getUrl(String pathKey);
}
