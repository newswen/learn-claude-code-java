# Learn Claude Code

一个按章节拆解 Agent 基础能力的学习项目，当前以 Spring Boot + OpenAI Java SDK 为载体，逐步把“模型会说话”推进到“模型会调用工具并持续完成任务”。

## 当前进度

| 章节 | 状态 | 说明 |
| --- | --- | --- |
| `s01 Agent Loop` | 已完成 | 最小闭环：模型请求、工具调用、结果回填、继续下一轮 |
| `s02+` | 待继续 | 后续可逐步补工具路由、规划状态、上下文压缩、权限与恢复 |

## 第一节在讲什么

第一节不是追求“大而全”的 Agent 系统，而是先把最小但正确的循环搭起来：

1. 把用户问题发给模型
2. 读取模型回复
3. 如果模型发起工具调用，就执行真实工具
4. 把工具结果重新写回消息历史
5. 再进入下一轮推理

这一步做对了，模型才从“只会输出文本”变成“可以基于真实观察继续推进任务”。

## 建议阅读顺序

1. 先看章节说明：[docs/s01-agent-loop.md](docs/s01-agent-loop.md)
2. 再看实现代码：`src/main/java/com/yw/learnclaudecode/service/S01AgentLoopService.java`
3. 最后对照工具定义：
   - `src/main/java/com/yw/learnclaudecode/tool/WeatherTool.java`
   - `src/main/java/com/yw/learnclaudecode/tool/AirQualityTool.java`

## 项目结构

```text
src/main/java/com/yw/learnclaudecode
├─ config
├─ model
├─ service
│  └─ S01AgentLoopService.java
└─ tool
   ├─ WeatherTool.java
   └─ AirQualityTool.java
docs
└─ s01-agent-loop.md
```

## 一句话记住

Agent Loop 的本质，不是写了一个 `while`，而是把“模型的动作意图”变成“真实工具结果”，再把结果送回模型继续推理。
