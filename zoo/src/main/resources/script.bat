@echo off
SET /A "index = 1"

:while
echo %index%
SET /A "index = index + 1"
timeout /t 10 /nobreak > nul
goto :while