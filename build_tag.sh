# !/bin/bash

# 测试
# 打 tag 操作
# 操作步骤

# 输入环境
# 自动切换到分支
# 输入 tag 名称
# 输入 tag 备注
# 自动创建 tag
# 自动推送 tag

# 方法, 确认
function checkY() {
  if [[ "${1}" != "y" ]]; then
    exit
  fi

  echo "\n"
}

TIPS="确认请输入 y, 否则退出! "

# 1. 提示
echo "项目冲突, 请自行解决一下, 该脚本暂时只负责打 tag! \n"
read -n 1 -p "${TIPS}" CONFIRM
checkY ${CONFIRM}

# 环境输入
read -n 1 -p "请输入需要打 tag 的环境, prod 请输入 1, poc 请输入 2. " ENV_NUM
ENV=""

if [[ ! "${ENV_NUM}" =~ ^[0-9]+$ ]]; then
  echo "环境错误, 退出!"
  exit

elif [ ${ENV_NUM} -eq 1 ]; then
  ENV="prod"
elif [ ${ENV_NUM} -eq 2 ]; then
  ENV="poc"

else
  echo "环境错误, 退出!"
  exit
fi

# 2. 远程分支, 切换到远程
BRANCH_ORIGIN="release/takin-web_release_${ENV}"
git checkout ${BRANCH_ORIGIN}

echo "\n"
# 3. tag 名字输入
read -p "请输入 tag 名称(不需要输入 v, 环境): v" TAG_NAME
# 4. tag 名字补充
if [[ ${TAG_NAME} != v* ]]; then
  TAG_NAME="v${TAG_NAME}"
fi
TAG_NAME="${TAG_NAME}.${ENV}"

# 5. 确认 tag 名称
echo "请确认 tag 名称: \"${TAG_NAME}\""
read -n 1 -p "${TIPS}" CONFIRM_TAG
checkY ${CONFIRM_TAG}

# 6. tag 备注输入
read -p "请输入 tag 备注(默认\"\"): " TAG_REMARK
if [ -z "${TAG_REMARK}" ]; then
  TAG_REMARK=""
fi

# 7. 确认 tag 名称
echo "请确认 tag 备注: \"${TAG_REMARK}\""
read -n 1 -p "${TIPS}" CONFIRM_REMARK
checkY ${CONFIRM_REMARK}

# 8. 打 tag
git tag -a "${TAG_NAME}" -m "${TAG_REMARK}"
# 推送 tag
git push origin "${TAG_NAME}"
# 9. git tag 列表时间倒序
git tag -n --sort=-v:refname