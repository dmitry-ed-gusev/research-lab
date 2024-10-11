=============================================================================================
On your local machine you should design and build a sample application based on Apache Storm,
MarkLogic and ActiveMQ. The application must contain 2 Apache Storm topologies. First topology
must read input huge txt file (~500Mb) line by line, wrap each line with separate XML document
and store each document into MarkLogic database. Then notification per each document must be
sent over JMS to the second topology. The second topology must receive JMS notification with
URI to the stored document, read XML document from MarkLogic, extract original line and append
the line to output file.
You should emulate occasional failure of some Apache Storm components during file processing
and be able to prove correctness of your program and eventual consistency of database state,
the number of JMS messages sent and the number of rows in output file.
=============================================================================================

Useful XQueries (for Marklogic sever).

1. (: delete all documents in current database :)
   for $doc in doc() return xdmp:document-delete(xdmp:node-uri($doc))
   
2. (: - clear all forests for database :)
   xdmp:forest-clear(xdmp:database-forests(xdmp:database("Documents")))

3. (: - number of documents in current database - :)
   xdmp:estimate(doc())

=============================================================================================