package com.springapp.mvc;

import com.lzp.ssm.po.ItemsCustom;
import com.lzp.ssm.service.ItemsService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;


public class AppTests {
    private ApplicationContext applicationContext;

    //在setUp这个方法得到spring容器
    @Before
    public void setUp() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
    }
    @Test
    public void testService() throws Exception {
        ItemsService service = (ItemsService) applicationContext.getBean("itemsService");
        List<ItemsCustom> list= service.findItemsList(null);
        System.out.println(list);
    }

}
