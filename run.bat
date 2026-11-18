@echo off
rem Compila e executa o AlertaBairro (precisa de JDK 17+). Rode na pasta do projeto.
if not exist out mkdir out
javac -encoding UTF-8 -cp lib\h2.jar -sourcepath src -d out src\ui\Main.java || exit /b 1
java -cp out;lib\h2.jar ui.Main
