package org.mrzhuyk.sqlfather.dict.service;

import org.mrzhuyk.sqlfather.dict.po.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author mrzhu
* @description 针对表【dict(词库)】的数据库操作Service
* @createDate 2023-08-25 21:17:20
*/
public interface DictService extends IService<Dict> {

    /**
     * 校验参数，
     *
     * @param dict
     * @param add 是否为创建校验
     */
    void validAndHandleDict(Dict dict, boolean add);
}
