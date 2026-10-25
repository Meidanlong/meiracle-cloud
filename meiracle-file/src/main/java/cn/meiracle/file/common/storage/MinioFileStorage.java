package cn.meiracle.file.common.storage;

import java.io.InputStream;

/**
 * MinIO 存储实现（扩展点占位）。
 *
 * <p>当前为占位实现，待引入 MinIO 客户端依赖与配置（endpoint / accessKey / secretKey / bucket）后落地。
 * 通过配置 {@code meiracle.file.storage.type=minio} 切换启用，业务层无需改动。</p>
 *
 * @author meiracle-file
 */
// @Component  // 待实现后放开，并由 StorageConfig 按 type 条件注入
public class MinioFileStorage implements FileStorage {

    @Override
    public String store(String pathKey, InputStream inputStream, long size) {
        throw new UnsupportedOperationException("MinIO 存储尚未实现，请先将 meiracle.file.storage.type 设为 local");
    }

    @Override
    public InputStream retrieve(String pathKey) {
        throw new UnsupportedOperationException("MinIO 存储尚未实现，请先将 meiracle.file.storage.type 设为 local");
    }

    @Override
    public boolean remove(String pathKey) {
        throw new UnsupportedOperationException("MinIO 存储尚未实现，请先将 meiracle.file.storage.type 设为 local");
    }

    @Override
    public String getAccessUrl(String pathKey) {
        throw new UnsupportedOperationException("MinIO 存储尚未实现，请先将 meiracle.file.storage.type 设为 local");
    }

    @Override
    public boolean exists(String pathKey) {
        throw new UnsupportedOperationException("MinIO 存储尚未实现，请先将 meiracle.file.storage.type 设为 local");
    }
}
