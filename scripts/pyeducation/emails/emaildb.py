"""
    This application will read the mailbox data (mbox.txt) count up the number email messages
    per organization (i.e. domain name of the email address) using a database with the
    following schema to maintain the counts.
"""

import sqlite3

# -- connect to sqlite db
conn = sqlite3.connect('emaildb.sqlite')
cur = conn.cursor()

# -- drop table and create it again
cur.execute('''DROP TABLE IF EXISTS Counts''')
cur.execute('''CREATE TABLE Counts (org TEXT, count INTEGER)''')

# -- open and read file
# fname = raw_input('Enter file name: ')
# if ( len(fname) < 1 ) : fname = 'mbox-short.txt'
fname = "mbox.txt"

# -- count lines number for file
print 'file length -> ', sum(1 for line in open(fname, 'r'))
counter = 0

# -- open and iterate over
fh = open(fname)
for line in fh:

    # -- simple debug output
    if counter % 200 == 0:
        print "processed ->", counter

    # -- increment counter
    counter += 1
    # -- skip uninterested lines
    if not line.startswith('From: ') : continue
    # -- get email address and domain (email part after @ sign)
    domain = line.split()[1].split('@')[1]
    #pieces = line.split()
    #email = pieces[1]
    #print 'email ->', email, 'domain ->', domain
    cur.execute('SELECT count FROM Counts WHERE org = ? ', (domain, ))
    row = cur.fetchone()
    if row is None:
        cur.execute('''INSERT INTO Counts (org, count) VALUES ( ?, 1 )''', (domain, ))
    else :
        cur.execute('UPDATE Counts SET count=count+1 WHERE org = ?', (domain, ))

    # This statement commits outstanding changes to disk each
    # time through the loop - the program can be made faster
    # by moving the commit so it runs only after the loop completes
    #conn.commit()

# -- last simple debug output
print "processed ->", counter
# -- moved outside loop for speed up program
conn.commit()

# https://www.sqlite.org/lang_select.html
sqlstr = 'SELECT org, count FROM Counts ORDER BY count DESC LIMIT 10'
print
print "Counts:"
for row in cur.execute(sqlstr) :
    print str(row[0]), row[1]

cur.close()