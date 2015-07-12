package com.lezo.idober.service.mock;

import java.util.ArrayList;
import java.util.List;

import com.lezo.iscript.service.crawler.dto.SkuRankDto;
import com.lezo.iscript.service.crawler.service.SkuRankService;

public class SkuRankServiceMock implements SkuRankService {

    @Override
    public int batchInsertDtos(List<SkuRankDto> arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int batchSaveDtos(List<SkuRankDto> arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int batchUpdateDtos(List<SkuRankDto> arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<SkuRankDto> getDtoByCategoryOrBarnd(String arg0, String arg1, int arg2, int arg3) {
        List<SkuRankDto> dtoList = new ArrayList<SkuRankDto>();
        for (int i = 1; i <= 5; i++) {
            SkuRankDto dto = new SkuRankDto();
            dto.setBaiduRank(0);
            dto.setImgUrl("imgUrl");
            dto.setMatchCode(1L);
            dto.setPriceRank(9999);
            dto.setProductCode("12345");
            dto.setSiteId(1001);
            dto.setProductName("name-" + i);
            dto.setTokenBrand("iphone");
            dto.setTokenCategory("手机");
            dto.setProductUrl("http://jd.com/item/" + i);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public List<SkuRankDto> getDtoByIds(List<Long> arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<SkuRankDto> getDtoByMatchCodes(List<Long> arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
