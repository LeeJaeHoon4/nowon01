package com.green.nowon.util.page;

import lombok.Getter;

@Getter
public class PagingUtils {	

	private int tot;//페이지총개수
	private int from; //출력되는 페이지 시작번호
	private int to; //출력되는 페이지 마직막 번호
	
	////////////////////////////
	///////더보기 기능구현 가능////////
	private boolean hasNext;
	private int page;
	
	/**
	 * @param page : 페이지번호
	 * @param limit : 페이지당 게시글 수
	 * @param rowCount : 총 게시글 수
	 * @param RANGE : 표현되는 페이지번호 개수 default RANGE=10
	 * @return from(출력되는 페이지 시작번호), to(출력되는 페이지 마직막 번호), tot(페이지 총 개수)
	 */
	public static PagingUtils create(int page,  int limit, int rowCount) {
		return new PagingUtils(page, limit, rowCount);
	}
	//rangerk 정해져 있지 않으면 10개씩 보이도록 설정함
	private PagingUtils(int page,  int limit, int rowCount){
		this(page, limit, rowCount, 10);	
	}
	/**
	 * @param page : 페이지번호
	 * @param limit : 페이지당 게시글 수
	 * @param rowCount : 총 게시글 수
	 * @param RANGE : 표현되는 페이지번호 개수
	 * @return from(출력되는 페이지 시작번호), to(출력되는 페이지 마직막 번호), tot(페이지 총 개수)
	 */
	public static PagingUtils create(int page,  int limit, int rowCount, int RANGE) {
		return new PagingUtils(page, limit, rowCount, RANGE);
	}
	
	private PagingUtils(int page,  int limit, int rowCount, int RANGE){
		//int LIMIT=limit;// 한 페이지에 표현되는 게시글 개수
		//int RANGE=range;// 한 페이지에 표현되는 페이지번호 개수
		this.hasNext=rowCount > limit*page?true:false;
		this.page=page;
		
		this.tot=rowCount/limit;
		if(rowCount%limit >0 ) this.tot++;//나머지가 존재하면 1증가
		
		//블럭 단위로 페이지 이동시키기 위한 rangeNO 설정
		int rangeNo=page/RANGE; //1~9:0 10:1
		if(page%RANGE > 0)rangeNo++; //1~10 : 1
		
		this.to=RANGE*rangeNo;//페이지 마지막번호
		this.from=this.to-RANGE+1;//페이지시작번호
		
		if(this.to > this.tot)this.to=this.tot;
	}
	

}