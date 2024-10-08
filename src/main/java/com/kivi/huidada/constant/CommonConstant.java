package com.kivi.huidada.constant;

/**
 * 通用常量
 */
public interface CommonConstant {

    /**
     * 升序
     */
    String SORT_ORDER_ASC = "ascend";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = " descend";

    String AI_GENERATE_QUESTIONS_SYSTEM_MESSAGE = "你是一位严谨的出题专家，我会给你如下信息：\n" +
            "\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "【【要生成的题目数】】，\n" +
            "每个题目的选项数\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来出题：" +
            "1. 要求：题目和选项尽可能地短，题目不要包含序号，每题的选项数以我提供的为主，题目不能重复。\n" +
            "2. 严格按照下面的 json 格式输出题目和选项[{\"options\":[{\"optionDesc\":\"选项内容\",\"key\":\"A\"},{\"optionDesc\":\"\",\"key\":\"B\"}],\"questionDesc\":\"题目标题\"}]\n" +
            "questionDesc 是题目描述，options 是选项，每个选项的 key 按照英文字母序（比如 A、B、C、D）以此类推，optionDesc 是选项内容\n" +
            "3.一个{\"options\":[{\"optionDesc\":\"选项内容\",\"key\":\"A\"},{\"optionDesc\":\"\",\"key\":\"B\"}],\"questionDesc\":\"题目标题\"}结构表示一道题目，生成的题目个数必须等于给定信息中的【【要生成的题目数】】\n"+
            "4. 题目标题不需要添加1，2，3，4，5等序号\n" +
            "5. 选项描述中不需要添加A,B,C,D等序号\n" +
            "6. 返回的题目列表格式必须为 JSON 数组";
    String AI_GENERATE_ANSWER_SYSTEM_MESSAGE = "你是一位严谨的判题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表：格式为 [{\"question\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1. 要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细，大于 200 字）\n" +
            "2. 严格按照下面的 json 格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\": \"评价名称\", \"resultDesc\": \"评价描述\"}\n" +
            "```\n" +
            "3. 返回格式必须为 JSON 对象\n";
}
