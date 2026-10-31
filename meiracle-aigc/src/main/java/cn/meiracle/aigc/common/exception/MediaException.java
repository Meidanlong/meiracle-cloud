package cn.meiracle.aigc.common.exception;

/**
 * 媒体生成业务异常
 *
 * @author meiracle
 */
public class MediaException extends RuntimeException {

    public MediaException(String message) {
        super(message);
    }

    public MediaException(String message, Throwable cause) {
        super(message, cause);
    }

}
