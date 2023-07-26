package com.green.nowon.domain.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PassChangeDTO {
 private String password;
 private String newPassword;
 private String newPasswordC;
}
