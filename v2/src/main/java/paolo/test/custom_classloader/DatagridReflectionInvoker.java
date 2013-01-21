package paolo.test.custom_classloader;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DatagridReflectionInvoker {

	private static final String DEFAULT_CACHE_NAME = "default";
	private static final String DATA_GRID_ADDRESS = "dataGridAddress";
	private static final String PATH_TO_JAR = "pathToJar";
	private static final String DATAGRID_PROPERTIES = "/resources/datagrid.properties";
	private static final String ORG_INFINISPAN_CLIENT_HOTROD_REMOTE_CACHE = "org.infinispan.client.hotrod.RemoteCache";
	private static final String ORG_INFINISPAN_CLIENT_HOTROD_REMOTE_CACHE_MANAGER = "org.infinispan.client.hotrod.RemoteCacheManager";

	private static final String[] CACHE_NAMES = { "bw", "pad", "currency",
			"ssca", "ps" };

	private static DirectoryBasedParentLastURLClassLoader cl = null;

	private static Class<?> classRemoteCache = null;
	private static Class<?> classRemoteCacheManage = null;

	private static Map caches = null;

	private static String pathToJar = "/ssca/lib/";
	private static String dataGridAddress = null;

	static {

		try {
			initialize();
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(
					"Datagrid classes not found. Passed parameter: "
							+ pathToJar, e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(
					"Datagrid classes have an unexpected API. Passed parameter: "
							+ pathToJar, e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(
					"Datagrid classes have an unexpected API. Passed parameter: "
							+ pathToJar, e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(
					"Problem with reflection invocation. Passed parameter: "
							+ pathToJar, e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(
					"Problem with reflection invocation. Passed parameter: "
							+ pathToJar, e);
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"Problem with the expected configuration file: "
							+ DATAGRID_PROPERTIES, e);
		}
	}

	// maybe needed by TIBCO, not sure
	public DatagridReflectionInvoker() {

	}

	public static Object get(Object key) {
		Method method = null;
		Object invoke = null;
		try {
			method = classRemoteCache.getMethod("get", Object.class);
			Object cache = caches.get(DEFAULT_CACHE_NAME);
			if (cache == null) {
				throw new IllegalArgumentException(
						String.format(
								"No cache with name [%s] found. Available caches are: %s",
								DEFAULT_CACHE_NAME, Arrays.asList(CACHE_NAMES)));
			}
			invoke = method.invoke(cache, key);
		} catch (SecurityException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		}
		return invoke;
	}

	public static Object get(String cacheName, Object key) {
		Method method = null;
		Object invoke = null;
		try {

			method = classRemoteCache.getMethod("get", Object.class);
			Object cache = caches.get(cacheName);
			if (cache == null) {
				throw new IllegalArgumentException(
						String.format(
								"No cache with name [%s] found. Available caches are: %s",
								cacheName, Arrays.asList(CACHE_NAMES)));
			}

			invoke = method.invoke(cache, key);
		} catch (SecurityException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		}
		return invoke;
	}

	public static Object put(Object key, Object value) {
		Method method = null;
		Object result = null;

		try {
			method = classRemoteCache.getMethod("put", Object.class,
					Object.class);

			Object cache = caches.get(DEFAULT_CACHE_NAME);
			if (cache == null) {
				throw new IllegalArgumentException(
						String.format(
								"No cache with name [%s] found. Available caches are: %s",
								DEFAULT_CACHE_NAME, Arrays.asList(CACHE_NAMES)));
			}
			result = method.invoke(cache, key, value);
		} catch (SecurityException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		}
		return result;

	}

	public static Object put(String cacheName, Object key, Object value) {
		Method method = null;
		Object result = null;

		try {
			method = classRemoteCache.getMethod("put", Object.class,
					Object.class);

			Object cache = caches.get(cacheName);
			if (cache == null) {
				throw new IllegalArgumentException(
						String.format(
								"No cache with name [%s] found. Available caches are: %s",
								cacheName, Arrays.asList(CACHE_NAMES)));
			}
			result = method.invoke(cache, key, value);
		} catch (SecurityException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Unexpected Reflection Error", e);
		}
		return result;

	}

	protected static void initialize() throws ClassNotFoundException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException, IOException {

		readProperties();

		cl = new DirectoryBasedParentLastURLClassLoader(pathToJar);

		classRemoteCache = cl
				.loadClass(ORG_INFINISPAN_CLIENT_HOTROD_REMOTE_CACHE);

		classRemoteCacheManage = cl
				.loadClass(ORG_INFINISPAN_CLIENT_HOTROD_REMOTE_CACHE_MANAGER);
		Object remoteCacheManager = createRemoteCacheManager();

		createRemoteCacheMap(remoteCacheManager);

	}

	private static void readProperties() throws IOException {

		Properties props = new Properties();
		props.load(DatagridReflectionInvoker.class
				.getResourceAsStream(DATAGRID_PROPERTIES));

		pathToJar = props.getProperty(PATH_TO_JAR);
		if (pathToJar != null && "".equals(pathToJar.trim())) {
			throw new IllegalArgumentException("Configuration problem: "
					+ PATH_TO_JAR + " is empty");
		}
		dataGridAddress = props.getProperty(DATA_GRID_ADDRESS);
		if (dataGridAddress != null && "".equals(dataGridAddress.trim())) {
			throw new IllegalArgumentException("Configuration problem: "
					+ DATA_GRID_ADDRESS + " is empty");
		}
	}

	private static void createRemoteCacheMap(Object remoteCacheManager)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		caches = new HashMap();

		Method method = classRemoteCacheManage.getMethod("getCache",
				String.class);
		for (String cacheName : CACHE_NAMES) {
			// mimicks RemoteCache rc = rcm.getCache();
			Object remoteCache = method.invoke(remoteCacheManager, cacheName);
			caches.put(cacheName, remoteCache);
		}

		// instantiate explicitly the default cache

		method = classRemoteCacheManage.getMethod("getCache");
		Object remoteCache = method.invoke(remoteCacheManager);
		caches.put(DEFAULT_CACHE_NAME, remoteCache);

	}

	protected static Object createRemoteCacheManager()
			throws ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		// new RemoteCacheManager("xxx");
		Constructor<?> constructor = classRemoteCacheManage
				.getConstructor(String.class);
		Object remoteCacheManager = constructor.newInstance(dataGridAddress);
		return remoteCacheManager;
	}

}
