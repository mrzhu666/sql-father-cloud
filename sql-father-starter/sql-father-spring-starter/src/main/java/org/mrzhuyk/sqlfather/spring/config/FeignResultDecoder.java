package org.mrzhuyk.sqlfather.spring.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;
import org.mrzhuyk.sqlfather.core.entity.Result;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 将远程接口的Result封装解耦
 */
public class FeignResultDecoder implements Decoder {
    static ObjectMapper mapper = new ObjectMapper();
    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        if (response.body() == null) {
            throw new DecodeException(response.status(), "没有返回有效的数据", response.request());
        }
        TypeFactory typeFactory = mapper.getTypeFactory();
        //这里留个坑，如果type就是BaseResponse类型，就不需要合并泛型，只要按实际转下即可，读者可以自己实现
        Result result = mapper.readValue(response.body().asInputStream(),
            typeFactory.constructParametricType(Result.class, typeFactory.constructType(type)));
        
        //如果返回错误，且为内部错误，则直接抛出异常
        if (result.getCode() != ErrorEnum.SUCCESS.getResultCode()) {
            //throw new DecodeException(response.status(), "远程接口返回错误：" + result.getMessage(), response.request());
            throw new BizException(response.status(), result.getMessage());
        }
        return result.getData();
    }
}
