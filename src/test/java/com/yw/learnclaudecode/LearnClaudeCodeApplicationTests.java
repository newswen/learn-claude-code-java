package com.yw.learnclaudecode;

import com.yw.learnclaudecode.service.S01AgentLoopService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LearnClaudeCodeApplicationTests {

    @Resource
    private S01AgentLoopService s01AgentLoopService;

    @Test
    void s01AgentLoop() {
        s01AgentLoopService.runChapter("宁波今日整体情况");
    }
}
