package kr.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.board.entity.Board;
import kr.board.mapper.BoardMapper;

@RequestMapping("/boards")
@RestController // @ResponseBody(JSON)응답
public class BoardController {

//	@RequestMapping("/boardMain.do")
//	public String main() {
//		return "board/main"; 
//	}

	@Autowired
	BoardMapper boardMapper;

	// @ResponseBody->jackson-databind(객체를->JSON 데이터포멧으로 변환)
	@GetMapping("/all")
	public List<Board> boardList() {
		List<Board> list = boardMapper.getLists();
		return list; // JSON 데이터 형식으로 변환(API)해서 리턴(응답)하겠다.
	}

	// @RequestMapping("/boardInsert.do")
	@PostMapping("/new")
	public String boardInsert(@ModelAttribute Board board) {
		System.out.println("board = " + board);
		String msg = board.toString();
		int result = boardMapper.boardInsert(board); // 등록성공
		return result == 1 ? "게시글 추가 완료." + msg : "게시글 추가 실패." + msg;
	}

	@DeleteMapping("/{idx}")
	public String deleteOneBoard(@PathVariable("idx") int idx) {
		int result = boardMapper.deleteOneBoard(idx);
		return result == 1 ? "성공" : "실패";
	}

	@PutMapping("/update")
	public String boardUpdate(@RequestBody Board board) {
		System.out.println(board);
		board.setIdx(board.getIdx());
		int result = boardMapper.boardUpdate(board);
		return result == 1 ? "성공" : "실패";
	}

	@GetMapping("/{idx}")
	public Board boardContent(@PathVariable("idx") int idx) {
		Board board = boardMapper.boardContent(idx);
		return board; // vo -> JSON
	}

	@PutMapping("/count/{idx}")
	public Board boardCount(@PathVariable("idx") int idx) {
		boardMapper.boardCount(idx);
		Board vo = boardMapper.boardContent(idx);
		return vo;
	}
}
