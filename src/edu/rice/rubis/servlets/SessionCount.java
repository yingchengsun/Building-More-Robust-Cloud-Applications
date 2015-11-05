package edu.rice.rubis.servlets;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class SessionCount implements HttpSessionListener {

	public static int a = 0;
	public static int peak = 150;
	public static int peakAfterCut = 100;

	public void sessionCreated(HttpSessionEvent hse) {
		a++;
		ServletContext application = hse.getSession().getServletContext();
		application.setAttribute("a", new Integer(a));
		System.out.println("The number of users visiting rubbis:"+application.getAttribute("a"));		
		//clearup();
		
	}
	public static void clearup(){
		if (a>=peak){
			int count=a;
			for(int i=0;i<count;i++){
				a--;
				System.out.println(a);
			}
		}
	}

	public int get_a() {
		return a;
	}

	public int get_peak() {
		return peak;
	}

	public int get_peakAfterCut() {
		return peakAfterCut;
	}

	public void sessionDestroyed(HttpSessionEvent red) {
		//a--;

	}

}