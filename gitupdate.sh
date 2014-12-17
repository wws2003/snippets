#!/bin/sh
 
REPOSRC=https://github.com/wws2003/StudyProjectL2.git
LOCALREPO=.
SUBDIR=MyReadWriteLock
 
# We do it this way so that we can abstract if from just git later on
LOCALREPO_VC_DIR=$LOCALREPO/.git
 
if [ ! -d $LOCALREPO_VC_DIR ]
then
    cd $LOCALREPO
    git init
    git remote add -f origin $REPOSRC
    git config core.sparsecheckout true
    echo $SUBDIR >> .git/info/sparse-checkout
    git pull origin master
else
    cd $LOCALREPO
    git pull
fi
 
# End
