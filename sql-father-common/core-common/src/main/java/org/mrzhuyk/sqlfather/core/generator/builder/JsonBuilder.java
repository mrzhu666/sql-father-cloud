package org.mrzhuyk.sqlfather.core.generator.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;

/**
 * json格式的模拟数据
 */
public class JsonBuilder {
    /**
     * 构造数据 json
     * e.g. {"id": 1}
     *
     * @param dataList 数据列表
     * @return 生成的 json 数组字符串
     */
    public static String buildJson(List<Map<String, Object>> dataList) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(dataList);
    }
}
