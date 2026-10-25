package cn.meiracle.file.provider.config;

import cn.meiracle.file.common.storage.FileStorage;
import cn.meiracle.file.common.storage.LocalFileStorage;
import cn.meiracle.file.common.storage.MinioFileStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 存储后端选择配置。
 *
 * <p>通过 {@code meiracle.file.storage.type} 在 local / minio 间切换：
 * 当前默认 local（可运行）；minio 为占位实现，启用后将抛 UnsupportedOperationException 直到落地。</p>
 *
 * @author meiracle-file
 */
@Slf4j
@Configuration
public class StorageConfig {

    @Value("${meiracle.file.storage.type:local}")
    private String storageType;

    @Bean
    public FileStorage fileStorage() {
        if ("minio".equalsIgnoreCase(storageType)) {
            log.warn("[存储] 选用 MinIO（占位，尚未实现）");
            return new MinioFileStorage();
        }
        log.info("[存储] 选用本地文件系统（local）");
        return new LocalFileStorage();
    }
}
