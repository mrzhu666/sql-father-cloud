package org.mrzhuyk.sqlfather.core.generator.data;

import com.mifmif.common.regex.Generex;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;

import java.util.ArrayList;
import java.util.List;

/**
 * 正则表达式数据生成
 */
public class RuleDataGenerator implements DataGenerate{
    @Override
    public List<String> doGenerate(TableSchema.Field field, int rowNum) {
        String mockParams = field.getMockParams();
        List<String> list = new ArrayList<>(rowNum);
        Generex generex = new Generex(mockParams);
        for (int i = 0; i < rowNum; i++) {
            String random = generex.random();
            list.add(random);
        }
        return list;
    }
}
