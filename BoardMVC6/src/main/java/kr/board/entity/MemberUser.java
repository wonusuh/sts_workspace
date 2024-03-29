package kr.board.entity;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class MemberUser extends User {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Member member;

	public MemberUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

	public MemberUser(Member mvo) {
		super(mvo.getMemID(), mvo.getMemPassword(), mvo.getAuthList().stream()
				.map(auth -> new SimpleGrantedAuthority(auth.getAuth())).collect(Collectors.toList()));
		this.member = mvo;
	}
}
