package com.zero1.realjavascript;

import java.util.List;

public class RJSBridge {

	public static List<RJSResult> interpret(Object from, String... command) {
		if (command == null || (command.length == 1 && (command[0] == null || command[0].trim().equals(""))))
			return null;
		try {
			List<RJSResult> results = RJSParser.parse(from, command);
			RJSDataPool.clear();
			return results;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
