package com.xfliu.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xfliu.dao.mapper.CardTransMapper;
import com.xfliu.dao.model.CardTrans;
import com.xfliu.service.HsTransService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping
@RestController
public class EsTestController {

    @Resource
    private HsTransService hsTransService;

    @Resource
    private CardTransMapper cardTransMapper;


    @GetMapping("queryRecord")
    public Object queryRecords(String cardNo) {

        List<CardTrans> list = new ArrayList<>();

        LocalDate localDate = LocalDate.of(2022, 10, 1);


        while (true) {

            if (localDate.isAfter(LocalDate.now())) {
                break;
            }
            if (localDate.isEqual(LocalDate.now())) {

                if (LocalDateTime.now().getHour() > 17) {
                    queryRecord(localDate.format(DateTimeFormatter.BASIC_ISO_DATE) + "_1500",list,cardNo);
                    queryRecord(localDate.format(DateTimeFormatter.BASIC_ISO_DATE),list,cardNo);

                } else {
                    queryRecord(localDate.format(DateTimeFormatter.BASIC_ISO_DATE),list,cardNo);
                }
            } else if (localDate.isAfter(LocalDate.of(2022, 10, 14))) {
                queryRecord(localDate.format(DateTimeFormatter.BASIC_ISO_DATE) + "_1500",list,cardNo);
                queryRecord(localDate.format(DateTimeFormatter.BASIC_ISO_DATE),list,cardNo);

            } else {
                queryRecord(localDate.format(DateTimeFormatter.BASIC_ISO_DATE),list,cardNo);
            }
            localDate = localDate.plusDays(1);
        }
        return JSONObject.toJSONString(list);
    }

    private  void queryRecord(String name,List<CardTrans> list,String cardNo) {
        log.info(name+"-"+cardNo);
        List<CardTrans> cardTrans = cardTransMapper.selectByCardNo("card_trans_" + name,cardNo);
        if (CollectionUtil.isNotEmpty(cardTrans)){
            list.add(cardTrans.get(0));
        }
    }

    @GetMapping("save")
    public void test() throws Exception {

        File file = new File("D:\\hs");
        File[] files = file.listFiles();
        for (File f : files) {

            hsTransService.dealFile(f);
        }

//        LocalDate localDate = LocalDate.of(2022, 10, 1);
//
//        while (true) {
//
//            if (localDate.format(DateTimeFormatter.BASIC_ISO_DATE).equals("20221114")) {
//                break;
//            }
//            if (localDate.isAfter(LocalDate.of(2022, 10, 14))) {
//                loadData(localDate.format(DateTimeFormatter.BASIC_ISO_DATE));
//                loadData(localDate.format(DateTimeFormatter.BASIC_ISO_DATE) + "_1500");
//            } else {
//                loadData(localDate.format(DateTimeFormatter.BASIC_ISO_DATE));
//            }
//            localDate = localDate.plusDays(1);
//        }

    }


    private void loadData(String name) {

        log.info("card_trans_" + name);

        PageHelper.startPage(1, 10000);

        PageInfo<CardTrans> page = new PageInfo<>(cardTransMapper.selectAll("card_trans_" + name));


        for (int i = 1; i <= page.getPages(); i++) {
            PageHelper.startPage(i, 10000);
            PageInfo<CardTrans> pageData = new PageInfo<>(cardTransMapper.selectAll("card_trans_" + name));

            try {

                hsTransService.bulkIndex(pageData.getList());
                log.info("card_trans_" + name + "--" + i);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }

}
