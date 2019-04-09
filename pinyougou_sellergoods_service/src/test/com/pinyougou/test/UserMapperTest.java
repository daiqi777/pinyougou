package com.pinyougou.test;

import com.pinyougou.mapper.UserMapper;
import com.pinyougou.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-*.xml")
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;



    @Test
    public  void  findById(){
        User user = userMapper.selectByPrimaryKey(24);
        System.out.println(user);
    }

    @Test
    public  void  findAll(){
        List<User> userList = userMapper.select(null);
        for (User user : userList) {
            System.out.println(user);
        }
    }
}
