@echo off
DEL *.png
SET dotpath="C:\Program Files (x86)\Graphviz2.38\bin\dot"
FOR %%G IN (".\*.dot") DO %dotpath% -Tpng %%G > %%G.png
SETLOCAL EnableDelayedExpansion

SET temp_file=temp_string.txt
echo. 2>%temp_file%
FOR /f "delims=" %%A IN ('DIR /B "%~dp0"') DO (
    ECHO %%A
    ECHO %%A > %temp_file%
    FOR /F "tokens=1,2 delims=." %%I IN (%temp_file%) DO (
        ECHO %%I %%J
        rename %%I.%%J.png %%I.png
    )

)
DEL %temp_file%