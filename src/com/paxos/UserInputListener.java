package com.paxos;
import java.util.Scanner;

import com.paxos.common.Process;
public class UserInputListener extends Process {

	private Main main;
	public  UserInputListener(Main main, String myProcessId) {
		// TODO Auto-generated method stub
		this.main = main;
		this.processId = myProcessId;
		
	}
	
	public void body() {
		Scanner reader = new Scanner(System.in);
		while(true) {
			String input = reader.nextLine();
			if(input != null) {
			 System.err.println("Killing process:"+input);
			 main.processes.get(input).alive = false;
			}
		}
	}

}
