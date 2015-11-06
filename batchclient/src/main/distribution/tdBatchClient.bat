@echo off

REM @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
REM
REM 	Author : krishnasingh07@gmail.com
REM
REM 	local Connection Properties 
REM 	( You can override Global properties defined in qc.connection.properties here )
REM
REM 	Do not leave spaces between param=value
REM 	Do not use " or ' around variables
REM
REM 
REM     Behavior Modification Properties
REM     ---------------------------------
REM
REM     1) In order to Create missing Test-Set Folders, Test-Sets and Test-Instances set 
REM        system property "create_if_not_found" to "true" in the ARGS
REM
REM        Example : set ARGS="-Dtd.url=%td_url% ....... -Dcreate_if_not_found=true"
REM
REM     2) In order to, by default, update First test Instance if multiple testInstances
REM        of test found in Test-Set set system property "use_first_instance" to "true"
REM        in the ARGS
REM
REM        Example : set ARGS="-Dtd.url=%td_url% ....... -Duse_first_instance=true"
REM
REM @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

setlocal

set td_url=
set td_domain=
set td_project=
set td_user=
set td_password=



IF EXIST %~dp0\lib\qc.connection.properties ( goto Run ) else ( goto Validate )

:Validate

if [%td_domain%]==[] ( echo "td_domain" NOT SET. Set the Parameter and Retry && goto end )
if [%td_project%]==[] ( echo "td_project" NOT SET. Set the Parameter and Retry && goto end )
if [%td_user%]==[] ( echo "td_user" NOT SET. Set the Parameter and Retry && goto end )
if [%td_password%]==[] ( echo "td_password" NOT SET. Set the Parameter and Retry && goto end )

:Run

set ARGS="-Dtd.url=%td_url% -Dtd.domain=%td_domain% -Dtd.project=%td_project% -Dtd.user=%td_user% -Dtd.password=%td_password%"
java %ARGS:"=% -Duse_first_instance=true -jar %~dp0lib\qc-notifier-batchclient-*.jar %*

:end
endlocal 