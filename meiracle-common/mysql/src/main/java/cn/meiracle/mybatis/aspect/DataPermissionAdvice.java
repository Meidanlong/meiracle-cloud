package cn.meiracle.mybatis.aspect;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 数据权限 Advice（最小可编译占位实现）。
 * <p>
 * 说明：meiracle-common 的 mysql 模块在 fork 时未携带原 RuoYi-Cloud-Plus 的完整
 * {@code DataPermissionAdvice} 逻辑，此处仅提供可编译的透传实现（不执行数据权限过滤）。
 * 若后续需要数据权限能力，应在此补齐原逻辑（参考 RuoYi-Cloud-Plus 的 DataPermissionAdvice）。
 * 注意：{@link DataPermissionPointcutAdvisor} 当前未被注册为 Spring Bean，此 Advice 不会在运行时生效。
 *
 * @author meiracle
 */
public class DataPermissionAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return invocation.proceed();
    }
}
