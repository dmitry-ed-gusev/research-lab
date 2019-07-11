import json
import sqlite3

conn = sqlite3.connect('rosterdb.sqlite')
cur = conn.cursor()

# Do some setup
cur.executescript('''
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Member;
DROP TABLE IF EXISTS Course;

CREATE TABLE User (
    id     INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
    name   TEXT UNIQUE
);

CREATE TABLE Course (
    id     INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
    title  TEXT UNIQUE
);

CREATE TABLE Member (
    user_id     INTEGER,
    course_id   INTEGER,
    role        INTEGER,
    PRIMARY KEY (user_id, course_id)
)
''')

# fname = raw_input('Enter file name: ')
# if ( len(fname) < 1 ) : fname = 'roster_data.json'
fname = 'roster_data.json'

# [
#   [ "Charley", "si110", 1 ],
#   [ "Mea", "si110", 0 ],

str_data = open(fname).read()
json_data = json.loads(str_data)

# -- iterate over parsed json data, get info and put it into db
for entry in json_data:

    name = entry[0]
    title = entry[1]
    role = entry[2]
    print(name, title, role)

    # -- insert user and get last inserted ID
    cur.execute('''INSERT OR IGNORE INTO User (name) VALUES ( ? )''', (name, ))
    cur.execute('SELECT id FROM User WHERE name = ? ', (name, ))
    user_id = cur.fetchone()[0]

    # -- insert course and get last inserted ID
    cur.execute('''INSERT OR IGNORE INTO Course (title) VALUES ( ? )''', (title, ))
    cur.execute('SELECT id FROM Course WHERE title = ? ', (title, ))
    course_id = cur.fetchone()[0]

    # -- insert row into join table for many-to-many relationship
    cur.execute('''INSERT OR REPLACE INTO Member (user_id, course_id, role) VALUES ( ?, ?, ? )''', (user_id, course_id, role))

    #conn.commit()

# -- commit when cycle finished - for speed up
conn.commit()