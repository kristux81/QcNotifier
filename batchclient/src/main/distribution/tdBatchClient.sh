#!/bin/sh

# ####################################################################################
#
# 	Author : krishnasingh07@gmail.com
#
# 	local Connection Properties 
# 	( You can override Global properties defined in qc.connection.properties here )
#	
#       Do not leave spaces between param=value
# 	Do not use " or ' around variables
#
#
#     Behavior Modification Properties
#     ---------------------------------
#
#     1) In order to Create missing Test-Set Folders, Test-Sets and Test-Instances set 
#        system property "create_if_not_found" to "true" in the ARGS
#
#        Example : ARGS="-Dtd.url=$td_url ....... -Dcreate_if_not_found=true"
#
#     2) In order to, by default, update First test Instance if multiple testInstances
#        of test found in Test-Set set system property "use_first_instance" to "true"
#        in the ARGS
#
#        Example : ARGS="-Dtd.url=$td_url ....... -Duse_first_instance=true"
#
# ##################################################################################### 

td_url=
td_domain=
td_project=
td_user=
td_password=



# Validate environment
function validate {
  if [ -z "$2" ]
  then
    echo $1 NOT SET. Set the Parameter and Retry.
    exit 1
  fi
}

if [ ! -f "$PWD/lib/qc.connection.properties" ]
then
	validate td_domain $td_domain
	validate td_project $td_project
	validate td_user $td_user
	validate td_password $td_password
fi	

ARGS="-Dtd.url=$td_url \
      -Dtd.domain=$td_domain \
      -Dtd.project=$td_project \
      -Dtd.user=$td_user \
      -Dtd.password=$td_password \
      -Duse_first_instance=true"

java $ARGS -jar lib/qc-notifier-batchclient*.jar $@
