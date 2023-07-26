package com.green.nowon.domain.board;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import com.green.nowon.domain.BaseDateEntity;
import com.green.nowon.domain.member.MemberEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "board")
@Getter
@Entity
public class BoardEntity extends BaseDateEntity {
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private long bno;
	
	@Column(nullable = true)
	private String content;
	
	@ManyToOne
	@JoinColumn(name = "mno")
	private MemberEntity no;
	
	//조회수 설정해주기 redis로 ip받아서 만료시간 1일로 설정 할거임
	@Column(nullable = false, columnDefinition = "int default 0") 
	private int read_count;
	
	@Column(nullable=false)
	private String subject;
	
	//삭제 글인지 아닌지 확인 (true면 보여주기 false면 안보여지게)
	@Column(nullable = false)
	private boolean isShow;
	
	@Column(nullable = false, columnDefinition = "int default 0")
	private int reportCount;
	
	@Column(nullable = false, columnDefinition = "boolean default false")
	private boolean deleted;
	
	//on delete cascade 옵션 적용
	@OneToMany(mappedBy = "bno", cascade = CascadeType.REMOVE)
	private List<CommentEntity> comments;
	
	private LocalDateTime createdDate = getCreatedDate();
	private LocalDateTime updateDate = getUpdateDate();
	
	public void setIsShowFalse() {
        this.isShow = false;
    }
	
	public void setIsShowTrue() {
        this.isShow = true;
    }
	
	
	public void increaseReadCount() {
		this.read_count++;
	}
	
	public void increaseReportCount() {
		this.reportCount++;
	}
	
	public void setDeletedTrue() {
		this.deleted = true;
	}
	
	public void setReportCount(int count) {
		this.reportCount = count;
	}
	
}
