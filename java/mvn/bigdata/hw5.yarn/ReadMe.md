# Tasks:

1. Write your own YARN Applications with a web page in ApplicationMaster container and container allocation for some
   computations* by click on button on the page (use for this YARN API, no Tez, no Slider);
   - Computations could include sorting of large array with 1 000 000 000 integer items
2. Provide ability to change several allocated resources (RAM, priority, number of containers and etc) on the
 web page and don’t forget logger for all your actions 


# Expected outputs:
	0. ZIP-ed src folder with your implementation
	1. Screenshot of running web application
	2. Attached jmap histo of all your process
	3. Attached jstack of your main process
	4. Attached Result in HDFS (Sorted integers top-100)
	5. Scheanshot of your settings for allocated resources

# Acceptance criteria
    - Custom yarn application is written
    - Provided simple (Web or Rest) API to change accocation resocure settings
    
# Hints:
    - There are two ways to write yarn appliation : 
        - https://hadoop.apache.org/docs/current/hadoop-yarn/hadoop-yarn-site/WritingYarnApplications.html
        - https://spring.io/guides/gs/yarn-basic/

