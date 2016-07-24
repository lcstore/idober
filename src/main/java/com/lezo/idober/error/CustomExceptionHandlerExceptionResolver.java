package com.lezo.idober.error;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import com.alibaba.fastjson.JSONObject;

public class CustomExceptionHandlerExceptionResolver extends DefaultHandlerExceptionResolver {
	public static final String KEY_ERROR_HANDLER = "KEY_"
			+ CustomExceptionHandlerExceptionResolver.class.getSimpleName() + "_PATH";
	public static final String VIEW_404 = "/errors/404";
	public static final String VIEW_500 = "/errors/500";

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		// make freemark to decorate
		request.setAttribute(KEY_ERROR_HANDLER, "/errors/");
		int statusCode = parseStatusCode(ex);
		ModelAndView modelAndView = new ModelAndView();
		if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
			modelAndView.setViewName(VIEW_404);
		} else {
			modelAndView.setViewName(VIEW_500);
			JSONObject errObject = new JSONObject();
			errObject.put("name", ex.getClass().getName());
			String msg = ExceptionUtils.getStackTrace(ex);
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
			errObject.put("name", ex.getClass().getName());
			errObject.put("message", sb.toString());
			modelAndView.addObject("error", errObject);
		}
		response.setStatus(statusCode);
		return modelAndView;
	}

	private int parseStatusCode(Exception ex) {
		ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(ex.getClass(),
				ResponseStatus.class);
		if (responseStatus != null) {
			return responseStatus.code().value();
		}
		int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		if (ex instanceof NoSuchRequestHandlingMethodException) {
			statusCode = HttpServletResponse.SC_NOT_FOUND;
		}
		else if (ex instanceof HttpRequestMethodNotSupportedException) {
			statusCode = HttpServletResponse.SC_NOT_FOUND;
		}
		else if (ex instanceof HttpMediaTypeNotSupportedException) {
			statusCode = HttpServletResponse.SC_NOT_FOUND;
		}
		else if (ex instanceof HttpMediaTypeNotAcceptableException) {
			statusCode = HttpServletResponse.SC_NOT_FOUND;
		}
		else if (ex instanceof MissingPathVariableException) {
			statusCode = HttpServletResponse.SC_NOT_FOUND;
		}
		else if (ex instanceof MissingServletRequestParameterException) {
			statusCode = HttpServletResponse.SC_NOT_FOUND;
		}
		else if (ex instanceof ServletRequestBindingException) {
			statusCode = HttpServletResponse.SC_NOT_FOUND;
		}
		else if (ex instanceof ConversionNotSupportedException) {
			statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}
		else if (ex instanceof TypeMismatchException) {
			statusCode = HttpServletResponse.SC_NOT_FOUND;
		}
		else if (ex instanceof HttpMessageNotReadableException) {
			statusCode = HttpServletResponse.SC_NOT_FOUND;
		}
		else if (ex instanceof HttpMessageNotWritableException) {
			statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}
		else if (ex instanceof MethodArgumentNotValidException) {
			statusCode = HttpServletResponse.SC_NOT_FOUND;
		}
		else if (ex instanceof MissingServletRequestPartException) {
			statusCode = HttpServletResponse.SC_NOT_FOUND;
		}
		else if (ex instanceof BindException) {
			statusCode = HttpServletResponse.SC_NOT_FOUND;
		}
		else if (ex instanceof NoHandlerFoundException) {
			statusCode = HttpServletResponse.SC_NOT_FOUND;
		}
		return statusCode;
	}
}
