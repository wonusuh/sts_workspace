package com.mybatisboard.board.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.mybatisboard.board.entity.Board;
import com.mybatisboard.board.mapper.BoardMapper;

@Controller
public class BoardController {
	@Autowired
	private BoardMapper mapper;

	@ModelAttribute("cp")
	public String getContextPath(HttpServletRequest request) {
		return request.getContextPath();
	}

	@GetMapping("/")
	public String home() {
		return "template";
	}

	@GetMapping("/boardList.do")
	public String boardList(Model model) {
		List<Board> list = mapper.getList();
		model.addAttribute("list", list);
		return "boardList";
	}

	@GetMapping("boardForm.do")
	public String boardForm() {
		return "boardForm";
	}

	@GetMapping("boardContent.do")
	public String boardCentent(int idx, Model model) {
		Board vo = mapper.boardContent(idx);
		mapper.boardCount(idx); // 조회수가 1 증가합니다.
		model.addAttribute("vo", vo);
		return "boardContent";
	}

	@PostMapping("/boardInsert.do")
	public String boardInsert(Board vo) {
		mapper.boardInsert(vo);
		return "redirect:/";
	}

	@GetMapping("/boardDelete.do/{idx}")
	public String boardDelete(@PathVariable("idx") int idx, Model model) {
		mapper.boardDelete(idx);
		return "redirect:/";
	}

	@GetMapping("/boardUpdateForm.do/{idx}")
	public String boardUpdateForm(@PathVariable("idx") int idx, Model model) {
		Board vo = mapper.boardContent(idx);
		model.addAttribute("vo", vo);
		return "boardUpdate";
	}

	@PostMapping("/boardUpdate.do")
	public String boardUpdate(Board vo) {
		mapper.boardUpdate(vo);
		return "redirect:/";
	}
}
