-- =============================================================
-- meiracle-agent 建表 / 初始化 SQL（MySQL 8.0）
-- =============================================================
-- 说明：
--   SnailAI（aizuda）的知识库 / 会话 / 文档 / 向量库元数据等表，
--   由其自带的 storage starter（snail-ai-mysql-storage 等）在应用启动时自动建表（MyBatis-Plus / DDL），
--   无需在本文件手工维护完整表结构。
--   本文件仅做「库与占位初始化」，便于跟 meiracle-deploy 的 init/init.sql 体系保持一致。
--   若生产希望关闭自动 DDL，可改为在此维护全量 DDL（从 starter 源码导出）。
-- =============================================================

-- 若使用独立数据库（推荐，避免与业务库混用），先建库：
-- CREATE DATABASE IF NOT EXISTS `meiracle_agent`
--   DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 占位：示例初始化数据（按需取消注释并替换为真实演示数据）
-- INSERT INTO `kb_info` (`name`, `description`, `create_time`) VALUES ('demo-kb', '演示知识库', NOW());
