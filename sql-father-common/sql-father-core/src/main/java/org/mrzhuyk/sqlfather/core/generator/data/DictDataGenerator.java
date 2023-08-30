package org.mrzhuyk.sqlfather.core.generator.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;

import java.util.ArrayList;
import java.util.List;

/**
 * 词典随机
 */
public class DictDataGenerator implements DataGenerate {
    private final static Gson GSON = new Gson();
    
    @Override
    public List<String> doGenerate(TableSchema.Field field, int rowNum) {
        String dictContent = field.getMockParams();
        
        if (StringUtils.isBlank(dictContent)) {
            throw new BizException(ErrorEnum.PARAMS_ERROR,"词典为空");
        }
        // json字符串转换
        List<String> wordList = GSON.fromJson(dictContent, new TypeToken<List<String>>() {
        }.getType());
        
        List<String> list = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            String randomStr = wordList.get(RandomUtils.nextInt(0, wordList.size()));
            list.add(randomStr);
        }
        return list;
    }
}
