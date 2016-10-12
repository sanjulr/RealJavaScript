package com.zero1.realjavascript;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.zero1.realjavascript.RJSParameter.ParameterType;

class RJSCommand {

	private int priority;
	private String command, upperCaseCommand, trimmedCommand;

	public RJSCommand(String command) {
		this.command = RJSKeyword.SPACE.toString() + command + RJSKeyword.SPACE.toString();
		this.upperCaseCommand = this.command.toUpperCase();
		this.trimmedCommand = command.trim();
	}

	public String getCommand() {
		return command;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public RJSResult execute(Object from)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		if (isCallCommand())
			return executeCallCommand(from);
		else if (isComputeCommand())
			return executeComputeCommand(from);
		else if (isCreateCommand())
			return executeCreateCommand(from);
		else if (isAssignCommand())
			return executeAssignCommand(from);
		else if (isIfThenCommand())
			return executeIfThenCommand(from);
		else if (isClassCommand())
			return executeClassCommand(from);
		else if (isLoopCommand())
			return executeLoopCommand(from);
		else if (RJSNumber.isNumber(trimmedCommand))
			return new RJSResult(new RJSNumber(trimmedCommand));
		else if (RJSParameter.isBooleanAccess(trimmedCommand))
			return new RJSResult(new RJSParameter(trimmedCommand).getParameter());
		else if (RJSParameter.isObjectAccess(trimmedCommand))
			return new RJSResult(new RJSParameter(trimmedCommand).getParameter());
		else if (RJSParameter.isStringAccess(command)) {
			return new RJSResult(RJSDataPool.get(command));
		} else if (isIsCommand())
			return executeIsCommand(from);
		else if (isVarCommand())
			return executeVarCommand(from);
		else
			return executeAccessVariable(from);
	}

	private boolean isCallCommand() {
		return Pattern.compile(Pattern.quote(RJSKeyword.CALL.toString()), Pattern.CASE_INSENSITIVE).matcher(command)
				.find();
	}

	private boolean isComputeCommand() {
		return Pattern.compile(Pattern.quote(RJSKeyword.COMPUTE.toString()), Pattern.CASE_INSENSITIVE).matcher(command)
				.find();
	}

	private boolean isCreateCommand() {
		return Pattern.compile(Pattern.quote(RJSKeyword.CREATE.toString()), Pattern.CASE_INSENSITIVE).matcher(command)
				.find();
	}

	private boolean isResultSetToCommand() {
		return Pattern.compile(Pattern.quote(RJSKeyword.TO.toString()), Pattern.CASE_INSENSITIVE).matcher(command)
				.find();
	}

	private boolean isAssignCommand() {
		return Pattern.compile(Pattern.quote(RJSKeyword.ASSIGN.toString()), Pattern.CASE_INSENSITIVE).matcher(command)
				.find();
	}

	private boolean isIfThenCommand() {
		return Pattern.compile(Pattern.quote(RJSKeyword.IF.toString()), Pattern.CASE_INSENSITIVE).matcher(command)
				.find();
	}

	private boolean isClassCommand() {
		return Pattern.compile(Pattern.quote(RJSKeyword.CLASS.toString()), Pattern.CASE_INSENSITIVE).matcher(command)
				.find();
	}

	private boolean isLoopCommand() {
		return Pattern.compile(Pattern.quote(RJSKeyword.LOOP.toString()), Pattern.CASE_INSENSITIVE).matcher(command)
				.find();
	}

	private boolean isIsCommand() {
		return Pattern.compile(Pattern.quote(RJSKeyword.IS.toString()), Pattern.CASE_INSENSITIVE).matcher(command)
				.find();
	}

	private boolean isVarCommand() {
		return Pattern.compile(Pattern.quote(RJSKeyword.VAR.toString()), Pattern.CASE_INSENSITIVE).matcher(command)
				.find();
	}

	private RJSResult executeCallCommand(Object from)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		Object callFrom = from;
		Object parametersFrom = from;
		String trimmedCommand = this.trimmedCommand.trim();
		String upperCaseCommand = trimmedCommand.toUpperCase();
		int indexOfMethodName = upperCaseCommand.indexOf(RJSKeyword.CALL.toString()) + RJSKeyword.CALL.length();
		int indexOfTo = -1;
		if (isResultSetToCommand())
			indexOfTo = upperCaseCommand.indexOf(RJSKeyword.TO.toString());
		int indexOfSpace = trimmedCommand.trim().indexOf(RJSKeyword.SPACE.toString(), indexOfMethodName);
		String methodName = trimmedCommand
				.substring(indexOfMethodName, indexOfSpace == -1 ? trimmedCommand.length() : indexOfSpace).trim();
		if (methodName.trim().isEmpty()) {
			indexOfSpace = trimmedCommand.indexOf(RJSKeyword.SPACE.toString() + RJSKeyword.DOT.toString(),
					indexOfMethodName);
			methodName = new StringBuilder(trimmedCommand).deleteCharAt(indexOfSpace).toString()
					.substring(trimmedCommand.indexOf(RJSKeyword.CALL.toString()) + RJSKeyword.CALL.length());
		}
		if (methodName.contains(RJSKeyword.DOT.toString())) {
			String[] methodParts = Pattern.compile(Pattern.quote(RJSKeyword.DOT.toString())).split(methodName);
			String object = methodName.replace(RJSKeyword.DOT.toString() + methodParts[methodParts.length - 1], "");
			methodName = methodParts[methodParts.length - 1];
			Object storedObject = null;
			String trimmedObject = object.trim();
			if (object.equals(RJSKeyword.THIS.toString()))
				storedObject = from;
			else if (trimmedObject.startsWith(RJSKeyword.WITH_DATA.toString().trim()))
				storedObject = RJSDataPool.get(trimmedObject);
			else if (trimmedObject.charAt(0) == RJSKeyword.OBJECT_ACCESS.toChar()) {
				String objectName = trimmedObject.substring(1);
				storedObject = RJSDataPool.getByName(objectName);
				if (storedObject == null && RJSDataPool.hasMethod(objectName))
					storedObject = new RJSCommand(RJSKeyword.CALL.toString() + trimmedObject).execute(from).getResult();
			}
			if (storedObject != null) {
				if (storedObject.toString().contains(RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString())) {
					List<RJSResult> objectResults = RJSParser.parse(from,
							RJSDataPool.get(storedObject.toString()).toString());
					if (objectResults != null && !objectResults.isEmpty()) {
						RJSResult objectResult = objectResults.get(0);
						if (objectResult.getResult() != null)
							storedObject = objectResult.getResult();
					}
				}
				callFrom = storedObject;
			} else {
				RJSResult callFromResult = new RJSCommand(object).execute(from);
				if (callFromResult != null && callFromResult.getResult() != null) {
					if (callFromResult.getObjectType().equals(String.class)
							&& object.equals(callFromResult.getResult()))
						try {
							callFrom = Class.forName(object);
						} catch (ClassNotFoundException e) {
							callFrom = callFromResult.getResult();
						}
					else
						callFrom = callFromResult.getResult();
				} else {
					try {
						callFrom = Class.forName(object);
					} catch (ClassNotFoundException e) {
					}
				}
			}
		}
		String parametersString;
		if (indexOfTo == -1)
			parametersString = command.substring(command.indexOf(methodName) + methodName.length());
		else
			parametersString = command.substring(command.indexOf(methodName) + methodName.length(), indexOfTo);
		List<RJSParameter> parameters = getParametersFromParametersString(callFrom, parametersString,
				RJSKeyword.COMMA.toString());
		parameters = getActualParameters(parametersFrom, parameters);
		List<Class<?>> parameterTypes = RJSParameter.getParameterTypes(parameters);
		if (methodName.charAt(0) == RJSKeyword.OBJECT_ACCESS.toChar()) {
			methodName = methodName.substring(RJSKeyword.OBJECT_ACCESS.length());
			Object[] parameterObjects = new Object[parameters.size()];
			for (int i = 0; i < parameters.size(); i++)
				parameterObjects[i] = parameters.get(i).getParameter();
			int methodId = RJSDataPool.getMethodId(methodName, parameterObjects);
			if (methodId != -1) {
				String method = RJSDataPool.getMethod(methodId);
				if (method.contains(RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString()))
					method = new RJSParameter(method).getParameter().toString();
				if (!parameterTypes.isEmpty())
					RJSDataPool.setMethodArguments(methodId, parameterObjects);
				method = method.replace(RJSKeyword.METHOD_ARGUMENT_ACCESS.toString(),
						RJSKeyword.METHOD_ARGUMENT_ACCESS.toString() + methodId);
				List<RJSResult> results = RJSParser.parse(callFrom, method);
				RJSDataPool.clearMethodArguments(methodName);
				return results.get(results.size() - 1);
			}
		} else if (callFrom instanceof RJSClass) {
			RJSClass rjsClass = (RJSClass) callFrom;
			Object[] parameterObjects = new Object[parameters.size()];
			for (int i = 0; i < parameters.size(); i++)
				parameterObjects[i] = parameters.get(i).getParameter();
			int methodId = rjsClass.getMethodId(methodName, parameterObjects);
			String method = rjsClass.getMethod(methodId);
			if (method == null || method.isEmpty()) {
				String methodString = rjsClass.getField(methodName).toString();
				if (methodString.contains(RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString()))
					method = new RJSParameter(methodString).getParameter().toString();
			}
			if (!parameterTypes.isEmpty())
				rjsClass.setMethodArguments(methodName, parameterObjects);
			method = method.replace(RJSKeyword.METHOD_ARGUMENT_ACCESS.toString(),
					RJSKeyword.METHOD_ARGUMENT_ACCESS.toString() + methodId);
			List<RJSResult> results = RJSParser.parse(callFrom, method);
			rjsClass.clearMethodArguments(methodName);
			return results.get(results.size() - 1);
		} else {
			Method methodToCall = getMethod(callFrom instanceof Class<?> ? (Class<?>) callFrom : callFrom.getClass(),
					methodName, parameterTypes);
			if (methodToCall != null) {
				methodToCall.setAccessible(true);
				Object result = methodToCall.invoke(callFrom, RJSParameter.getParameters(parameters).toArray());
				methodToCall.setAccessible(false);
				if (isResultSetToCommand())
					assignParameter(from, result);
				return new RJSResult(result);
			}
		}
		return null;
	}

	private RJSResult executeComputeCommand(Object from)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		int indexOfParameters = upperCaseCommand.indexOf(RJSKeyword.COMPUTE.toString()) + RJSKeyword.COMPUTE.length();
		int indexOfTo = -1;
		if (isResultSetToCommand())
			indexOfTo = upperCaseCommand.indexOf(RJSKeyword.TO.toString());
		String parametersString;
		if (indexOfTo == -1)
			parametersString = command.substring(indexOfParameters);
		else
			parametersString = command.substring(indexOfParameters, indexOfTo);
		if (!needsComputation(command)) {
			String formula = new RJSParameter(parametersString).getParameter().toString();
			if (needsBodmas(formula)) {
				RJSResult result = new RJSResult(applyBodmas(from, formula));
				if (indexOfTo != -1)
					assignParameter(from, result.getResult());
				return result;
			}
		}
		Object[] computationMethodArguments = getComputationMethod(parametersString);
		if (computationMethodArguments[0] != null && (int) computationMethodArguments[1] != -1) {
			List<RJSParameter> parameters = getBinaryParametersFromParametersString(parametersString,
					(int) computationMethodArguments[1], ((RJSKeyword) computationMethodArguments[0]).length());
			parameters = getActualParameters(from, parameters);
			RJSNumber number1 = new RJSNumber((Number) parameters.get(0).getParameter());
			RJSNumber number2 = new RJSNumber((Number) parameters.get(1).getParameter());
			RJSResult result = new RJSResult(number1.compute(number2, (RJSKeyword) computationMethodArguments[0]));
			if (indexOfTo != -1)
				assignParameter(from, result.getResult());
			return result;
		} else
			return null;
	}

	private RJSResult executeAccessVariable(Object from) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchFieldException, SecurityException, ClassNotFoundException,
			InstantiationException, NoSuchMethodException, ArrayIndexOutOfBoundsException {
		Class<?> castToClass = null;
		if (Pattern.compile(Pattern.quote(RJSKeyword.AS.toString()), Pattern.CASE_INSENSITIVE).matcher(trimmedCommand)
				.find()) {
			int indexOfAs = trimmedCommand.toUpperCase().indexOf(RJSKeyword.AS.toString());
			String className = trimmedCommand.substring(indexOfAs + +RJSKeyword.AS.length()).trim();
			castToClass = Class.forName(className);
			trimmedCommand = new StringBuilder(trimmedCommand).replace(indexOfAs, trimmedCommand.length(), "")
					.toString();
		}
		List<RJSResult> results = new ArrayList<>();
		Field[] fields;
		boolean not = false;
		boolean isArrayAccess = RJSParameter.isArrayAccess(trimmedCommand);
		if (not = (trimmedCommand.charAt(0) == RJSKeyword.LOGICAL_NOT.toChar()))
			trimmedCommand = trimmedCommand.substring(1);
		if (trimmedCommand.charAt(0) == RJSKeyword.OBJECT_ACCESS.toChar()) {
			String[] objectWithArray = trimmedCommand.substring(1).split(Pattern.quote(RJSKeyword.DOT.toString()));
			from = RJSDataPool.getByName(objectWithArray[0]);
			trimmedCommand = objectWithArray[1];
		}
		if (from instanceof RJSClass) {
			RJSClass rjsClass = (RJSClass) from;
			Object resultObject = null;
			for (String variable : Pattern.compile(Pattern.quote(RJSKeyword.COMMA.toString())).split(trimmedCommand)) {
				if (not = (variable.charAt(0) == RJSKeyword.LOGICAL_NOT.toChar()))
					variable = variable.substring(1);
				if (variable.equals(RJSKeyword.THIS.toString()))
					return new RJSResult(from, castToClass);
				if (isArrayAccess) {
					int indexOfOpenBrace = variable.indexOf(RJSKeyword.ARRAY_ACCESS_OPEN.toString());
					int indexOfCloseBrace = variable.indexOf(RJSKeyword.ARRAY_ACCESS_CLOSE.toString());
					String internal = variable.substring(indexOfOpenBrace + 1, indexOfCloseBrace);
					variable = variable.substring(0, indexOfOpenBrace);
					Object array = rjsClass.getField(variable);
					resultObject = arrayAccess(from, array, internal);
				} else if (trimmedCommand.charAt(0) == (RJSKeyword.METHOD_ARGUMENT_ACCESS.toChar())) {
					int indexOfCharacter = -1;
					for (int i = 1; i < trimmedCommand.length(); i++) {
						if (!Character.isDigit(trimmedCommand.charAt(i))) {
							indexOfCharacter = i;
							break;
						}
					}
					int methodId = Integer.parseInt(trimmedCommand.substring(1, indexOfCharacter));
					String parameterName = trimmedCommand.substring(indexOfCharacter);
					resultObject = rjsClass.getMethodArgument(methodId, parameterName);
				} else
					resultObject = rjsClass.getField(variable);
				if (not)
					resultObject = !((Boolean) resultObject);
				results.add(new RJSResult(resultObject, RJSResult.ResultType.VARIABLE, castToClass));
			}
		} else if (trimmedCommand.charAt(0) == (RJSKeyword.METHOD_ARGUMENT_ACCESS.toChar())) {
			int indexOfCharacter = -1;
			for (int i = 1; i < trimmedCommand.length(); i++) {
				if (!Character.isDigit(trimmedCommand.charAt(i))) {
					indexOfCharacter = i;
					break;
				}
			}
			int methodId = Integer.parseInt(trimmedCommand.substring(1, indexOfCharacter));
			String parameterName = trimmedCommand.substring(indexOfCharacter);
			Object resultObject = RJSDataPool.getMethodArgument(methodId, parameterName);
			results.add(new RJSResult(resultObject, RJSResult.ResultType.VARIABLE, castToClass));
		}
		if (results.isEmpty() || results.get(0).getResult() == null) {
			if (from instanceof Class)
				fields = ((Class<?>) from).getDeclaredFields();
			else
				fields = from.getClass().getDeclaredFields();
			for (String variable : Pattern.compile(Pattern.quote(RJSKeyword.COMMA.toString())).split(trimmedCommand)) {
				if (variable.equals(RJSKeyword.THIS.toString()))
					return new RJSResult(from, castToClass);
				String internal = null;
				if (isArrayAccess) {
					int indexOfOpenBrace = variable.indexOf(RJSKeyword.ARRAY_ACCESS_OPEN.toString());
					int indexOfCloseBrace = variable.indexOf(RJSKeyword.ARRAY_ACCESS_CLOSE.toString());
					internal = variable.substring(indexOfOpenBrace + 1, indexOfCloseBrace);
					variable = variable.substring(0, indexOfOpenBrace);
					if (variable.trim().isEmpty() && internal.contains(RJSKeyword.LOOP_INDEX.toString())) {
						internal = internal.replace(RJSKeyword.LOOP_INDEX.toString(), "");
						if (RJSNumber.isNumber(internal))
							return new RJSResult(new RJSNumber(internal), castToClass);
					}
				}
				for (Field field : fields) {
					if (field.getName().equals(variable)) {
						field.setAccessible(true);
						Object result = field.get(from);
						field.setAccessible(false);
						if (isArrayAccess && internal != null)
							result = arrayAccess(from, result, internal);
						if (not)
							result = !((Boolean) result);
						results.add(new RJSResult(result, RJSResult.ResultType.VARIABLE, castToClass));
						break;
					}
				}
			}
		}
		if (!results.isEmpty() && results.get(0).getResult() != null)
			if (results.size() == 1)
				return results.get(0);
			else
				return new RJSResult(results, RJSResult.ResultType.MULTIPLE);
		else {
			String commandString = trimmedCommand;
			int indexOfLastDot = commandString.lastIndexOf(RJSKeyword.DOT.toChar());
			if (indexOfLastDot != -1) {
				Object resultObject = null;
				try {
					Class<?> fromClass = Class.forName(commandString.substring(0, indexOfLastDot));
					Field field = fromClass.getDeclaredField(commandString.substring(indexOfLastDot + 1));
					resultObject = field.get(null);
				} catch (ClassNotFoundException e) {
					RJSParameter parameter = new RJSParameter(null, commandString, ParameterType.FOR_ACCESS);
					List<RJSParameter> parameterList = new ArrayList<>();
					parameterList.add(parameter);
					parameterList = getActualParameters(from, parameterList);
					resultObject = parameterList.get(0).getParameter();
				}
				if (not)
					resultObject = !((Boolean) resultObject);
				return new RJSResult(resultObject, castToClass);
			} else
				return null;
		}
	}

	private RJSResult executeCreateCommand(Object from)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		String trimmedUpperCaseCommand = upperCaseCommand.trim();
		int indexOfParameters = trimmedUpperCaseCommand.indexOf(RJSKeyword.CREATE.toString())
				+ RJSKeyword.CREATE.length();
		int indexOfTo = -1;
		if (isResultSetToCommand())
			indexOfTo = trimmedUpperCaseCommand.indexOf(RJSKeyword.TO.toString());
		String parametersString;
		if (indexOfTo != -1)
			parametersString = trimmedCommand.substring(indexOfParameters, indexOfTo);
		else
			parametersString = trimmedCommand.substring(indexOfParameters);
		int indexOfEqualTo = parametersString.indexOf(RJSKeyword.EQUAL.toString());
		String commandParameters[];
		if (indexOfEqualTo != -1)
			commandParameters = parametersString.substring(0, indexOfEqualTo).split(RJSKeyword.SPACE.toString());
		else
			commandParameters = parametersString.split(RJSKeyword.SPACE.toString());
		String className = commandParameters[0];
		boolean customClass = className.charAt(0) == RJSKeyword.OBJECT_ACCESS.toChar();
		if (customClass)
			className = className.substring(RJSKeyword.OBJECT_ACCESS.length());
		String objectName = commandParameters[1];
		if (!isVariableNameValid(objectName))
			return null;
		String instanceParameters = parametersString
				.substring(parametersString.indexOf(className + RJSKeyword.SPACE.toString() + objectName)
						+ className.length() + objectName.length() + 1);
		if (indexOfEqualTo != -1) {
			instanceParameters = instanceParameters
					.substring(instanceParameters.indexOf(RJSKeyword.EQUAL.toString()) + RJSKeyword.EQUAL.length());
			Object resultObject;
			if (instanceParameters.contains(RJSKeyword.ARRAY_SPLITTER.toString())) {
				List<RJSParameter> valueParameters = new ArrayList<>();
				if (instanceParameters.contains(RJSKeyword.ARRAY_SPLITTER.toString())) {
					List<RJSParameter> arrayParameters = getParametersFromParametersString(from, instanceParameters,
							RJSKeyword.ARRAY_SPLITTER.toString());
					arrayParameters = getActualParameters(from, arrayParameters);
					Class<?> fieldTypeClass = Class.forName(className);
					resultObject = Array.newInstance(fieldTypeClass, arrayParameters.size());
					for (int i = 0; i < arrayParameters.size(); i++)
						Array.set(resultObject, i, arrayParameters.get(i).getParameter());
					fieldTypeClass = resultObject.getClass();
				} else if (instanceParameters.contains(RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString()))
					resultObject = instanceParameters;
				else {
					valueParameters.add(new RJSParameter(instanceParameters));
					valueParameters = getActualParameters(from, valueParameters);
					resultObject = valueParameters.get(0).getParameter();
				}
			} else if (instanceParameters.contains(RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString())) {
				List<RJSResult> results = RJSParser.parse(from,
						new RJSParameter(instanceParameters).getParameter().toString());
				resultObject = results.get(results.size() - 1).getResult();
			} else {
				List<RJSParameter> parameter = new ArrayList<>();
				parameter.add(new RJSParameter(instanceParameters));
				resultObject = getActualParameters(from, parameter).get(0).getParameter();
			}
			RJSResult rjsResult = new RJSResult(resultObject, objectName);
			return rjsResult;
		}
		List<RJSParameter> parameters = getParametersFromParametersString(from, instanceParameters,
				RJSKeyword.COMMA.toString());
		parameters = getActualParameters(from, parameters);
		List<Class<?>> parameterTypes = RJSParameter.getParameterTypes(parameters);
		if (customClass && RJSDataPool.hasClass(className)) {
			RJSClass rjsClass = RJSDataPool.getClass(className).newInstance();
			if (isResultSetToCommand())
				assignParameter(from, rjsClass);
			RJSResult rjsResult = new RJSResult(rjsClass, objectName);
			return rjsResult;
		} else {
			Constructor<?> constructorToCall = null, lastMatchConstructor = null;
			constructorLoop: for (Constructor<?> constructor : Class.forName(className).getConstructors()) {
				Class<?>[] types = constructor.getParameterTypes();
				List<Class<?>> typesList = Arrays.asList(types);
				if (typesList.size() == parameterTypes.size()) {
					for (int i = 0; i < typesList.size(); i++) {
						Class<?> type = typesList.get(i);
						if (!type.isAssignableFrom(parameterTypes.get(i))
								|| !parameterTypes.get(i).isAssignableFrom(type)) {
							if (type.isPrimitive()) {
								Class<?> primitiveClass = getPrimitiveClass(parameterTypes.get(i));
								if (primitiveClass == null || !type.isAssignableFrom(primitiveClass)
										|| !!primitiveClass.isAssignableFrom(type)) {
									lastMatchConstructor = constructor;
									continue constructorLoop;
								}
							} else {
								lastMatchConstructor = constructor;
								continue constructorLoop;
							}
						}
					}
					constructorToCall = constructor;
				}
			}
			if (constructorToCall != null) {
				constructorToCall.setAccessible(true);
				Object result = constructorToCall.newInstance(RJSParameter.getParameters(parameters).toArray());
				constructorToCall.setAccessible(false);
				if (isResultSetToCommand())
					assignParameter(from, result);
				RJSResult rjsResult = new RJSResult(result, objectName);
				return rjsResult;
			} else {
				if (lastMatchConstructor != null)
					try {
						constructorToCall = lastMatchConstructor;
						constructorToCall.setAccessible(true);
						Object result = constructorToCall.newInstance(RJSParameter.getParameters(parameters).toArray());
						constructorToCall.setAccessible(false);
						if (isResultSetToCommand())
							assignParameter(from, result);
						RJSResult rjsResult = new RJSResult(result, objectName);
						return rjsResult;
					} catch (Exception e) {

					}
			}
		}
		return null;
	}

	private RJSResult executeAssignCommand(Object from)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		int indexOfParameters = upperCaseCommand.indexOf(RJSKeyword.ASSIGN.toString()) + RJSKeyword.ASSIGN.length();
		int indexOfTo = upperCaseCommand.indexOf(RJSKeyword.TO.toString());
		String parametersString;
		parametersString = command.substring(indexOfParameters, indexOfTo);
		List<RJSParameter> parameters = getParametersFromParametersString(from, parametersString,
				RJSKeyword.COMMA.toString());
		parameters = getActualParameters(from, parameters);
		assignParameter(from, parameters.get(0).getParameter());
		return null;
	}

	private RJSResult executeIfThenCommand(Object from)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		int indexOfIf = -1, indexOfThen = -1, indexOfElse = -1;
		indexOfIf = upperCaseCommand.indexOf(RJSKeyword.IF.toString()) + RJSKeyword.IF.length();
		indexOfThen = upperCaseCommand.indexOf(RJSKeyword.THEN.toString());
		indexOfElse = upperCaseCommand.indexOf(RJSKeyword.ELSE.toString());
		String parametersString;
		if (indexOfIf == -1)
			parametersString = command.substring(indexOfIf);
		else
			parametersString = command.substring(indexOfIf, indexOfThen);
		List<RJSParameter> parameters = getParametersFromParametersString(from, parametersString,
				RJSKeyword.COMMA.toString());
		parameters = getActualParameters(from, parameters);
		boolean isTrue = (boolean) parameters.get(0).getParameter();
		if (isTrue)
			if (indexOfElse != -1)
				parametersString = command.substring(indexOfThen + RJSKeyword.THEN.length(), indexOfElse);
			else
				parametersString = command.substring(indexOfThen + RJSKeyword.THEN.length());
		else if (indexOfElse != -1)
			parametersString = command.substring(indexOfElse + RJSKeyword.ELSE.length());
		if (isTrue || indexOfElse != -1)
			parametersString = (String) RJSDataPool.get(parametersString.trim());
		RJSParser.parse(from, parametersString.substring(0, parametersString.length()));
		return null;
	}

	private RJSResult executeClassCommand(Object from)
			throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException,
			NoSuchFieldException, SecurityException, InstantiationException, NoSuchMethodException {
		String upperCaseCommand = this.upperCaseCommand.trim();
		String trimmedCommand = this.trimmedCommand.trim();
		int indexOfClass = upperCaseCommand.indexOf(RJSKeyword.CLASS.toString()) + RJSKeyword.CLASS.length();
		String className = trimmedCommand.substring(indexOfClass,
				trimmedCommand.indexOf(RJSKeyword.SPACE.toString(), indexOfClass));
		RJSClass rjsClass = new RJSClass(className);
		int indexOfFields = upperCaseCommand.indexOf(RJSKeyword.FIELDS.toString());
		int indexOfMethods = -1;
		if (indexOfFields != -1) {
			int indexOfAfterFields = indexOfFields + RJSKeyword.FIELDS.length();
			int indexOfFieldsStart = command.indexOf(RJSKeyword.CLASS_FIELDS_OPEN.toString(), indexOfAfterFields)
					+ RJSKeyword.CLASS_FIELDS_OPEN.length();
			int indexOfFieldsEnd = indexOfMethods = command.indexOf(RJSKeyword.CLASS_FIELDS_CLOSE.toString(),
					indexOfFieldsStart);
			createClassFields(from, rjsClass, command.substring(indexOfFieldsStart, indexOfFieldsEnd));
		}
		if (upperCaseCommand.indexOf(RJSKeyword.METHODS.toString(), indexOfClass) != -1) {
			if (indexOfMethods == -1)
				indexOfMethods = upperCaseCommand.indexOf(RJSKeyword.METHODS.toString() + RJSKeyword.METHODS.length());
			int indexOfMethodsStart = command.indexOf(RJSKeyword.CLASS_METHODS_OPEN.toString(), indexOfMethods)
					+ RJSKeyword.CLASS_METHODS_OPEN.length();
			int indexOfMethodsEnd = command.indexOf(RJSKeyword.CLASS_METHODS_CLOSE.toString(), indexOfMethodsStart);
			createClassMethods(rjsClass, command.substring(indexOfMethodsStart, indexOfMethodsEnd));
		}
		RJSDataPool.addRJSClass(className, rjsClass);
		return null;
	}

	private RJSResult executeLoopCommand(Object from)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		String upperCaseCommand = this.upperCaseCommand.trim();
		String trimmedCommand = this.trimmedCommand.trim();
		int indexOfLoopCommand = upperCaseCommand.indexOf(RJSKeyword.LOOP.toString()) + RJSKeyword.LOOP.length();
		int indexOfArrayClose = trimmedCommand.indexOf(RJSKeyword.ARRAY_ACCESS_CLOSE.toString())
				+ RJSKeyword.ARRAY_ACCESS_CLOSE.length();
		String variable = trimmedCommand.substring(indexOfLoopCommand, indexOfArrayClose);
		int indexOfOpenBrace = variable.indexOf(RJSKeyword.ARRAY_ACCESS_OPEN.toString());
		int indexOfCloseBrace = variable.indexOf(RJSKeyword.ARRAY_ACCESS_CLOSE.toString());
		String internal = variable.substring(indexOfOpenBrace + 1, indexOfCloseBrace);
		int stride = 1;
		if (internal.contains(RJSKeyword.COMMA.toString())) {
			String[] internalWithStride = internal.split(RJSKeyword.COMMA.toString());
			internal = internalWithStride[0];
			stride = Integer.parseInt(internalWithStride[1].trim());
		} else if (RJSNumber.isNumber(internal)) {
			stride = Integer.parseInt(internal);
			variable = new StringBuilder(variable)
					.replace(indexOfOpenBrace + RJSKeyword.ARRAY_ACCESS_OPEN.length(), indexOfCloseBrace, "")
					.toString();
		}
		boolean reverse = false;
		int startIndex = 0, endIndex = 0;
		String innerCommand = trimmedCommand.substring(indexOfArrayClose);
		if (RJSParameter.isDataPoolParameter(innerCommand))
			innerCommand = RJSDataPool.get(innerCommand).toString();
		if (!internal.isEmpty() && internal.contains(RJSKeyword.LOOP_RANGE_DOTS.toString())) {
			String[] rangeIndex = Pattern.compile(Pattern.quote(RJSKeyword.LOOP_RANGE_DOTS.toString())).split(internal);
			String startIndexString = rangeIndex[0].trim();
			String endIndexString = rangeIndex[1].trim();
			if (RJSNumber.isNumber(startIndexString))
				startIndex = Integer.parseInt(startIndexString);
			else if (startIndexString.equals(RJSKeyword.LOOP_MAX_LENGTH.toString())) {
				variable = variable.substring(0, variable.indexOf(RJSKeyword.ARRAY_ACCESS_OPEN.toString()));
				variable = new RJSCommand(variable).execute(from).getParameterString();
				@SuppressWarnings("unchecked")
				List<Object> array = (List<Object>) RJSDataPool.get(variable);
				startIndex = array.size() - 1;
			}
			if (RJSNumber.isNumber(endIndexString))
				endIndex = Integer.parseInt(rangeIndex[1].trim());
			else if (endIndexString.equals(RJSKeyword.LOOP_MAX_LENGTH.toString())) {
				variable = variable.substring(0, variable.indexOf(RJSKeyword.ARRAY_ACCESS_OPEN.toString()));
				variable = new RJSCommand(variable).execute(from).getParameterString();
				@SuppressWarnings("unchecked")
				List<Object> array = (List<Object>) RJSDataPool.get(variable);
				endIndex = array.size() - 1;
			}
			reverse = endIndex - startIndex < 0;
		} else {
			variable = new RJSCommand(variable).execute(from).getParameterString();
			RJSParameter parameter = new RJSParameter(RJSDataPool.get(variable));
			List<RJSParameter> parameters = new ArrayList<>();
			parameters.add(parameter);
			parameters = getActualParameters(from, parameters);
			@SuppressWarnings("unchecked")
			List<Object> array = (List<Object>) parameters.get(0).getParameter();
			endIndex = array.size() - 1;
		}
		int i = startIndex;
		boolean execute = true;
		do {
			if (reverse && i >= endIndex)
				execute = true;
			else if (!reverse && i <= endIndex)
				execute = true;
			else
				execute = false;
			if (!execute)
				break;
			int indexOfLastArrayAccessClose = 0;
			while (innerCommand.indexOf(RJSKeyword.LOOP_INDEX.toString(), indexOfLastArrayAccessClose) != -1) {
				int indexOfArrayAccessOpen = innerCommand.indexOf(RJSKeyword.ARRAY_ACCESS_OPEN.toString(),
						indexOfLastArrayAccessClose) + RJSKeyword.ARRAY_ACCESS_OPEN.length();
				int indexOfArrayAccessClose = innerCommand.indexOf(RJSKeyword.ARRAY_ACCESS_CLOSE.toString(),
						indexOfArrayAccessOpen);
				StringBuilder stringBuilder = new StringBuilder(innerCommand);
				stringBuilder.replace(indexOfArrayAccessOpen, indexOfArrayAccessClose,
						RJSKeyword.LOOP_INDEX.toString() + i);
				innerCommand = stringBuilder.toString();
				indexOfLastArrayAccessClose = innerCommand.indexOf(RJSKeyword.ARRAY_ACCESS_CLOSE.toString(),
						indexOfArrayAccessClose) + 1;
			}
			RJSParser.parse(from, innerCommand);
			if (reverse)
				i -= stride;
			else
				i += stride;
		} while (execute);
		return null;
	}

	private RJSResult executeIsCommand(Object from)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		int indexOfParameters = upperCaseCommand.indexOf(RJSKeyword.IS.toString()) + RJSKeyword.IS.length();
		int indexOfTo = -1;
		if (isResultSetToCommand())
			indexOfTo = upperCaseCommand.indexOf(RJSKeyword.TO.toString());
		String parametersString;
		if (indexOfTo == -1)
			parametersString = command.substring(indexOfParameters);
		else
			parametersString = command.substring(indexOfParameters, indexOfTo);
		Object[] evaluationMethodArguments = getEvaluationMethod(parametersString);
		RJSKeyword evaluation = (RJSKeyword) evaluationMethodArguments[0];
		int indexOfEvaluation = (int) evaluationMethodArguments[1];
		if (evaluation != null && indexOfEvaluation != -1) {
			RJSResult result = null;
			if (evaluation == RJSKeyword.LOGICAL_AND || evaluation == RJSKeyword.LOGICAL_OR) {
				if (!parametersString.trim().isEmpty()) {
					String parameter1String = parametersString.substring(0, indexOfEvaluation);
					String parameter2String = parametersString.substring(indexOfEvaluation + evaluation.length());
					RJSParameter parameter1, parameter2;
					if (parameter1String.contains(RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString()))
						parameter1 = new RJSParameter(null, parameter1String, RJSParameter.ParameterType.FOR_ACCESS);
					else
						parameter1 = new RJSParameter(parameter1String);
					List<RJSParameter> parameters1 = new ArrayList<>();
					parameters1.add(parameter1);
					parameter1 = getActualParameters(from, parameters1).get(0);
					if (parameter1.getObjectType().isAssignableFrom(Boolean.class)) {
						if (evaluation == RJSKeyword.LOGICAL_AND && !(Boolean) parameter1.getParameter())
							result = new RJSResult(false);
						else if (evaluation == RJSKeyword.LOGICAL_OR && (Boolean) parameter1.getParameter())
							result = new RJSResult(true);
					}
					if (result == null) {
						if (parameter2String.contains(RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString()))
							parameter2 = new RJSParameter(null, parameter2String,
									RJSParameter.ParameterType.FOR_ACCESS);
						else
							parameter2 = new RJSParameter(parameter2String);
						List<RJSParameter> parameters2 = new ArrayList<>();
						parameters2.add(parameter2);
						parameter2 = getActualParameters(from, parameters2).get(0);
						result = new RJSResult(evaluateCondition(parameter1, parameter2, evaluation));
					}
				}
			} else {
				List<RJSParameter> parameters = getBinaryParametersFromParametersString(parametersString,
						indexOfEvaluation, evaluation.length());
				parameters = getActualParameters(from, parameters);
				RJSParameter parameter1 = parameters.get(0);
				RJSParameter parameter2 = parameters.get(1);
				result = new RJSResult(evaluateCondition(parameter1, parameter2, evaluation));
			}
			if (indexOfTo != -1)
				assignParameter(from, result.getResult());
			return result;
		} else
			return null;
	}

	private RJSResult executeVarCommand(Object from)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		int indexOfParameters = upperCaseCommand.indexOf(RJSKeyword.VAR.toString()) + RJSKeyword.VAR.length();
		String parameters = command.substring(indexOfParameters).trim();
		for (String fieldName : Pattern.compile(RJSKeyword.COMMA.toString()).split(parameters)) {
			String name = fieldName;
			Object value = null;
			boolean methodWithArguments = false;
			if (fieldName.contains(RJSKeyword.EQUAL.toString())) {
				String[] fieldWithValue = fieldName.split(RJSKeyword.EQUAL.toString());
				name = fieldWithValue[0];
				List<RJSParameter> valueParameters = new ArrayList<>();
				String valueString = fieldWithValue[1];
				if (valueString.contains(RJSKeyword.ARRAY_SPLITTER.toString())) {
					List<RJSParameter> arrayParameters = getParametersFromParametersString(from, valueString,
							RJSKeyword.ARRAY_SPLITTER.toString());
					for (int i = 0; i < arrayParameters.size(); i++) {
						RJSParameter parameter = arrayParameters.get(i);
						if (!parameter.getParameter().toString()
								.contains(RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString())) {
							List<RJSParameter> actualParameterList = new ArrayList<>();
							actualParameterList.add(parameter);
							actualParameterList = getActualParameters(from, actualParameterList);
							RJSParameter actualParameter = actualParameterList.get(0);
							arrayParameters.remove(i);
							arrayParameters.add(i, actualParameter);
						}
					}
					List<Object> arrayValues = new ArrayList<>();
					for (RJSParameter parameter : arrayParameters)
						arrayValues.add(parameter.getParameter());
					value = arrayValues;
				} else if (valueString.contains(RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString()))
					createVarMethod(name, valueString);
				else {
					valueParameters.add(new RJSParameter(fieldWithValue[1]));
					valueParameters = getActualParameters(from, valueParameters);
					value = valueParameters.get(0).getParameter();
				}
			}
			if (!methodWithArguments)
				RJSDataPool.add(value, name.trim());
		}
		return null;
	}

	private void createVarMethod(String methodName, String command) throws ClassNotFoundException {
		if (command.contains(RJSKeyword.CLASS_METHOD_ARGUMENT_OPEN.toString())) {
			String valueWithArguments = command.trim();
			int indexOfArgumentsEnd = -1;
			int indexOfArgumentsStart = valueWithArguments.indexOf(RJSKeyword.CLASS_METHOD_ARGUMENT_OPEN.toString())
					+ RJSKeyword.CLASS_METHOD_ARGUMENT_OPEN.length();
			indexOfArgumentsEnd = valueWithArguments.indexOf(RJSKeyword.CLASS_METHOD_ARGUMENT_CLOSE.toString(),
					indexOfArgumentsStart);
			LinkedHashMap<String, Class<?>> argumentToClassMap = new LinkedHashMap<>();
			for (String argument : Pattern.compile(RJSKeyword.COMMA.toString())
					.split(valueWithArguments.substring(indexOfArgumentsStart, indexOfArgumentsEnd))) {
				argument = argument.trim();
				String[] argumentWithClass = argument.split(RJSKeyword.SPACE.toString());
				String argumentType = argumentWithClass[0];
				Class<?> argumentTypeClass;
				if (argumentType.equalsIgnoreCase(RJSKeyword.VAR.toString().trim()))
					argumentTypeClass = Object.class;
				else
					argumentTypeClass = Class.forName(argumentType);
				String argumentName = argumentWithClass[1];
				argumentToClassMap.put(argumentName, argumentTypeClass);
			}
			RJSDataPool.declareMethod(methodName, argumentToClassMap);
			String methodDefinition = valueWithArguments.substring(indexOfArgumentsEnd);
			RJSDataPool.defineMethod(methodName, (String) RJSDataPool.get(methodDefinition));
		} else {
			RJSDataPool.declareMethod(methodName, null);
			RJSDataPool.defineMethod(methodName, command);
		}
	}

	private void assignParameter(Object from, Object value)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		int indexOfParameter = upperCaseCommand.indexOf(RJSKeyword.TO.toString()) + RJSKeyword.TO.length();
		String parameter = command.substring(indexOfParameter).trim();
		if (parameter.contains(RJSKeyword.DOT.toString())) {
			String[] parameterParts = Pattern.compile(Pattern.quote(RJSKeyword.DOT.toString())).split(parameter);
			String object = parameterParts[0];
			parameter = parameterParts[1];
			Object storedObject = null;
			if (object.charAt(0) == RJSKeyword.OBJECT_ACCESS.toChar())
				storedObject = RJSDataPool.getByName(object.substring(1));
			if (storedObject != null)
				from = storedObject;
			else
				from = new RJSCommand(object).execute(from).getResult();
		}
		if (parameter.charAt(0) == RJSKeyword.OBJECT_ACCESS.toChar()) {
			String objectName = parameter.substring(1);
			int indexOfObject = RJSDataPool.getIndexByName(objectName);
			RJSDataPool.remove(indexOfObject);
			RJSDataPool.addAt(value, indexOfObject, objectName);
		} else if (from instanceof RJSClass) {
			RJSClass rjsClass = (RJSClass) from;
			rjsClass.setField(parameter, value);
		} else {
			Field field;
			if (from instanceof Class)
				field = ((Class<?>) from).getDeclaredField(parameter);
			else
				field = from.getClass().getDeclaredField(parameter);
			field.setAccessible(true);
			field.set(from, value);
			field.setAccessible(false);
		}
	}

	private static List<RJSParameter> getParametersFromParametersString(Object from, String parametersString,
			String delimiter) {
		List<RJSParameter> parameters = new ArrayList<>();
		if (!parametersString.trim().isEmpty()) {
			for (String parameterString : Pattern.compile(Pattern.quote(delimiter)).split(parametersString)) {
				RJSParameter parameter;
				if (parameterString.contains(RJSKeyword.DATA_POOL_STRING_OPEN_BRACE.toString()))
					parameter = new RJSParameter(from, parameterString, RJSParameter.ParameterType.FOR_ACCESS);
				else if (parameterString.contains(RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString()))
					parameter = new RJSParameter(from, parameterString, RJSParameter.ParameterType.FOR_ACCESS);
				else
					parameter = new RJSParameter(from, parameterString);
				parameters.add(parameter);
			}
		}
		return parameters;
	}

	private static List<RJSParameter> getBinaryParametersFromParametersString(String parametersString, int splitIndex,
			int lengthOfSplitter) {
		List<RJSParameter> parameters = new ArrayList<>();
		if (!parametersString.trim().isEmpty()) {
			String parameter1String = parametersString.substring(0, splitIndex);
			String parameter2String = parametersString.substring(splitIndex + lengthOfSplitter);
			RJSParameter parameter1, parameter2;
			if (parameter1String.contains(RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString()))
				parameter1 = new RJSParameter(null, parameter1String, RJSParameter.ParameterType.FOR_ACCESS);
			else
				parameter1 = new RJSParameter(parameter1String);
			if (parameter2String.contains(RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString()))
				parameter2 = new RJSParameter(null, parameter2String, RJSParameter.ParameterType.FOR_ACCESS);
			else
				parameter2 = new RJSParameter(parameter2String);
			parameters.add(parameter1);
			parameters.add(parameter2);
		}
		return parameters;
	}

	private List<RJSParameter> getActualParameters(final Object from, List<RJSParameter> parameters)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		boolean negate = false, not = false, objectParameter = false;
		RJSParameter originalParameter = null;
		for (int i = 0; i < parameters.size(); i++) {
			RJSParameter parameter = parameters.get(i);
			if (parameter.getParameter() == null)
				continue;
			Object execFrom = from;
			String trimmedParameter = parameter.getParameter().toString().trim();
			negate = trimmedParameter.charAt(0) == RJSKeyword.SUBTRACT.toChar();
			not = trimmedParameter.charAt(0) == RJSKeyword.LOGICAL_NOT.toChar();
			if (parameter.getObjectType().equals(String.class)
					&& parameter.getParameter().toString().contains(RJSKeyword.DOT.toString())
					&& !trimmedParameter.equals(RJSKeyword.DOT.toString())) {
				objectParameter = true;
				String[] variableParts = Pattern.compile(Pattern.quote(RJSKeyword.DOT.toString()))
						.split(trimmedParameter);
				String object = variableParts[0];
				negate = object.charAt(0) == RJSKeyword.SUBTRACT.toChar();
				not = object.charAt(0) == RJSKeyword.LOGICAL_NOT.toChar();
				if (negate || not)
					object = object.substring(1);
				originalParameter = parameter;
				parameter = new RJSParameter(variableParts[1]);
				parameters.remove(i);
				parameters.add(i, parameter);
				Object storedObject = null;
				if (object.equals(RJSKeyword.THIS.toString()))
					storedObject = from;
				else if (object.charAt(0) == RJSKeyword.OBJECT_ACCESS.toChar() || ((negate || not)
						&& object.length() > 1 && object.charAt(1) == RJSKeyword.OBJECT_ACCESS.toChar()))
					storedObject = RJSDataPool.getByName(object.substring(1));
				if (storedObject != null)
					execFrom = storedObject;
				else {
					RJSResult fromObject = new RJSCommand(object).execute(from);
					if (fromObject != null && !fromObject.toString().trim().isEmpty())
						execFrom = new RJSCommand(object).execute(from).getResult();
					else {
						parameters.remove(i);
						parameters.add(i, originalParameter);
					}
				}
			}
			String parameterString = parameter.getParameter().toString();
			if (parameterString.contains(RJSKeyword.DATA_POOL_STRING_OPEN_BRACE.toString())) {
				parameters.remove(i);
				RJSParameter stringAccessParameter = new RJSParameter(
						parameter.getParameter().toString().replace(RJSKeyword.DATA_POOL_STRING_OPEN_BRACE.toString(),
								RJSKeyword.DATA_POOL_DATA_OPEN_BRACE.toString()));
				parameters.add(i, stringAccessParameter);
			} else if (parameterString.contains(RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString())) {
				parameters.remove(i);
				String commandParameterString = RJSDataPool.get(parameterString).toString();
				List<RJSResult> results = RJSParser.parse(from, commandParameterString);
				if (!results.isEmpty()) {
					RJSParameter commandResult = new RJSParameter(results.get(results.size() - 1).getResult());
					parameters.add(i, commandResult);
				}
			}
			if (parameter.getParameterType() == RJSParameter.ParameterType.VARIALBLE
					|| (parameter.getParameterType() == RJSParameter.ParameterType.ARRAY
							&& !parameter.getParameter().toString().contains(RJSKeyword.OBJECT_ACCESS.toString()))) {
				if ((negate || not) && !objectParameter)
					parameter = new RJSParameter(parameter.getParameter().toString().trim().substring(1));
				RJSResult result = new RJSCommand(parameter.getParameter().toString()).execute(execFrom);
				RJSParameter variableParameter = null;
				if (result == null) {
					trimmedParameter = parameter.getParameter().toString().trim();
					if (parameter.getParameterType() == RJSParameter.ParameterType.ARRAY)
						trimmedParameter = trimmedParameter.substring(0,
								trimmedParameter.indexOf(RJSKeyword.ARRAY_ACCESS_OPEN.toString()));
					if (execFrom instanceof RJSClass) {
						Class<?> type = ((RJSClass) execFrom).getFieldType(trimmedParameter);
						if (type != null)
							variableParameter = new RJSParameter(type);
					} else {
						Field field = null;
						if (execFrom instanceof Class)
							field = ((Class<?>) execFrom).getDeclaredField(trimmedParameter);
						else
							try {
								field = execFrom.getClass().getDeclaredField(trimmedParameter);
							} catch (NoSuchFieldException e) {
							}
						if (field != null) {
							if (field.getType().isArray())
								variableParameter = new RJSParameter(field.getType().getComponentType());
							else
								variableParameter = new RJSParameter(field.getType());
						} else
							variableParameter = originalParameter;
					}
				} else {
					if (negate) {
						RJSNumber number = new RJSNumber((Number) result.getResult()).multiply(-1);
						result = new RJSResult(number);
					} else if (not)
						result = new RJSResult(!(Boolean) result.getResult());
					variableParameter = new RJSParameter(result.getParameterString());
				}
				parameters.remove(i);
				parameters.add(i, variableParameter);
			} else if (parameter.getParameterType() == RJSParameter.ParameterType.ARRAY) {
				trimmedParameter = parameter.getParameter().toString().trim();
				if (negate || not)
					parameter = new RJSParameter(trimmedParameter = trimmedParameter.substring(1));
				if (trimmedParameter.charAt(0) == RJSKeyword.OBJECT_ACCESS.toChar()) {
					Object arrayObject = RJSDataPool
							.getByName(trimmedParameter.substring(RJSKeyword.OBJECT_ACCESS.length(),
									trimmedParameter.indexOf(RJSKeyword.ARRAY_ACCESS_OPEN.toString())));
					String variable = trimmedParameter.substring(RJSKeyword.OBJECT_ACCESS.length());
					int indexOfOpenBrace = variable.indexOf(RJSKeyword.ARRAY_ACCESS_OPEN.toString());
					int indexOfCloseBrace = variable.indexOf(RJSKeyword.ARRAY_ACCESS_CLOSE.toString());
					String internal = variable.substring(indexOfOpenBrace + 1, indexOfCloseBrace);
					variable = variable.substring(0, indexOfOpenBrace);
					Object value = arrayAccess(from, arrayObject, internal);
					if (negate) {
						RJSNumber number = new RJSNumber((Number) value).multiply(-1);
						value = number;
					} else if (not)
						value = !(Boolean) value;
					parameters.remove(i);
					parameters.add(i, new RJSParameter(new RJSResult(value).getParameterString()));
				}
			}
		}
		return parameters;
	}

	@SuppressWarnings("rawtypes")
	private Object arrayAccess(Object from, Object array, String command)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		Object result = array;
		if (result instanceof List)
			result = ((List) result).toArray();
		String internal = command;
		if (internal.contains(RJSKeyword.LOOP_INDEX.toString()))
			internal = internal.replace(RJSKeyword.LOOP_INDEX.toString(), "");
		if (RJSNumber.isNumber(internal)) {
			result = Array.get(result, Integer.parseInt(internal));
		} else {
			if (internal.trim().isEmpty()) {
				List<Object> arrayList = new ArrayList<>();
				for (int i = 0; i < Array.getLength(result); i++)
					arrayList.add(Array.get(result, i));
				result = new RJSResult(arrayList).getResult();
			} else if (internal.contains(RJSKeyword.LOOP_RANGE_DOTS.toString())) {
				Object[] objectArray = (Object[]) result;
				String[] range = Pattern.compile(Pattern.quote(RJSKeyword.LOOP_RANGE_DOTS.toString())).split(internal);
				int startIndex = Integer.parseInt(range[0].trim());
				String endRange = range[1].trim();
				int endIndex = 0;
				if (RJSNumber.isNumber(endRange))
					endIndex = Integer.parseInt(endRange) + 1;
				else if (endRange.equals(RJSKeyword.LOOP_MAX_LENGTH.toString()))
					endIndex = objectArray.length;
				boolean reverse = (endIndex - startIndex) < 0;
				List<Object> arrayList;
				if (reverse) {
					arrayList = Arrays.asList(Arrays.copyOfRange(objectArray, endIndex - 1, startIndex + 1));
					Collections.reverse(arrayList);
				} else
					arrayList = Arrays.asList(Arrays.copyOfRange(objectArray, startIndex, endIndex));
				result = new RJSResult(arrayList).getResult();
			} else {
				List<Object> arrayList = Arrays.asList((Object[]) result);
				result = new RJSCommand(internal).execute(from).getResult();
				int indexOfInternal = arrayList.indexOf(result);
				if (indexOfInternal != -1)
					result = indexOfInternal;
			}
		}
		if (result.toString().contains(RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString())) {
			String innerCommand = result.toString();
			innerCommand = RJSDataPool.get(innerCommand).toString();
			List<RJSResult> results = RJSParser.parse(from, innerCommand);
			if (results != null && !results.isEmpty())
				result = results.get(0).getResult();
		}
		return result;
	}

	private Object[] getComputationMethod(String parameters) {
		RJSKeyword computationMethod = null;
		int index = -1;
		for (int i = 1; i < parameters.length(); i++) {
			if (parameters.charAt(i) == RJSKeyword.DIVIDE.toChar()) {
				computationMethod = RJSKeyword.DIVIDE;
				index = i;
				break;
			} else if (parameters.charAt(i) == RJSKeyword.MULTIPLY.toChar()) {
				computationMethod = RJSKeyword.MULTIPLY;
				index = i;
				break;
			} else if (parameters.charAt(i) == RJSKeyword.ADD.toChar()) {
				computationMethod = RJSKeyword.ADD;
				index = i;
				break;
			} else if (parameters.charAt(i) == RJSKeyword.SUBTRACT.toChar()
					&& parameters.charAt(i + 1) != RJSKeyword.OBJECT_ACCESS.toChar()) {
				computationMethod = RJSKeyword.SUBTRACT;
				index = i;
				break;
			}
		}
		return new Object[] { computationMethod, index };
	}

	private Object[] getEvaluationMethod(String parameters) {
		RJSKeyword evaluation = null;
		int index = -1;
		if ((index = parameters.indexOf(RJSKeyword.EQUAL_TO.toString())) != -1)
			evaluation = RJSKeyword.EQUAL_TO;
		else if ((index = parameters.indexOf(RJSKeyword.GREATER_THAN_OR_EQUAL_TO.toString())) != -1)
			evaluation = RJSKeyword.GREATER_THAN_OR_EQUAL_TO;
		else if ((index = parameters.indexOf(RJSKeyword.LESSER_THAN_OR_EQUAL_TO.toString())) != -1)
			evaluation = RJSKeyword.LESSER_THAN_OR_EQUAL_TO;
		else if ((index = parameters.indexOf(RJSKeyword.GREATER_THAN.toString())) != -1)
			evaluation = RJSKeyword.GREATER_THAN;
		else if ((index = parameters.indexOf(RJSKeyword.LESSER_THAN.toString())) != -1)
			evaluation = RJSKeyword.LESSER_THAN;
		else if ((index = parameters.indexOf(RJSKeyword.NOT_EQUAL_TO.toString())) != -1)
			evaluation = RJSKeyword.NOT_EQUAL_TO;
		else if ((index = parameters.indexOf(RJSKeyword.LOGICAL_AND.toString())) != -1)
			evaluation = RJSKeyword.LOGICAL_AND;
		else if ((index = parameters.indexOf(RJSKeyword.LOGICAL_OR.toString())) != -1)
			evaluation = RJSKeyword.LOGICAL_OR;
		return new Object[] { evaluation, index };
	}

	private boolean isVariableNameValid(String name) {
		return Pattern.compile("^[a-zA-Z_$][a-zA-Z_$0-9]*$", Pattern.CASE_INSENSITIVE).matcher(name).find();
	}

	private boolean needsBodmas(String formula) {
		if (formula.trim().length() == 0)
			return false;
		int count = 0;
		for (int i = 0; i < formula.substring(1).length(); i++) {
			char character = formula.charAt(i);
			if (character == RJSKeyword.DIVIDE.toChar() || character == RJSKeyword.MULTIPLY.toChar()
					|| character == RJSKeyword.ADD.toChar() || character == RJSKeyword.SUBTRACT.toChar())
				count++;
			if (count > 1)
				break;
		}
		return count > 1;

	}

	private RJSNumber applyBodmas(Object from, String formula)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		while (formula.contains(RJSKeyword.INNER_COMMAND_OPEN.toString())
				|| formula.contains(RJSKeyword.INNER_COMMAND_CLOSE.toString())) {
			int indexOfOpenBraces = formula.lastIndexOf(RJSKeyword.INNER_COMMAND_OPEN.toString());
			int indexOfCloseBraces = formula.indexOf(RJSKeyword.INNER_COMMAND_CLOSE.toString(), indexOfOpenBraces);
			String subFormula = formula.substring(indexOfOpenBraces + 1, indexOfCloseBraces);
			String bodmasAppliedSubFormula = "";
			if (subFormula.contains(RJSKeyword.DIVIDE.toString()))
				bodmasAppliedSubFormula = bodmas(from, subFormula, RJSKeyword.DIVIDE.toChar());
			else if (subFormula.contains(RJSKeyword.MULTIPLY.toString()))
				bodmasAppliedSubFormula = bodmas(from, subFormula, RJSKeyword.MULTIPLY.toChar());
			else if (subFormula.contains(RJSKeyword.ADD.toString())
					|| subFormula.contains(RJSKeyword.SUBTRACT.toString()))
				bodmasAppliedSubFormula = bodmas(from, subFormula, RJSKeyword.ADD.toChar());
			if (needsComputation(bodmasAppliedSubFormula))
				formula = formula.replace(subFormula, bodmasAppliedSubFormula);
			else
				formula = formula.replace(RJSKeyword.INNER_COMMAND_OPEN.toString() + subFormula
						+ RJSKeyword.INNER_COMMAND_CLOSE.toString(), bodmasAppliedSubFormula);
		}
		while (formula.contains(RJSKeyword.DIVIDE.toString()))
			formula = bodmas(from, formula, RJSKeyword.DIVIDE.toChar());
		while (formula.contains(RJSKeyword.MULTIPLY.toString()))
			formula = bodmas(from, formula, RJSKeyword.MULTIPLY.toChar());
		while (formula.contains(RJSKeyword.ADD.toString())
				|| formula.substring(1).contains(RJSKeyword.SUBTRACT.toString()))
			formula = bodmas(from, formula, RJSKeyword.ADD.toChar());
		return new RJSNumber(formula);
	}

	private String bodmas(Object from, String formula, char operation)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
		boolean isAddition = operation == RJSKeyword.ADD.toChar() || operation == RJSKeyword.SUBTRACT.toChar();
		for (int i = 0; i < formula.length(); i++) {
			char c = formula.charAt(i);
			boolean isOperation;
			if (isAddition)
				isOperation = c == RJSKeyword.ADD.toChar() || c == RJSKeyword.SUBTRACT.toChar();
			else
				isOperation = c == operation;
			if (isOperation) {
				int leftIndex = getLeft(i, formula);
				int rightIndex = getRight(i, formula);
				String subFormula = formula.substring(leftIndex, rightIndex);
				RJSResult result = null;
				if (needsComputation(subFormula)) {
					result = new RJSCommand(RJSKeyword.COMPUTE + subFormula).execute(from);
				} else
					result = new RJSResult(subFormula, RJSResult.ResultType.OBJECT);
				if (subFormula.charAt(0) == RJSKeyword.SUBTRACT.toChar()
						&& Double.parseDouble(result.getResult().toString()) > 0)
					formula = formula.replace(subFormula,
							String.valueOf(RJSKeyword.ADD.toString() + result.getResult()));
				else
					formula = formula.replace(subFormula, String.valueOf(result.getResult()));
			}
		}
		return formula;
	}

	private int getLeft(int index, String formula) {
		int left = -1;
		boolean negativeNumber = false;
		for (int i = index - 1; i > 0; i--) {
			char c = formula.charAt(i);
			if (c == RJSKeyword.DIVIDE.toChar() || c == RJSKeyword.MULTIPLY.toChar() || c == RJSKeyword.ADD.toChar()
					|| c == RJSKeyword.SUBTRACT.toChar()) {
				if (c == RJSKeyword.SUBTRACT.toChar())
					if (i - 1 > 0 && formula.charAt(i - 1) == RJSKeyword.ADD.toChar())
						continue;
					else
						negativeNumber = true;
				left = i;
				if (negativeNumber)
					left--;
				break;
			}
		}
		return left + 1;
	}

	private int getRight(int index, String formula) {
		index = index + 1;
		if (formula.substring(index).charAt(0) == RJSKeyword.SUBTRACT.toChar())
			index += 1;
		int right = formula.length();
		for (int i = index; i < formula.length(); i++) {
			char c = formula.charAt(i);
			if (c == RJSKeyword.DIVIDE.toChar() || c == RJSKeyword.MULTIPLY.toChar() || c == RJSKeyword.ADD.toChar()
					|| c == RJSKeyword.SUBTRACT.toChar()) {
				right = i;
				break;
			}
		}
		return right;
	}

	private void createClassFields(Object from, RJSClass rjsClass, String command)
			throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException,
			NoSuchFieldException, SecurityException, InstantiationException, NoSuchMethodException {
		for (String fields : Pattern.compile(RJSKeyword.SEMI_COLON.toString()).split(command)) {
			fields = fields.trim();
			int indexOfFieldNames = fields.indexOf(RJSKeyword.SPACE.toString());
			String fieldType = fields.substring(0, indexOfFieldNames);
			Class<?> fieldTypeClass;
			boolean var = Pattern.compile(Pattern.quote(RJSKeyword.VAR.toString().trim()), Pattern.CASE_INSENSITIVE)
					.matcher(fieldType).matches();
			if (var)
				fieldTypeClass = java.lang.Object.class;
			else
				fieldTypeClass = Class.forName(fieldType);
			for (String fieldName : Pattern.compile(RJSKeyword.COMMA.toString())
					.split(fields.substring(indexOfFieldNames))) {
				String name = fieldName;
				Object value = null;
				if (fieldName.contains(RJSKeyword.EQUAL.toString())) {
					String[] fieldWithValue = fieldName.split(RJSKeyword.EQUAL.toString());
					name = fieldWithValue[0];
					List<RJSParameter> valueParameters = new ArrayList<>();
					String valueString = fieldWithValue[1];
					if (valueString.contains(RJSKeyword.ARRAY_SPLITTER.toString())) {
						List<RJSParameter> arrayParameters = getParametersFromParametersString(from, valueString,
								RJSKeyword.ARRAY_SPLITTER.toString());
						arrayParameters = getActualParameters(from, arrayParameters);
						if (var) {
							List<Object> arrayValues = new ArrayList<>();
							for (RJSParameter arrayParameter : arrayParameters)
								arrayValues.add(arrayParameter.getParameter());
							value = arrayValues;
						} else {
							value = Array.newInstance(fieldTypeClass, arrayParameters.size());
							for (int i = 0; i < arrayParameters.size(); i++)
								Array.set(value, i, arrayParameters.get(i).getParameter());
						}
						fieldTypeClass = value.getClass();
					} else if (valueString.contains(RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString()))
						value = valueString;
					else {
						valueParameters.add(new RJSParameter(fieldWithValue[1]));
						valueParameters = getActualParameters(from, valueParameters);
						value = valueParameters.get(0).getParameter();
					}
				}
				rjsClass.addField(name.trim(), value, fieldTypeClass);
			}
		}
	}

	private void createClassMethods(RJSClass rjsClass, String command) throws ClassNotFoundException {
		for (String methods : Pattern.compile(RJSKeyword.SEMI_COLON.toString()).split(command)) {
			methods = methods.trim();
			int indexOfSpace = methods.indexOf(RJSKeyword.SPACE.toString()) + RJSKeyword.SPACE.length();
			String methodName = methods.substring(0, indexOfSpace).trim();
			int indexOfArgumentsEnd = -1;
			if (methods.contains(RJSKeyword.CLASS_METHOD_ARGUMENT_OPEN.toString())) {
				int indexOfArgumentsStart = methods.indexOf(RJSKeyword.CLASS_METHOD_ARGUMENT_OPEN.toString(),
						indexOfSpace) + RJSKeyword.CLASS_METHOD_ARGUMENT_OPEN.length();
				indexOfArgumentsEnd = methods.indexOf(RJSKeyword.CLASS_METHOD_ARGUMENT_CLOSE.toString(),
						indexOfArgumentsStart);
				LinkedHashMap<String, Class<?>> argumentToClassMap = new LinkedHashMap<>();
				for (String argument : Pattern.compile(RJSKeyword.COMMA.toString())
						.split(methods.substring(indexOfArgumentsStart, indexOfArgumentsEnd))) {
					argument = argument.trim();
					String[] argumentWithClass = argument.split(RJSKeyword.SPACE.toString());
					String argumentType = argumentWithClass[0];
					Class<?> argumentTypeClass;
					if (argumentType.equalsIgnoreCase(RJSKeyword.VAR.toString().trim()))
						argumentTypeClass = Object.class;
					else
						argumentTypeClass = Class.forName(argumentType);
					String argumentName = argumentWithClass[1];
					argumentToClassMap.put(argumentName, argumentTypeClass);
				}
				rjsClass.declareMethod(methodName, argumentToClassMap);
			}
			if (indexOfArgumentsEnd == -1) {
				indexOfArgumentsEnd = indexOfSpace;
				rjsClass.declareMethod(methodName, null);
			}
			String methodDefinition = methods.substring(indexOfArgumentsEnd);
			rjsClass.defineMethod(methodName, (String) RJSDataPool.get(methodDefinition));
		}
	}

	private static boolean needsComputation(String formula) {
		if (formula.trim().length() > 0)
			formula = formula.trim().substring(1);
		return formula.contains(RJSKeyword.DIVIDE.toString()) || formula.contains(RJSKeyword.MULTIPLY.toString())
				|| formula.contains(RJSKeyword.ADD.toString()) || formula.contains(RJSKeyword.SUBTRACT.toString());
	}

	private boolean evaluateCondition(RJSParameter parameter1, RJSParameter parameter2, RJSKeyword evaluation) {
		if (Number.class.isAssignableFrom(parameter1.getObjectType())
				&& Number.class.isAssignableFrom(parameter2.getObjectType())) {
			RJSNumber number1 = new RJSNumber((Number) parameter1.getParameter());
			RJSNumber number2 = new RJSNumber((Number) parameter2.getParameter());
			return number1.evaluate(number2, (RJSKeyword) evaluation);
		} else if (Boolean.class.isAssignableFrom(parameter1.getObjectType())
				&& Boolean.class.isAssignableFrom(parameter2.getObjectType())) {
			boolean boolean1 = (boolean) parameter1.getParameter();
			boolean boolean2 = (boolean) parameter2.getParameter();
			if (evaluation == RJSKeyword.LOGICAL_AND)
				return boolean1 && boolean2;
			else if (evaluation == RJSKeyword.LOGICAL_OR)
				return boolean1 || boolean2;
		} else if (evaluation == RJSKeyword.EQUAL_TO) {
			if (parameter1.getParameter() == null)
				return parameter2.getParameter() == null;
			else if (parameter2.getParameter() == null)
				return parameter1.getParameter() == null;
			else
				return parameter1.getParameter().equals(parameter2.getParameter());
		}
		return false;
	}

	private static Method getMethod(Class<?> classToUse, String methodName, List<Class<?>> parameterTypes)
			throws NoSuchMethodException, SecurityException {
		while (classToUse != null) {
			Method[] methods = (classToUse.isInterface() ? classToUse.getMethods() : classToUse.getDeclaredMethods());
			methodLoop: for (Method method : methods)
				if (methodName.equals(method.getName())) {
					Class<?>[] types = method.getParameterTypes();
					List<Class<?>> typesList = Arrays.asList(types);
					if (typesList.size() == parameterTypes.size()) {
						for (int i = 0; i < typesList.size(); i++) {
							Class<?> type = typesList.get(i);
							if (!type.isAssignableFrom(parameterTypes.get(i))) {
								if (type.isPrimitive()) {
									Class<?> primitiveClass = getPrimitiveClass(parameterTypes.get(i));
									if (primitiveClass == null || !type.isAssignableFrom(primitiveClass))
										continue methodLoop;
								} else
									continue methodLoop;
							}
						}
						return method;
					}
				}
			classToUse = classToUse.getSuperclass();
		}
		return null;

	}

	private static Class<?> getPrimitiveClass(Class<?> objectClass) {
		if (objectClass.equals(Byte.class))
			return Byte.TYPE;
		else if (objectClass.equals(Short.class))
			return Short.TYPE;
		else if (objectClass.equals(Boolean.class))
			return Boolean.TYPE;
		else if (objectClass.equals(Character.class))
			return Character.TYPE;
		else if (objectClass.equals(Integer.class))
			return Integer.TYPE;
		else if (objectClass.equals(Long.class))
			return Long.TYPE;
		else if (objectClass.equals(Float.class))
			return Float.TYPE;
		else if (objectClass.equals(Double.class))
			return Double.TYPE;
		else if (objectClass.equals(Void.class))
			return Void.TYPE;
		else
			return null;
	}

}
