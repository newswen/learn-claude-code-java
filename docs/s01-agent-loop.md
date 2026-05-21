# s01 Agent Loop

> 最小闭环 | 最基础的一节 | 重点不是“能输出”，而是“能继续做”

## 这一节要解决什么问题

语言模型本身只会生成下一段内容。

它不会自己打开文件、运行命令、观察报错，也不会天然把工具结果带回下一轮继续推理。  
所以必须由一层外部代码把下面这条回路串起来：

```text
messages
  -> model
  -> tool_use
  -> execute tool
  -> tool_result
  -> append messages
  -> next turn
```

如果少了“工具结果重新进入消息历史”这一步，模型就只是会说，不是真的会做。

## 先记住 4 个词

### 1. loop

这里的 loop 不是“无意义死循环”，而是：

只要任务还没结束，系统就重复执行同一套步骤。

### 2. turn

turn 就是一轮交互。最小版本里，一轮通常包含：

1. 把当前消息发给模型
2. 读取模型回复
3. 如有工具调用则执行工具
4. 把工具结果写回历史
5. 再进入下一轮

### 3. tool_result

`tool_result` 不是打印在控制台的一行日志，而是一个要重新写回上下文、供模型下一轮读取的结果块。

### 4. state

state 是循环继续往下走时一直要带着的运行状态。  
最小形态至少要有：

- `messages`
- 当前是否继续
- 为什么继续到下一轮

## 最小心智模型

```text
user message
   |
   v
 LLM
   |
   +-- 普通回答 ----------> 结束
   |
   +-- tool_use ----------> 执行工具
                              |
                              v
                         tool_result
                              |
                              v
                         写回 messages
                              |
                              v
                         下一轮继续
```

真正关键的不是 `while` 本身，而是：

**assistant 回复要回写，tool result 也要回写。**

## 关键数据结构

### Message

消息历史不是展示层聊天记录，而是下一轮推理的工作上下文。

### Tool Result Block

最小教学抽象里，可以理解为：

```json
{
  "type": "tool_result",
  "tool_use_id": "tool-call-id",
  "content": "tool output"
}
```

`tool_use_id` 的作用是让模型知道：这条结果对应的是它刚才哪一次工具调用。

### Loop State

教学版最小状态可以理解成：

```json
{
  "messages": [],
  "turn_count": 1,
  "transition_reason": "tool_result"
}
```

这一节先只需要理解一种继续原因就够了：  
因为刚执行完工具，所以要继续下一轮。

## 对应到当前项目代码

当前实现文件：`src/main/java/com/yw/learnclaudecode/service/S01AgentLoopService.java`

### 1. 准备初始请求

`createInitialRequestBuilder` 负责初始化模型、工具和用户问题。

### 2. 发起模型请求

`requestChatCompletion` 负责把当前上下文发给模型。

### 3. 进入最小循环

`runChapter` 里的 `while (continueConversation)` 就是 s01 的主循环。

### 4. 先把 assistant 响应写回上下文

`appendAssistantMessagesAndToolResults` 里先执行：

- `requestBuilder.addMessage(...)`

这一步很重要。  
如果只关心最终答案、不保存 assistant 消息，下一轮上下文就会断掉。

### 5. 执行工具并回填结果

当模型返回工具调用后：

1. `executeToolFunction` 根据工具名执行本地工具
2. `appendToolResult` 用 `toolCallId` 把结果追加回上下文

这就是“真实世界结果回流模型”的关键一步。

## 为什么文案里说“1 个工具”，代码里却有 2 个

教学上说“1 个工具”，强调的是先理解最小闭环，不要一开始把复杂度堆太满。

而当前项目代码为了演示“同一个 loop 可以承接多个工具调用”，实际挂了：

- `WeatherTool`
- `AirQualityTool`

这不改变 s01 的本质：  
核心仍然是“模型发起调用 -> 本地执行 -> 结果回填 -> 下一轮继续”。

## 初学者最容易犯的错

### 1. 只打印工具结果，不回写消息历史

这样模型下一轮看不到真实执行结果。

### 2. 只保存 user，不保存 assistant

这样上下文会断层，模型无法稳定接着刚才继续做。

### 3. 工具结果不绑定调用 ID

模型会分不清结果对应的是哪次调用。

### 4. 第一章就把 streaming、恢复、并发、压缩全塞进来

这样会把最核心的主线冲淡。  
s01 最重要的事情只有一件：先把最小回路跑通。

### 5. 把 messages 当作聊天展示层

在 Agent 里，`messages` 更像“下一轮工作的输入缓存”。

## 这一节的教学边界

s01 刻意只讲透一件事：

**Agent 之所以从“会说”变成“会做”，是因为模型输出能走到工具，工具结果又能回到下一轮模型输入。**

所以这一节先不展开：

- streaming
- retry
- budget
- recovery
- permission
- hook / task system

这些都可以在后续章节继续叠加，但不应该抢走第一节的核心。

## 一句话记住

Agent Loop 的本质，是把“模型的动作意图”变成“真实执行结果”，再把结果送回模型继续推理。
