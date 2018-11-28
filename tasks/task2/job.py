import re
import string
import unicodedata
from mrjob.job import MRJob
from mrjob.protocol import TextProtocol, ReprProtocol


WORD_RE = re.compile(r"[\w']+")
LATIN_RE = re.compile(r"[a-zA-Z]+")
ABBR_RE = re.compile(r'[\[\(\s\ \b]+[a-zA-Zа-яА-Я]\.[a-zа-я]\.')
# comment this to run part 5
ABBR_RE = re.compile(r'[\[\(\s\ \b]+[a-zA-Zа-яА-я][a-zа-я]\.') 


def split_with_re(line, re_compiler=WORD_RE):
        translator = str.maketrans(string.punctuation, ' '*len(string.punctuation))
        return re_compiler.findall(line.translate(translator))


class MyMRJob(MRJob):
    OUTPUT_PROTOCOL = ReprProtocol


#Part 1
class MaxLenWord(MyMRJob):
    
    def mapper(self, _, line):
        for word in WORD_RE.findall(line):
            yield None, (len(word), word)

    def reducer(self, _, word):
        yield max(word)


#Part 2
class AvgWordLen(MyMRJob):

    def mapper(self, _, line):
        for word in WORD_RE.findall(line):
            yield 1, len(word)

    def reducer(self, _, length):
        n_words = 0
        sum_len = 0
        for l in length:
            n_words += 1
            sum_len += l
        yield None, sum_len / n_words


#Part 3
class MostFrequentWord(MyMRJob):

    def mapper(self, _, line):
        words = [w for w in split_with_re(line, re_compiler=LATIN_RE) if w.isalpha()]
        for word in words:
            yield word.lower(), 1
    
    def combiner(self, word, counts):
        yield None, (sum(counts), word)
    
    def reducer(self, _, words):
        # dict { word : n_word }
        dic = {}
        for count, word in words:
            dic[word] = dic.setdefault(word, 0) + count
        yield max(dic.items(), key=lambda x:x[1])


#Part 4
class FrequentAndCapitalWords(MyMRJob):

    def mapper(self, _, line):
        words = [w for w in split_with_re(line)] #можно добавить if w.isalpha() сюда
        for word in words:
            yield word.lower(), int(word[0].isupper())
    
    def combiner(self, word, counts):
        n_words = 0
        n_capital = 0
        for c in counts:
            n_words += 1
            n_capital = c
        yield None, (n_words, n_capital, word)
    
    def reducer(self, _, words):
        # dict { word : [n_word, n_capital]}
        dic = {}
        for n_words, n_capital, word in words:
            dic[word] = dic.setdefault(word, [0, 0])
            dic[word][0] += n_words
            dic[word][1] += n_capital
        for word in dic:
            if dic[word][0] > 10 and 2 * dic[word][1] > dic[word][0]:
                yield word, dic[word]


#Part 5 & 6
class CommonAbbreviations(MyMRJob):

    threshold = 30
    pattern_compiler = ABBR_RE
    # def __init__(self, threshold, pattern_compiler):
    #     super.__init__()
    #     self.threshold = 5
    #     self.patter_compiler = ABBR_RE1


    def mapper(self, _, line):
        for word in self.pattern_compiler.findall(line):
            processed_word = unicodedata.normalize(
                "NFKD", word.strip().replace("(", "").replace("[", "")
            )
            yield processed_word, 1
    
    def reducer(self, word, count):
        n_words = sum(count)
        if n_words > self.threshold:
            yield word, n_words


if __name__ == '__main__':
    # Part 1
    # MaxLenWord.run()

    # Part 2
    # AvgWordLen.run()

    # Part 3
    # MostFrequentWord.run()

    # Part 4
    # FrequentAndCapitalWords.run()

    # Part 5 & 6
    # CommonAbbreviations.run()