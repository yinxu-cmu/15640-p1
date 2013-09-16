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
