package kr.board.entity;

import lombok.Data;

@Data
public class Member {
	private int memIdx;
	private String memID;
	private String memPassword;
	private String memName;
	private int memAge; // <-null, 0
	private String memGender;
	private String memEmail;
	private String memProfile; // 사진정보

	public boolean nullValueCheck() {
		if (memID == null || memID.equals(""))
			return false;
		if (memPassword == null || memPassword.equals(""))
			return false;
		if (memName == null || memName.equals(""))
			return false;
		if (memAge < 0 || memAge > 200)
			return false;
		if (memGender == null || memGender.equals(""))
			return false;
		if (memEmail == null || memEmail.equals(""))
			return false;
		return true;
	}

	public String passwordCheck(String pw1, String pw2) {
		if (!pw1.equals(pw2)) {
			return "패스워드를 일치시켜주세요.";
		}
		if (!pw1.equals(memPassword)) {
			return "비밀번호가 틀립니다.";
		}
		return null;
	}
}
