package com.lezo.idober.error;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import com.alibaba.fastjson.JSONObject;

public class CustomExceptionHandlerExceptionResolver extends ExceptionHandlerExceptionResolver {
    public static final String KEY_ERROR_HANDLER = "KEY_"
            + CustomExceptionHandlerExceptionResolver.class.getSimpleName() + "_PATH";

    @Override
    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response,
            HandlerMethod handlerMethod, Exception exception) {
        // make freemark to decorate
        request.setAttribute(KEY_ERROR_HANDLER, "/errors/");
        ModelAndView modelAndView = super.doResolveHandlerMethodException(request, response, handlerMethod, exception);
        if (modelAndView == null && exception != null) {
            modelAndView = new ModelAndView("/errors/500");
            JSONObject errObject = new JSONObject();
            errObject.put("name", exception.getClass().getName());
            String msg = ExceptionUtils.getStackTrace(exception);
            int limit = 4;
            String splitor = "\tat ";
            String[] msgArr = msg.split(splitor, limit);
            StringBuilder sb = new StringBuilder();
            char point = '.';
            if (msgArr != null) {
                limit = limit - 1;
                for (int i = 0, size = msgArr.length; i < limit && i < size; i++) {
                    if (sb.length() > 0) {
                        sb.append(splitor);
                    }
                    String sLine = msgArr[i];
                    int index = sLine.lastIndexOf(point);
                    if (index > 1) {
                        index = sLine.lastIndexOf(point, index - 1);
                    }
                    index = index < 0 ? 0 : index;
                    sLine = sLine.substring(index, sLine.length());
                    sb.append(sLine);
                }
            }
            errObject.put("name", exception.getClass().getName());
            errObject.put("message", sb.toString());
            modelAndView.addObject("error", errObject);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return modelAndView;
    }
}
