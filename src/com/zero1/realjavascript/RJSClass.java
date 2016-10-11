package com.zero1.realjavascript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class RJSClass {

	private String className;
	private Map<String, Class<?>> fieldDeclarations;
	private Map<String, Object> fieldDefinitions;
	private Map<Integer, LinkedHashMap<String, Class<?>>> methodDeclarations;
	private Map<Integer, String> methodDefinitions;
	private Map<Integer, Map<String, Object>> methodParametersDefinition;
	private Map<String, List<Integer>> methodNameToIdMapping;

	public RJSClass(String className) {
		this.className = className;
		this.fieldDeclarations = new HashMap<>();
		this.fieldDefinitions = new HashMap<>();
		this.methodDeclarations = new HashMap<>();
		this.methodDefinitions = new HashMap<>();
		this.methodParametersDefinition = new HashMap<>();
		this.methodNameToIdMapping = new HashMap<>();
	}

	public RJSClass newInstance() {
		RJSClass newInstance = new RJSClass(this.className);
		newInstance.fieldDeclarations = this.fieldDeclarations;
		newInstance.fieldDefinitions = this.fieldDefinitions;
		newInstance.methodDeclarations = this.methodDeclarations;
		newInstance.methodDefinitions = this.methodDefinitions;
		newInstance.methodParametersDefinition = this.methodParametersDefinition;
		newInstance.methodNameToIdMapping = this.methodNameToIdMapping;
		return newInstance;
	}

	protected void addField(String name, Class<?> type) {
		this.addField(name, null, type);
	}

	protected void addField(String name, Object value, Class<?> type) {
		this.fieldDeclarations.put(name, type);
		if (value != null)
			this.fieldDefinitions.put(name, value);
	}

	protected void setField(String name, Object value) {
		if (fieldDeclarations.get(name).isAssignableFrom(value.getClass()))
			this.fieldDefinitions.put(name, value);
	}

	protected Object getField(String name) {
		if (this.fieldDefinitions.containsKey(name))
			return fieldDefinitions.get(name);
		else
			return null;
	}

	protected Class<?> getFieldType(String name) {
		if (this.fieldDeclarations.containsKey(name))
			return fieldDeclarations.get(name);
		else
			return null;
	}

	protected void declareMethod(String name, LinkedHashMap<String, Class<?>> arguments) {
		List<Integer> methodIds = this.methodNameToIdMapping.get(name);
		if (methodIds == null) {
			methodIds = new ArrayList<>();
			this.methodNameToIdMapping.put(name, methodIds);
		}
		int id = methodIds.size() + 1;
		methodIds.add(id);
		this.methodNameToIdMapping.put(name, methodIds);
		this.methodDeclarations.put(id, arguments);
	}

	protected void defineMethod(String name, String command) {
		List<Integer> methodIds = this.methodNameToIdMapping.get(name);
		this.methodDefinitions.put(methodIds.size(), command);
	}

	protected String getMethod(int methodId) {
		return this.methodDefinitions.get(methodId);
	}

	protected Set<Integer> getMethods() {
		return this.methodDefinitions.keySet();
	}

	protected ArrayList<Class<?>> getMethodParametersTypes(String name) {
		ArrayList<Class<?>> methodParametersList = new ArrayList<>();
		if (this.methodDeclarations.containsKey(name))
			methodParametersList.addAll(this.methodDeclarations.get(name).values());
		return methodParametersList;
	}

	protected void setMethodArguments(String name, Object... value) {
		int methodToExecute = getMethodId(name, value);
		if (methodToExecute != -1) {
			LinkedHashMap<String, Class<?>> methodParameters = this.methodDeclarations.get(methodToExecute);
			List<String> parameters = new ArrayList<>(methodParameters.keySet());
			if (parameters.size() == value.length) {
				Map<String, Object> methodParametersMap;
				if (this.methodParametersDefinition.containsKey(methodToExecute))
					methodParametersMap = this.methodParametersDefinition.get(methodToExecute);
				else {
					methodParametersMap = new HashMap<>();
					this.methodParametersDefinition.put(methodToExecute, methodParametersMap);
				}
				for (int i = 0; i < parameters.size(); i++) {
					String parameterName = parameters.get(i);
					methodParametersMap.put(parameterName, value[i]);
				}
			}
		}
	}

	protected void clearMethodArguments(String name) {
		if (this.methodParametersDefinition.containsKey(name))
			this.methodParametersDefinition.get(name).clear();
	}

	protected Object getMethodArgument(int methodId, String argumentName) {
		Map<String, Object> methodParameters;
		if (this.methodParametersDefinition.containsKey(methodId)
				&& (methodParameters = this.methodParametersDefinition.get(methodId)).containsKey(argumentName))
			return methodParameters.get(argumentName);
		else
			return null;
	}

	protected int getMethodId(String name, Object... value) {
		List<Integer> methodIds = this.methodNameToIdMapping.get(name);
		int methodToExecute = -1;
		if (methodIds != null)
			methodLoop: for (int methodId : methodIds) {
				LinkedHashMap<String, Class<?>> methodParameters = this.methodDeclarations.get(methodId);
				if (value == null || value.length == 0) {
					if (methodParameters != null && !methodParameters.isEmpty())
						continue;
					else {
						methodToExecute = methodId;
						break;
					}
				} else {
					List<Class<?>> parametersTypes = new ArrayList<>(methodParameters.values());
					if (parametersTypes.size() != value.length)
						continue;
					for (int i = 0; i < parametersTypes.size(); i++) {
						if (!parametersTypes.get(i).isAssignableFrom(value[i].getClass()))
							continue methodLoop;
					}
				}
				methodToExecute = methodId;
				break;
			}
		return methodToExecute;
	}

	protected String getMethod(String name, Object... value) {
		return this.methodDefinitions.get(getMethodId(name, value));
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return className + RJSDataPool.getClassIndex(className);
	}

}
