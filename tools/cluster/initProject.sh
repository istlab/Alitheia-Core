#! /bin/sh

###############################################
# initialize a repository
###############################################
#
# input: existing xml configuration for project
#
. ./config


read_default () {
# arg1 = string to display, arg2=default value 
 read -p "$1 (default is $2) :" ans
 if [ "$ans" = "" ]; then
   echo $2
 else
   echo $ans
 fi
}

scriptname=`basename $0`

projectslist=""
case "$1" in 
	all)
	   echo "Processing all files in $project_lists"
	   projectslist=`ls $project_lists/*.xml`
	;;
	"")
	   echo "
***************************
* SQO-OSS - $scriptname
***************************
Initialize a project: create a local svn repository
and initialize it for synchronization

Syntax: $scriptname all|<project xml configuration>

Example: $scriptname all
         $scriptname project1.xml project2.xml

"
	;;
	*) 
	   echo "Processing $*"
	   projectslist=$1
	;;
esac




for projectFile in $projectslist; do
    if [ ! -f $projectFile ]; then
	  echo "ACTION-SKIP: $projectFile not found - skipped"
	  continue
    fi
 
	projectFileBase=`basename $projectFile`
	isProjectFile=`$xml sel -t -v "count(/project)" $projectFile`
	if [ $isProjectFile -ne 1 ]; then
	 echo "ACTION-SKIP: $projectFileBase is not a project configuration file" 
	 continue
	fi
  
	isActive=`$xml sel -t -v "/project/@active" $projectFile`
	projectID=`$xml sel -t -v "/project/@id" $projectFile`
	case "$isActive" in 
	   yes)  ### ADD PROJECT
			if [ -f $data_home/$projectID/info.txt ]; then
				echo "ACTION-ADD: Project $projectFileBase has already been installed - skipped"
				continue
			fi
			echo "ACTION-ADD: Adding enabled project $projectFileBase"
			projectURL=`$xml sel -t -v "/project/url" $projectFile`
			projectEMAIL=`$xml sel -t -v "/project/email" $projectFile`
			projectMailingListCount=`$xml sel -t -v "count(/project/mailinglist)" $projectFile`
			projectBUGZILLA=`$xml sel -t -v "/project/bugzilla/@url" $projectFile`
			projectSVN_URL=`$xml sel -t -v "/project/svn" $projectFile`
			projectSVN_USERNAME=`$xml sel -t -v "/project/svn/@username" $projectFile`
			projectSVN_PASSWORD=`$xml sel -t -v "/project/svn/@password" $projectFile`

			## create project folder
			mkdir -p $data_home/$projectID/svn
			mkdir $data_home/$projectID/mail
			mkdir $data_home/$projectID/bugs
			chmod -R 775 $data_home/$projectID
			
			## create project subversion repo
			$svnadmin create $svnadmin_create_args $data_home/$projectID/svn 2>.errFile
			if [ -s .errFile ]; then
  			  echo "ERROR: The creation of svn repository stopped with the error:"
			  echo `cat .errFile`
			  rm -fR $data_home/$projectID
			  rm -f .errFile
			  continue;
			fi
			echo "#!/bin/sh" > $data_home/$projectID/svn/hooks/pre-revprop-change
			#chmod 755 $data_home/$projectID/svn/hooks/pre-revprop-change
			chmod -R 775 $data_home/$projectID
			
			## synchronize project subversion repo
			$svnsync init file://$data_home/$projectID/svn $projectSVN_URL 2>&1 > .errFile
			if [ $? -ne 0 ]; then
  			  echo "ERROR: The sync initialization of svn repository stopped with the error:"
			  echo `cat .errFile`
			  rm -fR $data_home/$projectID
			  rm -f .errFile
			  continue;
			fi
 
			## create info file
			if [ "$projectURL" != "" ]; then echo "Website $projectURL" >> $data_home/$projectID/info.txt; fi
			if [ "$projectEMAIL" != "" ]; then echo "Contact $projectEMAIL" >> $data_home/$projectID/info.txt; fi
			if [ "$projectBUGZILLA" != "" ]; then echo "Bugzilla $projectEMAIL" >> $data_home/$projectID/info.txt; fi
		
			# TODO: add  mailing lists
			# should it be here, or bulk creation ? that's the question :-p

		
		;;
	   no) ### INCACTIVE PROJECT
		  echo "ACTION-SKIP: Skipping disabled project $projectFileBase"
		;;
	   *)
	   ;;
	esac
done

exit 0 


