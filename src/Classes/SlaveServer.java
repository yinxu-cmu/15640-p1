package Classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import Exceptions.SlaveServiceException;

public class SlaveServer {
	
	public SlaveServer() {
		
	}
	
	public void startService() throws SlaveServiceException, UnknownHostException, IOException {
		System.out.println("Slave Service Started");
		Socket socket = new Socket(InetAddress.getByName("bambooshark.ics.cs.cmu.edu"), ProcessManager.MASTER_PORT);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		String str = null;
		String[] args = null;
		while ((str = in.readLine()) != null) {
			args = str.split(" ");
			MigratableProcess newProcess;
			try {
				Class<?> processClass = Class.forName("Classes." + args[0]);
				Constructor<?> processConstructor = processClass.getConstructor(String[].class);
			    Object[] processArgs = { Arrays.copyOfRange(args, 1, args.length) };
				newProcess = (MigratableProcess) processConstructor.newInstance(processArgs);
	        } catch (ClassNotFoundException e) {
				System.out.println("Could not find class " + args[0]);
				continue;
			} catch (SecurityException e) {
				System.out.println("Security Exception getting constructor for " + args[0]);
				continue;
			} catch (NoSuchMethodException e) {
				System.out.println("Could not find proper constructor for " + args[0]);
				continue;
			} catch (IllegalArgumentException e) {
				System.out.println("Illegal arguments for " + args[0]);
				continue;
			} catch (InstantiationException e) {
				System.out.println("Instantiation Exception for " + args[0]);
				continue;
			} catch (IllegalAccessException e) {
				System.out.println("IIlegal access exception for " + args[0]);
				continue;
			} catch (InvocationTargetException e) {
				System.out.println("Invocation target exception for " + args[0]);
				continue;
			}
			newProcess.run();
		}
	}

}
