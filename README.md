היי
אנחנו רוצים לעבוד במקביל על הקוד ולדחוף שינויים חדשים לתוך הקוד הראשי שנמצא במאסטר. הדרך הכי נוחה לעשות את זה היא ככה


## How to connect to this repository from git?:
Connect to git from intellij: VCS -> get from version control -> enter your account and search for the name rickben/CompilerMiniJava,
it should appear, I set you as a contributer.

Currently this is a private repo, once we submmit the project we can make it public, so now people won't still our code...

## How to work in the project with git?
Once you connected to git via Intellij you can see the code there.
Whenever you want to start working or changing the code (minor or big change), click on git in the buttom of the ide at the rigth, and 
by hitting the + icon you can add a new branch.
Give the branch informative name such as "ricky-add-print-visitor-numbers-static" (your name so we will know who did it at the start).
when you want to save your work you should hit commit: double Shift + "commit". Give it an informative message and click on commit. 
Remember you should be in a branch not in master.
If you want us to see your code or maybe review it for changes before we put it in the master which is our main code we will eventually submit, 
you should commit and then click on your branch at the right buttom of the ide: click on your current branch + click push.
Now if you will go to this address here: https://github.com/rickben/CompilerMiniJava, you will se it will suggest you to make a pull request.
Click to pull request and write what you did if you want.
Now we will need to go over the pull requests everyone submitted for their different branches and decide what is good and what will need to change.
We will talk more if problems come up!

## Compiling this code:
After connecting to the code, you will need to click on ant: +, and click on build.xml in this project.
Moreover, you need to add the tools as a library: Intellij -> File -> Project structure -> Modules -> Dependencies -> click + -> library -> 
add new library -> click on tools -> come back to dependencies and click on v on tools.
Now click compil in ant - it compiles. dist - creates the jar (we don't need that now, only for the final submmition I beleive).

The project arguments for the main class in src are: unmarshal print src/field.java.xml field_print.txt

To check you succeded: click on main + arguments - and check if after couple of seconds the file field_print.xml is generated.
