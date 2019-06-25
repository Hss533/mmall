package com.mmall.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常的包
 */
@Slf4j
@Component //全局异常一定要注册到spring中
public class ExceptionResolver implements HandlerExceptionResolver{
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        log.error("{} Exception",httpServletRequest.getRequestURI(),e);
        ModelAndView modelAndView=new ModelAndView(new MappingJacksonJsonView());//TODO
        //应该是以json的形式显示出来 而不是进行页面跳转
        // 这里为什么要添加一个MappingJacksonView()??
        //当时用jackson2.x
        //这个返回的格式和约定返回的格式相同  status 还有msg
        modelAndView.addObject("status",ResponseCode.ERROR.getCode());
        modelAndView.addObject("msg","接口异常，详情请查看服务端日志的异常信息");
        modelAndView.addObject("data",e.toString());
        return modelAndView;
    }
}
/**
 *
 *
 *
 *
 * */