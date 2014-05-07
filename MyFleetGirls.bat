
@echo off
cd /d %~dp0
java -jar update.jar
java -jar MyFleetGirls.jar
pause
