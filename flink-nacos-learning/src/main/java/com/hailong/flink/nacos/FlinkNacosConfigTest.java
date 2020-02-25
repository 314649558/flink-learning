package com.hailong.flink.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Properties;

/**
 * Created by 袁海龙 on 2020/2/26.
 * https://www.bilibili.com/video/av90742627
 */
public class FlinkNacosConfigTest {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env=StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(ParameterTool.fromArgs(args));
        //测试的时候设置为1，可以方便查看输出日志
        env.setParallelism(1);
        String serverAddr="localhost";
        String dataId="test";
        String group = "DEFAULT_GROUP";
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        ConfigService configService = NacosFactory.createConfigService(properties); //服务配置中心
        String content = configService.getConfig(dataId, group, 5000); //获取配置
        System.out.println("config content - - ->  "+content);
        //定义一个source
        DataStream ds=env.addSource(new FlinkNacosSource());
        //输出到控制台
        ds.print();
        env.execute();
    }
}
