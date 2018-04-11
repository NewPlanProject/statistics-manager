#!/bin/sh

filename=$(basename $0)
appname="${filename%%.*}"
echo "微服务:${appname}"

rm -rf  src/main/docker/*.jar

#修改配置文件
echo "将src/main/resources/bootstrap.properties localhost = "$2
sed -i "s#localhost#$2#g" src/main/resources/bootstrap.properties

mvn clean package -Dmaven.test.skip=true -P$1
if [ $? -eq 0 ]; then
  echo "build sucess!"

else
  echo  "build fail!"
  exit 0
fi
echo "done!"

sec=`grep ^info.version target/classes/bootstrap.properties |cut -d$'=' -f2`
tag="$1-${sec}"
image=${appname}:${tag}
echo "镜像名称 : ${image}"

cp -f target/*.jar  src/main/docker/
cp -f target/classes/Dockerfile  src/main/docker/
cp -f /usr/share/zoneinfo/Asia/Shanghai src/main/docker/

    echo "默认 build  $1 环境 start"
    #repository="registry.user.pcloud.citic.com/zxyw/iiot"
    repository="registry-vpc.cn-beijing.aliyuncs.com/iiotprd"

    fullimage="${repository}/${image}"
    echo "私服镜像名称 : ${fullimage}"

    echo "docker build ${fullimage} 开始......"
    docker build -t  ${fullimage}  -f src/main/docker/Dockerfile ./src/main/docker/
    echo "docker build ${fullimage} 结束......"

    echo "docker push ${fullimage} 开始......"
    docker push ${fullimage}
    echo "docker push ${fullimage} 结束......"

   #生产   和  开发使用不同的yml文件

if [ $1 = "prod" ]
    then
    sed -i "s#URL#${fullimage}#g"  ${appname}-$1.yml
elif [ $1 = "beta" ];
    then
    sed -i "s#URL#${fullimage}#g"  ${appname}-$1.yml
    echo "beta!"
elif [ $1 = "test" ];
    then
    sed -i "s#URL#${fullimage}#g"  ${appname}-$1.yml
    echo "test!"
elif [ $1 = "dev" ];
    then
    sed -i "s#URL#${fullimage}#g"  ${appname}-$1.yml
    echo "dev!"
else
   sed -i "s#URL#${fullimage}#g"  ${appname}.yml
fi

    echo "${fullimage} 删除镜像开始......"
    #docker rmi  ${fullimage}
    echo "${fullimage} 删除镜像开始......"

    echo "默认 build  $1 环境 end"