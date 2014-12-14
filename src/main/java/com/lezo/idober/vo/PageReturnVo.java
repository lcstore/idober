package com.lezo.idober.vo;

import java.io.Serializable;
import java.util.List;

public class PageReturnVo<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer curPage = 1;
	private Integer pageSize = 1;
	private Integer totalPage = 0;
	private Integer totalRow = 0;
	private Integer nextPage = 0;
	private Integer prevPage = 0;
	private T data;

	public PageReturnVo(Integer curPage, Integer pageSize) {
		super();
		this.curPage = curPage;
		this.pageSize = pageSize;
	}

	public Integer getCurPage() {
		return curPage;
	}

	// public void setCurPage(Integer curPage) {
	// this.curPage = curPage;
	// }

	public Integer getPageSize() {
		return pageSize;
	}

	// protected void setPageSize(Integer pageSize) {
	// this.pageSize = pageSize;
	// }

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalRow(Integer totalRow) {
		this.totalRow = totalRow;
		int pageCount = this.totalRow / this.pageSize;
		if (this.totalRow % this.pageSize > 0) {
			pageCount += 1;
		}
		setTotalPage(pageCount);
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
		if (curPage < totalPage) {
			this.nextPage = this.curPage + 1;
		}
		if (curPage > 1) {
			this.prevPage = curPage - 1;
		}
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Integer getPrevPage() {
		return prevPage;
	}

	public Integer getNextPage() {
		return nextPage;
	}

	public Integer getTotalRow() {
		return totalRow;
	}

	@SuppressWarnings("unchecked")
	public static <E extends List<?>> PageReturnVo<E> convert2PageReturn(E totalList, PageReturnVo<E> pageReturnVo) {
		List<?> totalCollection = totalList;
		pageReturnVo.setTotalRow(totalList.size());
		int fromIndex = pageReturnVo.getPrevPage() * pageReturnVo.getPageSize();
		int toIndex = fromIndex + pageReturnVo.getPageSize();
		toIndex = toIndex > pageReturnVo.getTotalRow() ? pageReturnVo.getTotalRow() : toIndex;
		List<?> pageList = totalCollection.subList(fromIndex, toIndex);
		pageReturnVo.setData((E) pageList);
		return pageReturnVo;
	}
}
