package com.xfliu;

import com.xfliu.dao.model.Student;
import com.xfliu.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class DatasourceDemoApplicationTests {


    @Resource
    StudentService studentService;


//    @Test
//    public void contextLoads() {
//        Student student = new Student();
//        student.setId(1);
//        student.setName("ssss");
//        studentService.save(student);
//    }


    @Test
    public void test(){

//        File file = new File("D:\\softdata\\");
//
//        File[] list = file.listFiles();

        List<File> list = new ArrayList<>();

        loopFileByPath(list,"D:\\\\softdata\\\\");

        int bufferSize = 1024 * 100;

        byte[] buf = new byte[bufferSize];

        System.out.println(list.get(0).getAbsolutePath());

        for (File f : list) {

            try (
                    Socket socket = new Socket("192.161.17.173", 8888);
                    DataInputStream dis = new DataInputStream(new FileInputStream(f.getAbsolutePath()));
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            ) {
                //自定义追加目录
                String appendPath = "test";
                dos.writeUTF( appendPath+File.separator + f.getName());
                dos.flush();

                dos.writeLong(f.length());

                dos.flush();

                int len = 0;
                while ((len = dis.read(buf)) != -1) {
                    dos.write(buf, 0, len);
                }
                dos.flush();
                dis.close();
                dos.close();
                socket.close();

                log.info(f.getName() + "---传输完成");
            } catch (Exception e) {
                log.info(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    public static void loopFileByPath(List<File> list, String path) {

        File file = new File(path);

        File[] files = file.listFiles();

        for (File f : files) {
            if (f.isFile()) {
                list.add(f);
            } else {
                loopFileByPath(list, f.getPath());
            }
        }
    }

}
