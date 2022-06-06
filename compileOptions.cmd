:MENU_START
@echo off
cls
set INPUT=false
set "MENU_OPTION="
set "OPTION1_INPUT="
set "OPTION2_INPUT="
echo +===============================================+
echo . BATCH SCRIPT - COMPILE OPTIONS                .
echo +===============================================+
echo .                                               .
echo .  [1] LOCAL/AZURE                              .
echo .  [2] LOCAL                                    .
echo .  [3] PERSISTENT H2                            .
echo .  [4] EXIT                                     .
echo .                                               .
echo +===============================================+
set /p MENU_OPTION="OPTION: "

IF %MENU_OPTION%==1 GOTO OPTION1
IF %MENU_OPTION%==2 GOTO OPTION2
IF %MENU_OPTION%==3 GOTO OPTION3
IF %MENU_OPTION%==4 GOTO OPTION3
IF %MENU_OPTION%==5 GOTO OPTION3
IF %INPUT%==false GOTO DEFAULT

:OPTION1
echo MESSAGE: STARTING UP ...
mvn spring-boot:run "-Dspring-boot.run.arguments=--spring_datasource_password=SwoAcademy2022Java!"
timeout 2 > NUL
GOTO MENU_START

:OPTION2
echo MESSAGE: STARTING UP ...
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
timeout 2 > NUL
GOTO MENU_START

:OPTION3
echo MESSAGE: STARTING UP ...
mvn spring-boot:run "-Dspring-boot.run.profiles=persistent"
echo Bye
timeout 2 > NUL
exit /b

:OPTION4
set INPUT=true
echo Bye
timeout 2 > NUL
exit /b

:DEFAULT
echo Option not available
timeout 2 > NUL
GOTO MENU_START