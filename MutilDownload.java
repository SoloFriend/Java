import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class Mutildownload {
	/**
	 * ============================================================
	 * 
	 * 版 权 ： XXX公司 版权所有 (c) 2015
	 * 
	 * 作 者 : syt
	 * 
	 * 版 本 ： 1.0
	 * 
	 * 创建日期 ： 2015-8-25 上午11:56:24
	 * 
	 * 描 述 ： 多线程断点下载
	 * 			1、获取文件大小；
	 *			2、在客户端创建一个大小和服务器一模一样的文件；
	 *			3、计算每个线程的开始位置和结束位置；
	 *			4、开启多个线程；
	 *			5、若有中断，读取中断时存储的位置；
	 *			6、记录当前线程下载的位置；
	 *			7、知道每个线程什么时候下载完毕。
	 *			8、删除保存位置的文件
	 *
	 * 修订历史 ：
	 * 
	 * ============================================================
	 **/
	/** 定义下载地址:http://192.168.1.103:8080/Notepad.exe*/
	private static String path = "http://192.168.1.103:8080/Notepad.exe";
	/** 定义线程数 */
	private static final int threadCount = 3;
	/** 定义当前运行线程数 */
	private static int runningThread = 0;

	public static void main(String[] args) {
		try {
			// 创建一个URL对象参数就是下载地址
			URL url = new URL(path);
			// 获取HttpURLConnection连接对象
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 设置参数发送get请求
			conn.setRequestMethod("GET");
			// 设置链接网络的超时时间
			conn.setConnectTimeout(5000);
			// 获取返回状态码
			int code = conn.getResponseCode();
			if (code == 200) {
				runningThread = threadCount;
				// 获取文件大小
				int length = conn.getContentLength();
				System.out.println("文件长度为:" + length);

				// 创建大小和服务器一模一样的文件
				RandomAccessFile rafAccessFile = new RandomAccessFile(
						getFileName(path), "rw");
				rafAccessFile.setLength(length);
				// 计算bolkSize，每个线程下载的大小
				int blockSize = length / threadCount;
				// 计算每个线程的开始位置和结束位置
				for (int i = 0; i < threadCount; i++) {
					// 每个线程的起始位置
					int startIndex = i * blockSize;
					// 每个线程的结束位置
					int endIndex = (i + 1) * blockSize - 1;
					// 若是最后一个线程，直接设置文件末尾
					if (i == threadCount - 1) {
						endIndex = length - 1;
					}
					// 开启线程去服务器下载文件
					DownLoadThread downLoadThread = new DownLoadThread(
							startIndex, endIndex, i);
					downLoadThread.start();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 定义线程去下载文件
	private static class DownLoadThread extends Thread {
		// 通过构造方法把每个线程下载的开始位置和结束位置传递进来
		private int startIndex;
		private int endIndex;
		private int threadId;

		public DownLoadThread(int startIndex, int endIndex, int threadId) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			this.threadId = threadId;
		}

		public void run() {
			// 实现去服务器下载文件的逻辑
			try {
				// 创建一个URL对象，参数是地址
				URL url = new URL(path);
				// 获取HttpURLConnection对象
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				// 设置访问模式
				conn.setRequestMethod("GET");
				// 设置访问链接超时时间
				conn.setReadTimeout(5000);
				// 如果中间断过，继续上次的位置，继续下载，从文件中读取上次下载的位置
				File file = new File(getFileName(path)+threadId + ".txt");
				if (file.exists() && file.length() > 0) {
					FileInputStream fis = new FileInputStream(file);
					BufferedReader bufr = new BufferedReader(
							new InputStreamReader(fis));
					String lastPositionn = bufr.readLine();// 读取出的内容就是上一次下载的位置
					int lastPosition = Integer.parseInt(lastPositionn);
					System.out.println("当前线程下载位置：" + lastPosition);
					// 改变startIndex的位置
					startIndex = lastPosition;
					fis.close();
					bufr.close();
				}
				// 设置一个请求头Range（告诉每个线程的写入位置）
				conn.setRequestProperty("Range", "bytes=" + startIndex + "-"
						+ endIndex);
				// 获取服务器返回状态码，200 代表获取服务器全部资源成功，206代表获取部分资源成功
				int code = conn.getResponseCode();
				if (code == 206) {
					// 创建随机读写文件对象
					RandomAccessFile raf = new RandomAccessFile(getFileName(path),
							"rw");
					// 每个线程都从自己的位置开始写
					raf.seek(startIndex);

					InputStream in = conn.getInputStream();
					// 把数据写入文件
					int len = -1;
					byte[] buffer = new byte[1024 * 1024];
					// 已下载的文件大小
					int total = 0;
					while ((len = in.read(buffer)) != -1) {
						raf.write(buffer, 0, len);
						total += len;
						// 实现断点续传，把当前线程下载的位置给存起来，下次下载的时候按照上次下载的位置继续下载就可以了
						int currentThreadPosition = startIndex + total;
						// 存放当前线程下载位置
						RandomAccessFile rafPosition = new RandomAccessFile(
								getFileName(path)+threadId + ".txt", "rwd");
						rafPosition.write(String.valueOf(currentThreadPosition)
								.getBytes());
						rafPosition.close();
					}

					// 关闭流，释放资源
					raf.close();
				}

				System.out.println("线程" + threadId + "下载完毕！");
				// 删掉存放位置的文件
				synchronized (DownLoadThread.class) {
					runningThread--;
					if (runningThread == 0) {
						for (int i = 0; i < threadCount; i++) {
							File deleFile = new File(getFileName(path)+i + ".txt");
							deleFile.delete();
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** 获取文件名 http://192.168.1.103:8080/Notepad.exe */
	public static String getFileName(String path) {
		int fileNameIndex = path.lastIndexOf("/")+1;
		return path.substring(fileNameIndex);
	}
}
