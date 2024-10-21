1. IP address validation  (http://www.mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression)

IP Address Regular Expression Pattern:

^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.
([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$

Description of above pattern:

^    #start of the line
 (    #  start of group #1
   [01]?\\d\\d? #    Can be one or two digits. If three digits appear, it must start either 0 or 1
    #    e.g ([0-9], [0-9][0-9],[0-1][0-9][0-9])
    |    #    ...or
   2[0-4]\\d  #    start with 2, follow by 0-4 and end with any digit (2[0-4][0-9]) 
    |           #    ...or
   25[0-5]      #    start with 2, follow by 5 and ends with 0-5 (25[0-5]) 
 )    #  end of group #2
  \.            #  follow by a dot "."
....            # repeat with 3 times (3x)
$    #end of the line

Whole combination means, digit from 0 to 255 and follow by a dot “.”, repeat 4 time and ending with no dot “.”
Valid IP address format is “0-255.0-255.0-255.0-255″.

-----------------------------------------------------------------------------------------------------------------------

2.