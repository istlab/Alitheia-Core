#! /bin/sh

###############################################
# Create a project configuration file 
# from an existing and synchronized svn repository
###############################################
#
#
. ./config

scriptname=`basename $0`

debug(){
 echo $1
}

# create output folder if it doesnt exist 
if [ ! -d $project_import_directory ]; then mkdir $project_import_directory; fi

projects=`find $data_home -maxdepth 1 -type d  -printf "%f\n"`

for project in $projects; do
 if [ -f $project_import_directory/$project.xml ]; then
    debug "SKIPPED: $project has already been imported. Remove it from $project_import_directory to re-import"
    continue;
 fi
 if [ ! -f $data_home/$project/svn/db/revprops/0 ]; then
   debug "SKIPPED: Can not find SVN properties for $project"
   continue;
 fi

 if [ !  -f $data_home/$project/info.txt ]; then
   debug "SKIPPED: Can not find info.txt in $project"
   continue;
 fi

      projectSVN=`$sed -n -e "/^.*:\/\/.*/{p;}" $data_home/$project/svn/db/revprops/0`
      projectURL=`$sed -n -e "s/^Website \(.*\)$/\1/p;" $data_home/$project/info.txt`
      projectMAIL=`$sed -n -e "s/^Contact \(.*\)$/\1/p;" $data_home/$project/info.txt`
      projectBUGZILLA=`$sed -n -e "s/^Bugzilla \(.*\)$/\1/p;" $data_home/$project/info.txt`
      if [ -f $procmail_home/.procmail/rc.$project ]; then 
         projectMailingLists=`$sed -n -f $scripts/importProject.mailinglists.sed $procmail_home/.procmail/rc.$project`
      else
         debug "WARNING: Cant locate procmailrc for project $project"
         projectMailingLists=
      fi
  
      projectCard=$project_import_directory/$project.xml
      touch $projectCard
      echo "<?xml version=\"1.0\"?>" >> $projectCard
      echo "<project id=\"$project\" active=\"no\">" >> $projectCard
      echo "   <svn username=\"\" password=\"\">$projectSVN</svn>" >> $projectCard

      if [ "$projectURL" != "" ]; then
         echo "   <url>$projectURL</url>" >> $projectCard
      fi

      if [  "$projectMAIL" != "" ]; then
      echo "   <email>$projectMAIL</email>" >> $projectCard
      fi

      if [ "$projectMailingLists" != "" ]; then
         echo "   $projectMailingLists" >> $projectCard
      fi
 
      if [ "$projectBUGZILLA" != "" ]; then
         echo "   <bugzilla url=\"$projectBUGZILLA\">" >> $projectCard
      fi
      
      echo "</project>" >> $projectCard


done
exit 0
