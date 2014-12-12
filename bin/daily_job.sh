#! /bin/sh

day=`date -d "1 days ago" +%Y%m%d`
#day=`date +%Y%m%d`

model_file=/data/odin_model/${day}.gz2
if [ -f $model_file ]; then
  cd /data/odin_model && tar -xjf $model_file
fi

curl "http://127.0.0.1:8080/thor/tool?action=model&day=${day}"

