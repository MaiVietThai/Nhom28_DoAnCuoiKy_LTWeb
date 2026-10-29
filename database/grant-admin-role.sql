/*
  CHỈ CHẠY BẰNG TÀI KHOẢN QUẢN TRỊ SQL SERVER.
  1) Tạo tài khoản thông thường trên website trước.
  2) Thay ten_dang_nhap_cua_ban bằng username cần cấp quyền.
  3) Chạy script này trong StoreManagementDB.
  Không đặt mật khẩu quản trị mặc định trong mã nguồn.
*/
USE StoreManagementDB;
GO
DECLARE @username NVARCHAR(50) = N'ten_dang_nhap_cua_ban';
DECLARE @userId BIGINT;
DECLARE @roleId BIGINT;
SELECT @userId = user_id FROM dbo.users WHERE username = @username;
IF @userId IS NULL
    THROW 50001, 'Khong tim thay username. Hay dang ky tai khoan truoc.', 1;
IF NOT EXISTS (SELECT 1 FROM dbo.roles WHERE role_name = 'ADMIN')
    INSERT INTO dbo.roles(role_name) VALUES ('ADMIN');
SELECT @roleId = role_id FROM dbo.roles WHERE role_name = 'ADMIN';
IF NOT EXISTS (SELECT 1 FROM dbo.user_roles WHERE user_id = @userId AND role_id = @roleId)
    INSERT INTO dbo.user_roles(user_id, role_id) VALUES (@userId, @roleId);
PRINT 'Da cap role ADMIN cho username duoc chon.';
GO
