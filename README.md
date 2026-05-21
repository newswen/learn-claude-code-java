# Learn Claude Code

一个按章节拆解 Agent 基础能力的学习项目，当前以 Spring Boot + OpenAI Java SDK 为载体，逐步把“模型会说话”推进到“模型会调用工具并持续完成任务”。

## 当前进度

| 章节 | 状态 | 说明 |
| --- | --- | --- |
| `s01 Agent Loop` | 已完成 | 最小闭环：单工具 + 稳定主循环 |
| `s02 Tool Use` | 已完成 | 多工具外部注入，handler 分发表，loop 不变 |
| `s03+` | 待继续 | 规划状态、上下文压缩、权限与恢复等 |

## 第一节在讲什么

第一节不是追求“大而全”的 Agent 系统，而是先把最小但正确的循环搭起来：

1. 把用户问题发给模型
2. 读取模型回复
3. 如果模型发起工具调用，就执行真实工具
4. 把工具结果重新写回消息历史
5. 再进入下一轮推理

这一步做对了，模型才从“只会输出文本”变成“可以基于真实观察继续推进任务”。

## 建议阅读顺序

### s01

1. [docs/s01-agent-loop.md](docs/s01-agent-loop.md)
2. `src/main/java/com/yw/learnclaudecode/agent/AgentLoopRunner.java`
3. `src/main/java/com/yw/learnclaudecode/service/S01AgentLoopService.java`
4. `src/main/java/com/yw/learnclaudecode/tool/WeatherTool.java`

### s02

1. [docs/s02-tool-use.md](docs/s02-tool-use.md)
2. `src/main/java/com/yw/learnclaudecode/agent/StageConfig.java`
3. `src/main/java/com/yw/learnclaudecode/service/S02ToolUseService.java`
4. `src/main/java/com/yw/learnclaudecode/tool/AirQualityTool.java`

## 项目结构

```text
src/main/java/com/yw/learnclaudecode
├─ agent
│  ├─ AgentLoopRunner.java
│  ├─ StageConfig.java
│  └─ ToolHandler.java
├─ config
├─ model
├─ service
│  ├─ S01AgentLoopService.java
│  └─ S02ToolUseService.java
└─ tool
   ├─ WeatherTool.java
   └─ AirQualityTool.java
docs
├─ s01-agent-loop.md
└─ s02-tool-use.md
```

## 测试

自动化测试不依赖真实 OpenAI 调用：

- `src/test/java/com/yw/learnclaudecode/agent/AgentLoopRunnerTest.java`
- `src/test/java/com/yw/learnclaudecode/agent/StageConfigTest.java`

手工演示 s01/s02 时，可自行注入对应 Service 调用 `runChapter`。

## 一句话记住

Agent Loop 的本质，不是写了一个 `while`，而是把“模型的动作意图”变成“真实工具结果”，再把结果送回模型继续推理。
