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
	private static Map<Integer, LinkedHashMap<String, Class<?>>> methodDeclarations = new HashMap<>();
	private static Map<Integer, String> methodDefinitions = new HashMap<>();
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
		RJSDataPool.methodNameToIdMapping.put(name, methodIds);
		RJSDataPool.methodDeclarations.put(id, arguments);
	}

	protected static void defineMethod(String name, String command) {
		List<Integer> methodIds = RJSDataPool.methodNameToIdMapping.get(name);
		RJSDataPool.methodDefinitions.put(methodIds.size(), command);
	}

	protected static String getMethod(int methodId) {
		return RJSDataPool.methodDefinitions.get(methodId);
	}

	protected static Set<Integer> getMethods() {
		return RJSDataPool.methodDefinitions.keySet();
	}

	protected static ArrayList<Class<?>> getMethodParametersTypes(String name) {
		ArrayList<Class<?>> methodParametersList = new ArrayList<>();
		if (RJSDataPool.methodDeclarations.containsKey(name))
			methodParametersList.addAll(RJSDataPool.methodDeclarations.get(name).values());
		return methodParametersList;
	}

	protected static void setMethodArguments(int methodId, Object... value) {
		if (methodId != -1) {
			LinkedHashMap<String, Class<?>> methodParameters = RJSDataPool.methodDeclarations.get(methodId);
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

	protected static int getMethodId(String name, Object... value) {
		List<Integer> methodIds = RJSDataPool.methodNameToIdMapping.get(name);
		int methodToExecute = -1;
		if (methodIds != null)
			methodLoop: for (int methodId : methodIds) {
				LinkedHashMap<String, Class<?>> methodParameters = RJSDataPool.methodDeclarations.get(methodId);
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
