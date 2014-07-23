/**
 *
 */
package net.lahwran.bukkit.jython;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.python.core.Py;

/**
 * Special listener to handle events that were registered with PythonHooks.
 * @author lahwran
 *
 */
public class PythonListener implements Listener {

    /**
     * handlers registered that this listener needs to handle
     */
    HashMap<Class<? extends Event>, Set<PythonEventHandler>> handlers = new HashMap<>();

    /*
     * Handle an event. Depends on jython being smart enough to cast the event
     * down to whatever it is. Hint: jython is smart enough.
     * @param e event
     * @deprecated The new Event API uses the fireEvent method
     */
    /* I guess the whole method is unneccessary now
    @Deprecated
    void onEvent(Event e) {
        PythonEventHandler handler = oldHandlers.get(e.getType());
        if (handler == null) {
            return;
        }
        handler.handler.__call__(Py.java2py(e));
    }*/

    void fireEvent(Event e, PythonEventHandler handler) {
        handler.handler.__call__(Py.java2py(e));
    }

    void addHandler(Class<? extends Event> type, PythonEventHandler handler) {
        Set<PythonEventHandler> set = this.handlers.get(type);

        if(set == null) {
            set = new HashSet<>();
            handlers.put(type, set);
        }

        set.add(handler);
    }
}
