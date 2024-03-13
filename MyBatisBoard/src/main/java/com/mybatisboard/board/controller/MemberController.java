package com.mybatisboard.board.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.mybatisboard.board.entity.Member;
import com.mybatisboard.board.mapper.MemberMapper;

@Controller
public class MemberController {
	@Autowired
	private MemberMapper mapper;

	@ModelAttribute("cp")
	public String getContextPath(HttpServletRequest request) {
		return request.getContextPath();
	}

	@GetMapping("/memberList.do")
	String memberList(Model model) {
		List<Member> memberList = mapper.getMemberList();
		model.addAttribute("memberList", memberList);
		return "/member/list";
	}

	@GetMapping("/loginForm.do")
	String loginForm() {
		return "/member/loginForm";
	}

	@PostMapping("/login.do")
	String login(HttpSession session, Member member) {
		int check = mapper.checkMember(member);
		return "redirect:/";
	}
}
