package org.vanbart;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author Ton van Bart
 * @since 3/4/14 2:29 PM
 */
public class Scripting {

    public static void main(String[] args) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine nashorn = manager.getEngineByName("nashorn");
        nashorn.eval("print('Hello, World!')");

        ScriptEngine js = manager.getEngineByExtension("js");
        js.eval("print ('Hello, again!');");

        for (ScriptEngineFactory factory : manager.getEngineFactories()) {
            log("engine name: %s (%s); language name: %s", factory.getEngineName(), factory.getEngineVersion(), factory.getLanguageName());
        }
    }

    private static void log(String format, Object... args) {
        System.out.println(String.format(format, args));
    }

}
