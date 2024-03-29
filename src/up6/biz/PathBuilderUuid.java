package up6.biz;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import up6.model.FileInf;


public class PathBuilderUuid extends PathBuilder{
	/* 生成文件夹存储路径，完全与客户端文件夹结构保持一致
	 * 格式： 	
	 *  upload/2016/05/17/uuid/folder_name
	 * 更新记录：
	 *  2016-03-01 upload/uid/folders/uuid/folder_name
	 * 	2016-05-17 将格式改为日期格式
	 * 
	 */
	public String genFolder(FileInf fd) throws IOException
	{
		SimpleDateFormat fmtDD = new SimpleDateFormat("dd");
		SimpleDateFormat fmtMM = new SimpleDateFormat("MM");
		SimpleDateFormat fmtYY = new SimpleDateFormat("yyyy");
		
		Date date = new Date();
		String strDD = fmtDD.format(date);
		String strMM = fmtMM.format(date);
		String strYY = fmtYY.format(date);
		
		String uuid = fd.id;//取消生成新ID,使用原始文件夹ID
		
		String path = this.getRoot() + "/";
		path = path.concat(strYY);
		path = path.concat("/");
		path = path.concat(strMM);
		path = path.concat("/");
		path = path.concat(strDD);
		path = path.concat("/");
		
		path = path.concat(uuid);
		path = path.concat("/");
		path = path.concat(fd.nameLoc);
		return path;
	}
	
	/* 保留原始文件名称，不检查文件是否重复
	 * 格式：
	 * 	upload/uid/年/月/日/uuid/file_name
	 * @see Xproer.PathBuilder#genFile(int, Xproer.xdb_files)
	 */
	public String genFile(int uid,FileInf f) throws IOException{
		String uuid = f.id;//取消生成ID，使用自已的ID

		SimpleDateFormat fmtDD = new SimpleDateFormat("dd");
		SimpleDateFormat fmtMM = new SimpleDateFormat("MM");
		SimpleDateFormat fmtYY = new SimpleDateFormat("yyyy");
		
		Date date = new Date();
		String strDD = fmtDD.format(date);
		String strMM = fmtMM.format(date);
		String strYY = fmtYY.format(date);
		
		String path = this.getRoot() + "/";
		path = path.concat(strYY);
		path = path.concat("/");
		path = path.concat(strMM);
		path = path.concat("/");
		path = path.concat(strDD);
		path = path.concat("/");
		path = path.concat(uuid);
		path = path.concat("/");
		path = path.concat(f.nameLoc);
		return path;
	}

}
