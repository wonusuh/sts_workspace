package com.mybatisboard.board.entity;

import lombok.Data;

@Data
public class Member {
	private int memIdx;
	private String memID;
	private String memPassword;
	private String memName;
	private int memAge;
	private String memGender;
	private String memEmail;
	private String memProfile;
}
