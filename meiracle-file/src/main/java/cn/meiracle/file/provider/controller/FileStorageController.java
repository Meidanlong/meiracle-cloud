package cn.meiracle.file.provider.controller;

import cn.meiracle.core.domain.R;
import cn.meiracle.file.common.storage.FileStorage;
import cn.meiracle.file.export.domain.request.FileUploadRequest;
import cn.meiracle.file.export.domain.response.FileDownloadResult;
import cn.meiracle.file.export.domain.response.FileUploadResult;
import cn.meiracle.file.export.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件中心 HTTP 入口。
 *
 * <ul>
 *   <li>POST /file/upload ：直接上传（仅本地直连场景，业务间优先走 Dubbo FileService）</li>
 *   <li>GET  /file/files/{pathKey} ：按存储键读取文件流（与 LocalFileStorage 的访问前缀 /files/ 对应）</li>
 * </ul>
 *
 * @author meiracle-file
 */
@Slf4j
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileStorageController {

    private final FileService fileService;
    private final FileStorage fileStorage;

    @PostMapping("/upload")
    public R<FileUploadResult> upload(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "bizModule", required = false) String bizModule) {
        try {
            FileUploadRequest req = new FileUploadRequest();
            req.setContent(file.getBytes());
            req.setFileName(file.getOriginalFilename());
            req.setContentType(file.getContentType());
            req.setBizModule(bizModule);
            return R.ok(fileService.upload(req));
        } catch (Exception e) {
            return R.fail("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/files/**")
    public ResponseEntity<byte[]> fetch(HttpServletRequest request) {
        String pathKey = extractPathKey(request);
        FileDownloadResult result = fileService.download(pathKey);
        if (result == null || result.getContent() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
            result.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : result.getContentType()));
        headers.setContentLength(result.getContent().length);
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
            "inline; filename=\"" + (result.getFileName() == null ? "file" : result.getFileName()) + "\"");
        return new ResponseEntity<>(result.getContent(), headers, HttpStatus.OK);
    }

    private String extractPathKey(HttpServletRequest request) {
        String uri = request.getRequestURI();
        int idx = uri.indexOf("/files/");
        if (idx < 0) {
            return "";
        }
        String key = uri.substring(idx + "/files/".length());
        // 去掉查询串
        int q = key.indexOf('?');
        if (q >= 0) {
            key = key.substring(0, q);
        }
        return key;
    }
}
