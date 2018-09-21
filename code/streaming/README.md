yarn jar /usr/lib/hadoop-mapreduce/hadoop-streaming.jar\
 -input /user/hdfs/weather.csv\
 -output /user/hdfs/stat\
 -file mapper.py\
 -file reducer.py\
 -mapper "python mapper.py"\
 -reducer "python reducer.py"