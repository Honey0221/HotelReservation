# 🏨 호텔 관리 시스템 (Hotel Management System)

## 📋 프로젝트 개요

이 프로젝트는 호텔 객실 예약 및 관리를 위한 웹 기반 시스템입니다. 관리자와 일반 사용자 모두 사용할 수 있으며, 객실 예약, 고객 관리, 객실 관리 등의 기능을 제공합니다.

## ✨ 주요 기능

### 1. 👥 사용자 관리
- 회원가입 및 로그인
- 관리자/일반 사용자 권한 구분
- 세션 기반 인증

### 2. 🛏️ 객실 관리
- 객실 정보 조회, 추가, 수정, 삭제
- 객실 상태 관리 (이용가능, 사용중, 청소중)
- 객실 타입별 분류 (Normal, Business, Suite, VIP)
- 층별 객실 조회

### 3. 📅 예약 시스템
- 객실 예약 생성
- 예약 조회 및 수정
- 중복 예약 방지 로직
- 날짜별 예약 현황 확인

### 4. 👤 고객 관리
- 고객 정보 조회, 추가, 수정, 삭제
- 고객별 예약 내역 조회
- 고객 검색 기능

### 5. 📊 통계 및 대시보드
- 객실 현황 통계
- 예약 통계 (월별 추이, 객실 타입별 분포 등)
- 캘린더 기반 예약 현황 시각화

## 🛠️ 기술 스택

### 백엔드
- Java Servlet
- Tomcat 내장 서버
- MySQL 데이터베이스
- JDBC

### 프론트엔드
- HTML5, CSS3, JavaScript
- Bootstrap 5
- Font Awesome
- FullCalendar (캘린더 시각화)

## 🏗️ 시스템 아키텍처

- **MVC 패턴**: 서블릿(Controller), HTML/JS(View), 데이터베이스 관리 클래스(Model)
- **싱글톤 패턴**: DatabaseManager 클래스를 통한 데이터베이스 연결 관리
- **RESTful API**: 클라이언트-서버 통신을 위한 JSON 기반 API

## 💾 데이터베이스 구조

- **user**: 사용자 정보 (id, username, name, email, password, admin_yn)
- **room**: 객실 정보 (room_number, room_type, room_status)
- **reservation**: 예약 정보 (id, name, room_number, room_type, checkin_dt, checkout_dt, reservation_dt)

## 🔍 주요 문제 해결 사례

### 중복 예약 방지 로직 구현

예약 시스템에서 가장 중요한 문제 중 하나인 중복 예약을 방지하기 위해 다음과 같은 해결책을 구현했습니다:

1. **트랜잭션 관리**: 예약 수정 시 트랜잭션을 사용하여 데이터 일관성 보장
   ```java
   conn.setAutoCommit(false);
   try {
       // 예약 처리 로직
       conn.commit();
   } catch (SQLException e) {
       conn.rollback();
   }
   ```

2. **중복 체크 쿼리**: 날짜 범위가 겹치는 예약이 있는지 확인하는 SQL 쿼리 구현
   ```sql
   SELECT COUNT(*) as count FROM reservation 
   WHERE room_number = ? 
   AND id != ? 
   AND NOT (
       checkout_dt <= ? 
       OR checkin_dt >= ? 
   )
   ```

3. **클라이언트 측 유효성 검사**: JavaScript를 통한 사전 검증으로 서버 부하 감소

## 🔮 향후 개선 사항

1. 보안 강화: 비밀번호 암호화, XSS 방지 등
2. 결제 시스템 연동
3. 이메일 알림 기능 추가
4. 모바일 반응형 디자인 개선
5. 다국어 지원

## 👨‍💻 개발자 정보
- 개발자 : 김지헌
- 이메일 : wlgjsrns@gmail.com
- GitHub : https://github.com/Honey0221