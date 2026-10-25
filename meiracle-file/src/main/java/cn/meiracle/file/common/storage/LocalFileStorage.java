package cn.meiracle.file.common.storage;

import cn.meiracle.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 本地文件系统存储实现（当前默认、可运行起点）。
 *
 * <p>文件按 {@code {storageDir}/{pathKey}} 落盘；访问 url 形如 {@code /files/{pathKey}}，
 * 由 {@code FileStorageController} 对外提供 HTTP 读取。</p>
 *
 * @author meiracle-file
 */
@Slf4j
@Component
public class LocalFileStorage implements FileStorage {

    @Value("${meiracle.file.storage.dir:./file-storage}")
    private String storageDir;

    /** HTTP 访问前缀（与 FileStorageController 映射保持一致） */
    private static final String ACCESS_PREFIX = "/files/";

    @Override
    public String store(String pathKey, InputStream inputStream, long size) {
        Path dest = resolve(pathKey);
        try {
            Files.createDirectories(dest.getParent());
            Files.copy(inputStream, dest, StandardCopyOption.REPLACE_EXISTING);
            log.info("[本地存储] 写入 pathKey={}, size={}, dest={}", pathKey, size, dest);
            return pathKey;
        } catch (IOException e) {
            throw new ServiceException("本地文件写入失败: " + e.getMessage());
        }
    }

    @Override
    public InputStream retrieve(String pathKey) {
        Path dest = resolve(pathKey);
        if (!Files.exists(dest)) {
            return null;
        }
        try {
            return Files.newInputStream(dest);
        } catch (IOException e) {
            throw new ServiceException("本地文件读取失败: " + e.getMessage());
        }
    }

    @Override
    public boolean remove(String pathKey) {
        Path dest = resolve(pathKey);
        try {
            return Files.deleteIfExists(dest);
        } catch (IOException e) {
            log.warn("[本地存储] 删除失败 pathKey={}, err={}", pathKey, e.getMessage());
            return false;
        }
    }

    @Override
    public String getAccessUrl(String pathKey) {
        return ACCESS_PREFIX + pathKey;
    }

    @Override
    public boolean exists(String pathKey) {
        return Files.exists(resolve(pathKey));
    }

    private Path resolve(String pathKey) {
        if (pathKey == null || pathKey.isBlank() || pathKey.contains("..")) {
            throw new ServiceException("非法存储键: " + pathKey);
        }
        return Paths.get(storageDir, pathKey);
    }
}
