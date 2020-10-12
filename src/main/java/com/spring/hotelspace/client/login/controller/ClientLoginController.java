package com.spring.hotelspace.client.login.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.hotelspace.client.login.vo.ClientLoginVO;
import com.spring.hotelspace.common.kakao.KakaoAPI;
import com.fasterxml.jackson.databind.JsonNode;
import com.spring.hotelspace.client.login.service.ClientLoginService;

@Controller
public class ClientLoginController {

	@Autowired
	private ClientLoginService clientLoginService;

	@Autowired
	private KakaoAPI kakao;

	private final String codeURI = "https://kauth.kakao.com/oauth/authorize?client_id=7fe2ea8fb8719474f5388f06fbf3f3ca&redirect_uri=http://ec2-18-216-125-181.us-east-2.compute.amazonaws.com/hotelspace/kakaoLogin.do&response_type=code";

	@RequestMapping(value = "/clientLogin.do", method = RequestMethod.GET)
	public String getloginPaget(ClientLoginVO client, HttpSession httpSession, Model model) {
		model.addAttribute("kakaoURI", codeURI);
		return "login/clientLogin";
	}

	@RequestMapping(value = "/clientLogin.do", method = RequestMethod.POST)
	public String loginToClient(ClientLoginVO client, HttpSession httpSession, Model model) {

		ClientLoginVO resultClient = clientLoginService.getClientLoginResult(client);

		//Object destination = httpSession.getAttribute("destination");

		if (resultClient != null) {
			if (resultClient.getClientPassword().equals(client.getClientPassword())) {
				if (resultClient.getClientState().equals("1")) {// 정지 상태
					model.addAttribute("message", "해당 계정은 정지상태입니다. 고객센터에 문의하세요.");
					return "login/clientLogin";
				} else {
					resultClient.setClientPassword("");
					httpSession.setAttribute("login", resultClient); // 로그인 성공
				}
			} else {
				model.addAttribute("message", "아이디 혹은 비밀번호가 틀렸습니다.");// 아이디 오류 처리 // 비밀번호 오류 처리
				return "login/clientLogin";
			}
		} else {
			model.addAttribute("message", "아이디 혹은 비밀번호가 틀렸습니다.");// 아이디 오류 처리 // 비밀번호 오류 처리
			return "login/clientLogin";
		}

		return "redirect:index.do";
	}

	@RequestMapping(value = "/kakaoLogin.do", method = RequestMethod.GET)
	public String loginToClientbyKakao(@RequestParam("code") String code, HttpSession httpSession, Model model) {

		//Object destination = httpSession.getAttribute("destination");

		System.out.println("code : " + code);

		String access_Token = kakao.getAccessToken(code);

		System.out.println("controller access_token : " + access_Token);

		String id = kakao.getKakaoId(access_Token);

		ClientLoginVO resultClient = clientLoginService.compareKakaoId(id);

		if (resultClient != null) {
			resultClient.setClientPassword("kakao");
			httpSession.setAttribute("access_Token", access_Token);
			httpSession.setAttribute("login", resultClient);

		} else {
			// 로그인을 요청한 회원정보를 카카오 회원가입 화면으로 전송
			ClientLoginVO wantR = new ClientLoginVO();
			wantR.setClientPassword("kakao");
			wantR.setClientId(id);
			httpSession.setAttribute("login", wantR);
			httpSession.setAttribute("access_Token", access_Token);
			return "register/kakaoRegister";
		}

		return "redirect:index.do";
	}

	// 로그아웃 & 카카오 회원가입 중 취소를 원하는 회원
	@RequestMapping(value = "/clientLogout.do", method = RequestMethod.GET)
	public String getlogout(HttpSession httpSession, Model model) {

		ClientLoginVO cl = (ClientLoginVO) httpSession.getAttribute("login");
		System.out.println(cl);
		if (cl.getClientPassword().equals("kakao")) {
			kakao.logout((String) httpSession.getAttribute("access_Token"));
			httpSession.removeAttribute("access_Token");
		}

		httpSession.removeAttribute("login");

		return "redirect:index.do";
	}

	@RequestMapping(value = "/clientMypage.do", method = RequestMethod.GET)
	public String getlogout() {
		return "mypage/mypage";
	}

}