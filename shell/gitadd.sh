sudo su - user2 << EOF
    descriptionCommit=$1
    cd /opt/gitrepo/ && mkdir abc && cd abc && git init && echo abc > abc.txt && git add abc.txt && git commit -m $1
   
EOF
