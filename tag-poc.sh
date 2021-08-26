# !/bin/bash

# 远程分支
BRANCH_ORIGIN_POC="release/takin-web_release_poc"
BRANCH_ORIGIN_PROD="origin/release/takin-web_release_prod"

# 切换到远程 poc
git checkout ${BRANCH_ORIGIN_POC}

# 合并一下 prod 的代码, 推送
#git merge ${BRANCH_ORIGIN_PROD}
#git push

# tag 名字输入
read -p "请输入 tag 名称(不需要输入 v, poc): v" TAG_NAME
echo ""

# tag 名字补充
if [[ ${TAG_NAME} != v* ]]; then
  TAG_NAME="v${TAG_NAME}"
fi
TAG_NAME="${TAG_NAME}.poc"

# 确认 tag 名称
read -n 1 -p "请确认 tag 名称: \"${TAG_NAME}\", 确认请输入 y, 否则退出!" CONFIRM_TAG
echo ""

if [[ "${CONFIRM_TAG}" != "y" ]]; then
  exit
fi

echo ""

# tag 备注
read -p "请输入 tag 备注(默认\"\"): " TAG_REMARK
echo ""

if [ -z "${TAG_REMARK}" ]; then
  TAG_REMARK=""
fi

echo ""

# 确认 tag 名称
read -n 1 -p "请确认 tag 备注: \"${TAG_REMARK}\", 确认请输入 y, 否则退出!" CONFIRM_REMARK
echo ""

if [[ "${CONFIRM_REMARK}" != "y" ]]; then
  exit
fi

# 打 tag
git tag -a "${TAG_NAME}" -m "${TAG_REMARK}"

# 推送 tag
git push origin "${TAG_NAME}"

# git tag 时间倒序
git tag -n --sort=-v:refname