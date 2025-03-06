package com.customer.servlets.reservation;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

import com.customer.DatabaseManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/api/reservation", "/api/reservation/*"})
public class ReservationServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws IOException {
    String pathInfo = request.getPathInfo();

    // HTML 페이지 요청 처리
    if (pathInfo == null || pathInfo.equals("/")) {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        // reservation.html 파일을 읽어서 응답으로 전송
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    getServletContext().getResourceAsStream("/html/reservation.html"), 
                    "UTF-8"))) {
            String line;
            StringBuilder htmlContent = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                htmlContent.append(line);
            }
            response.getWriter().write(htmlContent.toString());
            return;
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }

    // API 요청 처리
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    if (pathInfo.equals("/active")) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // 모든 예약을 조회하도록 수정
            String sql = "SELECT * FROM reservation";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            ResultSet rs = stmt.executeQuery();
            JSONArray reservations = new JSONArray();

            while (rs.next()) {
                JSONObject reservation = new JSONObject();
                reservation.put("roomNumber", rs.getString("room_number"));
                reservation.put("roomType", rs.getString("room_type"));
                reservation.put("checkinDt", rs.getString("checkin_dt"));
                reservation.put("checkoutDt", rs.getString("checkout_dt"));
                reservation.put("reservationDt", rs.getString("reservation_dt"));
                reservations.put(reservation);
            }

            // 디버깅을 위한 로그
            System.out.println("Found reservations: " + reservations.toString());
            
            response.getWriter().write(reservations.toString());
            
        } catch (Exception e) {
            e.printStackTrace(); // 서버 로그에 에러 출력
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    } else if (pathInfo.equals("/all")) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "SELECT * FROM reservation ORDER BY checkin_dt";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            JSONArray reservations = new JSONArray();
            while (rs.next()) {
                JSONObject reservation = new JSONObject();
                reservation.put("roomNumber", rs.getString("room_number"));
                reservation.put("roomType", rs.getString("room_type"));
                reservation.put("name", rs.getString("name"));
                reservation.put("checkinDt", rs.getString("checkin_dt"));
                reservation.put("checkoutDt", rs.getString("checkout_dt"));
                reservations.put(reservation);
            }
            
            response.getWriter().write(reservations.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    } else {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String username = (String) session.getAttribute("username");

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "SELECT * FROM reservation WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            JSONArray reservations = new JSONArray();

            while (rs.next()) {
                JSONObject reservation = new JSONObject();
                reservation.put("roomNumber", rs.getString("room_number"));
                reservation.put("roomType", rs.getString("room_type"));
                reservation.put("checkinDt", rs.getString("checkin_dt"));
                reservation.put("checkoutDt", rs.getString("checkout_dt"));
                reservation.put("reservationDt", rs.getString("reservation_dt"));
                reservations.put(reservation);
            }

            response.getWriter().write(reservations.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject error = new JSONObject();
            error.put("error", "데이터베이스 오류가 발생했습니다.");
            response.getWriter().write(error.toString());
        }
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
          throws IOException {
    request.setCharacterEncoding("UTF-8");
    response.setContentType("application/json; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");

    try {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(request.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        
        System.out.println("Received data: " + sb.toString());

        JSONObject jsonObject = new JSONObject(sb.toString());
        
        // 필수 데이터 추출
        String name = jsonObject.getString("name");
        String roomNumber = jsonObject.getString("roomNumber");
        String checkinDt = jsonObject.getString("checkinDt");
        String checkoutDt = jsonObject.getString("checkoutDt");
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // room 테이블에서 객실 타입 조회
            String roomTypeSql = "SELECT room_type FROM room WHERE room_number = ?";
            String roomType;
            
            try (PreparedStatement pstmt = conn.prepareStatement(roomTypeSql)) {
                pstmt.setString(1, roomNumber);
                ResultSet rs = pstmt.executeQuery();
                
                if (!rs.next()) {
                    throw new IllegalArgumentException("존재하지 않는 객실 번호입니다.");
                }
                roomType = rs.getString("room_type").toLowerCase();
                System.out.println("Found room type: " + roomType); // 디버깅용
            }
            
            // 예약 정보 저장
            String sql = "INSERT INTO reservation (name, room_number, room_type, checkin_dt, checkout_dt, reservation_dt) VALUES (?, ?, ?, ?, ?, NOW())";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, roomNumber);
                pstmt.setString(3, roomType);  // DB에서 조회한 객실 타입 사용
                pstmt.setString(4, checkinDt);
                pstmt.setString(5, checkoutDt);

                int result = pstmt.executeUpdate();

                JSONObject jsonResponse = new JSONObject();
                if (result > 0) {
                    jsonResponse.put("success", true);
                    jsonResponse.put("message", "예약이 완료되었습니다.");
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "예약에 실패했습니다.");
                }

                response.getWriter().write(jsonResponse.toString());
            }
        }
    } catch (Exception e) {
        System.err.println("Error in doPost: " + e.getMessage());
        e.printStackTrace();
        
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("success", false);
        errorResponse.put("message", "서버 오류: " + e.getMessage());
        
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write(errorResponse.toString());
    }
  }
}
