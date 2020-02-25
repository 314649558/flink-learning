package com.hailong.flink.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 自定义source
 */
public class FlinkNacosSource extends RichSourceFunction<String> {

    Properties properties=new Properties();
    ConfigService configService;
    String config;
    String dataId="test";
    String group="DEFAULT_GROUP";
    String serverAddr="localhost";


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        properties.put("serverAddr",serverAddr);
        configService= NacosFactory.createConfigService(properties);
        config=configService.getConfig(dataId,group,5000);

        //开启监听器监听配置中心配置数据
        configService.addListener(dataId, group, new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }
            @Override
            public void receiveConfigInfo(String configMsg) {
                config=configMsg;
                System.out.println("open listener receive - - - >" +configMsg);
            }
        });

    }

    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        for(;;){
            Thread.sleep(300);
            System.out.println("obtain config infomation - - - > "+config);
            //System.out.println("obtain config infomation - - - > "+configService.getConfig(dataId,group,5000));
            ctx.collect(String.valueOf(System.currentTimeMillis()));
        }
    }

    @Override
    public void cancel() {

    }
}
