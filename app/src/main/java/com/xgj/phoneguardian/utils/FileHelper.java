/**
 * 
 */
package com.xgj.phoneguardian.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * @author 
 * 文件管理 读写
 * 2014-10-30  
 * Sinopec
 */
public class FileHelper {
	/**
	 * 获取SD卡的目录，如果SD卡不存在则返回内存卡的目录
	 * 
	 * @return
	 */
	public static String getRootPath(String indexName) {
		// 判断SD卡是否可以读写。
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		File sdDir1 = Environment.getExternalStorageDirectory();// 如果没有SD卡，则存放于内存卡根目录
		// File sdDir2 = Environment.getRootDirectory();// 获取跟目录
		String sd = sdDir1.toString();

		if (sdCardExist && isContainMapFile(sdDir1 , indexName )) {
			return sdDir1.toString();
		} else {
			File file = new File(sd.split("/")[1]);
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					if (isContainMapFile(files[i] , indexName )) {
						return files[i].getAbsolutePath();
					}
				}
			}
		}
		return null;
	}

	/**
	 * 判断目录下是否含有HandLaser目录
	 *
	 * @param file
	 * @return
	 */
	private static boolean isContainMapFile(File file , String indexName) {
		File[] files = file.listFiles();
		if (null == files) {
			return false;
		}
		for (int i = 0; i < files.length; i++) {
			if (indexName.equals(files[i].getName())) {
				return true;
			}
		}
		return false;
	}


	/**
	 * 获取外部存储根目录
	 * @return
	 */
	public static String getRootStoryPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在

		if (sdCardExist) // 如果SD卡存在，则获取跟目录
		{
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		} else {
			sdDir = Environment.getRootDirectory();// 如果没有SD卡，则存放于内存卡根目录
		}

		return sdDir.toString();
	}
	
	/**
	 * 判断文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isFileExist(String fileName) {
		if ( fileName == null || fileName.equals("") == true ) {
			return false ;
		}
		File file = new File(fileName);
		return file.exists();
	}

	/**
	 * 把InputStream流拷贝到指定的文件目录下fileName
	 * 
	 * @param is
	 * @param fileName
	 * @throws IOException
	 */
	public static void copyFile(InputStream is, String fileName)
			throws IOException {

		BufferedOutputStream outBuff = null;

		File file = new File(fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		try {
			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(file));
			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = is.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();

		} finally {
			// 关闭流
			if (is != null)
				is.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	// 初始化地图数据和配置文件目录
	public static void zip2SD(Context context , String strZipName , String strToDir ) {
		File mapFile = new File( strToDir );
		//		if (mapFile.exists()) {
		//			return;
		//		}
		if ( mapFile.exists() == false ) {
			mapFile.mkdirs() ;
		}
		try {
			InputStream is = context.getAssets().open(strZipName + ".zip");
			copyFile(is, strToDir + "/" + strZipName + ".zip");
			File zipFilen = new File( strToDir + "/" + strZipName + ".zip");
			upZipFile(zipFilen, strToDir , null, 0);
			zipFilen.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static long getFileSize(Context context , String strZipName ) {
		
		InputStream is;
		try {
			is = context.getAssets().open(strZipName );
			long lenght = is.available();
	        return lenght;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return -1;
	}
	/** 
	 *  根据路径删除指定的目录或文件，无论存在与否 
	 * @param sPath  要删除的目录或文件 
 	 * @return 删除成功返回 true，否则返回 false。 
	 */  
	public static boolean deleteFolder( String sPath ) {
	    boolean flag = false;  
	    File file = new File(sPath);
	    // 判断目录或文件是否存在  
	    if (!file.exists()) {  // 不存在返回 false  
	        return flag;
	    } else {  
	        // 判断是否为文件  
	        if (file.isFile()) {  // 为文件时调用删除文件方法  
	            return deleteFile(sPath);  
	        } else {  // 为目录时调用删除目录方法  
	            return deleteDirectory( sPath );  
	        }  
	    }  
	}
	
	/** 
	 * 删除单个文件 
	 * @param   sPath    被删除文件的文件名 
	 * @return 单个文件删除成功返回true，否则返回false 
	 */  
	public static boolean deleteFile(String sPath) {
	    boolean flag = false;  
	    File file = new File(sPath);
	    // 路径为文件且不为空则进行删除  
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	        flag = true;  
	    }  
	    return flag;  
	} 
	
	/** 
	 * 清空目录以及目录下的文件 
	 * @param   sPath 被删除目录的文件路径 
	 * @return  目录删除成功返回true，否则返回false 
	 */  
	public static boolean clearDirectory(String sPath) {
	    //如果sPath不以文件分隔符结尾，自动添加文件分隔符  
	    if (!sPath.endsWith(File.separator)) {
	        sPath = sPath + File.separator;
	    }  
	    File dirFile = new File(sPath);
	    //如果dir对应的文件不存在，或者不是一个目录，则退出  
	    if (!dirFile.exists() || !dirFile.isDirectory()) {  
	        return false;  
	    }  
	    boolean flag = true;  
	    //删除文件夹下的所有文件(包括子目录)  
	    File[] files = dirFile.listFiles();
	    for (int i = 0; i < files.length; i++) {  
	        //删除子文件  
	        if (files[i].isFile()) {  
	            flag = deleteFile(files[i].getAbsolutePath());  
	            if (!flag) break;  
	        } //删除子目录  
	        else {  
	            flag = deleteDirectory(files[i].getAbsolutePath());  
	            if (!flag) break;  
	        }  
	    }
	    dirFile.delete() ;
	    if (flag) return true;  

	    return false;
	}
	
	/** 
	 * 删除目录（文件夹）以及目录下的文件 
	 * @param   sPath 被删除目录的文件路径 
	 * @return  目录删除成功返回true，否则返回false 
	 */  
	public static boolean deleteDirectory(String sPath) {
	    //如果sPath不以文件分隔符结尾，自动添加文件分隔符  
	    if (!sPath.endsWith(File.separator)) {
	        sPath = sPath + File.separator;
	    }  
	    File dirFile = new File(sPath);
	    //如果dir对应的文件不存在，或者不是一个目录，则退出  
	    if (!dirFile.exists() || !dirFile.isDirectory()) {  
	        return false;  
	    }  
	    boolean flag = true;  
	    //删除文件夹下的所有文件(包括子目录)  
	    File[] files = dirFile.listFiles();
	    for (int i = 0; i < files.length; i++) {  
	        //删除子文件  
	        if (files[i].isFile()) {  
	            flag = deleteFile(files[i].getAbsolutePath());  
	            if (!flag) break;  
	        } //删除子目录  
	        else {  
	            flag = deleteDirectory(files[i].getAbsolutePath());  
	            if (!flag) break;  
	        }  
	    }  
	    if (!flag) return false;  
	    //删除当前目录  
	    if (dirFile.delete()) {  
	        return true;  
	    } else {  
	        return false;  
	    }  
	}
	
	@SuppressWarnings("resource")
	private static boolean upZipFile(File zipFilen, String unzipDirectory , String AsyncTag , int type ) {
		boolean isZip = false ; 
		ZipFile zipFile;
		try {
			zipFile = new ZipFile(zipFilen);
			File unzipFile = new File(unzipDirectory);
			Enumeration<?> zipEnum = zipFile.entries();
			InputStream input = null;
			OutputStream output = null;
			while (zipEnum.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) zipEnum.nextElement();
				String entryName = new String(entry.getName().getBytes("ISO8859_1"));
				if (entry.isDirectory())
					new File(unzipFile.getAbsolutePath() + "/" + entryName).mkdir();
				else {
					input = zipFile.getInputStream(entry);
					output = new FileOutputStream(new File(
							unzipFile.getAbsolutePath() + "/" + entryName));
					byte[] buffer = new byte[1024 * 8];
					int readLen = 0;
					while ((readLen = input.read(buffer, 0, 1024 * 8)) != -1)
						output.write(buffer, 0, readLen);
					input.close();
					output.flush();
					output.close();
				}
				isZip = true ; 
			}
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isZip = false ; 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isZip = false ; 
		}		
		return isZip ;
	}
	
}
