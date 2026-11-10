@REM ----------------------------------------------------------------------------
@REM Maven Wrapper script for Windows NT and above
@REM ----------------------------------------------------------------------------

@IF "%DEBUG%" == "" @ECHO OFF
@SETLOCAL

SET ERROR_CODE=0

@REM Set local scope for the variables with windows NT shell
IF "%OS%"=="Windows_NT" @SETLOCAL

SET "BASE_DIR=%~dp0"
IF "%BASE_DIR:~-1%"=="\" SET "BASE_DIR=%BASE_DIR:~0,-1%"

SET "WRAPPER_PROPERTIES=%BASE_DIR%\.mvn\wrapper\maven-wrapper.properties"

IF NOT EXIST "%WRAPPER_PROPERTIES%" (
  ECHO Error: %WRAPPER_PROPERTIES% not found
  GOTO error
)

FOR /F "tokens=1,* delims==" %%A IN ('findstr /R "^distributionUrl=" "%WRAPPER_PROPERTIES%"') DO SET "DISTRIBUTION_URL=%%B"

SET "USER_HOME=%USERPROFILE%"
IF "%USER_HOME%"=="" SET "USER_HOME=%HOMEDRIVE%%HOMEPATH%"
SET "MAVEN_USER_HOME=%USER_HOME%\.m2"
SET "MAVEN_HOME=%MAVEN_USER_HOME%\wrapper\dists\apache-maven-3.9.9"

IF NOT EXIST "%MAVEN_HOME%\bin\mvn.cmd" (
  ECHO Downloading Maven...
  IF NOT EXIST "%MAVEN_USER_HOME%\wrapper\dists" MKDIR "%MAVEN_USER_HOME%\wrapper\dists"
  powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('%DISTRIBUTION_URL%', '%MAVEN_USER_HOME%\wrapper\dists\maven.zip')"
  powershell -Command "Expand-Archive -Path '%MAVEN_USER_HOME%\wrapper\dists\maven.zip' -DestinationPath '%MAVEN_USER_HOME%\wrapper\dists\tmp' -Force"
  FOR /D %%D IN ("%MAVEN_USER_HOME%\wrapper\dists\tmp\*") DO MOVE "%%D" "%MAVEN_HOME%"
  RMDIR /S /Q "%MAVEN_USER_HOME%\wrapper\dists\tmp"
  DEL "%MAVEN_USER_HOME%\wrapper\dists\maven.zip"
)

"%MAVEN_HOME%\bin\mvn.cmd" %*
IF ERRORLEVEL 1 GOTO error
GOTO end

:error
SET ERROR_CODE=1

:end
@ENDLOCAL
EXIT /B %ERROR_CODE%
