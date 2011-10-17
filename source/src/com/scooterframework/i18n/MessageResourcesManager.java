/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.i18n;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.scooterframework.admin.PropertyReader;
import com.scooterframework.common.logging.LogUtil;

/**
 * <p>
 * MessageResourcesManager is responsible for managing loading a locale's 
 * messages. It loads message resource files from the designated <tt>config/locales</tt> 
 * directory path based on the given <tt>base</tt> name. 
 * </p>
 * 
 * <p>
 * The way this class loads messages is the same as Java's <tt>ResourceBundle</tt>
 * operates. It first looks through the specified Locale's language, country 
 * and variant, then through the default Locale's language, country and 
 * variant and finally using just the <tt>base</tt>:
 * <pre>
 *      base + "_" + localeLanguage + "_" + localeCountry + "_" + localeVariant
 *      base + "_" + localeLanguage + "_" + localeCountry
 *      base + "_" + localeLanguage
 *      base + "_" + defaultLanguage + "_" + defaultCountry + "_" + defaultVariant
 *      base + "_" + defaultLanguage + "_" + defaultCountry
 *      base + "_" + defaultLanguage
 *      base
 * </pre>
 * </p>
 * 
 * @author (Fei) John Chen
 */
public class MessageResourcesManager {
    
    protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
	
    /*
     * Resource file extension
     */
	private static final String resourceFileExtension = ".properties";
	
	/*
	 * Path to config directory
	 */
	private String configPath;
	
	/*
	 * Base name of message resource files.
	 */
	private String baseName;
	
	/*
	 * map of all loaded locales and keys
	 */
	private static Map<String, Locale> loadedLocalesKeyMap = new HashMap<String, Locale>();
	
	/*
	 * map of all message files.
	 */
	private static Map<String, File> allMessageFiles = new HashMap<String, File>();
	
	/*
	 * map of message file and its messages.
	 */
	private static Map<String, Properties> fileMessagesMap = new HashMap<String, Properties>();
	
	/*
	 * map of locale files map in theory.
	 */
	private static Map<String, List<String>> localeFilesInTheoryMap = new HashMap<String, List<String>>();
	
	/*
	 * map of locale files map in reality.
	 */
	private static Map<String, List<String>> localeFilesInRealityMap = new HashMap<String, List<String>>();
	
	/*
	 * map of locale message map.
	 */
	private static Map<String, Properties> localeMsgMap = new HashMap<String, Properties>();
	
	public MessageResourcesManager(String configPath, String baseName) {
		this.configPath = configPath;
		this.baseName = baseName;
		
		loadAllFiles(configPath, baseName);
		if (allMessageFiles == null || allMessageFiles.size() == 0) {
			throw new IllegalArgumentException("There is no file under directory [" + configPath + 
					                           "] that starts with " + baseName + ".");
		}
	}
	
	/**
	 * Returns path to the directory where all message files are stored. 
	 */
	public String getConfigPath() {
		return configPath;
	}
	
    /**
     * Returns all message properties files as a map with file name as key and 
     * file object as value.
     * 
     * @return map of files
     */
	public Map<String, File> getAllFiles() {
		return allMessageFiles;
	}
	
	public Set<Map.Entry<String, Locale>> getAllLoadedLocalesSet() {
		return loadedLocalesKeyMap.entrySet();
	}
	
    /**
     * Returns a message associated with the <tt>key</tt> and the 
     * <tt>values</tt> in a specific <tt>locale</tt>.
     * 
     * <p>If there is no message associated with the <tt>key</tt> in messages 
     * property files, this method returns <tt>null</tt>.</p>
     * 
     * @param key       a message key in messages resource files
     * @param locale    a specific locale object
     * @return a message string
     */
	public String getMessage(String key, Locale locale) {
		if (!hasLoaded(locale)) {
			loadLocale(locale);
		}
		
		Properties prop = localeMsgMap.get(getLocaleKey(locale));
		return (prop != null)?prop.getProperty(key):null;
	}
	
	public void loadLocale(Locale locale) {
		if (locale == null) throw new IllegalArgumentException("Input locale cannot be null in loadLocale().");
		
		if (hasLoaded(locale)) {
			return;
		}

		String key = getLocaleKey(locale);
		localeFilesInTheoryMap.put(key, getFilesInTheory(locale));
		localeFilesInRealityMap.put(key, getFilesInReality(locale));
		localeMsgMap.put(key, getMessages(locale));
		loadedLocalesKeyMap.put(key, locale);
		
		log.debug("loaded locale: " + key);
	}
	
	private boolean hasLoaded(Locale locale) {
		return loadedLocalesKeyMap.containsKey(getLocaleKey(locale));
	}
	
	private String getLocaleKey(Locale locale) {
		return (locale != null)?locale.toString():null;
	}
	
	private void loadAllFiles(String configPath, String baseName) {
		File dir = new File(configPath);
		if (!dir.isDirectory()) {
            URL url = getClass().getClassLoader().getResource(configPath);
            if (url != null) {
                dir = new File(url.getFile());
                if (!dir.isDirectory()) 
                    throw new IllegalArgumentException("Config path [" + configPath + "] must be a directory path.");
            }
		}
		File[] files = dir.listFiles();
		if (files == null) {
			throw new IllegalArgumentException("There is no file in directory [" + configPath + "].");
		}
		
		int length = files.length;
		for (int i = 0; i < length; i++) {
			File file = files[i];
			if (file.isFile() && file.getName().startsWith(baseName)) {
				allMessageFiles.put(file.getName(), file);
			}
		}
	}
	
	private List<String> getFilesInTheory(Locale locale) {
		List<String> list = new ArrayList<String>();
		
		String base = baseName;
		list.add(getFileName(base));
		
		Locale defaultLocale = Locale.getDefault();
		if (!getLocaleKey(defaultLocale).equals(getLocaleKey(locale))) {
            populateList(list, base, defaultLocale);
        }
        
		populateList(list, base, locale);
		
		return list;
	}
	
	private void populateList(List<String> list, String base, Locale locale) {
		String language = locale.getLanguage();
		String country = locale.getCountry();
		String variant = locale.getVariant();
		
		language = (language != null)?language:"";
		country = (country != null)?country:"";
		
		String file = "";
		if (!"".equals(language)) {
			file = getFileName(base + "_" + language);
			list.add(file);
		}
		
		if (!"".equals(country)) {
			file = getFileName(base + "_" + language + "_" + country);
			list.add(file);
		}
		
		if (variant != null && !"".equals(variant)) {
			file = getFileName(base + "_" + language + "_" + country + "_" + variant);
		}
	}
	
	private String getFileName(String name) {
		return name + resourceFileExtension;
	}
	
	private List<String> getFilesInReality(Locale locale) {
		List<String> list = new ArrayList<String>();
		List<String> tFiles = localeFilesInTheoryMap.get(getLocaleKey(locale));
		if (tFiles != null) {
			for (String file : tFiles) {
				if (allMessageFiles.containsKey(file)) {
					list.add(file);
				}
			}
		}
		
		return list;
	}
	
	private Properties getMessages(Locale locale) {
		String key = getLocaleKey(locale);
		Properties messages = localeMsgMap.get(key);
		if (messages != null) return messages;
		
		messages = new Properties();
		List<String> files = (List<String>)localeFilesInRealityMap.get(key);
		if (files != null) {
			for (String fileName : files) {
				Properties props = loadPropertiesFromFile(fileName);
				messages.putAll(props);
			}
		}
		
		localeMsgMap.put(key, messages);
		
		return messages;
	}
	
	private Properties loadPropertiesFromFile(String fileName) {
		if (hasLoadedFile(fileName)) {
			return (Properties)fileMessagesMap.get(fileName);
		}
        
        Properties props = PropertyReader.loadOrderedPropertiesFromFile((File)allMessageFiles.get(fileName));
        fileMessagesMap.put(fileName, props);
        return props;
	}
	
	private boolean hasLoadedFile(String fileName) {
		return fileMessagesMap.containsKey(fileName);
	}
	
	/**
	 * Reloads locales related to the updated file.
	 * 
	 * @param file
	 */
	void fileUpdated(File file) {
		//remove the file from fileMessagesMap
		fileMessagesMap.remove(file.getName());
		
		//find all locales that use the file
		List<Locale> affectedLocales = new ArrayList<Locale>();
		for (Map.Entry<String, List<String>> entry : localeFilesInRealityMap.entrySet()) {
			String localeKey = entry.getKey();
			List<String> files = localeFilesInRealityMap.get(localeKey);
			if (files.contains(file.getName())) {
				affectedLocales.add(loadedLocalesKeyMap.get(localeKey));
			}
		}
		
		//reload for each affected locale
		for (Locale locale : affectedLocales) {
			String key = getLocaleKey(locale);
			localeMsgMap.remove(key);
			localeMsgMap.put(key, getMessages(locale));
		}
	}
    
    /**
	 * Reloads locales after a new file is added.
	 * 
	 * @param file
	 */
	void fileAdded(File file) {
        allMessageFiles.put(file.getName(), file);
        
        //reload all loaded locales
        for (Map.Entry<String, Locale> entry : loadedLocalesKeyMap.entrySet()) {
            String key = entry.getKey();
            Locale locale = entry.getValue();
            localeFilesInRealityMap.put(key, getFilesInReality(locale));
            localeMsgMap.remove(key);
            localeMsgMap.put(key, getMessages(locale));
        }
    }
    
    /**
	 * Reloads locales after a new file is deleted.
	 * 
	 * @param file
	 */
	void fileDeleted(File file) {
        allMessageFiles.remove(file.getName());
        fileMessagesMap.remove(file.getName());
        
        //reload all loaded locales
        for (Map.Entry<String, Locale> entry : loadedLocalesKeyMap.entrySet()) {
            String key = entry.getKey();
            Locale locale = entry.getValue();
            localeFilesInRealityMap.put(key, getFilesInReality(locale));
            localeMsgMap.remove(key);
            localeMsgMap.put(key, getMessages(locale));
        }
    }
}
