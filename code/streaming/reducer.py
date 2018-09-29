import sys

def compute_mean(vals):
    return sum(vals) / len(vals)

prev_k = None
vals = []
for line in sys.stdin:
    k, v = line.strip().split('\t')
    if prev_k != k and (prev_k is not None):
        mean = compute_mean(vals)
        vals = []
        print('%s\t%.3f' % (prev_k, mean))
    vals.append(float(v))
    prev_k = k
        
mean = compute_mean(vals)
print('%s\t%.3f' % (prev_k, mean))

