package com.lezo.idober.spring;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;

import com.lezo.idober.error.CustomExceptionHandlerExceptionResolver;

@Order(1)
public class CustomHttpRequestHandlerAdapter extends HttpRequestHandlerAdapter {
	private static final Pattern RES_REG = Pattern.compile("(^/assets/)||(^/img/)||(\\.txt$)||(\\.xml$)");

	@Override
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String sPath = request.getPathInfo();
		Matcher matcher = RES_REG.matcher(sPath);
		if (matcher.find()) {
			return super.handle(request, response, handler);
		}
		ModelAndView modelAndView = new ModelAndView(CustomExceptionHandlerExceptionResolver.VIEW_404);
		return modelAndView;
	}

}
