@echo off
rem - set console encoding to UTF-8
chcp 65001
rem - call SocialCrawler utility
java -jar @JAR_NAME@.jar -config crawler.properties -logLevel debug -search "Гусев Дмитрий" -output crawler.out -forceOutput