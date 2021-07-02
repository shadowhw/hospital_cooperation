package com.hl.utils;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

public class CodeGenerator
{
    public static void codeGenerator(String moudule_name, String t_name){
        // 1.全局配置
        GlobalConfig gc = new GlobalConfig();
        //获取项目路径,因为生成java文件需要文件路径
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java")//设置java文件的输出路径
                .setAuthor("何夜息")//作者
                .setOpen(false)// 是否打开输出目录
                .setFileOverride(true)//多次生成时文件覆盖
                .setIdType(IdType.AUTO)//主键ID策略
                .setBaseResultMap(true)// 生成ResultMap
                .setBaseColumnList(true)// 生成 sql片段
                .setOpen(false) // 自动打开生成后的文件夹
                .setServiceName("%sService") //service接口开头不带I,自动匹配实体
                .setMapperName("%sMapper")
                .setXmlName("%sMapper")
                .setXmlName("%sMapper");

        // 2.数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3307/fuwaiHos?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai");
        // dsc.setSchemaName("public");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("kingsley");


        //3. 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(moudule_name)//模块名,就是父包下一级的包名
                .setParent("com.hl") //设置父包
                .setMapper("mapper") //设置mapper包
                .setService("service") //设置service包
                .setServiceImpl("service.impl")
                .setEntity("pojo"); //设置实体包

        //4. 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.no_change) //表名生成实体名策略
                .setColumnNaming(NamingStrategy.no_change) //字段生成实体属性名称策略
                .setEntityLombokModel(true) //是否使用Lombok优化代码
                .setInclude(t_name) //需要生成的表
                .setTablePrefix("t_") //表前缀 可以保证生成的实体没有表的前缀
                .setRestControllerStyle(true);//控制器类型

        //5.整合配置
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        mpg.setGlobalConfig(gc)
                .setDataSource(dsc)
                .setPackageInfo(pc)
                .setStrategy(strategy);
        try
        {
            //执行
            mpg.execute();
            System.out.println("执行成功");
        }catch (Exception e)
        {
            System.out.println(e);
        }

    }
}
