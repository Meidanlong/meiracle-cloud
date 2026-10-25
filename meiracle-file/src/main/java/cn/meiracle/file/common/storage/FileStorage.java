package cn.meiracle.file.common.storage;

import java.io.InputStream;

/**
 * 文件存储抽象。所有存储后端（本地、MinIO、OSS 等）统一实现该接口，
 * 业务层（FileService 实现）仅依赖该抽象，不感知具体后端。
 *
 * <p>扩展点：新增 {@code MinioFileStorage} / {@code OssFileStorage} 实现本接口，
 * 并通过 {@code meiracle.file.storage.type} 配置切换即可，无需改动业务代码。</p>
 *
 * @author meiracle-file
 */
public interface FileStorage {

    /**
     * 存储文件。
     *
     * @param pathKey      存储键（调用方生成或由实现生成后回填）
     * @param inputStream  文件输入流
     * @param size         文件大小（字节）
     * @return 实际存储键（可能与入参一致）
     */
    String store(String pathKey, InputStream inputStream, long size);

    /**
     * 读取文件流。
     *
     * @param pathKey 存储键
     * @return 文件输入流；不存在时返回 null
     */
    InputStream retrieve(String pathKey);

    /**
     * 删除文件。
     *
     * @param pathKey 存储键
     * @return 是否删除成功
     */
    boolean remove(String pathKey);

    /**
     * 生成可访问 url。
     *
     * @param pathKey 存储键
     * @return 可访问 url
     */
    String getAccessUrl(String pathKey);

    /**
     * 判断文件是否存在。
     *
     * @param pathKey 存储键
     * @return 是否存在
     */
    boolean exists(String pathKey);
}
