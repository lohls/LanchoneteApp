@if "%DEBUG%" == "" @echo off
setlocal
set DIR=%~dp0
set APP_BASE_NAME=%~n0
set APP_HOME=%DIR%
set DEFAULT_JVM_OPTS=
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
if exist "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" (
    rem ok
) else (
    echo Missing gradle-wrapper.jar. Android Studio will regenerate it.
)
"%JAVA_HOME%\bin\java.exe" %DEFAULT_JVM_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
