package com.green.nowon.domain.board;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.green.nowon.domain.BaseDateEntity;
import com.green.nowon.domain.member.MemberEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "comment")
@Entity
public class CommentEntity extends BaseDateEntity{
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private long cno;
	
	
	@Column(nullable = false)
	private String writer;
	
	@Column(nullable= true)
	private String content;
	
	@ManyToOne
	@JoinColumn(name = "bno", nullable = false)
	private BoardEntity bno;
	
	@ManyToOne
	@JoinColumn(name="mno", nullable = false)
	private MemberEntity no;
	
	private LocalDateTime createdDate = getCreatedDate();
}
