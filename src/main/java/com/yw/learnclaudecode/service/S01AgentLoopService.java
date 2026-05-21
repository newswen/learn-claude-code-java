package com.yw.learnclaudecode.service;

import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessageFunctionToolCall;
import com.openai.models.chat.completions.ChatCompletionToolMessageParam;
import com.yw.learnclaudecode.model.IModelChatService;
import com.yw.learnclaudecode.tool.AirQualityTool;
import com.yw.learnclaudecode.tool.WeatherTool;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Chapter s01 agent loop.
 * 这一章主要演示：模型如何在多轮对话中调用工具，并把工具结果继续补回上下文。
 */
@Service
public class S01AgentLoopService {

    @Resource
    private IModelChatService modelChatService;

    /**
     * 执行 s01 章节示例。
     */
    public void runChapter(String question) {
        //1.发起首次ai请求
        ChatCompletionCreateParams.Builder requestBuilder = createInitialRequestBuilder(question);
        boolean continueConversation = true;

        //2.根据是否调用工具 执行下一轮 loop为agent核心
        while (continueConversation) {
            ChatCompletion completion = requestChatCompletion(requestBuilder);
            continueConversation = !isConversationFinished(completion);
            appendAssistantMessagesAndToolResults(requestBuilder, completion);
        }
    }

    /**
     * 初始化本章示例的首轮请求，包括模型、工具和用户问题。
     */
    private ChatCompletionCreateParams.Builder createInitialRequestBuilder(String question) {
        return ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4O_MINI)
                .addTool(WeatherTool.class)
                .addTool(AirQualityTool.class)
                .addUserMessage(question);
    }

    /**
     * 发起当前轮次请求，拿到模型的最新响应。
     */
    private ChatCompletion requestChatCompletion(ChatCompletionCreateParams.Builder requestBuilder) {
        return modelChatService.chat(requestBuilder.build());
    }

    /**
     * 当 finish reason 为 stop 时，表示这一轮对话已经可以结束。
     */
    private boolean isConversationFinished(ChatCompletion completion) {
        return "stop".equals(completion.choices().get(0).finishReason().asString());
    }

    /**
     * 把模型响应加入上下文；如果模型要求调用工具，还要把工具执行结果继续补回上下文。
     */
    private void appendAssistantMessagesAndToolResults(ChatCompletionCreateParams.Builder requestBuilder,
                                                       ChatCompletion completion) {
        completion.choices().stream()
                .map(ChatCompletion.Choice::message)
                // 把每一条 assistant 消息放回请求构造器，用于维护完整对话历史。
                .peek(requestBuilder::addMessage)
                // 如果当前轮次有文本内容，就先打印出来；纯工具调用时这里通常没有文本。
                .flatMap(message -> {
                    message.content().ifPresent(System.out::println);
                    return message.toolCalls().stream().flatMap(Collection::stream);
                })
                // 执行模型要求调用的工具，并把工具结果补回上下文，供下一轮对话继续使用。
                .forEach(toolCall -> appendToolResult(requestBuilder, toolCall.asFunction()));
    }

    /**
     * 执行指定工具，并把工具返回结果追加到对话上下文。
     */
    private void appendToolResult(ChatCompletionCreateParams.Builder requestBuilder,
                                  ChatCompletionMessageFunctionToolCall functionToolCall) {
        Object toolResult = executeToolFunction(functionToolCall.function());
        requestBuilder.addMessage(ChatCompletionToolMessageParam.builder()
                .toolCallId(functionToolCall.id())
                .contentAsJson(toolResult)
                .build());
    }

    /**
     * 根据模型返回的工具名称，执行对应的本地工具方法。
     */
    private Object executeToolFunction(ChatCompletionMessageFunctionToolCall.Function function) {
        return switch (function.name()) {
            case "WeatherTool" -> function.arguments(WeatherTool.class).getWeather();
            case "AirQualityTool" -> function.arguments(AirQualityTool.class).getAirQuality();
            default -> throw new IllegalArgumentException("未知的工具：" + function.name());
        };
    }
}
