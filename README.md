# StoreHub - Website bán hàng theo mô hình chuỗi cửa hàng

Dự án khởi đầu để chạy thử trên máy cá nhân, sử dụng:
- Java 21
- Spring Boot 3.5.5 + Maven
- Thymeleaf + Bootstrap 5
- Spring Data JPA + SQL Server
- Spring Security Session
- Thư viện JWT (dịch vụ tạo token cơ bản)
- WebSocket STOMP + SockJS endpoint
- Cloudinary SDK (cấu hình tùy chọn)

## 1. Chuẩn bị

Cài Java 21 và mở dự án bằng IntelliJ IDEA. Cần có SQL Server đang chạy và cho phép kết nối TCP/IP.

Tạo database trong SSMS:

```sql
IF DB_ID(N'StoreManagementDB') IS NULL
    CREATE DATABASE StoreManagementDB;
GO
```

## 2. Cấu hình kết nối

Mặc định dự án dùng:
- Host: `localhost`
- Port: `1433`
- Database: `StoreManagementDB`
- Username: `sa`
- Password: `YourStrong!Passw0rd`

**Bạn phải sửa mật khẩu cho khớp với SQL Server của mình.** Cách tốt hơn là đặt biến môi trường `DB_PASSWORD`, `DB_USERNAME` và `DB_URL`.

Ví dụ PowerShell (chỉ có hiệu lực trong cửa sổ hiện tại):

```powershell
$env:DB_URL="jdbc:sqlserver://localhost:1433;databaseName=StoreManagementDB;encrypt=true;trustServerCertificate=true"
$env:DB_USERNAME="sa"
$env:DB_PASSWORD="MAT_KHAU_SQL_SERVER_CUA_BAN"
```

Nếu SQL Server dùng cổng khác, thay `1433`. Nếu dùng named instance, cấu hình URL theo instance của máy.

## 3. Chạy ứng dụng

Trong IntelliJ:
1. Mở `StoreManagementApplication.java`.
2. Chọn Run.
3. Chờ log báo Spring Boot khởi động thành công.

Hoặc ở thư mục dự án:

```powershell
.\mvnw.cmd spring-boot:run
```

Nếu thư mục không có Maven Wrapper, dùng:

```powershell
mvn spring-boot:run
```

Mở trình duyệt: http://localhost:8080

## 4. Tài khoản mẫu

Ứng dụng tự tạo dữ liệu mẫu nếu chưa có:
- Tên đăng nhập: `admin`
- Mật khẩu: `Admin@123`

Tài khoản này chỉ để thử nghiệm cục bộ. Hãy đổi mật khẩu và không triển khai tài khoản mặc định ra môi trường thật.

## 5. Những gì chạy được trong bản khởi đầu

- Trang chủ hiển thị danh sách sản phẩm từ SQL Server.
- Tự khởi tạo vai trò ADMIN, MANAGER, STAFF, CUSTOMER.
- Tạo tài khoản admin thử nghiệm bằng BCrypt.
- Trang đăng nhập/đăng xuất dựa trên Spring Security Session.
- Trang `/admin` được giới hạn cho ADMIN.
- Entity cơ bản cho chi nhánh, danh mục, sản phẩm và tồn kho.
- Endpoint WebSocket `/ws`.
- Dịch vụ JWT tạo và đọc token cơ bản.
- Cloudinary được cấu hình tùy chọn; chưa có màn hình upload ảnh trong bản khởi đầu.

## 6. Cấu hình JWT

Mặc định chỉ là khóa bí mật dùng cho phát triển cục bộ. Trước khi dùng JWT trong API, hãy đặt `JWT_SECRET` dài tối thiểu 32 byte và thêm bộ lọc xác thực JWT cùng endpoint phát hành token phù hợp.

```powershell
$env:JWT_SECRET="THAY_BANG_CHUOI_BI_MAT_DAI_IT_NHAT_32_BYTE"
```

Không dùng khóa ví dụ trong môi trường thật.

## 7. Cấu hình Cloudinary (tùy chọn)

Tạo tài khoản Cloudinary và đặt biến môi trường:

```powershell
$env:CLOUDINARY_CLOUD_NAME="ten_cloud"
$env:CLOUDINARY_API_KEY="api_key"
$env:CLOUDINARY_API_SECRET="api_secret"
```

Không chia sẻ API secret công khai. Bản khởi đầu đã có cấu hình SDK nhưng chưa tích hợp form tải ảnh.

## 8. Lỗi thường gặp

### Cannot open database / Login failed
Kiểm tra database, username/password, SQL Server Authentication và TCP/IP.

### Connection refused
Kiểm tra dịch vụ SQL Server đang chạy, TCP/IP đã bật và cổng chính xác. Nếu SQL Server chạy trong Docker, kiểm tra port mapping.

### Invalid object name / lỗi schema
Bản khởi đầu dùng `spring.jpa.hibernate.ddl-auto=update`, nên JPA sẽ tạo bảng từ Entity khi kết nối thành công. Nếu đã có schema thủ công không khớp Entity, hãy dùng database thử nghiệm sạch hoặc đồng bộ schema trước.

### Maven tải dependency lỗi
Kiểm tra mạng và chọn Reload All Maven Projects trong IntelliJ.

## 9. Phạm vi chưa hoàn thiện

Đây là bản MVP để kiểm tra môi trường và chạy thử, chưa phải hệ thống bán hàng production. Các giai đoạn tiếp theo cần triển khai CRUD chi nhánh/sản phẩm, phân quyền theo chi nhánh, nghiệp vụ nhập-xuất/chuyển kho có transaction, giỏ hàng, đặt hàng, JWT filter/API login, xác thực WebSocket, upload Cloudinary, kiểm thử và migration database.


## Giao diện FIGUREVERSE

Giao diện đã được đổi sang cửa hàng sưu tầm mô hình anime với phong cách công nghệ tối, neon cyan/tím, bố cục responsive, khu vực series Dragon Ball / One Piece / Naruto / All Stars, ô tìm kiếm, bộ lọc sản phẩm và nút yêu thích.

Khi ứng dụng chạy, dữ liệu mẫu sẽ bổ sung các mô hình Goku, Vegeta, Luffy, Zoro, Naruto, Sasuke, Gojo và Tanjiro theo SKU riêng; việc khởi tạo có kiểm tra SKU để tránh thêm trùng mỗi lần khởi động. Ảnh sản phẩm đang dùng khung đồ họa dự phòng để bạn có thể gắn ảnh sản phẩm thật sau này.

## Bổ sung quản trị Figureverse

Bản cập nhật bổ sung các màn hình quản trị tại `/admin/dashboard`:
- Thống kê số đơn, đơn chờ xử lý, số sản phẩm, số tài khoản, feedback và tổng giá trị đơn chưa bị hủy.
- Quản lý đơn hàng, xem thông tin người nhận/sản phẩm và cập nhật trạng thái.
- Quản lý tài khoản khách hàng, khóa/mở khóa tài khoản khách; không cho thao tác khóa tài khoản ADMIN bằng màn hình này.
- Quản lý feedback và trạng thái xử lý.
- Chỉnh sửa nội dung trang chủ, quản lý sản phẩm và quản lý tin tức/khuyến mãi theo controller có sẵn.
- Đơn hàng được lưu vào bảng `shop_orders`; feedback được lưu vào `customer_feedback`. Với `spring.jpa.hibernate.ddl-auto=update`, Hibernate tạo/cập nhật bảng khi ứng dụng khởi động.

### Phân quyền
- Đăng ký công khai chỉ tạo tài khoản `CUSTOMER`.
- Các URL `/admin/**` yêu cầu `ROLE_ADMIN`; khách hàng không thể vào trang quản trị bằng cách tự sửa URL.
- Giỏ hàng, lịch sử đơn hàng, wishlist và feedback yêu cầu `ROLE_CUSTOMER`.
- Sau đăng nhập, ADMIN được chuyển đến `/admin/dashboard`, CUSTOMER về cửa hàng.
- Không có mật khẩu admin mặc định được tạo tự động. Để cấp quyền admin, đăng ký một tài khoản rồi chỉnh username trong `database/grant-admin-role.sql` và chạy script bằng SQL Server Management Studio.

### Lưu ý triển khai
- Phương thức VNPAY hiện chỉ là lựa chọn ghi trên đơn hàng; chưa tích hợp giao dịch thanh toán thật. Không xem đơn COD/chưa thanh toán là tiền đã thu thực tế.
- Tổng giá trị hiển thị trên dashboard là giá trị đơn chưa bị hủy, dùng làm số liệu vận hành tham khảo; muốn báo cáo doanh thu kế toán cần bổ sung trạng thái thanh toán và đối soát giao dịch.
- Sau khi cập nhật mã nguồn, mở project bằng IntelliJ, kiểm tra kết nối SQL Server, chạy Maven/Run và kiểm thử đăng ký, đăng nhập hai vai trò, đặt/hủy đơn, cập nhật trạng thái, khóa khách và gửi feedback trước khi triển khai.
