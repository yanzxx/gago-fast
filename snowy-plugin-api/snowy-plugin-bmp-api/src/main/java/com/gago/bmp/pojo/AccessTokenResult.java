package com.gago.bmp.pojo;

import lombok.Data;

/**
 * 对中台获取AccessToken结果的重新封装
 */
@Data
public class AccessTokenResult {

    private String accessToken;

    private boolean success;

    private String msg;
}
