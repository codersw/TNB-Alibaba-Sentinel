package com.mango.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义响应体
 * @author shaowen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatewayResponse<R> {
    private boolean success;
    private int code;
    private String msg;
    private R data;
}
