# **FolderFinder**
App to find a folder inside a folder that contains too many folders.

What can this app do?
- Set default folder to search in.
- Search folder name by typing in what the name contains.
- Open the folder with double click.
- Copy folder name to clipboard.

I found it painful to search folders at work.

I had to open folder - then keep opening more folders
until I got to the folder with all the project folders in it.

It took too many steps.

Of course I could put folders in my quicktab, but then that tab would be filled with about a hundred folders.

----------------------------------

# **How to use**

Clone this:

    git clone https://github.com/masonpham16/FolderFinder.git

- Compile (if you don't have a java compiler yet, download java 17 compiler.)
- Open the app with you desired IDE or terminal.
- (optional) Create an .exe file with Launch4J or similar.
- Make sure to create "META.INF" folder and create a "MANIFEST.MF" file in it like so:

Directory structure for manifest file:

    META-INF/MANIFEST.MF

Text inside the manifest file:

    Manifest-Version: 1.0
    Main-Class: OpenFolder
    //make sure to leave a space below


Package the .jar file:

    jar cfm FolderFinder.jar META-INF/MANIFEST.MF *.class

Then run the .jar file:

    java -jar FolderFinder.jar

If that works, you can use Launch4J to create an .exe file.
on Launch4J you can press the play button to see if it starts up; if it doesn't, try to fix some bug.

(new) You can now add icon to the .exe file.

----------------------------------

Updates :

10/01/2024
- Add copy button
- Update README
- Add icon

10/02/2024
- Copy button does not have pop-up anymore

10/03/2024
- Copy button is a 'copy' icon now
- Add open folder button

----------------------------------

TODO:
- Create installer
- Make it downloadable and installable on Windows
- Make it downloadable and installable on Mac
