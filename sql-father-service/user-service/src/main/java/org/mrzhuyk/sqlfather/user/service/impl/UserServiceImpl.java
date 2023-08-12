package org.mrzhuyk.sqlfather.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mrzhuyk.sqlfather.user.po.User;
import org.mrzhuyk.sqlfather.user.service.UserService;
import org.mrzhuyk.sqlfather.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author mrzhu
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-08-11 20:51:26
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




