package com.zero1.realjavascript;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

class RJSParser {

	public static List<RJSResult> parse(Object from, String... commandStrings)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		List<RJSResult> results = null;
		for (String commandString : commandStrings) {
			RJSCommand command = new RJSCommand(commandString);
			if (results == null)
				results = parse(from, command);
			else
				results.addAll(parse(from, commandString));
		}
		return results;
	}

	private static List<RJSResult> parse(Object from, RJSCommand command)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		while (commandHasFutureCommands(command)) {
			int openCommand = command.getCommand().lastIndexOf(RJSKeyword.FUTURE_COMMAND_OPEN_BRACE.toChar());
			int closeCommand = command.getCommand().indexOf(RJSKeyword.FUTURE_COMMAND_CLOSE_BRACE.toChar(),
					openCommand + 1);
			if (openCommand == -1 || closeCommand == -1)
				break;
			String commandString = command.getCommand().substring(openCommand + 1, closeCommand);
			RJSResult result = new RJSResult(commandString, RJSResult.ResultType.COMMAND);
			command = new RJSCommand(command.getCommand().replace(RJSKeyword.FUTURE_COMMAND_OPEN_BRACE.toChar()
					+ commandString + RJSKeyword.FUTURE_COMMAND_CLOSE_BRACE.toChar(), result.getParameterString()));
		}
		while (commandHasStrings(command)) {
			int openQuote = command.getCommand().indexOf(RJSKeyword.STRING_ENCLOSURE.toChar());
			int closeQuote = command.getCommand().indexOf(RJSKeyword.STRING_ENCLOSURE.toChar(), openQuote + 1);
			if (openQuote == -1 || closeQuote == -1)
				break;
			String string = command.getCommand().substring(openQuote, closeQuote + 1);
			RJSResult result = new RJSResult(string);
			command = new RJSCommand(command.getCommand().replace(string, result.getParameterString()));
		}
		List<RJSResult> results = new ArrayList<>();
		if (isSingleCommand(command))
			results.add(breakDownCommand(from, command));
		else {
			List<RJSCommand> commands = splitCommands(command);
			for (RJSCommand commandToExecute : commands)
				results.add(breakDownCommand(from, commandToExecute));
		}
		return results;
	}

	private static RJSResult breakDownCommand(Object from, RJSCommand command)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		while (isCommandValid(command)) {
			boolean negate = false, not = false;
			int openBracket = command.getCommand().lastIndexOf(RJSKeyword.INNER_COMMAND_OPEN.toString())
					+ RJSKeyword.INNER_COMMAND_OPEN.length();
			int closeBracket = command.getCommand().indexOf(RJSKeyword.INNER_COMMAND_CLOSE.toString(), openBracket);
			if (openBracket == -1 || closeBracket == -1)
				break;
			int indexOfNegativeSign = command.getCommand().lastIndexOf(RJSKeyword.SUBTRACT.toString());
			int indexOfNotSign = command.getCommand().lastIndexOf(RJSKeyword.LOGICAL_NOT.toString());
			if (Character.isDigit(command.getCommand().charAt(indexOfNegativeSign + RJSKeyword.SUBTRACT.length())))
				indexOfNegativeSign = command.getCommand().substring(0, indexOfNegativeSign)
						.lastIndexOf(RJSKeyword.SUBTRACT.toString());
			if (indexOfNegativeSign != -1 && indexOfNegativeSign < openBracket) {
				String negateCheckString = command.getCommand().substring(
						indexOfNegativeSign + RJSKeyword.SUBTRACT.length(), openBracket - RJSKeyword.SUBTRACT.length());
				if (negateCheckString.trim().length() == 0)
					negate = true;
			}
			if (indexOfNotSign != -1 && indexOfNotSign < openBracket) {
				String notCheckString = command.getCommand().substring(indexOfNotSign + RJSKeyword.LOGICAL_NOT.length(),
						openBracket - RJSKeyword.LOGICAL_NOT.length());
				if (notCheckString.trim().length() == 0)
					not = true;
			}
			RJSCommand subCommand;
			String subCommandString = command.getCommand().substring(openBracket, closeBracket);
			String unalteredSubCommandString = subCommandString;
			int indexOfWithData = subCommandString
					.indexOf(RJSKeyword.WITH_DATA.toString() + RJSKeyword.DATA_POOL_DATA_OPEN_BRACE.toString());
			while (indexOfWithData != -1) {
				int indexOfDataAccessEnd = subCommandString.indexOf(RJSKeyword.DATA_POOL_CLOSE_BRACE.toString(),
						indexOfWithData) + RJSKeyword.DATA_POOL_CLOSE_BRACE.length() + RJSKeyword.SPACE.length();
				int indexOfAppend = subCommandString.indexOf(RJSKeyword.RESULT_APPEND.toString());
				boolean appendAfter = indexOfAppend >= indexOfDataAccessEnd;
				if (indexOfAppend == -1 || (appendAfter
						? !subCommandString.substring(indexOfDataAccessEnd, indexOfAppend).trim().isEmpty()
						: !subCommandString
								.substring(indexOfAppend + RJSKeyword.RESULT_APPEND.length(), indexOfWithData).trim()
								.isEmpty()))
					break;
				String value = RJSDataPool
						.get(appendAfter ? subCommandString.substring(indexOfWithData, indexOfDataAccessEnd)
								: subCommandString.substring(indexOfAppend + RJSKeyword.RESULT_APPEND.length(),
										indexOfDataAccessEnd))
						.toString().trim();
				if (value.contains(RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString())) {
					List<RJSResult> objectResults = RJSParser.parse(from, RJSDataPool.get(value).toString());
					if (objectResults != null && !objectResults.isEmpty()) {
						RJSResult objectResult = objectResults.get(0);
						if (objectResult.getResult() != null)
							value = objectResult.getResult().toString();
					}
				}
				if (appendAfter)
					subCommandString = new StringBuilder(subCommandString)
							.replace(indexOfWithData, indexOfAppend + RJSKeyword.RESULT_APPEND.length(), value)
							.toString();
				else
					subCommandString = new StringBuilder(subCommandString)
							.replace(indexOfAppend, indexOfDataAccessEnd, value).toString();
				indexOfWithData = subCommandString
						.indexOf(RJSKeyword.WITH_DATA.toString() + RJSKeyword.DATA_POOL_DATA_OPEN_BRACE.toString());
			}
			Class<?> castToClass = null;
			if (Pattern.compile(Pattern.quote(RJSKeyword.AS.toString()), Pattern.CASE_INSENSITIVE)
					.matcher(subCommandString).find()) {
				int indexOfAs = subCommandString.toUpperCase().indexOf(RJSKeyword.AS.toString());
				String className = subCommandString.substring(indexOfAs + +RJSKeyword.AS.length()).trim();
				castToClass = Class.forName(className);
				subCommand = new RJSCommand(new StringBuilder(subCommandString)
						.replace(indexOfAs, subCommandString.length(), "").toString());
			} else
				subCommand = new RJSCommand(subCommandString);

			RJSResult result = subCommand.execute(from);
			if (not) {
				result = new RJSResult(!(Boolean) result.getResult());
				command = new RJSCommand(
						new StringBuilder(command.getCommand()).deleteCharAt(indexOfNotSign).toString());
			} else if (negate) {
				result = new RJSResult(new RJSNumber((Number) result.getResult()).multiply(-1));
				command = new RJSCommand(
						new StringBuilder(command.getCommand()).deleteCharAt(indexOfNegativeSign).toString());
			}
			if (castToClass != null && result != null) {
				int resultIndex = result.getIndex();
				RJSDataPool.remove(resultIndex);
				result = new RJSResult(result.getResult(), castToClass);
				RJSDataPool.addAt(result.getResult(), resultIndex);
			}
			String commandString = command.getCommand().replace(RJSKeyword.INNER_COMMAND_OPEN.toString()
					+ unalteredSubCommandString + RJSKeyword.INNER_COMMAND_CLOSE.toString(),
					result.getParameterString());
			command = new RJSCommand(commandString);
		}
		RJSResult result = command.execute(from);
		return result;
	}

	private static boolean isCommandValid(RJSCommand command) {
		String commandString = command.getCommand();
		int length = commandString.length();
		return ((length - commandString.replace(RJSKeyword.INNER_COMMAND_OPEN.toString(), "").length()) == (length
				- commandString.replace(RJSKeyword.INNER_COMMAND_CLOSE.toString(), "").length()));
	}

	private static boolean commandHasStrings(RJSCommand command) {
		String commandString = command.getCommand();
		int length = commandString.length();
		return (length - commandString.replace(RJSKeyword.STRING_ENCLOSURE.toString(), "").length()) % 2 == 0;
	}

	private static boolean commandHasFutureCommands(RJSCommand command) {
		String commandString = command.getCommand();
		int length = commandString.length();
		return ((length
				- commandString.replace(RJSKeyword.FUTURE_COMMAND_OPEN_BRACE.toString(), "").length()) == (length
						- commandString.replace(RJSKeyword.FUTURE_COMMAND_CLOSE_BRACE.toString(), "").length()));
	}

	private static boolean isSingleCommand(RJSCommand command) {
		return !Pattern.compile(Pattern.quote(RJSKeyword.AND.toString()), Pattern.CASE_INSENSITIVE)
				.matcher(command.getCommand()).find();
	}

	private static List<RJSCommand> splitCommands(RJSCommand command)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		String commandString = command.getCommand();
		List<RJSCommand> commands = new ArrayList<RJSCommand>();
		String[] commandsArray = Pattern.compile(Pattern.quote(RJSKeyword.AND.toString()), Pattern.CASE_INSENSITIVE)
				.split(commandString);
		for (String subCommand : commandsArray)
			commands.add(new RJSCommand(subCommand.trim()));
		return commands;
	}

}
