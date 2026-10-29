-- ============================================================================
-- FIGUREVERSE - DATABASE SCHEMA (SQL SERVER)
-- Mô hình chuỗi cửa hàng mô hình Anime & Manga
-- ============================================================================

IF DB_ID(N'StoreManagementDB') IS NULL
BEGIN
    CREATE DATABASE StoreManagementDB;
END;
GO

USE StoreManagementDB;
GO

-- 1. BẢNG VAI TRÒ (ROLES)
IF OBJECT_ID(N'dbo.Roles', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Roles (
        RoleId BIGINT IDENTITY(1,1) PRIMARY KEY,
        RoleName NVARCHAR(30) NOT NULL UNIQUE
    );
END;
GO

-- 2. BẢNG NGƯỜI DÙNG (USERS)
IF OBJECT_ID(N'dbo.Users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Users (
        UserId BIGINT IDENTITY(1,1) PRIMARY KEY,
        Username NVARCHAR(50) NOT NULL UNIQUE,
        Email NVARCHAR(255) NOT NULL UNIQUE,
        PasswordHash NVARCHAR(255) NOT NULL,
        FullName NVARCHAR(150) NOT NULL,
        Phone NVARCHAR(20) NULL,
        IsActive BIT NOT NULL DEFAULT 1,
        CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
    );
END;
GO

-- 3. BẢNG LIÊN KẾT NGƯỜI DÙNG - VAI TRÒ (USER_ROLES)
IF OBJECT_ID(N'dbo.UserRoles', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.UserRoles (
        UserId BIGINT NOT NULL,
        RoleId BIGINT NOT NULL,
        PRIMARY KEY (UserId, RoleId),
        CONSTRAINT FK_UserRoles_User FOREIGN KEY (UserId) REFERENCES dbo.Users(UserId) ON DELETE CASCADE,
        CONSTRAINT FK_UserRoles_Role FOREIGN KEY (RoleId) REFERENCES dbo.Roles(RoleId) ON DELETE CASCADE
    );
END;
GO

-- 4. BẢNG CHI NHÁNH (BRANCHES)
IF OBJECT_ID(N'dbo.Branches', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Branches (
        BranchId BIGINT IDENTITY(1,1) PRIMARY KEY,
        BranchCode NVARCHAR(20) NOT NULL UNIQUE,
        BranchName NVARCHAR(150) NOT NULL,
        Address NVARCHAR(300) NOT NULL,
        Phone NVARCHAR(20) NULL,
        IsActive BIT NOT NULL DEFAULT 1
    );
END;
GO

-- 5. BẢNG DANH MỤC (CATEGORIES)
IF OBJECT_ID(N'dbo.Categories', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Categories (
        CategoryId BIGINT IDENTITY(1,1) PRIMARY KEY,
        CategoryName NVARCHAR(100) NOT NULL UNIQUE,
        Description NVARCHAR(500) NULL,
        IsActive BIT NOT NULL DEFAULT 1
    );
END;
GO

-- 6. BẢNG SẢN PHẨM (PRODUCTS)
IF OBJECT_ID(N'dbo.Products', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Products (
        ProductId BIGINT IDENTITY(1,1) PRIMARY KEY,
        SKU NVARCHAR(50) NOT NULL UNIQUE,
        ProductName NVARCHAR(200) NOT NULL,
        Description NVARCHAR(MAX) NULL,
        Price DECIMAL(18,2) NOT NULL,
        ImageUrl NVARCHAR(1000) NULL,
        PublicId NVARCHAR(255) NULL,
        CategoryId BIGINT NOT NULL,
        IsActive BIT NOT NULL DEFAULT 1,
        CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        CONSTRAINT FK_Products_Category FOREIGN KEY (CategoryId) REFERENCES dbo.Categories(CategoryId)
    );
END;
GO

-- 7. BẢNG TỒN KHO THEO CHI NHÁNH (INVENTORIES)
IF OBJECT_ID(N'dbo.Inventories', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Inventories (
        BranchId BIGINT NOT NULL,
        ProductId BIGINT NOT NULL,
        Quantity INT NOT NULL DEFAULT 0,
        UpdatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        PRIMARY KEY (BranchId, ProductId),
        CONSTRAINT FK_Inventories_Branch FOREIGN KEY (BranchId) REFERENCES dbo.Branches(BranchId) ON DELETE CASCADE,
        CONSTRAINT FK_Inventories_Product FOREIGN KEY (ProductId) REFERENCES dbo.Products(ProductId) ON DELETE CASCADE
    );
END;
GO

-- 8. BẢNG NỘI DUNG TRANG CHỦ (HOMEPAGE_CONTENT)
IF OBJECT_ID(N'dbo.homepage_content', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.homepage_content (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        announcement NVARCHAR(500) NULL,
        heroEyebrow NVARCHAR(255) NULL,
        heroTitle NVARCHAR(255) NULL,
        heroDescription NVARCHAR(2000) NULL,
        heroButtonText NVARCHAR(255) NULL,
        heroButtonLink NVARCHAR(500) NULL,
        collectionEyebrow NVARCHAR(255) NULL,
        collectionTitle NVARCHAR(255) NULL,
        collectionDescription NVARCHAR(1000) NULL,
        productEyebrow NVARCHAR(255) NULL,
        productTitle NVARCHAR(255) NULL,
        productDescription NVARCHAR(1000) NULL,
        aboutEyebrow NVARCHAR(255) NULL,
        aboutTitle NVARCHAR(255) NULL,
        aboutDescription NVARCHAR(2000) NULL,
        footerTagline NVARCHAR(1000) NULL
    );
END;
GO
