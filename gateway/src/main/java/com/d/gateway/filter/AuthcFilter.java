package com.d.gateway.filter;

import com.d.base.Const;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@Slf4j
@Component
public class AuthcFilter extends ZuulFilter {
    @Value("${authc.include.paths}")
    private String[] includePaths;
    @Value("${authc.exclude.paths}")
    private String[] excludePaths;
    @Resource
    private StringRedisTemplate srt;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        if (needAuthc()) {
            doAuthc();
        }
        return null;
    }

    private boolean needAuthc() {
        String path = RequestContext.getCurrentContext().getRequest().getServletPath();
        return PatternMatchUtils.simpleMatch(includePaths, path) && !PatternMatchUtils.simpleMatch(excludePaths, path);
    }

    private void doAuthc() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String path = request.getRequestURI();
        String accessToken = request.getHeader(Const.TOKEN_HEAD);
        if (StringUtils.isEmpty(accessToken)) {
            deny();
            return;
        }
        Set<String> members = srt.opsForSet().members(accessToken);
        if (members == null || members.size() == 0) {
            deny();
            return;
        }
        String[] strings = new String[members.size()];
        members.toArray(strings);
        boolean match = PatternMatchUtils.simpleMatch(strings, path);
        if (!match) {
            deny();
        }
    }

    private void deny() {
        log.info("{}无权限访问路径:{} ", RequestContext.getCurrentContext().getRequest().getRemoteHost(), RequestContext.getCurrentContext().getRequest().getRequestURI());
        RequestContext.getCurrentContext().setSendZuulResponse(false);
        RequestContext.getCurrentContext().setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
