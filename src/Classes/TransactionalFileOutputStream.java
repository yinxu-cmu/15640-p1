/**
 * 
 */
package Classes;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

/**
 * @author Yin Xu
 *
 */
public class TransactionalFileOutputStream extends OutputStream implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7158829490078158413L;
	private String fileName;
	private long counter;
	private RandomAccessFile fileStream;

	/**
	 * constructor.
	 */
	public TransactionalFileOutputStream(){}
	public TransactionalFileOutputStream(String fileName) {
		this.fileName = fileName;
		counter = 0L;
	}
	
	/**
	 * Writes the specified byte to this file output stream.
	 */
	@Override
	public void write(int b) throws IOException {
		fileStream = new RandomAccessFile(fileName, "rws");
		fileStream.seek(counter++);
		fileStream.write(b);
		fileStream.close();
	}
	
	/**
	 * Writes b.length bytes from the specified byte array to this 
	 * file output stream.
	 */
	@Override
	public void write(byte[] b) throws IOException {
		fileStream = new RandomAccessFile(fileName, "rws");
		fileStream.seek(counter);
		fileStream.write(b);
		counter += b.length;
		fileStream.close();
	}
	
	/**
	 * Writes len bytes from the specified byte array starting at 
	 * offset off to this file output stream.
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		fileStream = new RandomAccessFile(fileName, "rws");
		fileStream.seek(counter);
		fileStream.write(b, off, len);
		counter += len;
		fileStream.close();
	}

}
