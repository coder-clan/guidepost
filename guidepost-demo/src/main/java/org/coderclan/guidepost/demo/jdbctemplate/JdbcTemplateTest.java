package org.coderclan.guidepost.demo.jdbctemplate;

import org.coderclan.guidepost.demo.jpa.UserPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TODO change me.
 *
 * @author aray(dot)chou(dot)cn(at)gmail(dot)com
 * @date 2023/1/17
 */
@Component
public class JdbcTemplateTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    public void query() {
        jdbcTemplate.query("select id,name from user ", new RowMapper<UserPo>() {
            @Override
            public UserPo mapRow(ResultSet resultSet, int i) throws SQLException {
                UserPo user = new UserPo();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                return user;
            }
        }).stream().forEach(System.out::println);
    }

    @Transactional(readOnly = false)
    public void insert() {
        jdbcTemplate.update("insert into user(id,name) values(?,?)", 999, "Chris");
    }

    @Transactional(readOnly = false)
    public void delete() {
        jdbcTemplate.update("delete from user where id=?", 999);
    }
}
