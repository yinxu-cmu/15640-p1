/**
 * 
 */
package Classes;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * @author Yin Xu
 *
 */
public class FileCompression implements MigratableProcess{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4774273775993606847L;
	private String inputFile;
	private String outputFile;
	private volatile boolean suspending = false;
	private boolean finished = false;
	
	/* transactionalIO */
	private TransactionalFileInputStream inStream;
	private TransactionalFileOutputStream outStream;
	
	/* constructor */
	public FileCompression(){}
	public FileCompression(String[] args) throws Exception{
		/* check input args */
		if (args.length != 1 ) {
			System.out.println("usage: FileCompression <inputfile>");
			throw new Exception("Invalid arguments");
		}
		if (!new File(args[0]).isFile()) {
			System.out.println("Not a valid file");
			throw new Exception("Invalid arguments");
		}
		
		inputFile = args[0];
		outputFile = args[0] + ".gz";
		
		inStream = new TransactionalFileInputStream(inputFile);
		outStream = new TransactionalFileOutputStream(outputFile);
	}
	
	public String toString(){
		return "FileCompression: " + inputFile;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		DataInputStream in = new DataInputStream(inStream);
		try {
			GZIPOutputStream out = new GZIPOutputStream(outStream);

			byte[] buf = new byte[256];
			int numbercount = 0;
			while (!suspending && !finished) {

				numbercount = in.read(buf);
				if (numbercount == -1) {
					finished = true;
					break;
				}
				
				out.write(buf, 0, numbercount);
				
				try {
					Thread.sleep(600);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		inStream.closeStream();
		inStream.setMigrated(true);
		suspending = false;
	}

	/**
	 * 
	 */
	@Override
	public void suspend() {
		suspending = true;
		while (suspending && !finished);
	}
	
	/**
	 * 
	 */
	@Override
	public boolean getFinished() {
		return this.finished;
	}


}
