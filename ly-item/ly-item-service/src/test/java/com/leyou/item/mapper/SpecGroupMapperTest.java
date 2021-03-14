package com.leyou.item.mapper;

import com.leyou.item.pojo.SpecGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpecGroupMapperTest {
    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Test
    public void testQueryById() throws Exception {
        SpecGroup record = new SpecGroup();
        record.setCid(76l);
        List<SpecGroup> groupList = specGroupMapper.select(record);
        groupList.forEach(specGroup -> System.err.println("specGroup.getName() = " + specGroup.getName()));
    }

}