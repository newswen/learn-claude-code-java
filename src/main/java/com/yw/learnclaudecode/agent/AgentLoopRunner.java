package com.yw.learnclaudecode.agent;

import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessageFunctionToolCall;
import com.openai.models.chat.completions.ChatCompletionToolMessageParam;
import com.yw.learnclaudecode.model.IModelChatService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Agent 主循环：不关心具体工具有哪些，只按 StageConfig 中的分发表执行。
 */
@Component
public class AgentLoopRunner {

    @Resource
    private IModelChatService modelChatService;

    /**
     * 执行 agent loop。
     *
     * @param question    本轮用户输入
     * @param stageConfig 本章外部配置
     */
    public void run(String question, StageConfig stageConfig) {
        ChatCompletionCreateParams.Builder requestBuilder = createInitialRequestBuilder(question, stageConfig);
        boolean continueConversation = true;

        while (continueConversation) {
            ChatCompletion completion = requestChatCompletion(requestBuilder);
            continueConversation = !isConversationFinished(completion);
            appendAssistantMessagesAndToolResults(requestBuilder, completion, stageConfig);
        }
    }

    private ChatCompletionCreateParams.Builder createInitialRequestBuilder(String question, StageConfig stageConfig) {
        ChatCompletionCreateParams.Builder builder = ChatCompletionCreateParams.builder()
                .model(stageConfig.getModel());
        //将工具添加进来
        stageConfig.getBaseTools().forEach(builder::addTool);
        return builder.addUserMessage(question);
    }

    private ChatCompletion requestChatCompletion(ChatCompletionCreateParams.Builder requestBuilder) {
        return modelChatService.chat(requestBuilder.build());
    }

    private boolean isConversationFinished(ChatCompletion completion) {
        return "stop".equals(completion.choices().get(0).finishReason().asString());
    }

    private void appendAssistantMessagesAndToolResults(ChatCompletionCreateParams.Builder requestBuilder,
                                                       ChatCompletion completion,
                                                       StageConfig stageConfig) {
        completion.choices().stream()
                .map(ChatCompletion.Choice::message)
                .peek(requestBuilder::addMessage)
                .flatMap(message -> {
                    message.content().ifPresent(System.out::println);
                    return message.toolCalls().stream().flatMap(Collection::stream);
                })
                .forEach(toolCall -> appendToolResult(requestBuilder, toolCall.asFunction(), stageConfig));
    }

    private void appendToolResult(ChatCompletionCreateParams.Builder requestBuilder,
                                  ChatCompletionMessageFunctionToolCall functionToolCall,
                                  StageConfig stageConfig) {
        Object toolResult = executeToolFunction(functionToolCall.function(), stageConfig);
        requestBuilder.addMessage(ChatCompletionToolMessageParam.builder()
                .toolCallId(functionToolCall.id())
                .contentAsJson(toolResult)
                .build());
    }

    private Object executeToolFunction(ChatCompletionMessageFunctionToolCall.Function function,
                                       StageConfig stageConfig) {
        // 根据工具名从外部配置中获取工具执行入口
        ToolHandler handler = stageConfig.getToolHandlers().get(function.name());
        if (handler == null) {
            throw new IllegalArgumentException("未注册的工具：" + function.name());
        }
        return handler.execute(function);
    }
}
