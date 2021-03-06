/****** Object:  Database [MiEmpresa]    Script Date: 1/09/2020 19:12:58 ******/
CREATE DATABASE [MiEmpresa]
GO
USE [MiEmpresa]
GO
/****** Object:  Table [dbo].[cliente]    Script Date: 1/09/2020 19:12:59 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[cliente](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[ruc] [varchar](11) NOT NULL,
	[razonSocial] [varchar](100) NOT NULL,
	[direccion] [varchar](150) NULL,
 CONSTRAINT [PK_cliente] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[factura]    Script Date: 1/09/2020 19:12:59 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[factura](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[nombre] [varchar](50) NULL,
	[idCliente] [int] NOT NULL,
	[fecha] [varchar](10) NOT NULL,
	[moneda] [varchar](20) NOT NULL,
	[igv] [decimal](11, 2) NOT NULL,
	[importe] [decimal](11, 2) NOT NULL,
 CONSTRAINT [PK_factura] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[cliente] ON

INSERT [dbo].[cliente] ([id], [ruc], [razonSocial], [direccion]) VALUES (1, N'20538856674', N'ARTROSCOPICTRAUMA S.A.C.', N'Lima')
INSERT [dbo].[cliente] ([id], [ruc], [razonSocial], [direccion]) VALUES (2, N'20549500553', N'ASERCO EB EMPRESA INDIVIDUAL DE RESPONSABILIDAD LIMITADA', N'Lima')
INSERT [dbo].[cliente] ([id], [ruc], [razonSocial], [direccion]) VALUES (3, N'20558629585', N'INSUMOS PISQUEROS DEL SUR E.I.R.L.', N'Lima')
INSERT [dbo].[cliente] ([id], [ruc], [razonSocial], [direccion]) VALUES (4, N'20551597939', N'	ASOCIACION COMUNIDAD CRISTIANA EVANGELICA FUEGO DE DIOS DEL PERU', N'Lima')
INSERT [dbo].[cliente] ([id], [ruc], [razonSocial], [direccion]) VALUES (5, N'20553856451', N'BI GRAND CONFECCIONES S.A.C.', N'Lima')
INSERT [dbo].[cliente] ([id], [ruc], [razonSocial], [direccion]) VALUES (6, N'20525426778', N'CONCEPTOS & CONSTRUCCIONES V.G EIRL', N'Lima')
INSERT [dbo].[cliente] ([id], [ruc], [razonSocial], [direccion]) VALUES (7, N'20601155185', N'CORPORACION CARMINA SAC', N'Lima')
INSERT [dbo].[cliente] ([id], [ruc], [razonSocial], [direccion]) VALUES (8, N'20555629541', N'CORPORACION INDUSTRIAL ALPA`C S.A.C.', N'Lima')
INSERT [dbo].[cliente] ([id], [ruc], [razonSocial], [direccion]) VALUES (9, N'20518639928', N'H & J E HIJOS E.I.R.L', N'Lima')
SET IDENTITY_INSERT [dbo].[cliente] OFF
SET IDENTITY_INSERT [dbo].[factura] ON

INSERT [dbo].[factura] ([id], [nombre], [idCliente], [fecha], [moneda], [igv], [importe]) VALUES (1, N'FF01-00000001', 3, N'01-09-2020', N'PEN', CAST(180.00 AS Decimal(11, 2)), CAST(1180.00 AS Decimal(11, 2)))
INSERT [dbo].[factura] ([id], [nombre], [idCliente], [fecha], [moneda], [igv], [importe]) VALUES (2, N'FF01-00000002', 9, N'01-09-2020', N'USD', CAST(126.00 AS Decimal(11, 2)), CAST(826.00 AS Decimal(11, 2)))
INSERT [dbo].[factura] ([id], [nombre], [idCliente], [fecha], [moneda], [igv], [importe]) VALUES (3, N'FF01-00000003', 1, N'01-09-2020', N'USD', CAST(27.00 AS Decimal(11, 2)), CAST(177.00 AS Decimal(11, 2)))
SET IDENTITY_INSERT [dbo].[factura] OFF
ALTER TABLE [dbo].[factura]  WITH CHECK ADD  CONSTRAINT [FK_factura_cliente] FOREIGN KEY([idCliente])
REFERENCES [dbo].[cliente] ([id])
GO
ALTER TABLE [dbo].[factura] CHECK CONSTRAINT [FK_factura_cliente]
GO
USE [master]
GO
ALTER DATABASE [MiEmpresa] SET  READ_WRITE
GO
