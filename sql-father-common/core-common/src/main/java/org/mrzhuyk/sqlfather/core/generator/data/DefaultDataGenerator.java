package org.mrzhuyk.sqlfather.core.generator.data;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 固定数值生成
 */
public class DefaultDataGenerator implements DataGenerate {
    @Override
    public List<String> doGenerate(TableSchema.Field field, int rowNum) {
        //模拟参数
        String mockParams = field.getMockParams();
        List<String> list = new ArrayList<>(rowNum);
        // 主键采用递增策略
        if (field.isPrimaryKey()) {
            //为空时，从1开始增加
            if (StringUtils.isBlank(mockParams)) {
                mockParams = "1";
            }
            int initValue = Integer.parseInt(mockParams);
            for (int i = 0; i < rowNum; i++) {
                list.add(String.valueOf(initValue + 1));
            }
            return list;
        }
        // 使用默认值
        String defaultValue = field.getDefaultValue();
        // 特殊逻辑，日期要伪造数据
        if ("CURRENT_TIMESTAMP".equals(defaultValue)) {
            defaultValue = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        }
        if (StringUtils.isNotBlank(defaultValue)) {
            for (int i = 0; i < rowNum; i++) {
                list.add(defaultValue);
            }
        }
        return list;
    }
}
