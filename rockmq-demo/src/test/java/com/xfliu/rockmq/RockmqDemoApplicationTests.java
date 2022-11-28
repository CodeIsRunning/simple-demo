package com.xfliu.rockmq;

import com.xfliu.rockmq.config.RocketMqProducer;
import com.xfliu.rockmq.utils.SnowflakeSequence;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RockmqDemoApplicationTests {

    @Autowired(required = false)
    private RocketMqProducer rocketProducer;

    @Test
    public void contextLoads() throws Exception {
        DefaultMQProducer producer = rocketProducer.getRocketMQProducer();
        Message repickMsg = new Message("rocketTopic", "tag", SnowflakeSequence.getSequenceNo("n"),
                "message".getBytes(RemotingHelper.DEFAULT_CHARSET));
        SendResult result = producer.send(repickMsg, producer.getSendMsgTimeout());
        if (result.getSendStatus() != null && result.getSendStatus() != SendStatus.SEND_OK) {
            log.error("发送失败:{}", "message");
        }
    }
    Socket socket = null;
    @Test
    public void context() throws IOException {

        System.out.println("启动客户端");
        socket = new Socket("192.168.91.136", 7792);
        System.out.println("客户端连接服务器成功");

        socket.getOutputStream().write("$$|ytsjfx##".getBytes());
        socket.getOutputStream().flush();

        PrintWriter pw = new PrintWriter(socket.getOutputStream());
        InputStream inputStream = socket.getInputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(buffer)) != -1) {
            System.out.println("收到服务器的数据:" + new String(buffer, 0, len,"GBK"));
        }
        System.out.println("客户端连接服务器断开");
        pw.close();

    }

}
