package cn.meiracle.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Agent 知识库 / RAG 对话能力启动类（meiracle-agent）。
 *
 * <p>核心能力由 {@code com.aizuda:snail-ai-starter} 自动装配（Agent 服务、管理后台 UI、
 * gRPC、RAG 文档解析等）。本模块仅做包名收敛与配置接入，不嵌入 SnailAI 内部实现。</p>
 *
 * <p>对外提供 HTTP 管理/对话接口（端口 9404，context /agent）；向量库（Elasticsearch /
 * PostgreSQL / Milvus）在管理后台运行时配置，不在本文件硬编码。</p>
 *
 * @author meiracle
 */
@SpringBootApplication
public class MeiracleAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeiracleAgentApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  meiracle-agent 启动成功   ლ(´ڡ`ლ)ﾞ");
    }

}
