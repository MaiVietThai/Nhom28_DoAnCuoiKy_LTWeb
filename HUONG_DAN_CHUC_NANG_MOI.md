# Chức năng mới: Hủy đơn hàng và Feedback

## Khách hàng
- Trong **Lịch sử đơn hàng**, đơn ở trạng thái **Đang xử lý** có biểu mẫu hủy.
- Khách bắt buộc chọn lý do hủy; có thể nhập chi tiết tối đa 500 ký tự. Nếu chọn **Lý do khác**, phần chi tiết là bắt buộc.
- Lý do và chi tiết hủy được lưu trong cơ sở dữ liệu, hiển thị lại ở lịch sử đơn hàng và trang quản trị.
- Tại mục **Góp ý**, khách chọn loại phản hồi, chấm 1–5 sao, nhập nội dung và xem lịch sử phản hồi cùng trả lời từ cửa hàng.

## Quản trị viên
- Vào **Quản lý đơn hàng** để xem lý do/chi tiết hủy.
- Vào **Feedback** để xem loại phản hồi, nội dung và gửi câu trả lời cho khách; phản hồi được lưu và khách xem được ở trang Góp ý.

## Cập nhật cơ sở dữ liệu
Dự án cấu hình `spring.jpa.hibernate.ddl-auto=update`, vì vậy Hibernate sẽ tự bổ sung các cột mới khi khởi động ứng dụng kết nối đúng SQL Server/database. Không cần xóa database cũ.

## Chạy dự án
Trong `application.properties`, cổng mặc định là **8081**, vì vậy URL cục bộ mặc định là `http://localhost:8081` (trừ khi biến môi trường `SERVER_PORT` được đặt khác).
