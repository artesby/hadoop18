import sys

for line in sys.stdin:
    date, t = line.strip().split(',')
    y, m, d = date.split('-')
    print(y + '\t' + t)