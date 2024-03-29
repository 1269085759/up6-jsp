package up6;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Tool {
    public static String getMD5(String info) {
        try {
            //获取 MessageDigest 对象，参数为 MD5 字符串，表示这是一个 MD5 算法（其他还有 SHA1 算法等）：
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            //update(byte[])方法，输入原数据
            //类似StringBuilder对象的append()方法，追加模式，属于一个累计更改的过程
            md5.update(info.getBytes("UTF-8"));
            //digest()被调用后,MessageDigest对象就被重置，即不能连续再次调用该方法计算原数据的MD5值。可以手动调用reset()方法重置输入源。
            //digest()返回值16位长度的哈希值，由byte[]承接
            byte[] md5Array = md5.digest();
            //byte[]通常我们会转化为十六进制的32位长度的字符串来使用,本文会介绍三种常用的转换方法
            return bytesToHex1(md5Array);
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static String bytesToHex1(byte[] md5Array) {
    StringBuilder strBuilder = new StringBuilder();
    for (int i = 0; i < md5Array.length; i++) {
        int temp = 0xff & md5Array[i];//TODO:此处为什么添加 0xff & ？
        String hexString = Integer.toHexString(temp);
        if (hexString.length() == 1) {//如果是十六进制的0f，默认只显示f，此时要补上0
            strBuilder.append("0").append(hexString);
        } else {
            strBuilder.append(hexString);
        }
    }
    return strBuilder.toString();
}

//通过java提供的BigInteger 完成byte->HexString
public static String bytesToHex2(byte[] md5Array) {
    BigInteger bigInt = new BigInteger(1, md5Array);
    return bigInt.toString(16);
}

//通过位运算 将字节数组到十六进制的转换
public static String bytesToHex3(byte[] byteArray) {
    char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    char[] resultCharArray = new char[byteArray.length * 2];
    int index = 0;
    for (byte b : byteArray) {
        resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
        resultCharArray[index++] = hexDigits[b & 0xf];
    }
    return new String(resultCharArray);
}

//文件MD5
public static String fileToMD5(String path){
    try {
        FileInputStream fis = new FileInputStream(path);
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) != -1) {
            digest.update(buffer, 0, len);
        }
        fis.close();
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return  bigInt.toString(16);
    } catch (IOException e){
        e.printStackTrace();
    }
    catch( NoSuchAlgorithmException e){
    	 e.printStackTrace();    	
    }
    return "";
}

//文件MD5
public static String fileToMD5(InputStream is){
	// 用来将字节转换成 16 进制表示的字符
	char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
 
	try {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] buffer = new byte[1024];
		int len = 0;
		while( (len = is.read(buffer)) != -1)
		{
			md.update(buffer,0,len);
		}
		is.close();
		//md.update(is.read(b).getBytes()); // 通过使用 update 方法处理数据,使指定的 byte数组更新摘要
		byte[] encryptStr = md.digest(); // 获得密文完成哈希计算,产生128 位的长整数
		char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符
		int k = 0; // 表示转换结果中对应的字符位置
		for (int i = 0; i < 16; i++) { // 从第一个字节开始，对每一个字节,转换成 16 进制字符的转换
			byte byte0 = encryptStr[i]; // 取第 i 个字节
			str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移
			str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
		}
		return new String(str); // 换后的结果转换为字符串
	} catch (IOException e){
        e.printStackTrace();
    }
    catch( NoSuchAlgorithmException e){
    	 e.printStackTrace();    	
    }
	return "";	
}
}
