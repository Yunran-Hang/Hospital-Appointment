package com.nbdeyy.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
}
