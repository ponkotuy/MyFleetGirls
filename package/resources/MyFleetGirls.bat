@echo off
title MyFleetGirls
cd /d %~dp0

REM 環境変数 JAVA_HOME または MFG_JAVA_HOME が設定されていれば PATH に追加設定
if Defined JAVA_HOME path %JAVA_HOME%\bin\;%HOME%
if Defined MFG_JAVA_HOME path %MYFFLEETGIRSL_JAVA_HOME%\bin\;%HOME%

REM java コマンドを実行し存在確認
java -version 1> NUL 2> NUL
if ErrorLevel 1 goto JavaError

REM update.jar で関連ファイルを更新後、MyFleetGirls.jar ( clinet 本体 ) を実行
java -jar update.jar
java -jar MyFleetGirls.jar
echo "MyFleetGirls を終了します"
pause > NUL
EXIT 0

:JavaError
echo "JAVA ランタイム見つかりませんでした。README.txt を参照し、適切に導入するか環境変数の設定を行ってください。"
pause > NUL
EXIT 1
