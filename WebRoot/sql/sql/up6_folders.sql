CREATE TABLE [dbo].[up6_folders](
	[f_id] [char](32) NOT NULL,
	[f_nameLoc] [nvarchar](255) NULL,
	[f_pid] [char](32) NULL,
	[f_uid] [int] NULL,
	[f_lenLoc] [bigint] NULL,
	[f_sizeLoc] [nvarchar](50) NULL,
	[f_pathLoc] [nvarchar](255) NULL,
	[f_pathSvr] [nvarchar](255) NULL,
	[f_folders] [int] NULL,
	[f_fileCount] [int] NULL,
	[f_filesComplete] [int] NULL,
	[f_complete] [bit] NULL,
	[f_deleted] [bit] NULL,
	[f_time] [datetime] NULL,
	[f_pidRoot] [char](32) NULL,
	[f_pathRel] [nvarchar](255) NULL
) ON [PRIMARY]

SET ANSI_PADDING OFF

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件夹名称' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_folders', @level2type=N'COLUMN', @level2name=N'f_nameLoc'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'父级ID' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_folders', @level2type=N'COLUMN', @level2name=N'f_pid'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'用户ID。' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_folders', @level2type=N'COLUMN', @level2name=N'f_uid'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'数字化的大小。以字节为单位。示例：1023652' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_folders', @level2type=N'COLUMN', @level2name=N'f_lenLoc'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'格式化的大小。示例：10G' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_folders', @level2type=N'COLUMN', @level2name=N'f_sizeLoc'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件夹在客户端的路径' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_folders', @level2type=N'COLUMN', @level2name=N'f_pathLoc'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件夹在服务端的路径' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_folders', @level2type=N'COLUMN', @level2name=N'f_pathSvr'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件夹数' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_folders', @level2type=N'COLUMN', @level2name=N'f_folders'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件数' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_folders', @level2type=N'COLUMN', @level2name=N'f_fileCount'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'已上传完的文件数' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_folders', @level2type=N'COLUMN', @level2name=N'f_filesComplete'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'是否已上传完毕' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_folders', @level2type=N'COLUMN', @level2name=N'f_complete'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'是否已删除' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_folders', @level2type=N'COLUMN', @level2name=N'f_deleted'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'上传时间' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_folders', @level2type=N'COLUMN', @level2name=N'f_time'
