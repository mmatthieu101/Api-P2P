sudo su - user2 << EOF
    descriptionCommit=$1
    cd /home/device/123456 && git init && git add file.txt && git commit -m $1
   
EOF
