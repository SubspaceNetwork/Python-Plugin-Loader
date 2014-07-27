package com.master.bukkit.python;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.lahwran.bukkit.jython.PythonPluginLoader;

import org.apache.commons.io.FileUtils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;

import org.python.core.*;

/**
 * Java plugin to initialize python plugin loader and provide it with a little moral boost.
 * @author masteroftime
 * @author lahwran
 *
 */
public class PythonLoader extends JavaPlugin {

    public void onDisable() {}
    public void onEnable() {}

    /**
     * Initialize and load up the plugin loader.
     */
    @Override
    public void onLoad() {
        // This must occur as early as possible, and only once.
        PluginManager pm = Bukkit.getServer().getPluginManager();
        boolean needsload = true;

        // We need to make sure Jython's registry file exists
        File jFile = new File(java.lang.System.getProperty("user.home") + "/.jython");

        if (!jFile.exists()) {
            URL inputUrl = getClass().getResource("/.jython");
            try {
                FileUtils.copyURLToFile(inputUrl, jFile);
            } catch (IOException e) {
                this.getLogger().warning("Unable to copy .jython to " + java.lang.System.getProperty("user.home"));
                this.getLogger().warning("Please create it yourself and add the following line to it (without \"quotes\"):");
                this.getLogger().warning("\"python.security.respectJavaAccessibility = false\"");
                e.printStackTrace();
            }
        } else {
            this.getLogger().info("File found: " + java.lang.System.getProperty("user.home") + "/.jython");
            this.getLogger().info("If your Python plugins behave oddly, please make sure this file contains \"python.security.respectJavaAccessibility = false\".");
        }

        String errorstr = "cannot ensure that the python loader class is not loaded twice!";
        Map<Pattern, PluginLoader> fileAssociations = ReflectionHelper.getFileAssociations(pm, errorstr);

        if (fileAssociations != null) {
            PluginLoader loader = fileAssociations.get(PythonPluginLoader.fileFilters[0]);
            if (loader != null) // already loaded
                needsload = false;
        }

        if (needsload) {
            //System.out.println("PythonLoader: loading into bukkit");
            pm.registerInterface(PythonPluginLoader.class);
            //pm.loadPlugins(this.getFile().getParentFile()); //TODO Check weather this reloads java plugins which were already loaded

            for (File file : this.getFile().getParentFile().listFiles()) {
                for (Pattern filter : PythonPluginLoader.fileFilters) {
                    Matcher match = filter.matcher(file.getName());
                    if (match.find()) {
                        try {
                            pm.loadPlugin(file);
                        } catch (InvalidPluginException | InvalidDescriptionException | UnknownDependencyException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}
