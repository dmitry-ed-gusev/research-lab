"""
 One of first programs in python. Just for education.

 Created: Gusev Dmitrii, 07.03.2017
"""

print("Hello! I'm first program!")

msg = "\nfirst message  "
# -- print as is
print("not stripped -> [" + msg + "]")

# -- print stripped
print("stripped -> [" + msg.strip() + "]")

# -- print titled (first letter in upper case)
print(msg.title())

# -- some other things with messages
msg = "\nsecond\n\tone"
print(msg)
print(msg.title())

# -- more things with strings
age = 30
name = 'Eddie'
print("I'm", name, "and I'm", age)
print("Name: " + name + ", age: " + str(age))

