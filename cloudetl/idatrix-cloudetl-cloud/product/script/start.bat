@echo off 
for /f "tokens=4" %%a in ('route print^|findstr 0.0.0.0.*0.0.0.0.*192') do (  set IP=%%a ) 
set Port=8080

set param=config\\cloudetl-config.xml 
if not exist "%param%" ( 
set default_exe=true
) else (
findstr "masterIp" "%param%" >nul 2>&1 && set default_exe=true
)

if "%default_exe%"=="true" (
set param= %IP%  %Port%  
echo "Visit: %IP%:%Port%"
)

::set DEBUG= -Xdebug -Xrunjdwp:transport=dt_socket,address=9999,server=y,suspend=n 
::java %DEBUG% -jar etl.jar %param%
java %DEBUG%  -Dfile.encoding=UTF-8  -DETL_LOG_FILENAME=cloudetl  -jar launcher/launcher.jar  %param%

pause