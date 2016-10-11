package com.zero1.realjavascript;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

class RJSParameter {

	private Object parameter;
	private Class<?> objectType;
	private ParameterType parameterType;

	public enum ParameterType {
		DATA_POOL, VARIALBLE, OBJECT, ARRAY, FOR_ACCESS;
	}

	public RJSParameter(Object from, Object parameter, ParameterType parameterType) {
		this.parameter = parameter;
		this.parameterType = parameterType;
		if (parameter != null) {
			this.objectType = parameter.getClass();
			if (parameterType != ParameterType.FOR_ACCESS) {
				String trimmedParameter = parameter.toString().trim();
				if (trimmedParameter.equals(RJSKeyword.NULL.toString())) {
					this.parameter = null;
					this.objectType = null;
				} else if (RJSNumber.isNumber(trimmedParameter)) {
					RJSNumber number = new RJSNumber(trimmedParameter);
					RJSResult result = new RJSResult(number);
					this.parameterType = ParameterType.OBJECT;
					this.objectType = result.getObjectType();
					this.parameter = result.getResult();
				} else if (isBooleanAccess(trimmedParameter)) {
					boolean not = trimmedParameter.charAt(0) == RJSKeyword.LOGICAL_NOT.toChar();
					RJSResult result = new RJSResult(
							trimmedParameter.substring(not ? RJSKeyword.LOGICAL_NOT.length() : 0));
					this.parameterType = ParameterType.OBJECT;
					boolean resultObject = Boolean.parseBoolean(result.getResult().toString());
					if (not)
						resultObject = !resultObject;
					this.objectType = Boolean.class;
					this.parameter = resultObject;
				} else if (isObjectAccess(trimmedParameter)) {
					if (isArrayAccess(trimmedParameter) || (trimmedParameter.charAt(0) == RJSKeyword.SUBTRACT.toChar()
							&& isArrayAccess(trimmedParameter.substring(1))))
						this.parameterType = ParameterType.ARRAY;
					else {
						boolean negate = trimmedParameter.charAt(0) == RJSKeyword.SUBTRACT.toChar();
						boolean not = trimmedParameter.charAt(0) == RJSKeyword.LOGICAL_NOT.toChar();
						int indexOfVariableName = RJSKeyword.OBJECT_ACCESS.length() + ((negate || not) ? 1 : 0);
						String variable = trimmedParameter.substring(indexOfVariableName);
						RJSResult result = new RJSResult(RJSDataPool.getByName(variable));
						if (result == null || result.getResult() == null) {
							int indexOfSpace = variable.indexOf(RJSKeyword.SPACE.toString());
							String methodName;
							if (indexOfSpace == -1)
								methodName = variable;
							else
								methodName = variable.substring(0, indexOfSpace);
							if (RJSDataPool.hasMethod(methodName)) {
								try {
									result = new RJSCommand(RJSKeyword.CALL.toString() + trimmedParameter)
											.execute(from);
								} catch (Exception e) {
								}
							}
						}
						Object resultObject = result.getResult();
						if (resultObject == null) {
							if (from != null) {
								try {
									Field field;
									if (from instanceof Class)
										field = ((Class<?>) from).getDeclaredField(variable);
									else
										field = from.getClass().getDeclaredField(variable);
									if (field != null) {
										this.objectType = field.getType();
										this.parameter = null;
									}
								} catch (NoSuchFieldException | SecurityException e) {
									// TODO Auto-generated catch block
									this.objectType = Object.class;
									this.parameter = null;
								}
							}
						} else {
							if (resultObject.toString().contains(RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString())) {
								try {
									List<RJSResult> results = RJSParser.parse(from,
											RJSDataPool.get(resultObject.toString()).toString());
									resultObject = results.get(results.size() - 1).getResult();
								} catch (Exception e) {
								}
							}
							if (negate) {
								resultObject = new RJSNumber(resultObject.toString().trim()).multiply(-1).getValue();
							} else if (not)
								resultObject = !(Boolean.parseBoolean(resultObject.toString()));
							if (this.parameterType != ParameterType.FOR_ACCESS)
								this.parameterType = ParameterType.OBJECT;
							this.objectType = result.getObjectType();
							this.parameter = resultObject;
						}
					}
				} else if (objectType.isAssignableFrom(String.class)) {
					if (isArrayAccess(trimmedParameter) || (trimmedParameter.charAt(0) == RJSKeyword.SUBTRACT.toChar()
							&& isArrayAccess(trimmedParameter.substring(1)))) {
						this.parameterType = ParameterType.ARRAY;
					} else if (isDataPoolParameter(parameter.toString())) {
						setParameterFromDataPool();
						this.parameterType = ParameterType.DATA_POOL;
					} else if (isVariable(trimmedParameter)
							|| (trimmedParameter.charAt(0) == RJSKeyword.SUBTRACT.toChar()
									&& isVariable(trimmedParameter.substring(RJSKeyword.SUBTRACT.length())))) {
						this.parameterType = ParameterType.VARIALBLE;
					} else if (isVariable(trimmedParameter)
							|| (trimmedParameter.charAt(0) == RJSKeyword.LOGICAL_NOT.toChar()
									&& isVariable(trimmedParameter.substring(RJSKeyword.LOGICAL_NOT.length())))) {
						this.parameterType = ParameterType.VARIALBLE;
					} else if (trimmedParameter.charAt(0) == RJSKeyword.OBJECT_ACCESS.toChar()
							|| (trimmedParameter.charAt(1) == RJSKeyword.OBJECT_ACCESS.toChar()
									&& (trimmedParameter.charAt(0) == RJSKeyword.SUBTRACT.toChar()
											|| trimmedParameter.charAt(0) == RJSKeyword.LOGICAL_NOT.toChar()))) {
						this.parameterType = ParameterType.FOR_ACCESS;
					} else {
						RJSResult result = staticAccess(parameter.toString());
						if (result != null) {
							this.parameter = result.getResult();
							this.objectType = result.getObjectType();
							this.parameterType = ParameterType.OBJECT;
						}
					}
				}
			}
		}
	}

	public RJSParameter(Object parameter) {
		this(null, parameter);
	}

	public RJSParameter(Object from, Object parameter) {
		this(from, parameter, null);
	}

	public RJSParameter(Class<?> objectType) {
		this.objectType = objectType;
	}

	private void setParameterFromDataPool() {
		String parameterString = this.parameter.toString();
		List<Object> objects = new ArrayList<>();
		int indexOfLastAccess = 0;
		while (parameterString.indexOf(RJSKeyword.WITH_DATA.toString(), indexOfLastAccess) != -1) {
			int indexOfDataPoolStart = parameterString.indexOf(RJSKeyword.WITH_DATA.toString(), indexOfLastAccess);
			int indexOfDataPoolEnd = parameterString.indexOf(RJSKeyword.DATA_POOL_CLOSE_BRACE.toString(),
					indexOfDataPoolStart) + RJSKeyword.DATA_POOL_CLOSE_BRACE.length();
			indexOfLastAccess = indexOfDataPoolEnd;
			String dataPoolString = parameterString.substring(indexOfDataPoolStart, indexOfDataPoolEnd);
			objects.add(RJSDataPool.get(dataPoolString));
		}
		boolean returnAsString = false;
		for (Object object : objects) {
			if (object instanceof String)
				returnAsString = true;
		}
		if (returnAsString) {
			StringBuilder stringBuilder = new StringBuilder();
			for (Object object : objects)
				stringBuilder.append(object.toString());
			this.parameter = stringBuilder.toString();
		}
		if (objects.size() == 1)
			this.parameter = objects.get(0);

		this.objectType = this.parameter.getClass();
	}

	protected static boolean isDataPoolParameter(String parameter) {
		return Pattern.compile(Pattern.quote(RJSKeyword.WITH_DATA.toString()), Pattern.CASE_INSENSITIVE)
				.matcher(parameter).find();
	}

	protected static boolean isVariable(String parameter) {
		return Pattern.compile("^[a-zA-Z_$][a-zA-Z_$0-9]*$", Pattern.CASE_INSENSITIVE)
				.matcher(parameter.toString().trim()).find();
	}

	protected static boolean isArrayAccess(String parameter) {
		int length = parameter.length();
		if (parameter.contains(RJSKeyword.ARRAY_ACCESS_OPEN.toString()))
			return ((length - parameter.replace(RJSKeyword.ARRAY_ACCESS_OPEN.toString(), "").length()) == (length
					- parameter.replace(RJSKeyword.ARRAY_ACCESS_CLOSE.toString(), "").length()));
		else
			return false;
	}

	protected static boolean isStringAccess(String parameter) {
		if (parameter.contains(RJSKeyword.WITH_DATA.toString() + RJSKeyword.DATA_POOL_STRING_OPEN_BRACE.toString())) {
			int indexOfWithData = parameter.indexOf(RJSKeyword.WITH_DATA.toString());
			int indexOfEndData = parameter.indexOf(RJSKeyword.DATA_POOL_CLOSE_BRACE.toString())
					+ RJSKeyword.SPACE.length();
			return new StringBuilder(parameter).replace(indexOfWithData, indexOfEndData, "").toString().trim()
					.length() == 0;
		} else
			return false;
	}

	private RJSResult staticAccess(String parameter) {
		try {
			return new RJSCommand(parameter).execute(RJSParameter.class);
		} catch (Exception e) {
		}
		return null;
	}

	protected static boolean isBooleanAccess(String parameter) {
		return (parameter.equals(RJSKeyword.BOOLEAN_FALSE.toString())
				|| parameter.equals(RJSKeyword.BOOLEAN_TRUE.toString()))
				|| (parameter.charAt(0) == RJSKeyword.LOGICAL_NOT.toChar()
						&& (parameter.substring(1).equals(RJSKeyword.BOOLEAN_FALSE.toString())
								|| parameter.substring(1).equals(RJSKeyword.BOOLEAN_TRUE.toString())));
	}

	protected static boolean isObjectAccess(String parameter) {
		if (!parameter.contains(RJSKeyword.DOT.toString())) {
			if (parameter.charAt(0) == RJSKeyword.SUBTRACT.toChar()
					|| parameter.charAt(0) == RJSKeyword.LOGICAL_NOT.toChar()) {
				if (parameter.charAt(1) == RJSKeyword.OBJECT_ACCESS.toChar())
					return true;
				else
					return false;
			} else if (parameter.charAt(0) == RJSKeyword.OBJECT_ACCESS.toChar())
				return true;
			else
				return false;
		}
		return false;
	}

	public Object getParameter() {
		return parameter;
	}

	public Class<?> getObjectType() {
		return objectType;
	}

	public ParameterType getParameterType() {
		return parameterType;
	}

	public static List<Class<?>> getParameterTypes(List<RJSParameter> parameters) {
		List<Class<?>> parameterTypes = new ArrayList<>();
		for (RJSParameter parameter : parameters)
			parameterTypes.add(parameter.getObjectType());
		return parameterTypes;
	}

	public static List<Object> getParameters(List<RJSParameter> parameters) {
		List<Object> parameterTypes = new ArrayList<>();
		for (RJSParameter parameter : parameters)
			parameterTypes.add(parameter.getParameter());
		return parameterTypes;
	}

}