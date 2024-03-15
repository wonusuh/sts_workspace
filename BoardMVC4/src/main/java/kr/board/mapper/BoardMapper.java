package kr.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import kr.board.entity.Board;

@Mapper // - Mybatis API
public interface BoardMapper {
	public List<Board> getLists();

	public int boardInsert(Board board);

	public Board boardContent(int idx);

	public int deleteOneBoard(int idx);

	public int boardUpdate(Board board);

	@Update("update myboard set count = count + 1 where idx = #{idx}")
	public void boardCount(int idx);
}
