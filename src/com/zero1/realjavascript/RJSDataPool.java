package com.zero1.realjavascript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class RJSDataPool {

	private static List<Object> data = new ArrayList<>();
	private static Map<String, Integer> objectNameMap = new HashMap<>();
	private static Map<Map<String, Integer>, LinkedHashMap<String, Class<?>>> methodDeclarations = new HashMap<>();
	private static Map<Map<String, Integer>, String> methodDefinitions = new HashMap<>();
	private static Map<Integer, Map<String, Object>> methodParametersDefinition = new HashMap<>();
	private static Map<String, List<Integer>> methodNameToIdMapping = new HashMap<>();
	private static HashMap<String, RJSClass> rjsClassMap;

	protected static void clear() {
		RJSDataPool.data.clear();
		RJSDataPool.objectNameMap.clear();
		RJSDataPool.methodDeclarations.clear();
		RJSDataPool.methodDefinitions.clear();
		RJSDataPool.methodParametersDefinition.clear();
		RJSDataPool.methodNameToIdMapping.clear();
	}

	protected static int add(Object data) {
		return RJSDataPool.add(data, null);
	}

	protected static void addAt(Object data, int index) {
		RJSDataPool.addAt(data, index, null);
	}

	protected static int add(Object data, String mapName) {
		if (!RJSDataPool.data.contains(data))
			RJSDataPool.data.add(data);
		int index = RJSDataPool.data.indexOf(data);
		if (mapName != null && !mapName.trim().isEmpty())
			RJSDataPool.objectNameMap.put(mapName, index);
		return index;
	}

	protected static void addAt(Object data, int index, String mapName) {
		RJSDataPool.data.add(index, data);
		if (mapName != null && !mapName.trim().isEmpty())
			RJSDataPool.objectNameMap.put(mapName, index);
	}

	protected static void remove(Object data) {
		RJSDataPool.data.remove(data);
	}

	protected static void remove(int index) {
		RJSDataPool.data.remove(index);
	}

	protected static void removeByName(String name) {
		int index = getIndexByName(name);
		RJSDataPool.data.remove(index);
		RJSDataPool.objectNameMap.remove(name);
	}

	protected static Object get(int index) {
		return RJSDataPool.data.get(index);
	}

	protected static Object get(String dataPoolParameter) {
		int index = Integer.parseInt(
				dataPoolParameter.substring(dataPoolParameter.indexOf(RJSKeyword.DATA_POOL_OPEN_BRACE.toString()) + 1,
						dataPoolParameter.indexOf(RJSKeyword.DATA_POOL_CLOSE_BRACE.toString())));
		return RJSDataPool.get(index);
	}

	protected static Object getByName(String name) {
		Integer index = RJSDataPool.objectNameMap.get(name);
		if (index != null && index != -1)
			return RJSDataPool.get(index);
		else
			return null;
	}

	protected static int getIndex(Object data) {
		if (RJSDataPool.data.contains(data))
			return RJSDataPool.getIndex(data);
		else
			return -1;
	}

	protected static int getIndexByName(String name) {
		Integer index = RJSDataPool.objectNameMap.get(name);
		if (index != null && index != -1)
			return index;
		else
			return -1;
	}

	protected static String getPrintableDataPool() {
		return RJSDataPool.data.toString();
	}

	protected static String getPrintableDataNameMap() {
		return RJSDataPool.objectNameMap.toString();
	}

	protected static void addRJSClass(String className, RJSClass rjsClass) {
		if (RJSDataPool.rjsClassMap == null)
			RJSDataPool.rjsClassMap = new HashMap<>();
		RJSDataPool.rjsClassMap.put(className, rjsClass);
	}

	protected static boolean hasClass(String className) {
		if (RJSDataPool.rjsClassMap != null && !RJSDataPool.rjsClassMap.isEmpty())
			return RJSDataPool.rjsClassMap.containsKey(className);
		else
			return false;
	}

	protected static RJSClass getClass(String className) {
		if (RJSDataPool.rjsClassMap != null && !RJSDataPool.rjsClassMap.isEmpty())
			return RJSDataPool.rjsClassMap.get(className);
		else
			return null;
	}

	protected static int getClassIndex(String className) {
		if (RJSDataPool.rjsClassMap != null && !RJSDataPool.rjsClassMap.isEmpty())
			return new ArrayList<>(RJSDataPool.rjsClassMap.keySet()).indexOf(className);
		else
			return -1;
	}

	protected static void declareMethod(String name, LinkedHashMap<String, Class<?>> arguments) {
		List<Integer> methodIds = RJSDataPool.methodNameToIdMapping.get(name);
		if (methodIds == null) {
			methodIds = new ArrayList<>();
			RJSDataPool.methodNameToIdMapping.put(name, methodIds);
		}
		int id = methodIds.size() + 1;
		methodIds.add(id);
		Map<String, Integer> methodNameToIdMapping = new HashMap<>();
		methodNameToIdMapping.put(name, id);
		RJSDataPool.methodNameToIdMapping.put(name, methodIds);
		RJSDataPool.methodDeclarations.put(methodNameToIdMapping, arguments);
	}

	protected static void defineMethod(String methodName, String command) {
		List<Integer> methodIds = RJSDataPool.methodNameToIdMapping.get(methodName);
		Map<String, Integer> methodNameToIdMapping = new HashMap<>();
		methodNameToIdMapping.put(methodName, methodIds.size());
		RJSDataPool.methodDefinitions.put(methodNameToIdMapping, command);
	}

	protected static String getMethod(String methodName, int methodId) {
		Map<String, Integer> methodNameToIdMapping = new HashMap<>();
		methodNameToIdMapping.put(methodName, methodId);
		return RJSDataPool.methodDefinitions.get(methodNameToIdMapping);
	}

	protected static Set<Map<String, Integer>> getMethods() {
		return RJSDataPool.methodDefinitions.keySet();
	}

	protected static ArrayList<Class<?>> getMethodParametersTypes(String methodName, int methodId) {
		ArrayList<Class<?>> methodParametersList = new ArrayList<>();
		Map<String, Integer> methodNameToIdMapping = new HashMap<>();
		methodNameToIdMapping.put(methodName, methodId);
		if (RJSDataPool.methodDeclarations.containsKey(methodNameToIdMapping))
			methodParametersList.addAll(RJSDataPool.methodDeclarations.get(methodNameToIdMapping).values());
		return methodParametersList;
	}

	protected static void setMethodArguments(String methodName, int methodId, Object... value) {
		if (methodId != -1) {
			Map<String, Integer> methodNameToIdMapping = new HashMap<>();
			methodNameToIdMapping.put(methodName, methodId);
			LinkedHashMap<String, Class<?>> methodParameters = RJSDataPool.methodDeclarations
					.get(methodNameToIdMapping);
			List<String> parameters = new ArrayList<>(methodParameters.keySet());
			if (parameters.size() == value.length) {
				Map<String, Object> methodParametersMap;
				if (RJSDataPool.methodParametersDefinition.containsKey(methodId))
					methodParametersMap = RJSDataPool.methodParametersDefinition.get(methodId);
				else {
					methodParametersMap = new HashMap<>();
					RJSDataPool.methodParametersDefinition.put(methodId, methodParametersMap);
				}
				for (int i = 0; i < parameters.size(); i++) {
					String parameterName = parameters.get(i);
					methodParametersMap.put(parameterName, value[i]);
				}
			}
		}
	}

	protected static void clearMethodArguments(String name) {
		if (RJSDataPool.methodParametersDefinition.containsKey(name))
			RJSDataPool.methodParametersDefinition.get(name).clear();
	}

	protected static Object getMethodArgument(int methodId, String argumentName) {
		Map<String, Object> methodParameters;
		if (RJSDataPool.methodParametersDefinition.containsKey(methodId)
				&& (methodParameters = RJSDataPool.methodParametersDefinition.get(methodId)).containsKey(argumentName))
			return methodParameters.get(argumentName);
		else
			return null;
	}

	protected static int getMethodId(String methodName, Object... value) {
		List<Integer> methodIds = RJSDataPool.methodNameToIdMapping.get(methodName);
		int methodToExecute = -1;
		if (methodIds != null)
			methodLoop: for (int methodId : methodIds) {
				Map<String, Integer> methodNameToIdMapping = new HashMap<>();
				methodNameToIdMapping.put(methodName, methodId);
				LinkedHashMap<String, Class<?>> methodParameters = RJSDataPool.methodDeclarations
						.get(methodNameToIdMapping);
				if (value == null || value.length == 0) {
					if (methodParameters != null && !methodParameters.isEmpty())
						continue;
					else {
						methodToExecute = methodId;
						break;
					}
				} else {
					List<Class<?>> parametersTypes = new ArrayList<>();
					if (methodParameters != null)
						parametersTypes.addAll(methodParameters.values());
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

	protected static String getMethod(String name, Object... value) {
		return RJSDataPool.methodDefinitions.get(getMethodId(name, value));
	}

	protected static boolean hasMethod(String name) {
		return RJSDataPool.methodNameToIdMapping.containsKey(name);
	}

	protected static void printDataPoolAndMap() {
		System.out.println("Data Pool: " + RJSDataPool.getPrintableDataPool());
		System.out.println("Object Name Map: " + RJSDataPool.getPrintableDataNameMap());
	}

}
