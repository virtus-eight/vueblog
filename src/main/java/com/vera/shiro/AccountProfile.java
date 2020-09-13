package com.vera.shiro;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountProfile implements Serializable {
    private static final long serialVersionUID = -3207880482640325843L;
    private Long id;

    private String username;

    private String avatar;

    private String email;
}
