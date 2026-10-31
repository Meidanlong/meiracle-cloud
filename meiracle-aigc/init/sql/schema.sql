-- =============================================================
-- meiracle-aigc 建表 SQL（MySQL 8.0）
-- 说明：本模块自身拥有的表为 ai_usage_record 与 media_gen_task。
-- =============================================================

-- AI 用量记录表：其他模块共用时靠 module 字段区分来源
CREATE TABLE IF NOT EXISTS `ai_usage_record`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`      BIGINT       NULL DEFAULT NULL COMMENT '使用能力的用户ID（内部任务/未登录记为NULL）',
    `module`       VARCHAR(32)  NOT NULL COMMENT '所属模块：media / aigc 等',
    `provider`     VARCHAR(64)  NOT NULL COMMENT '厂商：kling / volcano / openai 等',
    `model`        VARCHAR(128) NOT NULL COMMENT '具体模型名',
    `usage_type`   VARCHAR(32)  NOT NULL COMMENT '消耗类型：token / image_count / video_seconds',
    `usage_amount` DECIMAL(20, 6) NULL DEFAULT NULL COMMENT '消耗数量（token数 / 张数 / 秒数）',
    `cost_amount`  DECIMAL(20, 6) NULL DEFAULT NULL COMMENT '折算费用（元），可空',
    `task_id`      VARCHAR(128) NULL DEFAULT NULL COMMENT '关联任务ID（厂商返回）',
    `create_dept`  BIGINT       NULL DEFAULT NULL COMMENT '创建部门',
    `create_by`    BIGINT       NULL DEFAULT NULL COMMENT '创建者',
    `create_time`  DATETIME     NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`    BIGINT       NULL DEFAULT NULL COMMENT '更新者',
    `update_time`  DATETIME     NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_module` (`module`),
    KEY `idx_provider` (`provider`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = 'AI 用量记录';

-- 媒体生成任务表（异步任务落地，便于轮询对账与失败重试）
-- 注意：当前策略实现直接查询厂商 API 获取任务状态，本表为预留落地表，
--       后续可在策略层写入任务快照/结果，进一步增强可观测性与对账能力。
CREATE TABLE IF NOT EXISTS `media_gen_task`
(
    `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `provider`     VARCHAR(64)   NOT NULL COMMENT '厂商：kling / volcano',
    `task_id`      VARCHAR(128)  NOT NULL COMMENT '厂商返回的任务ID',
    `task_type`    VARCHAR(32)   NOT NULL COMMENT '任务类型：text2image / image2image / text2video / image2video',
    `model`        VARCHAR(128)  NULL DEFAULT NULL COMMENT '模型名',
    `status`       VARCHAR(16)   NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING / RUNNING / SUCCESS / FAILED',
    `prompt`       VARCHAR(2000) NULL DEFAULT NULL COMMENT '提示词',
    `result_urls`  TEXT          NULL DEFAULT NULL COMMENT '生成结果URL（逗号分隔，JSON 亦可）',
    `fail_reason`  VARCHAR(512)  NULL DEFAULT NULL COMMENT '失败原因',
    `user_id`      BIGINT        NULL DEFAULT NULL COMMENT '发起用户ID',
    `create_dept`  BIGINT        NULL DEFAULT NULL COMMENT '创建部门',
    `create_by`    BIGINT        NULL DEFAULT NULL COMMENT '创建者',
    `create_time`  DATETIME      NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`    BIGINT        NULL DEFAULT NULL COMMENT '更新者',
    `update_time`  DATETIME      NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_provider_task` (`provider`, `task_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '媒体生成任务';
