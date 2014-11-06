@echo off
title MyFleetGirls
SET MFG_HOME=%~dp0
If Defined b2eprogrampathname set MFG_HOME=%b2eprogrampathname%
cd /d %MGF_HOME%

REM 環境変数 JAVA_HOME または MFG_JAVA_HOME が設定されていれば PATH に追加設定
If Defined JAVA_HOME path %JAVA_HOME%\bin\;%PATH%
If Defined MFG_JAVA_HOME path %MFG_JAVA_HOME%\bin\;%PATH%

REM java コマンドを実行し存在確認
java.exe -version 2> NUL
If ErrorLevel 1 goto JavaError

REM update.jar で関連ファイルを更新後、MyFleetGirls.jar ( clinet 本体 ) を実行
java.exe -jar update.jar
java.exe -jar MyFleetGirls.jar
echo MyFleetGirls を終了します
pause > NUL
EXIT 0

:JavaError
echo JAVA ランタイムが見つかりませんでした。README.txt を参照し導入するか、環境変数の設定を行ってください。
pause > NUL
EXIT 1
