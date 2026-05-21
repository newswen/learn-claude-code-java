# s02 Tool Use

> 工具从外部注入 | 主循环不变 | 加一个工具只加一行注册

## 这一节要解决什么问题

s01 已经把最小 agent loop 跑通了。  
s02 要回答的是：**当工具变多时，loop 还要不要改？**

答案应该是：不用。变化只发生在章节配置里。

## 先记住 3 个词

### 1. schema

`baseTools` 发给模型看的工具定义列表（OpenAI SDK 的 `addTool(Class)`）。

### 2. handler map

`toolHandlers` 是代码里的真实分发表：`工具名 -> ToolHandler`。

### 3. tool_result

和 s01 一样，工具执行结果必须作为 `tool` 消息写回 `messages`，供下一轮推理使用。

## 两层配置，一次注册

`StageConfig.addTool(...)` 在注册时同时维护：

- 工具类进入 `baseTools`（给模型看）
- 以 `toolClass.getSimpleName()` 为 key 生成 `ToolHandler`（给代码执行）

章节侧写法：

```java
StageConfig.builder()
    .model(ChatModel.GPT_4O_MINI)
    .addTool(WeatherTool.class, tool -> tool.getWeather())
    .addTool(AirQualityTool.class, tool -> tool.getAirQuality())
    .build();
```

新增工具时：**多写一行 `addTool`，主循环零改动。**

## 对应到当前项目代码

| 文件 | 职责 |
| --- | --- |
| `AgentLoopRunner.java` | 与 s01 完全相同的主循环 |
| `S02ToolUseService.java` | 注册多个工具的 `StageConfig` |

对比 `S01AgentLoopService`：唯一差别是 `addTool` 行数更多。

## 工具分发替代 switch

runner 内部不再写 `switch (function.name())`，而是：

```java
ToolHandler handler = stageConfig.toolHandlers().get(function.name());
if (handler == null) {
    throw new IllegalArgumentException("未注册的工具：" + function.name());
}
return handler.execute(function);
```

每个 `ToolHandler` 自己负责 `function.arguments(toolClass)` 和具体业务调用。

## 错误处理（教学版）

本版刻意保持“明确失败”：

| 场景 | 行为 |
| --- | --- |
| 未注册工具名 | `IllegalArgumentException`，消息含工具名 |
| `StageConfig` 缺 model 或零工具 | `build()` 时即失败 |
| 工具执行异常 | `IllegalStateException`，保留原始 cause |

不在 s02 引入 retry / recovery，避免冲淡 dispatch 主线。

## 这一节的教学边界

s02 只讲透一件事：

**工具能力靠外部配置增长，agent loop 保持稳定。**

后续章节可在此基础上叠加规划、压缩、权限等，但不应回头改 loop 形状。

## 建议阅读顺序

1. 先回顾 [s01-agent-loop.md](s01-agent-loop.md)
2. 对比 `S01AgentLoopService` 与 `S02ToolUseService` 的配置差异
3. 阅读 `StageConfig` 的 `addTool` 实现
4. 运行单元测试：`AgentLoopRunnerTest`、`StageConfigTest`

## 一句话记住

schema 给模型看，handler map 给代码路由，tool_result 给下一轮继续推理；加工具只加 `addTool`，loop 不动。
