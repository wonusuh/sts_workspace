package kr.board.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import kr.board.entity.Member;
import kr.board.mapper.MemberMapper;

// mvc2
@RequestMapping("/member")
@Controller
public class MemberController {
	@Autowired
	MemberMapper memberMapper;

	@ModelAttribute("cp")
	public String getContextPath(Model model, HttpServletRequest request) {
		model.addAttribute("cp", request.getContextPath());
		return request.getContextPath();
	}

	@GetMapping("/memJoin.do")
	public String memJoin() {
		return "member/join"; // join.jsp
	}

	@RequestMapping("/memRegisterCheck.do")
	public @ResponseBody int memRegisterCheck(@RequestParam("memID") String memID) {
		Member m = memberMapper.registerCheck(memID);
		if (m != null || memID.equals("")) {
			return 0; // 이미 존재하는 회원, 입력불가
		}
		return 1; // 사용가능한 아이디
	}

	// 회원가입 처리
	@RequestMapping("/memRegister.do")
	public String memRegister(Member m, String memPassword1, String memPassword2, RedirectAttributes rttr,
			HttpSession session) {
		System.out.println("==== memRegister.do ====");
		if (m.nullValueCheck()) {
			// 누락메세지를 가지고 가기? =>객체바인딩(Model, HttpServletRequest, HttpSession)
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", "모든 내용을 입력하세요.");
			return "redirect:/member/memJoin.do"; // ${msgType} , ${msg}
		}
		if (!memPassword1.equals(memPassword2)) {
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", "비밀번호가 서로 다릅니다.");
			return "redirect:/member/memJoin.do"; // ${msgType} , ${msg}
		}
		m.setMemPassword(memPassword2);
		m.setMemProfile(""); // 사진이미는 없다는 의미 ""
		// 회원을 테이블에 저장하기
		int result = memberMapper.register(m);
		if (result == 1) { // 회원가입 성공 메세지
			rttr.addFlashAttribute("msgType", "성공 메세지");
			rttr.addFlashAttribute("msg", "회원가입에 성공했습니다.");
			// 회원가입이 성공하면=>로그인처리하기
			session.setAttribute("mvo", m); // ${!empty mvo}
			return "redirect:/";
		} else {
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", "이미 존재하는 회원입니다.");
			return "redirect:/member/memJoin.do";
		}
	}

	// 로그아웃 처리
	@RequestMapping("/memLogout.do")
	public String memLogout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	// 로그인 화면으로 이동
	@RequestMapping("/memLoginForm.do")
	public String memLoginForm() {
		return "member/memLoginForm"; // memLoginForm.jsp
	}

	// 로그인 기능 구현
	@RequestMapping("/memLogin.do")
	public String memLogin(@ModelAttribute Member m, RedirectAttributes rttr, HttpSession session) {
		if (m.getMemID() == null || m.getMemID().equals("") || m.getMemPassword() == null
				|| m.getMemPassword().equals("")) {
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", "모든 내용을 입력해주세요.");
			return "redirect:/member/memLoginForm.do";
		}
		Member mvo = memberMapper.memLogin(m);
		if (mvo != null) { // 로그인에 성공
			rttr.addFlashAttribute("msgType", "성공 메세지");
			rttr.addFlashAttribute("msg", "로그인에 성공했습니다.");
			session.setAttribute("mvo", mvo); // ${!empty mvo}
			return "redirect:/"; // 메인
		} else { // 로그인에 실패
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", "다시 로그인 해주세요.");
			return "redirect:/member/memLoginForm.do";
		}
	}

	// 회원정보수정화면
	@RequestMapping("/memUpdateForm.do")
	public String memUpdateForm() {
		return "member/memUpdateForm";
	}

	// 회원정보수정
	@RequestMapping("/memUpdate.do")
	public String memUpdate(@ModelAttribute Member m, RedirectAttributes rttr, @RequestParam String memPassword1,
			String memPassword2, HttpSession session) {
		// 실습
		System.out.println("pw1 : " + memPassword1);
		System.out.println("pw2 : " + memPassword2);
		if (!memPassword1.equals(memPassword2)) {
			System.out.println("pw1 과 pw2 를 확인하세요.");
			return "redirect:/member/memUpdateForm.do";
		}
		m.setMemPassword(memPassword2);
		if (!m.nullValueCheck()) {
			rttr.addFlashAttribute("msgType", "실패 메시지");
			rttr.addFlashAttribute("msg", "모든 값을 넣어주세요.");
			return "redirect:/member/memUpdateForm.do";
		}
		System.out.println(m.toString());
		memberMapper.memUpdate(m);
		session.setAttribute("mvo", m);
		return "redirect:/";
	}

	// 회원 사진 등록
	@GetMapping("/memImageForm.do")
	public String memImageForm() {
		return "/member/memImageForm";
	}

	@PostMapping("/memImageUpdate.do")
	public String memImageUpdate(HttpServletRequest request, HttpSession session, RedirectAttributes rttr) {
		MultipartRequest multi = null;
		int fileMaxSize = 10 * 1024 * 1024; // 10MB
		String savePath = request.getSession().getServletContext().getRealPath("resources/upload");
		Path uploadDirectory = Paths.get(savePath);
		int result = 0;
		if (!Files.exists(uploadDirectory)) { // 업로드 폴더 없으면 생성
			try {
				Files.createDirectory(uploadDirectory);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 이미지 업로드
		try {
			multi = new MultipartRequest(request, savePath, fileMaxSize, "UTF-8", new DefaultFileRenamePolicy());

			String memID = multi.getParameter("memID");
			Member mvo = memberMapper.getMember(memID);
			if (mvo == null) {
				return "redirect:/";
			}

			File file = multi.getFile("memProfile");
			if (file.exists()) {
				System.out.println("저장완료 ");
				System.out.println("저장 경로 " + savePath);

				String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
				ext = ext.toUpperCase(); // png, PNG, jpg, JPG,

				// 이미지 확장자 아니면 되돌아가기
				if (!(ext.equals("PNG") || ext.equals("JPG"))) {

					rttr.addFlashAttribute("msgType", "실패 메세지");
					rttr.addFlashAttribute("msg", " 이미지 사진만 업로드 가능합니다  ");
					return "redirect:/member/memImageForm.do";

				}

				String newProfile = file.getName(); // 현재 업로드한 파일 이름
				String oldProfile = mvo.getMemProfile(); // 기존 이미지 파일 이름

				// 기존에 이미지 파일이 있다면 삭제
				File oldFile = new File(savePath + "/" + oldProfile);

				if (oldFile.exists()) {
					oldFile.delete();
				}

				mvo.setMemProfile(newProfile);
				result = memberMapper.memProfileUpdate(mvo);
				System.out.println("이미지 업로드 mvo = " + mvo);

			}

			// db 이미지 업로드 성공 후
			if (result == 1) {
				session.setAttribute("mvo", mvo);

				rttr.addFlashAttribute("msgType", "성공 메세지");
				rttr.addFlashAttribute("msg", " 이미지 등록 성공  ");
				return "redirect:/";

			}

		} catch (IOException e) {

			e.printStackTrace();
		}

		return "redirect:/";
	}
}
