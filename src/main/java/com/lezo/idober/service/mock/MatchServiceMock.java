package com.lezo.idober.service.mock;

import java.util.ArrayList;
import java.util.List;

import com.lezo.iscript.service.crawler.dto.MatchDto;
import com.lezo.iscript.service.crawler.service.MatchService;

public class MatchServiceMock implements MatchService {

	@Override
	public int batchInsertDtos(List<MatchDto> arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int batchSaveDtos(List<MatchDto> arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int batchUpdateDtos(List<MatchDto> arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<MatchDto> getDtoByIds(List<Long> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MatchDto> getMatchDtoByMatchCodes(List<Long> arg0) {
		List<MatchDto> dtoList = new ArrayList<MatchDto>();
		for (int i = 1; i <= 5; i++) {
			MatchDto dto = new MatchDto();
			dto.setImgUrl("http://img13.360buyimg.com/n1/jfs/t151/192/1231473418/79998/ca97c053/53a7e84dNe9522ccd.jpg");
			dto.setMatchCode(1L);
			dto.setProductCode("12345-" + i);
			dto.setSiteId(1001);
			dto.setProductName("name-" + i);
			dto.setTokenBrand("iphone");
			dto.setTokenCategory("手机");
			dto.setProductUrl("http://jd.com/item/" + dto.getProductCode());
			dto.setShopId(1001 + i);
			dto.setMarketPrice(1000L);
			dtoList.add(dto);
		}
		return dtoList;
	}

	@Override
	public List<MatchDto> getMatchDtoByProductCodes(Integer arg0, List<String> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
