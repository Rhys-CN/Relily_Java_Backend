package com.relily;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author relily
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class RelilyApplication {
    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(RelilyApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  芮丽预约启动成功!!!");
    }
}
