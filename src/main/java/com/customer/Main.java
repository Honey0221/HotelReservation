package com.customer;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.servlets.DefaultServlet;

import com.customer.servlets.reservation.ReservationServlet;
import com.customer.servlets.reservation.ReservationStatusServlet;
import com.customer.servlets.auth.AdminCheckServlet;
import com.customer.servlets.auth.CheckEmailServlet;
import com.customer.servlets.auth.CheckUsernameServlet;
import com.customer.servlets.auth.LoginServlet;
import com.customer.servlets.auth.LogoutServlet;
import com.customer.servlets.auth.SessionCheckServlet;
import com.customer.servlets.auth.SignupServlet;
import com.customer.servlets.customer.CustomerManagementServlet;
import com.customer.servlets.customer.CustomerReservationServlet;
import com.customer.servlets.dashboard.DashboardServlet;
import com.customer.servlets.dashboard.MainServlet;
import com.customer.servlets.room.RoomManagementServlet;
import com.customer.servlets.reservation.ReservationStatisticsServlet;
import java.io.File;

public class Main {
	public static void main(String[] args) throws Exception {
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(8080);
		tomcat.getConnector();

		String baseDir = new File("target/tomcat").getAbsolutePath();
		tomcat.setBaseDir(baseDir);

		String docBase = new File("src/main/resources").getAbsolutePath();
		Context ctx = tomcat.addContext("", docBase);

		// static 리소스 매핑 추가
		Tomcat.addServlet(ctx, "defaultServlet", new DefaultServlet());
		ctx.addServletMappingDecoded("/", "defaultServlet");
		ctx.addServletMappingDecoded("/html/*", "defaultServlet");
		ctx.addServletMappingDecoded("/static/*", "defaultServlet"); // static 폴더 매핑 추가
		ctx.addServletMappingDecoded("/css/*", "defaultServlet"); // css 직접 매핑
		ctx.addServletMappingDecoded("/js/*", "defaultServlet"); // js 직접 매핑
		ctx.addServletMappingDecoded("/images/*", "defaultServlet");
		ctx.addServletMappingDecoded("/components/*", "defaultServlet");

		Tomcat.addServlet(ctx, "mainServlet", new MainServlet());
		ctx.addServletMappingDecoded("/", "mainServlet");

		Tomcat.addServlet(ctx, "loginServlet", new LoginServlet());
		ctx.addServletMappingDecoded("/login", "loginServlet");
		ctx.addServletMappingDecoded("/api/login", "loginServlet");

		Tomcat.addServlet(ctx, "dashboardServlet", new DashboardServlet());
		ctx.addServletMappingDecoded("/dashboard", "dashboardServlet");
		ctx.addServletMappingDecoded("/api/room", "dashboardServlet");
		ctx.addServletMappingDecoded("/api/reservation/all", "dashboardServlet");
		ctx.addServletMappingDecoded("/api/reservation/room/*", "dashboardServlet");

		Tomcat.addServlet(ctx, "roomManagementServlet", new RoomManagementServlet());
		ctx.addServletMappingDecoded("/room-management", "roomManagementServlet");
		ctx.addServletMappingDecoded("/api/room", "roomManagementServlet");
		ctx.addServletMappingDecoded("/api/room/*", "roomManagementServlet");

		Tomcat.addServlet(ctx, "adminCheckServlet", new AdminCheckServlet());
		ctx.addServletMappingDecoded("/api/check-admin", "adminCheckServlet");

		Tomcat.addServlet(ctx, "signupServlet", new SignupServlet());
		ctx.addServletMappingDecoded("/signup", "signupServlet");

		Tomcat.addServlet(ctx, "logoutServlet", new LogoutServlet());
		ctx.addServletMappingDecoded("/api/logout", "logoutServlet");

		Tomcat.addServlet(ctx, "sessionCheckServlet", new SessionCheckServlet());
		ctx.addServletMappingDecoded("/api/check-session", "sessionCheckServlet");

		Tomcat.addServlet(ctx, "checkUsernameServlet", new CheckUsernameServlet());
		ctx.addServletMappingDecoded("/api/check-username", "checkUsernameServlet");

		Tomcat.addServlet(ctx, "checkEmailServlet", new CheckEmailServlet());
		ctx.addServletMappingDecoded("/api/check-email", "checkEmailServlet");

		Tomcat.addServlet(ctx, "customerManagementServlet", new CustomerManagementServlet());
		ctx.addServletMappingDecoded("/customer-management", "customerManagementServlet");
		ctx.addServletMappingDecoded("/api/customer", "customerManagementServlet");
		ctx.addServletMappingDecoded("/api/customer/*", "customerManagementServlet");

		Tomcat.addServlet(ctx, "reservationServlet", new ReservationServlet());
		ctx.addServletMappingDecoded("/api/reservation", "reservationServlet");
		ctx.addServletMappingDecoded("/api/reservation/*", "reservationServlet");

		Tomcat.addServlet(ctx, "customerReservationServlet", new CustomerReservationServlet());
		ctx.addServletMappingDecoded("/api/reservation/customer/*", "customerReservationServlet");

		// ReservationStatusServlet 추가
		Tomcat.addServlet(ctx, "reservationStatusServlet", new ReservationStatusServlet());
		ctx.addServletMappingDecoded("/reservationStatus", "reservationStatusServlet");
		ctx.addServletMappingDecoded("/api/reservations", "reservationStatusServlet");

		Tomcat.addServlet(ctx, "reservationStatisticsServlet", new ReservationStatisticsServlet());
		ctx.addServletMappingDecoded("/reservationStatistics", "reservationStatisticsServlet");
		ctx.addServletMappingDecoded("/api/statistics", "reservationStatisticsServlet");

		try {
			tomcat.start();
			System.out.println("Server started on port 8080");
			tomcat.getServer().await();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
