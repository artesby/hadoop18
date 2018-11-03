from mrjob.job import MRJob
from mrjob.protocol import TextProtocol
import re

WORD_RE = re.compile(r"[\w']+")


class MRWordFreqCount(MRJob):
    
    def mapper(self, _, line):
        for word in WORD_RE.findall(line):
            if len(word) > 0:
                self.increment_counter('our_counter', str(len(word)), 1)
            yield word.lower(), 1

    def combiner(self, word, counts):
        yield word, sum(counts)

    def reducer(self, word, counts):
        yield word, sum(counts)
        
        
if __name__ == '__main__':    
    MRWordFreqCount.run()