package SocketChatRoom;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerChat {
	
	
	private static Set<String> userNames = new HashSet<>();
	
	private static Set<PrintWriter> writers = new HashSet<>();

	public static Random rand = new Random();
	
	public static void main(String [] args) throws Exception
	{
		
		System.out.println("The chat server is running..");
		ExecutorService pool = Executors.newFixedThreadPool(500);
		try(ServerSocket listener =  new ServerSocket(5000))
		{
			while(true)
			{
				pool.execute(new Handler(listener.accept()));
			}
		}
		
		
	}
	
	private static class Handler implements Runnable
	{
		private String name;
		private Socket socket;
		private Scanner in;
		private PrintWriter out;
		
		
		public Handler(Socket socket)
		{
			this.socket = socket;
		}
		
		
		@Override
		public void run() {
			try {
				in = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream(),true);
			
				while(true)
				{
					out.println("EnterName");
					name = in.nextLine();
				
					if(name==null)
					{
						return;
					}
					
					synchronized(userNames)
					{
						if(!name.isBlank() && !userNames.contains(name))
						{
							userNames.add(name);
							break;
						}
					}	
				}
				out.println("NameAvaliable "+name);
				for(PrintWriter writer: writers)
				{
					writer.println("Message "+name+ " has joined");
				}
				writers.add(out);
				
				
				while(true)
				{
					String input = in.nextLine();
					if(input.toLowerCase().startsWith("/quit"))
					{
						return;
					}
					for(PrintWriter writer: writers)
					{
						writer.println("Message "+ name +": "+input);
					}
				}
			}catch(Exception ex) {
				System.out.println(ex);
			}
			finally {
				if(out!=null)
				{
					writers.remove(out);
				}
				if(name!=null)
				{
					userNames.remove("name");
					for(PrintWriter writer: writers)
						writer.println("Message "+name+" has left the chat room");
				}
			}
			try {
				socket.close();
			}catch(IOException ex)
			{
				System.out.println(ex);
			}			
		}
		
	}

}
