#!/bin/bash
function checkY() {
  if [[ "${1}" != "y" ]]; then
    exit
  fi
  echo "\n"
}
data_file=Tagfile
TIPS="确认请输入 y, 否则退出! "

echo '********** 打tag之前请认真阅读以下打tag规范 ********** \n'
echo '1.请确认开发分支为对应【tag或者旧分支】中切出来的【新分支】\n'
read -n 1 -p "${TIPS}" CONFIRM
checkY ${CONFIRM}
echo '2.请确认是【最新代码】\n'
read -n 1 -p "${TIPS}" CONFIRM
checkY ${CONFIRM}
echo '3.请确认tag数据文件格式是否符合要求：\n'
echo '    【第1行】为打tag的分支名\n'
echo '    【第2行】为tag的版本，注意V开头\n'
echo '    【第3行】为tag的备注\n'
read -n 1 -p "${TIPS}" CONFIRM
checkY ${CONFIRM}
echo 'Good!\n'
BRANCH_ORIGIN=`head -n 1 $data_file`

TAG_VERSION=`sed -n '2,2p' $data_file`
NEW_TAG_VERSION=$TAG_VERSION
if [ $BRANCH_ORIGIN == 'release/tro-web_release_prod' ]; then
    NEW_TAG_VERSION=${TAG_VERSION}".prod"
fi

if [ $BRANCH_ORIGIN == 'release/tro-web_release_poc' ]; then
    NEW_TAG_VERSION=${TAG_VERSION}".poc"
fi
echo '********** 打Tag的分支 ********** \n'$BRANCH_ORIGIN

echo '\n********** tag版本 ********** \n'${NEW_TAG_VERSION}

TAG_REMARK=""
while read line
do
#echo $line
if [[ $line != $BRANCH_ORIGIN && $line != $TAG_VERSION ]];
then
TAG_REMARK=${TAG_REMARK}${line}'\n'
#echo ${remark} ${line}
fi
done < $data_file

echo '\n********** 备注 ********** \n'$TAG_REMARK
read -n 1 -p "${TIPS}" CONFIRM
checkY ${CONFIRM}

echo 'OK，开始打tag\n'

git checkout ${BRANCH_ORIGIN}
# 8. 打 tag
git tag -a "${TAG_VERSION}" -m "${TAG_REMARK}"
# 推送 tag
git push origin "${TAG_VERSION}"
echo 'tag远程推送成功！'
sleep 3
# 9. git tag 列表时间倒序
git tag -n --sort=-v:refname

#删除tag命令
#git tag -d ${TAG_VERSION}
#git push origin :refs/tags/${TAG_VERSION}