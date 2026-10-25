package cn.meiracle.mybatis.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import cn.meiracle.core.exception.ServiceException;
import cn.meiracle.core.utils.ObjectUtils;
import cn.meiracle.core.utils.ServletUtils;
import cn.meiracle.mybatis.core.domain.BaseEntity;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * MP 元对象字段自动填充处理器。
 * <p>
 * 在插入/更新 {@link BaseEntity} 时自动填充 create_time / update_time / create_by / update_by。
 * 当前登录用户 ID 的获取优先级：
 * 1) Web 环境下从请求 attribute（如网关 / API-Key 过滤器写入的 {@code userId}）读取；
 * 2) 都取不到时默认填充 {@link #DEFAULT_USER_ID}（-1，表示无用户）。
 * <p>
 * 说明：本模板的 common 层不包含 Sa-Token / system 业务模块，
 * 如需按真实登录用户填充，请注入你自己的用户上下文解析实现并替换 {@link #getUserId()}。
 *
 * @author Lion Li (adapted)
 */
@Slf4j
public class InjectionMetaObjectHandler implements MetaObjectHandler {

    /**
     * 如果用户不存在默认注入 -1 代表无用户
     */
    private static final Long DEFAULT_USER_ID = -1L;

    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity baseEntity) {
                LocalDateTime current = ObjectUtils.notNull(baseEntity.getCreateTime(), LocalDateTime.now());
                baseEntity.setCreateTime(current);
                baseEntity.setUpdateTime(current);

                if (ObjectUtil.isNull(baseEntity.getCreateBy())) {
                    Long userId = getUserId();
                    if (ObjectUtil.isNotNull(userId)) {
                        baseEntity.setCreateBy(userId);
                        baseEntity.setUpdateBy(userId);
                    } else {
                        baseEntity.setCreateBy(DEFAULT_USER_ID);
                        baseEntity.setUpdateBy(DEFAULT_USER_ID);
                        baseEntity.setCreateDept(ObjectUtils.notNull(baseEntity.getCreateDept(), DEFAULT_USER_ID));
                    }
                }
            } else {
                LocalDateTime date = LocalDateTime.now();
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, date);
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, date);
                Date legacyDate = new Date();
                this.strictInsertFill(metaObject, "createTime", Date.class, legacyDate);
                this.strictInsertFill(metaObject, "updateTime", Date.class, legacyDate);
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_INTERNAL_ERROR);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity baseEntity) {
                baseEntity.setUpdateTime(LocalDateTime.now());
                Long userId = getUserId();
                baseEntity.setUpdateBy(ObjectUtil.isNotNull(userId) ? userId : DEFAULT_USER_ID);
            } else {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_INTERNAL_ERROR);
        }
    }

    /**
     * 获取当前操作用户 ID。
     * <p>
     * 默认实现从 Web 请求的 attribute 中读取（如网关 / API-Key 过滤器写入的 {@code userId}）。
     * 非 Web 环境或取不到时返回 {@code null}。
     *
     * @return 用户 ID，取不到返回 null
     */
    private Long getUserId() {
        try {
            HttpServletRequest request = ServletUtils.getRequest();
            if (ObjectUtil.isNull(request)) {
                return null;
            }
            Object value = request.getAttribute("userId");
            if (ObjectUtil.isNull(value)) {
                value = request.getAttribute("apiKeyUserId");
            }
            if (value instanceof Long l) {
                return l;
            }
            if (value instanceof Number n) {
                return n.longValue();
            }
        } catch (Exception e) {
            // 忽略：非 Web 环境或无请求上下文
        }
        return null;
    }

}
