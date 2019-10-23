package com.mango.gateway.command;

import com.alibaba.csp.sentinel.command.CommandHandler;
import com.alibaba.csp.sentinel.command.CommandRequest;
import com.alibaba.csp.sentinel.command.CommandResponse;
import com.alibaba.csp.sentinel.command.annotation.CommandMapping;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.mango.gateway.util.SpringContextUtils;
import com.mango.gateway.service.RouteService;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.net.URLDecoder;

/**
 * 修改路由网关
 * @author shaowen
 */
@CommandMapping(name = "gateway/updateRoutes", desc = "Update gateway routes")
public class UpdateGatewayRoutesCommandHandler implements CommandHandler<String>{

    private RouteService routeService = SpringContextUtils.getBean(RouteService.class);

    @Override
    public CommandResponse<String> handle(CommandRequest request) {
        String data = request.getParam("data");
        if (StringUtil.isBlank(data)) {
            return CommandResponse.ofFailure(new IllegalArgumentException("Bad data"));
        }
        try {
            data = URLDecoder.decode(data, "utf-8");
        } catch (Exception e) {
            RecordLog.info("Decode gateway Routes data error", e);
            return CommandResponse.ofFailure(e, "decode gatewayRoutes data error");
        }
        RecordLog.info("[API Server] Receiving data change (type: gateway Routes): {0}", data);
        try {
            routeService.update(JSON.parseObject(data, RouteDefinition.class));
        } catch (Exception e) {
            e.printStackTrace();
            RecordLog.info("update gateway Routes data error", e);
        }
        return CommandResponse.ofSuccess(SUCCESS_MSG);
    }

    private static final String SUCCESS_MSG = "success";
}
