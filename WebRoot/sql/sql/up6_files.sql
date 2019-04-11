CREATE TABLE [dbo].[up6_files](
	[f_id] [char](32) COLLATE Chinese_PRC_CI_AS NOT NULL,
	[f_pid] [char](32) COLLATE Chinese_PRC_CI_AS NULL CONSTRAINT [DF_up6_files_f_pid]  DEFAULT (''),
	[f_pidRoot] [char](32) COLLATE Chinese_PRC_CI_AS NULL CONSTRAINT [DF_up6_files_f_pidRoot]  DEFAULT (''),
	[f_fdTask] [bit] NULL CONSTRAINT [DF_up6_files_f_fdTask]  DEFAULT ((0)),
	[f_fdChild] [bit] NULL CONSTRAINT [DF_up6_files_f_fdChild]  DEFAULT ((0)),
	[f_uid] [int] NULL CONSTRAINT [DF_up6_files_f_uid]  DEFAULT ((0)),
	[f_nameLoc] [varchar](255) COLLATE Chinese_PRC_CI_AS NULL CONSTRAINT [DF_up6_files_f_nameLoc]  DEFAULT (''),
	[f_nameSvr] [varchar](255) COLLATE Chinese_PRC_CI_AS NULL CONSTRAINT [DF_up6_files_f_nameSvr]  DEFAULT (''),
	[f_pathLoc] [varchar](512) COLLATE Chinese_PRC_CI_AS NULL CONSTRAINT [DF_up6_files_f_pathLoc]  DEFAULT (''),
	[f_pathSvr] [varchar](512) COLLATE Chinese_PRC_CI_AS NULL CONSTRAINT [DF_up6_files_f_pathSvr]  DEFAULT (''),
	[f_pathRel] [varchar](512) COLLATE Chinese_PRC_CI_AS NULL CONSTRAINT [DF_up6_files_f_pathRel]  DEFAULT (''),
	[f_md5] [varchar](40) COLLATE Chinese_PRC_CI_AS NULL CONSTRAINT [DF_up6_files_f_md5]  DEFAULT (''),
	[f_lenLoc] [bigint] NULL CONSTRAINT [DF_up6_files_f_lenLoc]  DEFAULT ((0)),
	[f_sizeLoc] [varchar](10) COLLATE Chinese_PRC_CI_AS NULL CONSTRAINT [DF_up6_files_f_sizeLoc]  DEFAULT ('0Bytes'),
	[f_pos] [bigint] NULL CONSTRAINT [DF_up6_files_f_pos]  DEFAULT ((0)),
	[f_lenSvr] [bigint] NULL CONSTRAINT [DF_up6_files_f_lenSvr]  DEFAULT ((0)),
	[f_perSvr] [varchar](6) COLLATE Chinese_PRC_CI_AS NULL CONSTRAINT [DF_up6_files_f_perSvr]  DEFAULT ('0%'),
	[f_complete] [bit] NULL CONSTRAINT [DF_up6_files_f_complete]  DEFAULT ((0)),
	[f_time] [datetime] NULL CONSTRAINT [DF_up6_files_f_time]  DEFAULT (getdate()),
	[f_deleted] [bit] NULL CONSTRAINT [DF_up6_files_f_deleted]  DEFAULT ((0)),
	[f_scan] [bit] NOT NULL CONSTRAINT [DF_up6_files_f_scan]  DEFAULT ((0)),
 CONSTRAINT [PK_up6_files_1] PRIMARY KEY CLUSTERED 
(
	[f_id] ASC
)WITH (IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

SET ANSI_PADDING OFF

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件唯一GUID,由控件生成' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_id'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'父级文件夹ID' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_pid'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'根级文件夹ID' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_pidRoot'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'表示是否是一个文件夹上传任务' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_fdTask'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'是否是文件夹中的子项' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_fdChild'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件在本地电脑中的名称。例：QQ.exe ' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_nameLoc'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件在服务器中的名称。一般为文件MD5+扩展名。' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_nameSvr'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件在本地电脑中的完整路径。
示例：D:\Soft\QQ.exe
' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_pathLoc'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件在服务器中的完整路径。
示例：F:\ftp\user1\QQ2012.exe
' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_pathSvr'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件在服务器中的相对路径。
示例：/www/web/upload/QQ2012.exe
' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_pathRel'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件MD5' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_md5'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件总长度。以字节为单位
最大值：9,223,372,036,854,775,807
' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_lenLoc'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'格式化的文件尺寸。示例：10MB' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_sizeLoc'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件续传位置。
最大值：9,223,372,036,854,775,807
' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_pos'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'已上传长度。以字节为单位。
最大值：9,223,372,036,854,775,807
' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_lenSvr'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'已上传百分比。示例：10%' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_perSvr'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'是否已上传完毕。' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_complete'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'文件上传时间' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_time'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'是否已删除。' ,@level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'up6_files', @level2type=N'COLUMN', @level2name=N'f_deleted'

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'是否已经扫描完毕，提供给大型文件夹使用' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'up6_files', @level2type=N'COLUMN',@level2name=N'f_scan'
