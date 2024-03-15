package kr.board.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import kr.board.entity.AuthVO;
import kr.board.entity.Member;
import kr.board.mapper.MemberMapper;
import kr.board.security.MemberUserDetailsService;

// mvc2
@RequestMapping("/member")
@Controller
public class MemberController {
	@Autowired
	MemberMapper memberMapper;
	@Autowired
	PasswordEncoder pwEncoder; // 암호화 할 수 있는 객체
	@Autowired
	MemberUserDetailsService memberUserDetailsService;

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
		m.setMemPassword(memPassword1); // 암호화 전 페스워드
		m.setMemProfile(""); // 사진이 없다는 의미

		String encyptPw = pwEncoder.encode(m.getMemPassword());
		System.out.println("encyptPw = " + encyptPw);

		// 암호화한 패스워드 다시 객체에 넣기
		m.setMemPassword(encyptPw);

		// 멤버회원 추가 됨
		int result = memberMapper.register(m);

		if (result == 1) {

			// 멤버 생성 후 권한 테이블 생성
			List<AuthVO> list = m.getAuthList();
			for (AuthVO vo : list) {
				if (vo.getAuth() != null) {
					AuthVO auth = new AuthVO();
					auth.setMemID(m.getMemID());
					auth.setAuth(vo.getAuth()); // ROLE_USER, ROLE_ADMIN..
					System.out.println("auth = " + auth);
					memberMapper.authInsert(auth);
				}
			}

			rttr.addFlashAttribute("msgType", "성공 메세지");
			rttr.addFlashAttribute("msg", "회원가입 성공했습니다 ");
//			session.setAttribute("mvo", m);
			return "redirect:/";
		} else {
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", " 회원가입 실패 다시시도해주세요 ");
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

//	// 로그인 기능 구현
//	@RequestMapping("/memLogin.do")
//	public String memLogin(@ModelAttribute Member m, RedirectAttributes rttr, HttpSession session) {
//
//		System.out.println("memLogin m = " + m);
//		if (m.getMemID() == null || m.getMemID().equals("") || m.getMemPassword() == null
//				|| m.getMemPassword().equals("")) {
//
//			rttr.addFlashAttribute("msgType", " 로그인 실패");
//			rttr.addFlashAttribute("msg", "모든 값을 넣어주세요 ");
//
//			return "redirect:/member/memLoginForm.do";
//		}
//
//		Member mvo = memberMapper.memLogin(m); // 암호화된 패스워드
//		if (mvo == null) {
//			rttr.addFlashAttribute("msgType", " 로그인 실패");
//			rttr.addFlashAttribute("msg", "로그인 정보가 없습니다 ");
//
//			return "redirect:/member/memLoginForm.do";
//		}
//
//		// 사용자가 입력받은 값을 다시 암호화
//		pwEncoder.encode(m.getMemPassword());
//
//		// db 암호화 코드 == 사용자 입력한 암호화 코드 비교
//		if (pwEncoder.matches(m.getMemPassword(), mvo.getMemPassword())) {
//			// 로그인 성공
//			session.setAttribute("mvo", mvo);
//			rttr.addFlashAttribute("msgType", "성공 메세지");
//			rttr.addFlashAttribute("msg", "로그인 성공 했습니다  ");
//		} else {
//			rttr.addFlashAttribute("msgType", "실패 메세지");
//			rttr.addFlashAttribute("msg", "비밀번호 불일치  ");
//			return "redirect:/member/memLoginForm.do";
//		}
//
//		return "redirect:/";
//	}

	// 회원정보수정화면
	@RequestMapping("/memUpdateForm.do")
	public String memUpdateForm() {
		return "member/memUpdateForm";
	}

	// 회원정보수정
	@RequestMapping("/memUpdate.do")
	public String memUpdate(@ModelAttribute Member m, RedirectAttributes rttr, @RequestParam String memPassword1,
			String memPassword2, HttpSession session) {

		if (!m.nullValueCheck()) {
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", "모든 값을 넣어주세요 ");
			return "redirect:/member/memUpdateForm.do";
		}

		if (!memPassword1.equals(memPassword2)) {
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", "패스워드 값이 서로 다릅니다 ");
			return "redirect:/member/memUpdateForm.do";
		}

		m.setMemPassword(memPassword2);
		System.out.println("update m = " + m);

		// 비번 암호화
		m.setMemPassword(pwEncoder.encode(m.getMemPassword()));

		int result = memberMapper.memUpdate(m);
		if (result == 1) {
			rttr.addFlashAttribute("msgType", "성공 메세지");
			rttr.addFlashAttribute("msg", "회원 정보 수정완료  ");

			// 기존 권한 다 삭제
			memberMapper.authDelete(m.getMemID());

			// 다시 새로운 권한 추가
			List<AuthVO> list = m.getAuthList();
			for (AuthVO vo : list) {
				if (vo.getAuth() != null) {
					AuthVO auth = new AuthVO();
					auth.setMemID(m.getMemID());
					auth.setAuth(vo.getAuth()); // ROLE_USER, ROLE_ADMIN..
					memberMapper.authInsert(auth);
				}
			}

			// 회원 정보업데이트 된 세션으로 재등록
			session.setAttribute("mvo", m);

			return "redirect:/";
		} else {
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", "회원 정보 수정실패  ");
			return "redirect:/member/memUpdateForm.do";
		}

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

	protected Authentication createNewAuthentication(Authentication currentAuth, String username) {
		UserDetails newPrincipal = memberUserDetailsService.loadUserByUsername(username);
		UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(newPrincipal,
				currentAuth.getCredentials(), newPrincipal.getAuthorities());
		newAuth.setDetails(currentAuth.getDetails());
		return newAuth;
	}
}
