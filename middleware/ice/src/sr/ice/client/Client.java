package sr.ice.client;
// **********************************************************************
//
// Copyright (c) 2003-2019 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

import Demo.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.LocalException;
import sr.ice.server.Cart;

public class Client 
{
	static java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
	static List<String> cart =  Arrays.asList("cart","rcart", "mcart");
	static List<String> temp =  Arrays.asList("heat","cool", "spec");
	static List<String> voice = Arrays.asList("cap","low", "silly");

	public static String getCategory() throws IOException {
		while(true){
			System.out.println(">>");
			String read = in.readLine();

			if(cart.contains(read) || temp.contains(read) || voice.contains(read) || read.equals("names") || read.equals("exit")){
				return read;
			}else{
				System.out.println(">> Wrong category: " + cart.toString() + " " + temp.toString() + " " + voice.toString() + " names");
			}
		}
	}

	public static void cartOperations(ObjectPrx base) throws IOException {
		IMovingPrx obj2 = IMovingPrx.checkedCast(base);
		if (obj2 == null){
			System.out.println("Wrong casting");
			return;
		}
		boolean isRunning = true;
		String line;

		while(isRunning){
			System.out.println("CART>>");
			line = in.readLine();
			switch (line){
				case "move":{
					long x = Long.parseLong(in.readLine());
					long y = Long.parseLong(in.readLine());

					Position result = obj2.move(new Position(x, y));
					System.out.println("x: " + result.x + " y: " + result.y);
					break;
				}
				case "pos":{
					Position result = obj2.getPosition();
					System.out.println("x: " + result.x + " y: " + result.y);
					break;
				}
				case "exit":{
					isRunning=false;
					break;
				}
				default: {
					System.out.println("commands: move exit");
				}
			}
		}
	}

	public static void tempOperations(ObjectPrx base) throws IOException {
		IRadiatorPrx obj2 = IRadiatorPrx.checkedCast(base);
		if (obj2 == null){
			System.out.println("Wrong casting");
			return;
		}

		String line;
		boolean isRunning = true;

		while(isRunning){
			System.out.println("TEMP >>");
			line = in.readLine();

			switch (line) {
				case "change":{
					long x = Long.parseLong(in.readLine());
					System.out.println("result: " + obj2.adjustTemp(x));
					break;
				}
				case "exit":{
					isRunning = false;
					break;
				}
				default: {
					System.out.println("commands: change exit");
				}
			}

		}
	}

	public static void voiceOperations(ObjectPrx base) throws IOException {
		IVoiceControlPrx obj2 = IVoiceControlPrx.checkedCast(base);
		if (obj2 == null){
			System.out.println("Wrong casting");
			return;
		}

		boolean isRunning = true;
		String line;

		while (isRunning){
			System.out.println("VOICE >>");
			line = in.readLine();
			switch (line) {
				case "process":{
					line = in.readLine();
					List<String> tokens = Arrays.asList(line.split("\\s+"));
					System.out.println(obj2.process(tokens));
					break;
				}
				case "exit":{
					isRunning = false;
					break;
				}
				default: {
					System.out.println("commands: process exit");
				}
			}

		}

	}



	public static void main(String[] args) 
	{
		int status = 0;
		Communicator communicator = null;
		String line = null;
		ObjectPrx base = null;

		try {
			communicator = Util.initialize(args);

			boolean isRunning = true;
			while(isRunning){
				String cat = getCategory();
				if(cat.equals("exit")){
					isRunning = false;
				}else if(!cat.equals("names")){
					System.out.println(">> insert object name");
					String name = in.readLine();
					base = communicator.stringToProxy(cat+"/"+ name+":tcp -h localhost -p 10000");
				}else{
					base = communicator.stringToProxy("names/all:tcp -h localhost -p 10000");
				}

				if(cart.contains(cat)){
					cartOperations(base);
				}else if(voice.contains(cat)){
					voiceOperations(base);
				}else if(temp.contains(cat)){
					tempOperations(base);
				}else if(cat.equals("names")){
					INamesPrx names = INamesPrx.checkedCast(base);
					System.out.println(names.getNames());
				}
			}
		} catch (LocalException e) {
			e.printStackTrace();
			status = 1;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			status = 1;
		}
		if (communicator != null) {

			try {
				communicator.destroy();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				status = 1;
			}
		}
		System.exit(status);
	}

}