package com.mybatisboard.board.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.mybatisboard.board.entity.Member;

@Mapper
public interface MemberMapper {
	List<Member> getMemberList();

	int checkMember(Member member);
}
