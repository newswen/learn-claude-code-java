package com.yw.learnclaudecode;

import com.openai.models.ChatModel;
import com.yw.learnclaudecode.agent.StageConfig;
import com.yw.learnclaudecode.service.S01AgentLoopService;
import com.yw.learnclaudecode.service.S02ToolUseService;
import com.yw.learnclaudecode.tool.AirQualityTool;
import com.yw.learnclaudecode.tool.WeatherTool;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LearnClaudeCodeApplicationTests {

    @Resource
    private S01AgentLoopService s01AgentLoopService;

    @Resource
    private S02ToolUseService s02ToolUseService;

    StageConfig.Builder build = StageConfig.builder()
            .model(ChatModel.GPT_4O_MINI);

    @Test
    void s01agentLoop() {
        StageConfig config = build
                .addTool(WeatherTool.class, WeatherTool::getWeather)
                .build();
        s01AgentLoopService.runChapter("今日宁波天气", config);
    }

    @Test
    void s02toolUse() {
        StageConfig config = StageConfig.builder()
                .model(ChatModel.GPT_4O_MINI)
                .addTool(WeatherTool.class, WeatherTool::getWeather)
                .addTool(AirQualityTool.class, AirQualityTool::getAirQuality)
                .build();
        s02ToolUseService.runChapter("今日宁波整体情况", config);
    }
}
