package com.yw.learnclaudecode.model.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.openai.client.OpenAIClient;
import com.openai.core.ObjectMappers;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.yw.learnclaudecode.model.IModelChatService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: yw
 * @Date: 2026/5/20 17:31
 * @Description:
 **/
@Service
public class OpenAiChatServiceImpl implements IModelChatService {

    @Resource
    private OpenAIClient openAIClient;

    /**
     * AI调用次数统计
     */
    private final AtomicInteger chatCount = new AtomicInteger(0);

    @Override
    public ChatCompletion chat(ChatCompletionCreateParams params) {

        int current = chatCount.incrementAndGet();

        printJson("========== 第 " + current + " 次请求 ==========", params._body());

        ChatCompletion completion =
                openAIClient.chat()
                        .completions()
                        .create(params);

        printJson("========== 第 " + current + " 次响应 ==========", completion);

        return completion;
    }

    private void printJson(String title, Object obj) {
        try {
            String json = ObjectMappers.jsonMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(obj);
            System.out.println(title + ":\n" + json);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("打印 JSON 失败", e);
        }
    }

}
