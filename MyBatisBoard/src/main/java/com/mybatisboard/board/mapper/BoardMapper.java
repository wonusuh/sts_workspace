package com.mybatisboard.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import com.mybatisboard.board.entity.Board;

@Mapper
public interface BoardMapper {
	public List<Board> getList();

	public void boardInsert(Board vo);

	public Board boardContent(int idx);

	public void boardDelete(int idx);

	public void boardUpdate(Board Vo);

	@Update("UPDATE myboard SET count = count + 1 WHERE idx = #{idx}")
	public void boardCount(int idx);
}
