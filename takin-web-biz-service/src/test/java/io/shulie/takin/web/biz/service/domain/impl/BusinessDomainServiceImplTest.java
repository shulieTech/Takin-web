package io.shulie.takin.web.biz.service.domain.impl;

import io.shulie.takin.web.biz.pojo.request.domain.BusinessDomainCreateRequest;
import io.shulie.takin.web.biz.service.domain.BusinessDomainService;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: fanxx
 * @Date: 2021/12/8 10:04 上午
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = BusinessDomainServiceImplTest.class)
public class BusinessDomainServiceImplTest extends TestCase {

    @Autowired
    private BusinessDomainService businessDomainService;

    @Test
    public void testCreate() {
        BusinessDomainCreateRequest createRequest = new BusinessDomainCreateRequest();
        createRequest.setName("库存域");
        businessDomainService.create(createRequest);
    }

    @Test
    public void testUpdate() {
    }

    @Test
    public void testDelete() {
    }

    @Test
    public void testDeleteById() {
    }

    @Test
    public void testList() {
    }

    @Test
    public void testListNoPage() {
    }
}