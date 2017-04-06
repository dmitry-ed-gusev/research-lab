import re
print(sum([int(x) for x in re.findall("[0-9]+", open("regex_sum_191791.txt").read())]))