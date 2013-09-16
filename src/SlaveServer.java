import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SlaveServer {

	private HashMap<Integer, ProcessInfo> processMap = new HashMap<Integer, ProcessInfo>();
	private int processID = 0;

	public SlaveServer() {

	}

	public void startService(String masterHostName)
			throws SlaveServiceException, UnknownHostException, IOException,
			ClassNotFoundException {
		System.out.println("Slave Service Started");

		/* get connection to master server */
		System.out.println(masterHostName);
		Socket socket = new Socket(InetAddress.getByName(masterHostName),
				ProcessManager.MASTER_PORT);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				socket.getOutputStream()));

		/* get ready for reading message from master server */
		String str = null;
		String[] args = null;
		while ((str = in.readLine()) != null) {
			args = str.split(" ");

			if (args[0].equals("start"))
				this.startNewProcess(args);

			/* suspend a process given process ID */
			else if (args[0].equals("suspend")) {

				/* check the process ID */
				int migrateProcessID = -1;
				try {
					migrateProcessID = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					System.err.println("wrong process ID format");
					continue;
				}

				MigratableProcess mpWrite = this.processMap
						.get(migrateProcessID).process;

				if (mpWrite == null) {
					System.err.println("wrong process ID");
					continue;
				}

				mpWrite.suspend();
				this.processMap.get(migrateProcessID).status = ProcessStatus.SUSPENDING;

				/* write the suspended process into a file */
				FileOutputStream outputFile = new FileOutputStream(args[1]
						+ args[2] + args[3] + ".obj");
				ObjectOutputStream outputObj = new ObjectOutputStream(
						outputFile);
				outputObj.writeObject(mpWrite);
				outputObj.flush();
				outputObj.close();
				outputFile.close();

				/* acknowledge back to master server */
				out.write("finish suspending\n");
				out.flush();

				/* remove the process from process list */
				this.processMap.remove(migrateProcessID);
			}

			/*
			 * resume a suspended process by reading from an *.obj file
			 * prevoisly dumped by another salve server
			 */
			else if (args[0].equals("resume")) {
				/* read the *.obj file */
				FileInputStream inputFile = new FileInputStream(args[1]
						+ args[2] + args[3] + ".obj");
				ObjectInputStream inputObj = new ObjectInputStream(inputFile);
				MigratableProcess mpRead = (MigratableProcess) inputObj
						.readObject();
				inputObj.close();
				inputFile.close();
				
				/* run the process */
				Thread newThread = new Thread(mpRead);
				newThread.start();

				/* add this newly started process to the process list */
				ProcessInfo processInfo = new ProcessInfo();
				processInfo.process = mpRead;
				processInfo.status = ProcessStatus.RUNNING;
				processID++;
				this.processMap.put(processID, processInfo);
			}

			/* iterate through the process list and send back to master server */
			else if (str.equals("processlist")) {
				for (Map.Entry<Integer, ProcessInfo> entry : processMap
						.entrySet()) {
					if (entry.getValue().process.getFinished())
						out.write("#"
								+ entry.getKey()
								+ "\t"
								+ entry.getValue().process.getClass()
										.getSimpleName() + " "
								+ ProcessStatus.TERMINATED + "\n");
					else
						out.write("#"
								+ entry.getKey()
								+ "\t"
								+ entry.getValue().process.getClass()
										.getSimpleName() + " "
								+ entry.getValue().status + "\n");
					out.flush();
				}

				out.write("process list finish\n");
				out.flush();
			}

		}
	}

	/**
	 * Instantiate a new object (running process)
	 * 
	 * @param args
	 */
	private void startNewProcess(String[] args) {
		MigratableProcess newProcess = null;
		try {
			Class<?> processClass = Class.forName("Classes." + args[2]);
			Constructor<?> processConstructor = processClass
					.getConstructor(String[].class);
			Object[] processArgs = { Arrays.copyOfRange(args, 3, args.length) };
			newProcess = (MigratableProcess) processConstructor
					.newInstance(processArgs);
		} catch (ClassNotFoundException e) {
			System.out.println("Could not find class " + args[2]);
			return;
		} catch (SecurityException e) {
			System.out.println("Security Exception getting constructor for "
					+ args[2]);
			return;
		} catch (NoSuchMethodException e) {
			System.out.println("Could not find proper constructor for "
					+ args[2]);
			return;
		} catch (IllegalArgumentException e) {
			System.out.println("Illegal arguments for " + args[2]);
			return;
		} catch (InstantiationException e) {
			System.out.println("Instantiation Exception for " + args[2]);
			return;
		} catch (IllegalAccessException e) {
			System.out.println("IIlegal access exception for " + args[2]);
			return;
		} catch (InvocationTargetException e) {
			System.out.println("Invocation target exception for " + args[2]);
			return;
		} catch (Exception e) {
			System.err.println(e.toString());
		}

		Thread newThread = new Thread(newProcess);
		newThread.start();

		/* add this newly started process to the process list */
		ProcessInfo processInfo = new ProcessInfo();
		processInfo.process = newProcess;
		processInfo.status = ProcessStatus.RUNNING;
		processID++;
		this.processMap.put(processID, processInfo);
	}

}
