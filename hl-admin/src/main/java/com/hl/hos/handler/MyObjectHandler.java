package com.hl.hos.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class MyObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        //
        this.strictInsertFill(metaObject,"create_time", LocalTime.class,LocalTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
//        this.updateFill(metaObject,"create_time", LocalTime.class,LocalTime.now());
    }

































































































































































































































}
