package Classes;

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

import Exceptions.SlaveServiceException;

public class SlaveServer {
	
	private HashMap<Integer, MigratableProcess> processMap = new HashMap<Integer, MigratableProcess>();
	
	public SlaveServer() {
		
	}
	
	public void startService() throws SlaveServiceException, UnknownHostException, IOException, ClassNotFoundException {
		System.out.println("Slave Service Started");
		
		// get connection to master server
		Socket socket = new Socket(InetAddress.getByName("bambooshark.ics.cs.cmu.edu"), ProcessManager.MASTER_PORT);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		// setup of reading message from master server
		String str = null;
		String[] args = null;
		while ((str = in.readLine()) != null) {
//			System.out.println(str);
			args = str.split(" ");
			
			if (args[0].equals("start"))
				this.startNewProcess(args);
			else if (args[0].equals("suspend")) {
				MigratableProcess mpWrite = this.processMap.get(0);
				mpWrite.suspend();
				FileOutputStream outputFile = new FileOutputStream("TestMigratableProcess.obj");
				ObjectOutputStream outputObj = new ObjectOutputStream(outputFile);
				outputObj.writeObject(mpWrite);
				outputObj.flush();
				outputObj.close();
				outputFile.close();
			} 
			
			else if (args[0].equals("resume")) {
				FileInputStream inputFile = new FileInputStream("TestMigratableProcess.obj");
				ObjectInputStream inputObj = new ObjectInputStream(inputFile);
				MigratableProcess mpRead = (MigratableProcess) inputObj.readObject();
				inputObj.close();
				inputFile.close();
				mpRead.run();
			}
			
		}
	}
	
	/**
	 * Instantiate a new object (running process)
	 * @param args
	 */
	private void startNewProcess(String[] args) {
		MigratableProcess newProcess;
		try {
			Class<?> processClass = Class.forName("Classes." + args[2]);
			Constructor<?> processConstructor = processClass.getConstructor(String[].class);
		    Object[] processArgs = { Arrays.copyOfRange(args, 3, args.length) };
			newProcess = (MigratableProcess) processConstructor.newInstance(processArgs);
        } catch (ClassNotFoundException e) {
			System.out.println("Could not find class " + args[2]);
			return;
		} catch (SecurityException e) {
			System.out.println("Security Exception getting constructor for " + args[2]);
			return;
		} catch (NoSuchMethodException e) {
			System.out.println("Could not find proper constructor for " + args[2]);
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
		}
		Thread newThread = new Thread(newProcess);
		newThread.start();
		
		// hard coding
		this.processMap.put(0, newProcess);
	}
	

}
