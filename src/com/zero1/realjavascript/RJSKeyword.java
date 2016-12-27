package com.zero1.realjavascript;

class RJSKeyword {

	protected static final RJSKeyword CALL = new RJSKeyword(" CALL ");
	protected static final RJSKeyword WITH = new RJSKeyword(" WITH ");
	protected static final RJSKeyword AND = new RJSKeyword(" AND ");
	protected static final RJSKeyword ADD = new RJSKeyword("+");
	protected static final RJSKeyword SUBTRACT = new RJSKeyword("-");
	protected static final RJSKeyword MULTIPLY = new RJSKeyword("*");
	protected static final RJSKeyword DIVIDE = new RJSKeyword("/");
	protected static final RJSKeyword COMPUTE = new RJSKeyword(" COMPUTE ");
	protected static final RJSKeyword TO = new RJSKeyword(" TO ");
	protected static final RJSKeyword WITH_DATA = new RJSKeyword(" WITH_DATA ");
	protected static final RJSKeyword CREATE = new RJSKeyword(" CREATE ");
	protected static final RJSKeyword ASSIGN = new RJSKeyword(" ASSIGN ");
	protected static final RJSKeyword INNER_COMMAND_OPEN = new RJSKeyword("(");
	protected static final RJSKeyword INNER_COMMAND_CLOSE = new RJSKeyword(")");
	protected static final RJSKeyword STRING_ENCLOSURE = new RJSKeyword("\"");
	protected static final RJSKeyword DOT = new RJSKeyword(".");
	protected static final RJSKeyword COMMA = new RJSKeyword(",");
	protected static final RJSKeyword OBJECT_ACCESS = new RJSKeyword("&");
	protected static final RJSKeyword DATA_POOL_OPEN_BRACE = new RJSKeyword("[");
	protected static final RJSKeyword DATA_POOL_CLOSE_BRACE = new RJSKeyword("]");
	protected static final RJSKeyword DATA_POOL_DATA_OPEN_BRACE = new RJSKeyword("data[");
	protected static final RJSKeyword SPACE = new RJSKeyword(" ");
	protected static final RJSKeyword IF = new RJSKeyword(" IF ");
	protected static final RJSKeyword THEN = new RJSKeyword(" THEN ");
	protected static final RJSKeyword ELSE = new RJSKeyword(" ELSE ");
	protected static final RJSKeyword FUTURE_COMMAND_OPEN_BRACE = new RJSKeyword("{");
	protected static final RJSKeyword FUTURE_COMMAND_CLOSE_BRACE = new RJSKeyword("}");
	protected static final RJSKeyword THIS = new RJSKeyword("this");
	protected static final RJSKeyword CLASS = new RJSKeyword(" CLASS ");
	protected static final RJSKeyword FIELDS = new RJSKeyword("FIELDS");
	protected static final RJSKeyword METHODS = new RJSKeyword("METHODS");
	protected static final RJSKeyword EQUAL = new RJSKeyword("=");
	protected static final RJSKeyword CLASS_FIELDS_OPEN = new RJSKeyword("<?");
	protected static final RJSKeyword CLASS_FIELDS_CLOSE = new RJSKeyword("?>");
	protected static final RJSKeyword CLASS_METHODS_OPEN = new RJSKeyword("<?");
	protected static final RJSKeyword CLASS_METHODS_CLOSE = new RJSKeyword("?>");
	protected static final RJSKeyword CLASS_METHOD_ARGUMENT_OPEN = new RJSKeyword("<@");
	protected static final RJSKeyword CLASS_METHOD_ARGUMENT_CLOSE = new RJSKeyword("@>");
	protected static final RJSKeyword SEMI_COLON = new RJSKeyword(";");
	protected static final RJSKeyword METHOD_ARGUMENT_ACCESS = new RJSKeyword("$");
	protected static final RJSKeyword ARRAY_ACCESS_OPEN = new RJSKeyword("<");
	protected static final RJSKeyword ARRAY_ACCESS_CLOSE = new RJSKeyword(">");
	protected static final RJSKeyword LOOP = new RJSKeyword(" LOOP ");
	protected static final RJSKeyword LOOP_RANGE_DOTS = new RJSKeyword("..");
	protected static final RJSKeyword LOOP_MAX_LENGTH = new RJSKeyword("l");
	protected static final RJSKeyword LOOP_INDEX = new RJSKeyword("#");
	protected static final RJSKeyword DATA_POOL_STRING_OPEN_BRACE = new RJSKeyword("STRING data[");
	protected static final RJSKeyword IS = new RJSKeyword(" IS ");
	protected static final RJSKeyword GREATER_THAN = new RJSKeyword(">");
	protected static final RJSKeyword LESSER_THAN = new RJSKeyword("<");
	protected static final RJSKeyword EQUAL_TO = new RJSKeyword("==");
	protected static final RJSKeyword NOT_EQUAL_TO = new RJSKeyword("!=");
	protected static final RJSKeyword GREATER_THAN_OR_EQUAL_TO = new RJSKeyword(">=");
	protected static final RJSKeyword LESSER_THAN_OR_EQUAL_TO = new RJSKeyword("<=");
	protected static final RJSKeyword LOGICAL_NOT = new RJSKeyword("!");
	protected static final RJSKeyword VAR = new RJSKeyword(" VAR ");
	protected static final RJSKeyword VAR_ACCESS = new RJSKeyword("$");
	protected static final RJSKeyword DATA_POOL_COMMAND_OPEN_BRACE = new RJSKeyword("COMMAND data[");
	protected static final RJSKeyword BOOLEAN_FALSE = new RJSKeyword("false");
	protected static final RJSKeyword BOOLEAN_TRUE = new RJSKeyword("true");
	protected static final RJSKeyword ARRAY_SPLITTER = new RJSKeyword(":");
	protected static final RJSKeyword LOGICAL_AND = new RJSKeyword("&&");
	protected static final RJSKeyword LOGICAL_OR = new RJSKeyword("||");
	protected static final RJSKeyword NULL = new RJSKeyword("null");
	protected static final RJSKeyword RESULT_APPEND = new RJSKeyword("+");
	protected static final RJSKeyword AS = new RJSKeyword(" AS ");
	protected static final RJSKeyword INTERFACE_METHODS_OPEN = new RJSKeyword("<?");
	protected static final RJSKeyword INTERFACE_METHODS_CLOSE = new RJSKeyword("?>");

	private String key;

	private RJSKeyword(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return key;
	}

	public char toChar() {
		return key.charAt(0);
	}

	public int length() {
		return key.length();
	}

	@Override
	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}

}
