package com.xfliu;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xfliu.dao.mapper.CardTransMapper;
import com.xfliu.dao.model.CardTrans;
import com.xfliu.service.HsTransService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsDemoApplicationTests {

    @Resource
    private HsTransService hsTransService;

    @Resource
    private CardTransMapper cardTransMapper;

    @Test
    public void contextLoads() throws Exception {

       // hsTransService.createIndex();
        //loadData();

//        List<CardTrans> result = cardTransMapper.selectAll("card_trans_20221002");
//
//        result.stream().forEach(s->{
//            try {
//                hsTransService.indexEs(s);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });


//        List<CardTrans> result = cardTransMapper.selectAll("card_trans");
//
//
//        int applyIdSelectSize = 10000;
//        int limit = (result.size() + applyIdSelectSize - 1) / applyIdSelectSize;
//
//
//        Stream.iterate(0, n -> n + 1).limit(limit).forEach(a -> {
//            //获取后面1000条中的前500条
//            // 拿到这个参数的流的 （a * applyIdSelectSize）后面的数据  .limit（applyIdSelectSize）->后面数据的500条  .collect(Collectors.toList()->组成一个toList
//            List<CardTrans> paperEntityList = result.stream().skip(a * applyIdSelectSize).limit(applyIdSelectSize).collect(Collectors.toList());
//
//            try {
//                hsTransService.bulkIndex(paperEntityList);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//        });

        //hsTransService.indexDelete("hs_trans");


    }
    @Test
    public void loadFileData()throws Exception{
        File file = new File("D:\\hs");
        File[] files =  file.listFiles();
        for (File f: files){

            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(f));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);

            String line = "";
            int i=0;
            int size = 50000;
            List<CardTrans> retryList = new ArrayList<>();

            while ((line = reader.readLine()) != null) {

                retryList.add(JSONObject.parseObject(line,CardTrans.class));
                if (retryList.size()==size){
                    hsTransService.bulkIndex(retryList);
                    log.info(""+i++);
                    log.info(f.getName());
                    retryList.clear();
                }
            }
        }
    }

    @Test
    public void queryRecord() {
        LocalDate localDate = LocalDate.of(2022, 10, 1);

        while (true) {

            if (localDate.format(DateTimeFormatter.BASIC_ISO_DATE).equals("20221117")) {
                break;
            }
            if (localDate.isAfter(LocalDate.of(2022, 10, 14))) {
                queryRecord(localDate.format(DateTimeFormatter.BASIC_ISO_DATE));
                queryRecord(localDate.format(DateTimeFormatter.BASIC_ISO_DATE) + "_1500");
            } else {
                queryRecord(localDate.format(DateTimeFormatter.BASIC_ISO_DATE));
            }
            localDate = localDate.plusDays(1);
        }
    }

    private void queryRecord(String name) {

        String cardNo = "0000000028";
        List<CardTrans> cardTrans = cardTransMapper.selectByCardNo("card_trans_" + name,cardNo);
        if (CollectionUtil.isNotEmpty(cardTrans)){
            log.info( name+"-"+JSONObject.toJSONString(cardTrans.get(0)));
        }
    }


    @Test
    public void loadData() {
        LocalDate localDate = LocalDate.of(2022, 11, 1);

        while (true) {

            if (localDate.format(DateTimeFormatter.BASIC_ISO_DATE).equals("20221117")) {
                break;
            }
            if (localDate.isAfter(LocalDate.of(2022, 10, 14))) {
                loadData(localDate.format(DateTimeFormatter.BASIC_ISO_DATE));
                loadData(localDate.format(DateTimeFormatter.BASIC_ISO_DATE) + "_1500");
            } else {
                loadData(localDate.format(DateTimeFormatter.BASIC_ISO_DATE));
            }
            localDate = localDate.plusDays(1);
        }
    }

    private void loadData(String name) {

        log.info("card_trans_" + name);

        PageHelper.startPage(1, 100000);

        PageInfo<CardTrans> page = new PageInfo<>(cardTransMapper.selectAll("card_trans_" + name));

        File file = new File("D:\\hs2" + "\\card_trans_" + name + ".txt");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 1; i <= page.getPages(); i++) {
            PageHelper.startPage(i, 100000);
            PageInfo<CardTrans> pageData = new PageInfo<>(cardTransMapper.selectAll("card_trans_" + name));

            try {


                for(CardTrans c : pageData.getList()){
                    fileWriter.write(JSONObject.toJSONString(c) + System.lineSeparator());
                }
                fileWriter.flush();

                //hsTransService.bulkIndex(pageData.getList());
                log.info("card_trans_" + name + "--" + i);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        IoUtil.close(fileWriter);


    }


}
