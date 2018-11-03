# Задание 1

Загрузите [файл со статьями статей из Википедии](https://yadi.sk/d/ObKNNcaFWEsK-w). Каждая строка в файле имеет следующий формат:

   >URL статьи `<tab>` название статьи `<tab>` текст 

Распакуйте и скопируйте файл на `HDFS` с помощью комманды:
```bash
    hadoop fs -put wiki.txt /user/root/wiki.txt        
```

Сохраните файл `jop.py` следующего содержания:
```python
from mrjob.job import MRJob
from mrjob.protocol import TextProtocol
import re

WORD_RE = re.compile(r"\w+")

class MRWordFreqCount(MRJob):
    OUTPUT_PROTOCOL = TextProtocol
    
    def mapper(self, _, line):
        for word in WORD_RE.findall(line):           
            yield word.lower(), 1

    def combiner(self, word, counts):
        yield word, sum(counts)

    def reducer(self, word, counts):
        yield word, str(sum(counts))
        
        
if __name__ == '__main__':    
    MRWordFreqCount.run()
```

Запустите  задачу с использованием локального движка `MRJob`

```bash
time python3 job.py wiki.txt -o result_local
```
затем с помощью `hadoop`

```bash
time python3 job.py -r hadoop hdfs:///user/root/wiki.txt -o hdfs:///user/root/result_hadoop
```

затем скопируйте 50 первых строчек из файла `wiki.txt`:

```bash
head -n 50 wiki.txt > wiki_trunc.txt
hadoop fs -put wiki_trunc.txt /user/root/wiki_trunc.txt 
```

и повторите вычисления на новом файле. Какие выводы можно сделать, сравнивая время работы этих 4 тестов.