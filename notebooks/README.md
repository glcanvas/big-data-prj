Сначала запускается kafka_router, затем toxicity_check

Для корректной работы Analyzer.py должен лежать рядом с toxicity_check

Оба ноутбука должны быть смонтированы в /home/jovyan в контейнер с pyspark

также нужно установить pymongo (pip install pymongo)
