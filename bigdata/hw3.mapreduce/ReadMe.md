#### DataSet
- [access logs](access_logs.rar) 

# Tasks:

1. Write MR job:
    - to count average bytes per request by IP and total bytes by IP
    - try to use combiner
    - output is CSV file with rows as next:
```
    IP,175.5,109854
```       
2. Add MR Unit tests for your Mapper/Reducer
3. Modify previous MR job to use custom Writable data type
4. Save output as Sequence file compressed with Snappy (key is IP, and value is custom object for avg and total size)
5. Use counters to get stats how many users of IE, Mozzila or other were detected and print them in STDOUT of Driver and make screenshot #2
 (parse it from UserAgent: 
```
ip13 - - [24/Apr/2011:04:41:53 -0400] "GET /logs/access_log.3 HTTP/1.1" 200 4846545 "-" "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)" 
```
where:
```    
     UserAgent - Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)
     bytes - 4846545
```
6. Read content of compressed file from console using command line (screenshot #3):
```
    hadoop fs –libjars <custom-jar> -text <src-on-hdfs> 
```

# Expected outputs:
	0. ZIP-ed src folder with your implementation
	1. Screenshot of successfully executed tests
	2. Screenshot #1 of successfully executed job from point 1
	3. Screenshot #2 of usage counters 
	4. Screenshot #3 of reading compressed content

# Acceptance criteria
    - Map Reduce API is used
    - Custom Type is defined and used
    - Counters is used
# Hints:
    - Use third party lib to parse UserAgent,  for example: 
        - https://github.com/HaraldWalker/user-agent-utils

