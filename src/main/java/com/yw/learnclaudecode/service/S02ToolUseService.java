package com.yw.learnclaudecode.service;

import com.openai.models.ChatModel;
import com.yw.learnclaudecode.agent.AgentLoopRunner;
import com.yw.learnclaudecode.agent.StageConfig;
import com.yw.learnclaudecode.tool.AirQualityTool;
import com.yw.learnclaudecode.tool.WeatherTool;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * Chapter s02 tool use.
 * 这一章主要演示：工具能力从外部注入，主循环不变，新增工具只需多一行 addTool。
 */
@Service
public class S02ToolUseService {

    @Resource
    private AgentLoopRunner agentLoopRunner;

    /**
     * 执行 s02 章节示例。
     */
    public void runChapter(String question, StageConfig config) {
        agentLoopRunner.run(question, config);
    }
}
