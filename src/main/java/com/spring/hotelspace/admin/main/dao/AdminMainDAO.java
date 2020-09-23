

package com.spring.hotelspace.admin.main.dao;

import java.util.ArrayList;

import com.spring.hotelspace.admin.main.vo.AdminMainVO;
import com.spring.hotelspace.admin.main.vo.ReservationDataVO;

public interface AdminMainDAO {

    public int gethotelCount();
    public int getresCount();
    public int getuserCount();
    public String getReservation(String month);
	public String getSales(String month);
	public String getReservation2019(String month);
	public String getSales2019(String month);
	public ArrayList<ReservationDataVO> getData();

}

