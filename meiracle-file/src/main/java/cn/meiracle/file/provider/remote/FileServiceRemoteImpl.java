package cn.meiracle.file.provider.remote;

import cn.meiracle.core.exception.ServiceException;
import cn.meiracle.file.common.storage.FileStorage;
import cn.meiracle.file.export.domain.request.FileUploadRequest;
import cn.meiracle.file.export.domain.response.FileDownloadResult;
import cn.meiracle.file.export.domain.response.FileUploadResult;
import cn.meiracle.file.export.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * FileService 的 Dubbo 暴露实现。
 *
 * <p>仅依赖 {@link FileStorage} 抽象，不感知具体后端；存储键由本实现统一生成，
 * 格式为 {@code {bizModule}/{uuid}{suffix}}（bizModule 为空时退化为 {@code {uuid}{suffix}}）。</p>
 *
 * @author meiracle-file
 */
@Slf4j
@DubboService
@RequiredArgsConstructor
public class FileServiceRemoteImpl implements FileService {

    private final FileStorage fileStorage;

    @Override
    public FileUploadResult upload(FileUploadRequest request) {
        if (request == null || request.getContent() == null || request.getContent().length == 0) {
            throw new ServiceException("上传文件内容不能为空");
        }
        String originalName = request.getFileName() == null ? "" : request.getFileName();
        int dotIndex = originalName.lastIndexOf(".");
        String suffix = dotIndex >= 0 ? originalName.substring(dotIndex) : "";

        String bizModule = request.getBizModule();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String pathKey = (bizModule == null || bizModule.isBlank())
            ? uuid + suffix
            : bizModule + "/" + uuid + suffix;

        try (ByteArrayInputStream input = new ByteArrayInputStream(request.getContent())) {
            fileStorage.store(pathKey, input, request.getContent().length);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("文件存储失败: " + e.getMessage());
        }

        FileUploadResult result = new FileUploadResult();
        result.setPathKey(pathKey);
        result.setUrl(fileStorage.getAccessUrl(pathKey));
        result.setFileName(originalName);
        result.setSize(request.getContent().length);
        result.setContentType(request.getContentType());
        log.info("[FileService] 上传成功 pathKey={}, size={}", pathKey, result.getSize());
        return result;
    }

    @Override
    public FileDownloadResult download(String pathKey) {
        if (pathKey == null || pathKey.isBlank()) {
            throw new ServiceException("存储键不能为空");
        }
        java.io.InputStream in = fileStorage.retrieve(pathKey);
        if (in == null) {
            return null;
        }
        try {
            FileDownloadResult result = new FileDownloadResult();
            result.setContent(in.readAllBytes());
            result.setFileName(stripModule(pathKey));
            result.setContentType(guessContentType(pathKey));
            return result;
        } catch (Exception e) {
            throw new ServiceException("文件下载失败: " + e.getMessage());
        } finally {
            try {
                in.close();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public boolean delete(String pathKey) {
        if (pathKey == null || pathKey.isBlank()) {
            return false;
        }
        return fileStorage.remove(pathKey);
    }

    @Override
    public String getUrl(String pathKey) {
        if (pathKey == null || pathKey.isBlank()) {
            return null;
        }
        return fileStorage.exists(pathKey) ? fileStorage.getAccessUrl(pathKey) : null;
    }

    private String stripModule(String pathKey) {
        int idx = pathKey.lastIndexOf("/");
        return idx >= 0 ? pathKey.substring(idx + 1) : pathKey;
    }

    private String guessContentType(String pathKey) {
        String lower = pathKey.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".mp4")) return "video/mp4";
        if (lower.endsWith(".json")) return "application/json";
        return "application/octet-stream";
    }
}
