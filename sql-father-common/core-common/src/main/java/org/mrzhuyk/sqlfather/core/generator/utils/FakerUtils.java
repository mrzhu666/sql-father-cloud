package org.mrzhuyk.sqlfather.core.generator.utils;

import net.datafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.mrzhuyk.sqlfather.core.enums.MockParamsRandomEnum;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 随机数据生成器，根据datafaker库生成
 */
public class FakerUtils {
    
    private final static Faker ZH_FAKER = new Faker(new Locale("zh-CN"));
    
    private final static Faker EN_FAKER = new Faker();
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static String getRandomValue(MockParamsRandomEnum randomEnum) {
        String defaultValue = RandomStringUtils.randomAlphabetic(2, 6);
        if (randomEnum == null) {
            return defaultValue;
        }
        switch (randomEnum) {
            case NAME:
                return ZH_FAKER.name().name();
            case CITY:
                return ZH_FAKER.address().city();
            case URL:
                return ZH_FAKER.internet().url();
            case EMAIL:
                return ZH_FAKER.internet().emailAddress();
            case IP:
                return ZH_FAKER.internet().ipV4Address();
            case INTEGER:
                return String.valueOf(ZH_FAKER.number().randomNumber());
            case DECIMAL:
                return String.valueOf(RandomUtils.nextFloat(0,1000));
            case UNIVERSITY:
                return ZH_FAKER.university().name();
            case DATE:
                return EN_FAKER.date()
                    .between(Timestamp.valueOf("2022-01-01 00:00:00"), Timestamp.valueOf("2023-01-01 00:00:00"))
                    .toLocalDateTime().format(DATE_TIME_FORMATTER);
            case TIMESTAMP:
                return String.valueOf(EN_FAKER.date()
                    .between(Timestamp.valueOf("2022-01-01 00:00:00"), Timestamp.valueOf("2023-01-01 00:00:00"))
                    .getTime());
            case PHONE:
                return ZH_FAKER.phoneNumber().cellPhone();
            default:
                return defaultValue;
        }
    }
    
    public static void main(String[] args) {
        getRandomValue(null);
    }
}
