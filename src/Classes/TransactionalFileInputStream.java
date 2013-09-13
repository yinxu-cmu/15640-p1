/**
 * 
 */
package Classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.io.StringReader;

/**
 * When a read is requested from the class, it:
 * 1. open the file
 * 2. seek to the requisite position
 * 3. perform the operation
 * 4. close the file.
 * 
 * @author Yin Xu
 *
 */
public class TransactionalFileInputStream extends InputStream implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2397096433935542458L;
	private String fileName;
	private long counter;
	/* cache the connection? */
	private RandomAccessFile fileStream;
	
	/**
	 * constructor
	 */
	public TransactionalFileInputStream(){}
	
	public TransactionalFileInputStream(String fileName){
		this.fileName = fileName;
		counter = 0L;
	}
	
	/**
	 * read a byte the file from specified position.
	 */
	@Override
	public int read() throws IOException {
		fileStream = new RandomAccessFile(fileName, "rws");
		fileStream.seek(counter);
		int retByte = fileStream.read();
		if (retByte != -1) {
			counter++;
		}
		fileStream.close();

		return retByte;
	}
	
	/**
	 * Reads up to b.length bytes of data from the input stream
	 * @throws IOException 
	 */
	@Override
	public int read(byte[] b) throws IOException {
		fileStream = new RandomAccessFile(fileName, "rws");
		fileStream.seek(counter);
		int numRead = fileStream.read(b);
		if (numRead != -1) {
			counter += numRead;
		}
		fileStream.close();
		
		return numRead;
		
	}
	
	/**
	 * Reads up to len bytes of data from the input stream
	 * starting from the offset.
	 * @throws IOException 
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		fileStream = new RandomAccessFile(fileName, "rws");
		fileStream.seek(counter++);
		int numRead = fileStream.read(b, off, len);
		if (numRead != -1) {
			counter += numRead;
		}
		fileStream.close();
		
		return numRead;
		
	}
	
	
	public static void main(String[] args) throws IOException{
		
		String s = "ABCD\n1234\nabcd";
		StringReader reader = new StringReader(s);
		BufferedReader br = new BufferedReader(reader);
		
		try {
			System.out.println(br.readLine());
			
			br.mark(0);
			System.out.println(br.readLine());
			br.reset();
			System.out.println(br.readLine());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (reader != null)
				reader.close();
			if (br != null)
				br.close();
		}
		
	}
	
}
