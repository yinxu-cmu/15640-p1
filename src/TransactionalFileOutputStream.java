
import java.io.FileNotFoundException;
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
	private boolean migrated; 		/* flag for migration */
	private transient RandomAccessFile fileStream;
	
	/**
	 * constructor.
	 */
	public TransactionalFileOutputStream(){}
	public TransactionalFileOutputStream(String fileName) {
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
	 * Writes the specified byte to this file output stream.
	 */
	@Override
	public void write(int b) throws IOException {
		if (migrated) {
			fileStream = new RandomAccessFile(fileName, "rws");
			migrated = false;
		}
		fileStream.seek(counter++);
		fileStream.write(b);
	}
	
	/**
	 * Writes b.length bytes from the specified byte array to this 
	 * file output stream.
	 */
	@Override
	public void write(byte[] b) throws IOException {
		if (migrated) {
			fileStream = new RandomAccessFile(fileName, "rws");
			migrated = false;
		}
		fileStream.seek(counter);
		fileStream.write(b);
		counter += b.length;
	}
	
	/**
	 * Writes len bytes from the specified byte array starting at 
	 * offset off to this file output stream.
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (migrated) {
			fileStream = new RandomAccessFile(fileName, "rws");
			migrated = false;
		}
		fileStream.seek(counter);
		fileStream.write(b, off, len);
		counter += len;
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
