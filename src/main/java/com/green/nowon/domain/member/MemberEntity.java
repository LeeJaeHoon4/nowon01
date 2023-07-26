package com.green.nowon.domain.member;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.green.nowon.domain.board.BoardEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table( name = "member")
@Entity
public class MemberEntity {
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private long no;
	
	@Column(unique = true, nullable=false)
	private String id;
	
	@Column(nullable=false)
	private String password;
	
	@Column(unique =  true)
	private String email;
	
	@Column(nullable = true)
	private String name;
	
	@Column(nullable = true)
	private String provider;
	
	@Builder.Default
	@Enumerated(EnumType.STRING)
	@CollectionTable(name="memberRole")
	@ElementCollection(fetch = FetchType.LAZY)
	private Set<MemberRole> role = new HashSet<>();
	
	public MemberEntity addRole(MemberRole mRole) {
		role.add(mRole);
		return this;
	}
	
	//on delete cascade 옵션 적용
	@OneToMany(mappedBy = "no", cascade = CascadeType.REMOVE)
	private List<BoardEntity> boards;
	
	public MemberEntity changePassword(String newPassword) {
		this.password = newPassword;
		return this;
	}
}
