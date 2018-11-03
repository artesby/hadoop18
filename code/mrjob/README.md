# MRJob

В файле `job.py` простой код для подсчета количества слов в тексовых данных. Для того, чтобы запустить локально:
```bash
python3 job.py < ../../data/weather.csv
```


Для того, чтобы запустить на кластере `Hadoop`:
1. Запустите bash в контексте контейнера
```bash
docker exec -it hadoop-sandbox bash
```

2. Скопируйте файл на `HDFS`
```bash 
hadoop fs -put /data/data/weather.csv /user/root/weather.csv
```

3. Запустите `job.py` с нужным ключем
```bash
python3 job.py -r hadoop  hdfs:///user/root/weather.csv -o hdfs:///user/root/weather_res
```

4. Смотрим на результат
```bash
hadoop fs -cat "hdfs:///user/root/weather_res/*"
```