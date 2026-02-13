package com.server.zero_down.Dto.View;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogPaginatedList<T> {

    private Integer page;            // 12-char ID from BaseEntity
    private Integer size;
    private Integer totalPage;
    private List<T> list;
}

