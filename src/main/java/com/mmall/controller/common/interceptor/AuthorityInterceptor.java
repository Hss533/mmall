package com.mmall.controller.common.interceptor;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor{
    @Override
    //进入controller之前
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        log.info("preHandler");
        HandlerMethod handlerMethod=(HandlerMethod) o;
        String  methodName=handlerMethod.getMethod().getName();
        String className=handlerMethod.getBean().getClass().getSimpleName();

        //解析参数 key value
        StringBuffer requestParamBuffer=new StringBuffer();
        Map paramMap=httpServletRequest.getParameterMap();
        Iterator it=paramMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry entry=(Map.Entry)it.next();
            String mapKey=(String)entry.getKey();

            String  mapValue= StringUtils.EMPTY;

            //request这个参数map,里面的value返回的是一个String
            Object object=entry.getValue();
            if(object instanceof String[])
            {
                String[] strs=(String[])object;
                mapValue= Arrays.toString(strs);
            }
            requestParamBuffer.append(mapKey).append("=").append(mapValue);

        }
        if(StringUtils.equals(className,"UserManageController")&&StringUtils.equals(methodName,"login")){
            log.info("权限拦截器拦截到请求 className:{},methodName:{}",className,methodName);
            //如果拦截到登录请求 不打印参数 因为参数中有密码 全部会打印到日志中，万一日志泄露了
            return true;
        }
        log.info("权限拦截器拦截到请求 className:{},methodName:{} param:{}",className,methodName);
        User user=null;
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isNotEmpty(loginToken)){
            String userJsonStr= RedisShardedPoolUtil.get(loginToken);
            user= JsonUtil.string2Object(userJsonStr,User.class);

        }
        if(user==null || (user.getRole().intValue()!= Const.Role.ROLE_ADMIN)){

            //返回false ，不会controller中的方法

            httpServletResponse.reset();//这里要添加reset 否则会报异常 getWriter()
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            //设置返回值的类型

            PrintWriter out=httpServletResponse.getWriter();

            //上传由于富文本的空间要求 要特殊处理返回值 这里面区分是否登录  要还是否有权限
            if(user==null)
            {
                if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richText_upload"))
                {
                    Map resultMap= Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","请登录管理员");
                    out.print(JsonUtil.obj2String(resultMap));
                }else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户未登录")));
                }
                out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户未登录")));
            }
            else {
                if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richText_upload"))
                {
                    Map resultMap= Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","无权限操作");
                    out.print(JsonUtil.obj2String(resultMap));
                }
                out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户无权限操作")));
            }
            out.flush();
            out.close();

            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

        log.info("postHandler");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("afterCompletion");
    }

}
