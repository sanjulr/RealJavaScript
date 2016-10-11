package com.zero1.realjavascript;

class RJSNumber {

	private Integer intValue;
	private Long longValue;
	private Float floatValue;
	private Double doubleValue;

	private Class<? extends Number> type;

	public RJSNumber(Number number) {
		this.type = number.getClass();
		if (type.equals(Integer.class))
			intValue = Integer.parseInt(number.toString());
		else if (type.equals(Long.class))
			longValue = Long.parseLong(number.toString());
		else if (type.equals(Float.class))
			floatValue = Float.parseFloat(number.toString());
		else if (type.equals(Double.class))
			doubleValue = Double.parseDouble(number.toString());
		else
			intValue = 0;
	}

	public RJSNumber(String number) {
		if (isNumber(number)) {
			boolean assigned = false;
			try {
				intValue = Integer.parseInt(number);
				this.type = Integer.class;
				assigned = true;
			} catch (NumberFormatException e) {
				assigned = false;
			}
			if (!assigned)
				try {
					longValue = Long.parseLong(number);
					this.type = Long.class;
					assigned = true;
				} catch (NumberFormatException e) {
					assigned = false;
				}
			if (!assigned)
				try {
					floatValue = Float.parseFloat(number);
					this.type = Float.class;
					assigned = true;
				} catch (NumberFormatException e) {
					assigned = false;
				}
			if (!assigned)
				try {
					doubleValue = Double.parseDouble(number);
					this.type = Double.class;
					assigned = true;
				} catch (NumberFormatException e) {
					assigned = false;
				}
			if (!assigned)
				intValue = 0;
		}
	}

	public RJSNumber getNumber() {
		if (type.equals(Integer.class))
			return new RJSNumber(intValue);
		else if (type.equals(Long.class))
			return new RJSNumber(longValue);
		else if (type.equals(Float.class))
			return new RJSNumber(floatValue);
		else if (type.equals(Double.class))
			return new RJSNumber(doubleValue);
		else
			return new RJSNumber(0);
	}

	public Class<? extends Number> getType() {
		return type;
	}

	public Number getValue() {
		if (type.equals(Integer.class))
			return intValue;
		else if (type.equals(Long.class))
			return longValue;
		else if (type.equals(Float.class))
			return floatValue;
		else if (type.equals(Double.class))
			return doubleValue;
		else
			return 0;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getValue().toString();
	}

	public RJSNumber compute(Number number, RJSKeyword operation) {
		return compute(new RJSNumber(number), operation);
	}

	public RJSNumber add(Number number) {
		return add(new RJSNumber(number));
	}

	public RJSNumber subtract(Number number) {
		return subtract(new RJSNumber(number));
	}

	public RJSNumber multiply(Number number) {
		return multiply(new RJSNumber(number));
	}

	public RJSNumber divide(Number number) {
		return divide(new RJSNumber(number));
	}

	public static RJSNumber compute(Number number1, Number number2, RJSKeyword operation) {
		return new RJSNumber(number1).compute(number2, operation);
	}

	public static RJSNumber compute(RJSNumber number1, RJSNumber number2, RJSKeyword operation) {
		return number1.compute(number2, operation);
	}

	public RJSNumber compute(RJSNumber number, RJSKeyword operation) {
		if (operation.equals(RJSKeyword.ADD.toString()))
			return add(number);
		else if (operation.equals(RJSKeyword.SUBTRACT.toString()))
			return subtract(number);
		else if (operation.equals(RJSKeyword.MULTIPLY.toString()))
			return multiply(number);
		else if (operation.equals(RJSKeyword.DIVIDE.toString()))
			return divide(number);
		else
			return number;
	}

	public boolean evaluate(RJSNumber number, RJSKeyword operation) {
		if (operation.equals(RJSKeyword.GREATER_THAN.toString()))
			return getValue().doubleValue() > number.getValue().doubleValue();
		else if (operation.equals(RJSKeyword.LESSER_THAN.toString()))
			return getValue().doubleValue() < number.getValue().doubleValue();
		else if (operation.equals(RJSKeyword.EQUAL_TO.toString()))
			return getValue().doubleValue() == number.getValue().doubleValue();
		else if (operation.equals(RJSKeyword.NOT_EQUAL_TO.toString()))
			return getValue().doubleValue() != number.getValue().doubleValue();
		else if (operation.equals(RJSKeyword.GREATER_THAN_OR_EQUAL_TO.toString()))
			return getValue().doubleValue() >= number.getValue().doubleValue();
		else if (operation.equals(RJSKeyword.LESSER_THAN_OR_EQUAL_TO.toString()))
			return getValue().doubleValue() <= number.getValue().doubleValue();
		else
			return false;
	}

	public RJSNumber add(RJSNumber number) {
		if (type.equals(Integer.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Integer.parseInt(this.toString()) + Integer.parseInt(number.toString()));
		else if (type.equals(Integer.class) && number.getType().equals(Float.class))
			return new RJSNumber(Integer.parseInt(this.toString()) + Float.parseFloat(number.toString()));
		else if (type.equals(Integer.class) && number.getType().equals(Long.class))
			return new RJSNumber(Integer.parseInt(this.toString()) + Long.parseLong(number.toString()));
		else if (type.equals(Integer.class) && number.getType().equals(Double.class))
			return new RJSNumber(Integer.parseInt(this.toString()) + Double.parseDouble(number.toString()));

		else if (type.equals(Float.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Float.parseFloat(this.toString()) + Integer.parseInt(number.toString()));
		else if (type.equals(Float.class) && number.getType().equals(Float.class))
			return new RJSNumber(Float.parseFloat(this.toString()) + Float.parseFloat(number.toString()));
		else if (type.equals(Float.class) && number.getType().equals(Long.class))
			return new RJSNumber(Float.parseFloat(this.toString()) + Long.parseLong(number.toString()));
		else if (type.equals(Float.class) && number.getType().equals(Double.class))
			return new RJSNumber(Float.parseFloat(this.toString()) + Double.parseDouble(number.toString()));

		else if (type.equals(Long.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Long.parseLong(this.toString()) + Integer.parseInt(number.toString()));
		else if (type.equals(Long.class) && number.getType().equals(Float.class))
			return new RJSNumber(Long.parseLong(this.toString()) + Float.parseFloat(number.toString()));
		else if (type.equals(Long.class) && number.getType().equals(Long.class))
			return new RJSNumber(Long.parseLong(this.toString()) + Long.parseLong(number.toString()));
		else if (type.equals(Long.class) && number.getType().equals(Double.class))
			return new RJSNumber(Long.parseLong(this.toString()) + Double.parseDouble(number.toString()));

		else if (type.equals(Double.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Double.parseDouble(this.toString()) + Integer.parseInt(number.toString()));
		else if (type.equals(Double.class) && number.getType().equals(Float.class))
			return new RJSNumber(Double.parseDouble(this.toString()) + Float.parseFloat(number.toString()));
		else if (type.equals(Double.class) && number.getType().equals(Long.class))
			return new RJSNumber(Double.parseDouble(this.toString()) + Long.parseLong(number.toString()));
		else if (type.equals(Double.class) && number.getType().equals(Double.class))
			return new RJSNumber(Double.parseDouble(this.toString()) + Double.parseDouble(number.toString()));

		else
			return new RJSNumber(0);
	}

	public RJSNumber subtract(RJSNumber number) {
		if (type.equals(Integer.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Integer.parseInt(this.toString()) - Integer.parseInt(number.toString()));
		else if (type.equals(Integer.class) && number.getType().equals(Float.class))
			return new RJSNumber(Integer.parseInt(this.toString()) - Float.parseFloat(number.toString()));
		else if (type.equals(Integer.class) && number.getType().equals(Long.class))
			return new RJSNumber(Integer.parseInt(this.toString()) - Long.parseLong(number.toString()));
		else if (type.equals(Integer.class) && number.getType().equals(Double.class))
			return new RJSNumber(Integer.parseInt(this.toString()) - Double.parseDouble(number.toString()));

		else if (type.equals(Float.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Float.parseFloat(this.toString()) - Integer.parseInt(number.toString()));
		else if (type.equals(Float.class) && number.getType().equals(Float.class))
			return new RJSNumber(Float.parseFloat(this.toString()) - Float.parseFloat(number.toString()));
		else if (type.equals(Float.class) && number.getType().equals(Long.class))
			return new RJSNumber(Float.parseFloat(this.toString()) - Long.parseLong(number.toString()));
		else if (type.equals(Float.class) && number.getType().equals(Double.class))
			return new RJSNumber(Float.parseFloat(this.toString()) - Double.parseDouble(number.toString()));

		else if (type.equals(Long.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Long.parseLong(this.toString()) - Integer.parseInt(number.toString()));
		else if (type.equals(Long.class) && number.getType().equals(Float.class))
			return new RJSNumber(Long.parseLong(this.toString()) - Float.parseFloat(number.toString()));
		else if (type.equals(Long.class) && number.getType().equals(Long.class))
			return new RJSNumber(Long.parseLong(this.toString()) - Long.parseLong(number.toString()));
		else if (type.equals(Long.class) && number.getType().equals(Double.class))
			return new RJSNumber(Long.parseLong(this.toString()) - Double.parseDouble(number.toString()));

		else if (type.equals(Double.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Double.parseDouble(this.toString()) - Integer.parseInt(number.toString()));
		else if (type.equals(Double.class) && number.getType().equals(Float.class))
			return new RJSNumber(Double.parseDouble(this.toString()) - Float.parseFloat(number.toString()));
		else if (type.equals(Double.class) && number.getType().equals(Long.class))
			return new RJSNumber(Double.parseDouble(this.toString()) - Long.parseLong(number.toString()));
		else if (type.equals(Double.class) && number.getType().equals(Double.class))
			return new RJSNumber(Double.parseDouble(this.toString()) - Double.parseDouble(number.toString()));

		else
			return new RJSNumber(0);
	}

	public RJSNumber multiply(RJSNumber number) {
		if (type.equals(Integer.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Integer.parseInt(this.toString()) * Integer.parseInt(number.toString()));
		else if (type.equals(Integer.class) && number.getType().equals(Float.class))
			return new RJSNumber(Integer.parseInt(this.toString()) * Float.parseFloat(number.toString()));
		else if (type.equals(Integer.class) && number.getType().equals(Long.class))
			return new RJSNumber(Integer.parseInt(this.toString()) * Long.parseLong(number.toString()));
		else if (type.equals(Integer.class) && number.getType().equals(Double.class))
			return new RJSNumber(Integer.parseInt(this.toString()) * Double.parseDouble(number.toString()));

		else if (type.equals(Float.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Float.parseFloat(this.toString()) * Integer.parseInt(number.toString()));
		else if (type.equals(Float.class) && number.getType().equals(Float.class))
			return new RJSNumber(Float.parseFloat(this.toString()) * Float.parseFloat(number.toString()));
		else if (type.equals(Float.class) && number.getType().equals(Long.class))
			return new RJSNumber(Float.parseFloat(this.toString()) * Long.parseLong(number.toString()));
		else if (type.equals(Float.class) && number.getType().equals(Double.class))
			return new RJSNumber(Float.parseFloat(this.toString()) * Double.parseDouble(number.toString()));

		else if (type.equals(Long.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Long.parseLong(this.toString()) * Integer.parseInt(number.toString()));
		else if (type.equals(Long.class) && number.getType().equals(Float.class))
			return new RJSNumber(Long.parseLong(this.toString()) * Float.parseFloat(number.toString()));
		else if (type.equals(Long.class) && number.getType().equals(Long.class))
			return new RJSNumber(Long.parseLong(this.toString()) * Long.parseLong(number.toString()));
		else if (type.equals(Long.class) && number.getType().equals(Double.class))
			return new RJSNumber(Long.parseLong(this.toString()) * Double.parseDouble(number.toString()));

		else if (type.equals(Double.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Double.parseDouble(this.toString()) * Integer.parseInt(number.toString()));
		else if (type.equals(Double.class) && number.getType().equals(Float.class))
			return new RJSNumber(Double.parseDouble(this.toString()) * Float.parseFloat(number.toString()));
		else if (type.equals(Double.class) && number.getType().equals(Long.class))
			return new RJSNumber(Double.parseDouble(this.toString()) * Long.parseLong(number.toString()));
		else if (type.equals(Double.class) && number.getType().equals(Double.class))
			return new RJSNumber(Double.parseDouble(this.toString()) * Double.parseDouble(number.toString()));

		else
			return new RJSNumber(0);
	}

	public RJSNumber divideOld(RJSNumber number) {
		if (type.equals(Integer.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Integer.parseInt(this.toString()) / Integer.parseInt(number.toString()));
		else if (type.equals(Integer.class) && number.getType().equals(Float.class))
			return new RJSNumber(Integer.parseInt(this.toString()) / Float.parseFloat(number.toString()));
		else if (type.equals(Integer.class) && number.getType().equals(Long.class))
			return new RJSNumber(Integer.parseInt(this.toString()) / Long.parseLong(number.toString()));
		else if (type.equals(Integer.class) && number.getType().equals(Double.class))
			return new RJSNumber(Integer.parseInt(this.toString()) / Double.parseDouble(number.toString()));

		else if (type.equals(Float.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Float.parseFloat(this.toString()) / Integer.parseInt(number.toString()));
		else if (type.equals(Float.class) && number.getType().equals(Float.class))
			return new RJSNumber(Float.parseFloat(this.toString()) / Float.parseFloat(number.toString()));
		else if (type.equals(Float.class) && number.getType().equals(Long.class))
			return new RJSNumber(Float.parseFloat(this.toString()) / Long.parseLong(number.toString()));
		else if (type.equals(Float.class) && number.getType().equals(Double.class))
			return new RJSNumber(Float.parseFloat(this.toString()) / Double.parseDouble(number.toString()));

		else if (type.equals(Long.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Long.parseLong(this.toString()) / Integer.parseInt(number.toString()));
		else if (type.equals(Long.class) && number.getType().equals(Float.class))
			return new RJSNumber(Long.parseLong(this.toString()) / Float.parseFloat(number.toString()));
		else if (type.equals(Long.class) && number.getType().equals(Long.class))
			return new RJSNumber(Long.parseLong(this.toString()) / Long.parseLong(number.toString()));
		else if (type.equals(Long.class) && number.getType().equals(Double.class))
			return new RJSNumber(Long.parseLong(this.toString()) / Double.parseDouble(number.toString()));

		else if (type.equals(Double.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Double.parseDouble(this.toString()) / Integer.parseInt(number.toString()));
		else if (type.equals(Double.class) && number.getType().equals(Float.class))
			return new RJSNumber(Double.parseDouble(this.toString()) / Float.parseFloat(number.toString()));
		else if (type.equals(Double.class) && number.getType().equals(Long.class))
			return new RJSNumber(Double.parseDouble(this.toString()) / Long.parseLong(number.toString()));
		else if (type.equals(Double.class) && number.getType().equals(Double.class))
			return new RJSNumber(Double.parseDouble(this.toString()) / Double.parseDouble(number.toString()));

		else
			return new RJSNumber(0);
	}

	public RJSNumber divide(RJSNumber number) {
		RJSNumber result = new RJSNumber(0);
		if (type.equals(Integer.class) && number.getType().equals(Integer.class))
			result = new RJSNumber(Integer.parseInt(this.toString()) / Float.parseFloat(number.toString()));
		else if (type.equals(Integer.class) && number.getType().equals(Float.class))
			return new RJSNumber(Integer.parseInt(this.toString()) / Float.parseFloat(number.toString()));
		else if (type.equals(Integer.class) && number.getType().equals(Long.class))
			result = new RJSNumber(Float.parseFloat(this.toString()) / Long.parseLong(number.toString()));
		else if (type.equals(Integer.class) && number.getType().equals(Double.class))
			return new RJSNumber(Integer.parseInt(this.toString()) / Double.parseDouble(number.toString()));

		else if (type.equals(Float.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Float.parseFloat(this.toString()) / Integer.parseInt(number.toString()));
		else if (type.equals(Float.class) && number.getType().equals(Float.class))
			return new RJSNumber(Float.parseFloat(this.toString()) / Float.parseFloat(number.toString()));
		else if (type.equals(Float.class) && number.getType().equals(Long.class))
			return new RJSNumber(Float.parseFloat(this.toString()) / Long.parseLong(number.toString()));
		else if (type.equals(Float.class) && number.getType().equals(Double.class))
			return new RJSNumber(Float.parseFloat(this.toString()) / Double.parseDouble(number.toString()));

		else if (type.equals(Long.class) && number.getType().equals(Integer.class))
			result = new RJSNumber(Long.parseLong(this.toString()) / Float.parseFloat(number.toString()));
		else if (type.equals(Long.class) && number.getType().equals(Float.class))
			return new RJSNumber(Long.parseLong(this.toString()) / Float.parseFloat(number.toString()));
		else if (type.equals(Long.class) && number.getType().equals(Long.class))
			result = new RJSNumber(Long.parseLong(this.toString()) / Double.parseDouble(number.toString()));
		else if (type.equals(Long.class) && number.getType().equals(Double.class))
			return new RJSNumber(Long.parseLong(this.toString()) / Double.parseDouble(number.toString()));

		else if (type.equals(Double.class) && number.getType().equals(Integer.class))
			return new RJSNumber(Double.parseDouble(this.toString()) / Integer.parseInt(number.toString()));
		else if (type.equals(Double.class) && number.getType().equals(Float.class))
			return new RJSNumber(Double.parseDouble(this.toString()) / Float.parseFloat(number.toString()));
		else if (type.equals(Double.class) && number.getType().equals(Long.class))
			return new RJSNumber(Double.parseDouble(this.toString()) / Long.parseLong(number.toString()));
		else if (type.equals(Double.class) && number.getType().equals(Double.class))
			return new RJSNumber(Double.parseDouble(this.toString()) / Double.parseDouble(number.toString()));
		if (!isDecimalNumber(result))
			result = getNonDecimalNumber(result);
		return result;

	}

	private static boolean isDecimalNumber(RJSNumber number) {
		String numberInString = number.toString();
		if (numberInString.contains(RJSKeyword.DOT.toString()))
			if (Long.parseLong(numberInString.split("\\.")[1]) != 0)
				return true;
		return false;
	}

	private static RJSNumber getNonDecimalNumber(RJSNumber number) {
		String numberInString = number.toString();
		if (numberInString.contains(RJSKeyword.DOT.toString()))
			numberInString = numberInString.split("\\.")[0];
		return new RJSNumber(numberInString);
	}

	protected static boolean isNumber(String number) {
		try {
			Double.parseDouble(number);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
