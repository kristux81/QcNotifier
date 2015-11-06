TDNotifier Batch Client
------------------------


Requirements
-------------
-Java 32 or 64 Bit JRE 1.6 or later



Supported Platforms
---------------------
- Linux / UNIX ( those supporting SUN Java )
- Windows ( those supporting JRE 6 )



Configuration ( before using the client ) 
------------------------------------------

The following parameters must be set at shell environment level :

td_url, 
td_domain,
td_project,
td_user,
td_password

in files tdBatchClient.bat or tdBatchClient.sh


OR
---- 


The following parameters must be set in qc.connection.properties file in "lib" folder :

td.url,
td.domain,
td.project,
td.user,
td.password




Command
-------
- Windows    :  tdBatchClient.bat <input_file>
- Linux/Unix :  sh tdBatchClient.sh <input_file>
                OR
                chmod 755 tdBatchClient.sh ; ./tdBatchClient.sh <input_file>
                

Input files
-----------
See docs/sample_input.txt
    docs/input_format_rules.txt

    
    