package com.yw.learnclaudecode.agent;

import com.openai.models.chat.completions.ChatCompletionMessageFunctionToolCall;

/**
 * 工具执行入口：由 StageConfig 按工具名注册，AgentLoopRunner 按名分发。
 */
@FunctionalInterface
public interface ToolHandler {

    Object execute(ChatCompletionMessageFunctionToolCall.Function function);
}
