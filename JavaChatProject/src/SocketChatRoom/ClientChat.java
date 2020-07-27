package SocketChatRoom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientChat {

	private String url;
	private Scanner in;
	private PrintWriter out;
	private String userName;
	JFrame frame = new JFrame("Chat room");
	JTextField txtField = new JTextField(50);
	JTextArea messageArea = new JTextArea(16,50);

	float r = ServerChat.rand.nextFloat();
	float g = ServerChat.rand.nextFloat();
	float b = ServerChat.rand.nextFloat();
	Color color = new Color(r,g,b);
	
	
	public ClientChat(String url)
	{
		this.url = url;
		txtField.setEditable(false);
		messageArea.setEditable(false);

		frame.getContentPane().add(txtField, BorderLayout.SOUTH);
		frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
		frame.pack();

		txtField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = txtField.getText();
				if(!msg.contentEquals("")) {
					out.println(txtField.getText());
					txtField.setText("");
				}
			}
		});
	}

	
	public static void main(String[] args)throws Exception
	{
		ClientChat client = new ClientChat("127.0.0.1");
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.run();
		
	}
	
	
	
	private String getName()
	{
		userName= JOptionPane.showInputDialog(frame,"Choose a Username: ","Username Selection ", JOptionPane.PLAIN_MESSAGE);
		return userName;
	}
	
	private void run() throws IOException
	{
		try (Socket sock = new Socket(url,5000)){
			in = new Scanner(sock.getInputStream());
			out = new PrintWriter(sock.getOutputStream(),true);
			messageArea.setForeground(color);
			
			while(in.hasNextLine())
			{
				String line = in.nextLine();
				if(line.startsWith("EnterName"))
				{
					out.println(getName());
				}
				else if(line.startsWith("NameAvaliable"))
				{
					this.frame.setTitle("Chat Room - "+line.substring(14));
					txtField.setEditable(true);
				}else if(line.startsWith("Message")) {
					messageArea.append(line.substring(8)+" \n");
					
				}
			}
			
		}finally {
			frame.setVisible(false);
			frame.dispose();
		}
	}


}
