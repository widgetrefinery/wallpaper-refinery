Wallpaper Refinery
------------------

This is a simple utility that will help you make wallpapers for your particular
multi-head desktop. For any given input image, Wallpaper Refinery will create a
new image that can "stretch" across all of your monitors in tiled mode.

On supported OS'es, Wallpaper Refinery can optionally set the generated image
as your wallpaper so you don't need to do it manually. Currently the only
supported OS is Windows XP as that's the environment I have. Patches are
welcome to make it work in additional environments.

Download
--------

You can find the latest build under the project's [downloads][wrd] tab.

Installation
------------

All you need is [Java 1.6+][Java] to use Wallpaper Refinery. There are no other
installation steps required. Just copy the jar wherever you want and you are
good to go. If you don't like Wallpaper Refinery, just delete the jar and
it's gone completely.

I plan to add a GUI but in the mean time, you can make Wallpaper Refinery more
accessible by creating a shortcut to the jar with the arguments you want plus
a `-i` at the end. This allows you to drag-and-drop images onto the shortcut
and it will work its magic without you having to bring up the command line. As
an example, the following shortcut will generate a BMP image in the current
folder called `wr.bmp` (`-o wr.bmp`) overwriting any existing file (`-f`),
update the Windows registry to use the new image (`-c`), and then tell Windows
to refresh itself so the new image displays on your desktop (`-r`):

    %JAVA_HOME%\bin\java.exe -jar wallpaper-refinery.jar -froc wr.bmp -i

Building
--------

You will need:

* [Java JDK 1.6+][Java]
* [Maven 3][Maven]
* [Refinery Tools source code][rfs]
* [Wallpaper Refinery source code][wrs]

To build:

    cd /path/to/refinery-tools
    mvn clean install
    cd /path/to/wallpaper-refinery
    mvn clean package assembly:single

The final jar file will be in: `/path/to/wallpaper-refinery/target/wallpaper-refinery.jar`

Misc
----

I originally built Wallpaper Refinery one weekend because a quick google search
for wallpaper tools gave me junk. I either needed to buy something or it didn't
look healthy for my system. I figured such an application should be relatively
simple and straight forward so with my IDE and some elbow grease Wallpaper
Refinery was born. Honestly, the hardest part was coming up with a name for it.
I figure other people mind find it useful as well so here it is. Enjoy.

[Java]:  http://www.oracle.com/technetwork/java/javase/downloads/index.html
[Maven]: http://maven.apache.org
[rfs]:   https://github.com/widgetrefinery/refinery-tools
[wrs]:   https://github.com/widgetrefinery/wallpaper-refinery
[wrd]:   https://github.com/widgetrefinery/wallpaper-refinery/downloads
