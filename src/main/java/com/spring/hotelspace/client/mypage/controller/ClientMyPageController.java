package com.spring.hotelspace.client.mypage.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.hotelspace.client.login.vo.ClientLoginVO;
import com.spring.hotelspace.client.main.vo.MainVO;
import com.spring.hotelspace.client.mypage.service.ClientMyPageService;
import com.spring.hotelspace.client.mypage.vo.ClientMyPageVO;
import com.spring.hotelspace.client.mypage.vo.ClientReviewVO;
import com.spring.hotelspace.client.reservation.vo.ClientReservationVO;

@Controller
public class ClientMyPageController {

	@Autowired
	private ClientMyPageService clientMyPageService;
	
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");

	@RequestMapping (value="/myinfoUpdate.do", method={RequestMethod.GET, RequestMethod.POST})
	public String mypageupdate(ClientLoginVO loginVO,HttpSession session,Model model) {

		return "mypage/myinfoupdate";

	}

	@RequestMapping (value="/mypageUpdate.do", method={RequestMethod.GET, RequestMethod.POST})
	public String getpageupdate(ClientLoginVO loginVO,HttpSession session,Model model) {
		//System.out.println(loginVO.getClientId());
		int aa= clientMyPageService.updateAction(loginVO);
		//System.out.println("update data count : "+ aa);
		session.setAttribute("login", loginVO);
		clientMyPageService.searchAll(loginVO);
		return "redirect:clientMypage.do";

	}

	@RequestMapping(value = "/pwChangeForm.do", method= {RequestMethod.GET, RequestMethod.POST}) 
	public String pwChangeForm(ClientMyPageVO client, HttpSession httpSession, Model model) {

		return "mypage/pwChange";
	}

	@RequestMapping(value = "/myreservation.do", method= {RequestMethod.GET, RequestMethod.POST}) 
	public String ClientReservationVO(ClientLoginVO client, HttpSession httpSession, Model model) {

		ClientLoginVO object = (ClientLoginVO)httpSession.getAttribute("login");
		//System.out.println(object.getClientId());
		String id = object.getClientId();

		List<ClientReservationVO> list = clientMyPageService.myReservation(id);
		model.addAttribute("myReserv", list);

		
		return "mypage/myreservation";
	}
	@Transactional
	@RequestMapping(value = "/addReview.do", method= {RequestMethod.GET, RequestMethod.POST}) 
	public @ResponseBody String mypageReview(ClientReviewVO client, HttpSession httpSession, Model model) {
		
		client.setReviewId("re" + simpleDateFormat.format(new Date()));
		
		ClientLoginVO object = (ClientLoginVO)httpSession.getAttribute("login");
		//System.out.println(object.getClientId());
		String id = object.getClientId();
		 
		//리뷰 등록
		client.setClientId(id);
		clientMyPageService.mypageReview(client);
		
		//리뷰 등록시 session에 vo를 불러 reservationVO의 리뷰체크를 1로 갱신
		ClientReservationVO reservVO = new ClientReservationVO();
		reservVO.setReservationId(client.getReservationId());
		clientMyPageService.reservCheck(reservVO);
		
		// 예약내역 같은 호텔ID들의 평균으로 갱신
		String review = client.getHotelId();
		clientMyPageService.avgReview(review);
		
		return "1";
	}
	
	@RequestMapping(value = "/reviewForm.do", method= {RequestMethod.GET, RequestMethod.POST}) 
	public String reviewForm(ClientReviewVO client, HttpSession httpSession, Model model) {
		model.addAttribute("review", client);
		
		// 평점 옵션
		Map<Integer, String> ratingOptions = new HashMap<Integer, String>();
		ratingOptions.put(0, "☆☆☆☆☆");
		ratingOptions.put(1, "★☆☆☆☆");
		ratingOptions.put(2, "★★☆☆☆");
		ratingOptions.put(3, "★★★☆☆");
		ratingOptions.put(4, "★★★★☆");
		ratingOptions.put(5, "★★★★★");
		model.addAttribute("ratingOptions", ratingOptions);	
		
		return "mypage/reviewForm";
	}


	@RequestMapping(value = "/withdrawalForm.do", method= {RequestMethod.GET, RequestMethod.POST}) 
	public String withdrawalForm(ClientMyPageVO client, HttpSession httpSession, Model model) {

		return "mypage/withdrawal";
	}

	@RequestMapping(value="/withdrawal.do", method = {RequestMethod.GET, RequestMethod.POST})
	public String withdrawal(ClientMyPageVO client, HttpSession httpSession, Model model) {

		//회원탈퇴
		ClientLoginVO object = (ClientLoginVO)httpSession.getAttribute("login");
		clientMyPageService.withdrawal(object);
		//로그인 세션 삭제

		if(object != null) {
			httpSession.removeAttribute("login");
			httpSession.invalidate();
		}

		return "redirect:index.do"; 
	}

	@RequestMapping(value="/pwChange.do", method = RequestMethod.POST)
	public String pwChange(ClientMyPageVO client, HttpSession httpSession, Model model) {

		//비밀번호 변경
		int a = clientMyPageService.changePW(client);

		return "redirect:clientMypage.do";
	}

	//비밀번호 확인
	@RequestMapping(value = "/checkPw.do", method = { RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody int checkId(HttpServletRequest request, Model model) {
		//return clientRegisterService.checkId(regvo);


		ClientMyPageVO myPagevo1 = new ClientMyPageVO();

		myPagevo1.setClientPassword(request.getParameter("pw"));
		int result = clientMyPageService.checkPw(myPagevo1);
		System.out.println(result);

		return result;
	}//

}
