/**
 * 
 */
package Classes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

/**
 * When a read is requested from the class, it:
 * 1. open the file
 * 2. seek to the requisite position
 * 3. perform the operation
 * 4. close the file.
 * 
 * To cache file handler, only close it when migrated.
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
	/* cache the connection */
	private boolean migrated; /* flag for migration */
	private transient RandomAccessFile fileStream;
	
	/**
	 * constructor
	 */
	public TransactionalFileInputStream(){}
	
	public TransactionalFileInputStream(String fileName) {
		this.fileName = fileName;
		counter = 0L;
		migrated = false;
		try {
			fileStream = new RandomAccessFile(fileName, "rws");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * read a byte the file from specified position.
	 */
	@Override
	public int read() throws IOException {
		int retByte = 0;
		if (migrated) {
			fileStream = new RandomAccessFile(fileName, "rws");
			migrated = false;
		}
		
		fileStream.seek(counter);
		retByte = fileStream.read();
		if (retByte != -1) {
			counter++;
		}

		return retByte;
	}
	
//	/**
//	 * Reads up to b.length bytes of data from the input stream
//	 * @throws IOException 
//	 */
//	@Override
//	public int read(byte[] b) throws IOException {
//		int numRead = 0;
//		if (!migrated) {
//			fileStream.seek(counter);
//			numRead = fileStream.read(b);
//			if (numRead != -1) {
//				counter += numRead;
//			}
//		} else {
//			fileStream.close();
//			fileStream = new RandomAccessFile(fileName, "rws");
//			migrated = false;
//		}
//
//		return numRead;
//
//	}
//	
//	/**
//	 * Reads up to len bytes of data from the input stream
//	 * starting from the offset.
//	 * @throws IOException 
//	 */
//	@Override
//	public int read(byte[] b, int off, int len) throws IOException {
//		int numRead = 0;
//		if (!migrated) {
//
//			fileStream.seek(counter);
//			numRead = fileStream.read(b, off, len);
//			if (numRead != -1) {
//				counter += numRead;
//			}
//		} else {
//			fileStream.close();
//			fileStream = new RandomAccessFile(fileName, "rws");
//			migrated = false;
//		}
//
//		return numRead;
//
//	}
//
	public void closeStream() {
		try {
			fileStream.close(); /* close the file handler of last node */
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in closing input file");
			e.printStackTrace();
		} 
	}
	/**
	 * @return the migrated
	 */
	public boolean getMigrated() {
		return migrated;
	}

	/**
	 * @param migrated the migrated to set
	 */
	public void setMigrated(boolean migrated) {
		this.migrated = migrated;
	}

}
