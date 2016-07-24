package com.lezo.idober.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lezo.idober.error.NotFoundException;
import com.lezo.idober.error.ServiceException;

@Controller
public class RouteController {

	@RequestMapping(value = { "/errors/404" }, method = RequestMethod.GET)
	public String doError404() throws Exception {
		// return CustomExceptionHandlerExceptionResolver.VIEW_404;
		throw new NotFoundException();
	}

	@RequestMapping(value = { "/errors/500" }, method = RequestMethod.GET)
	public String doError500() throws Exception {
		// return CustomExceptionHandlerExceptionResolver.VIEW_500;
		throw new ServiceException();
	}

}