package com.asafeorneles.gymstock.specifications;

import com.asafeorneles.gymstock.entities.Category;
import com.asafeorneles.gymstock.enums.ActivityStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

public class CategorySpec {

    public static Specification<Category> nameContains(String name){
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(name)){
                return null;
            }
            return builder.like(root.get("name"), "%" + name + "%");
        };
    }

    public static Specification<Category> activityStatusEquals(ActivityStatus activityStatus){
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(activityStatus)){
                return null;
            }
            return builder.equal(root.get("activityStatus"), activityStatus);
        };
    }
}
