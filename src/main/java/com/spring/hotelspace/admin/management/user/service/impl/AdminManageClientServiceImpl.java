package com.spring.hotelspace.admin.management.user.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.hotelspace.admin.main.vo.AdminMainVO;
import com.spring.hotelspace.admin.management.user.dao.AdminManageClientDAO;
import com.spring.hotelspace.admin.management.user.service.AdminManageClientService;
import com.spring.hotelspace.admin.management.user.vo.AdminManageClientVO;

@Service
public class AdminManageClientServiceImpl implements AdminManageClientService {

	@Autowired
	private AdminManageClientDAO adminManageClientDAO;

	public List<AdminManageClientVO> getClientList(AdminManageClientVO vo) {
		// System.out.println("서비스 vo받음 " +vo);
		return adminManageClientDAO.getClientTable(vo);

	}

	@Override
	public void getState(ArrayList<AdminManageClientVO> arr, ArrayList<AdminManageClientVO> Narr,
			AdminManageClientVO vo) {
		// TODO Auto-generated method stub

		System.out.println("state서비스");
		System.out.println(arr.size());
		System.out.println(Narr.size());
		if (Narr == null || Narr.size()==0) {
			
		}else {
			adminManageClientDAO.getBanState(Narr);
			System.out.println("ban처리완료");
		}
		adminManageClientDAO.getActiveState(arr);

		System.out.println("active처리완료");

		adminManageClientDAO.getClientTable(vo);
		System.out.println("새로고침");

	}
}
