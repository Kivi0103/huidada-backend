package com.kivi.huidada.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kivi.huidada.model.entity.App;
import com.kivi.huidada.service.AppService;
import com.kivi.huidada.mapper.AppMapper;
import org.springframework.stereotype.Service;

/**
* @author Kivi
* @description 针对表【app(应用)】的数据库操作Service实现
* @createDate 2024-06-28 22:05:48
*/
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>
    implements AppService{

}




