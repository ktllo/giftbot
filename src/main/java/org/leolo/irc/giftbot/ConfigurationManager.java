package org.leolo.irc.giftbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class ConfigurationManager {
    private static ConfigurationManager instance;
    private Logger log = LoggerFactory.getLogger(ConfigurationManager.class);
    private Properties properties = new Properties();

    private ConfigurationManager() {
        log.info("Initializing configuration manager...");
        try {
            properties.load(ClassLoader.getSystemResourceAsStream("settings.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String key : properties.stringPropertyNames()) {
            log.info(key + ": " + properties.getProperty(key));
        }
    }

    public synchronized static ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    /**
     * Searches for the property with the specified key in this property list.
     * If the key is not found in this property list, the default property list,
     * and its defaults, recursively, are then checked. The method returns
     * {@code null} if the property is not found.
     *
     * @param key the property key.
     * @return the value in this property list with the specified key value.
     * @see #setProperty
     * @see #defaults
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Searches for the property with the specified key in this property list.
     * If the key is not found in this property list, the default property list,
     * and its defaults, recursively, are then checked. The method returns the
     * default value argument if the property is not found.
     *
     * @param key          the hashtable key.
     * @param defaultValue a default value.
     * @return the value in this property list with the specified key value.
     * @see #setProperty
     * @see #defaults
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Returns an enumeration of all the keys in this property list,
     * including distinct keys in the default property list if a key
     * of the same name has not already been found from the main
     * properties list.
     *
     * @return an enumeration of all the keys in this property list, including
     * the keys in the default property list.
     * @throws ClassCastException if any key in this property list
     *                            is not a string.
     * @see Enumeration
     * @see Properties#defaults
     * @see #stringPropertyNames
     */
    public Enumeration<?> propertyNames() {
        return properties.propertyNames();
    }

    public Enumeration<Object> keys() {
        return properties.keys();
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public int size() {
        return properties.size();
    }

    public boolean contains(Object value) {
        return properties.contains(value);
    }

    public boolean containsValue(Object value) {
        return properties.containsValue(value);
    }

    public boolean containsKey(Object key) {
        return properties.containsKey(key);
    }

    public Object get(Object key) {
        return properties.get(key);
    }

    public Set<Object> keySet() {
        return properties.keySet();
    }

    public Collection<Object> values() {
        return properties.values();
    }

    public Set<Map.Entry<Object, Object>> entrySet() {
        return properties.entrySet();
    }
}
