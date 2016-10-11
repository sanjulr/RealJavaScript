package com.zero1.realjavascript;

import java.util.ArrayList;
import java.util.List;

class RJSResult {

	private Object result;
	private int index;
	private ResultType resultType;
	private List<RJSResult> results;
	private Class<?> objectType;

	public enum ResultType {
		VARIABLE, OBJECT, VOID, MULTIPLE, STORED_OBJECT, STRING, COMMAND;
	}

	public RJSResult(Object result, String name) {
		this(result, name, null);
	}

	public RJSResult(Object result, String name, Class<?> castToClass) {
		if (castToClass != null)
			try {
				result = castToClass.cast(result);
			} catch (ClassCastException e) {
				if (castToClass.equals(String.class)) {
					this.result = this.result.toString();
					this.objectType = String.class;
					this.resultType = ResultType.STRING;
				}
			}
		this.result = result;
		this.resultType = ResultType.STORED_OBJECT;
		this.index = RJSDataPool.add(this.result, name);
	}

	public RJSResult(Object result) {
		this(result, ResultType.OBJECT);
	}

	public RJSResult(Object result, ResultType type) {
		this(result, type, null);
	}

	public RJSResult(Object result, Class<?> castToClass) {
		this(result, ResultType.OBJECT, castToClass);
	}

	public RJSResult(Object result, ResultType type, Class<?> castToClass) {
		this.result = result;
		this.resultType = type;
		if (result != null) {
			if (isRJSNumber()) {
				this.result = ((RJSNumber) result).getValue();
				this.objectType = ((RJSNumber) result).getType();
			} else if (result.toString().trim().startsWith(RJSKeyword.STRING_ENCLOSURE.toString())
					&& result.toString().trim().endsWith(RJSKeyword.STRING_ENCLOSURE.toString())) {
				this.objectType = String.class;
				this.result = result.toString().trim().substring(1, result.toString().trim().length() - 1);
				this.resultType = ResultType.STRING;
			} else {
				this.objectType = this.result.getClass();
			}
			if (castToClass != null)
				try {
					this.result = castToClass.cast(this.result);
				} catch (ClassCastException e) {
					if (castToClass.equals(String.class)) {
						this.result = this.result.toString();
						this.objectType = String.class;
						this.resultType = ResultType.STRING;
					}
				}
			this.index = RJSDataPool.add(this.result);
		} else
			this.index = -1;
	}

	public RJSResult(List<RJSResult> results, ResultType type) {
		this.results = results;
		this.resultType = type;
	}

	private boolean isRJSNumber() {
		return result.getClass().equals(RJSNumber.class);
	}

	public Object getResult() {
		return result;
	}

	public List<RJSResult> getResults() {
		return results;
	}

	public int getIndex() {
		return index;
	}

	public Class<?> getObjectType() {
		return objectType;
	}

	public List<Class<?>> getObjectTypes() {
		List<Class<?>> objectTypes = new ArrayList<>();
		if (results != null)
			for (RJSResult result : results)
				objectTypes.add(result.getObjectType());
		return objectTypes;
	}

	public String getParameterString() {
		switch (resultType) {
		case OBJECT:
			return RJSKeyword.WITH_DATA.toString() + RJSKeyword.DATA_POOL_DATA_OPEN_BRACE.toString() + index
					+ RJSKeyword.DATA_POOL_CLOSE_BRACE.toString() + RJSKeyword.SPACE.toString();
		case VARIABLE:
			return RJSKeyword.WITH_DATA.toString() + RJSKeyword.DATA_POOL_DATA_OPEN_BRACE.toString() + index
					+ RJSKeyword.DATA_POOL_CLOSE_BRACE.toString() + RJSKeyword.SPACE.toString();
		case STORED_OBJECT:
			return RJSKeyword.WITH_DATA.toString() + RJSKeyword.DATA_POOL_DATA_OPEN_BRACE.toString() + index
					+ RJSKeyword.DATA_POOL_CLOSE_BRACE.toString() + RJSKeyword.SPACE.toString();
		case VOID:
			return "";
		case MULTIPLE:
			StringBuilder resultParameterString = new StringBuilder();
			for (RJSResult result : results)
				resultParameterString.append(result.getParameterString() + RJSKeyword.COMMA.toString());
			return resultParameterString.substring(0, resultParameterString.length() - 2);
		case STRING:
			return RJSKeyword.WITH_DATA.toString() + RJSKeyword.DATA_POOL_STRING_OPEN_BRACE.toString() + index
					+ RJSKeyword.DATA_POOL_CLOSE_BRACE.toString() + RJSKeyword.SPACE.toString();
		case COMMAND:
			return RJSKeyword.WITH_DATA.toString() + RJSKeyword.DATA_POOL_COMMAND_OPEN_BRACE.toString() + index
					+ RJSKeyword.DATA_POOL_CLOSE_BRACE.toString() + RJSKeyword.SPACE.toString();
		default:
			return RJSKeyword.SPACE.toString() + RJSKeyword.DATA_POOL_DATA_OPEN_BRACE.toString() + index
					+ RJSKeyword.DATA_POOL_CLOSE_BRACE.toString() + RJSKeyword.SPACE.toString();
		}
	}

}
