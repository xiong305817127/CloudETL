#!/bin/bash

olddir=/usr/hdp/2.5.3.0-37/cloudetl/v1
rootPath=$(cd `dirname $0`; pwd)

if [ x$1 = xhelp ];then
	echo "
		command [olddir [newdir]]
		
		   olddir - old package path  ,default ${olddir}
		   newdir - new zip package root path( the directory where the cloudetl.zip package is located) ,default ${rootPath}
	
	"
	exit 0
fi
if [ ! x$1 = x ];then
	olddir=$1
fi
newdir=${rootPath}/cloudetl
if [ ! x$2 = x ];then
	newdir=$2/cloudetl
fi
targetPath=${rootPath}/update



if [[ -e update*.zip ]] ; then
rm -fr update*.zip
fi
if [[ -d ${newdir} ]] ; then
rm -fr ${newdir}
fi 

unzip -o ${newdir}.zip

paths=
i=0
function getdir(){
    for element in `ls $1`
    do  
        dir_or_file=$1"/"$element
        if [ -d $dir_or_file ]
        then 
			if [ ! "./system/karaf/caches" = $dir_or_file ] && [ ! "./logs" = $dir_or_file ] 
            then
				getdir $dir_or_file
			fi	
        else
            paths[i]=$dir_or_file
            i=$[i+1]
        fi  
    done
}

targetUpdateBin=$targetPath/update
targetUpdateBinFile=ExistingFile
[[ ! -d $targetPath ]] && mkdir $targetPath
[[ ! -d $targetUpdateBin ]] && mkdir $targetUpdateBin

echo "================================================="
echo olddir:$olddir
echo newdir:$newdir
echo targetPath:$targetPath
echo "================================================="

cd $newdir
getdir .
echo ${#paths[*]}
for var in ${paths[@]};  
do  
	newpath=${newdir}/$var
	oldpath=${olddir}/$var
	
	diff $newpath $oldpath 1>/dev/null 2>&1 && result=0 || result=1
	if [[ ! -e $oldpath ]] || [[  "$result" == 1 ]] ;then
		parentDir=${targetPath}/$var
		parentDir=${parentDir%/*}
		[[ ! -d $parentDir ]] && mkdir -p $parentDir
		echo copy:$newpath
		\cp -fR $newpath  ${targetPath}/$var ;
	else
		echo $var >>  ${targetUpdateBin}/${targetUpdateBinFile}
	fi
	
done  

(
cat <<EOF
#!/bin/bash

updateDir=\$(cd \`dirname \$0\`; pwd)

packageName=cloudetl
packageDir=\${updateDir%/*}
rootDir=\${packageDir%/*}
oldPackage=\${rootDir}/\${packageName}
updateFile=\${updateDir}/ExistingFile

cd  \${rootDir}
if [[ -d \${oldPackage} ]] ; then
rm -fr \${oldPackage}
fi 
unzip -o \${packageName}.zip

while read var
do
	oldpath=\$oldPackage/\$var
	if [ ! -e \${oldpath} ];then 
		echo -n "\${oldpath} is not exist,Are you sure you want to continue(y or n)?"
		read Arg
		if [ \$Arg = "n" ];then
			exit 1
		fi
	fi
	

	parentDir=\${packageDir}/\$var
	parentDir=\${parentDir%/*}
	[[ ! -d \$parentDir ]] && mkdir -p \$parentDir
	echo copy:\$oldpath
	cp -R \$oldpath  \${packageDir}/\$var ;

done  < \$updateFile

cd  \${rootDir}
rm -fr \${packageName}
[[ ! -d \$packageName ]] && mkdir \$packageName
cp -rf \${packageDir}/*  \${packageName}/

zip -r \${packageName}-new.zip  \${packageName}/*
rm -fr \${packageName}
EOF
) >${targetUpdateBin}/update.sh

chmod 777  ${targetUpdateBin}/update.sh


updatePackageName=`ls -l ${olddir} | awk '/version*/ { print $9 }'` 
updatePackageName=update-${updatePackageName}


cd  ${targetPath%/*}
zip -r ${newdir%/*}/${updatePackageName}.zip  ${targetPath##*/}/*

rm -fr ${targetPath}
rm -fr ${newdir}





