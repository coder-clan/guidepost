package org.coderclan.guidepost.demo.jpa;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TODO change me.
 *
 * @author aray(dot)chou(dot)cn(at)gmail(dot)com
 * @date 2023/1/16
 */
@Entity
@Table(name = "user")
@Data
public class UserPo {
    @Id
    private Integer id;
    private String name;
}
