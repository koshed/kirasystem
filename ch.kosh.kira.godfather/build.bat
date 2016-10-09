echo build
cd D:\Mark\Code\workspace_git\kiraSystem\ch.kosh.kira.godfather\
call mvn clean verify 
rem pause
echo.copy
"c:\Program Files (x86)\pscp.exe" -pw pi D:\Mark\Code\workspace_git\kiraSystem\ch.kosh.kirasystem.server\target\ch.kosh.kirasystem.server-0.0.1-SNAPSHOT-jar-with-dependencies.jar pi@10.10.10.10:/home/pi/javaStuff/
"c:\Program Files (x86)\pscp.exe" -pw pi D:\Mark\Code\workspace_git\kiraSystem\ch.kosh.kirasystem.scanner\target\ch.kosh.kirasystem.scanner-0.0.1-SNAPSHOT-jar-with-dependencies.jar pi@10.10.10.10:/home/pi/javaStuff/
"c:\Program Files (x86)\pscp.exe" -pw pi D:\Mark\Code\workspace_git\kiraSystem\ch.kosh.kirasystem.scanner\target\ch.kosh.kirasystem.scanner-0.0.1-SNAPSHOT-jar-with-dependencies.jar pi@10.10.10.11:/home/pi/javaStuff/
pause
