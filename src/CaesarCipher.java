import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;

/**
 * @author Yin Xu
 * 
 */
public class CaesarCipher implements MigratableProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7847897814538035544L;
	private String mode;
	private String inputFile;
	private String outputFile;
	private volatile boolean suspending = false;
	private boolean finished = false;
	private int count;

	/* transactionalIO */
	private TransactionalFileInputStream inStream;
	private TransactionalFileOutputStream outStream;

	/* constructor */
	public CaesarCipher() {
	}

	public CaesarCipher(String[] args) throws Exception {
		if (args.length != 3
				|| (!args[0].equals("encode") && !args[0].equals("decode"))) {
			System.out
					.println("usage: CaesarCipher <option> <inputfile> <outputfile>");
			System.out.println("options:");
			System.out.println("\tencode: encoding the input file and output");
			System.out.println("\tdecode: decoding the input file and output");
			throw new Exception("Invalid arguments");
		}

		if (!new File(args[1]).isFile()) {
			System.out.println("Not a valid file");
			throw new Exception("Invalid arguments");
		}
		mode = args[0];
		inputFile = args[1];
		outputFile = args[2];
		count = 0;

		inStream = new TransactionalFileInputStream(inputFile);
		outStream = new TransactionalFileOutputStream(outputFile);

	}

	public String toString() {
		return "CaesarCipher: " + count + " Bytes have been processed.";
	}

	@Override
	public void run() {
		suspending = false;
		DataInputStream in = new DataInputStream(inStream);
		DataOutputStream out = new DataOutputStream(outStream);

		char current = '\0';
		char newchar = '\0';
		while (!suspending && !finished) {

			try {
				current = in.readChar();
			} catch (EOFException eof) {
				System.out.println("Finished processing");
				finished = true;
				break;
			} catch (IOException eio) {
				eio.printStackTrace();
			}

			if (mode.equals("encode")) {
				newchar = (char) (current + 3);
			} else {
				newchar = (char) (current - 3);
			}

			try {
				out.writeChar(newchar);
				count++;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		inStream.closeStream();
		inStream.setMigrated(true);
		outStream.closeStream();
		outStream.setMigrated(true);
		suspending = false;
	}

	@Override
	public void suspend() {
		suspending = true;
		while (suspending && !finished)
			;
	}

	@Override
	public boolean getFinished() {
		return this.finished;
	}

}
