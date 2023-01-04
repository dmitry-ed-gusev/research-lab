# My notes for the Assignment for the Module 05

1. On my Mac machines I have working: Pentaho Data Integration 9.2.0 wit Azul JDK 8.0.352
   (sdkman - 8.0.352-zulu). Only this ver of Pentaho working withour issues, use starting script
   `data-integration/spoon.sh`
2. In order to get Oracle DBMS I've used [Oracle VM Appliance](https://www.oracle.com/database/technologies/databaseappdev-vm.html). Some notes:
    - In order to communicate with the host - add Host-only Network both in the Oracle VIrtual Box and on the machine (adapter)
    - User password: oracle/oracle, for DBMS: system/oracle
    - VM has all necessary software installed
    - DBMS connection parameters:
      - user/pwd: system/oracle
      - host: &lt;your VM IP&gt;
      - port: 1521
      - Service name: orcl
3. For Pentaho Data Integration (PDI) to connect to Oracle DBMS - use the [ojdbc8.jar]() driver, put it in the folder **data-integration/lib**
4. ???
