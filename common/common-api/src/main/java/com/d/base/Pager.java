package com.d.base;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class Pager<T> implements Serializable {
    private List<T> list;
    private int pageNum;
    private int pageSize;
    private long total;

    public static Pager empty() {
        return new Pager<>().setList(Collections.emptyList());
    }

    public static <T> Pager copy(T page) {
        Pager<T> pager = new Pager<>();
        BeanUtils.copyProperties(page, pager);
        return pager;
    }
}
