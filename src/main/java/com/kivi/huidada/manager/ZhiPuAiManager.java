package com.kivi.huidada.manager;

import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class ZhiPuAiManager {
    @Resource
    private ClientV4 clientV4;

    private static final Float STABLE_TEMPERATURE = 0.05f;
    private static final Float UNSTABLE_TEMPERATURE = 0.99f;

    /**
     * ai回复不稳定请求
     * @param userMessage
     * @param systemMessage
     * @return
     */
    public String doUnStableRequest(String userMessage, String systemMessage){
        return doCommonRequest(userMessage, systemMessage, UNSTABLE_TEMPERATURE);
    }

    /**
     * ai回复稳定请求
     * @param userMessage
     * @param systemMessage
     * @return
     */
    public String doStableRequest(String userMessage, String systemMessage){
        return doCommonRequest(userMessage, systemMessage, STABLE_TEMPERATURE);
    }

    /**
     * 通用请求
     * @param userMessage
     * @param systemMessage
     * @param temperature
     * @return
     */
    public String doCommonRequest(String userMessage, String systemMessage, Float temperature) {
        // 构造请求
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        messages.add(systemChatMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        messages.add(userChatMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        // 调用
        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
        return invokeModelApiResp.getData().getChoices().get(0).toString();
    }
}
