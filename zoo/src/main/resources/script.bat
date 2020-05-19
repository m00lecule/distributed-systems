@echo off
SET /A "index = 1"

:while
echo %index%
SET /A "index = index + 1"
PING 1.1.1.1 -n 10 -w 30000 >NUL
goto :while