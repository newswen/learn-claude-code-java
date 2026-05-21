package com.yw.learnclaudecode.agent;

import com.openai.models.ChatModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 某一章的外部配置：模型、工具 schema 列表、工具分发表。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageConfig {

    /**
     * 模型
     */
    private ChatModel model;

    /**
     * 工具实体
     */
    private List<Class<?>> baseTools;

    /**
     * 工具执行入口与解析
     */
    private Map<String, ToolHandler> toolHandlers;


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private ChatModel model;
        private final List<Class<?>> baseTools = new ArrayList<>();
        private final Map<String, ToolHandler> toolHandlers = new LinkedHashMap<>();

        public Builder model(ChatModel model) {
            this.model = model;
            return this;
        }

        public <T> Builder addTool(Class<T> toolClass, Function<T, ?> executor) {
            String toolName = toolClass.getSimpleName();
            baseTools.add(toolClass);
            toolHandlers.put(toolName, function -> {
                try {
                    T args = function.arguments(toolClass);
                    return executor.apply(args);
                } catch (RuntimeException e) {
                    throw new IllegalStateException("工具执行失败: " + toolName, e);
                }
            });
            return this;
        }

        public StageConfig build() {
            if (model == null) {
                throw new IllegalArgumentException("model 不能为空");
            }
            if (toolHandlers.isEmpty()) {
                throw new IllegalArgumentException("至少需要注册 1 个工具");
            }
            return new StageConfig(model, baseTools, toolHandlers);
        }
    }
}
