-- ============================================================================
-- meiracle-cloud 初始化库表
-- ----------------------------------------------------------------------------
-- 容器首次启动时会自动执行本文件（挂载到 /docker-entrypoint-initdb.d）。
-- 包含两个库：
--   meiracle          -> meiracle-demo 业务样板库
--   meiracle_monitor  -> meiracle-monitor 变动追踪库
-- 表结构由对应实体的 @TableName / 字段定义而来，仅作模板演示，无真实业务字段。
-- 注意：生产环境请用迁移工具（如 Flyway / Liquibase）管理，而非依赖自动初始化。
-- ============================================================================

CREATE DATABASE IF NOT EXISTS `meiracle` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `meiracle_monitor` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ===================== meiracle-demo =====================
USE `meiracle`;

CREATE TABLE IF NOT EXISTS `demo_thing` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`        VARCHAR(128) DEFAULT NULL COMMENT '名称',
  `status`      INT          DEFAULT 0  COMMENT '状态：0 禁用 / 1 启用',
  `description` VARCHAR(512) DEFAULT NULL COMMENT '描述',
  `create_dept` BIGINT       DEFAULT NULL COMMENT '创建部门',
  `create_by`   BIGINT       DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_by`   BIGINT       DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='示例事物表（模板演示）';

-- 简单用户示例表：演示 HTTP / Dubbo provider / Dubbo consumer 三种链路
CREATE TABLE IF NOT EXISTS `demo_user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`        VARCHAR(128) DEFAULT NULL COMMENT '用户名',
  `email`       VARCHAR(255) DEFAULT NULL COMMENT '邮箱',
  `create_dept` BIGINT       DEFAULT NULL COMMENT '创建部门',
  `create_by`   BIGINT       DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_by`   BIGINT       DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简单用户示例表（模板演示）';

-- 种子数据：便于启动后直接验证 GET /demo/user/1 与 GET /demo/user/dubbo/1 两条链路
INSERT INTO `demo_user` (`name`, `email`, `create_time`)
SELECT 'demo_alice', 'alice@example.com', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `demo_user` WHERE `name` = 'demo_alice');
INSERT INTO `demo_user` (`name`, `email`, `create_time`)
SELECT 'demo_bob', 'bob@example.com', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `demo_user` WHERE `name` = 'demo_bob');

-- ===================== meiracle-monitor =====================
USE `meiracle_monitor`;

CREATE TABLE IF NOT EXISTS `monitor_change_record` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_domain`      VARCHAR(128) DEFAULT NULL COMMENT '业务域标识（由接入方自定义）',
  `biz_key`         VARCHAR(128) DEFAULT NULL COMMENT '实体主键',
  `change_type`     INT          DEFAULT NULL COMMENT '变更类型：1 新增 / 2 更新 / 3 删除',
  `before_snapshot` LONGTEXT     DEFAULT NULL COMMENT '变更前快照（JSON）',
  `after_snapshot`  LONGTEXT     DEFAULT NULL COMMENT '变更后快照（JSON）',
  `create_dept`     BIGINT       DEFAULT NULL COMMENT '创建部门',
  `create_by`       BIGINT       DEFAULT NULL COMMENT '创建者',
  `create_time`     DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_by`       BIGINT       DEFAULT NULL COMMENT '更新者',
  `update_time`     DATETIME     DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_biz_domain` (`biz_domain`),
  KEY `idx_biz_key` (`biz_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='变动追踪记录表（模板演示）';
