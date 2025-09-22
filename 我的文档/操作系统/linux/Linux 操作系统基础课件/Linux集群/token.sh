#!/bin/bash
to=$1
subject=$2
text=$3
curl 'https://oapi.dingtalk.com/robot/send?access_token=758cefbfde6108910a29fad7e35edd9ff76255df5ac16871ad7cf03b2262a7fc' \
-H 'Content-Type: application/json' \
-d '
{"msgtype": "text",
"text": {
"content": "'"$text"'"
},
"at":{
"atMobiles": [ "'"$1"'" ],
"isAtAll": false
}
}'
