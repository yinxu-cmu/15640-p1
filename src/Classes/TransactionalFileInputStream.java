/**
 * 
 */
package Classes;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;

/**
 * When a read is requested from the class, it:
 * 1. open the file
 * 2. seek to the requisite position
 * 3. perform the operation
 * 4. close the file.
 * 
 * @author yinxu
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
	private FileInputStream fis;
	
	/**
	 * constructor
	 */
	public TransactionalFileInputStream(){}
	
	public TransactionalFileInputStream(String fileName){
		this.fileName = fileName;
		this.counter = 0L;
	}
	
	/**
	 * read the file from specified position.
	 */
	@Override
	public int read() throws IOException {
		fis = new FileInputStream(fileName);
		fis.skip(counter);
		int retByte = fis.read();
		return retByte;
	}
	
	public static void main(String[] args) throws IOException{
		
		String s = "ABCD\n1234\nabcd";
		StringReader reader = new StringReader(s);
		BufferedReader br = new BufferedReader(reader);
		
		try {
			br.mark(0);
			System.out.println(br.readLine());
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
