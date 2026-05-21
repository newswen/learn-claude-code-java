package com.yw.learnclaudecode.model;

import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

/**
 * @Author: yw
 * @Date: 2026/5/20 17:24
 * @Description:
 **/
public interface IModelChatService {

    ChatCompletion chat(ChatCompletionCreateParams params);

}
