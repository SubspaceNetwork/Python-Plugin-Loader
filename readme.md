Python Plugin Loader
====================

Build: [![](https://camo.githubusercontent.com/3fe36a2508944552efd917025c237c98d3f723e4/687474703a2f2f62616d626f6f2e67736572762e6d652f706c7567696e732f736572766c65742f6275696c64537461747573496d6167652f504c55472d5059504c)](http://bamboo.gserv.me/browse/PLUG-PYPL/latest)

**Please note: The class API appears to be [broken](https://github.com/gdude2002/Python-Plugin-Loader/issues/1#issuecomment-50282565) and the decorator API has [a bug](https://github.com/gdude2002/Python-Plugin-Loader/issues/1).**

An update on the above: I'm not really sure we should *have* a decorator API, as it isn't in line with the Java way
of doing things, and we are essentially working with Java using Python's syntax here. Instead, I highly recommend
that you use the class-based API instead. Depending on whether I can fix it, the decorator API may be removed in a
later release.

You have been warned.

-----

**This plugin requires Java 7! If you're not using Java 7, go install it, Java 6 has been unsupported
since November 2012.**

-----

This is a fork of masteroftime's Python-Plugin-Loader. The original developers have abandoned it and do not update
it any more. This project will be used to maintain it and to keep things up to date. This project is a plugin loader
for Bukkit to load Python plugins using Jython 2.7.

* If there are changes you would like to see, please
  [open an issue](https://github.com/gdude2002/Python-Plugin-Loader/issues/new)
  or submit a pull request, instead of making your own fork.
* Take a look at the [wiki](https://github.com/gdude2002/Python-Plugin-Loader/wiki) for some extra information.
  Please note that the wiki is still under development!
* We also have [JavaDocs](http://cherry.gserv.me/docs/org.masteroftime.PyPluginLoader/). Sorry, the page titles
  are incorrect at the moment.
*Dev builds for this fork can be found [here](http://bamboo.gserv.me/browse/PLUG-PYPL). As of writing,
  there are no stable builds of this fork. Caveat emptor!


Using the plugin loader
-----------------------

### Building

* Get Gradle.
* Run `gradle clean build`
* Your product will be in build/lib/

### Running

* Ensure you are using a Bukkit build supporting **Minecraft 1.5.x** or later
* Put `PyPluginLoader-<version>.jar` into your `plugins/` folder
* [Re-]Start bukkit

### Using Python plugins

* Put the `plugin.pyp` file into your `plugins/` folder
* (Re)start Bukkit

Writing plugins
===============

Writing plugins with PythonLoader is fairly easy. There are two APIs, both
of which are pretty simple; The first is the bukkit api, which this loader
lightly wraps; and the other is a decorators-and-functions api.

Basics
------

Your plugins go in either a zip or a directory (known to windows users as "folders");
that zip or directory name must match this regex: \.py\.?(dir|zip|p|pl|plug|plugin)$

For example:

* plugin.pyp
* plugin.py.zip
* plugin.py.plugin

And so on.


Class (standard Bukkit API
---------------------------

**Note: The Class API is [currently not working](https://github.com/gdude2002/Python-Plugin-Loader/issues/1#issuecomment-50282565). Use the Decorator API (below) instead!**

To writing a plugin with this API is almost identical to writing one in Java - so
much so that you can safely use the documentation on how to write a java
plugin, simply translating it into python. The java2py tool might even work on existing
plugins, though we make no promises of that and it's unsupported.

See the "Sample plugin using class API" section for a more detailed example.

`plugin.yml`:

```yaml
name: MyHawtPlugin
main: MyPlugin
version: 0.1
```

`plugin.py`:

```python
class SampleClass(PythonPlugin):
    def onEnable():
        print "enabled!"
    def onDisable():
        print "disabled!"
```

Decorator API
-------------

**Note: The Decorator API has [a bug](https://github.com/gdude2002/Python-Plugin-Loader/issues/1)**

Writing a plugin with this api is much more concise, as you need to declare no
classes.

`plugin.yml`:

```yaml
name: MyHawtPlugin
main: main.py
version: 0.1
```

`main.py`:

```python
print "main.py run"

@hook.enable
def onEnable():
    print "main.py enabled"

@hook.disable
def onDisable():
    print "main.py disabled"

@hook.event("player.PlayerJoinEvent", "normal")
def playerJoin(event):
    event.getPlayer().sendMessage("Hello from python")

@hook.command
def example(sender, command, label, args):
    sender.sendMessage("you just used command /example!")
```

See the "Sample plugin using decorator API" section for a more detailed example.


API Details
===========

The api contains quite a few places where you can do things multiple ways. This
section documents these.

Plugin files
------------

Your plugin may go in:

- A zip whose name ends in either `.py.zip` or `.pyp`
- A directory whose name ends in `.py.dir` or `_py_dir` (for windows users)
- A python file (obviously, ending in `.py`)

Zips with the `.pyp` extension are recommended if you release any plugins. When
you use a zip, your must specify your own metadata - it will not allow guessed
metadata.

When using a dir or a zip, your zip or dir must contain a main python file and
optionally a `plugin.yml` containing metadata (see the following section). Your
python main file normally should be named either `plugin.py` or `main.py`.
`plugin.py` should generally be used when you are using the class API and `main.py`
when using the decorator API. Under some conditions you may want to change the
name of your main file (For example, when other plugins need to be able to import
it). This is not recommended - but is possible with the main field in the
metadata.

When using a single `.py` file in plugins, your single `.py` is your main python
file. You cannot have a separate `plugin.yml` - if you want to have any special
metadata, you will need a directory or zip plugin.

Plugin metadata
---------------

Plugins require metadata. The absolute minimum metadata is a name and a version.
The location of your main file/class is also required, if you don't like the
defaults. The 'main' field of the plugin metadata has some special behavior:

* If `main` is set in `plugin.yml`, it searches for the value set in main as
   the main file before searching for the default file names - see "Main files".
* `main` is used to search for a main class before searching the default
   class name.

There are three places you can put this metadata. In order of quality:

* `plugin.yml`
* Your main python file
* Your plugin filename

`plugin.yml` is the best as you are able to set all metadata fields that exist
in Bukkit, and should be used for all plugins that you release. `plugin.yml` is
used in all Java plugins (as it is the only option for Java plugins). As such,
opening up Java plugin jars is a good way to learn what can go in it. Here is
an example of a `plugin.yml`:

```yaml
name: SamplePlugin
main: SampleClass
version: 0.1-dev
commands:
    samplecommand:
        description: send a sample message
        usage: /<command>
```

The plugin filename is automatically used if no `plugin.yml` is found. The
extension is removed from the filename and used as the `name` field.
The `version` field is set to `dev` (as this case should only occur when first
creating a plugin). The `main` field is set to a default value that has no
effect.

The plugin's main Python file can be used if (and only if) you do not have a
`plugin.yml` file, so that you can override the defaults set by the plugin
filename case. It is recommended that you set these values at the top of your
main Python file. None of these values are required. These are the values you
can set:

```python
__plugin_name__ = "SamplePlugin"
__plugin_version__ = "0.1-dev"
__plugin_mainclass__ = "SampleClass"
__plugin_website__ = "http://example.com/sampleplugin"
```

**Note:** Plugin_mainclass can only be used to set the main class; it
cannot be used to set the main Python file, as it must be contained in the
main Python file. if you want to change the main python file, you must have a
`plugin.yml`.

Summary of fields:

- `main` - name of main Python file or name of main class
- `name` - name of plugin to show in the `/plugins` list and such. Used to name the
   config directory. For this reason, it must not equal the full name of the
   plugin file.
- `version` - version of plugin. shown in errors, and other plugins can access it
- `website` - mainly for people reading the code


Decorator api
-------------

The decorator API preloads an object called `hook` into your interpreter. This
object contains the decorators you can use. After first run, an object called
pyplugin is also inserted into your globals, so that it may be accessed from
functions. Some other stuff is also preloaded for you. The code runs something
like this:

```python
hook = PythonHooks()
info = getPluginDescription()

from pythonplugin import PythonPlugin

# Your code happens here

updateInfo()
pyplugin = PythonPlugin();
```

### Commands

Commands are added with the hook.command decorator. It can be used in both
no-arguments mode and in arguments mode. In no-arguments mode, it uses the
function name as the command name. In arguments mode, it takes one positional
argument, which is the command name, and three optional named arguments - desc
or description, usage, and aliases.

The decorator will attempt to register the command as though you had put it in
a plugin.yml file. If you do not provide any command metadata to the decorator
(that is, description, usage and aliases), then it will not be an error if the
command already exists, and it will not overwrite the existing metadata. 
However, if you provide metadata, then it will bail out if the command already
exists.

The decorated function may have one of these signatures:

```python
func(sender, command, label, args)
func(sender, label, args)
func(sender, args)
```

Sender is the originator of the command; this might be a player, the console,
or something plugin-created. Command is the object representing this command;
this contains the metadata you set about the command. Label a string of the
command that the player actually called; this will usually be either the
command name or an alias. Args is the list of args to the command; normally
bukkit space-splits the arguments.

The command function must return a value that evaluates to true when it has
handled the command; if it does not, any other handlers that might have been
attached to the same command name will be executed. This includes the usage
printer.

some examples:

```python
@hook.command
def samplecommand(sender, args):
    sender.sendMessage("You just used the sample command!")
    return True

@hook.command
def samplecommand2(sender, label, args):
    sender.sendMessage(label + " args: " + " ".join(args))
    return True

@hook.command
def samplecommand3(sender, command, label, args):
    sender.sendMessage("what would you EVER use command for? I'm sure there is something...")
    return True

@hook.command("samplecommand4", desc="sexeh command", usage="/<command>",
                  aliases=["samplecommand5", "samplecommand6"])
def samplecommand4(sender, label, args):
    sender.sendMessage("You just used teh sexeh command! "+label)
    return True
```

Note that you cannot do @hook.command():

```python
@hook.command()
def thisWillError(sender, args):
    print "this plugin will not load."
```


### Events

Events are registered with the hook.event decorator. This decorator may only
be used with arguments. It takes two arguments: the event type and priority.
Both are strings. Priority may be omitted. The event type has to be the class
name of the event plus the package which contains the class (e.g. player.PlayerChatEvent).
For a list of available events look into the org.bukkit.event package. Also 
note that the class name is case sensitive. If you want to register an event
that is not contained in the org.bukkit.event package you have to specify the
full path to the event (e.g. net.anotherplugin.events.CustomEvent).

The priority is one of the org.bukkit.event.EventPriority enumeration, this time case insensitive.

> Note:
> 
> Previously the event type was one of the event types in 
> org.bukkit.event.Event.Type, in any upper/lower case mix you liked and the
> priority was one of org.bukkit.event.Event.Priority, also in any case mix.
> However due to the introduction of the new EventHandling API Event.Type and
> Event.Priority are now deprecated and should not be used any more. Instead
> use the class name of the desired event and the EventPriority enumeration.

The decorated function must take exactly one argument: the event to handle.

It is worth noting that Bukkit handles priority in reverse: the highest
priority event handler is called last. Apparently they think this makes sense
because the highest priority handler should have the last say in whether the
event is cancelled ... well, in most of our world, we aren't wanting to cancel
the event, but to act on it, so what they think is forward is really reverse.


examples:

```python
@hook.event("player.PlayerJoinEvent", "normal")
def onPlayerJoin(event):
    event.getPlayer().sendMessage("hello from python!")

@hook.event("player.PlayerChatEvent", "monitor")
def onPlayerChat(event):
    event.getPlayer().sendMessage("hai")
```

### Enable and Disable

Functions decorated with hook.enable and hook.disable are called when your
plugin is activated and deactivated, respectively. if you want your plugin to
be properly reloadable, you should clean up all your objects in your
hook.disable function.

examples:
```python
@hook.enable
def onEnable():
    print "enabled!"

@hook.disable
def onDisable():
    print "disabled!"
```

### Accessing the plugin object

The plugin instance is loaded into your globals as pyplugin.

Specifications for sample plugin
--------------------------------

- name is "SamplePlugin"
- main class, if applicable, should be "SampleClass"
- version is "0.1-dev"
- should print "sample plugin main file run" when loaded by the interpreter
- should print "sample plugin main class instantiated" when the main class is
   instantiated, if applicable
- should print "sample plugin enabled" when enabled
- should print "sample plugin disabled" when disabled
- should print and reply "sample plugin command" when the sample command
   "/samplecommand", is used
- sample command should have usage "/<command>" and should have description
   "send a sample message"
- should print and send message "welcome from the sample plugin, %s" % username
   when a player joins the server

Sample plugin using decorator api
---------------------------------

### main.py

```python
__plugin_name__ = "SamplePlugin"
__plugin_version__ = "0.1-dev"

@hook.enable
def onEnable():
    print "sample plugin enabled"

@hook.disable
def onDisable():
    print "sample plugin disabled"

@hook.event("player_join", "normal")
def onPlayerJoin(event):
    msg = "welcome from the sample plugin, %s" % event.getPlayer().getName()
    print msg
    event.getPlayer().sendMessage(msg)

@hook.command("samplecommand", usage="/<command>",
                desc="send a sample message")
def onSampleCommand(sender, command, label, args):
    msg = "sample plugin command"
    print msg
    sender.sendMessage(msg)
    return True

print "sample plugin main file run"
```

Sample plugin using class api
-----------------------------

### plugin.yml

```yaml
name: SamplePlugin
main: SampleClass
version: 0.1-dev
commands:
    samplecommand:
        description: send a sample message
        usage: /<command>
```

### plugin.py

```python
from org.bukkit.event.player import PlayerListener
from org.bukkit.event.Event import Type, Priority

class SampleClass(PythonPlugin):
    def __init__(self):
        self.listener = SampleListener(self)
        print "sample plugin main class instantiated"

    def onEnable(self):
        pm = self.getServer().getPluginManager()
        pm.registerEvent(Type.PLAYER_PICKUP_ITEM, listener, Priority.Normal, self)
        pm.registerEvent(Type.PLAYER_RESPAWN, listener, Priority.Normal, self)

        print "sample plugin enabled"

    def onDisable(self):
        print "sample plugin disabled"

    def onCommand(self, sender, command, label, args):
        msg = "sample plugin command"
        print msg
        sender.sendMessage(msg)
        return True

class SampleListener(PlayerListener):
    def __init__(self, plugin):
        self.plugin = plugin

    def onPlayerJoin(self, event):
        msg = "welcome from the sample plugin, %s" % event.getPlayer().getName()
        print msg
        event.getPlayer().sendMessage(msg)

print "sample plugin main file run"
```
