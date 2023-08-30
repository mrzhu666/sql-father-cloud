package org.mrzhuyk.sqlfather.dict.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.dict.po.Dict;
import org.mrzhuyk.sqlfather.dict.service.DictService;
import org.mrzhuyk.sqlfather.dict.mapper.DictMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;



/**
* @author mrzhu
* @description 针对表【dict(词库)】的数据库操作Service实现
* @createDate 2023-08-25 21:17:20
*/
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict>
    implements DictService{
    private final static Gson GSON = new Gson();
    
    /**
     * 校验添加词典请求参数
     * @param dict
     * @param add  是否为创建校验
     */
    @Override
    public void validAndHandleDict(Dict dict, boolean add) {
        if (dict == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        String content = dict.getContent();
        String name = dict.getName();
        
        Integer reviewStatus = dict.getReviewStatus();
        //判断是否为空
        if (add && StringUtils.isAnyBlank(content, name)) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        //过长
        if (StringUtils.isNotBlank(name) && name.length() > 30) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        if (StringUtils.isNotBlank(content)) {
            //过长
            if (content.length() > 20000) {
                throw new BizException(ErrorEnum.PARAMS_ERROR);
            }
            // 对 content 进行转换，验证内容
            try {
                String[] words = content.split("[,，]");
                // 移除开头结尾空格
                for (int i = 0; i < words.length; i++) {
                    words[i] = words[i].trim();
                }
                // 过滤空单词
                List<String> wordList = Arrays.stream(words)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
                // 转换为json
                dict.setContent(GSON.toJson(wordList));
            } catch (Exception e) {
                throw new BizException(ErrorEnum.PARAMS_ERROR, "内容格式错误");
            }
        }

    }
}




