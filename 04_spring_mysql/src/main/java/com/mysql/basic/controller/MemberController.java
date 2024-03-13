package com.mysql.basic.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mysql.basic.entity.Member;
import com.mysql.basic.repository.MemberDAO;

@Controller
public class MemberController {

	@Autowired
	MemberDAO memberDAO;

	@ModelAttribute("cp")
	public String getContextPath(HttpServletRequest request) {
		return request.getContextPath();
	}

	// get, post, put , delete 모든 값들이 허용가능하다
	@RequestMapping(value = "/member/userMenu", method = RequestMethod.GET)
	public String userMenu() {
		return "/member/userMenu";
	}

	@GetMapping("/member/list")
	public String list(Model model) {
		ArrayList<Member> memberList = memberDAO.getMemberList();

		model.addAttribute("memberList", memberList);
		return "/member/list";
	}

	// 회원 가입 페이지로 이동하는 메서드
	@RequestMapping("/member/joinForm")
	public String joinForm() {
		return "/member/joinForm"; // 회원 가입을 입력할 수 있는 폼 페이지의 이름을 반환합니다.
	}

	// 회원 가입을 처리하는 메서드
	@PostMapping("/member/joinPro")
	public String joinPro(Member member) {
		memberDAO.memberJoin(member);
		return "redirect:/member/list";
	}

	@GetMapping("/member/loginForm")
	public String loginForm() {
		return "/member/loginForm";
	}

	@PostMapping("/member/loginPro")
	public String loginPro(Member member, Model model, HttpSession session) {
		int check = memberDAO.checkMember(member);
		if (check == 1) {
			session.setAttribute("log", member.getId());
		}
		model.addAttribute("check", check);
//		model.addAttribute("id", member.getId());
		return "/member/loginPro";
	}

	@GetMapping("/member/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "/member/index";
	}

	@GetMapping("/member/modifyForm")
	public String modifyForm(Model model, HttpSession session) {
//		if (session.getAttribute("log") == null) {
//			return "/";
//		}
		if (session.getAttribute("log") != null) {
			Member member = memberDAO.getOneMember((String) session.getAttribute("log"));
			model.addAttribute("member", member);
		}
		return "/member/modifyForm";
	}

	@PostMapping("/member/modifyPro")
	public String modifyPro(Member member, HttpSession session) {
		if (session.getAttribute("log") == null) {
			return "/member/index";
		}
		member.setId((String) session.getAttribute("log"));
		memberDAO.updateMember(member);
		return "redirect:/member/list";
	}

	@GetMapping("/member/resign")
	public String resign(HttpSession session) {
		String id = (String) session.getAttribute("log");
		memberDAO.resign(id);
		session.invalidate();
		return "redirect:/member/list";
	}
}
