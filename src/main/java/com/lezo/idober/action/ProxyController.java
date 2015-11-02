package com.lezo.idober.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lezo.iscript.service.crawler.dto.ProxyHomeDto;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.ProxyHomeService;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;

//@Controller
//@RequestMapping("proxy")
public class ProxyController {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(ProxyController.class);
	private ProxyHomeService proxyHomeService = SpringBeanUtils.getBean(ProxyHomeService.class);
	private TaskPriorityService taskPriorityService = SpringBeanUtils.getBean(TaskPriorityService.class);
	private static final Comparator<ProxyHomeDto> DEL_ASC_COMPARATOR = new Comparator<ProxyHomeDto>() {
		@Override
		public int compare(ProxyHomeDto o1, ProxyHomeDto o2) {
			return o1.getIsDelete().compareTo(o2.getIsDelete());
		}
	};

	@RequestMapping("")
	public String listSource(@ModelAttribute("model") ModelMap model, @RequestParam(defaultValue = "1") Integer curPage, @RequestParam(defaultValue = "12") Integer pageSize) {
		List<ProxyHomeDto> proxyList = proxyHomeService.getProxyHomeDtoByStatus(null, null);
		Collections.sort(proxyList, DEL_ASC_COMPARATOR);
		model.addAttribute("proxyList", proxyList);
		return "proxyList";
	}

	@RequestMapping("usource")
	@ResponseBody
	public String updateSource(@ModelAttribute("model") ModelMap model, @RequestParam(defaultValue = "", value = "id") Long id, @RequestParam("homeUrl") String homeUrl,
			@RequestParam("configParser") String configParser, @RequestParam(defaultValue = "1", value = "maxPage") Integer maxPage, Integer isDelete, Integer status) {
		ProxyHomeDto dto = new ProxyHomeDto();
		dto.setConfigParser(configParser);
		dto.setHomeUrl(homeUrl);
		dto.setId(id);
		dto.setIsDelete(isDelete);
		dto.setMaxPage(maxPage);
		dto.setStatus(status);
		dto.setCreateTime(new Date());
		dto.setUpdateTime(dto.getCreateTime());
		List<ProxyHomeDto> dtoList = new ArrayList<ProxyHomeDto>();
		dtoList.add(dto);
		if (id != null) {
			proxyHomeService.batchUpdateDtos(dtoList);
		} else {
			proxyHomeService.batchInsertDtos(dtoList);
		}
		return "OK";
	}

	@RequestMapping("execsrc")
	@ResponseBody
	public String execSource(@ModelAttribute("model") ModelMap model, @RequestParam("id") Long id, @RequestParam("homeUrl") String homeUrl, @RequestParam("configParser") String configParser,
			@RequestParam(defaultValue = "1", value = "maxPage") Integer maxPage, Integer isDelete, Integer status) {
		String taskId = UUID.randomUUID().toString();
		JSONObject paramObject = new JSONObject();
		JSONUtils.put(paramObject, "maxPage", maxPage);
		JSONUtils.put(paramObject, "strategy", "ProxyCollectorStrategy");
		TaskPriorityDto taskPriorityDto = new TaskPriorityDto();
		taskPriorityDto.setBatchId(taskId);
		taskPriorityDto.setType(configParser);
		taskPriorityDto.setUrl(homeUrl);
		taskPriorityDto.setLevel(1);
		taskPriorityDto.setSource("execSource");
		taskPriorityDto.setCreatTime(new Date());
		taskPriorityDto.setUpdateTime(taskPriorityDto.getCreatTime());
		taskPriorityDto.setStatus(0);
		taskPriorityDto.setParams(paramObject.toString());
		List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>(1);
		taskList.add(taskPriorityDto);
		taskPriorityService.batchInsert(taskList);
		return "" + taskList.size();
	}

}