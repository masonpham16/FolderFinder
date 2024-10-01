# **FolderFinder**
App to find a folder inside a folder that contrains hundreds of folders.

What can this app do?
- Set default folder to search in
- Search folder name by typing in what the name contains
- Open the folder with double click
- Copy folder name to clipboard

I found it painful to search folders at work, because I had to open a folder, then go to the directory, then search for the folder.
It took too many steps.
- Of course I could put folders in my quicktab, but then that tab would be filled with about a hundred folders.

# **how to use**

- clone this
- compile (if you don't have a java compiler yet, download java 17 compiler.)
- open the app with you desired IDE or terminal.
- (optional) create an .exe file with Launch4J or similar.
- make sure to create "META.INF" folder and create a "MANIFEST.MF" file in it like so:

directory stucture for manifest file:
    META-INF/MANIFEST.MF

text inside the manifest file:
'''
Manifest-Version: 1.0
Main-Class: OpenFolder

'''

package the .jar file:
    jar cfm FolderFinder.jar META-INF/MANIFEST.MF *.class

- then run the .jar file:
    java -jar FolderFinder.jar

if that works, you can use Launch4J to create an .exe file.



----------------------------------




