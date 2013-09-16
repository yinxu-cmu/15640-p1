
import java.io.IOException;

public class ProcessManager {
	
	public static final int MASTER_PORT = 62742;
	public static final int SLAVE_PORT = 62743;

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		/* start master server */
		if (args.length == 0) {
			MasterServer ms = new MasterServer();
			try {
				ms.startService();
			} catch (MasterServiceException e) {
				System.err.println("Master Serivce Ended with Exception");
			}
			System.out.println("Master Serivce Ended");
			System.exit(0);
		} 
		
		/* start slave server */
		else if (args.length == 2 && args[0].equals("-c")) {
			SlaveServer ss = new SlaveServer();
			try {
				ss.startService(args[1]);
			} catch (SlaveServiceException e) {
				System.err.println("Slave Serivce Ended with Exception");
			}
			System.out.println("Slave Serivce Ended");
		}
		
		else {
			System.out.println("Usage: java ProcessManager [-c <master hostname or ip>]");
		}
	}

}
