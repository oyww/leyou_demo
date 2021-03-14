package com.leyou.order.pojo;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

@Data
public class TestNotNull {
    @NotNull
    private String name;
    @NonNull
    private String sex;
}
