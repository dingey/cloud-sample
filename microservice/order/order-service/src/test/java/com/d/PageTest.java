package com.d;

import com.d.base.Pager;
import com.d.order.mapper.OrderInfoMapper;
import com.d.order.model.OrderInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PageTest {
    @Autowired
    OrderInfoMapper orderInfoMapper;

    //@Test
    public void test() {
        PageHelper.startPage(1, 5);
        PageInfo pageInfo = new PageInfo(orderInfoMapper.listAll());
        System.out.println(pageInfo);

        Pager<OrderInfo> pager = Pager.copy(pageInfo);
        System.out.println(pager);
    }
}
